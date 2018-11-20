package org.marketcetera.marketdata.rpc;

import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessage1P;
import org.marketcetera.util.log.I18NMessageProvider;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * The internationalization constants used by this package.
 *
 * @author klim@marketcetera.com
 * @since 0.6.0
 * @version $Id: Messages.java 17411 2017-04-28 14:50:38Z colin $
 */
@ClassVersion("$Id: Messages.java 17411 2017-04-28 14:50:38Z colin $")
public interface Messages
{
    /**
     * The message provider.
     */
    static final I18NMessageProvider PROVIDER = new I18NMessageProvider("marketdata_rpc_core");  //$NON-NLS-1$
    /**
     * The logger.
     */
    static final I18NLoggerProxy LOGGER = new I18NLoggerProxy(PROVIDER);
    static final I18NMessage1P MESSAGE_HOLDER = new I18NMessage1P(LOGGER,"message_holder");   //$NON-NLS-1$
}
