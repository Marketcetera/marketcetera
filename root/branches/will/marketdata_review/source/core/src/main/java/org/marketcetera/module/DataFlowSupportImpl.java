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
        return createDataFlow(inRequests, true);
    }

    @Override
    public DataFlowID createDataFlow(DataRequest[] inRequests,
                                     boolean inAppendSink)
            throws ModuleException {
        return mManager.createDataFlow(inRequests,
                inAppendSink, mRequester);
    }

    @Override
    public void cancel(DataFlowID inFlowID)
            throws ModuleException {
        mManager.cancel(inFlowID, mRequester);
    }
    private final Module mRequester;
    private final ModuleManager mManager;
}
