package org.marketcetera.marketdata;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.core.publisher.PublisherEngine;

/**
 * Represents the responses to a market data request.
 *
 * <p>This class provides a handle to the responses to a market data request.  A token
 * has a stateless and a stateful component.  The stateless component, the
 * <code>MarketDataFeedTokenSpec</code> contains the information necessary to reproduce
 * the query represented by this token.
 * 
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.43-SNAPSHOT
 */
public abstract class AbstractMarketDataFeedToken<F extends AbstractMarketDataFeed,
                                                  C extends IMarketDataFeedCredentials>
    implements IMarketDataFeedToken<C>
{
    /**
     * the stateless portion of the token which represents the original request 
     */
    private final MarketDataFeedTokenSpec<C> mTokenSpec;
    /**
     * the data feed to which this token is bound
     */
    private final F mFeed;
    /**
     * publishing engine used to update subscribers of responses to the request
     */
    private final PublisherEngine mPublisher;        
    /**
     * the status of this token
     */
    private Status mStatus = Status.NOT_STARTED;
    /**
     * indicates whether the token would prefer to publish synchronously or asynchronously
     */
    private boolean mSynchronousPublications = false;
    /**
     * Create a new <code>AbstractMarketDataFeedToken</code> object.
     *
     * @param inTokenSpec a <code>MarketDataFeedTokenSpec&lt;C&gt;</code> value encapsulating the data feed request
     * @param inFeed a <code>F</code> value containing the feed to which this token is bound
     * @throws NullPointerException if the token spec or feed is null
     */
    protected AbstractMarketDataFeedToken(MarketDataFeedTokenSpec<C> inTokenSpec,
                                          F inFeed) 
    {
        if(inTokenSpec == null ||
           inFeed == null) {
            throw new NullPointerException();
        }
        mTokenSpec = inTokenSpec;
        mFeed = inFeed;
        mPublisher = new PublisherEngine();
    }
    /**
     * Publishes the given data to all subscribers.
     *
     * @param inData an <code>Object</code> value
     * @throws InterruptedException if synchronous publications are selected and the thread is interrupted
     *   while notifying a publisher
     * @throws ExecutionException 
     */
    protected final void publish(Object inData) 
        throws InterruptedException, ExecutionException
    {
        if(isSynchronousPublications()) {
            getPublisher().publishAndWait(inData);
        } else {
            getPublisher().publish(inData);
        }
    }
    /**
     * Adds all given subscribers to the subscriber list.
     *
     * @param inSubscribers a <code>List&lt;ISubscriber&gt;</code> value
     */
    public final void subscribeAll(List<? extends ISubscriber> inSubscribers)
    {
        if(inSubscribers == null) {
            return;
        }
        for(ISubscriber subscriber : inSubscribers) {
            subscribe(subscriber);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.publisher.IPublisher#subscribe(org.marketcetera.core.publisher.ISubscriber)
     */
    public final void subscribe(ISubscriber inSubscriber)
    {
        getPublisher().subscribe(inSubscriber);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.publisher.IPublisher#unsubscribe(org.marketcetera.core.publisher.ISubscriber)
     */
    public final void unsubscribe(ISubscriber inSubscriber)
    {
        getPublisher().unsubscribe(inSubscriber);
    }
    /**
     * Get the status value.
     *
     * @return a <code>Status</code> value
     */
    public final Status getStatus()
    {
        return mStatus;
    }
    /**
     * Sets the status value.
     *
     * @param inStatus a <code>AbstractMarketDataFeedToken</code> value
     */
    protected final void setStatus(Status inStatus)
    {
        mStatus = inStatus;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.IMarketDataFeedToken#cancel()
     */
    // this is to avoid having to add all the types to the class declaration to
    //  define the type of mFeed
    @SuppressWarnings("unchecked")
    public final void cancel()
    {
        if(getStatus().cancelable()) {
            mFeed.cancel(this);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.IMarketDataFeedToken#getTokenSpec()
     */
    public final MarketDataFeedTokenSpec<C> getTokenSpec()
    {
        return mTokenSpec;
    }    
    /**
     * Gets the publisher engine.
     * 
     * @return a <code>PublisherEnginer</code> value
     */
    protected final PublisherEngine getPublisher()
    {
        return mPublisher;
    }
    /**
     * Get the synchronousPublications value.
     *
     * @return a <code>AbstractMarketDataFeedToken</code> value
     */
    protected final boolean isSynchronousPublications()
    {
        return mSynchronousPublications;
    }
    /**
     * Sets the synchronousPublications value.
     *
     * @param a <code>AbstractMarketDataFeedToken</code> value
     */
    protected final void setSynchronousPublications(boolean inSynchronousPublications)
    {
        mSynchronousPublications = inSynchronousPublications;
    }
}
