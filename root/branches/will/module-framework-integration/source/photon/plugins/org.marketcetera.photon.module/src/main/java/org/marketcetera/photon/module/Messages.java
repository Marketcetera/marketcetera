package org.marketcetera.photon.module;

import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessage0P;
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
			"photon_module"); //$NON-NLS-1$

	/**
	 * The logger.
	 */
	static final I18NLoggerProxy LOGGER = new I18NLoggerProxy(PROVIDER);

	/*
	 * UI Text
	 */
	static final I18NMessage0P SINK_CONSOLE_NAME = new I18NMessage0P(
			LOGGER, "sink_console.name"); //$NON-NLS-1$
	
	/*
	 * Log Messages
	 */
	static final I18NMessage0P MODULE_PLUGIN_ERROR_INITIALIZING_MODULE_MANAGER = new I18NMessage0P(
			LOGGER, "module_plugin.error_initializing_module_manager"); //$NON-NLS-1$
	static final I18NMessage0P MODULE_PROPERTIES_PREFERENCE_PAGE_ERROR_LOADING_PROPERTIES = new I18NMessage0P(
			LOGGER,
			"module_properties_preference_page.error_loading_properties"); //$NON-NLS-1$
	static final I18NMessage0P MODULE_PROPERTIES_PREFERENCE_PAGE_ERROR_SAVING_PROPERTIES = new I18NMessage0P(
			LOGGER, "module_properties_preference_page.error_saving_properties"); //$NON-NLS-1$
}
