package org.marketcetera.event.util;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.TEN;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.marketcetera.event.util.BookPriceComparator.askComparator;
import static org.marketcetera.event.util.BookPriceComparator.bidComparator;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.marketcetera.event.QuoteEvent;
import org.marketcetera.event.impl.QuoteEventBuilder;
import org.marketcetera.marketdata.DateUtils;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.options.ExpirationType;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.OptionType;

/* $License$ */

/**
 * Tests {@link BookPriceComparator} and {@link PriceAndSizeComparator}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.0.0
 */
public class PriceComparatorsTest
{
    /**
     * Run before each test.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Before
    public void before()
            throws Exception
    {
        doOption = false;
    }
    /**
     * Tests {@link BookPriceComparator} and {@link PriceAndSizeComparator}.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void testComparators()
        throws Exception
    {
        Option option = new Option("METC",
                                   "201001",
                                   BigDecimal.TEN,
                                   OptionType.Put);
        Equity equity = new Equity("METC");
        doBookPriceComparatorTest(QuoteEventBuilder.equityAskEvent(),
                                  equity);
        doBookPriceComparatorTest(QuoteEventBuilder.equityBidEvent(),
                                  equity);
        doPriceAndSizeComparatorTest(QuoteEventBuilder.equityAskEvent(),
                                     equity);
        doPriceAndSizeComparatorTest(QuoteEventBuilder.equityBidEvent(),
                                     equity);
        doOption = true;
        doBookPriceComparatorTest(QuoteEventBuilder.optionAskEvent(),
                                  option);
        doBookPriceComparatorTest(QuoteEventBuilder.optionBidEvent(),
                                  option);
        doPriceAndSizeComparatorTest(QuoteEventBuilder.optionAskEvent(),
                                     option);
        doPriceAndSizeComparatorTest(QuoteEventBuilder.optionBidEvent(),
                                     option);
    }
    /**
     * Tests {@link PriceAndSizeComparator}.
     *
     * @param inBuilder a <code>QuoteEventBuilder&lt;E&gt;</code> value
     * @param inBuilder an <code>Instrument</code> value
     * @throws Exception if an unexpected error occurs
     */
    private <E extends QuoteEvent> void doPriceAndSizeComparatorTest(QuoteEventBuilder<E> inBuilder,
                                                                     Instrument inInstrument)
        throws Exception
    {
        final E quote1 = doOption(inBuilder).withMessageId(System.nanoTime())
                                            .withTimestamp(new Date())
                                            .withQuoteDate(DateUtils.dateToString(new Date()))
                                            .withInstrument(inInstrument)
                                            .withExchange("Q")
                                            .withPrice(ONE)
                                            .withSize(TEN).create();
        // check nulls first
        new ExpectedFailure<NullPointerException>() {
            @Override
            protected void run()
                throws Exception
            {
                PriceAndSizeComparator.instance.compare(null,
                                                        quote1);
            }
        };
        new ExpectedFailure<NullPointerException>() {
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
        E quote2 = doOption(inBuilder).withMessageId(System.nanoTime())
                                      .withTimestamp(new Date())
                                      .withQuoteDate(DateUtils.dateToString(new Date()))
                                      .withInstrument(inInstrument)
                                      .withExchange("Q")
                                      .withPrice(quote1.getPrice().add(ONE))
                                      .withSize(TEN).create();
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
        quote2 = doOption(inBuilder).withMessageId(System.nanoTime())
                                    .withTimestamp(new Date())
                                    .withQuoteDate(DateUtils.dateToString(new Date()))
                                    .withInstrument(inInstrument)
                                    .withExchange("Q")
                                    .withPrice(quote1.getPrice())
                                    .withSize(quote1.getSize()).create();
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
        quote2 = doOption(inBuilder).withMessageId(System.nanoTime())
                                    .withTimestamp(new Date())
                                    .withQuoteDate(DateUtils.dateToString(new Date()))
                                    .withInstrument(inInstrument)
                                    .withExchange("Q")
                                    .withPrice(quote1.getPrice())
                                    .withSize(quote1.getSize().add(TEN)).create();
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
     * Tests {@link BookPriceComparator}.
     *
     * @param inBuilder a <code>QuoteEventBuilder&lt;E&gt;</code> value
     * @param inBuilder an <code>Instrument</code> value
     * @throws Exception if an unexpected error occurs
     */
    private <E extends QuoteEvent> void doBookPriceComparatorTest(QuoteEventBuilder<E> inBuilder,
                                                                  Instrument inInstrument)
            throws Exception
    {
        final E quote1 = doOption(inBuilder).withMessageId(System.nanoTime())
                                            .withTimestamp(new Date())
                                            .withQuoteDate(DateUtils.dateToString(new Date()))
                                            .withInstrument(inInstrument)
                                            .withExchange("Q")
                                            .withPrice(ONE)
                                            .withSize(TEN).create();
        // check nulls first
        new ExpectedFailure<NullPointerException>() {
            @Override
            protected void run()
                    throws Exception
            {
                askComparator.compare(null,
                                      quote1);
            }
        };
        new ExpectedFailure<NullPointerException>() {
            @Override
            protected void run()
            throws Exception
            {
                bidComparator.compare(null,
                                      quote1);
            }
        };
        new ExpectedFailure<NullPointerException>() {
            @Override
            protected void run()
            throws Exception
            {
                askComparator.compare(quote1,
                                      null);
            }
        };
        new ExpectedFailure<NullPointerException>() {
            @Override
            protected void run()
            throws Exception
            {
                bidComparator.compare(quote1,
                                      null);
            }
        };
        // this null-only case is OK
        assertEquals(0,
                     askComparator.compare(null,
                                           null));
        assertEquals(0,
                     bidComparator.compare(null,
                                           null));
        // comparator works on price then timestamp
        E quote2 = doOption(inBuilder).withMessageId(System.nanoTime())
                                      .withTimestamp(new Date())
                                      .withQuoteDate(DateUtils.dateToString(new Date()))
                                      .withInstrument(inInstrument)
                                      .withExchange("Q")
                                      .withPrice(quote1.getPrice().add(ONE))
                                      .withSize(TEN).create();
        assertTrue("Expected " + quote2.getPrice() + " > " + quote1.getPrice(),
                   quote2.getPrice().intValue() > quote1.getPrice().intValue());
        // e1 price < e2 price (remember that bid and ask are opposite sorts)
        assertEquals(-1,
                     askComparator.compare(quote1,
                                           quote2));
        assertEquals(1,
                     bidComparator.compare(quote1,
                                           quote2));
        // invert the test
        assertEquals(1,
                     askComparator.compare(quote2,
                                           quote1));
        assertEquals(-1,
                     bidComparator.compare(quote2,
                                           quote1));
        // e1 price == e2 price (and timestamp)
        quote2 = doOption(inBuilder).withMessageId(System.nanoTime())
                                    .withTimestamp(quote1.getTimestamp())
                                    .withQuoteDate(DateUtils.dateToString(new Date()))
                                    .withInstrument(inInstrument)
                                    .withExchange("Q")
                                    .withPrice(quote1.getPrice())
                                    .withSize(TEN).create();
        assertEquals(quote1.getPrice(),
                     quote2.getPrice());
        assertEquals(0,
                     askComparator.compare(quote1,
                                           quote2));
        assertEquals(0,
                     bidComparator.compare(quote1,
                                           quote2));
        // invert the test
        assertEquals(0,
                     askComparator.compare(quote2,
                                           quote1));
        assertEquals(0,
                     bidComparator.compare(quote2,
                                           quote1));
        // e1 timestamp < e2 timestamp
        quote2 = doOption(inBuilder).withMessageId(System.nanoTime())
                                    .withTimestamp(new Date(quote1.getTimeMillis() + 1000))
                                    .withQuoteDate(DateUtils.dateToString(new Date()))
                                    .withInstrument(inInstrument)
                                    .withExchange("Q")
                                    .withPrice(quote1.getPrice())
                                    .withSize(TEN).create();
        assertEquals(quote1.getPrice(),
                     quote2.getPrice());
        assertTrue(quote1.getTimeMillis() < quote2.getTimeMillis());
        assertEquals(-1,
                     askComparator.compare(quote1,
                                           quote2));
        assertEquals(1,
                     bidComparator.compare(quote1,
                                           quote2));
        // invert the test
        assertEquals(1,
                     askComparator.compare(quote2,
                                           quote1));
        assertEquals(-1,
                     bidComparator.compare(quote2,
                                           quote1));
    }
    /**
     * Modifies the given <code>QuoteEventBuilder</code> with option attributes if necessary. 
     *
     * @param inBuilder a <code>QuoteEventBuilder&lt;? extends QuoteEvent&gt;</code> value
     * @return a <code>QuoteEventBuilder&lt;? extends QuoteEvent&gt;</code> value
     */
    private <E extends QuoteEvent> QuoteEventBuilder<E> doOption(QuoteEventBuilder<E> inBuilder)
    {
        if(doOption) {
             return inBuilder.withExpirationType(ExpirationType.AMERICAN)
                             .withMultiplier(BigDecimal.ZERO)
                             .withUnderlyingInstrument(new Equity("METC"));
        }
        return inBuilder;
    }
    /**
     * if set, for some tests, will cause option characteristics to be activated
     */
    private boolean doOption = false;
}
