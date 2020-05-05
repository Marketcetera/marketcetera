package org.marketcetera.webui.login;

import org.marketcetera.eventbus.EventBusService;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.login.AbstractLogin.LoginEvent;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.polymertemplate.Id;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.templatemodel.TemplateModel;

/**
 * A Designer generated component for the login-view template.
 *
 * Designer will add and remove fields with @Id mappings but
 * does not overwrite or otherwise change this file.
 */
@Route("login")
@Tag("login-view")
@JsModule("./src/org/marketcetera/webui/login/login-view.js")
public class LoginView
        extends PolymerTemplate<LoginView.LoginViewModel>
{
    /**
     * Creates a new LoginView.
     */
    public LoginView()
    {
        // You can initialise any data required for the connected UI components here.
        LoginI18n loginI18n = LoginI18n.createDefault();
        loginI18n.getForm().setTitle("Marketcetera\nAutomated Trading Platform");
        vaadinLoginForm.setI18n(loginI18n);
        vaadinLoginForm.addLoginListener(new ComponentEventListener<LoginEvent>() {
            @Override
            public void onComponentEvent(LoginEvent inEvent)
            {
                SLF4JLoggerProxy.debug(LoginView.this,
                                       "{} received {}",
                                       LoginView.this,
                                       inEvent);
                eventbusService.post(inEvent);
            }}
        );
    }
    /**
     * This model binds properties between LoginView and login-view
     */
    public interface LoginViewModel
            extends TemplateModel
    {
        // Add setters and getters for template properties here.
    }
    @Autowired
    private EventBusService eventbusService;
    /**
     * login form
     */
    @Id("vaadinLoginForm")
    private LoginForm vaadinLoginForm;
    private static final long serialVersionUID = -1484460171083815693L;
}
