package org.marketcetera.core.notifications;

/* $License$ */

/**
 * Sends notifications to the console.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: EmailNotificationExecutorMethod.java 17879 2019-08-19 17:30:03Z colin $
 * @since $Release$
 */
public class ConsoleNotificationExecutorMethod
        extends AbstractNotificationExecutorMethod
{
    /* (non-Javadoc)
     * @see org.marketcetera.core.notifications.AbstractNotificationExecutorMethod#doNotify(org.marketcetera.core.notifications.INotification)
     */
    @Override
    protected void doNotify(INotification inNotification)
            throws Exception
    {
        System.out.println(String.valueOf(inNotification));
    }
}
