package com.marketcetera.marketdata.reuters;

import org.marketcetera.marketdata.AbstractMarketDataFeedToken;
import org.marketcetera.marketdata.MarketDataFeedTokenSpec;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: ReutersFeedToken.java 82348 2012-05-03 23:45:18Z colin $
 * @since $Release$
 */
@ClassVersion("$Id: ReutersFeedToken.java 82348 2012-05-03 23:45:18Z colin $")
public class ReutersFeedToken
        extends AbstractMarketDataFeedToken<ReutersFeed>
{
    /**
     * Create a new ReutersFeedToken instance.
     *
     * @param inTokenSpec
     * @param inFeed
     */
    public ReutersFeedToken(MarketDataFeedTokenSpec inTokenSpec,
                            ReutersFeed inFeed)
    {
        super(inTokenSpec,
              inFeed);
    }
}
