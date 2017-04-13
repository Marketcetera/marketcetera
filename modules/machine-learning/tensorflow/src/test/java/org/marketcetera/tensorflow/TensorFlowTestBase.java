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
                moduleManager.cancel(dataFlow);
            }
            dataFlows.clear();
        }
        synchronized(modelModules) {
            for(ModuleURN modelUrn : modelModules) {
                moduleManager.stop(modelUrn);
            }
            modelModules.clear();
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
     * @return a <code>DataFlowID</code> value
     */
    protected DataFlowID startConverterDataFlow()
    {
        DataFlowID dataFlow = moduleManager.createDataFlow(getConverterDataRequest());
        synchronized(dataFlows) {
            dataFlows.add(dataFlow);
        }
        return dataFlow;
    }
    /**
     * Start a model data flow.
     *
     * @param inModelName a <code>String</code> value
     * @param inRunner a <code>TensorFlowRunner</code> value
     * @return a <code>DataFlowID</code> value
     */
    protected DataFlowID startModelDataFlow(String inModelName,
                                            TensorFlowRunner inRunner)
    {
        DataFlowID dataFlow = moduleManager.createDataFlow(getModelDataRequest(inModelName,
                                                                               inRunner));
        synchronized(dataFlows) {
            dataFlows.add(dataFlow);
        }
        return dataFlow;
    }
    /**
     * Build a standard data request.
     *
     * @return a <code>DataRequest[]</code> value
     */
    protected DataRequest[] getConverterDataRequest()
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
     * Build a data request for the given TensorFlow module with the given runner.
     *
     * @param inModelName
     * @param inRunner
     * @return a <code>DataRequest[]</code> value
     */
    protected DataRequest[] getModelDataRequest(String inModelName,
                                                TensorFlowRunner inRunner)
    {
        List<DataRequest> dataRequestBuilder = Lists.newArrayList();
        createHeadwaterModule();
        createModelModule(inModelName);
        createPublisherModule();
        dataRequestBuilder.add(new DataRequest(headwaterUrn));
        dataRequestBuilder.add(new DataRequest(TensorFlowConverterModuleFactory.INSTANCE_URN));
        dataRequestBuilder.add(new DataRequest(TensorFlowModelModuleFactory.PROVIDER_URN,
                                               inRunner));
        dataRequestBuilder.add(new DataRequest(publisherUrn));
        return dataRequestBuilder.toArray(new DataRequest[dataRequestBuilder.size()]);
    }
    /**
     * Build a data request for the given TensorFlow module with the given runner.
     *
     * @param inModelName
     * @param inRunnerId
     * @return a <code>DataRequest[]</code> value
     */
    protected DataRequest[] getModelDataRequest(String inModelName,
                                                String inRunnerId)
    {
        List<DataRequest> dataRequestBuilder = Lists.newArrayList();
        createHeadwaterModule();
        createModelModule(inModelName);
        createPublisherModule();
        dataRequestBuilder.add(new DataRequest(headwaterUrn));
        dataRequestBuilder.add(new DataRequest(TensorFlowModelModuleFactory.PROVIDER_URN,
                                               inRunnerId));
        dataRequestBuilder.add(new DataRequest(publisherUrn));
        return dataRequestBuilder.toArray(new DataRequest[dataRequestBuilder.size()]);
    }
    /**
     * Create the model module.
     *
     * @return a <code>ModuleURN</code> value
     */
    protected ModuleURN createModelModule(String inModelName)
    {
        ModuleURN modelUrn = moduleManager.createModule(TensorFlowModelModuleFactory.PROVIDER_URN,
                                                        inModelName);
        modelModules.add(modelUrn);
        return modelUrn;
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
     * tensor flow model modules created during the test
     */
    protected final List<ModuleURN> modelModules = Lists.newArrayList();
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
