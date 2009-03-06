package org.marketcetera.photon.internal.module.ui;

import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.log.I18NMessage1P;
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
	static final I18NMessageProvider PROVIDER = new I18NMessageProvider("photon_module_ui"); //$NON-NLS-1$

	/**
	 * The logger.
	 */
	static final I18NLoggerProxy LOGGER = new I18NLoggerProxy(PROVIDER);

	/*
	 * UI Text
	 */
	static final I18NMessage0P SINK_CONSOLE_NAME = new I18NMessage0P(LOGGER, "sink_console_name"); //$NON-NLS-1$
	static final I18NMessage0P NEW_PROPERTY_DIALOG_TITLE = new I18NMessage0P(LOGGER,
			"new_property_dialog.title"); //$NON-NLS-1$
	static final I18NMessage0P NEW_PROPERTY_DIALOG_KEY_LABEL = new I18NMessage0P(LOGGER,
			"new_property_dialog.key.label"); //$NON-NLS-1$
	static final I18NMessage0P NEW_PROPERTY_DIALOG_INVALID_INITIAL_CHARACTER_ERROR = new I18NMessage0P(
			LOGGER, "new_property_dialog.invalid_initial_character_error"); //$NON-NLS-1$
	static final I18NMessage0P NEW_PROPERTY_DIALOG_CONTAINS_SPACE_ERROR = new I18NMessage0P(LOGGER,
			"new_property_dialog.contains_space_error"); //$NON-NLS-1$
	static final I18NMessage1P NEW_PROPERTY_DIALOG_INVALID_CHARACTER_ERROR = new I18NMessage1P(
			LOGGER, "new_property_dialog.invalid_character_error"); //$NON-NLS-1$
	static final I18NMessage0P NEW_PROPERTY_DIALOG_END_WITH_PERIOD_ERROR = new I18NMessage0P(
			LOGGER, "new_property_dialog.end_with_period_error"); //$NON-NLS-1$
	static final I18NMessage0P NEW_PROPERTY_DIALOG_VALUE_LABEL = new I18NMessage0P(LOGGER,
			"new_property_dialog.value.label"); //$NON-NLS-1$
	static final I18NMessage0P NEW_PROPERTY_DIALOG_INSTANCE_DEFAULTS_LABEL = new I18NMessage0P(
			LOGGER, "new_property_dialog.instance_defaults.label"); //$NON-NLS-1$
	static final I18NMessage0P MODULE_PROPERTIES_PREFERENCE_PAGE_ADD_BUTTON_LABEL = new I18NMessage0P(
			LOGGER, "module_properties_preference_page.add_button.label"); //$NON-NLS-1$
	static final I18NMessage0P MODULE_PROPERTIES_PREFERENCE_PAGE_ADD_ACTION_LABEL = new I18NMessage0P(
			LOGGER, "module_properties_preference_page.add_action.label"); //$NON-NLS-1$
	static final I18NMessage0P MODULE_PROPERTIES_PREFERENCE_PAGE_DELETE_ACTION_LABEL = new I18NMessage0P(
			LOGGER, "module_properties_preference_page.delete_action.label"); //$NON-NLS-1$
	static final I18NMessage0P MODULE_PROPERTIES_PREFERENCE_PAGE_RESTART_WARNING = new I18NMessage0P(
			LOGGER, "module_properties_preference_page.restart_warning"); //$NON-NLS-1$
	static final I18NMessage0P MODULE_PROPERTIES_PREFERENCE_PAGE_PASSWORD_MATCH = new I18NMessage0P(
			LOGGER, "module_properties_preference_page.password_match"); //$NON-NLS-1$
	static final I18NMessage0P MODULE_ATTRIBUTE_PREFERENCE_PAGE_UPDATE_FAILURE_SEE_DETAILS = new I18NMessage0P(
			LOGGER, "module_attribute_preference_page.update_failure_see_details"); //$NON-NLS-1$
	static final I18NMessage0P MODULE_ATTRIBUTE_PREFERENCE_PAGE_UPDATE_FAILURE = new I18NMessage0P(
			LOGGER, "module_attribute_preference_page_update_failure"); //$NON-NLS-1$
}
