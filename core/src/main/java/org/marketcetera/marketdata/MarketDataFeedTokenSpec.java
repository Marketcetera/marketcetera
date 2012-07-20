package org.marketcetera.marketdata;

import java.util.Arrays;

import org.marketcetera.core.publisher.ISubscriber;

/**
 * Stateless portion of the market data feed token.
 * 
 * <p>The token spec encapsulates the information necessary to initiate
 * a query with an {@link MarketDataFeed} instance.  The token spec
 * is passed to {@link MarketDataFeed#execute(MarketDataFeedTokenSpec)}
 * to initiate the request.
 * 
 * <p>Currently, a token spec is bound to a specific feed implemenation
 * upon construction.  This means that the {@link MarketDataFeedToken}
 * that is returned by the feed may be reused via its token spec
 * for execution calls to the same feed type that originated the token.
 * In the future, this will be more flexible and the token spec can
 * be used to initiate a request with any feed.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.5.0
 */
public final class MarketDataFeedTokenSpec
{
    /**
     * the <code>MarketDataRequest</code> encapsulating the query
     */
    private final MarketDataRequest dataRequest;
    /**
     * the subscribers to whom to send query results
     */
    private final ISubscriber[] mSubscribers;
    /**
     * Generates a new token spec containing the passed information.
     *
     * <p>This token spec can be passed to a data feed to initiate a query.
     * The {@link MarketDataFeedToken} that is returned is bound to a
     * specific transaction, but the token spec is stateless and can be used
     * for many transactions.
     * 
     * @param inRequest a <code>Message</code> value
     * @param inSubscribers an <code>ISubscriber...</code> value which may be empty
     *   or null if no subscribers need to be notified of query results
     * @return a <code>MarketDataFeedTokenSpec</code> value
     * @throws NullPointerException if the passed credentials or message is null
     */
    public static MarketDataFeedTokenSpec generateTokenSpec(MarketDataRequest inRequest,
                                                            ISubscriber... inSubscribers)
    {
        return new MarketDataFeedTokenSpec(inRequest,
                                           inSubscribers);
    }
    /**
     * Create a new MarketDataFeedTokenSpec instance.
     *
     * @param inRequest a <code>MarketDataRequest</code> value
     * @param inSubscribers an <code>ISubscriber...</code> value which may be empty
     *   or null if no subscribers need to be notified of query results
     * @throws NullPointerException if the passed credentials or message is null
     */
    private MarketDataFeedTokenSpec(MarketDataRequest inRequest,
                                    ISubscriber... inSubscribers)
    {
        if(inRequest == null) {
            throw new NullPointerException();
        }
        dataRequest = inRequest;
        if(inSubscribers == null) {
            mSubscribers = new ISubscriber[0];
        } else {
            mSubscribers = inSubscribers;
        }
    }
    /**
     * Gets the market data request associated with this token spec.
     *
     * @return a <code>MarketDataRequest</code> value
     */
    public MarketDataRequest getDataRequest()
    {
        return dataRequest;
    }
    /**
     * Gets the subscribers associated with this token spec.
     *
     * @return an <code>ISubscriber[]</code> value
     */
    public ISubscriber[] getSubscribers()
    {
        return Arrays.copyOf(mSubscribers,
                             mSubscribers.length);
    }
}
