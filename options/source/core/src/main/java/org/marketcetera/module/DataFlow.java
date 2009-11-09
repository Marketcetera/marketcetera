package org.marketcetera.module;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.core.NoMoreIDsException;
import org.marketcetera.core.IDFactory;
import org.marketcetera.core.InMemoryIDFactory;

import java.util.Date;
import java.util.HashSet;

/* $License$ */
/**
 * Instances of this class maintain all the information on a data flow.
 * These instances are used by the module framework to keep track of
 * all the data flows.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")  //$NON-NLS-1$
class DataFlow {

    /**
     * Returns the data flow ID.
     *
     * @return the data flow ID.
     */
    public DataFlowID getFlowID() {
        return mFlowID;
    }

    /**
     * The URN of the module that requested this data flow.
     *
     * @return URN of the module that requested this data flow.
     * null, if this data flow was not requested by a module.
     */
    public ModuleURN getRequesterURN() {
        return mRequesterURN;
    }

    /**
     * Creates a data flow info instance that snapshots the current
     * state of this data flow.
     *
     * @return a snapshot of the current state of this data flow.
     */
    public DataFlowInfo toDataFlowInfo() {
        DataFlowStep [] steps = new DataFlowStep[mRequests.length];
        for(int i = 0; i < mRequests.length; i++) {
            //every module except the last one is an emitter
            //number of couplers is one less than number of requests.
            boolean isEmitter = i < mCouplers.length;
            //every module except the first one is a receiver
            boolean isReceiver = i > 0;
            steps[i] = new DataFlowStep(
                    mRequests[i].toStringRequest(),
                    isEmitter
                            ? mCouplers[i].getEmitterURN()
                            : mCouplers[i - 1].getReceiverURN(),
                    isEmitter,
                    isReceiver,
                    isEmitter
                            ? mCouplers[i].getEmitted()
                            : 0,
                    isReceiver
                            ? mCouplers[i - 1].getReceived()
                            : 0,
                    isEmitter
                            ? mCouplers[i].getEmitErrors()
                            : 0,
                    isReceiver
                            ? mCouplers[i-1].getReceiveErrors()
                            : 0,
                    isEmitter
                            ? mCouplers[i].getLastEmitError()
                            : null,
                    isReceiver
                            ? mCouplers[i - 1].getLastReceiveError()
                            : null);
        }
        return new DataFlowInfo(steps, mFlowID, mRequesterURN,
                mStopRequesterURN, mCreated, mStopped);
    }

    /**
     * Returns true if this data flow was created by a module.
     *
     * @return if this data flow was created by a module.
     */
    public boolean isModuleCreated() {
        return mRequesterURN != null;
    }
    /**
     * Creates an instance.
     *
     * @param inManager the module manager instance.
     * @param inRequesterURN the data flow requester URN.
     * @param inRequests the data requests specified when requesting
     * this data flow.
     * @param inModules the modules participating in this data flow.
     *
     * @throws ModuleException if there was an error generating a flowID
     * for this data flow.
     */
    DataFlow(ModuleManager inManager,
             ModuleURN inRequesterURN,
             DataRequest[] inRequests,
             Module[] inModules) throws ModuleException {
        mFlowID = generateFlowID();
        mRequesterURN = inRequesterURN;
        mRequests = inRequests;
        mCouplers = new AbstractDataCoupler[inModules.length - 1];
        assert inModules.length == mRequests.length;
        for(int i = mCouplers.length - 1; i >= 0; i--) {
            mCouplers[i] = mRequests[i].getCoupling().createCoupler(
                    inManager, inModules[i], inModules[i + 1], mFlowID);
        }
    }

    /**
     * Initialize the flow by initiating requests on the emitters and plumbing
     * them to the respective receivers.
     *
     * @throws ModuleException if there was a failure setting up the data flow.
     */
    void initFlow() throws ModuleException {
        boolean failed = true;

        int i = mCouplers.length - 1;
        //Acquire lock to prevent concurrent cancellation that might happen
        //if a participating module emits or throws an error within the data
        //flow requesting that the data flow be stopped. This lock will
        //cause the cancellation to block until the flow has been
        //completely setup.
        synchronized (this) {
            try {
                for(; i >= 0; i--) {
                    mCouplers[i].initiateRequest(generateRequestID(),
                            mRequests[i]);
                }
                failed = false;
            } finally {
                if(failed) {
                    mCancelling = true;
                    //go through all the initiated requests and cancel them.
                    while(++i < mCouplers.length) {
                        mCouplers[i].cancelRequest();
                    }
                }
            }
        }
    }

    /**
     * Cancels the data flow. Iterates through all the data flows
     * and cancels their requests.
     *
     * @param inStopRequester the module requesting that the data flow
     * be stopped. null, if this request is not being made by a module.
     * 
     * @throws DataFlowException if the data flow is in the process of
     * being canceled by another operation.
     */
    void cancel(ModuleURN inStopRequester) throws DataFlowException {
        SLF4JLoggerProxy.debug(this,
                "Stopping flow {} requested by {}",  //$NON-NLS-1$
                getFlowID(), inStopRequester );
        //Only allow one thread to carry out cancellation at a time.
        synchronized (this) {
            if(mCancelling) {
                throw new DataFlowException(new I18NBoundMessage1P(
                        Messages.DATA_FLOW_ALREADY_CANCELING,
                        getFlowID().toString()));
            } else {
                mCancelling = true;
            }
        }
        // Go through each of the requests and cancel the requests
        // from first to last
        for(AbstractDataCoupler coupler: mCouplers) {
            coupler.cancelRequest();
        }
        mStopRequesterURN = inStopRequester;
        mStopped = new Date();
    }

    /**
     * Returns the set of URNs of modules that are participating
     * in this data flow.
     *
     * @return the set of URNs of modules participating in this
     * data flow.
     */
    HashSet<ModuleURN> getParticipants() {
        HashSet<ModuleURN> participants = new HashSet<ModuleURN>();
        for(int i = 0; i < mCouplers.length; i++) {
            if(i == 0) {
                participants.add(mCouplers[i].getEmitterURN());
            }
            participants.add(mCouplers[i].getReceiverURN());
        }
        return participants;
    }
    /**
     * Generates a unique ID for a data flow.
     *
     * @return a unique ID for identifying a data flow
     *
     * @throws ModuleException if there were errors generating the
     * data flow ID.
     */
    private static DataFlowID generateFlowID() throws ModuleException {
        try {
            return new DataFlowID(sIDFactory.getNext());
        } catch (NoMoreIDsException e) {
            throw new ModuleException(e, Messages.UNABLE_GENERATE_FLOW_ID);
        }
    }

    /**
     * Generates the request ID.
     *
     * @return the request ID.
     *
     * @throws ModuleException if there were errors generating the request ID.
     */
    private static RequestID generateRequestID() throws ModuleException {
        try {
            return new RequestID(sIDFactory.getNext());
        } catch (NoMoreIDsException e) {
            throw new ModuleException(e, Messages.UNABLE_GENERATE_REQUEST_ID);
        }
    }

    /*
     * These variables are defined as volatile as they can be read
     * and written by different threads. Its assumed that synchronization
     * is carried out at a higher level.
     */
    private volatile ModuleURN mStopRequesterURN;
    private volatile Date mStopped = null;

    private final ModuleURN mRequesterURN;
    private final DataFlowID mFlowID;
    private final DataRequest[] mRequests;
    private final AbstractDataCoupler[] mCouplers;
    private final Date mCreated = new Date();
    private boolean mCancelling = false;
    /**
     * ID factory for generating data flow IDs.
     */
    private static final IDFactory sIDFactory = new InMemoryIDFactory(1);
}
