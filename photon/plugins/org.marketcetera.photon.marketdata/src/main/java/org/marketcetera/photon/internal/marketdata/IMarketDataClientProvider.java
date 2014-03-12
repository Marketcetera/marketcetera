package org.marketcetera.photon.internal.marketdata;

import org.marketcetera.marketdata.core.webservice.MarketDataServiceClient;

import com.google.inject.ImplementedBy;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ImplementedBy(MarketDataManager.class)
public interface IMarketDataClientProvider
{
    MarketDataServiceClient getMarketDataClient();
}
