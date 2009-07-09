package org.marketcetera.photon.module.ui.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.marketcetera.photon.internal.module.ui.MessagesTest;
import org.marketcetera.photon.internal.module.ui.PropertiesTreeTest;

/* $License$ */

/**
 * Test suite that can run as a regular JUnit suite.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
	PropertiesTreeTest.class,
	MessagesTest.class
})
public class HeadlessSuite {

}
