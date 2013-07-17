package org.marketcetera.module;


import org.marketcetera.util.misc.ClassVersion;

import java.util.Date;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/* $License$ */
/**
 * An abstraction representing a module that can emit data, consume data or
 * create / cancel data flows.
 *
 * A module that is capable of emitting data must
 * implement {@link org.marketcetera.module.DataEmitter}.
 *
 * A module that is capable of receiving data must implement
 * {@link org.marketcetera.module.DataReceiver}.
 *
 * A module that can request data to be supplied to it must implement
 * {@link DataFlowRequester}
 *
 * If the module wants to expose a management interface, it should
 * implement an interface that is annotated with
 * {@link javax.management.MXBean}. The module framework will automatically
 * export that interface as the management interface for the module after
 * it has been initialized.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")  //$NON-NLS-1$
public abstract class Module {

    /**
     * Returns true if this module should be auto-started when its
     * instantiated. If this method returns true, the module
     * is auto-started right after its created. Otherwise, the user
     * has to explicitly request the module framework to start
     * the module.
     *
     * @return true if the module should auto-started on creation.
     */
    public final boolean isAutoStart() {
        return mAutoStart;
    }

    /**
     * Returns the module's URN. 
     *
     * The module URN should have the provider's URN as its parent.
     * If it doesn't, the module creation fails with an error.
     *
     * @return the module's URN.
     */
    public final ModuleURN getURN() {
        return mURN;
    }

    /**
     * Returns the module state.
     *
     * @return the module state.
     */
    public final ModuleState getState() {
        return mState;
    }

    /**
     * The timestamp, when the module was created.
     *
     * @return the timestamp when the module was created.
     */
    public final Date getCreated() {
        return mCreated;
    }

    /**
     * Returns true if the module has been auto-created.
     *
     * A module instance is auto-created, if it doesn't exist, when its URN
     * is specified in a create data flow request and its factory supports
     * {@link ModuleFactory#isAutoInstantiate() auto-instantiation}.
     *
     * @return if the module has been auto-created.
     */
    public final boolean isAutoCreated() {
        return mAutoCreated;
    }

    /**
     * The timestamp when the module was last started. Null,
     * if the module hasn't been started yet.
     *
     * @return the timestamp when the module was started.
     */
    public final Date getStarted() {
        return mStarted;
    }

    /**
     * The timestamp when the module was last stopped. Null,
     * if the module hasn't been stopped yet.
     *
     * @return the timestamp when the module was stopped.
     */
    public final Date getStopped() {
        return mStopped;
    }

    /**
     * The failure message from the last attempt to start
     * the module. Null, if the last module start did not fail.
     *
     * @return the failure message from the last attempt
     * to start the module.
     */
    public final String getLastStartFailure() {
        return mLastStartFailure;
    }

    /**
     * The failure message from the last failed attempt to stop
     * the module. Null, if the last module stop did not fail.
     *
     * @return the failure message from the last failed attempt
     * to stop the module
     */
    public final String getLastStopFailure() {
        return mLastStopFailure;
    }
    /**
     * This method is invoked when the module is being started
     * before it receives or can send data. Module can implement this method to
     * carry out any checks / initialization.
     *
     * This method can be used by the module to verify that all
     * its correctly setup and is configured to start operating.
     *
     * The module framework will start sending this module data,
     * if its a receiver or data requests if this module is an
     * emitter once this method returns successfully.
     *
     * Typically, a module that implements {@link DataFlowRequester} will
     * create data flows from within this method. Do note that any
     * data flows created from within this method are started immediately.
     * If this module is participating in the data flow that its creating,
     * it should make sure that its ready to participate in the
     * data flows as it will get plumbed into the data flow right away.  
     *
     * The module state is set to {@link ModuleState#STARTING} when its
     * executing this method. If the method returns without throwing any
     * exceptions, the module state is set to {@link ModuleState#STARTED}.
     * If the methods throws an exception, the module fails to start and
     * its state is set to {@link ModuleState#START_FAILED}.
     *
     * @throws ModuleException if the module is not ready to
     * start receiving / sending data.
     */
    protected abstract void preStart() throws ModuleException;

    /**
     * This method is invoked when a module is being stopped.
     * Module can implement this method to carry out
     * cleanups or cancel data flow requests in an orderly way.
     *
     * After this method returns, all the data flows that the module
     * initiated and are still running, will be stopped.
     *
     * A module can prevent itself from stopping by throwing an
     * exception from this method.
     *
     * @throws ModuleException if the module is not ready to be
     * stopped. Throwing this exception will cause stop to fail.
     */
    protected abstract void preStop() throws ModuleException;

    /**
     * Creates an instance.
     * The specified module URN should have the provider URN as
     * its parent. ie.
     * <code>inURN.parent().equals(providersURN);</code>
     *
     * Otherwise, module creation fails with an error.
     *
     * @param inURN the module's URN.
     * @param inAutoStart if the module should be automatically started
     * after being created.
     */
    protected Module(ModuleURN inURN, boolean inAutoStart) {
        mURN = inURN;
        mAutoStart = inAutoStart;
    }

    /**
     * Sets if the module has been auto-created. This method is
     * invoked by the module framework. This attribute is set to
     * true when the module instance is auto-created by the framework.
     *
     * @param inAutoCreated if the module has been auto-created
     */
    final void setAutoCreated(boolean inAutoCreated) {
        mAutoCreated = inAutoCreated;
    }

    /**
     * Sets the module state.
     *
     * @param inState the module state.
     */
    final void setState(ModuleState inState) {
        assert inState != null;
        mState = inState;
        switch (inState) {
            case STARTED:
                mStarted = new Date();
                break;
            case STOPPED:
                mStopped = new Date();
                break;
            default:
                //do nothing
        }
    }

    /**
     * Sets the failure message from the last attempt to start
     * the module.
     *
     * @param inLastStartFailure failure message from the last
     * attempt to start the module. null, if the last attempt to
     * start the module succeeded.
     */
    final void setLastStartFailure(String inLastStartFailure) {
        mLastStartFailure = inLastStartFailure;
    }

    /**
     * Sets the failure message from the last failed attempt to
     * stop the module.
     *
     * @param inLastStopFailure failure message from the last
     * failed attempt to stop the module. null, if the last
     * attempt to stop the module succeeded.
     */
    final void setLastStopFailure(String inLastStopFailure) {
        mLastStopFailure = inLastStopFailure;
    }

    /**
     * Returns the lock that should be used for serializing module
     * operations.
     *
     * @return the lock to be used for serializing module operations.
     */
    final ReadWriteLock getLock() {
        return mLock;
    }

    /**
     * Returns module info describing the current state of the module.
     *
     * @param inInitiatedFlows the set of IDs for the data flows that this
     * module has initiated
     * @param inParticipatingFlows the set of IDs for the data flows that this
     * module is participating in.
     *
     * @return the module info.
     */
    final ModuleInfo getModuleInfo(DataFlowID[] inInitiatedFlows,
                                   DataFlowID[] inParticipatingFlows) {
        return new ModuleInfo(getURN(), getState(), inInitiatedFlows,
                inParticipatingFlows, getCreated(), getStarted(),
                getStopped(), isAutoStart(), isAutoCreated(),
                this instanceof DataReceiver, this instanceof DataEmitter,
                this instanceof DataFlowRequester, getLastStartFailure(),
                getLastStopFailure(), mLock.getReadLockCount(),
                mLock.isWriteLocked(), mLock.getQueueLength());
    }


    private final ReentrantReadWriteLock mLock = new ReentrantReadWriteLock();
    private volatile boolean mAutoCreated = false;
    private volatile ModuleState mState = ModuleState.CREATED;
    private volatile String mLastStartFailure;
    private volatile String mLastStopFailure;
    private volatile Date mStarted = null;
    private volatile Date mStopped = null;
    private final Date mCreated = new Date();
    private final ModuleURN mURN;
    private final boolean mAutoStart;
}
