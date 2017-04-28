package org.marketcetera.marketdata.exsim;

import static org.junit.Assert.assertEquals;

import java.lang.management.ManagementFactory;

import javax.management.JMX;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.marketcetera.marketdata.AbstractMarketDataModuleMXBean;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.module.ModuleState;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/* $License$ */

/**
 * Test {@link ExsimFeedModule}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"file:src/test/sample_data/conf/test.xml"})
public class ExsimFeedModuleTest
        implements ApplicationContextAware
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
        mbeanServer = ManagementFactory.getPlatformMBeanServer();
        moduleManager = applicationContext.getBean(ModuleManager.class);
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
        try {
            moduleManager.stop(ExsimFeedModuleFactory.INSTANCE_URN);
        } catch (Exception ignored) {}
    }
    /**
     * Test start and stop of the module.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testStartAndStop()
            throws Exception
    {
        moduleManager.start(ExsimFeedModuleFactory.INSTANCE_URN);
        assertEquals(ModuleState.STARTED,
                     moduleManager.getModuleInfo(ExsimFeedModuleFactory.INSTANCE_URN).getState());
        // uncomment this next block to make it actually connect
//        final AbstractMarketDataModuleMXBean moduleBean = getModuleBean();
//        assertNotNull(moduleBean);
//        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
//            @Override
//            public Boolean call()
//                    throws Exception
//            {
//                FeedStatus feedStatus = FeedStatus.valueOf(moduleBean.getFeedStatus());
//                return feedStatus.isRunning();
//            }
//        });
        moduleManager.stop(ExsimFeedModuleFactory.INSTANCE_URN);
    }
    /* (non-Javadoc)
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    @Override
    public void setApplicationContext(ApplicationContext inApplicationContext)
            throws BeansException
    {
        applicationContext = inApplicationContext;
    }
    /**
     * Gets the admin bean for the given session.
     *
     * @return an <code>AbstractMarketDataModuleMXBean</code> value
     * @throws MalformedObjectNameException if an error occurs getting the provider bean
     */
    @SuppressWarnings("unused")
    private AbstractMarketDataModuleMXBean getModuleBean()
            throws MalformedObjectNameException
    {
        ObjectName sessionObjectName = getModuleObjectName();
        AbstractMarketDataModuleMXBean sessionAdmin = JMX.newMXBeanProxy(mbeanServer,
                                                                         sessionObjectName,
                                                                         AbstractMarketDataModuleMXBean.class,
                                                                         true);
        return sessionAdmin;
    }
    /**
     * Gets the <code>ObjectName</code> for the module to be watched.
     *
     * @return an <code>ObjectName</code> value
     * @throws MalformedObjectNameException if the object name cannot be constructed
     */
    private ObjectName getModuleObjectName()
            throws MalformedObjectNameException
    {
        // sample for old module: org.marketcetera.module:type=mdata,provider=activ,name=single
        StringBuilder builder = new StringBuilder();
        builder.append("org.marketcetera.module:type=mdata,provider=").append(ExsimFeedModuleFactory.IDENTIFIER).append(",name=single"); //$NON-NLS-1$ //$NON-NLS-2$
        return new ObjectName(builder.toString());
    }
    /**
     * test application context
     */
    private ApplicationContext applicationContext;
    /**
     * test module manager
     */
    private ModuleManager moduleManager;
    /**
     * provides access to mbean objects
     */
    private MBeanServer mbeanServer;
}
