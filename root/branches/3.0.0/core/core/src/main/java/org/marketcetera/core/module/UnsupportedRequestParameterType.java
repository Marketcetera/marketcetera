package org.marketcetera.core.module;

import org.marketcetera.core.util.log.I18NBoundMessage;
import org.marketcetera.core.util.log.I18NBoundMessage2P;
import org.marketcetera.api.attributes.ClassVersion;

/* $License$ */
/**
 * Thrown by a data emitter when its unable to
 * process the supplied request parameter type when processing
 * a data request.
 *
 * @see DataEmitter#requestData(DataRequest, DataEmitterSupport)
 * @author anshul@marketcetera.com
 * @version $Id: UnsupportedRequestParameterType.java 16063 2012-01-31 18:21:55Z colin $
 * @since 1.0.0
 */
@ClassVersion("$Id: UnsupportedRequestParameterType.java 16063 2012-01-31 18:21:55Z colin $")  //$NON-NLS-1$
public class UnsupportedRequestParameterType extends RequestDataException {
    private static final long serialVersionUID = 7502538172056373356L;

    /**
     * Creates an instance.
     *
     * @param message the error message
     */
    public UnsupportedRequestParameterType(I18NBoundMessage message) {
        super(message);
    }

    /**
     * Creates an instance.
     *
     * @param inModuleURN the URN of the module throwing this error.
     * @param inParameter the offending parameter value.
     */
    public UnsupportedRequestParameterType(ModuleURN inModuleURN,
                                           Object inParameter) {
        super(new I18NBoundMessage2P(Messages.UNSUPPORTED_REQ_PARM_TYPE,
                inModuleURN.getValue(), inParameter == null
                ? String.valueOf(null)
                :inParameter.getClass().getName()));
    }
}
