package org.marketcetera.marketdata.bogus;

import org.junit.BeforeClass;
import org.junit.Test;
import org.marketcetera.api.systemmodel.Subscriber;
import org.marketcetera.core.LoggerConfiguration;
import org.marketcetera.core.trade.impl.EquityImpl;
import org.marketcetera.marketdata.Content;
import org.marketcetera.marketdata.request.MarketDataRequest;
import org.marketcetera.marketdata.request.MarketDataRequestBuilder;
import org.marketcetera.marketdata.request.impl.MarketDataRequestBuilderImpl;

/* $License$ */

/**
 * Tests {@link BogusFeed}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class BogusFeedTest
{
    /**
     * Run once before all tests.
     *
     * @throws Exception if an unexpected exception occurs
     */
    @BeforeClass
    public static void once()
            throws Exception
    {
        LoggerConfiguration.logSetup();
    }
    @Test
    public void testBasic()
            throws Exception
    {
        BogusFeed feed = new BogusFeed();
        feed.start();
        MarketDataRequestBuilder builder = new MarketDataRequestBuilderImpl();
        builder.withContent(Content.TOP_OF_BOOK)
               .withInstruments(new EquityImpl("METC"));
        MarketDataRequest request = builder.create();
        Subscriber subscriber = new Subscriber() {
            @Override
            public boolean isInteresting(Object inData)
            {
                return true;
            }
            @Override
            public void publishTo(Object inData)
            {
                System.out.println("Received " + inData);
            }
        };
        System.out.println("Received " + feed.requestMarketData(request,
                                                                subscriber));
        Thread.sleep(5000);
        feed.stop();
    }
}
