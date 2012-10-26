package org.marketcetera.marketdata;

import java.util.Collection;
import java.util.Set;

import org.marketcetera.api.systemmodel.Publisher;
import org.marketcetera.api.systemmodel.Subscriber;
import org.marketcetera.core.trade.SecurityType;
import org.marketcetera.marketdata.events.Event;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface MarketDataProvider
        extends Publisher
{
    public Collection<Event> requestMarketData(MarketDataRequest inRequest,
                                               Subscriber inSubscriber)
            throws InterruptedException;
    public Collection<Event> requestMarketData(MarketDataRequest inRequest)
            throws InterruptedException;
    public String getProviderName();
    public Set<Capability> getCapabilities();
    public Set<SecurityType> getHandledTypes();
    public FeedStatus getFeedStatus()
            throws InterruptedException;
    public FeedType getFeedType();
}
