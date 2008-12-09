package org.marketcetera.ors.history;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.log.*;

/* $License$ */
/**
 * Internationalized messages for this package.
 *
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public interface Messages {
    /**
     * The message provider
     */
    static final I18NMessageProvider PROVIDER =
            new I18NMessageProvider("ors_history",  //$NON-NLS-1$
                    Messages.class.getClassLoader());
    /**
     * The message logger.
     */
    static final I18NLoggerProxy LOGGER =
            new I18NLoggerProxy(PROVIDER);

    static final I18NMessage2P LOG_ROOT_ID_NOT_FOUND =
            new I18NMessage2P(LOGGER, "log_root_id_not_found");  //$NON-NLS-1$
    static final I18NMessage1P ERROR_RECONSTITUTE_FIX_MSG =
            new I18NMessage1P(LOGGER, "error_reconstitute_fix_msg");   //$NON-NLS-1$

}