package org.marketcetera.photon.internal.module;

import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessage0P;
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
			"photon_module"); //$NON-NLS-1$

	/**
	 * The logger.
	 */
	static final I18NLoggerProxy LOGGER = new I18NLoggerProxy(PROVIDER);

	/*
	 * Log Messages
	 */
	public static I18NMessage0P ACTIVATOR_FAILED_TO_STOP_MODULE_MANAGER = new I18NMessage0P(
			LOGGER, "activator.failed_to_stop_module_manager"); //$NON-NLS-1$
	public static I18NMessage0P ACTIVATOR_FAILED_TO_SAVE_PREFERENCES = new I18NMessage0P(
			LOGGER, "activator.failed_to_save_preferences"); //$NON-NLS-1$
	public static I18NMessage0P PREFERENCE_ATTRIBUTE_DEFAULTS_FAILED_TO_SAVE_PREFERENCES = new I18NMessage0P(
			LOGGER, "preference_attribute_defaults.failed_to_save_preferences"); //$NON-NLS-1$
	public static I18NMessage2P MODULE_ATTRIBUTE_SUPPORT_FAILED_GET_ATTRIBUTE = new I18NMessage2P(
			LOGGER, "module_attribute_support.failed_to_get_attribute"); //$NON-NLS-1$
	public static I18NMessage2P MODULE_ATTRIBUTE_SUPPORT_FAILED_SET_ATTRIBUTE = new I18NMessage2P(
			LOGGER, "module_attribute_support.failed_to_set_attribute"); //$NON-NLS-1$
}
