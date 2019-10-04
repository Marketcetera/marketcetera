package org.marketcetera.webui.app.security;

import org.marketcetera.webui.ui.components.OfflineBanner;
import org.marketcetera.webui.ui.exceptions.AccessDeniedException;
import org.marketcetera.webui.ui.views.login.LoginView;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import com.vaadin.flow.spring.annotation.SpringComponent;

/**
 * Adds before enter listener to check access to views.
 * Adds the Offline banner.
 * 
 */
@SpringComponent
public class ConfigureUIServiceInitListener
        implements VaadinServiceInitListener
{
    @Override
    public void serviceInit(ServiceInitEvent event)
    {
        event.getSource().addUIInitListener(uiEvent -> {
            final UI ui = uiEvent.getUI();
            ui.add(new OfflineBanner());
            ui.addBeforeEnterListener(this::beforeEnter);
        });
    }
    /**
     * Reroutes the user if she is not authorized to access the view. 
     *
     * @param event before navigation event with event details
     */
    private void beforeEnter(BeforeEnterEvent event)
    {
        final boolean accessGranted = SecurityUtils.isAccessGranted(event.getNavigationTarget());
        if(!accessGranted) {
            if(SecurityUtils.isUserLoggedIn()) {
                event.rerouteToError(AccessDeniedException.class);
            } else {
                event.rerouteTo(LoginView.class);
            }
        }
    }
    private static final long serialVersionUID = -4933606162229090877L;
}
