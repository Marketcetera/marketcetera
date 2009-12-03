package org.marketcetera.module;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.log.I18NBoundMessage;

/* $License$ */
/**
 * This exception is thrown by a module factory when its
 * unable to instantiate a module.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")  //$NON-NLS-1$
public class ModuleCreationException extends ModuleException {
    private static final long serialVersionUID = -745547503264544339L;

    /**
     * Creates an instance.
     *
     * @param inMessage the error message.
     */
    public ModuleCreationException(I18NBoundMessage inMessage) {
        super(inMessage);
    }

    /**
     * Creates an instance.
     *
     * @param inCause the underlying cause
     * @param inMessage the error message
     */
    public ModuleCreationException(Throwable inCause,
                                   I18NBoundMessage inMessage) {
        super(inCause, inMessage);
    }
}
