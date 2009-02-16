package org.marketcetera.marketdata;

import java.util.Set;

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
public interface MarketDataFeed<T extends MarketDataFeedToken, 
                                C extends MarketDataFeedCredentials> 
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
    public T execute(MarketDataFeedTokenSpec inTokenSpec)
            throws FeedException;
    /**
     * Logs in to the Market Data Feed with the given credentials.
     *
     * <p>If the feed has already been logged in to, this method does nothing.
     * 
     * @param inCredentials a <code>C</code> value
     * @return a boolean value set to true if the login was successful, false otherwise
     */
    public boolean login(C inCredentials);
    /**
     * Logs out of the Market Data feed.
     *
     * <p>If the feed is not currently logged in, this method does nothing.
     */
    public void logout();
    /**
     * Gets the set of capabilities for this market data feed.
     *
     * @return a <code>Set&lt;Capability&gt;</code> value
     */
    public Set<Capability> getCapabilities();
}
