package org.marketcetera.fix;

/* $License$ */

/**
 * Provides a mutable {@link ActiveFixSession} implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface MutableActiveFixSession
        extends ActiveFixSession,MutableFixSession
{
    /**
     * Set the cluster instance on which the session is running.
     *
     * @param inInstance a <code>String</code> value
     */
    void setInstance(String inInstance);
    /**
     * Set the next target sequence number.
     *
     * @param inTargetSequenceNumber an <code>int</code> value
     */
    void setTargetSequenceNumber(int inTargetSequenceNumber);
    /**
     * Set the next sender sequence number.
     *
     * @param inSenderSequenceNumber an <code>int</code> value
     */
    void setSenderSequenceNumber(int inSenderSequenceNumber);
    /**
     * Set the session status.
     *
     * @param inSessionStatus a <code>FixSessionStatus</code> value
     */
    void setStatus(FixSessionStatus inFixSessionStatus);
}
