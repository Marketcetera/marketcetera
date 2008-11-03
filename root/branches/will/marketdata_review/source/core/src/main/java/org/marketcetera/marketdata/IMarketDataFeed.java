package org.marketcetera.marketdata;

import org.marketcetera.core.publisher.ISubscriber;
import org.springframework.context.Lifecycle;

import quickfix.field.SubscriptionRequestType;

/**
 * A market data feed capable of resolving FIX message queries and returning
 * market data. 
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.5.0
 */
public interface IMarketDataFeed<T extends IMarketDataFeedToken<C>, 
                                 C extends IMarketDataFeedCredentials> 
    extends IFeedComponent, Lifecycle
{
    /**
     * Executes the given <code>Message</code> on this Market Data Feed.
     * 
     * <p>The <code>ISubscriber</code> value specified will receive the
     * response or responses from the market data feed either in the
     * case of a snapshot or a subscription.  To specify a subscription,
     * which will give updates as they become available until canceled,
     * set the appropriate field in the <code>Message</code> accordingly.
     *  
     * @see SubscriptionRequestType#FIELD
     *
     * @param inTokenSpec a <code>MarketDataFeedTokenSpec&lt;C&gt;</code> value encapsulating the data feed request
     * @return a <code>T</code> value
     * @throws NullPointerException if valid credentials are not available to execute this request
     * @throws FeedException if an error occurs submitting the request
     */
    public T execute(MarketDataFeedTokenSpec<C> inTokenSpec)
            throws FeedException;
    /**
     * Executes the given <code>Message</code> on this Market Data Feed.
     * 
     * <p>The <code>ISubscriber</code> value specified will receive the
     * response or responses from the market data feed either in the
     * case of a snapshot or a subscription.  To specify a subscription,
     * which will give updates as they become available until canceled,
     * set the appropriate field in the <code>Message</code> accordingly.
     *  
     * @see SubscriptionRequestType#FIELD
     *
     * @param inCredentials a <code>C</code> value or null to use the last known credentials
     * @param inMessage a <code>MarketDataRequest</code> value
     * @param inSubscriber an <code>ISubscriber...</code> value
     * @return a <code>T</code> value
     * @throws NullPointerException if valid credentials are not available to execute this request or
     *   a null message or subscriber is passed
     */
    public T execute(C inCredentials,
                     DataRequest inMessage,
                     ISubscriber... inSubscriber)
        throws FeedException;
    /**
     * Executes the given <code>MarketDataRequest</code> on this Market Data Feed.
     * 
     * <p>The <code>ISubscriber</code> value specified will receive the
     * response or responses from the market data feed either in the
     * case of a snapshot or a subscription.  To specify a subscription,
     * which will give updates as they become available until canceled,
     * set the appropriate field in the <code>MarketDataRequest</code> accordingly.
     * 
     * <p>The credentials used for this command are the credentials that
     * were last supplied.  If no credentials have been yet provided,
     * a <code>NullPointerException</code> will be thrown.
     *  
     * @see SubscriptionRequestType#FIELD
     *
     * @param inMessage a <code>MarketDataRequest</code> value
     * @param inSubscribers an <code>ISubscriber...</code> value
     * @return a <code>T</code> value
     * @throws NullPointerException if valid credentials are not available to execute this request or
     *   a null message or subscriber is passed
     */
    public T execute(DataRequest inMessage,
                     ISubscriber... inSubscribers)
        throws FeedException;
    /**
     * Subscribes to all market data passing through the feed.
     * 
     * <p>Subscribers will be notified of all market data received
     * from all queries.
     *
     * @param inSubscriber an <code>ISubscriber</code> value
     */
    public void subscribeToAll(ISubscriber inSubscriber);
    /**
     * Unsubscribes to all market data passing through the feed.
     *
     * @param inSubscriber an <code>ISubscriber</code> value
     */
    public void unsubscribeFromAll(ISubscriber inSubscriber);
}
