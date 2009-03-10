package org.marketcetera.photon.notification;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleListener;
import org.eclipse.ui.console.IConsoleView;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.marketcetera.core.notifications.INotification;
import org.marketcetera.core.notifications.INotificationManager;
import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.photon.test.SWTTestUtil;

/* $License$ */

/**
 * Test {@link NotificationConsoleController}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 0.8.0
 */
public class NotificationConsoleControllerTest {

	private NotificationConsoleController mFixture;

	private INotificationManager mMockNotificationManager;
	
	private NotificationPlugin mMockPlugin;

	@Before
	public void setUp() {
		mMockNotificationManager = mock(INotificationManager.class);
		mMockPlugin = mock(NotificationPlugin.class);
		NotificationPlugin.setOverride(mMockPlugin);
		stub(mMockPlugin.getNotificationManager()).toReturn(mMockNotificationManager);
		mFixture = new NotificationConsoleController();
	}
	
	@After
	public void tearDown() {
		NotificationPlugin.setOverride(null);
	}

	/**
	 * Test to verify the openConsole method indeed opens the Notification
	 * Console, subscribes it to the notification manager, and brings it to the
	 * front. Also tests that removing the Notification Console unsubscribes it
	 * from the notification manager.
	 */
	@Test
	public void testOpenConsole() {
		final IConsole[] console = new IConsole[1];
		IConsoleListener listener = new IConsoleListener() {
		
			@Override
			public void consolesAdded(IConsole[] consoles) {
				assertThat(consoles.length, is(1));
				console[0] = consoles[0];
			}
		
			@Override
			public void consolesRemoved(IConsole[] consoles) {
				assertThat(consoles.length, is(1));
				assertThat(consoles[0], sameInstance(console[0]));
				console[0] = null;
			}
		};
		ConsolePlugin.getDefault().getConsoleManager().addConsoleListener(listener);
		mFixture.openConsole();
		verify(mMockNotificationManager).subscribe((ISubscriber) anyObject());
		SWTTestUtil.conditionalDelayUnchecked(10, TimeUnit.SECONDS,
				new Callable<Boolean>() {
					@Override
					public Boolean call() {
						IConsoleView view = getConsoleView();
						return view != null
								&& Messages.NOTIFICATION_CONSOLE_NAME.getText()
										.equals(view.getConsole().getName());
					}
				});
		assertNotNull(console[0]);
		ConsolePlugin.getDefault().getConsoleManager().removeConsoles(
				new IConsole[] { console[0] });
		assertNull(console[0]);
		verify(mMockNotificationManager).unsubscribe((ISubscriber) anyObject());
	}

	@Test
	public void testIsInteresting() {
		assertFalse(mFixture.isInteresting(new Object()));
		assertTrue(mFixture.isInteresting(mock(INotification.class)));
	}

	@Test
	public void testFormat() {
		final String string = Integer.toString(new Random().nextInt());
		assertEquals(string, mFixture.format(new Object() {
			@Override
			public String toString() {
				return string;
			}
		}));
	}

	private IConsoleView getConsoleView() {
		return (IConsoleView) PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().findView(
						IConsoleConstants.ID_CONSOLE_VIEW);
	}

}
