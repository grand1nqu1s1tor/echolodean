package dev.dipesh.gui;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;

@Route("")
@CssImport("./styles/styles.css")
public class RootView extends VerticalLayout {

    public RootView() {
        addClassName("root-view");

        // Title of the application
        H1 title = new H1("Echolodean");
        title.addClassName("root-title");

        // Router links
        RouterLink loginLink = new RouterLink("Login with Spotify", WelcomeView.class);
        RouterLink homeLink = new RouterLink("Home", MainView.class);
        RouterLink mySongsLink = new RouterLink("My Songs", MySongsView.class);

        // Adding a custom class name for styling links
        loginLink.addClassName("router-link");
        homeLink.addClassName("router-link");
        mySongsLink.addClassName("router-link");

        // Link container for better layout control
        HorizontalLayout linkContainer = new HorizontalLayout(loginLink, homeLink, mySongsLink);
        linkContainer.addClassName("link-container");

        // Aligning elements
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        // Adding components to the layout
        add(title, linkContainer);
    }
}
