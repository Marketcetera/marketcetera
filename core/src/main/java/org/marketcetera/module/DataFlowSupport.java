package org.marketcetera.module;

import org.marketcetera.util.misc.ClassVersion;


/* $License$ */
/**
 * This interface is implemented by modules that need the capability
 * to create and cancel data flows.
 * <p>
 * Note that the methods defined in this interface can be invoked
 * from {@link Module#preStart()} and {@link Module#preStop()} methods or
 * any other context. The methods in this interface <b>should not</b> be
 * invoked from {@link DataEmitter#requestData(DataRequest, DataEmitterSupport)}
 * or {@link DataEmitter#cancel(DataFlowID, RequestID)} methods. 
 *
 * <p>
 * Do note that the <code>ModuleURN</code> specified when requesting
 * data flows can contain the keyword '<code>this</code>' in place of
 * providerType, providerName or instanceName and the system will replace
 * '<code>this</code>' with value of the same URN element from the requesting
 * module's URN.
 * <p>
 * For example, if the requesting module's URN is
 * <code>metc:strategy:java:hedgeme</code> and the data request contains the
 * URN, <code>metc:cep:vendor:this</code>, the keyword <code>this</code> in
 * the URN will be substitued with the appropriate value from the
 * requester's URN, <code>hedgeme</code> in this case. The URN used by the
 * system to look for the module instance when creating the data flow will
 * be <code>metc:cep:vendor:hedgme</code>.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")   //$NON-NLS-1$
public interface DataFlowSupport {
    /**
     * Initiates a data flow request. Invoking this API will
     * automatically append the sink module to the end of the
     * data pipeline, if its not already requested as the last
     * module in the pipeline and the last module in the pipeline
     * is capable of emitting data.
     *
     * @param inRequests The ordered list of requests. Each instance
     * identifies a stage of the data pipeline. The data from the
     * first stage is piped to the next.
     *
     * @return a unique ID identifying the data flow. The ID can be used to
     * cancel the data flow request and get more details on it.
     *
     * @throws ModuleException if any of the requested modules could
     * not be found, or instantiated or configured. Or if any of the
     * modules were not capable of emitting or receiving data as
     * requested. Or if any of the modules didn't understand the
     * request parameters or were unable to emit data as requested. 
     */
    public DataFlowID createDataFlow(DataRequest[] inRequests)
            throws ModuleException;

    /**
     * Initiates a data flow request. Unlike its other over-loaded
     * variant, this API will only append the sink module automatically
     * to the end of the pipeline, if its explicitly requested to do so.
     *
     * @param inRequests The ordered list of requests. Each instance
     * identifies a stage of the data pipeline. The data from the
     * first stage is piped to the next.
     * @param inAppendSink if the sink module should be appended to the
     * data pipeline, if its not already requested as the last module
     * and the last module is capable of emitting data.
     *
     * @return a unique ID identifying the data flow. The ID can be used to
     * cancel the data flow request and get more details on it.
     *
     * @throws ModuleException if any of the requested modules could
     * not be found, or instantiated or configured. Or if any of the
     * modules were not capable of emitting or receiving data as
     * requested. Or if any of the modules didn't understand the
     * request parameters or were unable to emit data as requested.
     * 
     * @see ModuleManager#createDataFlow(DataRequest[], boolean)  
     */
    public DataFlowID createDataFlow(DataRequest[] inRequests,
                                     boolean inAppendSink)
            throws ModuleException;

    /**
     * Cancels a data flow identified by the supplied data flow ID.
     *
     * @param inFlowID the request handle that was returned from
     * a prior call to {@link #createDataFlow(DataRequest[])}
     * 
     * @throws DataFlowNotFoundException if there were errors canceling the
     * data flow.
     */
    public void cancel(DataFlowID inFlowID) throws ModuleException;
}
