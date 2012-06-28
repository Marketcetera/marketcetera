package org.marketcetera.module;

import org.marketcetera.core.attributes.ClassVersion;
import org.marketcetera.util.log.I18NBoundMessage;

/* $License$ */
/**
 * Thrown when errors are encountered when
 * setting attributes on a provider or module MBean.
 *
 * @author anshul@marketcetera.com
 * @version $Id: BeanAttributeSetException.java 16063 2012-01-31 18:21:55Z colin $
 * @since 1.0.0
 */
@ClassVersion("$Id: BeanAttributeSetException.java 16063 2012-01-31 18:21:55Z colin $")  //$NON-NLS-1$
public class BeanAttributeSetException extends MXBeanOperationException {
    /**
     * Creates an instance.
     *
     * @param inCause the underlying cause
     * @param inMessage the error message
     */
    BeanAttributeSetException(Throwable inCause,
                                     I18NBoundMessage inMessage) {
        super(inCause, inMessage);
    }

    private static final long serialVersionUID = 4039390667884626354L;
}
