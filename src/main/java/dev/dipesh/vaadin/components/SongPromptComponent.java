package dev.dipesh.vaadin.components;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.UIScope;
import dev.dipesh.dto.PromptRequestDTO;
import dev.dipesh.controller.SongController;
import dev.dipesh.vaadin.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@UIScope
@Component
public class SongPromptComponent extends VerticalLayout {

    private TextField songPromptField;
    private ComboBox<String> modelVersionComboBox;
    private Checkbox makeInstrumentalCheckbox;
    private Button createButton;
    private ProgressBar progressBar;
    private Span progressText;

    @Autowired
    private SongController songController;

    public SongPromptComponent() {
        setWidthFull();
        setSpacing(true);
        setMargin(true);

        // Song Prompt components
        HorizontalLayout promptLayout = new HorizontalLayout();
        songPromptField = new TextField();
        songPromptField.setPlaceholder("Enter prompt for the song...");
        songPromptField.setWidthFull();
        Span songPromptLabel = new Span("Song Prompt");
        promptLayout.add(songPromptLabel, songPromptField);
        add(promptLayout);

        // Model Version components
        HorizontalLayout versionLayout = new HorizontalLayout();
        modelVersionComboBox = new ComboBox<>("Model Version");
        modelVersionComboBox.setItems("chirp-v3-0");
        modelVersionComboBox.setValue("chirp-v3-0");
        modelVersionComboBox.setWidth("300px");
        Span modelVersionLabel = new Span("Model Version");
        versionLayout.add(modelVersionLabel, modelVersionComboBox);
        add(versionLayout);

        // Instrumental Checkbox
        makeInstrumentalCheckbox = new Checkbox("Make Instrumental");
        add(makeInstrumentalCheckbox);

        // Create Button
        createButton = new Button("Create", new Icon(VaadinIcon.MUSIC));
        createButton.addClickListener(click -> {
            try {
                createSong();
            } catch (Exception e) {
                Notification.show("Error: " + e.getMessage(), 5000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        add(createButton);

        // Progress components
        progressBar = new ProgressBar();
        progressText = new Span("Processing...");
        HorizontalLayout progressLayout = new HorizontalLayout(progressBar, progressText);
        progressText.setVisible(false);
        progressBar.setVisible(false);
        add(progressLayout);
    }

    private void createSong() throws JsonProcessingException {
        if (SecurityUtils.isUserLoggedIn()) {
            PromptRequestDTO promptRequest = new PromptRequestDTO(
                    songPromptField.getValue(),
                    modelVersionComboBox.getValue(),
                    makeInstrumentalCheckbox.getValue(),
                    ""
            );
            progressBar.setVisible(true);
            progressText.setVisible(true);
            songController.generateSong(promptRequest);
            progressBar.setVisible(false);
            progressText.setVisible(false);
            Notification.show("Song created successfully!", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } else {
            UI.getCurrent().navigate("login");
        }
    }
}
