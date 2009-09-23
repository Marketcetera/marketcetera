package org.marketcetera.module;

import org.marketcetera.util.misc.ClassVersion;

import java.util.EnumSet;
import java.util.Set;
import java.util.Collections;

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
 *        |           |  v   \                 delete()
 *        |           |  {@link #START_FAILED}--------->.
 *        |           |                                 |
 *        |           v                                 |
 *        |         {@link #STARTED}                    |
 *        |           |                                 |
 *        |           |                                 |
 * start()|           | stop()                          |
 *        |           |                                 |
 *        |           v                                 |
 *        |         {@link #STOPPING}                   |
 *        |           |\   ^                            |
 *        |           | \   \ stop()                    |
 *        |           |  v   \                delete()  |
 *        |           |  {@link #STOP_FAILED}---------->|
 *        |           |                                 |
 *        \           v                                 |
 *         '------- {@link #STOPPED}                    |
 *                    |                                 |
 *                    | delete()                        |
 *                    |<--------------------------------'
 *                    v
 *                    O
 *
 * </pre>
 * <p>
 * The following table specifies the module states for which
 * various module framework operations can be invoked on a module. If
 * a module operation is invoked while it's not in one of the states for
 * that operation in the table below, the operation will fail.
 * <p>
 * <table border="1" cellspacing="0">
 * <tr><th>Module Operation</th><th>Module State</th></tr>
 * <tr><td>delete</td><td>{@link #CREATED}, {@link #START_FAILED}, {@link #STOPPED}</td>
 * <tr><td>stop</td><td>{@link #STARTED}, {@link #STOP_FAILED}</td>
 * <tr><td>start</td><td>{@link #CREATED}, {@link #START_FAILED}, {@link #STOPPED}</td>
 * <tr><td>request data flow</td><td>{@link #STARTING}, {@link #STARTED}, {@link #STOP_FAILED}</td>
 * <tr><td>cancel data flow</td><td>{@link #STARTED}, {@link #STOPPING}, {@link #STOP_FAILED}</td>
 * <tr><td>participate data flow</td><td>{@link #STARTED}, {@link #STOP_FAILED}</td>
 * </table>
 * <p>
 * <b>Note:</b>
 * <ul>
 * <li><i>request / cancel data flow</i> operations apply only to modules
 * that implement {@link DataFlowRequester}.</li>
 * <li><i>participate data flow</i> operation relates to a create data flow request
 * operation that contains a reference to the module in question.</li>
 * </ul> 
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")  //$NON-NLS-1$
public enum ModuleState {
    /**
     * Module has been created. It hasn't been started or stopped yet.
     */
    CREATED,
    /**
     * The module is started
     */
    STARTED,
    /**
     * The module is in the process of being started.
     * The module is in this state when its
     * {@link org.marketcetera.module.Module#preStart()}
     * is being invoked.
     */
    STARTING,
    /**
     * Last attempt to start the module failed.
     */
    START_FAILED,
    /**
     * The module is in the process of stopping.
     * The module is in this state when its
     * {@link org.marketcetera.module.Module#preStop()}
     * method is being invoked.
     */
    STOPPING,
    /**
     * Last attempt to stop the module failed.
     */
    STOP_FAILED,
    /**
     * The module is stopped.
     */
    STOPPED;

    /**
     * If the module is started.
     *
     * @return true, if the module is started, false otherwise.
     */
    public boolean isStarted() {
        return STARTED_STATES.contains(this);
    }

    /**
     * If the module can start data flows in this state.
     *
     * @return if the module can start data flows
     */
    boolean canParticipateFlows() {
        return PARTICIPATE_FLOW_STATES.contains(this);
    }

    /**
     * Returns true if a module can request data flows in this state.
     *
     * @return if the module can request data flows.
     */
    boolean canRequestFlows() {
        return REQUEST_FLOW_STATES.contains(this);
    }

    /**
     * If the module can stop data flows in this state.
     *
     * @return if the module can stop data flows.
     */
    boolean canCancelFlows() {
        return CANCEL_FLOW_STATES.contains(this);
    }

    /**
     * Returns true if the module can be deleted in this state.
     *
     * @return if the module can be deleted.
     */
    boolean canBeDeleted() {
        return DELETABLE_STATES.contains(this);
    }

    /**
     * Returns true if the module can be stopped in this state.
     *
     * @return if the module can be stopped.
     */
    boolean canBeStopped() {
        return STOPPABLE_STATES.contains(this);
    }
    /**
     * Returns true if the module can be started in this state.
     *
     * @return if the module can be started.
     */
    boolean canBeStarted() {
        return STARTABLE_STATES.contains(this);
    }

    static final Set<ModuleState> DELETABLE_STATES;
    static final Set<ModuleState> STOPPABLE_STATES;
    static final Set<ModuleState> STARTABLE_STATES;
    static final Set<ModuleState> STARTED_STATES;
    static final Set<ModuleState> REQUEST_FLOW_STATES;
    static final Set<ModuleState> PARTICIPATE_FLOW_STATES;
    static final Set<ModuleState> CANCEL_FLOW_STATES;
    static{
        /*
         * NOTE: Make sure to update the class javadoc above
         * when updating any of the collections below.
         */
        DELETABLE_STATES = Collections.unmodifiableSet(
                EnumSet.of(CREATED, START_FAILED, STOPPED));
        STOPPABLE_STATES = Collections.unmodifiableSet(
                EnumSet.of(STARTED, STOP_FAILED));
        STARTABLE_STATES = Collections.unmodifiableSet(
                EnumSet.of(CREATED, START_FAILED, STOPPED));
        STARTED_STATES = Collections.unmodifiableSet(
                EnumSet.of(STARTED, STOP_FAILED));
        REQUEST_FLOW_STATES = Collections.unmodifiableSet(
                EnumSet.of(STARTING, STARTED, STOP_FAILED));
        PARTICIPATE_FLOW_STATES = Collections.unmodifiableSet(
                EnumSet.of(STARTED, STOP_FAILED));
        CANCEL_FLOW_STATES = Collections.unmodifiableSet(
                EnumSet.of(STARTED, STOPPING, STOP_FAILED));
    }
}
