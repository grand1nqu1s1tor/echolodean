package dev.dipesh.gui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.VaadinServletRequest;
import com.vaadin.flow.server.VaadinService;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

@Route("profile")
@PageTitle("Profile")
public class ProfileDashboard extends VerticalLayout {

    public ProfileDashboard() {
        H2 heading = new H2("User Profile");

        // Logout button
        Button logoutButton = new Button("Logout", e -> logout());

        // Styling and adding components to the layout
        addClassName("profile-view");
        add(heading, logoutButton);
    }

    private void logout() {
        // We'll perform the logout using Spring Security's logout handler
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(
                VaadinServletRequest.getCurrent().getHttpServletRequest(), null, null);
        VaadinService.getCurrentResponse().setHeader("Location", VaadinService.getCurrentRequest().getContextPath());
        VaadinService.getCurrentResponse().setStatus(302);
    }
}
