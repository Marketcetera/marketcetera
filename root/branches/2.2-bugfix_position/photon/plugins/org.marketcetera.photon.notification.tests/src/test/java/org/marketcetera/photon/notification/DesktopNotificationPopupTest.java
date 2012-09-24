package org.marketcetera.photon.notification;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.marketcetera.core.notifications.Notification;
import org.marketcetera.photon.test.SWTTestUtil;
import org.marketcetera.photon.test.WorkbenchRunner;
import org.marketcetera.photon.test.AbstractUIRunner.UI;

/* $License$ */

/**
 * Test {@link DesktopNotificationPopup}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id: DesktopNotificationPopupTest.java 10324 2009-02-16 21:37:25Z
 *          colin $
 * @since 1.1.0
 */
@RunWith(WorkbenchRunner.class)
public class DesktopNotificationPopupTest {

    @Test
    @UI
    public void autoClose() throws Exception {
        Display display = Display.getCurrent();
        DesktopNotificationPopup popup = new DesktopNotificationPopup(display,
                Notification.high("Test", "Test", getClass().toString()));
        popup.open();
        final Shell s = popup.getShell();
        SWTTestUtil.conditionalDelay(15, TimeUnit.SECONDS,
                new Callable<Boolean>() {

                    @Override
                    public Boolean call() throws Exception {
                        return s.isDisposed();
                    }
                });
    }
}
