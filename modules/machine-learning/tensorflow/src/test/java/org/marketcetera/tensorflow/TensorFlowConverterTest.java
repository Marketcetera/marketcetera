package org.marketcetera.tensorflow;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Deque;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.module.DataRequest;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.modules.headwater.HeadwaterModule;
import org.marketcetera.modules.headwater.HeadwaterModuleFactory;
import org.marketcetera.modules.publisher.PublisherModuleFactory;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.tensorflow.Tensor;

import com.google.common.collect.Lists;

/* $License$ */

/**
 * Tests {@link TensorFlowConverterModule}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/test.xml"})
public class TensorFlowConverterTest
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
        reset();
    }
    /**
     * Test behavior of a data flow for which no tensor converter exists.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testNoConverter()
            throws Exception
    {
        DataFlowID dataFlow1 = moduleManager.createDataFlow(getDataRequest());
        // no converter for type of TensorFlowConverterTest
        HeadwaterModule.getInstance(headwaterInstance).emit(this,
                                                            dataFlow1);
        assertTrue(receivedTensors.isEmpty());
    }
    /**
     * Build a standard data request.
     *
     * @return a <code>DataRequest[]</code> value
     */
    private DataRequest[] getDataRequest()
    {
        List<DataRequest> dataRequestBuilder = Lists.newArrayList();
        createHeadwaterModule();
        createPublisherModule();
        dataRequestBuilder.add(new DataRequest(headwaterUrn));
        dataRequestBuilder.add(new DataRequest(TensorFlowConverterModuleFactory.INSTANCE_URN));
        dataRequestBuilder.add(new DataRequest(publisherUrn));
        return dataRequestBuilder.toArray(new DataRequest[dataRequestBuilder.size()]);
    }
    /**
     * Create the headwater module, if necessary.
     *
     * @return a <code>ModuleURN</code> value
     */
    private ModuleURN createHeadwaterModule()
    {
        if(headwaterUrn == null) {
            headwaterInstance = "hw"+System.nanoTime();
            headwaterUrn = moduleManager.createModule(HeadwaterModuleFactory.PROVIDER_URN,
                                                      headwaterInstance);
        }
        return headwaterUrn;
    }
    /**
     * Create the publisher module, if necessary.
     *
     * @return a <code>ModuleURN</code> value
     */
    private ModuleURN createPublisherModule()
    {
        if(publisherUrn == null) {
            publisherUrn = moduleManager.createModule(PublisherModuleFactory.PROVIDER_URN,
                                                      new ISubscriber(){
                @Override
                public boolean isInteresting(Object inData)
                {
                    return inData instanceof Tensor;
                }
                @Override
                public void publishTo(Object inData)
                {
                    SLF4JLoggerProxy.debug(TensorFlowConverterTest.this,
                                           "Received {}",
                                           inData);
                    synchronized(receivedTensors) {
                        receivedTensors.add((Tensor)inData);
                        receivedTensors.notifyAll();
                    }
                }});
        }
        return publisherUrn;
    }
    /**
     * Reset the test objects.
     */
    private void reset()
    {
        synchronized(receivedTensors) {
            receivedTensors.clear();
        }
    }
    /**
     * Wait for an remove the next tensor in the received tensor queue.
     *
     * @return a <code>Tensor</code> value
     * @throws Exception if an unexpected error occurs
     */
    @SuppressWarnings("unused")
    private Tensor waitForTensor()
                throws Exception
    {
        long start = System.currentTimeMillis();
        while(System.currentTimeMillis() < start+10000) {
            synchronized(receivedTensors) {
                Tensor tensor = receivedTensors.poll();
                if(tensor != null) {
                    return tensor;
                }
                receivedTensors.wait(100);
            }
        }
        fail("No tensor received in 10s");
        return null;
    }
    /**
     * stores received tensors
     */
    private final Deque<Tensor> receivedTensors = Lists.newLinkedList();
    /**
     * provides an entry point to a data flow
     */
    private ModuleURN headwaterUrn;
    /**
     * receives and published objects in a data flow
     */
    private ModuleURN publisherUrn;
    /**
     * provides a handle to the headwater module used to initiate the data flow
     */
    private String headwaterInstance;
    /**
     * test application context
     */
    @Autowired
    private ApplicationContext applicationContext;
    /**
     * test module manager
     */
    private ModuleManager moduleManager;
}
