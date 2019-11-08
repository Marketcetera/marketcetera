package org.marketcetera.web.fixadmin;

import javax.annotation.PostConstruct;

import org.marketcetera.brokers.BrokerStatusListener;
import org.marketcetera.fix.ActiveFixSession;
import org.marketcetera.web.service.ServiceManager;
import org.marketcetera.web.service.admin.AdminClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

/* $License$ */

/**
 * Monitors FIX sessions and sends notifications.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FixSessionWatcher
        implements BrokerStatusListener
{
    // TODO when/how to start this, when to stop it?, do all sessions get the notification?
    /**
     * Validate and start the object.
     */
    @PostConstruct
    public void start()
    {
        serviceManager.getService(AdminClientService.class).addBrokerStatusListener(this);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.BrokerStatusListener#receiveBrokerStatus(org.marketcetera.fix.ActiveFixSession)
     */
    @Override
    public void receiveBrokerStatus(ActiveFixSession inActiveFixSession)
    {
        Notification.show(inActiveFixSession.getFixSession().getName() + " " + inActiveFixSession.getStatus(),
                          Type.TRAY_NOTIFICATION);
    }
    /**
     * provides access to client services
     */
    @Autowired
    private ServiceManager serviceManager;
}
