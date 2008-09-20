package org.marketcetera.photon.notification;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.junit.Before;
import org.junit.Test;
import org.marketcetera.core.notifications.INotification;

/* $License$ */

/**
 * Tests for {@link PhotonNotificationSubscriber}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public class PhotonNotificationSubscriberTest {

	private PhotonNotificationSubscriber fixture;

	@Before
	public void setUp() {
		fixture = new PhotonNotificationSubscriber();
	}

	@Test
	public void testIsInteresting() {
		assertFalse(fixture.isInteresting(new Object()));
		assertTrue(fixture.isInteresting(mock(INotification.class)));
	}
	
	@Test
	public void testGetNotificationQueue() {
		Queue<INotification> queue = fixture.getNotificationQueue();
		assertTrue(queue instanceof ConcurrentLinkedQueue);
		assertEquals(queue, fixture.getNotificationQueue());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testPublishTo() {
		final Queue<INotification> mockQueue = mock(Queue.class);
		fixture = new PhotonNotificationSubscriber() {
			@Override
			public Queue<INotification> getNotificationQueue() {
				return mockQueue;
			}
		};
		INotification mockNotification = mock(INotification.class);
		fixture.publishTo(mockNotification);
		verify(mockQueue).add(mockNotification);
	}
}
