package org.marketcetera.marketdata;

import org.marketcetera.core.publisher.Publisher;
import org.marketcetera.core.publisher.PublisherEngine;
import org.marketcetera.core.publisher.Subscriber;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.MDReqID;

/**
 * Encapsulates a market data request and the responses to the request.
 *
 * <p>Subclasses of this class are used to perform a market data request
 * with {@link IMarketDataFeed#execute(MarketDataFeedToken)}.  The token
 * also acts as a {@link Publisher}.  Upon receipt of the token before
 * executing a request with {@link IMarketDataFeed#execute(MarketDataFeedToken)},
 * subscribe to updates with the token using {@link MarketDataFeedToken#subscribe(Subscriber)}.
 * 
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: $
 * @since 0.43-SNAPSHOT
 */
public abstract class MarketDataFeedToken
    implements Publisher
{
    /**
     * the underlying message that represents the request
     */
    private final Message mFixMessage;
    /**
     * unique identifier for this token
     */
    private final String mID;
    /**
     * publishing engine used to update subscribers of responses to the request
     */
    private final PublisherEngine mPublisher;        
    /**
     * the counter guaranteeing uniqueness among IDs issued by this class
     */
    private static long sIDCounter = 0;        
    /**
     * the data feed connector that spawned this request
     */
    private MarketDataFeedConnector mConnector;
    
    /**
     * Create a new <code>MarketDataFeedToken</code> object.
     *
     * @param inFixMessage a <code>Message</code> value containing the data request to execute
     * @throws FieldNotFound if the underlying message doesn't contain a valid {@link MDReq}
     *   field
     */
    protected MarketDataFeedToken(Message inFixMessage) 
        throws FieldNotFound
    {
        mFixMessage = inFixMessage;
        mID = mFixMessage.getString(MDReqID.FIELD);
        mPublisher = new PublisherEngine();
    }
    
    /**
     * Gets the underlying FIX message.
     * 
     * <p>This object may be modified, which will effect the underlying object
     * 
     * @return a <code>Message</code> value
     */
    public Message getFixMessage()
    {
        return mFixMessage;
    }
    
    /**
     * Gets the unique identifier for this message.
     * 
     * <p>Modifying the value returned by this method will not effect the underlying
     * object.
     * 
     * @return a <code>String</code> value
     */
    public String getID()
    {
        // this value must not be null
        return new String(mID);
    }
    
    /**
     * Generates a unique identifier.
     * 
     * @param inBase a <code>String</code> value containing a descriptive token that
     *   should be common to all tokens of the same class
     * @return a <code>String</code> value
     */
    protected static String generateID(String inBase)
    {
        StringBuffer id = new StringBuffer();
        id.append(inBase).append("-").append(++sIDCounter).append("-").append(System.nanoTime());
        return id.toString();
    }

    /**
     * Gets the publisher engine.
     * 
     * @return a <code>PublisherEnginer</code> value
     */
    protected PublisherEngine getPublisher()
    {
        return mPublisher;
    }

    public void subscribe(Subscriber inSubscriber)
    {
        getPublisher().subscribe(inSubscriber);
    }

    public void unsubscribe(Subscriber inSubscriber)
    {
        getPublisher().unsubscribe(inSubscriber);
    }
    
    public void cancel()
        throws FeedException
    {
        MarketDataFeedConnector connector = getConnector();
        connector.cancel(this);
    }

    /**
     * @return the connector
     */
    protected MarketDataFeedConnector getConnector()
    {
        return mConnector;
    }

    /**
     * @param inConnector the connector to set
     */
    public void setConnector(MarketDataFeedConnector inConnector)
    {
        mConnector = inConnector;
    }
}
