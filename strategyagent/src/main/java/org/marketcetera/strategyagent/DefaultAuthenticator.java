package org.marketcetera.strategyagent;

import org.apache.commons.lang.ObjectUtils;
import org.marketcetera.client.ClientManager;
import org.marketcetera.core.ApplicationVersion;
import org.marketcetera.core.Util;
import org.marketcetera.saclient.SAClientVersion;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.log.I18NBoundMessage2P;
import org.marketcetera.util.log.I18NBoundMessage3P;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.stateful.Authenticator;
import org.marketcetera.util.ws.stateless.StatelessClientContext;

/* $License$ */

/**
 * Provides authentication services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class DefaultAuthenticator
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
        // Verify client version
        String serverVersion = ApplicationVersion.getVersion();
        String clientName = Util.getName(inContext.getAppId());
        String clientVersion = Util.getVersion(inContext.getAppId());
        if(!compatibleApp(clientName)) {
            throw new I18NException(new I18NBoundMessage2P(Messages.APP_MISMATCH,
                                                           clientName,
                                                           inUser));
        }
        if(!compatibleVersions(clientVersion,
                               serverVersion)) {
            throw new I18NException(new I18NBoundMessage3P(Messages.VERSION_MISMATCH,
                                                           clientVersion,
                                                           serverVersion,
                                                           inUser));
        }
        // Use client to carry out authentication.
        return ClientManager.getInstance().isCredentialsMatch(inUser,
                                                              inPassword);
    }
    /**
     * Checks for compatibility between the given client and server
     * versions.
     *
     * @param clientVersion The client version.
     * @param serverVersion The server version.
     * @return True if the two versions are compatible.
     */
    private static boolean compatibleVersions(String clientVersion,
                                              String serverVersion)
    {
        // If the server's version is unknown, any client is allowed.
        return (ApplicationVersion.DEFAULT_VERSION.equals(serverVersion) || ObjectUtils.equals(clientVersion, serverVersion));
    }
    /**
     * Checks if a client with the supplied name is compatible with this server.
     *
     * @param clientName The client name.
     *
     * @return True if a client with the supplied name is compatible with this
     * server.
     */
    private static boolean compatibleApp(String clientName)
    {
        return SAClientVersion.APP_ID_NAME.equals(clientName);
    }
}
