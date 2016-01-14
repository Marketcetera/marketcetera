package org.marketcetera.module;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Represents a stage within a data flow request. Instances of this class
 * should be used when creating a new data flow.
 * <p>
 * A data flow comprises
 * of 2 or more modules connected together, where the data is emitted
 * by the first module and is processed by the intervening modules before
 * being delivered to the last module in the data flow.
 * <p>
 * A <code>DataRequest</code> instance identifies each stage or module
 * in this data flow.
 * <p>
 * A data request is composed of a module URN and a request payload.
 * The request payload can be empty depending on the module identified
 * in the data request.
 * <p>
 * The URN must uniquely identify a module instance. Partial URNs may be used
 * if they do uniquely identify a module instance within the module
 * container.
 * <p>
 * If the data flow is being created by a module, the keyword
 * <code>this</code> can be used to dynamically substitute the keyword with
 * the appropriate value from the requesting module's URN. See
 * {@link org.marketcetera.module.DataFlowSupport} documentation for more
 * details on interpretation of <code>this</code> keyword. 
 * <p>
 * The data payload supplies request
 * parameters that may be used by the module to figure out what data to
 * emit or the kind of processing to apply to the data it receives. Some
 * modules may be capable of emitting data without any request parameters.
 * Others may require that a request parameter be specified and may choose
 * to throw errors if one is not specified.
 * <p>
 * Consult the module provider documentation to figure out specific
 * parameter types and values supported by the module.
 *
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public final class DataRequest
        extends DataRequestBase
{
    /**
     * Create a new DataRequest instance.
     *
     * @param inRequestUrn
     * @param inCoupling
     * @param inExceptionHandler
     * @param inData
     */
    public DataRequest(ModuleURN inRequestUrn,
                       DataCoupling inCoupling,
                       DataFlowExceptionHandler inExceptionHandler,
                       Object inData)
    {
        super(inCoupling == null ? DataCoupling.SYNC : inCoupling,
              inRequestUrn);
        mData = inData;
        exceptionHandler = inExceptionHandler;
    }
    /**
     * Create a new DataRequest instance.
     *
     * @param inRequestUrn
     * @param inExceptionHandler
     * @param inData
     */
    public DataRequest(ModuleURN inRequestUrn,
                       DataFlowExceptionHandler inExceptionHandler,
                       Object inData)
    {
        this(inRequestUrn,
             DataCoupling.SYNC,
             inExceptionHandler,
             inData);
    }
    /**
     * Creates an instance.
     *
     * @param inRequestURN the instance URN
     * @param inCoupling the coupling to use
     * @param inData the request parameter
     */
    public DataRequest(ModuleURN inRequestURN,
                       DataCoupling inCoupling,
                       Object inData)
    {
        this(inRequestURN,
             inCoupling == null ? DataCoupling.SYNC : inCoupling,
             null,
             inData);
    }
    /**
     * Creates an instance. Coupling is defaulted to {@link DataCoupling#SYNC}
     *
     * @param inRequestURN the instance URN
     * @param inData the request parameter
     */
    public DataRequest(ModuleURN inRequestURN,
                       Object inData)
    {
        this(inRequestURN,
             DataCoupling.SYNC,
             null,
             inData);
    }

    /**
     * Creates an instance. Coupling is defaulted to {@link DataCoupling#SYNC}
     * and the request parameter is set to null. 
     *
     * @param inRequestURN the instance URN.
     */
    public DataRequest(ModuleURN inRequestURN) {
        this(inRequestURN,
             DataCoupling.SYNC,
             null);
    }

    /**
     * The request data. The request data type and semantics are defined
     * by the specific module receiving the request.
     *
     * @return the request data
     */
    public Object getData() {
        return mData;
    }
    /**
     * Get the exceptionHandler value.
     *
     * @return a <code>DataReceiverExceptionHandler</code> value
     */
    public DataFlowExceptionHandler getExceptionHandler()
    {
        return exceptionHandler;
    }
    /**
     * Converts this request to a string data request instance.
     * The returned instance is similar to this instance except
     * that the request data is converted to string using its
     * <code>toString()</code> method.
     *
     * @return the equivalent string data request.
     */
    StringDataRequest toStringRequest() {
        return new StringDataRequest(getRequestURN(), getCoupling(),
                getData() == null
                        ? null
                        : getData().toString());
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return toStringRequest().getData();
    }
    /**
     * optional exception handler for data reception events
     */
    private final DataFlowExceptionHandler exceptionHandler;
    /**
     * data object passed as object of data request
     */
    private final Object mData;
    private static final long serialVersionUID = -7154279139293567511L;
}
