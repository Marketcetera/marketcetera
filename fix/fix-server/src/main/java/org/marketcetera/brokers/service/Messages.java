package org.marketcetera.brokers.service;

import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessage1P;
import org.marketcetera.util.log.I18NMessageProvider;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */
/**
 * Internationalized messages for this package.
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
    static final I18NMessageProvider PROVIDER = new I18NMessageProvider("fix_server",  //$NON-NLS-1$
                                                                        Messages.class.getClassLoader());
    /**
     * The message logger.
     */
    static final I18NLoggerProxy LOGGER = new I18NLoggerProxy(PROVIDER);
    static final I18NMessage1P UNKNOWN_BROKER_ID = new I18NMessage1P(LOGGER,"unknown_broker_id"); //$NON-NLS-1$
}
