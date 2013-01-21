package org.marketcetera.marketdata.csv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.marketcetera.marketdata.csv.Messages.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;
import org.marketcetera.core.CoreException;
import org.marketcetera.event.*;
import org.marketcetera.marketdata.DateUtils;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.MarketDataRequestBuilder;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.options.ExpirationType;
import org.marketcetera.options.OptionUtils;
import org.marketcetera.trade.*;

/* $License$ */

/**
 * Tests {@link BasicCSVFeedEventTranslator}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.1.0
 */
public class BasicCSVFeedEventTranslatorTest
{
    /**
     * Tests the ability of the translator to parse <code>BigDecimal</code> values.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGuessBigDecimal()
            throws Exception
    {
        assertNull(translator.guessBigDecimal(null,
                                              0));
        assertNull(translator.guessBigDecimal(CSVQuantum.getQuantum(new String[] { "" },
                                                                    request,
                                                                    1.0),
                                              0));
        // invalid chunk
        new ExpectedFailure<CoreException>(CANNOT_GUESS_BIG_DECIMAL,
                                           "this-is-not-a-number") {
            @Override
            protected void run()
                    throws Exception
            {
                translator.guessBigDecimal(CSVQuantum.getQuantum(new String[] { "this-is-not-a-number" },
                                                                 request,
                                                                 1.0),
                                           0);
            }
        };
        // valid chunks
        assertEquals(new BigDecimal("123.456"),
                     translator.guessBigDecimal(CSVQuantum.getQuantum(new String[] { "123.456" },
                                                                      request, 1.0),
                                                0));
        assertEquals(new BigDecimal(Long.MIN_VALUE),
                     translator.guessBigDecimal(CSVQuantum.getQuantum(new String[] { Long.toString(Long.MIN_VALUE) },
                                                                      request, 1.0),
                                                0));
        assertEquals(new BigDecimal(Long.MAX_VALUE),
                     translator.guessBigDecimal(CSVQuantum.getQuantum(new String[] { Long.toString(Long.MAX_VALUE) },
                                                                      request, 1.0),
                                                0));
    }
    /**
     * Tests the ability of the translator to parse <code>Date</code> values.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGuessDate()
            throws Exception
    {
        assertNull(translator.guessDate(null,
                                        0));
        assertNull(translator.guessDate(CSVQuantum.getQuantum(new String[] { "" },
                                                              request, 1.0),
                                        0));
        // invalid chunk
        new ExpectedFailure<CoreException>(CANNOT_GUESS_DATE,
                                           "this-is-not-a-date") {
            @Override
            protected void run()
                    throws Exception
            {
                translator.guessDate(CSVQuantum.getQuantum(new String[] { "this-is-not-a-date" },
                                                           request, 1.0),
                                     0);
            }
        };
        final Date date = new Date();
        // invalid chunk
        new ExpectedFailure<CoreException>(CANNOT_GUESS_DATE,
                                           date.toString()) {
            @Override
            protected void run()
                    throws Exception
            {
                translator.guessDate(CSVQuantum.getQuantum(new String[] { date.toString() },
                                                           request, 1.0),
                                     0);
            }
        };
        // valid chunks
        assertEquals(date,
                     translator.guessDate(CSVQuantum.getQuantum(new String[] { Long.toString(date.getTime()) },
                                                                request, 1.0),
                                          0));
        assertEquals(new Date(0),
                     translator.guessDate(CSVQuantum.getQuantum(new String[] { Long.toString(0) },
                                                                request, 1.0),
                                          0));
        assertEquals(new Date(Long.MIN_VALUE),
                     translator.guessDate(CSVQuantum.getQuantum(new String[] { Long.toString(Long.MIN_VALUE) },
                                                                request, 1.0),
                                          0));
        assertEquals(new Date(Long.MAX_VALUE),
                     translator.guessDate(CSVQuantum.getQuantum(new String[] { Long.toString(Long.MAX_VALUE) },
                                                                request, 1.0),
                                          0));
    }
    /**
     * Tests the ability to read a close date from a data line.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGuessCloseDate()
            throws Exception
    {
        assertNull(translator.guessCloseDate(null));
        assertNull(translator.guessCloseDate(CSVQuantum.getQuantum(new String[] { "" },
                                                                   request, 1.0)));
        assertEquals("nine",
                     translator.guessCloseDate(CSVQuantum.getQuantum(new String[] { "zero","one","two","three","four","five","six","seven","eight","nine","ten","eleven" },
                                                                     request, 1.0)));
    }
    /**
     * Tests the ability to read an open price from a data line.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGuessOpenPrice()
            throws Exception
    {
        assertNull(translator.guessOpenPrice(null));
        assertNull(translator.guessOpenPrice(CSVQuantum.getQuantum(new String[] { "" },
                                                                   request, 1.0)));
        new ExpectedFailure<CoreException>(CANNOT_GUESS_BIG_DECIMAL,
                                           "this-is-not-a-number") {
            @Override
            protected void run()
                    throws Exception
            {
                translator.guessOpenPrice(CSVQuantum.getQuantum(new String[] { "0","1","2","this-is-not-a-number","4" },
                                                                request, 1.0));
            }
        };
        assertEquals(new BigDecimal("3"),
                     translator.guessOpenPrice(CSVQuantum.getQuantum(new String[] { "0","1","2","3","4" },
                                                                     request, 1.0)));
    }
    /**
     * Tests the ability to read a previous close price from a data line.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGuessPreviousClosePrice()
            throws Exception
    {
        assertNull(translator.guessPreviousClosePrice(null));
        assertNull(translator.guessPreviousClosePrice(CSVQuantum.getQuantum(new String[] { "" },
                                                                            request, 1.0)));
        new ExpectedFailure<CoreException>(CANNOT_GUESS_BIG_DECIMAL,
                                           "this-is-not-a-number") {
            @Override
            protected void run()
                    throws Exception
            {
                translator.guessPreviousClosePrice(CSVQuantum.getQuantum(new String[] { "0","1","2","3","4","5","6","this-is-not-a-number" },
                                                                         request, 1.0));
            }
        };
        assertEquals(new BigDecimal("7"),
                     translator.guessPreviousClosePrice(CSVQuantum.getQuantum(new String[] { "0","1","2","3","4","5","6","7" },
                                                                              request, 1.0)));
    }
    /**
     * Tests the ability to read a volume from a data line.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGuessVolume()
            throws Exception
    {
        assertNull(translator.guessVolume(null));
        assertNull(translator.guessVolume(CSVQuantum.getQuantum(new String[] { "" },
                                                                request, 1.0)));
        new ExpectedFailure<CoreException>(CANNOT_GUESS_BIG_DECIMAL,
                                           "this-is-not-a-number") {
            @Override
            protected void run()
                    throws Exception
            {
                translator.guessVolume(CSVQuantum.getQuantum(new String[] { "0","1","2","3","4","5","6","7","this-is-not-a-number","6" },
                                                             request, 1.0));
            }
        };
        assertEquals(new BigDecimal("8"),
                     translator.guessVolume(CSVQuantum.getQuantum(new String[] { "0","1","2","3","4","5","6","7","8" },
                                                                  request, 1.0)));
    }
    /**
     * Tests the ability to read a close price from a data line.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGuessClosePrice()
            throws Exception
    {
        assertNull(translator.guessClosePrice(null));
        assertNull(translator.guessClosePrice(CSVQuantum.getQuantum(new String[] { "" },
                                                                    request, 1.0)));
        new ExpectedFailure<CoreException>(CANNOT_GUESS_BIG_DECIMAL,
                                           "this-is-not-a-number") {
            @Override
            protected void run()
                    throws Exception
            {
                translator.guessClosePrice(CSVQuantum.getQuantum(new String[] { "0","1","2","3","4","5","this-is-not-a-number","7" },
                                                                 request, 1.0));
            }
        };
        assertEquals(new BigDecimal("6"),
                     translator.guessClosePrice(CSVQuantum.getQuantum(new String[] { "0","1","2","3","4","5","6","7" },
                                                                      request, 1.0)));
    }
    /**
     * Tests the ability to read a high price from a data line.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGuessHighPrice()
            throws Exception
    {
        assertNull(translator.guessHighPrice(null));
        assertNull(translator.guessHighPrice(CSVQuantum.getQuantum(new String[] { "" },
                                                                   request, 1.0)));
        new ExpectedFailure<CoreException>(CANNOT_GUESS_BIG_DECIMAL,
                                           "this-is-not-a-number") {
            @Override
            protected void run()
                    throws Exception
            {
                translator.guessHighPrice(CSVQuantum.getQuantum(new String[] { "0","1","2","3","this-is-not-a-number","5","6" },
                                                                request, 1.0));
            }
        };
        assertEquals(new BigDecimal("4"),
                     translator.guessHighPrice(CSVQuantum.getQuantum(new String[] { "0","1","2","3","4","5","6","7" },
                                                                     request, 1.0)));
    }
    /**
     * Tests the ability to read a low price from a data line.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGuessLowPrice()
            throws Exception
    {
        assertNull(translator.guessLowPrice(null));
        assertNull(translator.guessLowPrice(CSVQuantum.getQuantum(new String[] { "" },
                                                                  request, 1.0)));
        new ExpectedFailure<CoreException>(CANNOT_GUESS_BIG_DECIMAL,
                                           "this-is-not-a-number") {
            @Override
            protected void run()
                    throws Exception
            {
                translator.guessLowPrice(CSVQuantum.getQuantum(new String[] { "0","1","2","3","4","this-is-not-a-number","6" },
                                                               request, 1.0));
            }
        };
        assertEquals(new BigDecimal("5"),
                     translator.guessLowPrice(CSVQuantum.getQuantum(new String[] { "0","1","2","3","4","5","6","7" },
                                                                    request, 1.0)));
    }
    /**
     * Tests the ability to read a previous close date from a data line.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGuessPreviousCloseDate()
            throws Exception
    {
        assertNull(translator.guessPreviousCloseDate(null));
        assertNull(translator.guessPreviousCloseDate(CSVQuantum.getQuantum(new String[] { "" },
                                                                           request, 1.0)));
        assertEquals("ten",
                     translator.guessPreviousCloseDate(CSVQuantum.getQuantum(new String[] { "zero","one","two","three","four","five","six","seven","eight","nine","ten","eleven" },
                                                                             request, 1.0)));
    }
    /**
     * Tests the ability to read a trade high time from a data line.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGuessTradeHighTime()
            throws Exception
    {
        assertNull(translator.guessTradeHighTime(null));
        assertNull(translator.guessTradeHighTime(CSVQuantum.getQuantum(new String[] { "" },
                                                                       request, 1.0)));
        assertEquals("eleven",
                     translator.guessTradeHighTime(CSVQuantum.getQuantum(new String[] { "zero","one","two","three","four","five","six","seven","eight","nine","ten","eleven","twelve" },
                                                                         request, 1.0)));
    }
    /**
     * Tests the ability to read a trade low time from a data line.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGuessTradeLowTime()
            throws Exception
    {
        assertNull(translator.guessTradeLowTime(null));
        assertNull(translator.guessTradeLowTime(CSVQuantum.getQuantum(new String[] { "" },
                                                                      request, 1.0)));
        assertEquals("twelve",
                     translator.guessTradeLowTime(CSVQuantum.getQuantum(new String[] { "zero","one","two","three","four","five","six","seven","eight","nine","ten","eleven","twelve","thirteen" },
                                                                        request, 1.0)));
    }
    /**
     * Tests the ability to read an open exchange from a data line.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGuessOpenExchange()
            throws Exception
    {
        assertNull(translator.guessOpenExchange(null));
        assertNull(translator.guessOpenExchange(CSVQuantum.getQuantum(new String[] { "" },
                                                                      request, 1.0)));
        assertEquals("thirteen",
                     translator.guessOpenExchange(CSVQuantum.getQuantum(new String[] { "zero","one","two","three","four","five","six","seven","eight","nine","ten","eleven","twelve","thirteen","fourteen" },
                                                                        request, 1.0)));
    }
    /**
     * Tests the ability to read a high exchange from a data line.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGuessHighExchange()
            throws Exception
    {
        assertNull(translator.guessHighExchange(null));
        assertNull(translator.guessHighExchange(CSVQuantum.getQuantum(new String[] { "" },
                                                                      request, 1.0)));
        assertEquals("fourteen",
                     translator.guessHighExchange(CSVQuantum.getQuantum(new String[] { "zero","one","two","three","four","five","six","seven","eight","nine","ten","eleven","twelve","thirteen","fourteen","fifteen" },
                                                                        request, 1.0)));
    }
    /**
     * Tests the ability to read a low exchange from a data line.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGuessLowExchange()
            throws Exception
    {
        assertNull(translator.guessLowExchange(null));
        assertNull(translator.guessLowExchange(CSVQuantum.getQuantum(new String[] { "" },
                                                                     request, 1.0)));
        assertEquals("fifteen",
                     translator.guessLowExchange(CSVQuantum.getQuantum(new String[] { "zero","one","two","three","four","five","six","seven","eight","nine","ten","eleven","twelve","thirteen","fourteen","fifteen","sixteen" },
                                                                       request, 1.0)));
    }
    /**
     * Tests the ability to read a close exchange from a data line.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGuessCloseExchange()
            throws Exception
    {
        assertNull(translator.guessCloseExchange(null));
        assertNull(translator.guessCloseExchange(CSVQuantum.getQuantum(new String[] { "" },
                                                                       request, 1.0)));
        assertEquals("sixteen",
                     translator.guessCloseExchange(CSVQuantum.getQuantum(new String[] { "zero","one","two","three","four","five","six","seven","eight","nine","ten","eleven","twelve","thirteen","fourteen","fifteen","sixteen","seventeen" },
                                                                         request, 1.0)));
    }
    /**
     * Tests the ability of the translator to parse <code>Date</code> values.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGuessEventTimestamp()
            throws Exception
    {
        assertNull(translator.guessEventTimestamp(null));
        assertNull(translator.guessEventTimestamp(CSVQuantum.getQuantum(new String[] { "","" },
                                                                        request, 1.0)));
        // invalid chunk
        new ExpectedFailure<CoreException>(CANNOT_GUESS_DATE,
                                           "this-is-not-a-date") {
            @Override
            protected void run()
                    throws Exception
            {
                translator.guessEventTimestamp(CSVQuantum.getQuantum(new String[] { "","this-is-not-a-date" },
                                                                     request, 1.0));
            }
        };
        final Date date = new Date();
        // invalid chunk
        new ExpectedFailure<CoreException>(CANNOT_GUESS_DATE,
                                           date.toString()) {
            @Override
            protected void run()
                    throws Exception
            {
                translator.guessEventTimestamp(CSVQuantum.getQuantum(new String[] { "",date.toString() },
                                                                     request, 1.0));
            }
        };
        // valid chunks
        assertEquals(date,
                     translator.guessEventTimestamp(CSVQuantum.getQuantum(new String[] { "",Long.toString(date.getTime()) },
                                                                          request, 1.0)));
        assertEquals(new Date(0),
                     translator.guessEventTimestamp(CSVQuantum.getQuantum(new String[] { "",Long.toString(0) },
                                                                          request, 1.0)));
        assertEquals(new Date(Long.MIN_VALUE),
                     translator.guessEventTimestamp(CSVQuantum.getQuantum(new String[] { "",Long.toString(Long.MIN_VALUE) },
                                                                          request, 1.0)));
        assertEquals(new Date(Long.MAX_VALUE),
                     translator.guessEventTimestamp(CSVQuantum.getQuantum(new String[] { "",Long.toString(Long.MAX_VALUE) },
                                                                          request, 1.0)));
    }
    /**
     * Tests the ability to read an exchange from a data line.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGuessExchange()
            throws Exception
    {
        assertNull(translator.guessExchange(null));
        assertNull(translator.guessExchange(CSVQuantum.getQuantum(new String[] { "" },
                                                                  request, 1.0)));
        assertEquals("four",
                     translator.guessExchange(CSVQuantum.getQuantum(new String[] { "zero","one","two","three","four","five","six","seven","eight","nine","ten","eleven","twelve","thirteen","fourteen","fifteen","sixteen","seventeen" },
                                                                    request, 1.0)));
    }
    /**
     * Tests the ability to read a quote action from a data line.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGuessQuoteAction()
            throws Exception
    {
        assertEquals(QuoteAction.ADD,
                     translator.guessQuoteAction(CSVQuantum.getQuantum(new String[] { "zero","one","two","three","four","five","six","seven","eight","nine","ten","eleven","twelve","thirteen","fourteen","fifteen","sixteen","seventeen" },
                                                                       request, 1.0)));
        assertEquals(QuoteAction.ADD,
                     translator.guessQuoteAction(CSVQuantum.getQuantum(new String[] { },
                                                                       request, 1.0)));
        assertEquals(QuoteAction.ADD,
                     translator.guessQuoteAction(CSVQuantum.getQuantum(null,
                                                                       request, 1.0)));
    }
    /**
     * Tests the ability to guess an instrument.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGuessInstrument()
            throws Exception
    {
        assertNull(translator.guessInstrument(null));
        assertNull(translator.guessInstrument(CSVQuantum.getQuantum(new String[] { "zero","one" },
                                                                    request, 1.0)));
        // naked equity
        assertEquals(new Equity("symbol"),
                     translator.guessInstrument(CSVQuantum.getQuantum(new String[] { "zero","one","symbol" },
                                                                 request, 1.0)));
        // naked option
        assertEquals(option,
                     translator.guessInstrument(CSVQuantum.getQuantum(new String[] { "zero","one",OptionUtils.getOsiSymbolFromOption(option) },
                                                                      request, 1.0)));
        // unsupported CFI code
        new ExpectedFailure<CoreException>(UNSUPPORTED_CFI_CODE,
                                           "D:SYMBOL",
                                           "D",
                                           BasicCSVFeedEventTranslator.SUPPORTED_CFI_CODES.toString()) {
            @Override
            protected void run()
                    throws Exception
            {
                translator.guessInstrument(CSVQuantum.getQuantum(new String[] { "zero","one","D:SYMBOL" },
                                                                 request, 1.0));
            }
        };
        // invalid CFI code
        new ExpectedFailure<CoreException>(INVALID_CFI_CODE,
                                           "Q:SYMBOL",
                                           "Q",
                                           BasicCSVFeedEventTranslator.SUPPORTED_CFI_CODES.toString()) {
            @Override
            protected void run()
                    throws Exception
            {
                translator.guessInstrument(CSVQuantum.getQuantum(new String[] { "zero","one","Q:SYMBOL" },
                                                                 request, 1.0));
            }
        };
        // CFI equity
        assertEquals(new Equity("SYMBOL"),
                     translator.guessInstrument(CSVQuantum.getQuantum(new String[] { "zero","one","E:SYMBOL" },
                                                                 request, 1.0)));
        // CFI OSI-compliant option
        assertEquals(option,
                     translator.guessInstrument(CSVQuantum.getQuantum(new String[] { "zero","one","O:" + OptionUtils.getOsiSymbolFromOption(option) },
                                                                 request, 1.0)));
        // seems to be an option, but not OSI-compliant
        new ExpectedFailure<CoreException>(NOT_OSI_COMPLIANT,
                                           "O:SYMBOL",
                                           "SYMBOL") {
            @Override
            protected void run()
                    throws Exception
            {
                translator.guessInstrument(CSVQuantum.getQuantum(new String[] { "zero","one","O:SYMBOL" },
                                                                 request, 1.0));
            }
        };
        // contains more than one ':' character
        new ExpectedFailure<CoreException>(UNKNOWN_SYMBOL_FORMAT,
                                           "X:Y:Z") {
            @Override
            protected void run()
                    throws Exception
            {
                translator.guessInstrument(CSVQuantum.getQuantum(new String[] { "zero","one","X:Y:Z" },
                                                                 request, 1.0));
            }
        };
    }
    /**
     * Tests {@link BasicCSVFeedEventTranslator#guessMessageId(CSVQuantum)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGuessMessageId()
            throws Exception
    {
        long initialValue = translator.guessMessageId(null);
        assertTrue(initialValue > 0);
        long subsequentValue = translator.guessMessageId(CSVQuantum.getQuantum(new String[] { "zero","one" },
                                                                 request, 1.0));
        assertTrue(subsequentValue > 0);
        assertFalse(initialValue == subsequentValue);
    }
    /**
     * Tests {@link BasicCSVFeedEventTranslator#guessPrice(CSVQuantum)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGuessPrice()
            throws Exception
    {
        assertNull(translator.guessPrice(null));
        assertNull(translator.guessPrice(CSVQuantum.getQuantum(new String[] { "" },
                                                               request, 1.0)));
        assertEquals(BigDecimal.ZERO,
                     translator.guessPrice(CSVQuantum.getQuantum(new String[] { "zero","one","two","three","four","0","six","seven","eight","nine","ten","eleven","twelve","thirteen","fourteen","fifteen","sixteen","seventeen" },
                                                                 request, 1.0)));
        assertEquals(new BigDecimal(Long.MIN_VALUE),
                     translator.guessPrice(CSVQuantum.getQuantum(new String[] { "zero","one","two","three","four",Long.toString(Long.MIN_VALUE),"six","seven","eight","nine","ten","eleven","twelve","thirteen","fourteen","fifteen","sixteen","seventeen" },
                                                                 request, 1.0)));
        assertEquals(new BigDecimal(Long.MAX_VALUE),
                     translator.guessPrice(CSVQuantum.getQuantum(new String[] { "zero","one","two","three","four",Long.toString(Long.MAX_VALUE),"six","seven","eight","nine","ten","eleven","twelve","thirteen","fourteen","fifteen","sixteen","seventeen" },
                                                                 request, 1.0)));
        assertEquals(new BigDecimal("123.4567"),
                     translator.guessPrice(CSVQuantum.getQuantum(new String[] { "zero","one","two","three","four","123.4567","six","seven","eight","nine","ten","eleven","twelve","thirteen","fourteen","fifteen","sixteen","seventeen" },
                                                                 request, 1.0)));
        new ExpectedFailure<CoreException>(CANNOT_GUESS_BIG_DECIMAL) {
            @Override
            protected void run()
                    throws Exception
            {
                translator.guessPrice(CSVQuantum.getQuantum(new String[] { "zero","one","two","three","four","this-is-not-a-price", },
                                                            request, 1.0));
            }
        };
    }
    /**
     * Tests {@link BasicCSVFeedEventTranslator#guessQuoteDate(CSVQuantum)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGuessQuoteDate()
            throws Exception
    {
        assertNull(translator.guessQuoteDate(null));
        assertNull(translator.guessQuoteDate(CSVQuantum.getQuantum(new String[] { "" },
                                                                   request, 1.0)));
        assertEquals("this-is-not-a-date",
                     translator.guessQuoteDate(CSVQuantum.getQuantum(new String[] { "one","two","three","this-is-not-a-date" },
                                                                     request, 1.0)));
        String dateString = DateUtils.dateToString(new Date());
        assertEquals(dateString,
                     translator.guessQuoteDate(CSVQuantum.getQuantum(new String[] { "one","two","three",dateString },
                                                                     request, 1.0)));
    }
    /**
     * Tests {@link BasicCSVFeedEventTranslator#guessSize(CSVQuantum)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGuessSize()
            throws Exception
    {
        assertNull(translator.guessSize(null));
        assertNull(translator.guessSize(CSVQuantum.getQuantum(new String[] { "" },
                                                              request, 1.0)));
        assertEquals(BigDecimal.ZERO,
                     translator.guessSize(CSVQuantum.getQuantum(new String[] { "zero","one","two","three","four","five","0","seven","eight","nine","ten","eleven","twelve","thirteen","fourteen","fifteen","sixteen","seventeen" },
                                                                request, 1.0)));
        assertEquals(new BigDecimal(Long.MIN_VALUE),
                     translator.guessSize(CSVQuantum.getQuantum(new String[] { "zero","one","two","three","four","five",Long.toString(Long.MIN_VALUE),"seven","eight","nine","ten","eleven","twelve","thirteen","fourteen","fifteen","sixteen","seventeen" },
                                                                request, 1.0)));
        assertEquals(new BigDecimal(Long.MAX_VALUE),
                     translator.guessSize(CSVQuantum.getQuantum(new String[] { "zero","one","two","three","four","five",Long.toString(Long.MAX_VALUE),"seven","eight","nine","ten","eleven","twelve","thirteen","fourteen","fifteen","sixteen","seventeen" },
                                                                request, 1.0)));
        assertEquals(new BigDecimal("123.4567"),
                     translator.guessSize(CSVQuantum.getQuantum(new String[] { "zero","one","two","three","four","five","123.4567","seven","eight","nine","ten","eleven","twelve","thirteen","fourteen","fifteen","sixteen","seventeen" },
                                                                request, 1.0)));
        new ExpectedFailure<CoreException>(CANNOT_GUESS_BIG_DECIMAL) {
            @Override
            protected void run()
                    throws Exception
            {
                translator.guessSize(CSVQuantum.getQuantum(new String[] { "zero","one","two","three","four","five","this-is-not-a-price", },
                                                           request, 1.0));
            }
        };
    }
    /**
     * Tests {@link BasicCSVFeedEventTranslator#guessExpirationType(CSVQuantum, Option)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGuessExpirationType()
            throws Exception
    {
        // not very exciting - the default action always returns a single value
        assertEquals(ExpirationType.UNKNOWN,
                     translator.guessExpirationType(null,
                                                    null));
    }
    /**
     * Tests {@link BasicCSVFeedEventTranslator#guessMultiplier(CSVQuantum, Option)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGuessMultiplier()
            throws Exception
    {
        assertEquals(BigDecimal.ONE,
                     translator.guessMultiplier(null,
                                                null));
    }
    /**
     * Tests {@link BasicCSVFeedEventTranslator#guessUnderlyingInstrument(CSVQuantum, Option)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGuessUnderlyingInstrument()
            throws Exception
    {
        new ExpectedFailure<NullPointerException>() {
            @Override
            protected void run()
                    throws Exception
            {
                translator.guessUnderlyingInstrument(null,
                                                     null);
            }
        };
        new ExpectedFailure<NullPointerException>() {
            @Override
            protected void run()
                    throws Exception
            {
                translator.guessUnderlyingInstrument(CSVQuantum.getQuantum(new String[] { },
                                                                           request, 1.0),
                                                     null);
            }
        };
        // null line is ok
        assertEquals(new Equity(option.getSymbol()),
                     translator.guessUnderlyingInstrument(null,
                                                          option));
    }
    /**
     * Tests {@link BasicCSVFeedEventTranslator#guessHasDeliverable(CSVQuantum, Option)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testHasDeliverable()
            throws Exception
    {
        assertEquals(true,
                     translator.guessHasDeliverable(null,
                                                    null));
    }
    /**
     * Tests {@link BasicCSVFeedEventTranslator#guessTradeDate(CSVQuantum)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGuessTradeDate()
            throws Exception
    {
        assertNull(translator.guessTradeDate(null));
        assertNull(translator.guessTradeDate(CSVQuantum.getQuantum(new String[] { "" },
                                                                   request, 1.0)));
        assertEquals("this-is-not-a-date",
                     translator.guessTradeDate(CSVQuantum.getQuantum(new String[] { "one","two","three","this-is-not-a-date" },
                                                                     request, 1.0)));
        String dateString = DateUtils.dateToString(new Date());
        assertEquals(dateString,
                     translator.guessTradeDate(CSVQuantum.getQuantum(new String[] { "one","two","three",dateString },
                                                                     request, 1.0)));
    }
    /**
     * Tests {@link BasicCSVFeedEventTranslator#guessDividendAmount(CSVQuantum)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGuessDividendAmount()
            throws Exception
    {
        assertNull(translator.guessDividendAmount(null));
        assertNull(translator.guessDividendAmount(CSVQuantum.getQuantum(new String[] { "" },
                                                                        request, 1.0)));
        assertEquals(BigDecimal.ZERO,
                     translator.guessDividendAmount(CSVQuantum.getQuantum(new String[] { "zero","one","two","0" },
                                                                          request, 1.0)));
        assertEquals(new BigDecimal(Long.MIN_VALUE),
                     translator.guessDividendAmount(CSVQuantum.getQuantum(new String[] { "zero","one","two",Long.toString(Long.MIN_VALUE) },
                                                                          request, 1.0)));
        assertEquals(new BigDecimal(Long.MAX_VALUE),
                     translator.guessDividendAmount(CSVQuantum.getQuantum(new String[] { "zero","one","two",Long.toString(Long.MAX_VALUE) },
                                                                          request, 1.0)));
        assertEquals(new BigDecimal("123.4567"),
                     translator.guessDividendAmount(CSVQuantum.getQuantum(new String[] { "zero","one","two","123.4567" },
                                                                          request, 1.0)));
        new ExpectedFailure<CoreException>(CANNOT_GUESS_BIG_DECIMAL) {
            @Override
            protected void run()
                    throws Exception
            {
                translator.guessDividendAmount(CSVQuantum.getQuantum(new String[] { "zero","one","two","this-is-not-a-dividend-amount", },
                                                                     request, 1.0));
            }
        };
    }
    /**
     * Tests {@link BasicCSVFeedEventTranslator#guessDividendCurrency(CSVQuantum)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGuessDividendCurrency()
            throws Exception
    {
        assertNull(translator.guessDividendCurrency(null));
        assertNull(translator.guessDividendCurrency(CSVQuantum.getQuantum(new String[] { "" },
                                                                          request, 1.0)));
        assertEquals("this-is-a-currency",
                     translator.guessDividendCurrency(CSVQuantum.getQuantum(new String[] { "zero","one","two","three","this-is-a-currency" },
                                                                            request, 1.0)));
        assertNull(translator.guessDividendCurrency(CSVQuantum.getQuantum(new String[] { "zero","one","two","three","" },
                                                                          request, 1.0)));
    }
    /**
     * Tests {@link BasicCSVFeedEventTranslator#guessDividendDeclareDate(CSVQuantum)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testDividendDeclareDate()
            throws Exception
    {
        assertNull(translator.guessDividendDeclareDate(null));
        assertNull(translator.guessDividendDeclareDate(CSVQuantum.getQuantum(new String[] { "" },
                                                                             request, 1.0)));
        assertEquals("this-is-not-a-date",
                     translator.guessDividendDeclareDate(CSVQuantum.getQuantum(new String[] { "zero","one","two","three","four","five","six","seven","eight","nine","ten","this-is-not-a-date" },
                                                                               request, 1.0)));
        String dateString = DateUtils.dateToString(new Date());
        assertEquals(dateString,
                     translator.guessDividendDeclareDate(CSVQuantum.getQuantum(new String[] { "zero","one","two","three","four","five","six","seven","eight","nine","ten",dateString },
                                                                               request, 1.0)));
    }
    /**
     * Tests {@link BasicCSVFeedEventTranslator#guessDividendExecutionDate(CSVQuantum)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGuessDividendExecutionDate()
            throws Exception
    {
        assertNull(translator.guessDividendExecutionDate(null));
        assertNull(translator.guessDividendExecutionDate(CSVQuantum.getQuantum(new String[] { "" },
                                                                               request, 1.0)));
        assertEquals("this-is-not-a-date",
                     translator.guessDividendExecutionDate(CSVQuantum.getQuantum(new String[] { "zero","one","two","three","four","five","six","seven","this-is-not-a-date" },
                                                                                 request, 1.0)));
        String dateString = DateUtils.dateToString(new Date());
        assertEquals(dateString,
                     translator.guessDividendExecutionDate(CSVQuantum.getQuantum(new String[] { "zero","one","two","three","four","five","six","seven",dateString },
                                                                                 request, 1.0)));
    }
    /**
     * Tests {@link BasicCSVFeedEventTranslator#guessDividendFrequency(CSVQuantum)}. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGuessDividendFrequency()
            throws Exception
    {
        assertNull(translator.guessDividendFrequency(null));
        assertNull(translator.guessDividendFrequency(CSVQuantum.getQuantum(new String[] { "" },
                                                                           request, 1.0)));
        new ExpectedFailure<CoreException>(CANNOT_INTERPRET_DIVIDEND_FREQUENCY) {
            @Override
            protected void run()
                    throws Exception
            {
                translator.guessDividendFrequency(CSVQuantum.getQuantum(new String[] { "zero","one","two","three","four","five","invalid-frequency" },
                                                                        request, 1.0));
            }
        };
        for(DividendFrequency frequency : DividendFrequency.values()) {
            // default case
            assertEquals(frequency,
                         translator.guessDividendFrequency(CSVQuantum.getQuantum(new String[] { "zero","one","two","three","four","five",frequency.name() },
                                                                                 request, 1.0)));
            // upper-case
            assertEquals(frequency,
                         translator.guessDividendFrequency(CSVQuantum.getQuantum(new String[] { "zero","one","two","three","four","five",frequency.name().toUpperCase() },
                                                                                 request, 1.0)));
            // lower-case
            assertEquals(frequency,
                         translator.guessDividendFrequency(CSVQuantum.getQuantum(new String[] { "zero","one","two","three","four","five",frequency.name().toLowerCase() },
                                                                                 request, 1.0)));
        }
    }
    /**
     * Tests {@link BasicCSVFeedEventTranslator#guessDividendEquity(CSVQuantum)}. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGuessDividendEquity()
            throws Exception
    {
        assertNull(translator.guessDividendEquity(null));
        assertNull(translator.guessDividendEquity(CSVQuantum.getQuantum(new String[] { "" },
                                                                        request, 1.0)));
        assertEquals(new Equity("symbol"),
                     translator.guessDividendEquity(CSVQuantum.getQuantum(new String[] { "one","two","symbol","four","five","six","seven","eight" },
                                                                          request, 1.0)));
    }
    /**
     * Tests {@link BasicCSVFeedEventTranslator#guessDividendPaymentDate(CSVQuantum)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testDividendPaymentDate()
            throws Exception
    {
        assertNull(translator.guessDividendPaymentDate(null));
        assertNull(translator.guessDividendPaymentDate(CSVQuantum.getQuantum(new String[] { "" },
                                                                             request, 1.0)));
        assertEquals("this-is-not-a-date",
                     translator.guessDividendPaymentDate(CSVQuantum.getQuantum(new String[] { "zero","one","two","three","four","five","six","seven","eight","nine","this-is-not-a-date" },
                                                                               request, 1.0)));
        String dateString = DateUtils.dateToString(new Date());
        assertEquals(dateString,
                     translator.guessDividendPaymentDate(CSVQuantum.getQuantum(new String[] { "zero","one","two","three","four","five","six","seven","eight","nine",dateString },
                                                                               request, 1.0)));
    }
    /**
     * Tests {@link BasicCSVFeedEventTranslator#guessDividendRecordDate(CSVQuantum)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testDividendRecordDate()
            throws Exception
    {
        assertNull(translator.guessDividendRecordDate(null));
        assertNull(translator.guessDividendRecordDate(CSVQuantum.getQuantum(new String[] { "" },
                                                                            request, 1.0)));
        assertEquals("this-is-not-a-date",
                     translator.guessDividendRecordDate(CSVQuantum.getQuantum(new String[] { "one","two","three","four","five","six","seven","eight","nine","this-is-not-a-date" },
                                                                               request, 1.0)));
        String dateString = DateUtils.dateToString(new Date());
        assertEquals(dateString,
                     translator.guessDividendRecordDate(CSVQuantum.getQuantum(new String[] { "one","two","three","four","five","six","seven","eight","nine",dateString },
                                                                               request, 1.0)));
    }
    /**
     * Tests {@link BasicCSVFeedEventTranslator#guessDividendStatus(CSVQuantum)}. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGuessDividendStatus()
            throws Exception
    {
        assertNull(translator.guessDividendStatus(null));
        assertNull(translator.guessDividendStatus(CSVQuantum.getQuantum(new String[] { "" },
                                                                        request, 1.0)));
        new ExpectedFailure<CoreException>(CANNOT_INTERPRET_DIVIDEND_STATUS) {
            @Override
            protected void run()
                    throws Exception
            {
                translator.guessDividendStatus(CSVQuantum.getQuantum(new String[] { "zero","one","two","three","four","five","six","invalid-status" },
                                                                        request, 1.0));
            }
        };
        for(DividendStatus status : DividendStatus.values()) {
            // default case
            assertEquals(status,
                         translator.guessDividendStatus(CSVQuantum.getQuantum(new String[] { "zero","one","two","three","four","five","six",status.name() },
                                                                                 request, 1.0)));
            // upper-case
            assertEquals(status,
                         translator.guessDividendStatus(CSVQuantum.getQuantum(new String[] { "zero","one","two","three","four","five","six",status.name().toUpperCase() },
                                                                                 request, 1.0)));
            // lower-case
            assertEquals(status,
                         translator.guessDividendStatus(CSVQuantum.getQuantum(new String[] { "zero","one","two","three","four","five","six",status.name().toLowerCase() },
                                                                                 request, 1.0)));
        }
    }
    /**
     * Tests {@link BasicCSVFeedEventTranslator#guessDividendType(CSVQuantum)}. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGuessDividendType()
            throws Exception
    {
        assertNull(translator.guessDividendType(null));
        assertNull(translator.guessDividendType(CSVQuantum.getQuantum(new String[] { "" },
                                                                      request, 1.0)));
        new ExpectedFailure<CoreException>(CANNOT_INTERPRET_DIVIDEND_TYPE) {
            @Override
            protected void run()
                    throws Exception
            {
                translator.guessDividendType(CSVQuantum.getQuantum(new String[] { "zero","one","two","three","four","invalid-Type" },
                                                                   request, 1.0));
            }
        };
        for(DividendType type : DividendType.values()) {
            // default case
            assertEquals(type,
                         translator.guessDividendType(CSVQuantum.getQuantum(new String[] { "zero","one","two","three","four",type.name() },
                                                                            request, 1.0)));
            // upper-case
            assertEquals(type,
                         translator.guessDividendType(CSVQuantum.getQuantum(new String[] { "zero","one","two","three","four",type.name().toUpperCase() },
                                                                            request, 1.0)));
            // lower-case
            assertEquals(type,
                         translator.guessDividendType(CSVQuantum.getQuantum(new String[] { "zero","one","two","three","four",type.name().toLowerCase() },
                                                                            request, 1.0)));
        }
    }
    /**
     * Tests {@link BasicCSVFeedEventTranslator#validateRequiredFields(CSVQuantum, java.util.Set)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testValidateRequiredFields()
            throws Exception
    {
        new ExpectedFailure<NullPointerException>() {
            @Override
            protected void run()
                    throws Exception
            {
                translator.validateRequiredFields(null,
                                                  null);
            }
        };
        new ExpectedFailure<NullPointerException>() {
            @Override
            protected void run()
                    throws Exception
            {
                translator.validateRequiredFields(CSVQuantum.getQuantum(new String[] { "" },
                                                                        request, 1.0),
                                                  null);
            }
        };
        // empty - nothing provided, nothing required
        translator.validateRequiredFields(CSVQuantum.getQuantum(new String[] { "" },
                                                                request, 1.0),
                                          new HashSet<Integer>());
        // several fields provided, nothing required
        translator.validateRequiredFields(CSVQuantum.getQuantum(new String[] { "one","two","three" },
                                                                request, 1.0),
                                          new HashSet<Integer>());
        // several fields provided, subset of provided is required
        translator.validateRequiredFields(CSVQuantum.getQuantum(new String[] { "one","two","three" },
                                                                request, 1.0),
                                          new HashSet<Integer>(Arrays.asList(new Integer[] { 0,2 })));
        // several fields provided, superset of provided is required
        new ExpectedFailure<CoreException>(LINE_MISSING_REQUIRED_FIELDS) {
            @Override
            protected void run()
                    throws Exception
            {
                translator.validateRequiredFields(CSVQuantum.getQuantum(new String[] { "one","two","three" },
                                                                        request, 1.0),
                                                  new HashSet<Integer>(Arrays.asList(new Integer[] { 0,1,2,3 })));
            }
        };
        // several fields provided, intersection of required is empty
        new ExpectedFailure<CoreException>(LINE_MISSING_REQUIRED_FIELDS) {
            @Override
            protected void run()
                    throws Exception
            {
                translator.validateRequiredFields(CSVQuantum.getQuantum(new String[] { "one","two","three" },
                                                                        request, 1.0),
                                                  new HashSet<Integer>(Arrays.asList(new Integer[] { 3,4,5,6 })));
            }
        };
    }
    /**
     * Tests {@link BasicCSVFeedEventTranslator#validateTrade(CSVQuantum)}. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testValidateTrade()
            throws Exception
    {
        new ExpectedFailure<NullPointerException>() {
            @Override
            protected void run()
                    throws Exception
            {
                translator.validateTrade(null);
            }
        };
        // no required fields
        new ExpectedFailure<CoreException>(LINE_MISSING_REQUIRED_FIELDS) {
            @Override
            protected void run()
                    throws Exception
            {
                translator.validateTrade(CSVQuantum.getQuantum(new String[] { },
                                                               request, 1.0));
            }
        };
        // some required fields
        new ExpectedFailure<CoreException>(LINE_MISSING_REQUIRED_FIELDS) {
            @Override
            protected void run()
                    throws Exception
            {
                translator.validateTrade(CSVQuantum.getQuantum(new String[] { "one","two","three" },
                                                               request, 1.0));
            }
        };
        // exactly required fields
        translator.validateTrade(CSVQuantum.getQuantum(new String[] { "one","two","three","four","five","six","seven" },
                                                       request, 1.0));
        // superset of required fields
        translator.validateTrade(CSVQuantum.getQuantum(new String[] { "one","two","three","four","five","six","seven","eight" },
                                                       request, 1.0));
    }
    /**
     * Tests {@link BasicCSVFeedEventTranslator#validateQuote(CSVQuantum)}. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testValidateQuote()
            throws Exception
    {
        new ExpectedFailure<NullPointerException>() {
            @Override
            protected void run()
                    throws Exception
            {
                translator.validateQuote(null);
            }
        };
        // no required fields
        new ExpectedFailure<CoreException>(LINE_MISSING_REQUIRED_FIELDS) {
            @Override
            protected void run()
                    throws Exception
            {
                translator.validateQuote(CSVQuantum.getQuantum(new String[] { },
                                                               request, 1.0));
            }
        };
        // some required fields
        new ExpectedFailure<CoreException>(LINE_MISSING_REQUIRED_FIELDS) {
            @Override
            protected void run()
                    throws Exception
            {
                translator.validateQuote(CSVQuantum.getQuantum(new String[] { "one","two","three" },
                                                               request, 1.0));
            }
        };
        // exactly required fields
        translator.validateQuote(CSVQuantum.getQuantum(new String[] { "one","two","three","four","five","six","seven" },
                                                       request, 1.0));
        // superset of required fields
        translator.validateQuote(CSVQuantum.getQuantum(new String[] { "one","two","three","four","five","six","seven","eight" },
                                                       request, 1.0));
    }
    /**
     * Tests {@link BasicCSVFeedEventTranslator#validateBid(CSVQuantum)}. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testValidateBid()
            throws Exception
    {
        new ExpectedFailure<NullPointerException>() {
            @Override
            protected void run()
                    throws Exception
            {
                translator.validateBid(null);
            }
        };
        // no required fields
        new ExpectedFailure<CoreException>(LINE_MISSING_REQUIRED_FIELDS) {
            @Override
            protected void run()
                    throws Exception
            {
                translator.validateBid(CSVQuantum.getQuantum(new String[] { },
                                                             request, 1.0));
            }
        };
        // some required fields
        new ExpectedFailure<CoreException>(LINE_MISSING_REQUIRED_FIELDS) {
            @Override
            protected void run()
                    throws Exception
            {
                translator.validateBid(CSVQuantum.getQuantum(new String[] { "one","two","three" },
                                                             request, 1.0));
            }
        };
        // exactly required fields
        translator.validateBid(CSVQuantum.getQuantum(new String[] { "one","two","three","four","five","six","seven" },
                                                     request, 1.0));
        // superset of required fields
        translator.validateBid(CSVQuantum.getQuantum(new String[] { "one","two","three","four","five","six","seven","eight" },
                                                     request, 1.0));
    }
    /**
     * Tests {@link BasicCSVFeedEventTranslator#validateAsk(CSVQuantum)}. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testValidateAsk()
            throws Exception
    {
        new ExpectedFailure<NullPointerException>() {
            @Override
            protected void run()
                    throws Exception
            {
                translator.validateAsk(null);
            }
        };
        // no required fields
        new ExpectedFailure<CoreException>(LINE_MISSING_REQUIRED_FIELDS) {
            @Override
            protected void run()
                    throws Exception
            {
                translator.validateAsk(CSVQuantum.getQuantum(new String[] { },
                                                             request, 1.0));
            }
        };
        // some required fields
        new ExpectedFailure<CoreException>(LINE_MISSING_REQUIRED_FIELDS) {
            @Override
            protected void run()
                    throws Exception
            {
                translator.validateAsk(CSVQuantum.getQuantum(new String[] { "one","two","three" },
                                                             request, 1.0));
            }
        };
        // exactly required fields
        translator.validateAsk(CSVQuantum.getQuantum(new String[] { "one","two","three","four","five","six","seven" },
                                                     request, 1.0));
        // superset of required fields
        translator.validateAsk(CSVQuantum.getQuantum(new String[] { "one","two","three","four","five","six","seven","eight" },
                                                     request, 1.0));
    }
    /**
     * Tests {@link BasicCSVFeedEventTranslator#validateDividend(CSVQuantum)}. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testValidateDividend()
            throws Exception
    {
        new ExpectedFailure<NullPointerException>() {
            @Override
            protected void run()
                    throws Exception
            {
                translator.validateDividend(null);
            }
        };
        // no required fields
        new ExpectedFailure<CoreException>(LINE_MISSING_REQUIRED_FIELDS) {
            @Override
            protected void run()
                    throws Exception
            {
                translator.validateDividend(CSVQuantum.getQuantum(new String[] { },
                                                             request, 1.0));
            }
        };
        // some required fields
        new ExpectedFailure<CoreException>(LINE_MISSING_REQUIRED_FIELDS) {
            @Override
            protected void run()
                    throws Exception
            {
                translator.validateDividend(CSVQuantum.getQuantum(new String[] { "one","two","three" },
                                                             request, 1.0));
            }
        };
        // exactly required fields
        translator.validateDividend(CSVQuantum.getQuantum(new String[] { "zero","one","two","three","four","","six","seven","","","ten","eleven" },
                                                          request, 1.0));
        // superset of required fields
        translator.validateDividend(CSVQuantum.getQuantum(new String[] { "zero","one","two","three","four","five","six","seven","eight","nine","ten","eleven","twelve" },
                                                          request, 1.0));
    }
    /**
     * Tests {@link BasicCSVFeedEventTranslator#validateMarketstat(CSVQuantum)}. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testValidateMarketstat()
            throws Exception
    {
        new ExpectedFailure<NullPointerException>() {
            @Override
            protected void run()
                    throws Exception
            {
                translator.validateMarketstat(null);
            }
        };
        // no required fields
        new ExpectedFailure<CoreException>(LINE_MISSING_REQUIRED_FIELDS) {
            @Override
            protected void run()
                    throws Exception
            {
                translator.validateMarketstat(CSVQuantum.getQuantum(new String[] { },
                                                             request, 1.0));
            }
        };
        // some required fields
        new ExpectedFailure<CoreException>(LINE_MISSING_REQUIRED_FIELDS) {
            @Override
            protected void run()
                    throws Exception
            {
                translator.validateMarketstat(CSVQuantum.getQuantum(new String[] { "zero","one" },
                                                             request, 1.0));
            }
        };
        // exactly required fields
        translator.validateMarketstat(CSVQuantum.getQuantum(new String[] { "zero","one","two" },
                                                          request, 1.0));
        // superset of required fields
        translator.validateMarketstat(CSVQuantum.getQuantum(new String[] { "zero","one","two","three","four","five","six","seven","eight","nine","ten","eleven","twelve","thirteen","fourteen","fifteen","sixteen" },
                                                          request, 1.0));
    }
    /**
     * Tests {@link BasicCSVFeedEventTranslator#processAsk(CSVQuantum)} and {@link BasicCSVFeedEventTranslator#processBid(CSVQuantum)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testProcessQuotes()
            throws Exception
    {
        new ExpectedFailure<NullPointerException>() {
            @Override
            protected void run()
                    throws Exception
            {
                translator.processAsk(null);
            }
        };
        new ExpectedFailure<NullPointerException>() {
            @Override
            protected void run()
                    throws Exception
            {
                translator.processBid(null);
            }
        };
        // not enough fields 
        new ExpectedFailure<CoreException>(UNABLE_TO_CONSTRUCT_QUOTE) {
            @Override
            protected void run()
                    throws Exception
            {
                translator.processAsk(CSVQuantum.getQuantum(new String[] { "zero","one" },
                                                            request, 1.0));
            }
        };
        new ExpectedFailure<CoreException>(UNABLE_TO_CONSTRUCT_QUOTE) {
            @Override
            protected void run()
                    throws Exception
            {
                translator.processBid(CSVQuantum.getQuantum(new String[] { "zero","one" },
                                                            request, 1.0));
            }
        };
        long askTimestamp = System.currentTimeMillis();
        long bidTimestamp = System.currentTimeMillis() + 5000;
        AskEvent ask = translator.processAsk(CSVQuantum.getQuantum(new String[] { "ask",String.valueOf(askTimestamp),"ask-symbol","ask-date","ask-exchange","1234.56","9876.543" },
                                                                   request, 1.0));
        assertEquals(QuoteAction.ADD,
                     ask.getAction());
        assertEquals("ask-exchange",
                     ask.getExchange());
        assertEquals("ask-date",
                     ask.getExchangeTimestamp());
        assertEquals(new Equity("ask-symbol"),
                     ask.getInstrument());
        assertEquals(new BigDecimal("1234.56"),
                     ask.getPrice());
        assertEquals("ask-date",
                     ask.getQuoteDate());
        assertEquals(new BigDecimal("9876.543"),
                     ask.getSize());
        assertEquals(new Date(askTimestamp),
                     ask.getTimestamp());
        BidEvent bid = translator.processBid(CSVQuantum.getQuantum(new String[] { "bid",String.valueOf(bidTimestamp),"bid-symbol","bid-date","bid-exchange","12340.056","98760.0543" },
                                                                   request, 1.0));
        assertEquals(QuoteAction.ADD,
                     bid.getAction());
        assertEquals("bid-exchange",
                     bid.getExchange());
        assertEquals("bid-date",
                     bid.getExchangeTimestamp());
        assertEquals(new Equity("bid-symbol"),
                     bid.getInstrument());
        assertEquals(new BigDecimal("12340.056"),
                     bid.getPrice());
        assertEquals("bid-date",
                     bid.getQuoteDate());
        assertEquals(new BigDecimal("98760.0543"),
                     bid.getSize());
        assertEquals(new Date(bidTimestamp),
                     bid.getTimestamp());
        // repeat just one of the tests using an option instead
        bidTimestamp += 5000;
        bid = translator.processBid(CSVQuantum.getQuantum(new String[] { "bid",String.valueOf(bidTimestamp),OptionUtils.getOsiSymbolFromOption(option),"bid-date","bid-exchange","12340.056","98760.0543" },
                                                          request, 1.0));
        assertEquals(QuoteAction.ADD,
                     bid.getAction());
        assertEquals("bid-exchange",
                     bid.getExchange());
        assertEquals("bid-date",
                     bid.getExchangeTimestamp());
        assertEquals(option,
                     bid.getInstrument());
        assertEquals(new BigDecimal("12340.056"),
                     bid.getPrice());
        assertEquals("bid-date",
                     bid.getQuoteDate());
        assertEquals(new BigDecimal("98760.0543"),
                     bid.getSize());
        assertEquals(new Date(bidTimestamp),
                     bid.getTimestamp());
    }
    /**
     * Tests {@link BasicCSVFeedEventTranslator#processTrade(CSVQuantum)}. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testProcessTrade()
            throws Exception
    {
        new ExpectedFailure<NullPointerException>() {
            @Override
            protected void run()
                    throws Exception
            {
                translator.processTrade(null);
            }
        };
        // not enough fields 
        new ExpectedFailure<CoreException>(UNABLE_TO_CONSTRUCT_TRADE) {
            @Override
            protected void run()
                    throws Exception
            {
                translator.processTrade(CSVQuantum.getQuantum(new String[] { "zero","one" },
                                                              request, 1.0));
            }
        };
        long tradeTimestamp = System.currentTimeMillis();
        TradeEvent trade = translator.processTrade(CSVQuantum.getQuantum(new String[] { "trade",String.valueOf(tradeTimestamp),"trade-symbol","trade-date","trade-exchange","1234.56","9876.543" },
                                                                         request, 1.0));
        assertEquals("trade-exchange",
                     trade.getExchange());
        assertEquals("trade-date",
                     trade.getExchangeTimestamp());
        assertEquals(new Equity("trade-symbol"),
                     trade.getInstrument());
        assertEquals(new BigDecimal("1234.56"),
                     trade.getPrice());
        assertEquals("trade-date",
                     trade.getTradeDate());
        assertEquals(new BigDecimal("9876.543"),
                     trade.getSize());
        assertEquals(new Date(tradeTimestamp),
                     trade.getTimestamp());
        // repeat with an option
        tradeTimestamp += 5000;
        trade = translator.processTrade(CSVQuantum.getQuantum(new String[] { "trade",String.valueOf(tradeTimestamp),OptionUtils.getOsiSymbolFromOption(option),"trade-date","trade-exchange","12340.056","98760.0543" },
                                                              request, 1.0));
        assertEquals("trade-exchange",
                     trade.getExchange());
        assertEquals("trade-date",
                     trade.getExchangeTimestamp());
        assertEquals(option,
                     trade.getInstrument());
        assertEquals(new BigDecimal("12340.056"),
                     trade.getPrice());
        assertEquals("trade-date",
                     trade.getTradeDate());
        assertEquals(new BigDecimal("98760.0543"),
                     trade.getSize());
        assertEquals(new Date(tradeTimestamp),
                     trade.getTimestamp());
    }
    /**
     * Tests {@link BasicCSVFeedEventTranslator#processDividend(CSVQuantum)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testProcessDividend()
            throws Exception
    {
        new ExpectedFailure<NullPointerException>() {
            @Override
            protected void run()
                    throws Exception
            {
                translator.processDividend(null);
            }
        };
        // not enough fields
        new ExpectedFailure<CoreException>(UNABLE_TO_CONSTRUCT_DIVIDEND) {
            @Override
            protected void run()
                    throws Exception
            {
                translator.processDividend(CSVQuantum.getQuantum(new String[] { "zero","one" },
                                                                 request, 1.0));
            }
        };
        // minimum fields
        long dividendTimestamp = System.currentTimeMillis();
        DividendEvent dividend = translator.processDividend(CSVQuantum.getQuantum(new String[] { "dividend",String.valueOf(dividendTimestamp),"dividend-symbol","123.45","USD",DividendType.CURRENT.name(),DividendFrequency.ANNUALLY.name(),DividendStatus.OFFICIAL.name(),"execution-date" },
                                                                                  request, 1.0));
        assertEquals(new BigDecimal("123.45"),
                     dividend.getAmount());
        assertEquals("USD",
                     dividend.getCurrency());
        assertEquals(null,
                     dividend.getDeclareDate());
        assertEquals(new Equity("dividend-symbol"),
                     dividend.getEquity());
        assertEquals("execution-date",
                     dividend.getExecutionDate());
        assertEquals(DividendFrequency.ANNUALLY,
                     dividend.getFrequency());
        assertEquals(new Equity("dividend-symbol"),
                     dividend.getInstrument());
        assertEquals(null,
                     dividend.getPaymentDate());
        assertEquals(null,
                     dividend.getRecordDate());
        assertEquals(DividendStatus.OFFICIAL,
                     dividend.getStatus());
        assertEquals(new Date(dividendTimestamp),
                     dividend.getTimestamp());
        // maximum fields
        dividendTimestamp = System.currentTimeMillis() + 5000;
        dividend = translator.processDividend(CSVQuantum.getQuantum(new String[] { "dividend",String.valueOf(dividendTimestamp),"dividend-symbol-new","123.45678","CAD",DividendType.FUTURE.name(),DividendFrequency.MONTHLY.name(),DividendStatus.UNKNOWN.name(),"new-execution-date","record-date","payment-date","declare-date" },
                                                                    request, 1.0));
        assertEquals(new BigDecimal("123.45678"),
                     dividend.getAmount());
        assertEquals("CAD",
                     dividend.getCurrency());
        assertEquals("declare-date",
                     dividend.getDeclareDate());
        assertEquals(new Equity("dividend-symbol-new"),
                     dividend.getEquity());
        assertEquals("new-execution-date",
                     dividend.getExecutionDate());
        assertEquals(DividendFrequency.MONTHLY,
                     dividend.getFrequency());
        assertEquals(new Equity("dividend-symbol-new"),
                     dividend.getInstrument());
        assertEquals("payment-date",
                     dividend.getPaymentDate());
        assertEquals("record-date",
                     dividend.getRecordDate());
        assertEquals(DividendStatus.UNKNOWN,
                     dividend.getStatus());
        assertEquals(new Date(dividendTimestamp),
                     dividend.getTimestamp());
    }
    /**
     * Tests {@link BasicCSVFeedEventTranslator#processMarketstat(CSVQuantum)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testProcessMarketstat()
            throws Exception
    {
        new ExpectedFailure<NullPointerException>() {
            @Override
            protected void run()
                    throws Exception
            {
                translator.processMarketstat(null);
            }
        };
        // not enough fields
        new ExpectedFailure<CoreException>(UNABLE_TO_CONSTRUCT_MARKETSTAT) {
            @Override
            protected void run()
                    throws Exception
            {
                translator.processMarketstat(CSVQuantum.getQuantum(new String[] { "zero","one" },
                                                                   request, 1.0));
            }
        };
        // minimum fields
        long marketstatTimestamp = System.currentTimeMillis();
        MarketstatEvent marketstat = translator.processMarketstat(CSVQuantum.getQuantum(new String[] { "stat",String.valueOf(marketstatTimestamp),"marketstat-symbol" },
                                                                                        request, 1.0));
        assertNull(marketstat.getClose());
        assertNull(marketstat.getCloseDate());
        assertNull(marketstat.getCloseExchange());
        assertNull(marketstat.getHigh());
        assertNull(marketstat.getHighExchange());
        assertEquals(new Equity("marketstat-symbol"),
                     marketstat.getInstrument());
        assertNull(marketstat.getLow());
        assertNull(marketstat.getLowExchange());
        assertNull(marketstat.getOpen());
        assertNull(marketstat.getOpenExchange());
        assertNull(marketstat.getPreviousClose());
        assertNull(marketstat.getPreviousCloseDate());
        assertEquals(new Date(marketstatTimestamp),
                     marketstat.getTimestamp());
        assertNull(marketstat.getTradeHighTime());
        assertNull(marketstat.getTradeLowTime());
        assertNull(marketstat.getVolume());
        // minimum fields with an option
        marketstat = translator.processMarketstat(CSVQuantum.getQuantum(new String[] { "stat",String.valueOf(marketstatTimestamp),OptionUtils.getOsiSymbolFromOption(option) },
                                                                        request, 1.0));
        assertEquals(option,
                     marketstat.getInstrument());
        // maximum fields
        marketstatTimestamp = System.currentTimeMillis() + 5000;
        marketstat = translator.processMarketstat(CSVQuantum.getQuantum(new String[] { "stat",String.valueOf(marketstatTimestamp),OptionUtils.getOsiSymbolFromOption(option),"1.0","2.1","3.2","4.3","5.4","6.5","close-date","previous-close-date","trade-high-time","trade-low-time","open-exchange","high-exchange","low-exchange","close-exchange" },
                                                                        request, 1.0));
        assertEquals(new BigDecimal("4.3"),
                     marketstat.getClose());
        assertEquals("close-date",
                     marketstat.getCloseDate());
        assertEquals("close-exchange",
                     marketstat.getCloseExchange());
        assertEquals(new BigDecimal("2.1"),
                     marketstat.getHigh());
        assertEquals("high-exchange",
                     marketstat.getHighExchange());
        assertEquals(option,
                     marketstat.getInstrument());
        assertEquals(new BigDecimal("3.2"),
                     marketstat.getLow());
        assertEquals("low-exchange",
                     marketstat.getLowExchange());
        assertEquals(new BigDecimal("1.0"),
                     marketstat.getOpen());
        assertEquals("open-exchange",
                     marketstat.getOpenExchange());
        assertEquals(new BigDecimal("5.4"),
                     marketstat.getPreviousClose());
        assertEquals("previous-close-date",
                     marketstat.getPreviousCloseDate());
        assertEquals(new Date(marketstatTimestamp),
                     marketstat.getTimestamp());
        assertEquals("trade-high-time",
                     marketstat.getTradeHighTime());
        assertEquals("trade-low-time",
                     marketstat.getTradeLowTime());
        assertEquals(new BigDecimal("6.5"),
                     marketstat.getVolume());
    }
    /**
     * Tests {@link BasicCSVFeedEventTranslator#toEvent(Object, String)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testToEvent()
            throws Exception
    {
        new ExpectedFailure<ClassCastException>()
        {
            @Override
            protected void run()
                    throws Exception
            {
                translator.toEvent(this,
                                   "1");
            }
        };
        new ExpectedFailure<NullPointerException>()
        {
            @Override
            protected void run()
                    throws Exception
            {
                translator.toEvent(null,
                                   "1");
            }
        };
        new ExpectedFailure<CoreException>(EMPTY_LINE)
        {
            @Override
            protected void run()
                    throws Exception
            {
                translator.toEvent(CSVQuantum.getQuantum(new String[] { },
                                                         request, 1.0),
                                   "1");
            }
        };
        new ExpectedFailure<CoreException>(UNKNOWN_BASIC_EVENT_TYPE)
        {
            @Override
            protected void run()
                    throws Exception
            {
                translator.toEvent(CSVQuantum.getQuantum(new String[] { "not-a-type" },
                                                         request, 1.0),
                                   "1");
            }
        };
        // invalid bid
        new ExpectedFailure<CoreException>(LINE_MISSING_REQUIRED_FIELDS)
        {
            @Override
            protected void run()
                    throws Exception
            {
                translator.toEvent(CSVQuantum.getQuantum(new String[] { "bid" },
                                                         request, 1.0),
                                   "1");
            }
        };
        // valid bid
        long timestamp = System.currentTimeMillis();
        List<Event> events = translator.toEvent(CSVQuantum.getQuantum(new String[] { "bid",String.valueOf(timestamp),"bid-symbol","bid-date","bid-exchange","12340.056","98760.0543" },
                                                                      request, 1.0),
                                                "handle-1");
        assertEquals(1,
                     events.size());
        BidEvent bid = (BidEvent)events.get(0);
        assertEquals(QuoteAction.ADD,
                     bid.getAction());
        assertEquals("bid-exchange",
                     bid.getExchange());
        assertEquals("bid-date",
                     bid.getExchangeTimestamp());
        assertEquals(new Equity("bid-symbol"),
                     bid.getInstrument());
        assertEquals(new BigDecimal("12340.056"),
                     bid.getPrice());
        assertEquals("bid-date",
                     bid.getQuoteDate());
        assertEquals(new BigDecimal("98760.0543"),
                     bid.getSize());
        assertEquals(new Date(timestamp),
                     bid.getTimestamp());
        // invalid ask
        new ExpectedFailure<CoreException>(LINE_MISSING_REQUIRED_FIELDS)
        {
            @Override
            protected void run()
                    throws Exception
            {
                translator.toEvent(CSVQuantum.getQuantum(new String[] { "ask" },
                                                         request, 1.0),
                                   "1");
            }
        };
        // valid ask
        timestamp = System.currentTimeMillis();
        events = translator.toEvent(CSVQuantum.getQuantum(new String[] { "ask",String.valueOf(timestamp),"ask-symbol","ask-date","ask-exchange","12340.056","98760.0543" },
                                                          request, 1.0),
                                    "handle-1");
        assertEquals(1,
                     events.size());
        AskEvent ask = (AskEvent)events.get(0);
        assertEquals(QuoteAction.ADD,
                     ask.getAction());
        assertEquals("ask-exchange",
                     ask.getExchange());
        assertEquals("ask-date",
                     ask.getExchangeTimestamp());
        assertEquals(new Equity("ask-symbol"),
                     ask.getInstrument());
        assertEquals(new BigDecimal("12340.056"),
                     ask.getPrice());
        assertEquals("ask-date",
                     ask.getQuoteDate());
        assertEquals(new BigDecimal("98760.0543"),
                     ask.getSize());
        assertEquals(new Date(timestamp),
                     ask.getTimestamp());
        // invalid trade
        new ExpectedFailure<CoreException>(LINE_MISSING_REQUIRED_FIELDS)
        {
            @Override
            protected void run()
                    throws Exception
            {
                translator.toEvent(CSVQuantum.getQuantum(new String[] { "trade" },
                                                         request, 1.0),
                                   "1");
            }
        };
        // valid trade
        timestamp = System.currentTimeMillis();
        events = translator.toEvent(CSVQuantum.getQuantum(new String[] { "trade",String.valueOf(timestamp),"trade-symbol","trade-date","trade-exchange","12340.056","98760.0543" },
                                                          request, 1.0),
                                    "handle-1");
        assertEquals(1,
                     events.size());
        TradeEvent trade = (TradeEvent)events.get(0);
        assertEquals("trade-exchange",
                     trade.getExchange());
        assertEquals("trade-date",
                     trade.getExchangeTimestamp());
        assertEquals(new Equity("trade-symbol"),
                     trade.getInstrument());
        assertEquals(new BigDecimal("12340.056"),
                     trade.getPrice());
        assertEquals("trade-date",
                     trade.getTradeDate());
        assertEquals(new BigDecimal("98760.0543"),
                     trade.getSize());
        assertEquals(new Date(timestamp),
                     trade.getTimestamp());
        // invalid dividend
        new ExpectedFailure<CoreException>(LINE_MISSING_REQUIRED_FIELDS)
        {
            @Override
            protected void run()
                    throws Exception
            {
                translator.toEvent(CSVQuantum.getQuantum(new String[] { "dividend" },
                                                         request, 1.0),
                                   "1");
            }
        };
        // valid dividend
        timestamp = System.currentTimeMillis();
        events = translator.toEvent(CSVQuantum.getQuantum(new String[] { "dividend",String.valueOf(timestamp),"dividend-symbol","123.45","USD",DividendType.CURRENT.name(),DividendFrequency.ANNUALLY.name(),DividendStatus.OFFICIAL.name(),"execution-date" },
                                                          request, 1.0),
                                    "handle-1");
        DividendEvent dividend = (DividendEvent)events.get(0);
        assertEquals(1,
                     events.size());
        assertEquals(new BigDecimal("123.45"),
                     dividend.getAmount());
        assertEquals("USD",
                     dividend.getCurrency());
        assertEquals(null,
                     dividend.getDeclareDate());
        assertEquals(new Equity("dividend-symbol"),
                     dividend.getEquity());
        assertEquals("execution-date",
                     dividend.getExecutionDate());
        assertEquals(DividendFrequency.ANNUALLY,
                     dividend.getFrequency());
        assertEquals(new Equity("dividend-symbol"),
                     dividend.getInstrument());
        assertEquals(null,
                     dividend.getPaymentDate());
        assertEquals(null,
                     dividend.getRecordDate());
        assertEquals(DividendStatus.OFFICIAL,
                     dividend.getStatus());
        assertEquals(new Date(timestamp),
                     dividend.getTimestamp());
        // invalid marketstat
        new ExpectedFailure<CoreException>(LINE_MISSING_REQUIRED_FIELDS)
        {
            @Override
            protected void run()
                    throws Exception
            {
                translator.toEvent(CSVQuantum.getQuantum(new String[] { "stat" },
                                                         request, 1.0),
                                   "1");
            }
        };
        // valid marketstat
        events = translator.toEvent(CSVQuantum.getQuantum(new String[] { "stat",String.valueOf(timestamp),"marketstat-symbol" },
                                                                                        request, 1.0),
                                    "handle-2");
        MarketstatEvent marketstat = (MarketstatEvent)events.get(0);
        assertNull(marketstat.getClose());
        assertNull(marketstat.getCloseDate());
        assertNull(marketstat.getCloseExchange());
        assertNull(marketstat.getHigh());
        assertNull(marketstat.getHighExchange());
        assertEquals(new Equity("marketstat-symbol"),
                     marketstat.getInstrument());
        assertNull(marketstat.getLow());
        assertNull(marketstat.getLowExchange());
        assertNull(marketstat.getOpen());
        assertNull(marketstat.getOpenExchange());
        assertNull(marketstat.getPreviousClose());
        assertNull(marketstat.getPreviousCloseDate());
        assertEquals(new Date(timestamp),
                     marketstat.getTimestamp());
        assertNull(marketstat.getTradeHighTime());
        assertNull(marketstat.getTradeLowTime());
        assertNull(marketstat.getVolume());
    }
    /**
     * this variable is declared as static/final because the contract of {@link CSVFeedEventTranslator} is that
     * the object <em>must</em> be stateless
     */
    private static final BasicCSVFeedEventTranslator translator = new BasicCSVFeedEventTranslator();
    /**
     * a market data request to use for testing
     */
    private MarketDataRequest request = MarketDataRequestBuilder.newRequest().withSymbols("GOOG").create();
    /**
     * option to use for testing
     */
    private final Option option = new Option("symbol",
                                             "20150319",
                                             BigDecimal.ONE,
                                             OptionType.Call);
}
