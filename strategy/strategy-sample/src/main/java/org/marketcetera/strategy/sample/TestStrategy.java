package org.marketcetera.strategy.sample;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.marketcetera.core.notifications.INotification.Severity;
import org.marketcetera.event.Event;
import org.marketcetera.event.QuoteEvent;
import org.marketcetera.event.TopOfBookEvent;
import org.marketcetera.event.TradeEvent;
import org.marketcetera.marketdata.Content;
import org.marketcetera.marketdata.MarketDataCacheElement;
import org.marketcetera.marketdata.MarketDataClient;
import org.marketcetera.marketdata.MarketDataListener;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.MarketDataRequestBuilder;
import org.marketcetera.strategy.StrategyClient;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.trade.OrderSingleSuggestion;
import org.marketcetera.trade.OrderType;
import org.marketcetera.trade.Side;
import org.marketcetera.trade.Suggestion;
import org.marketcetera.trade.SuggestionFactory;
import org.marketcetera.trade.client.TradeClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.stereotype.Component;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/* $License$ */

/**
 * Test strategy that demonstrates how a strategy can be built and use services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Component
@ConfigurationProperties
@EnableAutoConfiguration
@PropertySources({@PropertySource("classpath:application.properties")})
public class TestStrategy
{
    /**
     * Validate and start the object.
     */
    @PostConstruct
    public void start()
    {
        strategyClient.emitMessage(Severity.INFO,
                                   "Starting test strategy");
        MarketDataRequestBuilder marketDataRequestBuilder = MarketDataRequestBuilder.newRequest();
        marketDataRequestBuilder.withContent(Content.TOP_OF_BOOK,Content.LATEST_TICK).withSymbols("AAPL,METC");
        MarketDataRequest request = marketDataRequestBuilder.create();
        marketDataRequestId = marketDataClient.request(request,
                                                       new MarketDataListener() {
            /* (non-Javadoc)
             * @see org.marketcetera.marketdata.MarketDataListener#receiveMarketData(org.marketcetera.event.Event)
             */
            @Override
            public void receiveMarketData(Event inEvent)
            {
                strategyClient.emitMessage(Severity.INFO,
                                           String.valueOf(inEvent));
                if(inEvent instanceof QuoteEvent) {
                    QuoteEvent quoteEvent = (QuoteEvent)inEvent;
                    MarketDataCacheElement topOfBookCache = marketDataCache.getUnchecked(quoteEvent.getInstrument());
                    topOfBookCache.update(Content.TOP_OF_BOOK,
                                          inEvent);
                } else if(inEvent instanceof TradeEvent) {
                    TradeEvent tradeEvent = (TradeEvent)inEvent;
                    MarketDataCacheElement topOfBookCache = marketDataCache.getUnchecked(tradeEvent.getInstrument());
                    topOfBookCache.update(Content.LATEST_TICK,
                                          inEvent);
                    issueSuggestion(topOfBookCache);
                } else {
                    strategyClient.emitMessage(Severity.WARN,
                                               "Ignored unexpected event: " + inEvent);
                }
            }
        });
    }
    /**
     * Stop the object.
     */
    @PreDestroy
    public void stop()
    {
        strategyClient.emitMessage(Severity.INFO,
                                   "Stopping test strategy");
        if(marketDataRequestId != null) {
            try {
                marketDataClient.cancel(marketDataRequestId);
                marketDataRequestId = null;
            } catch (Exception ignored) {}
        }
    }
    /**
     * Create an order suggestion using the cached market data.
     *
     * @param inCacheElement a <code>MarketDataCacheElement</code> value
     */
    private void issueSuggestion(MarketDataCacheElement inCacheElement)
    {
        if(!createSuggestions) {
            return;
        }
        TopOfBookEvent topOfBook = (TopOfBookEvent)inCacheElement.getSnapshot(Content.TOP_OF_BOOK);
        if(topOfBook == null) {
            return;
        }
        QuoteEvent quote;
        Side side;
        if(random.nextBoolean()) {
            // trade on the bid
            quote = topOfBook.getBid();
            side = Side.Sell;
        } else {
            // trade on the ask
            quote = topOfBook.getAsk();
            side = Side.Buy;
        }
        if(quote == null) {
            return;
        }
        issueSuggestion(quote.getInstrument(),
                        side,
                        OrderType.Limit,
                        quote.getPrice(),
                        new BigDecimal(10*(random.nextInt(10)+1)));
    }
    /**
     * Create an order suggestion using the cached market data.
     *
     * @param inInstrument an <code>Instrument</code> value
     * @param inSide a <code>Side</code> value
     * @param inOrderType an <code>OrderType</code> value
     * @param inPrice a <code>BigDecimal</code> value or <code>null</code>
     * @param inQuantity a <code>BigDecimal</code> value
     */
    private void issueSuggestion(Instrument inInstrument,
                                 Side inSide,
                                 OrderType inOrderType,
                                 BigDecimal inPrice,
                                 BigDecimal inQuantity)
    {
        if(!createSuggestions) {
            return;
        }
        OrderSingleSuggestion orderSingleSuggestion = suggestionFactory.createOrderSingleSuggestion();
        orderSingleSuggestion.setIdentifier("Test Strategy");
        orderSingleSuggestion.setScore(new BigDecimal(random.nextDouble()));
        OrderSingle orderSingle = Factory.getInstance().createOrderSingle();
        orderSingle.setInstrument(inInstrument);
        orderSingle.setOrderType(inOrderType);
        orderSingle.setQuantity(inQuantity);
        if(inPrice != null) {
            orderSingle.setPrice(inPrice);
        }
        orderSingle.setSide(inSide);
        orderSingleSuggestion.setOrder(orderSingle);
        tradeClient.sendOrderSuggestion(orderSingleSuggestion);
    }
    /**
     * caches market data
     */
    private final LoadingCache<Instrument,MarketDataCacheElement> marketDataCache = CacheBuilder.newBuilder().build(new CacheLoader<Instrument,MarketDataCacheElement>() {
        @Override
        public MarketDataCacheElement load(Instrument inKey)
                throws Exception
        {
            return new MarketDataCacheElement(inKey);
        }}
    );
    /**
     * generates random numbers
     */
    private Random random = new SecureRandom();
    /**
     * holds the market data request id
     */
    private String marketDataRequestId;
    /**
     * strategy should create suggestions or not
     */
    @Value("${metc.strategy.create.suggestions}")
    private boolean createSuggestions;
    /**
     * provides access to strategy services
     */
    @Autowired
    private StrategyClient strategyClient;
    /**
     * provides access to market data services
     */
    @Autowired
    private MarketDataClient marketDataClient;
    /**
     * provides access to trade services
     */
    @Autowired
    private TradeClient tradeClient;
    /**
     * creates new {@link Suggestion} objects
     */
    @Autowired
    private SuggestionFactory suggestionFactory;
}
