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
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.spring.annotation.UIScope;
import dev.dipesh.controller.SongController;
import dev.dipesh.dto.PromptRequestDTO;
import dev.dipesh.vaadin.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Timer;
import java.util.TimerTask;

@UIScope
@Component
@PageTitle("Create Song")
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
        addClassName("song-prompt-component");
        setWidthFull();
        setAlignItems(Alignment.START);
        setJustifyContentMode(JustifyContentMode.START);

        // Heading for song creation
        Span createSongHeading = new Span("Create New Song");
        createSongHeading.addClassNames("heading");
        add(createSongHeading);

        // Song Prompt components
        HorizontalLayout promptLayout = new HorizontalLayout();
        promptLayout.addClassNames("prompt-layout");
        songPromptField = new TextField();
        songPromptField.setPlaceholder("Enter prompt for the song...");
        songPromptField.setWidthFull();
        promptLayout.add(new Span("Song Prompt"), songPromptField);
        add(promptLayout);

        // Model Version components
        HorizontalLayout versionLayout = new HorizontalLayout();
        versionLayout.addClassNames("version-layout");
        modelVersionComboBox = new ComboBox<>("Model Version");
        modelVersionComboBox.setItems("chirp-v3-0");
        modelVersionComboBox.setValue("chirp-v3-0");
        modelVersionComboBox.setWidth("300px");
        versionLayout.add(new Span("Model Version"), modelVersionComboBox);
        add(versionLayout);

        // Instrumental Checkbox
        makeInstrumentalCheckbox = new Checkbox("Make Instrumental");
        makeInstrumentalCheckbox.addClassNames("checkbox");
        add(makeInstrumentalCheckbox);

        // Create Button
        configureCreateButton();
        add(createButton);

        // Progress components
        progressBar = new ProgressBar();
        progressText = new Span("Processing...");
        HorizontalLayout progressLayout = new HorizontalLayout(progressBar, progressText);
        progressLayout.addClassNames("progress-layout");
        progressBar.setVisible(false);
        progressText.setVisible(false);
        add(progressLayout);
    }

    private void configureCreateButton() {
        createButton = new Button("Create", new Icon(VaadinIcon.MUSIC));
        createButton.addClassNames("create-button");
        createButton.addClickListener(event -> {
            try {
                disableComponents();
                createSong();
            } catch (Exception e) {
                Notification.show("Error: " + e.getMessage(), 5000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                reenableComponents();
            }
        });
    }

    private void disableComponents() {
        createButton.setEnabled(false);
        progressBar.setVisible(true);
        progressText.setVisible(true);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                UI.getCurrent().access(() -> reenableComponents());
            }
        }, 60000); // 60000 milliseconds = 1 minute
    }

    private void reenableComponents() {
        createButton.setEnabled(true);
        progressBar.setVisible(false);
        progressText.setVisible(false);
    }

    private void createSong() throws JsonProcessingException {
        if (SecurityUtils.isUserLoggedIn()) {
            PromptRequestDTO promptRequest = new PromptRequestDTO(
                    songPromptField.getValue(),
                    modelVersionComboBox.getValue(),
                    makeInstrumentalCheckbox.getValue(),
                    ""
            );
            songController.generateSong(promptRequest);
            Notification.show("Song created successfully!", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } else {
            UI.getCurrent().navigate("login");
        }
    }
}
