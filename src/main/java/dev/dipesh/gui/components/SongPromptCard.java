package dev.dipesh.gui.components;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.spring.annotation.UIScope;
import dev.dipesh.DTO.PromptRequestDTO;
import dev.dipesh.controller.SongController;
import dev.dipesh.gui.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@UIScope
@Component
public class SongPromptCard extends VerticalLayout {

    private TextField songPromptField;
    private ComboBox<String> modelVersionComboBox;
    private Checkbox makeInstrumentalCheckbox;
    private Button createButton;
    @Autowired
    private SongController songController;


    public SongPromptCard() {
        songPromptField = new TextField("Song Prompt");
        songPromptField.setPlaceholder("Enter prompt for the song...");
        songPromptField.setHeight("200px");
        modelVersionComboBox = new ComboBox<>("Model Version");
        modelVersionComboBox.setItems("chirp-v3-0");
        modelVersionComboBox.setValue("chirp-v3-0"); // Set default value
        makeInstrumentalCheckbox = new Checkbox("Instrumental");

        createButton = new Button("Create", new Icon(VaadinIcon.MUSIC));
        createButton.addClickListener(click -> {
            try {
                createSong();
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });

        add(songPromptField, modelVersionComboBox, makeInstrumentalCheckbox, createButton);
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
        } else {
            // Redirect to the login view
            UI.getCurrent().navigate("login");
        }
    }
}
