package org.marketcetera.marketdata.bogus;

import java.util.*;

import org.marketcetera.api.systemmodel.Subscriber;
import org.marketcetera.core.event.Event;
import org.marketcetera.core.event.HasInstrument;
import org.marketcetera.core.marketdata.ExchangeRequest;
import org.marketcetera.core.marketdata.ExchangeRequestBuilder;
import org.marketcetera.core.marketdata.SimulatedExchange;
import org.marketcetera.core.marketdata.SimulatedExchange.Token;
import org.marketcetera.core.symbolresolver.SymbolResolver;
import org.marketcetera.core.trade.Instrument;
import org.marketcetera.core.util.log.SLF4JLoggerProxy;
import org.marketcetera.marketdata.Capability;
import org.marketcetera.marketdata.FeedType;
import org.marketcetera.marketdata.manager.MarketDataRequestFailed;
import org.marketcetera.marketdata.provider.AbstractMarketDataProvider;
import org.marketcetera.marketdata.request.MarketDataRequest;
import org.marketcetera.marketdata.request.MarketDataRequestAtom;

/* $License$ */

/**
 * Provides simulated market data.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class BogusFeed
        extends AbstractMarketDataProvider
{
    /**
     * Sets the symbolResolver value.
     *
     * @param inSymbolResolver a <code>SymbolResolver</code> value
     */
    public void setSymbolResolver(SymbolResolver inSymbolResolver)
    {
        symbolResolver = inSymbolResolver;
    }
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
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return getProviderName();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.provider.AbstractMarketDataProvider#addSymbolMapping(java.lang.String, org.marketcetera.core.trade.Instrument)
     */
    @Override
    protected void addSymbolMapping(String inSymbol,
                                    Instrument inInstrument)
    {
        if(!resolvedSymbols.contains(inSymbol)) {
            resolvedSymbols.add(inSymbol);
            super.addSymbolMapping(inSymbol,
                                   inInstrument);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.provider.AbstractMarketDataProvider#doStart()
     */
    @Override
    protected void doStart()
    {
        // TODO add more than one exchange to produce a LEVEL_2 
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
     * @see org.marketcetera.marketdata.provider.AbstractMarketDataProvider#doCancel(org.marketcetera.marketdata.request.MarketDataRequestAtom)
     */
    @Override
    protected void doCancel(MarketDataRequestAtom inAtom)
    {
        Token token = tokensBySymbol.remove(inAtom);
        if(token != null) {
            Collection<SimulatedExchange> exchanges = getExchangesForCode(inAtom.getExchange());
            for(SimulatedExchange exchange : exchanges) {
                exchange.cancel(token);
            }
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.provider.AbstractMarketDataProvider#doMarketDataRequest(org.marketcetera.marketdata.MarketDataRequest, org.marketcetera.marketdata.provider.MarketDataRequestAtom)
     */
    @Override
    protected void doMarketDataRequest(MarketDataRequest inCompleteRequest,
                                       MarketDataRequestAtom inRequestAtom)
    {
        Collection<SimulatedExchange> exchanges = getExchangesForCode(inRequestAtom.getExchange());
        for(SimulatedExchange exchange : exchanges) {
            execute(exchange,
                    inRequestAtom);
        }
    }
    /**
     * 
     *
     *
     * @param inExchange
     * @param inAtom
     */
    private void execute(SimulatedExchange inExchange,
                         final MarketDataRequestAtom inAtom)
    {
        Instrument resolvedInstrument = symbolResolver.resolve(inAtom.getSymbol());
        if(resolvedInstrument == null) {
            throw new MarketDataRequestFailed(); // TODO message
        } else {
            SLF4JLoggerProxy.debug(this,
                                   "{} resolved to {}",
                                   inAtom.getSymbol(),
                                   resolvedInstrument);
        }
        addSymbolMapping(inAtom.getSymbol(),
                         resolvedInstrument);
        Subscriber eventSubscriber = new Subscriber() {
            @Override
            public void publishTo(Object inData)
            {
                if(inData instanceof HasInstrument) {
                    Instrument eventInstrument = ((HasInstrument)inData).getInstrument();
                    addSymbolMapping(inAtom.getSymbol(),
                                     eventInstrument);
                    publishEvents(inAtom.getContent(),
                                  eventInstrument,
                                  (Event)inData);
                }
            }
        };
        ExchangeRequest request;
        if(inAtom.isUnderlyingSymbol()) {
            request = ExchangeRequestBuilder.newRequest().withUnderlyingInstrument(resolvedInstrument).create(); 
        } else {
            request = ExchangeRequestBuilder.newRequest().withInstrument(resolvedInstrument).create(); 
        }
        Token subscriptionToken = null;
        switch(inAtom.getContent()) {
            case DIVIDEND:
                subscriptionToken = inExchange.getDividends(request,
                                                            eventSubscriber);
                break;
            case LATEST_TICK:
                subscriptionToken = inExchange.getLatestTick(request,
                                                             eventSubscriber);
                break;
            case MARKET_STAT:
                subscriptionToken = inExchange.getStatistics(request,
                                                             eventSubscriber);
                break;
            case TOP_OF_BOOK:
                subscriptionToken = inExchange.getTopOfBook(request,
                                                            eventSubscriber);
                break;
            case OPEN_BOOK:
            case TOTAL_VIEW:
            case UNAGGREGATED_DEPTH:
                subscriptionToken = inExchange.getDepthOfBook(request,
                                                              eventSubscriber);
                break;
            case LEVEL_2:
            case AGGREGATED_DEPTH:
            default:
                throw new MarketDataRequestFailed();
        }
        tokensBySymbol.put(inAtom,
                           subscriptionToken);
    }
    /**
     * Gets the list of exchanges associated with the given exchange code.
     *
     * @param inExchange a <code>String</code> value containing an exchange code or <code>null</code> to return all exchanges
     * @return a <code>Collection&lt;SimulatedExchange&gt;</code> value
     */
    private Collection<SimulatedExchange> getExchangesForCode(String inExchange)
    {
        if(inExchange == null ||
           inExchange.isEmpty()) {
            // request data from all exchanges
            return new ArrayList<SimulatedExchange>(exchanges.values());
        }
        if(exchanges.containsKey(inExchange)) {
            return Arrays.asList(new SimulatedExchange[] { exchanges.get(inExchange) });
        }
        return Collections.emptyList();
    }
    /**
     * exchanges that make up the group for which the bogus feed can report data
     */
    private final Map<String,SimulatedExchange> exchanges = new HashMap<String,SimulatedExchange>();
    /**
     * 
     */
    private final Map<MarketDataRequestAtom,Token> tokensBySymbol = new HashMap<MarketDataRequestAtom,Token>();
    /**
     * 
     */
    private volatile SymbolResolver symbolResolver;
    /**
     * 
     */
    private static final Set<String> resolvedSymbols = new HashSet<String>();
    /**
     * 
     */
    private static final Set<Capability> capabilities = EnumSet.of(Capability.TOP_OF_BOOK,Capability.OPEN_BOOK,Capability.TOTAL_VIEW,Capability.LATEST_TICK,Capability.MARKET_STAT,Capability.DIVIDEND,Capability.UNAGGREGATED_DEPTH);
    /**
     * 
     */
    private static final String provider = "bogus";
}
