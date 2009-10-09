package org.marketcetera.photon.module.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.marketcetera.photon.internal.module.MessagesTest;
import org.marketcetera.photon.internal.module.NotificationHandlerTest;
import org.marketcetera.photon.internal.module.PreferenceAttributeDefaultsTest;
import org.marketcetera.photon.internal.module.SinkDataManagerTest;

/* $License$ */

/**
 * Test suite for this bundle.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@RunWith(Suite.class)
@Suite.SuiteClasses( { MessagesTest.class, SinkDataManagerTest.class,
        NotificationHandlerTest.class, PreferenceAttributeDefaultsTest.class })
public class ModuleCoreSuite {
}
