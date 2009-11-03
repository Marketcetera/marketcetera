package org.marketcetera.marketdata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.log4j.Level;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.marketcetera.core.LoggerConfiguration;
import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.event.AggregateEvent;
import org.marketcetera.event.AskEvent;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.BookEntryTuple;
import org.marketcetera.event.DepthOfBookEvent;
import org.marketcetera.event.Event;
import org.marketcetera.event.EventTestBase;
import org.marketcetera.event.MarketstatEvent;
import org.marketcetera.event.QuantityTuple;
import org.marketcetera.event.QuoteAction;
import org.marketcetera.event.QuoteEvent;
import org.marketcetera.event.TopOfBookEvent;
import org.marketcetera.event.TradeEvent;
import org.marketcetera.event.impl.TopOfBookEventBuilder;
import org.marketcetera.marketdata.SimulatedExchange.Status;
import org.marketcetera.marketdata.SimulatedExchange.Token;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.test.CollectionAssert;
import org.marketcetera.util.test.TestCaseBase;

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
    private BidEvent bid;
    private AskEvent ask;
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
       bid = EventTestBase.generateEquityBidEvent(System.nanoTime(),
                                                  System.currentTimeMillis(),
                                                  metc,
                                                  exchange.getCode(),
                                                  new BigDecimal("100"),
                                                  new BigDecimal("1000"));
       // intentionally creating a large spread to make sure no trades get executed
       ask = EventTestBase.generateEquityAskEvent(System.nanoTime(),
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
                exchange.getDepthOfBook(metc);
            }
        };
        new ExpectedFailure<IllegalStateException>() {
            @Override
            protected void run()
                    throws Exception
            {
                exchange.getTopOfBook(metc);
            }
        };
        new ExpectedFailure<IllegalStateException>() {
            @Override
            protected void run()
                    throws Exception
            {
                exchange.getLatestTick(metc);
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
                        asks,
                        bids,
                        null);
        // re-execute the same query (book already exists, make sure we're reading from the already existing book)
        verifySnapshots(exchange,
                        metc,
                        asks,
                        bids,
                        null);
        // execute a request for an empty book
        verifySnapshots(exchange,
                        goog,
                        new ArrayList<AskEvent>(),
                        new ArrayList<BidEvent>(),
                        null);
        exchange.stop();
        // start the exchange again in scripted mode, this time with events in opposition to each other
        script.add(EventTestBase.generateEquityBidEvent(System.nanoTime(),
                                                        System.currentTimeMillis(),
                                                        metc,
                                                        exchange.getCode(),
                                                        ask.getPrice(),
                                                        ask.getSize()));
        script.add(EventTestBase.generateEquityAskEvent(System.nanoTime(),
                                                        System.currentTimeMillis(),
                                                        metc,
                                                        exchange.getCode(),
                                                        bid.getPrice(),
                                                        bid.getSize()));
        exchange.start(script);
        // verify that the book is empty (but there should be an existing trade)
        verifySnapshots(exchange,
                        metc,
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
        // make new dob request (adds a book for this symbol)
        DepthOfBookEvent dob = exchange.getDepthOfBook(metc);
        // note that since the exchange was started this time in random mode we don't know exactly what the
        //  values will be, but there should be at least one entry on each side of the book
        assertFalse(dob.getAsks().isEmpty());
        assertFalse(dob.getBids().isEmpty());
        // re-execute (this time the book exists)
        dob = exchange.getDepthOfBook(metc);
        assertFalse(dob.getAsks().isEmpty());
        assertFalse(dob.getBids().isEmpty());
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
                exchange.getStatistics(metc);
            }
        };
        // done with error conditions
        exchange.start();
        // quantities are random, even for subsequent calls and scripted mode, but
        //  there are some conditions we can expect the values to adhere to
        for(int i=0;i<50000;i++) {
            verifyStatistics(exchange.getStatistics(metc),
                             metc);
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
        final AllEventsSubscriber stream = new AllEventsSubscriber();
        exchange.getStatistics(metc,
                               stream);
        exchange.start();
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                return stream.events.size() >= 15;
            }
        });
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
        // generate a script with a number of bids and asks
        BigDecimal baseValue = new BigDecimal("100.00");
        BigDecimal bidSize = new BigDecimal("100");
        BigDecimal askSize = new BigDecimal("50");
        List<QuoteEvent> script = new ArrayList<QuoteEvent>();
        List<BidEvent> bids = new ArrayList<BidEvent>();
        List<AskEvent> asks = new ArrayList<AskEvent>();
        for(int i=0;i<5;i++) {
            bids.add(EventTestBase.generateEquityBidEvent(System.nanoTime(),
                                                          System.currentTimeMillis(),
                                                          metc,
                                                          exchange.getCode(),
                                                          baseValue.add(BigDecimal.ONE.multiply(new BigDecimal(i))),
                                                          bidSize));
        }
        for(int i=4;i>=0;i--) {
            asks.add(EventTestBase.generateEquityAskEvent(System.nanoTime(),
                                                          System.currentTimeMillis(),
                                                          metc,
                                                          exchange.getCode(),
                                                          baseValue.add(BigDecimal.ONE.multiply(new BigDecimal(i))),
                                                          askSize));
        }
        script.addAll(bids);
        script.addAll(asks);
        // set up the subscriptions
        TopOfBookSubscriber topOfBook = new TopOfBookSubscriber(metc);
        exchange.getTopOfBook(metc,
                              topOfBook);
        AllEventsSubscriber tick = new AllEventsSubscriber();
        exchange.getLatestTick(metc,
                               tick);
        AllEventsSubscriber depthOfBook = new AllEventsSubscriber();
        exchange.getDepthOfBook(metc,
                                depthOfBook);
        // start the script
        exchange.start(script);
        // wait for the script to complete (we can predict that the exchange will send 10 quote adds which will result in 5 trades and
        //  10 quote corrections (bid/ask del/chg), 25 events altogether)
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                return exchange.getStatus() == Status.COMPLETE;
            }
        });
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
        100 100.00 |
        100 101.00 |
        100 102.00 |
        100 103.00 |
        100 104.00 |
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
        verifySubscriptions(topOfBook.tops,
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
        exchange.getTopOfBook(metc,
                              sub1);
        exchange2.getTopOfBook(metc,
                               sub2);
        // create an event targeted to the second exchange
        AskEvent ask2 = EventTestBase.generateEquityAskEvent(System.nanoTime(),
                                                                     System.currentTimeMillis(),
                                                                     metc,
                                                                     exchange2.getCode(),
                                                                     new BigDecimal("150"),
                                                                     new BigDecimal("500"));
        // create an event targeted to the second exchange but with the wrong symbol
        AskEvent ask3 = EventTestBase.generateEquityAskEvent(System.nanoTime(),
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
           bids.add(EventTestBase.generateEquityBidEvent(System.nanoTime(),
                                                         System.currentTimeMillis(),
                                                         metc,
                                                         exchange.getCode(),
                                                         new BigDecimal(10-i),
                                                         new BigDecimal("1000")));
           asks.add(EventTestBase.generateEquityAskEvent(System.nanoTime(),
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
       verifyDepthOfBook(exchange.getDepthOfBook(metc),
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
        Token t1 = exchange.getTopOfBook(metc,
                                         stream1);
        Token t2 = exchange.getTopOfBook(metc,
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
        exchange.cancel(t1);
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
        exchange.cancel(t1);
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
        exchange.getTopOfBook(metc,
                              stream1);
        exchange.start();
        exchange.getTopOfBook(metc,
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
        verifyTopOfBook(exchange.getTopOfBook(metc),
                        ask,
                        bid);
        // verify that nothing's there for GOOG
        verifyTopOfBook(exchange.getTopOfBook(goog),
                        null,
                        null);
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
                TopOfBookEvent top = exchange.getTopOfBook(metc);
                return top.getAsk() != null &&
                       top.getBid() != null;
            }
        });
        // the book for METC has at least an ask and bid in it, grab the value
        TopOfBookEvent top1 = exchange.getTopOfBook(metc);
        // now, issue the same request for exchange2
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                TopOfBookEvent top = exchange2.getTopOfBook(metc);
                return top.getAsk() != null &&
                       top.getBid() != null;
            }
        });
        TopOfBookEvent top2 = exchange2.getTopOfBook(metc);
        // and exchange3
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                TopOfBookEvent top = exchange3.getTopOfBook(metc);
                return top.getAsk() != null &&
                       top.getBid() != null;
            }
        });
        TopOfBookEvent top3 = exchange3.getTopOfBook(metc);
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
        assertTrue(top1.getAsk().getPrice().subtract(top2.getAsk().getPrice()).abs().intValue() < 3);
        assertTrue(top1.getAsk().getPrice().subtract(top3.getAsk().getPrice()).abs().intValue() < 3);
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
                exchange.getStatistics(symbol);
                exchange.getDepthOfBook(symbol);
                exchange.getLatestTick(symbol);
                exchange.getTopOfBook(symbol);
                currentTime = System.currentTimeMillis();
            }
            assertNoEvents();
        } finally {
            TestCaseBase.setLevel(SimulatedExchange.class.getName(),
                                  Level.INFO);
        }
    }
    /**
     * Verifies the given actual subscriptions against the expected results. 
     *
     * @param inActualTopOfBook a <code>List&lt;TopOfBook&gt;</code> value
     * @param inExpectedTopOfBook a <code>List&lt;BookEntryTuple&gt;</code> value
     * @param inActualTicks a <code>List&lt;EventBase&gt;</code> value
     * @param inExpectedTicks a <code>List&lt;QuantityTuple&gt;</code> value
     * @param inActualDepthOfBook a <code>List&lt;EventBase&gt;</code> value
     * @param inExpectedDepthOfBook a <code>List&lt;QuantityTuple&gt;</code> value
     * @throws Exception if an error occurs
     */
    private void verifySubscriptions(List<TopOfBookEvent> inActualTopOfBook,
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
        for(TopOfBookEvent event : inActualTopOfBook) {
            actualTopOfBook.add(new BookEntryTuple(OrderBookTest.convertEvent(event.getBid()),
                                                   OrderBookTest.convertEvent(event.getAsk())));
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
     * @param inStatistics a <code>MarketstatEvent</code> value containing the actual value
     * @param inEquity an <code>Equity</code> value containing the expected symbol
     * @throws Exception if an error occurs
     */
    private void verifyStatistics(MarketstatEvent inStatistics,
                                  Equity inEquity)
        throws Exception
    {
        assertNotNull(inStatistics.getOpen());
        assertNotNull(inStatistics.getHigh());
        assertNotNull(inStatistics.getLow());
        assertNotNull(inStatistics.getClose());
        assertNotNull(inStatistics.getPreviousClose());
        assertNotNull(inStatistics.getVolume());
        assertNotNull(inStatistics.getCloseDate());
        assertNotNull(inStatistics.getPreviousCloseDate());
        assertNotNull(inStatistics.getTradeHighTime());
        assertNotNull(inStatistics.getTradeLowTime());
        assertNotNull(inStatistics.getOpenExchange());
        assertNotNull(inStatistics.getCloseExchange());
        assertNotNull(inStatistics.getHighExchange());
        assertNotNull(inStatistics.getLowExchange());
   }
    /**
     * Verifies that the given exchange and symbol will produce the expected snapshots. 
     *
     * @param inExchange a <code>SimulatedExchange</code> value
     * @param inEquity an <code>Equity</code> value
     * @param inExpectedAsks a <code>List&lt;AskEvent&gt;</code> value
     * @param inExpectedBids a <code>List&lt;BidEvent&gt;</code> value
     * @throws Exception if an error occurs
     */
    private void verifySnapshots(SimulatedExchange inExchange,
                                 Equity inEquity,
                                 List<AskEvent> inExpectedAsks,
                                 List<BidEvent> inExpectedBids,
                                 TradeEvent inExpectedLatestTick)
        throws Exception
    {
        verifyDepthOfBook(inExchange.getDepthOfBook(inEquity),
                          inExpectedAsks,
                          inExpectedBids);
        verifyTopOfBook(inExchange.getTopOfBook(inEquity),
                        inExpectedAsks.isEmpty() ? null : inExpectedAsks.get(0),
                        inExpectedBids.isEmpty() ? null : inExpectedBids.get(0));
        assertEquals(OrderBookTest.convertEvent(inExpectedLatestTick),
                     OrderBookTest.convertEvent(exchange.getLatestTick(inEquity)));
    }
    /**
     * Verifies the given actual<code>TopOfBook</code> contains the expected values. 
     *
     * @param inActualTopOfBook a <code>TopOfBook</code> value
     * @param inAsk an <code>AskEvent</code> value
     * @param inBid a <code>BidEvent</code> value
     * @throws Exception if an error occurs
     */
    private void verifyTopOfBook(TopOfBookEvent inActualTopOfBook,
                                 AskEvent inExpectedAsk,
                                 BidEvent inExpectedBid)
        throws Exception
    {
        assertEquals(OrderBookTest.convertEvent(inExpectedAsk),
                     OrderBookTest.convertEvent(inActualTopOfBook.getAsk()));
        assertEquals(OrderBookTest.convertEvent(inExpectedBid),
                     OrderBookTest.convertEvent(inActualTopOfBook.getBid()));
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
     * @param inActualDepthOfBook a <code>DepthOfBook</code> value
     * @param inExpectedAsks a <code>List&lt;AskEvent&gt;</code> value
     * @param inExpectedBids a <code>List&lt;BidEvent&gt;</code> value
     * @throws Exception if an error occurs
     */
    public static void verifyDepthOfBook(DepthOfBookEvent inActualDepthOfBook,
                                         List<AskEvent> inExpectedAsks,
                                         List<BidEvent> inExpectedBids)
        throws Exception
    {
        assertEquals(OrderBookTest.convertEvents(inExpectedAsks),
                     OrderBookTest.convertEvents(inActualDepthOfBook.getAsks()));
        assertEquals(OrderBookTest.convertEvents(inExpectedBids),
                     OrderBookTest.convertEvents(inActualDepthOfBook.getBids()));
        assertNotNull(inActualDepthOfBook.toString());
        List<Event> expectedEvents = new LinkedList<Event>();
        expectedEvents.addAll(inExpectedAsks);
        expectedEvents.addAll(inExpectedBids);
        verifyDecomposedEvents(inActualDepthOfBook,
                               expectedEvents);
    }
    /**
     * Subscribes to top-of-book and captures the state of the exchange top every time it changes.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 1.5.0
     */
    private static class TopOfBookSubscriber
        implements ISubscriber
    {
        /**
         * the events received
         */
        private final List<TopOfBookEvent> tops = new ArrayList<TopOfBookEvent>();
        private AskEvent lastAsk = null;
        private BidEvent lastBid = null;
        private final Instrument instrument;
        /**
         * Create a new TopOfBookSubscriber instance.
         *
         * @param inInstrument an <code>Instrument</code> value
         */
        private TopOfBookSubscriber(Instrument inInstrument)
        {
            instrument = inInstrument;
        }
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
        public void publishTo(Object inData)
        {
            QuoteEvent quote = (QuoteEvent)inData;
            BidEvent newBid = (quote instanceof BidEvent ? quote.getAction() == QuoteAction.DELETE ? null : (BidEvent)quote : lastBid);
            AskEvent newAsk = (quote instanceof AskEvent ? quote.getAction() == QuoteAction.DELETE ? null : (AskEvent)quote : lastAsk);
            TopOfBookEvent newTop = TopOfBookEventBuilder.topOfBookEvent().withBid(newBid)
                                                                          .withAsk(newAsk)
                                                                          .withInstrument(instrument)
                                                                          .withTimestamp(quote.getTimestamp()).create();
            tops.add(newTop);
            lastBid = newBid;
            lastAsk = newAsk;
        }
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
}
