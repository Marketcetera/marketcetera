package org.marketcetera.core.notifications;

import org.marketcetera.core.publisher.IPublisher;

/* $License$ */

/**
 * Transmits notifications to the user.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
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
