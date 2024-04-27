package dev.dipesh.gui;

import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterListener;
import org.springframework.stereotype.Component;

@Component
public class SecuredViewAccessChecker implements BeforeEnterListener {

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (!SecurityUtils.isUserLoggedIn()) {
            event.rerouteTo(LoginView.class);
        }
    }
}
