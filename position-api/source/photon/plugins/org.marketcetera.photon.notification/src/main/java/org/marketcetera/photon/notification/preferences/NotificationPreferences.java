package org.marketcetera.photon.notification.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.marketcetera.core.notifications.INotification.Severity;
import org.marketcetera.photon.notification.NotificationPlugin;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * This class defines the preferences for this plugin. It extends
 * {@link AbstractPreferenceInitializer} so it can be used by the
 * <code>org.eclipse.core.runtime.preferences</code> extension point to handle
 * setting preference defaults when necessary.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 0.8.0
 */
@ClassVersion("$Id$")//$NON-NLS-1$
public class NotificationPreferences extends AbstractPreferenceInitializer {
	
	/**
	 * Base string for desktop notification preferences 
	 */
	private static final String DESKTOP_NOTIFICATION_BASE = "desktop_notification_"; //$NON-NLS-1$
	
	/**
	 * Preference controlling minimum severity that should trigger a popup 
	 */
	public static final String PRIORITY = DESKTOP_NOTIFICATION_BASE + "priority"; //$NON-NLS-1$
	
	/**
	 * Preference prefix controlling sound enablement.  Actual preferences have the severity appended. 
	 */
	public static final String SOUND_ENABLED_PREFIX = DESKTOP_NOTIFICATION_BASE + "sound_enabled_"; //$NON-NLS-1$
	
	/**
	 * Preference prefix storing sound clip path.  Actual preferences have the severity appended. 
	 */
	public static final String SOUND_CLIP_PREFIX = DESKTOP_NOTIFICATION_BASE + "sound_clip_"; //$NON-NLS-1$

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = NotificationPlugin.getDefault()
				.getPreferenceStore();
		store.setDefault(PRIORITY, Severity.HIGH.name());
		for (Severity severity : Severity.values()) {
			store.setDefault(SOUND_ENABLED_PREFIX + severity.name(), false);
			store.setDefault(SOUND_ENABLED_PREFIX + severity.name(), ""); //$NON-NLS-1$
		}
	}
}
