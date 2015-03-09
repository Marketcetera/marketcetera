package org.marketcetera.marketdata.core.webservice.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Deque;
import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.marketcetera.event.AskEvent;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.DividendEvent;
import org.marketcetera.event.Event;
import org.marketcetera.event.MarketstatEvent;
import org.marketcetera.event.QuoteEvent;
import org.marketcetera.event.TradeEvent;
import org.marketcetera.marketdata.AssetClass;
import org.marketcetera.marketdata.Content;
import org.marketcetera.marketdata.MarketDataFeedTestBase;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.MarketDataRequestBuilder;
import org.marketcetera.marketdata.bogus.BogusFeedModuleFactory;
import org.marketcetera.marketdata.core.webservice.MarketDataServiceClient;
import org.marketcetera.marketdata.core.webservice.MarketDataServiceClientFactory;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.trade.ConvertibleBond;
import org.marketcetera.trade.Currency;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Future;
import org.marketcetera.trade.FutureExpirationMonth;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.OptionType;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.collect.Sets;

/* $License$ */

/**
 * Tests {@link MarketDataServiceImpl}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.4.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"file:src/test/sample_data/conf/web.xml"})
public class MarketDataServiceImplTest
        implements ApplicationContextAware
{
    /**
     * Runs before each test.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Before
    public void setup()
            throws Exception
    {
        MockServer server = context.getBean(MockServer.class);
        MarketDataServiceClientFactory clientFactory = context.getBean(MarketDataServiceClientFactory.class);
        marketDataClient = clientFactory.create("username",
                                                "password",
                                                server.getHostname(),
                                                server.getPort());
        marketDataClient.start();
        moduleManager = new ModuleManager();
        moduleManager.init();
        moduleManager.start(BogusFeedModuleFactory.INSTANCE_URN);
    }
    /**
     * Runs after each test.
     *
     * @throws Exception if an unexpected error occurs
     */
    @After
    public void cleanup()
            throws Exception
    {
        if(marketDataClient != null) {
            marketDataClient.stop();
            marketDataClient = null;
        }
        if(moduleManager != null) {
            moduleManager.stop();
            moduleManager = null;
        }
    }
    /* (non-Javadoc)
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    @Override
    public void setApplicationContext(ApplicationContext inContext)
            throws BeansException
    {
        context = inContext;
    }
    /**
     * Tests the transmission of all event types via {@link MarketDataService#getEvents(org.marketcetera.util.ws.stateful.ClientContext, long).
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testSubscribeAllEventTypes()
            throws Exception
    {
        Set<Instrument> instruments = Sets.newLinkedHashSet(Arrays.asList(new Instrument[] { equity,option,future,currency,bond } ));
        Set<Content> equityContent = EnumSet.of(Content.TOP_OF_BOOK,Content.MARKET_STAT,Content.LATEST_TICK,Content.DIVIDEND);
        Set<Content> nonequityContent = EnumSet.of(Content.LATEST_TICK,Content.MARKET_STAT,Content.TOP_OF_BOOK);
        for(Instrument instrument : instruments) {
            final AtomicBoolean trade = new AtomicBoolean(false);
            final AtomicBoolean bid = new AtomicBoolean(false);
            final AtomicBoolean ask = new AtomicBoolean(false);
            final AtomicBoolean dividend = new AtomicBoolean(!(instrument instanceof Equity));
            final AtomicBoolean stat = new AtomicBoolean(false);
            MarketDataRequestBuilder requestBuilder = MarketDataRequestBuilder.newRequest().withAssetClass(AssetClass.getFor(instrument.getSecurityType()));
            if(instrument instanceof Equity) {
                requestBuilder.withContent(equityContent);
            } else {
                requestBuilder.withContent(nonequityContent);
            }
            requestBuilder.withSymbols(instrument.getFullSymbol());
            MarketDataRequest request = requestBuilder.create();
            SLF4JLoggerProxy.debug(this,
                                   "Testing with request {}",
                                   request);
            final long id = marketDataClient.request(request,
                                                     true);
            MarketDataFeedTestBase.wait(new Callable<Boolean>() {
                @Override
                public Boolean call()
                        throws Exception
                {
                    Deque<Event> events = marketDataClient.getEvents(id);
                    if(events != null) {
                        for(Event event : events) {
                            if(event instanceof TradeEvent) {
                                validateEvent((TradeEvent)event);
                                trade.set(true);
                            } else if(event instanceof BidEvent) {
                                validateEvent((QuoteEvent)event);
                                bid.set(true);
                            } else if(event instanceof AskEvent) {
                                validateEvent((QuoteEvent)event);
                                ask.set(true);
                            } else if(event instanceof DividendEvent) {
                                dividend.set(true);
                            } else if(event instanceof MarketstatEvent) {
                                validateEvent((MarketstatEvent)event);
                                stat.set(true);
                            } else {
                                throw new UnsupportedOperationException("Unexpected event type: " + event.getClass().getName());
                            }
                        }
                    }
                    SLF4JLoggerProxy.debug(this,
                                           "trade: {} bid: {} ask: {} stat: {} dividend: {}",
                                           trade,
                                           bid,
                                           ask,
                                           stat,
                                           dividend);
                    return trade.get() && bid.get() && ask.get() && stat.get() && dividend.get();
                }
            });
        }
    }
    /**
     * Validates the the given event contains expected, non-null values.
     *
     * @param inEvent a <code>MarketstatEvent</code> value
     * @throws Exception if an unexpected error occurs
     */
    private void validateEvent(MarketstatEvent inEvent)
            throws Exception
    {
        SLF4JLoggerProxy.debug(this,
                               "Validating {}",
                               inEvent);
        assertNotNull(inEvent.getClose());
        assertNotNull(inEvent.getCloseDate());
        assertNotNull(inEvent.getCloseExchange());
        assertNotNull(inEvent.getEventType());
        assertNotNull(inEvent.getHigh());
        assertNotNull(inEvent.getHighExchange());
        assertNotNull(inEvent.getInstrument());
        assertNotNull(inEvent.getInstrumentAsString());
        assertNotNull(inEvent.getLow());
        assertNotNull(inEvent.getLowExchange());
        assertTrue(inEvent.getMessageId() >= 0);
        assertNotNull(inEvent.getOpen());
        assertNotNull(inEvent.getOpenExchange());
        assertNotNull(inEvent.getPreviousClose());
        assertNotNull(inEvent.getPreviousCloseDate());
        assertNotNull(inEvent.getProvider());
        assertTrue(inEvent.getTimeMillis() >= 0);
        assertNotNull(inEvent.getTimestamp());
        assertNotNull(inEvent.getTradeHighTime());
        assertNotNull(inEvent.getTradeLowTime());
        assertNotNull(inEvent.getValue());
        assertNotNull(inEvent.getVolume());
    }
    /**
     * Validates the the given event contains expected, non-null values.
     *
     * @param inEvent a <code>TradeEvent</code> value
     * @throws Exception if an unexpected error occurs
     */
    private void validateEvent(TradeEvent inEvent)
            throws Exception
    {
        SLF4JLoggerProxy.debug(this,
                               "Validating {}",
                               inEvent);
        assertNotNull(inEvent.getEventType());
        assertNotNull(inEvent.getExchange());
        assertNotNull(inEvent.getExchangeTimestamp());
        assertNotNull(inEvent.getInstrument());
        assertNotNull(inEvent.getInstrumentAsString());
        assertTrue(inEvent.getMessageId() >= 0);
        assertNotNull(inEvent.getPrice());
        assertTrue(!inEvent.getPrice().equals(BigDecimal.ZERO));
        assertNotNull(inEvent.getProvider());
        assertNotNull(inEvent.getSize());
        assertTrue(!inEvent.getSize().equals(BigDecimal.ZERO));
        assertTrue(inEvent.getTimeMillis() >= 0);
        assertNotNull(inEvent.getTimestamp());
        assertNotNull(inEvent.getTradeDate());
    }
    /**
     * Validates the the given event contains expected, non-null values.
     *
     * @param inEvent a <code>QuoteEvent</code> value
     * @throws Exception if an unexpected error occurs
     */
    private void validateEvent(QuoteEvent inEvent)
            throws Exception
    {
        SLF4JLoggerProxy.debug(this,
                               "Validating {}",
                               inEvent);
        assertNotNull(inEvent.getAction());
        assertNotNull(inEvent.getEventType());
        assertNotNull(inEvent.getExchange());
        assertNotNull(inEvent.getExchangeTimestamp());
        assertNotNull(inEvent.getInstrument());
        assertNotNull(inEvent.toString(),
                      inEvent.getInstrumentAsString());
        assertTrue(inEvent.getMessageId() >= 0);
        assertNotNull(inEvent.getPrice());
        assertTrue(!inEvent.getPrice().equals(BigDecimal.ZERO));
        assertNotNull(inEvent.getProvider());
        assertNotNull(inEvent.getQuoteDate());
        assertNotNull(inEvent.getSize());
        assertTrue(!inEvent.getSize().equals(BigDecimal.ZERO));
        assertTrue(inEvent.getTimeMillis() >= 0);
        assertNotNull(inEvent.getTimestamp());
    }
    /**
     * test module manager used to activate market data providers
     */
    private ModuleManager moduleManager;
    /**
     * client used to connect to test market data nexus service
     */
    private MarketDataServiceClient marketDataClient;
    /**
     * test application context
     */
    private ApplicationContext context;
    /**
     * test option
     */
    private Option option = new Option("METC",
                                       "20150115",
                                       BigDecimal.TEN,
                                       OptionType.Put);
    /**
     * test future value
     */
    private Future future = new Future("METC",
                                       FutureExpirationMonth.DECEMBER,
                                       2014);
    /**
     * test currency value
     */
    private Currency currency = new Currency("USD/BTC");
    /**
     * test equity value
     */
    private Equity equity = new Equity("METC");
    /**
     * test convertible bond value
     */
    private ConvertibleBond bond = new ConvertibleBond("FR0011453463");
}
