package org.marketcetera.strategyagent;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.log.*;

/* $License$ */
/**
 * Messages for the strategy agent.
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public interface Messages {
    /**
     * The message provider
     */
    static final I18NMessageProvider PROVIDER =
            new I18NMessageProvider("strategy_agent");  //$NON-NLS-1$
    /**
     * The message logger.
     */
    static final I18NLoggerProxy LOGGER =
            new I18NLoggerProxy(PROVIDER);
    static final I18NMessage4P LOG_SINK_DATA =
            new I18NMessage4P(LOGGER, "log_sink_data");   //$NON-NLS-1$
    static final I18NMessage1P LOG_ERROR_CONFIGURE_AGENT =
            new I18NMessage1P(LOGGER, "log_error_configure_agent");  //$NON-NLS-1$
    static final I18NMessage1P LOG_ERROR_INITIALIZING_AGENT =
            new I18NMessage1P(LOGGER, "log_error_initializing_agent");  //$NON-NLS-1$
    static final I18NMessage4P LOG_ERROR_EXEC_CMD =
            new I18NMessage4P(LOGGER, "log_error_exec_cmd");  //$NON-NLS-1$
    static final I18NMessage2P INVALID_COMMAND_NAME =
            new I18NMessage2P(LOGGER, "invalid_command_name");  //$NON-NLS-1$
    static final I18NMessage2P INVALID_COMMAND_SYNTAX =
            new I18NMessage2P(LOGGER, "invalid_command_syntax");  //$NON-NLS-1$
    static final I18NMessage1P LOG_COMMAND_PARSE_ERRORS =
            new I18NMessage1P(LOGGER, "log_command_parse_errors");  //$NON-NLS-1$
    static final I18NMessage1P LOG_JAR_LOADER_INIT =
            new I18NMessage1P(LOGGER, "log_jar_loader_init");  //$NON-NLS-1$
    static final I18NMessage1P LOG_JAR_LOADER_ADD_URL =
            new I18NMessage1P(LOGGER, "log_jar_loader_add_url");  //$NON-NLS-1$
    static final I18NMessage0P LOG_REFRESH_JAR_LOADER =
            new I18NMessage0P(LOGGER, "log_refresh_jar_loader");  //$NON-NLS-1$
    static final I18NMessage1P JAR_DIR_DOES_NOT_EXIST =
            new I18NMessage1P(LOGGER, "jar_dir_does_not_exist");   //$NON-NLS-1$
    static final I18NMessage2P LOG_RUNNING_COMMAND =
            new I18NMessage2P(LOGGER, "log_running_command");  //$NON-NLS-1$
    static final I18NMessage2P LOG_COMMAND_RUN_RESULT =
            new I18NMessage2P(LOGGER, "log_command_run_result");  //$NON-NLS-1$
    static final I18NMessage1P CREATE_MODULE_INVALID_SYNTAX =
            new I18NMessage1P(LOGGER, "create_module_invalid_syntax");  //$NON-NLS-1$
    static final I18NMessage0P LOG_APP_COPYRIGHT =
            new I18NMessage0P(LOGGER, "log_app_copyright");   //$NON-NLS-1$
    static final I18NMessage2P LOG_APP_VERSION_BUILD =
            new I18NMessage2P(LOGGER, "log_app_version_build");   //$NON-NLS-1$
}
