package org.marketcetera.photon.notification;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleView;
import org.junit.Test;
import org.marketcetera.photon.test.util.SWTTestUtil;

/* $License$ */

/**
 * Test {@link NotificationConsoleController}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public class NotificationConsoleControllerTest {

	/**
	 * Simple test to verify the openConsole method indeed opens the 
	 * Notification console and brings it to the front.
	 */
	@Test
	public void testOpenConsole() {
		NotificationConsoleController fixture = new NotificationConsoleController();
		fixture.openConsole();
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

}
