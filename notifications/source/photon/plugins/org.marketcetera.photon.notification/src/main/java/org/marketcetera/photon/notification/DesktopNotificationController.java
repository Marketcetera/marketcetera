package org.marketcetera.photon.notification;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.PlatformUI;
import org.marketcetera.core.notifications.INotification;
import org.marketcetera.core.notifications.INotificationManager;
import org.marketcetera.core.notifications.NotificationManager;
import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Manages desktop notification popups.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")//$NON-NLS-1$
public class DesktopNotificationController implements ISubscriber {

	/**
	 * Core notification manager
	 */
	private final INotificationManager mNotificationManager = NotificationManager
			.getNotificationManager();

	/**
	 * Queue for incoming notifications
	 */
	private Queue<INotification> mQueue;

	/**
	 * Scheduled job for processing notifications.
	 */
	private Job mPopupJob;

	/**
	 * Initializes the controller to start listening for notifications.
	 */
	public void init() {
		mNotificationManager.subscribe(this);
	}

	@Override
	public boolean isInteresting(Object inData) {
		return inData instanceof INotification;
	}

	@Override
	public void publishTo(final Object inData) {
		if (mQueue == null) {
			mQueue = new ConcurrentLinkedQueue<INotification>();
			mPopupJob = new PopupJob(mQueue, PlatformUI.getWorkbench()
					.getDisplay());
			mPopupJob.schedule();
		}
		mQueue.add((INotification) inData);
	}

	/**
	 * Stops the controller from listening for notifications.
	 */
	public void shutdown() {
		if (mPopupJob != null) {
			mPopupJob.cancel();
			mPopupJob = null;
		}
		mNotificationManager.unsubscribe(this);
	}
}
