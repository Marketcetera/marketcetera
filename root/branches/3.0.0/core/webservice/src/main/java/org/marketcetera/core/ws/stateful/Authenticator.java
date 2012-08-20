package org.marketcetera.core.ws.stateful;

import org.marketcetera.core.util.except.I18NException;
import org.marketcetera.core.ws.stateless.StatelessClientContext;

/**
 * An authenticator of user credentials.
 * 
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id: Authenticator.java 82324 2012-04-09 20:56:08Z colin $
 */

/* $License$ */

public interface Authenticator
{

    /**
     * Checks whether the given credentials can be used to initiate a
     * new session on behalf of the client with the given context.
     *
     * @param context The context.
     * @param user The user name.
     * @param password The password.
     *
     * @return True if the given credentials are acceptable.
     *
     * @throws I18NException Thrown if the authenticator encounters an
     * error while checking the credentials.
     */

    boolean shouldAllow
        (StatelessClientContext context,
         String user,
         char[] password)
        throws I18NException;
}
