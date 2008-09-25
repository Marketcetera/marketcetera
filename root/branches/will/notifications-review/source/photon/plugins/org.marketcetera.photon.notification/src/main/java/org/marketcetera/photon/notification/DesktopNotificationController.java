package org.marketcetera.photon.notification;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.ui.PlatformUI;
import org.marketcetera.core.notifications.INotification;
import org.marketcetera.core.notifications.INotificationManager;
import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Manages desktop notification popups.
 * 
 * This class may be subclassed to override
 * <ul>
 * <li>{@link #isInteresting(Object)} - to filter notifications</li>
 * <li>{@link #createQueue()} - to provide an alternate queue implementation</li>
 * <li>{@link #createJob(Queue)} - to provide an alternate job implementation</li>
 * </ul>
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")//$NON-NLS-1$
class DesktopNotificationController implements ISubscriber {

	/**
	 * Core notification manager
	 */
	private final INotificationManager mNotificationManager;

	/**
	 * Queue for incoming notifications, lazily instantiated
	 */
	private Queue<INotification> mQueue;

	/**
	 * Scheduled job for processing notifications, lazily instantiated
	 */
	private Job mPopupJob;

	/**
	 * Indicates whether the controller has been disposed.
	 */
	private boolean mDisposed;

	/**
	 * Constructor.
	 */
	protected DesktopNotificationController() {
		mNotificationManager = NotificationPlugin.getDefault()
				.getNotificationManager();
		mNotificationManager.subscribe(this);
	}

	/**
	 * This implementation of {@link ISubscriber#isInteresting(Object)} returns
	 * true for all {@link INotification} <code>inData</code> with severity
	 * greater than or equal to the severity chosen in the user preferences.
	 * 
	 * Subclasses may override to customize the filtering. However, all
	 * implementations must reject <code>inData</code> that is not an
	 * {@link INotification}.
	 */
	@Override
	public boolean isInteresting(Object inData) {
		return inData instanceof INotification
				&& NotificationPlugin.getDefault().shouldDisplayPopup(
						((INotification) inData).getSeverity());
	}

	/**
	 * This implementation of {@link ISubscriber#publishTo(Object)} caches the
	 * notification in a queue. The first time this method is called, the queue
	 * is created by calling {@link #createQueue()} and the job is created by
	 * calling {@link #createJob(Queue)}.
	 * 
	 * After {@link #dispose()} is called, this method will do nothing.
	 */
	@Override
	public final synchronized void publishTo(final Object inData) {
		if (!mDisposed) {
			if (mQueue == null) {
				mQueue = createQueue();
				mPopupJob = createJob(mQueue);
				mPopupJob.addJobChangeListener(new JobChangeAdapter() {
					@Override
					public void done(IJobChangeEvent event) {
						if (Status.CANCEL_STATUS.equals(event.getResult())) {
							// if the job is cancelled, notifications 
							// will no longer be processed
							dispose();
						}
					}
				});
				mPopupJob.schedule();
			}
			mQueue.add((INotification) inData);
		}
	}

	/**
	 * Helper method to create a queue to cache incoming notifications.
	 * 
	 * Subclasses may override to provide a custom queue implementation. The
	 * queue must be thread-safe.
	 * 
	 * @return a thread-safe queue for {@link INotification} objects
	 */
	protected Queue<INotification> createQueue() {
		return new ConcurrentLinkedQueue<INotification>();
	}

	/**
	 * Helper method to create a {@link Job} to processes {@link INotification}
	 * objects in the given queue. The job is responsible for presenting the
	 * notification to the user. The default implementation uses
	 * {@link PopupJob}.
	 * 
	 * Subclasses may override to use a different implementation. Neither the
	 * subclass nor the created {@link Job} should write to the queue. It is
	 * intended to be read-only. The created job must remove items from the
	 * queue in a timely manner and is responsible for re-scheduling itself as
	 * necessary.
	 * 
	 * @param queue
	 *            the queue that will contain notifications
	 * @return the job that will present the notification to the user
	 */
	protected Job createJob(Queue<INotification> queue) {
		return new PopupJob(queue, PlatformUI.getWorkbench().getDisplay());
	}

	/**
	 * Stops the controller from listening for notifications and displaying
	 * popups. After this method has been called, this object should no longer
	 * be used.
	 */
	public final synchronized void dispose() {
		mDisposed = true;
		mNotificationManager.unsubscribe(this);
		if (mPopupJob != null) {
			mPopupJob.cancel();
			mPopupJob = null;
		}
		mQueue = null;
	}
}
