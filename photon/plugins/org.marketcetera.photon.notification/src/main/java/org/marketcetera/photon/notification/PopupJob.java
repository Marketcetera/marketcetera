package org.marketcetera.photon.notification;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.marketcetera.core.notifications.INotification;
import org.marketcetera.core.notifications.INotification.Severity;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Uses {@link NotificationPlugin} preferences to determine behavior of
 * {@link #showPopup(INotification, IProgressMonitor)}. Popup windows are
 * created and displayed with an optional sound.
 * 
 * This class may be subclassed to override
 * <ul>
 * <li>{@link #createPopup(INotification)} - to provide an alternate popup
 * window implementation</li>
 * <li>{@link #playSoundClip(String)} - to provide an method of playing the
 * sound file</li>
 * </ul>
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 0.8.0
 */
@ClassVersion("$Id$")
public class PopupJob extends AbstractNotificationJob {

	/**
	 * The plugin, used for determining user preferences.
	 */
	private final NotificationPlugin mPlugin = NotificationPlugin.getDefault();

	/**
	 * Display on which to perform UI actions.
	 */
	private Display mDisplay;

	/**
	 * Caches the popup created on the UI thread.
	 */
	private volatile Window mPopup;

	/**
	 * Constructor. Will throw an unchecked exception if <code>display</code> is null.
	 * 
	 * @param display
	 *            display to use for UI, cannot be null
	 */
	public PopupJob(Display display) {
		super("Desktop Notification Popup Job"); //$NON-NLS-1$
		Assert.isNotNull(display);
		this.mDisplay = display;
		setSystem(true);
	}

	/**
	 * This implementation of
	 * {@link AbstractNotificationJob#showPopup(INotification, IProgressMonitor)} displays the
	 * popup created by {@link #createPopup(INotification)} and determines if a
	 * sound should be played.
	 * 
	 * This method will block until the popup has been closed.
	 */
	@Override
	public void showPopup(final INotification notification,
			final IProgressMonitor monitor) {
		mPopup = null;
		if (!PlatformUI.isWorkbenchRunning()) return;
		mDisplay.syncExec(new Runnable() {
			@Override
			public void run() {
				Severity severity = notification.getSeverity();
				if (mPlugin.shouldPlaySound(severity))
					playSoundClip(mPlugin.getSoundClip(severity));
				mPopup = createPopup(notification);
				mPopup.open();
			}
		});
		// wait for popup to close before returning
		while (mPopup != null && mPopup.getShell() != null
				&& !mPopup.getShell().isDisposed())
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
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
		return new DesktopNotificationPopup(mDisplay, notification);
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
