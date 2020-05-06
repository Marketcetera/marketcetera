package org.marketcetera.webui.security;

import org.marketcetera.webui.login.LoginView;
import org.springframework.stereotype.Component;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;

/* $License$ */

/**
 * Provides protection against rerouting from login page.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Component
public class ConfigureUIServiceInitListener
        implements VaadinServiceInitListener
{
    /* (non-Javadoc)
     * @see com.vaadin.flow.server.VaadinServiceInitListener#serviceInit(com.vaadin.flow.server.ServiceInitEvent)
     */
    @Override
    public void serviceInit(ServiceInitEvent inEvent)
    {
        inEvent.getSource().addUIInitListener(uiEvent -> {
            final UI ui = uiEvent.getUI();
            ui.addBeforeEnterListener(this::beforeEnter);
        });
    }
    /**
     * Reroutes the user if (s)he is not authorized to access the view.
     *
     * @param inEvent a <code>BeforeEnterEvent</code> with navigation event details
     */
    private void beforeEnter(BeforeEnterEvent inEvent)
    {
        if(!LoginView.class.equals(inEvent.getNavigationTarget()) && !SecurityUtils.isUserLoggedIn()) {
            inEvent.rerouteTo(LoginView.class);
        }
    }
    private static final long serialVersionUID = 6754286798028187433L;
}
