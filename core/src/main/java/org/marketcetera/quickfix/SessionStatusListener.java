package org.marketcetera.quickfix;

/* $License$ */

/**
 * Listens for changes in session status.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface SessionStatusListener
{
    /**
     * Accepts an updated session status notification value.
     *
     * @param inNotification a <code>SessionStatusNotification</code> value
     */
    void receiveSessionStatus(SessionStatus inNotification);
}
