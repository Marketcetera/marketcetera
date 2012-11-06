package org.marketcetera.marketdata.manager;

import org.marketcetera.api.systemmodel.Subscriber;
import org.marketcetera.marketdata.request.MarketDataRequest;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface MarketDataManager
{
    public void requestMarketData(MarketDataRequest inRequest,
                                  Subscriber inSubscriber);
    public void cancelMarketDataRequest(Subscriber inSubscriber);
}
