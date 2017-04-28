package org.marketcetera.tensorflow;

import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.Deque;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.module.DataRequest;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.modules.headwater.HeadwaterModuleFactory;
import org.marketcetera.modules.publisher.PublisherModuleFactory;
import org.marketcetera.tensorflow.converter.TensorFlowConverterModuleFactory;
import org.marketcetera.tensorflow.converters.TensorFromObjectConverter;
import org.marketcetera.tensorflow.dao.GraphContainerDao;
import org.marketcetera.tensorflow.model.TensorFlowModelModuleFactory;
import org.marketcetera.tensorflow.model.TensorFlowRunner;
import org.marketcetera.tensorflow.service.TensorFlowService;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.collect.Lists;

/* $License$ */

/**
 * Provides common test behavior for TensorFlow tests.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:/test.xml"})
public class TensorFlowTestBase
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
     * Reset the test objects.
     */
    protected void reset()
    {
        synchronized(dataFlows) {
            for(DataFlowID dataFlow : dataFlows) {
                try {
                    moduleManager.cancel(dataFlow);
                } catch (Exception ignored) {}
            }
            dataFlows.clear();
        }
        synchronized(receivedData) {
            receivedData.clear();
        }
        graphContainerDao.deleteAll();
    }
    /**
     * Wait for an remove the next object in the received data queue.
     *
     * @return an <code>Object</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected Object waitForData()
                throws Exception
    {
        long start = System.currentTimeMillis();
        while(System.currentTimeMillis() < start+10000) {
            synchronized(receivedData) {
                Object data = receivedData.poll();
                if(data != null) {
                    return data;
                }
                receivedData.wait(100);
            }
        }
        fail("No tensor received in 10s");
        return null;
    }
    /**
     * Start a test data flow.
     *
     * @param inConverter a <code>TensorFromObjectConverter&lt;?&gt;</code> value
     * @return a <code>DataFlowID</code> value
     */
    protected DataFlowID startConverterDataFlow(TensorFromObjectConverter<?> inConverter)
    {
        DataFlowID dataFlow = moduleManager.createDataFlow(getConverterDataRequest(inConverter));
        synchronized(dataFlows) {
            dataFlows.add(dataFlow);
        }
        return dataFlow;
    }
    /**
     * Start a model data flow.
     *
     * @param inConverter a <code>TensorFromObjectConverter&lt;?&gt;</code> value
     * @param inRunner a <code>TensorFlowRunner</code> value
     * @return a <code>DataFlowID</code> value
     */
    protected DataFlowID startModelDataFlow(TensorFromObjectConverter<?> inConverter,
                                            TensorFlowRunner inRunner)
    {
        DataFlowID dataFlow = moduleManager.createDataFlow(getModelDataRequest(inConverter,
                                                                               inRunner));
        synchronized(dataFlows) {
            dataFlows.add(dataFlow);
        }
        return dataFlow;
    }
    /**
     * Build a standard data request.
     *
     * @param inConverter a <code>TensorFromObjectConverter&lt;?&gt;</code> value
     * @return a <code>DataRequest[]</code> value
     */
    protected DataRequest[] getConverterDataRequest(TensorFromObjectConverter<?> inConverter)
    {
        List<DataRequest> dataRequestBuilder = Lists.newArrayList();
        createHeadwaterModule();
        createPublisherModule();
        dataRequestBuilder.add(new DataRequest(headwaterUrn));
        dataRequestBuilder.add(new DataRequest(TensorFlowConverterModuleFactory.INSTANCE_URN,
                                               inConverter));
        dataRequestBuilder.add(new DataRequest(publisherUrn));
        return dataRequestBuilder.toArray(new DataRequest[dataRequestBuilder.size()]);
    }
    /**
     * Build a data request for the given TensorFlow module with the given runner.
     *
     * @param inConverter a <code>TensorFromObjectConverter&lt;?&gt;</code> value
     * @param inRunner a <code>TensorFlowRunner</code> value
     * @return a <code>DataRequest[]</code> value
     */
    protected DataRequest[] getModelDataRequest(TensorFromObjectConverter<?> inConverter,
                                                TensorFlowRunner inRunner)
    {
        List<DataRequest> dataRequestBuilder = Lists.newArrayList();
        createHeadwaterModule();
        createPublisherModule();
        dataRequestBuilder.add(new DataRequest(headwaterUrn));
        dataRequestBuilder.add(new DataRequest(TensorFlowConverterModuleFactory.INSTANCE_URN,
                                               inConverter));
        dataRequestBuilder.add(new DataRequest(TensorFlowModelModuleFactory.INSTANCE_URN,
                                               inRunner));
        dataRequestBuilder.add(new DataRequest(publisherUrn));
        return dataRequestBuilder.toArray(new DataRequest[dataRequestBuilder.size()]);
    }
    /**
     * Build a data request for the given TensorFlow module with the given runner.
     *
     * @param inRunnerId a <code>String</code> value
     * @return a <code>DataRequest[]</code> value
     */
    protected DataRequest[] getModelDataRequest(String inRunnerId)
    {
        List<DataRequest> dataRequestBuilder = Lists.newArrayList();
        createHeadwaterModule();
        createPublisherModule();
        dataRequestBuilder.add(new DataRequest(headwaterUrn));
        dataRequestBuilder.add(new DataRequest(TensorFlowModelModuleFactory.INSTANCE_URN,
                                               inRunnerId));
        dataRequestBuilder.add(new DataRequest(publisherUrn));
        return dataRequestBuilder.toArray(new DataRequest[dataRequestBuilder.size()]);
    }
    /**
     * Create the headwater module, if necessary.
     *
     * @return a <code>ModuleURN</code> value
     */
    protected ModuleURN createHeadwaterModule()
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
    protected ModuleURN createPublisherModule()
    {
        if(publisherUrn == null) {
            publisherUrn = moduleManager.createModule(PublisherModuleFactory.PROVIDER_URN,
                                                      new ISubscriber(){
                @Override
                public boolean isInteresting(Object inData)
                {
                    return true;
                }
                @Override
                public void publishTo(Object inData)
                {
                    SLF4JLoggerProxy.debug(TensorFlowTestBase.this,
                                           "Received {}",
                                           inData);
                    synchronized(receivedData) {
                        receivedData.add(inData);
                        receivedData.notifyAll();
                    }
                }});
        }
        return publisherUrn;
    }
    /**
     * data flows created during the test
     */
    protected final Collection<DataFlowID> dataFlows = Lists.newArrayList();
    /**
     * stores received data
     */
    protected final Deque<Object> receivedData = Lists.newLinkedList();
    /**
     * provides an entry point to a data flow
     */
    protected ModuleURN headwaterUrn;
    /**
     * receives and published objects in a data flow
     */
    protected ModuleURN publisherUrn;
    /**
     * provides a handle to the headwater module used to initiate the data flow
     */
    protected String headwaterInstance;
    /**
     * provides access to the test context
     */
    @Autowired
    protected ApplicationContext applicationContext;
    /**
     * test module manager
     */
    @Autowired
    protected ModuleManager moduleManager;
    /**
     * provides tensor flow services
     */
    @Autowired
    protected TensorFlowService tensorFlowService;
    /**
     * provides data store access to graph containers
     */
    @Autowired
    protected GraphContainerDao graphContainerDao;
}
