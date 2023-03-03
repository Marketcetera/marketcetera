package org.marketcetera.ui;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.ui.events.LoginEvent;
import org.marketcetera.ui.service.NoServiceException;
import org.marketcetera.ui.service.SessionUser;
import org.marketcetera.ui.service.WebMessageService;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.ws.stateful.Authenticator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import io.grpc.StatusRuntimeException;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

/* $License$ */

/**
 * Provides a login input to the user.
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
    /**
     * Validate and start the object.
     */
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
        usernameText.textProperty().addListener((ChangeListener<String>) (inObservable,inOldValue,inNewValue) -> enableLoginButton()); 
        passwordText.textProperty().addListener((ChangeListener<String>) (inObservable,inOldValue,inNewValue) -> enableLoginButton()); 
        adviceLabel = new Label();
        adviceLabel.setVisible(false);
        setOnCloseRequest(this::onCloseRequest);
        VBox root = new VBox(5);
        root.getChildren().addAll(usernameBox,
                                  passwordBox,
                                  adviceLabel,
                                  loginButton);
        root.setPadding(new Insets(10));
        root.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if(event.getCode() == KeyCode.ENTER) {
               loginButton.fire();
               event.consume(); 
            } else if(event.getCode() == KeyCode.ESCAPE) {
                fireEvent(new WindowEvent(this,
                                          WindowEvent.WINDOW_CLOSE_REQUEST));
            }
        });
        Scene scene = new Scene(root);
        scene.getStylesheets().clear();
        scene.getStylesheets().add("dark-mode.css");
        setScene(scene);
        // The title of the stage is not visible for all styles.
        setTitle("Login");
        initStyle(StageStyle.UTILITY);
        setResizable(false);
    }
    /**
     * Enables or disables the login button.
     */
    private void enableLoginButton()
    {
        String value = StringUtils.trimToNull(usernameText.getText());
        String password = StringUtils.trimToNull(passwordText.getText());
        loginButton.setDisable(value == null || password == null);
    }
    /**
     * Fired when the logon button is pressed.
     *
     * @param inEvent an <code>ActionEvent</code> value
     */
    private void onLogin(ActionEvent inEvent)
    {
        adviceLabel.setVisible(false);
        String username = StringUtils.trimToNull(usernameText.getText());
        String password = StringUtils.trimToNull(passwordText.getText());
        SLF4JLoggerProxy.debug(this,
                               "Attempting to log in {}",
                               username);
        SessionUser sessionUser = new SessionUser(username,
                                                  password);
        SessionUser.getCurrent().setAttribute(SessionUser.class,
                                              sessionUser);
        String message = null;
        boolean authenticationSuccess;
        try {
            authenticationSuccess = webAuthenticator.shouldAllow(null,
                                                                 username,
                                                                 password.toCharArray());
            if(authenticationSuccess) {
                SLF4JLoggerProxy.info(this,
                                      "{} logged in",
                                      username);
                // Navigate to main view
                webMessageService.post(new LoginEvent(sessionUser));
                close();
            } else {
                message = "Username or password does not match";
            }
        } catch (StatusRuntimeException | NoServiceException e) {
            message = "Username or password does not match";
            authenticationSuccess = false;
        } catch (Exception e) {
            authenticationSuccess = false;
            if(message == null) {
                message = PlatformServices.getMessage(e);
            }
        }
        if(!authenticationSuccess) {
            SLF4JLoggerProxy.warn(this,
                                  "{} failed to log in: {}",
                                  username,
                                  message);
            SessionUser.getCurrent().setAttribute(SessionUser.class,
                                                  null);
            adviceLabel.setVisible(true);
            adviceLabel.setStyle(PhotonServices.errorMessage);
            adviceLabel.setText(message);
            passwordText.textProperty().set("");
        }
    }
    /**
     * Fired when the window is attempted to be closed without logging on.
     *
     * @param inEvent a <code>WindowEvent</code> value
     */
    private void onCloseRequest(WindowEvent inEvent)
    {
        // shutdown the whole app
        Platform.exit();
    }
    /**
     * button used to trigger logon attempt
     */
    private Button loginButton;
    /**
     * password field value
     */
    private PasswordField passwordText;
    /**
     * username field value
     */
    private TextField usernameText;
    /**
     * shows an error message, if appropriate
     */
    private Label adviceLabel;
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
