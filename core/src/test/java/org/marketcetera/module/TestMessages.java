package org.marketcetera.module;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.log.*;

/* $License$ */
/**
 * Messages used by test code.
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$")
public interface TestMessages {
    /**
     * The message provider
     */
    static final I18NMessageProvider PROVIDER =
            new I18NMessageProvider("core_module_test");  //$NON-NLS-1$
    /**
     * The message logger.
     */
    static final I18NLoggerProxy LOGGER =
            new I18NLoggerProxy(PROVIDER);
    static final I18NMessage0P SINGLE_1_PROVIDER =
            new I18NMessage0P(LOGGER, "single_1_provider");  //$NON-NLS-1$
    static final I18NMessage0P MULTIPLE_1_PROVIDER =
            new I18NMessage0P(LOGGER, "multiple_1_provider");  //$NON-NLS-1$
    static final I18NMessage0P MULTIPLE_2_PROVIDER =
            new I18NMessage0P(LOGGER, "multiple_2_provider");  //$NON-NLS-1$
    static final I18NMessage1P INCORRECT_FILE_PATH =
            new I18NMessage1P(LOGGER, "incorrect_file_path");  //$NON-NLS-1$
    static final I18NMessage1P INCORRECT_URL =
            new I18NMessage1P(LOGGER, "incorrect_url");  //$NON-NLS-1$
    static final I18NMessage1P DATE_NOT_SUPPLIED =
            new I18NMessage1P(LOGGER, "date_not_supplied");  //$NON-NLS-1$
    static final I18NMessage0P TEST_START_STOP_FAILURE =
            new I18NMessage0P(LOGGER, "test_start_stop_failure");  //$NON-NLS-1$
    static final I18NMessage0P MULTIPLE_3_PROVIDER =
            new I18NMessage0P(LOGGER,"multiple_3_provider");  //$NON-NLS-1$
    static final I18NMessage0P MULTIPLE_4_PROVIDER = new
            I18NMessage0P(LOGGER, "multiple_4_provider");  //$NON-NLS-1$
    static final I18NMessage0P EMIT_PROVIDER =
            new I18NMessage0P(LOGGER, "emit_provider");  //$NON-NLS-1$
    static final I18NMessage0P PROCESSOR_PROVIDER =
            new I18NMessage0P(LOGGER, "processor_provider");  //$NON-NLS-1$
    static final I18NMessage1P ERROR_PARAMETER_VALUE =
            new I18NMessage1P(LOGGER, "error_parameter_value");  //$NON-NLS-1$
    static final I18NMessage0P BAD_DATA =
            new I18NMessage0P(LOGGER, "bad_data");  //$NON-NLS-1$
    static final I18NMessage0P EMIT_DATA_ERROR =
            new I18NMessage0P(LOGGER, "emit_data_error");  //$NON-NLS-1$
    static final I18NMessage0P STOP_DATA_FLOW =
            new I18NMessage0P(LOGGER, "stop_data_flow");  //$NON-NLS-1$
    static final I18NMessage0P FLOW_REQUESTER_PROVIDER =
            new I18NMessage0P(LOGGER, "flow_requester_provider");  //$NON-NLS-1$
    static final I18NMessage2P EXCEPTION_TEST =
            new I18NMessage2P(LOGGER, "exception_test");   //$NON-NLS-1$
    static final I18NMessage0P FAILURE =
            new I18NMessage0P(LOGGER, "failure");   //$NON-NLS-1$
    static final I18NMessage0P INCORRECT_SEMAPHORE_STATE =
            new I18NMessage0P(LOGGER, "incorrect_semaphore_state");   //$NON-NLS-1$

}
