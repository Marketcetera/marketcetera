package org.marketcetera.marketdata.bogus;

import static org.marketcetera.marketdata.MarketDataRequest.Content.LATEST_TICK;
import static org.marketcetera.marketdata.MarketDataRequest.Content.LEVEL_2;
import static org.marketcetera.marketdata.MarketDataRequest.Content.OPEN_BOOK;
import static org.marketcetera.marketdata.MarketDataRequest.Content.MARKET_STAT;
import static org.marketcetera.marketdata.MarketDataRequest.Content.TOP_OF_BOOK;
import static org.marketcetera.marketdata.MarketDataRequest.Content.TOTAL_VIEW;
import static org.marketcetera.marketdata.Messages.UNSUPPORTED_REQUEST;

import org.marketcetera.core.CoreException;
import org.marketcetera.marketdata.DataRequestTranslator;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Bogus feed implementation of {@link DataRequestTranslator}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.5.0
 */
@ClassVersion("$Id$")
public class BogusFeedMessageTranslator
    implements DataRequestTranslator<MarketDataRequest>
{
    /**
     * static instance
     */
    private static final BogusFeedMessageTranslator sInstance = new BogusFeedMessageTranslator();
    /**
     * Gets a <code>BogusFeedMessageTranslator</code> instance.
     * 
     * @return a <code>BogusFeedMessageTranslator</code> value
     */
    static BogusFeedMessageTranslator getInstance()
    {
        return sInstance;
    }
    /**
     * Create a new BogusFeedMessageTranslator instance.
     *
     */
    private BogusFeedMessageTranslator()
    {
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.DataRequestTranslator#translate(org.marketcetera.marketdata.DataRequest)
     */
    @Override
    public MarketDataRequest fromDataRequest(MarketDataRequest inRequest)
            throws CoreException
    {
        if(inRequest.validateWithCapabilities(TOP_OF_BOOK,
                                              LEVEL_2,
                                              OPEN_BOOK,
                                              TOTAL_VIEW,
                                              LATEST_TICK,
                                              MARKET_STAT)) {
            return inRequest;
        }
        throw new CoreException(new I18NBoundMessage1P(UNSUPPORTED_REQUEST,
                                                       String.valueOf(inRequest.getContent())));
    }
}
