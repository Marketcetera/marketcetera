package org.marketcetera.ors.filters;

import org.marketcetera.core.CoreException;
import org.marketcetera.ors.security.SimpleUser;
import org.marketcetera.util.misc.ClassVersion;

import quickfix.Message;

/* $License$ */

/**
 * Determines whether an outgoing order should be accepted or rejected.
 *
 * @author tlerios@marketcetera.com
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @since 1.0.0
 * @version $Id$
 */
@ClassVersion("$Id$")
public interface OrderFilter
{
    /**
     * Indicates if the given message should be accepted or not.
     * 
     * <p>Implementers can either throw a <code>CoreException</code> with a
     * custom message or return <code>false</code> to reject a message.
     *
     * @param inMessageInfo a <code>MessageInfo</code> value
     * @param inMessage a <code>Message</code> value
     * @return a <code>boolean</code> value
     * @throws CoreException if the message should be rejected
     */
    public boolean isAccepted(MessageInfo inMessageInfo,
                              Message inMessage)
            throws CoreException;
    /**
     * Contains meta information about an outgoing message.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    public interface MessageInfo
    {
        /**
         * Gets the user which is sending the message.
         *
         * @return a <code>SimpleUser</code> value
         */
        public SimpleUser getUser();
    }
}
