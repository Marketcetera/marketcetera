package org.marketcetera.photon.notification.preferences;

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
 * @since 0.8.0
 */
@ClassVersion("$Id$")//$NON-NLS-1$
public interface Messages {
	/**
	 * The message provider.
	 */
	static final I18NMessageProvider PROVIDER = new I18NMessageProvider(
			"photon_notification_preferences"); //$NON-NLS-1$

	/**
	 * The logger.
	 */
	static final I18NLoggerProxy LOGGER = new I18NLoggerProxy(PROVIDER);

	/*
	 * UI Text
	 */
	static final I18NMessage0P DESKTOP_NOTIFICATIONS_PRIORITY = new I18NMessage0P(
			LOGGER, "desktop_notifications.priority"); //$NON-NLS-1$
	static final I18NMessage0P DESKTOP_NOTIFICATIONS_SEVERITY_LABEL_HIGH = new I18NMessage0P(
			LOGGER, "desktop_notifications.severity_label.HIGH"); //$NON-NLS-1$
	static final I18NMessage0P DESKTOP_NOTIFICATIONS_SEVERITY_LABEL_MEDIUM = new I18NMessage0P(
			LOGGER, "desktop_notifications.severity_label.MEDIUM"); //$NON-NLS-1$
	static final I18NMessage0P DESKTOP_NOTIFICATIONS_SEVERITY_LABEL_LOW = new I18NMessage0P(
			LOGGER, "desktop_notifications.severity_label.LOW"); //$NON-NLS-1$
	static final I18NMessage0P DESKTOP_NOTIFICATIONS_FEWEST = new I18NMessage0P(
			LOGGER, "desktop_notifications.fewest"); //$NON-NLS-1$
	static final I18NMessage0P DESKTOP_NOTIFICATIONS_MOST = new I18NMessage0P(
			LOGGER, "desktop_notifications.most"); //$NON-NLS-1$
	static final I18NMessage2P DESKTOP_NOTIFICATIONS_PARENTHETICAL_PATTERN = new I18NMessage2P(
			LOGGER, "desktop_notifications.parenthetical_pattern"); //$NON-NLS-1$
	static final I18NMessage0P DESKTOP_NOTIFICATIONS_SOUNDS_GROUP = new I18NMessage0P(
			LOGGER, "desktop_notifications.sounds_group"); //$NON-NLS-1$
	static final I18NMessage0P DESKTOP_NOTIFICATIONS_SOUNDS_GROUP_DESCRIPTION = new I18NMessage0P(
			LOGGER, "desktop_notifications.sounds_group_description"); //$NON-NLS-1$
	static final I18NMessage0P DESKTOP_NOTIFICATIONS_PLAY_SOUND_LABEL = new I18NMessage0P(
			LOGGER, "desktop_notifications.play_sound_label"); //$NON-NLS-1$
	static final I18NMessage0P DESKTOP_NOTIFICATIONS_TEST_BUTTON_LABEL = new I18NMessage0P(
			LOGGER, "desktop_notifications.test_button_label"); //$NON-NLS-1$
}
