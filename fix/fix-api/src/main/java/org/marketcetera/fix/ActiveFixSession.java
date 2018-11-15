package org.marketcetera.fix;

/* $License$ */

/**
 * Provides information about an active FIX Session.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface ActiveFixSession
        extends FixSession
{
    /**
     * Get the cluster instance on which the session is running.
     *
     * @return a <code>String</code> value
     */
    String getInstance();
    /**
     * Get the next target sequence number.
     *
     * @return an <code>int</code> value
     */
    int getTargetSequenceNumber();
    /**
     * Get the next sender sequence number.
     *
     * @return an <code>int</code> value
     */
    int getSenderSequenceNumber();
    /**
     * Get the session status.
     *
     * @return a <code>FixSessionStatus</code> value
     */
    FixSessionStatus getStatus();
}
