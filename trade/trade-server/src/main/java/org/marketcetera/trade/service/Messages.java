package org.marketcetera.trade.service;

import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.log.I18NMessage1P;
import org.marketcetera.util.log.I18NMessageProvider;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */
/**
 * Internationalized messages for this package.
 *
 * @author anshul@marketcetera.com
 * @version $Id: Messages.java 16468 2014-05-12 00:36:56Z colin $
 * @since 1.0.0
 */
@ClassVersion("$Id: Messages.java 16468 2014-05-12 00:36:56Z colin $")
public interface Messages {
    /**
     * The message provider
     */
    static final I18NMessageProvider PROVIDER = new I18NMessageProvider("trade_server",  //$NON-NLS-1$
                                                                        Messages.class.getClassLoader());
    /**
     * The message logger.
     */
    static final I18NLoggerProxy LOGGER = new I18NLoggerProxy(PROVIDER);
    static final I18NMessage1P ERROR_RECONSTITUTE_FIX_MSG = new I18NMessage1P(LOGGER, "error_reconstitute_fix_msg");   //$NON-NLS-1$
    static final I18NMessage0P UNAVAILABLE_BROKER = new I18NMessage0P(LOGGER,"unavailable_broker"); //$NON-NLS-1$
    static final I18NMessage1P NO_BROKER_SELECTED = new I18NMessage1P(LOGGER,"no_broker_selected");
}
