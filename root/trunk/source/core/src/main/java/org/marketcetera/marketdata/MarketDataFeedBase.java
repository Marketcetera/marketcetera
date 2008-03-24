package org.marketcetera.marketdata;

import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;

import org.marketcetera.core.InMemoryIDFactory;
import org.marketcetera.core.InternalID;
import org.marketcetera.core.MSymbol;
import org.marketcetera.core.MarketceteraException;
import org.marketcetera.core.MessageKey;
import org.marketcetera.core.NoMoreIDsException;
import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.quickfix.FIXVersion;

import quickfix.Message;
import quickfix.field.SubscriptionRequestType;

/**
 * An abstract base class for all market data feeds. 
 * 
 * <p>Contains logic common to all market data feed implementations with 
 * the mechanics of adding/removing symbol/feed component listeners and keeping track of the feed 
 * status.
 * 
 * <p>Subclasses represent a connection to a specific market data feed.
 *
 * @author andrei@lissovski.org
 * @author gmiller
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 */
public abstract class MarketDataFeedBase 
    implements IMarketDataFeed 
{
    /**
     * the status of the feed
     */
    private FeedStatus mFeedStatus;
    /**
     * the type of the feed
     */
    private final FeedType mFeedType;
    /**
     * the credentials used to establish the feed
     */
    private final MarketDataFeedCredentials mCredentials;
    /**
     * the id factory used to generate unique ids within the context of all feeds for this JVM session
     */
    private static final InMemoryIDFactory sIDFactory = new InMemoryIDFactory(0,
                                                                              Long.toString(System.currentTimeMillis()));
    /**
     * default FIX message factory to use to construct messages
     */
    protected static final FIXVersion DEFAULT_MESSAGE_FACTORY = FIXVersion.FIX44;
    
    /**
     * Create a new <code>MarketDataFeedBase</code> instance.
     *
     * @param inFeedType a <code>FeedType</code> value
     * @param inCredentials a <code>MarketDataFeedCredentials</code> value
     */
    protected MarketDataFeedBase(FeedType inFeedType,
                                 MarketDataFeedCredentials inCredentials)
    {
        // since these are final, make sure they are non-null
        if(inFeedType == null ||
           inCredentials == null) {
            throw new NullPointerException();
        }
        mFeedType = inFeedType;
        mCredentials = inCredentials;
        setFeedStatus(FeedStatus.OFFLINE);
    }
    
    /**
     * Executes the given <code>Message</code> on this Market Data Feed.
     * 
     * <p>The <code>ISubscriber</code> value specified will receive the
     * response or responses from the market data feed either in the
     * case of a snapshot or a subscription.  To specify a subscription,
     * which will give updates as they become available until cancelled,
     * set the appropriate field in the <code>Message</code> accordingly.
     *  
     * @see SubscriptionRequestType.FIELD
     *
     * @param inMessage a <code>Message</code> value
     * @param inSubscriber an <code>ISubscriber</code> value
     * @return a <code>MarketDataFeedToken</code> value
     * @throws FeedException if an error occurs submitting the request
     */
    public MarketDataFeedToken execute(Message inMessage,
                                       ISubscriber inSubscriber)
            throws FeedException
    {
        // the token is used to track the request and its responses
        MarketDataFeedToken token = generateToken(inMessage);
        // the connector is used to bridge the divide between this object, which is an abstraction
        //  for a data feed service and the client, which does the actual job of connecting to the
        //  service
        IMarketDataFeedConnector connector = getConnector(getCredentials(),
                                                          token);
        // it's possible that some messages won't need subscribers, perhaps if the caller doesn't care
        //  about responses.  if the subscriber is null, ignore it.  otherwise, set the token to receive
        //  the responses        
        if(inSubscriber != null) {
            token.subscribe(inSubscriber);
        }
        
        // the thread pool is the object which manages the threads on which the connections are run
        // retrieve the thread pool to execute the request
        ExecutorService threadPool = getThreadPool();
        try {
            // this command executes the request using the connector.  the connector has all the information it needs
            //  to execute the request because of the token.  this command is asynchronous.
            Future<MarketDataFeedToken> response = threadPool.submit(connector);
            // wait for the response to be returned.  this doesn't mean that the results are back yet, just that
            //  the request has been received and acknowledged by the feed service.  if you can dig it, it's an
            //  asynchronous request for an asynchronous response.
            return response.get();
        } catch (RejectedExecutionException e) {
            throw new FeedException(MessageKey.ERROR_MARKET_DATA_FEED_EXECUTION_REJECTED.getLocalizedMessage(),
                                    e);
        } catch (CancellationException e) {
            throw new FeedException(MessageKey.ERROR_MARKET_DATA_FEED_EXECUTION_CANCELLED.getLocalizedMessage(),
                                    e);
        } catch (InterruptedException e) {
            throw new FeedException(MessageKey.ERROR_MARKET_DATA_FEED_EXECUTION_INTERRUPTED.getLocalizedMessage(),
                                    e);
        } catch (ExecutionException e) {
            throw new FeedException(MessageKey.ERROR_MARKET_DATA_FEED_EXECUTION_FAILED.getLocalizedMessage(),
                                    e);
        }
    }
    
    /**
     * The connector is responsible for taking a request in the form of a FIX message and transmitting it to a
     * market data feed service.
     * 
     * @param inCredentials a <code>MarketDataFeedCredentials</code> value
     * @return a <code>MarketDataFeedConnector</code> value encapsulating the request
     * @throws FeedException if an error occurs
     */
    protected abstract IMarketDataFeedConnector getConnector(MarketDataFeedCredentials inCredentials,
                                                             MarketDataFeedToken inToken)
        throws FeedException;
    
    /**
     * Generates a token encapsulating the given request.
     * 
     * <p>The object returned is dedicated to the execution of the given message.
     * 
     * @param inMessage a <code>Message</code> value encapsulating the data feed request
     * @return a <code>MarketDataFeedToken</code> value
     * @throws MarketceteraException if an error occurs
     */
    protected abstract MarketDataFeedToken generateToken(Message inMessage)
        throws FeedException;
    
    /**
     * Gets the thread pool for this type of data feed.
     * 
     * <p>The subclass is responsible for implementing an <code>ExecutorService</code>
     * thread pool for this data feed type.  Thread pools are typically common to all
     * objects of a given subclass, so the thread pool should be declared static to
     * the particlar subclass.  For example:
     * <pre>
     *   private static ExecutorService sThreadPool = Executors.newFixedThreadPool(10);
     *   
     *   protected ExecutorService getThreadPool()
     *   {
     *       return sThreadPool;
     *   }
     * </pre>
     * 
     * @return an <code>ExecutorService</code> value
     */
    protected abstract ExecutorService getThreadPool();
    
    public FeedStatus getFeedStatus()
    {
        return mFeedStatus;
    }

    public FeedType getFeedType()
    {
        return mFeedType;
    }

    public boolean isRunning()
    {
        return getFeedStatus().isRunning();
    }

    public void start()
    {
        setFeedStatus(FeedStatus.AVAILABLE);
    }

    public void stop()
    {
        setFeedStatus(FeedStatus.OFFLINE);
    }

    /**
     * @param inFeedStatus the feedStatus to set
     */
    protected void setFeedStatus(FeedStatus inFeedStatus)
    {
        mFeedStatus = inFeedStatus;
    }

    /**
     * @return the credentials
     */
    protected MarketDataFeedCredentials getCredentials()
    {
        return mCredentials;
    }

    /**
     * Creates a FIX message requesting market data on the given symbols.
     *
     * @param inSymbols a <code>List&lt;MSymbol&gt;</code> value containing the symbols on which to make requests
     * @param inUpdate a <code>boolean</code> value indicating whether the request should be a single snapshot or an
     *  ongoing subscription
     * @return a <code>Message</code> value
     * @throws FeedException if an error occurs constructing the <code>Message</code>
     */
    public Message marketDataRequest(List<MSymbol> inSymbols,
                                     boolean inUpdate) 
        throws FeedException 
    {
        try {
            // generate a unique ID for this FIX message
            InternalID id = getNextID();
            // generate the message using the current FIXMessageFactory
            Message message = getFIXVersion().getMessageFactory().newMarketDataRequest(id.toString(), 
                                                                                       inSymbols);
            // this little bit determines whether we subscribe to updates or not
            message.setChar(SubscriptionRequestType.FIELD, 
                            inUpdate ? '1' : '0');
            return message;
        } catch (NoMoreIDsException e) {
            throw new FeedException(MessageKey.ERROR_MARKET_DATA_FEED_CANNOT_GENERATE_MESSAGE.getLocalizedMessage(),
                                    e);
        }
    }
    
    /**
     * Gets the next ID in sequence for assiging unique identifiers to market data feed objects.
     *
     * @return an <code>InternalID</code> value
     * @throws NoMoreIDsException if no more IDs are available
     */
    protected static InternalID getNextID() 
        throws NoMoreIDsException
    {
        return new InternalID(sIDFactory.getNext());
    }

    /**
     * Gets the FIX version which the feed should use to construct FIX messages.
     * 
     * <p>The default is {@link FIXVersion.FIX44}.
     *
     * @return a <code>FIXVersion</code> value
     */
    protected FIXVersion getFIXVersion()
    {
        return DEFAULT_MESSAGE_FACTORY;
    }    
}
