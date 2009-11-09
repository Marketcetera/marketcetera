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
            new I18NMessageProvider("ors_security"); //$NON-NLS-1$
    /**
     * The message logger.
     */
    static final I18NLoggerProxy LOGGER =
            new I18NLoggerProxy(PROVIDER);
    static final I18NMessage0P EMPTY_USERNAME =
            new I18NMessage0P(LOGGER, "empty_username");  //$NON-NLS-1$
    static final I18NMessage1P CANNOT_SET_PASSWORD =
            new I18NMessage1P(LOGGER, "cannot_set_password"); //$NON-NLS-1$
    static final I18NMessage0P EMPTY_PASSWORD =
            new I18NMessage0P(LOGGER, "empty_password"); //$NON-NLS-1$
    static final I18NMessage0P INVALID_PASSWORD =
            new I18NMessage0P(LOGGER, "invalid_password"); //$NON-NLS-1$
    static final I18NMessage0P PROMPT_USERNAME =
            new I18NMessage0P(LOGGER, "prompt_username"); //$NON-NLS-1$
    static final I18NMessage0P PROMPT_PASSWORD =
            new I18NMessage0P(LOGGER, "prompt_password"); //$NON-NLS-1$
    static final I18NMessage0P USER_LOGIN_ERROR =
            new I18NMessage0P(LOGGER, "user_login_error"); //$NON-NLS-1$
    static final I18NMessage1P USER_LOGIN_ERROR_LOG =
            new I18NMessage1P(LOGGER, "user_login_error_log"); //$NON-NLS-1$
    static final I18NMessage1P USER_LOGIN_LOG =
            new I18NMessage1P(LOGGER, "user_login_log"); //$NON-NLS-1$
    static final I18NMessage1P USER_LOGOUT_LOG =
            new I18NMessage1P(LOGGER, "user_logout_log"); //$NON-NLS-1$
    static final I18NMessage0P CLI_PARM_USER =
            new I18NMessage0P(LOGGER, "cli_parm_user"); //$NON-NLS-1$
    static final I18NMessage0P CLI_PARM_PASSWORD =
            new I18NMessage0P(LOGGER, "cli_parm_password"); //$NON-NLS-1$
    static final I18NMessage0P CLI_CMD_LIST_USERS =
            new I18NMessage0P(LOGGER, "cli_cmd_list_users"); //$NON-NLS-1$
    static final I18NMessage0P CLI_CMD_ADD_USER =
            new I18NMessage0P(LOGGER, "cli_cmd_add_user"); //$NON-NLS-1$
    static final I18NMessage0P CLI_CMD_DELETE_USER =
            new I18NMessage0P(LOGGER, "cli_cmd_delete_user"); //$NON-NLS-1$
    static final I18NMessage0P CLI_CMD_RESTORE_USER =
            new I18NMessage0P(LOGGER, "cli_cmd_restore_user"); //$NON-NLS-1$
    static final I18NMessage0P CLI_CMD_CHANGE_PASSWORD =
            new I18NMessage0P(LOGGER, "cli_cmd_change_password"); //$NON-NLS-1$
    static final I18NMessage0P CLI_CMD_CHANGE_SUPERUSER =
            new I18NMessage0P(LOGGER, "cli_cmd_change_superuser"); //$NON-NLS-1$
    static final I18NMessage0P CLI_PARM_OP_USER =
            new I18NMessage0P(LOGGER, "cli_parm_op_user"); //$NON-NLS-1$
    static final I18NMessage0P CLI_PARM_OP_PASSWORD =
            new I18NMessage0P(LOGGER, "cli_parm_op_password"); //$NON-NLS-1$
    static final I18NMessage0P CLI_PARM_OP_SUPERUSER =
            new I18NMessage0P(LOGGER, "cli_parm_op_superuser"); //$NON-NLS-1$
    static final I18NMessage0P CLI_PARM_OP_ACTIVE =
            new I18NMessage0P(LOGGER, "cli_parm_op_active"); //$NON-NLS-1$
    static final I18NMessage1P CLI_ERR_OPTION_MISSING =
            new I18NMessage1P(LOGGER, "cli_err_option_missing"); //$NON-NLS-1$
    static final I18NMessage1P CLI_OUT_USER_CREATED =
            new I18NMessage1P(LOGGER, "cli_out_user_created"); //$NON-NLS-1$
    static final I18NMessage1P CLI_OUT_USER_DELETED =
            new I18NMessage1P(LOGGER, "cli_out_user_deleted"); //$NON-NLS-1$
    static final I18NMessage1P CLI_OUT_USER_RESTORED =
            new I18NMessage1P(LOGGER, "cli_out_user_restored"); //$NON-NLS-1$
    static final I18NMessage1P CLI_OUT_USER_CHG_PASS =
            new I18NMessage1P(LOGGER, "cli_out_user_chg_pass"); //$NON-NLS-1$
    static final I18NMessage1P CLI_OUT_USER_CHG_SUPERUSER =
            new I18NMessage1P(LOGGER, "cli_out_user_chg_superuser"); //$NON-NLS-1$
    static final I18NMessage0P CLI_UNAUTHORIZED_ACTION =
            new I18NMessage0P(LOGGER, "cli_unauthorized_action"); //$NON-NLS-1$
    static final I18NMessage1P CLI_ERR_UNAUTH_DELETE =
            new I18NMessage1P(LOGGER, "cli_err_unauth_delete"); //$NON-NLS-1$
    static final I18NMessage1P CLI_ERR_UNAUTH_RESTORE =
            new I18NMessage1P(LOGGER, "cli_err_unauth_restore"); //$NON-NLS-1$
    static final I18NMessage1P CLI_ERR_UNAUTH_CHANGE_SUPERUSER =
            new I18NMessage1P(LOGGER, "cli_err_unauth_change_superuser"); //$NON-NLS-1$
    static final I18NMessage0P CLI_ERR_INVALID_LOGIN =
            new I18NMessage0P(LOGGER, "cli_err_invalid_login"); //$NON-NLS-1$
    static final I18NMessage0P CLI_ERR_INACTIVE_USER =
            new I18NMessage0P(LOGGER, "cli_err_inactive_user"); //$NON-NLS-1$
    static final I18NMessage0P CLI_ARG_LOGIN_VALUE =
            new I18NMessage0P(LOGGER, "cli_arg_login_value"); //$NON-NLS-1$
    static final I18NMessage0P CLI_ARG_LOGIN_PASSWORD_VALUE =
            new I18NMessage0P(LOGGER, "cli_arg_login_password_value"); //$NON-NLS-1$
    static final I18NMessage0P CLI_ARG_USER_NAME_VALUE =
            new I18NMessage0P(LOGGER, "cli_arg_user_name_value"); //$NON-NLS-1$
    static final I18NMessage0P CLI_ARG_USER_PASSWORD_VALUE =
            new I18NMessage0P(LOGGER, "cli_arg_user_password_value"); //$NON-NLS-1$
    static final I18NMessage0P CLI_ARG_USER_SUPERUSER_VALUE =
            new I18NMessage0P(LOGGER, "cli_arg_user_superuser_value"); //$NON-NLS-1$
    static final I18NMessage0P CLI_ARG_USER_ACTIVE_VALUE =
            new I18NMessage0P(LOGGER, "cli_arg_user_active_value"); //$NON-NLS-1$
    static final I18NMessage0P CLI_DESC_OPTIONS_HEADER =
            new I18NMessage0P(LOGGER, "cli_desc_options_header"); //$NON-NLS-1$
    static final I18NMessage1P CLI_PROMPT_PASSWORD =
            new I18NMessage1P(LOGGER, "cli_prompt_password"); //$NON-NLS-1$
    static final I18NMessage1P CLI_PROMPT_NEW_PASSWORD =
            new I18NMessage1P(LOGGER, "cli_prompt_new_password"); //$NON-NLS-1$
    static final I18NMessage0P SIMPLE_USER_NAME =
            new I18NMessage0P(LOGGER, "simple_user_name"); //$NON-NLS-1$
}
