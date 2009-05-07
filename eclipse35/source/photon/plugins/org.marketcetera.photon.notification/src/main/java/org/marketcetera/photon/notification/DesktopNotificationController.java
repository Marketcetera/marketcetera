package org.marketcetera.photon.notification;

import java.util.Queue;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
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
 * <li>{@link #createJob(Queue)} - to provide an alternate
 * {@link AbstractNotificationJob} implementation</li>
 * </ul>
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 0.8.0
 */
@ClassVersion("$Id$")//$NON-NLS-1$
class DesktopNotificationController implements ISubscriber {

	/**
	 * Core notification manager
	 */
	private final INotificationManager mNotificationManager;

	/**
	 * Scheduled job for processing notifications, lazily instantiated
	 */
	private AbstractNotificationJob mPopupJob;

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
	 * Decides whether a popup should be displayed for the given
	 * <code>inData</code>.
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
	 * This implementation of {@link ISubscriber#publishTo(Object)} enqueues the
	 * {@link INotification} to be processed by an
	 * {@link AbstractNotificationJob}.
	 * 
	 * After {@link #dispose()} is called, this method will do nothing.
	 */
	@Override
	public final synchronized void publishTo(final Object inData) {
		if (!mDisposed) {
			if (mPopupJob == null) {
				mPopupJob = createJob();
				mPopupJob.addJobChangeListener(new JobChangeAdapter() {
					@Override
					public void done(IJobChangeEvent event) {
						if (event.getResult().getSeverity() == IStatus.CANCEL) {
							// if the job is canceled, notifications
							// will no longer be processed
							dispose();
						}
					}
				});
				mPopupJob.schedule();
			}
			mPopupJob.enqueueNotification((INotification) inData);
		}
	}

	/**
	 * Helper method to create a {@link AbstractNotificationJob} responsible for
	 * presenting the notification to the user. The default implementation uses
	 * {@link PopupJob}.
	 * 
	 * Subclasses may override to use a different implementation. The created
	 * job must process notifications in a timely manner and is responsible for
	 * re-scheduling itself as necessary.
	 * 
	 * @return the job that will present the notification to the user
	 */
	protected AbstractNotificationJob createJob() {
		return new PopupJob(PlatformUI.getWorkbench().getDisplay());
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
	}
}
