package org.marketcetera.photon.notification;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.runtime.jobs.Job;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.marketcetera.core.notifications.INotification;
import org.marketcetera.core.notifications.INotification.Severity;
import org.marketcetera.photon.notification.AbstractPopupJob.ThresholdReachedNotification;
import org.marketcetera.photon.notification.tests.NotificationUtil;
import org.marketcetera.photon.test.MultiThreadedTestBase;

/* $License$ */

/**
 * Tests AbstractPopupJob.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public class AbstractPopupJobTest extends MultiThreadedTestBase {

	private static final long TIMEOUT = AbstractPopupJob.FREQUENCY * 5;
	private static final TimeUnit TIMEOUT_UNIT = TimeUnit.MILLISECONDS;

	private Queue<INotification> queue;
	private SynchronousQueue<INotification> processed;
	private Job job;
	
	@Before
	public void setUp() {
		queue = new ConcurrentLinkedQueue<INotification>();
		processed = new SynchronousQueue<INotification>();
		job = new AbstractPopupJob("Test Popup Job", queue) {
			@Override
			public void showPopup(final INotification notification) {
				try {
					processed.offer(notification, TIMEOUT, TIMEOUT_UNIT);
				} catch (Throwable e) {
					setFailure(e);
				}
			}
		};
		job.schedule();
	}

	@After
	public void tearDown() {
		job.cancel();
	}

	@Test
	public void steadyStream() throws InterruptedException {
		for (int i = 0; i < 4; i++) {
			final INotification notification = createNotification(Severity.HIGH);
			queue.add(notification);
			checkFailureAndAssertEquals(notification, processed.poll(TIMEOUT, TIMEOUT_UNIT));
		}
	}

	@Test
	public void burst() throws InterruptedException {
		for (int i = 0; i < 4; i++) {
			queue.add(createNotification(Severity.HIGH));
		}
		checkFailureAndAssertEquals(createSummaryExpectation(4, Severity.HIGH), processed
				.poll(TIMEOUT, TimeUnit.MILLISECONDS));
	}

	@Test
	public void summaryAggregation() throws InterruptedException {
		queue.add(createNotification(Severity.LOW));
		queue.add(createNotification(Severity.MEDIUM));
		checkFailureAndAssertEquals(createSummaryExpectation(2, Severity.MEDIUM), processed
				.poll(TIMEOUT, TimeUnit.MILLISECONDS));
	}

	@Test
	public void summaryAggregation2() throws InterruptedException {
		queue.add(createNotification(Severity.LOW));
		queue.add(createNotification(Severity.MEDIUM));
		queue.add(createNotification(Severity.HIGH));
		queue.add(createNotification(Severity.LOW));
		checkFailureAndAssertEquals(createSummaryExpectation(2, Severity.HIGH), processed
				.poll(TIMEOUT, TimeUnit.MILLISECONDS));
	}

	@Test
	public void threshold() throws InterruptedException {
		for (int i = 0; i < AbstractPopupJob.THRESHOLD + 2; i++) {
			queue.add(createNotification(Severity.LOW));
		}
		INotification processedNotification = processed.poll(TIMEOUT, TimeUnit.MILLISECONDS);
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
