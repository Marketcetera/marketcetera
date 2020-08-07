package org.marketcetera.marketdata.event;

/* $License$ */

/**
 * Indicates that a market data request was accepted by a specific provider.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface MarketDataRequestAcceptedEvent
        extends HasMarketDataRequest,HasMarketDataRequestId,HasMarketDataRequestProvider
{
}
