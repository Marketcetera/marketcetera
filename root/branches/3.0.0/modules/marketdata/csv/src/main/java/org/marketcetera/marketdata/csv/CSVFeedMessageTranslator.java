package org.marketcetera.marketdata.csv;

import static org.marketcetera.core.marketdata.Content.DIVIDEND;
import static org.marketcetera.core.marketdata.Content.LATEST_TICK;
import static org.marketcetera.core.marketdata.Content.MARKET_STAT;
import static org.marketcetera.core.marketdata.Content.TOP_OF_BOOK;
import static org.marketcetera.core.marketdata.Messages.UNSUPPORTED_REQUEST;

import org.marketcetera.core.CoreException;
import org.marketcetera.core.marketdata.DataRequestTranslator;
import org.marketcetera.core.marketdata.MarketDataRequest;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.core.attributes.ClassVersion;

/**
 * Translates {@link MarketDataRequest} objects to a format that the {@link CSVFeed} can understand.
 * 
 * @author toli kuznets
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @since 2.1.0
 * @version $Id: CSVFeedMessageTranslator.java 16063 2012-01-31 18:21:55Z colin $
 */
@ClassVersion("$Id: CSVFeedMessageTranslator.java 16063 2012-01-31 18:21:55Z colin $")
public class CSVFeedMessageTranslator     
        implements DataRequestTranslator<MarketDataRequest>
{
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.DataRequestTranslator#translate(org.marketcetera.marketdata.DataRequest)
     */
    @Override
    public MarketDataRequest fromDataRequest(MarketDataRequest inRequest)
            throws CoreException
    {
        if(inRequest.validateWithCapabilities(TOP_OF_BOOK,LATEST_TICK,DIVIDEND,MARKET_STAT)) {
            return inRequest;
        }
        throw new CoreException(new I18NBoundMessage1P(UNSUPPORTED_REQUEST,
                                                       String.valueOf(inRequest.getContent())));
    }
    /**
     * Gets a <code>CSVFeedMessageTranslator</code> instance.
     * 
     * @return a <code>CSVFeedMessageTranslator</code> value
     */
    static CSVFeedMessageTranslator getInstance()
    {
        return sInstance;
    }
    /**
     * Create a new CSVFeedMessageTranslator instance.
     *
     */
    private CSVFeedMessageTranslator()
    {
    }
    /**
     * static instance
     */
    private static final CSVFeedMessageTranslator sInstance = new CSVFeedMessageTranslator();
}
