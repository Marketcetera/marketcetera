package org.marketcetera.module;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */
/**
 * Enumerates various states of a module.
 *
 * Here's a state transition diagram
 * for the different states the a Module goes through
 * <pre>
 *
 *                    o
 *                    |
 *                    | create()
 *                    v
 *                  {@link #CREATED}
 *                    |
 *                    |
 *                    | start()
 *                    |
 *                    v
 *         ,------> {@link #STARTING}
 *        /           |\   ^
 *        |           | \   \ start()
 *        |           |  v   \
 *        |           |  {@link #START_FAILED}
 *        |           |
 *        |           v
 *        |         {@link #STARTED}
 *        |           |
 *        |           |
 * start()|           | stop()
 *        |           |
 *        |           v
 *        |         {@link #STOPPING}
 *        |           |\   ^
 *        |           | \   \ stop()
 *        |           |  v   \
 *        |           |  {@link #STOP_FAILED}
 *        |           |
 *        \           v
 *         '------- {@link #STOPPED}
 *                    |
 *                    |
 *                    | delete()
 *                    v
 *                    O
 *
 *
 * </pre>
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$")  //$NON-NLS-1$
public enum ModuleState {
    /**
     * Module has been created. It hasn't been started or stopped yet.
     */
    CREATED(false, false, false),
    /**
     * The module is started
     */
    STARTED(true, true, true),
    /**
     * The module is in the process of being started.
     * The module is in this state when its
     * {@link org.marketcetera.module.Module#preStart()}
     * is being invoked.
     */
    STARTING(false, true, false),
    /**
     * Last attempt to start the module failed.
     */
    START_FAILED(false, false, false),
    /**
     * The module is in the process of stopping.
     * The module is in this state when its
     * {@link org.marketcetera.module.Module#preStop()}
     * method is being invoked.
     */
    STOPPING(false, false, true),
    /**
     * Last attempt to stop the module failed.
     */
    STOP_FAILED(true, true, true),
    /**
     * The module is stopped.
     */
    STOPPED(false, false, false);

    /**
     * If the module is started.
     *
     * @return true, if the module is started, false otherwise.
     */
    public boolean isStarted() {
        return mStarted;
    }

    /**
     * If the module can start data flows in this state.
     *
     * @return if the module can start data flows
     */
    public boolean canParticipateFlows() {
        return mCanParticipateFlows;
    }

    /**
     * If the module can stop data flows in this state.
     *
     * @return if the module can stop data flows.
     */
    public boolean canStopFlows() {
        return mCanStopFlows;
    }

    /**
     * Creates an instance.
     *
     * @param inInStarted if the module is considered started in this state
     * @param inCanParticipateFlows if the module can participate in data flows
     * in this state.
     * @param inCanStopFlows if the module can stop data flows in this state
     */
    private ModuleState(boolean inInStarted,
                        boolean inCanParticipateFlows,
                        boolean inCanStopFlows) {
        mStarted = inInStarted;
        mCanParticipateFlows = inCanParticipateFlows;
        mCanStopFlows = inCanStopFlows;
    }

    private final boolean mStarted;
    private final boolean mCanParticipateFlows;
    private final boolean mCanStopFlows;
}
