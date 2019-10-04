package org.marketcetera.webui.ui.views.login;

import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.webui.app.security.SecurityUtils;
import org.marketcetera.webui.ui.utils.UiConst;
import org.marketcetera.webui.ui.views.dashboard.DashboardView;

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
@PageTitle("my-starter-project")
@JsModule("./styles/shared-styles.js")
@Viewport(UiConst.VIEWPORT)
public class LoginView
        extends LoginOverlay
        implements AfterNavigationObserver, BeforeEnterObserver
{
    public LoginView()
    {
        LoginI18n i18n = LoginI18n.createDefault();
        i18n.setHeader(new LoginI18n.Header());
        i18n.getHeader().setTitle("my-starter-project");
        i18n.getHeader().setDescription("admin@vaadin.com + admin\n" + "barista@vaadin.com + barista");
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
    public void beforeEnter(BeforeEnterEvent event)
    {
        SLF4JLoggerProxy.warn(this,
                              "Coco: entering LoginView.beforeEnter: {}",
                              event);
        if(SecurityUtils.isUserLoggedIn()) {
            event.forwardTo(DashboardView.class);
        } else {
            setOpened(true);
        }
    }
    @Override
    public void afterNavigation(AfterNavigationEvent event)
    {
        setError(event.getLocation().getQueryParameters().getParameters().containsKey("error"));
    }
    private static final long serialVersionUID = 5612515141123922868L;
}
