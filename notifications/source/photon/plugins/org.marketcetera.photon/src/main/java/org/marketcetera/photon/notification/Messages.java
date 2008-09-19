package org.marketcetera.photon.notification;

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
			"photon_notification"); //$NON-NLS-1$

	/**
	 * The logger.
	 */
	static final I18NLoggerProxy LOGGER = new I18NLoggerProxy(PROVIDER);

	/* 
	 * UI Text
	 */
	static final I18NMessage0P NOTIFICATION_CONSOLE_NAME = new I18NMessage0P(
			LOGGER, "notification_console_name"); //$NON-NLS-1$
		
	/*
	 * INFO Log Messages 
	 */
	static final I18NMessage0P NOTIFICATION_CONSOLE_INIT = new I18NMessage0P(LOGGER,
			"notification_console_init"); //$NON-NLS-1$
}
