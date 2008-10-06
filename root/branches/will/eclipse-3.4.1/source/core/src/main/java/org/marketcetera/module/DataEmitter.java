package org.marketcetera.module;

import org.marketcetera.util.misc.ClassVersion;


/* $License$ */
/**
 * This interface is implemented by a module that is capable of
 * emitting data. 
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$")  //$NON-NLS-1$
public interface DataEmitter {

    /**
     * This method is invoked by the module framework to request
     * the emitter to start generating data.
     *
     * The request object supplied when requesting the data flow is
     * supplied as the <code>inRequest</code> parameter. The emitter
     * module can access the request parameters specifying the details
     * of the request via {@link DataRequest#getData()} 
     *
     * The <code>inSupport</code> instance has
     * {@link DataEmitterSupport#getRequestID() requestID} 
     * uniquely identifying this request. The same <code>requestID</code> is
     * supplied by the framework when {@link #cancel(RequestID) canceling}
     * the request.
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
     */
    public void requestData(DataRequest inRequest,
                            DataEmitterSupport inSupport)
            throws UnsupportedRequestParameterType,
            IllegalRequestParameterValue;

    /**
     * The module should stop emitting data corresponding to the specified
     * request ID when this method is invoked.
     *
     * Does nothing if the request wasn't active.
     *
     * @param inRequestID the request handle
     */
    public void cancel(RequestID inRequestID);
}
