package org.marketcetera.photon.notification;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.Queue;

import org.eclipse.core.runtime.jobs.Job;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.marketcetera.core.notifications.INotification;
import org.marketcetera.core.notifications.INotificationManager;
import org.marketcetera.core.notifications.INotification.Severity;
import org.marketcetera.photon.notification.tests.NotificationUtil;

/* $License$ */

/**
 * Tests for {@link DesktopNotificationController}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public class DesktopNotificationControllerTest {

	private NotificationPlugin mMockPlugin;
	
	private INotificationManager mMockNotificationManager;
	
	private Queue<INotification> mMockQueue;
	
	private Job mMockJob;
	
	private DesktopNotificationController mFixture;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() {
		mMockPlugin = mock(NotificationPlugin.class);
		NotificationPlugin.setOverride(mMockPlugin);
		mMockNotificationManager = mock(INotificationManager.class);
		stub(mMockPlugin.getNotificationManager()).toReturn(mMockNotificationManager);
		mMockQueue = mock(Queue.class);
		mMockJob = mock(Job.class);
		mFixture = new DesktopNotificationController() {
			@Override
			protected Queue<INotification> createQueue() {
				return mMockQueue;
			}
			
			@Override
			protected Job createJob(Queue<INotification> queue) {
				assertEquals(mMockQueue, queue);
				return mMockJob;
			}
		};
	}
	
	@After
	public void tearDown() {
		NotificationPlugin.setOverride(null);
	}

	@Test
	public void testIsInteresting() {
		assertFalse(mFixture.isInteresting(new Object()));
		stub(mMockPlugin.shouldDisplayPopup((Severity) anyObject())).toReturn(true);
		assertTrue(mFixture.isInteresting(NotificationUtil.createNotification(Severity.HIGH)));
		assertTrue(mFixture.isInteresting(NotificationUtil.createNotification(Severity.MEDIUM)));
		assertTrue(mFixture.isInteresting(NotificationUtil.createNotification(Severity.LOW)));
		stub(mMockPlugin.shouldDisplayPopup((Severity) anyObject())).toReturn(false);
		assertFalse(mFixture.isInteresting(NotificationUtil.createNotification(Severity.HIGH)));
		assertFalse(mFixture.isInteresting(NotificationUtil.createNotification(Severity.MEDIUM)));
		assertFalse(mFixture.isInteresting(NotificationUtil.createNotification(Severity.LOW)));
	}
	
	@Test
	public void testPublishTo() {
		verify(mMockNotificationManager).subscribe(mFixture);
		INotification mockNotification = mock(INotification.class);
		mFixture.publishTo(mockNotification);
		verify(mMockQueue).add(mockNotification);
		mFixture.dispose();
		verify(mMockNotificationManager).unsubscribe(mFixture);
	}
}
