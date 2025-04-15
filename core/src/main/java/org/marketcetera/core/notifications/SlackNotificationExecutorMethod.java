package org.marketcetera.core.notifications;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.util.log.SLF4JLoggerProxy;

/* $License$ */

/**
 * Sends notifications to Slack.
 * 
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: SlackNotificationExecutorMethod.java 17411 2018-02-06 21:25:09Z colin $
 * @since 2.4.0
 */
@ClassVersion("$Id: SlackNotificationExecutorMethod.java 17411 2018-02-06 21:25:09Z colin $")
public class SlackNotificationExecutorMethod
        implements INotificationExecutorMethod
{
    /* (non-Javadoc)
     * @see org.marketcetera.core.notifications.INotificationExecutorMethod#notify(org.marketcetera.core.notifications.INotification)
     */
    @Override
    public void notify(INotification inNotification)
    {
        // TODO: This class needs to be updated for HTTPClient 5 in Spring Boot 3
        SLF4JLoggerProxy.warn(this, 
                             "Slack notification disabled during Spring Boot 3 migration: {}", 
                             inNotification);
    }
}