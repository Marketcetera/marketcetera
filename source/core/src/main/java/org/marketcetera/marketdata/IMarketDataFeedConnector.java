package org.marketcetera.marketdata;

import java.util.concurrent.Callable;

public interface IMarketDataFeedConnector
    extends Callable<MarketDataFeedToken>
{
    public void cancel(MarketDataFeedToken inToken)
        throws FeedException;
}
