package org.marketcetera.marketdata.marketcetera;

import static org.marketcetera.core.marketdata.Content.LATEST_TICK;
import static org.marketcetera.core.marketdata.Content.TOP_OF_BOOK;
import static org.marketcetera.core.marketdata.Messages.UNSUPPORTED_REQUEST;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.marketcetera.core.CoreException;
import org.marketcetera.core.marketdata.DataRequestTranslator;
import org.marketcetera.core.marketdata.MarketDataRequest;
import org.marketcetera.core.quickfix.FIXVersion;
import org.marketcetera.core.trade.Equity;
import org.marketcetera.core.trade.impl.EquityImpl;
import org.marketcetera.core.util.log.I18NBoundMessage1P;
import org.marketcetera.marketdata.marketcetera.MarketceteraFeed.Request;

import quickfix.Message;
import quickfix.field.SubscriptionRequestType;

/* $License$ */

/**
 * Marketcetera feed implementation of {@link DataRequestTranslator}.
 *
 * @version $Id: MarketceteraFeedMessageTranslator.java 16063 2012-01-31 18:21:55Z colin $
 * @since 0.5.0
 */
public class MarketceteraFeedMessageTranslator
    implements DataRequestTranslator<Request>
{
    /**
     * default FIX message factory to use to construct messages
     */
    private static final FIXVersion DEFAULT_MESSAGE_FACTORY = FIXVersion.FIX44;
    /**
     * the instance used for all message translations
     */
    private static final MarketceteraFeedMessageTranslator sInstance = new MarketceteraFeedMessageTranslator();
    /**
     * counter used to identify translated messages
     */
    private static final AtomicLong counter = new AtomicLong(0);
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.DataRequestTranslator#translate(org.marketcetera.marketdata.DataRequest)
     */
    @Override
    public Request fromDataRequest(MarketDataRequest inRequest)
            throws CoreException
    {
        if(inRequest.validateWithCapabilities(TOP_OF_BOOK,LATEST_TICK)) {
            return fixMessageFromMarketDataRequest((MarketDataRequest)inRequest);
        }
        throw new CoreException(new I18NBoundMessage1P(UNSUPPORTED_REQUEST,
                                                       String.valueOf(inRequest.getContent())));
    }
    /**
     * Gets a <code>MarketceteraFeedMessageTranslator</code> instance.
     *
     * @return a <code>MarketceteraFeedMessageTranslator</code> value
     */
    static MarketceteraFeedMessageTranslator getInstance()
    {
        return sInstance;
    }
    /**
     * Create a new <code>MarketceteraFeedMessageTranslator</code> instance.
     */
    private MarketceteraFeedMessageTranslator()
    {        
    }
    /**
     * Creates a <code>Message</code> value representing a market data request to pass to the feed server. 
     *
     * @param inRequest a <code>MarketDataRequest</code> value
     * @return a <code>Message</code> value
     */
    private static Request fixMessageFromMarketDataRequest(MarketDataRequest inRequest)
    {
        List<Equity> symbolList = new ArrayList<Equity>();
        for(String symbol : inRequest.getSymbols()) {
            symbolList.add(new EquityImpl(symbol));
        }
        long id = counter.incrementAndGet();
        // generate the message using the current FIXMessageFactory
        // sets symbols
        Message message = DEFAULT_MESSAGE_FACTORY.getMessageFactory().newMarketDataRequest(Long.toString(id), 
                                                                                           symbolList,
                                                                                           inRequest.getExchange());
        // set the update type indicator
        message.setChar(SubscriptionRequestType.FIELD, 
                        SubscriptionRequestType.SNAPSHOT_PLUS_UPDATES);
        return new Request(id,
                           message,
                           inRequest);
    }
    
}
