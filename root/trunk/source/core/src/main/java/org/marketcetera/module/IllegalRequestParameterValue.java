package org.marketcetera.module;

import org.apache.commons.lang.ObjectUtils;
import org.marketcetera.util.log.I18NBoundMessage;
import org.marketcetera.util.log.I18NBoundMessage2P;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */
/**
 * Thrown by a data emitter when its unable to
 * process the supplied request parameter value when processing
 * a data request.
 *
 * @see DataEmitter#requestData(DataRequest, DataEmitterSupport)
 * 
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")  //$NON-NLS-1$
public class IllegalRequestParameterValue extends RequestDataException {
    private static final long serialVersionUID = 7502538172056373356L;

    /**
     * Creates an instance.
     *
     * @param inMessage the error message
     */
    public IllegalRequestParameterValue(I18NBoundMessage inMessage) {
        super(inMessage);
    }

    /**
     * Creates an instance.
     *
     * @param inCause the cause
     * @param inMessage the error message
     */
    public IllegalRequestParameterValue(Throwable inCause,
                                        I18NBoundMessage inMessage) {
        super(inCause, inMessage);
    }

    /**
     * Creates an instance.
     *
     * @param inModuleURN the module throwing this exception
     * @param inParameter the offending parameter value
     */
    public IllegalRequestParameterValue(ModuleURN inModuleURN,
                                        Object inParameter) {
        super(new I18NBoundMessage2P(Messages.ILLEGAL_REQ_PARM_VALUE,
                                     inModuleURN.getValue(), ObjectUtils.toString(inParameter,null)));
    }

    /**
     * Creates an instance
     *
     * @param inModuleURN the module throwing this exception
     * @param inParameter the parameter value
     * @param inCause the cause
     */
    public IllegalRequestParameterValue(ModuleURN inModuleURN,
                                        Object inParameter,
                                        Throwable inCause) {
        super(inCause, new I18NBoundMessage2P(Messages.ILLEGAL_REQ_PARM_VALUE,
                                              inModuleURN.getValue(), ObjectUtils.toString(inParameter,null)));
    }
}