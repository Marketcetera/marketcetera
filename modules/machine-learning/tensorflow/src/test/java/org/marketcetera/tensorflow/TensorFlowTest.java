package org.marketcetera.tensorflow;

import java.lang.management.ManagementFactory;
import java.util.List;

import javax.management.MBeanServer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.marketcetera.module.DataRequest;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.module.ModuleURN;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.collect.Lists;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/test.xml"})
public class TensorFlowTest
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
    }
    @Test
    public void testOne()
            throws Exception
    {
        moduleManager.createDataFlow(getDataRequest("test"));
    }
    private DataRequest[] getDataRequest(String inNamespace)
    {
        List<DataRequest> dataRequestBuilder = Lists.newArrayList();
        dataRequestBuilder.add(new DataRequest(new ModuleURN(String.format("metc:ml:%s:%s",
                                                                           "tensorflow",
                                                                           inNamespace))));
        return dataRequestBuilder.toArray(new DataRequest[dataRequestBuilder.size()]);
    }
    /**
     * test application context
     */
    @Autowired
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
