package org.marketcetera.ui;

import java.awt.Taskbar;
import java.awt.Taskbar.Feature;
import java.awt.Toolkit;
import java.io.IOException;

import org.controlsfx.control.NotificationPane;
import org.marketcetera.ui.events.LoginEvent;
import org.marketcetera.ui.events.LogoutEvent;
import org.marketcetera.ui.events.NotificationEvent;
import org.marketcetera.ui.service.SessionUser;
import org.marketcetera.ui.service.WebMessageService;
import org.marketcetera.ui.service.WindowManagerService;
import org.marketcetera.ui.view.ApplicationMenu;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.google.common.eventbus.Subscribe;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
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
        windowManagerService = context.getBean(WindowManagerService.class);
        webMessageService.register(this);
    }
    @Override
    public void start(Stage inStage)
            throws IOException
    {
        SLF4JLoggerProxy.info(this,
                              "Starting main stage");
        mainStage = inStage;
        windowManagerService.initializeMainStage(mainStage);
        root = new VBox();
        menuLayout = new VBox();
        workspace = new VBox();
        workspace.setPrefWidth(1024);
        workspace.setPrefHeight(768);
        initializeFooter();
        Separator separator = new Separator(Orientation.HORIZONTAL);
        root.getChildren().addAll(menuLayout,
                                  workspace,
                                  separator,
                                  footer);
        Scene mainScene = new Scene(root);
        inStage.setScene(mainScene);
        inStage.setTitle("Marketcetera Automated Trading Platform");
        initializeNotificationPane();
        inStage.getIcons().addAll(new Image("/images/photon-16x16.png"),
                                new Image("/images/photon-24x24.png"),
                                new Image("/images/photon-32x32.png"),
                                new Image("/images/photon-48x48.png"),
                                new Image("/images/photon-48x48.png"),
                                new Image("/images/photon-64x64.png"),
                                new Image("/images/photon-128x128.png"));
        if (Taskbar.isTaskbarSupported()) {
            Taskbar taskbar = Taskbar.getTaskbar();
            if(taskbar.isSupported(Feature.ICON_IMAGE)) {
                final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
                java.awt.Image dockIcon = defaultToolkit.getImage(getClass().getResource("/images/photon-128x128.png"));
                taskbar.setIconImage(dockIcon);
            }
        }
        inStage.setOnCloseRequest(closeEvent -> {
            isShuttingDown = true;
            webMessageService.post(new LogoutEvent());
            try {
                ((ConfigurableApplicationContext)context).close();
            } catch (Exception ignored) {}
            Platform.exit();
        });
        inStage.show();
        doLogin();
    }
    private void initializeFooter()
    {
        footer = new HBox(10);
        statusLayout = new HBox();
        statusLayout.setAlignment(Pos.BOTTOM_LEFT);
        clockLabel = new Label();
        clockUpdater = new ClockUpdater(clockLabel);
        clockUpdater.start();
        userLabel = new Label();
        userLabel.setAlignment(Pos.BASELINE_CENTER);
        footer.setAlignment(Pos.BOTTOM_CENTER);
        footer.getChildren().addAll(statusLayout,
                                    clockLabel,
                                    userLabel);
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
    private void initializeNotificationPane()
    {
        // Create a WebView
        WebView webView = new WebView();
        // Wrap it inside a NotificationPane
        notificationPane = new NotificationPane(webView);
        // and put the NotificationPane inside a Tab
        Tab tab1 = new Tab("Tab 1");
        tab1.setContent(notificationPane);
        // and the Tab inside a TabPane. We just have one tab here, but of course 
        // you can have more!
        TabPane tabPane = new TabPane();
        tabPane.getTabs().addAll(tab1);
        notificationPane.setShowFromTop(false);
        workspace.getChildren().add(notificationPane);
        notificationPane.setPrefHeight(700);
    }
    @Subscribe
    public void onLogon(LoginEvent inEvent)
    {
        Platform.runLater(() -> { userLabel.setText(inEvent.getSessionUser().getUsername());});
    }
    @Subscribe
    public void onNotification(NotificationEvent inEvent)
    {
        SLF4JLoggerProxy.debug(this,
                               "Received: {}",
                               inEvent);
        Platform.runLater(() -> {
            notificationPane.setText(inEvent.getMessage());
            notificationPane.show();
        });
    }
    @Subscribe
    public void onLogout(LogoutEvent inEvent)
    {
        SessionUser.getCurrent().setAttribute(ApplicationMenu.class,
                                              null);
        SessionUser.getCurrent().setAttribute(SessionUser.class,
                                              null);
        Platform.runLater(() -> {
            userLabel.setText("");
            menuLayout.getChildren().clear();
            if(!isShuttingDown) {
                doLogin();
            }
        });
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
    public static Stage getMainStage()
    {
        return mainStage;
    }
    private static Stage mainStage;
    public static void main(String[] args)
    {
        launch();
    }
    private boolean isShuttingDown = false;
    /**
     * web message service value
     */
    private WebMessageService webMessageService;
    private WindowManagerService windowManagerService;
    private VBox menuLayout;
    private ApplicationContext context;
    private VBox root;
    private HBox footer;
    private HBox statusLayout;
    private Label clockLabel;
    private Label userLabel;
    private VBox workspace;
    private NotificationPane notificationPane;
    private ClockUpdater clockUpdater;

}