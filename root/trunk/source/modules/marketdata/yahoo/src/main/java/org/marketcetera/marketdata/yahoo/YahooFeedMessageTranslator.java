package org.marketcetera.marketdata.yahoo;

import org.marketcetera.marketdata.DataRequestTranslator;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Translates market data requests to a format Yahoo can understand.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public enum YahooFeedMessageTranslator
        implements DataRequestTranslator<YahooRequest>
{
    INSTANCE;
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.DataRequestTranslator#fromDataRequest(org.marketcetera.marketdata.MarketDataRequest)
     */
    @Override
    public YahooRequest fromDataRequest(MarketDataRequest inRequest)
    {
        return new YahooRequest(inRequest);
    }
}
