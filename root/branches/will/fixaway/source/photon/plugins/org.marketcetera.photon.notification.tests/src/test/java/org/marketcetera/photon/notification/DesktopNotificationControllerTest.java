package org.marketcetera.photon.notification;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import org.junit.After;
import org.junit.Before;
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
 * @since 0.8.0
 */
public class DesktopNotificationControllerTest {

	private NotificationPlugin mMockPlugin;
	
	private INotificationManager mMockNotificationManager;
	
	private AbstractNotificationJob mMockJob;
	
	private DesktopNotificationController mFixture;

	@Before
	public void setUp() {
		mMockPlugin = mock(NotificationPlugin.class);
		NotificationPlugin.setOverride(mMockPlugin);
		mMockNotificationManager = mock(INotificationManager.class);
		stub(mMockPlugin.getNotificationManager()).toReturn(mMockNotificationManager);
		mMockJob = mock(AbstractNotificationJob.class);
		mFixture = new DesktopNotificationController() {
			@Override
			protected AbstractNotificationJob createJob() {
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
		verify(mMockJob).enqueueNotification(mockNotification);
		mFixture.dispose();
		verify(mMockNotificationManager).unsubscribe(mFixture);
	}
}
