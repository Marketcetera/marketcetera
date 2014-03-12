package org.marketcetera.marketdata.core.webservice;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.ContextClassProvider;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface MarketDataServiceClientFactory
{
    MarketDataServiceClient create(String inUsername,
                                   String inPassword,
                                   String inHostname,
                                   int inPort,
                                   ContextClassProvider inContextClassProvider);
    MarketDataServiceClient create(String inUsername,
                                   String inPassword,
                                   String inHostname,
                                   int inPort);
}
