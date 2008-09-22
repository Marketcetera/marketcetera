package org.marketcetera.photon.notification;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.marketcetera.core.notifications.INotification;

/* $License$ */

/**
 * Tests for {@link DesktopNotificationController}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public class DesktopNotificationControllerTest {

	private DesktopNotificationController fixture;

	@Before
	public void setUp() {
		fixture = new DesktopNotificationController();
	}

	@Test
	public void testIsInteresting() {
		assertFalse(fixture.isInteresting(new Object()));
		assertTrue(fixture.isInteresting(mock(INotification.class)));
	}
}
