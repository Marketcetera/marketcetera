package org.marketcetera.ors.ws;

import org.apache.commons.lang.ObjectUtils;
import org.marketcetera.client.ClientVersion;
import org.marketcetera.client.IncompatibleComponentsException;
import org.marketcetera.core.ApplicationVersion;
import org.marketcetera.core.Util;
import org.marketcetera.ors.dao.UserService;
import org.marketcetera.ors.security.SimpleUser;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.log.I18NBoundMessage2P;
import org.marketcetera.util.log.I18NBoundMessage3P;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.stateful.Authenticator;
import org.marketcetera.util.ws.stateless.StatelessClientContext;
import org.springframework.beans.factory.annotation.Autowired;

/* $License$ */

/**
 * A session authenticator that uses the database for authentication. 
 *
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */
@ClassVersion("$Id$")
public class DBAuthenticator
        implements Authenticator
{
    /**
     * Checks for compatibility between the given client and server
     * versions.
     *
     * @param clientVersion The client version.
     * @param serverVersion The server version.
     *
     * @return True if the two versions are compatible.
     */
    static boolean compatibleVersions(String clientVersion,
                                      String serverVersion)
    {
        // If the server's version is unknown, any client is allowed.
        return (ApplicationVersion.DEFAULT_VERSION.equals(serverVersion) || ObjectUtils.equals(clientVersion,serverVersion));
    }
    /**
     * Checks if a client with the supplied name is compatible with
     * this server.
     *
     * @param clientName The client name.
     *
     * @return True if the client is compatible with this server.
     */
    static boolean compatibleApp
        (String clientName)
    {
        return ClientVersion.APP_ID_NAME.equals(clientName);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.util.ws.stateful.Authenticator#shouldAllow(org.marketcetera.util.ws.stateless.StatelessClientContext, java.lang.String, char[])
     */
    @Override
    public boolean shouldAllow(StatelessClientContext inContext,
                               String inUsername,
                               char[] inPassword)
            throws I18NException
    {
        String serverVersion = ApplicationVersion.getVersion();
        String clientName = Util.getName(inContext.getAppId());
        String clientVersion = Util.getVersion(inContext.getAppId());
        System.out.println("ServerVersion: " + serverVersion + " clientName: " + clientName + " clientVersion: " + clientVersion);
        if(!compatibleApp(clientName)) {
            throw new IncompatibleComponentsException(new I18NBoundMessage2P(Messages.APP_MISMATCH,
                                                                             clientName,
                                                                             inUsername),
                                                       serverVersion);
        }
        if(!compatibleVersions(clientVersion,
                               serverVersion)) {
            throw new IncompatibleComponentsException(new I18NBoundMessage3P(Messages.VERSION_MISMATCH,
                                                                             clientVersion,
                                                                             serverVersion,
                                                                             inUsername),
                                                       serverVersion);
        }
        SimpleUser u = userService.findByName(inUsername);
        if(u == null || !u.isActive()) {
            Messages.BAD_CREDENTIALS.warn(this,
                                          inUsername);
            return false;
        }
        u.validatePassword(inPassword);
        return true;
    }
    /**
     * allows access to user objects
     */
    @Autowired
    private UserService userService;
}
