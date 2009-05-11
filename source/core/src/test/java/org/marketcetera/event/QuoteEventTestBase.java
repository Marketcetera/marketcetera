package org.marketcetera.event;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.TEN;
import static java.math.BigDecimal.ZERO;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.marketcetera.event.QuoteEvent.Action.ADD;
import static org.marketcetera.event.QuoteEvent.Action.CHANGE;
import static org.marketcetera.event.QuoteEvent.Action.DELETE;
import static org.marketcetera.event.QuoteEvent.BookPriceComparator.AskComparator;
import static org.marketcetera.event.QuoteEvent.BookPriceComparator.BidComparator;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;

import org.junit.BeforeClass;
import org.junit.Test;
import org.marketcetera.core.LoggerConfiguration;
import org.marketcetera.event.QuoteEvent.Action;
import org.marketcetera.event.QuoteEvent.PriceAndSizeComparator;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.trade.MSymbol;
import org.marketcetera.util.log.SLF4JLoggerProxy;

/* $License$ */

/**
 * Provides testing services for subclasses of {@link QuoteEvent}.
 * 
 * <p>To test a subclass of {@link QuoteEvent}, create a mirror subclass of
 * <code>QuoteEventTestBase</code>.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.5.0
 */
public abstract class QuoteEventTestBase
{
    /**
     * Executed once before all tests.
     *
     * @throws Exception if an error occurs
     */
    @BeforeClass
    public static void once()
        throws Exception
    {
        LoggerConfiguration.logSetup();
    }
    /**
     * Tests construction of the event with a variety of inputs.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void construction()
        throws Exception
    {
        final long[] longValues = new long[] { -1l, 0l, 1l };
        final MSymbol[] symbols = new MSymbol[] { null, new MSymbol("METC") };
        final String[] exchanges = new String[] { null, "", "Q" };
        final BigDecimal[] bigValues = new BigDecimal[] { null, new BigDecimal("-1"), ZERO, TEN };
        // note that there are currently no restrictions on values < 0, not sure enough to impose that restriction yet
        for(int idCounter=0;idCounter<longValues.length;idCounter++) {
            for(int timestampCounter=0;timestampCounter<longValues.length;timestampCounter++) {
                for(int newTimestampCounter=0;newTimestampCounter<longValues.length;newTimestampCounter++) {
                    for(int symbolCounter=0;symbolCounter<symbols.length;symbolCounter++) {
                        for(int exchangeCounter=0;exchangeCounter<exchanges.length;exchangeCounter++) {
                            for(int priceCounter=0;priceCounter<bigValues.length;priceCounter++) {
                                for(int sizeCounter=0;sizeCounter<bigValues.length;sizeCounter++) {
                                    for(int newSizeCounter=0;newSizeCounter<bigValues.length;newSizeCounter++) {
                                        final long id = longValues[idCounter];
                                        final long timestamp = longValues[timestampCounter];
                                        final long newTimestamp = longValues[newTimestampCounter];
                                        final MSymbol symbol = symbols[symbolCounter];
                                        final String exchange = exchanges[exchangeCounter];
                                        final BigDecimal price = bigValues[priceCounter];
                                        final BigDecimal size = bigValues[sizeCounter];
                                        final BigDecimal newSize = bigValues[newSizeCounter];
                                        SLF4JLoggerProxy.debug(QuoteEventTestBase.class,
                                                               "{} {} {} {} {} {} {}",
                                                               id,
                                                               timestamp,
                                                               newTimestamp,
                                                               symbol,
                                                               exchange,
                                                               price,
                                                               size,
                                                               newSize);
                                        if(id < 0 ||
                                           timestamp < 0) {
                                            new ExpectedFailure<IllegalArgumentException>(null) {
                                                @Override
                                                protected void run()
                                                    throws Exception
                                                {
                                                    constructObject(id,
                                                                    timestamp,
                                                                    symbol,
                                                                    exchange,
                                                                    price,
                                                                    size);
                                                }
                                            };
                                            continue;
                                        }
                                        if(symbol == null ||
                                           exchange == null ||
                                           price == null ||
                                           size == null) {
                                            new ExpectedFailure<NullPointerException>(null) {
                                                @Override
                                                protected void run()
                                                    throws Exception
                                                {
                                                    constructObject(id,
                                                                    timestamp,
                                                                    symbol,
                                                                    exchange,
                                                                    price,
                                                                    size);
                                                }
                                            };
                                            continue;
                                        }
                                        if(exchange.isEmpty()) {
                                            new ExpectedFailure<IllegalArgumentException>(null) {
                                                @Override
                                                protected void run()
                                                    throws Exception
                                                {
                                                    constructObject(id,
                                                                    timestamp,
                                                                    symbol,
                                                                    exchange,
                                                                    price,
                                                                    size);
                                                }
                                            };
                                            continue;
                                        }
                                        final QuoteEvent event = constructObject(id,
                                                                                 timestamp,
                                                                                 symbol,
                                                                                 exchange,
                                                                                 price,
                                                                                 size);
                                        verifyEvent(event,
                                                    id,
                                                    timestamp,
                                                    symbol,
                                                    exchange,
                                                    price,
                                                    size,
                                                    getEventType(),
                                                    ADD);
                                        QuoteEvent deleteEvent = deleteEvent(event); 
                                        verifyEvent(deleteEvent,
                                                    id,
                                                    timestamp,
                                                    symbol,
                                                    exchange,
                                                    price,
                                                    size,
                                                    getEventType(),
                                                    DELETE);
                                        verifyEvent(deleteEvent(deleteEvent),
                                                    id,
                                                    timestamp,
                                                    symbol,
                                                    exchange,
                                                    price,
                                                    size,
                                                    getEventType(),
                                                    DELETE);
                                        QuoteEvent addEvent = addEvent(event); 
                                        verifyEvent(addEvent,
                                                    id,
                                                    timestamp,
                                                    symbol,
                                                    exchange,
                                                    price,
                                                    size,
                                                    getEventType(),
                                                    ADD);
                                        verifyEvent(addEvent(addEvent),
                                                    id,
                                                    timestamp,
                                                    symbol,
                                                    exchange,
                                                    price,
                                                    size,
                                                    getEventType(),
                                                    ADD);
                                        if(newSize == null) {
                                            new ExpectedFailure<NullPointerException>(null) {
                                                @Override
                                                protected void run()
                                                    throws Exception
                                                {
                                                    verifyEvent(changeEvent(event,
                                                                            timestamp,
                                                                            newSize),
                                                                            id,
                                                                            timestamp,
                                                                            symbol,
                                                                            exchange,
                                                                            price,
                                                                            size,
                                                                            getEventType(),
                                                                            CHANGE);
                                                }
                                            };
                                            continue;
                                        }
                                        if(newTimestamp < 0) {
                                            new ExpectedFailure<IllegalArgumentException>(null) {
                                                @Override
                                                protected void run()
                                                    throws Exception
                                                {
                                                    changeEvent(event,
                                                                newTimestamp,
                                                                newSize);                                                }
                                            };
                                            continue;
                                        }
                                        QuoteEvent changeEvent = changeEvent(event,
                                                                             newTimestamp,
                                                                             newSize);
                                        verifyEvent(changeEvent,
                                                    id,
                                                    newTimestamp,
                                                    symbol,
                                                    exchange,
                                                    price,
                                                    newSize,
                                                    getEventType(),
                                                    CHANGE);
                                        verifyEvent(changeEvent(changeEvent,
                                                                newTimestamp,
                                                                newSize),
                                                                id,
                                                                newTimestamp,
                                                                symbol,
                                                                exchange,
                                                                price,
                                                                newSize,
                                                                getEventType(),
                                                                CHANGE);
                                    }
                                }
                            }                            
                        }
                    }
                }
            }
        }
    }
    /**
     * Tests the detection of a null action.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void action()
        throws Exception
    {
        new ExpectedFailure<NullPointerException>(null) {
            @Override
            protected void run()
                throws Exception
            {
                new MockEvent(null);
            }
        };
    }
    /**
     * Tests the ability to create a DELETE event from an event.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void deleteEvent()
        throws Exception
    {
        new ExpectedFailure<NullPointerException>(null) {
            @Override
            protected void run()
                throws Exception
            {
                QuoteEvent.deleteEvent(null);
            }
        };
        // delete from add
        QuoteEvent event = constructObject(System.nanoTime(),
                                           System.currentTimeMillis(),
                                           new MSymbol("metc"),
                                           "Q",
                                           ONE,
                                           TEN);
        assertEquals(ADD,
                     event.getAction());
        verifyEvent(QuoteEvent.deleteEvent(event),
                    event.getMessageId(),
                    event.getTimeMillis(),
                    event.getSymbol(),
                    event.getExchange(),
                    event.getPrice(),
                    event.getSize(),
                    event.getClass(),
                    DELETE);
        // delete from change
        event = changeEvent(event,
                            event.getTimeMillis(),
                            ONE);
        assertEquals(CHANGE,
                     event.getAction());
        verifyEvent(QuoteEvent.deleteEvent(event),
                    event.getMessageId(),
                    event.getTimeMillis(),
                    event.getSymbol(),
                    event.getExchange(),
                    event.getPrice(),
                    event.getSize(),
                    event.getClass(),
                    DELETE);
        // delete from delete
        event = deleteEvent(event);
        assertEquals(DELETE,
                     event.getAction());
        verifyEvent(QuoteEvent.deleteEvent(event),
                    event.getMessageId(),
                    event.getTimeMillis(),
                    event.getSymbol(),
                    event.getExchange(),
                    event.getPrice(),
                    event.getSize(),
                    event.getClass(),
                    DELETE);
    }
    public void addEvent()
        throws Exception
    {
        new ExpectedFailure<NullPointerException>(null) {
            @Override
            protected void run()
            throws Exception
            {
                QuoteEvent.addEvent(null);
            }
        };
        // add from add
        QuoteEvent event = constructObject(System.nanoTime(),
                                           System.currentTimeMillis(),
                                           new MSymbol("metc"),
                                           "Q",
                                           ONE,
                                           TEN);
        assertEquals(ADD,
                     event.getAction());
        verifyEvent(QuoteEvent.addEvent(event),
                    event.getMessageId(),
                    event.getTimeMillis(),
                    event.getSymbol(),
                    event.getExchange(),
                    event.getPrice(),
                    event.getSize(),
                    event.getClass(),
                    ADD);
        // add from change
        event = changeEvent(event,
                            event.getTimeMillis(),
                            ONE);
        assertEquals(CHANGE,
                     event.getAction());
        verifyEvent(QuoteEvent.addEvent(event),
                    event.getMessageId(),
                    event.getTimeMillis(),
                    event.getSymbol(),
                    event.getExchange(),
                    event.getPrice(),
                    event.getSize(),
                    event.getClass(),
                    ADD);
        // add from delete
        event = deleteEvent(event);
        assertEquals(DELETE,
                     event.getAction());
        verifyEvent(QuoteEvent.addEvent(event),
                    event.getMessageId(),
                    event.getTimeMillis(),
                    event.getSymbol(),
                    event.getExchange(),
                    event.getPrice(),
                    event.getSize(),
                    event.getClass(),
                    ADD);
    }
    /**
     * Tests {@link QuoteEvent.BookPriceComparator}.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void bookPriceComparator()
        throws Exception
    {
        final QuoteEvent quote1 = constructObject(System.nanoTime(),
                                                  System.currentTimeMillis(),
                                                  new MSymbol("metc"),
                                                  "Q",
                                                  ONE,
                                                  TEN);
        // check nulls first
        new ExpectedFailure<NullPointerException>(null) {
            @Override
            protected void run()
                throws Exception
            {
                AskComparator.compare(null,
                                      quote1);
            }
        };
        new ExpectedFailure<NullPointerException>(null) {
            @Override
            protected void run()
                throws Exception
            {
                BidComparator.compare(null,
                                      quote1);
            }
        };
        new ExpectedFailure<NullPointerException>(null) {
            @Override
            protected void run()
                throws Exception
            {
                AskComparator.compare(quote1,
                                      null);
            }
        };
        new ExpectedFailure<NullPointerException>(null) {
            @Override
            protected void run()
                throws Exception
            {
                BidComparator.compare(quote1,
                                      null);
            }
        };
        // this null-only case is OK
        assertEquals(0,
                     AskComparator.compare(null,
                                           null));
        assertEquals(0,
                     BidComparator.compare(null,
                                           null));
        // comparator works on price then timestamp
        QuoteEvent quote2 = constructObject(System.nanoTime(),
                                            System.currentTimeMillis(),
                                            new MSymbol("metc"),
                                            "Q",
                                            quote1.getPrice().add(ONE),
                                            TEN);
        assertTrue(quote2.getPrice().intValue() > quote1.getPrice().intValue());
        // e1 price < e2 price (remember that bid and ask are opposite sorts)
        assertEquals(-1,
                     AskComparator.compare(quote1,
                                           quote2));
        assertEquals(1,
                     BidComparator.compare(quote1,
                                           quote2));
        // invert the test
        assertEquals(1,
                     AskComparator.compare(quote2,
                                           quote1));
        assertEquals(-1,
                     BidComparator.compare(quote2,
                                           quote1));
        // e1 price == e2 price (and timestamp)
        quote2 = constructObject(System.nanoTime(),
                                 quote1.getTimeMillis(),
                                 new MSymbol("metc"),
                                 "Q",
                                 quote1.getPrice(),
                                 TEN);
        assertEquals(quote1.getPrice(),
                     quote2.getPrice());
        assertEquals(0,
                     AskComparator.compare(quote1,
                                           quote2));
        assertEquals(0,
                     BidComparator.compare(quote1,
                                           quote2));
        // invert the test
        assertEquals(0,
                     AskComparator.compare(quote2,
                                           quote1));
        assertEquals(0,
                     BidComparator.compare(quote2,
                                           quote1));
        // e1 timestamp < e2 timestamp
        quote2 = constructObject(System.nanoTime(),
                                 quote1.getTimeMillis() + 1000,
                                 new MSymbol("metc"),
                                 "Q",
                                 quote1.getPrice(),
                                 TEN);
        assertEquals(quote1.getPrice(),
                     quote2.getPrice());
        assertTrue(quote1.getTimeMillis() < quote2.getTimeMillis());
        assertEquals(-1,
                     AskComparator.compare(quote1,
                                           quote2));
        assertEquals(1,
                     BidComparator.compare(quote1,
                                           quote2));
        // invert the test
        assertEquals(1,
                     AskComparator.compare(quote2,
                                           quote1));
        assertEquals(-1,
                     BidComparator.compare(quote2,
                                           quote1));
    }
    /**
     * Tests {@link QuoteEvent.PriceAndSizeComparator}.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void priceAndSizeComparator()
        throws Exception
    {
        final QuoteEvent quote1 = constructObject(System.nanoTime(),
                                                  System.currentTimeMillis(),
                                                  new MSymbol("metc"),
                                                  "Q",
                                                  ONE,
                                                  TEN);
        // check nulls first
        new ExpectedFailure<NullPointerException>(null) {
            @Override
            protected void run()
                throws Exception
            {
                PriceAndSizeComparator.instance.compare(null,
                                                        quote1);
            }
        };
        new ExpectedFailure<NullPointerException>(null) {
            @Override
            protected void run()
                throws Exception
            {
                PriceAndSizeComparator.instance.compare(quote1,
                                                        null);
            }
        };
        // this null-only case is OK
        assertEquals(0,
                     PriceAndSizeComparator.instance.compare(null,
                                                             null));
        // comparator works on price then size
        QuoteEvent quote2 = constructObject(System.nanoTime(),
                                            System.currentTimeMillis(),
                                            new MSymbol("metc"),
                                            "Q",
                                            quote1.getPrice().add(ONE),
                                            TEN);
        assertTrue(quote2.getPrice().intValue() > quote1.getPrice().intValue());
        // e1 price < e2 price (remember that bid and ask are opposite sorts)
        assertEquals(-1,
                     PriceAndSizeComparator.instance.compare(quote1,
                                                             quote2));
        // invert the test
        assertEquals(1,
                     PriceAndSizeComparator.instance.compare(quote2,
                                                             quote1));
        // e1 price == e2 price (and size)
        quote2 = constructObject(System.nanoTime(),
                                 System.currentTimeMillis(),
                                 new MSymbol("metc"),
                                 "Q",
                                 quote1.getPrice(),
                                 quote1.getSize());
        assertEquals(quote1.getPrice(),
                     quote2.getPrice());
        assertEquals(quote1.getSize(),
                     quote2.getSize());
        assertEquals(0,
                     PriceAndSizeComparator.instance.compare(quote1,
                                                             quote2));
        // invert the test
        assertEquals(0,
                     PriceAndSizeComparator.instance.compare(quote2,
                                                             quote1));
        // e1 size < e2 size
        quote2 = constructObject(System.nanoTime(),
                                 System.currentTimeMillis(),
                                 new MSymbol("metc"),
                                 "Q",
                                 quote1.getPrice(),
                                 quote1.getSize().add(TEN));
        assertEquals(quote1.getPrice(),
                     quote2.getPrice());
        assertTrue(quote2.getSize().intValue() > quote1.getSize().intValue());
        assertEquals(-1,
                     PriceAndSizeComparator.instance.compare(quote1,
                                                             quote2));
        // invert the test
        assertEquals(1,
                     PriceAndSizeComparator.instance.compare(quote2,
                                                             quote1));
    }
    /**
     * Verifies that the given event matches the expected values.
     *
     * @param inActualEvent a <code>QuoteEvent</code> value
     * @param inExpectedId a <code>long</code> value
     * @param inExpectedTimestamp a <code>long</code> value
     * @param inExpectedSymbol an <code>MSymbol</code> value
     * @param inExpectedExchange a <code>String</code> value
     * @param inExpectedPrice a <code>BigDecimal</code> value
     * @param inExpectedSize a <code>BigDecimal</code> value
     * @param inExpectedType a <code>Class&lt;? extends QuoteEvent&gt;</code> value
     * @param inExpectedAction an <code>Action</code> value
     * @throws Exception if an error occurs
     */
    private void verifyEvent(QuoteEvent inActualEvent,
                             long inExpectedId,
                             long inExpectedTimestamp,
                             MSymbol inExpectedSymbol,
                             String inExpectedExchange,
                             BigDecimal inExpectedPrice,
                             BigDecimal inExpectedSize,
                             Class<? extends QuoteEvent> inExpectedType,
                             Action inExpectedAction)
        throws Exception
    {
        assertEquals(inExpectedId,
                     inActualEvent.getMessageId());
        assertEquals(inExpectedTimestamp,
                     inActualEvent.getTimeMillis());
        assertNotNull(inActualEvent.getTimestampAsDate());
        assertEquals(inExpectedSymbol,
                     inActualEvent.getSymbol());
        assertEquals(inExpectedExchange,
                     inActualEvent.getExchange());
        assertEquals(inExpectedType,
                     inActualEvent.getClass());
        assertEquals(inExpectedAction,
                     inActualEvent.getAction());
        assertNotNull(inActualEvent.toString());
        assertEquals(inExpectedPrice,
                     inActualEvent.getPrice());
        assertEquals(inExpectedSize,
                     inActualEvent.getSize());
    }
    /**
     * Gets the type of object to test.
     *
     * @return a <code>Class&lt;? extends QuoteEvent&gt;</code> value
     */
    protected abstract Class<? extends QuoteEvent> getEventType();
    /**
     * Creates an event of {@link Action#DELETE} action for the given event. 
     *
     * @param inEvent a <code>QuoteEvent</code> value
     * @return a <code>QuoteEvent</code>
     * @throws Exception if an error occurs
     */
    private QuoteEvent deleteEvent(QuoteEvent inEvent)
        throws Exception
    {
        return QuoteEvent.deleteEvent(inEvent);        
    }
    /**
     * Creates an event of {@link Action#ADD} action for the given event. 
     *
     * @param inEvent a <code>QuoteEvent</code> value
     * @return a <code>QuoteEvent</code>
     * @throws Exception if an error occurs
     */
    private QuoteEvent addEvent(QuoteEvent inEvent)
        throws Exception
    {
        return QuoteEvent.addEvent(inEvent);        
    }
    /**
     * Creates an event of {@link Action#CHANGE} action for the given event. 
     *
     * @param inEvent a <code>QuoteEvent</code> value
     * @param inTimestamp a <code>long</code> value
     * @param inNewSize a <code>BigDecimal</code> value
     * @return a <code>QuoteEvent</code>
     * @throws Exception if an error occurs
     */
    private QuoteEvent changeEvent(QuoteEvent inEvent,
                                   long inTimestamp,
                                   BigDecimal inNewSize)
        throws Exception
    {
        return QuoteEvent.changeEvent(inEvent,
                                      inTimestamp,
                                      inNewSize);
    }
    /**
     * Constructs a new object of the type to test.
     *
     * @param inParameters an <code>Object...</code> value containing the parameters to the constructor
     * @return a <code>QuoteEvent</code> value
     * @throws Exception if an error occurs
     */
    private QuoteEvent constructObject(Object...inParameters)
        throws Exception
    {
        Constructor<?>[] constructors = getEventType().getConstructors();
        assertEquals("This method is no longer reliable - it depends on there being one public constructor for " + getEventType(),
                     1,
                     constructors.length);
        try {
            return (QuoteEvent)constructors[0].newInstance(inParameters);
        } catch (InvocationTargetException e) {
            throw (Exception)e.getCause();
        }
    }
    /**
     * Test subclass of {@link QuoteEvent}.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 1.5.0
     */
    private static class MockEvent
        extends QuoteEvent
    {
        /**
         * Create a new MockEvent instance.
         *
         * @param inAction an <code>Action</code> value
         */
        protected MockEvent(Action inAction)
        {
            super(System.nanoTime(),
                  System.currentTimeMillis(),
                  new MSymbol("METC"),
                  "Q",
                  ONE,
                  ONE,
                  inAction);
        }
        /* (non-Javadoc)
         * @see org.marketcetera.event.QuoteEvent#getDescription()
         */
        @Override
        protected String getDescription()
        {
            return "Mock";
        }
        private static final long serialVersionUID = 1L;
    }
}
