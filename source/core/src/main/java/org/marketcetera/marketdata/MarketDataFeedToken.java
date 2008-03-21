package org.marketcetera.marketdata;

import org.marketcetera.core.MessageKey;
import org.marketcetera.core.publisher.IPublisher;
import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.core.publisher.PublisherEngine;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.MDReqID;

/**
 * Represents the responses to a market data request.
 *
 * <p>This class provides a handle to the responses to a market data request.
 * 
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: $
 * @since 0.43-SNAPSHOT
 */
public abstract class MarketDataFeedToken
    implements IPublisher
{
    /**
     * the underlying message that represents the request
     */
    private final Message mFixMessage;
    /**
     * publishing engine used to update subscribers of responses to the request
     */
    private final PublisherEngine mPublisher;        
    /**
     * the data feed connector that spawned this request
     */
    private IMarketDataFeedConnector mConnector;
    
    /**
     * Create a new <code>MarketDataFeedToken</code> object.
     *
     * @param inFixMessage a <code>Message</code> value containing the data request to execute
     * @throws FeedException if the underlying message doesn't contain a valid {@link MDReqID}
     *   field
     */
    protected MarketDataFeedToken(Message inFixMessage) 
        throws FeedException 
    {
        mFixMessage = inFixMessage;
        try {
            mFixMessage.getString(MDReqID.FIELD);
        } catch (FieldNotFound e) {
            throw new FeedException(MessageKey.ERROR_NO_ID_FOR_TOKEN.getLocalizedMessage(),
                                    e);
        }
        mPublisher = new PublisherEngine();
    }
    
    /**
     * Gets the underlying FIX message.
     * 
     * @return a <code>Message</code> value
     */
    public Message getFixMessage()
    {
        // TODO defensive copying needed here
        return mFixMessage;
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

    public void subscribe(ISubscriber inSubscriber)
    {
        getPublisher().subscribe(inSubscriber);
    }

    public void unsubscribe(ISubscriber inSubscriber)
    {
        getPublisher().unsubscribe(inSubscriber);
    }
    
    /**
     * Cancels any subscriptions held by this token.
     *
     * @throws FeedException if an error occurs while cancelling the subscriptions
     */
    public void cancel()
        throws FeedException
    {
        IMarketDataFeedConnector connector = getConnector();
        connector.cancel(this);
    }

    /**
     * @return the connector
     */
    protected IMarketDataFeedConnector getConnector()
    {
        return mConnector;
    }

    /**
     * @param inConnector the connector to set
     */
    protected void setConnector(IMarketDataFeedConnector inConnector)
    {
        mConnector = inConnector;
    }
}
