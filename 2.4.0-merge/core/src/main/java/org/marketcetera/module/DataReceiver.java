package org.marketcetera.module;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */
/**
 * This interface is implemented by a module that is capable of
 * receiving data from other modules within a data flow.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")   //$NON-NLS-1$
public interface DataReceiver {
    /**
     * The module framework invokes this method to supply data to this module.
     * The receiving module must implement this method to receive the
     * data and process it.
     *
     * If the module is also an emitter, the module usually will eventually
     * emit data after processing the data it receives.
     *
     * @param inFlowID The ID of the data flow under the auspices of which
     * this data is being sent to this module 
     * @param inData the data object, can be null. If the module that is
     * emitting data emitted a null value.
     * 
     * @throws UnsupportedDataTypeException if the module does not support
     * receiving data that was sent to it.
     * @throws StopDataFlowException if the module cannot receive any more
     * data and wants to stop any more data flowing into it.
     * @throws ReceiveDataException if the module cannot receive data for
     * any other reason.
     */
    public void receiveData(DataFlowID inFlowID, Object inData)
            throws ReceiveDataException;
}
