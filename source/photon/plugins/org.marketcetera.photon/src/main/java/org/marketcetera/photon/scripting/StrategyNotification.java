package org.marketcetera.photon.scripting;

import java.util.Date;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.notifications.INotification;

/* $License$ */

/**
 * <code>INotification</code> implementation used by {@link Strategy}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: $
 * @since $Release$
 */
@ClassVersion("$Id: OrderBook.java 9477 2008-08-08 23:38:47Z klim $") //$NON-NLS-1$
public class StrategyNotification
        implements INotification
{
    /**
     * notification body
     */
    private final String mBody;
    /**
     * notification date
     */
    private final Date mDate;
    /**
     * originating class
     */
    private final Class<StrategyNotification> mOriginator = StrategyNotification.class;
    /**
     * notification severity
     */
    private final Severity mSeverity;
    /**
     * notification subject
     */
    private final String mSubject;
    /**
     * Create a <code>StrategyNotification</code> object of severity <code>debug</code>.
     *
     * @param inSubject a <code>String</code> value
     * @param inBody a <code>String</code> value
     * @return a <code>StrategyNotification</code> value
     */
    static StrategyNotification low(String inSubject,
                                    String inBody)
    {
        return new StrategyNotification(inSubject,
                                        inBody,
                                        Severity.LOW);
    }
    /**
     * Create a <code>StrategyNotification</code> object of severity <code>info</code>.
     *
     * @param inSubject a <code>String</code> value
     * @param inBody a <code>String</code> value
     * @return a <code>StrategyNotification</code> value
     */
    static StrategyNotification medium(String inSubject,
                                       String inBody)
    {
        return new StrategyNotification(inSubject,
                                        inBody,
                                        Severity.MEDIUM);
    }
    /**
     * Create a <code>StrategyNotification</code> object of severity <code>error</code>.
     *
     * @param inSubject a <code>String</code> value
     * @param inBody a <code>String</code> value
     * @return a <code>StrategyNotification</code> value
     */
    static StrategyNotification high(String inSubject,
                                     String inBody)
    {
        return new StrategyNotification(inSubject,
                                        inBody,
                                        Severity.HIGH);
    }
    /**
     * Create a new StrategyNotification instance.
     *
     * @param inSubject a <code>String</code> value
     * @param inBody a <code>String</code> value
     * @param inSeverity a <code>Severity</code> value
     */
    private StrategyNotification(String inSubject,
                                 String inBody,
                                 Severity inSeverity)
    {
        mSubject = inSubject;
        mBody = inBody;
        mSeverity = inSeverity;
        mDate = new Date();
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
    public final Class<?> getOriginator()
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
    public final String toString()
    {
        return String.format("Photon %s at %s:\n%s\n%s", //$NON-NLS-1$
                             getSeverity().toString(),
                             getDate().toString(),
                             getSubject(),
                             getBody());
    }
}
