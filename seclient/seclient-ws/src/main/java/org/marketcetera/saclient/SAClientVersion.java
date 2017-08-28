package org.marketcetera.saclient;

import org.marketcetera.core.ApplicationVersion;
import org.marketcetera.core.Util;
import org.marketcetera.core.VersionInfo;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.tags.AppId;

/**
 * The SA client version manager.
 *
 * @author anshul@marketcetera.com
 * @version $Id: SAClientVersion.java 16841 2014-02-20 19:59:04Z colin $
 * @since 2.0.0
 */

/* $License$ */

@ClassVersion("$Id: SAClientVersion.java 16841 2014-02-20 19:59:04Z colin $")
public interface SAClientVersion {
    /**
     * The SA client's application ID: the application name.
     */

    static final String APP_ID_NAME = "SAClient"; //$NON-NLS-1$

    /**
     * The SA client's application ID: the version.
     */

    static final VersionInfo APP_ID_VERSION = ApplicationVersion.getVersion(SAClientVersion.class);

    /**
     * The client's application ID: the ID.
     */

    static final AppId APP_ID = Util.getAppId(APP_ID_NAME, APP_ID_VERSION.getVersionInfo());

}