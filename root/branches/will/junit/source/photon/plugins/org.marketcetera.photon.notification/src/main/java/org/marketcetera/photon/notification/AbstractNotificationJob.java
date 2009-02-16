package org.marketcetera.photon.notification;

import java.util.Date;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.marketcetera.core.notifications.INotification;
import org.marketcetera.core.notifications.Notification;
import org.marketcetera.core.notifications.INotification.Severity;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * A {@link Job} that processes {@link INotification} objects asynchronously.
 * Clients call {@link #enqueueNotification(INotification)} to queue a
 * notifications to be processed when this job runs.
 * 
 * When multiple notifications are enqueued between runs, they will be replaced
 * by a single {@link SummaryNotification}.
 * 
 * If too many notifications arrive in a short period of time, a
 * {@link ThresholdReachedNotification} will be issued and the job will stop
 * running, ignoring any later notifications.
 * 
 * Subclasses must define how a notification should processed by overriding
 * {@link #showPopup(INotification, IProgressMonitor)}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 0.8.0
 */
@ClassVersion("$Id$")//$NON-NLS-1$
public abstract class AbstractNotificationJob extends Job {

	/**
	 * The frequency in milliseconds at which the job checks for new
	 * notifications.
	 */
	/* default for test access */static final long FREQUENCY = 2000;

	/**
	 * When too many notifications arrive, a single summary notification is
	 * generated. Before the summary is shown, there must be a break in the
	 * notification stream. This constant is the minimum time in milliseconds
	 * that the stream must be "quiet".
	 */
	/* default for test access */static final long SUMMARY_DELAY = 1000;

	/**
	 * When the notification queue surpasses the threshold, the job will shut
	 * down and a single popup will be displayed to inform the user.
	 */
	/* default for test access */static final int THRESHOLD = 50;

	/**
	 * The queue which provides notifications.
	 */
	private final Queue<INotification> mQueue;

	/**
	 * Constructor.
	 * 
	 * @param name
	 *            the name of the job, see {@link Job#Job(String)}
	 */
	protected AbstractNotificationJob(String name) {
		super(name);
		this.mQueue = new ConcurrentLinkedQueue<INotification>();
	}

	/**
	 * This implementation of {@link Job#run(IProgressMonitor)} processes
	 * notifications in the queue, summarizing them if necessary. Unless the
	 * threshold is exceeded or the job is interrupted/canceled, this method
	 * will reschedule the job.
	 */
	@Override
	protected final IStatus run(IProgressMonitor monitor) {
		long delay = 0;
		INotification notification = null;
		int size = mQueue.size();
		if (size == 0) {
			// Wait a bit before running again
			delay = FREQUENCY;
		} else if (size == 1) {
			// Show popup for the single notification on the queue
			notification = mQueue.poll();
			if (notification != null) {
				showPopup(notification, monitor);
			}
		} else {
			// Too many notifications, summarize into one
			Severity max = Severity.LOW;
			int count = 0;
			while (!monitor.isCanceled() && !mQueue.isEmpty()) {
				while (!mQueue.isEmpty()) {
					INotification n = mQueue.poll();
					if (n != null) {
						count++;
						// End job if threshold is reached
						if (count > THRESHOLD) {
							showPopup(new ThresholdReachedNotification(),
									monitor);
							return Status.CANCEL_STATUS;
						}
						// Update max severity
						max = max.compareTo(n.getSeverity()) > 0 ? max : n
								.getSeverity();
					}
				}
				try {
					// The idea here is to wait a bit for more notifications. In
					// other words, the summary notification isn't generated
					// until there is a break in the notification stream.
					Thread.sleep(SUMMARY_DELAY);
				} catch (InterruptedException e) {
					// Cancelling is the proper way to notify an interrupt.
					// The framework ignores
					// Thread.currentThread().isInterrupted()
					return Status.CANCEL_STATUS;
				}
			}
			if (count > 0 && !monitor.isCanceled())
				showPopup(new SummaryNotification(count, max), monitor);
		}

		if (monitor.isCanceled()) {
			return Status.CANCEL_STATUS;
		}

		schedule(delay);
		return Status.OK_STATUS;
	}

	/**
	 * Enqueues an {@link INotification} to be processed by this job. If the job
	 * is not scheduled, this method does nothing.
	 * 
	 * @param notification
	 *            notification for this job to process asynchronously.
	 */
	public void enqueueNotification(INotification notification) {
		if (getState() != Job.NONE) {
			mQueue.add(notification);
		}
	}

	/**
	 * Process the notification.
	 * 
	 * This method should not return until the notification has been completely
	 * processed and the user is ready to receive another, e.g. when the popup
	 * has been closed.
	 * 
	 * @param notification
	 *            notification to process
	 * @param monitor
	 *            the monitor to be used for reporting progress and responding
	 *            to cancellation. The monitor is never <code>null</code>
	 */
	protected abstract void showPopup(INotification notification,
			IProgressMonitor monitor);

	/**
	 * A notification that summarizes a set of notifications when they arrive
	 * too quickly.
	 * 
	 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
	 * @version $Id$
	 * @since 0.8.0
	 */
	@ClassVersion("$Id$")//$NON-NLS-1$
	public static class SummaryNotification extends Notification {

	    private static final long serialVersionUID = 1L;

	    /**
		 * Constructor.
		 * 
		 * @param count
		 *            the number of notifications summarized
		 * @param severity
		 *            the severity of the summary notification
		 */
		public SummaryNotification(int count, Severity severity) {
			super(Messages.SUMMARY_NOTIFICATION_SUBJECT.getText(),
					Messages.SUMMARY_NOTIFICATION_BODY.getText(count),
					new Date(), severity, AbstractNotificationJob.class.getName());
		}
	}

	/**
	 * A notification that indicates popups have been disabled.
	 * 
	 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
	 * @version $Id$
	 * @since 0.8.0
	 */
	@ClassVersion("$Id$")//$NON-NLS-1$
	public static class ThresholdReachedNotification extends Notification {

        private static final long serialVersionUID = 1L;

        /**
		 * Constructor.
		 */
		public ThresholdReachedNotification() {
			super(Messages.THRESHOLD_NOTIFICATION_SUBJECT.getText(),
					Messages.THRESHOLD_NOTIFICATION_BODY.getText(), new Date(),
					Severity.HIGH, AbstractNotificationJob.class.toString());
		}
	}

}
