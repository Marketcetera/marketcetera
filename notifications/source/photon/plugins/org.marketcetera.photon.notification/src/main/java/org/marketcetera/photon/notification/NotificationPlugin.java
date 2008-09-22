package org.marketcetera.photon.notification;

import org.eclipse.ui.IStartup;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.marketcetera.core.notifications.INotification.Severity;
import org.marketcetera.photon.notification.preferences.NotificationPreferences;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;
import org.osgi.framework.BundleContext;

/* $License$ */

/**
 * The activator class controls the plug-in life cycle.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")//$NON-NLS-1$
public class NotificationPlugin extends AbstractUIPlugin {

	/**
	 * The plug-in ID
	 */
	public static final String PLUGIN_ID = "org.marketcetera.photon.notification"; //$NON-NLS-1$

	/**
	 * The shared instance
	 */
	private static NotificationPlugin sPlugin;

	/**
	 * The plugin temporarily overriding <code>plugin</code>. See
	 * {@link #setOverride}.
	 */
	private static NotificationPlugin sOverridePlugin;

	/**
	 * Handles desktop notification popups
	 */
	private DesktopNotificationController mDesktopNotificationController;
	
	/**
	 * The constructor
	 */
	public NotificationPlugin() {
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		sPlugin = this;
		mDesktopNotificationController = new DesktopNotificationController();
		mDesktopNotificationController.init();
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		mDesktopNotificationController.shutdown();
		sPlugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance, or an override if
	 * {@link #setOverride(NotificationPlugin)} was called.
	 * 
	 * @return the shared instance
	 */
	public static NotificationPlugin getDefault() {
		if (sOverridePlugin != null)
			return sOverridePlugin;
		return sPlugin;
	}

	/**
	 * Sets the plugin to be returned from {@link #getDefault()}. This is
	 * intended for testing purposes only. Tests should take care to reset this
	 * to null when finished.
	 * 
	 * @param overridePlugin
	 *            the override plugin
	 */
	public static void setOverride(NotificationPlugin overridePlugin) {
		NotificationPlugin.sOverridePlugin = overridePlugin;
	}

	/**
	 * Convenience method to read appropriate preferences and determine if a
	 * popup should be displayed for the given severity.
	 * 
	 * @param severity
	 *            the severity of notification
	 * @return true if popup should be displayed, false otherwise
	 */
	public boolean shouldDisplayPopup(Severity severity) {
		String string = getPreferenceStore().getString(
				NotificationPreferences.PRIORITY);
		if (!string.isEmpty()) {
			try {
				return Severity.valueOf(string).compareTo(severity) <= 0;
			} catch (IllegalArgumentException e) {
				SLF4JLoggerProxy.debug(this, e);
			}
		}
		return false;
	}

	/**
	 * Convenience method to read appropriate preferences and determine if a
	 * sound clip should be displayed for the given severity.
	 * 
	 * @param severity
	 *            the severity of notification
	 * @return true if sound should be displayed, false otherwise
	 */
	public boolean shouldPlaySound(Severity severity) {
		return getPreferenceStore().getBoolean(
				NotificationPreferences.SOUND_ENABLED_PREFIX + severity.name());
	}

	/**
	 * Convenience method to read appropriate preferences and return the path to
	 * the sound clip to play for the given severity.
	 * 
	 * @param severity
	 *            the severity of notification
	 * @return the path to the sound clip to play for the given severity
	 */
	public String getSoundClip(Severity severity) {
		return getPreferenceStore().getString(
				NotificationPreferences.SOUND_CLIP_PREFIX + severity.name());
	}

	/**
	 * This class is used by the <code>org.eclipse.ui.startup</code> extension
	 * point to trigger activation of this plugin after the workbench starts up.
	 * Otherwise, the plugin would only be activated when a user requests
	 * something from it (opens the notification console, the preference page,
	 * etc).
	 * 
	 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
	 * @version $Id$
	 * @since $Release$
	 */
	@ClassVersion("$Id$")
	public static class NotificationPluginStartup implements IStartup {
		@Override
		public void earlyStartup() {
			// Do nothing
		}
	}
}
