package org.marketcetera.util.ws.stateful;

import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.stateless.StatelessClientContext;

/**
 * An authenticator of user credentials.
 * 
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id: Authenticator.java 16154 2012-07-14 16:34:05Z colin $
 */

/* $License$ */

@ClassVersion("$Id: Authenticator.java 16154 2012-07-14 16:34:05Z colin $")
public interface Authenticator
{

    /**
     * Checks whether the given credentials can be used to initiate a
     * new session on behalf of the client with the given context.
     *
     * @param inContext The context.
     * @param inUsername The user name.
     * @param inRawPassword The password.
     *
     * @return True if the given credentials are acceptable.
     *
     * @throws I18NException Thrown if the authenticator encounters an
     * error while checking the credentials.
     */

    boolean shouldAllow (StatelessClientContext inContext,
                         String inUsername,
                         char[] inRawPassword)
        throws I18NException;
}
