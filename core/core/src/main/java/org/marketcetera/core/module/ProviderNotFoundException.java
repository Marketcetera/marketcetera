package org.marketcetera.core.module;

import org.marketcetera.core.attributes.ClassVersion;
import org.marketcetera.core.util.log.I18NBoundMessage;

/* $License$ */
/**
 * This exception is thrown when the requested
 * module provider as identified by its URN, does not exist.
 *
 * @author anshul@marketcetera.com
 * @version $Id: ProviderNotFoundException.java 16063 2012-01-31 18:21:55Z colin $
 * @since 1.0.0
 */
@ClassVersion("$Id: ProviderNotFoundException.java 16063 2012-01-31 18:21:55Z colin $")  //$NON-NLS-1$
public class ProviderNotFoundException extends ModuleException {
    /**
     * Creates a new instance.
     *
     * @param inMessage the error message.
     */
    ProviderNotFoundException(I18NBoundMessage inMessage) {
        super(inMessage);
    }

    private static final long serialVersionUID = 4835970580129669317L;
}
