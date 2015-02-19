package org.marketcetera.client;

import org.apache.commons.lang.ObjectUtils;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.stateful.Authenticator;
import org.marketcetera.util.ws.stateless.StatelessClientContext;

/**
 * A test authenticator to aid testing of Client via {@link
 * MockServer}. It allows login when the username is the same as the
 * password.
 * 
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$") //$NON-NLS-1$
public class MockAuthenticator 
    implements Authenticator
{

    // CLASS DATA.

    static String VERSION_MISMATCH_USER=
        "VERSION_MISMATCH_USER";
    static String VERSION_MISMATCH_SERVER_VERSION=
        "VERSION_MISMATCH_SERVER_VERSION";


    // Authenticator.

    @Override
    public boolean shouldAllow
        (StatelessClientContext context,
         String user,
         char[] password)
        throws IncompatibleComponentsException
    {
        if (VERSION_MISMATCH_USER.equals(user)) {
            throw new IncompatibleComponentsException
                (TestMessages.MOCK_VERSION_MISMATCH,
                 VERSION_MISMATCH_SERVER_VERSION);
        }
        return ObjectUtils.equals(user,String.valueOf(password));
    }
}
