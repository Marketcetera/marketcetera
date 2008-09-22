package org.marketcetera.photon.notification;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.Queue;

import org.eclipse.core.runtime.AssertionFailedException;
import org.eclipse.swt.widgets.Display;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.marketcetera.core.notifications.INotification;
import org.marketcetera.core.notifications.INotification.Severity;
import org.marketcetera.photon.notification.tests.NotificationUtil;

/* $License$ */

/**
 * Test {@link PopupJob}.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public class PopupJobTest {
	
	private NotificationPlugin mockPlugin;
	private Queue<INotification> mockQueue;
	private Display mockDisplay;
	

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() {
		mockPlugin = mock(NotificationPlugin.class);
		NotificationPlugin.setOverride(mockPlugin);
		
		mockQueue = mock(Queue.class);
		mockDisplay = mock(Display.class);
	}
	
	@After
	public void tearDown() {
		NotificationPlugin.setOverride(null);
	}
	
	@Test(expected=AssertionFailedException.class)
	public void testConstructor() {
		new PopupJob(null, null);
	}
	
	@Test(expected=AssertionFailedException.class)
	public void testConstructor2() {
		new PopupJob(mockQueue, null);
	}
	
	@Test(expected=AssertionFailedException.class)
	public void testConstructor3() {
		new PopupJob(null, mockDisplay);
	}

	@Test
	public void testNotificationFilter() {
		stub(mockPlugin.shouldDisplayPopup((Severity) anyObject())).toReturn(true);
		new PopupJob(mockQueue, mockDisplay).showPopup(NotificationUtil.createNotification(Severity.HIGH));
		new PopupJob(mockQueue, mockDisplay).showPopup(NotificationUtil.createNotification(Severity.MEDIUM));
		new PopupJob(mockQueue, mockDisplay).showPopup(NotificationUtil.createNotification(Severity.LOW));
		verify(mockDisplay, times(3)).syncExec((Runnable) anyObject());
	}

	@Test
	public void testNotificationFilter2() {
		stub(mockPlugin.shouldDisplayPopup((Severity) anyObject())).toReturn(false);
		new PopupJob(mockQueue, mockDisplay).showPopup(NotificationUtil.createNotification(Severity.HIGH));
		new PopupJob(mockQueue, mockDisplay).showPopup(NotificationUtil.createNotification(Severity.MEDIUM));
		new PopupJob(mockQueue, mockDisplay).showPopup(NotificationUtil.createNotification(Severity.LOW));
		verify(mockDisplay, times(0)).syncExec((Runnable) anyObject());
	}
}
