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
    FIX_FIELD_ALREADY_SET("core.error.fix.field_already_set"),
    FIX_OUTGOING_NO_MSGTYPE("core.error.fix.outgoing_no_msgtype"),
    FIX_UNEXPECTED_MSGTYPE("core.error.fix.unexpected_msgtype"),
    FIX_VERSION_UNSUPPORTED("core.error.fix.version_unsuppported"),
    FIX_DICTIONARY_SET("core.fix.dictionary_set"),
    FIX_MD_MERGE_INVALID_INCOMING_SNAPSHOT("core.fix.md_merge.invalid_snapshot"),
    FIX_MD_MERGE_INVALID_INCOMING_INCREMENTAL("core.fix.md_merge.invalid_incremental"),
    ERROR_FIX_DICT_ALREADY_INITIALIZED("core.fix.dictionary_already_set"),
    ERROR_FIX_DICT_NOT_INITIALIZED("core.fix.dictionary_not_set"),

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
    ERROR_UNRECOGNIZED_ROUTE("core.error.unrecognized_route"),
    ERROR_MSG_NOT_EXEC_REPORT("core.error.msg_must_be_exec_report"),

    LOGGER_MISSING_CAT("core.logger.missing_cat"),
    LOGGER_INIT("core.logger.init"),

    ERROR_DBFACTORY_HTTP_PARSER_INIT("core.error.dbfactory_http_parser_init"),
    ERROR_DBFACTORY_MISSING_PARSER("core.error.dbfactory_missing_parser"),
    ERROR_DBFACTORY_FAILED_INIT("core.error.dbfactory_init"),

    ORDER_MODIFIER_WRONG_FIELD_FORMAT("core.init.ordermodifier.wrong_format"),

    ERR0R_JMS_MESSAGE_CONVERSION("core.jms.message_conversion"),

    ERROR_DB_ID_FACTORY_INIT("core.db_id_factory.init.error"), 
    ERROR_DB_ID_FACTORY_DB_CONN_ERROR("core.db_id_factory.db_conn.error"),
    APP_SHUTDOWN("core.init.app_shutdown"),
    APP_START("core.init.app_start"),
    APP_EXIT("core.init.app_exit"),
    ERROR_RESOURCE_POOL_COULD_NOT_ALLOCATE_NEW_RESOURCE("core.resourcepool.could_not_allocate_new_resource.error"),
    ERROR_RESOURCE_POOL_RESOURCE_ALREADY_RETURNED("core.resourcepool.resource_already_returned.error"),
    ERROR_RESOURCE_POOL_SHUTTING_DOWN("core.resourcepool.resource_shutting_down.error"),
    ERROR_CANNOT_CREATE_RESOURCE_FOR_POOL("core.resourcepool.cannot_create_resource_for_pool.error"),
    ERROR_RESOURCE_POOL_EXECUTABLE_BLOCK_ERROR("core.resourcepool.executable_block_error"),
    ERROR_RESOURCE_POOL_RESERVATION_CANCELLED("core.resourcepool.resource_reservation_cancelled"),
    INFO_WAITING_FOR_RESOURCE("core.resourcepool.waiting_for_resource"),
    /* market data feed messages*/
    ERROR_NO_ID_FOR_TOKEN("marketdatafeed.no_id_for_token"),
    ERROR_MARKET_DATA_FEED_EXECUTION_FAILED("marketdatafeed.market_data_feed_execution_failed"),
    ERROR_MARKET_DATA_FEED_CANNOT_GENERATE_MESSAGE("marketdatafeed.market_data_feed_cannot_generate_message"),
    ERROR_MARKET_DATA_FEED_CANNOT_FIND_SYMBOL("marketdatafeed.market_data_feed_cannot_find_symbol"),
    ERROR_MARKET_DATA_FEED_UNKNOWN_MESSAGE_TYPE("marketdatafeed.market_data_feed_unknown_message_type"),
    WARNING_MARKET_DATA_FEED_CANNOT_DETERMINE_SUBSCRIPTION("marketdatafeed.market_data_feed_cannot_determine_subscription"),
    WARNING_MARKET_DATA_FEED_CANNOT_CANCEL_SUBSCRIPTION("marketdatafeed.market_data_feed_cannot_cancel_subscription"),
    WARNING_MARKET_DATA_FEED_DATA_IGNORED("marketdatafeed.market_data_feed_data_ignored"),
    /* session messages */
    SESSION_NOT_FOUND("core.error.fix.session_not_found");
    
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
     * Corresponds to the prefix in the message bundle file. Currently, we are not distinguishing between different
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
