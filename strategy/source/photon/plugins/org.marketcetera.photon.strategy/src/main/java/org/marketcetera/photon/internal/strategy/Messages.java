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
@ClassVersion("$Id: Messages.java 9860 2008-10-10 22:00:53Z klim $")//$NON-NLS-1$
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
	static final I18NMessage0P REGISTER_RUBY_STRATEGY_TITLE = new I18NMessage0P(
			LOGGER, "register_ruby_strategy.title"); //$NON-NLS-1$
	static final I18NMessage0P REGISTER_RUBY_STRATEGY_DESCRIPTION = new I18NMessage0P(
			LOGGER, "register_ruby_strategy.description"); //$NON-NLS-1$
	static final I18NMessage1P REGISTER_RUBY_STRATEGY_NAME_NOT_UNIQUE = new I18NMessage1P(
			LOGGER, "register_ruby_strategy.name_not_unique"); //$NON-NLS-1$
	static final I18NMessage1P REGISTER_RUBY_STRATEGY_REQUIRED_FIELD_BLANK = new I18NMessage1P(
			LOGGER, "register_ruby_strategy.required_field_blank"); //$NON-NLS-1$
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
	
	/*
	 * Log Messages 
	 */
	static final I18NMessage2P NEW_RUBY_STRATEGY_CREATION_FAILED = new I18NMessage2P(
			LOGGER, "new_ruby_strategy.creation_failed"); //$NON-NLS-1$
}
	
