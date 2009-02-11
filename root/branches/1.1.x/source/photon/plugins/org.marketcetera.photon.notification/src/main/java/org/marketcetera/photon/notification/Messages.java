package org.marketcetera.photon.notification;

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
 * @since 0.8.0
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
			LOGGER, "notification_console.name"); //$NON-NLS-1$
	static final I18NMessage0P SUMMARY_NOTIFICATION_SUBJECT = new I18NMessage0P(
			LOGGER, "summary_notification.subject"); //$NON-NLS-1$
	static final I18NMessage1P SUMMARY_NOTIFICATION_BODY = new I18NMessage1P(
			LOGGER, "summary_notification.body"); //$NON-NLS-1$
	static final I18NMessage0P THRESHOLD_NOTIFICATION_SUBJECT = new I18NMessage0P(
			LOGGER, "threshold_notification.subject"); //$NON-NLS-1$
	static final I18NMessage0P THRESHOLD_NOTIFICATION_BODY = new I18NMessage0P(
			LOGGER, "threshold_notification.body"); //$NON-NLS-1$
	static final I18NMessage0P POPUP_ELLIPSIS = new I18NMessage0P(
			LOGGER, "popup.ellipsis"); //$NON-NLS-1$
	static final I18NMessage0P POPUP_SUBJECT_LABEL = new I18NMessage0P(
			LOGGER, "popup.subject_label"); //$NON-NLS-1$
	static final I18NMessage0P POPUP_PRIORITY_LABEL = new I18NMessage0P(
			LOGGER, "popup.priority_label"); //$NON-NLS-1$
	static final I18NMessage0P POPUP_TIMESTAMP_LABEL = new I18NMessage0P(
			LOGGER, "popup.timestamp_label"); //$NON-NLS-1$
	static final I18NMessage0P POPUP_SEVERITY_LABEL_HIGH = new I18NMessage0P(
			LOGGER, "popup.severity_label.HIGH"); //$NON-NLS-1$
	static final I18NMessage0P POPUP_SEVERITY_LABEL_MEDIUM = new I18NMessage0P(
			LOGGER, "popup.severity_label.MEDIUM"); //$NON-NLS-1$
	static final I18NMessage0P POPUP_SEVERITY_LABEL_LOW = new I18NMessage0P(
			LOGGER, "popup.severity_label.LOW"); //$NON-NLS-1$
		
	/*
	 * INFO Log Messages 
	 */
	static final I18NMessage0P NOTIFICATION_CONSOLE_INIT = new I18NMessage0P(LOGGER,
			"notification_console.init"); //$NON-NLS-1$
		
	/*
	 * ERROR Log Messages 
	 */
	static final I18NMessage1P AUDIO_CANNOT_FIND_FILE = new I18NMessage1P(LOGGER,
			"audio.cannot_find_file"); //$NON-NLS-1$
}
