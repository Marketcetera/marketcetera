package org.marketcetera.marketdata;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.marketcetera.core.InMemoryIDFactory;
import org.marketcetera.core.InternalID;
import org.marketcetera.core.MSymbol;
import org.marketcetera.core.MarketceteraException;
import org.marketcetera.core.NoMoreIDsException;
import org.marketcetera.core.publisher.Subscriber;
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
 * @author andrei@lissovski.org
 * @author gmiller
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 */
public abstract class MarketDataFeedBase 
    implements IMarketDataFeed 
{
    private FeedStatus mFeedStatus;
    private final FeedType mFeedType;
    private final MarketDataFeedCredentials mCredentials;
    private static final InMemoryIDFactory sIDFactory = new InMemoryIDFactory(System.currentTimeMillis(),
                                                                              "mdf");
    
    protected MarketDataFeedBase(FeedType inFeedType,
                                 MarketDataFeedCredentials inCredentials)
    {
        setFeedStatus(FeedStatus.OFFLINE);
        mFeedType = inFeedType;
        mCredentials = inCredentials;
    }
    
    public MarketDataFeedToken execute(Message inMessage,
                                       Subscriber inSubscriber)
            throws FeedException
    {
        MarketDataFeedToken token = generateToken(inMessage);
        MarketDataFeedConnector connector = getConnector(getCredentials(),
                                                         token);
        
        if(inSubscriber != null) {
            token.subscribe(inSubscriber);
        }
        
        ExecutorService threadPool = getThreadPool();
        Future<MarketDataFeedToken> response = threadPool.submit(connector);        
        try {
            return response.get();
        } catch (Throwable t) {
            throw new FeedException(t);
        }
    }
    
    /**
     * Returns an object capable of translating a FIX message into a data request to its
     * data feed service.
     * 
     * @param inCredentials a <code>MarketDataFeedCredentials</code> value
     * @return a <code>MarketDataFeedConnector</code> value encapsulating the request
     * @throws FeedException if an error occurs
     */
    protected abstract MarketDataFeedConnector getConnector(MarketDataFeedCredentials inCredentials,
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

    public static Message marketDataRequest(List<MSymbol> inSymbols,
                                            boolean inUpdate) 
        throws FeedException 
    {
        InternalID id = null;
        try {
            id = getNextID();
        } catch (NoMoreIDsException e) {
            throw new FeedException(e);
        }
        Message message = FIXVersion.FIX44.getMessageFactory().newMarketDataRequest(id.toString(), 
                                                                                    inSymbols);
        message.setChar(SubscriptionRequestType.FIELD, 
                        inUpdate ? '1' : '0');
        return message;
    }
    
    protected static InternalID getNextID() 
        throws NoMoreIDsException
    {
        return new InternalID(sIDFactory.getNext());
    }
}
