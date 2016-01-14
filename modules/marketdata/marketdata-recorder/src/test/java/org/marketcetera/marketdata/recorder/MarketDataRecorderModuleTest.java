package org.marketcetera.marketdata.recorder;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.marketcetera.event.QuoteEvent;
import org.marketcetera.event.TimestampGenerator;
import org.marketcetera.event.TradeEvent;
import org.marketcetera.module.ModuleManager;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class MarketDataRecorderModuleTest
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
        moduleManager = new ModuleManager();
        moduleManager.init();
        timestampGenerator = new TimestampGenerator() {
            @Override
            public DateTime generateTimestamp(TradeEvent inTrade)
            {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException();
            }
            @Override
            public DateTime generateTimestamp(QuoteEvent inQuote)
            {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException();
            }
            @Override
            public DateTime generateTimestamp(String inTimestamp)
            {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException();
            }
        };
    }
    /**
     * Run after each test.
     *
     * @throws Exception if an unexpected error occurs
     */
    @After
    public void cleanup()
            throws Exception
    {
        moduleManager.stop();
    }
    @Ignore@Test
    public void testOne()
            throws Exception
    {
        ApplicationContext context = generateApplicationContext(new MarketDataRecorderModuleConfiguration());
        Thread.sleep(10000);
    }
    private ApplicationContext generateApplicationContext(MarketDataRecorderModuleConfiguration inConfig)
    {
        inConfig.start();
        GenericApplicationContext context = new GenericApplicationContext();
        ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
        beanFactory.registerSingleton(inConfig.getClass().getSimpleName(),
                                      inConfig);
        context.refresh();
        context.start();
        return context;
    }
    private TimestampGenerator timestampGenerator;
    private ModuleManager moduleManager;
}
