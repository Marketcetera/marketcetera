package org.marketcetera.ui;

import java.awt.Taskbar;
import java.awt.Taskbar.Feature;
import java.awt.Toolkit;
import java.io.IOException;

import org.marketcetera.ui.events.LoginEvent;
import org.marketcetera.ui.events.LogoutEvent;
import org.marketcetera.ui.service.PhotonNotificationService;
import org.marketcetera.ui.service.SessionUser;
import org.marketcetera.ui.service.StyleService;
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
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
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
        styleService = context.getBean(StyleService.class);
        webMessageService.register(this);
    }
    /* (non-Javadoc)
     * @see javafx.application.Application#start(javafx.stage.Stage)
     */
    @Override
    public void start(Stage inPrimaryStage)
            throws Exception
    {
        SLF4JLoggerProxy.info(this,
                              "Starting main stage");
        primaryStage = inPrimaryStage;
        windowManagerService.initializeMainStage(primaryStage);
        root = new VBox();
        menuLayout = new VBox();
        workspace = new VBox();
        workspace.setId(getClass().getCanonicalName() + ".workspace");
        workspace.setPrefWidth(1024);
        workspace.setPrefHeight(768);
        initializeFooter();
        Separator separator = new Separator(Orientation.HORIZONTAL);
        root.getChildren().addAll(menuLayout,
                                  workspace,
                                  separator,
                                  footer);
        Scene mainScene = new Scene(root);
        inPrimaryStage.setScene(mainScene);
        inPrimaryStage.setTitle("Marketcetera Automated Trading Platform");
        inPrimaryStage.getIcons().addAll(new Image("/images/photon-16x16.png"),
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
        inPrimaryStage.setOnCloseRequest(closeEvent -> {
            isShuttingDown = true;
            webMessageService.post(new LogoutEvent());
            try {
                ((ConfigurableApplicationContext)context).close();
            } catch (Exception ignored) {}
            Platform.exit();
        });
        VBox.setVgrow(menuLayout,
                      Priority.NEVER);
        VBox.setVgrow(workspace,
                      Priority.ALWAYS);
        VBox.setVgrow(footer,
                      Priority.NEVER);
        styleService.addStyleToAll(menuLayout,
                                   workspace,
                                   separator,
                                   footer,
                                   root);
        inPrimaryStage.show();
        doLogin();
    }
    /**
     * Receive <code>LoginEvent</code> values.
     *
     * @param inEvent a <code>LoginEvent</code> value
     */
    @Subscribe
    public void onLogon(LoginEvent inEvent)
    {
        Platform.runLater(() -> { userLabel.setText(inEvent.getSessionUser().getUsername());});
        notificationService = context.getBean(PhotonNotificationService.class);
    }
    /**
     * Receive logout events.
     *
     * @param inEvent a <code>LogoutEvent</code> value
     */
    @Subscribe
    public void onLogout(LogoutEvent inEvent)
    {
        if(notificationService != null) {
            notificationService.stop();
            notificationService = null;
        }
        if(SessionUser.getCurrent() != null) {
            SessionUser.getCurrent().setAttribute(ApplicationMenu.class,
                                                  null);
            SessionUser.getCurrent().setAttribute(SessionUser.class,
                                                  null);
        }
        Platform.runLater(() -> {
            userLabel.setText("");
            menuLayout.getChildren().clear();
            if(!isShuttingDown) {
                doLogin();
            }
        });
    }
    private void initializeFooter()
    {
        footer = new HBox();
        footer.setId(getClass().getCanonicalName() + ".footer");
        footerToolBar = new ToolBar();
        footerToolBar.setOrientation(Orientation.HORIZONTAL);
        footerToolBar.setId(getClass().getCanonicalName() + ".footerToolBar");
        statusToolBar = new ToolBar();
        statusToolBar.setId(getClass().getCanonicalName() + ".statusToolBar");
        statusToolBar.getItems().add(new ImageView(new Image("/images/Session_Status_Green.png")));
        statusToolBar.getItems().add(new ImageView(new Image("/images/Session_Status_Green.png")));
        statusToolBar.getItems().add(new ImageView(new Image("/images/Session_Status_Green.png")));
        statusToolBar.getItems().add(new ImageView(new Image("/images/Session_Status_Red.png")));
        clockLabel = new Label();
        clockLabel.setId(getClass().getCanonicalName() + ".clockLabel");
        clockUpdater = new ClockUpdater(clockLabel);
        clockUpdater.start();
        userLabel = new Label();
        userLabel.setId(getClass().getCanonicalName() + ".userLabel");
        Separator footerToolBarSeparator1 = new Separator(Orientation.VERTICAL);
        footerToolBarSeparator1.setId(getClass().getCanonicalName() + ".footerToolBarSeparator1");
        Separator footerToolBarSeparator2 = new Separator(Orientation.VERTICAL);
        footerToolBarSeparator2.setId(getClass().getCanonicalName() + ".footerToolBarSeparator2");
        footerToolBar.getItems().addAll(statusToolBar,
                                        footerToolBarSeparator1,
                                        clockLabel,
                                        footerToolBarSeparator2,
                                        userLabel);
        HBox.setHgrow(footerToolBar,
                      Priority.ALWAYS);
        footer.getChildren().add(footerToolBar);
        styleService.addStyleToAll(footer,
                                   footerToolBar,
                                   footerToolBarSeparator1,
                                   footerToolBarSeparator2,
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
    public static Stage getPrimaryStage()
    {
        return primaryStage;
    }
    public static Region getWorkspace()
    {
        return workspace;
    }
    public static void main(String[] args)
    {
        launch();
    }
    /**
     * holds main stage object
     */
    private static Stage primaryStage;
    private static Scene scene;
    /**
     * indicates if the app is shutting down now
     */
    private boolean isShuttingDown = false;
    /**
     * provides style services
     */
    private StyleService styleService;
    /**
     * web message service value
     */
    private WebMessageService webMessageService;
    private WindowManagerService windowManagerService;
    private VBox menuLayout;
    private ApplicationContext context;
    private VBox root;
    private HBox footer;
    private Label clockLabel;
    private Label userLabel;
    private static VBox workspace;
    private ClockUpdater clockUpdater;
    private ToolBar statusToolBar;
    private ToolBar footerToolBar;
    private PhotonNotificationService notificationService;
}
