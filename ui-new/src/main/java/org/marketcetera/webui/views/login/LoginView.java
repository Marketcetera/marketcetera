package org.marketcetera.webui.views.login;

import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/* $License$ */

/**
 * Provides the login view.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@PageTitle("Login")
@Route(value = "login")
public class LoginView
        extends LoginOverlay
        implements BeforeEnterObserver
{
    /**
     * Create a new LoginView instance.
     */
    public LoginView()
    {
        setAction("login");
        LoginI18n i18n = LoginI18n.createDefault();
        i18n.setHeader(new LoginI18n.Header());
        i18n.getHeader().setTitle("Marketcetera Automated Trading Platform");
        i18n.setAdditionalInformation(null);
        setI18n(i18n);
        setForgotPasswordButtonVisible(false);
        setOpened(true);
    }
    /* (non-Javadoc)
     * @see com.vaadin.flow.router.internal.BeforeEnterHandler#beforeEnter(com.vaadin.flow.router.BeforeEnterEvent)
     */
    @Override
    public void beforeEnter(BeforeEnterEvent inEvent)
    {
     // inform the user about an authentication error
        if(inEvent.getLocation().getQueryParameters().getParameters().containsKey("error")) {
            setError(true);
        }
    }
    private static final long serialVersionUID = -1570396516756629146L;
}
