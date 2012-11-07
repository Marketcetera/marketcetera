package org.marketcetera.marketdata.bogus;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.marketcetera.api.systemmodel.Subscriber;
import org.marketcetera.core.marketdata.ExchangeRequest;
import org.marketcetera.core.marketdata.ExchangeRequestBuilder;
import org.marketcetera.core.marketdata.SimulatedExchange;
import org.marketcetera.core.trade.SecurityType;
import org.marketcetera.core.util.log.SLF4JLoggerProxy;
import org.marketcetera.marketdata.Capability;
import org.marketcetera.marketdata.Content;
import org.marketcetera.marketdata.FeedType;
import org.marketcetera.marketdata.events.Event;
import org.marketcetera.marketdata.provider.AbstractMarketDataProvider;
import org.marketcetera.marketdata.request.MarketDataRequest;
import org.marketcetera.marketdata.request.MarketDataRequestAtom;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class BogusFeed
        extends AbstractMarketDataProvider
{
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataProvider#getFeedType()
     */
    @Override
    public FeedType getFeedType()
    {
        return FeedType.SIMULATED;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataProvider#getProviderName()
     */
    @Override
    public String getProviderName()
    {
        return provider;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataProvider#getCapabilities()
     */
    @Override
    public Set<Capability> getCapabilities()
    {
        return capabilities;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataProvider#getHandledTypes()
     */
    @Override
    public Set<SecurityType> getHandledTypes()
    {
        return handledTypes;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.provider.AbstractMarketDataProvider#doStart()
     */
    @Override
    protected void doStart()
    {
        SimulatedExchange exchange = new SimulatedExchange(getProviderName(),
                                                           "BGS");
        SLF4JLoggerProxy.debug(BogusFeed.class,
                               "BogusFeed starting exchange {}...", //$NON-NLS-1$
                               exchange.getCode());
        exchange.start();
        exchanges.put(exchange.getCode(),
                      exchange);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.provider.AbstractMarketDataProvider#doStop()
     */
    @Override
    protected void doStop()
    {
        for(SimulatedExchange exchange : exchanges.values()) {
            SLF4JLoggerProxy.debug(BogusFeed.class,
                                   "BogusFeed stopping exchange {}...", //$NON-NLS-1$
                                   exchange.getCode());
            exchange.stop();
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.provider.AbstractMarketDataProvider#doCancel(java.lang.String)
     */
    @Override
    protected void doCancel(String inInternalHandle)
    {
        Request.cancel(inInternalHandle);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.provider.AbstractMarketDataProvider#doMarketDataRequest(org.marketcetera.marketdata.MarketDataRequest, org.marketcetera.marketdata.provider.MarketDataRequestAtom, java.util.concurrent.atomic.AtomicBoolean)
     */
    @Override
    protected String doMarketDataRequest(MarketDataRequest inCompleteRequest,
                                         MarketDataRequestAtom inRequestAtom,
                                         AtomicBoolean inUpdateSemaphore)
            throws InterruptedException
    {
        String handle = Request.execute(inCompleteRequest,
                                        inRequestAtom,
                                        this);
        return handle;
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
     * Corresponds to a single market data request submitted to {@link BogusFeed}.
     *
     * @version $Id$
     * @since 1.5.0
     */
    private static class Request
    {
        /**
         * Executes the given <code>MarketDataRequest</code> and returns
         * a handle corresponding to the request.
         *
         * @param inRequest a <code>MarketDataRequest</code> value
         * @param inRequestAtom a <code>MarketDataRequestAtom</code> value
         * @param inParentFeed a <code>BogusFeed</code> value
         * @return a <code>String</code> value
         */
        private static String execute(MarketDataRequest inRequest,
                                      MarketDataRequestAtom inRequestAtom,
                                      BogusFeed inParentFeed)
        {
            Request request = new Request(inRequest,
                                          inRequestAtom,
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
         * @param inRequestAtom a <code>MarketDataRequestAtom</code> value
         * @param inFeed a <code>BogusFeed</code> value
         */
        private Request(MarketDataRequest inRequest,
                        MarketDataRequestAtom inRequestAtom,
                        BogusFeed inFeed)
        {
            marketDataRequest = inRequest;
            marketDataRequestAtom = inRequestAtom;
            feed = inFeed;
            subscriber = new Subscriber() {
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
                                      (Event)inData);
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
                List<ExchangeRequest> exchangeRequests = new ArrayList<ExchangeRequest>();
                if(marketDataRequestAtom.getInstrument() != null) {
                    exchangeRequests.add(ExchangeRequestBuilder.newRequest().withInstrument(marketDataRequestAtom.getInstrument()).create());
                } else {
                    // marketDataRequestAtom has no instrument - should then have underlyingInstrument instead
                    exchangeRequests.add(ExchangeRequestBuilder.newRequest().withUnderlyingInstrument(marketDataRequestAtom.getUnderlyingInstrument()).create());
                }
                for(ExchangeRequest exchangeRequest : exchangeRequests) {
                    // all symbols for which we want data are collected in the symbols list
                    // each type of subscription is managed differently
                    for(Content content : marketDataRequest.getContent()) {
                        switch(content) {
                            case TOP_OF_BOOK :
                                // TOP_OF_BOOK from the specified exchange only
                                doTopOfBook(exchangeRequest,
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
                            case UNAGGREGATED_DEPTH :
                                doDepthOfBook(exchangeRequest,
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
         * the actual market data being requested
         */
        private final MarketDataRequestAtom marketDataRequestAtom;
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
        private final Subscriber subscriber;
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
    /**
     * exchanges that make up the group for which the bogus feed can report data
     */
    private final Map<String,SimulatedExchange> exchanges = new HashMap<String,SimulatedExchange>();
    private static final Set<SecurityType> handledTypes = EnumSet.of(SecurityType.CommonStock,SecurityType.Future,SecurityType.Option);
    private static final Set<Capability> capabilities = EnumSet.of(Capability.TOP_OF_BOOK,Capability.OPEN_BOOK,Capability.TOTAL_VIEW,Capability.LATEST_TICK,Capability.MARKET_STAT,Capability.DIVIDEND,Capability.UNAGGREGATED_DEPTH);
    private static final String provider = "bogus";
}
