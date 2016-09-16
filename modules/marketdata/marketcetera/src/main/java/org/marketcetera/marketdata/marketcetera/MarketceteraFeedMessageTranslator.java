package org.marketcetera.marketdata.marketcetera;

import static org.marketcetera.marketdata.Content.LATEST_TICK;
import static org.marketcetera.marketdata.Content.MARKET_STAT;
import static org.marketcetera.marketdata.Content.TOP_OF_BOOK;
import static org.marketcetera.marketdata.Messages.UNSUPPORTED_REQUEST;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;

import org.marketcetera.core.CoreException;
import org.marketcetera.marketdata.DataRequestTranslator;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.marketcetera.MarketceteraFeed.Request;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.log.I18NBoundMessage1P;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.SubscriptionRequestType;

/* $License$ */

/**
 * Marketcetera feed implementation of {@link DataRequestTranslator}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
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
        if(inRequest.validateWithCapabilities(TOP_OF_BOOK,LATEST_TICK,MARKET_STAT)) {
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
        List<Instrument> symbolList = new ArrayList<>();
        for(String symbol : inRequest.getSymbols()) {
            symbolList.add(new Equity(symbol));
        }
        long id = counter.incrementAndGet();
        // generate the message using the current FIXMessageFactory
        // sets symbols
        Message message;
        try {
            message = DEFAULT_MESSAGE_FACTORY.getMessageFactory().newMarketDataRequest(Long.toString(id), 
                                                                                       symbolList,
                                                                                       inRequest.getExchange());
        } catch (FieldNotFound | ExecutionException e) {
            throw new RuntimeException(e);
        }
        // set the update type indicator
        message.setChar(SubscriptionRequestType.FIELD, 
                        SubscriptionRequestType.SNAPSHOT_PLUS_UPDATES);
        return new Request(id,
                           message,
                           inRequest);
    }
    
}
