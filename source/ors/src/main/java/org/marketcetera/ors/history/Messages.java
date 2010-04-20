package org.marketcetera.ors.history;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.log.*;

/* $License$ */
/**
 * Internationalized messages for this package.
 *
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public interface Messages {
    /**
     * The message provider
     */
    static final I18NMessageProvider PROVIDER =
            new I18NMessageProvider("ors_history",  //$NON-NLS-1$
                    Messages.class.getClassLoader());
    /**
     * The message logger.
     */
    static final I18NLoggerProxy LOGGER =
            new I18NLoggerProxy(PROVIDER);

    static final I18NMessage2P LOG_ROOT_ID_NOT_FOUND =
            new I18NMessage2P(LOGGER, "log_root_id_not_found");  //$NON-NLS-1$
    static final I18NMessage1P ERROR_RECONSTITUTE_FIX_MSG =
            new I18NMessage1P(LOGGER, "error_reconstitute_fix_msg");   //$NON-NLS-1$

    static final I18NMessage0P RHS_CANNOT_CREATE_QUEUE =
        new I18NMessage0P(LOGGER, "rhs_cannot_create_queue"); //$NON-NLS-1$
    static final I18NMessage0P RHS_NO_MORE_IDS =
        new I18NMessage0P(LOGGER, "rhs_no_more_ids"); //$NON-NLS-1$
    static final I18NMessage1P RHS_ENQUEUED_REPLY=
        new I18NMessage1P(LOGGER,"rhs_enqueued_reply"); //$NON-NLS-1$
    static final I18NMessage1P RHS_DEQUEUED_REPLY=
        new I18NMessage1P(LOGGER,"rhs_dequeued_reply"); //$NON-NLS-1$
    static final I18NMessage1P RHS_PERSISTED_REPLY=
        new I18NMessage1P(LOGGER,"rhs_persisted_reply"); //$NON-NLS-1$
    static final I18NMessage1P RHS_PERSIST_ERROR=
        new I18NMessage1P(LOGGER,"rhs_persist_error"); //$NON-NLS-1$
}