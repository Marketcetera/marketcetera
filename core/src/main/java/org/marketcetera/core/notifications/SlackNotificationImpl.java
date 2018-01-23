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
public class SlackNotificationImpl
        extends Notification
        implements SlackNotification
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
    public SlackNotificationImpl(String inSubject,
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
     * Create a new SlackNotificationImpl instance.
     */
    public SlackNotificationImpl() {}
    /* (non-Javadoc)
     * @see org.marketcetera.core.notifications.NewSlackNotification#shouldSlack()
     */
    @Override
    public boolean shouldSlack()
    {
        return shouldSlack;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.notifications.NewSlackNotification#getSlackWebHookUrl()
     */
    @Override
    public String getSlackWebHookUrl()
    {
        return slackWebHookUrl;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.notifications.NewSlackNotification#getSlackWebHookParams()
     */
    @Override
    public String getSlackWebHookParams()
    {
        return slackWebHookParams;
    }
    /**
     * Sets the shouldSlack value.
     *
     * @param inShouldSlack a <code>boolean</code> value
     */
    public void setShouldSlack(boolean inShouldSlack)
    {
        shouldSlack = inShouldSlack;
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
    /**
     * indicates if the slack notificaton should be sent
     */
    private boolean shouldSlack = true;
    private static final long serialVersionUID = -8553074298965521703L;
}
