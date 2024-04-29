package dev.dipesh.gui;

import com.vaadin.flow.component.UI;
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
        // Perform logout using Vaadin's session and security context handling
        VaadinServletRequest request = VaadinServletRequest.getCurrent();
        VaadinService.getCurrentRequest().getWrappedSession().invalidate();
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(request, null, null); // Pass null as response because it's managed by Vaadin

        UI.getCurrent().getPage().setLocation("/");
        // After logout, redirect the user
        /*getUI().ifPresent(ui -> {
            ui.getPage().setLocation("/login"); // Redirect to login or root page
        });*/
    }


}
