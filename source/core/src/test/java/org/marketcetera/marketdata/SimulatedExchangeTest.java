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

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.marketcetera.core.LoggerConfiguration;
import org.marketcetera.core.Pair;
import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.event.AggregateEvent;
import org.marketcetera.event.AskEvent;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.BookEntryTuple;
import org.marketcetera.event.DepthOfBook;
import org.marketcetera.event.DepthOfBookTest;
import org.marketcetera.event.EventBase;
import org.marketcetera.event.QuantityTuple;
import org.marketcetera.event.QuoteEvent;
import org.marketcetera.event.SymbolExchangeEvent;
import org.marketcetera.event.SymbolStatisticEvent;
import org.marketcetera.event.TopOfBook;
import org.marketcetera.event.TradeEvent;
import org.marketcetera.marketdata.SimulatedExchange.Token;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.trade.MSymbol;

/* $License$ */

/**
 * Tests {@link SimulatedExchange}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.5.0
 */
public class SimulatedExchangeTest
{
    private SimulatedExchange exchange;
    private final MSymbol metc = new MSymbol("METC");
    private final MSymbol goog = new MSymbol("GOOG");
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
       bid = new BidEvent(System.nanoTime(),
                          System.currentTimeMillis(),
                          metc,
                          exchange.getCode(),
                          new BigDecimal("100"),
                          new BigDecimal("1000"));
       // intentionally creating a large spread to make sure no trades get executed
       ask = new AskEvent(System.nanoTime(),
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
        new ExpectedFailure<IllegalStateException>(null) {
            @Override
            protected void run()
                    throws Exception
            {
                exchange.stop();
            }
        };
        exchange.start();
        new ExpectedFailure<IllegalStateException>(null) {
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
        new ExpectedFailure<NullPointerException>(null) {
            @Override
            protected void run()
                    throws Exception
            {
                new SimulatedExchange(null,
                                      code);
            }
        };
        new ExpectedFailure<NullPointerException>(null) {
            @Override
            protected void run()
                    throws Exception
            {
                new SimulatedExchange(null,
                                      code,
                                      1);
            }
        };
        new ExpectedFailure<NullPointerException>(null) {
            @Override
            protected void run()
                    throws Exception
            {
                new SimulatedExchange(name,
                                      null);
            }
        };
        new ExpectedFailure<NullPointerException>(null) {
            @Override
            protected void run()
                    throws Exception
            {
                new SimulatedExchange(name,
                                      null,
                                      1);
            }
        };
        new ExpectedFailure<IllegalArgumentException>(null) {
            @Override
            protected void run()
                    throws Exception
            {
                new SimulatedExchange(name,
                                      code,
                                      -2);
            }
        };
        new ExpectedFailure<IllegalArgumentException>(null) {
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
        new ExpectedFailure<NullPointerException>(null) {
            @Override
            protected void run()
                    throws Exception
            {
                exchange.getDepthOfBook(null);
            }
        };
        new ExpectedFailure<NullPointerException>(null) {
            @Override
            protected void run()
                    throws Exception
            {
                exchange.getTopOfBook(null);
            }
        };
        new ExpectedFailure<NullPointerException>(null) {
            @Override
            protected void run()
                    throws Exception
            {
                exchange.getLatestTick(null);
            }
        };
        // exchange is not started yet
        new ExpectedFailure<IllegalStateException>(null) {
            @Override
            protected void run()
                    throws Exception
            {
                exchange.getDepthOfBook(metc);
            }
        };
        new ExpectedFailure<IllegalStateException>(null) {
            @Override
            protected void run()
                    throws Exception
            {
                exchange.getTopOfBook(metc);
            }
        };
        new ExpectedFailure<IllegalStateException>(null) {
            @Override
            protected void run()
                    throws Exception
            {
                exchange.getLatestTick(metc);
            }
        };
        // start the exchange with a script with only one event for each side of the book
        List<QuoteEvent> script = new ArrayList<QuoteEvent>(Arrays.asList(new QuoteEvent[] { bid, ask }));
        exchange.start(script);
        // get the depth-of-book for the symbol
        verifySnapshots(exchange,
                        metc,
                        Arrays.asList(new AskEvent[] { ask } ),
                        Arrays.asList(new BidEvent[] { bid } ),
                        null);
        // re-execute the same query (book already exists, make sure we're reading from the already existing book)
        verifySnapshots(exchange,
                        metc,
                        Arrays.asList(new AskEvent[] { ask } ),
                        Arrays.asList(new BidEvent[] { bid } ),
                        null);
        // execute a request for an empty book
        verifySnapshots(exchange,
                        goog,
                        Arrays.asList(new AskEvent[] { } ),
                        Arrays.asList(new BidEvent[] { } ),
                        null);
        exchange.stop();
        // start the exchange again in scripted mode, this time with events in opposition to each other
        script.add(new BidEvent(System.nanoTime(),
                                System.currentTimeMillis(),
                                metc,
                                exchange.getCode(),
                                ask.getPrice(),
                                ask.getSize()));
        script.add(new AskEvent(System.nanoTime(),
                                System.currentTimeMillis(),
                                metc,
                                exchange.getCode(),
                                bid.getPrice(),
                                bid.getSize()));
        exchange.start(script);
        // verify that the book is empty (but there should be an existing trade)
        verifySnapshots(exchange,
                        metc,
                        Arrays.asList(new AskEvent[] { } ),
                        Arrays.asList(new BidEvent[] { } ),
                        new TradeEvent(1,
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
        DepthOfBook dob = exchange.getDepthOfBook(metc);
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
        new ExpectedFailure<NullPointerException>(null) {
            @Override
            protected void run()
                    throws Exception
            {
                exchange.getStatistics(null);
            }
        };
        // exchange not started
        new ExpectedFailure<IllegalStateException>(null) {
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
            bids.add(new BidEvent(System.nanoTime(),
                                  System.currentTimeMillis(),
                                  metc,
                                  exchange.getCode(),
                                  baseValue.add(BigDecimal.ONE.multiply(new BigDecimal(i))),
                                  bidSize));
        }
        for(int i=4;i>=0;i--) {
            asks.add(new AskEvent(System.nanoTime(),
                                  System.currentTimeMillis(),
                                  metc,
                                  exchange.getCode(),
                                  baseValue.add(BigDecimal.ONE.multiply(new BigDecimal(i))),
                                  askSize));
        }
        script.addAll(bids);
        script.addAll(asks);
        // set up the subscriptions
        final StreamSubscriber stream = new StreamSubscriber();
        exchange.getStream(metc,
                           stream);
        BookSubscriber topOfBook = new BookSubscriber();
        exchange.getTopOfBook(metc,
                              topOfBook);
        StreamSubscriber tick = new StreamSubscriber();
        exchange.getLatestTick(metc,
                               tick);
        BookSubscriber depthOfBook = new BookSubscriber();
        exchange.getDepthOfBook(metc,
                                depthOfBook);
        // start the script
        exchange.start(script);
        // wait for the script to complete (we can predict that the exchange will send 10 quote adds and 5 trades, 15 events altogether)
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                return stream.events.size() == 15;
            }
        });
        // verify the results
        // prepare the expected stream results
        List<QuantityTuple> expectedStream = new ArrayList<QuantityTuple>();
        // the first events will be the bids
        expectedStream.addAll(OrderBookTest.convertEvents(bids));
        // next will be the asks interleaved with trades as the books are settled after each ask
        for(AskEvent ask : asks) {
            expectedStream.add(OrderBookTest.convertEvent(ask));
            // figure the corresponding trade
            // remember the asks are smaller than the corresponding bids so we can use the ask size for the trade
            expectedStream.add(new QuantityTuple(ask.getPrice(),
                                                 ask.getSize(),
                                                 TradeEvent.class));
        }
        // prepare the expected top-of-book results
        List<BookEntryTuple> expectedTopOfBook = new ArrayList<BookEntryTuple>();
        // the top of the book changes with each bid/ask/trade, so we'll have 15 here, too
        // start with the bids (first five)
        for(BidEvent bid : bids) {
            expectedTopOfBook.add(new BookEntryTuple(OrderBookTest.convertEvent(bid),
                                                     null));
        }
        // the next ten are more complicated
        // as an ask is added, first you see the (matching) bid and ask, then, as the trade gets resolved,
        //  the bid shrinks in size and the ask disappears.  when the bid shrinks (in size) to zero, the
        //  next best bid rises to the top
        int bidIndex = bids.size()-1;
        BigDecimal currentBidPrice = bids.get(bidIndex).getPrice();
        BigDecimal currentBidSize = bids.get(bidIndex).getSize();
        for(AskEvent ask : asks) {
            // add ask
            expectedTopOfBook.add(new BookEntryTuple(new QuantityTuple(currentBidPrice,
                                                                       currentBidSize,
                                                                       BidEvent.class),
                                                     OrderBookTest.convertEvent(ask)));
            // a trade gets settled (reduces the size of the bid, removes the ask)
            currentBidSize = currentBidSize.subtract(ask.getSize());
            if(currentBidSize.equals(BigDecimal.ZERO)) {
                currentBidPrice = bids.get(--bidIndex).getPrice();
                currentBidSize = bids.get(bidIndex).getSize();
            }
            expectedTopOfBook.add(new BookEntryTuple(new QuantityTuple(currentBidPrice,
                                                                       currentBidSize,
                                                                       BidEvent.class),
                                                     null));
        }
        // prepare the expected latest tick results
        List<QuantityTuple> expectedLatestTicks = new ArrayList<QuantityTuple>();
        for(SymbolExchangeEvent event : stream.events) {
            if(event instanceof TradeEvent) {
                expectedLatestTicks.add(OrderBookTest.convertEvent(event));
            }
        }
        // prepare the expected depth-of-book results
        List<Pair<List<QuantityTuple>,List<QuantityTuple>>> expectedDepthOfBook = new ArrayList<Pair<List<QuantityTuple>,List<QuantityTuple>>>();
        // the first set of expected depth-of-books are the bids being added to the book (two things to note about this:
        //  first, the expecteds need to be in new lists each time because otherwise we just modify the same list each time (thus
        //  modifying all the expecteds at once), second, the bids are arranged in the initial list above from worst to
        //  best but they will appear in the book from best to worst, so the new bid needs to be added to the front of the
        //  list.  this works because of the way the bids were added to the list initially.  a more fool-proof method would
        //  be to sort the list, but that gets complicated, too)
        LinkedList<QuantityTuple> bidQuantities = new LinkedList<QuantityTuple>();
        for(BidEvent bid : bids) {
            bidQuantities.addFirst(OrderBookTest.convertEvent(bid));
            List<QuantityTuple> theseBids = new ArrayList<QuantityTuple>(bidQuantities);
            expectedDepthOfBook.add(new Pair<List<QuantityTuple>,List<QuantityTuple>>(theseBids,
                                                                                      new ArrayList<QuantityTuple>()));
        }
        // bidQuantities now has the complete set of bids in the right order, we'll use that below
        // next, we start adding the asks (and reflecting the trades)
        for(AskEvent ask : asks) {
            // first, add the ask
            List<QuantityTuple> theseBids = new ArrayList<QuantityTuple>(bidQuantities);
            // add the book
            expectedDepthOfBook.add(new Pair<List<QuantityTuple>,List<QuantityTuple>>(theseBids,
                                                                                      Arrays.asList(new QuantityTuple[] { OrderBookTest.convertEvent(ask) } )));
            // reflect the settled trade with another book entry
            QuantityTuple topBid = bidQuantities.removeFirst();
            // deduct the ask quantity
            BigDecimal newSize = topBid.getSize().subtract(ask.getSize());
            // if the bid is not fully consumed, put it back
            if(!newSize.equals(BigDecimal.ZERO)) {
                bidQuantities.addFirst(new QuantityTuple(topBid.getPrice(),
                                                         newSize,
                                                         topBid.getType()));
            }
            // now add another depth-of-book with the new bids and no asks
            expectedDepthOfBook.add(new Pair<List<QuantityTuple>,List<QuantityTuple>>(new ArrayList<QuantityTuple>(bidQuantities),
                                                                                      new ArrayList<QuantityTuple>()));
        }
        // ready to verify results
        verifySubscriptions(stream.events,
                            expectedStream,
                            topOfBook.events,
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
        // create two different stream subscribers
        StreamSubscriber sub1 = new StreamSubscriber();
        StreamSubscriber sub2 = new StreamSubscriber();
        // set up the subscriptions
        exchange.getStream(metc,
                           sub1);
        exchange2.getStream(metc,
                            sub2);
        // create an event targeted to the second exchange
        AskEvent ask2 = new AskEvent(System.nanoTime(),
                                     System.currentTimeMillis(),
                                     metc,
                                     exchange2.getCode(),
                                     new BigDecimal("150"),
                                     new BigDecimal("500"));
        // create an event targeted to the second exchange but with the wrong symbol
        AskEvent ask3 = new AskEvent(System.nanoTime(),
                                     System.currentTimeMillis(),
                                     goog,
                                     exchange2.getCode(),
                                     new BigDecimal("150"),
                                     new BigDecimal("500"));
        // create two different scripts
        // the events that are targeted to the wrong exchange or symbol should get skipped
        List<QuoteEvent> script1 = Arrays.asList(new QuoteEvent[] { bid, ask2, ask3 } );
        List<QuoteEvent> script2 = Arrays.asList(new QuoteEvent[] { ask2, bid, ask3 } );
        // start the exchanges
        exchange.start(script1);
        exchange2.start(script2);
        // measure the results
        verifyStream(sub1.events,
                     OrderBookTest.convertEvents(Arrays.asList(new QuoteEvent[] { bid } )));
        verifyStream(sub2.events,
                     OrderBookTest.convertEvents(Arrays.asList(new QuoteEvent[] { ask2 } )));
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
           bids.add(new BidEvent(System.nanoTime(),
                                 System.currentTimeMillis(),
                                 metc,
                                 exchange.getCode(),
                                 new BigDecimal(10-i),
                                 new BigDecimal("1000")));
           asks.add(new AskEvent(System.nanoTime(),
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
       DepthOfBookTest.verifyDepthOfBook(exchange.getDepthOfBook(metc),
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
        final StreamSubscriber stream1 = new StreamSubscriber();
        final StreamSubscriber stream2 = new StreamSubscriber();
        Token t1 = exchange.getStream(metc,
                                      stream1);
        Token t2 = exchange.getStream(metc,
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
        final StreamSubscriber stream1 = new StreamSubscriber();
        final StreamSubscriber stream2 = new StreamSubscriber();
        exchange.getStream(metc,
                           stream1);
        exchange.start();
        exchange.getStream(metc,
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
        exchange.start(Arrays.asList(new QuoteEvent[] { bid, ask } ));
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
                TopOfBook top = exchange.getTopOfBook(metc);
                return top.getAsk() != null &&
                       top.getBid() != null;
            }
        });
        // the book for METC has at least an ask and bid in it, grab the value
        TopOfBook top1 = exchange.getTopOfBook(metc);
        // now, issue the same request for exchange2
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                TopOfBook top = exchange2.getTopOfBook(metc);
                return top.getAsk() != null &&
                       top.getBid() != null;
            }
        });
        TopOfBook top2 = exchange2.getTopOfBook(metc);
        // and exchange3
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                TopOfBook top = exchange3.getTopOfBook(metc);
                return top.getAsk() != null &&
                       top.getBid() != null;
            }
        });
        TopOfBook top3 = exchange3.getTopOfBook(metc);
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
     * Verifies the given actual subscriptions against the expected results. 
     *
     * @param inActualStream
     * @param inExpectedStream
     * @param inActualTopOfBook
     * @param inExpectedTopOfBook
     * @param inActualTicks
     * @param inExpectedTicks
     * @param inActualDepthOfBook
     * @param inExpectedDepthOfBook
     * @throws Exception if an error occurs
     */
    private void verifySubscriptions(List<SymbolExchangeEvent> inActualStream,
                                     List<QuantityTuple> inExpectedStream,
                                     List<AggregateEvent> inActualTopOfBook,
                                     List<BookEntryTuple> inExpectedTopOfBook,
                                     List<SymbolExchangeEvent> inActualTicks,
                                     List<QuantityTuple> inExpectedTicks,
                                     List<AggregateEvent> inActualDepthOfBook,
                                     List<Pair<List<QuantityTuple>,List<QuantityTuple>>> inExpectedDepthOfBook)
        throws Exception
    {
        // verifyStream
        verifyStream(inActualStream,
                     inExpectedStream);
        // test top-of-book
        assertEquals(inActualTopOfBook.size(),
                     inExpectedTopOfBook.size());
        List<BookEntryTuple> actualTopOfBookTuples = new ArrayList<BookEntryTuple>();
        for(AggregateEvent event : inActualTopOfBook) {
            assertTrue(event instanceof TopOfBook);
            TopOfBook topOfBook = (TopOfBook)event;
            actualTopOfBookTuples.add(new BookEntryTuple(OrderBookTest.convertEvent(topOfBook.getBid()),
                                                         OrderBookTest.convertEvent(topOfBook.getAsk())));
        }
        assertEquals(inExpectedTopOfBook,
                     actualTopOfBookTuples);
        // test latest-tick
        assertEquals(OrderBookTest.convertEvents(inActualTicks),
                     inExpectedTicks);
        // test depth-of-book
        assertEquals(inExpectedDepthOfBook.size(),
                     inActualDepthOfBook.size());
        List<Pair<List<QuantityTuple>,List<QuantityTuple>>> actualDepthOfBookTuples = new ArrayList<Pair<List<QuantityTuple>,List<QuantityTuple>>>();
        for(AggregateEvent event : inActualDepthOfBook) {
            assertTrue(event instanceof DepthOfBook);
            DepthOfBook depthOfBook = (DepthOfBook)event;
            actualDepthOfBookTuples.add(new Pair<List<QuantityTuple>,List<QuantityTuple>>(OrderBookTest.convertEvents(depthOfBook.getBids()),
                                                                                          OrderBookTest.convertEvents(depthOfBook.getAsks())));
        }
        assertEquals(inExpectedDepthOfBook,
                     actualDepthOfBookTuples);
    }
    /**
     * Verifies that the actual stream matches the expected stream.
     *
     * @param inActualStream a <code>List&lt;SymbolExchangeEvent&gt;</code> value
     * @param inExpectedStream a <code>List&lt;QuantityTuple&gt;</code> value
     * @throws Exception if an error occurs
     */
    private void verifyStream(List<SymbolExchangeEvent> inActualStream,
                              List<QuantityTuple> inExpectedStream)
        throws Exception
    {
        // test stream
        assertEquals(inExpectedStream,
                     OrderBookTest.convertEvents(inActualStream));
    }
    /**
     * Verifies symbol statistical data.
     *
     * @param inStatistics a <code>SymbolStatisticEvent</code> value containing the actual value
     * @param inSymbol an <code>MSymbol</code> value containing the expected symbol
     * @throws Exception if an error occurs
     */
    private void verifyStatistics(SymbolStatisticEvent inStatistics,
                                  MSymbol inSymbol)
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
   }
    /**
     * Verifies that the given exchange and symbol will produce the expected snapshots. 
     *
     * @param inExchange a <code>SimulatedExchange</code> value
     * @param inSymbol a <code>MSymbol</code> value
     * @param inExpectedAsks a <code>List&lt;AskEvent&gt;</code> value
     * @param inExpectedBids a <code>List&lt;BidEvent&gt;</code> value
     * @throws Exception if an error occurs
     */
    private void verifySnapshots(SimulatedExchange inExchange,
                                 MSymbol inSymbol,
                                 List<AskEvent> inExpectedAsks,
                                 List<BidEvent> inExpectedBids,
                                 TradeEvent inExpectedLatestTick)
        throws Exception
    {
        DepthOfBookTest.verifyDepthOfBook(inExchange.getDepthOfBook(inSymbol),
                                          inExpectedAsks,
                                          inExpectedBids);
        verifyTopOfBook(inExchange.getTopOfBook(inSymbol),
                        inExpectedAsks.isEmpty() ? null : inExpectedAsks.get(0),
                        inExpectedBids.isEmpty() ? null : inExpectedBids.get(0));
        assertEquals(OrderBookTest.convertEvent(inExpectedLatestTick),
                     OrderBookTest.convertEvent(exchange.getLatestTick(inSymbol)));
    }
    /**
     * Verifies the given actual<code>TopOfBook</code> contains the expected values. 
     *
     * @param inActualTopOfBook a <code>TopOfBook</code> value
     * @param inAsk an <code>AskEvent</code> value
     * @param inBid a <code>BidEvent</code> value
     * @throws Exception if an error occurs
     */
    private void verifyTopOfBook(TopOfBook inActualTopOfBook,
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
     * Captures <code>AggregateEvents</code> from a <code>SimulatedExchange</code>.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 1.5.0
     */
    private static class BookSubscriber
        implements ISubscriber
    {
        /**
         * the events received
         */
        private final List<AggregateEvent> events = new ArrayList<AggregateEvent>();

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
            events.add((AggregateEvent)inData);
        }
    }
   /**
    * Captures the stream of events from a <code>SimulatedExchange</code>.
    *
    * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
    * @version $Id$
    * @since 1.5.0
    */
   private static class StreamSubscriber
       implements ISubscriber
   {
       /**
        * the events received
        */
       private final List<SymbolExchangeEvent> events = new ArrayList<SymbolExchangeEvent>();
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
           events.add((SymbolExchangeEvent)inData);
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
       private final List<EventBase> events = new ArrayList<EventBase>();
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
           events.add((EventBase)inData);
       }
   }
}
