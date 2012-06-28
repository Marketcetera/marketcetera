package org.marketcetera.module;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.log.I18NBoundMessage;

/* $License$ */
/**
 * Indicates errors are encountered registering or unregistering
 * module factory or instance mxbeans with the mbean server.
 *
 * @author anshul@marketcetera.com
 * @version $Id: BeanRegistrationException.java 16063 2012-01-31 18:21:55Z colin $
 * @since 1.0.0
 */
@ClassVersion("$Id: BeanRegistrationException.java 16063 2012-01-31 18:21:55Z colin $")  //$NON-NLS-1$
public class BeanRegistrationException extends MXBeanOperationException {
    /**
     * Creates an instance.
     *
     * @param inCause the underlying cause.
     * @param inMessage the error message
     */
    BeanRegistrationException(Throwable inCause,
                                     I18NBoundMessage inMessage) {
        super(inCause, inMessage);
    }

    private static final long serialVersionUID = -4300262276734891698L;
}
