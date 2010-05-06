package org.marketcetera.marketdata.csv;

import static org.marketcetera.marketdata.Content.DIVIDEND;
import static org.marketcetera.marketdata.Content.LATEST_TICK;
import static org.marketcetera.marketdata.Content.MARKET_STAT;
import static org.marketcetera.marketdata.Content.TOP_OF_BOOK;
import static org.marketcetera.marketdata.Messages.UNSUPPORTED_REQUEST;

import org.marketcetera.core.CoreException;
import org.marketcetera.marketdata.DataRequestTranslator;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.misc.ClassVersion;

/**
 * CSV reader implementation of {@link DataRequestTranslator}
 * Can support bid,ask,trade events
 * Since we create Marketcetera-based request manually to begin with,
 * no translation needs to be done here
 * @author toli kuznets
 * @version $Id: CSVFeedMessageTranslator.java 4348 2009-09-24 02:33:11Z toli $
 */

@ClassVersion("$Id: CSVFeedMessageTranslator.java 4348 2009-09-24 02:33:11Z toli $")
public class CSVFeedMessageTranslator     
        implements DataRequestTranslator<MarketDataRequest>
{
    /**
     * static instance
     */
    private static final CSVFeedMessageTranslator sInstance = new CSVFeedMessageTranslator();
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
}
