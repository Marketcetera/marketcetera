package org.marketcetera.photon.notification;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.junit.Test;
import org.marketcetera.core.notifications.Notification;


/* $License$ */

/**
 * Test {@link DesktopNotificationPopup}.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.1.0
 */
public class DesktopNotificationPopupTest {

	/**
	 * This test will hang if the popup does not automatically close.
	 */
	@Test
	public void autoClose() {
		Display display = PlatformUI.getWorkbench().getDisplay();
		DesktopNotificationPopup popup = new DesktopNotificationPopup(display, Notification.high("Test", "Test", getClass().toString()));
		popup.open();
		Shell s = popup.getShell();
		while(!s.isDisposed())
			if (!display.readAndDispatch())
				display.sleep();
	}
}
