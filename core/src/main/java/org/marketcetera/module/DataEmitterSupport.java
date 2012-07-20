package org.marketcetera.module;

import org.marketcetera.util.log.I18NBoundMessage;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */
/**
 * This interface enables a data emitter to emit data to
 * other modules. An instance of this interface is supplied to
 * data emitter modules via
 * {@link DataEmitter#requestData(DataRequest, DataEmitterSupport)}
 * to enable emitters to emit data for a data flow. 
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 * 
 * @see DataEmitter
 */
@ClassVersion("$Id$")  //$NON-NLS-1$
public interface DataEmitterSupport {
    /**
     * Sends the data to any downstream module
     *
     * @param data the data
     */
    public void send(Object data);

    /**
     * If the data emitter is facing an error that might inhibit
     * it from being able to emit data.
     *
     * @param inMessage the error message explaining why and
     * what can be done to fix this issue.
     * @param inStopDataFlow if the emitter is no longer capable of emitting
     * data and the data flow should be stopped.
     */
    public void dataEmitError(I18NBoundMessage inMessage,
                              boolean inStopDataFlow);

    /**
     * The request ID associated with this request.
     *
     * @return the request ID.
     */
    public RequestID getRequestID();

    /**
     * The flowID uniquely identifying this data flow.
     *
     * @return the flowID uniquely identifying this data flow.
     */
    public DataFlowID getFlowID();
}
