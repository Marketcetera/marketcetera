package org.marketcetera.module;

import org.marketcetera.util.misc.ClassVersion;

import java.io.Serializable;
import java.beans.ConstructorProperties;

/* $License$ */
/**
 * Instances of this class describe the state of a module in a data flow.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")  //$NON-NLS-1$
public final class DataFlowStep implements Serializable {
    /**
     * The data request associated with this flow step.
     *
     * @return the data request.
     */
    public StringDataRequest getRequest() {
        return mRequest;
    }

    /**
     * The instance URN of the module in this flow step
     *
     * @return the module URN
     */
    public ModuleURN getModuleURN() {
        return mModuleURN;
    }

    /**
     * If this module is participating as a data emitter
     *
     * @return true if this module is emitting data in this data flow.
     */
    public boolean isEmitter() {
        return mEmitter;
    }

    /**
     * If this module is participating as a data receiver
     *
     * @return true if this module is receiving data in this data flow.
     */
    public boolean isReceiver() {
        return mReceiver;
    }

    /**
     * Number of data objects emitted. This value is valid only if
     * {@link #isEmitter()} is true.
     *
     * @return number of data objects emitted.
     */
    public long getNumEmitted() {
        return mNumEmitted;
    }

    /**
     * Number of data objects received. This value is valid only if
     * {@link #isReceiver()} is true.
     *
     * @return number of data objects received.
     */
    public long getNumReceived() {
        return mNumReceived;
    }

    /**
     * Returns the number of errors encountered on this data flow step
     * when receiving data. This value is valid only if {@link #isReceiver()}
     * is true.
     * 
     * Zero, if no errors have been encountered so far.
     *
     * @return the number of errors encountered when receiving data.
     */
    public long getNumReceiveErrors() {
        return mNumReceiveErrors;
    }

    /**
     * Returns a non-null value explaining the last error that was
     * encountered while receiving data in this flow step.
     * This value is valid only if {@link #isReceiver()} is true.
     *
     * @return a non-null value explaining the last error encountered
     * while receiving data. Null, if there have no errors so far.
     */
    public String getLastReceiveError() {
        return mLastReceiveError;
    }

    /**
     * Returns the number of data emit errors encountered by this module
     * when emitting data.
     *
     * This value is valid only if {@link #isEmitter()} is true.
     *
     * @return the number of data emit errors encountered when emitting
     * data. zero, if no errors have been encountered so far.
     * 
     * @see DataEmitterSupport#dataEmitError(org.marketcetera.util.log.I18NBoundMessage, boolean) 
     */
    public long getNumEmitErrors() {
        return mNumEmitErrors;
    }

    /**
     * Returns the last data emit error encountered by this module.
     * 
     * This value is valid only if {@link #isEmitter()} is true.
     *
     * @return the last data emit error encountered by this module, null if
     * no errors have been encountered so far.
     */
    public String getLastEmitError() {
        return mLastEmitError;
    }

    /**
     * Creates an instance.
     *
     * @param inRequest The original data request
     * @param inModuleURN the module instance URN
     * @param inEmitter if this module is emitting data in this data flow
     * @param inReceiver if this module is receiving data in this data flow
     * @param inNumEmitted number of data objects emitted
     * @param inNumReceived number of data objects received
     * @param inNumEmitErrors number of errors encountered when emitting
     * data.
     * @param inNumReceiveErrors number of errors encountered when
     * receiving data
     * @param inLastEmitError last error encountered when emitting data.
     * @param inLastReceiveError last error encountered when receiving data
     */
    @ConstructorProperties({
            "request",          //$NON-NLS-1$
            "moduleURN",        //$NON-NLS-1$
            "emitter",          //$NON-NLS-1$
            "receiver",         //$NON-NLS-1$
            "numEmitted",       //$NON-NLS-1$
            "numReceived",      //$NON-NLS-1$
            "numEmitErrors",    //$NON-NLS-1$
            "numReceiveErrors", //$NON-NLS-1$
            "lastEmitError",    //$NON-NLS-1$
            "lastReceiveError"  //$NON-NLS-1$
            })
    public DataFlowStep(StringDataRequest inRequest,
                        ModuleURN inModuleURN,
                        boolean inEmitter,
                        boolean inReceiver,
                        long inNumEmitted,
                        long inNumReceived,
                        long inNumEmitErrors,
                        long inNumReceiveErrors,
                        String inLastEmitError,
                        String inLastReceiveError) {
        mRequest = inRequest;
        mModuleURN = inModuleURN;
        mEmitter = inEmitter;
        mReceiver = inReceiver;
        mNumEmitted = inNumEmitted;
        mNumReceived = inNumReceived;
        mNumEmitErrors = inNumEmitErrors;
        mNumReceiveErrors = inNumReceiveErrors;
        mLastEmitError = inLastEmitError;
        mLastReceiveError = inLastReceiveError;
    }

    private final StringDataRequest mRequest;
    private final ModuleURN mModuleURN;
    private final boolean mEmitter;
    private final boolean mReceiver;
    private final long mNumEmitted;
    private final long mNumReceived;
    private final long mNumEmitErrors;
    private final long mNumReceiveErrors;
    private final String mLastEmitError;
    private final String mLastReceiveError;
    private static final long serialVersionUID = 2259786776527863195L;
}
