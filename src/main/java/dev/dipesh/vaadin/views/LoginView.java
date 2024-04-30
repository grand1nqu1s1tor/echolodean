package dev.dipesh.vaadin.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("login") // Change to match your route planning
@PageTitle("Login with Spotify")
public class LoginView extends VerticalLayout {

    public LoginView() {
        Button loginButton = new Button("Login with Spotify", event -> {
            UI.getCurrent().getPage().setLocation("/api/login");
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
