package com.marketcetera.colin.ui.views.login;

import com.marketcetera.colin.app.security.SecurityUtils;
import com.marketcetera.colin.ui.utils.WebUiConst;
import com.marketcetera.colin.ui.views.storefront.StorefrontView;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route
@PageTitle("Marketcetera Automated Trading Platform")
@JsModule("./styles/shared-styles.js")
@Viewport(WebUiConst.VIEWPORT)
public class LoginView
        extends LoginOverlay
        implements AfterNavigationObserver, BeforeEnterObserver
{
    /**
     * Create a new LoginView instance.
     */
    public LoginView()
    {
        LoginI18n i18n = LoginI18n.createDefault();
        i18n.setHeader(new LoginI18n.Header());
        i18n.getHeader().setTitle("Marketcetera Automated Trading Platform");
//        i18n.getHeader().setDescription("admin@vaadin.com + admin\n" + "barista@vaadin.com + barista");
        i18n.setAdditionalInformation(null);
        i18n.setForm(new LoginI18n.Form());
        i18n.getForm().setSubmit("Sign in");
        i18n.getForm().setTitle("Sign in");
        i18n.getForm().setUsername("Email");
        i18n.getForm().setPassword("Password");
        setI18n(i18n);
        setForgotPasswordButtonVisible(false);
        setAction("login");
    }

    @Override
    public void beforeEnter(BeforeEnterEvent inEvent)
    {
        if(SecurityUtils.isUserLoggedIn()) {
            inEvent.forwardTo(StorefrontView.class);
        } else {
            setOpened(true);
        }
    }

    @Override
    public void afterNavigation(AfterNavigationEvent inEvent)
    {
        setError(inEvent.getLocation().getQueryParameters().getParameters().containsKey("error"));
    }
    private static final long serialVersionUID = 7144610570583754167L;
}
