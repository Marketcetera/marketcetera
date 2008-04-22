package org.marketcetera.marketdata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.marketcetera.core.publisher.ISubscriber;

import quickfix.Message;

/**
 * Stateless portion of the market data feed token.
 * 
 * <p>The token spec encapsulates the information necessary to initiate
 * a query with an {@link IMarketDataFeed} instance.  The token spec
 * is passed to {@link IMarketDataFeed#execute(MarketDataFeedTokenSpec)}
 * to initiate the request.
 * 
 * <p>Currently, a token spec is bound to a specific feed implemenation
 * upon construction.  This means that the {@link IMarketDataFeedToken}
 * that is returned by the feed may be reused via its token spec
 * for execution calls to the same feed type that originated the token.
 * In the future, this will be more flexible and the token spec can
 * be used to initiate a request with any feed.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: $
 * @since 0.43-SNAPSHOT
 */
public class MarketDataFeedTokenSpec<C extends IMarketDataFeedCredentials>
{
    /**
     * the credentials supplied to be passed to the feed
     */
    private final C mCredentials;
    /**
     * the <code>FIX</code> message encapsulating the query
     */
    private final Message mMessage;
    /**
     * the subscribers to whom to send query results
     */
    private final List<? extends ISubscriber> mSubscribers;
    /**
     * Generates a new token spec containing the passed information.
     *
     * <p>This token spec can be passed to a data feed to initiate a query.
     * The {@link IMarketDataFeedToken} that is returned is bound to a
     * specific transaction, but the token spec is stateless and can be used
     * for many transactions.
     * 
     * @param inCredentials a <code>C</code> value
     * @param inMessage a <code>Message</code> value
     * @param inSubscribers a <code>List&lt;? extends ISubscriber&gt;</code> value which may be empty
     *   if no subscribers need to be notified of query resultss
     * @return a MarketDataFeedTokenSpec&lt;C&gt; value
     * @throws NullPointerException if the passed credentials or message is null
     */
    public static <C extends IMarketDataFeedCredentials>MarketDataFeedTokenSpec<C> generateTokenSpec(C inCredentials,
                                                                                                     Message inMessage,
                                                                                                     List<? extends ISubscriber> inSubscribers)
    {
        return new MarketDataFeedTokenSpec<C>(inCredentials,
                                              inMessage,
                                              inSubscribers);
    }
    /**
     * Create a new MarketDataFeedTokenSpec instance.
     *
     * @param inCredentials a <code>C</code> value
     * @param inMessage a <code>Message</code> value
     * @param inSubscribers a <code>List&lt;? extends ISubscriber&gt;</code> value which may be empty
     * @throws NullPointerException if the passed credentials or message is null
     */
    private MarketDataFeedTokenSpec(C inCredentials,
                                    Message inMessage,
                                    List<? extends ISubscriber> inSubscribers)
    {
        if(inCredentials == null ||
           inMessage == null) {
            throw new NullPointerException();
        }
        mCredentials = inCredentials;
        mMessage = inMessage;
        if(inSubscribers == null) {
            mSubscribers = new ArrayList<ISubscriber>();
        } else {
            mSubscribers = new ArrayList<ISubscriber>(inSubscribers);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.IMarketDataFeedTokenSpec#getCredentialSet()
     */
    public C getCredentials()
    {
        return mCredentials;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.IMarketDataFeedTokenSpec#getMessage()
     */
    public Message getMessage()
    {
        return mMessage;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.IMarketDataFeedTokenSpec#getSubscribers()
     */
    public List<? extends ISubscriber> getSubscribers()
    {
        return Collections.unmodifiableList(mSubscribers);
    }
}
