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
        if(!mFlowIDs.isEmpty()) {
            for(DataFlowID id: mFlowIDs) {
                mSupport.cancel(id);
            }
            mFlowIDs.clear();
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

    private DataFlowID mFlowID;
    private List<DataFlowID> mFlowIDs = new LinkedList<DataFlowID>();
    private boolean mInvokeDefault;
    private boolean mAppendSink;
    private boolean mSkipCancel = false;
    private boolean mFailPreStart = false;
    private DataRequest[] mRequests;
    private List<DataRequest[]> mDataRequests = new LinkedList<DataRequest[]>();
    private DataFlowSupport mSupport;
}
