package org.marketcetera.trade;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.log.I18NBoundMessage;

/* $License$ */
/**
 * Instances of this exception are thrown for errors related to IDs.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class IDException extends I18NException {
    /**
     * Creates an instance.
     *
     * @param message The message to use for this error.
     */
    public IDException(I18NBoundMessage message) {
        super(message);
    }

    private static final long serialVersionUID = 1L;
}
