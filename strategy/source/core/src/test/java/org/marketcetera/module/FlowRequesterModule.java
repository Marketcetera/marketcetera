package org.marketcetera.module;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */
/**
 * A processor module that is capable of creating / canceling data flows.
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$")
public class FlowRequesterModule extends ProcessorModule
        implements DataFlowRequester {
    public FlowRequesterModule(ModuleURN inModuleURN) {
        super(inModuleURN, false);
    }

    @Override
    protected void preStart() throws ModuleException {
        super.preStart();
        createFlow();
        if(mFailPreStart) {
            throw new ModuleException(
                    TestMessages.TEST_START_STOP_FAILURE);
        }
    }

    void createFlow() throws ModuleException {
        if(mRequests != null) {
            if(mInvokeDefault) {
                setFlowID(mSupport.createDataFlow(mRequests));
            } else {
                setFlowID(mSupport.createDataFlow(mRequests, mAppendSink));
            }
        }
    }

    @Override
    public void preStop() throws ModuleException {
        super.preStop();
        if (!mSkipCancel) {
            cancelFlow();
        }
    }

    void cancelFlow() throws ModuleException {
        if(mFlowID != null) {
            mSupport.cancel(mFlowID);
            setFlowID(null);
        }
    }

    @Override
    public void setFlowSupport(DataFlowSupport support) {
        mSupport = support;
    }

    /**
     * If the API to create that appends sink by default should be invoked.
     *
     * @param inInvokeDefault if the createDataFlow() API that appends
     * the sink module by default should be invoked 
     */
    public void setInvokeDefault(boolean inInvokeDefault) {
        mInvokeDefault = inInvokeDefault;
    }

    /**
     * The value of the appendSink flag when invoking the API that
     * doesn't append the sink module by default.
     *
     * @param inAppendSink the appendSink flag.
     */
    public void setAppendSink(boolean inAppendSink) {
        mAppendSink = inAppendSink;
    }

    /**
     * The data requests that should be used by this module when
     * creating data flows.
     *
     * @param inRequests the set of requests to use when creating
     * data flows.
     */
    public void setRequests(DataRequest[] inRequests) {
        mRequests = inRequests;
    }

    /**
     * The data flow ID.
     *
     * @param inFlowID the data flowID.
     */
    public void setFlowID(DataFlowID inFlowID) {
        mFlowID = inFlowID;
    }

    /**
     * The data flow ID.
     *
     * @return the data flow ID.
     */
    public DataFlowID getFlowID() {
        return mFlowID;
    }

    /**
     * If the module should skip canceling the data flow when its stopped.
     *
     * @return if the module should skip canceling the data flow when
     * its stopped.
     */
    public boolean isSkipCancel() {
        return mSkipCancel;
    }

    /**
     * If the module should skip canceling the data flow when its stopped.
     *
     * @param inSkipCancel if the module should skip canceling the data
     * flow when its stopped.
     */
    public void setSkipCancel(boolean inSkipCancel) {
        mSkipCancel = inSkipCancel;
    }

    /**
     * If the module should throw an exception from the preStart method.
     *
     * @param inFailPreStart if the module should throw an exception from
     * the preStart method.
     */
    public void setFailPreStart(boolean inFailPreStart) {
        mFailPreStart = inFailPreStart;
    }

    private DataFlowID mFlowID;
    private boolean mInvokeDefault;
    private boolean mAppendSink;
    private boolean mSkipCancel = false;
    private boolean mFailPreStart = false;
    private DataRequest[] mRequests;
    private DataFlowSupport mSupport;
}
