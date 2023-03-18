package org.marketcetera.ui.service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.controlsfx.control.Notifications;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.ui.PhotonApp;
import org.marketcetera.ui.events.NotificationEvent;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.eventbus.Subscribe;

import javafx.application.Platform;
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
@EnableAutoConfiguration
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
    }
    /**
     * Receive a notification to display.
     *
     * @param inEvent a <code>NotificationEvent</code> value
     */
    @Subscribe
    public void onNotification(NotificationEvent inEvent)
    {
        SLF4JLoggerProxy.debug(this,
                               "Received: {}",
                               inEvent);
        Platform.runLater(new Runnable() {
            @Override
            public void run()
            {
                Notifications notification = Notifications.create()
                        .darkStyle()
                        .title(inEvent.getTitle())
                        .text(inEvent.getMessage())
                        .owner(PhotonApp.getWorkspace())
                        .hideAfter(Duration.seconds(notificationDelay));
                notification.threshold(notificationThreshold,
                                       notification);
                switch(inEvent.getAlertType()) {
                    case CONFIRMATION:
                        notification.showConfirm();
                        break;
                    case ERROR:
                        notification.showError();
                        break;
                    case INFORMATION:
                        notification.showInformation();
                        break;
                    case NONE:
                        notification.show();
                        break;
                    case WARNING:
                        notification.showWarning();
                        break;
                    default:
                        throw new UnsupportedOperationException();
                }
            }}
        );
    }
    /**
     * notifications delay in seconds
     */
    @Value("${metc.notifications.delay.seconds:3}")
    private int notificationDelay;
    /**
     * notifications delay in seconds
     */
    @Value("${metc.notifications.threshold:10}")
    private int notificationThreshold;
    /**
     * web message service value
     */
    @Autowired
    private WebMessageService webMessageService;
}
