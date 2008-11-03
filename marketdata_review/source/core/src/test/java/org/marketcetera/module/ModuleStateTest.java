package org.marketcetera.module;

import org.marketcetera.util.misc.ClassVersion;
import static org.junit.Assert.*;
import static org.marketcetera.module.ModuleState.*;
import org.junit.Test;

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
        assertModuleState(CREATED, false, false, false);
        assertModuleState(STARTED, true, true, true);
        assertModuleState(STARTING, false, true, false);
        assertModuleState(START_FAILED, false, false, false);
        assertModuleState(STOPPING, false, false, true);
        assertModuleState(STOP_FAILED, true, true, true);
        assertModuleState(STOPPED, false, false, false);
    }
    /**
     * Verifies the supplied ModuleState instance.
     *
     * @param inState the module state instance
     * @param inStarted if the module is considered started in this state
     * @param inParticipate if the module can participate in data flows
     * in this state
     * @param inStop if the module can stop data flows in this state.
     */
    private static void assertModuleState(ModuleState inState,
                                          boolean inStarted,
                                          boolean inParticipate,
                                          boolean inStop) {
        assertEquals(inStarted, inState.isStarted());
        assertEquals(inParticipate, inState.canParticipateFlows());
        assertEquals(inStop, inState.canStopFlows());
    }
}
