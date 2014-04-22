package org.marketcetera.saclient.rpc;

import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.ws.stateful.Authenticator;
import org.marketcetera.util.ws.stateless.StatelessClientContext;

/* $License$ */

/**
 * Provides an {@link Authenticator} for testing.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class MockAuthenticator
        implements Authenticator
{
    /* (non-Javadoc)
     * @see org.marketcetera.util.ws.stateful.Authenticator#shouldAllow(org.marketcetera.util.ws.stateless.StatelessClientContext, java.lang.String, char[])
     */
    @Override
    public boolean shouldAllow(StatelessClientContext inContext,
                               String inUser,
                               char[] inPassword)
            throws I18NException
    {
        return true;
    }
}
