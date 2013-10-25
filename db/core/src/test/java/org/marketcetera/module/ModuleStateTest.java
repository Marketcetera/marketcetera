package org.marketcetera.module;

import org.marketcetera.util.misc.ClassVersion;
import static org.junit.Assert.*;
import static org.marketcetera.module.ModuleState.*;
import org.junit.Test;

import java.util.Set;
import java.util.HashSet;
import java.util.EnumSet;

/* $License$ */
/**
 * Tests various aspects of {@link ModuleState}
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$")
public class ModuleStateTest extends ModuleTestBase {

    /**
     * Tests attributes of various module states.
     */
    @Test
    public void states() {
        assertModuleState(CREATED, true, false, false, false, false, false, true);
        assertModuleState(STARTED, false, true, true, true, true, true, false);
        assertModuleState(STARTING, false, false, true, false, false, false, false);
        assertModuleState(START_FAILED, true, false, false, false, false, false, true);
        assertModuleState(STOPPING, false, false, false, false, true, false, false);
        assertModuleState(STOP_FAILED, false, true, true, true, true, true, false);
        assertModuleState(STOPPED, true, false, false, false, false, false, true);
        assertTrue("All states not tested",mAllValues.isEmpty());
    }
    /**
     * Verifies the supplied ModuleState instance.
     *
     * @param inState the module state value
     * @param inCanStart if the module can be started from this state
     * @param inStarted if the module is considered started in this state
     * @param inRequest if the module can request data flows in this state
     * @param inParticipate if the module can participate in data flows
     * in this state
     * @param inCancel if the moduel can cancel data flows in this state.
     * @param inCanStop if the module can stop data flows in this state.
     * @param inCanDelete if the module can be deleted in this state.
     */
    private void assertModuleState(ModuleState inState,
                                          boolean inCanStart,
                                          boolean inStarted,
                                          boolean inRequest,
                                          boolean inParticipate,
                                          boolean inCancel,
                                          boolean inCanStop,
                                          boolean inCanDelete) {
        assertEquals(inCanStart, inState.canBeStarted());
        assertEquals(inCanStart, STARTABLE_STATES.contains(inState));
        assertEquals(inStarted, inState.isStarted());
        assertEquals(inStarted, STARTED_STATES.contains(inState));
        assertEquals(inRequest, inState.canRequestFlows());
        assertEquals(inRequest, REQUEST_FLOW_STATES.contains(inState));
        assertEquals(inParticipate, inState.canParticipateFlows());
        assertEquals(inParticipate, PARTICIPATE_FLOW_STATES.contains(inState));
        assertEquals(inCancel, inState.canCancelFlows());
        assertEquals(inCancel, CANCEL_FLOW_STATES.contains(inState));
        assertEquals(inCanStop, inState.canBeStopped());
        assertEquals(inCanStop, STOPPABLE_STATES.contains(inState));
        assertEquals(inCanDelete, inState.canBeDeleted());
        assertEquals(inCanDelete, DELETABLE_STATES.contains(inState));
        mAllValues.remove(inState);
    }
    private final Set<ModuleState> mAllValues = new HashSet<ModuleState>(EnumSet.allOf(ModuleState.class));
}
