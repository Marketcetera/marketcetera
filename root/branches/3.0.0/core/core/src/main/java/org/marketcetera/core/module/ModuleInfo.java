package org.marketcetera.core.module;

import java.beans.ConstructorProperties;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/* $License$ */
/**
 * Provided detailed information on a module instance.
 *
 * @author anshul@marketcetera.com
 * @version $Id: ModuleInfo.java 16063 2012-01-31 18:21:55Z colin $
 * @since 1.0.0
 */
@XmlAccessorType(XmlAccessType.FIELD)
public final class ModuleInfo implements Serializable {

    /**
     * Returns the module instance URN.
     *
     * @return the module instance URN.
     */
    public ModuleURN getURN() {
        return mURN;
    }

    /**
     * The module's state.
     *
     * @return the module state
     */
    public ModuleState getState() {
        return mState;
    }

    /**
     * List of data flows that this module has initiated.
     *
     * @return the list of data flows that this module has initiated.
     */
    public DataFlowID[] getInitiatedDataFlows() {
        return mInitiatedDataFlows;
    }

    /**
     * List of data flows that the module is participating in.
     *
     * @return list of data flows that this module is participating in.
     */
    public DataFlowID[] getParticipatingDataFlows() {
        return mParticipatingDataFlows;
    }

    /**
     * The time stamp when the module was created.
     *
     * @return the time stamp when the module was created
     */
    public Date getCreated() {
        return mCreated;
    }

    /**
     * The time stamp when the module was last started.
     *
     * @return the time stamp when the module was last started.
     */
    public Date getStarted() {
        return mStarted;
    }

    /**
     * The time stamp when the module was last stopped.
     *
     * @return the timestamp when the module was last stopped.
     */
    public Date getStopped() {
        return mStopped;
    }

    /**
     * If the module is automatically started.
     *
     * @return true, if the module is auto started.
     */
    public boolean isAutostart() {
        return mAutostart;
    }

    /**
     * If the module instance was auto-created
     *
     * @return if the module instance was auto-created. 
     */
    public boolean isAutocreated() {
        return mAutocreated;
    }

    /**
     * If the module can receive data.
     *
     * @return if the module can receive data.
     */
    public boolean isReceiver() {
        return mReceiver;
    }

    /**
     * If the module can emit data.
     *
     * @return if the module can emit data
     */
    public boolean isEmitter() {
        return mEmitter;
    }

    /**
     * If the module can request data flows.
     *
     * @return if the module can request data flows
     */
    public boolean isFlowRequester() {
        return mFlowRequester;
    }

    /**
     * Returns a non-null string, detailing the last failure
     * when starting the module, if the module is currently
     * not started.
     * If the module is started or if the module didn't fail
     * to start the last time it was started, the return value
     * is null.
     *
     * @return the last failure when starting the module.
     */
    public String getLastStartFailure() {
        return mLastStartFailure;
    }

    /**
     * Returns a non-null string, detailing the last failure
     * when stopping the module, if the module is currently
     * not stopped.
     * If the module is stopped or if the module didn't fail
     * to stop the last time it was stopped, the return value
     * is null.
     *
     * @return the last failure when stopping the module.
     */

    public String getLastStopFailure() {
        return mLastStopFailure;
    }

    /**
     * The number of read locks acquired on the module.
     *
     * @return the number of read locks.
     */
    public int getReadLockCount() {
        return mReadLockCount;
    }

    /**
     * Returns true, if the module is write locked.
     *
     * @return true if the module is write locked.
     */
    public boolean isWriteLocked() {
        return mWriteLocked;
    }

    /**
     * Returns the number of threads waiting to acquire either
     * the read or the write lock.
     *
     * @return the lock queue length.
     */
    public int getLockQueueLength() {
        return mLockQueueLength;
    }

    /**
     * Creates an instance
     *
     * @param inURN the module instance URN.
     * @param inState the current state of the module
     * @param inInitiatedDataFlows the set of data flows that the
     *  module initiated.
     * @param inParticipatingDataFlows the set of data flows that
     *  the module is participating in.
     * @param inCreated the time stamp when this module was created.
     * @param inStarted the time stamp when this module was started.
     * @param inStopped the time stamp when this module was stopped.
     * @param inAutostart if the module can be auto-started.
     * @param inAutocreated if the module was auto-created
     * @param inReceiver if the module can receive data
     * @param inEmitter if the module can emit data
     * @param inFlowRequester if the module can request data flows
     * @param inLastStartFailure the failure message, if the last attempt
     *  to start the module failed.
     * @param inLastStopFailure the failure message, if the last attempt
     * @param inReadLockCount the number of read locks acquired on the module.
     * @param inWriteLocked if the module is write locked.
     * @param inLockQueueLength the lock queue length.
     */
    @ConstructorProperties({
            "URN",                        //$NON-NLS-1$
            "state",                      //$NON-NLS-1$
            "initiatedDataFlows",         //$NON-NLS-1$
            "participatingDataFlows",     //$NON-NLS-1$
            "created",                    //$NON-NLS-1$
            "started",                    //$NON-NLS-1$
            "stopped",                    //$NON-NLS-1$
            "autostart",                  //$NON-NLS-1$
            "autocreated",                //$NON-NLS-1$
            "receiver",                   //$NON-NLS-1$
            "emitter",                    //$NON-NLS-1$
            "flowRequester",              //$NON-NLS-1$
            "lastStartFailure",           //$NON-NLS-1$
            "lastStopFailure",            //$NON-NLS-1$
            "readLockCount",              //$NON-NLS-1$
            "writeLocked",                //$NON-NLS-1$
            "lockQueueLength"             //$NON-NLS-1$
            })
    public ModuleInfo(ModuleURN inURN,
                      ModuleState inState,
                      DataFlowID[] inInitiatedDataFlows,
                      DataFlowID[] inParticipatingDataFlows,
                      Date inCreated,
                      Date inStarted,
                      Date inStopped,
                      boolean inAutostart,
                      boolean inAutocreated,
                      boolean inReceiver,
                      boolean inEmitter,
                      boolean inFlowRequester,
                      String inLastStartFailure,
                      String inLastStopFailure,
                      int inReadLockCount,
                      boolean inWriteLocked,
                      int inLockQueueLength) {
        mURN = inURN;
        mState = inState;
        mInitiatedDataFlows = inInitiatedDataFlows;
        mParticipatingDataFlows = inParticipatingDataFlows;
        mCreated = inCreated;
        mStarted = inStarted;
        mStopped = inStopped;
        mAutostart = inAutostart;
        mAutocreated = inAutocreated;
        mReceiver = inReceiver;
        mEmitter = inEmitter;
        mFlowRequester = inFlowRequester;
        mLastStartFailure = inLastStartFailure;
        mLastStopFailure = inLastStopFailure;
        mReadLockCount = inReadLockCount;
        mWriteLocked = inWriteLocked;
        mLockQueueLength = inLockQueueLength;
    }

    /**
     * This constructor has been added for JAXB and is not meant to be
     * used.
     */
    @SuppressWarnings("unused")
    private ModuleInfo() {
        this(null, null, null, null, null, null, null, false, false,
                false, false, false, null, null, -1, false, -1);
    }

    @Override
    public String toString() {
        return "ModuleInfo{" +  //$NON-NLS-1$
                "mURN=" + mURN +  //$NON-NLS-1$
                ", mState=" + mState +  //$NON-NLS-1$
                ", mInitiatedDataFlows=" + (mInitiatedDataFlows == null ? null : Arrays.asList(mInitiatedDataFlows)) +  //$NON-NLS-1$
                ", mParticipatingDataFlows=" + (mParticipatingDataFlows == null ? null : Arrays.asList(mParticipatingDataFlows)) +  //$NON-NLS-1$
                ", mCreated=" + mCreated +  //$NON-NLS-1$
                ", mStarted=" + mStarted +  //$NON-NLS-1$
                ", mStopped=" + mStopped +  //$NON-NLS-1$
                ", mAutostart=" + mAutostart +  //$NON-NLS-1$
                ", mAutocreated=" + mAutocreated +  //$NON-NLS-1$
                ", mReceiver=" + mReceiver +  //$NON-NLS-1$
                ", mEmitter=" + mEmitter +  //$NON-NLS-1$
                ", mFlowRequester=" + mFlowRequester +  //$NON-NLS-1$
                ", mLastStartFailure='" + mLastStartFailure + '\'' +  //$NON-NLS-1$ $NON-NLS-2$
                ", mLastStopFailure='" + mLastStopFailure + '\'' +  //$NON-NLS-1$ $NON-NLS-2$
                ", mReadLockCount=" + mReadLockCount +  //$NON-NLS-1$
                ", mWriteLocked=" + mWriteLocked +  //$NON-NLS-1$
                ", mLockQueueLength=" + mLockQueueLength +  //$NON-NLS-1$
                '}';  //$NON-NLS-1$
    }

    private final ModuleURN mURN;
    private final ModuleState mState;
    private final DataFlowID[] mInitiatedDataFlows;
    private final DataFlowID[] mParticipatingDataFlows;
    private final Date mCreated;
    private final Date mStarted;
    private final Date mStopped;
    private final boolean mAutostart;
    private final boolean mAutocreated;
    private final boolean mReceiver;
    private final boolean mEmitter;
    private final boolean mFlowRequester;
    private final String mLastStartFailure;
    private final String mLastStopFailure;
    private final int mReadLockCount;
    private final boolean mWriteLocked;
    private final int mLockQueueLength;
    private static final long serialVersionUID = 296591521121503992L;
}
