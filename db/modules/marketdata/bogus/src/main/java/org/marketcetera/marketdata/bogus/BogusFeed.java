package org.marketcetera.marketdata.bogus;

import static org.marketcetera.marketdata.AssetClass.EQUITY;
import static org.marketcetera.marketdata.AssetClass.FUTURE;
import static org.marketcetera.marketdata.AssetClass.OPTION;
import static org.marketcetera.marketdata.AssetClass.CURRENCY;
import static org.marketcetera.marketdata.Capability.*;
import static org.marketcetera.marketdata.bogus.Messages.UNSUPPORTED_OPTION_SPECIFICATION;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import org.marketcetera.core.NoMoreIDsException;
import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.event.Event;
import org.marketcetera.marketdata.*;
import org.marketcetera.options.OptionUtils;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Future;
import org.marketcetera.trade.Currency;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Sample implementation of {@link MarketDataFeed}.
 *
 * <p>This implementation generates random market data for each
 * symbol for which a market data request is received.  Data is returned
 * from the feed via {@link Event} objects.
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
     * @see org.marketcetera.marketdata.MarketDataFeed#getSupportedAssetClasses()
     */
    @Override
    public Set<AssetClass> getSupportedAssetClasses()
    {
        return assetClasses;
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
    private static final Set<Capability> capabilities = Collections.unmodifiableSet(EnumSet.of(TOP_OF_BOOK,LEVEL_2,OPEN_BOOK,TOTAL_VIEW,LATEST_TICK,MARKET_STAT,DIVIDEND));
    /**
     * supported asset classes
     */
    private static final Set<AssetClass> assetClasses = EnumSet.of(EQUITY,OPTION,FUTURE,CURRENCY);
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
         * @throws FeedException if the request could not be executed 
         */
        private static String execute(MarketDataRequest inRequest,
                                      BogusFeed inParentFeed)
                throws FeedException
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
            marketDataRequest = inRequest;
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
         * Gets the underlying instrument for the given symbol. 
         *
         * @param inSymbol a <code>String</code> value
         * @return an <code>Instrument</code> value
         */
        private Instrument getUnderlyingInstrument(String inSymbol)
        {
            return new Equity(inSymbol); // this is slightly restrictive in the long run, but certainly acceptable for now
        }
        /**
         * Executes the market data request associated with this object.
         * 
         * @throws IllegalStateException if this method has already been executed for this object
         * @throws FeedException if the request is for Option asset class and contains a symbol that is not OSI-compliant
         */
        private synchronized void execute()
                throws FeedException
        {
            if(executed) {
                throw new IllegalStateException();
            }
            try {
                List<ExchangeRequest> exchangeRequests = new ArrayList<ExchangeRequest>();
                Set<String> symbols = marketDataRequest.getSymbols(); 
                if(!symbols.isEmpty()) {
                    if(marketDataRequest.getAssetClass() == AssetClass.EQUITY) {
                        for(String symbol : symbols) {
                            exchangeRequests.add(ExchangeRequestBuilder.newRequest().withInstrument(new Equity(symbol)).create());
                        }
                    } else if(marketDataRequest.getAssetClass() == AssetClass.FUTURE) {
                        for(String symbol : symbols) {
                            exchangeRequests.add(ExchangeRequestBuilder.newRequest().withInstrument(Future.fromString(symbol)).create());
                        }
                    } else if(marketDataRequest.getAssetClass() == AssetClass.CURRENCY) {
                        for(String symbol : symbols) {
                            exchangeRequests.add(ExchangeRequestBuilder.newRequest().withInstrument(new Currency(symbol)).create());
                        }
                    } else if(marketDataRequest.getAssetClass() == AssetClass.OPTION) {
                        for(String symbol : symbols) {
                            // this assumes that the symbol is an OSI-compliant symbol, otherwise, there's no way to parse it
                            //  deterministically.  if it's not OSI-compliant, an IAE will be thrown, which puts the kaibosh on
                            //  the whole request.  this is a limitation ironed into the Bogus adapter
                            try {
                                Option basicOption = OptionUtils.getOsiOptionFromString(symbol);
                                exchangeRequests.add(ExchangeRequestBuilder.newRequest().withInstrument(basicOption)
                                                                                        .withUnderlyingInstrument(getUnderlyingInstrument(basicOption.getSymbol())).create());
                            } catch (IllegalArgumentException e) {
                                throw new FeedException(e,
                                                        new I18NBoundMessage1P(UNSUPPORTED_OPTION_SPECIFICATION,
                                                                               symbol));
                            }
                        }
                    } else {
                        // this is a new asset class and there is no support for it here
                        throw new UnsupportedOperationException();
                    }
                } else {
                    // marketDataRequest has no symbols - should then have underlyingsymbols instead
                    Set<String> underlyingSymbols = marketDataRequest.getUnderlyingSymbols();
                    assert(!underlyingSymbols.isEmpty());
                    for(String symbol : marketDataRequest.getUnderlyingSymbols()) {
                        exchangeRequests.add(ExchangeRequestBuilder.newRequest().withUnderlyingInstrument(getUnderlyingInstrument(symbol)).create());
                    }
                }
                for(ExchangeRequest exchangeRequest : exchangeRequests) {
                    // all symbols for which we want data are collected in the symbols list
                    // each type of subscription is managed differently
                    for(Content content : marketDataRequest.getContent()) {
                        switch(content) {
                            case LEVEL_2 :
                                // LEVEL_2 is NASDAQ Level II data, which is TOP_OF_BOOK from all
                                //  managed exchanges.
                                doTopOfBook(exchangeRequest,
                                            null);
                                break;
                            case TOP_OF_BOOK :
                                // TOP_OF_BOOK from the specified exchange only
                                doTopOfBook(exchangeRequest,
                                            marketDataRequest.getExchange());
                                break;
                            case OPEN_BOOK :
                                // OPEN_BOOK is depth-of-book from the specified exchange
                                doDepthOfBook(exchangeRequest,
                                              marketDataRequest.getExchange());
                                break;
                            case TOTAL_VIEW :
                                // TOTAL_VIEW is depth-of-book from the specified exchange
                                doDepthOfBook(exchangeRequest,
                                              marketDataRequest.getExchange());
                                break;
                            case LATEST_TICK :
                                // LATEST_TICK is the most recent trade
                                doLatestTick(exchangeRequest,
                                             marketDataRequest.getExchange());
                                break;
                            case MARKET_STAT :
                                doStatistics(exchangeRequest,
                                             marketDataRequest.getExchange());
                                break;
                            case DIVIDEND :
                                doDividends(exchangeRequest,
                                            marketDataRequest.getExchange());
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
         * Executes a statistics request for the given request using the
         * given exchange code.
         *
         * @param inExchangeRequest an <code>ExchangeRequest</code> value
         * @param inExchangeToUse a <code>String</code> value
         */
        private void doStatistics(ExchangeRequest inExchangeRequest,
                                  String inExchangeToUse)
        {
            for(SimulatedExchange exchange : feed.getExchangesForCode(inExchangeToUse)) {
                exchangeTokens.add(exchange.getStatistics(inExchangeRequest,
                                                          subscriber));
            }
        }
        /**
         * Executes a dividend request for the given request using the
         * given exchange code.
         *
         * @param inExchangeRequest an <code>ExchangeRequest</code> value
         * @param inExchangeToUse a <code>String</code> value
         */
        private void doDividends(ExchangeRequest inExchangeRequest,
                                 String inExchangeToUse)
        {
            for(SimulatedExchange exchange : feed.getExchangesForCode(inExchangeToUse)) {
                exchangeTokens.add(exchange.getDividends(inExchangeRequest,
                                                         subscriber));
                // dividends are the same across all feeds, so the first answer is sufficient
                break;
            }
        }
        /**
         * Executes a depth-of-book request for the given request using the
         * given exchange code.
         *
         * @param inRequest an <code>ExchangeRequest</code> value
         * @param inExchangeToUse a <code>String</code> value
         */
        private void doDepthOfBook(ExchangeRequest inRequest,
                                   String inExchangeToUse)
        {
            for(SimulatedExchange exchange : feed.getExchangesForCode(inExchangeToUse)) {
                exchangeTokens.add(exchange.getDepthOfBook(inRequest,
                                                           subscriber));
            }
        }
        /**
         * Executes a top-of-book request for the given request using the
         * given exchange code.
         *
         * @param inRequest an <code>ExchangeRequest</code> value
         * @param inExchangeToUse a <code>String</code> value
         */
        private void doTopOfBook(ExchangeRequest inRequest,
                                 String inExchangeToUse)
        {
            for(SimulatedExchange exchange : feed.getExchangesForCode(inExchangeToUse)) {
                exchangeTokens.add(exchange.getTopOfBook(inRequest,
                                                         subscriber));
            }
        }
        /**
         * Executes a latest-tick request for the given request using the given
         * exchange code.
         *
         * @param inRequest an <code>ExchangeRequest</code> value
         * @param inExchangeToUse a <code>String</code> value
         */
        private void doLatestTick(ExchangeRequest inRequest,
                                  String inExchangeToUse)
        {
            for(SimulatedExchange exchange : feed.getExchangesForCode(inExchangeToUse)) {
                exchangeTokens.add(exchange.getLatestTick(inRequest,
                                                          subscriber));
            }
        }
        /**
         * Cancels all subscriptions associated with this request.
         */
        private void cancel()
        {
            for(SimulatedExchange.Token token : exchangeTokens) {
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
        private final List<SimulatedExchange.Token> exchangeTokens = new ArrayList<SimulatedExchange.Token>();
        /**
         * the market data request associated with this object
         */
        private final MarketDataRequest marketDataRequest;
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
