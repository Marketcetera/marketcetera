package org.marketcetera.ui.service;

import java.util.concurrent.TimeUnit;

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

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.eventbus.Subscribe;

import javafx.application.Platform;
import javafx.util.Duration;

/* $License$ */

/**
 * Provides notification services for the Photon platform.
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
        notificationCache = CacheBuilder.newBuilder().expireAfterAccess(duplicateNotificationCacheTtl,TimeUnit.SECONDS).build();
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
        String notificationHash = getNotificationHash(inEvent);
        try {
            if(notificationCache.getIfPresent(notificationHash) == null) {
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
        } finally {
            notificationCache.put(notificationHash,
                                  inEvent);
        }
    }
    /**
     * Creates a notification hash from the given notification.
     *
     * @param inEvent a <code>NotificationEvent</code> value
     * @return a <code>String</code> value
     */
    private String getNotificationHash(NotificationEvent inEvent)
    {
        StringBuilder rawValue = new StringBuilder();
        rawValue.append(inEvent.getAlertType().name()).append("-").append(inEvent.getTitle()).append("-").append(inEvent.getMessage());
        return rawValue.toString();
    }
    /**
     * caches notifications to make sure that duplicate tray events don't overwhelm the screen
     */
    private Cache<String,NotificationEvent> notificationCache;
    /**
     * notifications delay in seconds
     */
    @Value("${metc.notifications.delay.seconds:3}")
    private int notificationDelay;
    /**
     * notifications threshold for alerts
     */
    @Value("${metc.notifications.threshold:10}")
    private int notificationThreshold;
    /**
     * indicates how long to retain notifications in cache to prevent re-notification if no change, in seconds
     */
    @Value("${metc.duplicate.notification.cache.ttl:10}")
    private int duplicateNotificationCacheTtl;
    /**
     * web message service value
     */
    @Autowired
    private UiMessageService webMessageService;
}
