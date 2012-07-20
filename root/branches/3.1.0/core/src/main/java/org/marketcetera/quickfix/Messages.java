package org.marketcetera.quickfix;

import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.log.I18NMessage1P;
import org.marketcetera.util.log.I18NMessage2P;
import org.marketcetera.util.log.I18NMessageProvider;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * The internationalization constants used by this package.
 *
 * @author klim@marketcetera.com
 * @since 0.6.0
 * @version $Id$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public interface Messages
{
    /**
     * The message provider.
     */

    static final I18NMessageProvider PROVIDER = 
        new I18NMessageProvider("quickfix"); //$NON-NLS-1$

    /**
     * The logger.
     */

    static final I18NLoggerProxy LOGGER = 
        new I18NLoggerProxy(PROVIDER);

    /*
     * The messages.
     */

    static final I18NMessage1P GROUP_NUMBER_COULD_NOT_TRANSLATE = 
        new I18NMessage1P(LOGGER,"group_number_could_not_translate"); //$NON-NLS-1$

    static final I18NMessage0P FIX_OUTGOING_NO_MSGTYPE = 
        new I18NMessage0P(LOGGER,"fix_outgoing_no_msgtype"); //$NON-NLS-1$
    static final I18NMessage0P FIX_MD_MERGE_INVALID_INCOMING_SNAPSHOT = 
        new I18NMessage0P(LOGGER,"fix_md_merge_invalid_incoming_snapshot"); //$NON-NLS-1$
    static final I18NMessage0P FIX_MD_MERGE_INVALID_INCOMING_INCREMENTAL = 
        new I18NMessage0P(LOGGER,"fix_md_merge_invalid_incoming_incremental"); //$NON-NLS-1$
    static final I18NMessage1P FIX_VERSION_UNSUPPORTED = 
        new I18NMessage1P(LOGGER,"fix_version_unsupported"); //$NON-NLS-1$
    static final I18NMessage1P FIX_FNF_NOMSG = 
        new I18NMessage1P(LOGGER,"fix_fnf_nomsg"); //$NON-NLS-1$
    static final I18NMessage2P FIX_FNF_MSG = 
        new I18NMessage2P(LOGGER,"fix_fnf_msg"); //$NON-NLS-1$

    static final I18NMessage0P ERROR_WRITING_EVENT_TO_LOG = 
        new I18NMessage0P(LOGGER,"error_writing_event_to_log"); //$NON-NLS-1$
    static final I18NMessage1P ERROR_COULD_NOT_CLEAR_LOG = 
        new I18NMessage1P(LOGGER,"error_could_not_clear_log"); //$NON-NLS-1$
    static final I18NMessage0P ERROR_COULD_NOT_CREATE_FIX_DATA_DICTIONARY = 
        new I18NMessage0P(LOGGER,"error_could_not_create_fix_data_dictionary"); //$NON-NLS-1$
    static final I18NMessage1P CANNOT_CREATE_FIX_FIELD = new I18NMessage1P(LOGGER,
                                                                           "cannot_create_fix_field"); //$NON-NLS-1$
}
