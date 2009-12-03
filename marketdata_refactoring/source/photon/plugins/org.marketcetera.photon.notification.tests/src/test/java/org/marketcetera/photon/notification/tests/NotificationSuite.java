package org.marketcetera.photon.notification.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.marketcetera.photon.notification.AbstractNotificationJobTest;
import org.marketcetera.photon.notification.DesktopNotificationControllerTest;
import org.marketcetera.photon.notification.DesktopNotificationPopupTest;
import org.marketcetera.photon.notification.NotificationConsoleControllerTest;
import org.marketcetera.photon.notification.NotificationPluginTest;
import org.marketcetera.photon.notification.PopupJobTest;
import org.marketcetera.photon.notification.preferences.NotificationPreferencesTest;

/* $License$ */

/**
 * Test suite for this bundle.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 0.8.0
 */
@RunWith(Suite.class)
@Suite.SuiteClasses( { AbstractNotificationJobTest.class, PopupJobTest.class,
        NotificationPreferencesTest.class, NotificationPluginTest.class,
        DesktopNotificationControllerTest.class,
        NotificationConsoleControllerTest.class,
        DesktopNotificationPopupTest.class })
public class NotificationSuite {
}
