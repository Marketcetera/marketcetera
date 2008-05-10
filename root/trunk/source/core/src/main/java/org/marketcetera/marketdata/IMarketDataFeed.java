package org.marketcetera.marketdata;

import java.util.List;

import org.marketcetera.core.publisher.ISubscriber;
import org.springframework.context.Lifecycle;

import quickfix.Message;
import quickfix.field.SubscriptionRequestType;

/**
 * A market data feed capable of resolving FIX message queries and returning
 * market data. 
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.43-SNAPSHOT
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
     * @param inMessage a <code>Message</code> value
     * @param inSubscriber an <code>ISubscriber</code> value or null if no notifications are required
     * @return a <code>T</code> value
     * @throws NullPointerException if valid credentials are not available to execute this request
     */
    public T execute(C inCredentials,
                     Message inMessage,
                     ISubscriber inSubscriber)
        throws FeedException;
    /**
     * Executes the given <code>Message</code> on this Market Data Feed.
     * 
     * <p>The <code>ISubscriber</code> value specified will receive the
     * response or responses from the market data feed either in the
     * case of a snapshot or a subscription.  To specify a subscription,
     * which will give updates as they become available until cancelled,
     * set the appropriate field in the <code>Message</code> accordingly.
     *  
     * @see SubscriptionRequestType#FIELD
     *
     * @param inCredentials a <code>C</code> value
     * @param inMessage a <code>Message</code> value
     * @param inSubscribers a <code>List&lt;? extends ISubscriber&gt;</code> value
     * @return a <code>T</code> value
     * @throws NullPointerException if valid credentials are not available to execute this request
     */
    public T execute(C inCredentials,
                     Message inMessage,
                     List<? extends ISubscriber> inSubscribers)
        throws FeedException;
    /**
     * Executes the given <code>Message</code> on this Market Data Feed.
     * 
     * <p>The <code>ISubscriber</code> value specified will receive the
     * response or responses from the market data feed either in the
     * case of a snapshot or a subscription.  To specify a subscription,
     * which will give updates as they become available until cancelled,
     * set the appropriate field in the <code>Message</code> accordingly.
     * 
     * <p>The credentials used for this command are the credentials that
     * were last supplied.  If no credentials have been yet provided,
     * a <code>NullPointerException</code> will be thrown.
     *  
     * @see SubscriptionRequestType#FIELD
     *
     * @param inMessage a <code>Message</code> value
     * @param inSubscriber an <code>ISubscriber</code> value
     * @return a <code>T</code> value
     * @throws NullPointerException if valid credentials are not available to execute this request
     */
    public T execute(Message inMessage,
                     ISubscriber inSubscriber)
        throws FeedException;
    /**
     * Executes the given <code>Message</code> on this Market Data Feed.
     * 
     * <p>The <code>ISubscriber</code> value specified will receive the
     * response or responses from the market data feed either in the
     * case of a snapshot or a subscription.  To specify a subscription,
     * which will give updates as they become available until cancelled,
     * set the appropriate field in the <code>Message</code> accordingly.
     * 
     * <p>The credentials used for this command are the credentials that
     * were last supplied.  If no credentials have been yet provided,
     * a <code>NullPointerException</code> will be thrown.
     *  
     * @see SubscriptionRequestType#FIELD
     *
     * @param inMessage a <code>Message</code> value
     * @param inSubscribers a <code>List&lt;? extends ISubscriber&gt;</code> value
     * @return a <code>T</code> value
     * @throws NullPointerException if valid credentials are not available to execute this request
     */
    public T execute(Message inMessage,
                     List<? extends ISubscriber> inSubscribers)
        throws FeedException;
}
