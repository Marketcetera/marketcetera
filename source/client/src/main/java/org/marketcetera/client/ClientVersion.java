package org.marketcetera.client;

import org.apache.commons.lang.ObjectUtils;
import org.marketcetera.core.ApplicationVersion;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.tags.AppId;

/**
 * The client version manager.
 *
 * @author tlerios@marketcetera.com
 * @since 1.5.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public final class ClientVersion
{

    // CLASS DATA.

    /**
     * The client's application ID: the application name.
     */

    public static final String APP_ID_NAME="Client"; //$NON-NLS-1$

    /**
     * The client's application ID: the version separator.
     */

    public static final String APP_ID_VERSION_SEPARATOR="/"; //$NON-NLS-1$

    /**
     * The client's application ID: the version.
     */

    public static final String APP_ID_VERSION="1.5.0"; //$NON-NLS-1$

    /**
     * The client's application ID: the ID.
     */

    public static final AppId APP_ID=
        new AppId(APP_ID_NAME+APP_ID_VERSION_SEPARATOR+APP_ID_VERSION);


    // CLASS METHODS.

    /**
     * Returns the version portion of the given application ID.
     *
     * @param id The application ID. It may be null.
     *
     * @return The version portion. It may be null if the provided ID
     * lacks a version.
     */

    public static String getVersion
        (AppId id)
    {
        if ((id==null) || (id.getValue()==null)) {
            return null;
        }
        int index=id.getValue().indexOf(APP_ID_VERSION_SEPARATOR);
        if (index==-1) {
            return null;
        }
        index++;
        if (index>=id.getValue().length()) {
            return null;
        }
        return id.getValue().substring(index);
    }

    /**
     * Checks for compatibility between the given client and server
     * versions.
     *
     * @param clientVersion The client version.
     * @param serverVersion The server version.
     *
     * @return True if the two versions are compatible.
     */

    public static boolean compatibleVersions
        (String clientVersion,
         String serverVersion)
    {
        // If the server's version is unknown, any client is allowed.
        return (ApplicationVersion.DEFAULT_VERSION.equals(serverVersion) ||
                ObjectUtils.equals(clientVersion,serverVersion));            
    }


    // CONSTRUCTOR.

    /**
     * Constructor. It is private so that no instances can be created.
     */

    private ClientVersion() {}
}
