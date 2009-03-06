package org.marketcetera.module;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.test.CollectionAssert;
import org.marketcetera.core.Pair;
import org.marketcetera.core.LoggerConfiguration;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

import javax.management.*;
import javax.management.openmbean.SimpleType;
import java.util.List;
import java.util.HashSet;
import java.util.Arrays;
import java.util.LinkedList;
import java.lang.management.ManagementFactory;

/* $License$ */
/**
 * A base class with colletion of all utility methods for testing modules
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$")
public class ModuleTestBase {

    /**
     * Setup logging for unit tests.
     */
    @BeforeClass
    public static void logSetup() {
        LoggerConfiguration.logSetup();
    }
    /**
     * Verifies all the providers that are expected to be available
     * in the unit testing environment.
     * 
     * @param inProviders the actual set of provider URNs found.
     */
    protected static void checkAllProviders(List<ModuleURN> inProviders) {
        assertEquals(12, inProviders.size());
        CollectionAssert.assertArrayPermutation(new ModuleURN[]{
                SinkModuleFactory.PROVIDER_URN,
                SingleModuleFactory.PROVIDER_URN,
                MultipleModuleFactory.PROVIDER_URN,
                ComplexModuleFactory.PROVIDER_URN,
                JMXTestModuleFactory.PROVIDER_URN,
                ConfigurationProviderTestFactory.PROVIDER_URN,
                EmitterModuleFactory.PROVIDER_URN,
                ProcessorModuleFactory.PROVIDER_URN,
                FlowRequesterModuleFactory.PROVIDER_URN,
                SingleParmModuleFactory.PROVIDER_URN,
                CopierModuleFactory.PROVIDER_URN,
                ConcurrentTestFactory.PROVIDER_URN
        }, inProviders.toArray(new ModuleURN[inProviders.size()]));
    }

    /**
     * Verifies the ProviderInfo field values.
     *
     * @param inInfo the provider info to verify.
     * @param inURN the expected provider URN.
     * @param parameterTypeNames the expected parameter type names.
     * @param parameterTypes the expected parameter types
     * @param description the expected provider description
     * @param autoInstantiate if the provider supports
     * auto-instantiated modules.
     * @param multipleInstances if the provider supports multiple
     * instances of the modules
     * @throws ClassNotFoundException if there was an error.
     */
    protected static void assertProviderInfo(ProviderInfo inInfo,
                                   ModuleURN inURN,
                                   String[] parameterTypeNames,
                                   Class<?>[] parameterTypes,
                                   String description,
                                   boolean autoInstantiate,
                                   boolean multipleInstances)
            throws ClassNotFoundException {
        assertEquals(inURN, inInfo.getURN());
        List<String> list = inInfo.getParameterTypeNames();
        CollectionAssert.assertArrayPermutation(parameterTypeNames,
                list.toArray(new String[list.size()]));
        assertArrayEquals(parameterTypes, inInfo.parameterTypes());
        assertEquals(description, inInfo.getDescription());
        assertEquals(autoInstantiate,  inInfo.isAutoInstantiate());
        assertEquals(multipleInstances, inInfo.isMultipleInstances());
    }

    /**
     * Verifies the module info fields.
     *
     * @param inInfo the module info to verify.
     * @param inURN the expected module URN
     * @param inState the expected module state
     * @param inInitDataFlows the set of data flows initiated
     * @param inParticipateDataFlows the set of data flows participating in
     * @param inAutocreated if the module is autocreated.
     * @param inAutostart if the module is autostarted
     * @param inReceiver if the module is a receiver
     * @param inEmitter if the module is an emitter
     * @param inFlowRequester if the module is a flow requester
     * @return the supplied module info
     */
    protected static ModuleInfo assertModuleInfo(ModuleInfo inInfo,
                                       ModuleURN inURN,
                                       ModuleState inState,
                                       DataFlowID[] inInitDataFlows,
                                       DataFlowID[] inParticipateDataFlows,
                                       boolean inAutocreated,
                                       boolean inAutostart,
                                       boolean inReceiver,
                                       boolean inEmitter,
                                       boolean inFlowRequester) {
        assertEquals(inURN, inInfo.getURN());
        assertEquals(inState, inInfo.getState());
        CollectionAssert.assertArrayPermutation(inInitDataFlows,
                inInfo.getInitiatedDataFlows());
        CollectionAssert.assertArrayPermutation(inParticipateDataFlows,
                inInfo.getParticipatingDataFlows());
        assertEquals(inAutocreated, inInfo.isAutocreated());
        assertEquals(inAutostart, inInfo.isAutostart());
        assertEquals(inReceiver, inInfo.isReceiver());
        assertEquals(inEmitter, inInfo.isEmitter());
        assertEquals(inFlowRequester, inInfo.isFlowRequester());
        assertNotNull(inInfo.getCreated());
        if(inInfo.getState().isStarted()) {
            //verify started time stamp
            assertNotNull(inInfo.getStarted());
        }
        if(ModuleState.STOPPED == inInfo.getState()) {
            //verify stopped time stamp
            assertNotNull(inInfo.getStopped());
        }
        return inInfo;
    }

    /**
     * Verify the state of ModuleBase instance.
     *
     * @param inURN the module instance URN
     * @param inStartInvoked if module start has been invoked
     * @param inStopInvoked if the module stop has been invoked
     * @param inAutoStart if the module is auto-start
     * @param inAutoCreated if the module is auto-created.
     * @return the module instance
     */
    protected static ModuleBase assertModuleBase(ModuleURN inURN,
                                             boolean inStartInvoked,
                                             boolean inStopInvoked,
                                             boolean inAutoStart,
                                             boolean inAutoCreated) {
        ModuleBase module = ModuleBase.getInstance(inURN);
        assertModuleBase(module, inURN, inStartInvoked, inStopInvoked,
                inAutoStart, inAutoCreated);
        return module;
    }

    /**
     * Verify the state of a ModuleBase instance.
     *
     * @param inModule the module base instance.
     * @param inURN the module instance URN.
     * @param inStartInvoked if the module start has been invoked
     * @param inStopInvoked if the module stop has been invoked
     * @param inAutoStart if the module is auto-start
     * @param inAutoCreated if the module is auto-created.
     */
    protected static void assertModuleBase(ModuleBase inModule,
                                           ModuleURN inURN,
                                           boolean inStartInvoked,
                                           boolean inStopInvoked,
                                           boolean inAutoStart,
                                           boolean inAutoCreated) {
        assertNotNull(inModule);
        assertEquals(inURN, inModule.getURN());
        assertEquals(inStartInvoked, inModule.isStartInvoked());
        assertEquals(inStopInvoked, inModule.isStopInvoked());
        assertEquals(inAutoStart, inModule.isAutoStart());
        assertEquals(inAutoCreated, inModule.isAutoCreated());
    }

    /**
     * Verify the provider info, after querying it from the module
     * manager.
     *
     * @param inManager the module manager
     * @param inURN the provider URN.
     * @param parameterTypeNames the provider parameter type names
     * @param parameterTypes the provider parameter types
     * @param description the provider description
     * @param autoInstantiate if the provider supports auto-instantiated instances
     * @param multipleInstances if the provider supports multiple instances
     *
     * @throws Exception if there was an unexpected failure
     */
    protected static void assertProviderInfo(ModuleManager inManager,
                                           ModuleURN inURN,
                                           String[] parameterTypeNames,
                                           Class<?>[] parameterTypes,
                                           String description,
                                           boolean autoInstantiate,
                                           boolean multipleInstances)
            throws Exception {
        ProviderInfo info = inManager.getProviderInfo(inURN);
        assertProviderInfo(info, inURN, parameterTypeNames, parameterTypes,
                description, autoInstantiate, multipleInstances);

    }

    /**
     * Verifies the module info queried from the module manager
     *
     * @param inManager the module manager instance
     * @param inURN the module URN
     * @param inState the module state
     * @param inInitDataFlows the data flows initiated by the module
     * @param inParticipateDataFlows the data flows that module is
     * participating in
     * @param inAutocreated if the module is auto-created
     * @param inAutostart if the module is auto-started
     * @param inReceiver if the module is a receiver
     * @param inEmitter if the module is an emitter
     * @param inFlowRequester if the module is a data flow creator
     *
     * @return the module info for the module
     *
     * @throws Exception if there was an unexpected error
     */
    protected static ModuleInfo assertModuleInfo(ModuleManager inManager,
                                               ModuleURN inURN,
                                               ModuleState inState,
                                               DataFlowID[] inInitDataFlows,
                                               DataFlowID[] inParticipateDataFlows,
                                               boolean inAutocreated,
                                               boolean inAutostart,
                                               boolean inReceiver,
                                               boolean inEmitter,
                                               boolean inFlowRequester)
            throws Exception {
        ModuleInfo info = inManager.getModuleInfo(inURN);
        return assertModuleInfo(info, inURN, inState,
                inInitDataFlows, inParticipateDataFlows,
                inAutocreated, inAutostart, inReceiver,
                inEmitter, inFlowRequester);
    }

    /**
     * Verify if all the URNs specified in the <code>inContents</code>
     * are present in the <code>inContainer</code> as well.
     *
     * @param inContainer if the container of URNs
     * @param inContents the set of URNs that should be present
     * in the container
     */
    protected static void assertContains(List<ModuleURN> inContainer,
                                         ModuleURN [] inContents) {
        HashSet<ModuleURN> container =
                new HashSet<ModuleURN>(inContainer);
        List<ModuleURN> urnList = Arrays.asList(inContents);
        HashSet<ModuleURN> contents = new HashSet<ModuleURN>(urnList);
        contents.removeAll(container);
        assertTrue(contents.toString(), container.containsAll(urnList));
    }

    /**
     * Verifies the contents of the supplied data flow info.
     *
     * @param inInfo the data flow info
     * @param inFlowID the expected data flow ID
     * @param inNumSteps the expected number of data flow steps
     * @param hasCreated if the data flow has a created time stamp
     * @param hasStopped if the data flow has a stopped time stamp
     * @param inRequesterURN the data flow requester URN
     * @param inStopperURN the data flow stopper URN
     */
    protected static void assertFlowInfo(DataFlowInfo inInfo,
                                       DataFlowID inFlowID,
                                       int inNumSteps,
                                       boolean hasCreated,
                                       boolean hasStopped,
                                       ModuleURN inRequesterURN,
                                       ModuleURN inStopperURN) {
        assertEquals(inFlowID, inInfo.getFlowID());
        assertEquals(inNumSteps, inInfo.getFlowSteps().length);
        assertEquals(hasCreated, inInfo.getCreated() != null);
        assertEquals(hasStopped, inInfo.getStopped() != null);
        assertEquals(inRequesterURN, inInfo.getRequesterURN());
        assertEquals(inStopperURN, inInfo.getStopperURN());
    }

    /**
     * Verifies contents of a data flow step
     *
     * @param inStep the data flow step
     * @param inURN the URN of the module in this step
     * @param inEmitter if the module is an emitter
     * @param inNumEmitted number of data instances emitted
     * @param inNumEmitErrors numbers of emit errors encountered
     * @param inLastEmitError the last emit error encountered
     * @param inReceiver if the module is a receiver
     * @param inNumReceived number of data instances received
     * @param inNumReceiveErrors number of receive errors encountered
     * @param inLastReceiveError the last receive error encountered
     * @param inRequestURN the module URN specified in the data request
     * @param inRequestParam the request parameter specified in the
     * data request
     */
    protected static void assertFlowStep(DataFlowStep inStep,
                                       ModuleURN inURN,
                                       boolean inEmitter,
                                       int inNumEmitted,
                                       int inNumEmitErrors,
                                       String inLastEmitError,
                                       boolean inReceiver,
                                       int inNumReceived,
                                       int inNumReceiveErrors,
                                       String inLastReceiveError,
                                       ModuleURN inRequestURN,
                                       Object inRequestParam) {
        if (inURN != null) {
            assertEquals(inURN, inStep.getModuleURN());
        }
        assertEquals(inEmitter,inStep.isEmitter());
        assertEquals(inNumEmitted,inStep.getNumEmitted());
        assertEquals(inNumEmitErrors,inStep.getNumEmitErrors());
        if (inLastEmitError != null) {
            assertNotNull(inStep.getLastEmitError());
            assertTrue(inStep.getLastEmitError().startsWith(inLastEmitError));
        } else {
            assertNull(inStep.getLastEmitError());
        }
        assertEquals(inReceiver,inStep.isReceiver());
        assertEquals(inNumReceived,inStep.getNumReceived());
        assertEquals(inNumReceiveErrors,inStep.getNumReceiveErrors());
        if (inLastReceiveError != null) {
            assertNotNull(inStep.getLastReceiveError());
            assertTrue(inStep.getLastReceiveError(),
                    inStep.getLastReceiveError().startsWith(inLastReceiveError));
        } else {
            assertNull(inStep.getLastReceiveError(),inStep.getLastReceiveError());
        }
        if (inRequestURN != null) {
            assertEquals(inRequestURN,inStep.getRequest().getRequestURN());
        }
        assertEquals(inRequestParam,inStep.getRequest().getData());
        //Only sync data coupling is supported for now.
        assertEquals(DataCoupling.SYNC, inStep.getRequest().getCoupling());
    }

    /**
     * Gets the MBean server to use for all the tests.
     *
     * @return the mbean server to use for all tests.
     */
    protected static MBeanServer getMBeanServer() {
        return ManagementFactory.getPlatformMBeanServer();
    }

    /**
     * Verifies that the bean, attribute, operation and parameter info
     * all have the descriptor name, {@link org.marketcetera.module.DisplayName} specified.
     *
     * Also verifies that all the parameter types are simple types.
     *
     * @param inInfo the bean info to verify.
     */
    public static void verifyBeanInfo(MBeanInfo inInfo) {
        //verify that every info has the display name descriptor
        assertDescriptor(inInfo.getDescriptor(), false);
        for (MBeanAttributeInfo attrib : inInfo.getAttributes()) {
            assertDescriptor(attrib.getDescriptor(), false);
        }
        for (MBeanOperationInfo opInfo : inInfo.getOperations()) {
            assertDescriptor(opInfo.getDescriptor(), false);
            for (MBeanParameterInfo parameterInfo : opInfo.getSignature()) {
                assertDescriptor(parameterInfo.getDescriptor(), true);
            }
        }
    }

    /**
     * Verifies the descriptor value.
     *
     * @param inDescriptor the descriptor.
     * @param inSimpleType if the descriptor should be validated to describe
     * a simple type.
     */
    public static void assertDescriptor(Descriptor inDescriptor, boolean inSimpleType) {
        if (inSimpleType) {
            Object value = inDescriptor.getFieldValue("openType");
            assertNotNull(value);
            assertTrue(value.getClass().toString(), value instanceof SimpleType);
        }
        assertNotNull(inDescriptor.getFieldValue("name"));
    }

    /**
     * A class that contains data collected by {@link Sink}
     */
    protected static class FlowData extends Pair<DataFlowID,Object> {

        /**
         * Creates an instance.
         *
         * @param inFlowID the data flow ID
         * @param inObject the data object received
         */
        FlowData(DataFlowID inFlowID, Object inObject) {
            super(inFlowID, inObject);
        }
    }

    /**
     * A sink data listener to help with testing.
     * <p>
     * This listener has a special behavior for boolean false values
     * received. Whenever it receives a false value, it notifies
     * all the threads that are waiting in {@link #waitUntilTerminator()}
     * method. The boolean false value is never added to the set of
     * data received.
     * <p>
     * The waiting threads are notified only once. Any boolean
     * false values received after the first one are ignored
     * until {@link #clear()} is invoked.
     */
    protected static class Sink implements SinkDataListener {

        /**
         * Adds the received data to the set of objects received.
         *
         * if a boolean false value is received the received
         * terminator flag is cleared and any threads waiting
         * in {@link #waitUntilTerminator()} are notified.
         *
         * @param inFlowID the data flow ID.
         * @param inData the data.
         */
        public synchronized void receivedData(DataFlowID inFlowID,
                                              Object inData) {
            if (inData instanceof Boolean && !(Boolean) inData) {
                // a boolean false value is a terminator
                mReceivedTerminator = true;
                notifyAll();
            } else {
                mData.add(new FlowData(inFlowID, inData));
            }
            if(mThrowException) {
                throw new IllegalArgumentException();
            }
        }

        /**
         * Gets the list of data received by this listener in
         * the order it was received.
         *
         * @return the flow data received until now.
         */
        public synchronized FlowData[] getData() {
            return mData.toArray(new FlowData[mData.size()]);
        }

        /**
         * Clears all the data in the sink and the
         * terminator received flag. any invocation
         * of {@link #waitUntilTerminator()} will block
         * until a boolean false value is received by the sink.
         */
        public synchronized void clear() {
            mData.clear();
            mReceivedTerminator = false;
        }

        /**
         * The {@link #receivedData(DataFlowID, Object)} throws an exception
         * if this attribute is set to true.
         *
         * @param inThrowException if an exception should be thrown
         * when receiving data.
         */
        public void setThrowException(boolean inThrowException) {
            mThrowException = inThrowException;
        }

        /**
         * Waits until we receive the boolean: false data flow
         * terminator.
         *
         * @throws InterruptedException if the wait was interrupted
         */
        public synchronized void waitUntilTerminator()
                throws InterruptedException {
            while(!mReceivedTerminator) {
                wait();
            }
        }
        private boolean mReceivedTerminator = false;
        private LinkedList<FlowData> mData =
                new LinkedList<FlowData>();
        private boolean mThrowException;
    }
}
