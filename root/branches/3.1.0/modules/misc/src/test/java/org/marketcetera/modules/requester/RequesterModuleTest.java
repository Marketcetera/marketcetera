package org.marketcetera.modules.requester;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.marketdata.MarketDataFeedTestBase;
import org.marketcetera.marketdata.MarketDataRequestBuilder;
import org.marketcetera.marketdata.bogus.BogusFeedModuleFactory;
import org.marketcetera.module.*;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: RequesterModuleTest.java 82384 2012-07-20 19:09:59Z colin $
 * @since $Release$
 */
public class RequesterModuleTest
        extends ModuleTestBase
{
    /**
     * 
     *
     *
     * @throws Exception
     */
    @Before
    public void setup()
            throws Exception
    {
        moduleManager = new ModuleManager();
        moduleManager.init();
    }
    /**
     * 
     *
     *
     * @throws Exception
     */
    @After
    public void cleanup()
            throws Exception
    {
        moduleManager.stop();
        moduleManager = null;
    }
    /**
     * Verifies the provider and module infos.
     *
     * @throws Exception if there were unexpected errors
     */
    @Test
    public void info()
            throws Exception
    {
        assertProviderInfo(moduleManager,
                           RequesterModule.RequesterModuleFactory.PROVIDER_URN,
                           new String[] { String.class.getCanonicalName() },
                           new Class[] { String.class },
                           Messages.PROVIDER_DESCRIPTION.getText(),
                           false,
                           true);
        ModuleURN instanceUrn = moduleManager.createModule(RequesterModule.RequesterModuleFactory.PROVIDER_URN,
                                                           "test");
        assertModuleInfo(moduleManager,
                         instanceUrn,
                         ModuleState.STARTED,
                         null,
                         null,
                         false,
                         true,
                         true,
                         false,
                         true);
    }
    /**
     * 
     *
     *
     * @throws Exception
     */
    @Test
    public void testRequest()
            throws Exception
    {
        moduleManager.start(BogusFeedModuleFactory.INSTANCE_URN);
        ModuleURN instanceUrn = moduleManager.createModule(RequesterModule.RequesterModuleFactory.PROVIDER_URN,
                                                           "test");
        Requester requester = RequesterModule.getRequesterFor(instanceUrn);
        assertNotNull(requester);
        final List<Object> data = new ArrayList<Object>();
        ISubscriber subscriber = new ISubscriber() {
            @Override
            public boolean isInteresting(Object inData)
            {
                return true;
            }
            @Override
            public void publishTo(Object inData)
            {
                data.add(inData);
            }
        };
        requester.subscribeToDataFlow(subscriber,
                                      new DataRequest[] { new DataRequest(BogusFeedModuleFactory.PROVIDER_URN,
                                                                          MarketDataRequestBuilder.newRequest().withSymbols("GOOG").create()) });
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                return data.size() >= 10;
            }
        });
        requester.cancelSubscription(subscriber);
        moduleManager.stop(BogusFeedModuleFactory.INSTANCE_URN);
    }
    /**
     * module manager value
     */
   private ModuleManager moduleManager;
}
