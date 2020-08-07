package org.marketcetera.marketdata;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.marketcetera.event.Event;
import org.marketcetera.event.HasEventType;
import org.marketcetera.event.HasTimestamps;
import org.marketcetera.event.QuoteEvent;
import org.marketcetera.marketdata.service.MarketDataService;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

import junitparams.JUnitParamsRunner;

/* $License$ */

/**
 * Provides common test behavior for market data feeds.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@RunWith(JUnitParamsRunner.class)
@ComponentScan(basePackages={"org.marketcetera","com.marketcetera"})
@SpringBootTest(classes=NewMarketDataFeedTestConfiguration.class)
public class NewMarketDataFeedTestBase
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
    }
    /**
     * Create a data flow using the given market data request.
     *
     * @param inMarketDataRequest a <code>MarketDataRequest</code> value
     * @return a <code>MarketDataReceiver</code> value
     */
    protected MarketDataReceiver createDataFlow(MarketDataRequest inMarketDataRequest)
    {
        MarketDataReceiver marketDataReceiver = new MarketDataReceiver();
        SLF4JLoggerProxy.debug(this,
                               "Creating market data request for {}",
                               inMarketDataRequest);
        marketDataReceiver.setMarketDataRequestId(marketDataService.request(inMarketDataRequest,
                                                                            marketDataReceiver));
        return marketDataReceiver;
    }
    /**
     * Receives market data for the tests.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    protected class MarketDataReceiver
            implements MarketDataListener
    {
        /* (non-Javadoc)
         * @see org.marketcetera.marketdata.MarketDataListener#receiveMarketData(org.marketcetera.event.Event)
         */
        @Override
        public void receiveMarketData(Event inEvent)
        {
            SLF4JLoggerProxy.debug(NewMarketDataFeedTestBase.this,
                                   "Received published event: {}",
                                   inEvent);
            synchronized(events) {
                events.add(inEvent);
            }
            if(inEvent instanceof QuoteEvent) {
                QuoteEvent quote = (QuoteEvent)inEvent;
                if(quote.getLevel() == 0) {
                    // TODO top-of-book
                } else {
                    OrderBook orderbook = orderbooks.get(quote.getInstrument());
                    if(orderbook == null) {
                        orderbook = new OrderBook(quote.getInstrument());
                        orderbooks.put(quote.getInstrument(),
                                       orderbook);
                    }
                    orderbook.process(quote);
                }
            }
        }
        /* (non-Javadoc)
         * @see org.marketcetera.marketdata.MarketDataListener#onError(java.lang.Throwable)
         */
        @Override
        public void onError(Throwable inThrowable)
        {
            SLF4JLoggerProxy.warn(NewMarketDataFeedTestBase.this,
                                  inThrowable,
                                  "Received error");
            errors.add(inThrowable);
        }
        /**
         * Reset the object.
         */
        public void reset()
        {
            errors.clear();
            events.clear();
            orderbooks.clear();
        }
        /**
         * Set the market data request id value.
         *
         * @param inMarketDataRequest a <code>String</code> value
         */
        protected void setMarketDataRequestId(String inMarketDataRequest)
        {
            marketDataRequestId = inMarketDataRequest;
        }
        /**
         * Display calculated statistics from the event collection.
         */
        protected void displayStats()
        {
            SLF4JLoggerProxy.debug(this,
                                   "Beginning stat calculation");
            BigDecimal sum = BigDecimal.ZERO;
            int count = events.size();
            try {
                if(count == 0) {
                    return;
                }
                synchronized(events) {
                    for(Event event : events) {
                        if(event instanceof HasEventType) {
                            if(((HasEventType)event).getEventType().isSnapshot()) {
                                continue;
                            }
                        }
                        if(event instanceof HasTimestamps) {
                            count += 1;
                            sum = sum.add(new BigDecimal((((HasTimestamps)event).getProcessedTimestamp() - ((HasTimestamps)event).getReceivedTimestamp())));
                        }
                    }
                }
                BigDecimal result = sum.divide(new BigDecimal(1000000)).divide(new BigDecimal(count),
                                                                               new MathContext(6,RoundingMode.HALF_UP));
                SLF4JLoggerProxy.info(this,
                                      "Average latency for {} event(s) was {}ms with {} error(s)",
                                      count,
                                      result.toPlainString(),
                                      errors.size());
            } finally {
                synchronized(eventCount) {
                    eventCount.addAndGet(count);
                }
            }
        }
        /**
         * Cancel the data flow.
         */
        public void cancelMarketDataRequest()
        {
            marketDataService.cancel(marketDataRequestId);
        }
        /**
         * market data request id value
         */
        protected String marketDataRequestId;
        /**
         * order book objects used to track market data
         */
        protected final Map<Instrument,OrderBook> orderbooks = new HashMap<>();
        /**
         * events received
         */
        public final List<Event> events = new ArrayList<>();
        /**
         * errors received
         */
        protected final List<Throwable> errors = new ArrayList<>();
    }
    /**
     * counts events
     */
    protected final AtomicInteger eventCount = new AtomicInteger(0);
    /**
     * test artifact used to identify the current test case
     */
    @Rule
    public TestName name = new TestName();
    /**
     * rule used to load test context
     */
    @ClassRule
    public static final SpringClassRule SCR = new SpringClassRule();
    /**
     * test spring method rule
     */
    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();
    /**
     * provides access to market data services
     */
    @Autowired
    protected MarketDataService marketDataService;
}
