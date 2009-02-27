package org.marketcetera.module;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */
/**
 * The type of coupling to use between two modules
 * within a data flow.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")  //$NON-NLS-1$
public enum DataCoupling {
    /**
     * Data is communicated synchronously, ie. the data receiver
     * receives the data within the context of the same thread as
     * the emitter.
     */
    SYNC {
        AbstractDataCoupler createCoupler(ModuleManager inManager,
                                          Module inEmitter,
                                          Module inReceiver,
                                          DataFlowID inFlowID) {
            return new SyncCoupler(inManager, inEmitter,
                    inReceiver, inFlowID);
        }
    },
    /**
     * Data is communicated asynchronously, ie. the data receiver
     * receivers the data in a separate thread from the one emitting
     * data.
     *
     * <b>this coupling is currently not supported, attempts to use
     * this coupling will result in {@link UnsupportedOperationException}</b>

     *
     */
    ASYNC {
        AbstractDataCoupler createCoupler(ModuleManager inManager,
                                          Module inEmitter,
                                          Module inReceiver,
                                          DataFlowID inFlowID) {
            throw new UnsupportedOperationException();
        }
    };

    /**
     * Creates an instance of the data coupler for data coupling mode.
     *
     * @param inManager the module manager instance
     * @param inEmitter the module that will emit data into the coupling
     * @param inReceiver the module that will receive data from the coupling
     * @param inFlowID the data flow ID for the data flow within which this
     * coupling is operating.
     * @return the coupler for this data coupling mode.
     */
    abstract AbstractDataCoupler createCoupler(
            ModuleManager inManager,
            Module inEmitter,
            Module inReceiver,
            DataFlowID inFlowID);
}
