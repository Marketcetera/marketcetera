package org.marketcetera.event.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.marketcetera.event.EventTestBase;
import org.marketcetera.event.Messages;
import org.marketcetera.event.OptionEvent;
import org.marketcetera.event.TradeEvent;
import org.marketcetera.marketdata.DateUtils;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.options.ExpirationType;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.OptionType;
import org.marketcetera.util.test.EqualityAssert;

/* $License$ */

/**
 * Tests {@link TradeEventBuilder} and {@link TradeEvent} implementations.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class TradeEventTest
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
        useEquity = true;
        useInstrument = false;
    }
    /**
     * Tests the ability to create various types of {@link TradeEventBuilder} objects.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void builderTypes()
            throws Exception
    {
       useEquity = false; useInstrument = false;
       verify(setDefaults(getBuilder()));
       useEquity = false; useInstrument = true;
       verify(setDefaults(getBuilder()));
       useEquity = true; useInstrument = false;
       verify(setDefaults(getBuilder()));
       useEquity = true; useInstrument = true;
       verify(setDefaults(getBuilder()));
       // create a new kind of instrument
       new ExpectedFailure<UnsupportedOperationException>() {
           @Override
           protected void run()
                   throws Exception
           {
               TradeEventBuilder.tradeEvent(EventTestBase.generateUnsupportedInstrument());
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
        // create equity builders and supply an option instrument
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_EQUITY_REQUIRED.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                setDefaults(TradeEventBuilder.equityTradeEvent()).withInstrument(option).create();
            }
        };
        verify(setDefaults(TradeEventBuilder.equityTradeEvent()).withInstrument(equity));
        // now check option builders with an equity instrument
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_OPTION_REQUIRED.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                setDefaults(TradeEventBuilder.optionTradeEvent()).withInstrument(equity).create();
            }
        };
        verify(setDefaults(TradeEventBuilder.optionTradeEvent()).withInstrument(option));
    }
    /**
     * Tests {@link TradeEventBuilder#hasDeliverable(boolean)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void hasDeliverable()
            throws Exception
    {
        TradeEventBuilder<TradeEvent> builder = setDefaults(getBuilder());
        builder.hasDeliverable(false);
        assertEquals(false,
                     builder.getOption().hasDeliverable());
        builder.hasDeliverable(true);
        assertEquals(true,
                     builder.getOption().hasDeliverable());
        verify(builder);
    }
    /**
     * Tests {@link TradeEventBuilder#withExchange(String)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withExchange()
            throws Exception
    {
        TradeEventBuilder<TradeEvent> builder = setDefaults(getBuilder());
        String exchange = null;
        builder.withExchange(exchange);
        assertEquals(exchange,
                     builder.getMarketData().getExchange());
        exchange = "";
        builder.withExchange(exchange);
        assertEquals(exchange,
                     builder.getMarketData().getExchange());
        exchange = "exchange";
        builder.withExchange(exchange);
        assertEquals(exchange,
                     builder.getMarketData().getExchange());
        verify(builder);
    }
    /**
     * Tests {@link TradeEventBuilder#withExpirationType(ExpirationType)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withExpirationType()
            throws Exception
    {
        TradeEventBuilder<TradeEvent> builder = setDefaults(getBuilder());
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
     * Tests {@link TradeEventBuilder#withInstrument(Instrument)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withInstrument()
            throws Exception
    {
        TradeEventBuilder<TradeEvent> builder = setDefaults(getBuilder());
        Instrument instrument = null;
        builder.withInstrument(instrument);
        assertEquals(instrument,
                     builder.getMarketData().getInstrument());
        assertEquals(instrument,
                     builder.getMarketData().getInstrumentAsString());
        assertEquals(instrument,
                     builder.getOption().getInstrument());
        instrument = equity;
        builder.withInstrument(instrument);
        assertEquals(instrument,
                     builder.getMarketData().getInstrument());
        assertEquals(instrument.getSymbol(),
                     builder.getMarketData().getInstrumentAsString());
        assertFalse(instrument.equals(builder.getOption().getInstrument()));
        useEquity = false;
        builder = setDefaults(getBuilder());
        instrument = option;
        builder.withInstrument(instrument);
        assertEquals(instrument,
                     builder.getMarketData().getInstrument());
        assertEquals(instrument.getSymbol(),
                     builder.getMarketData().getInstrumentAsString());
        assertEquals(instrument,
                     builder.getOption().getInstrument());
        verify(builder);
    }
    /**
     * Tests {@link TradeEventBuilder#withMessageId(long)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withMessageId()
            throws Exception
    {
        TradeEventBuilder<TradeEvent> builder = getBuilder();
        setDefaults(builder);
        builder.withMessageId(Long.MIN_VALUE);
        assertEquals(Long.MIN_VALUE,
                     builder.getMarketData().getMessageId());
        builder.withMessageId(-1);
        assertEquals(-1,
                     builder.getMarketData().getMessageId());
        builder.withMessageId(Long.MAX_VALUE);
        assertEquals(Long.MAX_VALUE,
                     builder.getMarketData().getMessageId());
        verify(builder);
    }
    /**
     * Tests {@link TradeEventBuilder#withMultiplier(int)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withMultiplier()
            throws Exception
    {
        TradeEventBuilder<TradeEvent> builder = setDefaults(getBuilder());
        builder.withMultiplier(Integer.MIN_VALUE);
        assertEquals(Integer.MIN_VALUE,
                     builder.getOption().getMultiplier());
        builder.withMultiplier(0);
        assertEquals(0,
                     builder.getOption().getMultiplier());
        builder.withMultiplier(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE,
                     builder.getOption().getMultiplier());
        verify(builder);
    }
    /**
     * Tests {@link TradeEventBuilder#withPrice(BigDecimal)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withPrice()
            throws Exception
    {
        TradeEventBuilder<TradeEvent> builder = setDefaults(getBuilder());
        BigDecimal price = null;
        builder.withPrice(price);
        assertNull(builder.getMarketData().getPrice());
        price = new BigDecimal(-10);
        builder.withPrice(price);
        assertEquals(price,
                     builder.getMarketData().getPrice());
        price = BigDecimal.ZERO;
        builder.withPrice(price);
        assertEquals(price,
                     builder.getMarketData().getPrice());
        price = BigDecimal.TEN;
        builder.withPrice(price);
        assertEquals(price,
                     builder.getMarketData().getPrice());
        verify(builder);
    }
    /**
     * Tests {@link TradeEventBuilder#withTradeDate(String)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withTradeDate()
            throws Exception
    {
        TradeEventBuilder<TradeEvent> builder = setDefaults(getBuilder());
        String date = null;
        builder.withTradeDate(date);
        assertEquals(date,
                     builder.getMarketData().getExchangeTimestamp());
        date = "";
        builder.withTradeDate(date);
        assertEquals(date,
                     builder.getMarketData().getExchangeTimestamp());
        date = "not-a-date";
        builder.withTradeDate(date);
        assertEquals(date,
                     builder.getMarketData().getExchangeTimestamp());
        date = DateUtils.dateToString(new Date());
        builder.withTradeDate(date);
        assertEquals(date,
                     builder.getMarketData().getExchangeTimestamp());
        verify(builder);
    }
    /**
     * Tests {@link TradeEventBuilder#withSize(BigDecimal)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withSize()
            throws Exception
    {
        TradeEventBuilder<TradeEvent> builder = setDefaults(getBuilder());
        BigDecimal size = null;
        builder.withSize(size);
        assertNull(builder.getMarketData().getSize());
        size = new BigDecimal(-10);
        builder.withSize(size);
        assertEquals(size,
                     builder.getMarketData().getSize());
        size = BigDecimal.ZERO;
        builder.withSize(size);
        assertEquals(size,
                     builder.getMarketData().getSize());
        size = BigDecimal.TEN;
        builder.withSize(size);
        assertEquals(size,
                     builder.getMarketData().getSize());
        verify(builder);
    }
    /**
     * Tests {@link TradeEventBuilder#withSource(Object)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withSource()
            throws Exception
    {
        TradeEventBuilder<TradeEvent> builder = getBuilder();
        setDefaults(builder);
        // null source
        builder.withSource(null);
        assertEquals(null,
                     builder.getMarketData().getSource());
        // non-null source
        builder.withSource(this);
        assertEquals(this,
                     builder.getMarketData().getSource());
        verify(builder);
    }
    /**
     * Tests {@link TradeEventBuilder#withTimestamp(Date)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withTimestamp()
            throws Exception
    {
        TradeEventBuilder<TradeEvent> builder = getBuilder();
        setDefaults(builder);
        // null timestamp
        builder.withTimestamp(null);
        assertEquals(null,
                     builder.getMarketData().getTimestamp());
        // regular timestamp
        Date timestamp = new Date();
        builder.withTimestamp(timestamp);
        assertEquals(timestamp,
                     builder.getMarketData().getTimestamp());
        // make a weird timestamp
        timestamp = new Date(-1);
        builder.withTimestamp(timestamp);
        assertEquals(timestamp,
                     builder.create().getTimestamp());
        verify(builder);
    }
    /**
     * Tests {@link TradeEventBuilder#withUnderlyingInstrument(Instrument)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withUnderylingInstrument()
            throws Exception
    {
        TradeEventBuilder<TradeEvent> builder = setDefaults(getBuilder());
        Instrument instrument = null;
        builder.withUnderlyingInstrument(instrument);
        assertEquals(instrument,
                     builder.getOption().getUnderlyingInstrument());
        instrument = equity;
        builder.withUnderlyingInstrument(instrument);
        assertEquals(instrument,
                     builder.getOption().getUnderlyingInstrument());
        useEquity = false;
        builder = setDefaults(getBuilder());
        instrument = option;
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
        TradeEventBuilder<TradeEvent> builder = getBuilder();
        TradeEvent event1 = setDefaults(builder).create();
        TradeEvent event2 = builder.create();
        TradeEvent event3 = setDefaults(builder).create();
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
     * Verifies that the given builder can produce an event of the
     * correct type with the builder's attributes.
     * 
     * <p>Note that the builder is assumed to be in a state that
     * can produce an event without error.
     * 
     * @param inBuilder a <code>TradeEventBuilder</code> value
     * @return a <code>TradeEvent</code> value
     * @throws Exception if an unexpected error occurs
     */
    private TradeEvent verify(TradeEventBuilder<TradeEvent> inBuilder)
            throws Exception
    {
        assertNotNull(inBuilder);
        assertNotNull(inBuilder.toString());
        TradeEvent event = inBuilder.create();
        assertNotNull(event);
        assertNotNull(event.toString());
        assertEquals(inBuilder.getMarketData().getExchange(),
                     event.getExchange());
        assertEquals(inBuilder.getMarketData().getExchangeTimestamp(),
                     event.getExchangeTimestamp());
        assertEquals(inBuilder.getMarketData().getExchangeTimestamp(),
                     event.getTradeDate());
        assertEquals(inBuilder.getMarketData().getInstrument(),
                     event.getInstrument());
        // check the instrumentAsString method
        assertEquals(inBuilder.getMarketData().getInstrumentAsString(),
                     event.getInstrumentAsString());
        // there is a special case for messageId - if equal to Long.MIN_VALUE
        //  then it will be some value >= 0
        if(inBuilder.getMarketData().getMessageId() == Long.MIN_VALUE) {
            assertTrue(event.getMessageId() >= 0);
        } else {
            assertEquals(inBuilder.getMarketData().getMessageId(),
                         event.getMessageId());
        }
        assertEquals(inBuilder.getMarketData().getPrice(),
                     event.getPrice());
        assertEquals(inBuilder.getMarketData().getSize(),
                     event.getSize());
        assertEquals(inBuilder.getMarketData().getSource(),
                     event.getSource());
        // there's a special case for timestamp, too
        if(inBuilder.getMarketData().getTimestamp() == null) {
            assertNotNull(event.getTimestamp());
            assertEquals(event.getTimestamp().getTime(),
                         event.getTimeMillis());
        } else {
            assertEquals(inBuilder.getMarketData().getTimestamp(),
                         event.getTimestamp());
            assertEquals(inBuilder.getMarketData().getTimeMillis(),
                         event.getTimeMillis());
        }
        if(event instanceof OptionEvent) {
            OptionEvent optionEvent = (OptionEvent)event;
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
     * @param inBuilder a <code>TradeEventBuilder</code> value
     * @return a <code>TradeEventBuilder</code> value
     * @throws Exception if an unexpected error occurs
     */
    private TradeEventBuilder<TradeEvent> setDefaults(TradeEventBuilder<TradeEvent> inBuilder)
            throws Exception
    {
        long millis = System.currentTimeMillis();
        long millisInADay = 1000 * 60 * 60 * 24;
        int counter = 0;
        inBuilder.hasDeliverable(false);
        inBuilder.withExchange("exchange");
        inBuilder.withExpirationType(ExpirationType.AMERICAN);
        inBuilder.withInstrument(useEquity ? equity : option);
        inBuilder.withMessageId(System.nanoTime());
        inBuilder.withMultiplier(0);
        inBuilder.withPrice(BigDecimal.ONE);
        inBuilder.withTradeDate(DateUtils.dateToString(new Date(millis + (millisInADay * counter++))));
        inBuilder.withSize(BigDecimal.TEN);
        inBuilder.withTimestamp(new Date());
        inBuilder.withUnderlyingInstrument(useEquity ? equity : option);
        return inBuilder;
    }
    /**
     * Gets the builder to use for testing.
     *
     * @return a <code>TradeEventBuilder</code> value
     */
    private TradeEventBuilder<TradeEvent> getBuilder()
    {
        if(useEquity) {
            if(useInstrument) {
                return TradeEventBuilder.tradeEvent(equity);
            } else {
                return TradeEventBuilder.equityTradeEvent();
            }
        } else {
            if(useInstrument) {
                return TradeEventBuilder.tradeEvent(option);
            } else {
                return TradeEventBuilder.optionTradeEvent();
            }
        }
    }
    /**
     * if set to true, will cause the builder to be created
     */
    private boolean useInstrument;
    /**
     * indicates whether to use an equity builder or an option builder
     */
    private boolean useEquity;
    /**
     * test instrument with {@link TradeEventBuilder#tradeEvent(Instrument)}.
     */
    private final Equity equity = new Equity("METC");
    /**
     * test option
     */
    private final Option option = new Option("MSFT",
                                             "20100319",
                                             BigDecimal.ONE,
                                             OptionType.Call);
}
