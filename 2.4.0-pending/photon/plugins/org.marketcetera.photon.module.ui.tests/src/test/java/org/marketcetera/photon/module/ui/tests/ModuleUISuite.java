package org.marketcetera.photon.module.ui.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.marketcetera.photon.internal.module.ui.MessagesTest;
import org.marketcetera.photon.internal.module.ui.PreferencesAdapterTest;
import org.marketcetera.photon.internal.module.ui.PropertiesTreeTest;

/* $License$ */

/**
 * Test suite for this bundle.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@RunWith(Suite.class)
@Suite.SuiteClasses( { PropertiesTreeTest.class, MessagesTest.class,
        PreferencesAdapterTest.class })
public class ModuleUISuite {
}
