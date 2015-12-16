package org.marketcetera.core.notifications;

import org.marketcetera.core.notifications.INotification;
import org.marketcetera.core.publisher.IPublisher;

/* $License$ */

/**
 * Transmits notifications to the user.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: NotificationExecutor.java 85003 2015-11-13 15:57:55Z colin $
 * @since 1.3.1
 */
public interface NotificationExecutor
        extends IPublisher
{
    /**
     * Executes the given notification.
     *
     * @param inNotification an <code>INotification</code> value
     */
    public void notify(INotification inNotification);
}
