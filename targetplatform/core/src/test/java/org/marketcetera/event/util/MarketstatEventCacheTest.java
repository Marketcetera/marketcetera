package org.marketcetera.event.util;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.Test;
import org.marketcetera.event.EventTestBase;
import org.marketcetera.event.MarketstatEvent;
import org.marketcetera.event.OptionMarketstatEvent;
import org.marketcetera.event.impl.MarketstatEventBuilder;
import org.marketcetera.marketdata.DateUtils;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.options.ExpirationType;
import org.marketcetera.trade.*;

/* $License$ */

/**
 * Tests {@link MarketstatEventCache}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.0.0
 */
public class MarketstatEventCacheTest
{
    /**
     * Tests the {@link MarketstatEventCache} constructor.
     *
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void constructor()
            throws Exception
    {
        new ExpectedFailure<NullPointerException>()
        {
            @Override
            protected void run()
                    throws Exception
            {
                new MarketstatEventCache(null);
            }
        };
        new ExpectedFailure<UnsupportedOperationException>()
        {
            @Override
            protected void run()
                    throws Exception
            {
                new MarketstatEventCache(EventTestBase.generateUnsupportedInstrument());
            }
        };
       verifyCache(new MarketstatEventCache(equity),
                   null); 
       verifyCache(new MarketstatEventCache(option),
                   null); 
    }
    /**
     * Tests the ability to cache values.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void cachedValues()
            throws Exception
    {
        final MarketstatEventBuilder equityBuilder = MarketstatEventBuilder.marketstat(equity);
        final MarketstatEventBuilder optionBuilder = MarketstatEventBuilder.marketstat(option);
        final MarketstatEventBuilder futureBuilder = MarketstatEventBuilder.marketstat(future);
        final MarketstatEventBuilder currencyBuilder = MarketstatEventBuilder.marketstat(currency);
        final MarketstatEventCache equityCache = new MarketstatEventCache(equity);
        final MarketstatEventCache optionCache = new MarketstatEventCache(option);
        final MarketstatEventCache futureCache = new MarketstatEventCache(future);
        final MarketstatEventCache currencyCache = new MarketstatEventCache(currency);
        new ExpectedFailure<IllegalArgumentException>()
        {
            @Override
            protected void run()
                    throws Exception
            {
                optionCache.cache(equityBuilder.create());
            }
        };
        new ExpectedFailure<IllegalArgumentException>()
        {
            @Override
            protected void run()
                    throws Exception
            {
                equityCache.cache(optionBuilder.create());
            }
        };
        new ExpectedFailure<IllegalArgumentException>()
        {
            @Override
            protected void run()
                    throws Exception
            {
                futureCache.cache(equityBuilder.create());
            }
        };
        new ExpectedFailure<IllegalArgumentException>()
        {
            @Override
            protected void run()
                    throws Exception
            {
                currencyCache.cache(equityBuilder.create());
            }
        };
        // these values are not nullable, so set them up now
        optionBuilder.withExpirationType(ExpirationType.EUROPEAN);
        optionBuilder.withUnderlyingInstrument(equity);
        doCacheTest(equityBuilder,
                    equityCache);
        doCacheTest(optionBuilder,
                    optionCache);
        doCacheTest(futureBuilder,
                    futureCache);
        doCacheTest(currencyBuilder,
                currencyCache);
    }
    /**
     * Executes a set of cache tests with the given builder and cache.
     *
     * @param inBuilder a <code>MarketstatEventBuilder</code> value
     * @param inCache a <code>MarketstatEventCache</code> value
     * @throws Exception if an unexpected error occurs
     */
    private void doCacheTest(final MarketstatEventBuilder inBuilder,
                             final MarketstatEventCache inCache)
            throws Exception
    {
        new ExpectedFailure<NullPointerException>()
        {
            @Override
            protected void run()
                    throws Exception
            {
                inCache.cache(null);
            }
        };
        MarketstatEvent event = inBuilder.create();
        inCache.cache(event);
        verifyCache(inCache,
                    event);
        // test values that are always different
        Thread.sleep(250);
        event = inBuilder.withSource(this).create();
        inCache.cache(event);
        verifyCache(inCache,
                    event);
        event = inBuilder.withSource(null).create();
        inCache.cache(event);
        verifyCache(inCache,
                    event);
        // start changing values that may or may not change
        // close price
        assertNull(event.getClose());
        event = inBuilder.withClosePrice(EventTestBase.generateDecimalValue()).create();
        inCache.cache(event);
        verifyCache(inCache,
                    event);
        BigDecimal amount = event.getClose();
        inCache.cache(inBuilder.withClosePrice(null).create());
        verifyCache(inCache,
                    event);
        inBuilder.withClosePrice(amount);
        long timestamp = System.currentTimeMillis();
        int counter = 0;
        // close date
        assertNull(event.getCloseDate());
        event = inBuilder.withCloseDate(DateUtils.dateToString(new Date(timestamp + (counter++ * 1000)))).create();
        inCache.cache(event);
        verifyCache(inCache,
                    event);
        String date = event.getCloseDate();
        inCache.cache(inBuilder.withCloseDate(null).create());
        verifyCache(inCache,
                    event);
        inBuilder.withCloseDate(date);
        // close exchange
        assertNull(event.getCloseExchange());
        event = inBuilder.withCloseExchange("close exchange").create();
        inCache.cache(event);
        verifyCache(inCache,
                    event);
        String exchange = event.getCloseExchange();
        inCache.cache(inBuilder.withCloseExchange(null).create());
        verifyCache(inCache,
                    event);
        inBuilder.withCloseExchange(exchange);
        // high price
        assertNull(event.getHigh());
        event = inBuilder.withHighPrice(EventTestBase.generateDecimalValue()).create();
        inCache.cache(event);
        verifyCache(inCache,
                    event);
        amount = event.getHigh();
        inCache.cache(inBuilder.withHighPrice(null).create());
        verifyCache(inCache,
                    event);
        inBuilder.withHighPrice(amount);
        // high exchange
        assertNull(event.getHighExchange());
        event = inBuilder.withHighExchange("high exchange").create();
        inCache.cache(event);
        verifyCache(inCache,
                    event);
        exchange = event.getHighExchange();
        inCache.cache(inBuilder.withHighExchange(null).create());
        verifyCache(inCache,
                    event);
        inBuilder.withHighExchange(exchange);
        // low price
        assertNull(event.getLow());
        event = inBuilder.withLowPrice(EventTestBase.generateDecimalValue()).create();
        inCache.cache(event);
        verifyCache(inCache,
                    event);
        amount = event.getLow();
        inCache.cache(inBuilder.withLowPrice(null).create());
        verifyCache(inCache,
                    event);
        inBuilder.withLowPrice(amount);
        // low exchange
        assertNull(event.getLowExchange());
        event = inBuilder.withLowExchange("low exchange").create();
        inCache.cache(event);
        verifyCache(inCache,
                    event);
        exchange = event.getLowExchange();
        inCache.cache(inBuilder.withLowExchange(null).create());
        verifyCache(inCache,
                    event);
        inBuilder.withLowExchange(exchange);
        // open price
        assertNull(event.getOpen());
        event = inBuilder.withOpenPrice(EventTestBase.generateDecimalValue()).create();
        inCache.cache(event);
        verifyCache(inCache,
                    event);
        amount = event.getOpen();
        inCache.cache(inBuilder.withOpenPrice(null).create());
        verifyCache(inCache,
                    event);
        inBuilder.withOpenPrice(amount);
        // open exchange
        assertNull(event.getOpenExchange());
        event = inBuilder.withOpenExchange("open exchange").create();
        inCache.cache(event);
        verifyCache(inCache,
                    event);
        exchange = event.getOpenExchange();
        inCache.cache(inBuilder.withOpenExchange(null).create());
        verifyCache(inCache,
                    event);
        inBuilder.withOpenExchange(exchange);
        // previous close price
        assertNull(event.getPreviousClose());
        event = inBuilder.withPreviousClosePrice(EventTestBase.generateDecimalValue()).create();
        inCache.cache(event);
        verifyCache(inCache,
                    event);
        amount = event.getPreviousClose();
        inCache.cache(inBuilder.withPreviousClosePrice(null).create());
        verifyCache(inCache,
                    event);
        inBuilder.withPreviousClosePrice(amount);
        // previous close date
        assertNull(event.getPreviousCloseDate());
        event = inBuilder.withPreviousCloseDate(DateUtils.dateToString(new Date(timestamp + (counter++ * 1000)))).create();
        inCache.cache(event);
        verifyCache(inCache,
                    event);
        date = event.getPreviousCloseDate();
        inCache.cache(inBuilder.withPreviousCloseDate(null).create());
        verifyCache(inCache,
                    event);
        inBuilder.withPreviousCloseDate(date);
        // trade high time
        assertNull(event.getTradeHighTime());
        event = inBuilder.withTradeHighTime(DateUtils.dateToString(new Date(timestamp + (counter++ * 1000)))).create();
        inCache.cache(event);
        verifyCache(inCache,
                    event);
        date = event.getTradeHighTime();
        inCache.cache(inBuilder.withTradeHighTime(null).create());
        verifyCache(inCache,
                    event);
        inBuilder.withTradeHighTime(date);
        // trade low time
        assertNull(event.getTradeLowTime());
        event = inBuilder.withTradeLowTime(DateUtils.dateToString(new Date(timestamp + (counter++ * 1000)))).create();
        inCache.cache(event);
        verifyCache(inCache,
                    event);
        date = event.getTradeLowTime();
        inCache.cache(inBuilder.withTradeLowTime(null).create());
        verifyCache(inCache,
                    event);
        inBuilder.withTradeLowTime(date);
        // trade volume
        assertNull(event.getVolume());
        event = inBuilder.withVolume(EventTestBase.generateDecimalValue()).create();
        inCache.cache(event);
        verifyCache(inCache,
                    event);
        amount = event.getVolume();
        inCache.cache(inBuilder.withVolume(null).create());
        verifyCache(inCache,
                    event);
        inBuilder.withVolume(amount);
        // trade value
        assertNull(event.getValue());
        event = inBuilder.withValue(EventTestBase.generateDecimalValue()).create();
        inCache.cache(event);
        verifyCache(inCache,
                    event);
        amount = event.getValue();
        inCache.cache(inBuilder.withValue(null).create());
        verifyCache(inCache,
                    event);
        inBuilder.withValue(amount);
        if(event instanceof OptionMarketstatEvent) {
            OptionMarketstatEvent optionEvent = (OptionMarketstatEvent)event;
            // deliverable
            assertFalse(optionEvent.hasDeliverable());
            optionEvent = (OptionMarketstatEvent)inBuilder.hasDeliverable(true).create();
            inCache.cache(optionEvent);
            verifyCache(inCache,
                        optionEvent);
            // expiration type
            optionEvent = (OptionMarketstatEvent)inBuilder.withExpirationType(ExpirationType.AMERICAN).create();
            inCache.cache(optionEvent);
            verifyCache(inCache,
                        optionEvent);
            // multiplier
            assertNull(optionEvent.getMultiplier());
            optionEvent = (OptionMarketstatEvent)inBuilder.withMultiplier(EventTestBase.generateDecimalValue()).create();
            inCache.cache(optionEvent);
            verifyCache(inCache,
                        optionEvent);
            amount = optionEvent.getMultiplier();
            inCache.cache(inBuilder.withMultiplier(null).create());
            verifyCache(inCache,
                        optionEvent);
            inBuilder.withMultiplier(amount);
            // provider symbol
            assertNull(optionEvent.getProviderSymbol());
            optionEvent = (OptionMarketstatEvent)inBuilder.withProviderSymbol("Symbol").create();
            inCache.cache(optionEvent);
            verifyCache(inCache,
                        optionEvent);
            String symbol = optionEvent.getProviderSymbol();
            inCache.cache(inBuilder.withProviderSymbol(null).create());
            verifyCache(inCache,
                        optionEvent);
            inBuilder.withProviderSymbol(symbol);
            // underlying instrument
            optionEvent = (OptionMarketstatEvent)inBuilder.withUnderlyingInstrument(option).create();
            inCache.cache(optionEvent);
            verifyCache(inCache,
                        optionEvent);
            // volume change
            assertNull(optionEvent.getVolumeChange());
            optionEvent = (OptionMarketstatEvent)inBuilder.withVolumeChange(EventTestBase.generateDecimalValue()).create();
            inCache.cache(optionEvent);
            verifyCache(inCache,
                        optionEvent);
            // interest change
            assertNull(optionEvent.getInterestChange());
            optionEvent = (OptionMarketstatEvent)inBuilder.withInterestChange(EventTestBase.generateDecimalValue()).create();
            inCache.cache(optionEvent);
            verifyCache(inCache,
                        optionEvent);
        }
    }
    /**
     * Verifies that the given <code>MarketstatEventCache</code> produces the
     * given expected event.
     *
     * @param inActualCache a <code>MarketstatEventCache</code> value
     * @param inExpectedEvent a <code>MarketstatEvent</code> value
     */
    private static void verifyCache(MarketstatEventCache inActualCache,
                                    MarketstatEvent inExpectedEvent)
    {
        if(inExpectedEvent == null) {
            assertNull(inActualCache.get());
            return;
        }
        MarketstatEvent actualEvent = inActualCache.get();
        assertNotNull(actualEvent);
        // messageId and timestamp are intentionally not tested here because the contract
        //  does not guarantee their contents
        assertEquals(inExpectedEvent.getClose(),
                     actualEvent.getClose());
        assertEquals(inExpectedEvent.getCloseDate(),
                     actualEvent.getCloseDate());
        assertEquals(inExpectedEvent.getCloseExchange(),
                     actualEvent.getCloseExchange());
        assertEquals(inExpectedEvent.getHigh(),
                     actualEvent.getHigh());
        assertEquals(inExpectedEvent.getHighExchange(),
                     actualEvent.getHighExchange());
        assertEquals(inExpectedEvent.getInstrument(),
                     actualEvent.getInstrument());
        assertEquals(inExpectedEvent.getLow(),
                     actualEvent.getLow());
        assertEquals(inExpectedEvent.getLowExchange(),
                     actualEvent.getLowExchange());
        assertEquals(inExpectedEvent.getOpen(),
                     actualEvent.getOpen());
        assertEquals(inExpectedEvent.getOpenExchange(),
                     actualEvent.getOpenExchange());
        assertEquals(inExpectedEvent.getPreviousClose(),
                     actualEvent.getPreviousClose());
        assertEquals(inExpectedEvent.getPreviousCloseDate(),
                     actualEvent.getPreviousCloseDate());
        assertEquals(inExpectedEvent.getSource(),
                     actualEvent.getSource());
        assertEquals(inExpectedEvent.getTradeHighTime(),
                     actualEvent.getTradeHighTime());
        assertEquals(inExpectedEvent.getTradeLowTime(),
                     actualEvent.getTradeLowTime());
        assertEquals(inExpectedEvent.getVolume(),
                     actualEvent.getVolume());
        assertEquals(inExpectedEvent.getValue(),
                     actualEvent.getValue());
        if(inExpectedEvent instanceof OptionMarketstatEvent) {
            assertTrue(actualEvent instanceof OptionMarketstatEvent);
            OptionMarketstatEvent expectedOptionEvent = (OptionMarketstatEvent)inExpectedEvent;
            OptionMarketstatEvent actualOptionEvent = (OptionMarketstatEvent)actualEvent;
            assertEquals(expectedOptionEvent.getInterestChange(),
                         actualOptionEvent.getInterestChange());
            assertEquals(expectedOptionEvent.getVolumeChange(),
                         actualOptionEvent.getVolumeChange());
            assertEquals(expectedOptionEvent.hasDeliverable(),
                         actualOptionEvent.hasDeliverable());
            assertEquals(expectedOptionEvent.getMultiplier(),
                         actualOptionEvent.getMultiplier());
            assertEquals(expectedOptionEvent.getProviderSymbol(),
                         actualOptionEvent.getProviderSymbol());
            assertEquals(expectedOptionEvent.getUnderlyingInstrument(),
                         actualOptionEvent.getUnderlyingInstrument());
            assertEquals(expectedOptionEvent.getExpirationType(),
                         actualOptionEvent.getExpirationType());
        }
    }
    /**
     * test equity
     */
    private final Equity equity = new Equity("METC");
    /**
     * test option
     */
    private final Option option = new Option(equity.getSymbol(),
                                             DateUtils.dateToString(new Date()),
                                             EventTestBase.generateDecimalValue(),
                                             OptionType.Call);
    /**
     * test future
     */
    private final Future future = new Future("IB",
                                             FutureExpirationMonth.FEBRUARY,
                                             2012);
    /**
     * test currency
     */
    private final Currency currency = new Currency("USD/GBP");
}
