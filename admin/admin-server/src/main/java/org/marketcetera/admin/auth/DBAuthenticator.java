package org.marketcetera.admin.auth;

import org.apache.commons.lang.ObjectUtils;
import org.marketcetera.admin.Messages;
import org.marketcetera.admin.service.UserService;
import org.marketcetera.admin.user.PersistentUser;
import org.marketcetera.core.ApplicationVersion;
import org.marketcetera.core.IncompatibleComponentsException;
import org.marketcetera.core.Util;
import org.marketcetera.core.VersionInfo;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.log.I18NBoundMessage2P;
import org.marketcetera.util.log.I18NBoundMessage3P;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.stateful.Authenticator;
import org.marketcetera.util.ws.stateless.StatelessClientContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/* $License$ */

/**
 * A session authenticator that uses the database for authentication. 
 *
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id: DBAuthenticator.java 17344 2017-08-10 17:40:06Z colin $
 */
@Service
@ClassVersion("$Id: DBAuthenticator.java 17344 2017-08-10 17:40:06Z colin $")
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
    private boolean compatibleVersions(VersionInfo clientVersion,
                                      VersionInfo serverVersion)
    {
	if(!enforceVersionCompatibility) {
	    return true;
	}
        return (ApplicationVersion.DEFAULT_VERSION.equals(clientVersion) || ApplicationVersion.DEFAULT_VERSION.equals(serverVersion) || ObjectUtils.equals(clientVersion,serverVersion));
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
        return true;
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
        VersionInfo serverVersion = ApplicationVersion.getVersion();
        VersionInfo clientVersion = VersionInfo.DEFAULT_VERSION;
        String clientName = Util.getName(inContext.getAppId());
        String clientVersionValue = Util.getVersion(inContext.getAppId());
        if(VersionInfo.isValid(clientVersionValue)) {
            clientVersion = new VersionInfo(clientVersionValue);
        }
        if(!compatibleApp(clientName)) {
            throw new IncompatibleComponentsException(new I18NBoundMessage2P(Messages.APP_MISMATCH,
                                                                             clientName,
                                                                             inUsername),
                                                       serverVersion.getVersionInfo());
        }
        if(!compatibleVersions(clientVersion,
                               serverVersion)) {
            throw new IncompatibleComponentsException(new I18NBoundMessage3P(Messages.VERSION_MISMATCH,
                                                                             clientVersion,
                                                                             serverVersion,
                                                                             inUsername),
                                                       serverVersion.getVersionInfo());
        }
        PersistentUser u = (PersistentUser)userService.findByName(inUsername);
        if(u == null || !u.isActive()) {
            Messages.BAD_CREDENTIALS.warn(this,
                                          inUsername);
            return false;
        }
        u.validatePassword(inPassword);
        return true;
    }
    /**
     * Get the userService value.
     *
     * @return a <code>UserService</code> value
     */
    public UserService getUserService()
    {
        return userService;
    }
    /**
     * Sets the userService value.
     *
     * @param inUserService a <code>UserService</code> value
     */
    @Autowired
    public void setUserService(UserService inUserService)
    {
        userService = inUserService;
    }
    /**
     * Get the enforceVersionCompatibility value.
     *
     * @return a <code>boolean</code> value
     */
    public boolean getEnforceVersionCompatibility()
    {
        return enforceVersionCompatibility;
    }
    /**
     * Sets the enforceVersionCompatibility value.
     *
     * @param inEnforceVersionCompatibility a <code>boolean</code> value
     */
    public void setEnforceVersionCompatibility(boolean inEnforceVersionCompatibility)
    {
        enforceVersionCompatibility = inEnforceVersionCompatibility;
    }
    /**
     * allows access to user objects
     */
    private UserService userService;
    /**
     * indicates whether to enforce version compatibility or not
     */
    private boolean enforceVersionCompatibility = false;
}
