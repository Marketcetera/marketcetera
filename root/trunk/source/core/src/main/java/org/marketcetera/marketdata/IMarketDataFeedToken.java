package org.marketcetera.marketdata;

import org.marketcetera.core.publisher.IPublisher;

/**
 * Represents a transaction with an {@link IMarketDataFeed}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: $
 * @since 0.43-SNAPSHOT
 */
public interface IMarketDataFeedToken<C extends IMarketDataFeedCredentials>
        extends IPublisher
{
    /**
     * Describes the set of states for a token.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id: $
     * @since 0.43-SNAPSHOT
     */
    public enum Status {
        NOT_STARTED, 
        RUNNING, 
        ACTIVE, 
        CANCELED, 
        LOGIN_FAILED, 
        INITIALIZATION_FAILED, 
        EXECUTION_FAILED;

        /**
         * Indicates if a token with this status may be canceled.
         *
         * @return a <code>boolean</code> value
         */
        public boolean cancelable()
        {
            return this.equals(NOT_STARTED) ||
                   this.equals(RUNNING) ||
                   this.equals(ACTIVE);
        }
    };    
    /**
     * Returns the status of the transaction represented by this token.
     *
     * @return a <code>Status</code> value
     */
    public Status getStatus();
    /**
     * Cancels the query represented by this token.
     *
     * <p>Once cancelled, the query cannot be reactivated.  The token
     * can be used to generate a new query with the same terms and
     * subscribers.
     * @see IMarketDataFeed#execute(MarketDataFeedTokenSpec)
     */
    public void cancel();
    /**
     * Returns the token spec or stateless component of this token.
     *
     * <p>The token spec can be used to replicate the query represented
     * by this token.  Currently, the token specs are bound to a specific
     * feed.  In the future, the token spec will be usable with any feed
     * regardless of its origin.
     * 
     * @return a <code>MarketDataFeedTokenSpec&lt;C&gt;</code> value
     */
    public MarketDataFeedTokenSpec<C> getTokenSpec();
}
