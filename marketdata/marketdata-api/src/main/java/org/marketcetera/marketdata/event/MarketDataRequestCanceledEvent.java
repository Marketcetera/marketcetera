package org.marketcetera.marketdata.event;

import org.marketcetera.marketdata.MarketDataRequest;

/* $License$ */

/**
 * Indicates that a {@link MarketDataRequest} has been cancel has been processed by a provider.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface MarketDataRequestCanceledEvent
        extends HasMarketDataRequestId,HasMarketDataRequestProvider
{
}
