package org.marketcetera.photon.notification;

import java.util.Queue;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.marketcetera.core.notifications.INotification;
import org.marketcetera.core.notifications.INotification.Severity;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Uses {@link NotificationPlugin} preferences to determine behavior of
 * {@link #showPopup(INotification)}. Popups are created using Mylyn
 * {@link DesktopNotificationPopup} and sounds are played using {@link PlayWave}
 * .
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class PopupJob extends AbstractPopupJob {

	/**
	 * The plugin, used for determining user preferences.
	 */
	private final NotificationPlugin plugin = NotificationPlugin
			.getDefault();

	/**
	 * Display on which to perform UI actions.
	 */
	private Display display;

	/**
	 * Caches the popup created on the UI thread.
	 */
	private volatile Window popup;

	/**
	 * Constructor.
	 * 
	 * @param queue
	 *            notification queue, cannot be null
	 * @param display
	 *            display to use for UI, cannot be null
	 */
	public PopupJob(Queue<INotification> queue, Display display) {
		super("Desktop Notification Popup Job", queue); //$NON-NLS-1$
		Assert.isNotNull(display);
		this.display = display;
		setSystem(true);
	}

	/**
	 * This implementation of {@link AbstractPopupJob#showPopup(INotification)}
	 * consults the plugin preferences to determine correct behavior. If a popup
	 * should be displayed, this method will show the popup and will not return
	 * until the popup is closed. If configured to do so, a sound clip will be
	 * played as well.
	 */
	@Override
	public void showPopup(final INotification notification) {
		final Severity severity = notification.getSeverity();
		if (plugin.shouldDisplayPopup(severity)) {
			popup = null;
			display.syncExec(new Runnable() {
				@Override
				public void run() {
					if (plugin.shouldPlaySound(severity))
						playSoundClip(plugin.getSoundClip(severity));
					popup = createPopup(notification);
					popup.open();
				}
			});
			// wait for popup to close before returning
			while (popup != null && popup.getShell() != null
					&& !popup.getShell().isDisposed())
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
		}
	}

	/**
	 * Create a popup window for the given notification.
	 * 
	 * @param notification
	 *            the notification to display in the popup
	 * @return the popup
	 */
	protected Window createPopup(final INotification notification) {
		return new DesktopNotificationPopup(display, notification);
	}

	/**
	 * Plays the sound clip with given path.
	 * 
	 * @param clip
	 *            path to sound clip
	 */
	protected void playSoundClip(final String clip) {
		new PlayWave(clip).start();
	}

}
