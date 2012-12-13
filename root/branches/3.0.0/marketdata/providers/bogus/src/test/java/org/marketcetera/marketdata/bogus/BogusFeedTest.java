package org.marketcetera.marketdata.bogus;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.marketcetera.api.systemmodel.Subscriber;
import org.marketcetera.core.LoggerConfiguration;
import org.marketcetera.core.symbolresolver.MockSymbolResolver;
import org.marketcetera.core.trade.Equity;
import org.marketcetera.core.trade.Instrument;
import org.marketcetera.marketdata.Content;
import org.marketcetera.marketdata.request.MarketDataRequest;
import org.marketcetera.marketdata.request.MarketDataRequestBuilder;
import org.marketcetera.marketdata.request.MarketDataRequestToken;
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
    @Before
    public void setup()
            throws Exception
    {
        symbolResolver = new MockSymbolResolver();
        equity = new Equity("METC");
        symbolResolver.addSymbolMap(equity.getFullSymbol(),
                                    equity);
        feed = new BogusFeed();
        feed.setSymbolResolver(symbolResolver);
        feed.start();
    }
    @Test
    public void testBasic()
            throws Exception
    {
        MarketDataRequestBuilder builder = new MarketDataRequestBuilderImpl();
        builder.withContent(Content.TOP_OF_BOOK)
               .withSymbols(equity.getFullSymbol());
        final MarketDataRequest request = builder.create();
        final Subscriber subscriber = new Subscriber() {
            @Override
            public void publishTo(Object inData)
            {
                System.out.println("Received " + inData);
            }
        };
        final long id = System.nanoTime();
        MarketDataRequestToken inRequestToken = new MarketDataRequestToken() {
            @Override
            public long getId()
            {
                return id;
            }
            @Override
            public Subscriber getSubscriber()
            {
                return subscriber;
            }
            @Override
            public MarketDataRequest getRequest()
            {
                return request;
            }
            private static final long serialVersionUID = 1L;
        };
        feed.requestMarketData(inRequestToken);
        Thread.sleep(5000);
        feed.stop();
    }
    private MockSymbolResolver symbolResolver;
    private BogusFeed feed;
    private Instrument equity;
}
