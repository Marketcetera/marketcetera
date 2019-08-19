package org.marketcetera.fix;

/* $License$ */

/**
 * Notifies upon change to a {@link FixSession}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface FixSessionListener
{
    /**
     * Announce that the given session has been disabled.
     *
     * @param inSession a <code>FixSession</code> value
     */
    void sessionDisabled(FixSession inSession);
    /**
     * Announce that the given session has been enabled.
     *
     * @param inSession a <code>FixSession</code> value
     */
    void sessionEnabled(FixSession inSession);
    /**
     * Announce that the given session has stopped.
     *
     * @param inSession a <code>FixSession</code> value
     */
    void sessionStopped(FixSession inSession);
    /**
     * Announce that the given session has started.
     *
     * @param inSession a <code>FixSession</code> value
     */
    void sessionStarted(FixSession inSession);
}
