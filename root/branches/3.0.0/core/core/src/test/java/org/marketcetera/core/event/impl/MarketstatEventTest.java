package org.marketcetera.core.event.impl;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Before;
import org.junit.Test;
import org.marketcetera.api.systemmodel.instruments.*;
import org.marketcetera.core.ExpectedFailure;
import org.marketcetera.core.event.*;
import org.marketcetera.core.marketdata.DateUtils;
import org.marketcetera.core.options.ExpirationType;
import org.marketcetera.core.trade.EquityImpl;
import org.marketcetera.core.trade.FutureImpl;
import org.marketcetera.core.trade.OptionImpl;
import org.marketcetera.util.test.EqualityAssert;

/* $License$ */

/**
 * Tests {@link org.marketcetera.core.event.impl.MarketstatEventBuilder}, {@link org.marketcetera.core.event.impl.EquityMarketstatEventImpl}, and {@link org.marketcetera.core.event.impl.OptionMarketstatEventImpl}.
 *
 * @version $Id: MarketstatEventTest.java 82331 2012-04-10 16:29:48Z colin $
 * @since 2.0.0
 */
public class MarketstatEventTest
        implements Messages
{
    /**
     * Run before each test.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Before
    public void setup()
            throws Exception
    {
        instrument = equity;
        useInstrument = false;
    }
    /**
     * Tests the ability to create various types of {@link org.marketcetera.core.event.impl.MarketstatEventBuilder} objects.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void builderTypes()
            throws Exception
    {
       instrument = option;
       useInstrument = false;
       verify(setDefaults(getBuilder()));
       useInstrument = true;
       verify(setDefaults(getBuilder()));
       instrument = equity;
       useInstrument = false;
       verify(setDefaults(getBuilder()));
       useInstrument = true;
       verify(setDefaults(getBuilder()));
       instrument = future;
       useInstrument = false;
       verify(setDefaults(getBuilder()));
       useInstrument = true;
       verify(setDefaults(getBuilder()));
       // create a new kind of instrument
       new ExpectedFailure<UnsupportedOperationException>() {
           @Override
           protected void run()
                   throws Exception
           {
               MarketstatEventBuilder.marketstat(EventTestBase.generateUnsupportedInstrument());
           }
       };
    }
    /**
     * Tests that the builder checks the instrument type when creating a builder.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void wrongInstrumentType()
            throws Exception
    {
        final MarketstatEventBuilder equityBuilder = setDefaults(MarketstatEventBuilder.equityMarketstat());
        equityBuilder.withInstrument(option);
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_EQUITY_REQUIRED.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                equityBuilder.create();
            }
        };
        equityBuilder.withInstrument(equity);
        assertNotNull(equityBuilder.create());
        final MarketstatEventBuilder optionBuilder = setDefaults(MarketstatEventBuilder.optionMarketstat());
        optionBuilder.withInstrument(equity);
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_OPTION_REQUIRED.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                optionBuilder.create();
            }
        };
        optionBuilder.withInstrument(option);
        assertNotNull(optionBuilder.create());
        final MarketstatEventBuilder futureBuilder = setDefaults(MarketstatEventBuilder.futureMarketstat());
        futureBuilder.withInstrument(equity);
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_FUTURE_REQUIRED.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                futureBuilder.create();
            }
        };
        futureBuilder.withInstrument(future);
        assertNotNull(futureBuilder.create());
    }
    /**
     * Tests {@link MarketstatEventBuilder#withMessageId(long)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withMessageId()
            throws Exception
    {
        MarketstatEventBuilder builder = getBuilder();
        setDefaults(builder);
        builder.withMessageId(Long.MIN_VALUE);
        assertEquals(Long.MIN_VALUE,
                     builder.getMarketstat().getMessageId());
        builder.withMessageId(-1);
        assertEquals(-1,
                     builder.getMarketstat().getMessageId());
        builder.withMessageId(Long.MAX_VALUE);
        assertEquals(Long.MAX_VALUE,
                     builder.getMarketstat().getMessageId());
        verify(builder);
    }
    /**
     * Tests {@link MarketstatEventBuilder#withTimestamp(Date)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withTimestamp()
            throws Exception
    {
        MarketstatEventBuilder builder = getBuilder();
        setDefaults(builder);
        // null timestamp
        builder.withTimestamp(null);
        assertEquals(null,
                     builder.getMarketstat().getTimestamp());
        // regular timestamp
        Date timestamp = new Date();
        builder.withTimestamp(timestamp);
        assertEquals(timestamp,
                     builder.getMarketstat().getTimestamp());
        // make a weird timestamp
        timestamp = new Date(-1);
        builder.withTimestamp(timestamp);
        assertEquals(timestamp,
                     builder.create().getTimestamp());
        verify(builder);
    }
    /**
     * Tests {@link MarketstatEventBuilder#withSource(Object)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withSource()
            throws Exception
    {
        MarketstatEventBuilder builder = getBuilder();
        setDefaults(builder);
        // null source
        builder.withSource(null);
        assertEquals(null,
                     builder.getMarketstat().getSource());
        // non-null source
        builder.withSource(this);
        assertEquals(this,
                     builder.getMarketstat().getSource());
        verify(builder);
    }
    /**
     * Tests {@link MarketstatEventBuilder#withClosePrice(BigDecimal)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withClosePrice()
            throws Exception
    {
        MarketstatEventBuilder builder = setDefaults(getBuilder());
        BigDecimal price = null;
        builder.withClosePrice(price);
        assertNull(builder.getMarketstat().getClose());
        price = new BigDecimal(-10);
        builder.withClosePrice(price);
        assertEquals(price,
                     builder.getMarketstat().getClose());
        price = BigDecimal.ZERO;
        builder.withClosePrice(price);
        assertEquals(price,
                     builder.getMarketstat().getClose());
        price = BigDecimal.TEN;
        builder.withClosePrice(price);
        assertEquals(price,
                     builder.getMarketstat().getClose());
        verify(builder);
    }
    /**
     * Tests {@link MarketstatEventBuilder#withCloseDate(String)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withCloseDate()
            throws Exception
    {
        MarketstatEventBuilder builder = setDefaults(getBuilder());
        String date = null;
        builder.withCloseDate(date);
        assertEquals(date,
                     builder.getMarketstat().getCloseDate());
        date = "";
        builder.withCloseDate(date);
        assertEquals(date,
                     builder.getMarketstat().getCloseDate());
        date = "not-a-date";
        builder.withCloseDate(date);
        assertEquals(date,
                     builder.getMarketstat().getCloseDate());
        date = DateUtils.dateToString(new Date());
        builder.withCloseDate(date);
        assertEquals(date,
                     builder.getMarketstat().getCloseDate());
        verify(builder);
    }
    /**
     * Tests {@link MarketstatEventBuilder#withCloseExchange(String)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withCloseExchange()
            throws Exception
    {
        MarketstatEventBuilder builder = setDefaults(getBuilder());
        String exchange = null;
        builder.withCloseExchange(exchange);
        assertEquals(exchange,
                     builder.getMarketstat().getCloseExchange());
        exchange = "";
        builder.withCloseExchange(exchange);
        assertEquals(exchange,
                     builder.getMarketstat().getCloseExchange());
        exchange = "exchange";
        builder.withCloseExchange(exchange);
        assertEquals(exchange,
                     builder.getMarketstat().getCloseExchange());
        verify(builder);
    }
    /**
     * Tests {@link MarketstatEventBuilder#withHighPrice(BigDecimal)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withHighPrice()
            throws Exception
    {
        MarketstatEventBuilder builder = setDefaults(getBuilder());
        BigDecimal price = null;
        builder.withHighPrice(price);
        assertNull(builder.getMarketstat().getHigh());
        price = new BigDecimal(-10);
        builder.withHighPrice(price);
        assertEquals(price,
                     builder.getMarketstat().getHigh());
        price = BigDecimal.ZERO;
        builder.withHighPrice(price);
        assertEquals(price,
                     builder.getMarketstat().getHigh());
        price = BigDecimal.TEN;
        builder.withHighPrice(price);
        assertEquals(price,
                     builder.getMarketstat().getHigh());
        verify(builder);
    }
    /**
     * Tests {@link MarketstatEventBuilder#withHighExchange(String)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withHighExchange()
            throws Exception
    {
        MarketstatEventBuilder builder = setDefaults(getBuilder());
        String exchange = null;
        builder.withHighExchange(exchange);
        assertEquals(exchange,
                     builder.getMarketstat().getHighExchange());
        exchange = "";
        builder.withHighExchange(exchange);
        assertEquals(exchange,
                     builder.getMarketstat().getHighExchange());
        exchange = "exchange";
        builder.withHighExchange(exchange);
        assertEquals(exchange,
                     builder.getMarketstat().getHighExchange());
        verify(builder);
    }
    /**
     * Tests {@link MarketstatEventBuilder#withTradeHighTime(String)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withTradeHighTime()
            throws Exception
    {
        MarketstatEventBuilder builder = setDefaults(getBuilder());
        String date = null;
        builder.withTradeHighTime(date);
        assertEquals(date,
                     builder.getMarketstat().getTradeHighTime());
        date = "";
        builder.withTradeHighTime(date);
        assertEquals(date,
                     builder.getMarketstat().getTradeHighTime());
        date = "not-a-date";
        builder.withTradeHighTime(date);
        assertEquals(date,
                     builder.getMarketstat().getTradeHighTime());
        date = DateUtils.dateToString(new Date());
        builder.withTradeHighTime(date);
        assertEquals(date,
                     builder.getMarketstat().getTradeHighTime());
        verify(builder);
    }
    /**
     * Tests {@link MarketstatEventBuilder#withTradeLowTime(String)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withTradeLowTime()
            throws Exception
    {
        MarketstatEventBuilder builder = setDefaults(getBuilder());
        String date = null;
        builder.withTradeLowTime(date);
        assertEquals(date,
                     builder.getMarketstat().getTradeLowTime());
        date = "";
        builder.withTradeLowTime(date);
        assertEquals(date,
                     builder.getMarketstat().getTradeLowTime());
        date = "not-a-date";
        builder.withTradeLowTime(date);
        assertEquals(date,
                     builder.getMarketstat().getTradeLowTime());
        date = DateUtils.dateToString(new Date());
        builder.withTradeLowTime(date);
        assertEquals(date,
                     builder.getMarketstat().getTradeLowTime());
        verify(builder);
    }
    /**
     * Tests {@link MarketstatEventBuilder#withInstrument(org.marketcetera.core.trade.Instrument)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withInstrument()
            throws Exception
    {
        MarketstatEventBuilder builder = setDefaults(getBuilder());
        instrument = null;
        builder.withInstrument(instrument);
        assertEquals(instrument,
                     builder.getMarketstat().getInstrument());
        assertEquals(instrument,
                     builder.getMarketstat().getInstrumentAsString());
        instrument = equity;
        builder.withInstrument(instrument);
        assertEquals(instrument,
                     builder.getMarketstat().getInstrument());
        assertEquals(instrument.getSymbol(),
                     builder.getMarketstat().getInstrumentAsString());
        instrument = option;
        builder = setDefaults(getBuilder());
        builder.withInstrument(instrument);
        assertEquals(instrument,
                     builder.getMarketstat().getInstrument());
        assertEquals(instrument.getSymbol(),
                     builder.getMarketstat().getInstrumentAsString());
        verify(builder);
    }
    /**
     * Tests {@link MarketstatEventBuilder#withLowPrice(BigDecimal)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withLowPrice()
            throws Exception
    {
        MarketstatEventBuilder builder = setDefaults(getBuilder());
        BigDecimal price = null;
        builder.withLowPrice(price);
        assertNull(builder.getMarketstat().getLow());
        price = new BigDecimal(-10);
        builder.withLowPrice(price);
        assertEquals(price,
                     builder.getMarketstat().getLow());
        price = BigDecimal.ZERO;
        builder.withLowPrice(price);
        assertEquals(price,
                     builder.getMarketstat().getLow());
        price = BigDecimal.TEN;
        builder.withLowPrice(price);
        assertEquals(price,
                     builder.getMarketstat().getLow());
        verify(builder);
    }
    /**
     * Tests {@link MarketstatEventBuilder#withLowExchange(String)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withLowExchange()
            throws Exception
    {
        MarketstatEventBuilder builder = setDefaults(getBuilder());
        String exchange = null;
        builder.withLowExchange(exchange);
        assertEquals(exchange,
                     builder.getMarketstat().getLowExchange());
        exchange = "";
        builder.withLowExchange(exchange);
        assertEquals(exchange,
                     builder.getMarketstat().getLowExchange());
        exchange = "exchange";
        builder.withLowExchange(exchange);
        assertEquals(exchange,
                     builder.getMarketstat().getLowExchange());
        verify(builder);
    }
    /**
     * Tests {@link MarketstatEventBuilder#withOpenPrice(BigDecimal)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withOpenPrice()
            throws Exception
    {
        MarketstatEventBuilder builder = setDefaults(getBuilder());
        BigDecimal price = null;
        builder.withOpenPrice(price);
        assertNull(builder.getMarketstat().getOpen());
        price = new BigDecimal(-10);
        builder.withOpenPrice(price);
        assertEquals(price,
                     builder.getMarketstat().getOpen());
        price = BigDecimal.ZERO;
        builder.withOpenPrice(price);
        assertEquals(price,
                     builder.getMarketstat().getOpen());
        price = BigDecimal.TEN;
        builder.withOpenPrice(price);
        assertEquals(price,
                     builder.getMarketstat().getOpen());
        verify(builder);
    }
    /**
     * Tests {@link MarketstatEventBuilder#withOpenExchange(String)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withOpenExchange()
            throws Exception
    {
        MarketstatEventBuilder builder = setDefaults(getBuilder());
        String exchange = null;
        builder.withOpenExchange(exchange);
        assertEquals(exchange,
                     builder.getMarketstat().getOpenExchange());
        exchange = "";
        builder.withOpenExchange(exchange);
        assertEquals(exchange,
                     builder.getMarketstat().getOpenExchange());
        exchange = "exchange";
        builder.withOpenExchange(exchange);
        assertEquals(exchange,
                     builder.getMarketstat().getOpenExchange());
        verify(builder);
    }
    /**
     * Tests {@link MarketstatEventBuilder#withPreviousClosePrice(BigDecimal)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withPreviousClosePrice()
            throws Exception
    {
        MarketstatEventBuilder builder = setDefaults(getBuilder());
        BigDecimal price = null;
        builder.withPreviousClosePrice(price);
        assertNull(builder.getMarketstat().getPreviousClose());
        price = new BigDecimal(-10);
        builder.withPreviousClosePrice(price);
        assertEquals(price,
                     builder.getMarketstat().getPreviousClose());
        price = BigDecimal.ZERO;
        builder.withPreviousClosePrice(price);
        assertEquals(price,
                     builder.getMarketstat().getPreviousClose());
        price = BigDecimal.TEN;
        builder.withPreviousClosePrice(price);
        assertEquals(price,
                     builder.getMarketstat().getPreviousClose());
        verify(builder);
    }
    /**
     * Tests {@link MarketstatEventBuilder#withPreviousCloseDate(String)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withPreviousCloseDate()
            throws Exception
    {
        MarketstatEventBuilder builder = setDefaults(getBuilder());
        String date = null;
        builder.withPreviousCloseDate(date);
        assertEquals(date,
                     builder.getMarketstat().getPreviousCloseDate());
        date = "";
        builder.withPreviousCloseDate(date);
        assertEquals(date,
                     builder.getMarketstat().getPreviousCloseDate());
        date = "not-a-date";
        builder.withPreviousCloseDate(date);
        assertEquals(date,
                     builder.getMarketstat().getPreviousCloseDate());
        date = DateUtils.dateToString(new Date());
        builder.withPreviousCloseDate(date);
        assertEquals(date,
                     builder.getMarketstat().getPreviousCloseDate());
        verify(builder);
    }
    /**
     * Tests {@link MarketstatEventBuilder#withVolume(BigDecimal)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withVolume()
            throws Exception
    {
        MarketstatEventBuilder builder = setDefaults(getBuilder());
        BigDecimal volume = null;
        builder.withVolume(volume);
        assertNull(builder.getMarketstat().getVolume());
        volume = new BigDecimal(-10);
        builder.withVolume(volume);
        assertEquals(volume,
                     builder.getMarketstat().getVolume());
        volume = BigDecimal.ZERO;
        builder.withVolume(volume);
        assertEquals(volume,
                     builder.getMarketstat().getVolume());
        volume = BigDecimal.TEN;
        builder.withVolume(volume);
        assertEquals(volume,
                     builder.getMarketstat().getVolume());
        verify(builder);
    }
    /**
     * Tests {@link MarketstatEventBuilder#withValue(BigDecimal)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withValue()
            throws Exception
    {
        MarketstatEventBuilder builder = setDefaults(getBuilder());
        BigDecimal value = null;
        builder.withValue(value);
        assertNull(builder.getMarketstat().getValue());
        value = new BigDecimal(-10);
        builder.withValue(value);
        assertEquals(value,
                     builder.getMarketstat().getValue());
        value = BigDecimal.ZERO;
        builder.withValue(value);
        assertEquals(value,
                     builder.getMarketstat().getValue());
        value = BigDecimal.TEN;
        builder.withValue(value);
        assertEquals(value,
                     builder.getMarketstat().getValue());
        verify(builder);
    }
    /**
     * Tests {@link MarketstatEventBuilder#hasDeliverable(boolean)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void hasDeliverable()
            throws Exception
    {
        MarketstatEventBuilder builder = setDefaults(getBuilder());
        builder.hasDeliverable(false);
        assertEquals(false,
                     builder.getOption().hasDeliverable());
        builder.hasDeliverable(true);
        assertEquals(true,
                     builder.getOption().hasDeliverable());
        verify(builder);
    }
    /**
     * Tests {@link MarketstatEventBuilder#withExpirationType(ExpirationType)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withExpirationType()
            throws Exception
    {
        MarketstatEventBuilder builder = setDefaults(getBuilder());
        builder.withExpirationType(null);
        assertNull(builder.getOption().getExpirationType());
        for(ExpirationType expirationType : ExpirationType.values()) {
            builder.withExpirationType(expirationType);
            assertEquals(expirationType,
                         builder.getOption().getExpirationType());
        }
        verify(builder);
    }
    /**
     * Tests {@link MarketstatEventBuilder#withProviderSymbol(String)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withProviderSymbol()
            throws Exception
    {
        MarketstatEventBuilder builder = setDefaults(getBuilder());
        String symbol = null;
        builder.withProviderSymbol(symbol);
        assertEquals(symbol,
                     builder.getOption().getProviderSymbol());
        assertEquals(symbol,
                     builder.getFuture().getProviderSymbol());
        symbol = "";
        builder.withProviderSymbol(symbol);
        assertEquals(symbol,
                     builder.getOption().getProviderSymbol());
        assertEquals(symbol,
                     builder.getFuture().getProviderSymbol());
        symbol = "MQF/W/X";
        builder.withProviderSymbol(symbol);
        assertEquals(symbol,
                     builder.getOption().getProviderSymbol());
        assertEquals(symbol,
                     builder.getFuture().getProviderSymbol());
        verify(builder);
    }
    /**
     * Tests {@link MarketstatEventBuilder#withEventType(org.marketcetera.core.event.EventType)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withEventType()
            throws Exception
    {
        MarketstatEventBuilder builder = setDefaults(getBuilder());
        EventType type = null;
        builder.withEventType(type);
        assertEquals(type,
                     builder.getMarketstat().getEventType());
        type = EventType.UNKNOWN;
        builder.withEventType(type);
        assertEquals(type,
                     builder.getMarketstat().getEventType());
        type = EventType.SNAPSHOT_PART;
        builder.withEventType(type);
        assertEquals(type,
                     builder.getMarketstat().getEventType());
        verify(builder);
    }
    /**
     * Tests {@link MarketstatEventBuilder#withContractSize(int)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withContractSize()
            throws Exception
    {
        MarketstatEventBuilder builder = setDefaults(getBuilder());
        builder.withContractSize(Integer.MIN_VALUE);
        assertEquals(Integer.MIN_VALUE,
                     builder.getFuture().getContractSize());
        builder.withContractSize(0);
        assertEquals(0,
                     builder.getFuture().getContractSize());
        builder.withContractSize(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE,
                     builder.getFuture().getContractSize());
        verify(builder);
    }
    /**
     * Tests {@link MarketstatEventBuilder#withMultiplier(BigDecimal)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withMultiplier()
            throws Exception
    {
        MarketstatEventBuilder builder = setDefaults(getBuilder());
        builder.withMultiplier(new BigDecimal(Integer.MIN_VALUE));
        assertEquals(new BigDecimal(Integer.MIN_VALUE),
                     builder.getOption().getMultiplier());
        builder.withMultiplier(BigDecimal.ZERO);
        assertEquals(BigDecimal.ZERO,
                     builder.getOption().getMultiplier());
        builder.withMultiplier(new BigDecimal(Integer.MAX_VALUE));
        assertEquals(new BigDecimal(Integer.MAX_VALUE),
                     builder.getOption().getMultiplier());
        verify(builder);
    }
    /**
     * Tests {@link MarketstatEventBuilder#withInterestChange(BigDecimal)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withInterestChange()
            throws Exception
    {
        MarketstatEventBuilder builder = setDefaults(getBuilder());
        builder.withInterestChange(new BigDecimal(Integer.MIN_VALUE));
        assertEquals(new BigDecimal(Integer.MIN_VALUE),
                     builder.getInterestChange());
        builder.withInterestChange(BigDecimal.ZERO);
        assertEquals(BigDecimal.ZERO,
                     builder.getInterestChange());
        builder.withInterestChange(new BigDecimal(Integer.MAX_VALUE));
        assertEquals(new BigDecimal(Integer.MAX_VALUE),
                     builder.getInterestChange());
        verify(builder);
    }
    /**
     * Tests {@link MarketstatEventBuilder#withVolumeChange(BigDecimal)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withVolumeChange()
            throws Exception
    {
        MarketstatEventBuilder builder = setDefaults(getBuilder());
        builder.withVolumeChange(new BigDecimal(Integer.MIN_VALUE));
        assertEquals(new BigDecimal(Integer.MIN_VALUE),
                     builder.getVolumeChange());
        builder.withVolumeChange(BigDecimal.ZERO);
        assertEquals(BigDecimal.ZERO,
                     builder.getVolumeChange());
        builder.withVolumeChange(new BigDecimal(Integer.MAX_VALUE));
        assertEquals(new BigDecimal(Integer.MAX_VALUE),
                     builder.getVolumeChange());
        verify(builder);
    }
    /**
     * Tests {@link MarketstatEventBuilder#withUnderlyingInstrument(org.marketcetera.core.trade.Instrument)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withUnderylingInstrument()
            throws Exception
    {
        MarketstatEventBuilder builder = setDefaults(getBuilder());
        builder.withUnderlyingInstrument(instrument);
        assertEquals(instrument,
                     builder.getOption().getUnderlyingInstrument());
        instrument = equity;
        builder.withUnderlyingInstrument(instrument);
        assertEquals(instrument,
                     builder.getOption().getUnderlyingInstrument());
        instrument = option;
        builder = setDefaults(getBuilder());
        builder.withUnderlyingInstrument(instrument);
        assertEquals(instrument,
                     builder.getOption().getUnderlyingInstrument());
        verify(builder);
    }
    /**
     * Tests event <code>hashCode</code> and <code>equals</code>.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void hashCodeAndEquals()
            throws Exception
    {
        MarketstatEventBuilder builder = getBuilder();
        MarketstatEvent event1 = setDefaults(builder).create();
        MarketstatEvent event2 = builder.create();
        MarketstatEvent event3 = setDefaults(builder).create();
        assertEquals(event1.getMessageId(),
                     event2.getMessageId());
        assertFalse(event2.getMessageId() == event3.getMessageId());
        EqualityAssert.assertEquality(event1,
                                      event2,
                                      event3,
                                      null,
                                      this);
    }
    /**
     * Tests {@link org.marketcetera.core.event.impl.DividendEventImpl} validation.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void validation()
            throws Exception
    {
        final MarketstatEventBuilder builder = setDefaults(getBuilder());
        // check messageId
        builder.withMessageId(-1);
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_INVALID_MESSAGEID.getText(builder.getMarketstat().getMessageId())) {
            @Override
            protected void run()
                    throws Exception
            {
                builder.create();
            }
        };
        setDefaults(builder);
        // 0 is ok
        builder.withMessageId(0);
        verify(builder);
        // minimum value is ok (asks for a generated id)
        setDefaults(builder).withMessageId(Long.MIN_VALUE);
        verify(builder);
        // maximum value is ok
        setDefaults(builder).withMessageId(Long.MAX_VALUE);
        verify(builder);
        // timestamp
        // negative timestamp ok (not even sure what this means, maybe 1ms before epoch?)
        builder.withTimestamp(new Date(-1));
        verify(builder);
       // 0 timestamp
        setDefaults(builder).withTimestamp(new Date(0));
        verify(builder);
        // null timestamp (requests a new timestamp)
        setDefaults(builder).withTimestamp(null);
        verify(builder);
        // normal timestamp
        setDefaults(builder).withTimestamp(new Date());
        verify(builder);
        // instrument
        setDefaults(builder).withInstrument(null);
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_EQUITY_REQUIRED.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                builder.create();
            }
        };
        setDefaults(builder).withInstrument(option);
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_EQUITY_REQUIRED.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                builder.create();
            }
        };
        final MarketstatEventBuilder optionBuilder = MarketstatEventBuilder.optionMarketstat();
        instrument = option;
        setDefaults(optionBuilder).withInstrument(null);
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_OPTION_REQUIRED.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                optionBuilder.create();
            }
        };
        setDefaults(optionBuilder).withInstrument(equity);
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_OPTION_REQUIRED.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                optionBuilder.create();
            }
        };
        final MarketstatEventBuilder futureBuilder = MarketstatEventBuilder.futureMarketstat();
        instrument = future;
        setDefaults(futureBuilder).withInstrument(null);
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_FUTURE_REQUIRED.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                futureBuilder.create();
            }
        };
        setDefaults(futureBuilder).withInstrument(equity);
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_FUTURE_REQUIRED.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                futureBuilder.create();
            }
        };
    }
    /**
     * Verifies that the given builder can produce an event of the
     * correct type with the builder's attributes.
     * 
     * <p>Note that the builder is assumed to be in a state that
     * can produce an event without error.
     * 
     * @param inBuilder a <code>MarketstatEventBuilder</code> value
     * @return a <code>MarketstatEvent</code> value
     * @throws Exception if an unexpected error occurs
     */
    private MarketstatEvent verify(MarketstatEventBuilder inBuilder)
            throws Exception
    {
        assertNotNull(inBuilder);
        assertNotNull(inBuilder.toString());
        MarketstatEvent event = inBuilder.create();
        assertNotNull(event);
        assertNotNull(event.toString());
        assertEquals(inBuilder.getMarketstat().getClose(),
                     event.getClose());
        assertEquals(inBuilder.getMarketstat().getCloseDate(),
                     event.getCloseDate());
        assertEquals(inBuilder.getMarketstat().getCloseExchange(),
                     event.getCloseExchange());
        assertEquals(inBuilder.getMarketstat().getHigh(),
                     event.getHigh());
        assertEquals(inBuilder.getMarketstat().getHighExchange(),
                     event.getHighExchange());
        assertEquals(inBuilder.getMarketstat().getInstrument(),
                     event.getInstrument());
        // check the instrumentAsString method
        assertEquals(inBuilder.getMarketstat().getInstrumentAsString(),
                     event.getInstrumentAsString());
        assertEquals(inBuilder.getMarketstat().getLow(),
                     event.getLow());
        assertEquals(inBuilder.getMarketstat().getLowExchange(),
                     event.getLowExchange());
        assertEquals(inBuilder.getMarketstat().getEventType(),
                     event.getEventType());
        assertFalse(event.getEventType() == EventType.SNAPSHOT_FINAL);
        event.setEventType(EventType.SNAPSHOT_FINAL);
        assertEquals(EventType.SNAPSHOT_FINAL,
                     event.getEventType());
        // there is a special case for messageId - if equal to Long.MIN_VALUE
        //  then it will be some value >= 0
        if(inBuilder.getMarketstat().getMessageId() == Long.MIN_VALUE) {
            assertTrue(event.getMessageId() >= 0);
        } else {
            assertEquals(inBuilder.getMarketstat().getMessageId(),
                         event.getMessageId());
        }
        assertEquals(inBuilder.getMarketstat().getOpen(),
                     event.getOpen());
        assertEquals(inBuilder.getMarketstat().getOpenExchange(),
                     event.getOpenExchange());
        assertEquals(inBuilder.getMarketstat().getPreviousClose(),
                     event.getPreviousClose());
        assertEquals(inBuilder.getMarketstat().getPreviousCloseDate(),
                     event.getPreviousCloseDate());
        assertEquals(inBuilder.getMarketstat().getSource(),
                     event.getSource());
        assertEquals(inBuilder.getMarketstat().getTradeHighTime(),
                     event.getTradeHighTime());
        assertEquals(inBuilder.getMarketstat().getTradeLowTime(),
                     event.getTradeLowTime());
        // there's a special case for timestamp, too
        if(inBuilder.getMarketstat().getTimestamp() == null) {
            assertNotNull(event.getTimestamp());
            assertEquals(event.getTimestamp().getTime(),
                         event.getTimeMillis());
        } else {
            assertEquals(inBuilder.getMarketstat().getTimestamp(),
                         event.getTimestamp());
            assertEquals(inBuilder.getMarketstat().getTimeMillis(),
                         event.getTimeMillis());
        }
        assertEquals(inBuilder.getMarketstat().getVolume(),
                     event.getVolume());
        assertEquals(inBuilder.getMarketstat().getValue(),
                     event.getValue());
        if(event instanceof OptionMarketstatEvent) {
            OptionMarketstatEvent optionEvent = (OptionMarketstatEvent)event;
            assertEquals(inBuilder.getOption().getExpirationType(),
                         optionEvent.getExpirationType());
            assertEquals(inBuilder.getOption().getInstrument(),
                         optionEvent.getInstrument());
            assertEquals(inBuilder.getOption().getInstrument().getSymbol(),
                         optionEvent.getInstrumentAsString());
            assertEquals(inBuilder.getOption().getMultiplier(),
                         optionEvent.getMultiplier());
            assertEquals(inBuilder.getOption().getUnderlyingInstrument(),
                         optionEvent.getUnderlyingInstrument());
            assertEquals(inBuilder.getOption().hasDeliverable(),
                         optionEvent.hasDeliverable());
            assertEquals(inBuilder.getOption().getProviderSymbol(),
                         optionEvent.getProviderSymbol());
            assertEquals(inBuilder.getInterestChange(),
                         optionEvent.getInterestChange());
            assertEquals(inBuilder.getVolumeChange(),
                         optionEvent.getVolumeChange());
        }
        if(event instanceof FutureEvent) {
            FutureEvent futureEvent = (FutureEvent)event;
            assertEquals(inBuilder.getFuture().getProviderSymbol(),
                         futureEvent.getProviderSymbol());
            assertEquals(inBuilder.getFuture().getContractSize(),
                         futureEvent.getContractSize());
        }
        Object newSource = new Object();
        event.setSource(newSource);
        assertEquals(newSource,
                     event.getSource());
        return event;
    }
    /**
     * Sets valid defaults in the given builder.
     * 
     * @param inBuilder a <code>MarketstatEventBuilder</code> value
     * @return a <code>MarketstatEventBuilder</code> value
     * @throws Exception if an unexpected error occurs
     */
    private MarketstatEventBuilder setDefaults(MarketstatEventBuilder inBuilder)
            throws Exception
    {
        long millis = System.currentTimeMillis();
        long millisInADay = 1000 * 60 * 60 * 24;
        int counter = 0;
        inBuilder.hasDeliverable(false);
        inBuilder.withCloseDate(DateUtils.dateToString(new Date(millis + (millisInADay * counter++))));
        inBuilder.withCloseExchange("close exchange");
        inBuilder.withClosePrice(new BigDecimal(counter++));
        inBuilder.withExpirationType(ExpirationType.AMERICAN);
        inBuilder.withHighExchange("high exchange");
        inBuilder.withHighPrice(new BigDecimal(counter++));
        inBuilder.withInstrument(instrument);
        inBuilder.withLowExchange("low exchange");
        inBuilder.withLowPrice(new BigDecimal(counter++));
        inBuilder.withMessageId(idCounter.incrementAndGet());
        inBuilder.withMultiplier(BigDecimal.ZERO);
        inBuilder.withOpenExchange("open exchange");
        inBuilder.withOpenPrice(new BigDecimal(counter++));
        inBuilder.withProviderSymbol("MSQ/K/X");
        inBuilder.withEventType(EventType.UNKNOWN);
        inBuilder.withContractSize(1200);
        inBuilder.withPreviousCloseDate(DateUtils.dateToString(new Date(millis + (millisInADay * counter++))));
        inBuilder.withPreviousClosePrice(new BigDecimal(counter++));
        inBuilder.withSource(this);
        inBuilder.withTimestamp(new Date());
        inBuilder.withTradeHighTime(DateUtils.dateToString(new Date(millis + (millisInADay * counter++))));
        inBuilder.withTradeLowTime(DateUtils.dateToString(new Date(millis + (millisInADay * counter++))));
        inBuilder.withUnderlyingInstrument(instrument);
        inBuilder.withVolume(new BigDecimal(counter++));
        inBuilder.withValue(new BigDecimal(counter++));
        inBuilder.withVolumeChange(EventTestBase.generateDecimalValue());
        inBuilder.withInterestChange(EventTestBase.generateDecimalValue());
        return inBuilder;
    }
    /**
     * Gets the builder to use for testing.
     *
     * @return a <code>MarketstatEventBuilder</code> value
     */
    private MarketstatEventBuilder getBuilder()
    {
        if(useInstrument) {
            return MarketstatEventBuilder.marketstat(instrument);
        } else {
            if(instrument instanceof EquityImpl) {
                return MarketstatEventBuilder.equityMarketstat();
            } else if(instrument instanceof Option) {
                return MarketstatEventBuilder.optionMarketstat();
            } else if(instrument instanceof FutureImpl) {
                return MarketstatEventBuilder.futureMarketstat();
            }
        }
        throw new UnsupportedOperationException();
    }
    /**
     * if set to true, will cause the builder to be created with {@link MarketstatEventBuilder#marketstat(org.marketcetera.core.trade.Instrument)}
     */
    private boolean useInstrument;
    /**
     * indicates what instrument to use during tests
     */
    private Instrument instrument;
    /**
     * test instrument
     */
    private final Equity equity = new EquityImpl("METC");
    /**
     * test option
     */
    private final Option option = new OptionImpl("MSFT",
                                             "20100319",
                                             BigDecimal.ONE,
                                             OptionType.Call);
    /**
     * test future
     */
    private final Future future = new FutureImpl("AAPL",
                                             FutureExpirationMonth.APRIL,
                                             12);
    /**
     * counter used to guarantee unique events
     */
    private static final AtomicLong idCounter = new AtomicLong(0);
}
