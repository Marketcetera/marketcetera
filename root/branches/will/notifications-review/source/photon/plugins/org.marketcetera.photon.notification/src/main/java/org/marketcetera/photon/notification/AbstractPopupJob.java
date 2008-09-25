package org.marketcetera.photon.notification;

import java.util.Date;
import java.util.Queue;

import org.eclipse.core.runtime.Assert;
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
 * Abstract base class for popup notification job. Handles the queue logic, but
 * delegates view aspect to subclasses.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")//$NON-NLS-1$
public abstract class AbstractPopupJob extends Job {

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
	 * Constructor. Will throw an unchecked exception if <code>queue</code> is
	 * null.
	 * 
	 * @param name
	 *            the name of the job, see {@link Job#Job(String)}
	 * @param queue
	 *            the queue from which to retrieve notifications, must be
	 *            non-null and thread safe
	 */
	protected AbstractPopupJob(String name, Queue<INotification> queue) {
		super(name);
		Assert.isNotNull(queue);
		this.mQueue = queue;
	}

	/**
	 * This implementation of {@link Job#run(IProgressMonitor)} processes
	 * notifications in the queue, summarizing them if necessary. Unless
	 * something goes wrong, this method will reschedule the job.
	 */
	@Override
	protected final IStatus run(IProgressMonitor monitor) {
		int size = mQueue.size();
		if (size == 0) {
			// Wait a bit before running again
			schedule(FREQUENCY);
		} else if (size == 1) {
			// Show popup for the single notification on the queue
			INotification n = mQueue.poll();
			if (n != null)
				showPopup(n);
			schedule();
		} else {
			// Too many notifications, summarize into one
			Severity max = Severity.LOW;
			int count = 0;
			while (!mQueue.isEmpty()) {
				while (!mQueue.isEmpty()) {
					INotification n = mQueue.poll();
					if (n != null) {
						count++;
						// End job if threshold is reached
						if (count > THRESHOLD) {
							showPopup(new ThresholdReachedNotification());
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
					// TODO: should I re-throw? do anything else
					Thread.currentThread().interrupt();
				}
			}
			if (count > 0)
				showPopup(new SummaryNotification(count, max));
			schedule();
		}

		return Status.OK_STATUS;
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
	 */
	public abstract void showPopup(INotification notification);

	/**
	 * A notification that summarizes a set of notifications when they arrive
	 * too quickly.
	 * 
	 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
	 * @version $Id$
	 * @since $Release$
	 */
	@ClassVersion("$Id$")//$NON-NLS-1$
	public static class SummaryNotification extends Notification {

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
					new Date(), severity, AbstractPopupJob.class);
		}
	}

	/**
	 * A notification that indicates popups have been disabled.
	 * 
	 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
	 * @version $Id$
	 * @since $Release$
	 */
	@ClassVersion("$Id$")//$NON-NLS-1$
	public static class ThresholdReachedNotification extends Notification {

		/**
		 * Constructor.
		 */
		public ThresholdReachedNotification() {
			super(Messages.THRESHOLD_NOTIFICATION_SUBJECT.getText(),
					Messages.THRESHOLD_NOTIFICATION_BODY.getText(), new Date(),
					Severity.HIGH, AbstractPopupJob.class);
		}
	}

}
