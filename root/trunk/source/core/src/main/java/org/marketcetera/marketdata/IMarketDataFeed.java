package org.marketcetera.marketdata;

import org.marketcetera.core.publisher.Subscriber;
import org.springframework.context.Lifecycle;

import quickfix.Message;

/**
 * Indicates an object capable of responding to requests for market data.
 * 
 * <p><code>IMarketDataFeed</code> objects can be constructed by calling
 * the appropriate {@link IMarketDataFeedFactory}.
 *
 * @author gmiller
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.43-SNAPSHOT
 */
public interface IMarketDataFeed 
    extends IFeedComponent, Lifecycle 
{
    /**
     * Executes a market data query described by the given FIX message.
     * 
     * @param inMessage a <code>Message</code> value
     * @throws FeedException if an error occurs
     */
    public MarketDataFeedToken execute(Message inMessage,
                                       Subscriber inSubscriber)
        throws FeedException;
}
