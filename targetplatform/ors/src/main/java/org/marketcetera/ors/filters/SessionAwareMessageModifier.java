package org.marketcetera.ors.filters;

import org.marketcetera.ors.info.SessionInfo;

/* $License$ */

/**
 * Provides a {@link MessageModifier} implementation that has access to the <code>SessionInfo</code>
 * pertaining to the current request.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface SessionAwareMessageModifier
        extends MessageModifier
{
    /**
     * Gets the session information of the current user.
     *
     * @return a <code>SessionInfo</code> value
     */
    public SessionInfo getSessionInfo();
    /**
     * Sets the session information of the current user.
     *
     * @param inSessionInfo a <code>SessionInfo</code> value
     */
    public void setSessionInfo(SessionInfo inSessionInfo);
}
