package org.marketcetera.core.notifications;

/* $License$ */

/**
 * Provides an {@link INotification} implementation for Slack.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface SlackNotification
        extends INotification
{
    /**
     * Web hook URL to use for slack notifications.
     *
     * @return a <code>String</code> value
     */
    String getSlackWebHookUrl();
    /**
     * Optional web hook params used for slack notifications.
     *
     * @return a <code>String</code> value
     */
    String getSlackWebHookParams();
    /**
     * Indicate if this notification should be sent via slack.
     *
     * @return a <code>boolean</code> value
     */
    boolean shouldSlack();
}
