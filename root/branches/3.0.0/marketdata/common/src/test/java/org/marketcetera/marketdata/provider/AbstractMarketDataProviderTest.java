package org.marketcetera.marketdata.provider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.marketcetera.core.ExpectedFailure;
import org.marketcetera.core.LoggerConfiguration;
import org.marketcetera.marketdata.FeedStatus;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class AbstractMarketDataProviderTest
{
    /**
     * 
     *
     * @throws Exception if an unexpected error occurs
     */
    @BeforeClass
    public static void once()
            throws Exception
    {
        LoggerConfiguration.logSetup();
    }
    /**
     * 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Before
    public void setup()
            throws Exception
    {
        provider = new MockMarketDataProvider();
        provider.start();
    }
    /**
     * Tests {@link AbstractMarketDataProvider#start()} and {@link AbstractMarketDataProvider#stop()}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testStartStop()
            throws Exception
    {
        assertTrue(provider.isRunning());
        assertEquals(FeedStatus.AVAILABLE,
                     provider.getFeedStatus());
        provider.start();
        assertTrue(provider.isRunning());
        assertEquals(FeedStatus.AVAILABLE,
                     provider.getFeedStatus());
        provider.stop();
        assertFalse(provider.isRunning());
        assertEquals(FeedStatus.OFFLINE,
                     provider.getFeedStatus());
        provider.stop();
        assertFalse(provider.isRunning());
        assertEquals(FeedStatus.OFFLINE,
                     provider.getFeedStatus());
        // pathological cases
        NullPointerException exception = new NullPointerException("this exception is expected");
        provider.setExceptionOnStart(exception);
        new ExpectedFailure<MarketDataProviderStartFailed>() {
            @Override
            protected void run()
                    throws Exception
            {
                provider.start();
            }
        };
        assertFalse(provider.isRunning());
        assertEquals(FeedStatus.ERROR,
                     provider.getFeedStatus());
        provider.reset();
        provider.start();
        assertTrue(provider.isRunning());
        assertEquals(FeedStatus.AVAILABLE,
                     provider.getFeedStatus());
        provider.setExceptionOnStop(exception);
        provider.stop();
        assertFalse(provider.isRunning());
        assertEquals(FeedStatus.ERROR,
                     provider.getFeedStatus());
    }
    /**
     * test market data provider
     */
    private MockMarketDataProvider provider;
}
