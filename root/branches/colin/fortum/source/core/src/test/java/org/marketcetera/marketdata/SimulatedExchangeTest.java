package org.marketcetera.marketdata;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import org.apache.log4j.Level;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.marketcetera.core.LoggerConfiguration;
import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.event.*;
import org.marketcetera.event.impl.QuoteEventBuilder;
import org.marketcetera.event.impl.TradeEventBuilder;
import org.marketcetera.marketdata.SimulatedExchange.TopOfBook;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.options.ExpirationType;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.OptionType;
import org.marketcetera.util.test.CollectionAssert;
import org.marketcetera.util.test.TestCaseBase;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

/* $License$ */

/**
 * Tests {@link SimulatedExchange}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.5.0
 */
public class SimulatedExchangeTest
    extends TestCaseBase
{
    private SimulatedExchange exchange;
    private final Equity metc = new Equity("METC");
    private final Equity goog = new Equity("GOOG");
    private final Option metc1Put = new Option(metc.getSymbol(),
                                               "20100319",
                                               EventTestBase.generateDecimalValue(),
                                               OptionType.Put);
    private final Option metc1Call = new Option(metc1Put.getSymbol(),
                                                metc1Put.getExpiry(),
                                                metc1Put.getStrikePrice(),
                                                OptionType.Call);
    private final Option metc2Put = new Option(metc.getSymbol(),
                                               "20110319",
                                               EventTestBase.generateDecimalValue(),
                                               OptionType.Put);
    private final Option metc2Call = new Option(metc2Put.getSymbol(),
                                                metc2Put.getExpiry(),
                                                metc2Put.getStrikePrice(),
                                                OptionType.Call);
    private final Option goog1Put = new Option(goog.getSymbol(),
                                               "20100319",
                                               EventTestBase.generateDecimalValue(),
                                               OptionType.Put);
    private final Option goog1Call = new Option(goog1Put.getSymbol(),
                                                goog1Put.getExpiry(),
                                                goog1Put.getStrikePrice(),
                                                OptionType.Call);
    private BidEvent bid;
    private AskEvent ask;
    private static final AtomicLong counter = new AtomicLong(0);
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
     * Executed before each test.
     *
     * @throws Exception if an error occurs
     */
    @Before
    public void setup()
        throws Exception
    {
       exchange = new SimulatedExchange("Test exchange",
                                        "TEST");
       assertFalse(metc.equals(goog));
       bid = EventTestBase.generateEquityBidEvent(counter.incrementAndGet(),
                                                  System.currentTimeMillis(),
                                                  metc,
                                                  exchange.getCode(),
                                                  new BigDecimal("100"),
                                                  new BigDecimal("1000"));
       // intentionally creating a large spread to make sure no trades get executed
       ask = EventTestBase.generateEquityAskEvent(counter.incrementAndGet(),
                                                  System.currentTimeMillis(),
                                                  metc,
                                                  exchange.getCode(),
                                                  new BigDecimal("150"),
                                                  new BigDecimal("500"));
    }
    /**
     * Execute after each test. 
     *
     * @throws Exception if an error occurs
     */
    @After
    public void teardown()
        throws Exception
    {
        try {
            exchange.stop();
        } catch (Exception e) {}
    }
    /**
     * Tests starting and stopping exchange already started and stopped.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void redundantStartAndStop()
        throws Exception
    {
        new ExpectedFailure<IllegalStateException>() {
            @Override
            protected void run()
                    throws Exception
            {
                exchange.stop();
            }
        };
        exchange.start();
        new ExpectedFailure<IllegalStateException>() {
            @Override
            protected void run()
                    throws Exception
            {
                exchange.start();
            }
        };
    }
    /**
     * Tests the <code>SimulatedExchange</code> constructors.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void constructors()
        throws Exception
    {
        final String name = "name";
        final String code = "code";
        new ExpectedFailure<NullPointerException>() {
            @Override
            protected void run()
                    throws Exception
            {
                new SimulatedExchange(null,
                                      code);
            }
        };
        new ExpectedFailure<NullPointerException>() {
            @Override
            protected void run()
                    throws Exception
            {
                new SimulatedExchange(null,
                                      code,
                                      1);
            }
        };
        new ExpectedFailure<NullPointerException>() {
            @Override
            protected void run()
                    throws Exception
            {
                new SimulatedExchange(name,
                                      null);
            }
        };
        new ExpectedFailure<NullPointerException>() {
            @Override
            protected void run()
                    throws Exception
            {
                new SimulatedExchange(name,
                                      null,
                                      1);
            }
        };
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                new SimulatedExchange(name,
                                      code,
                                      -2);
            }
        };
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                new SimulatedExchange(name,
                                      code,
                                      0);
            }
        };
        verifyExchange(new SimulatedExchange(name,
                                             code),
                       name,
                       code);
        verifyExchange(new SimulatedExchange(name,
                                             code,
                                             100),
                       name,
                       code);
        verifyExchange(new SimulatedExchange(name,
                                             code,
                                             OrderBook.UNLIMITED_DEPTH),
                       name,
                       code);
    }
    /**
     * Tests snapshot requests.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void snapshots()
        throws Exception
    {
        new ExpectedFailure<NullPointerException>() {
            @Override
            protected void run()
                    throws Exception
            {
                exchange.getDepthOfBook(null);
            }
        };
        new ExpectedFailure<NullPointerException>() {
            @Override
            protected void run()
                    throws Exception
            {
                exchange.getTopOfBook(null);
            }
        };
        new ExpectedFailure<NullPointerException>() {
            @Override
            protected void run()
                    throws Exception
            {
                exchange.getLatestTick(null);
            }
        };
        // exchange is not started yet
        new ExpectedFailure<IllegalStateException>() {
            @Override
            protected void run()
                    throws Exception
            {
                exchange.getDepthOfBook(ExchangeRequestBuilder.newRequest().withInstrument(metc).create());
            }
        };
        new ExpectedFailure<IllegalStateException>() {
            @Override
            protected void run()
                    throws Exception
            {
                exchange.getTopOfBook(ExchangeRequestBuilder.newRequest().withInstrument(metc).create());
            }
        };
        new ExpectedFailure<IllegalStateException>() {
            @Override
            protected void run()
                    throws Exception
            {
                exchange.getLatestTick(ExchangeRequestBuilder.newRequest().withInstrument(metc).create());
            }
        };
        // start the exchange with a script with only one event for each side of the book
        List<QuoteEvent> script = new ArrayList<QuoteEvent>();
        script.add(bid);
        script.add(ask);
        exchange.start(script);
        // get the depth-of-book for the symbol
        List<AskEvent> asks = new ArrayList<AskEvent>();
        asks.add(ask);
        List<BidEvent> bids = new ArrayList<BidEvent>();
        bids.add(bid);
        verifySnapshots(exchange,
                        metc,
                        null,
                        asks,
                        bids,
                        null);
        // re-execute the same query (book already exists, make sure we're reading from the already existing book)
        verifySnapshots(exchange,
                        metc,
                        null,
                        asks,
                        bids,
                        null);
        // execute a request for an empty book
        verifySnapshots(exchange,
                        goog,
                        null,
                        new ArrayList<AskEvent>(),
                        new ArrayList<BidEvent>(),
                        null);
        exchange.stop();
        // start the exchange again in scripted mode, this time with events in opposition to each other
        script.add(EventTestBase.generateEquityBidEvent(counter.incrementAndGet(),
                                                        System.currentTimeMillis(),
                                                        metc,
                                                        exchange.getCode(),
                                                        ask.getPrice(),
                                                        ask.getSize()));
        script.add(EventTestBase.generateEquityAskEvent(counter.incrementAndGet(),
                                                        System.currentTimeMillis(),
                                                        metc,
                                                        exchange.getCode(),
                                                        bid.getPrice(),
                                                        bid.getSize()));
        exchange.start(script);
        // verify that the book is empty (but there should be an existing trade)
        verifySnapshots(exchange,
                        metc,
                        null,
                        new ArrayList<AskEvent>(),
                        new ArrayList<BidEvent>(),
                        EventTestBase.generateEquityTradeEvent(1,
                                                               1,
                                                               metc,
                                                               exchange.getCode(),
                                                               bid.getPrice(),
                                                               bid.getSize()));
        exchange.stop();
        // restart exchange in random mode
        exchange.start();
        // books are empty
        doRandomBookCheck(exchange,
                          metc, null);
        // re-execute (this time the book exists)
        doRandomBookCheck(exchange,
                          metc, null);
    }
    /**
     * Tests snapshots using options instead of equities.
     * 
     * <p>Note that this method doesn't have to re-execute all
     * the equity tests - most of the code is identical.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void snapshotWithOptions()
            throws Exception
    {
        List<QuoteEvent> script = new ArrayList<QuoteEvent>();
        // set up a book for a few options in a chain
        BidEvent bid1 = EventTestBase.generateOptionBidEvent(metc1Put,
                                                             metc,
                                                             exchange.getCode());
        BidEvent bid2 = EventTestBase.generateOptionBidEvent(metc1Call,
                                                             metc,
                                                             exchange.getCode());
        BidEvent bid3 = EventTestBase.generateOptionBidEvent(metc2Put,
                                                             metc,
                                                             exchange.getCode());
        BidEvent bid4 = EventTestBase.generateOptionBidEvent(metc2Call,
                                                             metc,
                                                             exchange.getCode());
        script.add(bid1);
        script.add(bid2);
        script.add(bid3);
        script.add(bid4);
        // set up a few asks, too
        QuoteEventBuilder<AskEvent> askBuilder = QuoteEventBuilder.optionAskEvent();
        askBuilder.withExchange(exchange.getCode())
                  .withExpirationType(ExpirationType.AMERICAN)
                  .withQuoteDate(DateUtils.dateToString(new Date()))
                  .withUnderlyingInstrument(metc);
        askBuilder.withInstrument(metc1Put);
        // create an ask that is more than the bid to prevent a trade occurring (keeps the top populated)
        askBuilder.withPrice(bid1.getPrice().add(BigDecimal.ONE)).withSize(bid1.getSize());
        AskEvent ask1 = askBuilder.create();
        script.add(ask1);
        // and an ask that does cause a trade
        askBuilder.withInstrument(metc2Put);
        askBuilder.withPrice(bid3.getPrice()).withSize(bid3.getSize());
        AskEvent ask2 = askBuilder.create();
        script.add(ask2);
        // there should now be books for the underlying (metc) and 4 entries in the chain (metc1Put, metc1Call, metc2Put, and metc2Call)
        exchange.start(script);
        // verify the state of the options
        verifySnapshots(exchange,
                        metc1Put,
                        metc,
                        Arrays.asList(new AskEvent[] { ask1 } ),
                        Arrays.asList(new BidEvent[] { bid1 } ),
                        null);
        verifySnapshots(exchange,
                        metc1Call,
                        metc,
                        Arrays.asList(new AskEvent[] { } ),
                        Arrays.asList(new BidEvent[] { bid2 } ),
                        null);
        TradeEvent trade = TradeEventBuilder.tradeEvent(metc2Put).withExchange(bid3.getExchange())
                                                                 .withExpirationType(((OptionEvent)bid3).getExpirationType())
                                                                 .withPrice(bid3.getPrice())
                                                                 .withSize(bid3.getSize())
                                                                 .withTradeDate(bid3.getQuoteDate())
                                                                 .withUnderlyingInstrument(metc).create();
        verifySnapshots(exchange,
                        metc2Put,
                        metc,
                        Arrays.asList(new AskEvent[] { } ),
                        Arrays.asList(new BidEvent[] { } ),
                        trade);
        verifySnapshots(exchange,
                        metc2Call,
                        metc,
                        Arrays.asList(new AskEvent[] { } ),
                        Arrays.asList(new BidEvent[] { bid4 } ),
                        null);
        // generate expected results for verifying snapshots for the underlying instrument
        Map<Instrument,InstrumentState> expectedResults = new HashMap<Instrument,InstrumentState>();
        expectedResults.put(metc1Put,
                            new InstrumentState(Arrays.asList(new BidEvent[] { bid1 }),
                                                Arrays.asList(new AskEvent[] { ask1 }),
                                                null));
        expectedResults.put(metc1Call,
                            new InstrumentState(Arrays.asList(new BidEvent[] { bid2 }),
                                                Arrays.asList(new AskEvent[] { }),
                                                null));
        expectedResults.put(metc2Put,
                            new InstrumentState(Arrays.asList(new BidEvent[] { }),
                                                Arrays.asList(new AskEvent[] { }),
                                                trade));
        expectedResults.put(metc2Call,
                            new InstrumentState(Arrays.asList(new BidEvent[] { bid4 }),
                                                Arrays.asList(new AskEvent[] { }),
                                                null));
        verifyUnderlyingSnapshots(exchange,
                                  metc,
                                  expectedResults);
        // restart exchange in random mode
        exchange.stop();
        exchange.start();
        // books are empty
        // start traffic for each of the options in the metc chain
        doRandomBookCheck(exchange,
                          metc1Put,
                          metc);
        doRandomBookCheck(exchange,
                          metc1Call,
                          metc);
        doRandomBookCheck(exchange,
                          metc2Put,
                          metc);
        doRandomBookCheck(exchange,
                          metc2Call,
                          metc);
        doRandomBookCheck(exchange,
                          goog1Put,
                          goog);
        doRandomBookCheck(exchange,
                          goog1Call,
                          goog);
        // re-execute (this time the books exist)
        doRandomBookCheck(exchange,
                          metc1Put,
                          metc);
        doRandomBookCheck(exchange,
                          metc1Call,
                          metc);
        doRandomBookCheck(exchange,
                          metc2Put,
                          metc);
        doRandomBookCheck(exchange,
                          metc2Call,
                          metc);
        doRandomBookCheck(exchange,
                          goog1Put,
                          goog);
        doRandomBookCheck(exchange,
                          goog1Call,
                          goog);
        exchange.stop();
    }
    /**
     * Tests that the <code>FilteringSubscriber</code> correctly tracks top-of-book state for
     * different option chain members of the same underlying instrument.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void subscriptionsWithOptions()
            throws Exception
    {
        List<QuoteEvent> script = new ArrayList<QuoteEvent>();
        // set up a book for a few options in a chain
        BidEvent bid1 = EventTestBase.generateOptionBidEvent(metc1Put,
                                                             metc,
                                                             exchange.getCode());
        BidEvent bid2 = EventTestBase.generateOptionBidEvent(metc1Call,
                                                             metc,
                                                             exchange.getCode());
        script.add(bid1); // top1
        script.add(bid2); // top2
        // there are two entries in the option chain for metc
        // add an ask for just one instrument in the option chain - make sure the bid and the ask don't match
        QuoteEventBuilder<AskEvent> askBuilder = QuoteEventBuilder.optionAskEvent();
        askBuilder.withExchange(exchange.getCode())
                  .withExpirationType(ExpirationType.AMERICAN)
                  .withQuoteDate(DateUtils.dateToString(new Date()))
                  .withUnderlyingInstrument(metc);
        askBuilder.withInstrument(bid2.getInstrument());
        // create an ask that is more than the bid to prevent a trade occurring (keeps the top populated)
        askBuilder.withPrice(bid2.getPrice().add(BigDecimal.ONE)).withSize(bid2.getSize());
        AskEvent ask1 = askBuilder.create();
        script.add(ask1); // top3
        // this creates a nice, two-sided top-of-book for the instrument
        // create a new ask for the same instrument that *won't* change the top - a new top should not be generated
        askBuilder.withPrice(bid2.getPrice().add(BigDecimal.TEN)).withSize(bid2.getSize());
        AskEvent ask2 = askBuilder.create();
        script.add(ask2); // no top4!
        // set up a subscriber to top-of-book for the underlying instrument metc
        TopOfBookSubscriber topOfBook = new TopOfBookSubscriber();
        exchange.getTopOfBook(ExchangeRequestBuilder.newRequest().withUnderlyingInstrument(metc).create(),
                              topOfBook);
        // start the exchange
        exchange.start(script);
        exchange.stop();
        // measure the tops collected by the subscriber
        // should have:
        //  a top for the bid1 instrument with just bid1
        //  a top for the bid2 instrument with just bid2
        //  a top for the bid2 instrument bid2-ask1
        //  no new top for ask2 - a total of three tops
        assertEquals(3,
                     topOfBook.getTops().size());
    }
    /**
     * Tests the ability to generate symbol statistics data.
     * 
     * <p>Note that testing this is made challenging by the random nature of data generation
     * for statistics.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void statistics()
        throws Exception
    {
        new ExpectedFailure<NullPointerException>() {
            @Override
            protected void run()
                    throws Exception
            {
                exchange.getStatistics(null);
            }
        };
        // exchange not started
        new ExpectedFailure<IllegalStateException>() {
            @Override
            protected void run()
                    throws Exception
            {
                exchange.getStatistics(ExchangeRequestBuilder.newRequest().withInstrument(metc).create());
            }
        };
        // done with error conditions
        exchange.start();
        // quantities are random, even for subsequent calls and scripted mode, but
        //  there are some conditions we can expect the values to adhere to
        for(int i=0;i<25000;i++) {
            verifyStatistics(exchange.getStatistics(ExchangeRequestBuilder.newRequest().withInstrument(metc).create()));
        }
        for(int i=0;i<25000;i++) {
            verifyStatistics(exchange.getStatistics(ExchangeRequestBuilder.newRequest().withInstrument(metc1Put)
                                                                                       .withUnderlyingInstrument(metc).create()));
        }
    }
    /**
     * Tests the ability to receive statistics in a subscription.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void statisticSubscriber()
        throws Exception
    {
        final AllEventsSubscriber equityStream = new AllEventsSubscriber();
        final AllEventsSubscriber optionStream = new AllEventsSubscriber();
        exchange.getStatistics(ExchangeRequestBuilder.newRequest().withInstrument(metc).create(),
                               equityStream);
        exchange.getStatistics(ExchangeRequestBuilder.newRequest().withInstrument(metc1Put)
                                                                  .withUnderlyingInstrument(metc).create(),
                               optionStream);
        exchange.start();
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                return equityStream.events.size() >= 15 &&
                       optionStream.events.size() >= 15;
            }
        });
        List<MarketstatEvent> stats = new ArrayList<MarketstatEvent>();
        for(Event event : equityStream.events) {
            if(event instanceof MarketstatEvent) {
                stats.add((MarketstatEvent)event);
            }
        }
        for(Event event : optionStream.events) {
            if(event instanceof MarketstatEvent) {
                stats.add((MarketstatEvent)event);
            }
        }
        verifyStatistics(stats);
    }
    /**
     * Tests the ability to generate symbol dividends data.
     * 
     * <p>Note that testing this is made challenging by the random nature of data generation
     * for dividends.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void dividends()
            throws Exception
    {
        new ExpectedFailure<NullPointerException>() {
            @Override
            protected void run()
                    throws Exception
            {
                exchange.getDividends(null);
            }
        };
        // exchange not started
        new ExpectedFailure<IllegalStateException>() {
            @Override
            protected void run()
                    throws Exception
            {
                exchange.getDividends(ExchangeRequestBuilder.newRequest().withInstrument(metc).create());
            }
        };
        exchange.start();
        // dividends for an option
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                exchange.getDividends(ExchangeRequestBuilder.newRequest().withInstrument(metc1Put)
                                                                         .withUnderlyingInstrument(metc).create());
            }
        };
        // dividends by underlying
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                exchange.getDividends(ExchangeRequestBuilder.newRequest().withUnderlyingInstrument(metc).create());
            }
        };
        for(int i=0;i<1000;i++) {
            Equity e = new Equity(String.format("equity-%s",
                                                i));
            verifyDividends(exchange.getDividends(ExchangeRequestBuilder.newRequest().withInstrument(e).create()),
                            e);
        }
    }
    /**
     * Tests the ability to receive dividends in a subscription.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void dividendSubscriber()
            throws Exception
    {
        final AllEventsSubscriber dividendStream = new AllEventsSubscriber();
        exchange.start();
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                Equity e = new Equity("e-" + counter.incrementAndGet());
                exchange.getDividends(ExchangeRequestBuilder.newRequest().withInstrument(e).create(),
                                      dividendStream);
                return dividendStream.events.size() >= 20;
            }
        });
        // sleep for a couple of ticks to make sure we don't get extra dividends (the same dividends sent more than once)
        Thread.sleep(5000);
        Multimap<Equity,DividendEvent> dividends = LinkedListMultimap.create();
        for(Event event : dividendStream.events) {
            DividendEvent dividendEvent = (DividendEvent)event;
            dividends.put(dividendEvent.getEquity(),
                          dividendEvent);
        }
        for(Equity equity : dividends.keySet()) {
            verifyDividends(new ArrayList<DividendEvent>(dividends.get(equity)),
                            equity);
        }
    }
    /**
     * Tests subscribing to market data from an exchange.
     * 
     * <p>This is a very complicated test due to the problem of generating complex expected
     * results.  Apologies in advance to anyone who has to review or modify this method.  The general
     * approach is to compile data structures that mimic the result (top-of-book, depth-of-book, etc)
     * using {@link QuantityTuple} objects that mirror {@link QuoteEvent} objects.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void subscriptions()
        throws Exception
    {
        new ExpectedFailure<NullPointerException>() {
            @Override
            protected void run()
                    throws Exception
            {
                exchange.getTopOfBook(ExchangeRequestBuilder.newRequest().withInstrument(metc).create(),
                                      null);
            }
        };
        // generate a script with a number of bids and asks
        BigDecimal baseValue = new BigDecimal("100.00");
        BigDecimal bidSize = new BigDecimal("100");
        BigDecimal askSize = new BigDecimal("50");
        List<QuoteEvent> script = new ArrayList<QuoteEvent>();
        List<BidEvent> bids = new ArrayList<BidEvent>();
        List<AskEvent> asks = new ArrayList<AskEvent>();
        for(int i=0;i<5;i++) {
            bids.add(EventTestBase.generateEquityBidEvent(counter.incrementAndGet(),
                                                          System.currentTimeMillis(),
                                                          metc,
                                                          exchange.getCode(),
                                                          baseValue.add(BigDecimal.ONE.multiply(new BigDecimal(i))),
                                                          bidSize));
        }
        for(int i=4;i>=0;i--) {
            asks.add(EventTestBase.generateEquityAskEvent(counter.incrementAndGet(),
                                                          System.currentTimeMillis(),
                                                          metc,
                                                          exchange.getCode(),
                                                          baseValue.add(BigDecimal.ONE.multiply(new BigDecimal(i))),
                                                          askSize));
        }
        script.addAll(bids);
        script.addAll(asks);
        // add one outrageous ask to make the book interesting
        AskEvent bigAsk = EventTestBase.generateEquityAskEvent(counter.incrementAndGet(),
                                                               System.currentTimeMillis(),
                                                               metc,
                                                               exchange.getCode(),
                                                               new BigDecimal(1000),
                                                               new BigDecimal(1000)); 
        script.add(bigAsk);
        // set up the subscriptions
        TopOfBookSubscriber topOfBook = new TopOfBookSubscriber();
        exchange.getTopOfBook(ExchangeRequestBuilder.newRequest().withInstrument(metc).create(),
                              topOfBook);
        AllEventsSubscriber tick = new AllEventsSubscriber();
        exchange.getLatestTick(ExchangeRequestBuilder.newRequest().withInstrument(metc).create(),
                               tick);
        AllEventsSubscriber depthOfBook = new AllEventsSubscriber();
        exchange.getDepthOfBook(ExchangeRequestBuilder.newRequest().withInstrument(metc).create(),
                                depthOfBook);
        // start the script
        exchange.start(script);
        // we can predict that the exchange will send 10 quote adds which will result in 5 trades and
        //  10 quote corrections (bid/ask del/chg), 25 events altogether
        // verify the results
        // this list will hold all the expected events
        List<QuantityTuple> allExpectedEvents = new ArrayList<QuantityTuple>();
        List<BookEntryTuple> expectedTopOfBook = new ArrayList<BookEntryTuple>();
        // the first events will be the bids
        for(BidEvent bid : bids) {
            QuantityTuple convertedBid = OrderBookTest.convertEvent(bid); 
            allExpectedEvents.add(convertedBid);
            expectedTopOfBook.add(new BookEntryTuple(convertedBid,
                                                     null));
        }
        /*
        bid        | ask
        -----------+---------
        100 104.00 |
        100 103.00 |
        100 102.00 |
        100 101.00 |
        100 100.00 |
        */
        // next will be the asks interleaved with trades and corrections as the books are settled after each ask
        allExpectedEvents.add(new QuantityTuple(new BigDecimal("104.00"), // 1st of 5 asks
                                                new BigDecimal("50"),
                                                AskEvent.class));
        /*
        bid        | ask
        -----------+-----------
        100 104.00 | 104.00 50
        100 103.00 | 
        100 102.00 | 
        100 101.00 | 
        100 100.00 |
         */
        allExpectedEvents.add(new QuantityTuple(new BigDecimal("104.00"), // resulting trade
                                                new BigDecimal("50"),
                                                TradeEvent.class));
        allExpectedEvents.add(new QuantityTuple(new BigDecimal("104.00"), // bid correction (change)
                                                new BigDecimal("50"),
                                                BidEvent.class));
        /*
        bid        | ask
        -----------+-----------
         50 104.00 | 104.00 50
        100 103.00 | 
        100 102.00 | 
        100 101.00 | 
        100 100.00 |
         */
        allExpectedEvents.add(new QuantityTuple(new BigDecimal("104.00"), // ask correction (delete)
                                                new BigDecimal("50"),
                                                AskEvent.class));
        /*
        bid        | ask
        -----------+-----------
         50 104.00 |
        100 103.00 | 
        100 102.00 | 
        100 101.00 | 
        100 100.00 |
         */
        allExpectedEvents.add(new QuantityTuple(new BigDecimal("103.00"), // 2nd of 5 asks
                                                new BigDecimal("50"),
                                                AskEvent.class));
        /*
        bid        | ask
        -----------+-----------
         50 104.00 | 103.00 50
        100 103.00 | 
        100 102.00 | 
        100 101.00 | 
        100 100.00 |
         */
        allExpectedEvents.add(new QuantityTuple(new BigDecimal("103.00"), // resulting trade
                                                new BigDecimal("50"),
                                                TradeEvent.class));
        allExpectedEvents.add(new QuantityTuple(new BigDecimal("104.00"), // bid correction (delete of fully consumed bid)
                                                new BigDecimal("50"),
                                                BidEvent.class));
        /*
        bid        | ask
        -----------+-----------
        100 103.00 | 103.00 50
        100 102.00 | 
        100 101.00 | 
        100 100.00 | 
         */
        allExpectedEvents.add(new QuantityTuple(new BigDecimal("103.00"), // ask correction (delete of fully consumed ask)
                                                new BigDecimal("50"),
                                                AskEvent.class));
        /*
        bid        | ask
        -----------+-----------
        100 103.00 |
        100 102.00 | 
        100 101.00 | 
        100 100.00 | 
         */
        allExpectedEvents.add(new QuantityTuple(new BigDecimal("102.00"), // 3rd of 5 asks
                                                new BigDecimal("50"),
                                                AskEvent.class));
        /*
        bid        | ask
        -----------+-----------
        100 103.00 | 102.00 50
        100 102.00 | 
        100 101.00 | 
        100 100.00 | 
         */
        allExpectedEvents.add(new QuantityTuple(new BigDecimal("102.00"), // resulting trade
                                                new BigDecimal("50"),
                                                TradeEvent.class));
        allExpectedEvents.add(new QuantityTuple(new BigDecimal("103.00"), // bid correction (change of partially consumed bid)
                                                new BigDecimal("50"),
                                                BidEvent.class));
        /*
        bid        | ask
        -----------+-----------
         50 103.00 | 102.00 50
        100 102.00 | 
        100 101.00 | 
        100 100.00 | 
         */
        allExpectedEvents.add(new QuantityTuple(new BigDecimal("102.00"), // ask correction (delete of fully consumed ask)
                                                new BigDecimal("50"),
                                                AskEvent.class));
        /*
        bid        | ask
        -----------+-----------
         50 103.00 | 
        100 102.00 | 
        100 101.00 | 
        100 100.00 | 
         */
        allExpectedEvents.add(new QuantityTuple(new BigDecimal("101.00"), // 4th of 5 asks
                                                new BigDecimal("50"),
                                                AskEvent.class));
        /*
        bid        | ask
        -----------+-----------
         50 103.00 | 101.00 50 
        100 102.00 | 
        100 101.00 | 
        100 100.00 | 
         */
        allExpectedEvents.add(new QuantityTuple(new BigDecimal("101.00"), // resulting trade
                                                new BigDecimal("50"),
                                                TradeEvent.class));
        allExpectedEvents.add(new QuantityTuple(new BigDecimal("103.00"), // bid correction (delete of fully consumed bid)
                                                new BigDecimal("50"),
                                                BidEvent.class));
        /*
        bid        | ask
        -----------+-----------
        100 102.00 | 101.00 50 
        100 101.00 | 
        100 100.00 | 
         */
        allExpectedEvents.add(new QuantityTuple(new BigDecimal("101.00"), // ask correction (delete of fully consumed ask)
                                                new BigDecimal("50"),
                                                AskEvent.class));
        /*
        bid        | ask
        -----------+-----------
        100 102.00 | 
        100 101.00 | 
        100 100.00 | 
         */
        allExpectedEvents.add(new QuantityTuple(new BigDecimal("100.00"), // 5th of 5 asks
                                                new BigDecimal("50"),
                                                AskEvent.class));
        /*
        bid        | ask
        -----------+-----------
        100 102.00 | 100.00 50 
        100 101.00 | 
        100 100.00 | 
         */
        allExpectedEvents.add(new QuantityTuple(new BigDecimal("100.00"), // resulting trade
                                                new BigDecimal("50"),
                                                TradeEvent.class));
        allExpectedEvents.add(new QuantityTuple(new BigDecimal("102.00"), // bid correction (change of partially consumed bid)
                                                new BigDecimal("50"),
                                                BidEvent.class));
        /*
        bid        | ask
        -----------+-----------
         50 102.00 | 100.00 50 
        100 101.00 | 
        100 100.00 | 
         */
        allExpectedEvents.add(new QuantityTuple(new BigDecimal("100.00"), // ask correction (delete of fully consumed ask)
                                                new BigDecimal("50"),
                                                AskEvent.class));
        /*
        bid        | ask
        -----------+-----------
         50 102.00 | 
        100 101.00 | 
        100 100.00 | 
         */
        allExpectedEvents.add(new QuantityTuple(bigAsk.getPrice(), // big ask
                                                bigAsk.getSize(),
                                                AskEvent.class));
        /*
        bid        | ask
        -----------+-------------
         50 102.00 | 1000.00 1000
        100 101.00 | 
        100 100.00 | 
         */
        // prepare the expected top-of-book results
        expectedTopOfBook.addAll(Arrays.asList(new BookEntryTuple[] { 
            /* 100 104.00 | 104.00 50 */
            new BookEntryTuple(new QuantityTuple(new BigDecimal("104.00"),
                                                 new BigDecimal("100"),
                                                 BidEvent.class),
                               new QuantityTuple(new BigDecimal("104.00"),
                                                 new BigDecimal("50"),
                                                 AskEvent.class)),
            /* 50 104.00 | 104.00 50 */
            new BookEntryTuple(new QuantityTuple(new BigDecimal("104.00"),
                                                 new BigDecimal("50"),
                                                 BidEvent.class),
                               new QuantityTuple(new BigDecimal("104.00"),
                                                 new BigDecimal("50"),
                                                 AskEvent.class)),
            /*  50 104.00 | */
            new BookEntryTuple(new QuantityTuple(new BigDecimal("104.00"),
                                                 new BigDecimal("50"),
                                                 BidEvent.class),
                               null),
            /* 50 104.00 | 103.00 50 */
            new BookEntryTuple(new QuantityTuple(new BigDecimal("104.00"),
                                                 new BigDecimal("50"),
                                                 BidEvent.class),
                               new QuantityTuple(new BigDecimal("103.00"),
                                                 new BigDecimal("50"),
                                                 AskEvent.class)),
           /* 100 103.00 | 103.00 50 */
           new BookEntryTuple(new QuantityTuple(new BigDecimal("103.00"),
                                                new BigDecimal("100"),
                                                BidEvent.class),
                              new QuantityTuple(new BigDecimal("103.00"),
                                                new BigDecimal("50"),
                                                AskEvent.class)),
          /* 100 103.00 | */
          new BookEntryTuple(new QuantityTuple(new BigDecimal("103.00"),
                                               new BigDecimal("100"),
                                               BidEvent.class),
                             null),
          /* 100 103.00 | 102.00 50 */
          new BookEntryTuple(new QuantityTuple(new BigDecimal("103.00"),
                                               new BigDecimal("100"),
                                               BidEvent.class),
                             new QuantityTuple(new BigDecimal("102.00"),
                                               new BigDecimal("50"),
                                               AskEvent.class)),
          /* 50 103.00 | 102.00 50  */
          new BookEntryTuple(new QuantityTuple(new BigDecimal("103.00"),
                                               new BigDecimal("50"),
                                               BidEvent.class),
                             new QuantityTuple(new BigDecimal("102.00"),
                                               new BigDecimal("50"),
                                               AskEvent.class)),
          /* 50 103.00 | */
          new BookEntryTuple(new QuantityTuple(new BigDecimal("103.00"),
                                               new BigDecimal("50"),
                                               BidEvent.class),
                             null),
          /* 50 103.00 | 101.00 50 */ 
          new BookEntryTuple(new QuantityTuple(new BigDecimal("103.00"),
                                               new BigDecimal("50"),
                                               BidEvent.class),
                             new QuantityTuple(new BigDecimal("101.00"),
                                               new BigDecimal("50"),
                                               AskEvent.class)),
         /* 100 102.00 | 101.00 50 */ 
         new BookEntryTuple(new QuantityTuple(new BigDecimal("102.00"),
                                              new BigDecimal("100"),
                                              BidEvent.class),
                            new QuantityTuple(new BigDecimal("101.00"),
                                              new BigDecimal("50"),
                                              AskEvent.class)),
         /* 100 102.00 | */
         new BookEntryTuple(new QuantityTuple(new BigDecimal("102.00"),
                                              new BigDecimal("100"),
                                              BidEvent.class),
                            null),
         /* 100 102.00 | 100.00 50 */
         new BookEntryTuple(new QuantityTuple(new BigDecimal("102.00"),
                                              new BigDecimal("100"),
                                              BidEvent.class),
                            new QuantityTuple(new BigDecimal("100.00"),
                                              new BigDecimal("50"),
                                              AskEvent.class)),
         /* 50 102.00 | 100.00 50 */
         new BookEntryTuple(new QuantityTuple(new BigDecimal("102.00"),
                                              new BigDecimal("50"),
                                              BidEvent.class),
                            new QuantityTuple(new BigDecimal("100.00"),
                                              new BigDecimal("50"),
                                              AskEvent.class)),
         /* 50 102.00 | */
         new BookEntryTuple(new QuantityTuple(new BigDecimal("102.00"),
                                              new BigDecimal("50"),
                                              BidEvent.class),
                            null),
         /* 50 102.00 | 1000 1000 */
         new BookEntryTuple(new QuantityTuple(new BigDecimal("102.00"),
                                              new BigDecimal("50"),
                                              BidEvent.class),
                            new QuantityTuple(new BigDecimal("1000"),
                                              new BigDecimal("1000"),
                                              AskEvent.class)),
        }));
        // prepare the expected latest tick results
        List<QuantityTuple> expectedLatestTicks = new ArrayList<QuantityTuple>();
        // prepare the expected depth-of-book results
        List<QuantityTuple> expectedDepthOfBook = new ArrayList<QuantityTuple>();
        for(QuantityTuple tuple : allExpectedEvents) {
            if(tuple.getType().equals(TradeEvent.class)) {
                expectedLatestTicks.add(tuple);
            } else {
                expectedDepthOfBook.add(tuple);
            }
        }
        // ready to verify results
        verifySubscriptions(topOfBook.getTops(),
                            expectedTopOfBook,
                            tick.events,
                            expectedLatestTicks,
                            depthOfBook.events,
                            expectedDepthOfBook);
    }
    /**
     * Tests that subscribers to different exchanges for the same type of data
     * get only the data they are supposed to.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void subscriptionTargeting()
        throws Exception
    {
        // create two different exchanges (one already exists, of course)
        SimulatedExchange exchange2 = new SimulatedExchange("Test exchange 2",
                                                            "TEST2");
        assertFalse(exchange.equals(exchange2));
        // create two different subscribers
        AllEventsSubscriber sub1 = new AllEventsSubscriber();
        AllEventsSubscriber sub2 = new AllEventsSubscriber();
        // set up the subscriptions
        exchange.getTopOfBook(ExchangeRequestBuilder.newRequest().withInstrument(metc).create(),
                              sub1);
        exchange2.getTopOfBook(ExchangeRequestBuilder.newRequest().withInstrument(metc).create(),
                               sub2);
        // create an event targeted to the second exchange
        AskEvent ask2 = EventTestBase.generateEquityAskEvent(counter.incrementAndGet(),
                                                                     System.currentTimeMillis(),
                                                                     metc,
                                                                     exchange2.getCode(),
                                                                     new BigDecimal("150"),
                                                                     new BigDecimal("500"));
        // create an event targeted to the second exchange but with the wrong symbol
        AskEvent ask3 = EventTestBase.generateEquityAskEvent(counter.incrementAndGet(),
                                                                     System.currentTimeMillis(),
                                                                     goog,
                                                                     exchange2.getCode(),
                                                                     new BigDecimal("150"),
                                                                     new BigDecimal("500"));
        // create two different scripts
        // the events that are targeted to the wrong exchange or symbol should get skipped
        List<QuoteEvent> script1 = new ArrayList<QuoteEvent>();
        script1.add(bid);
        script1.add(ask2);
        script1.add(ask3);
        List<QuoteEvent> script2 = new ArrayList<QuoteEvent>();
        script2.add(ask2);
        script2.add(bid);
        script2.add(ask3);
        // start the exchanges
        exchange.start(script1);
        exchange2.start(script2);
        // measure the results
        assertEquals(1,
                     sub1.events.size());
        assertEquals(bid,
                     sub1.events.get(0));
        assertEquals(1,
                     sub2.events.size());
        assertEquals(ask2,
                     sub2.events.get(0));
    }
    /**
     * Tests that an over-filled (according to its max depth) order book gets pruned.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void bookPruning()
        throws Exception
    {
        // create an exchange with a small maximum depth (2)
        exchange = new SimulatedExchange("Test exchange",
                                         "TEST",
                                         2);
        // create a script of events that will exceed the max on both sides of the book
        // note that the spread is intentionally large in order to prevent any trades
       List<QuoteEvent> script = new ArrayList<QuoteEvent>();
       List<AskEvent> asks = new ArrayList<AskEvent>();
       List<BidEvent> bids = new ArrayList<BidEvent>();
       for(int i=0;i<3;i++) {
           bids.add(EventTestBase.generateEquityBidEvent(counter.incrementAndGet(),
                                                         System.currentTimeMillis(),
                                                         metc,
                                                         exchange.getCode(),
                                                         new BigDecimal(10-i),
                                                         new BigDecimal("1000")));
           asks.add(EventTestBase.generateEquityAskEvent(counter.incrementAndGet(),
                                                         System.currentTimeMillis(),
                                                         metc,
                                                         exchange.getCode(),
                                                         new BigDecimal(100+i),
                                                         new BigDecimal("1000")));
       }
       script.addAll(bids);
       script.addAll(asks);
       exchange.start(script);
       // now, trim the oldest (first) bid and ask from each list to simulate the pruning
       bids.remove(0);
       asks.remove(0);
       // check the results
       verifyDepthOfBook(exchange.getDepthOfBook(ExchangeRequestBuilder.newRequest().withInstrument(metc).create()),
                         asks,
                         bids);
    }
    /**
     * Tests canceling a subscription.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void subscriptionCanceling()
        throws Exception
    {
        final AllEventsSubscriber stream1 = new AllEventsSubscriber();
        final AllEventsSubscriber stream2 = new AllEventsSubscriber();
        SimulatedExchange.Token t1 = exchange.getTopOfBook(ExchangeRequestBuilder.newRequest().withInstrument(metc).create(),
                                                           stream1);
        SimulatedExchange.Token t2 = exchange.getTopOfBook(ExchangeRequestBuilder.newRequest().withInstrument(metc).create(),
                                                           stream2);
        assertFalse(t1.equals(t2));
        assertFalse(t1.hashCode() == t2.hashCode());
        exchange.start();
        // start the exchange in random mode (wait until a reasonable number of events comes in)
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                Thread.sleep(250);
                return stream1.events.size() >= 10;
            }
        });
        // t2 should have received at least the same number of events (won't be deterministically in
        //  sync)
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                Thread.sleep(250);
                return stream2.events.size() >= 10;
            }
        });
        // both subscribers have now received at least 10 events (this shows us that they're
        //  both receiving events)
        // now, cancel one subscription - note that since it's async, we can't guarantee that
        //  no more than 10 events will come (one may have come in even while you read this
        //  comment)
        t1.cancel();
        // some time very shortly (certainly in the next minute), updates should stop coming in
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                int currentCount = stream1.events.size();
                // at least 2 events should come in every second, so by waiting 2.5 seconds,
                //  we should be able to tell with a reasonable degree of confidence that
                //  no new events are coming in
                Thread.sleep(2500);
                return stream1.events.size() == currentCount;
            }
        });
        int stream1Count = stream1.events.size();
        int stream2Count = stream2.events.size();
        // stream2 is still receiving events, but stream1 is not
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                Thread.sleep(250);
                return stream2.events.size() >= 20;
            }
        });
        // the size of stream2 has grown
        assertTrue(stream2.events.size() >= stream2Count);
        // the size of stream1 has not
        assertEquals(stream1Count,
                     stream1.events.size());
        // cancel the same thing again just to make sure nothing flies off the handle
        t1.cancel();
    }
    /**
     * Tests the ability to subscribe before and after the exchange starts.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void subscribeBeforeAndAfterStart()
        throws Exception
    {
        final AllEventsSubscriber stream1 = new AllEventsSubscriber();
        final AllEventsSubscriber stream2 = new AllEventsSubscriber();
        exchange.getTopOfBook(ExchangeRequestBuilder.newRequest().withInstrument(metc).create(),
                              stream1);
        exchange.start();
        exchange.getTopOfBook(ExchangeRequestBuilder.newRequest().withInstrument(metc).create(),
                              stream2);
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                Thread.sleep(250);
                return stream1.events.size() >= 10 &&
                       stream2.events.size() >= 10;
            }
        });
    }
    /**
     * Tests that a scripted exchange doesn't start new activity after running its
     * script.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void otherSymbolsFromScriptedExchange()
        throws Exception
    {
        // start the exchange in scripted mode with two events for METC
        List<QuoteEvent> script = new ArrayList<QuoteEvent>();
        script.add(bid);
        script.add(ask);
        exchange.start(script);
        // allow the exchange the opportunity to do something off the script, if it's going to
        Thread.sleep(5000);
        // verify the top for METC
        verifyTopOfBook(makeTopOfBook(exchange.getTopOfBook(ExchangeRequestBuilder.newRequest().withInstrument(metc).create())),
                        ask,
                        bid);
        // verify that nothing's there for GOOG
        assertTrue(exchange.getTopOfBook(ExchangeRequestBuilder.newRequest().withInstrument(goog).create()).isEmpty());
    }
    /**
     * Tests that exchanges initially have closely grouped values for the same symbol.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void valueSync()
        throws Exception
    {
        // create two extra exchanges
        final SimulatedExchange exchange2 = new SimulatedExchange("Test Exchange2",
                                                                  "TEST2");
        final SimulatedExchange exchange3 = new SimulatedExchange("Test Exchange3",
                                                                  "TEST3");
        // start all three in random mode (note that exchange-sync behavior does not apply to scripted mode)
        exchange.start();
        exchange2.start();
        exchange3.start();
        // all three are ticking over, but the books are not yet populated
        // wait until the book is populated for METC
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                return exchange.getTopOfBook(ExchangeRequestBuilder.newRequest().withInstrument(metc).create()).size() == 2;
            }
        });
        // the book for METC has at least an ask and bid in it, grab the value
        List<QuoteEvent> top1 = exchange.getTopOfBook(ExchangeRequestBuilder.newRequest().withInstrument(metc).create());
        // now, issue the same request for exchange2
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                return exchange2.getTopOfBook(ExchangeRequestBuilder.newRequest().withInstrument(metc).create()).size() == 2;
            }
        });
        List<QuoteEvent> top2 = exchange2.getTopOfBook(ExchangeRequestBuilder.newRequest().withInstrument(metc).create());
        // and exchange3
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                return exchange3.getTopOfBook(ExchangeRequestBuilder.newRequest().withInstrument(metc).create()).size() == 2;
            }
        });
        List<QuoteEvent> top3 = exchange3.getTopOfBook(ExchangeRequestBuilder.newRequest().withInstrument(metc).create());
        // we cannot guarantee how many ticks (if any) have happened during the course of these
        //  instructions.  regardless, each of the exchanges has
        //  a book for METC with at least a bid and ask.  it's unlikely they're exactly the same, but
        //  they can have varied by no more than .01/second.  the absolute maximum that is possible is limited
        //  by about 3 minutes (each of the wait calls could take just under 60 seconds without failing).
        //  so, we'll say that the most the values could have changed is 2.50 (that's equivalent to
        //  a bit over 4 minutes of run time with one tick/second).
        // the default book start values vary between (0.01,99.99), the odds of three values hitting the
        //  same 5.00 interval randomly without sync are not large, about 1.25 in 10,000).  the worst case for
        //  this test is a false negative, that is, sync isn't working and all three books randomly start
        //  in the same interval.  since this should happen a little more often than once in 10,000, that
        //  makes this test effective enough.
        assertTrue(((MarketDataEvent)top1.get(0)).getPrice().subtract(((MarketDataEvent)top2.get(0)).getPrice()).abs().intValue() < 3);
        assertTrue(((MarketDataEvent)top1.get(0)).getPrice().subtract(((MarketDataEvent)top3.get(0)).getPrice()).abs().intValue() < 3);
    }
    /**
     * Tests that the exchange data structures are protected against concurrent access violations.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void concurrency()
        throws Exception
    {
        try {
            TestCaseBase.setLevel(SimulatedExchange.class.getName(),
                                  Level.WARN);
            // start the exchange in random mode
            exchange.start();
            // execute a number of data requests, making sure that the requests span a few tick executions
            long startTime = System.currentTimeMillis();
            long currentTime = System.currentTimeMillis();
            int counter = 0;
            while(currentTime-startTime < 10000) {
                Equity symbol = new Equity(String.format("symbol-%d",
                                                           counter++));
                exchange.getStatistics(ExchangeRequestBuilder.newRequest().withInstrument(symbol).create());
                exchange.getDepthOfBook(ExchangeRequestBuilder.newRequest().withInstrument(symbol).create());
                exchange.getLatestTick(ExchangeRequestBuilder.newRequest().withInstrument(symbol).create());
                exchange.getTopOfBook(ExchangeRequestBuilder.newRequest().withInstrument(symbol).create());
                currentTime = System.currentTimeMillis();
            }
            assertNoEvents();
        } finally {
            TestCaseBase.setLevel(SimulatedExchange.class.getName(),
                                  Level.INFO);
        }
    }
    /**
     * Tests the output of the exchange in random mode.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void randomModeOutput()
            throws Exception
    {
        exchange.start();
        // start the exchange in random mode
        final AllEventsSubscriber all = new AllEventsSubscriber();
        exchange.getTopOfBook(ExchangeRequestBuilder.newRequest().withInstrument(metc).create(),
                              all);
        // this block should actually take less than 30s - there are 2 new events/sec and every now and then
        //  a trade will create a few extras
        MarketDataFeedTestBase.wait(new Callable<Boolean>(){
            @Override
            public Boolean call()
                    throws Exception
            {
                return all.events.size() >= 60;
            }
        });
        exchange.stop();
    }
    /**
     * Executes a test to make sure that the given <code>Instrument</code> and underlying <code>Instrument</code>
     * get order books created for them.
     *
     * @param inExchange a <code>SimulatedExchange</code> value
     * @param inInstrument an <code>Instrument</code> value
     * @param inUnderlyingInstrument an <code>Instrument</code> value
     * @throws Exception if an unexpected error occurs
     */
    private void doRandomBookCheck(SimulatedExchange inExchange,
                                   Instrument inInstrument,
                                   Instrument inUnderlyingInstrument)
            throws Exception
    {
        List<QuoteEvent> dob = inExchange.getDepthOfBook(ExchangeRequestBuilder.newRequest().withInstrument(inInstrument)
                                                                                            .withUnderlyingInstrument(inUnderlyingInstrument).create());
        // note that since the exchange was started this time in random mode we don't know exactly what the
        //  values will be, but there should be at least one entry on each side of the book
        assertFalse(dob.isEmpty());
        boolean foundAsk = false;
        boolean foundBid = false;
        for(Event event : dob) {
            if(event instanceof BidEvent) {
                foundBid = true;
            } else if(event instanceof AskEvent) {
                foundAsk = true;
            }
        }
        assertTrue(foundBid);
        assertTrue(foundAsk);
        // repeat, checking by underlying instrument only
        if(inUnderlyingInstrument != null) {
            dob = inExchange.getDepthOfBook(ExchangeRequestBuilder.newRequest().withUnderlyingInstrument(inUnderlyingInstrument).create());
            assertFalse(dob.isEmpty());
            foundAsk = false;
            foundBid = false;
            for(Event event : dob) {
                if(event instanceof BidEvent) {
                    foundBid = true;
                } else if(event instanceof AskEvent) {
                    foundAsk = true;
                }
            }
            assertTrue(foundBid);
            assertTrue(foundAsk);
        }
    }
    /**
     * Verifies the given actual subscriptions against the expected results. 
     *
     * @param inActualTopOfBook a <code>List&lt;Pair&lt;BidEvent,AskEvent&gt;&gt;</code> value
     * @param inExpectedTopOfBook a <code>List&lt;BookEntryTuple&gt;</code> value
     * @param inActualTicks a <code>List&lt;EventBase&gt;</code> value
     * @param inExpectedTicks a <code>List&lt;QuantityTuple&gt;</code> value
     * @param inActualDepthOfBook a <code>List&lt;EventBase&gt;</code> value
     * @param inExpectedDepthOfBook a <code>List&lt;QuantityTuple&gt;</code> value
     * @throws Exception if an error occurs
     */
    private void verifySubscriptions(List<TopOfBook> inActualTopOfBook,
                                     List<BookEntryTuple> inExpectedTopOfBook,
                                     List<Event> inActualTicks,
                                     List<QuantityTuple> inExpectedTicks,
                                     List<Event> inActualDepthOfBook,
                                     List<QuantityTuple> inExpectedDepthOfBook)
        throws Exception
    {
        // test top-of-book
        assertEquals(inExpectedTopOfBook.size(),
                     inActualTopOfBook.size());
        List<BookEntryTuple> actualTopOfBook = new ArrayList<BookEntryTuple>();
        for(TopOfBook top : inActualTopOfBook) {
            actualTopOfBook.add(new BookEntryTuple(OrderBookTest.convertEvent(top.getFirstMember()),
                                                   OrderBookTest.convertEvent(top.getSecondMember())));
        }
        assertEquals(inExpectedTopOfBook,
                     actualTopOfBook);
        // test latest-tick
        assertEquals(inExpectedTicks,
                     OrderBookTest.convertEvents(inActualTicks));
        // test depth-of-book
        assertEquals(inExpectedDepthOfBook.size(),
                     inActualDepthOfBook.size());
        assertEquals(inExpectedDepthOfBook,
                     OrderBookTest.convertEvents(inActualDepthOfBook));
    }
    /**
     * Verifies symbol statistical data.
     *
     * @param inStatistics a <code>List&lt;MarketstatEvent&gt;</code> value containing the actual value
     * @throws Exception if an error occurs
     */
    private void verifyStatistics(List<MarketstatEvent> inStatistics)
            throws Exception
    {
        for(MarketstatEvent stat : inStatistics)  {
            assertNotNull(stat.getOpen());
            assertNotNull(stat.getHigh());
            assertNotNull(stat.getLow());
            assertNotNull(stat.getClose());
            assertNotNull(stat.getPreviousClose());
            assertNotNull(stat.getVolume());
            assertNotNull(stat.getCloseDate());
            assertNotNull(stat.getPreviousCloseDate());
            assertNotNull(stat.getTradeHighTime());
            assertNotNull(stat.getTradeLowTime());
            assertNotNull(stat.getOpenExchange());
            assertNotNull(stat.getCloseExchange());
            assertNotNull(stat.getHighExchange());
            assertNotNull(stat.getLowExchange());
            Instrument instrument = stat.getInstrument();
            if(instrument instanceof Option) {
                assertNotNull(((OptionMarketstatEvent)stat).getInterestChange());
                assertNotNull(((OptionMarketstatEvent)stat).getVolumeChange());
            }
        }
    }
    /**
     * Verifies symbol dividends.
     *
     * @param inDividends a <code>List&lt;Marketstatevent&gt;</code> value
     * @param inEquity an <code>Equity</code> value
     * @throws Exception if an unexpected error occurs
     */
    private void verifyDividends(List<DividendEvent> inDividends,
                                 Equity inEquity)
            throws Exception
    {
        // make sure that the dividends we got match the dividends we get a second
        //  time for the same equity
        assertEquals(inDividends,
                     exchange.getDividends(ExchangeRequestBuilder.newRequest().withInstrument(inEquity).create()));
        if(!inDividends.isEmpty()) {
            assertEquals(4,
                         inDividends.size());
            DividendEvent currentDividend = inDividends.get(0);
            assertTrue(currentDividend.getAmount().compareTo(BigDecimal.ZERO) == 1);
            assertEquals("USD",
                         currentDividend.getCurrency());
            assertEquals(inEquity,
                         currentDividend.getEquity());
            Date today = new Date();
            assertNotNull(currentDividend.getDeclareDate());
            assertTrue(today.after(DateUtils.stringToDate(currentDividend.getDeclareDate())));
            assertNotNull(currentDividend.getExecutionDate());
            assertTrue(today.after(DateUtils.stringToDate(currentDividend.getExecutionDate())));
            assertNotNull(currentDividend.getPaymentDate());
            assertTrue(today.after(DateUtils.stringToDate(currentDividend.getPaymentDate())));
            assertNotNull(currentDividend.getRecordDate());
            assertTrue(today.after(DateUtils.stringToDate(currentDividend.getRecordDate())));
            assertEquals(DividendFrequency.QUARTERLY,
                         currentDividend.getFrequency());
            assertEquals(DividendStatus.OFFICIAL,
                         currentDividend.getStatus());
            assertEquals(DividendType.CURRENT,
                         currentDividend.getType());
            // now check future dividends
            for(int counter=1;counter<=3;counter++) {
                DividendEvent futureDividend = inDividends.get(counter);
                assertTrue(futureDividend.getAmount().compareTo(BigDecimal.ZERO) == 1);
                assertEquals("USD",
                             futureDividend.getCurrency());
                assertEquals(inEquity,
                             futureDividend.getEquity());
                assertEquals(DividendFrequency.QUARTERLY,
                             futureDividend.getFrequency());
                assertEquals(DividendStatus.UNOFFICIAL,
                             futureDividend.getStatus());
                assertEquals(DividendType.FUTURE,
                             futureDividend.getType());
                assertNull(futureDividend.getDeclareDate());
                assertNull(futureDividend.getPaymentDate());
                assertNull(futureDividend.getRecordDate());
                String executionDate = futureDividend.getExecutionDate();
                assertNotNull(executionDate);
                assertTrue(today.before(DateUtils.stringToDate(executionDate)));
            }
        }
    }
    /**
     * Verifies that the given exchange and symbol will produce the expected snapshots. 
     *
     * @param inExchange a <code>SimulatedExchange</code> value
     * @param inInstrument an <code>Instrument</code> value
     * @param inUnderlyingInstrument an <code>Instrument</code> value or <code>null</code>
     * @param inExpectedAsks a <code>List&lt;AskEvent&gt;</code> value
     * @param inExpectedBids a <code>List&lt;BidEvent&gt;</code> value
     * @throws Exception if an error occurs
     */
    private void verifySnapshots(SimulatedExchange inExchange,
                                 Instrument inInstrument,
                                 Instrument inUnderlyingInstrument,
                                 List<AskEvent> inExpectedAsks,
                                 List<BidEvent> inExpectedBids,
                                 TradeEvent inExpectedLatestTick)
        throws Exception
    {
        ExchangeRequest request = ExchangeRequestBuilder.newRequest().withInstrument(inInstrument)
                                                                     .withUnderlyingInstrument(inUnderlyingInstrument).create();
        verifyDepthOfBook(inExchange.getDepthOfBook(request),
                          inExpectedAsks,
                          inExpectedBids);
        verifyTopOfBook(makeTopOfBook(inExchange.getTopOfBook(request)),
                        inExpectedAsks.isEmpty() ? null : inExpectedAsks.get(0),
                        inExpectedBids.isEmpty() ? null : inExpectedBids.get(0));
        assertEquals(OrderBookTest.convertEvent(inExpectedLatestTick),
                     OrderBookTest.convertEvent(exchange.getLatestTick(request).get(0)));
    }
    /**
     * Verifies that the underlying instrument leads to the given expected states in the
     * given exchange.
     *
     * @param inExchange a <code>SimulatedExchange</code> value
     * @param inUnderlyingInstrument an <code>Instrument</code> value
     * @param inExpectedStates a <code>Map&lt;Instrument,InstrumentState&gt;</code> value
     * @throws Exception if an unexpected error occurs
     */
    private void verifyUnderlyingSnapshots(SimulatedExchange inExchange,
                                           Instrument inUnderlyingInstrument,
                                           Map<Instrument,InstrumentState> inExpectedStates)
            throws Exception
    {
        ExchangeRequest request = ExchangeRequestBuilder.newRequest().withUnderlyingInstrument(inUnderlyingInstrument).create();
        EventOrganizer.process(inExchange.getTopOfBook(request),
                               EventOrganizer.RequestType.TOP_OF_BOOK);
        EventOrganizer.process(inExchange.getDepthOfBook(request),
                               EventOrganizer.RequestType.DEPTH_OF_BOOK);
        EventOrganizer.process(inExchange.getLatestTick(request),
                               EventOrganizer.RequestType.LATEST_TICK);
        EventOrganizer.process(inExchange.getStatistics(request),
                               EventOrganizer.RequestType.MARKET_STAT);
        // all the actual events have been collected and organized, examine them
        for(Map.Entry<Instrument,InstrumentState> entry : inExpectedStates.entrySet()) {
            InstrumentState expectedState = inExpectedStates.get(entry.getKey());
            EventOrganizer organizer = EventOrganizer.organizers.remove(entry.getKey());
            assertNotNull("No actual results for " + entry.getKey(),
                          organizer);
            verifyTopOfBook(organizer.getTop(),
                            expectedState.asks.isEmpty() ? null : expectedState.asks.get(0),
                            expectedState.bids.isEmpty() ? null : expectedState.bids.get(0));
            verifyDepthOfBook(organizer.depthOfBook,
                              expectedState.asks,
                              expectedState.bids);
            verifyStatistics(Arrays.asList(new MarketstatEvent[] { organizer.marketstat } ));
            assertEquals(OrderBookTest.convertEvent(organizer.latestTrade),
                         OrderBookTest.convertEvent(expectedState.latestTrade));
        }
    }
    /**
     * Creates a <code>TopOfBook</code> object from the given list.
     *
     * @param inEvents a <code>List&lt;QuoteEvent&gt;</code> value
     * @return a <code>TopOfBook</code> value
     * @throws Exception if an unexpected error occurs
     */
    private static TopOfBook makeTopOfBook(List<QuoteEvent> inEvents)
            throws Exception
    {
        assertTrue("An instrument should have a single top-of-book",
                   inEvents.size() <= 2);
        BidEvent bid = null;
        AskEvent ask = null;
        if(inEvents.size() == 2) {
            bid = (BidEvent)inEvents.get(0);
            ask = (AskEvent)inEvents.get(1);
        } else if(inEvents.size() == 1) {
            Event e = inEvents.get(0);
            if(e instanceof BidEvent) {
                bid = (BidEvent)e;
            } else if(e instanceof AskEvent) {
                ask = (AskEvent)e;
            } else {
                fail("Unknown contents in top-of-book: " + e);
            }
        }
        return new TopOfBook(bid,
                             ask);
    }
    /**
     * Verifies the given actual<code>TopOfBook</code> contains the expected values. 
     *
     * @param inActualTopOfBook a <code>TopOfBook</code> value
     * @param inAsk a <code>AskEvent</code> value
     * @param inBid a <code>BidEvent</code> value
     * @throws Exception if an error occurs
     */
    private void verifyTopOfBook(TopOfBook inActualTopOfBook,
                                 AskEvent inExpectedAsk,
                                 BidEvent inExpectedBid)
        throws Exception
    {
        assertEquals(OrderBookTest.convertEvent(inExpectedBid),
                     OrderBookTest.convertEvent(inActualTopOfBook.getBid()));
        assertEquals(OrderBookTest.convertEvent(inExpectedAsk),
                     OrderBookTest.convertEvent(inActualTopOfBook.getAsk()));
    }
    /**
     * Verifies that the given exchange has the given expected attributes.
     *
     * @param inActualExchange a <code>SimulatedExchange</code> value
     * @param inExpectedName a <code>String</code> value
     * @param inExpectedCode a <code>String</code> value
     * @throws Exception if an error occurs
     */
    private void verifyExchange(SimulatedExchange inActualExchange,
                                String inExpectedName,
                                String inExpectedCode)
        throws Exception
    {
        assertEquals(inExpectedName,
                     inActualExchange.getName());
        assertEquals(inExpectedCode,
                     inActualExchange.getCode());
    }
    /**
     * Verifies that the given <code>AggregateEvent</code> decomposes into the
     * given expected events.
     * 
     * <p>No guarantee is made as to the order of the events.
     *
     * @param inActualEvent an <code>AggregateEvent</code> value
     * @param inExpectedEvents a <code>List&lt;Event&gt;</code> value
     * @throws Exception if an error occurs
     */
    final static void verifyDecomposedEvents(AggregateEvent inActualEvent,
                                             List<Event> inExpectedEvents)
        throws Exception
    {
        CollectionAssert.assertArrayPermutation(inExpectedEvents.toArray(),
                                                inActualEvent.decompose().toArray());
    }
    /**
     * Verifies the given actual <code>DepthOfBook</code> contains the expected values.
     *
     * @param inActualDepthOfBook a <code>List&lt;QuoteEvent&gt;</code> value
     * @param inExpectedAsks a <code>List&lt;AskEvent&gt;</code> value
     * @param inExpectedBids a <code>List&lt;BidEvent&gt;</code> value
     * @throws Exception if an error occurs
     */
    public static void verifyDepthOfBook(List<QuoteEvent> inActualDepthOfBook,
                                         List<AskEvent> inExpectedAsks,
                                         List<BidEvent> inExpectedBids)
        throws Exception
    {
        List<BidEvent> actualBids = new ArrayList<BidEvent>();
        List<AskEvent> actualAsks = new ArrayList<AskEvent>();
        for(Event event : inActualDepthOfBook) {
            if(event instanceof BidEvent) {
                actualBids.add((BidEvent)event);
            } else if(event instanceof AskEvent)  {
                actualAsks.add((AskEvent)event);
            }
        }
        assertEquals(OrderBookTest.convertEvents(inExpectedAsks),
                     OrderBookTest.convertEvents(actualAsks));
        assertEquals(OrderBookTest.convertEvents(inExpectedBids),
                     OrderBookTest.convertEvents(actualBids));
    }
    /**
     * Subscribes to top-of-book and captures the state of the exchange top every time it changes.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 1.5.0
     */
    @ThreadSafe
    private static class TopOfBookSubscriber
        implements ISubscriber
    {
        /* (non-Javadoc)
         * @see org.marketcetera.core.publisher.ISubscriber#isInteresting(java.lang.Object)
         */
        @Override
        public boolean isInteresting(Object inData)
        {
            return inData instanceof QuoteEvent;
        }
        /* (non-Javadoc)
         * @see org.marketcetera.core.publisher.ISubscriber#publishTo(java.lang.Object)
         */
        @Override
        public synchronized void publishTo(Object inData)
        {
            QuoteEvent quote = (QuoteEvent)inData;
            BidEvent lastBid = lastBids.get(quote.getInstrument());
            AskEvent lastAsk = lastAsks.get(quote.getInstrument());
            BidEvent newBid = (quote instanceof BidEvent ? quote.getAction() == QuoteAction.DELETE ? null : (BidEvent)quote : lastBid);
            AskEvent newAsk = (quote instanceof AskEvent ? quote.getAction() == QuoteAction.DELETE ? null : (AskEvent)quote : lastAsk);
            tops.add(new TopOfBook(newBid,
                                   newAsk));
            lastBids.put(quote.getInstrument(),
                         newBid);
            lastAsks.put(quote.getInstrument(),
                         newAsk);
        }
        /**
         * 
         *
         *
         * @return
         */
        private List<TopOfBook> getTops()
        {
            return Collections.unmodifiableList(tops);
        }
        /**
         * the events received
         */
        @GuardedBy("this")
        private final List<TopOfBook> tops = new ArrayList<TopOfBook>();
        /**
         * the latest ask received, may be <code>null</code> 
         */
        @GuardedBy("this")
        private final Map<Instrument,AskEvent> lastAsks = new HashMap<Instrument,AskEvent>();
        /**
         * the latest bid received, may be <code>null</code>
         */
        @GuardedBy("this")
        private final Map<Instrument,BidEvent> lastBids = new HashMap<Instrument,BidEvent>();
    }
   /**
    * Captures any events from a <code>SimulatedExchange</code>.
    *
    * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
    * @version $Id$
    * @since 1.5.0
    */
   private static class AllEventsSubscriber
       implements ISubscriber
   {
       /**
        * the events received
        */
       private final List<Event> events = new ArrayList<Event>();
       /* (non-Javadoc)
        * @see org.marketcetera.core.publisher.ISubscriber#isInteresting(java.lang.Object)
        */
       @Override
       public boolean isInteresting(Object inData)
       {
           return true;
       }
       /* (non-Javadoc)
        * @see org.marketcetera.core.publisher.ISubscriber#publishTo(java.lang.Object)
        */
       @Override
       public void publishTo(Object inData)
       {
           events.add((Event)inData);
       }
   }
   /**
    * Describes the expected state of a given <code>Instrument</code> in an unspecified exchange
    * at an unspecified point in time.
    *
    * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
    * @version $Id$
    * @since 2.0.0
    */
   private static class InstrumentState
   {
       /**
        * Create a new InstrumentState instance.
        *
        * @param inBids a <code>List&lt;BidEvent&gt;</code> value
        * @param inAsks a <code>List&lt;AskEvent&gt;</code> value
        * @param inTrade a <code>TradeEvent</code> value
        */
       private InstrumentState(List<BidEvent> inBids,
                               List<AskEvent> inAsks,
                               TradeEvent inTrade)
       {
           bids = inBids;
           asks = inAsks;
           latestTrade = inTrade;
       }
       /**
        * the expected bids, may be empty
        */
       private final List<BidEvent> bids;
       /**
        * the expected asks, may be empty
        */
       private final List<AskEvent> asks;
       /**
        * the expected trade, may be <code>null</code>
        */
       private final TradeEvent latestTrade;
   }
   /**
    * Organizes actual results from an exchange by <code>Instrument</code>.
    *
    * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
    * @version $Id$
    * @since 2.0.0
    */
   private static class EventOrganizer
   {
    /**
        * Process the given events to make them available by <code>Instrument</code> and purpose.
        *
        * @param inEvents a <code>List&lt;? extends Event&gt;</code> value
        * @param inRequestType a <code>RequestType</code> value
        */
       private static void process(List<? extends Event> inEvents,
                                   RequestType inRequestType)
       {
           Multimap<EventOrganizer,Event> sortedEvents = LinkedHashMultimap.create();
           for(Event event : inEvents) {
               if(event instanceof HasInstrument) {
                   Instrument instrument = ((HasInstrument)event).getInstrument();
                   EventOrganizer organizer = organizers.get(instrument);
                   if(organizer == null) {
                       organizer = new EventOrganizer();
                       organizers.put(instrument,
                                      organizer);
                   }
                   sortedEvents.put(organizer,
                                    event);
               }
           }
           for(EventOrganizer organizer : sortedEvents.keySet()) {
               Collection<Event> events = sortedEvents.get(organizer);
               switch(inRequestType) {
                   case LATEST_TICK :
                       organizer.latestTrade = null;
                       if(events.size() > 1) {
                           fail("Unable to translate " + events + " as latest tick (should be one event)");
                       }
                       if(!events.isEmpty()) {
                           organizer.latestTrade = (TradeEvent)events.iterator().next();
                       }
                       break;
                   case MARKET_STAT :
                       organizer.marketstat = null;
                       if(events.size() > 1) {
                           fail("Unable to translate " + events + " as marketstat (should be one event)");
                       }
                       if(!events.isEmpty()) {
                           organizer.marketstat = (MarketstatEvent)events.iterator().next();
                       }
                       break;
                   case TOP_OF_BOOK :
                       organizer.topOfBook.clear();
                       for(Event event : events) {
                           if(event instanceof QuoteEvent) {
                               organizer.topOfBook.add((QuoteEvent)event);
                           }
                       }
                       break;
                   case DEPTH_OF_BOOK :
                       organizer.depthOfBook.clear();
                       for(Event event : events) {
                           if(event instanceof QuoteEvent) {
                               organizer.depthOfBook.add((QuoteEvent)event);
                           }
                       }
                       break;
                   default :
                       fail("Unexpected request type");    
               }
           }
       }
       /**
        * Gets the <code>TopOfBook</code> that represents the current state of {@link #topOfBook}.
        *
        * @return a <code>TopOfBook</code> value
        */
       private TopOfBook getTop()
       {
           assertTrue("There are too many events in the top-of-book collection",
                      topOfBook.size() <= 2);
           BidEvent bid = null;
           AskEvent ask = null;
           while(!topOfBook.isEmpty()) {
               Event event = topOfBook.remove(0);
               if(event instanceof BidEvent) {
                   bid = (BidEvent)event;
               } else if(event instanceof AskEvent) {
                   ask = (AskEvent)event;
               }
           }
           return new TopOfBook(bid,
                                ask);
       }
       /**
        * the set of event organizers by instrument
        */
       private static Map<Instrument,EventOrganizer> organizers = new HashMap<Instrument,EventOrganizer>();
       /**
        * the current depth-of-book for this instrument
        */
       private final List<QuoteEvent> depthOfBook = new ArrayList<QuoteEvent>();
       /**
        * the current top-of-book for this instrument
        */
       private final List<QuoteEvent> topOfBook = new ArrayList<QuoteEvent>();
       /**
        * the current lates trade for this instrument
        */
       private TradeEvent latestTrade = null;
       /**
        * the current marketstat for this instrument
        */
       private MarketstatEvent marketstat;
       /**
        * Indicates the type of request made to the exchange.
        *
        * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
        * @version $Id$
        * @since 2.0.0
        */
       private static enum RequestType
       {
           LATEST_TICK,
           TOP_OF_BOOK,
           MARKET_STAT,
           DEPTH_OF_BOOK
       }
   }
}
