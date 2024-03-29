package org.marketcetera.rpc;

import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessage1P;
import org.marketcetera.util.log.I18NMessage3P;
import org.marketcetera.util.log.I18NMessageProvider;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */
/**
 * Internationalized messages used by this package.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 2.4.0
 */
@ClassVersion("$Id$")
public interface Messages
{
    /**
     * The message provider
     */
    static final I18NMessageProvider PROVIDER = new I18NMessageProvider("rpc_core",Messages.class.getClassLoader());  //$NON-NLS-1$ 
    /**
     * The message logger.
     */
    static final I18NLoggerProxy LOGGER = new I18NLoggerProxy(PROVIDER);
    static final I18NMessage3P SERVER_STARTING = new I18NMessage3P(LOGGER,"server_starting");   //$NON-NLS-1$
    static final I18NMessage1P SERVICE_STARTING = new I18NMessage1P(LOGGER,"service_starting");   //$NON-NLS-1$
    static final I18NMessage1P SERVER_STOPPING = new I18NMessage1P(LOGGER,"server_stopping");   //$NON-NLS-1$
}
