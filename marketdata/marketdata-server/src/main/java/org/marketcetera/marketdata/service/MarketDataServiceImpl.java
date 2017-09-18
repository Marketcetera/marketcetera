package org.marketcetera.marketdata.service;

import java.util.Deque;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.marketcetera.event.Event;
import org.marketcetera.marketdata.Capability;
import org.marketcetera.marketdata.Content;
import org.marketcetera.marketdata.MarketDataListener;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.MarketDataStatus;
import org.marketcetera.marketdata.MarketDataStatusListener;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.trade.Instrument;
import org.springframework.stereotype.Service;

/* $License$ */

/**
 * Provides market data services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Service
public class MarketDataServiceImpl
        implements MarketDataService
{
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataStatusPublisher#addMarketDataStatusListener(org.marketcetera.marketdata.MarketDataStatusListener)
     */
    @Override
    public void addMarketDataStatusListener(MarketDataStatusListener inMarketDataStatusListener)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataStatusPublisher#removeMarketDataStatusListener(org.marketcetera.marketdata.MarketDataStatusListener)
     */
    @Override
    public void removeMarketDataStatusListener(MarketDataStatusListener inMarketDataStatusListener)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataStatusBroadcaster#reportMarketDataStatus(org.marketcetera.marketdata.MarketDataStatus)
     */
    @Override
    public void reportMarketDataStatus(MarketDataStatus inMarketDataStatus)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.service.MarketDataService#request(org.marketcetera.marketdata.MarketDataRequest, org.marketcetera.marketdata.MarketDataListener)
     */
    @Override
    public String request(MarketDataRequest inRequest,
                          MarketDataListener inMarketDataListener)
    {
        
        return inRequest.getRequestId();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.service.MarketDataService#cancel(java.lang.String)
     */
    @Override
    public void cancel(String inId)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.service.MarketDataService#getSnapshot(org.marketcetera.trade.Instrument, org.marketcetera.marketdata.Content, java.lang.String)
     */
    @Override
    public Deque<Event> getSnapshot(Instrument inInstrument,
                                    Content inContent,
                                    String inProvider)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.service.MarketDataService#getSnapshotPage(org.marketcetera.trade.Instrument, org.marketcetera.marketdata.Content, java.lang.String, org.marketcetera.persist.PageRequest)
     */
    @Override
    public Deque<Event> getSnapshotPage(Instrument inInstrument,
                                        Content inContent,
                                        String inProvider,
                                        PageRequest inPageRequest)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.service.MarketDataService#getAvailableCapability()
     */
    @Override
    public Set<Capability> getAvailableCapability()
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /**
     * 
     *
     *
     */
    @PostConstruct
    public void start()
    {
        
    }
}
