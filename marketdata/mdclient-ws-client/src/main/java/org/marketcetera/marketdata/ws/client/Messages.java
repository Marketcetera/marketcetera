package org.marketcetera.marketdata.ws.client;

import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.log.I18NMessageProvider;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Provides messages for the market data web services packages.
 *
 * @version $Id: Messages.java 17200 2016-08-17 17:02:56Z colin $
 * @since 2.4.0
 */
@ClassVersion("$Id: Messages.java 17200 2016-08-17 17:02:56Z colin $")
public interface Messages
{
    /**
     * The message provider.
     */
    static final I18NMessageProvider PROVIDER = new I18NMessageProvider("marketdata_ws_client", //$NON-NLS-1$
                                                                        Messages.class.getClassLoader());
    /**
     * The logger.
     */
    static final I18NLoggerProxy LOGGER = new I18NLoggerProxy(PROVIDER);
    /*
     * The messages.
     */
    static final I18NMessage0P MARKETDATA_NEXUS_CONNECTION_LOST = new I18NMessage0P(LOGGER,"marketdata_nexus_connection_lost"); //$NON-NLS-1$
}
