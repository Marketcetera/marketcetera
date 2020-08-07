package org.marketcetera.marketdata.event;

import org.marketcetera.marketdata.HasFeedStatus;

/* $License$ */

/**
 * Indicates that the feed status of a market data provider has changed.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface MarketDataFeedStatusEvent
        extends HasFeedStatus,HasMarketDataRequestProvider
{
}
