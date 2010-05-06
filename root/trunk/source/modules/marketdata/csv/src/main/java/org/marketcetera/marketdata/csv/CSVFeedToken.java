package org.marketcetera.marketdata.csv;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.marketdata.AbstractMarketDataFeedToken;
import org.marketcetera.marketdata.MarketDataFeedTokenSpec;

/**
 * Token for {@link CSVFeed}.
 * Dummy implementation, we are not doing anything clever in our subscriptions.
 *
 * @author <a href="mailto:toli@marketcetera.com">Toli Kuznets</a>
 * @version $Id: CSVFeedToken.java 4348 2009-09-24 02:33:11Z toli $
 */
@ClassVersion("$Id: CSVFeedToken.java 4348 2009-09-24 02:33:11Z toli $") //$NON-NLS-1$
public class CSVFeedToken
        extends AbstractMarketDataFeedToken<CSVFeed>
{
    static CSVFeedToken getToken(MarketDataFeedTokenSpec inTokenSpec,
                                   CSVFeed inFeed) 
    {
        return new CSVFeedToken(inTokenSpec,
                                  inFeed);
    }   
    /**
     * Create a new CSVFeedToken instance.
     */
    private CSVFeedToken(MarketDataFeedTokenSpec inTokenSpec,
                           CSVFeed inFeed) 
    {
        super(inTokenSpec,
              inFeed);
    }
    public String toString()
    {
        return String.format("CSVFeedToken(%s)", //$NON-NLS-1$
                             getStatus());
    }
}
