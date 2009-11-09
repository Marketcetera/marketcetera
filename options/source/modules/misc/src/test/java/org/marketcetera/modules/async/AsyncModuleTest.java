package org.marketcetera.modules.async;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.module.*;
import org.marketcetera.core.LoggerConfiguration;
import static org.marketcetera.modules.async.SimpleAsyncProcessorFactory.PROVIDER_URN;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.hamcrest.Matchers;

import javax.management.*;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.math.BigDecimal;


/* $License$ */
/**
 * Tests the {@link SimpleAsyncProcessor} module.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class AsyncModuleTest extends ModuleTestBase {

    @BeforeClass
    public static void logSetup() {
        LoggerConfiguration.logSetup();
    }

    /**
     * Verifies the provider and module infos.
     *
     * @throws Exception if there were unexpected errors
     */
    @Test
    public void info() throws Exception {
        //Provider Info
        assertProviderInfo(mManager, PROVIDER_URN,
                new String[]{ModuleURN.class.getName()}, new Class[]{ModuleURN.class},
                Messages.PROVIDER_DESCRIPTION.getText(),
                true, true);
        //Create a module and test its info
        ModuleURN instanceURN = new ModuleURN(PROVIDER_URN, "mymodule");
        assertEquals(instanceURN, mManager.createModule(
                PROVIDER_URN,
                instanceURN));
        assertModuleInfo(mManager, instanceURN, ModuleState.STARTED, null,
                null, false, true, true, true, false);
        //verify that the module has no flow attributes.
        assertTrue(getAttributes(instanceURN).isEmpty());
        //Stop and Delete the module
        mManager.stop(instanceURN);
        mManager.deleteModule(instanceURN);
    }

    /**
     * Verifies what kinds of request parameters (null) can be supplied to the
     * module when initiating data flow requests.
     *
     * @throws Exception if there was an error.
     */
    @Test
    public void requestParameters() throws Exception {
        final ModuleURN instanceURN = new ModuleURN(PROVIDER_URN, "mymodule");
        final String requestParm = "not null value";
        new ExpectedFailure<IllegalRequestParameterValue>(
                org.marketcetera.module.Messages.ILLEGAL_REQ_PARM_VALUE,
                instanceURN.getValue(), requestParm) {
            public void run() throws Exception {
                mManager.createDataFlow(new DataRequest[]{
                        new DataRequest(CopierModuleFactory.INSTANCE_URN,
                                "doesnt matter"),
                        new DataRequest(instanceURN, requestParm)
                });

            }
        };
        //Verify that the module instance is not leaked.
        List<ModuleURN> instances = mManager.getModuleInstances(PROVIDER_URN);
        assertTrue(instances.toString(), instances.isEmpty());
    }

    /**
     * Verifies a simple data flow.
     *
     * @throws Exception if there were errors.
     */
    @Test
    public void simpleFlowAndJMX() throws Exception {
        ModuleURN instanceURN = new ModuleURN(PROVIDER_URN, "mymodule");
        //Set up the data
        Object []requestParm = {BigDecimal.ONE, 2, "three"};
        CopierModule.SynchronousRequest req =
                new CopierModule.SynchronousRequest(requestParm);
        req.semaphore.acquire();
        //Set up the sink listener
        BlockingSinkDataListener listener = new BlockingSinkDataListener();
        mManager.addSinkListener(listener);
        //Create the data flow.
        DataFlowID flowID = mManager.createDataFlow(new DataRequest[]{
                new DataRequest(CopierModuleFactory.INSTANCE_URN,
                        req),
                new DataRequest(instanceURN, null)
        });
        //wait for the data to be emitted.
        req.semaphore.acquire();
        //wait for the data to be received
        for(Object o: requestParm) {
            assertEquals(o, listener.getNextData());
        }
        //verify the flow info
        DataFlowInfo flowInfo = mManager.getDataFlowInfo(flowID);
        assertFlowInfo(flowInfo, flowID, 3, true, false, null, null);
        //verify the last 2 flow steps
        assertFlowStep(flowInfo.getFlowSteps()[1], instanceURN, true, 3, 0,
                null, true, 3, 0, null, instanceURN, null);
        assertFlowStep(flowInfo.getFlowSteps()[2],
                SinkModuleFactory.INSTANCE_URN, false, 0, 0, null, true, 3, 0,
                null, SinkModuleFactory.INSTANCE_URN, null);
        //verify the module info to double check that it's auto-created.
        assertModuleInfo(mManager, instanceURN, ModuleState.STARTED, null,
                new DataFlowID[]{flowID}, true, true, true, true, false);
        //verify JMX MXBean Info
        final ObjectName on = instanceURN.toObjectName();
        final MBeanServer beanServer = getMBeanServer();
        assertTrue(beanServer.isRegistered(on));
        MBeanInfo beanInfo = beanServer.getMBeanInfo(on);
        assertEquals(SimpleAsyncProcessor.class.getName(), beanInfo.getClassName());
        assertEquals(Messages.JMX_MXBEAN_DESCRIPTION.getText(), beanInfo.getDescription());
        assertEquals(0, beanInfo.getOperations().length);
        assertEquals(0, beanInfo.getConstructors().length);
        assertEquals(0, beanInfo.getNotifications().length);
        assertEquals(0, beanInfo.getDescriptor().getFieldNames().length);
        MBeanAttributeInfo[] attributeInfos = beanInfo.getAttributes();
        assertEquals(1, attributeInfos.length);
        final String validAttribute = SimpleAsyncProcessor.ATTRIB_PREFIX + flowID;
        assertEquals(validAttribute, attributeInfos[0].getName());
        assertEquals(Integer.class.getName(), attributeInfos[0].getType());
        assertEquals(Messages.JMX_ATTRIBUTE_FLOW_CNT_DESCRIPTION.getText(flowID), attributeInfos[0].getDescription());
        assertEquals(0, attributeInfos[0].getDescriptor().getFieldNames().length);
        assertFalse(attributeInfos[0].isIs());
        assertFalse(attributeInfos[0].isWritable());
        assertTrue(attributeInfos[0].isReadable());
        
        //verify Attributes
        Object value = beanServer.getAttribute(on, SimpleAsyncProcessor.ATTRIB_PREFIX + flowID);
        assertEquals((Integer)0, (Integer)value);
        final String invalidAttribute = SimpleAsyncProcessor.ATTRIB_PREFIX + 1;
        new ExpectedFailure<AttributeNotFoundException>(invalidAttribute){
            @Override
            protected void run() throws Exception {
                beanServer.getAttribute(on, invalidAttribute);
            }
        };
        new ExpectedFailure<AttributeNotFoundException>("blah"){
            @Override
            protected void run() throws Exception {
                beanServer.getAttribute(on, "blah");
            }
        };
        AttributeList attribList = beanServer.getAttributes(on,
                new String[]{validAttribute, invalidAttribute});
        assertEquals(1, attribList.size());
        assertEquals(new Attribute(validAttribute, 0), attribList.get(0));
        new ExpectedFailure<AttributeNotFoundException>(){
            @Override
            protected void run() throws Exception {
                beanServer.setAttribute(on, new Attribute(validAttribute, 34));
            }
        };
        new ExpectedFailure<AttributeNotFoundException>(){
            @Override
            protected void run() throws Exception {
                beanServer.setAttribute(on, new Attribute(invalidAttribute, 34));
            }
        };
        AttributeList list = new AttributeList(Arrays.asList(
                new Attribute(validAttribute, 12),
                new Attribute(invalidAttribute, 13)
                ));
        assertEquals(0, beanServer.setAttributes(on, list).size());
        //verify no operations can be performed
        ReflectionException excpt = new ExpectedFailure<ReflectionException>() {
            @Override
            protected void run() throws Exception {
                beanServer.invoke(on, "getQueueSizes", null, null);
            }
        }.getException();
        assertTrue(excpt.toString(), excpt.getCause() instanceof NoSuchMethodException);

        //stop the flow
        mManager.cancel(flowID);
        //verify that the module is deleted.
        List<ModuleURN> instances = mManager.getModuleInstances(PROVIDER_URN);
        assertTrue(instances.toString(), instances.isEmpty());
        //remove the listener
        mManager.removeSinkListener(listener);
    }

    /**
     * Verifies that the async module, when participating in multiple data flows,
     * only emits data into respective data flows.
     *
     * @throws Exception if there were errors.
     */
    @Test
    public void noEmitFromOtherFlows() throws Exception {
        ModuleURN instanceURN = new ModuleURN(PROVIDER_URN, "mymodule");
        FlowSpecificListener listener = new FlowSpecificListener();
        mManager.addSinkListener(listener);
        //Set up a data flow with this module as an emitter.
        //No data should be received from within this data flow
        DataFlowID emitOnlyflowID = mManager.createDataFlow(new DataRequest[]{
                new DataRequest(instanceURN)
        });
        //Setup two other data flows
        Object[] reqParms1 = {"firstOne", BigDecimal.TEN, "uno"};
        Object[] reqParms2 = {"secondOne", BigDecimal.ZERO, "dos"};
        DataFlowID flowID1 = mManager.createDataFlow(new DataRequest[]{
                new DataRequest(CopierModuleFactory.INSTANCE_URN, reqParms1),
                new DataRequest(instanceURN)
        });
        DataFlowID flowID2 = mManager.createDataFlow(new DataRequest[]{
                new DataRequest(CopierModuleFactory.INSTANCE_URN, reqParms2),
                new DataRequest(instanceURN)
        });
        //Wait for data from each of the flows
        //flowID2
        while(!listener.getFlows().contains(flowID2)) {
            Thread.sleep(100);
        }
        for(Object o: reqParms2) {
            assertEquals(o, listener.getNextDataFor(flowID2));
        }
        //flowID1
        while(!listener.getFlows().contains(flowID1)) {
            Thread.sleep(100);
        }
        for(Object o: reqParms1) {
            assertEquals(o, listener.getNextDataFor(flowID1));
        }
        //No data should be received for the very first data flow
        assertThat(listener.getFlows(), Matchers.not(Matchers.hasItem(emitOnlyflowID)));
        //verify queue sizes for all of them via JMX
        assertEquals(0, getMBeanServer().getAttribute(instanceURN.toObjectName(), SimpleAsyncProcessor.ATTRIB_PREFIX + emitOnlyflowID));
        assertEquals(0, getMBeanServer().getAttribute(instanceURN.toObjectName(), SimpleAsyncProcessor.ATTRIB_PREFIX + flowID1));
        assertEquals(0, getMBeanServer().getAttribute(instanceURN.toObjectName(), SimpleAsyncProcessor.ATTRIB_PREFIX + flowID2));
        //verify that the data for each flow is delivered in a unique thread.
        assertEquals(null, listener.getThreadNameFor(emitOnlyflowID));
        assertThat(listener.getThreadNameFor(flowID1),
                Matchers.startsWith(SimpleAsyncProcessor.ASYNC_THREAD_NAME_PREFIX + "-" + instanceURN.instanceName()));
        assertThat(listener.getThreadNameFor(flowID2), 
                Matchers.startsWith(SimpleAsyncProcessor.ASYNC_THREAD_NAME_PREFIX + "-" + instanceURN.instanceName()));
        assertThat(listener.getThreadNameFor(flowID1), Matchers.not(
                Matchers.equalTo(listener.getThreadNameFor(flowID2))));
        assertThat(listener.getThreadNameFor(flowID1), Matchers.not(
                Matchers.equalTo(Thread.currentThread().getName())));
        //Cancel all the data flows
        mManager.cancel(emitOnlyflowID);
        mManager.cancel(flowID1);
        //verify that we only have a single flow attribute left
        List<String> list = getAttributes(instanceURN);
        assertEquals(1, list.size());
        assertThat(list, Matchers.hasItem(SimpleAsyncProcessor.ATTRIB_PREFIX + flowID2));
        mManager.cancel(flowID2);
        //verify that the module is deleted.
        List<ModuleURN> instances = mManager.getModuleInstances(PROVIDER_URN);
        assertTrue(instances.toString(), instances.isEmpty());
        mManager.removeSinkListener(listener);
    }

    /**
     * Verifies that a really slow consumer of events does not slow down
     * the emitter of events when the event is delivered via the async module.
     *
     * @throws Exception if there were exceptions
     */
    @Test(timeout = 10000)
    public void slowConsumer() throws Exception {
        ModuleURN instanceURN = new ModuleURN(PROVIDER_URN, "mymodule");
        Object [] data = {"item1", "item2", "item3", "item4"};
        DataFlowID flowID = mManager.createDataFlow(new DataRequest[]{
                new DataRequest(CopierModuleFactory.INSTANCE_URN, data),
                new DataRequest(instanceURN),
                new DataRequest(BlockingModuleFactory.INSTANCE_URN)
        });
        //wait until copier is done emitting all the data
        DataFlowInfo flowInfo;
        do {
            Thread.sleep(100);
            flowInfo = mManager.getDataFlowInfo(flowID);
        } while(flowInfo.getFlowSteps()[0].getNumEmitted() < 4);
        
        for(int i = 0; i < data.length; i++) {
            //wait for the data to get delivered
            BlockingModuleFactory.getLastInstance().getSemaphore().acquire();
            //verify the flow info for the last step
            flowInfo = mManager.getDataFlowInfo(flowID);
            assertFlowStep(flowInfo.getFlowSteps()[2], BlockingModuleFactory.INSTANCE_URN,
                    false, 0, 0, null, true, i + 1, 0, null, BlockingModuleFactory.INSTANCE_URN, null);
            //verify the jmx flow queue size attribute value
            if(i < data.length - 1) {
                assertEquals(data.length - 1 - i,
                        getMBeanServer().getAttribute(instanceURN.toObjectName(),
                                SimpleAsyncProcessor.ATTRIB_PREFIX + flowID));
            }
            //consume the data
            assertEquals(data[i], BlockingModuleFactory.getLastInstance().getNextData());
        }
        //verify that the queue size is now zero.
        assertEquals(0, getMBeanServer().getAttribute(
                instanceURN.toObjectName(),
                SimpleAsyncProcessor.ATTRIB_PREFIX + flowID));
        //cancel data flow
        mManager.cancel(flowID);
    }

    /**
     * Returns the list of attributes exposed by the mbean having
     * the supplied URN.
     *
     * @param inURN the module URN.
     *
     * @return the list of advertised attributes.
     *
     * @throws Exception if there were exceptions.
     */
    private List<String> getAttributes(ModuleURN inURN) throws Exception {
        MBeanInfo beanInfo = getMBeanServer().getMBeanInfo(inURN.toObjectName());
        List<String> attribs = new ArrayList<String>();
        for(MBeanAttributeInfo info: beanInfo.getAttributes()) {
            attribs.add(info.getName());
        }
        return attribs;
    }

    /**
     * A sinkd data listener that saves data received for each data flow
     * into a separate queue. For each data flow, each object delivered and
     * the number of objects available can be queried.
     * The class also records the thread that was used to deliver the first
     * data item for each data flow.
     */
    private static class FlowSpecificListener implements SinkDataListener {

        @Override
        public void receivedData(DataFlowID inFlowID, Object inData) {
            BlockingQueue<Object> queue = mFlowData.get(inFlowID);
            if(queue == null) {
                synchronized(this) {
                    queue = mFlowData.get(inFlowID);
                    if(queue == null) {
                        queue = new LinkedBlockingQueue<Object>();
                        mFlowData.put(inFlowID, queue);
                        mThreadNames.put(inFlowID, Thread.currentThread().getName());
                    }
                }
            }
            queue.add(inData);
        }

        /**
         * Returns the next data delivered to the listener for the supplied
         * data flow ID.
         *
         * @param inFlowID the data flow ID.
         *
         * @return the next data item.
         *
         * @throws InterruptedException if the wait for receiving the next
         * data item was interrupted.
         */
        public Object getNextDataFor(DataFlowID inFlowID) throws InterruptedException {
            BlockingQueue<Object> queue = mFlowData.get(inFlowID);
            return queue == null
                    ? null
                    : queue.take();
        }

        /**
         * Returns the number of data items that have been received but not
         * yet retrieved via {@link #getNextDataFor(DataFlowID)}.
         * If no items for the specified data flowID have been received, -1
         * value is returned.
         *
         * @param inFlowID the flowID.
         *
         * @return number of data items received but not yet retrieved
         * for the flow with the specified flowID.
         */
        public int sizeFor(DataFlowID inFlowID) {
            BlockingQueue<Object> queue = mFlowData.get(inFlowID);
            return queue == null
                    ? -1
                    : queue.size();

        }

        /**
         * The name of the thread that delivered the first data iterm for
         * the specified flowID.
         *
         * @param inFlowID the flowID.
         *
         * @return the name of the thread that delivered the first data item
         * for the flow with the specified flowID.
         */
        public String getThreadNameFor(DataFlowID inFlowID) {
            synchronized (this) {
                return mThreadNames.get(inFlowID);
            }
        }

        /**
         * The set of data flows that have delivered data to this listener.
         *
         * @return the set of IDs of data flows that have delivered data to
         * this listener.
         */
        public Set<DataFlowID> getFlows() {
            return mFlowData.keySet();
        }
        private final Map<DataFlowID, BlockingQueue<Object>> mFlowData =
                new ConcurrentHashMap<DataFlowID, BlockingQueue<Object>>();
        private final Map<DataFlowID, String> mThreadNames =
                new HashMap<DataFlowID, String>();
    }
    @Before
    public void setup() throws Exception {
        mManager = new ModuleManager();
        mManager.init();
    }

    @After
    public void cleanup() throws Exception {
        mManager.stop();
        mManager = null;
    }

    private ModuleManager mManager;
}