package org.marketcetera.strategy;

import org.marketcetera.core.ClassVersion;

/* $License$ */

/**
 * Indicates the status of a running strategy.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public enum Status
{
    /**
     * the strategy has been created but not yet started
     */
    UNSTARTED,
    /**
     * the strategy is being prepared and, if appropriate, compiled
     */
    COMPILING,
    /**
     * the strategy is executing {@link RunningStrategy#onStart()}
     */
    STARTING,
    /**
     * the strategy is currently running
     */
    RUNNING,
    /**
     * the strategy is executing {@link RunningStrategy#onStop()}
     */
    STOPPING,
    /**
     * the strategy was started and running, but has been stopped
     */
    STOPPED,
    /**
     * the strategy is not running because of an error
     */
    FAILED;
    /**
     * Indicates if the strategy is currently executing.
     *
     * @return a <code>boolean</code> value
     */
    public boolean isRunning()
    {
        return this.equals(COMPILING) ||
               this.equals(STARTING) ||
               this.equals(RUNNING);
    }
    /**
     * Indicates if a strategy can receive incoming data.
     *
     * @return a <code>boolean</code> value
     */
    boolean canReceiveData()
    {
        return this.equals(STARTING) ||
               this.equals(RUNNING);
    }
    /**
     * Indicates if a strategy can send outgoing data.
     *
     * @return a <code>boolean</code> value
     */
    boolean canSendData()
    {
        return this.equals(STARTING) ||
               this.equals(RUNNING);
    }
    /**
     * Validates a potential state change based on the current state. 
     *
     * @param inNewStatus
     * @return a <code>boolean</code> value indicating whether the state change is allowed
     */
    boolean canChangeStatusTo(Status inNewStatus)
    {
        assert(inNewStatus != null);
        // try to quickly catch an increase in the number of states that wasn't updated in the static table 
        assert(LEGAL_STATE_CHANGES[0].length == FAILED.ordinal()+1);
        // conduct the check
        return LEGAL_STATE_CHANGES[this.ordinal()][inNewStatus.ordinal()];
    }
    /**
     * matrix of state changes - the first dimension is the current state, the second dimension is the proposed new state
     */
    private static final boolean[][] LEGAL_STATE_CHANGES = new boolean[][] { { false, true,  false, false, false, false, false },   // UNSTARTED to
                                                                             { false, false, true,  false, false, false, true  },   // COMPILING to
                                                                             { false, false, false, true,  false, false, true  },   // STARTING to
                                                                             { false, false, false, false, true,  false, false },   // RUNNING to
                                                                             { false, false, false, false, false, true,  true  },   // STOPPING to
                                                                             { true,  false, false, false, true,  false, false },   // STOPPED to
                                                                             { true,  false, false, false, true,  false, false } }; // FAILED to
}
