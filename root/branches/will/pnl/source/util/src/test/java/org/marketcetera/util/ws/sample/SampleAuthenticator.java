package org.marketcetera.util.ws.sample;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.stateful.Authenticator;
import org.marketcetera.util.ws.stateless.StatelessClientContext;

/**
 * Sample authenticator. It allows only the given user, with the given
 * password, to login.
 * 
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$") //$NON-NLS-1$
public class SampleAuthenticator
    implements Authenticator
{

    // INSTANCE DATA.

    private String mUser;
    private char[] mPassword;


    // CONSTRUCTORS.

    public SampleAuthenticator
        (String user,
         char[] password)
    {
        mUser=user;
        mPassword=password;
    }


    // INSTANCE METHODS.

    private String getUser()
    {
        return mUser;
    }

    private char[] getPassword()
    {
        return mPassword;
    }


    // Authenticator.

    @Override
    public boolean shouldAllow
        (StatelessClientContext context,
         String user,
         char[] password)
    {
        return (ObjectUtils.equals(getUser(),user) &&
                ArrayUtils.isEquals(getPassword(),password));
    }
}
