package org.marketcetera.photon.notification.tests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.marketcetera.photon.notification.AbstractNotificationJobTest;
import org.marketcetera.photon.notification.NotificationPluginTest;
import org.marketcetera.photon.notification.DesktopNotificationControllerTest;
import org.marketcetera.photon.notification.PopupJobTest;
import org.marketcetera.photon.notification.preferences.NotificationPreferencesTest;

/* $License$ */

/**
 * Test suite that can run as a regular JUnit suite.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 0.8.0
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
	AbstractNotificationJobTest.class,
	PopupJobTest.class,
	NotificationPreferencesTest.class,
	NotificationPluginTest.class,
	DesktopNotificationControllerTest.class
})
public class HeadlessSuite {

}
