package org.marketcetera.module;

import org.marketcetera.util.misc.ClassVersion;

import java.util.concurrent.locks.Lock;
import java.util.Map;
import java.util.Hashtable;

/* $License$ */
/**
 * A module for testing locking within module framework when carrying
 * out various module framework operations. 
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.1.0
 */
@ClassVersion("$Id$")
public class ConcurrentTestModule extends Module
        implements DataReceiver, DataEmitter, DataFlowRequester,
        ConcurrentTestModuleMXBean {
    ConcurrentTestModule(ModuleURN inURN, boolean inAutoStart) {
        super(inURN, inAutoStart);
        sModules.put(inURN, this);
    }

    @Override
    protected void preStart() throws ModuleException {
        //Create a data flow
        mFlowID = mSupport.createDataFlow(new DataRequest[]{
                new DataRequest(getURN())
        });
        DataRequest[] requests = getFlowRequests();
        if(requests != null) {
            mOtherFlowID = mSupport.createDataFlow(requests);
        }
        //lock after create data flows.
        lockIfNotNull(getPreStartLock());
        if(isPreStartFail()) {
            throw new ModuleException(TestMessages.FAILURE);
        }
    }

    @Override
    protected void preStop() throws ModuleException {
        lockIfNotNull(getPreStopLock());
        if(isPreStopFail()) {
            throw new ModuleException(TestMessages.FAILURE);
        }
        if(mOtherFlowID != null) {
            mSupport.cancel(mOtherFlowID);
        }
        mSupport.cancel(mFlowID);
    }

    @Override
    public void receiveData(DataFlowID inFlowID, Object inData)
            throws ReceiveDataException {
        //do nothing
    }

    @Override
    public void requestData(DataRequest inRequest, DataEmitterSupport inSupport)
            throws RequestDataException {
        lockIfNotNull(getRequestDataLock());
    }

    @Override
    public void cancel(DataFlowID inFlowID, RequestID inRequestID) {
        lockIfNotNull(getCancelLock());
    }

    @Override
    public void setFlowSupport(DataFlowSupport inSupport) {
        mSupport = inSupport;
        lockIfNotNull(getSetFlowSupportLock());
    }

    @Override
    public void setValue(String inValue) {
        lockIfNotNull(getSetValueLock());
    }

    /**
     * Clears all the testing helper state associated with the module
     * instances.
     */
    static void clear() {
        sHelperTable.clear();
        sModules.clear();
    }

    /**
     * Returns  the testing helper for the module instance with the
     * supplied URN.
     *
     * @param inURN the URN of the module instance whose helper is needed.
     *
     * @return the helper for the module instance with the supplied URN.
     */
    static Helper helper(ModuleURN inURN) {
        Helper helper = sHelperTable.get(inURN);
        if (helper == null) {
            helper = new Helper();
            sHelperTable.put(inURN, helper);
        }
        return helper;
    }

    /**
     * The module instance, given the module URN.
     *
     * @param inURN the module instance URN.
     *
     * @return the module instance having the specified URN, null if a
     * module with the supplied URN is not found.
     */
    static ConcurrentTestModule getModule(ModuleURN inURN) {
        return sModules.get(inURN);
    }

    /**
     * Creates a data flow using the {@link DataFlowSupport} instance supplied
     * to the module.
     *
     * @param inRequests the data requests for setting up the data flow.
     *
     * @return the ID of the created flow.
     *
     * @throws ModuleException if there were errors setting up the flow.
     */
    DataFlowID createFlow(DataRequest[] inRequests) throws ModuleException {
        return mSupport.createDataFlow(inRequests);
    }

    /**
     * The flowID of the flow initiated by this module.
     * 
     * @return the flowID of the flow initiated by this module.
     */
    DataFlowID getFlowID() {
        return mFlowID;
    }

    /**
     * Cancels the data flow using the {@link DataFlowSupport} instance
     * supplied to the module.
     *
     * @param inFlowID the flowID of the flow that needs to be cancelled.
     *
     * @throws ModuleException if there were errors setting up the flow.
     */
    void cancelFlow(DataFlowID inFlowID) throws ModuleException {
        mSupport.cancel(inFlowID);
    }
    private Lock getSetValueLock() {
        Helper helper = getHelper(getURN());
        return helper == null? null: helper.mSetValueLock;
    }

    private Lock getPreStartLock() {
        Helper helper = getHelper(getURN());
        return helper == null? null: helper.mPreStartLock;
    }

    private boolean isPreStartFail() {
        Helper helper = getHelper(getURN());
        return helper == null? false: helper.mPreStartFail;
    }

    private Lock getPreStopLock() {
        Helper helper = getHelper(getURN());
        return helper == null? null: helper.mPreStopLock;
    }
    private boolean isPreStopFail() {
        Helper helper = getHelper(getURN());
        return helper == null? false: helper.mPreStopFail;
    }

    private Lock getRequestDataLock() {
        Helper helper = getHelper(getURN());
        return helper == null? null: helper.mRequestDataLock;
    }

    private Lock getCancelLock() {
        Helper helper = getHelper(getURN());
        return helper == null? null: helper.mCancelLock;
    }

    private Lock getSetFlowSupportLock() {
        Helper helper = getHelper(getURN());
        return helper == null? null: helper.mSetFlowSupportLock;
    }
    private DataRequest[] getFlowRequests() {
        Helper helper = getHelper(getURN());
        return helper == null? null: helper.mFlowRequests;
    }
    private static void lockIfNotNull(Lock inLock) {
        if(inLock != null) {
            inLock.lock();
        }
    }
    private static Helper getHelper(ModuleURN inURN) {
        return sHelperTable.get(inURN);
    }
    private static final Map<ModuleURN, Helper> sHelperTable =
            new Hashtable<ModuleURN, Helper>();
    private static final Map<ModuleURN, ConcurrentTestModule> sModules =
            new Hashtable<ModuleURN, ConcurrentTestModule>();
    private volatile DataFlowSupport mSupport;
    private DataFlowID mFlowID = null;
    private DataFlowID mOtherFlowID = null;

    /**
     * Helper class for testing.
     */
    static class Helper {
        /**
         * Sets the lock that should be acquired from within
         * {@link ConcurrentTestModule#setValue(String)}.
         *
         * @param inSetValueLock the lock instance.
         *
         * @return the helper instance.
         */
        public Helper setSetValueLock(Lock inSetValueLock) {
            mSetValueLock = inSetValueLock;
            return this;
        }

        /**
         * Sets the lock that should be acquired from within
         * {@link ConcurrentTestModule#preStart()}.
         *
         * @param inPreStartLock the lock instance.
         *
         * @return the helper instance.
         */
        public Helper setPreStartLock(Lock inPreStartLock) {
            mPreStartLock = inPreStartLock;
            return this;
        }

        /**
         * Sets if preStart() should fail.
         *
         * @param inPreStartFail if preStart() should fail
         *
         * @return the helper instance.
         */
        public Helper setPreStartFail(boolean inPreStartFail) {
            mPreStartFail = inPreStartFail;
            return this;
        }

        /**
         * Sets the lock that should be acquired from within
         * {@link ConcurrentTestModule#preStop()}.
         *
         * @param inPreStopLock the lock instance.
         *
         * @return the helper instance.
         */
        public Helper setPreStopLock(Lock inPreStopLock) {
            mPreStopLock = inPreStopLock;
            return this;
        }

        /**
         * Sets if preStop() should fail.
         *
         * @param inPreStopFail if preStop() should fail.
         *
         * @return the helper instance.
         */
        public Helper setPreStopFail(boolean inPreStopFail) {
            mPreStopFail = inPreStopFail;
            return this;
        }

        /**
         *
         * @param inRequestDataLock the lock instance.
         *
         * @return the helper instance.
         */
        public Helper setRequestDataLock(Lock inRequestDataLock) {
            mRequestDataLock = inRequestDataLock;
            return this;
        }

        /**
         * Sets the lock that should be acquired from within
         * {@link ConcurrentTestModule#cancel(DataFlowID, RequestID)}.
         *
         * @param inCancelLock the lock instance.
         *
         * @return the helper instance.
         */
        public Helper setCancelLock(Lock inCancelLock) {
            mCancelLock = inCancelLock;
            return this;
        }

        /**
         * Sets the lock that should be acquired from within
         * {@link ConcurrentTestModule#setFlowSupport(DataFlowSupport)}.
         *
         * @param inSetFlowSupportLock the lock instance.
         *
         * @return the helper instance.
         */
        public Helper setSetFlowSupportLock(Lock inSetFlowSupportLock) {
            mSetFlowSupportLock = inSetFlowSupportLock;
            return this;
        }

        /**
         * Sets flow requests that should be used to set up data flow
         * from within preStart().
         *
         * @param inFlowRequests flow requests.
         *
         * @return the helper instance.
         */
        public Helper setFlowRequests(DataRequest[] inFlowRequests) {
            mFlowRequests = inFlowRequests;
            return this;
        }
        private volatile Lock mSetValueLock = null;
        private volatile Lock mPreStartLock = null;
        private volatile boolean mPreStartFail = false;
        private volatile Lock mPreStopLock = null;
        private volatile boolean mPreStopFail = false;
        private volatile Lock mRequestDataLock = null;
        private volatile Lock mCancelLock = null;
        private volatile Lock mSetFlowSupportLock = null;
        private volatile DataRequest[] mFlowRequests = null;

    }
}
