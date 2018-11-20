package org.marketcetera.dataflow.client.rpc;

import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessage1P;
import org.marketcetera.util.log.I18NMessageProvider;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */
/**
 * Internationalized Message keys for classes in this package.
 *
 * @author anshul@marketcetera.com
 * @version $Id: Messages.java 16154 2012-07-14 16:34:05Z colin $
 * @since 2.0.0
 */
@ClassVersion("$Id: Messages.java 16154 2012-07-14 16:34:05Z colin $")
public interface Messages {
    /**
     * The message provider
     */
    static final I18NMessageProvider PROVIDER = new I18NMessageProvider("dataflow_rpc",Messages.class.getClassLoader());
    /**
     * The message logger.
     */
    static final I18NLoggerProxy LOGGER = new I18NLoggerProxy(PROVIDER);

    static final I18NMessage1P LOG_ERROR_RECEIVE_CONNECT_STATUS = new I18NMessage1P(LOGGER, "log_error_receive_connect_status");   //$NON-NLS-1$
}
