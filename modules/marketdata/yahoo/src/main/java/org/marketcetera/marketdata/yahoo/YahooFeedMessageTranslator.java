package org.marketcetera.marketdata.yahoo;

import java.util.ArrayList;
import java.util.List;

import org.marketcetera.core.marketdata.DataRequestTranslator;
import org.marketcetera.core.marketdata.MarketDataRequest;

/* $License$ */

/**
 * Translates market data requests to a format Yahoo can understand.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: YahooFeedMessageTranslator.java 16063 2012-01-31 18:21:55Z colin $
 * @since 2.1.4
 */
public enum YahooFeedMessageTranslator
        implements DataRequestTranslator<List<YahooRequest>>
{
    INSTANCE;
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.DataRequestTranslator#fromDataRequest(org.marketcetera.marketdata.MarketDataRequest)
     */
    @Override
    public List<YahooRequest> fromDataRequest(MarketDataRequest inRequest)
    {
        List<YahooRequest> requests = new ArrayList<YahooRequest>();
        for(String symbol : inRequest.getSymbols()) {
            requests.add(new YahooRequest(inRequest,
                                          symbol));
        }
        return requests;
    }
}
