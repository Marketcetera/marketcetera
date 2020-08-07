package org.marketcetera.marketdata.event;

import org.marketcetera.core.HasReason;

/* $License$ */

/**
 * Indicates that a market data request was accepted by a specific provider.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface MarketDataRequestRejectedEvent
        extends HasMarketDataRequest,HasMarketDataRequestId,HasMarketDataRequestProvider,HasReason
{
}
