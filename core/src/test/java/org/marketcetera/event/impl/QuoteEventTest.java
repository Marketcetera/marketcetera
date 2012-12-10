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
 * Tests {@link QuoteEventBuilder}, {@link EquityAskEventImpl}, {@link EquityBidEventImpl}, 
 * {@link OptionAskEventImpl}, and {@link OptionBidEventImpl}.
 * {@link CurrencyAskEventImpl}, and {@link CurrencyBidEventImpl}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.0.0
 */
public class QuoteEventTest
        implements Messages
{
    /**
     * Run before each test.
     *
     * @throws Exception if an error occurs
     */
    @Before
    public void setup()
            throws Exception
    {
        instrument = equity;
        useAsk = true;
        useInstrument = false;
    }
    /**
     * Tests the ability to create various types of {@link QuoteEventBuilder} objects.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void builderTypes()
            throws Exception
    {
       instrument = option; useInstrument = false; useAsk = false;
       verify(setDefaults(getBuilder()));
       instrument = option; useInstrument = false; useAsk = true;
       verify(setDefaults(getBuilder()));
       instrument = option; useInstrument = true; useAsk = false;
       verify(setDefaults(getBuilder()));
       instrument = option; useInstrument = true; useAsk = true;
       verify(setDefaults(getBuilder()));
       instrument = equity; useInstrument = false; useAsk = false;
       verify(setDefaults(getBuilder()));
       instrument = equity; useInstrument = false; useAsk = true;
       verify(setDefaults(getBuilder()));
       instrument = equity; useInstrument = true; useAsk = false;
       verify(setDefaults(getBuilder()));
       instrument = equity; useInstrument = true; useAsk = true;
       verify(setDefaults(getBuilder()));
       instrument = future; useInstrument = false; useAsk = false;
       verify(setDefaults(getBuilder()));
       instrument = future; useInstrument = false; useAsk = true;
       verify(setDefaults(getBuilder()));
       instrument = future; useInstrument = true; useAsk = false;
       verify(setDefaults(getBuilder()));
       instrument = future; useInstrument = true; useAsk = true;
       verify(setDefaults(getBuilder()));
       instrument = currency; useInstrument = false; useAsk = false;
       verify(setDefaults(getBuilder()));
       instrument = currency; useInstrument = false; useAsk = true;
       verify(setDefaults(getBuilder()));
       instrument = currency; useInstrument = true; useAsk = false;
       verify(setDefaults(getBuilder()));
       instrument = currency; useInstrument = true; useAsk = true;
       verify(setDefaults(getBuilder()));
   
       // create a new kind of instrument
       final Instrument unsupportedInstrument = EventTestBase.generateUnsupportedInstrument(); 
       new ExpectedFailure<UnsupportedOperationException>() {
           @Override
           protected void run()
                   throws Exception
           {
               QuoteEventBuilder.askEvent(unsupportedInstrument);
           }
       };
       new ExpectedFailure<UnsupportedOperationException>() {
           @Override
           protected void run()
                   throws Exception
           {
               QuoteEventBuilder.bidEvent(unsupportedInstrument);
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
                setDefaults(QuoteEventBuilder.equityAskEvent()).withInstrument(option).create();
            }
        };
        assertNotNull(setDefaults(QuoteEventBuilder.equityAskEvent()).withInstrument(equity).create());
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_EQUITY_REQUIRED.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                setDefaults(QuoteEventBuilder.equityBidEvent()).withInstrument(option).create();
            }
        };
        assertNotNull(setDefaults(QuoteEventBuilder.equityBidEvent()).withInstrument(equity).create());
        // now check option builders with an equity instrument
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_OPTION_REQUIRED.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                setDefaults(QuoteEventBuilder.optionAskEvent()).withInstrument(equity).create();
            }
        };
        assertNotNull(setDefaults(QuoteEventBuilder.optionAskEvent()).withInstrument(option).create());
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_OPTION_REQUIRED.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                setDefaults(QuoteEventBuilder.optionBidEvent()).withInstrument(equity).create();
            }
        };
        assertNotNull(setDefaults(QuoteEventBuilder.optionBidEvent()).withInstrument(option).create());
    }
    /**
     * Tests {@link QuoteEventBuilder#add(QuoteEvent)}, {@link QuoteEventBuilder#change(QuoteEvent, Date, BigDecimal)}, and
     * {@link QuoteEventBuilder#delete(QuoteEvent)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void quoteCreators()
            throws Exception
    {
        // generate a quote event of various types (equity vs option && ask vs bid)
        // all the following events are of action ADD (using various actions as the source)
        // then make sure the generated event is the same in all ways except the action, which must be ADD
        instrument = option; useAsk = false;
        QuoteEvent sourceEvent = generateQuote(QuoteAction.ADD);
        QuoteEvent generatedEvent = QuoteEventBuilder.add(sourceEvent);
        verifyQuoteEvent(sourceEvent,
                         generatedEvent,
                         QuoteAction.ADD,
                         sourceEvent.getSize(),
                         sourceEvent.getTimestamp());
        instrument = option; useAsk = true;
        sourceEvent = generateQuote(QuoteAction.CHANGE);
        generatedEvent = QuoteEventBuilder.add(sourceEvent);
        verifyQuoteEvent(sourceEvent,
                         generatedEvent,
                         QuoteAction.ADD,
                         sourceEvent.getSize(),
                         sourceEvent.getTimestamp());
        instrument = equity; useAsk = false;
        sourceEvent = generateQuote(QuoteAction.DELETE);
        generatedEvent = QuoteEventBuilder.add(sourceEvent);
        verifyQuoteEvent(sourceEvent,
                         generatedEvent,
                         QuoteAction.ADD,
                         sourceEvent.getSize(),
                         sourceEvent.getTimestamp());
        instrument = equity; useAsk = true;
        sourceEvent = generateQuote(QuoteAction.CHANGE);
        generatedEvent = QuoteEventBuilder.add(sourceEvent);
        verifyQuoteEvent(sourceEvent,
                         generatedEvent,
                         QuoteAction.ADD,
                         sourceEvent.getSize(),
                         sourceEvent.getTimestamp());
        instrument = future; useAsk = false;
        sourceEvent = generateQuote(QuoteAction.DELETE);
        generatedEvent = QuoteEventBuilder.add(sourceEvent);
        verifyQuoteEvent(sourceEvent,
                         generatedEvent,
                         QuoteAction.ADD,
                         sourceEvent.getSize(),
                         sourceEvent.getTimestamp());
        instrument = future; useAsk = true;
        sourceEvent = generateQuote(QuoteAction.CHANGE);
        generatedEvent = QuoteEventBuilder.add(sourceEvent);
        verifyQuoteEvent(sourceEvent,
                         generatedEvent,
                         QuoteAction.ADD,
                         sourceEvent.getSize(),
                         sourceEvent.getTimestamp());
        instrument = currency; useAsk = false;
        sourceEvent = generateQuote(QuoteAction.DELETE);
        generatedEvent = QuoteEventBuilder.add(sourceEvent);
        verifyQuoteEvent(sourceEvent,
                         generatedEvent,
                         QuoteAction.ADD,
                         sourceEvent.getSize(),
                         sourceEvent.getTimestamp());
        instrument = currency; useAsk = true;
        sourceEvent = generateQuote(QuoteAction.CHANGE);
        generatedEvent = QuoteEventBuilder.add(sourceEvent);
        verifyQuoteEvent(sourceEvent,
                         generatedEvent,
                         QuoteAction.ADD,
                         sourceEvent.getSize(),
                         sourceEvent.getTimestamp());
        // repeat the tests generating CHANGE events
        Date timestamp = new Date();
        BigDecimal size = new BigDecimal(100);
        instrument = option; useAsk = false;
        sourceEvent = generateQuote(QuoteAction.ADD);
        generatedEvent = QuoteEventBuilder.change(sourceEvent,
                                                  timestamp,
                                                  size);
        verifyQuoteEvent(sourceEvent,
                         generatedEvent,
                         QuoteAction.CHANGE,
                         size,
                         timestamp);
        instrument = option; useAsk = true;
        sourceEvent = generateQuote(QuoteAction.CHANGE);
        generatedEvent = QuoteEventBuilder.change(sourceEvent,
                                                  timestamp,
                                                  size);
        verifyQuoteEvent(sourceEvent,
                         generatedEvent,
                         QuoteAction.CHANGE,
                         size,
                         timestamp);
        instrument = equity; useAsk = false;
        sourceEvent = generateQuote(QuoteAction.CHANGE);
        generatedEvent = QuoteEventBuilder.change(sourceEvent,
                                                  timestamp,
                                                  size);
        verifyQuoteEvent(sourceEvent,
                         generatedEvent,
                         QuoteAction.CHANGE,
                         size,
                         timestamp);
        instrument = equity; useAsk = true;
        sourceEvent = generateQuote(QuoteAction.CHANGE);
        generatedEvent = QuoteEventBuilder.change(sourceEvent,
                                                  timestamp,
                                                  size);
        verifyQuoteEvent(sourceEvent,
                         generatedEvent,
                         QuoteAction.CHANGE,
                         size,
                         timestamp);
        instrument = future; useAsk = false;
        sourceEvent = generateQuote(QuoteAction.CHANGE);
        generatedEvent = QuoteEventBuilder.change(sourceEvent,
                                                  timestamp,
                                                  size);
        verifyQuoteEvent(sourceEvent,
                         generatedEvent,
                         QuoteAction.CHANGE,
                         size,
                         timestamp);
        instrument = future; useAsk = true;
        sourceEvent = generateQuote(QuoteAction.CHANGE);
        generatedEvent = QuoteEventBuilder.change(sourceEvent,
                                                  timestamp,
                                                  size);
        verifyQuoteEvent(sourceEvent,
                         generatedEvent,
                         QuoteAction.CHANGE,
                         size,
                         timestamp);
        
        instrument = currency; useAsk = false;
        sourceEvent = generateQuote(QuoteAction.CHANGE);
        generatedEvent = QuoteEventBuilder.change(sourceEvent,
                                                  timestamp,
                                                  size);
        verifyQuoteEvent(sourceEvent,
                         generatedEvent,
                         QuoteAction.CHANGE,
                         size,
                         timestamp);
        instrument = currency; useAsk = true;
        sourceEvent = generateQuote(QuoteAction.CHANGE);
        generatedEvent = QuoteEventBuilder.change(sourceEvent,
                                                  timestamp,
                                                  size);
        verifyQuoteEvent(sourceEvent,
                         generatedEvent,
                         QuoteAction.CHANGE,
                         size,
                         timestamp);
        // repeat the tests generating DELETE events
        instrument = option; useAsk = false;
        sourceEvent = generateQuote(QuoteAction.ADD);
        generatedEvent = QuoteEventBuilder.delete(sourceEvent);
        verifyQuoteEvent(sourceEvent,
                         generatedEvent,
                         QuoteAction.DELETE,
                         sourceEvent.getSize(),
                         sourceEvent.getTimestamp());
        instrument = option; useAsk = true;
        sourceEvent = generateQuote(QuoteAction.CHANGE);
        generatedEvent = QuoteEventBuilder.delete(sourceEvent);
        verifyQuoteEvent(sourceEvent,
                         generatedEvent,
                         QuoteAction.DELETE,
                         sourceEvent.getSize(),
                         sourceEvent.getTimestamp());
        instrument = equity; useAsk = false;
        sourceEvent = generateQuote(QuoteAction.DELETE);
        generatedEvent = QuoteEventBuilder.delete(sourceEvent);
        verifyQuoteEvent(sourceEvent,
                         generatedEvent,
                         QuoteAction.DELETE,
                         sourceEvent.getSize(),
                         sourceEvent.getTimestamp());
        instrument = equity; useAsk = true;
        sourceEvent = generateQuote(QuoteAction.CHANGE);
        generatedEvent = QuoteEventBuilder.delete(sourceEvent);
        verifyQuoteEvent(sourceEvent,
                         generatedEvent,
                         QuoteAction.DELETE,
                         sourceEvent.getSize(),
                         sourceEvent.getTimestamp());
        instrument = future; useAsk = false;
        sourceEvent = generateQuote(QuoteAction.DELETE);
        generatedEvent = QuoteEventBuilder.delete(sourceEvent);
        verifyQuoteEvent(sourceEvent,
                         generatedEvent,
                         QuoteAction.DELETE,
                         sourceEvent.getSize(),
                         sourceEvent.getTimestamp());
        instrument = future; useAsk = true;
        sourceEvent = generateQuote(QuoteAction.CHANGE);
        generatedEvent = QuoteEventBuilder.delete(sourceEvent);
        verifyQuoteEvent(sourceEvent,
                         generatedEvent,
                         QuoteAction.DELETE,
                         sourceEvent.getSize(),
                         sourceEvent.getTimestamp());
        instrument = currency; useAsk = false;
        sourceEvent = generateQuote(QuoteAction.DELETE);
        generatedEvent = QuoteEventBuilder.delete(sourceEvent);
        verifyQuoteEvent(sourceEvent,
                         generatedEvent,
                         QuoteAction.DELETE,
                         sourceEvent.getSize(),
                         sourceEvent.getTimestamp());
        instrument = currency; useAsk = true;
        sourceEvent = generateQuote(QuoteAction.CHANGE);
        generatedEvent = QuoteEventBuilder.delete(sourceEvent);
        verifyQuoteEvent(sourceEvent,
                         generatedEvent,
                         QuoteAction.DELETE,
                         sourceEvent.getSize(),
                         sourceEvent.getTimestamp());
    }
    /**
     * Tests {@link QuoteEventBuilder#hasDeliverable(boolean)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void hasDeliverable()
            throws Exception
    {
        QuoteEventBuilder<?> builder = setDefaults(getBuilder());
        builder.hasDeliverable(false);
        assertEquals(false,
                     builder.getOption().hasDeliverable());
        builder.hasDeliverable(true);
        assertEquals(true,
                     builder.getOption().hasDeliverable());
        verify(builder);
    }
    /**
     * Tests {@link QuoteEventBuilder#withAction(QuoteAction)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withAction()
            throws Exception
    {
        QuoteEventBuilder<?> builder = setDefaults(getBuilder());
        builder.withAction(null);
        assertNull(builder.getQuote().getAction());
        for(QuoteAction action : QuoteAction.values()) {
            builder.withAction(action);
            assertEquals(action,
                         builder.getQuote().getAction());
        }
        verify(builder);
    }
    /**
     * Tests {@link QuoteEventBuilder#withExchange(String)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withExchange()
            throws Exception
    {
        QuoteEventBuilder<?> builder = setDefaults(getBuilder());
        String exchange = null;
        builder.withExchange(exchange);
        assertEquals(exchange,
                     builder.getQuote().getExchange());
        exchange = "";
        builder.withExchange(exchange);
        assertEquals(exchange,
                     builder.getQuote().getExchange());
        exchange = "exchange";
        builder.withExchange(exchange);
        assertEquals(exchange,
                     builder.getQuote().getExchange());
        verify(builder);
    }
    /**
     * Tests {@link QuoteEventBuilder#withExpirationType(ExpirationType)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withExpirationType()
            throws Exception
    {
        QuoteEventBuilder<?> builder = setDefaults(getBuilder());
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
     * Tests {@link QuoteEventBuilder#withProviderSymbol(String)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withProviderSymbol()
            throws Exception
    {
        QuoteEventBuilder<?> builder = setDefaults(getBuilder());
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
     * Tests {@link QuoteEventBuilder#withEventType(org.marketcetera.event.EventType)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withEventType()
            throws Exception
    {
        QuoteEventBuilder<?> builder = setDefaults(getBuilder());
        EventType type = null;
        builder.withEventType(type);
        assertEquals(type,
                     builder.getQuote().getEventType());
        type = EventType.UNKNOWN;
        builder.withEventType(type);
        assertEquals(type,
                     builder.getQuote().getEventType());
        type = EventType.SNAPSHOT_PART;
        builder.withEventType(type);
        assertEquals(type,
                     builder.getQuote().getEventType());
        verify(builder);
    }
    /**
     * Tests {@link QuoteEventBuilder#withContractSize(int)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withContractSize()
            throws Exception
    {
        QuoteEventBuilder<?> builder = setDefaults(getBuilder());
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
     * Tests {@link QuoteEventBuilder<?>#withInstrument(Instrument)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withInstrument()
            throws Exception
    {
        QuoteEventBuilder<?> builder = setDefaults(getBuilder());
        instrument = null;
        builder.withInstrument(instrument);
        assertEquals(instrument,
                     builder.getQuote().getInstrument());
        assertEquals(instrument,
                     builder.getQuote().getInstrumentAsString());
        assertEquals(instrument,
                     builder.getOption().getInstrument());
        instrument = equity;
        builder.withInstrument(instrument);
        assertEquals(instrument,
                     builder.getQuote().getInstrument());
        assertEquals(instrument.getSymbol(),
                     builder.getQuote().getInstrumentAsString());
        assertFalse(instrument.equals(builder.getOption().getInstrument()));
        instrument = option;
        builder = setDefaults(getBuilder());
        builder.withInstrument(instrument);
        assertEquals(instrument,
                     builder.getQuote().getInstrument());
        assertEquals(instrument.getSymbol(),
                     builder.getQuote().getInstrumentAsString());
        assertEquals(instrument,
                     builder.getOption().getInstrument());
        verify(builder);
        instrument = future;
        builder = setDefaults(getBuilder());
        builder.withInstrument(instrument);
        assertEquals(instrument,
                     builder.getQuote().getInstrument());
        assertEquals(instrument.getSymbol(),
                     builder.getQuote().getInstrumentAsString());
        verify(builder);
        instrument = currency;
        builder = setDefaults(getBuilder());
        builder.withInstrument(instrument);
        assertEquals(instrument,
                     builder.getQuote().getInstrument());
        assertEquals(instrument.getSymbol(),
                     builder.getQuote().getInstrumentAsString());
        verify(builder);
    }
    /**
     * Tests {@link QuoteEventBuilder#withMessageId(long)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withMessageId()
            throws Exception
    {
        QuoteEventBuilder<?> builder = getBuilder();
        setDefaults(builder);
        builder.withMessageId(Long.MIN_VALUE);
        assertEquals(Long.MIN_VALUE,
                     builder.getQuote().getMessageId());
        builder.withMessageId(-1);
        assertEquals(-1,
                     builder.getQuote().getMessageId());
        builder.withMessageId(Long.MAX_VALUE);
        assertEquals(Long.MAX_VALUE,
                     builder.getQuote().getMessageId());
        verify(builder);
    }
    /**
     * Tests {@link QuoteEventBuilder#withMultiplier(int)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withMultiplier()
            throws Exception
    {
        QuoteEventBuilder<?> builder = setDefaults(getBuilder());
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
     * Tests {@link QuoteEventBuilder#withPrice(BigDecimal)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withPrice()
            throws Exception
    {
        QuoteEventBuilder<?> builder = setDefaults(getBuilder());
        BigDecimal price = null;
        builder.withPrice(price);
        assertNull(builder.getQuote().getPrice());
        price = new BigDecimal(-10);
        builder.withPrice(price);
        assertEquals(price,
                     builder.getQuote().getPrice());
        price = BigDecimal.ZERO;
        builder.withPrice(price);
        assertEquals(price,
                     builder.getQuote().getPrice());
        price = BigDecimal.TEN;
        builder.withPrice(price);
        assertEquals(price,
                     builder.getQuote().getPrice());
        verify(builder);
    }
    /**
     * Tests {@link QuoteEventBuilder#withQuoteDate(String)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withQuoteDate()
            throws Exception
    {
        QuoteEventBuilder<?> builder = setDefaults(getBuilder());
        String date = null;
        builder.withQuoteDate(date);
        assertEquals(date,
                     builder.getQuote().getExchangeTimestamp());
        date = "";
        builder.withQuoteDate(date);
        assertEquals(date,
                     builder.getQuote().getExchangeTimestamp());
        date = "not-a-date";
        builder.withQuoteDate(date);
        assertEquals(date,
                     builder.getQuote().getExchangeTimestamp());
        date = DateUtils.dateToString(new Date());
        builder.withQuoteDate(date);
        assertEquals(date,
                     builder.getQuote().getExchangeTimestamp());
        verify(builder);
    }
    /**
     * Tests {@link QuoteEventBuilder#withSize(BigDecimal)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withSize()
            throws Exception
    {
        QuoteEventBuilder<?> builder = setDefaults(getBuilder());
        BigDecimal size = null;
        builder.withSize(size);
        assertNull(builder.getQuote().getSize());
        size = new BigDecimal(-10);
        builder.withSize(size);
        assertEquals(size,
                     builder.getQuote().getSize());
        size = BigDecimal.ZERO;
        builder.withSize(size);
        assertEquals(size,
                     builder.getQuote().getSize());
        size = BigDecimal.TEN;
        builder.withSize(size);
        assertEquals(size,
                     builder.getQuote().getSize());
        verify(builder);
    }
    /**
     * Tests {@link QuoteEventBuilder#withSource(Object)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withSource()
            throws Exception
    {
        QuoteEventBuilder<?> builder = getBuilder();
        setDefaults(builder);
        // null source
        builder.withSource(null);
        assertEquals(null,
                     builder.getQuote().getSource());
        // non-null source
        builder.withSource(this);
        assertEquals(this,
                     builder.getQuote().getSource());
        verify(builder);
    }
    /**
     * Tests {@link QuoteEventBuilder#withTimestamp(Date)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withTimestamp()
            throws Exception
    {
        QuoteEventBuilder<?> builder = getBuilder();
        setDefaults(builder);
        // null timestamp
        builder.withTimestamp(null);
        assertEquals(null,
                     builder.getQuote().getTimestamp());
        // regular timestamp
        Date timestamp = new Date();
        builder.withTimestamp(timestamp);
        assertEquals(timestamp,
                     builder.getQuote().getTimestamp());
        // make a weird timestamp
        timestamp = new Date(-1);
        builder.withTimestamp(timestamp);
        assertEquals(timestamp,
                     builder.create().getTimestamp());
        verify(builder);
    }
    /**
     * Tests {@link QuoteEventBuilder#withUnderlyingInstrument(Instrument)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withUnderylingInstrument()
            throws Exception
    {
        QuoteEventBuilder<?> builder = setDefaults(getBuilder());
        Instrument instrument = null;
        builder.withUnderlyingInstrument(instrument);
        assertEquals(instrument,
                     builder.getOption().getUnderlyingInstrument());
        instrument = equity;
        builder.withUnderlyingInstrument(instrument);
        assertEquals(instrument,
                     builder.getOption().getUnderlyingInstrument());
        instrument = future;
        builder.withUnderlyingInstrument(instrument);
        assertEquals(instrument,
                     builder.getOption().getUnderlyingInstrument());
        instrument = option;
        builder = setDefaults(getBuilder());
        builder.withUnderlyingInstrument(instrument);
        assertEquals(instrument,
                     builder.getOption().getUnderlyingInstrument());
        verify(builder);
        instrument = currency;
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
        QuoteEventBuilder<?> builder = getBuilder();
        QuoteEvent event1 = setDefaults(builder).create();
        QuoteEvent event2 = builder.create();
        QuoteEvent event3 = setDefaults(builder).create();
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
     * Tests {@link DividendEventImpl} validation.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void validation()
            throws Exception
    {
        final QuoteEventBuilder<?> builder = setDefaults(getBuilder());
        // check messageId
        builder.withMessageId(-1);
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_INVALID_MESSAGEID.getText(builder.getQuote().getMessageId())) {
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
        // add validation for other attributes
        setDefaults(builder).withAction(null); // this is ok - a default is supplied
        verify(builder);
        setDefaults(builder).withExchange(null);
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_NULL_EXCHANGE.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                builder.create();
            }
        };
        setDefaults(builder).withExchange("");
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_NULL_EXCHANGE.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                builder.create();
            }
        };
        setDefaults(builder).withExchange("exchange");
        verify(builder);
        setDefaults(builder).withQuoteDate(null);
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_NULL_EXCHANGE_TIMESTAMP.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                builder.create();
            }
        };
        setDefaults(builder).withQuoteDate("");
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_NULL_EXCHANGE_TIMESTAMP.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                builder.create();
            }
        };
        // this value is ok
        setDefaults(builder).withQuoteDate("not-a-date");
        verify(builder);
        setDefaults(builder).withQuoteDate(DateUtils.dateToString(new Date()));
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
        final QuoteEventBuilder<?> optionBuilder = QuoteEventBuilder.optionAskEvent();
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
        final QuoteEventBuilder<?> futureAskBuilder = QuoteEventBuilder.futureAskEvent();
        instrument = future;
        setDefaults(futureAskBuilder).withInstrument(null);
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_FUTURE_REQUIRED.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                futureAskBuilder.create();
            }
        };
        setDefaults(futureAskBuilder).withInstrument(equity);
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_FUTURE_REQUIRED.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                futureAskBuilder.create();
            }
        };
        final QuoteEventBuilder<?> futureBidBuilder = QuoteEventBuilder.futureBidEvent();
        setDefaults(futureBidBuilder).withInstrument(null);
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_FUTURE_REQUIRED.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                futureBidBuilder.create();
            }
        };
        setDefaults(futureBidBuilder).withInstrument(equity);
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_FUTURE_REQUIRED.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                futureBidBuilder.create();
            }
        };
        
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_OPTION_REQUIRED.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                optionBuilder.create();
            }
        };
        final QuoteEventBuilder<?> currencyAskBuilder = QuoteEventBuilder.currencyAskEvent();
        instrument = currency;
        setDefaults(currencyAskBuilder).withInstrument(null);
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_CURRENCY_REQUIRED.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                currencyAskBuilder.create();
            }
        };
        setDefaults(currencyAskBuilder).withInstrument(equity);
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_CURRENCY_REQUIRED.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                currencyAskBuilder.create();
            }
        };
        final QuoteEventBuilder<?> currencyBidBuilder = QuoteEventBuilder.currencyBidEvent();
        setDefaults(currencyBidBuilder).withInstrument(null);
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_CURRENCY_REQUIRED.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                currencyBidBuilder.create();
            }
        };
        setDefaults(currencyBidBuilder).withInstrument(equity);
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_CURRENCY_REQUIRED.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                currencyBidBuilder.create();
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
     * @param inBuilder a <code>QuoteEventBuilder</code> value
     * @return a <code>QuoteEvent</code> value
     * @throws Exception if an unexpected error occurs
     */
    private QuoteEvent verify(QuoteEventBuilder<?> inBuilder)
            throws Exception
    {
        assertNotNull(inBuilder);
        assertNotNull(inBuilder.toString());
        QuoteEvent event = inBuilder.create();
        assertNotNull(event);
        assertNotNull(event.toString());
        // special case for action due to setDefaults
        if(inBuilder.getQuote().getAction() == null) {
            assertEquals(QuoteAction.ADD,
                         event.getAction());
        } else {
            assertEquals(inBuilder.getQuote().getAction(),
                         event.getAction());
        }
        assertEquals(inBuilder.getQuote().getExchange(),
                     event.getExchange());
        assertEquals(inBuilder.getQuote().getExchangeTimestamp(),
                     event.getExchangeTimestamp());
        assertEquals(inBuilder.getQuote().getExchangeTimestamp(),
                     event.getQuoteDate());
        assertEquals(inBuilder.getQuote().getInstrument(),
                     event.getInstrument());
        // check the instrumentAsString method
        assertEquals(inBuilder.getQuote().getInstrumentAsString(),
                     event.getInstrumentAsString());
        // there is a special case for messageId - if equal to Long.MIN_VALUE
        //  then it will be some value >= 0
        if(inBuilder.getQuote().getMessageId() == Long.MIN_VALUE) {
            assertTrue(event.getMessageId() >= 0);
        } else {
            assertEquals(inBuilder.getQuote().getMessageId(),
                         event.getMessageId());
        }
        assertEquals(inBuilder.getQuote().getPrice(),
                     event.getPrice());
        assertEquals(inBuilder.getQuote().getSize(),
                     event.getSize());
        assertEquals(inBuilder.getQuote().getSource(),
                     event.getSource());
        assertEquals(inBuilder.getQuote().getEventType(),
                     event.getEventType());
        assertFalse(event.getEventType() == EventType.SNAPSHOT_FINAL);
        event.setEventType(EventType.SNAPSHOT_FINAL);
        assertEquals(EventType.SNAPSHOT_FINAL,
                     event.getEventType());
        // there's a special case for timestamp, too
        if(inBuilder.getQuote().getTimestamp() == null) {
            assertNotNull(event.getTimestamp());
            assertEquals(event.getTimestamp().getTime(),
                         event.getTimeMillis());
        } else {
            assertEquals(inBuilder.getQuote().getTimestamp(),
                         event.getTimestamp());
            assertEquals(inBuilder.getQuote().getTimeMillis(),
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
     * @param inBuilder a <code>QuoteEventBuilder</code> value
     * @return a <code>QuoteEventBuilder</code> value
     * @throws Exception if an unexpected error occurs
     */
    private QuoteEventBuilder<?> setDefaults(QuoteEventBuilder<?> inBuilder)
            throws Exception
    {
        long millis = System.currentTimeMillis();
        long millisInADay = 1000 * 60 * 60 * 24;
        int counter = 0;
        inBuilder.hasDeliverable(false);
        inBuilder.withAction(QuoteAction.ADD);
        inBuilder.withExchange("exchange");
        inBuilder.withExpirationType(ExpirationType.AMERICAN);
        inBuilder.withProviderSymbol("MSQ/K/X");
        inBuilder.withEventType(EventType.UPDATE_FINAL);
        inBuilder.withInstrument(instrument);
        inBuilder.withMessageId(idCounter.incrementAndGet());
        inBuilder.withMultiplier(BigDecimal.ZERO);
        inBuilder.withPrice(BigDecimal.ONE);
        inBuilder.withQuoteDate(DateUtils.dateToString(new Date(millis + (millisInADay * counter++))));
        inBuilder.withSize(BigDecimal.TEN);
        inBuilder.withTimestamp(new Date());
        inBuilder.withUnderlyingInstrument(instrument);
        inBuilder.withContractSize(3600);
        return inBuilder;
    }
    /**
     * Gets the builder to use for testing.
     *
     * @return a <code>QuoteEventBuilder<?></code> value
     */
    private QuoteEventBuilder<?> getBuilder()
    {
        if(useInstrument) {
            if(useAsk) {
                return QuoteEventBuilder.askEvent(instrument);
            } else {
                return QuoteEventBuilder.bidEvent(instrument);
            }
        } else {
            if(instrument instanceof Equity) {
                if(useAsk) {
                    return QuoteEventBuilder.equityAskEvent();
                } else {
                    return QuoteEventBuilder.equityBidEvent();
                }
            } else if(instrument instanceof Option) {
                if(useAsk) {
                    return QuoteEventBuilder.optionAskEvent();
                } else {
                    return QuoteEventBuilder.optionBidEvent();
                }
            } else if(instrument instanceof Future) {
                if(useAsk) {
                    return QuoteEventBuilder.futureAskEvent();
                } else {
                    return QuoteEventBuilder.futureBidEvent();
                }
            } else if(instrument instanceof Currency) {
                if(useAsk) {
                    return QuoteEventBuilder.currencyAskEvent();
                } else {
                    return QuoteEventBuilder.currencyBidEvent();
                }
            }  
            else {
                throw new UnsupportedOperationException();
            }
        }
    }
    /**
     * Verifies that the given generated event matches the source event using the given
     * action, size, and timestamp.
     *
     * @param inSourceEvent a <code>QuoteEvent</code> value
     * @param inGeneratedEvent a <code>QuoteEvent</code> value
     * @param inExpectedAction a <code>QuoteAction</code> value
     * @param inExpectedSize a <code>BigDecimal</code> value
     * @param inExpectedTimestamp a <code>Date</code> value
     */
    private void verifyQuoteEvent(QuoteEvent inSourceEvent,
                                  QuoteEvent inGeneratedEvent,
                                  QuoteAction inExpectedAction,
                                  BigDecimal inExpectedSize,
                                  Date inExpectedTimestamp)
    {
        assertEquals(inExpectedAction,
                     inGeneratedEvent.getAction());
        assertEquals(inSourceEvent.getExchange(),
                     inGeneratedEvent.getExchange());
        assertEquals(inSourceEvent.getExchangeTimestamp(),
                     inGeneratedEvent.getExchangeTimestamp());
        assertEquals(inSourceEvent.getInstrument(),
                     inGeneratedEvent.getInstrument());
        assertEquals(inSourceEvent.getMessageId(),
                     inGeneratedEvent.getMessageId());
        assertEquals(inSourceEvent.getPrice(),
                     inGeneratedEvent.getPrice());
        assertEquals(inExpectedSize,
                     inGeneratedEvent.getSize());
        assertEquals(inSourceEvent.getSource(),
                     inGeneratedEvent.getSource());
        assertEquals(inExpectedTimestamp,
                     inGeneratedEvent.getTimestamp());
        assertEquals(inSourceEvent.getEventType(),
                     inGeneratedEvent.getEventType());
        if(inSourceEvent instanceof OptionEvent) {
            OptionEvent expectedOptionEvent = (OptionEvent)inSourceEvent;
            OptionEvent actualOptionEvent = (OptionEvent)inGeneratedEvent;
            assertEquals(expectedOptionEvent.getExpirationType(),
                         actualOptionEvent.getExpirationType());
            assertEquals(expectedOptionEvent.getInstrument(),
                         actualOptionEvent.getInstrument());
            assertEquals(expectedOptionEvent.getMultiplier(),
                         actualOptionEvent.getMultiplier());
            assertEquals(expectedOptionEvent.getUnderlyingInstrument(),
                         actualOptionEvent.getUnderlyingInstrument());
            assertEquals(expectedOptionEvent.getProviderSymbol(),
                         actualOptionEvent.getProviderSymbol());
        }
        if(inSourceEvent instanceof FutureEvent)  {
            FutureEvent expectedFutureEvent = (FutureEvent)inSourceEvent;
            FutureEvent actualFutureEvent = (FutureEvent)inGeneratedEvent;
            assertEquals(expectedFutureEvent.getProviderSymbol(),
                         actualFutureEvent.getProviderSymbol());
            assertEquals(expectedFutureEvent.getContractSize(),
                         actualFutureEvent.getContractSize());
        }
        if(inSourceEvent instanceof CurrencyEvent)  {
        	CurrencyEvent expectedCurrencyEvent = (CurrencyEvent)inSourceEvent;
        	CurrencyEvent actualCurrencyEvent = (CurrencyEvent)inGeneratedEvent;
            assertEquals(expectedCurrencyEvent.getInstrument().getSymbol(),
                         actualCurrencyEvent.getInstrument().getSymbol());
            assertEquals(expectedCurrencyEvent.getContractSize(),
                         actualCurrencyEvent.getContractSize());
        }
    }
    /**
     * Generates a <code>QuoteEvent<?></code> value.
     *
     * @param inAction a <code>QuoteAction</code> value
     * @return a <code>QuoteEvent&lt;?&gt;</code> value
     */
    private QuoteEvent generateQuote(QuoteAction inAction)
    {
        if(instrument instanceof Equity) {
            if(useAsk) {
                return EventTestBase.generateEquityAskEvent(equity,
                                                            inAction);
            } else {
                return EventTestBase.generateEquityBidEvent(equity,
                                                            inAction);
            }
        } else if(instrument instanceof Option) {
            if(useAsk) {
                return EventTestBase.generateOptionAskEvent(option,
                                                            inAction);
            } else {
                return EventTestBase.generateOptionBidEvent(option,
                                                            inAction);
            }
        } else if(instrument instanceof Future) {
            if(useAsk) {
                return EventTestBase.generateFutureAskEvent(future,
                                                            inAction);
            } else {
                return EventTestBase.generateFutureBidEvent(future,
                                                            inAction);
            }
        } else if(instrument instanceof Currency) {
            if(useAsk) {
                return EventTestBase.generateCurrencyAskEvent(currency,
                                                            inAction);
            } else {
                return EventTestBase.generateCurrencyBidEvent(currency,
                                                            inAction);
            }
        } else {
            throw new UnsupportedOperationException();
        }
    }
    /**
     * indicates whether to use Bid or Ask events to create builders
     */
    private boolean useAsk = true;
    /**
     * indicates whether to use the instrument constructor or not
     */
    private boolean useInstrument = false;
    /**
     * test instrument
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
     * test future
     */
    private final Future future = new Future("GOOG",
                                             FutureExpirationMonth.DECEMBER,
                                             2015);
    
    /**
     * test currency
     */
    private final Currency currency = new Currency("USD","INR","","");
    /**
     * indicates the test instrument to use
     */
    private Instrument instrument = equity;
    /**
     * counter used to guarantee unique events
     */
    private static final AtomicLong idCounter = new AtomicLong(0);
}
