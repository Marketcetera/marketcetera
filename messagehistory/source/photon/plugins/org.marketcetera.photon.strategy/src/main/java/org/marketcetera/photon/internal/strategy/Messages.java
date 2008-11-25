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
 * @since $Release$
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
	static final I18NMessage0P STRATEGYUI_DESTINATION_LABEL = new I18NMessage0P(
			LOGGER, "strategyui.destination.label"); //$NON-NLS-1$
	static final I18NMessage0P STRATEGYUI_DESTINATION_TOOLTIP = new I18NMessage0P(
			LOGGER, "strategyui.destination.tooltip"); //$NON-NLS-1$
	static final I18NMessage0P REGISTER_RUBY_STRATEGY_TITLE = new I18NMessage0P(
			LOGGER, "register_ruby_strategy.title"); //$NON-NLS-1$
	static final I18NMessage0P REGISTER_RUBY_STRATEGY_DESCRIPTION = new I18NMessage0P(
			LOGGER, "register_ruby_strategy.description"); //$NON-NLS-1$
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
	static final I18NMessage0P STRATEGY_SERVER_DESTINATION_LABEL = new I18NMessage0P(
			LOGGER, "strategy.server_destination.label"); //$NON-NLS-1$
	static final I18NMessage0P STRATEGY_SINK_DESTINATION_LABEL = new I18NMessage0P(
			LOGGER, "strategy.sink_destination.label"); //$NON-NLS-1$
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
	static final I18NMessage0P TRADE_SUGGESTION_DESTINATION_ID_LABEL = new I18NMessage0P(
			LOGGER, "trade_suggestion.destination_id.label"); //$NON-NLS-1$
	static final I18NMessage0P TRADE_SUGGESTION_TIMESTAMP_LABEL = new I18NMessage0P(
			LOGGER, "trade_suggestion.timestamp.label"); //$NON-NLS-1$
	
	/*
	 * Module information
	 */
	static final I18NMessage0P TRADE_SUGGESTION_RECEIVER_DESCRIPTION = new I18NMessage0P(
			LOGGER, "trade_suggestion_receiver.description"); //$NON-NLS-1$
	
	/*
	 * Log Messages 
	 */
	static final I18NMessage2P NEW_RUBY_STRATEGY_CREATION_FAILED = new I18NMessage2P(
			LOGGER, "new_ruby_strategy.creation_failed"); //$NON-NLS-1$
	static final I18NMessage0P STRATEGY_MANAGER_PERSIST_FAILED = new I18NMessage0P(
			LOGGER, "strategy_manager.persist_failed"); //$NON-NLS-1$
	static final I18NMessage0P STRATEGY_MANAGER_RESTORE_FAILED = new I18NMessage0P(
			LOGGER, "strategy_manager.restore_failed"); //$NON-NLS-1$
	static final I18NMessage2P STRATEGY_MANAGER_SCRIPT_NOT_FOUND = new I18NMessage2P(
			LOGGER, "strategy_manager.script_not_found"); //$NON-NLS-1$
	static final I18NMessage1P STRATEGY_MANAGER_INVALID_DESTINATION = new I18NMessage1P(
			LOGGER, "strategy_manager.invalid_destination"); //$NON-NLS-1$
	static final I18NMessage1P STRATEGY_MANAGER_STRATEGY_START_FAILED = new I18NMessage1P(
			LOGGER, "strategy_manager.strategy_start_failed"); //$NON-NLS-1$
	static final I18NMessage1P TRADE_SUGGESTION_RECEIVER_INVALID_DATA_TYPE = new I18NMessage1P(
			LOGGER, "trade_suggestion_receiver.invalid_data_type"); //$NON-NLS-1$
	static final I18NMessage0P TRADE_SUGGESTION_RECEIVER_INVALID_DATA_NO_ORDER = new I18NMessage0P(
			LOGGER, "trade_suggestion_receiver.invalid_data_no_order"); //$NON-NLS-1$
}
	
