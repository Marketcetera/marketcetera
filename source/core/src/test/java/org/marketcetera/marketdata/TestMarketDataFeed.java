package org.marketcetera.marketdata;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.marketcetera.core.IFeedComponentListener;
import org.marketcetera.core.MSymbol;
import org.marketcetera.core.MarketceteraException;
import org.marketcetera.core.publisher.ISubscriber;

import quickfix.Message;

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: $
 * @since 0.43-SNAPSHOT
 */
public class TestMarketDataFeed
    extends AbstractMarketDataFeed
{
    private static ExecutorService sThreadPool = Executors.newFixedThreadPool(1);
    private final int mDelay;
      
    public TestMarketDataFeed() 
        throws FeedException
    {
        this(FeedType.UNKNOWN,
             new TestMarketDataFeedCredentials());
    }
    
    /**
     * Create a new <code>TestMarketDataFeed</code> instance.
     *
     * @param inFeedType
     * @param inCredentials
     */
    public TestMarketDataFeed(FeedType inFeedType,
                              MarketDataFeedCredentials inCredentials)
    {
        this(inFeedType,
             inCredentials,
             0);
    }

    /**
     * Create a new <code>TestMarketDataFeed</code> instance.
     *
     * @param inFeedType
     * @param inCredentials
     */
    public TestMarketDataFeed(FeedType inFeedType,
                              MarketDataFeedCredentials inCredentials,
                              int inDelay)
    {
        super(inFeedType,
              inCredentials);
        mDelay = inDelay;
    }

    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#generateToken(quickfix.Message)
     */
    @Override
    protected TestMarketDataFeedToken generateToken(Message inMessage)
            throws FeedException
    {
        return new TestMarketDataFeedToken(inMessage);
    }

    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#getConnector(org.marketcetera.marketdata.MarketDataFeedCredentials, org.marketcetera.marketdata.MarketDataFeedToken)
     */
    @Override
    protected TestMarketDataFeedConnector getConnector(MarketDataFeedCredentials inCredentials,
                                                       MarketDataFeedToken inToken)
            throws FeedException
    {
        return new TestMarketDataFeedConnector((TestMarketDataFeedToken)inToken,
                                               mDelay);
    }

    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#getThreadPool()
     */
    @Override
    protected ExecutorService getThreadPool()
    {
        return sThreadPool;
    }

    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.IMarketDataFeed#asyncQuery(quickfix.Message)
     */
    public ISubscription asyncQuery(Message inQuery)
            throws MarketceteraException
    {
        return null;
    }

    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.IMarketDataFeed#asyncUnsubscribe(org.marketcetera.marketdata.ISubscription)
     */
    public void asyncUnsubscribe(ISubscription inSubscription)
            throws MarketceteraException
    {
    }

    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.IMarketDataFeed#getMarketDataListener()
     */
    public IMarketDataListener getMarketDataListener()
    {
        return null;
    }

    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.IMarketDataFeed#setMarketDataListener(org.marketcetera.marketdata.IMarketDataListener)
     */
    public void setMarketDataListener(IMarketDataListener inListener)
    {
    }

    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.IMarketDataFeed#symbolFromString(java.lang.String)
     */
    public MSymbol symbolFromString(String inSymbolString)
    {
        return null;
    }

    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.IMarketDataFeed#syncQuery(quickfix.Message, long, java.util.concurrent.TimeUnit)
     */
    public List<Message> syncQuery(Message inQuery,
                                   long inTimeout,
                                   TimeUnit inUnits)
            throws MarketceteraException, TimeoutException
    {
        return null;
    }

    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.IFeedComponent#addFeedComponentListener(org.marketcetera.core.IFeedComponentListener)
     */
    public void addFeedComponentListener(IFeedComponentListener inListener)
    {
    }

    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.IFeedComponent#getID()
     */
    public String getID()
    {
        return null;
    }

    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.IFeedComponent#removeFeedComponentListener(org.marketcetera.core.IFeedComponentListener)
     */
    public void removeFeedComponentListener(IFeedComponentListener inListener)
    {
    }

    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#execute(quickfix.Message, org.marketcetera.core.publisher.ISubscriber)
     */
    @Override
    public TestMarketDataFeedToken execute(Message inMessage,
                                           ISubscriber inSubscriber)
            throws FeedException
    {
        return (TestMarketDataFeedToken)super.execute(inMessage,
                                                      inSubscriber);
    }
}
