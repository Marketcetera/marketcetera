package org.marketcetera.event.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Test;
import org.marketcetera.event.*;
import org.marketcetera.marketdata.DateUtils;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.trade.Equity;
import org.marketcetera.util.test.EqualityAssert;

/* $License$ */

/**
 * Tests {@link DividendEventBuilder} and {@link DividendEventImpl}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.0.0
 */
public class DividendEventTest
        implements Messages
{
    /**
     * Tests {@link DividendEventBuilder#withMessageId(long)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withMessageId()
            throws Exception
    {
        DividendEventBuilder builder = getBuilder();
        setDefaults(builder);
        builder.withMessageId(Long.MIN_VALUE);
        assertEquals(Long.MIN_VALUE,
                     builder.getDividend().getMessageId());
        builder.withMessageId(-1);
        assertEquals(-1,
                     builder.getDividend().getMessageId());
        builder.withMessageId(Long.MAX_VALUE);
        assertEquals(Long.MAX_VALUE,
                     builder.getDividend().getMessageId());
        verify(builder);
    }
    /**
     * Tests {@link DividendEventBuilder#withTimestamp(Date)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withTimestamp()
            throws Exception
    {
        DividendEventBuilder builder = getBuilder();
        setDefaults(builder);
        // null timestamp
        builder.withTimestamp(null);
        assertEquals(null,
                     builder.getDividend().getTimestamp());
        // regular timestamp
        Date timestamp = new Date();
        builder.withTimestamp(timestamp);
        assertEquals(timestamp,
                     builder.getDividend().getTimestamp());
        // make a weird timestamp
        timestamp = new Date(-1);
        builder.withTimestamp(timestamp);
        assertEquals(timestamp,
                     builder.create().getTimestamp());
        verify(builder);
    }
    /**
     * Tests {@link DividendEventBuilder#withSource(Object)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withSource()
            throws Exception
    {
        DividendEventBuilder builder = getBuilder();
        setDefaults(builder);
        // null source
        builder.withSource(null);
        assertEquals(null,
                     builder.getDividend().getSource());
        // non-null source
        builder.withSource(this);
        assertEquals(this,
                     builder.getDividend().getSource());
        verify(builder);
    }
    /**
     * Tests {@link DividendEventBuilder#withEventType(org.marketcetera.event.EventType)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withEventType()
            throws Exception
    {
        DividendEventBuilder builder = setDefaults(getBuilder());
        EventType type = null;
        builder.withEventType(type);
        assertEquals(type,
                     builder.getDividend().getEventType());
        type = EventType.UNKNOWN;
        builder.withEventType(type);
        assertEquals(type,
                     builder.getDividend().getEventType());
        type = EventType.SNAPSHOT_PART;
        builder.withEventType(type);
        assertEquals(type,
                     builder.getDividend().getEventType());
        verify(builder);
    }
    /**
     * Tests {@link DividendEventBuilder#withEquity(Equity)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withEquity()
            throws Exception
    {
        DividendEventBuilder builder = getBuilder();
        setDefaults(builder);
        // null equity
        builder.withEquity(null);
        assertEquals(null,
                     builder.getDividend().getEquity());
        // non-null equity
        builder.withEquity(equity);
        assertEquals(equity,
                     builder.getDividend().getEquity());
        verify(builder);
    }
    /**
     * Tests {@link DividendEventBuilder#withAmount(BigDecimal)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withAmount()
            throws Exception
    {
        DividendEventBuilder builder = getBuilder();
        setDefaults(builder);
        // null amount
        builder.withAmount(null);
        assertEquals(null,
                     builder.getDividend().getAmount());
        // non-null amount
        builder.withAmount(BigDecimal.ONE);
        assertEquals(BigDecimal.ONE,
                     builder.getDividend().getAmount());
        verify(builder);
    }
    /**
     * Tests {@link DividendEventBuilder#withCurrency(String)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withCurrency()
            throws Exception
    {
        DividendEventBuilder builder = getBuilder();
        setDefaults(builder);
        // null currency
        builder.withCurrency(null);
        assertEquals(null,
                     builder.getDividend().getCurrency());
        // empty currency
        builder.withCurrency("");
        assertEquals("",
                     builder.getDividend().getCurrency());
        // non-null currency
        builder.withCurrency("Peanuts");
        assertEquals("Peanuts",
                     builder.getDividend().getCurrency());
        verify(builder);
    }
    /**
     * Tests {@link DividendEventBuilder#withDeclareDate(String)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withDeclareDate()
            throws Exception
    {
        DividendEventBuilder builder = getBuilder();
        setDefaults(builder);
        // null DeclareDate
        builder.withDeclareDate(null);
        assertEquals(null,
                     builder.getDividend().getDeclareDate());
        // empty DeclareDate
        builder.withDeclareDate("");
        assertEquals("",
                     builder.getDividend().getDeclareDate());
        // non-null DeclareDate (invalid date)
        builder.withDeclareDate("this-is-not-a-date");
        assertEquals("this-is-not-a-date",
                     builder.getDividend().getDeclareDate());
        // non-null (valid date)
        String date = DateUtils.dateToString(new Date());
        builder.withDeclareDate(date);
        assertEquals(date,
                     builder.getDividend().getDeclareDate());
        verify(builder);
    }
    /**
     * Tests {@link DividendEventBuilder#withExecutionDate(String)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withExecutionDate()
            throws Exception
    {
        DividendEventBuilder builder = getBuilder();
        setDefaults(builder);
        // null ExecutionDate
        builder.withExecutionDate(null);
        assertEquals(null,
                     builder.getDividend().getExecutionDate());
        // empty ExecutionDate
        builder.withExecutionDate("");
        assertEquals("",
                     builder.getDividend().getExecutionDate());
        // non-null ExecutionDate (invalid date)
        builder.withExecutionDate("this-is-not-a-date");
        assertEquals("this-is-not-a-date",
                     builder.getDividend().getExecutionDate());
        // non-null (valid date)
        String date = DateUtils.dateToString(new Date());
        builder.withExecutionDate(date);
        assertEquals(date,
                     builder.getDividend().getExecutionDate());
        verify(builder);
    }
    /**
     * Tests {@link DividendEventBuilder#withPaymentDate(String)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withPaymentDate()
            throws Exception
    {
        DividendEventBuilder builder = getBuilder();
        setDefaults(builder);
        // null PaymentDate
        builder.withPaymentDate(null);
        assertEquals(null,
                     builder.getDividend().getPaymentDate());
        // empty PaymentDate
        builder.withPaymentDate("");
        assertEquals("",
                     builder.getDividend().getPaymentDate());
        // non-null PaymentDate (invalid date)
        builder.withPaymentDate("this-is-not-a-date");
        assertEquals("this-is-not-a-date",
                     builder.getDividend().getPaymentDate());
        // non-null (valid date)
        String date = DateUtils.dateToString(new Date());
        builder.withPaymentDate(date);
        assertEquals(date,
                     builder.getDividend().getPaymentDate());
        verify(builder);
    }
    /**
     * Tests {@link DividendEventBuilder#withRecordDate(String)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withRecordDate()
            throws Exception
    {
        DividendEventBuilder builder = getBuilder();
        setDefaults(builder);
        // null RecordDate
        builder.withRecordDate(null);
        assertEquals(null,
                     builder.getDividend().getRecordDate());
        // empty RecordDate
        builder.withRecordDate("");
        assertEquals("",
                     builder.getDividend().getRecordDate());
        // non-null RecordDate (invalid date)
        builder.withRecordDate("this-is-not-a-date");
        assertEquals("this-is-not-a-date",
                     builder.getDividend().getRecordDate());
        // non-null (valid date)
        String date = DateUtils.dateToString(new Date());
        builder.withRecordDate(date);
        assertEquals(date,
                     builder.getDividend().getRecordDate());
        verify(builder);
    }
    /**
     * Tests {@link DividendEventBuilder#withFrequency(DividendFrequency)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withFrequency()
            throws Exception
    {
        DividendEventBuilder builder = getBuilder();
        setDefaults(builder);
        // null Frequency
        builder.withFrequency(null);
        assertEquals(null,
                     builder.getDividend().getFrequency());
        // non-null Frequency
        builder.withFrequency(DividendFrequency.MONTHLY);
        assertEquals(DividendFrequency.MONTHLY,
                     builder.getDividend().getFrequency());
        verify(builder);
    }
    /**
     * Tests {@link DividendEventBuilder#withStatus(DividendStatus)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void withStatus()
            throws Exception
    {
        DividendEventBuilder builder = getBuilder();
        setDefaults(builder);
        // null Status
        builder.withStatus(null);
        assertEquals(null,
                     builder.getDividend().getStatus());
        // non-null Status
        builder.withStatus(DividendStatus.UNOFFICIAL);
        assertEquals(DividendStatus.UNOFFICIAL,
                     builder.getDividend().getStatus());
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
        DividendEventBuilder builder = getBuilder();
        DividendEvent event1 = setDefaults(builder).create();
        DividendEvent event2 = builder.create();
        DividendEvent event3 = setDefaults(builder).create();
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
        final DividendEventBuilder builder = setDefaults(getBuilder());
        // check messageId
        builder.withMessageId(-1);
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_INVALID_MESSAGEID.getText(builder.getDividend().getMessageId())) {
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
        setDefaults(builder).withEquity(null);
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_NULL_EQUITY.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                builder.create();
            }
        };
        setDefaults(builder).withAmount(null);
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_NULL_AMOUNT.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                builder.create();
            }
        };
        setDefaults(builder).withCurrency(null);
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_NULL_CURRENCY.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                builder.create();
            }
        };
        setDefaults(builder).withCurrency("");
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_NULL_CURRENCY.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                builder.create();
            }
        };
        // this value is ok
        setDefaults(builder).withDeclareDate("not-a-date");
        verify(builder);
        setDefaults(builder).withDeclareDate(DateUtils.dateToString(new Date()));
        verify(builder);
        setDefaults(builder).withExecutionDate(null);
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_NULL_EXECUTION_DATE.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                builder.create();
            }
        };
        setDefaults(builder).withExecutionDate("");
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_NULL_EXECUTION_DATE.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                builder.create();
            }
        };
        // this value is ok
        setDefaults(builder).withExecutionDate("not-a-date");
        verify(builder);
        setDefaults(builder).withExecutionDate(DateUtils.dateToString(new Date()));
        verify(builder);
        // this value is ok
        setDefaults(builder).withPaymentDate("not-a-date");
        verify(builder);
        setDefaults(builder).withPaymentDate(DateUtils.dateToString(new Date()));
        verify(builder);
        setDefaults(builder).withRecordDate(null);
        verify(builder);
        setDefaults(builder).withRecordDate("");
        verify(builder);
        // this value is ok
        setDefaults(builder).withRecordDate("not-a-date");
        verify(builder);
        setDefaults(builder).withRecordDate(DateUtils.dateToString(new Date()));
        verify(builder);
        setDefaults(builder).withFrequency(null);
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_NULL_FREQUENCY.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                builder.create();
            }
        };
        setDefaults(builder).withStatus(null);
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_NULL_STATUS.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                builder.create();
            }
        };
        setDefaults(builder).withType(null);
        new ExpectedFailure<IllegalArgumentException>(VALIDATION_NULL_TYPE.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                builder.create();
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
     * @param inBuilder a <code>DividendEventBuilder</code> value
     * @return a <code>DividendEvent</code> value
     * @throws Exception if an unexpected error occurs
     */
    private DividendEvent verify(DividendEventBuilder inBuilder)
            throws Exception
    {
        assertNotNull(inBuilder);
        assertNotNull(inBuilder.toString());
        DividendEvent event = inBuilder.create();
        assertNotNull(event);
        assertNotNull(event.toString());
        // compare event to builder
        assertEquals(inBuilder.getDividend().getAmount(),
                     event.getAmount());
        assertEquals(inBuilder.getDividend().getCurrency(),
                     event.getCurrency());
        assertEquals(inBuilder.getDividend().getDeclareDate(),
                     event.getDeclareDate());
        assertEquals(inBuilder.getDividend().getEquity(),
                     event.getEquity());
        assertEquals(inBuilder.getDividend().getEquity(),
                     event.getInstrument());
        assertEquals(inBuilder.getDividend().getExecutionDate(),
                     event.getExecutionDate());
        assertEquals(inBuilder.getDividend().getFrequency(),
                     event.getFrequency());
        assertEquals(inBuilder.getDividend().getInstrumentAsString(),
                     event.getInstrumentAsString());
        assertEquals(inBuilder.getDividend().getEventType(),
                     event.getEventType());
        assertFalse(event.getEventType() == EventType.SNAPSHOT_FINAL);
        event.setEventType(EventType.SNAPSHOT_FINAL);
        assertEquals(EventType.SNAPSHOT_FINAL,
                     event.getEventType());
        // there is a special case for messageId - if equal to Long.MIN_VALUE
        //  then it will be some value >= 0
        if(inBuilder.getDividend().getMessageId() == Long.MIN_VALUE) {
            assertTrue(event.getMessageId() >= 0);
        } else {
            assertEquals(inBuilder.getDividend().getMessageId(),
                         event.getMessageId());
        }
        assertEquals(inBuilder.getDividend().getPaymentDate(),
                     event.getPaymentDate());
        assertEquals(inBuilder.getDividend().getRecordDate(),
                     event.getRecordDate());
        assertEquals(inBuilder.getDividend().getSource(),
                     event.getSource());
        assertEquals(inBuilder.getDividend().getStatus(),
                     event.getStatus());
        // there's a special case for timestamp, too
        if(inBuilder.getDividend().getTimestamp() == null) {
            assertNotNull(event.getTimestamp());
            assertEquals(event.getTimestamp().getTime(),
                         event.getTimeMillis());
        } else {
            assertEquals(inBuilder.getDividend().getTimestamp(),
                         event.getTimestamp());
            assertEquals(inBuilder.getDividend().getTimeMillis(),
                         event.getTimeMillis());
        }
        assertEquals(inBuilder.getDividend().getType(),
                     event.getType());
        Object newSource = new Object();
        event.setSource(newSource);
        assertEquals(newSource,
                     event.getSource());
        return event;
    }
    /**
     * Sets valid defaults in the given builder.
     * 
     * @param inBuilder a <code>DividendEventBuilder</code> value
     * @return a <code>DividendEventBuilder</code> value
     * @throws Exception if an unexpected error occurs
     */
    private DividendEventBuilder setDefaults(DividendEventBuilder inBuilder)
            throws Exception
    {
        long timestampMillis = System.currentTimeMillis();
        inBuilder.withAmount(BigDecimal.ONE);
        inBuilder.withCurrency("US Dollars");
        inBuilder.withDeclareDate(DateUtils.dateToString(new Date(timestampMillis + (1000 * 60 * 60 * 24)*1)));
        inBuilder.withEquity(equity);
        inBuilder.withEventType(EventType.UPDATE_FINAL);
        inBuilder.withExecutionDate(DateUtils.dateToString(new Date(timestampMillis + (1000 * 60 * 60 * 24)*2)));
        inBuilder.withFrequency(DividendFrequency.ANNUALLY);
        inBuilder.withMessageId(idCounter.incrementAndGet());
        inBuilder.withPaymentDate(DateUtils.dateToString(new Date(timestampMillis + (1000 * 60 * 60 * 24)*3)));
        inBuilder.withRecordDate(DateUtils.dateToString(new Date(timestampMillis + (1000 * 60 * 60 * 24)*4)));
        inBuilder.withSource(this);
        inBuilder.withStatus(DividendStatus.OFFICIAL);
        inBuilder.withTimestamp(new Date(timestampMillis + (1000 * 60 * 60 * 24)*5));
        inBuilder.withType(DividendType.CURRENT);
        return inBuilder;
    }
    /**
     * Gets the builder to use for testing.
     *
     * @return a <code>DividendEventBuilder</code> value
     */
    private DividendEventBuilder getBuilder()
    {
        return DividendEventBuilder.dividend();
    }
    /**
     * test instrument
     */
    private Equity equity = new Equity("METC");
    /**
     * id counter used to guarantee unique events
     */
    private static final AtomicLong idCounter = new AtomicLong(0);
}
