package org.marketcetera.photon.notification;

import static org.junit.Assert.*;

import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleView;
import org.junit.Test;
import org.marketcetera.photon.test.util.SWTTestUtil;

public class NotificationConsoleControllerTest {

	@Test
	public void testOpenConsole() {
		NotificationConsoleController fixture = new NotificationConsoleController();
		fixture.openConsole();
		SWTTestUtil.delay(3000);
		IConsoleView view = (IConsoleView) PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().findView(
						IConsoleConstants.ID_CONSOLE_VIEW);
		assertEquals("Notifications", view.getConsole().getName());
	}	

}
