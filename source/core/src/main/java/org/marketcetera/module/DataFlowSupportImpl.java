package org.marketcetera.module;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */
/**
 * Instances of this class are supplied to modules that need
 * the capability to create and cancel data flows. The methods
 * of this class delegate to the respective {@link ModuleManager} methods
 * adding the URN of the module requesting data flow creation / cancelation. 
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")  //$NON-NLS-1$
class DataFlowSupportImpl implements DataFlowSupport {
    /**
     * Creates a new instance.
     *
     * @param inRequester the module that will be requesting
     * @param inManager the module manager instance
     */
    DataFlowSupportImpl(Module inRequester, ModuleManager inManager) {
        assert inRequester != null;
        assert inManager != null;
        
        mRequester = inRequester;
        mManager = inManager;
    }

    @Override
    public DataFlowID createDataFlow(DataRequest[] inRequests)
            throws ModuleException {
        verifyIncorrectInvocation();
        return createDataFlow(inRequests, true);
    }

    @Override
    public DataFlowID createDataFlow(DataRequest[] inRequests,
                                     boolean inAppendSink)
            throws ModuleException {
        verifyIncorrectInvocation();
        return mManager.createDataFlow(inRequests,
                inAppendSink, mRequester);
    }

    @Override
    public void cancel(DataFlowID inFlowID)
            throws ModuleException {
        verifyIncorrectInvocation();
        mManager.cancel(inFlowID, mRequester);
    }

    /**
     * Fails if invoked from within
     * {@link DataEmitter#requestData(DataRequest, DataEmitterSupport)} or
     * {@link DataEmitter#cancel(DataFlowID, RequestID)}.
     *
     * @throws ModuleException if its an incorrect nested flow request.
     */
    private static void verifyIncorrectInvocation() throws ModuleException {
        if(AbstractDataCoupler.isNestedFlowCall()) {
            throw new ModuleException(Messages.INCORRECT_NESTED_FLOW_REQUEST);
        }
    }
    private final Module mRequester;
    private final ModuleManager mManager;
}
