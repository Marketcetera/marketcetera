package org.marketcetera.marketdata.bogus;

import static org.marketcetera.marketdata.Capability.LATEST_TICK;
import static org.marketcetera.marketdata.Capability.LEVEL_2;
import static org.marketcetera.marketdata.Capability.OPEN_BOOK;
import static org.marketcetera.marketdata.Capability.MARKET_STAT;
import static org.marketcetera.marketdata.Capability.TOP_OF_BOOK;
import static org.marketcetera.marketdata.Capability.TOTAL_VIEW;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.marketcetera.core.NoMoreIDsException;
import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.event.EventBase;
import org.marketcetera.marketdata.AbstractMarketDataFeed;
import org.marketcetera.marketdata.Capability;
import org.marketcetera.marketdata.FeedException;
import org.marketcetera.marketdata.MarketDataFeed;
import org.marketcetera.marketdata.MarketDataFeedTokenSpec;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.SimulatedExchange;
import org.marketcetera.marketdata.MarketDataRequest.Content;
import org.marketcetera.marketdata.SimulatedExchange.Token;
import org.marketcetera.trade.MSymbol;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Sample implementation of {@link MarketDataFeed}.
 *
 * <p>This implementation generates random market data for each
 * symbol for which a market data request is received.  Data is returned
 * from the feed via {@link EventBase} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.5.0
 */
@ClassVersion("$Id$")
public class BogusFeed 
    extends AbstractMarketDataFeed<BogusFeedToken,
                                   BogusFeedCredentials,
                                   BogusFeedMessageTranslator,
                                   BogusFeedEventTranslator,
                                   MarketDataRequest,
                                   BogusFeed> 
{
    /**
     * Returns an instance of <code>BogusFeed</code>.
     *
     * @param inProviderName a <code>String</code> value
     * @return a <code>BogusFeed</code> value
     * @throws NoMoreIDsException if a unique identifier could not be generated to
     *   be assigned
     */
    public synchronized static BogusFeed getInstance(String inProviderName) 
        throws NoMoreIDsException
    {
        if(sInstance != null) {
            return sInstance;
        }
        sInstance = new BogusFeed(inProviderName);
        return sInstance;
    }
    /**
     * Create a new BogusFeed instance.
     *
     * @param inProviderName a <code>String</code> value
     * @throws NoMoreIDsException if a unique identifier could not be generated to
     *   be assigned
     */
	private BogusFeed(String inProviderName) 
		throws NoMoreIDsException 
	{
		super(FeedType.SIMULATED,
              inProviderName);
        setLoggedIn(false);
	}
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#start()
     */
    @Override
	public synchronized void start() {
        if(getFeedStatus().isRunning()) {
            throw new IllegalStateException();
        }
        for(int i=0;i<EXCHANGE_COUNT;i++) {
            SimulatedExchange exchange = new SimulatedExchange(String.format("%s-%d", //$NON-NLS-1$
                                                                             getProviderName(),
                                                                             i+1),
                                                               String.format("BGS%d", //$NON-NLS-1$
                                                                             i+1));
            SLF4JLoggerProxy.debug(BogusFeed.class,
                                   "BogusFeed starting exchange {}...", //$NON-NLS-1$
                                   exchange.getCode());
            exchange.start();
            exchanges.put(exchange.getCode(),
                          exchange);
        }
        super.start();
	}
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#stop()
     */
    @Override
    public synchronized void stop() {
        if(!getFeedStatus().isRunning()) {
            throw new IllegalStateException();
        }
        for(SimulatedExchange exchange : exchanges.values()) {
            SLF4JLoggerProxy.debug(BogusFeed.class,
                                   "BogusFeed stopping exchange {}...", //$NON-NLS-1$
                                   exchange.getCode());
            exchange.stop();
        }
        super.stop();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataFeed#getCapabilities()
     */
    @Override
    public Set<Capability> getCapabilities()
    {
        return capabilities;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#doCancel(java.lang.String)
     */
    @Override
    protected final synchronized void doCancel(String inHandle)
    {
        Request.cancel(inHandle);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#doLevelOneMarketDataRequest(java.lang.Object)
     */
    @Override
    protected final synchronized List<String> doMarketDataRequest(MarketDataRequest inData)
        throws FeedException
    {
        return Arrays.asList(Request.execute(inData,
                                             this));
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#doLogin(org.marketcetera.marketdata.AbstractMarketDataFeedCredentials)
     */
    @Override
    protected final boolean doLogin(BogusFeedCredentials inCredentials)
    {
        setLoggedIn(true);
        return true;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#doLogout()
     */
    @Override
    protected final void doLogout()
    {
        setLoggedIn(false);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#generateToken(quickfix.Message)
     */
    @Override
    protected final BogusFeedToken generateToken(MarketDataFeedTokenSpec inTokenSpec)
            throws FeedException
    {
        return BogusFeedToken.getToken(inTokenSpec,
                                       this);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#getEventTranslator()
     */
    @Override
    protected final BogusFeedEventTranslator getEventTranslator()
    {
        return BogusFeedEventTranslator.getInstance();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#getMessageTranslator()
     */
    @Override
    protected final BogusFeedMessageTranslator getMessageTranslator()
    {
        return BogusFeedMessageTranslator.getInstance();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#isLoggedIn(org.marketcetera.marketdata.AbstractMarketDataFeedCredentials)
     */
    @Override
    protected final boolean isLoggedIn()
    {
        return mLoggedIn;
    }
    /**
     * Sets the loggedIn value.
     *
     * @param inLoggedIn Logged-in status of the feed
     */
    private void setLoggedIn(boolean inLoggedIn)
    {
        mLoggedIn = inLoggedIn;
    }
    /**
     * Gets the list of exchanges associated with the given exchange code.
     *
     * @param inExchange a <code>String</code> value containing an exchange code or null to return
     *  all exchanges
     * @return a <code>List&lt;SimulatedExchange&gt;</code> value
     */
    private List<SimulatedExchange> getExchangesForCode(String inExchange)
    {
        if(inExchange == null ||
           inExchange.isEmpty()) {
            // request data from all exchanges
            return new ArrayList<SimulatedExchange>(exchanges.values());
        }
        if(exchanges.containsKey(inExchange)) {
            return Arrays.asList(new SimulatedExchange[] { exchanges.get(inExchange) });
        }
        return new ArrayList<SimulatedExchange>();
    }
    /**
     * capabilities for BogusFeed - note that these are not dynamic as Bogus requires no provisioning
     */
    private static final Set<Capability> capabilities = Collections.unmodifiableSet(EnumSet.of(TOP_OF_BOOK,LEVEL_2,OPEN_BOOK,TOTAL_VIEW,LATEST_TICK,MARKET_STAT));
    /**
     * indicates if the feed has been logged in to
     */
    private boolean mLoggedIn;
    /**
     * exchanges that make up the group for which the bogus feed can report data
     */
    private final Map<String,SimulatedExchange> exchanges = new HashMap<String,SimulatedExchange>();
    /**
     * static instance of <code>BogusFeed</code>
     */
    private static BogusFeed sInstance;
    /**
     * arbitrarily chosen number of internal exchanges to aggregate
     */
    private static final int EXCHANGE_COUNT = 3;
    /**
     * Corresponds to a single market data request submitted to {@link BogusFeed}.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 1.5.0
     */
    @ClassVersion("$Id$")
    private static class Request
    {
        /**
         * Executes the given <code>MarketDataRequest</code> and returns
         * a handle corresponding to the request.
         *
         * @param inRequest a <code>MarketDataRequest</code> value
         * @param inParentFeed a <code>BogusFeed</code> value
         * @return a <code>String</code> value
         */
        private static String execute(MarketDataRequest inRequest,
                                      BogusFeed inParentFeed)
        {
            Request request = new Request(inRequest,
                                          inParentFeed);
            request.execute();
            return request.getIDAsString();
        }
        /**
         * Cancels the market data request associated with the given handle.
         *
         * @param inHandle a <code>String</code> value
         */
        private static void cancel(String inHandle)
        {
            Request request;
            synchronized(requests) {
                request = requests.remove(inHandle);
            }
            if(request != null) {
                request.cancel();
            }
        }
        /**
         * Create a new Request instance.
         *
         * @param inRequest a <code>MarketDataRequest</code> value
         * @param inFeed a <code>BogusFeed</code> value
         */
        private Request(MarketDataRequest inRequest,
                        BogusFeed inFeed)
        {
            request = inRequest;
            feed = inFeed;
            subscriber = new ISubscriber() {
                @Override
                public boolean isInteresting(Object inData)
                {
                    return true;
                }
                @Override
                public void publishTo(Object inData)
                {
                    SLF4JLoggerProxy.debug(BogusFeed.class,
                                           "BogusFeed publishing {}", //$NON-NLS-1$
                                           inData);
                    feed.dataReceived(getIDAsString(),
                                      inData);
                }
            };
            synchronized(requests) {
                requests.put(getIDAsString(),
                             this);
            }
        }
        /**
         * Executes the market data request associated with this object.
         * 
         * @throws IllegalStateException if this method has already been executed for this object
         */
        private synchronized void execute()
        {
            if(executed) {
                throw new IllegalStateException();
            }
            try {
                List<MSymbol> symbols = new ArrayList<MSymbol>();
                for(String symbol : request.getSymbols()) {
                    symbols.add(new MSymbol(symbol));
                }
                for(MSymbol symbol : symbols) {
                    // all symbols for which we want data are collected in the symbols list
                    // each type of subscription is managed differently
                    for(Content content : request.getContent()) {
                        switch(content) {
                            case LEVEL_2 :
                                // LEVEL_2 is NASDAQ Level II data, which is TOP_OF_BOOK from all
                                //  managed exchanges.
                                doTopOfBook(symbol,
                                            null);
                                break;
                            case TOP_OF_BOOK :
                                // TOP_OF_BOOK from the specified exchange only
                                doTopOfBook(symbol,
                                            request.getExchange());
                                break;
                            case OPEN_BOOK :
                                // OPEN_BOOK is depth-of-book from the specified exchange
                                doDepthOfBook(symbol,
                                              request.getExchange());
                                break;
                            case TOTAL_VIEW :
                                // TOTAL_VIEW is depth-of-book from the specified exchange
                                doDepthOfBook(symbol,
                                              request.getExchange());
                                break;
                            case LATEST_TICK :
                                // LATEST_TICK is the most recent trade
                                doLatestTick(symbol,
                                             request.getExchange());
                                break;
                            case MARKET_STAT :
                                doStatistics(symbol,
                                             request.getExchange());
                                break;
                            default:
                                throw new UnsupportedOperationException();
                        }
                    }
                }
            } finally {
                executed = true;
            }
        }
        /**
         * Executes a statistics request for the given symbol using the
         * given exchange code.
         *
         * @param inSymbol an <code>MSymbol</code> value
         * @param inExchangeToUse a <code>String</code> value
         */
        private void doStatistics(MSymbol inSymbol,
                                  String inExchangeToUse)
        {
            for(SimulatedExchange exchange : feed.getExchangesForCode(inExchangeToUse)) {
                exchangeTokens.add(exchange.getStatistics(inSymbol,
                                                          subscriber));
            }
        }
        /**
         * Executes a depth-of-book request for the given symbol using the
         * given exchange code.
         *
         * @param inSymbol an <code>MSymbol</code> value
         * @param inExchangeToUse a <code>String</code> value
         */
        private void doDepthOfBook(MSymbol inSymbol,
                                   String inExchangeToUse)
        {
            for(SimulatedExchange exchange : feed.getExchangesForCode(inExchangeToUse)) {
                exchangeTokens.add(exchange.getDepthOfBook(inSymbol,
                                                           subscriber));
            }
        }
        /**
         * Executes a top-of-book request for the given symbol using the
         * given exchange code.
         *
         * @param inSymbol an <code>MSymbol</code> value
         * @param inExchangeToUse a <code>String</code> value
         */
        private void doTopOfBook(MSymbol inSymbol,
                                 String inExchangeToUse)
        {
            for(SimulatedExchange exchange : feed.getExchangesForCode(inExchangeToUse)) {
                exchangeTokens.add(exchange.getTopOfBook(inSymbol,
                                                         subscriber));
            }
        }
        /**
         * Executes a latest-tick request for the given symbol using the given
         * exchange code.
         *
         * @param inSymbol an <code>MSymbol</code> value
         * @param inExchangeToUse a <code>String</code> value
         */
        private void doLatestTick(MSymbol inSymbol,
                                  String inExchangeToUse)
        {
            for(SimulatedExchange exchange : feed.getExchangesForCode(inExchangeToUse)) {
                exchangeTokens.add(exchange.getLatestTick(inSymbol,
                                                          subscriber));
            }
        }
        /**
         * Cancels all subscriptions associated with this request.
         */
        private void cancel()
        {
            for(Token token : exchangeTokens) {
                token.cancel();
            }
        }
        /**
         * Returns the request ID as a <code>String</code>. 
         *
         * @return a <code>String</code> value
         */
        private String getIDAsString()
        {
            return Long.toHexString(id);
        }
        /**
         * the collection of subscription tokens associated with this request
         */
        private final List<Token> exchangeTokens = new ArrayList<Token>();
        /**
         * the market data request associated with this object
         */
        private final MarketDataRequest request;
        /**
         * the unique identifier of this request
         */
        private final long id = counter.incrementAndGet();
        /**
         * the parent object for this request
         */
        private final BogusFeed feed;
        /**
         * the bridge object which receives responses from the parent's nested exchanges
         * and forwards them to the submitter of the request
         */
        private final ISubscriber subscriber;
        /**
         * indicates whether this object has been executed yet or not
         */
        private boolean executed = false;
        /**
         * all requests by their ID (represented as string)
         */
        private static final Map<String,Request> requests = new HashMap<String,Request>();
        /**
         * counter used to generate unique ids
         */
        private static final AtomicLong counter = new AtomicLong(0);
    }
}
