package org.marketcetera.module;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.log.I18NBoundMessage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.Hashtable;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;

/* $License$ */
/**
 * A module that emits data.
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$")
public class EmitterModule extends ModuleBase implements DataEmitter {
    protected EmitterModule() {
        super(EmitterModuleFactory.INSTANCE_URN);
    }

    @Override
    protected void preStart() throws ModuleException {
        super.preStart();
        mService = Executors.newCachedThreadPool();
    }

    @Override
    public void preStop() throws ModuleException {
        super.preStop();
        mService.shutdown();
    }

    @Override
    public void requestData(DataRequest inRequest,
                            DataEmitterSupport inSupport)
            throws UnsupportedRequestParameterType,
            IllegalRequestParameterValue {
        //Reset the flag to make testing easier
        setThrowExceptionOnCancel(false);
        Object obj = inRequest.getData();
        if(obj == null) {
            throw new IllegalRequestParameterValue(getURN(), obj);
        }
        if(!(obj instanceof String ||
                obj instanceof Number ||
                obj instanceof Map ||obj instanceof Object[])) {
            throw new UnsupportedRequestParameterType(getURN(), obj);
        }
        mLastTask = new EmitTask(obj, inSupport);
        Future<Integer> handle = mService.submit(mLastTask);
        mRequests.put(inSupport.getRequestID(),handle);
        mFlowIDs.add(inSupport.getFlowID());
    }

    @Override
    public void cancel(DataFlowID inFlowID, RequestID inID) {
        mFlowIDs.remove(inFlowID);
        Future<Integer> handle = getTask(inID);
        if(handle != null) {
            // handles are not removed from the table so that
            // we can do extra validation after.
            handle.cancel(true);
        }
        if(mThrowExceptionOnCancel) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Returns the set of requests that are currently being
     * handled and have been handled in the past by this module.
     *
     * @return the set of all requests hendled by this module.
     */
    public Set<RequestID> getRequests() {
        return new HashSet<RequestID>(mRequests.keySet());
    }

    /**
     * Returns the set of data flow IDs for the flows that are currently
     * being handled by this module.
     *
     * @return the set of data flow IDs for the flows that are currently
     * being handled by this module.
     */
    public Set<DataFlowID> getFlows() {
        return new HashSet<DataFlowID>(mFlowIDs);
    }

    /**
     * Clears the set of requests handled by this module
     */
    public void clear() {
        mRequests.clear();
    }

    /**
     * Returns the task handle corresponding to specified request.
     *
     * @param inRequestID the request ID.
     *
     * @return the task handle.
     */
    public Future<Integer> getTask(RequestID inRequestID) {
        return mRequests.get(inRequestID);
    }

    /**
     * Invoked by the task to wait before it requests a stop via
     * the data emit support interface or it emits a null value.
     *
     * @throws InterruptedException if the wait is interrupted.
     */
    private static void waitToProceed() throws InterruptedException {
        synchronized (STOP_LOCK) {
            if (!sReadyToProceed) {
                STOP_LOCK.wait();
            }
            //Reset the flag, for the next test.
            sReadyToProceed = false;
        }
    }

    /**
     * Invoked by testing code to allow the task to proceed to request a stop
     * via the data emit interface or emit a null value.
     */
    static void readyToProceed() {
        synchronized (STOP_LOCK) {
            sReadyToProceed = true;
            STOP_LOCK.notify();
        }
    }

    /**
     * Gets the last task that was executed by this module.
     *
     * @return the last task executed by this module.
     */
    public EmitTask getLastTask() {
        return mLastTask;
    }

    /**
     * If {@link #cancel(DataFlowID, RequestID)} should throw an exception.
     *
     * @param inThrowExceptionOnCancel if the module should throw
     * an exception when canceling a request.
     */
    public void setThrowExceptionOnCancel(boolean inThrowExceptionOnCancel) {
        mThrowExceptionOnCancel = inThrowExceptionOnCancel;
    }

    private static final Object STOP_LOCK = new Object();
    private static boolean sReadyToProceed = false;

    /**
     * A task thats run in a separate thread to emit data.
     */
    static class EmitTask implements Callable<Integer> {
        public Integer call() throws Exception {
            int i = 0;
            try {
                //Loop a maximum of 10 times
                while(i < 10) {
                    if (mData instanceof Map) {
                        Map m = (Map) mData;
                        Object obj = m.get("value");
                        Object error = m.get("error");
                        int times = (Integer)m.get("times");
                        Boolean requestStop = (Boolean)m.get("requestStop");
                        //will cause the processor to request data flow stop.
                        boolean emitNull = m.containsKey("emitNull");
                        while(times > 0) {
                            mSupport.send(obj);
                            if(error != null && error instanceof I18NBoundMessage) {
                                mSupport.dataEmitError((I18NBoundMessage) error,false);
                            }
                            --times;
                        }
                        //Use a special marker to mark the end of this transmission.
                        mSupport.send(Boolean.FALSE);
                        if(emitNull || (requestStop != null && requestStop)) {
                            //wait if asked to emit null or request stop.
                            waitToProceed();
                        }
                        if(emitNull) {
                            mSupport.send(null);
                        }
                        //if asked to request data flow stop, do it
                        if(requestStop != null && requestStop) {
                            mSupport.dataEmitError(
                                    TestMessages.STOP_DATA_FLOW, true);
                        }
                    } else if(mData instanceof Object[]){
                        for(Object o: (Object[])mData) {
                            mSupport.send(o);
                        }
                    } else {
                        mSupport.send(mData);
                    }
                    i++;
                    //Sleep for long to be able to test thread's interruption.
                    Thread.sleep(1000000); // 10 seconds
                }
            } catch (InterruptedException ignore) {
            }
            return i;
        }


        private EmitTask(Object inData, DataEmitterSupport inSupport) {
            mData = inData;
            mSupport = inSupport;
        }

        public DataEmitterSupport getSupport() {
            return mSupport;
        }

        private Object mData;
        private DataEmitterSupport mSupport;
    }
    private ExecutorService mService;
    private EmitTask mLastTask;
    private final Set<DataFlowID> mFlowIDs = new HashSet<DataFlowID>();
    private final Hashtable<RequestID,Future<Integer>> mRequests =
            new Hashtable<RequestID, Future<Integer>>();
    private boolean mThrowExceptionOnCancel = false;
}
