package org.marketcetera.core.module;

/* $License$ */
/**
 * Implementations of this interface are used to listen to
 * all the data received by the sink module.
 *
 * @author anshul@marketcetera.com
 * @version $Id: SinkDataListener.java 16063 2012-01-31 18:21:55Z colin $
 * @since 1.0.0
 */
public interface SinkDataListener {
    /**
     * Receives data received by the sink module. This method
     * should not carry out significant processing and should
     * execute as quickly as possible.
     *
     * @param inFlowID the data flow ID of the data flow
     * which delivered this data.
     * @param inData the data delivered
     */
    public void receivedData(DataFlowID inFlowID, Object inData);
}
