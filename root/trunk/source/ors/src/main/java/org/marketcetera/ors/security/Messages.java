package org.marketcetera.ors.security;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.util.log.*;

/* $License$ */
/**
 * Messages
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$")
public interface Messages {
    /**
     * The message provider
     */
    static final I18NMessageProvider PROVIDER =
            new I18NMessageProvider("security");
    /**
     * The message logger.
     */
    static final I18NLoggerProxy LOGGER =
            new I18NLoggerProxy(PROVIDER);
    static final I18NMessage0P EMPTY_USERNAME =
            new I18NMessage0P(LOGGER, "empty_username"); 
    static final I18NMessage1P CANNOT_SET_PASSWORD =
            new I18NMessage1P(LOGGER, "cannot_set_password");
    static final I18NMessage0P EMPTY_PASSWORD =
            new I18NMessage0P(LOGGER, "empty_password");
    static final I18NMessage0P PASSWORD_NOT_SET =
            new I18NMessage0P(LOGGER, "password_not_set");
    static final I18NMessage0P INVALID_PASSWORD =
            new I18NMessage0P(LOGGER, "invalid_password");
    static final I18NMessage0P PROMPT_USERNAME =
            new I18NMessage0P(LOGGER, "prompt_username");
    static final I18NMessage0P PROMPT_PASSWORD =
            new I18NMessage0P(LOGGER, "prompt_password");
    static final I18NMessage0P USER_LOGIN_ERROR =
            new I18NMessage0P(LOGGER, "user_login_error");
    static final I18NMessage1P USER_LOGIN_ERROR_LOG =
            new I18NMessage1P(LOGGER, "user_login_error_log");
    static final I18NMessage1P USER_LOGIN_LOG =
            new I18NMessage1P(LOGGER, "user_login_log");
    static final I18NMessage1P USER_LOGOUT_LOG =
            new I18NMessage1P(LOGGER, "user_logout_log");
    static final I18NMessage0P CLI_PARM_USER =
            new I18NMessage0P(LOGGER, "cli_parm_user");
    static final I18NMessage0P CLI_PARM_PASSWORD =
            new I18NMessage0P(LOGGER, "cli_parm_password");
    static final I18NMessage0P CLI_CMD_LIST_USERS =
            new I18NMessage0P(LOGGER, "cli_cmd_list_users");
    static final I18NMessage0P CLI_CMD_ADD_USER =
            new I18NMessage0P(LOGGER, "cli_cmd_add_user");
    static final I18NMessage0P CLI_CMD_DELETE_USER =
            new I18NMessage0P(LOGGER, "cli_cmd_delete_user");
    static final I18NMessage0P CLI_CMD_CHANGE_PASSWORD =
            new I18NMessage0P(LOGGER, "cli_cmd_change_password");
    static final I18NMessage0P CLI_PARM_OP_USER =
            new I18NMessage0P(LOGGER, "cli_parm_op_user");
    static final I18NMessage0P CLI_PARM_OP_PASSWORD =
            new I18NMessage0P(LOGGER, "cli_parm_op_password");
    static final I18NMessage1P CLI_ERR_OPTION_MISSING =
            new I18NMessage1P(LOGGER, "cli_err_option_missing");
    static final I18NMessage1P CLI_OUT_USER_CREATED =
            new I18NMessage1P(LOGGER, "cli_out_user_created");
    static final I18NMessage1P CLI_OUT_USER_DELETED =
            new I18NMessage1P(LOGGER, "cli_out_user_deleted");
    static final I18NMessage1P CLI_OUT_USER_CHG_PASS =
            new I18NMessage1P(LOGGER, "cli_out_user_chg_pass");
    static final I18NMessage0P CLI_UNAUTHORIZED_ACTION =
            new I18NMessage0P(LOGGER, "cli_unauthorized_action");
    static final I18NMessage1P CLI_ERR_UNAUTH_DELETE =
            new I18NMessage1P(LOGGER, "cli_err_unauth_delete");
    static final I18NMessage0P CLI_ERR_INVALID_LOGIN =
            new I18NMessage0P(LOGGER, "cli_err_invalid_login");
    static final I18NMessage0P CLI_ARG_LOGIN_VALUE =
            new I18NMessage0P(LOGGER, "cli_arg_login_value");
    static final I18NMessage0P CLI_ARG_LOGIN_PASSWORD_VALUE =
            new I18NMessage0P(LOGGER, "cli_arg_login_password_value");
    static final I18NMessage0P CLI_ARG_USER_NAME_VALUE =
            new I18NMessage0P(LOGGER, "cli_arg_user_name_value");
    static final I18NMessage0P CLI_ARG_USER_PASSWORD_VALUE =
            new I18NMessage0P(LOGGER, "cli_arg_user_password_value");
    static final I18NMessage0P CLI_DESC_OPTIONS_HEADER =
            new I18NMessage0P(LOGGER, "cli_desc_options_header");
    static final I18NMessage1P CLI_PROMPT_PASSWORD =
            new I18NMessage1P(LOGGER, "cli_prompt_password");
    static final I18NMessage1P CLI_PROMPT_NEW_PASSWORD =
            new I18NMessage1P(LOGGER, "cli_prompt_new_password");
}
