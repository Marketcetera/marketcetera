package org.marketcetera.ui;

import java.io.IOException;

import jakarta.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.ui.events.LoginEvent;
import org.marketcetera.ui.service.NoServiceException;
import org.marketcetera.ui.service.ServerConnectionService;
import org.marketcetera.ui.service.ServerConnectionService.ServerConnectionData;
import org.marketcetera.ui.service.SessionUser;
import org.marketcetera.ui.service.UiMessageService;
import org.marketcetera.ui.view.ValidatingTextField;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.ws.stateful.Authenticator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import io.grpc.StatusRuntimeException;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
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
        serverConnectionDataChanged = false;
        serverConnectionData = serverConnectionService.getConnectionData();
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
        final BooleanProperty networkInvalid = new SimpleBooleanProperty(false);
        final BooleanProperty credentialsInvalid = new SimpleBooleanProperty(true);
        final BooleanProperty disableLogin = new SimpleBooleanProperty(networkInvalid.get() || credentialsInvalid.get());
        networkInvalid.addListener((observable,oldValue,newValue) -> {
            disableLogin.set(networkInvalid.get() || credentialsInvalid.get());
        });
        credentialsInvalid.addListener((observable,oldValue,newValue) -> {
            disableLogin.set(networkInvalid.get() || credentialsInvalid.get());
        });
        loginButton = new Button("Login");
        loginButton.setOnAction(this::onLogin);
        loginButton.setDisable(true);
        usernameText.textProperty().addListener((observable,oldValue,newValue) -> {
            credentialsInvalid.set(!(StringUtils.trimToNull(newValue) != null && StringUtils.trimToNull(passwordText.textProperty().get()) != null));
            disableLogin.set(networkInvalid.get() || credentialsInvalid.get());
        }); 
        passwordText.textProperty().addListener((observable,oldValue,newValue) -> {
            credentialsInvalid.set(!(StringUtils.trimToNull(usernameText.textProperty().get()) != null && StringUtils.trimToNull(newValue) != null));
            disableLogin.set(networkInvalid.get() || credentialsInvalid.get());
        }); 
        adviceLabel = new Label();
        adviceLabel.setVisible(false);
        setOnCloseRequest(this::onCloseRequest);
        VBox root = new VBox(5);
        root.setPadding(new Insets(10));
        root.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if(networkInvalid.get()) {
                return;
            }
            if(event.getCode() == KeyCode.ENTER) {
               loginButton.fire();
               event.consume(); 
            } else if(event.getCode() == KeyCode.ESCAPE) {
                fireEvent(new WindowEvent(this,
                                          WindowEvent.WINDOW_CLOSE_REQUEST));
            }
        });
        loginButton.disableProperty().bind(disableLogin);
        GridPane serverConnectionGrid = new GridPane();
        ValidatingTextField hostnameTextField = new ValidatingTextField(input -> PhotonServices.isValidHostNameSyntax(input));
        hostnameTextField.setPromptText("MATP host or ip address");
        hostnameTextField.textProperty().set(serverConnectionData.getHostname());
        ValidatingTextField portTextField = new ValidatingTextField(input -> StringUtils.trimToNull(input) != null && input.matches("^([1-9][0-9]{0,3}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5])$"));
        portTextField.setPromptText("MATP port");
        portTextField.textProperty().set(String.valueOf(serverConnectionData.getPort()));
        CheckBox useSslCheckBox = new CheckBox();
        useSslCheckBox.selectedProperty().set(serverConnectionData.useSsl());
        useSslCheckBox.selectedProperty().addListener((observable,oldValue,newValue) -> {
            serverConnectionData.setUseSsl(newValue);
            serverConnectionDataChanged = true;
        });
        hostnameTextField.textProperty().addListener((observable,oldValue,newValue) -> {
            networkInvalid.set(!(hostnameTextField.isValidProperty().get() && portTextField.isValidProperty().get()));
            disableLogin.set(networkInvalid.get() || credentialsInvalid.get());
            serverConnectionDataChanged = true;
            serverConnectionData.setHostname(StringUtils.trimToNull(newValue));
        });
        portTextField.textProperty().addListener((observable,oldValue,newValue) -> {
            networkInvalid.set(!(hostnameTextField.isValidProperty().get() && portTextField.isValidProperty().get()));
            disableLogin.set(networkInvalid.get() || credentialsInvalid.get());
            String rawValue = StringUtils.trimToNull(newValue);
            if(rawValue != null) {
                try {
                    serverConnectionData.setPort(Integer.parseInt(rawValue));
                    serverConnectionDataChanged = true;
                } catch (NumberFormatException ignored) {}
            }
        });
        serverConnectionGrid.setHgap(10);
        serverConnectionGrid.setVgap(10);
        serverConnectionGrid.setPadding(new Insets(20,150,10,10));
        serverConnectionGrid.add(new Label("Hostname"),0,0);
        serverConnectionGrid.add(hostnameTextField,1,0);
        serverConnectionGrid.add(new Label("Port"),0,1);
        serverConnectionGrid.add(portTextField,1,1);
        serverConnectionGrid.add(new Label("SSL"),0,2);
        serverConnectionGrid.add(useSslCheckBox,1,2);
        Accordion accordion = new Accordion();
        TitledPane pane1 = new TitledPane("Server Connection" ,
                                          serverConnectionGrid);
        pane1.setAnimated(false);
        pane1.expandedProperty().addListener((observable,oldValue,newValue) -> {
            Platform.runLater(() -> {
                sizeToScene();
            });
        });
        accordion.getPanes().add(pane1);
        VBox serverConnectionLayout = new VBox(accordion);
        root.getChildren().addAll(usernameBox,
                                  passwordBox,
                                  serverConnectionLayout,
                                  adviceLabel,
                                  loginButton);
        mainScene = new Scene(root);
        PhotonServices.style(mainScene);
        setScene(mainScene);
        setTitle("Login");
        initStyle(StageStyle.UTILITY);
        setResizable(false);
    }
    /**
     * Fired when the logon button is pressed.
     *
     * @param inEvent an <code>ActionEvent</code> value
     */
    private void onLogin(ActionEvent inEvent)
    {
        if(serverConnectionDataChanged) {
            serverConnectionDataChanged = false;
            try {
                serverConnectionService.setConnectionData(serverConnectionData);
            } catch (IOException e) {
                SLF4JLoggerProxy.warn(this,
                                      e,
                                      "Unable to update config with changes");
            }
        }
        adviceLabel.setText("");
        adviceLabel.setVisible(false);
        sizeToScene();
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
            message = PlatformServices.getMessage(e);
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
            sizeToScene();
        }
    }
    /**
     * Fired when the window is attempted to be closed without logging on.
     *
     * @param inEvent a <code>WindowEvent</code> value
     */
    private void onCloseRequest(WindowEvent inEvent)
    {
        PhotonApp.getApp().doAppShutdown();
    }
    /**
     * main scene of the dialog
     */
    private Scene mainScene;
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
     * holds information about how to connect to the server
     */
    private ServerConnectionData serverConnectionData;
    /**
     * indicates if the server connection data has changed or not
     */
    private boolean serverConnectionDataChanged;
    /**
     * provides access to server connection services
     */
    @Autowired
    private ServerConnectionService serverConnectionService;
    /**
     * provides authentication services
     */
    @Autowired
    private Authenticator webAuthenticator;
    /**
     * web message service value
     */
    @Autowired
    private UiMessageService webMessageService;
}
