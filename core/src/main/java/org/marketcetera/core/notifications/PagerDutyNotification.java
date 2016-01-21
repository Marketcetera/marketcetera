package org.marketcetera.core.notifications;

import java.util.Date;

import org.marketcetera.core.notifications.INotification;

/* $License$ */

/**
 * Represents a notification to be sent to PagerDuty.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: PagerDutyNotification.java 85003 2015-11-13 15:57:55Z colin $
 * @since $Release$
 */
public class PagerDutyNotification
        implements INotification
{
    /**
     * Create a new PagerDutyNotification instance.
     */
    public PagerDutyNotification() {}
    /**
     * Create a new PagerDutyNotification instance.
     *
     * @param inSubject a <code>String</code> value
     * @param inBody a <code>String</code> value
     * @param inSeverity a <code>Severity</code> value
     * @param inDate a <code>Date</code> value
     * @param inOriginator a <code>String</code> value
     * @param inEventType an <code>EventType</code> value
     * @param inIncidentKey a <code>String</code> value
     */
    public PagerDutyNotification(String inSubject,
                                 String inBody,
                                 Severity inSeverity,
                                 Date inDate,
                                 String inOriginator,
                                 EventType inEventType,
                                 String inIncidentKey)
    {
        subject = inSubject;
        body = inBody;
        severity = inSeverity;
        date = inDate;
        originator = inOriginator;
        eventType = inEventType;
        incidentKey = inIncidentKey;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.notifications.INotification#getSubject()
     */
    @Override
    public String getSubject()
    {
        return subject;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.notifications.INotification#getBody()
     */
    @Override
    public String getBody()
    {
        return body;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.notifications.INotification#getSeverity()
     */
    @Override
    public Severity getSeverity()
    {
        return severity;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.notifications.INotification#getDate()
     */
    @Override
    public Date getDate()
    {
        return date;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.notifications.INotification#getOriginator()
     */
    @Override
    public String getOriginator()
    {
        return originator;
    }
    /**
     * Get the eventType value.
     *
     * @return an <code>EventType</code> value
     */
    public EventType getEventType()
    {
        return eventType;
    }
    /**
     * Sets the eventType value.
     *
     * @param an <code>EventType</code> value
     */
    public void setEventType(EventType inEventType)
    {
        eventType = inEventType;
    }
    /**
     * Get the incidentKey value.
     *
     * @return a <code>String</code> value
     */
    public String getIncidentKey()
    {
        return incidentKey;
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
     * Sets the subject value.
     *
     * @param a <code>String</code> value
     */
    public void setSubject(String inSubject)
    {
        subject = inSubject;
    }
    /**
     * Sets the body value.
     *
     * @param a <code>String</code> value
     */
    public void setBody(String inBody)
    {
        body = inBody;
    }
    /**
     * Sets the severity value.
     *
     * @param a <code>Severity</code> value
     */
    public void setSeverity(Severity inSeverity)
    {
        severity = inSeverity;
    }
    /**
     * Sets the date value.
     *
     * @param a <code>Date</code> value
     */
    public void setDate(Date inDate)
    {
        date = inDate;
    }
    /**
     * Sets the originator value.
     *
     * @param a <code>String</code> value
     */
    public void setOriginator(String inOriginator)
    {
        originator = inOriginator;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("PagerDutyNotification [").append(severity).append("]: ").append(subject);
        return builder.toString();
    }
    /**
     * Represents the event types available to PagerDuty.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id: PagerDutyNotification.java 85003 2015-11-13 15:57:55Z colin $
     * @since 1.7.2
     */
    public static enum EventType
    {
        Acknowledge,
        Resolve,
        Trigger;
    }
    /**
     * subject value
     */
    private String subject;
    /**
     * body value
     */
    private String body;
    /**
     * severity value
     */
    private Severity severity;
    /**
     * date value
     */
    private Date date;
    /**
     * originator value
     */
    private String originator;
    /**
     * event type value
     */
    private EventType eventType;
    /**
     * incident key value
     */
    private String incidentKey;
    private static final long serialVersionUID = 5768515572519318305L;
}
