package org.marketcetera.module;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.log.I18NBoundMessage;

/* $License$ */
/**
 * This exception is thrown when the requested
 * module provider as identified by its URN, does not exist.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")  //$NON-NLS-1$
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
