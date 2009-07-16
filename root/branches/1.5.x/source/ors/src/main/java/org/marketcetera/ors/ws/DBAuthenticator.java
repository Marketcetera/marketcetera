package org.marketcetera.ors.ws;

import org.apache.commons.lang.ObjectUtils;
import org.marketcetera.client.ClientVersion;
import org.marketcetera.core.ApplicationVersion;
import org.marketcetera.ors.security.SimpleUser;
import org.marketcetera.ors.security.SingleSimpleUserQuery;
import org.marketcetera.persist.NoResultException;
import org.marketcetera.persist.PersistenceException;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.log.I18NBoundMessage3P;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.stateful.Authenticator;
import org.marketcetera.util.ws.stateless.StatelessClientContext;

/**
 * A session authenticator that uses the database for authentication. 
 *
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class DBAuthenticator
    implements Authenticator
{

    // CLASS DATA.

    private static final String VERSION_1_5_0=
        "1.5.0"; //$NON-NLS-1$
    private static final String VERSION_1_5_1=
        "1.5.1"; //$NON-NLS-1$


    // CLASS METHODS.

    /**
     * Checks for compatibility between the given client and server
     * versions.
     *
     * @param clientVersion The client version.
     * @param serverVersion The server version.
     *
     * @return True if the two versions are compatible.
     */

    static boolean compatibleVersions
        (String clientVersion,
         String serverVersion)
    {
        // If the server's version is unknown, any client is allowed.
        return (ApplicationVersion.DEFAULT_VERSION.equals(serverVersion) ||
                ObjectUtils.equals(clientVersion,serverVersion) ||
                (VERSION_1_5_0.equals(clientVersion) && //
                 VERSION_1_5_1.equals(serverVersion)));
    }


    // Authenticator.

    @Override
    public boolean shouldAllow
        (StatelessClientContext context,
         String user,
         char[] password)
        throws I18NException
    {
        String serverVersion=ApplicationVersion.getVersion();
        String clientVersion=ClientVersion.getVersion(context.getAppId());
        if (!compatibleVersions(clientVersion,serverVersion)) {
            throw new I18NException
                (new I18NBoundMessage3P(Messages.VERSION_MISMATCH,
                                        clientVersion,serverVersion,user));
        }
        try {
            SimpleUser u=new SingleSimpleUserQuery(user).fetch();
            if (!u.isActive()) {
                Messages.BAD_CREDENTIALS.warn(this,user);
                return false;
            }
            u.validatePassword(password);
        } catch (NoResultException ex) {
            Messages.BAD_CREDENTIALS.warn(this,ex,user);
            return false;
        } catch (PersistenceException ex) {
            Messages.BAD_CREDENTIALS.warn(this,ex,user);
            return false;
        }
        return true;
    }
}
