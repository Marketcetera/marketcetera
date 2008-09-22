package org.marketcetera.photon.notification;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleView;
import org.junit.Before;
import org.junit.Test;
import org.marketcetera.core.notifications.INotification;
import org.marketcetera.photon.test.SWTTestUtil;

/* $License$ */

/**
 * Test {@link NotificationConsoleController}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public class NotificationConsoleControllerTest {
	
	private NotificationConsoleController mFixture;

	@Before
	public void setUp() {
		mFixture = new NotificationConsoleController();
	}

	/**
	 * Simple test to verify the openConsole method indeed opens the 
	 * Notification console and brings it to the front.
	 */
	@Test
	public void testOpenConsole() {
		mFixture.openConsole();
		SWTTestUtil.conditionalDelayUnchecked(10, TimeUnit.SECONDS,
				new Callable<Boolean>() {
					@Override
					public Boolean call() {
						IConsoleView view = (IConsoleView) PlatformUI
								.getWorkbench().getActiveWorkbenchWindow()
								.getActivePage().findView(
										IConsoleConstants.ID_CONSOLE_VIEW);
						return Messages.NOTIFICATION_CONSOLE_NAME.getText()
								.equals(view.getConsole().getName());
					}
				});
	}

	@Test
	public void testIsInteresting() {
		assertFalse(mFixture.isInteresting(new Object()));
		assertTrue(mFixture.isInteresting(mock(INotification.class)));
	}

}
