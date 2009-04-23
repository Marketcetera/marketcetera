package org.marketcetera.marketdata.bogus;

import static org.junit.Assert.assertTrue;
import static org.marketcetera.marketdata.Capability.LATEST_TICK;
import static org.marketcetera.marketdata.Capability.LEVEL_2;
import static org.marketcetera.marketdata.Capability.MARKET_STAT;
import static org.marketcetera.marketdata.Capability.OPEN_BOOK;
import static org.marketcetera.marketdata.Capability.TOP_OF_BOOK;
import static org.marketcetera.marketdata.Capability.TOTAL_VIEW;
import static org.marketcetera.marketdata.Capability.UNKNOWN;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.marketcetera.event.HasSymbol;
import org.marketcetera.marketdata.Capability;
import org.marketcetera.marketdata.MarketDataModuleTestBase;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.MarketDataRequest.Content;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.module.DataRequest;
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
    extends MarketDataModuleTestBase
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
        final Set<String> symbols = new HashSet<String>();
        moduleManager.addSinkListener(new SinkDataListener() {
            @Override
            public void receivedData(DataFlowID inFlowID,
                                     Object inData) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                if(inData instanceof HasSymbol) {
                    symbols.add(((HasSymbol)inData).getSymbol().getFullSymbol());
                }
            }
        });
        assertTrue(symbols.isEmpty());
        DataFlowID id1 = moduleManager.createDataFlow(new DataRequest[] { new DataRequest(getInstanceURN(),
                                                                                          MarketDataRequest.newRequest().withSymbols("IBM")) });
        Thread.sleep(2000);
        DataFlowID id2 = moduleManager.createDataFlow(new DataRequest[] { new DataRequest(getInstanceURN(),
                                                                                          MarketDataRequest.newRequest().withSymbols("GOOG").withContent(Content.MARKET_STAT)) });
        Thread.sleep(5000);
        moduleManager.cancel(id1);
        moduleManager.cancel(id2);
        assertTrue(symbols.contains("IBM"));
        assertTrue(symbols.contains("GOOG"));
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
        return new Capability[] { TOP_OF_BOOK,LEVEL_2,OPEN_BOOK,TOTAL_VIEW,LATEST_TICK,MARKET_STAT };
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
