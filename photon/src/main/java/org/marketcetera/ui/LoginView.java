package org.marketcetera.ui;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.marketcetera.ui.events.LoginEvent;
import org.marketcetera.ui.events.NotificationEvent;
import org.marketcetera.ui.service.SessionUser;
import org.marketcetera.ui.service.WebMessageService;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.ws.stateful.Authenticator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class LoginView
        extends Stage
{
    @PostConstruct
    public void start()
    {
        initModality(Modality.APPLICATION_MODAL);
        HBox usernameBox = new HBox(5);
        Label usernameLabel = new Label("Username");
        usernameText = new TextField();
        usernameBox.getChildren().addAll(usernameLabel,
                                         usernameText);
        HBox passwordBox = new HBox(5);
        Label passwordLabel = new Label("Password");
        passwordText = new PasswordField();
        passwordBox.getChildren().addAll(passwordLabel,
                                         passwordText);
        // A button to close the stage
        loginButton = new Button("Login");
        loginButton.setOnAction(this::onLogin);
        loginButton.setDisable(true);
        usernameText.setOnKeyTyped(this::enableLoginButton);
        passwordText.setOnKeyTyped(this::enableLoginButton);
        setOnCloseRequest(this::onCloseRequest);
        VBox root = new VBox(5);
        root.getChildren().addAll(usernameBox,
                                  passwordBox,
                                  loginButton);
        Scene scene = new Scene(root);
        setScene(scene);
        // The title of the stage is not visible for all styles.
        setTitle("Login");
        initStyle(StageStyle.UTILITY);
        setResizable(false);
    }
    private void enableLoginButton(KeyEvent inKeyEvent)
    {
        String value = StringUtils.trimToNull(usernameText.getText());
        String password = StringUtils.trimToNull(passwordText.getText());
        loginButton.setDisable(value == null || password == null);
    }
    private void onLogin(ActionEvent inEvent)
    {
        String username = StringUtils.trimToNull(usernameText.getText());
        String password = StringUtils.trimToNull(passwordText.getText());
        SLF4JLoggerProxy.debug(this,
                               "Attempting to log in {}",
                               username);
        SessionUser sessionUser = new SessionUser(username,
                                                  password);
        SessionUser.getCurrent().setAttribute(SessionUser.class,
                                              sessionUser);
        try {
            if(webAuthenticator.shouldAllow(null,
                                            username,
                                            password.toCharArray())) {
                SLF4JLoggerProxy.info(this,
                                      "{} logged in",
                                      username);
                // Navigate to main view
                webMessageService.post(new LoginEvent(sessionUser));
                close();
            } else {
                throw new IllegalArgumentException("Failed to log in");
            }
        } catch (Exception e) {
            String message = ExceptionUtils.getRootCauseMessage(e);
            SLF4JLoggerProxy.warn(this,
                                  e,
                                  "{} failed to log in: {}",
                                  username,
                                  message);
            showPopupMessage(message);
            SessionUser.getCurrent().setAttribute(SessionUser.class,
                                                  null);
        }
    }
    private void showPopupMessage(final String message)
    {
        // TODO this doesn't show
        System.out.println("Positing notification event");
        webMessageService.post(new NotificationEvent(message));
    }
    private void onCloseRequest(WindowEvent inEvent)
    {
        inEvent.consume();
    }
    
    private Button loginButton;
    private PasswordField passwordText;
    private TextField usernameText;
    /**
     * provides authentication services
     */
    @Autowired
    private Authenticator webAuthenticator;
    /**
     * web message service value
     */
    @Autowired
    private WebMessageService webMessageService;
}
