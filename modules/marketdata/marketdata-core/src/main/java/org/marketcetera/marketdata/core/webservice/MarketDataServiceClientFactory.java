package org.marketcetera.marketdata.core.webservice;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.ContextClassProvider;

/* $License$ */

/**
 * Creates {@link MarketDataServiceClient} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface MarketDataServiceClientFactory
{
    /**
     * 
     *
     *
     * @param inUsername
     * @param inPassword
     * @param inHostname
     * @param inPort
     * @param inContextClassProvider
     * @return
     */
    MarketDataServiceClient create(String inUsername,
                                   String inPassword,
                                   String inHostname,
                                   int inPort,
                                   ContextClassProvider inContextClassProvider);
    /**
     * 
     *
     *
     * @param inUsername
     * @param inPassword
     * @param inHostname
     * @param inPort
     * @return
     */
    MarketDataServiceClient create(String inUsername,
                                   String inPassword,
                                   String inHostname,
                                   int inPort);
}
