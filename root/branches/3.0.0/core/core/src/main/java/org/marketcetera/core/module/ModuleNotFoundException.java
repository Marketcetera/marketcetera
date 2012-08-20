package org.marketcetera.core.module;

import org.marketcetera.api.attributes.ClassVersion;
import org.marketcetera.core.util.log.I18NBoundMessage;

/* $License$ */
/**
 * This exception is thrown if the module as identified by its URN,
 * cannot be found.
 *
 * @author anshul@marketcetera.com
 * @version $Id: ModuleNotFoundException.java 16063 2012-01-31 18:21:55Z colin $
 * @since 1.0.0
 */
@ClassVersion("$Id: ModuleNotFoundException.java 16063 2012-01-31 18:21:55Z colin $")  //$NON-NLS-1$
public class ModuleNotFoundException extends ModuleException {
    /**
     * Creates an instance.
     *
     * @param inMessage the error message.
     */
    public ModuleNotFoundException(I18NBoundMessage inMessage) {
        super(inMessage);
    }

    private static final long serialVersionUID = 2311801823968533198L;
}
