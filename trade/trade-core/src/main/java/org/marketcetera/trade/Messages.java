package org.marketcetera.trade;

import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.log.I18NMessage1P;
import org.marketcetera.util.log.I18NMessageProvider;
import org.marketcetera.util.misc.ClassVersion;

/**
 * The internationalization constants used by this package.
 *
 * @author klim@marketcetera.com
 * @since 0.6.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public interface Messages
{
    /**
     * The message provider.
     */
    static final I18NMessageProvider PROVIDER = new I18NMessageProvider("trade_core");  //$NON-NLS-1$
    /**
     * The logger.
     */
    static final I18NLoggerProxy LOGGER = new I18NLoggerProxy(PROVIDER);
    /*
     * The messages.
     */
    static final I18NMessage1P ORUM_LOG_ERROR_LOADING_FILE = new I18NMessage1P(LOGGER, "orum_log_error_loading_file");   //$NON-NLS-1$
    static final I18NMessage0P ORUM_LOG_SKIP_LOAD_FILE = new I18NMessage0P(LOGGER, "orum_log_skip_load_file");   //$NON-NLS-1$
}
