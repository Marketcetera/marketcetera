package org.marketcetera.client;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.tags.AppId;
import org.marketcetera.core.Util;
import org.marketcetera.core.ApplicationVersion;

/**
 * The client version manager.
 *
 * @author tlerios@marketcetera.com
 * @since 1.5.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public interface ClientVersion
{

    // CLASS DATA.

    /**
     * The client's application ID: the application name.
     */

    public static final String APP_ID_NAME="Client"; //$NON-NLS-1$

    /**
     * The client's application ID: the version.
     */

    public static final String APP_ID_VERSION=ApplicationVersion.VERSION_2_1_2;

    /**
     * The client's application ID: the ID.
     */

    public static final AppId APP_ID=Util.getAppId(APP_ID_NAME,APP_ID_VERSION);
}
