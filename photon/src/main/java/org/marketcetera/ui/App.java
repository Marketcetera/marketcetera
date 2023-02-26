package org.marketcetera.ui;

import java.io.IOException;

import org.marketcetera.ui.events.LogoutEvent;
import org.marketcetera.ui.service.SessionUser;
import org.marketcetera.ui.service.WebMessageService;
import org.marketcetera.ui.view.ApplicationMenu;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.google.common.eventbus.Subscribe;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 * @see https://openjfx.io/openjfx-docs/#maven
 */
public class App
        extends Application
{

    private static Scene scene;

    /* (non-Javadoc)
     * @see javafx.application.Application#init()
     */
    @Override
    public void init()
            throws Exception
    {
        super.init();
        context = new AnnotationConfigApplicationContext("org.marketcetera","com.marketcetera");
        webMessageService = context.getBean(WebMessageService.class);
        webMessageService.register(this);
    }
    @Override
    public void start(Stage stage)
            throws IOException
    {
        //        scene = new Scene(loadFXML("primary"), 640, 480);
        //        stage.setScene(scene);
        //        stage.show();
        SLF4JLoggerProxy.info(this,
                              "Starting main stage");
        root = new VBox();
        menuLayout = new VBox();
        root.getChildren().add(menuLayout);
        Scene mainScene = new Scene(root,
                                    1024,
                                    768);
        stage.setScene(mainScene);
        stage.setTitle("Marketcetera Automated Trading Platform");
        stage.show();
        doLogin();
    }
    private void showMenu()
    {
        ApplicationMenu applicationMenu = SessionUser.getCurrent().getAttribute(ApplicationMenu.class);
        if(applicationMenu == null) {
            SLF4JLoggerProxy.debug(App.class,
                                   "Session is now logged in, building application menu");
            applicationMenu = context.getBean(ApplicationMenu.class);
            menuLayout.getChildren().add(applicationMenu.getMenu());
            SessionUser.getCurrent().setAttribute(ApplicationMenu.class,
                                                  applicationMenu);
        }
        applicationMenu.refreshMenuPermissions();
    }
    @Subscribe
    public void onLogout(LogoutEvent inEvent)
    {
        SessionUser.getCurrent().setAttribute(ApplicationMenu.class,
                                              null);
        SessionUser.getCurrent().setAttribute(SessionUser.class,
                                              null);
        Platform.runLater(() -> menuLayout.getChildren().clear());
        Platform.runLater(() -> doLogin());
    }
    private void doLogin()
    {
        LoginView loginView = context.getBean(LoginView.class);
        loginView.showAndWait();
        showMenu();
    }
    static void setRoot(String fxml)
            throws IOException
    {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml)
            throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args)
    {
        launch();
    }
    /**
     * web message service value
     */
    private WebMessageService webMessageService;
    private VBox menuLayout;
    private ApplicationContext context;
    private VBox root;

}