package org.marketcetera.event.impl;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Before;
import org.junit.Test;
import org.marketcetera.event.*;
import org.marketcetera.event.Messages;
import org.marketcetera.marketdata.DateUtils;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.options.ExpirationType;
import org.marketcetera.trade.*;
import org.marketcetera.util.test.EqualityAssert;

/* $License$ */

/**
 * Tests {@link TradeEventBuilder} and {@link TradeEvent} implementations.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.0.0
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
        instrument = equity;
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
       instrument = option; useInstrument = false;
       verify(setDefaults(getBuilder()));
       instrument = option; useInstrument = true;
       verify(setDefaults(getBuilder()));
       instrument = equity; useInstrument = false;
       verify(setDefaults(getBuilder()));
       instrument = equity; useInstrument = true;
       verify(setDefaults(getBuilder()));
       instrument = future; useInstrument = false;
       verify(setDefaults(getBuilder()));
       instrument = future; useInstrument = true;
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
        // now check future builders with an equity instrument
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_FUTURE_REQUIRED.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                setDefaults(TradeEventBuilder.futureTradeEvent()).withInstrument(equity).create();
            }
        };
        verify(setDefaults(TradeEventBuilder.futureTradeEvent()).withInstrument(future));
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
     * Tests {@link TradeEventBuilder#withProviderSymbol(String)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withProviderSymbol()
            throws Exception
    {
        TradeEventBuilder<TradeEvent> builder = setDefaults(getBuilder());
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
        symbol = "MSQ/W/X";
        builder.withProviderSymbol(symbol);
        assertEquals(symbol,
                     builder.getOption().getProviderSymbol());
        assertEquals(symbol,
                     builder.getFuture().getProviderSymbol());
        verify(builder);
    }
    /**
     * 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withTradeCondition()
            throws Exception
    {
        TradeEventBuilder<TradeEvent> builder = setDefaults(getBuilder());
        String tradeCondition = null;
        builder.withTradeCondition(tradeCondition);
        assertEquals(tradeCondition,
                     builder.getMarketData().getTradeCondition());
        tradeCondition = "";
        builder.withTradeCondition(tradeCondition);
        assertEquals(tradeCondition,
                     builder.getMarketData().getTradeCondition());
        tradeCondition = "EFG";
        builder.withTradeCondition(tradeCondition);
        assertEquals(tradeCondition,
                     builder.getMarketData().getTradeCondition());
        verify(builder);
    }
    /**
     * Tests {@link TradeEventBuilder#withEventType(org.marketcetera.event.EventType)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withEventType()
            throws Exception
    {
        TradeEventBuilder<TradeEvent> builder = setDefaults(getBuilder());
        EventType type = null;
        builder.withEventType(type);
        assertEquals(type,
                     builder.getMarketData().getEventType());
        type = EventType.UNKNOWN;
        builder.withEventType(type);
        assertEquals(type,
                     builder.getMarketData().getEventType());
        type = EventType.SNAPSHOT_PART;
        builder.withEventType(type);
        assertEquals(type,
                     builder.getMarketData().getEventType());
        verify(builder);
    }
    /**
     * Tests {@link TradeEventBuilder#withContractSize(int)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withContractSize()
            throws Exception
    {
        TradeEventBuilder<TradeEvent> builder = setDefaults(getBuilder());
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
     * Tests {@link TradeEventBuilder#withInstrument(Instrument)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withInstrument()
            throws Exception
    {
        TradeEventBuilder<TradeEvent> builder = setDefaults(getBuilder());
        instrument = null;
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
        instrument = option;
        builder = setDefaults(getBuilder());
        builder.withInstrument(instrument);
        assertEquals(instrument,
                     builder.getMarketData().getInstrument());
        assertEquals(instrument.getSymbol(),
                     builder.getMarketData().getInstrumentAsString());
        assertEquals(instrument,
                     builder.getOption().getInstrument());
        verify(builder);
        instrument = future;
        builder = setDefaults(getBuilder());
        builder.withInstrument(instrument);
        assertEquals(instrument,
                     builder.getMarketData().getInstrument());
        assertEquals(instrument.getSymbol(),
                     builder.getMarketData().getInstrumentAsString());
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
        instrument = null;
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
        instrument = future;
        builder = setDefaults(getBuilder());
        builder.withUnderlyingInstrument(instrument);
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
        assertEquals(inBuilder.getMarketData().getTradeCondition(),
                     event.getTradeCondition());
        assertEquals(inBuilder.getMarketData().getEventType(),
                     event.getEventType());
        assertFalse(event.getEventType() == EventType.SNAPSHOT_FINAL);
        event.setEventType(EventType.SNAPSHOT_FINAL);
        assertEquals(EventType.SNAPSHOT_FINAL,
                     event.getEventType());
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
            assertEquals(inBuilder.getOption().getProviderSymbol(),
                         optionEvent.getProviderSymbol());
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
        inBuilder.withInstrument(instrument);
        inBuilder.withMessageId(idCounter.incrementAndGet());
        inBuilder.withMultiplier(BigDecimal.ZERO);
        inBuilder.withProviderSymbol("MSQ/K/X");
        inBuilder.withEventType(EventType.UPDATE_FINAL);
        inBuilder.withPrice(BigDecimal.ONE);
        inBuilder.withTradeDate(DateUtils.dateToString(new Date(millis + (millisInADay * counter++))));
        inBuilder.withSize(BigDecimal.TEN);
        inBuilder.withTimestamp(new Date());
        inBuilder.withUnderlyingInstrument(instrument);
        inBuilder.withContractSize(3600);
        return inBuilder;
    }
    /**
     * Gets the builder to use for testing.
     *
     * @return a <code>TradeEventBuilder</code> value
     */
    private TradeEventBuilder<TradeEvent> getBuilder()
    {
        if(useInstrument) {
            return TradeEventBuilder.tradeEvent(instrument);
        } else {
            if(instrument instanceof Equity) {
                return TradeEventBuilder.equityTradeEvent();
            } else if(instrument instanceof Option) {
                return TradeEventBuilder.optionTradeEvent();
            } else if(instrument instanceof Future) {
                return TradeEventBuilder.futureTradeEvent();
            }
        }
        throw new UnsupportedOperationException();
    }
    /**
     * if set to true, will cause the builder to be created
     */
    private boolean useInstrument;
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
    /**
     * test instrument with {@link TradeEventBuilder#tradeEvent(Instrument)}.
     */
    private final Future future = new Future("METC",
                                             FutureExpirationMonth.MARCH,
                                             15);
    /**
     * instrument used during tests
     */
    private Instrument instrument = equity;
    /**
     * id counter used to guarantee unique events
     */
    private static final AtomicLong idCounter = new AtomicLong(0);
}
