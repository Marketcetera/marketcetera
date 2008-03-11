package org.marketcetera.marketdata;

import java.util.concurrent.Callable;

public interface MarketDataFeedConnector
    extends Callable<MarketDataFeedToken>
{
    public void cancel(MarketDataFeedToken inToken)
        throws FeedException;
}
