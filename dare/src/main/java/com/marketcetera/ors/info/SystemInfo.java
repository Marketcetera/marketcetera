package com.marketcetera.ors.info;

import org.marketcetera.util.misc.ClassVersion;

import com.marketcetera.ors.history.ReportHistoryServices;

/**
 * A store for system-wide key-value pairs.
 *
 * @author tlerios@marketcetera.com
 * @since 2.0.0
 * @version $Id: SystemInfo.java 16468 2014-05-12 00:36:56Z colin $
 */

/* $License$ */

@ClassVersion("$Id: SystemInfo.java 16468 2014-05-12 00:36:56Z colin $")
public interface SystemInfo
    extends ReadInfo
{

    /**
     * The {@link ReportHistoryServices} key.
     */

    static final String HISTORY_SERVICES=
        "HISTORY_SERVICES"; //$NON-NLS-1$
}
