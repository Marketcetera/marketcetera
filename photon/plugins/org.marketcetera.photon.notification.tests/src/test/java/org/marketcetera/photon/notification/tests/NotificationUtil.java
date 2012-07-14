package org.marketcetera.photon.notification.tests;

import java.util.Date;

import org.marketcetera.core.notifications.INotification;
import org.marketcetera.core.notifications.Notification;
import org.marketcetera.core.notifications.INotification.Severity;
import org.marketcetera.photon.notification.AbstractNotificationJob.SummaryNotification;

/* $License$ */

/**
 * Utilities for creating test notifications.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 0.8.0
 */
public class NotificationUtil {

	/**
	 * Creates a simple notification for the given severity.
	 * 
	 * @param severity
	 *            severity of the notification to create
	 * @return the notification
	 */
	public static INotification createNotification(final Severity severity) {
		return new Notification("Subject", "Body", new Date(), severity,
				NotificationUtil.class.toString()) {
                    private static final long serialVersionUID = 1L;
		};
	}

	/**
	 * Creates a summary notification of the given severity and notification
	 * count.
	 * 
	 * @param count
	 *            the number of notifications being summarized
	 * @param severity
	 *            the overall severity
	 * @return the notification
	 */
	public static INotification createSummaryExpectation(final int count,
			final Severity severity) {
		return new SummaryNotification(count, severity) {
            private static final long serialVersionUID = 1L;
            @Override
			public boolean equals(final Object obj) {
				if (this == obj)
					return true;
				if (obj instanceof SummaryNotification) {
					final SummaryNotification other = (SummaryNotification) obj;
					return getSeverity().equals(other.getSeverity())
							&& getBody().equals(other.getBody());
				}
				return false;
			}
		};
	}
}
