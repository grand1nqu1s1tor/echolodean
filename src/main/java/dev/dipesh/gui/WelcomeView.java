package dev.dipesh.gui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.notification.Notification;

@Route("login") // Change to match your route planning
@PageTitle("Login with Spotify")
public class WelcomeView extends VerticalLayout {

    public WelcomeView() {
        Button loginButton = new Button("Login with Spotify", event -> {
            // Redirect to the backend controller handling Spotify OAuth
            getUI().ifPresent(ui -> ui.getPage().setLocation("/api/login"));
        });

        // Styling the button (similar setup as previously mentioned)
        loginButton.getStyle()
                .set("color", "white")
                .set("background", "green")
                .set("border", "none")
                .set("padding", "10px 20px")
                .set("border-radius", "5px")
                .set("font-size", "16px")
                .set("cursor", "pointer");

        // Aligning elements in the center
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        add(loginButton);
    }
}
