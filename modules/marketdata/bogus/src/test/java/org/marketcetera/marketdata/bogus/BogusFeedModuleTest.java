package org.marketcetera.marketdata.bogus;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.marketcetera.marketdata.Capability.BBO10;
import static org.marketcetera.marketdata.Capability.DIVIDEND;
import static org.marketcetera.marketdata.Capability.EVENT_BOUNDARY;
import static org.marketcetera.marketdata.Capability.LATEST_TICK;
import static org.marketcetera.marketdata.Capability.LEVEL_2;
import static org.marketcetera.marketdata.Capability.MARKET_STAT;
import static org.marketcetera.marketdata.Capability.OPEN_BOOK;
import static org.marketcetera.marketdata.Capability.TOP_OF_BOOK;
import static org.marketcetera.marketdata.Capability.TOTAL_VIEW;
import static org.marketcetera.marketdata.Capability.UNKNOWN;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import org.junit.Test;
import org.marketcetera.event.HasInstrument;
import org.marketcetera.marketdata.Capability;
import org.marketcetera.marketdata.Content;
import org.marketcetera.marketdata.MarketDataFeedTestBase;
import org.marketcetera.marketdata.MarketDataRequestBuilder;
import org.marketcetera.marketdata.NewMarketDataModuleTestBase;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.module.DataRequest;
import org.marketcetera.module.ModuleException;
import org.marketcetera.module.ModuleFactory;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.module.SinkDataListener;

/* $License$ */

/**
 * Tests {@link BogusFeedModule}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.0.0
 */
public class BogusFeedModuleTest
    extends NewMarketDataModuleTestBase
{
    /**
     * Tests a deadlock scenario that occurred for Bogus feed.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void deadlock()
        throws Exception
    {
        final Set<String> symbols = Collections.synchronizedSet(new HashSet<String>());
        moduleManager.addSinkListener(new SinkDataListener() {
            @Override
            public void receivedData(DataFlowID inFlowID,
                                     Object inData) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                if(inData instanceof HasInstrument) {
                    symbols.add(((HasInstrument)inData).getInstrument().getSymbol());
                }
            }
        });
        assertTrue(symbols.isEmpty());
        DataFlowID id1 = moduleManager.createDataFlow(new DataRequest[] { new DataRequest(getInstanceURN(),
                                                                                          MarketDataRequestBuilder.newRequest().withSymbols("IBM").create()) });
        Thread.sleep(2000);
        DataFlowID id2 = moduleManager.createDataFlow(new DataRequest[] { new DataRequest(getInstanceURN(),
                                                                                          MarketDataRequestBuilder.newRequest().withSymbols("GOOG").withContent(Content.MARKET_STAT).create()) });
        Thread.sleep(5000);
        moduleManager.cancel(id1);
        moduleManager.cancel(id2);
        assertTrue(symbols.contains("IBM"));
        assertTrue(symbols.contains("GOOG"));
    }
    /**
     * Tests another deadlock that shows up with Bogus.
     *
     * @throws Exception if an error occurs
     */
    @Test(timeout=15000)
    public void yetAnotherDeadlock()
        throws Exception
    {
        final List<DataFlowID> createdFlows = new ArrayList<DataFlowID>();
        try {
            final List<Exception> caughtExceptions = new ArrayList<Exception>();
            final List<Object> receivedData = new ArrayList<Object>();
            // use this to guarantee that only about 1 or so additional data flows will be requested (I say or so because the flag isn't synchronized, but that's ok)
            final boolean[] dataRequested = new boolean[] { false };
            assertTrue(caughtExceptions.isEmpty());
            assertTrue(receivedData.isEmpty());
            moduleManager.addSinkListener(new SinkDataListener() {
                @Override
                public void receivedData(DataFlowID inFlowID,
                                         Object inData)
                {
                    try {
                        if(!dataRequested[0]) {
                            // this call will hang if the deadlock has not been repaired 
                            createdFlows.add(moduleManager.createDataFlow(new DataRequest[] { new DataRequest(BogusFeedModuleFactory.INSTANCE_URN,
                                                                                                              MarketDataRequestBuilder.newRequest().withSymbols("GOOG").create()) }));
                            dataRequested[0] = true;
                        }
                        receivedData.add(inData);
                    } catch (ModuleException e) {
                        caughtExceptions.add(e);
                    }
                }
            });
            createdFlows.add(moduleManager.createDataFlow(new DataRequest[] { new DataRequest(BogusFeedModuleFactory.INSTANCE_URN,
                                                                                              MarketDataRequestBuilder.newRequest().withSymbols("IBM").create()) }));
            // need to infinite loop here since the deadlock is between the market data delivery thread and the new ExecutorThread
            MarketDataFeedTestBase.wait(new Callable<Boolean>() {
                @Override
                public Boolean call()
                        throws Exception
                {
                    return !receivedData.isEmpty();
                }
            });
            assertTrue(caughtExceptions.isEmpty());
            assertFalse(receivedData.isEmpty());
        } finally {
            for(DataFlowID id : createdFlows) {
                moduleManager.cancel(id);
            }
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataModuleTestBase#getFactory()
     */
    @Override
    protected ModuleFactory getFactory()
    {
        return new BogusFeedModuleFactory();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataModuleTestBase#getInstanceURN()
     */
    @Override
    protected ModuleURN getInstanceURN()
    {
        return BogusFeedModuleFactory.INSTANCE_URN;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataModuleTestBase#getExpectedCapabilities()
     */
    @Override
    protected Capability[] getExpectedCapabilities()
    {
        return new Capability[] { TOP_OF_BOOK,LEVEL_2,OPEN_BOOK,TOTAL_VIEW,LATEST_TICK,MARKET_STAT,DIVIDEND,EVENT_BOUNDARY,BBO10 };
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataModuleTestBase#getUnexpectedCapability()
     */
    @Override
    protected Capability getUnexpectedCapability()
    {
        return UNKNOWN;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataModuleTestBase#getProvider()
     */
    @Override
    protected String getProvider()
    {
        return BogusFeedModuleFactory.IDENTIFIER;
    }
}
