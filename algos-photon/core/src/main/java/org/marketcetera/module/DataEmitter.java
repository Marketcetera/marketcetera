package org.marketcetera.module;

import org.marketcetera.util.misc.ClassVersion;


/* $License$ */
/**
 * This interface is implemented by a module that is capable of
 * emitting data. 
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")  //$NON-NLS-1$
public interface DataEmitter {

    /**
     * This method is invoked by the module framework to request
     * the emitter to start generating data.
     * <p>
     * The request object supplied when requesting the data flow is
     * supplied as the <code>inRequest</code> parameter. The emitter
     * module can access the request parameters specifying the details
     * of the request via {@link DataRequest#getData()} 
     * <p>
     * The <code>inSupport</code> instance has
     * {@link DataEmitterSupport#getRequestID() requestID} 
     * uniquely identifying this request. The same <code>requestID</code> is
     * supplied by the framework when {@link #cancel(DataFlowID, RequestID) canceling}
     * the request.
     *
     * <p>
     * Do note that it's illegal to invoke
     * {@link DataEmitterSupport#dataEmitError(org.marketcetera.util.log.I18NBoundMessage, boolean)}
     * to stop the data flow from within this method. Data flow creation
     * is not complete unless this method returns. <code>dataEmitError</code>
     * can only be invoked after the data flow has been created. 
     * <p>
     * To prevent the data flow from getting created, throw an exception.
     * <p>
     * The emitter is not expected to emit data from within this method. If
     * this emitter is only a data emitter, then the data must be emitted
     * from a separate thread. It's expected that the data for the
     * same data flow is always emitted from the same thread sequentially. If
     * this emitter is a data receiver and an emitter, it may emit data from
     * within the {@link DataReceiver#receiveData(DataFlowID, Object)} method
     * or it may spawn a new thread to emit data like any other emitter. 
     *
     * @param inRequest the data request supplied when requesting the
     * data flow.
     * @param inSupport the emitter support instance that can be used
     * to emit data.
     *
     * @throws IllegalRequestParameterValue if the supplied request
     * parameter is invalid.
     * @throws UnsupportedRequestParameterType if the supplied request
     * parameter type is not understood by this module.
     * @throws RequestDataException if the module is unable to fulfill
     * this request to emit data for any other reason.
     */
    public void requestData(DataRequest inRequest,
                            DataEmitterSupport inSupport)
            throws RequestDataException;

    /**
     * The module should stop emitting data corresponding to the specified
     * request ID when this method is invoked.
     *
     * Does nothing if the request wasn't active.
     * @param inFlowID the data flowID
     * @param inRequestID the request handle
     */
    public void cancel(DataFlowID inFlowID, RequestID inRequestID);
}
