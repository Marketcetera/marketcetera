package org.marketcetera.marketdata.core.webservice.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.marketcetera.core.LoggerConfiguration;
import org.marketcetera.marketdata.MarketDataRequestBuilder;
import org.marketcetera.marketdata.bogus.BogusFeedModuleFactory;
import org.marketcetera.marketdata.core.webservice.MarketDataWebServiceClient;
import org.marketcetera.module.ModuleManager;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/* $License$ */

/**
 * Tests {@link MarketDataWebServiceImpl}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"file:src/test/sample_data/conf/web.xml"})
public class MarketDataWebServiceImplTest
        implements ApplicationContextAware
{
    /**
     * Runs once before all tests.
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
     * Runs before each test.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Before
    public void setup()
            throws Exception
    {
        MockServer server = context.getBean(MockServer.class);
        MarketDataWebServiceClientImpl client = new MarketDataWebServiceClientImpl();
        client.setHostname(server.getHostname());
        client.setPort(server.getPort());
        client.start();
        marketDataClient = client;
        moduleManager = new ModuleManager();
        moduleManager.init();
        moduleManager.start(BogusFeedModuleFactory.INSTANCE_URN);
    }
    /**
     * Runs after each test.
     *
     * @throws Exception if an unexpected error occurs
     */
    @After
    public void cleanup()
            throws Exception
    {
        if(marketDataClient != null) {
            marketDataClient.stop();
            marketDataClient = null;
        }
        if(moduleManager != null) {
            moduleManager.stop();
            moduleManager = null;
        }
    }
    /* (non-Javadoc)
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    @Override
    public void setApplicationContext(ApplicationContext inContext)
            throws BeansException
    {
        context = inContext;
    }
    @Test
    public void testRequest()
            throws Exception
    {
        marketDataClient.request(MarketDataRequestBuilder.newRequestFromString("SYMBOLS=METC:CONTENT=TOP_OF_BOOK"));
    }
    /**
     * 
     */
    private ModuleManager moduleManager;
    /**
     * 
     */
    private MarketDataWebServiceClient marketDataClient;
    /**
     * 
     */
    private ApplicationContext context;
}
