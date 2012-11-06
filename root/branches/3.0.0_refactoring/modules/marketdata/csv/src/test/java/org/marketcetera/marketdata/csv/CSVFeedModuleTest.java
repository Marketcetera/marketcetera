package org.marketcetera.marketdata.csv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.marketcetera.core.marketdata.Capability.LATEST_TICK;
import static org.marketcetera.core.marketdata.Capability.TOP_OF_BOOK;

import java.math.BigDecimal;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.junit.BeforeClass;
import org.junit.Test;
import org.marketcetera.core.LoggerConfiguration;
import org.marketcetera.core.event.AskEvent;
import org.marketcetera.core.event.BidEvent;
import org.marketcetera.core.event.Event;
import org.marketcetera.core.event.TradeEvent;
import org.marketcetera.core.marketdata.Capability;
import org.marketcetera.core.marketdata.MarketDataModuleTestBase;
import org.marketcetera.core.marketdata.MarketDataRequestBuilder;
import org.marketcetera.core.module.*;

/**
 * Subclass from the main Market data test and verify that basic module functionality works
 * @version $Id: CSVFeedModuleTest.java 16063 2012-01-31 18:21:55Z colin $
 */
public class CSVFeedModuleTest
        extends MarketDataModuleTestBase
{
    private static final String DATA_DIR = "src/test/sample_data/";

    @BeforeClass
    public static void beforeClass() {
        LoggerConfiguration.logSetup();
    }

    @Override
    protected void populateConfigurationProvider(ConfigurationProviderTest.MockConfigurationProvider inProvider)
    {
        inProvider.addValue(CSVFeedModuleFactory.INSTANCE_URN,
                            "MarketdataDirectory",
                            DATA_DIR);
    }

    @Override
    protected Capability[] getExpectedCapabilities()
    {
        return new Capability[] { TOP_OF_BOOK,LATEST_TICK };
    }


    @Override
    protected String getProvider() {
        return CSVFeedModuleFactory.IDENTIFIER;
    }

    @Override
    protected ModuleFactory getFactory() {
        return new CSVFeedModuleFactory();
    }

    @Override
    protected ModuleURN getInstanceURN() {
        return CSVFeedModuleFactory.INSTANCE_URN;
    }

    @Test(timeout = 60*1000) // 60 secs
    /** Setup a data flow where we receive events in our custom listener
	 * Verify that the right events are coming through
	 *  Expecting one trade:
     * 12345,GOOG,N,400,100,TRADE
     */
    public void testCSVMarketData_Trade() throws Exception {
        final BlockingQueue<Event> events = new ArrayBlockingQueue<Event>(50);
        moduleManager.addSinkListener(new SinkDataListener() {
            @Override
            public void receivedData(DataFlowID inFlowID, Object inData) {
                events.add((Event) inData);
            }
        });
        CSVFeed feed = CSVFeedFactory.getInstance().getMarketDataFeed();
        feed.doLogin(CSVFeedCredentials.getInstance(0,
                                                    false,
                                                    DATA_DIR,
                                                    MockCSVFeedEventTranslator.class.getName()));
        MarketDataRequestBuilder builder = MarketDataRequestBuilder.newRequest();
        DataFlowID dfid = moduleManager.createDataFlow(new DataRequest[] { new DataRequest(getInstanceURN(),
                                                                                           builder.withSymbols("GOOG.csv")
                                                                                                  .withContent("LATEST_TICK").create()) });
        try {
            Event event = events.take();
            assertTrue(event.toString(), event instanceof TradeEvent);
            TradeEvent theTrade = (TradeEvent) event;
            assertEquals("GOOG", theTrade.getInstrument().getSymbol());
            assertEquals(12345, theTrade.getTimeMillis());
            assertEquals("N", theTrade.getExchange());
            assertEquals(new BigDecimal("400"), theTrade.getPrice());
            assertEquals(new BigDecimal("100"), theTrade.getSize());
            assertEquals("GOOG", theTrade.getInstrument().getSymbol());
        } finally {
            moduleManager.cancel(dfid);
        }
    }

    @Test(timeout = 60*1000) // 60 secs
    /** Setup a data flow where we receive events in our custom listener
     * Expecting 22 events, 2 asks, 20 bids
	 * Verify that the right events are coming through
	 *  Expecting one trade:
     * 12345,GOOG,N,400,100,TRADE
     */
    public void testCSVMarketData_TOB() throws Exception {
        final BlockingQueue<Event> events = new ArrayBlockingQueue<Event>(50);
        moduleManager.addSinkListener(new SinkDataListener() {
            @Override
            public void receivedData(DataFlowID inFlowID, Object inData) {
                events.add((Event) inData);
            }
        });
        CSVFeed feed = CSVFeedFactory.getInstance().getMarketDataFeed();
        feed.doLogin(CSVFeedCredentials.getInstance(0,
                                                    false,
                                                    DATA_DIR,
                                                    MockCSVFeedEventTranslator.class.getName()));
        DataFlowID dfid = moduleManager.createDataFlow(new DataRequest[] { new DataRequest(getInstanceURN(),
                                                                                          MarketDataRequestBuilder.newRequest().withSymbols("GOOG.csv")
                                                                                                                               .withContent("TOP_OF_BOOK").create()) });

        try {
            int nBids=0, nAsks = 0;
            for (int i=0; i<22; i++) {
                Event theEvent = events.take();
                if(theEvent instanceof AskEvent) {
                    nAsks++;
                } else if(theEvent instanceof BidEvent) {
                    nBids++;
                } else {
                    fail("Unexpected event: "+theEvent);
                }
            }
            assertEquals("bids", 20, nBids);
            assertEquals("asks", 2, nAsks);
        } finally {
            moduleManager.cancel(dfid);
        }

    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataModuleTestBase#dataRequestFromString()
     */
    @Override
    public void dataRequestFromString()
            throws Exception
    {
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataModuleTestBase#dataRequestProducesData()
     */
    @Override
    public void dataRequestProducesData()
            throws Exception
    {
    }
}
