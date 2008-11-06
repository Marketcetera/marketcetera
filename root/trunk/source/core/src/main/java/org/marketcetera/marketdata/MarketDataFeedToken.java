package org.marketcetera.marketdata;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.publisher.IPublisher;

/* $License$ */

/**
 * Represents a transaction with an {@link MarketDataFeed}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.5.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public interface MarketDataFeedToken
        extends IPublisher
{
    /**
     * Describes the set of states for a token.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 0.5.0
     */
    public enum Status {
        /**
         * indicates that a token has been created, but it has not yet begun the process of
         * execution
         */
        NOT_STARTED,
        /**
         * execution is under way but has not yet been completed
         */
        RUNNING,
        /**
         * execution has been completed and the query is actively receiving updates
         */
        ACTIVE,
        /**
         * the query is in the process of being resubmitted
         */
        RESUBMITTING,
        /**
         * the query was active when the feed was stopped
         */
        SUSPENDED,
        /**
         * the user canceled the query
         */
        CANCELED,
        /**
         * the feed was not logged in to when the query was submitted and login with
         * the token's credentials failed
         */
        LOGIN_FAILED,
        /**
         * query initialization on the feed failed
         */
        INITIALIZATION_FAILED,
        /**
         * query execution failed
         */
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
                   this.equals(RESUBMITTING) ||
                   this.equals(SUSPENDED) ||
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
     * @see MarketDataFeed#execute(MarketDataFeedTokenSpec)
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
     * @return a <code>MarketDataFeedTokenSpec</code> value
     */
    public MarketDataFeedTokenSpec getTokenSpec();
}
