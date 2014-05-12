package org.marketcetera.module;

import org.marketcetera.util.misc.ClassVersion;

import java.util.List;
import java.util.LinkedList;

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

    @Override
    public void preStop() throws ModuleException {
        super.preStop();
        if (!mSkipCancel) {
            cancelFlow();
        }
    }

    @Override
    public void setFlowSupport(DataFlowSupport support) {
        mSupport = support;
    }

    /**
     * Overridden to test failures when invoking {@link DataFlowSupport}
     * APIs from within this method.
     */
    @Override
    public void requestData(DataRequest inRequest,
                            DataEmitterSupport inSupport)
            throws RequestDataException {
        try {
            if (mNestDataFlowInRequest) {
                doNestedRequestOrCancel();
            }
        } catch (ModuleException e) {
            throw new RequestDataException(e);
        }
        super.requestData(inRequest, inSupport);
    }

    /**
     * Overridden to test failures when invoking {@link DataFlowSupport}
     * APIs from within this method.
     */
    @Override
    public void cancel(DataFlowID inFlowID, RequestID inRequestID) {
        super.cancel(inFlowID, inRequestID);
        if(mNestDataFlowInCancel) {
            try {
                doNestedRequestOrCancel();
            } catch (ModuleException e) {
                mNestedCancelFailure = e;
            }
        }
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
     * The data request that should be used by this module when
     * creating data flows.
     *
     * @param inRequests the set of request used to use when creating
     * data flow.
     */
    public void addRequests(DataRequest[] inRequests) {
        mDataRequests.add(inRequests);
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
     * The data flow ID for the request supplied via
     * {@link #setRequests(DataRequest[])}.
     *
     * @return the data flow ID.
     */
    public DataFlowID getFlowID() {
        return mFlowID;
    }

    /**
     * The data flow IDs corresponding to the requests added via
     * {@link #addRequests(DataRequest[])}.
     *
     * @return the flow IDs for the data requests created.
     */
    public DataFlowID[] getFlowIDs() {
        return mFlowIDs.toArray(new DataFlowID[mFlowIDs.size()]);
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

    /**
     * If the module should attempt to create a data flow from within
     * the requestData() or cancel() method.
     *
     * @param inNestedCreateDataFlow if the module should attempt to
     * create a nested data flow.
     */
    public void setNestedCreateDataFlow(boolean inNestedCreateDataFlow) {
        mNestedCreateDataFlow = inNestedCreateDataFlow;
    }

    /**
     * If the module should attempt to cancel a data flow from within
     * the requestData() or cancel() method.
     *
     * @param inNestedCancelDataFlow if the module should attempt to
     * cancel a data flow when canceling another data flow.
     */
    public void setNestedCancelDataFlow(boolean inNestedCancelDataFlow) {
        mNestedCancelDataFlow = inNestedCancelDataFlow;
    }

    /**
     * if the module should invoke nested flow requests from within
     * requestData().
     *
     * @param inNestDataFlowInRequest if the module should invoke nested
     * flow requests from within requestData().
     */
    public void setNestDataFlowInRequest(boolean inNestDataFlowInRequest) {
        mNestDataFlowInRequest = inNestDataFlowInRequest;
    }

    /**
     * if the module should invoke nested flow requests from within cancel().
     *
     * @param inNestDataFlowInCancel if the module should invoke nested
     * flow requests from within cancel().
     */
    public void setNestDataFlowInCancel(boolean inNestDataFlowInCancel) {
        mNestDataFlowInCancel = inNestDataFlowInCancel;
    }

    /**
     * Gets the failure observed when invoking data flow APIs from within
     * cancel().
     *
     * @return the failure observed when invoking data flow APIs from within
     * cancel().
     */
    public ModuleException getNestedCancelFailure() {
        return mNestedCancelFailure;
    }

    /**
     * Clears the failure observed when invoking data flow APIs from within.
     * cancel().
     */
    public void resetNestedCancelFailure() {
        mNestedCancelFailure = null;
    }

    void createFlow() throws ModuleException {
        if(mRequests != null) {
            if(mInvokeDefault) {
                setFlowID(mSupport.createDataFlow(mRequests));
            } else {
                setFlowID(mSupport.createDataFlow(mRequests, mAppendSink));
            }
        }
        if(mDataRequests != null) {
            mFlowIDs.clear();
            for(DataRequest[] request: mDataRequests) {
                if(mInvokeDefault) {
                    mFlowIDs.add(mSupport.createDataFlow(request));
                } else {
                    mFlowIDs.add(mSupport.createDataFlow(request, mAppendSink));
                }
            }
        }
    }

    void cancelFlow() throws ModuleException {
        if(mFlowID != null) {
            mSupport.cancel(mFlowID);
            setFlowID(null);
        }
        if(!mFlowIDs.isEmpty()) {
            for(DataFlowID id: mFlowIDs) {
                mSupport.cancel(id);
            }
            mFlowIDs.clear();
        }
    }

    private void doNestedRequestOrCancel() throws ModuleException {
        if (mNestedCreateDataFlow) {
            if (mInvokeDefault) {
                mSupport.createDataFlow(null);
            } else {
                mSupport.createDataFlow(null, true);
            }
        }
        if (mNestedCancelDataFlow) {
            mSupport.cancel(null);
        }
    }

    private DataFlowID mFlowID;
    private List<DataFlowID> mFlowIDs = new LinkedList<DataFlowID>();
    private boolean mInvokeDefault;
    private boolean mAppendSink;
    private boolean mSkipCancel = false;
    private boolean mFailPreStart = false;
    private DataRequest[] mRequests;
    private List<DataRequest[]> mDataRequests = new LinkedList<DataRequest[]>();
    private DataFlowSupport mSupport;
    private boolean mNestedCreateDataFlow;
    private boolean mNestedCancelDataFlow;
    private boolean mNestDataFlowInRequest;
    private boolean mNestDataFlowInCancel;
    private ModuleException mNestedCancelFailure;
}
