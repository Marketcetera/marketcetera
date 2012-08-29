package org.marketcetera.module;

import org.marketcetera.util.misc.ClassVersion;
import org.junit.Test;
import static org.junit.Assert.*;

/* $License$ */
/**
 * Tests {@link ModuleManager#stop}
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class ManagerStopTest extends ModuleTestBase {
    /**
     * Verifies that all the data flows and modules are stopped when the
     * module manager is stopped.
     *
     * @throws Exception if there were unexpected errors.
     */
    @Test
    public void managerStop() throws Exception {
        ModuleManager manager = new ModuleManager();
        manager.init();
        //Start emitter module.
        manager.start(EmitterModuleFactory.INSTANCE_URN);
        // A module for data flow initiated by the API
        ModuleURN procURN = new ModuleURN(ProcessorModuleFactory.PROVIDER_URN,
                "mStop");
        //create the flow requester module.
        //Have the instance name similar to that of emitter so that we can
        //test expansion of 'this' in the URN
        ModuleURN flowURN = new ModuleURN(FlowRequesterModuleFactory.PROVIDER_URN,
                "mStop");
        manager.createModule(FlowRequesterModuleFactory.PROVIDER_URN, flowURN);
        ModuleTestBase.assertModuleInfo(manager.getModuleInfo(flowURN),
                flowURN, ModuleState.CREATED, null, null, false, false,
                true, true, true);
        final FlowRequesterModule module = (FlowRequesterModule)
                ModuleBase.getInstance(flowURN);
        assertNotNull(module);
        //Create data flows
        DataFlowID flow1 = manager.createDataFlow(new DataRequest[]{
                new DataRequest(EmitterModuleFactory.INSTANCE_URN,"some string"),
                new DataRequest(procURN, String.class.getName())
        });
        module.addRequests(new DataRequest[]{
                new DataRequest(EmitterModuleFactory.INSTANCE_URN,"some what"),
                new DataRequest(procURN, String.class.getName())
        });
        module.addRequests(new DataRequest[]{
                new DataRequest(EmitterModuleFactory.INSTANCE_URN,"some total"),
                new DataRequest(procURN, String.class.getName())
        });
        module.setInvokeDefault(true);
        //Start the data flow.
        manager.start(flowURN);
        DataFlowID[] flowIDs = module.getFlowIDs();
        //Verify the data flows are running
        assertFlowInfo(manager.getDataFlowInfo(flow1),flow1, 3, true, false,
                null, null);
        assertNotNull(flowIDs);
        assertEquals(2, flowIDs.length);
        assertFlowInfo(manager.getDataFlowInfo(flowIDs[0]),flowIDs[0], 3, true, false,
                flowURN, null);
        assertFlowInfo(manager.getDataFlowInfo(flowIDs[1]),flowIDs[1], 3, true, false,
                flowURN, null);
        //Now stop the module manager.
        manager.stop();
        //Verify that the data flows are stopped
        assertTrue(manager.getDataFlows(true).isEmpty());
        //Verify that all the modules are stopped
        assertModuleBase(EmitterModuleFactory.INSTANCE_URN, true, true,
                false, false);
        assertModuleBase(procURN, true, true, true, true);
        assertModuleBase(flowURN, true, true, false, false);
    }

}
