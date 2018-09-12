package org.marketcetera.web.view;

import java.util.Collection;
import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.marketcetera.admin.Permission;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.web.SessionUser;
import org.marketcetera.web.config.HostnameConfiguration;
import org.marketcetera.web.services.AdminClientService;
import org.marketcetera.web.services.ConnectableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

/* $License$ */

/**
 * Provides login services to users.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@SpringView(name=LoginView.NAME)
public class LoginView
        extends CustomComponent
        implements View,Button.ClickListener
{
    /**
     * Create a new LoginView instance.
     */
    public LoginView()
    {
        setSizeFull();
        // Create the user input field
        user = new TextField("User:");
        user.setWidth("300px");
        user.setRequired(true);
        
        user.setInputPrompt("Your username");
//        user.addValidator(new EmailValidator("Username must be an email address"));
        user.setInvalidAllowed(false);
        // Create the password input field
        password = new PasswordField("Password:");
        password.setWidth("300px");
//        password.addValidator(new PasswordValidator());
        password.setRequired(true);
        password.setValue("");
        password.setNullRepresentation("");
        // Create login button
        loginButton = new Button("Login", this);
        // Add both to a panel
        fields = new VerticalLayout(user, password, loginButton);
        fields.setCaption("Please login to access the application");
        fields.setSpacing(true);
        fields.setMargin(new MarginInfo(true, true, true, false));
        fields.setSizeUndefined();
        // The view root layout
        VerticalLayout viewLayout = new VerticalLayout(fields);
        viewLayout.setSizeFull();
        viewLayout.setComponentAlignment(fields, Alignment.MIDDLE_CENTER);
        viewLayout.setStyleName(ValoTheme.LAYOUT_CARD);
        setCompositionRoot(viewLayout);
    }
    /* (non-Javadoc)
     * @see com.vaadin.ui.Button.ClickListener#buttonClick(com.vaadin.ui.Button.ClickEvent)
     */
    @Override
    public void buttonClick(ClickEvent inEvent)
    {
        fields.setCaption("Please login to access the application");
        String username = user.getValue();
        String password = this.password.getValue();
        try {
            SLF4JLoggerProxy.debug(this,
                                   "Attempting to log in {}",
                                   username);
            AdminClientService adminClientService = new AdminClientService(username);
            boolean isValid = adminClientService.connect(password);
            if(isValid) {
                SLF4JLoggerProxy.info(this,
                                      "{} logged in",
                                       username);
                // connect other services
                Map<String,ConnectableService> services = applicationContext.getBeansOfType(ConnectableService.class);
                for(Map.Entry<String,ConnectableService> serviceEntry : services.entrySet()) {
                    SLF4JLoggerProxy.debug(this,
                                           "Connecting {}",
                                           serviceEntry.getKey());
                    try {
                        ConnectableService service = serviceEntry.getValue();
                        boolean result = service.connect(username,
                                                         password,
                                                         HostnameConfiguration.getInstance().getHostname(),
                                                         HostnameConfiguration.getInstance().getPort());
                        if(!result) {
                            SLF4JLoggerProxy.warn(this,
                                                  "{} failed to connect to {}",
                                                  username,
                                                  serviceEntry.getKey());
                        }
                    } catch (Exception e) {
                        String message = ExceptionUtils.getRootCauseMessage(e);
                        SLF4JLoggerProxy.warn(this,
                                              "{} failed to connect to {}: {}",
                                              username,
                                              serviceEntry.getKey(),
                                              message);
                    }
                }
                // get the permissions available for this user
                Collection<Permission> permissions = adminClientService.getPermissions();
                // Store the current user in the service session
                SessionUser loggedInUser = new SessionUser(username,
                                                           password);
                loggedInUser.getPermissions().addAll(permissions);
                VaadinSession.getCurrent().setAttribute(SessionUser.class,
                                                        loggedInUser);
                VaadinSession.getCurrent().setAttribute(AdminClientService.class,
                                                        adminClientService);
                // Navigate to main view
                getUI().getNavigator().navigateTo(MainView.NAME);
            } else {
                SLF4JLoggerProxy.warn(this,
                                      "{} failed to log in",
                                       username);
                VaadinSession.getCurrent().setAttribute(SessionUser.class,
                                                        null);
                VaadinSession.getCurrent().setAttribute(AdminClientService.class,
                                                        null);
            }
        } catch (Exception e) {
            String message = ExceptionUtils.getRootCauseMessage(e);
            SLF4JLoggerProxy.warn(this,
                                  e,
                                  "{} failed to log in: {}",
                                  username,
                                  message);
            fields.setCaption(message);
            VaadinSession.getCurrent().setAttribute(SessionUser.class,
                                                    null);
            VaadinSession.getCurrent().setAttribute(AdminClientService.class,
                                                    null);
        }
        this.password.setValue(null);
        this.user.focus();
    }
    /* (non-Javadoc)
     * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
     */
    @Override
    public void enter(ViewChangeEvent inEvent)
    {
        user.focus();
    }
    /**
     * application context value
     */
    @Autowired
    private ApplicationContext applicationContext;
    /**
     * user UI widget
     */
    private final TextField user;
    /**
     * password UI widget
     */
    private final PasswordField password;
    /**
     * login UI widget
     */
    private final Button loginButton;
    /**
     * layout widget
     */
    private final VerticalLayout fields;
    /**
     * view name value
     */
    public static final String NAME = "LoginView";
    private static final long serialVersionUID = 4281507010927439250L;
}
