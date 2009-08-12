package org.marketcetera.ors.info;

import org.marketcetera.ors.history.ReportHistoryServices;
import org.marketcetera.util.misc.ClassVersion;

/**
 * A store for system-wide key-value pairs.
 *
 * @author tlerios@marketcetera.com
 * @since $Release$
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public interface SystemInfo
    extends ReadInfo
{

    /**
     * The {@link ReportHistoryServices} key.
     */

    static final String HISTORY_SERVICES=
        "HISTORY_SERVICES"; //$NON-NLS-1$
}
