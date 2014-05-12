package org.marketcetera.marketdata.provider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.EnumSet;
import java.util.Set;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.marketcetera.core.LoggerConfiguration;
import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.marketdata.*;
import org.marketcetera.marketdata.core.Messages;
import org.marketcetera.marketdata.core.ProviderStatus;
import org.marketcetera.marketdata.core.manager.MarketDataProviderNotAvailable;
import org.marketcetera.marketdata.core.manager.MarketDataRequestFailed;
import org.marketcetera.marketdata.core.provider.AbstractMarketDataProvider;
import org.marketcetera.marketdata.core.provider.MarketDataProviderStartFailed;
import org.marketcetera.marketdata.core.request.MarketDataRequestAtom;
import org.marketcetera.marketdata.core.request.MarketDataRequestToken;
import org.marketcetera.module.ExpectedFailure;

/* $License$ */

/**
 * Tests {@link AbstractMarketDataProvider}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.4.0
 */
public class AbstractMarketDataProviderTest
{
    /**
     * Run once before all tests.
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
     * Run before each test.
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
        assertEquals(ProviderStatus.AVAILABLE,
                     provider.getProviderStatus());
        provider.start();
        assertTrue(provider.isRunning());
        assertEquals(ProviderStatus.AVAILABLE,
                     provider.getProviderStatus());
        provider.stop();
        assertFalse(provider.isRunning());
        assertEquals(ProviderStatus.OFFLINE,
                     provider.getProviderStatus());
        provider.stop();
        assertFalse(provider.isRunning());
        assertEquals(ProviderStatus.OFFLINE,
                     provider.getProviderStatus());
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
        assertEquals(ProviderStatus.ERROR,
                     provider.getProviderStatus());
        provider.reset();
        provider.start();
        assertTrue(provider.isRunning());
        assertEquals(ProviderStatus.AVAILABLE,
                     provider.getProviderStatus());
        provider.setExceptionOnStop(exception);
        provider.stop();
        assertFalse(provider.isRunning());
        assertEquals(ProviderStatus.ERROR,
                     provider.getProviderStatus());
    }
    /**
     * Tests {@link AbstractMarketDataProvider#requestMarketData(org.marketcetera.marketdata.core.request.MarketDataRequestToken)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testRequestMarketData()
            throws Exception
    {
        provider.stop();
        assertFalse(provider.isRunning());
        new ExpectedFailure<MarketDataProviderNotAvailable>() {
            @Override
            protected void run()
                    throws Exception
            {
                provider.requestMarketData(generateRequestToken());
            }
        };
        provider.start();
        assertTrue(provider.getRequestedAtoms().isEmpty());
        final MarketDataRequest request = generateRequest(new String[] { "METC" },
                                                          EnumSet.of(Content.TOP_OF_BOOK));
        provider.requestMarketData(generateRequestToken(request));
        assertEquals(1,
                     provider.getRequestedAtoms().size());
        MarketDataRequestAtom requestedAtom = provider.getRequestedAtoms().get(0);
        assertEquals(Content.TOP_OF_BOOK,
                     requestedAtom.getContent());
        assertEquals("METC",
                     requestedAtom.getSymbol());
        // make a request for an unsupported capability
        provider.stop();
        provider.start();
        provider.reset();
        provider.setCapabilities(EnumSet.of(Capability.LATEST_TICK));
        new ExpectedFailure<MarketDataRequestFailed>(Messages.UNSUPPORTED_MARKETDATA_CONTENT,
                                                     Content.TOP_OF_BOOK,
                                                     provider.getCapabilities().toString()) {
            @Override
            protected void run()
                    throws Exception
            {
                provider.requestMarketData(generateRequestToken(request));
            }
        };
        assertTrue(provider.getRequestedAtoms().isEmpty());
        // make a request for multiple atoms with different content
        final MarketDataRequest multiContentRequest = generateRequest(new String[] { "METC" },
                                                                      EnumSet.of(Content.LATEST_TICK,Content.TOP_OF_BOOK));
        assertTrue(provider.getCapabilities().contains(Capability.LATEST_TICK));
        assertFalse(provider.getCapabilities().contains(Capability.TOP_OF_BOOK));
        assertTrue(provider.getRequestedAtoms().isEmpty());
        assertFalse(provider.getCanceledAtoms().isEmpty());
        new ExpectedFailure<MarketDataRequestFailed>(Messages.UNSUPPORTED_MARKETDATA_CONTENT,
                                                     Content.TOP_OF_BOOK,
                                                     provider.getCapabilities().toString()) {
            @Override
            protected void run()
                    throws Exception
            {
                provider.requestMarketData(generateRequestToken(multiContentRequest));
            }
        };
        assertEquals(0,
                     provider.getRequestedAtoms().size());
        assertEquals(3,
                     provider.getCanceledAtoms().size());
//        assertSame(provider.getRequestedAtoms().get(0),
//                   provider.getCanceledAtoms().get(0));
    }
    private MarketDataRequest generateRequest(String[] inSymbols,
                                              Set<Content> inContent)
    {
        MarketDataRequestBuilder requestBuilder = MarketDataRequestBuilder.newRequest();
        return requestBuilder.withSymbols(inSymbols)
                             .withContent(inContent).create();
    }
    private MarketDataRequest generateRequest()
    {
        return generateRequest(new String[] { "AAPL","GOOG","METC" },
                               EnumSet.of(Content.TOP_OF_BOOK,Content.LATEST_TICK));
    }
    private MarketDataRequestToken generateRequestToken()
    {
        return generateRequestToken(new ISubscriber() {
            @Override
            public void publishTo(Object inData)
            {
            }
            @Override
            public boolean isInteresting(Object inData)
            {
                return true;
            }
        });
    }
    private MarketDataRequestToken generateRequestToken(MarketDataRequest inRequest)
    {
        return generateRequestToken(inRequest,
                                    new ISubscriber() {
            @Override
            public void publishTo(Object inData)
            {
            }
            @Override
            public boolean isInteresting(Object inData)
            {
                return true;
            }
        });
    }
    private MarketDataRequestToken generateRequestToken(ISubscriber inSubscriber)
    {
        return generateRequestToken(generateRequest(),
                                    inSubscriber);
    }
    private MarketDataRequestToken generateRequestToken(MarketDataRequest inRequest,
                                                        ISubscriber inSubscriber)
    {
        MarketDataRequestToken token = mock(MarketDataRequestToken.class);
        when(token.getId()).thenReturn(System.nanoTime());
        when(token.getRequest()).thenReturn(inRequest);
        when(token.getSubscriber()).thenReturn(inSubscriber);
        return token;
    }
    /**
     * test market data provider
     */
    private MockMarketDataProvider provider;
}
