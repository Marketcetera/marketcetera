package org.marketcetera.marketdata.core.webservice;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.ContextClassProvider;

/* $License$ */

/**
 * Creates {@link MarketDataServiceClient} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.4.0
 */
@ClassVersion("$Id$")
public interface MarketDataServiceClientFactory
{
    /**
     * Create a MarketDataServiceClientFactory instance.
     *
     * @param inUsername a <code>String</code> value
     * @param inPassword a <code>String</code> value
     * @param inHostname a <code>String</code> value
     * @param inPort an <code>int</code> value
     * @param inContextClassProvider a <code>ContextClassProvider</code> value
     * @return a <code>MarketDataServiceClient</code> value
     */
    MarketDataServiceClient create(String inUsername,
                                   String inPassword,
                                   String inHostname,
                                   int inPort,
                                   ContextClassProvider inContextClassProvider);
    /**
     * Create a MarketDataServiceClientFactory instance.
     *
     * @param inUsername a <code>String</code> value
     * @param inPassword a <code>String</code> value
     * @param inHostname a <code>String</code> value
     * @param inPort an <code>int</code> value
     * @return a <code>MarketDataServiceClient</code> value
     */
    MarketDataServiceClient create(String inUsername,
                                   String inPassword,
                                   String inHostname,
                                   int inPort);
}
