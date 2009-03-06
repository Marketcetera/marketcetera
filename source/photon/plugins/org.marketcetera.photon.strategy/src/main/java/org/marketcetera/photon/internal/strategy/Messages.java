package org.marketcetera.photon.internal.strategy;

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
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")//$NON-NLS-1$
public interface Messages {
	/**
	 * The message provider.
	 */
	static final I18NMessageProvider PROVIDER = new I18NMessageProvider(
			"photon_strategy"); //$NON-NLS-1$

	/**
	 * The logger.
	 */
	static final I18NLoggerProxy LOGGER = new I18NLoggerProxy(PROVIDER);

	/*
	 * UI Text
	 */
	static final I18NMessage0P STRATEGYUI_FILE_LABEL = new I18NMessage0P(
			LOGGER, "strategyui.file.label"); //$NON-NLS-1$
	static final I18NMessage0P STRATEGYUI_FILE_TOOLTIP = new I18NMessage0P(
			LOGGER, "strategyui.file.tooltip"); //$NON-NLS-1$
	static final I18NMessage0P STRATEGYUI_CLASS_LABEL = new I18NMessage0P(
			LOGGER, "strategyui.class.label"); //$NON-NLS-1$
	static final I18NMessage0P STRATEGYUI_CLASS_TOOLTIP = new I18NMessage0P(
			LOGGER, "strategyui.class.tooltip"); //$NON-NLS-1$
	static final I18NMessage0P STRATEGYUI_DISPLAY_NAME_LABEL = new I18NMessage0P(
			LOGGER, "strategyui.display_name.label"); //$NON-NLS-1$
	static final I18NMessage0P STRATEGYUI_DISPLAY_NAME_TOOLTIP = new I18NMessage0P(
			LOGGER, "strategyui.display_name.tooltip"); //$NON-NLS-1$
	static final I18NMessage0P STRATEGYUI_ROUTE_TO_SERVER_LABEL = new I18NMessage0P(
			LOGGER, "strategyui.route_to_server.label"); //$NON-NLS-1$
	static final I18NMessage0P STRATEGYUI_ROUTE_TO_SERVER_TOOLTIP = new I18NMessage0P(
			LOGGER, "strategyui.route_to_server.tooltip"); //$NON-NLS-1$
	static final I18NMessage0P REGISTER_RUBY_STRATEGY_TITLE = new I18NMessage0P(
			LOGGER, "register_ruby_strategy.title"); //$NON-NLS-1$
	static final I18NMessage0P REGISTER_RUBY_STRATEGY_DESCRIPTION = new I18NMessage0P(
			LOGGER, "register_ruby_strategy.description"); //$NON-NLS-1$
	static final I18NMessage0P REGISTER_RUBY_STRATEGY_CONFIGURATION_GROUP_LABEL = new I18NMessage0P(
			LOGGER, "register_ruby_strategy.configuration_group_label"); //$NON-NLS-1$
	static final I18NMessage1P STRATEGY_VALIDATION_NAME_NOT_UNIQUE = new I18NMessage1P(
			LOGGER, "strategy_validation.name_not_unique"); //$NON-NLS-1$
	static final I18NMessage1P STRATEGY_VALIDATION_REQUIRED_FIELD_BLANK = new I18NMessage1P(
			LOGGER, "strategy_validation.required_field_blank"); //$NON-NLS-1$
	static final I18NMessage0P NEW_RUBY_STRATEGY_GENERIC_EXCEPTION_MESSAGE = new I18NMessage0P(
			LOGGER, "new_ruby_strategy.generic_exception_message"); //$NON-NLS-1$
	static final I18NMessage0P NEW_RUBY_STRATEGY_ERROR_DIALOG_TITLE = new I18NMessage0P(
			LOGGER, "new_ruby_strategy.error_dialog_title"); //$NON-NLS-1$
	static final I18NMessage1P NEW_RUBY_STRATEGY_CREATING_FILE = new I18NMessage1P(
			LOGGER, "new_ruby_strategy.creating_file"); //$NON-NLS-1$
	static final I18NMessage1P NEW_RUBY_STRATEGY_MISSING_CONTAINER = new I18NMessage1P(
			LOGGER, "new_ruby_strategy.missing_container"); //$NON-NLS-1$
	static final I18NMessage0P NEW_RUBY_STRATEGY_OPENING_FILE = new I18NMessage0P(
			LOGGER, "new_ruby_strategy.opening_file"); //$NON-NLS-1$
	static final I18NMessage0P NEW_RUBY_STRATEGY_TITLE = new I18NMessage0P(
			LOGGER, "new_ruby_strategy.title"); //$NON-NLS-1$
	static final I18NMessage0P NEW_RUBY_STRATEGY_DESCRIPTION = new I18NMessage0P(
			LOGGER, "new_ruby_strategy.description"); //$NON-NLS-1$
	static final I18NMessage0P NEW_RUBY_STRATEGY_CONTAINER_LABEL = new I18NMessage0P(
			LOGGER, "new_ruby_strategy.container.label"); //$NON-NLS-1$
	static final I18NMessage0P NEW_RUBY_STRATEGY_BROWSE_LABEL = new I18NMessage0P(
			LOGGER, "new_ruby_strategy.browse.label"); //$NON-NLS-1$
	static final I18NMessage0P NEW_RUBY_STRATEGY_CLASS_NAME_LABEL = new I18NMessage0P(
			LOGGER, "new_ruby_strategy.class_name.label"); //$NON-NLS-1$
	static final I18NMessage0P NEW_RUBY_STRATEGY_CONTAINER_SELECTION_INSTRUCTIONS = new I18NMessage0P(
			LOGGER, "new_ruby_strategy.container_selection_instructions"); //$NON-NLS-1$
	static final I18NMessage1P NEW_RUBY_STRATEGY_FILE_EXISTS = new I18NMessage1P(
			LOGGER, "new_ruby_strategy.file_exists"); //$NON-NLS-1$
	static final I18NMessage0P NEW_RUBY_STRATEGY_INVALID_CLASS_NAME = new I18NMessage0P(
			LOGGER, "new_ruby_strategy.invalid_class_name"); //$NON-NLS-1$
	static final I18NMessage0P STRATEGY_PROPERTIES_ADD_LABEL = new I18NMessage0P(
			LOGGER, "strategy_properties.add.label"); //$NON-NLS-1$
	static final I18NMessage0P STRATEGY_PROPERTIES_ADD_BUTTON_LABEL = new I18NMessage0P(
			LOGGER, "strategy_properties.add_button.label"); //$NON-NLS-1$
	static final I18NMessage0P STRATEGY_PROPERTIES_DELETE_LABEL = new I18NMessage0P(
			LOGGER, "strategy_properties.delete.label"); //$NON-NLS-1$
	static final I18NMessage0P STRATEGY_PROPERTIES_PARAMETERS_DESCRIPTION = new I18NMessage0P(
			LOGGER, "strategy_properties.parameters.description"); //$NON-NLS-1$
	static final I18NMessage0P TRADE_SUGGESTION_IDENTIFIER_LABEL = new I18NMessage0P(
			LOGGER, "trade_suggestion.identifier.label"); //$NON-NLS-1$
	static final I18NMessage0P TRADE_SUGGESTION_SIDE_LABEL = new I18NMessage0P(
			LOGGER, "trade_suggestion.side.label"); //$NON-NLS-1$
	static final I18NMessage0P TRADE_SUGGESTION_SECURITY_TYPE_LABEL = new I18NMessage0P(
			LOGGER, "trade_suggestion.security_type.label"); //$NON-NLS-1$
	static final I18NMessage0P TRADE_SUGGESTION_QUANTITY_LABEL = new I18NMessage0P(
			LOGGER, "trade_suggestion.quantity.label"); //$NON-NLS-1$
	static final I18NMessage0P TRADE_SUGGESTION_SYMBOL_LABEL = new I18NMessage0P(
			LOGGER, "trade_suggestion.symbol.label"); //$NON-NLS-1$
	static final I18NMessage0P TRADE_SUGGESTION_PRICE_LABEL = new I18NMessage0P(
			LOGGER, "trade_suggestion.price.label"); //$NON-NLS-1$
	static final I18NMessage0P TRADE_SUGGESTION_ORDER_TYPE_LABEL = new I18NMessage0P(
			LOGGER, "trade_suggestion.order_type.label"); //$NON-NLS-1$
	static final I18NMessage0P TRADE_SUGGESTION_TIME_IN_FORCE_LABEL = new I18NMessage0P(
			LOGGER, "trade_suggestion.time_in_force.label"); //$NON-NLS-1$
	static final I18NMessage0P TRADE_SUGGESTION_ORDER_CAPACITY_LABEL = new I18NMessage0P(
			LOGGER, "trade_suggestion.order_capacity.label"); //$NON-NLS-1$
	static final I18NMessage0P TRADE_SUGGESTION_POSITION_EFFECT_LABEL = new I18NMessage0P(
			LOGGER, "trade_suggestion.position_effect.label"); //$NON-NLS-1$
	static final I18NMessage0P TRADE_SUGGESTION_SCORE_LABEL = new I18NMessage0P(
			LOGGER, "trade_suggestion.score.label"); //$NON-NLS-1$
	static final I18NMessage0P TRADE_SUGGESTION_ACCOUNT_LABEL = new I18NMessage0P(
			LOGGER, "trade_suggestion.account.label"); //$NON-NLS-1$
	static final I18NMessage0P TRADE_SUGGESTION_BROKER_ID_LABEL = new I18NMessage0P(
			LOGGER, "trade_suggestion.broker_id.label"); //$NON-NLS-1$
	static final I18NMessage0P TRADE_SUGGESTION_TIMESTAMP_LABEL = new I18NMessage0P(
			LOGGER, "trade_suggestion.timestamp.label"); //$NON-NLS-1$
	static final I18NMessage0P SEND_ORDERS_HANDLER_SERVER_FAILURE = new I18NMessage0P(
			LOGGER, "send_orders_handler.server_failure"); //$NON-NLS-1$
	static final I18NMessage0P SEND_ORDERS_HANDLER_SEND_ORDERS_FAILURE = new I18NMessage0P(
			LOGGER, "send_orders_handler.send_orders_failure"); //$NON-NLS-1$
	static final I18NMessage1P SEND_ORDERS_HANDLER_SEND_ORDER_FAILURE = new I18NMessage1P(
			LOGGER, "send_orders_handler.send_order_failure"); //$NON-NLS-1$
	static final I18NMessage0P SEND_ORDERS_HANDLER_SEND_ORDERS_FAILURE_SEE_DETAILS = new I18NMessage0P(
			LOGGER, "send_orders_handler.send_orders_failure.see_details"); //$NON-NLS-1$
	static final I18NMessage0P OPEN_SUGGESTION_HANDLER_CONVERSION_FAILURE = new I18NMessage0P(
			LOGGER, "open_suggestions_handler.conversion_failure"); //$NON-NLS-1$
	static final I18NMessage0P REMOTE_AGENT_PROPERTY_PAGE_URL_LABEL = new I18NMessage0P(
			LOGGER, "remote_agent_property_page.url_label"); //$NON-NLS-1$
	static final I18NMessage0P REMOTE_AGENT_PROPERTY_PAGE_URL_TOOLTIP = new I18NMessage0P(
			LOGGER, "remote_agent_property_page.url_tooltip"); //$NON-NLS-1$
	static final I18NMessage0P REMOTE_AGENT_PROPERTY_PAGE_CREDENTIALS_LABEL = new I18NMessage0P(
			LOGGER, "remote_agent_property_page.credentials_label"); //$NON-NLS-1$
	static final I18NMessage0P REMOTE_AGENT_PROPERTY_PAGE_CREDENTIALS_DESCRIPTION = new I18NMessage0P(
			LOGGER, "remote_agent_property_page.credentials_description"); //$NON-NLS-1$
	static final I18NMessage0P REMOTE_AGENT_PROPERTY_PAGE_USERNAME_LABEL = new I18NMessage0P(
			LOGGER, "remote_agent_property_page.username_label"); //$NON-NLS-1$
	static final I18NMessage0P REMOTE_AGENT_PROPERTY_PAGE_PASSWORD_LABEL = new I18NMessage0P(
			LOGGER, "remote_agent_property_page.password_label"); //$NON-NLS-1$
	static final I18NMessage1P REMOTE_AGENT_PROPERTY_PAGE_INVALID_URL = new I18NMessage1P(
			LOGGER, "remote_agent_property_page.invalid_url"); //$NON-NLS-1$
	static final I18NMessage0P REMOTE_AGENT_MANAGER_DELETE_MODULE_QUESTION =
		new I18NMessage0P(LOGGER, "remote_agent_manager.delete_module_question"); //$NON-NLS-1$
	static final I18NMessage0P STRATEGY_MANAGER_REMOTE_AGENT_DISPLAY_NAME =
		new I18NMessage0P(LOGGER, "strategy_manager_remote_agent_display_name"); //$NON-NLS-1$

	/*
	 * Error Messages
	 */
	static final I18NMessage2P NEW_RUBY_STRATEGY_CREATION_FAILED = new I18NMessage2P(
			LOGGER, "new_ruby_strategy.creation_failed"); //$NON-NLS-1$
	static final I18NMessage0P STRATEGY_MANAGER_PERSIST_FAILED = new I18NMessage0P(
			LOGGER, "strategy_manager.persist_failed"); //$NON-NLS-1$
	static final I18NMessage0P STRATEGY_MANAGER_RESTORE_FAILED = new I18NMessage0P(
			LOGGER, "strategy_manager.restore_failed"); //$NON-NLS-1$
	static final I18NMessage2P STRATEGY_MANAGER_SCRIPT_NOT_FOUND = new I18NMessage2P(
			LOGGER, "strategy_manager.script_not_found"); //$NON-NLS-1$
	static final I18NMessage0P STRATEGY_MANAGER_RESTORE_URI_FAILED = new I18NMessage0P(
			LOGGER, "strategy_manager.restore_uri_failed"); //$NON-NLS-1$
	static final I18NMessage1P STRATEGY_MANAGER_STRATEGY_START_FAILED = new I18NMessage1P(
			LOGGER, "strategy_manager.strategy_start_failed"); //$NON-NLS-1$
	static final I18NMessage1P STRATEGY_MANAGER_STRATEGY_STOP_FAILED = new I18NMessage1P(
			LOGGER, "strategy_manager.strategy_stop_failed"); //$NON-NLS-1$
	static final I18NMessage0P TRADE_SUGGESTION_MANAGER_INVALID_DATA_NO_ORDER = new I18NMessage0P(
			LOGGER, "trade_suggestion_manager.invalid_data_no_order"); //$NON-NLS-1$
	static final I18NMessage1P REMOTE_AGENT_MANAGER_CONNECT_FAILED =
			new I18NMessage1P(LOGGER, "remote_agent_manager.connect_failed"); //$NON-NLS-1$
	static final I18NMessage0P REMOTE_AGENT_MANAGER_DISCONNECT_FAILED =
			new I18NMessage0P(LOGGER, "remote_agent_manager.disconnect_failed"); //$NON-NLS-1$
	static final I18NMessage0P REMOTE_AGENT_MANAGER_COULD_NOT_SUBSCRIBE_TO_STATUS_UPDATES =
			new I18NMessage0P(LOGGER, "remote_agent_manager.could_not_subscribe_to_status_updates"); //$NON-NLS-1$
	static final I18NMessage1P REMOTE_AGENT_MANAGER_LOST_CONNECTION =
			new I18NMessage1P(LOGGER, "remote_agent_manager.lost_connection"); //$NON-NLS-1$
	static final I18NMessage0P REMOTE_AGENT_MANAGER_LOST_CONNECTION_UNKNOWN_CAUSE =
			new I18NMessage0P(LOGGER, "remote_agent_manager.lost_connection_unknown_cause"); //$NON-NLS-1$
	static final I18NMessage0P REMOTE_AGENT_MANAGER_CLEANUP_FAILED =
			new I18NMessage0P(LOGGER, "remote_agent_manager.cleanup_failed"); //$NON-NLS-1$
	static final I18NMessage0P REMOTE_AGENT_MANAGER_MISSING_URI =
			new I18NMessage0P(LOGGER, "remote_agent_manager.missing_uri"); //$NON-NLS-1$
	static final I18NMessage2P CHANGE_STATE_HANDLER_INVALID_STATE =
			new I18NMessage2P(LOGGER, "change_state_handler.invalid_state"); //$NON-NLS-1$
}
