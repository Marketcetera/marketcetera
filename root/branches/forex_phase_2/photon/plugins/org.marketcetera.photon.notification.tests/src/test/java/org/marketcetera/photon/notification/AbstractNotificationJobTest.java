package org.marketcetera.photon.notification;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.runtime.IProgressMonitor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.marketcetera.core.notifications.INotification;
import org.marketcetera.core.notifications.INotification.Severity;
import org.marketcetera.photon.notification.AbstractNotificationJob.ThresholdReachedNotification;
import org.marketcetera.photon.notification.tests.NotificationUtil;
import org.marketcetera.photon.test.MultiThreadedTestBase;

/* $License$ */

/**
 * Tests {@link AbstractNotificationJob}.
 * 
 * TODO: these tests are not tightly synchronized and may fail due to race conditions.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 0.8.0
 */
public class AbstractNotificationJobTest extends MultiThreadedTestBase {

	private static final long TIMEOUT = AbstractNotificationJob.FREQUENCY * 5;
	private static final TimeUnit TIMEOUT_UNIT = TimeUnit.MILLISECONDS;

	private SynchronousQueue<INotification> mProcessed;
	private AbstractNotificationJob mJob;
	
	@Before
	public void setUp() {
		mProcessed = new SynchronousQueue<INotification>();
		mJob = new AbstractNotificationJob("Test Popup Job") {
			@Override
			public void showPopup(final INotification notification, final IProgressMonitor monitor) {
				try {
					mProcessed.offer(notification, TIMEOUT, TIMEOUT_UNIT);
				} catch (Throwable e) {
					setFailure(e);
				}
			}
		};
		mJob.schedule(2000);
	}

	@After
	public void tearDown() {
		mJob.cancel();
	}

	@Test
	public void burst() throws InterruptedException {
		for (int i = 0; i < 4; i++) {
			mJob.enqueueNotification(createNotification(Severity.HIGH));
		}
		checkFailureAndAssertEquals(createSummaryExpectation(4, Severity.HIGH), mProcessed
				.poll(TIMEOUT, TimeUnit.MILLISECONDS));
	}

	@Test
	public void summaryAggregation() throws InterruptedException {
		mJob.enqueueNotification(createNotification(Severity.LOW));
		mJob.enqueueNotification(createNotification(Severity.MEDIUM));
		checkFailureAndAssertEquals(createSummaryExpectation(2, Severity.MEDIUM), mProcessed
				.poll(TIMEOUT, TimeUnit.MILLISECONDS));
	}

	@Test
	public void summaryAggregation2() throws InterruptedException {
		mJob.enqueueNotification(createNotification(Severity.LOW));
		mJob.enqueueNotification(createNotification(Severity.MEDIUM));
		mJob.enqueueNotification(createNotification(Severity.HIGH));
		mJob.enqueueNotification(createNotification(Severity.LOW));
		checkFailureAndAssertEquals(createSummaryExpectation(4, Severity.HIGH), mProcessed
				.poll(TIMEOUT, TimeUnit.MILLISECONDS));
	}

	@Test
	public void threshold() throws InterruptedException {
		for (int i = 0; i < AbstractNotificationJob.THRESHOLD + 2; i++) {
			mJob.enqueueNotification(createNotification(Severity.LOW));
		}
		INotification processedNotification = mProcessed.poll(TIMEOUT, TimeUnit.MILLISECONDS);
		checkFailureAndAssertTrue(processedNotification instanceof ThresholdReachedNotification);
	}

	private INotification createNotification(final Severity severity) {
		return NotificationUtil.createNotification(severity);
	}

	private INotification createSummaryExpectation(final int count,
			final Severity severity) {
		return NotificationUtil.createSummaryExpectation(count, severity);
	}
}
