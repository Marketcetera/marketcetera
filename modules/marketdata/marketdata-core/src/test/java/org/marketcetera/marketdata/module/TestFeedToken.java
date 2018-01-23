package org.marketcetera.marketdata.module;

import org.marketcetera.marketdata.AbstractMarketDataFeedToken;
import org.marketcetera.marketdata.MarketDataFeedTokenSpec;

/* $License$ */

/**
 * Provides a market data feed token implementation for {@link TestFeed}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class TestFeedToken
        extends AbstractMarketDataFeedToken<TestFeed>
{
    /**
     * Create a new TestFeedToken instance.
     *
     * @param inTokenSpec a <code>MarketDataFeedTokenSpec</code> value
     * @param inFeed a <code>TestFeed</code> value
     */
    public TestFeedToken(MarketDataFeedTokenSpec inTokenSpec,
                         TestFeed inFeed)
    {
        super(inTokenSpec,
              inFeed);
    }
}
