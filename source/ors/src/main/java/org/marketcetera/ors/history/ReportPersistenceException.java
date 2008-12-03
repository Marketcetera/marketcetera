package org.marketcetera.ors.history;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.log.I18NBoundMessage;

/* $License$ */
/**
 * ReportPersistenceException
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class ReportPersistenceException extends I18NException {
    /**
     * Create an instance.
     *
     * @param cause the underlying cause.
     * @param message the error message.
     */
    public ReportPersistenceException(Throwable cause, I18NBoundMessage message) {
        super(cause, message);
    }

    private static final long serialVersionUID = 1L;
}
