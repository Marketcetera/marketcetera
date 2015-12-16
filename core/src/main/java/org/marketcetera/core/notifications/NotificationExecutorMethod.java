package org.marketcetera.core.notifications;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface NotificationExecutorMethod
{
    /**
     * Executes the given notification.
     *
     * @param inNotification an <code>INotification</code> value
     */
    public void notify(INotification inNotification);
}
