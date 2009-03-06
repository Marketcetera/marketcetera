/**
 * 
 */
package org.marketcetera.marketdata.marketcetera;

import java.util.ArrayList;
import java.util.List;

import org.marketcetera.core.CoreException;
import org.marketcetera.marketdata.DataRequest;
import org.marketcetera.marketdata.DataRequestTranslator;
import org.marketcetera.marketdata.FeedException;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.MarketDataRequest.RequestType;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.trade.MSymbol;

import quickfix.Message;
import quickfix.field.MarketDepth;
import quickfix.field.SubscriptionRequestType;

/**
 * @author colin
 *
 */
public class MarketceteraFeedMessageTranslator
    implements DataRequestTranslator<Message>
{
    /**
     * default FIX message factory to use to construct messages
     */
    private static final FIXVersion DEFAULT_MESSAGE_FACTORY = FIXVersion.FIX44;
    private static final MarketceteraFeedMessageTranslator sInstance = new MarketceteraFeedMessageTranslator();
    static MarketceteraFeedMessageTranslator getInstance()
    {
        return sInstance;
    }
    private MarketceteraFeedMessageTranslator()
    {        
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.DataRequestTranslator#asDataRequest(java.lang.Object)
     */
    @Override
    public DataRequest toDataRequest(Message inData)
            throws CoreException
    {
        throw new UnsupportedOperationException();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.DataRequestTranslator#translate(org.marketcetera.marketdata.DataRequest)
     */
    @Override
    public Message fromDataRequest(DataRequest inRequest)
            throws CoreException
    {
        if(inRequest instanceof MarketDataRequest) {
            return fixMessageFromMarketDataRequest((MarketDataRequest)inRequest);
        }
        throw new UnsupportedOperationException();
    }
    /**
     * Creates a FIX message for the given symbols at the given depth with subscription set accordingly.
     *
     * @param inSymbols a <code>List&lt;MSymbol&gt;</code> value containing the symbols on which to make requests
     * @param inSubscribeToResults a <code>boolean</code> value indicating whether the request should be a single snapshot or an
     *  ongoing subscription
     * @return a <code>Message</code> value
     * @throws FeedException if an error occurs constructing the <code>Message</code>
     */
    private static Message fixMessageFromMarketDataRequest(MarketDataRequest inRequest)
    {
        long id = inRequest.getId();
        List<MSymbol> symbolList = new ArrayList<MSymbol>();
        for(String symbol : inRequest.getSymbols()) {
            symbolList.add(new MSymbol(symbol));
        }
        // generate the message using the current FIXMessageFactory
        Message message = DEFAULT_MESSAGE_FACTORY.getMessageFactory().newMarketDataRequest(Long.toString(id), 
                                                                                           symbolList);
        message.setInt(MarketDepth.FIELD, 
                       inRequest.getDepth());
        // this little bit determines whether we subscribe to updates or not
        message.setChar(SubscriptionRequestType.FIELD, 
                        inRequest.getRequestType().equals(RequestType.SUBSCRIBE) ? '1' : '0');
        return message;
    }
    
}
