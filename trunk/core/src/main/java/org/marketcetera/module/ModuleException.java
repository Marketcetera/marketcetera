package org.marketcetera.module;

import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.log.I18NBoundMessage;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */
/**
 * Instances of this class are used for errors pertaining to the module
 * framework and its components.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")  //$NON-NLS-1$
public class ModuleException extends I18NException {
    /**
     * Creates an instance
     *
     * @param inCause the cause
     */
    public ModuleException(Throwable inCause) {
        super(inCause);
    }

    /**
     * Creates an instance
     *
     * @param inMessage the internationalized exception message.
     */
    public ModuleException(I18NBoundMessage inMessage) {
        super(inMessage);
    }

    /**
     * Creates an instance
     *
     * @param inCause the cause
     * @param inMessage the internationalized exception message
     */
    public ModuleException(Throwable inCause, I18NBoundMessage inMessage) {
        super(inCause, inMessage);
    }

    private static final long serialVersionUID = 5179008155594808862L;
}
