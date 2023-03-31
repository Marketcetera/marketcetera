package org.marketcetera.marketdata.service;

import java.util.Deque;
import java.util.Set;

import org.marketcetera.core.ClientStatusListener;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.event.Event;
import org.marketcetera.marketdata.Capability;
import org.marketcetera.marketdata.Content;
import org.marketcetera.marketdata.MarketDataClient;
import org.marketcetera.marketdata.MarketDataListener;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.MarketDataStatusListener;
import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class DirectMarketDataClient
        implements MarketDataClient
{
    /* (non-Javadoc)
     * @see org.marketcetera.core.BaseClient#start()
     */
    @Override
    public void start()
            throws Exception
    {
        SLF4JLoggerProxy.info(this,
                              "Starting {}",
                              PlatformServices.getServiceName(getClass()));
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.BaseClient#stop()
     */
    @Override
    public void stop()
            throws Exception
    {
        SLF4JLoggerProxy.info(this,
                              "Stopping {}",
                              PlatformServices.getServiceName(getClass()));
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.BaseClient#isRunning()
     */
    @Override
    public boolean isRunning()
    {
        return true;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.BaseClient#addClientStatusListener(org.marketcetera.core.ClientStatusListener)
     */
    @Override
    public void addClientStatusListener(ClientStatusListener inListener)
    {
        throw new UnsupportedOperationException();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.BaseClient#removeClientStatusListener(org.marketcetera.core.ClientStatusListener)
     */
    @Override
    public void removeClientStatusListener(ClientStatusListener inListener)
    {
        throw new UnsupportedOperationException();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataClient#request(org.marketcetera.marketdata.MarketDataRequest, org.marketcetera.marketdata.MarketDataListener)
     */
    @Override
    public String request(MarketDataRequest inRequest,
                          MarketDataListener inMarketDataListener)
    {
        return marketDataService.request(inRequest,
                                         inMarketDataListener);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataClient#cancel(java.lang.String)
     */
    @Override
    public void cancel(String inRequestId)
    {
        marketDataService.cancel(inRequestId);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataClient#getSnapshot(org.marketcetera.trade.Instrument, org.marketcetera.marketdata.Content)
     */
    @Override
    public Deque<Event> getSnapshot(Instrument inInstrument,
                                    Content inContent)
    {
        return marketDataService.getSnapshot(inInstrument,
                                             inContent);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataClient#getSnapshot(org.marketcetera.trade.Instrument, org.marketcetera.marketdata.Content, org.marketcetera.persist.PageRequest)
     */
    @Override
    public CollectionPageResponse<Event> getSnapshot(Instrument inInstrument,
                                                     Content inContent,
                                                     PageRequest inPage)
    {
        return marketDataService.getSnapshot(inInstrument,
                                             inContent,
                                             inPage);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataClient#addMarketDataStatusListener(org.marketcetera.marketdata.MarketDataStatusListener)
     */
    @Override
    public void addMarketDataStatusListener(MarketDataStatusListener inListener)
    {
        marketDataService.addMarketDataStatusListener(inListener);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataClient#removeMarketDataStatusListener(org.marketcetera.marketdata.MarketDataStatusListener)
     */
    @Override
    public void removeMarketDataStatusListener(MarketDataStatusListener inListener)
    {
        marketDataService.removeMarketDataStatusListener(inListener);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataClient#getAvailableCapability()
     */
    @Override
    public Set<Capability> getAvailableCapability()
    {
        return marketDataService.getAvailableCapability();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataClient#getProviders()
     */
    @Override
    public Set<String> getProviders()
    {
        return marketDataService.getProviders();
    }
    /**
     * provides access to market data services
     */
    @Autowired
    private MarketDataService marketDataService;
}
