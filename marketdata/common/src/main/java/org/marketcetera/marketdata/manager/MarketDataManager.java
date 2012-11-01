package org.marketcetera.marketdata.manager;

import java.util.Collection;

import org.marketcetera.api.systemmodel.Subscriber;
import org.marketcetera.marketdata.events.Event;
import org.marketcetera.marketdata.request.MarketDataRequest;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: MarketDataManager.java 16325 2012-10-25 23:13:12Z colin $
 * @since $Release$
 */
public interface MarketDataManager
{
    public Collection<Event> requestMarketData(MarketDataRequest inRequest);
    public Collection<Event> requestMarketData(MarketDataRequest inRequest,
                                               Subscriber inSubscriber);
    public void cancelMarketDataRequest(Subscriber inSubscriber);
}
