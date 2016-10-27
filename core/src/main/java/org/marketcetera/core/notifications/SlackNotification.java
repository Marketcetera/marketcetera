package org.marketcetera.core.notifications;

import java.util.Date;

/* $License$ */

/**
 * Provides a custom {@link INotification} for {@link SlackNotificationExecutorMethod}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SlackNotification
        extends Notification
{
    /**
     * Create a new SlackNotification instance.
     *
     * @param inSubject a <code>String</code> value
     * @param inBody a <code>String</code> value
     * @param inDate a <code>Date</code> value
     * @param inSeverity a <code>Severity</code> value
     * @param inOriginator a <code>String</code> value
     */
    public SlackNotification(String inSubject,
                             String inBody,
                             Date inDate,
                             Severity inSeverity,
                             String inOriginator)
    {
        super(inSubject,
              inBody,
              inDate,
              inSeverity,
              inOriginator);
    }
    /**
     * Get the slackWebHookUrl value.
     *
     * @return a <code>String</code> value
     */
    public String getSlackWebHookUrl()
    {
        return slackWebHookUrl;
    }
    /**
     * Sets the slackWebHookUrl value.
     *
     * @param inSlackWebHookUrl a <code>String</code> value
     */
    public void setSlackWebHookUrl(String inSlackWebHookUrl)
    {
        slackWebHookUrl = inSlackWebHookUrl;
    }
    /**
     * Get the slackWebHookParams value.
     *
     * @return a <code>String</code> value
     */
    public String getSlackWebHookParams()
    {
        return slackWebHookParams;
    }
    /**
     * Sets the slackWebHookParams value.
     *
     * @param inSlackWebHookParams a <code>String</code> value
     */
    public void setSlackWebHookParams(String inSlackWebHookParams)
    {
        slackWebHookParams = inSlackWebHookParams;
    }
    /**
     * URL used to identify the slack web hook site, may be <code>null</code>, indicating no slack notification
     */
    private String slackWebHookUrl;
    /**
     * extra, optional params that are used for the slack webhook, may be <code>null</code>, indicating not used
     */
    private String slackWebHookParams;
    private static final long serialVersionUID = -4168366776289392789L;
}
