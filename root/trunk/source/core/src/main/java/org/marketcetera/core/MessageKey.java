package org.marketcetera.core;

import org.apache.commons.i18n.MessageManager;

import java.util.Locale;

/**
 * Collection of enums corresponding to keys in message bundle files
 *
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
public enum MessageKey implements LocalizedMessage {
    JMX_BEAN_FAILURE("core.init.jmx_reg_error"),

    FIX_FNF("core.error.fix.fnf"),
    FIX_OUTGOING_NO_MSGTYPE("core.error.fix.outgoing_no_msgtype"),
    FIX_UNEXPECTED_MSGTYPE("core.error.fix.unexpected_msgtype"),

    ERROR_EXCHANGES_INIT("core.error.exchanges_init"),
    ERROR_EXCHANGE_DNE("core.error_exchange_not_found"),

    CONFIG_ERROR("core.error.config"),
    CONFIG_ERROR_REASON("core.error.config_reason"),
    CONFIG_FILE_DNE("core.error.config_file_dne"),
    CONFIG_FILE_OPEN("core.error.config_file_open"),
    ERROR("core.error.error"),
    ERROR_WITH_DETAILS("core.error.error_with_details"),
    CLASS_DNE("core.error.class_dne"),
    CLASS_DNE_NAME("core.error.class_name_dne"),
    ERROR_JNDI_CREATE("core.error.jndi_create"),
    ERROR_JNDI_CLOSE("core.error.jndi_close"),
    ERROR_NULL_DELAYED_ITEM("core.error.null_delayed_item"),
    DELAYED_ITEM_DESC("core.delayed_item_desc"),
    IN_MEMORY_ID_FACTORY_OVERRUN("core.error.inmmemory_id_factory_overrun"),
    ERROR_NULL_ID("core.error.null_id"),
    ERROR_NULL_MSYMBOL("core.error.null_msymbol"),

    LOGGER_MISSING_CAT("core.logger.missing_cat"),
    LOGGER_INIT("core.logger.init"),

    ERROR_DBFACTORY_HTTP_PARSER_INIT("core.error.dbfactory_http_parser_init"),
    ERROR_DBFACTORY_MISSING_PARSER("core.error.dbfactory_missing_parser"),
    ERROR_DBFACTORY_FAILED_INIT("core.error.dbfactory_init"),

    ORDER_MODIFIER_WRONG_FIELD_FORMAT("core.init.ordermodifier.wrong_format"),

    ERR0R_JMS_MESSAGE_CONVERSION("core.jms.message_conversion"),

    APP_SHUTDOWN("core.init.app_shutdown"),
    APP_START("core.init.app_start"),
    APP_EXIT("core.init.app_exit");

    private MessageKey(String inKey) {
        key = inKey;
    }

    private final String key;

    public String toString() {
        return key;
    }

    public String getLocalizedMessage()
    {
        return getMessageString(toString());
    }

    public String getLocalizedMessage(Object ... args)
    {
        return getMessageString(toString(), args);
    }

    /**
     * Corresponds to the suffix in the message bundle file. Currently, we are not distinguishing between different
     * kids of entries (title, summary, detail, etc) so we just use 'msg'.
     */
    private static String MESSAGE_BUNDLE_ENTRY = "msg";

    public static String getMessageString(String inKey)
    {
        return MessageManager.getText(inKey, MESSAGE_BUNDLE_ENTRY,  null, Locale.getDefault());
    }

    public static String getMessageString(String inKey, Object... args)
    {
        return MessageManager.getText(inKey, MESSAGE_BUNDLE_ENTRY,  args, Locale.getDefault());
    }
}


