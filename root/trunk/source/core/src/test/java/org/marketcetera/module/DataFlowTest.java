package org.marketcetera.module;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.After;
import static org.junit.Assert.*;

import java.util.*;
import java.util.concurrent.Future;

/* $License$ */
/**
 * Tests data flows
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$")
public class DataFlowTest extends ModuleTestBase {
    @BeforeClass
    public static void setup() throws Exception {
        try {
            sManager = new ModuleManager();
            sManager.init();
            sManager.addSinkListener(sSink);
        } catch (Exception e) {
            SLF4JLoggerProxy.error(e,e);
            throw e;
        }
    }
    @AfterClass
    public static void cleanup() throws Exception {
        sManager.stop();
    }
    @After
    public void clearUp() throws Exception {
        //clear the sink
        sSink.clear();
        //clear the emitter
        ((EmitterModule) ModuleBase.getInstance(
                EmitterModuleFactory.INSTANCE_URN)).clear();

        //prune all history records
        sManager.setMaxFlowHistory(0);
        sManager.setMaxFlowHistory(ModuleManager.DEFAULT_MAX_FLOW_HISTORY);
        assertEquals(ModuleManager.DEFAULT_MAX_FLOW_HISTORY,
                sManager.getMaxFlowHistory());
    }

    /**
     * Verify failures when fetching data flow info specifying
     * an invalid ID.
     *
     * @throws Exception if there were unexpected failures during
     * testing
     */
    @Test
    public void getDataFlowInvalidID() throws Exception {
        final DataFlowID id = new DataFlowID("blah");
        new ExpectedFailure<DataFlowNotFoundException>(
                Messages.DATA_FLOW_NOT_FOUND, id.getValue()){
            protected void run() throws Exception {
                sManager.getDataFlowInfo(id);
            }
        };
    }

    /**
     * Verify failures when canceling a data flow specifying
     * an invalid ID.
     *
     * @throws Exception if there were unexpected failures during
     * testing
     */
    @Test
    public void cancelDataFlowInvalidID() throws Exception {
        final DataFlowID flowID = new DataFlowID("blah");
         new ExpectedFailure<DataFlowNotFoundException>(
                 Messages.DATA_FLOW_NOT_FOUND,flowID.getValue()){
             protected void run() throws Exception {
                 sManager.cancel(flowID);
             }
         };
    }

    /**
     * Tests creation & cancellation of simple data flows through the module
     * manager API.
     *
     * @throws Exception if there's an unexpected error.
     */
    @Test
    public void createFlowManager() throws Exception {
        //verify that there are no data flows
        assertTrue(sManager.getDataFlows(true).isEmpty());

        //Start emitter module, if not already started
        startEmitter();
        ModuleURN procURN = new ModuleURN(ProcessorModuleFactory.PROVIDER_URN,
                "proc");
        //verify that this module does not exist, so that we can verify
        //it gets auto-instantiated.
        List<ModuleURN> urns = sManager.getModuleInstances(
                ProcessorModuleFactory.PROVIDER_URN);
        assertFalse(urns.toString(), urns.contains(procURN));
        //data flow with sink module auto-appended
        checkDataFlowManager(true, false, true, new DataRequest(procURN,
                String.class.getName()));
        //data flow with sink explicitly requested to be auto-appended
        checkDataFlowManager(false, true, true, new DataRequest(procURN,
                String.class.getName()));
        //data flow with sink explicitly added
        checkDataFlowManager(false, true, true, 
                new DataRequest(procURN, String.class.getName()),
                new DataRequest(SinkModuleFactory.INSTANCE_URN));
        //manually create the processor module as the auto-created one
        //gets deleted as soon as the data flow is over.
        sManager.createModule(procURN.parent(), procURN);
        //test module search functionality when setting up data flows
        checkDataFlowManager(false, true, false,
                new DataRequest(new ModuleURN("metc:::proc"),
                        String.class.getName()),
                new DataRequest(SinkModuleFactory.INSTANCE_URN.parent()));
        checkDataFlowManager(false, true, false,
                new DataRequest(new ModuleURN("metc:::proc"),
                        String.class.getName()),
                new DataRequest(SinkModuleFactory.INSTANCE_URN.parent().parent()));
    }

    /**
     * Verifies that attempts to emit data fail when
     * the data flow has ended.
     *
     * @throws Exception if there are unexpected errors
     */
    @Test(timeout = 10000)
    public void emitFailStoppedFlow() throws Exception {
        assertTrue(sManager.getDataFlowHistory().isEmpty());
        startEmitter();
        DataFlowID flowID = sManager.createDataFlow(new DataRequest[]{
                new DataRequest(EmitterModuleFactory.INSTANCE_URN,"send this data")
        });
        //Wait until sink receives this data
        while(sSink.getData().length < 1) {
            Thread.sleep(500);
        }
        sManager.cancel(flowID);
        EmitterModule module = (EmitterModule) ModuleBase.getInstance(
                EmitterModuleFactory.INSTANCE_URN);
        DataEmitterSupport support = module.getLastTask().getSupport();
        assertNotNull(support);
        int numData = sSink.getData().length;
        support.send(new Object());
        //verify that the sink doesn't get it.
        assertEquals(numData, sSink.getData().length);
        //verify nothing happens if you try to emit error, its silently ignored
        support.dataEmitError(TestMessages.BAD_DATA, true);
        module.clear();
    }

    /**
     * Tests creation & cancellation of simple data flows through the
     * data flow support interface.
     *
     * @throws Exception if there's an unexpected error.
     */
    @Test
    public void createFlowModule() throws Exception {
        //verify that there are no data flows
        assertTrue(sManager.getDataFlows(true).isEmpty());

        //Start emitter module, if not already started
        startEmitter();

        //create the flow requester module.
        //Have the instance name similar to that of emitter so that we can
        //test expansion of 'this' in the URN
        ModuleURN procURN = new ModuleURN(FlowRequesterModuleFactory.PROVIDER_URN,
                EmitterModuleFactory.INSTANCE_URN.instanceName());
        sManager.createModule(FlowRequesterModuleFactory.PROVIDER_URN, procURN);
        ModuleTestBase.assertModuleInfo(sManager.getModuleInfo(procURN),
                procURN, ModuleState.CREATED, null, null, false, false,
                true, true, true);
        final FlowRequesterModule module = (FlowRequesterModule) ModuleBase.getInstance(procURN);
        assertNotNull(module);

        //verify data flows
        //data flow with sink module auto-appended
        checkDataFlowModule(module, null, true, false, new DataRequest(procURN,
                String.class.getName()));
        //data flow with sink explicitly requested to be auto-appended
        checkDataFlowModule(module, null, false, true, new DataRequest(procURN,
                String.class.getName()));
        //data flow with sink explicitly added
        checkDataFlowModule(module, null, false, true,
                new DataRequest(procURN, String.class.getName()),
                new DataRequest(SinkModuleFactory.INSTANCE_URN));
        //verify expansion of 'this' keyword
        checkDataFlowModule(module, null, false, true,
                new DataRequest(new ModuleURN("metc:this:this:this"),
                        String.class.getName()),
                new DataRequest(SinkModuleFactory.INSTANCE_URN.parent()));
        //verify module search works
        checkDataFlowModule(module, null, false, true,
                new DataRequest(new ModuleURN("metc:flow::default"),
                        String.class.getName()),
                new DataRequest(SinkModuleFactory.INSTANCE_URN.parent().parent()));
        //verify expansion of this in emitter URN
        checkDataFlowModule(module, new ModuleURN("metc:emit:this:this"),
                false, true, new DataRequest(
                new ModuleURN("metc:flow::default"), String.class.getName()),
                new DataRequest(SinkModuleFactory.INSTANCE_URN.parent().parent()));
        //verify that the initiated data flows are canceled by the system
        //if the module doesn't stop them in prestop
        module.setSkipCancel(true);
        checkDataFlowModule(module, new ModuleURN("metc:emit:this:this"),
                false, true, new DataRequest(
                new ModuleURN("metc:flow::default"), String.class.getName()),
                new DataRequest(SinkModuleFactory.INSTANCE_URN.parent().parent()));

        //verify that requests cannot be made when the module is not started.
        module.setInvokeDefault(true);
        module.setRequests(new DataRequest[]{
                new DataRequest(EmitterModuleFactory.INSTANCE_URN),
                new DataRequest(module.getURN(), String.class.getName())
        });
        new ExpectedFailure<ModuleStateException>(
                Messages.DATAFLOW_FAILED_REQ_MODULE_STATE_INCORRECT, procURN.toString(),
                ModuleState.STOPPED, ModuleState.REQUEST_FLOW_STATES.toString()){
            protected void run() throws Exception {
                module.createFlow();
            }
        };
        // verify requests cannot be canceled when the module
        // is not started
        final DataFlowID flowID = new DataFlowID("doesntmatter");
        module.setFlowID(flowID);
        new ExpectedFailure<ModuleStateException>(
                Messages.CANCEL_FAILED_MODULE_STATE_INCORRECT, flowID.getValue(),
                module.getURN().toString(), ModuleState.STOPPED,
                ModuleState.CANCEL_FLOW_STATES.toString()){
            protected void run() throws Exception {
                module.cancelFlow();
            }
        };
    }

    /**
     * Verifies that attempts to invoke data flow setup/cancel APIs, in
     * {@link DataFlowSupport}, from within
     * {@link DataEmitter#requestData(DataRequest, DataEmitterSupport)} &
     * {@link DataEmitter#cancel(DataFlowID, RequestID)} fails.
     *
     * @throws Exception if there were errors.
     */
    @Test
    public void checkNestedFlowRequestFailures() throws Exception {
        //create the flow requester module.
        //Have the instance name similar to that of emitter so that we can
        //test expansion of 'this' in the URN
        final ModuleURN procURN = new ModuleURN(FlowRequesterModuleFactory.PROVIDER_URN,
                "flow");
        sManager.createModule(FlowRequesterModuleFactory.PROVIDER_URN, procURN);
        ModuleTestBase.assertModuleInfo(sManager.getModuleInfo(procURN),
                procURN, ModuleState.CREATED, null, null, false, false,
                true, true, true);
        final FlowRequesterModule module = (FlowRequesterModule) ModuleBase.getInstance(procURN);
        assertNotNull(module);
        //Start the module
        sManager.start(procURN);
        //Carry out nested flow requests from request data
        module.setNestDataFlowInRequest(true);
        module.setNestedCreateDataFlow(true);
        module.setInvokeDefault(true);
        //invoking default createDataflow
        runNestedFlowRequestFailureInRequestData(procURN);
        module.setInvokeDefault(false);
        //invoking createDataFlow with explicit sink module append
        runNestedFlowRequestFailureInRequestData(procURN);
        module.setNestedCreateDataFlow(false);
        module.setNestedCancelDataFlow(true);
        //invoking cancel flow
        runNestedFlowRequestFailureInRequestData(procURN);
        //Carry out nested flow requests from cancel request
        DataFlowID flowID = createFlowForNestedFlowTesting(module);
        module.setNestDataFlowInCancel(true);
        module.setNestedCancelDataFlow(false);
        module.setNestedCreateDataFlow(true);
        module.setInvokeDefault(true);
        //invoking default create data flow
        runNestedFlowRequestFailureInCancel(module, flowID);
        //invoking create data flow with explicit sink module append
        flowID = createFlowForNestedFlowTesting(module);
        module.setNestDataFlowInCancel(true);
        module.setInvokeDefault(false);
        runNestedFlowRequestFailureInCancel(module, flowID);
        //invoking cancel flow
        flowID = createFlowForNestedFlowTesting(module);
        module.setNestDataFlowInCancel(true);
        module.setNestedCreateDataFlow(false);
        module.setNestedCancelDataFlow(true);
        runNestedFlowRequestFailureInCancel(module, flowID);
    }

    private void runNestedFlowRequestFailureInCancel(
            FlowRequesterModule inModule,
            DataFlowID inFlowID) throws ModuleException {
        //make sure there's no failure
        inModule.resetNestedCancelFailure();
        assertNull(inModule.getNestedCancelFailure());
        sManager.cancel(inFlowID);
        //verify we get a failure
        assertNotNull(inModule.getNestedCancelFailure());
        assertEquals(ModuleException.class,
                ExpectedFailure.assertI18NException(
                        inModule.getNestedCancelFailure(),
                        Messages.INCORRECT_NESTED_FLOW_REQUEST).getClass());
    }

    private DataFlowID createFlowForNestedFlowTesting(
            FlowRequesterModule inModule) throws ModuleException {
        inModule.setNestDataFlowInRequest(false);
        inModule.setNestDataFlowInCancel(false);
        //Create a data flow so that we can cancel it
        DataFlowID flowID = sManager.createDataFlow(new DataRequest[]{
                new DataRequest(inModule.getURN(),String.class.getName())
        });
        //verify flow is active
        sManager.getDataFlowInfo(flowID);
        return flowID;
    }

    private void runNestedFlowRequestFailureInRequestData(
            final ModuleURN inProcURN) throws Exception {
        assertEquals(ModuleException.class, ExpectedFailure.assertI18NException(
                new ExpectedFailure<RequestDataException>(null){
                    protected void run() throws Exception {
                        sManager.createDataFlow(new DataRequest[]{
                                new DataRequest(inProcURN)
                        });
                    }
                }.getException().getCause(),
                Messages.INCORRECT_NESTED_FLOW_REQUEST).getClass());
    }

    /**
     * Test create data flow errors due to system reported failures.
     *
     * @throws Exception if there's an unexpected failure.
     */
    @Test
    public void createFlowSystemFailures() throws Exception {
        assertTrue(sManager.getDataFlows(true).isEmpty());
        //null request
        new ExpectedFailure<DataFlowException>(
                Messages.DATA_REQUEST_TOO_SHORT,0){
            protected void run() throws Exception {
                sManager.createDataFlow(null);
            }
        };
        //empty request
        new ExpectedFailure<DataFlowException>(
                Messages.DATA_REQUEST_TOO_SHORT,0){
            protected void run() throws Exception {
                sManager.createDataFlow(new DataRequest[0]);
            }
        };
        //short request without sink auto-appended
        new ExpectedFailure<DataFlowException>(
                Messages.DATA_REQUEST_TOO_SHORT, 1){
            protected void run() throws Exception {
                sManager.createDataFlow(new DataRequest[]{new DataRequest(
                        new ModuleURN("metc:dontmatter"))},false);
            }
        };
        //invalid module URN for first module
        final ModuleURN urn1 = new ModuleURN("invalidURN");
        new ExpectedFailure<InvalidURNException>(Messages.INVALID_URN_SCHEME,
                urn1.scheme(), urn1.toString(), ModuleURN.SCHEME){
            protected void run() throws Exception {
                sManager.createDataFlow(new DataRequest[]{
                        new DataRequest(urn1),
                        new DataRequest(SingleModuleFactory.INSTANCE_URN)
                });
            }
        };
        //invalid module URN for the second module
        new ExpectedFailure<InvalidURNException>(Messages.INVALID_URN_SCHEME,
                urn1.scheme(),  urn1.toString(), ModuleURN.SCHEME){
            protected void run() throws Exception {
                sManager.createDataFlow(new DataRequest[]{
                        new DataRequest(SingleModuleFactory.INSTANCE_URN),
                        new DataRequest(urn1),
                });
            }
        };
        // A provider urn with a provider that doesn't exist
        final ModuleURN urn2 = new ModuleURN("metc:not:exist");
        //non-existent first module
        new ExpectedFailure<ModuleNotFoundException>(Messages.MODULE_NOT_FOUND,
                urn2.toString()){
            protected void run() throws Exception {
                sManager.createDataFlow(new DataRequest[]{
                        new DataRequest(urn2),
                        new DataRequest(SingleModuleFactory.INSTANCE_URN)
                });
            }
        };
        //non-existent second module
        new ExpectedFailure<ModuleNotFoundException>(Messages.MODULE_NOT_FOUND,
                urn2.toString()){
            protected void run() throws Exception {
                sManager.createDataFlow(new DataRequest[]{
                        new DataRequest(SingleModuleFactory.INSTANCE_URN),
                        new DataRequest(urn2)
                });
            }
        };
        // An instance urn for a module that does exist
        final ModuleURN urn3 = new ModuleURN(
                ComplexModuleFactory.PROVIDER_URN,"notexist");
        //non-existent first module
        new ExpectedFailure<ModuleNotFoundException>(Messages.MODULE_NOT_FOUND,
                urn3.toString()){
            protected void run() throws Exception {
                sManager.createDataFlow(new DataRequest[]{
                        new DataRequest(urn3),
                        new DataRequest(SingleModuleFactory.INSTANCE_URN)
                });
            }
        };
        //non-existent second module
        new ExpectedFailure<ModuleNotFoundException>(Messages.MODULE_NOT_FOUND,
                urn3.toString()){
            protected void run() throws Exception {
                sManager.createDataFlow(new DataRequest[]{
                        new DataRequest(SingleModuleFactory.INSTANCE_URN),
                        new DataRequest(urn3)
                });
            }
        };

        //An instance urn for a module whose provider does not exist
        final ModuleURN urn4 = new ModuleURN("metc:not:exist:no");
        //non-existent first module
        new ExpectedFailure<ModuleNotFoundException>(Messages.MODULE_NOT_FOUND,
                urn4.toString()){
            protected void run() throws Exception {
                sManager.createDataFlow(new DataRequest[]{
                        new DataRequest(urn4),
                        new DataRequest(SingleModuleFactory.INSTANCE_URN)
                });
            }
        }.getException().printStackTrace();
        //non-existent second module
        new ExpectedFailure<ModuleNotFoundException>(Messages.MODULE_NOT_FOUND,
                urn4.toString()){
            protected void run() throws Exception {
                sManager.createDataFlow(new DataRequest[]{
                        new DataRequest(SingleModuleFactory.INSTANCE_URN),
                        new DataRequest(urn4)
                });
            }
        };


        //non-emitting first module
        new ExpectedFailure<DataFlowException>(Messages.MODULE_NOT_EMITTER,
                SingleModuleFactory.INSTANCE_URN.toString()) {
            protected void run() throws Exception {
                sManager.createDataFlow(new DataRequest[]{
                        new DataRequest(SingleModuleFactory.INSTANCE_URN),
                        new DataRequest(EmitterModuleFactory.INSTANCE_URN)
                });
            }
        };
        //start the emitter module so as to not get module not started errors.
        startEmitter();
        //non-emitting second module
        new ExpectedFailure<DataFlowException>(Messages.MODULE_NOT_EMITTER,
                SingleModuleFactory.INSTANCE_URN.toString()) {
            protected void run() throws Exception {
                sManager.createDataFlow(new DataRequest[]{
                        new DataRequest(EmitterModuleFactory.INSTANCE_URN),
                        new DataRequest(SingleModuleFactory.INSTANCE_URN),
                        new DataRequest(SinkModuleFactory.INSTANCE_URN)
                },false);
            }
        };
        //non-receiving second module
        new ExpectedFailure<DataFlowException>(Messages.MODULE_NOT_RECEIVER,
                SingleModuleFactory.INSTANCE_URN.toString()) {
            protected void run() throws Exception {
                sManager.createDataFlow(new DataRequest[]{
                        new DataRequest(EmitterModuleFactory.INSTANCE_URN),
                        new DataRequest(SingleModuleFactory.INSTANCE_URN)
                });
            }
        };
        //create a receiver module, its autostarted
        final ModuleURN receiverURN = new ModuleURN(
                ProcessorModuleFactory.PROVIDER_URN,"myreceiver");
        sManager.createModule(ProcessorModuleFactory.PROVIDER_URN,receiverURN);

        //non-receiving third module
        new ExpectedFailure<DataFlowException>(Messages.MODULE_NOT_RECEIVER,
                SingleModuleFactory.INSTANCE_URN.toString()) {
            protected void run() throws Exception {
                sManager.createDataFlow(new DataRequest[]{
                        new DataRequest(EmitterModuleFactory.INSTANCE_URN),
                        new DataRequest(receiverURN),
                        new DataRequest(SingleModuleFactory.INSTANCE_URN)
                });
            }
        };
        //create another receiver module.
        final ModuleURN receiver2URN = new ModuleURN(
                ProcessorModuleFactory.PROVIDER_URN,"mysecondreceiver");
        sManager.createModule(ProcessorModuleFactory.PROVIDER_URN,receiver2URN);
        //try creating a data flow such that the specified URN matches multiple
        //modules, verify it fails.
        final ModuleURN multiMatchURN = receiver2URN.parent();
        HashSet actualParams = new HashSet<Object>(Arrays.asList(
                new ExpectedFailure<ModuleNotFoundException>(
                        Messages.MULTIPLE_MODULES_MATCH_URN){
                    protected void run() throws Exception {
                        sManager.createDataFlow(new DataRequest[]{
                                new DataRequest(EmitterModuleFactory.INSTANCE_URN),
                                new DataRequest(multiMatchURN)
                        });
                    }
                }.getException().getI18NBoundMessage().getParams()));
        HashSet expectedParams = new HashSet<Object>(Arrays.asList(
                multiMatchURN.getValue(), receiverURN.getValue(),
                receiver2URN.getValue()));
        //verify the message parameters
        assertEquals(expectedParams, actualParams);

        //verify this keyword expansion doesn't work
        final ModuleURN requestURN = new ModuleURN(
                ProcessorModuleFactory.PROVIDER_URN, "this");
        new ExpectedFailure<InvalidURNException>(
                Messages.INVALID_INSTANCE_URN,
                requestURN.toString(),
                requestURN.instanceName()){
            protected void run() throws Exception {
                sManager.createDataFlow(new DataRequest[]{
                        new DataRequest(EmitterModuleFactory.INSTANCE_URN),
                        new DataRequest(requestURN)
                });
            }
        };
        //Stop the receiver
        sManager.stop(receiverURN);
        //verify module stopped related failures 
        //last module stopped
        new ExpectedFailure<ModuleStateException>(
                Messages.DATAFLOW_FAILED_PCPT_MODULE_STATE_INCORRECT,
                receiverURN.toString(), ModuleState.STOPPED,
                ModuleState.PARTICIPATE_FLOW_STATES.toString()) {
            protected void run() throws Exception {
                sManager.createDataFlow(new DataRequest[]{
                        new DataRequest(EmitterModuleFactory.INSTANCE_URN),
                        new DataRequest(receiverURN),
                },false);
            }
        };
        //second module stopped
        new ExpectedFailure<ModuleStateException>(
                Messages.DATAFLOW_FAILED_PCPT_MODULE_STATE_INCORRECT,
                receiverURN.toString(), ModuleState.STOPPED,
                ModuleState.PARTICIPATE_FLOW_STATES.toString()) {
            protected void run() throws Exception {
                sManager.createDataFlow(new DataRequest[]{
                        new DataRequest(EmitterModuleFactory.INSTANCE_URN),
                        new DataRequest(receiverURN)});
            }
        };
        //first module stopped
        sManager.stop(EmitterModuleFactory.INSTANCE_URN);
        new ExpectedFailure<ModuleStateException>(
                Messages.DATAFLOW_FAILED_PCPT_MODULE_STATE_INCORRECT,
                EmitterModuleFactory.INSTANCE_URN.toString(),
                ModuleState.STOPPED,
                ModuleState.PARTICIPATE_FLOW_STATES.toString()) {
            protected void run() throws Exception {
                sManager.createDataFlow(new DataRequest[]{
                        new DataRequest(EmitterModuleFactory.INSTANCE_URN),
                        new DataRequest(receiverURN)});
            }
        };

        assertTrue(sManager.getDataFlows(true).isEmpty());
    }

    /**
     * Tests module's auto-creation and deletion as it participates
     * in data flows.
     *
     * @throws Exception if there were unexpected errors
     */
    @Test
    public void moduleAutoDelete() throws Exception {

        //Start emitter module, if not already started
        startEmitter();
        final ModuleURN procURN = new ModuleURN(
                ProcessorModuleFactory.PROVIDER_URN, "autod");
        //verify that this module does not exist, so that we can verify
        //it gets auto-instantiated.
        List<ModuleURN> urns = sManager.getModuleInstances(
                ProcessorModuleFactory.PROVIDER_URN);
        assertFalse(urns.toString(), urns.contains(procURN));
        //Create the first data flow
        DataFlowID flowID1 = sManager.createDataFlow(new DataRequest[]{
                new DataRequest(EmitterModuleFactory.INSTANCE_URN, "no"),
                new DataRequest(procURN, String.class.getName())
        });
        //verify that the module has been created.
        urns = sManager.getModuleInstances(
                ProcessorModuleFactory.PROVIDER_URN);
        assertTrue(urns.toString(), urns.contains(procURN));
        assertFlowInfo(sManager.getDataFlowInfo(flowID1), flowID1, 3, true,
                false, null, null);
        assertModuleInfo(sManager, procURN, ModuleState.STARTED,
                null, new DataFlowID[]{flowID1}, true, true, true, true, false);
        //create another one
        DataFlowID flowID2 = sManager.createDataFlow(new DataRequest[]{
                new DataRequest(EmitterModuleFactory.INSTANCE_URN, "so"),
                new DataRequest(procURN, String.class.getName())
        });
        assertFlowInfo(sManager.getDataFlowInfo(flowID2), flowID2, 3, true,
                false, null, null);
        assertModuleInfo(sManager, procURN, ModuleState.STARTED,
                null, new DataFlowID[]{flowID1, flowID2}, true, true,
                true, true, false);
        //Stop the first flow.
        sManager.cancel(flowID1);
        //verify that the module is still there.
        assertModuleInfo(sManager, procURN, ModuleState.STARTED,
                null, new DataFlowID[]{flowID2}, true, true,
                true, true, false);
        //Stop the second flow
        sManager.cancel(flowID2);
        //verify that the module has been deleted.
        urns = sManager.getModuleInstances(
                ProcessorModuleFactory.PROVIDER_URN);
        assertFalse(urns.toString(), urns.contains(procURN));

        //verify that the auto-created module is not orphaned
        //if the attempt to create a data flow fails
        //Create a data flow that fails to setup
        new ExpectedFailure<IllegalRequestParameterValue>(null){
            protected void run() throws Exception {
                sManager.createDataFlow(new DataRequest[]{
                        new DataRequest(EmitterModuleFactory.INSTANCE_URN, null),
                        new DataRequest(procURN, String.class.getName())
                });
            }
        };
        //verify that the module does not exist
        urns = sManager.getModuleInstances(
                ProcessorModuleFactory.PROVIDER_URN);
        assertFalse(urns.toString(), urns.contains(procURN));
    }

    /**
     * Verify create data flow request errors due to errors raised by modules
     *
     * @throws Exception if there's an error
     */
    @Test
    public void createFlowModuleFailures() throws Exception {
        //verify that there are no data flows
        assertTrue(sManager.getDataFlows(true).isEmpty());

        //Start emitter module, if not already started
        startEmitter();
        final ModuleURN procURN = new ModuleURN(
                ProcessorModuleFactory.PROVIDER_URN, "failures");
        //verify that this module does not exist, so that we can verify
        //it gets auto-instantiated.
        List<ModuleURN> urns = sManager.getModuleInstances(
                ProcessorModuleFactory.PROVIDER_URN);
        assertFalse(urns.toString(), urns.contains(procURN));
        //test various emitter module failures: parm value
        new ExpectedFailure<IllegalRequestParameterValue>(
                Messages.ILLEGAL_REQ_PARM_VALUE,
                EmitterModuleFactory.INSTANCE_URN.getValue(),
                null) {
            protected void run() throws Exception {
                sManager.createDataFlow(new DataRequest[]{
                        new DataRequest(EmitterModuleFactory.INSTANCE_URN),
                        new DataRequest(procURN,String.class.getName())});
            }
        };
        //verify that when a request to initate data flow fails any module
        //that had sucessfully initiated requests have their requests
        //canceled. In this case the processor module should have its
        //request canceled.
        ProcessorModule proc = (ProcessorModule) ModuleBase.getInstance(procURN);
        assertEquals(0, proc.getNumRequests());
        //test various emitter module failures: parm type
        new ExpectedFailure<UnsupportedRequestParameterType>(
                Messages.UNSUPPORTED_REQ_PARM_TYPE,
                EmitterModuleFactory.INSTANCE_URN.getValue(),
                Boolean.class.getName()) {
            protected void run() throws Exception {
                sManager.createDataFlow(new DataRequest[]{
                        new DataRequest(EmitterModuleFactory.INSTANCE_URN,
                                true),
                        new DataRequest(procURN, String.class.getName())});
            }
        };

        //test processor module failures
        new ExpectedFailure<IllegalRequestParameterValue>(
                Messages.ILLEGAL_REQ_PARM_VALUE,
                procURN.getValue(),
                null) {
            protected void run() throws Exception {
                sManager.createDataFlow(new DataRequest[]{
                        new DataRequest(EmitterModuleFactory.INSTANCE_URN,
                                "send data"),
                        new DataRequest(procURN)});
            }
        };
        new ExpectedFailure<UnsupportedRequestParameterType>(
                Messages.UNSUPPORTED_REQ_PARM_TYPE,
                procURN.getValue(),
                Boolean.class.getName()) {
            protected void run() throws Exception {
                sManager.createDataFlow(new DataRequest[]{
                        new DataRequest(EmitterModuleFactory.INSTANCE_URN,
                                "send data"),
                        new DataRequest(procURN, true)});
            }
        };
        new ExpectedFailure<IllegalRequestParameterValue>(
                Messages.ILLEGAL_REQ_PARM_VALUE,
                procURN.getValue(),
                "blah") {
            protected void run() throws Exception {
                sManager.createDataFlow(new DataRequest[]{
                        new DataRequest(EmitterModuleFactory.INSTANCE_URN,
                                "send data"),
                        new DataRequest(procURN, "blah")});
            }
        };
    }

    /**
     * Verify that if the module fails to start, any flows it created
     * within its preStart() method are canceled.
     *
     * @throws Exception if there was an unexpected failure.
     */
    @Test
    public void preStartFlowsCleanup() throws Exception {
        startEmitter();
        //create a flow requester
        final ModuleURN procURN = new ModuleURN(
                FlowRequesterModuleFactory.PROVIDER_URN,
                "prestart");
        sManager.createModule(FlowRequesterModuleFactory.PROVIDER_URN, procURN);
        //construct data request for it to issue.
        DataRequest[] req = new DataRequest[]{
                new DataRequest(EmitterModuleFactory.INSTANCE_URN,"somestring"),
                new DataRequest(procURN, String.class.getName())
        };
        final FlowRequesterModule module = (FlowRequesterModule) ModuleBase.
                getInstance(procURN);
        assertNotNull(module);
        module.setRequests(req);
        module.setInvokeDefault(true);
        module.setFailPreStart(true);
        new ExpectedFailure<ModuleException>(
                TestMessages.TEST_START_STOP_FAILURE){
            protected void run() throws Exception {
                sManager.start(procURN);
            }
        };
        //verify module state
        assertModuleInfo(sManager, procURN, ModuleState.START_FAILED, null,
                null, false, false, true, true, true);
        assertNotNull(module.getFlowID());
        //verify that that the data flow is not running.
        new ExpectedFailure<DataFlowNotFoundException>(
                Messages.DATA_FLOW_NOT_FOUND,
                module.getFlowID().getValue()){
            protected void run() throws Exception {
                sManager.getDataFlowInfo(module.getFlowID());
            }
        };
        //verify the data flow in history.
        DataFlowInfo flowInfo = sManager.getDataFlowHistory().get(0);
        assertFlowInfo(flowInfo, module.getFlowID(), 3, true, true,
                procURN, null);
    }

    /**
     * Test data flow stop requested by an emitter.
     *
     * @throws Exception if there are unexpected errors
     */
    @Test(timeout = 60000)
    public void dataFlowStopEmitter() throws Exception {
        assertTrue(sManager.getDataFlowHistory().isEmpty());
        Map<String,Object> param = new HashMap<String, Object>();
        final String emitData = "my data";
        param.put("value", emitData);
        param.put("error", TestMessages.EMIT_DATA_ERROR);
        param.put("times", NUM_TIMES);
        param.put("requestStop", Boolean.TRUE);
        startEmitter();
        ModuleURN procURN = new ModuleURN(ProcessorModuleFactory.PROVIDER_URN,
                "emitFail");
        DataFlowID flowID = sManager.createDataFlow(new DataRequest[]{
                new DataRequest(EmitterModuleFactory.INSTANCE_URN, param),
                new DataRequest(procURN, String.class.getName())
        });
        sSink.waitUntilTerminator();
        List<DataFlowID> flows = sManager.getDataFlows(true);
        assertEquals(1,flows.size());
        assertEquals(flowID, flows.get(0));
        EmitterModule.readyToProceed();
        //wait for the flow to stop
        while(!sManager.getDataFlows(true).isEmpty()) {
            Thread.sleep(1000);
        }
        assertEquals(1, sManager.getDataFlowHistory().size());
        assertFlowInfo(sManager.getDataFlowHistory().get(0),flowID,
                3, true, true, null, EmitterModuleFactory.INSTANCE_URN);
        verifyFlowSteps(param, EmitterModuleFactory.INSTANCE_URN,
                procURN, sManager.getDataFlowHistory().get(0), true, false);
        //verify emitter has the task stopped.
        EmitterModule emitter = (EmitterModule) ModuleBase.getInstance(
                EmitterModuleFactory.INSTANCE_URN);
        final Set<RequestID> requestIDs = emitter.getRequests();
        assertEquals(1, requestIDs.size());
        Future<Integer> task = emitter.getTask(requestIDs.iterator().next());
        assertTrue(task.isCancelled());
        //reset the counter for test.
        emitter.clear();
    }

    /**
     * Tests data flow stop requested by a data receiver.
     *
     * @throws Exception if there were unexpected errors.
     */
    @Test(timeout = 60000)
    public void dataFlowStopReceiver() throws Exception {
        assertTrue(sManager.getDataFlowHistory().isEmpty());
        Map<String,Object> param = new HashMap<String, Object>();
        final String emitData = "my data";
        param.put("value", emitData);
        param.put("error", TestMessages.EMIT_DATA_ERROR);
        param.put("times", NUM_TIMES);
        param.put("emitNull", null);
        startEmitter();
        ModuleURN procURN = new ModuleURN(ProcessorModuleFactory.PROVIDER_URN,
                "receiveFail");
        DataFlowID flowID = sManager.createDataFlow(new DataRequest[]{
                new DataRequest(EmitterModuleFactory.INSTANCE_URN, param),
                new DataRequest(procURN, String.class.getName())
        });
        List<DataFlowID> flows = sManager.getDataFlows(true);
        assertEquals(1,flows.size());
        assertEquals(flowID, flows.get(0));
        sSink.waitUntilTerminator();
        //Now let the emitter emit null
        EmitterModule.readyToProceed();
        //wait for the flow to stop
        while(!sManager.getDataFlows(true).isEmpty()) {
            Thread.sleep(1000);
        }
        assertEquals(1, sManager.getDataFlowHistory().size());
        assertFlowInfo(sManager.getDataFlowHistory().get(0),flowID,
                3, true, true, null, procURN);
        verifyFlowSteps(param, EmitterModuleFactory.INSTANCE_URN, procURN,
                sManager.getDataFlowHistory().get(0), false, true);
        //verify emitter has the task stopped.
        EmitterModule emitter = (EmitterModule) ModuleBase.getInstance(
                EmitterModuleFactory.INSTANCE_URN);
        final Set<RequestID> requestIDs = emitter.getRequests();
        assertEquals(1, requestIDs.size());
        Future<Integer> task = emitter.getTask(requestIDs.iterator().next());
        assertTrue(task.isCancelled());
        //reset the counter for test.
        emitter.clear();
    }

    /**
     * Verifies that any exceptions thrown by a module when canceling
     * a request is ignored and doesn't prevent the rest of the modules
     * in a data flow from getting their own request cancellations.
     *
     * @throws Exception if there were unexpected errors.
     */
    @Test
    public void requestCancelExceptionIgnored() throws Exception {
        //Create a data flow, setup the emitter to throw an
        //exception when its requested to cancel the data flow
        //and verify that the data flow is canceled and that
        //the processor got its request canceled.
        ModuleURN procURN = new ModuleURN(ProcessorModuleFactory.PROVIDER_URN,
                "cancelFail");
        startEmitter();
        DataFlowID flowID = sManager.createDataFlow(new DataRequest[]{
                new DataRequest(EmitterModuleFactory.INSTANCE_URN, "something"),
                new DataRequest(procURN, String.class.getName())
        });
        //Get a reference to the emitter
        EmitterModule em = (EmitterModule) ModuleBase.getInstance(
                EmitterModuleFactory.INSTANCE_URN);
        assertEquals(1, em.getRequests().size());
        //Get a reference to the processor
        ProcessorModule pm = (ProcessorModule) ModuleBase.getInstance(procURN);
        assertEquals(1, pm.getNumRequests());
        //Setup emitter to throw exception on cancel
        em.setThrowExceptionOnCancel(true);
        //verify the flow
        assertFlowInfo(sManager.getDataFlowInfo(flowID), flowID, 3,
                true, false, null, null);
        //cancel the flow
        sManager.cancel(flowID);
        //verify that the flows are no longer running
        assertTrue(sManager.getDataFlows(true).isEmpty());
        //verify both emitter and processor had their requests canceled.
        assertEquals(0,pm.getNumRequests());
        assertEquals(1,em.getRequests().size());
        assertTrue(em.getTask(em.getRequests().iterator().next()).isCancelled());
    }


    /**
     * Starts the emitter if its not already running.
     *
     * @throws ModuleException if there were errors.
     */
    private static void startEmitter() throws ModuleException {
        ModuleInfo info = sManager.getModuleInfo(EmitterModuleFactory.INSTANCE_URN);
        if(!info.getState().isStarted()) {
            sManager.start(EmitterModuleFactory.INSTANCE_URN);
        }
    }

    /**
     * Checks simple data flow of 3 modules, emitter, processor and sink.
     *
     * @param inInvokeDefault if the API that defaults append sink flag
     * should be invoked.
     * @param inAppendSink the value of append sink flag, if inInvokeDefault
     * is false.
     * @param inProcAutoCreated if the processor module was auto-created.
     * @param inDataRequests the data requests, emitter module is
     * automatically prepended to it.
     * 
     * @throws Exception if there are errors
     */
    private void checkDataFlowManager(boolean inInvokeDefault,
                                      boolean inAppendSink,
                                      boolean inProcAutoCreated,
                                      DataRequest... inDataRequests)
            throws Exception {
        int numFlowHistory = sManager.getDataFlowHistory().size();
        List<DataFlowID> flows;
        //Initialize the request parameter for emitter.
        Map<String,Object> param = new HashMap<String, Object>();
        final String emitData = "my data";
        param.put("value", emitData);
        param.put("error", TestMessages.EMIT_DATA_ERROR);
        param.put("times", NUM_TIMES);
        //Prepend emitter to the supplied request.
        final DataRequest[] requests = new DataRequest[inDataRequests.length + 1];
        requests[0] = new DataRequest(EmitterModuleFactory.INSTANCE_URN, param);
        System.arraycopy(inDataRequests,0,requests,1, inDataRequests.length);
        //The processor module is always the second one, get its URL
        ModuleURN procModuleURN = requests[1].getRequestURN();

        DataFlowID flowID;
        //Create the data flow
        if (inInvokeDefault) {
            flowID = sManager.createDataFlow(requests);
        } else {
            flowID = sManager.createDataFlow(requests,inAppendSink);
        }
        assertNotNull(flowID);
        //verify that the flow ID is reported
        flows = sManager.getDataFlows(true);
        assertEquals(1, flows.size());
        assertEquals(flowID, flows.get(0));
        assertEquals(flows,sManager.getDataFlows(false));
        // verify that we cannot stop a module when its participating
        // in data flows
        HashSet<DataFlowID> set = new HashSet<DataFlowID>();
        set.add(flowID);
        new ExpectedFailure<DataFlowException>(
                Messages.CANNOT_STOP_MODULE_DATAFLOWS,
                EmitterModuleFactory.INSTANCE_URN.toString(),
                set.toString()){
            protected void run() throws Exception {
                sManager.stop(EmitterModuleFactory.INSTANCE_URN);
            }
        };

        //wait until we get all the data through the sink
        sSink.waitUntilTerminator();
        //get data flow info for verification
        DataFlowInfo flowInfo = sManager.getDataFlowInfo(flowID);
        //now verify the flow info
        assertFlowInfo(flowInfo,flowID, 3, true, false, null, null);
        final ModuleURN actualProcURN = verifyFlowSteps(param,
                EmitterModuleFactory.INSTANCE_URN, procModuleURN, flowInfo, false, false);
        //verify module infos
        assertModuleInfo(sManager.getModuleInfo(
                EmitterModuleFactory.INSTANCE_URN),
                EmitterModuleFactory.INSTANCE_URN,ModuleState.STARTED, null,
                new DataFlowID[]{flowID},false, false, false, true, false);
        assertModuleInfo(sManager.getModuleInfo(actualProcURN),
                actualProcURN,ModuleState.STARTED, null,
                new DataFlowID[]{flowID},inProcAutoCreated, true, true,
                true, false);
        assertModuleInfo(sManager.getModuleInfo(
                SinkModuleFactory.INSTANCE_URN),
                SinkModuleFactory.INSTANCE_URN,ModuleState.STARTED, null,
                new DataFlowID[]{flowID},false, true, true, false, false);
        //Verify that the emitter has the data flow ID
        EmitterModule emitter = (EmitterModule) ModuleBase.getInstance(
                EmitterModuleFactory.INSTANCE_URN);
        assertTrue(emitter.getFlows().contains(flowID));
        assertEquals(1, emitter.getFlows().size());
        //verify processor was invoked with correct parameters
        ProcessorModule proc = (ProcessorModule) ModuleBase.getInstance(actualProcURN);
        assertEquals(1, proc.getNumRequests());
        assertTrue(proc.isStartInvoked());
        assertEquals(1, proc.getFlows().length);
        assertEquals(flowID, proc.getFlows()[0]);
        //cancel the data flow as soon as we get all the data
        sManager.cancel(flowID);
        //verify the received data
        FlowData[] data = sSink.getData();
        assertEquals(NUM_TIMES / 2, data.length);
        for(FlowData d:data) {
            assertEquals(flowID, d.getFirstMember());
            assertEquals(emitData, d.getSecondMember());
        }
        //clear the sink
        sSink.clear();
        //verify that the modules have been stopped
        //emitter
        final Set<RequestID> requestIDs = emitter.getRequests();
        assertEquals(1, requestIDs.size());
        Future<Integer> task = emitter.getTask(requestIDs.iterator().next());
        assertTrue(task.isCancelled());
        //verify that the flow got canceled
        assertTrue(emitter.getFlows().isEmpty());
        //reset the counter for test.
        emitter.clear();
        //verify no flows running.
        assertTrue(sManager.getDataFlows(true).isEmpty());

        //verify processor was able to clear its state.
        proc = (ProcessorModule) ModuleBase.getInstance(actualProcURN);
        assertEquals(0, proc.getNumRequests());
        assertEquals(0, proc.getFlows().length);

        //verify flow history record
        List<DataFlowInfo> history = sManager.getDataFlowHistory();
        //should have one more record
        assertEquals(numFlowHistory + 1, history.size());
        //The record corresponding to this flow must be the first one.
        //verify the record and the steps
        assertFlowInfo(history.get(0), flowID, 3, true, true, null, null);
        verifyFlowSteps(param, EmitterModuleFactory.INSTANCE_URN,
                procModuleURN, history.get(0), false, false);
        //if the processor module was auto-created, verify that it
        //got deleted.
        if(inProcAutoCreated) {
            new ExpectedFailure<ModuleNotFoundException>(
                    Messages.MODULE_NOT_FOUND, actualProcURN.toString()) {
                protected void run() throws Exception {
                    sManager.getModuleInfo(actualProcURN);
                }
            };
        }
    }
    /**
     * Checks simple data flow of 3 modules, emitter, processor and sink.
     *
     * @param inModule The flow requester module instance
     * @param inEmitterURN the Emitter URN, if null the default value is used
     * @param inInvokeDefault if the API that defaults append sink flag
     * should be invoked.
     * @param inAppendSink the value of append sink flag, if inInvokeDefault
     *  is false.
     * @param inDataRequests the data requests, emitter module is
     *  automatically prepended to it. @throws Exception if there are errors
     *
     * @throws Exception if there's a failure
     */
    private void checkDataFlowModule(FlowRequesterModule inModule,
                                     ModuleURN inEmitterURN,
                                     boolean inInvokeDefault,
                                     boolean inAppendSink,
                                     DataRequest... inDataRequests)
            throws Exception {
        int numFlowHistory = sManager.getDataFlowHistory().size();
        List<DataFlowID> flows;
        //Initialize the request parameter for emitter.
        Map<String,Object> param = new HashMap<String, Object>();
        final String emitData = "my data";
        param.put("value", emitData);
        param.put("error", TestMessages.EMIT_DATA_ERROR);
        param.put("times", NUM_TIMES);
        //Prepend emitter to the supplied request.
        final DataRequest[] requests = new DataRequest[inDataRequests.length + 1];
        if(inEmitterURN == null) {
            inEmitterURN = EmitterModuleFactory.INSTANCE_URN;
        }
        requests[0] = new DataRequest(inEmitterURN, param);
        System.arraycopy(inDataRequests,0,requests,1, inDataRequests.length);
        //The processor module is always the second one, get its URL
        ModuleURN procModuleURN = requests[1].getRequestURN();

        DataFlowID flowID;
        //Supply the data flow request parameters to the module
        inModule.setRequests(requests);
        inModule.setInvokeDefault(inInvokeDefault);
        inModule.setAppendSink(inAppendSink);
        //Start the module which should initiate the data flow.
        sManager.start(inModule.getURN());
        flowID = inModule.getFlowID();
        assertNotNull(flowID);
        //verify that the flow ID is reported
        flows = sManager.getDataFlows(true);
        assertEquals(1, flows.size());
        assertEquals(flowID, flows.get(0));
        //verify that this flow gets filtered out as its module initiated.
        assertTrue(sManager.getDataFlows(false).isEmpty());
        //wait until we get all the data through the sink
        sSink.waitUntilTerminator();
        //get data flow info for verification
        DataFlowInfo flowInfo = sManager.getDataFlowInfo(flowID);
        //now verify the flow info
        assertFlowInfo(flowInfo,flowID, 3, true, false, inModule.getURN(), null);
        ModuleURN actualProcURN = verifyFlowSteps(param, inEmitterURN,
                procModuleURN, flowInfo, false, false);

        //verify module infos
        ModuleTestBase.assertModuleInfo(sManager.getModuleInfo(
                EmitterModuleFactory.INSTANCE_URN),
                EmitterModuleFactory.INSTANCE_URN,ModuleState.STARTED, null,
                new DataFlowID[]{flowID},false, false, false, true, false);
        ModuleTestBase.assertModuleInfo(sManager.getModuleInfo(actualProcURN),
                actualProcURN,ModuleState.STARTED, new DataFlowID[]{flowID},
                new DataFlowID[]{flowID},false, false, true, true, true);
        ModuleTestBase.assertModuleInfo(sManager.getModuleInfo(
                SinkModuleFactory.INSTANCE_URN),
                SinkModuleFactory.INSTANCE_URN,ModuleState.STARTED, null,
                new DataFlowID[]{flowID},false, true, true, false, false);
        //Verify that the emitter has the data flow ID
        EmitterModule emitter = (EmitterModule) ModuleBase.getInstance(
                EmitterModuleFactory.INSTANCE_URN);
        assertTrue(emitter.getFlows().contains(flowID));
        assertEquals(1, emitter.getFlows().size());
        //verify processor was invoked with correct parameters
        ProcessorModule proc = (ProcessorModule) ModuleBase.getInstance(actualProcURN);
        assertEquals(1, proc.getNumRequests());
        assertTrue(proc.isStartInvoked());
        assertEquals(1, proc.getFlows().length);
        assertEquals(flowID, proc.getFlows()[0]);
        //cancel the data flow as soon as we get all the data by stopping the module
        sManager.stop(inModule.getURN());
        //verify the received data
        FlowData[] data = sSink.getData();
        assertEquals(NUM_TIMES / 2, data.length);
        for(FlowData d:data) {
            assertEquals(flowID, d.getFirstMember());
            assertEquals(emitData, d.getSecondMember());
        }
        //clear the sink
        sSink.clear();
        //verify that the modules have been stopped
        //emitter
        final Set<RequestID> requestIDs = emitter.getRequests();
        assertEquals(1, requestIDs.size());
        Future<Integer> task = emitter.getTask(requestIDs.iterator().next());
        assertTrue(task.isCancelled());
        //verify that the flow got canceled
        assertTrue(emitter.getFlows().isEmpty());
        //reset the counter for test.
        emitter.clear();
        //verify processor was started.
        proc = (ProcessorModule) ModuleBase.getInstance(actualProcURN);
        assertEquals(0, proc.getNumRequests());
        assertTrue(proc.isStartInvoked());
        assertTrue(proc.isStopInvoked());
        //verify no flows running.
        assertTrue(sManager.getDataFlows(true).isEmpty());

        //verify flow history record
        List<DataFlowInfo> history = sManager.getDataFlowHistory();
        //should have one more record
        assertEquals(numFlowHistory + 1, history.size());
        //The record corresponding to this flow must be the first one.
        //verify the record and the steps
        assertFlowInfo(history.get(0), flowID, 3, true, true, inModule.getURN(),
                inModule.isSkipCancel()
                        ? null
                        : inModule.getURN());
        verifyFlowSteps(param, inEmitterURN, procModuleURN,
                history.get(0), false, false);
    }

    private ModuleURN verifyFlowSteps(Map<String, Object> inParam,
                                      ModuleURN inEmitterURN,
                                      ModuleURN inProcModuleURN,
                                      DataFlowInfo inFlowInfo,
                                      boolean inEmitStop,
                                      boolean inReceiveStop) {
        assertFlowStep(inFlowInfo.getFlowSteps()[0],
                EmitterModuleFactory.INSTANCE_URN,
                true,
                NUM_TIMES + 1 + (inReceiveStop? 1:0),
                NUM_TIMES + (inEmitStop? 1:0),
                inEmitStop
                        ? TestMessages.STOP_DATA_FLOW.getText()
                        : TestMessages.EMIT_DATA_ERROR.getText(),
                false,
                0,
                0,
                null,
                inEmitterURN,
                inParam.toString());
        assertFlowStep(inFlowInfo.getFlowSteps()[1],
                // The actual URN may be different from the
                // supplied one, if the supplied one was
                // abbreviated, do not check
                null,
                true,
                NUM_TIMES / 2 + 1,
                0,
                null,
                true,
                NUM_TIMES + 1 + (inReceiveStop? 1: 0),
                NUM_TIMES / 2 + (inReceiveStop? 1: 0),
                inReceiveStop
                        ? TestMessages.STOP_DATA_FLOW.getText()
                        : TestMessages.BAD_DATA.getText(),
                inProcModuleURN,
                String.class.getName());
        ModuleURN actualProcURN = inFlowInfo.getFlowSteps()[1].getModuleURN();
        assertFlowStep(inFlowInfo.getFlowSteps()[2],
                SinkModuleFactory.INSTANCE_URN,
                false,
                0,
                0,
                null,
                true,
                NUM_TIMES / 2 + 1,
                0,
                null,
                //Requested URN may be an abbreviated one
                //skip the check
                null,
                null);
        return actualProcURN;
    }
    private static ModuleManager sManager;
    private static final int NUM_TIMES = 6;
    private static Sink sSink = new Sink();
}
