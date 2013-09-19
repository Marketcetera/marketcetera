package org.marketcetera.marketdata.marketcetera;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.marketdata.AbstractMarketDataFeedToken;
import org.marketcetera.marketdata.MarketDataFeedToken;
import org.marketcetera.marketdata.MarketDataFeedTokenSpec;

/* $License$ */

/**
 * {@link MarketDataFeedToken} implementation for {@link MarketceteraFeed}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class MarketceteraFeedToken
    extends AbstractMarketDataFeedToken<MarketceteraFeed>
{
    /**
     * Gets a <code>MarketceteraFeedToken</code> value.
     *
     * @param inTokenSpec a <code>MarketDataFeedTokenSpec&lt;MarketceteraFeedCredentials&gt;</code> value
     * @param inFeed a <code>MarketceteraFeed</code> value
     * @return a <code>MarketceteraFeedToken</code> value
     */
    static MarketceteraFeedToken getToken(MarketDataFeedTokenSpec inTokenSpec,
                                          MarketceteraFeed inFeed) 
    {
        return new MarketceteraFeedToken(inTokenSpec,
                                         inFeed);
    }
    /**
     * Create a new MarketceteraFeedToken instance.
     *
     * @param inTokenSpec a <code>MarketDataFeedTokenSpec&lt;MarketceteraFeedCredentials&gt;</code> value
     * @param inFeed a <code>MarketceteraFeed</code> value
     */
    private MarketceteraFeedToken(MarketDataFeedTokenSpec inTokenSpec,
                                  MarketceteraFeed inFeed)
    {
        super(inTokenSpec, 
              inFeed);
    }
}
