package org.marketcetera.ui.fixadmin;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.lang3.StringUtils;
import org.marketcetera.brokers.BrokerStatusListener;
import org.marketcetera.fix.ActiveFixSession;
import org.marketcetera.ui.events.LoginEvent;
import org.marketcetera.ui.events.LogoutEvent;
import org.marketcetera.ui.events.NotificationEvent;
import org.marketcetera.ui.service.ServiceManager;
import org.marketcetera.ui.service.SessionUser;
import org.marketcetera.ui.service.WebMessageService;
import org.marketcetera.ui.service.admin.AdminClientService;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.eventbus.Subscribe;

import javafx.application.Platform;

/* $License$ */

/**
 * Monitors FIX sessions and sends notifications.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Component
public class FixSessionWatcher
{
    /**
     * Validate and start the object.
     */
    @PostConstruct
    public void start()
    {
        messageService.register(this);
    }
    /**
     * Stop the object.
     */
    @PreDestroy
    public void stop()
    {
        messageService.unregister(this);
    }
    /**
     * Notify on new login events.
     *
     * @param inEvent a <code>LoginEvent</code> value
     */
    @Subscribe
    public void onLogin(LoginEvent inEvent)
    {
        SLF4JLoggerProxy.trace(this,
                               "{} logged in",
                               inEvent.getSessionUser());
        SessionUser currentUser = inEvent.getSessionUser();
        FixSessionWatcherSubscriber subscriber = currentUser.getAttribute(FixSessionWatcherSubscriber.class);
        if(subscriber == null) {
            subscriber = new FixSessionWatcherSubscriber(currentUser);
            currentUser.setAttribute(FixSessionWatcherSubscriber.class,
                                     subscriber);
        }
        serviceManager.getService(AdminClientService.class).addBrokerStatusListener(subscriber);
    }
    /**
     * Notify on logout events.
     *
     * @param inEvent a <code>LogoutEvent</code> value
     */
    @Subscribe
    public void onLogout(LogoutEvent inEvent)
    {
        SessionUser currentUser = SessionUser.getCurrent();
        SLF4JLoggerProxy.trace(this,
                               "{} logged out",
                               currentUser);
        if(currentUser == null) {
            return;
        }
        FixSessionWatcherSubscriber subscriber = currentUser.getAttribute(FixSessionWatcherSubscriber.class);
        if(subscriber != null) {
            serviceManager.getService(AdminClientService.class).removeBrokerStatusListener(subscriber);
        }
    }
    /**
     * Subscribes to broker status changes and manages updates.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private class FixSessionWatcherSubscriber
            implements BrokerStatusListener
    {
        /* (non-Javadoc)
         * @see org.marketcetera.brokers.BrokerStatusListener#receiveBrokerStatus(org.marketcetera.fix.ActiveFixSession)
         */
        @Override
        public void receiveBrokerStatus(ActiveFixSession inActiveFixSession)
        {
            SLF4JLoggerProxy.trace(FixSessionWatcher.this,
                                   "{} notifying {}",
                                   user,
                                   inActiveFixSession);
            StringBuilder prettyStatus = new StringBuilder();
            for(String statusComponent : inActiveFixSession.getStatus().name().split("_")) {
                prettyStatus.append(StringUtils.lowerCase(statusComponent)).append(' ');
            }
            Platform.runLater(new Runnable() {
                @Override
                public void run()
                {
                    messageService.post(new NotificationEvent(inActiveFixSession.getFixSession().getName() + " " + StringUtils.trim(prettyStatus.toString())));
//                    // TODO this really wants to be a tray notification
//                    Alert a = new Alert(AlertType.INFORMATION);
//                    a.setContentText(inActiveFixSession.getFixSession().getName() + " " + StringUtils.trim(prettyStatus.toString()));
//                    a.show();
                    
                }
            });
        }
        /**
         * Create a new FixSessionWatcherSubscriber instance.
         *
         * @param inUser a <code>SessionUser</code> value
         */
        private FixSessionWatcherSubscriber(SessionUser inUser)
        {
            user = inUser;
        }
        /**
         * session user that owns this listener
         */
        private final SessionUser user;
    }
    /**
     * provides access to client services
     */
    @Autowired
    private ServiceManager serviceManager;
    /**
     * provides access to web message services
     */
    @Autowired
    private WebMessageService messageService;
}
