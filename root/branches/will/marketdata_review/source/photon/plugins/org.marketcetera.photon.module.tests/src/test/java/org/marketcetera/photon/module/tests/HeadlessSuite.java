package org.marketcetera.photon.module.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.marketcetera.photon.module.PropertiesTreeTest;
import org.marketcetera.photon.module.preferences.NewPropertyInputDialogTest;

/* $License$ */

/**
 * Test suite that can run as a regular JUnit suite.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
	PropertiesTreeTest.class,
	NewPropertyInputDialogTest.class
})
public class HeadlessSuite {

}
