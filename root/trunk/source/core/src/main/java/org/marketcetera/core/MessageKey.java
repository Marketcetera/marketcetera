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
    JCYCLONE_SHUTDOWN_ERR("core.jcyclone.shutdown_err"),
    JCYCLONE_PLUGIN_INIT("core.jcyclone.plugin_init"),
    JCYCLONE_STAGE_INIT("core.jcyclone.stage_init"),
    JCYCLONE_UNEXPECTED_ELEM("core.jcyclone.unexpected_elem"),
    JCYCLONE_ERROR_OUTPUT_SEND("core.jcyclone.error_output_send"),
    JCYCLONE_SEND_NUM_ERRORS("core.jcyclone.output_send_num_errors"),
    JCYCLONE_ERROR_SEND_NEXT_STAGE("core.jcyclone.error_send_next_stage"),
    JMX_BEAN_FAILURE("core.init.jmx_reg_error"),


    JMS_ERROR("core.error.jms"),
    JMS_CLEAR_ERROR("core.jms.error_clearing"),
    JMS_CONNECTION_START_ERROR("core.jms.error_start_jms"),
    JMS_CONNECTION_CLOSE_ERROR("core.jms.error_connection_close"),
    JMS_QUEUE_DNE("core.jms.queue_not_found"),
    JMS_TOPIC_DNE("core.jms.topic_not_found"),
    JMS_QUEUE_CONNECT_ERROR("core.jms.queue_connect"),
    JMS_TOPIC_CONNECT_ERROR("core.jms.topic_connect"),

    FIX_FNF("core.error.fix.fnf"),
    FIX_OUTGOING_NO_MSGTYPE("core.error.fix.outgoing_no_msgtype"),
    FIX_UNEXPECTED_MSGTYPE("core.error.fix.unexpected_msgtype"),

    ERROR_EXCHANGES_INIT("core.error.exchanges_init"),
    ERROR_EXCHANGE_DNE("core.error_exchange_not_found"),

    CONFIG_ERROR("core.error.config"),
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

    DB_ID_FETCH("core.error.db_id_fetch"),
    FIX_SEND_ERROR("core.error.fix_send"),
    JMS_SEND_ERROR("core.error.jms_send"),

    HIBERNATE_CREATION_ERR("core.hibernate.error.creation"),

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

    public <T> String getLocalizedMessage(T[] args)
    {
        return getMessageString(toString(), args);
    }

    public <T> String getLocalizedMessage(T arg)
    {
        return getMessageString(toString(), new Object[] {arg});
    }

    /** Corresponds to the suffix in the message bundle file. Currently, we are not distinguishing between different
     * kids of entries (title, summary, detail, etc) so we just use 'msg'.
     */
    private static String MESSAGE_BUNDLE_ENTRY = "msg";

    public static String getMessageString(String inKey)
    {
        return MessageManager.getText(inKey, MESSAGE_BUNDLE_ENTRY,  new Object[0], Locale.getDefault());
    }
    public static <T> String getMessageString(String inKey, T[] args)
    {
        return MessageManager.getText(inKey, MESSAGE_BUNDLE_ENTRY,  args, Locale.getDefault());
    }
}


