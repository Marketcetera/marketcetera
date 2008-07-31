package org.marketcetera.core;

import org.apache.commons.i18n.MessageManager;

import java.util.Locale;

/**
 * Collection of enums corresponding to keys in message bundle files
 *
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$") //$NON-NLS-1$
public enum MessageKey implements LocalizedMessage {
    JMX_BEAN_FAILURE("core.init.jmx_reg_error"), //$NON-NLS-1$

    FIX_FNF("core.error.fix.fnf"), //$NON-NLS-1$
    FIX_OUTGOING_NO_MSGTYPE("core.error.fix.outgoing_no_msgtype"), //$NON-NLS-1$
    FIX_UNEXPECTED_MSGTYPE("core.error.fix.unexpected_msgtype"), //$NON-NLS-1$
    FIX_VERSION_UNSUPPORTED("core.error.fix.version_unsuppported"), //$NON-NLS-1$
    FIX_DICTIONARY_SET("core.fix.dictionary_set"), //$NON-NLS-1$
    FIX_MD_MERGE_INVALID_INCOMING_SNAPSHOT("core.fix.md_merge.invalid_snapshot"), //$NON-NLS-1$
    FIX_MD_MERGE_INVALID_INCOMING_INCREMENTAL("core.fix.md_merge.invalid_incremental"), //$NON-NLS-1$
    ERROR_FIX_DICT_ALREADY_INITIALIZED("core.fix.dictionary_already_set"), //$NON-NLS-1$
    ERROR_FIX_DICT_NOT_INITIALIZED("core.fix.dictionary_not_set"), //$NON-NLS-1$

    ERROR_EXCHANGES_INIT("core.error.exchanges_init"), //$NON-NLS-1$
    ERROR_EXCHANGE_DNE("core.error_exchange_not_found"), //$NON-NLS-1$

    CONFIG_ERROR("core.error.config"), //$NON-NLS-1$
    CONFIG_ERROR_REASON("core.error.config_reason"), //$NON-NLS-1$
    CONFIG_FILE_DNE("core.error.config_file_dne"), //$NON-NLS-1$
    CONFIG_FILE_OPEN("core.error.config_file_open"), //$NON-NLS-1$
    ERROR("core.error.error"), //$NON-NLS-1$
    ERROR_WITH_DETAILS("core.error.error_with_details"), //$NON-NLS-1$
    CLASS_DNE("core.error.class_dne"), //$NON-NLS-1$
    CLASS_DNE_NAME("core.error.class_name_dne"), //$NON-NLS-1$
    ERROR_JNDI_CREATE("core.error.jndi_create"), //$NON-NLS-1$
    ERROR_JNDI_CLOSE("core.error.jndi_close"), //$NON-NLS-1$
    ERROR_NULL_DELAYED_ITEM("core.error.null_delayed_item"), //$NON-NLS-1$
    DELAYED_ITEM_DESC("core.delayed_item_desc"), //$NON-NLS-1$
    IN_MEMORY_ID_FACTORY_OVERRUN("core.error.inmmemory_id_factory_overrun"), //$NON-NLS-1$
    ERROR_NULL_ID("core.error.null_id"), //$NON-NLS-1$
    ERROR_NULL_MSYMBOL("core.error.null_msymbol"), //$NON-NLS-1$
    ERROR_UNRECOGNIZED_ROUTE("core.error.unrecognized_route"), //$NON-NLS-1$
    ERROR_MSG_NOT_EXEC_REPORT("core.error.msg_must_be_exec_report"), //$NON-NLS-1$

    LOGGER_MISSING_CAT("core.logger.missing_cat"), //$NON-NLS-1$

    ERROR_DBFACTORY_HTTP_PARSER_INIT("core.error.dbfactory_http_parser_init"), //$NON-NLS-1$
    ERROR_DBFACTORY_MISSING_PARSER("core.error.dbfactory_missing_parser"), //$NON-NLS-1$
    ERROR_DBFACTORY_FAILED_INIT("core.error.dbfactory_init"), //$NON-NLS-1$

    ORDER_MODIFIER_WRONG_FIELD_FORMAT("core.init.ordermodifier.wrong_format"), //$NON-NLS-1$

    ERR0R_JMS_MESSAGE_CONVERSION("core.jms.message_conversion"), //$NON-NLS-1$

    ERROR_DB_ID_FACTORY_INIT("core.db_id_factory.init.error"), //$NON-NLS-1$
    APP_SHUTDOWN("core.init.app_shutdown"), //$NON-NLS-1$
    APP_START("core.init.app_start"), //$NON-NLS-1$
    APP_EXIT("core.init.app_exit"), //$NON-NLS-1$
    ERROR_RESOURCE_POOL_COULD_NOT_ALLOCATE_NEW_RESOURCE("core.resourcepool.could_not_allocate_new_resource.error"), //$NON-NLS-1$
    ERROR_RESOURCE_POOL_RESOURCE_ALREADY_RETURNED("core.resourcepool.resource_already_returned.error"), //$NON-NLS-1$
    ERROR_RESOURCE_POOL_SHUTTING_DOWN("core.resourcepool.resource_shutting_down.error"), //$NON-NLS-1$
    ERROR_CANNOT_CREATE_RESOURCE_FOR_POOL("core.resourcepool.cannot_create_resource_for_pool.error"), //$NON-NLS-1$
    ERROR_RESOURCE_POOL_EXECUTABLE_BLOCK_ERROR("core.resourcepool.executable_block_error"), //$NON-NLS-1$
    ERROR_RESOURCE_POOL_RESERVATION_CANCELLED("core.resourcepool.resource_reservation_cancelled"), //$NON-NLS-1$
    INFO_WAITING_FOR_RESOURCE("core.resourcepool.waiting_for_resource"), //$NON-NLS-1$
    /* market data feed messages*/
    ERROR_NO_ID_FOR_TOKEN("marketdatafeed.no_id_for_token"), //$NON-NLS-1$
    ERROR_MARKET_DATA_FEED_EXECUTION_FAILED("marketdatafeed.market_data_feed_execution_failed"), //$NON-NLS-1$
    ERROR_MARKET_DATA_FEED_CANNOT_GENERATE_MESSAGE("marketdatafeed.market_data_feed_cannot_generate_message"), //$NON-NLS-1$
    ERROR_MARKET_DATA_FEED_CANNOT_FIND_SYMBOL("marketdatafeed.market_data_feed_cannot_find_symbol"), //$NON-NLS-1$
    ERROR_MARKET_DATA_FEED_UNKNOWN_MESSAGE_TYPE("marketdatafeed.market_data_feed_unknown_message_type"), //$NON-NLS-1$
    WARNING_MARKET_DATA_FEED_CANNOT_DETERMINE_SUBSCRIPTION("marketdatafeed.market_data_feed_cannot_determine_subscription"), //$NON-NLS-1$
    WARNING_MARKET_DATA_FEED_CANNOT_CANCEL_SUBSCRIPTION("marketdatafeed.market_data_feed_cannot_cancel_subscription"), //$NON-NLS-1$
    WARNING_MARKET_DATA_FEED_DATA_IGNORED("marketdatafeed.market_data_feed_data_ignored"), //$NON-NLS-1$
    /* session messages */
    SESSION_NOT_FOUND("core.error.fix.session_not_found"); //$NON-NLS-1$
    
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
    private static String MESSAGE_BUNDLE_ENTRY = "msg"; //$NON-NLS-1$

    public static String getMessageString(String inKey)
    {
        return MessageManager.getText(inKey, MESSAGE_BUNDLE_ENTRY,  null, Locale.getDefault());
    }

    public static String getMessageString(String inKey, Object... args)
    {
        return MessageManager.getText(inKey, MESSAGE_BUNDLE_ENTRY,  args, Locale.getDefault());
    }
}
