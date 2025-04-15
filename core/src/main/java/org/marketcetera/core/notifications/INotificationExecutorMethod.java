package org.marketcetera.core.notifications;

import org.marketcetera.core.ClassVersion;

/* $License$ */

/**
 * Executes notifications.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.4.0
 */
@ClassVersion("$Id$")
public interface INotificationExecutorMethod
{
    /**
     * Sends the given notification.
     *
     * @param inNotification a <code>INotification</code> value
     */
    void notify(INotification inNotification);
}