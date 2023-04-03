package org.marketcetera.ui;

import java.awt.Taskbar;
import java.awt.Taskbar.Feature;
import java.awt.Toolkit;

import org.marketcetera.ui.events.LoginEvent;
import org.marketcetera.ui.events.LogoutEvent;
import org.marketcetera.ui.service.PhotonNotificationService;
import org.marketcetera.ui.service.SessionUser;
import org.marketcetera.ui.service.StyleService;
import org.marketcetera.ui.service.UiMessageService;
import org.marketcetera.ui.view.ApplicationMenu;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.google.common.eventbus.Subscribe;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/* $License$ */

/**
 * Main Photon Application.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 * @see https://openjfx.io/openjfx-docs/#maven
 */
public class PhotonApp
        extends Application
{
    /**
     * Main application entry point.
     *
     * @param args a <code>String[]</code> value
     */
    public static void main(String[] args)
    {
        launch();
    }
    /* (non-Javadoc)
     * @see javafx.application.Application#init()
     */
    @Override
    public void init()
            throws Exception
    {
        instance = this;
        super.init();
        applicationContext = new AnnotationConfigApplicationContext("org.marketcetera","com.marketcetera");
        webMessageService = applicationContext.getBean(UiMessageService.class);
        styleService = applicationContext.getBean(StyleService.class);
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
        root = new VBox();
        menuLayout = new VBox();
        workspace = new Pane();
        workspace.setId(getClass().getCanonicalName() + ".workspace");
        workspace.setPrefWidth(1024);
        workspace.setPrefHeight(768);
        initializeFooter();
        Separator separator = new Separator(Orientation.HORIZONTAL);
        root.getChildren().addAll(menuLayout,
                                  workspace,
                                  separator,
                                  footer);
        scene = new Scene(root);
        inPrimaryStage.setScene(scene);
        inPrimaryStage.setTitle("Marketcetera Automated Trading Platform");
        inPrimaryStage.getIcons().addAll(new Image("/images/photon-16x16.png"),
                                         new Image("/images/photon-24x24.png"),
                                         new Image("/images/photon-32x32.png"),
                                         new Image("/images/photon-48x48.png"),
                                         new Image("/images/photon-48x48.png"),
                                         new Image("/images/photon-64x64.png"),
                                         new Image("/images/photon-128x128.png"));
        if(Taskbar.isTaskbarSupported()) {
            Taskbar taskbar = Taskbar.getTaskbar();
            if(taskbar.isSupported(Feature.ICON_IMAGE)) {
                final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
                java.awt.Image dockIcon = defaultToolkit.getImage(getClass().getResource("/images/photon-128x128.png"));
                taskbar.setIconImage(dockIcon);
            }
        }
        inPrimaryStage.setOnCloseRequest(closeEvent -> {
            doAppShutdown();
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
        scene.getStylesheets().clear();
        scene.getStylesheets().add("dark-mode.css");
        inPrimaryStage.show();
        doLogin();
    }
    /**
     * Shutdown the app.
     */
    public void doAppShutdown()
    {
        isShuttingDown = true;
        webMessageService.post(new LogoutEvent());
        try {
            ((ConfigurableApplicationContext)applicationContext).close();
        } catch (Exception ignored) {}
        Platform.exit();
        System.exit(0);
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
        notificationService = applicationContext.getBean(PhotonNotificationService.class);
        clientStatusUpdater = applicationContext.getBean(ClientStatusUpdater.class,
                                                         clientStatusImageView);
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
        if(clientStatusUpdater != null) {
            clientStatusUpdater.stop();
            clientStatusUpdater = null;
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
    /**
     * Get the primary application stage.
     *
     * @return a <code>Stage</code> value
     */
    public static Stage getPrimaryStage()
    {
        return primaryStage;
    }
    /**
     * Get the main workspace.
     *
     * @return a <code>Pane</code> value
     */
    public static Pane getWorkspace()
    {
        return workspace;
    }
    /**
     * Get the main singleton application instance.
     *
     * @return a <code>PhotonApp</code> value
     */
    public static PhotonApp getApp()
    {
        return instance;
    }
    /**
     * Initialize the workspace footer.
     */
    private void initializeFooter()
    {
        footer = new HBox();
        footer.setId(getClass().getCanonicalName() + ".footer");
        footerToolBar = new ToolBar();
        footerToolBar.setOrientation(Orientation.HORIZONTAL);
        footerToolBar.setId(getClass().getCanonicalName() + ".footerToolBar");
        statusToolBar = new ToolBar();
        statusToolBar.setId(getClass().getCanonicalName() + ".statusToolBar");
        clientStatusImageView = new ImageView(new Image("/images/LedNone.gif"));
        statusToolBar.getItems().add(clientStatusImageView);
        clockLabel = new Label();
        clockLabel.setId(getClass().getCanonicalName() + ".clockLabel");
        // create the clock updater service, though we don't need to refer to it hereafter
        applicationContext.getBean(ClockUpdater.class,
                                   clockLabel);
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
    /**
     * Build and show the main application menu.
     */
    private void showMenu()
    {
        SessionUser currentUser = SessionUser.getCurrent();
        if(currentUser == null) {
            return;
        }
        ApplicationMenu applicationMenu = currentUser.getAttribute(ApplicationMenu.class);
        if(applicationMenu == null) {
            SLF4JLoggerProxy.debug(PhotonApp.class,
                                   "Session is now logged in, building application menu");
            applicationMenu = applicationContext.getBean(ApplicationMenu.class);
            menuLayout.getChildren().add(applicationMenu.getMenu());
            SessionUser.getCurrent().setAttribute(ApplicationMenu.class,
                                                  applicationMenu);
        }
        applicationMenu.refreshMenuPermissions();
    }
    /**
     * Perform the login sequence, including showing the login dialog to the user.
     */
    private void doLogin()
    {
        LoginView loginView = applicationContext.getBean(LoginView.class);
        loginView.showAndWait();
        showMenu();
    }
    /**
     * footer holder for the server connection status image
     */
    private ImageView clientStatusImageView;
    /**
     * service to update the server connection status in the footer
     */
    private ClientStatusUpdater clientStatusUpdater;
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
    private UiMessageService webMessageService;
    private VBox menuLayout;
    private ApplicationContext applicationContext;
    private VBox root;
    private HBox footer;
    private Label clockLabel;
    private Label userLabel;
    private static Pane workspace;
    private ToolBar statusToolBar;
    private ToolBar footerToolBar;
    private PhotonNotificationService notificationService;
    /**
     * singleton applcation isntance
     */
    private static PhotonApp instance;
}
