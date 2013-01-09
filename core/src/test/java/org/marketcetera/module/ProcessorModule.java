package org.marketcetera.module;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.core.Pair;

import java.util.Hashtable;
import java.util.HashSet;

/* $License$ */
/**
 * A module that emits and receives data.
 * This module accepts a single string request parameter
 * that is the class name of the data type to expect.
 * The module filters all the data it receives and only emits
 * data that is of the supplied type onward in the data pipe.
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$")
public class ProcessorModule extends ModuleBase
        implements DataEmitter, DataReceiver {
    public ProcessorModule(ModuleURN inModuleURN) {
        this(inModuleURN, true);
    }
    protected ProcessorModule(ModuleURN inModuleURN,
                              boolean inAutoStart) {
        super(inModuleURN, inAutoStart);
    }

    @Override
    public void receiveData(DataFlowID inFlowID, Object data)
            throws UnsupportedDataTypeException, StopDataFlowException {
        if(data == null) {
            throw new StopDataFlowException(TestMessages.STOP_DATA_FLOW);
        }
        //pass thru data is sent right across.
        if(mPassThru != null) {
            mPassThru.send(data);
            return;
        }
        //boolean data is transmitted without filtering to all requests
        if(data instanceof Boolean) {
            for(Pair<Class,DataEmitterSupport> p:mTable.values()) {
                p.getSecondMember().send(data);
            }
            return;
        }
        if(mNumReceive++ % 2 == 0) {
            //throw an error every other time just for testing error reporting
            throw new UnsupportedDataTypeException(TestMessages.BAD_DATA);
        }
        for(Pair<Class,DataEmitterSupport> p:mTable.values()) {
            if(p.getFirstMember().isInstance(data)) {
                p.getSecondMember().send(data);
            }
        }
    }

    @Override
    public void requestData(
            DataRequest inRequest,
            DataEmitterSupport inSupport)
            throws RequestDataException {
        Object obj = inRequest.getData();
        if(obj == null) {
            throw new IllegalRequestParameterValue(getURN(), obj);
        }
        if(!(obj instanceof String)) {
            throw new UnsupportedRequestParameterType(getURN(), obj);
        }
        if("passThru".equals(obj)) {
            if(mPassThru != null) {
                throw new IllegalRequestParameterValue(getURN(),obj);
            }
            mPassThru = inSupport;
            mPassThruID = inSupport.getRequestID();
        } else {
            try {
                Class filter = Class.forName(obj.toString());
                mTable.put(inSupport.getRequestID(),
                        new Pair<Class,DataEmitterSupport>(
                        filter,inSupport));
                mFlows.add(inSupport.getFlowID());
            } catch (ClassNotFoundException e) {
                throw new IllegalRequestParameterValue(getURN(),
                        obj.toString(),e);
            }
        }
    }

    @Override
    public void cancel(DataFlowID inFlowID, RequestID inRequestID) {
        if (inRequestID.equals(mPassThruID)) {
            mPassThru = null;
        }
        Pair<Class, DataEmitterSupport> pair = mTable.remove(inRequestID);
        if(pair != null) {
            mFlows.remove(pair.getSecondMember().getFlowID());
        }
    }

    /**
     * Number of requests being processed.
     *
     * @return number of requests being processed.
     */
    public int getNumRequests() {
        return mTable.size();
    }

    /**
     * Set of data flows that this module is particpating in.
     *
     * @return the set of data flows.
     */
    public DataFlowID[] getFlows() {
        return mFlows.toArray(new DataFlowID[mFlows.size()]);
    }
    private int mNumReceive = 0;

    private DataEmitterSupport mPassThru = null;
    private RequestID mPassThruID;
    private Hashtable<RequestID, Pair<Class,DataEmitterSupport>> mTable =
            new Hashtable<RequestID, Pair<Class, DataEmitterSupport>>();
    private HashSet<DataFlowID> mFlows = new HashSet<DataFlowID>();
}
