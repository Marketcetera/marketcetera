package org.marketcetera.rpc;

import java.util.HashMap;
import java.util.Map;

import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.ws.stateful.Authenticator;
import org.marketcetera.util.ws.stateless.StatelessClientContext;

/* $License$ */

/**
 * Provides a test {@link Authenticator} implementation.
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
        if(userstore.containsKey(inUser)) {
            String password = userstore.get(inUser);
            if(password == null) {
                return true;
            }
            return password.equals(new String(inPassword));
        }
        return false;
    }
    /**
     * Get the userstore value.
     *
     * @return a <code>Map&lt;String,String&gt;</code> value
     */
    public Map<String,String> getUserstore()
    {
        return userstore;
    }
    /**
     * userstore used to validate logins
     */
    private final Map<String,String> userstore = new HashMap<>();
}
