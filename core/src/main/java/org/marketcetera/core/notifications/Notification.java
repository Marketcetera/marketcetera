package org.marketcetera.core.notifications;

import java.util.Date;

import org.marketcetera.core.ClassVersion;

/* $License$ */

/**
 * A notification of a system event.
 * 
 * <p>It is preferable to subclass this class to create notifications 
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.8.0
 */
@ClassVersion("$Id$")
public class Notification
        implements INotification
{
    /**
     * Returns a <em>low</em> priority <code>Notification</code>.
     *
     * @param inSubject a <code>String</code> value
     * @param inBody a <code>String</code> value
     * @param inOriginator a <code>String</code> value
     * @return a <code>Notification</code> value
     */
    public static Notification low(String inSubject,
                                   String inBody,
                                   String inOriginator)
    {
        return new Notification(inSubject,
                                inBody,
                                new Date(),
                                Severity.LOW,
                                inOriginator);
    }
    /**
     * Returns a <em>medium</em> priority <code>Notification</code>.
     *
     * @param inSubject a <code>String</code> value
     * @param inBody a <code>String</code> value
     * @param inOriginator a <code>String</code> value
     * @return a <code>Notification</code> value
     */
    public static Notification medium(String inSubject,
                                      String inBody,
                                      String inOriginator)
    {
        return new Notification(inSubject,
                                inBody,
                                new Date(),
                                Severity.MEDIUM,
                                inOriginator);
    }
    /**
     * Returns a <em>high</em> priority <code>Notification</code>.
     *
     * @param inSubject a <code>String</code> value
     * @param inBody a <code>String</code> value
     * @param inOriginator a <code>String</code> value
     * @return a <code>Notification</code> value
     */
    public static Notification high(String inSubject,
                                    String inBody,
                                    String inOriginator)
    {
        return new Notification(inSubject,
                                inBody,
                                new Date(),
                                Severity.HIGH,
                                inOriginator);
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
     * @see org.marketcetera.core.notifications.INotification#getDate()
     */
    @Override
    public Date getTimestamp()
    {
        return timestamp;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.notifications.INotification#getOriginator()
     */
    @Override
    public String getOriginator()
    {
        return originator;
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
     * @see org.marketcetera.core.notifications.INotification#getSubject()
     */
    @Override
    public String getSubject()
    {
        return subject;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return String.format("%s Priority Notification at %s:\n%s\n%s", //$NON-NLS-1$
                             getSeverity().toString(),
                             getTimestamp().toString(),
                             getSubject(),
                             getBody());
    }
    /**
     * Sets the subject value.
     *
     * @param inSubject a <code>String</code> value
     */
    public void setSubject(String inSubject)
    {
        subject = inSubject;
    }
    /**
     * Sets the body value.
     *
     * @param inBody a <code>String</code> value
     */
    public void setBody(String inBody)
    {
        body = inBody;
    }
    /**
     * Sets the timestamp value.
     *
     * @param inTimestamp a <code>Date</code> value
     */
    public void setTimestamp(Date inTimestamp)
    {
        timestamp = inTimestamp;
    }
    /**
     * Sets the severity value.
     *
     * @param inSeverity a <code>Severity</code> value
     */
    public void setSeverity(Severity inSeverity)
    {
        severity = inSeverity;
    }
    /**
     * Sets the originator value.
     *
     * @param inOriginator a <code>String</code> value
     */
    public void setOriginator(String inOriginator)
    {
        originator = inOriginator;
    }
    /**
     * Create a new Notification instance.
     *
     * @param inSubject a <code>String</code> value
     * @param inBody a <code>String</code> value
     * @param inTimestamp a <code>Date</code> value
     * @param inSeverity a <code>Severity</code> value
     * @param inOriginator a <cod>String</code> value
     */
    public Notification(String inSubject,
                        String inBody,
                        Date inTimestamp,
                        Severity inSeverity,
                        String inOriginator)
    {
        subject = inSubject;
        body = inBody;
        timestamp = inTimestamp;
        severity = inSeverity;
        originator = inOriginator;
    }
    /**
     * Create a new Notification instance.
     */
    public Notification() {}
    /**
     * the subject of the notification
     */
    private String subject;
    /**
     * the body of the notification
     */
    private String body;
    /**
     * the timestamp of the notification
     */
    private Date timestamp = new Date();
    /**
     * the severity of the notification
     */
    private Severity severity;
    /**
     * the originator of the notification
     */
    private String originator;
    private static final long serialVersionUID = -5858874611465846212L;
}
