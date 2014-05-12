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
@ClassVersion("$Id$") //$NON-NLS-1$
public class Notification
        implements INotification
{
    private static final long serialVersionUID = 1L;
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
    /**
     * the subject of the notification
     */
    private final String mSubject;
    /**
     * the body of the notification
     */
    private final String mBody;
    /**
     * the timestamp of the notification
     */
    private final Date mDate;
    /**
     * the severity of the notification
     */
    private final Severity mSeverity;
    /**
     * the originator of the notification
     */
    private final String mOriginator;
    /**
     * Create a new Notification instance.
     */
    protected Notification(String inSubject,
                           String inBody,
                           Date inDate,
                           Severity inSeverity,
                           String inOriginator)
    {
        mSubject = inSubject;
        mBody = inBody;
        mDate = inDate;
        mSeverity = inSeverity;
        mOriginator = inOriginator;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.notifications.INotification#getBody()
     */
    @Override
    public final String getBody()
    {
        return mBody;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.notifications.INotification#getDate()
     */
    @Override
    public final Date getDate()
    {
        return mDate;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.notifications.INotification#getOriginator()
     */
    @Override
    public final String getOriginator()
    {
        return mOriginator;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.notifications.INotification#getSeverity()
     */
    @Override
    public final Severity getSeverity()
    {
        return mSeverity;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.notifications.INotification#getSubject()
     */
    @Override
    public final String getSubject()
    {
        return mSubject;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return String.format("%s Priority Notification at %s:\n%s\n%s", //$NON-NLS-1$
                             getSeverity().toString(),
                             getDate().toString(),
                             getSubject(),
                             getBody());
    }
}
