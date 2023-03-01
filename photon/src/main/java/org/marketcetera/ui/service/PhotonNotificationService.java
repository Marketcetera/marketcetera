package org.marketcetera.ui.service;

import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.marketcetera.core.PlatformServices;
import org.marketcetera.core.QueueProcessor;
import org.marketcetera.ui.App;
import org.marketcetera.ui.events.NotificationEvent;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.eventbus.Subscribe;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.stage.Popup;
import javafx.stage.PopupWindow;
import javafx.util.Duration;

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
public class PhotonNotificationService
{
    /**
     * Initialize and start the service.
     */
    @PostConstruct
    public void start()
    {
        SLF4JLoggerProxy.info(this,
                              "Starting {}",
                              PlatformServices.getServiceName(getClass()));
        initializeNotifications();
        webMessageService.register(this);
    }
    /**
     * Stop the service.
     */
    @PreDestroy
    public void stop()
    {
        SLF4JLoggerProxy.info(this,
                              "Stopping {}",
                              PlatformServices.getServiceName(getClass()));
        webMessageService.unregister(this);
        stopNotifications();
    }
    /**
     * Receive a notification to display.
     *
     * @param inEvent a <code>NotificationEvent</code> value
     */
    @Subscribe
    public void onNotification(NotificationEvent inEvent)
    {
        // TODO need to add this attribution:
        // <a target="_blank" href="https://icons8.com/icon/21085/info">Info</a> icon by <a target="_blank" href="https://icons8.com">Icons8</a>
        SLF4JLoggerProxy.debug(this,
                               "Received: {}",
                               inEvent);
        notificationQueue.add(inEvent);
    }
    /**
     * Shows the given notification and causes it to disappear.
     *
     * @param inEvent a <code>NotificationEvent</code> value
     */
    private void showNotification(NotificationEvent inEvent)
    {
        Region workspace = App.getWorkspace();
        ImageView popupIcon = null;
        Label popupMessage = new Label();
        popupMessage.setId(getClass().getCanonicalName() + ".popupMessage");
        GridPane popupPane = new GridPane();
        popupPane.setVgap(5);
        popupPane.setHgap(5);
        popupPane.setId(getClass().getCanonicalName() + ".popupLayout");
        popupPane.setPadding(new Insets(10));
        Popup notification = new Popup();
        notification.getContent().add(popupPane);
        Point2D anchorPoint = workspace.localToScreen(workspace.getWidth(),
                                                      workspace.getHeight());
        notification.setAnchorLocation(PopupWindow.AnchorLocation.WINDOW_BOTTOM_RIGHT);
        switch(inEvent.getAlertType()) {
            case CONFIRMATION:
                popupIcon = new ImageView(new Image("images/icons8-checkmark-40.png"));
                break;
            case ERROR:
                popupIcon = new ImageView(new Image("images/icons8-high-risk-40.png"));
                break;
            case INFORMATION:
                popupIcon = new ImageView(new Image("images/icons8-info-40.png"));
                break;
            case NONE:
                break;
            case WARNING:
                popupIcon = new ImageView(new Image("images/icons8-medium-risk-40.png"));
                break;
            default:
                throw new UnsupportedOperationException();
        }
        if(popupIcon != null) {
            popupIcon.setId(getClass().getCanonicalName() + ".popupIcon");
            popupPane.add(popupIcon,0,0);
            styleService.addStyleToAll(popupIcon);
        }
        popupPane.add(popupMessage,1,0);
        popupMessage.textProperty().set(inEvent.getMessage());
        styleService.addStyleToAll(popupMessage,
                                   popupPane);
        Platform.runLater(() -> {
            FadeTransition popupFadeTransition = new FadeTransition(Duration.millis(5000),
                                                                    popupPane);
            popupFadeTransition.setByValue(-1);
            notification.show(workspace,
                              anchorPoint.getX(),
                              anchorPoint.getY());
            popupFadeTransition.play();
            popupTimer.schedule(new TimerTask() {
                @Override
                public void run()
                {
                    Platform.runLater(() -> {
                        notification.hide();
                    });
                }},5000);
        });
    }
    /**
     * Initialize the notifications system.
     */
    private void initializeNotifications()
    {
        notificationQueue = new NotificationQueueProcessor();
        notificationQueue.start();
        popupTimer = new Timer();
    }
    /**
     * Shut down the notification system.
     */
    private void stopNotifications()
    {
        if(notificationQueue != null) {
            notificationQueue.stop();
            notificationQueue = null;
        }
        if(popupTimer != null) {
            popupTimer.cancel();
        }
        popupTimer = null;
    }
    /**
     *
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private class NotificationQueueProcessor
            extends QueueProcessor<NotificationEvent>
    {
        /* (non-Javadoc)
         * @see org.marketcetera.core.QueueProcessor#processData(java.lang.Object)
         */
        @Override
        protected void processData(NotificationEvent inData)
                throws Exception
        {
            // TODO make this configurable
            while(System.currentTimeMillis() < (lastNotificationDisplayed+5000)) {
                Thread.sleep(250);
            }
            showNotification(inData);
            lastNotificationDisplayed = System.currentTimeMillis();
        }
        private long lastNotificationDisplayed = 0;
        /* (non-Javadoc)
         * @see org.marketcetera.core.QueueProcessor#add(java.lang.Object)
         */
        @Override
        protected void add(NotificationEvent inData)
        {
            super.add(inData);
        }
        /**
         * Create a new NotificationQueueProcessor instance.
         */
        private NotificationQueueProcessor()
        {
            super(PlatformServices.getServiceName(PhotonNotificationService.class)+ "-NotificationThread");
        }
    }
    private NotificationQueueProcessor notificationQueue;
    /**
     * timer used to close notifications later
     */
    private Timer popupTimer;
    /**
     * web message service value
     */
    @Autowired
    private WebMessageService webMessageService;
    /**
     * provides access to style services
     */
    @Autowired
    private StyleService styleService;
}
