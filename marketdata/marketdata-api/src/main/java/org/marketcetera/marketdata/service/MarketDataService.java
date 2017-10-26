package org.marketcetera.marketdata.service;

import java.util.Deque;
import java.util.Set;

import org.marketcetera.event.Event;
import org.marketcetera.marketdata.Capability;
import org.marketcetera.marketdata.Content;
import org.marketcetera.marketdata.MarketDataCapabilityBroadcaster;
import org.marketcetera.marketdata.MarketDataListener;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.MarketDataStatusBroadcaster;
import org.marketcetera.marketdata.MarketDataStatusPublisher;
import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Provides market data services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: MarketDataService.java 17245 2016-09-03 01:25:42Z colin $
 * @since 2.4.0
 */
@ClassVersion("$Id: MarketDataService.java 17245 2016-09-03 01:25:42Z colin $")
public interface MarketDataService
        extends MarketDataStatusPublisher,MarketDataStatusBroadcaster,MarketDataCapabilityBroadcaster
{
    /**
     * Request market data.
     * 
     * @param inRequest a <code>MarketDataRequest</code> value
     * @param inMarketDataListener a <code>MarketDataListener</code> value
     * @return a <code>String</code> value
     */
    String request(MarketDataRequest inRequest,
                   MarketDataListener inMarketDataListener);
    /**
     * Cancels the given market data request.
     *
     * @param inId a <code>String</code> value
     */
    void cancel(String inId);
    /**
     * Gets the most recent snapshot for the given attributes.
     *
     * @param inInstrument an <code>Instrument</code> value
     * @param inContent a <code>Content</code> value
     * @return a <code>Deque&lt;Event&gt;</code> value
     */
    Deque<Event> getSnapshot(Instrument inInstrument,
                             Content inContent);
    /**
     * Gets the most recent snapshot page for the given attributes.
     *
     * @param inInstrument an <code>Instrument</code> value
     * @param inContent a <code>Content</code> value
     * @param inPageRequest a <code>PageRequest</code> value
     * @return a <code>Deque&lt;Event&gt;</code> value
     */
    CollectionPageResponse<Event> getSnapshot(Instrument inInstrument,
                                              Content inContent,
                                              PageRequest inPageRequest);
    /**
     * Gets the available capabilities.
     *
     * @return a <code>Set&lt;Capability&gt;</code> value
     */
    Set<Capability> getAvailableCapability();
}
