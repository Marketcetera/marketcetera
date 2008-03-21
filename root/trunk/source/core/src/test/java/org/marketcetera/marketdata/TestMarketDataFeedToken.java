package org.marketcetera.marketdata;

import quickfix.Message;

/**
 * Test implementation of {@link MarketDataFeedToken}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: $
 * @since 0.43-SNAPSHOT
 */
public class TestMarketDataFeedToken
    extends MarketDataFeedToken
{
    /**
     * Create a new <code>TestMarketDataFeedToken</code> instance.
     *
     * @param inFixMessage
     * @throws FeedException
     */
    public TestMarketDataFeedToken(Message inFixMessage)
        throws FeedException
    {
        super(inFixMessage);
    }
}
