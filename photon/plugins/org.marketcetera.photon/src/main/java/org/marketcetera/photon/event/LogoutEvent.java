package org.marketcetera.photon.event;

/* $License$ */

/**
 * Indicates that a user has successfully logged in.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class LogoutEvent
{
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return LogoutEvent.class.getSimpleName();
    }
}
