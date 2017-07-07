package org.marketcetera.core.notifications;

import java.util.Date;

/* $License$ */

/**
 * Provides a custom {@link INotification} for {@link PagerDutyNotificationExecutorMethod}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: PagerDutyNotification.java 85003 2015-11-13 15:57:55Z colin $
 * @since $Release$
 */
public class PagerDutyNotificationImpl
        extends Notification
        implements PagerDutyNotification
{
    /**
     * Create a new PagerDutyNotification instance.
     */
    public PagerDutyNotificationImpl() {}
    /**
     * Create a new PagerDutyNotification instance.
     *
     * @param inSubject a <code>String</code> value
     * @param inBody a <code>String</code> value
     * @param inSeverity a <code>Severity</code> value
     * @param inTimestamp a <code>Date</code> value
     * @param inOriginator a <code>String</code> value
     * @param inEventType an <code>EventType</code> value
     * @param inIncidentKey a <code>String</code> value
     */
    public PagerDutyNotificationImpl(String inSubject,
                                     String inBody,
                                     Severity inSeverity,
                                     Date inTimestamp,
                                     String inOriginator,
                                     PagerDutyEventType inEventType,
                                     String inIncidentKey)
    {
        super(inSubject,
              inBody,
              inTimestamp,
              inSeverity,
              inOriginator);
        eventType = inEventType;
        incidentKey = inIncidentKey;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.notifications.NewPagerDutyNotification#getIncidentKey()
     */
    @Override
    public String getIncidentKey()
    {
        return incidentKey;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.notifications.NewPagerDutyNotification#getEventType()
     */
    @Override
    public PagerDutyEventType getEventType()
    {
        return eventType;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.notifications.NewPagerDutyNotification#shouldPagerDuty()
     */
    @Override
    public boolean shouldPagerDuty()
    {
        return shouldPagerDuty;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.notifications.PagerDutyNotification#getPagerDutyUrl()
     */
    @Override
    public String getPagerDutyUrl()
    {
        return pagerDutyUrl;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.notifications.PagerDutyNotification#getServiceKey()
     */
    @Override
    public String getServiceKey()
    {
        return pagerDutyServiceKey;
    }
    /**
     * Sets the eventType value.
     *
     * @param an <code>EventType</code> value
     */
    public void setEventType(PagerDutyEventType inEventType)
    {
        eventType = inEventType;
    }
    /**
     * Sets the incidentKey value.
     *
     * @param a <code>String</code> value
     */
    public void setIncidentKey(String inIncidentKey)
    {
        incidentKey = inIncidentKey;
    }
    /**
     * Sets the shouldPagerDuty value.
     *
     * @param inShouldPagerDuty a <code>boolean</code> value
     */
    public void setShouldPagerDuty(boolean inShouldPagerDuty)
    {
        shouldPagerDuty = inShouldPagerDuty;
    }
    /**
     * Sets the pagerDutyServiceKey value.
     *
     * @param inPagerDutyServiceKey a <code>String</code> value
     */
    public void setPagerDutyServiceKey(String inPagerDutyServiceKey)
    {
        pagerDutyServiceKey = inPagerDutyServiceKey;
    }
    /**
     * Sets the pagerDutyUrl value.
     *
     * @param inPagerDutyUrl a <code>String</code> value
     */
    public void setPagerDutyUrl(String inPagerDutyUrl)
    {
        pagerDutyUrl = inPagerDutyUrl;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("PagerDutyNotification [").append(getSeverity()).append("]: ").append(getSubject());
        return builder.toString();
    }
    /**
     * URL used to identify the pager duty site, may be <code>null</code>, indicating no pager duty notification
     */
    private String pagerDutyUrl;
    /**
     * pager duty service key value
     */
    private String pagerDutyServiceKey;
    /**
     * event type value
     */
    private PagerDutyEventType eventType;
    /**
     * incident key value
     */
    private String incidentKey;
    /**
     * indicates if the notification should send
     */
    private boolean shouldPagerDuty = true;
    private static final long serialVersionUID = 3188544898152082231L;
}
