package org.marketcetera.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.marketcetera.core.LoggerConfiguration;
import org.marketcetera.marketdata.OrderBookTest;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.trade.MSymbol;
import org.marketcetera.util.log.SLF4JLoggerProxy;

/* $License$ */

/**
 * Tests {@link OpenHighLowClose}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class OpenHighLowCloseTest
{
    private final MSymbol metc = new MSymbol("METC");
    private final MSymbol goog = new MSymbol("GOOG");
    private final String exchange1 = "TEST1";
    private final String exchange2 = "TEST2";
    private TradeEvent open;
    private TradeEvent high;
    private TradeEvent low;
    private TradeEvent close;
    private Date start;
    private Date end;
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
     * Run before each test.
     *
     * @throws Exception if an error occurs
     */
    @Before
    public void setup()
        throws Exception
    {
        open = EventBaseTest.generateTradeEvent(metc,
                                                exchange1,
                                                new BigDecimal(5));
        high = EventBaseTest.generateTradeEvent(metc,
                                                exchange1,
                                                BigDecimal.TEN);
        low = EventBaseTest.generateTradeEvent(metc,
                                               exchange1,
                                               BigDecimal.ONE);
        Thread.sleep(250);
        close = EventBaseTest.generateTradeEvent(metc,
                                                 exchange1,
                                                 new BigDecimal(7));
        assertTrue(close.getTimeMillis() > open.getTimeMillis());
        start = open.getTimestampAsDate();
        end = close.getTimestampAsDate();
    }
    /**
     * Tests the ability to construct OHLC events. 
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void construction()
        throws Exception
    {
        // test nulls
        new ExpectedFailure<NullPointerException>(null) {
            @Override
            protected void run()
                    throws Exception
            {
                new OpenHighLowClose(null,
                                     high,
                                     low,
                                     close,
                                     start,
                                     end,
                                     new Date(),
                                     metc);
            }
        };
        new ExpectedFailure<NullPointerException>(null) {
            @Override
            protected void run()
                    throws Exception
            {
                new OpenHighLowClose(open,
                                     null,
                                     low,
                                     close,
                                     start,
                                     end,
                                     new Date(),
                                     metc);
            }
        };
        new ExpectedFailure<NullPointerException>(null) {
            @Override
            protected void run()
                    throws Exception
            {
                new OpenHighLowClose(open,
                                     high,
                                     null,
                                     close,
                                     start,
                                     end,
                                     new Date(),
                                     metc);
            }
        };
        new ExpectedFailure<NullPointerException>(null) {
            @Override
            protected void run()
                    throws Exception
            {
                new OpenHighLowClose(open,
                                     high,
                                     low,
                                     close,
                                     null,
                                     end,
                                     new Date(),
                                     metc);
            }
        };
        new ExpectedFailure<NullPointerException>(null) {
            @Override
            protected void run()
                    throws Exception
            {
                new OpenHighLowClose(open,
                                     high,
                                     low,
                                     close,
                                     start,
                                     null,
                                     new Date(),
                                     metc);
            }
        };
        new ExpectedFailure<NullPointerException>(null) {
            @Override
            protected void run()
                    throws Exception
            {
                new OpenHighLowClose(open,
                                     high,
                                     low,
                                     close,
                                     start,
                                     end,
                                     null,
                                     metc);
            }
        };
        new ExpectedFailure<NullPointerException>(null) {
            @Override
            protected void run()
                    throws Exception
            {
                new OpenHighLowClose(open,
                                     high,
                                     low,
                                     close,
                                     start,
                                     end,
                                     new Date(),
                                     null);
            }
        };
        // Start after end
        new ExpectedFailure<IllegalArgumentException>(null) {
            @Override
            protected void run()
                    throws Exception
            {
                new OpenHighLowClose(open,
                                     high,
                                     low,
                                     close,
                                     end,
                                     start,
                                     new Date(),
                                     metc);
            }
        };
        // required close (end < event timestamp)
        new ExpectedFailure<NullPointerException>(null) {
            @Override
            protected void run()
                    throws Exception
            {
                new OpenHighLowClose(open,
                                     high,
                                     low,
                                     null,
                                     start,
                                     end,
                                     new Date(end.getTime() + 1),
                                     metc);
            }
        };
        verifyOHLC(new OpenHighLowClose(open,
                                        high,
                                        low,
                                        close,
                                        start,
                                        end,
                                        new Date(end.getTime() + 1),
                                        metc),
                   open,
                   high,
                   low,
                   close,
                   start,
                   end);
        verifyOHLC(new OpenHighLowClose(open,
                                        high,
                                        low,
                                        close,
                                        start,
                                        end,
                                        new Date(),
                                        metc),
                   open,
                   high,
                   low,
                   close,
                   start,
                   end);
        // don't need close for an open interval
        verifyOHLC(new OpenHighLowClose(open,
                                        high,
                                        low,
                                        null,
                                        start,
                                        end,
                                        new Date(end.getTime() - 1),
                                        metc),
                   open,
                   high,
                   low,
                   null,
                   start,
                   end);
        // don't need close for an open interval, but can specify one if desired
        verifyOHLC(new OpenHighLowClose(open,
                                        high,
                                        low,
                                        close,
                                        start,
                                        end,
                                        new Date(end.getTime() - 1),
                                        metc),
                   open,
                   high,
                   low,
                   close,
                   start,
                   end);
    }
    /**
     * Tests what happens if the wrong symbol is provided on the trades.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void wrongSymbol()
        throws Exception
    {
        final TradeEvent badSymbolTrade = new TradeEvent(System.nanoTime(),
                                                         open.getTimeMillis() + 1,
                                                         goog,
                                                         exchange1,
                                                         new BigDecimal(5),
                                                         new BigDecimal(10));
        assertTrue(badSymbolTrade.getTimeMillis() >= open.getTimeMillis());
        assertTrue(badSymbolTrade.getTimeMillis() <= close.getTimeMillis());
        // substitute trades with the wrong symbol
        new ExpectedFailure<IllegalArgumentException>(null) {
            @Override
            protected void run()
                    throws Exception
            {
                new OpenHighLowClose(badSymbolTrade,
                                     high,
                                     low,
                                     close,
                                     open.getTimestampAsDate(),
                                     close.getTimestampAsDate(),
                                     new Date(),
                                     metc);
            }
        };
        new ExpectedFailure<IllegalArgumentException>(null) {
            @Override
            protected void run()
                    throws Exception
            {
                new OpenHighLowClose(open,
                                     badSymbolTrade,
                                     low,
                                     close,
                                     open.getTimestampAsDate(),
                                     close.getTimestampAsDate(),
                                     new Date(),
                                     metc);
            }
        };
        new ExpectedFailure<IllegalArgumentException>(null) {
            @Override
            protected void run()
                    throws Exception
            {
                new OpenHighLowClose(open,
                                     high,
                                     badSymbolTrade,
                                     close,
                                     open.getTimestampAsDate(),
                                     close.getTimestampAsDate(),
                                     new Date(),
                                     metc);
            }
        };
        // this close is required
        new ExpectedFailure<IllegalArgumentException>(null) {
            @Override
            protected void run()
                    throws Exception
            {
                new OpenHighLowClose(open,
                                     high,
                                     low,
                                     badSymbolTrade,
                                     open.getTimestampAsDate(),
                                     close.getTimestampAsDate(),
                                     new Date(),
                                     metc);
            }
        };
        // proving that the close is not required
        verifyOHLC(new OpenHighLowClose(open,
                                        high,
                                        low,
                                        null,
                                        open.getTimestampAsDate(),
                                        low.getTimestampAsDate(),
                                        new Date(open.getTimeMillis() - 1),
                                        metc),
                   open,
                   high,
                   low,
                   null,
                   open.getTimestampAsDate(),
                   low.getTimestampAsDate());
        // same setup, but if the optional close is provided, it has to be valid
        new ExpectedFailure<IllegalArgumentException>(null) {
            @Override
            protected void run()
                    throws Exception
            {
                new OpenHighLowClose(open,
                                     high,
                                     low,
                                     badSymbolTrade,
                                     open.getTimestampAsDate(),
                                     new Date(low.getTimeMillis()+1),
                                     new Date(low.getTimeMillis()-1),
                                     metc);
            }
        };
    }
    /**
     * Tests that an event may not have a price higher than the high price nor lower than
     * the low price.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void lowerThanLowHigherThanHigh()
        throws Exception
    {
        high = EventBaseTest.generateTradeEvent(metc,
                                                exchange1,
                                                BigDecimal.TEN);
        low = EventBaseTest.generateTradeEvent(metc,
                                               exchange1,
                                               new BigDecimal(5));
        BigDecimal[] highPrices = new BigDecimal[] { high.getPrice().subtract(BigDecimal.ONE),
                                                     high.getPrice(),
                                                     high.getPrice().add(BigDecimal.ONE) };
        BigDecimal[] lowPrices = new BigDecimal[] { low.getPrice().subtract(BigDecimal.ONE),
                                                    low.getPrice(),
                                                    low.getPrice().add(BigDecimal.ONE) };
        // create blocks of events with each of the high and low prices above
        TradeEvent[] opens = new TradeEvent[highPrices.length + lowPrices.length];
        TradeEvent[] highs = new TradeEvent[highPrices.length + lowPrices.length];
        TradeEvent[] lows = new TradeEvent[highPrices.length + lowPrices.length];
        TradeEvent[] closes = new TradeEvent[highPrices.length + lowPrices.length];
        for(int i=0;i<highPrices.length;i++) {
            opens[i] = EventBaseTest.generateTradeEvent(metc,
                                                        exchange1,
                                                        highPrices[i]);
            highs[i] = EventBaseTest.generateTradeEvent(metc,
                                                        exchange1,
                                                        highPrices[i]);
            lows[i] = EventBaseTest.generateTradeEvent(metc,
                                                       exchange1,
                                                       highPrices[i]);
            closes[i] = EventBaseTest.generateTradeEvent(metc,
                                                         exchange1,
                                                         highPrices[i]);
        }
        for(int i=0;i<lowPrices.length;i++) {
            opens[i+highPrices.length] = EventBaseTest.generateTradeEvent(metc,
                                                                          exchange1,
                                                                          lowPrices[i]);
            highs[i+highPrices.length] = EventBaseTest.generateTradeEvent(metc,
                                                                          exchange1,
                                                                          lowPrices[i]);
            lows[i+highPrices.length] = EventBaseTest.generateTradeEvent(metc,
                                                                         exchange1,
                                                                         lowPrices[i]);
            closes[i+highPrices.length] = EventBaseTest.generateTradeEvent(metc,
                                                                           exchange1,
                                                                           lowPrices[i]);
        }
       // adjust interval end to after the latest trade event
        end = new Date(closes[closes.length-1].getTimeMillis() + 1);
        for(int openCounter=0;openCounter<opens.length;openCounter++) {
            for(int highCounter=0;highCounter<highs.length;highCounter++) {
                for(int lowCounter=0;lowCounter<lows.length;lowCounter++) {
                    for(int closeCounter=0;closeCounter<closes.length;closeCounter++) {
                        final TradeEvent openToUse = opens[openCounter];
                        final TradeEvent highToUse = highs[highCounter];
                        final TradeEvent lowToUse = lows[lowCounter];
                        final TradeEvent closeToUse = closes[closeCounter];
                        SLF4JLoggerProxy.debug(this,
                                               "{} {} {} {}",
                                               openCounter,
                                               highCounter,
                                               lowCounter,
                                               closeCounter);
                        if(openToUse.getPrice().intValue() > highToUse.getPrice().intValue() ||
                           lowToUse.getPrice().intValue() > highToUse.getPrice().intValue() ||
                           closeToUse.getPrice().intValue() > highToUse.getPrice().intValue() ||
                           openToUse.getPrice().intValue() < lowToUse.getPrice().intValue() ||
                           highToUse.getPrice().intValue() < lowToUse.getPrice().intValue() ||
                           closeToUse.getPrice().intValue() < lowToUse.getPrice().intValue()) {
                            // required close
                            new ExpectedFailure<IllegalArgumentException>(null) {
                                @Override
                                protected void run()
                                        throws Exception
                                {
                                    new OpenHighLowClose(openToUse,
                                                         highToUse,
                                                         lowToUse,
                                                         closeToUse,
                                                         start,
                                                         end,
                                                         new Date(end.getTime() + 1),
                                                         metc);
                                }
                            };
                            // optional close
                            new ExpectedFailure<IllegalArgumentException>(null) {
                                @Override
                                protected void run()
                                        throws Exception
                                {
                                    new OpenHighLowClose(openToUse,
                                                         highToUse,
                                                         lowToUse,
                                                         closeToUse,
                                                         start,
                                                         end,
                                                         new Date(end.getTime() - 1),
                                                         metc);
                                }
                            };
                        } else {
                            // required close
                            verifyOHLC(new OpenHighLowClose(openToUse,
                                                            highToUse,
                                                            lowToUse,
                                                            closeToUse,
                                                            start,
                                                            end,
                                                            new Date(end.getTime() + 1),
                                                            metc),
                                       openToUse,
                                       highToUse,
                                       lowToUse,
                                       closeToUse,
                                       start,
                                       end);
                            // optional close
                            verifyOHLC(new OpenHighLowClose(openToUse,
                                                            highToUse,
                                                            lowToUse,
                                                            closeToUse,
                                                            start,
                                                            end,
                                                            new Date(end.getTime() - 1),
                                                            metc),
                                       openToUse,
                                       highToUse,
                                       lowToUse,
                                       closeToUse,
                                       start,
                                       end);
                        }
                    }
                }            
            }
        }
    }
    /**
     * Tests that events cannot have a timestamp before the interval start or after the interval end.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void timeOutOfRange()
        throws Exception
    {
        Date[] starts = new Date[] { new Date(open.getTimeMillis() - 1),
                                     open.getTimestampAsDate(),
                                     new Date(open.getTimeMillis() + 1),
                                     new Date(close.getTimeMillis() - 1),
                                     close.getTimestampAsDate(),
                                     new Date(close.getTimeMillis() + 1) };
        Date[] ends = new Date[] { new Date(open.getTimeMillis() - 1),
                                   open.getTimestampAsDate(),
                                   new Date(open.getTimeMillis() + 1),
                                   new Date(close.getTimeMillis() - 1),
                                   close.getTimestampAsDate(),
                                   new Date(close.getTimeMillis() + 1) };
        for(int startCounter=0;startCounter<starts.length;startCounter++) {
            for(int endCounter=0;endCounter<ends.length;endCounter++) {
                SLF4JLoggerProxy.debug(this,
                                       "{} {}",
                                       startCounter,
                                       endCounter);
                final Date startToUse = starts[startCounter];
                final Date endToUse = ends[endCounter];
                if(open.getTimeMillis() < startToUse.getTime() ||
                   high.getTimeMillis() < startToUse.getTime() ||
                   low.getTimeMillis() < startToUse.getTime() ||
                   close.getTimeMillis() < startToUse.getTime() ||
                   open.getTimeMillis() > endToUse.getTime() ||
                   high.getTimeMillis() > endToUse.getTime() ||
                   low.getTimeMillis() > endToUse.getTime() ||
                   close.getTimeMillis() > endToUse.getTime()) {
                    new ExpectedFailure<IllegalArgumentException>(null) {
                        @Override
                        protected void run()
                                throws Exception
                        {
                            new OpenHighLowClose(open,
                                                 high,
                                                 low,
                                                 close,
                                                 startToUse,
                                                 endToUse,
                                                 new Date(endToUse.getTime() + 1),
                                                 metc);
                        }
                    };
                } else {
                    verifyOHLC(new OpenHighLowClose(open,
                                                    high,
                                                    low,
                                                    close,
                                                    startToUse,
                                                    endToUse,
                                                    new Date(endToUse.getTime() - 1),
                                                    metc),
                               open,
                               high,
                               low,
                               close,
                               startToUse,
                               endToUse);
                }
            }
        }
    }
    /**
     * Verifies that events can be from different exchanges in the same OHLC.
     *
     * @throws Exception
     */
    @Test
    public void wrongExchange()
        throws Exception
    {
        TradeEvent wrongOpen = EventBaseTest.generateTradeEvent(metc,
                                                                exchange2,
                                                                open.getPrice());
        TradeEvent wrongHigh = EventBaseTest.generateTradeEvent(metc,
                                                                exchange2,
                                                                high.getPrice());
        TradeEvent wrongLow = EventBaseTest.generateTradeEvent(metc,
                                                               exchange2,
                                                               low.getPrice());
        TradeEvent wrongClose = EventBaseTest.generateTradeEvent(metc,
                                                                 exchange2,
                                                                 close.getPrice());
        // reset end timestamp to make sure all events are in the correct interval
        end = new Date(wrongClose.getTimeMillis() + 1);
        // using mixed exchanges does not cause a problem
        verifyOHLC(new OpenHighLowClose(wrongOpen,
                                        high,
                                        low,
                                        close,
                                        start,
                                        end,
                                        new Date(),
                                        metc),
                   wrongOpen,
                   high,
                   low,
                   close,
                   start,
                   end);
        verifyOHLC(new OpenHighLowClose(open,
                                        wrongHigh,
                                        low,
                                        close,
                                        start,
                                        end,
                                        new Date(),
                                        metc),
                   open,
                   wrongHigh,
                   low,
                   close,
                   start,
                   end);
        verifyOHLC(new OpenHighLowClose(open,
                                        high,
                                        wrongLow,
                                        close,
                                        start,
                                        end,
                                        new Date(),
                                        metc),
                   open,
                   high,
                   wrongLow,
                   close,
                   start,
                   end);
        verifyOHLC(new OpenHighLowClose(open,
                                        high,
                                        low,
                                        wrongClose,
                                        start,
                                        end,
                                        new Date(),
                                        metc),
                   open,
                   high,
                   low,
                   wrongClose,
                   start,
                   end);
    }
    /**
     * Verifies that the given OHLC event contains the expected attributes. 
     *
     * @param inOHLC an <code>OpenHighLowClose</code> value
     * @param inExpectedOpen a <code>TradeEvent</code> value
     * @param inExpectedHigh a <code>TradeEvent</code> value
     * @param inExpectedLow a <code>TradeEvent</code> value
     * @param inExpectedClose a <code>TradeEvent</code> value
     * @param inExpectedStart a <code>Date</code> value
     * @param inExpectedEnd a <code>Date</code> value
     * @throws Exception if an error occurs
     */
    private static void verifyOHLC(final OpenHighLowClose inOHLC,
                                   TradeEvent inExpectedOpen,
                                   TradeEvent inExpectedHigh,
                                   TradeEvent inExpectedLow,
                                   TradeEvent inExpectedClose,
                                   Date inExpectedStart,
                                   Date inExpectedEnd)
        throws Exception
    {
        assertEquals(OrderBookTest.convertEvent(inExpectedOpen),
                     OrderBookTest.convertEvent(inOHLC.getOpen()));
        assertEquals(OrderBookTest.convertEvent(inExpectedHigh),
                     OrderBookTest.convertEvent(inOHLC.getHigh()));
        assertEquals(OrderBookTest.convertEvent(inExpectedLow),
                     OrderBookTest.convertEvent(inOHLC.getLow()));
        if(inExpectedClose == null) {
            assertNull(inOHLC.getClose());
        } else {
            assertEquals(OrderBookTest.convertEvent(inExpectedClose),
                         OrderBookTest.convertEvent(inOHLC.getClose()));
        }
        assertEquals(inExpectedStart,
                     inOHLC.getIntervalStart());
        assertEquals(inExpectedEnd,
                     inOHLC.getIntervalEnd());
        assertNotNull(inOHLC.toString());
        new ExpectedFailure<UnsupportedOperationException>(null) {
            @Override
            protected void run()
                    throws Exception
            {
                inOHLC.decompose();
            }
        };
    }
}
