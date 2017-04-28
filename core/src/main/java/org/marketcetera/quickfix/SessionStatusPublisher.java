package org.marketcetera.quickfix;

/* $License$ */

/**
 * Publishes changes in FIX session status.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface SessionStatusPublisher
{
    /**
     * Adds the given session status listener to the subscriber list.
     *
     * @param inSessionStatusListener a <code>SessionStatusListener</code> value
     */
    void addSessionStatusListener(SessionStatusListener inSessionStatusListener);
    /**
     * Removes the given session status listener from the subscriber list.
     *
     * @param inSessionStatusListener a <code>SessionStatusListener</code> value
     */
    void removeSessionStatusListener(SessionStatusListener inSessionStatusListener);
}
