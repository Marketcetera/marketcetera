package org.marketcetera.module;

import org.marketcetera.util.misc.ClassVersion;

import java.util.Date;
import java.beans.ConstructorProperties;
import java.io.Serializable;

/* $License$ */
/**
 * Instances of this class describe the current state of a data flow.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")  //$NON-NLS-1$
public final class DataFlowInfo implements Serializable {
    /**
     * Returns information on individual data flow steps
     * in this data flow. There's one data flow step per participating module
     * in a data flow.
     *
     * @return information on individual flow steps.
     */
    public DataFlowStep[] getFlowSteps() {
        return mFlowSteps;
    }

    /**
     * Returns the data flow ID that uniquely identifies this
     * data flow.
     *
     * @return the data flow ID.
     */
    public DataFlowID getFlowID() {
        return mFlowID;
    }

    /**
     * Returns the URN of the module that created this
     * data flow. Returned value is null, if the data flow was
     * not created by a module.
     *
     * @return the URN identifying the module that created this
     * data flow. Null, if a module did not create this data flow
     *
     * @see DataFlowSupport#createDataFlow(DataRequest[])
     */
    public ModuleURN getRequesterURN() {
        return mRequesterURN;
    }

    /**
     * Returns the URN identifying the module that stopped this
     * data flow. Returned value is null, if the data flow is
     * not stopped or if the data flow was not stopped
     * by a module.
     *
     * @return the URN identifying the module that stopped this
     * data flow. Null, if a module did not stop this data flow or
     * if the data flow is currently active
     *
     * @see DataFlowSupport#cancel(DataFlowID)
     * @see DataEmitterSupport#dataEmitError(org.marketcetera.util.log.I18NBoundMessage, boolean)
     * @see org.marketcetera.module.StopDataFlowException 
     */
    public ModuleURN getStopperURN() {
        return mStopperURN;
    }

    /**
     * The timestamp when the data flow was created.
     *
     * @return the timestamp when the data flow was created. 
     */
    public Date getCreated() {
        return mCreated;
    }

    /**
     * The timestamp when the data flow was stopped. null,
     * if the data flow is currently active.
     *
     * @return the timestamp when the data flow was stopped. 
     */
    public Date getStopped() {
        return mStopped;
    }

    /**
     * Creates an instance.
     *
     * @param inFlowSteps the individual data flow steps, identifying
     * the details of each module instance participating in the data flow.
     * @param inFlowID the data flow ID uniquely identifying this data flow.
     * @param inRequesterURN the data flow requester's URN.
     * @param inStopperURN the URN of the module that stopped this data flow.
     * @param inCreated the timestamp when the data flow was created.
     * @param inStopped the timestamp when the data flow was stopped.
     */
    @ConstructorProperties({
            "flowSteps",      //$NON-NLS-1$
            "flowID",         //$NON-NLS-1$
            "requesterURN",   //$NON-NLS-1$
            "stopperURN",     //$NON-NLS-1$
            "created",        //$NON-NLS-1$
            "stopped"         //$NON-NLS-1$
            })
    public DataFlowInfo(DataFlowStep[] inFlowSteps,
                        DataFlowID inFlowID,
                        ModuleURN inRequesterURN,
                        ModuleURN inStopperURN,
                        Date inCreated,
                        Date inStopped) {
        mFlowSteps = inFlowSteps;
        mFlowID = inFlowID;
        mRequesterURN = inRequesterURN;
        mStopperURN = inStopperURN;
        mCreated = inCreated;
        mStopped = inStopped;
    }

    private final DataFlowStep[] mFlowSteps;
    private final DataFlowID mFlowID;
    private final ModuleURN mRequesterURN;
    private final ModuleURN mStopperURN;
    private final Date mCreated;
    private final Date mStopped;
    private static final long serialVersionUID = 5755115729366822314L;
}
