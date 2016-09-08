package org.marketcetera.marketdata.module;

import org.marketcetera.core.CoreException;
import org.marketcetera.marketdata.DataRequestTranslator;
import org.marketcetera.marketdata.MarketDataRequest;

/* $License$ */

/**
 * Translates market data requests for {@link TestFeed}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class TestFeedMessageTranslator
        implements DataRequestTranslator<MarketDataRequest>
{
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.DataRequestTranslator#fromDataRequest(org.marketcetera.marketdata.MarketDataRequest)
     */
    @Override
    public MarketDataRequest fromDataRequest(MarketDataRequest inRequest)
            throws CoreException
    {
        return inRequest;
    }
    /**
     * singleton instance
     */
    public static final TestFeedMessageTranslator instance = new TestFeedMessageTranslator();
}
