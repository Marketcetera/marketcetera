package org.marketcetera.server.service.impl;

import org.marketcetera.util.log.*;

/* $License$ */

/**
 * 
 * 
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface Messages
{
    static final I18NMessageProvider PROVIDER = new I18NMessageProvider("server"); //$NON-NLS-1$
    static final I18NLoggerProxy LOGGER = new I18NLoggerProxy(PROVIDER);
    static final I18NMessage2P QF_SENDING_STATUS = new I18NMessage2P(LOGGER,
                                                                     "qf_sending_status"); //$NON-NLS-1$
    static final I18NMessage1P QF_SENDING_TRADE_RECORD = new I18NMessage1P(LOGGER,
                                                                           "qf_sending_trade_record"); //$NON-NLS-1$
    static final I18NMessage2P QF_REPORT_FAILED = new I18NMessage2P(LOGGER,
                                                                    "qf_report_failed"); //$NON-NLS-1$
    static final I18NMessage1P QF_SENDING_REPLY = new I18NMessage1P(LOGGER,
                                                                    "qf_sending_reply"); //$NON-NLS-1$
    static final I18NMessage2P QF_TO_ADMIN = new I18NMessage2P(LOGGER,
                                                               "qf_to_admin"); //$NON-NLS-1$
    static final I18NMessage2P QF_FROM_ADMIN = new I18NMessage2P(LOGGER,
                                                                 "qf_from_admin"); //$NON-NLS-1$
    static final I18NMessage2P QF_TO_APP = new I18NMessage2P(LOGGER,
                                                             "qf_to_app"); //$NON-NLS-1$
    static final I18NMessage2P QF_FROM_APP = new I18NMessage2P(LOGGER,
                                                               "qf_from_app"); //$NON-NLS-1$
    static final I18NMessage2P QF_MODIFICATION_FAILED = new I18NMessage2P(LOGGER,
                                                                          "qf_modification_failed"); //$NON-NLS-1$
    static final I18NMessage2P QF_IN_MESSAGE_REJECTED = new I18NMessage2P(LOGGER,
                                                                          "qf_in_message_rejected"); //$NON-NLS-1$
    static final I18NMessage0P QF_DISALLOWED_MESSAGE = new I18NMessage0P(LOGGER,
                                                                         "qf_disallowed_message"); //$NON-NLS-1$
    static final I18NMessage1P QF_TRADE_SESSION_STATUS = new I18NMessage1P(LOGGER,
                                                                           "qf_trade_session_status"); //$NON-NLS-1$
    static final I18NMessage1P QF_COMP_ID_REJECT = new I18NMessage1P(LOGGER,
                                                                     "qf_comp_id_reject"); //$NON-NLS-1$
    static final I18NMessage1P QF_COMP_ID_REJECT_FAILED = new I18NMessage1P(LOGGER,
                                                                            "qf_comp_id_reject_failed"); //$NON-NLS-1$
    static final I18NMessage1P ANALYZED_MESSAGE = new I18NMessage1P(LOGGER,
                                                                    "analyzed_message"); //$NON-NLS-1$
}
