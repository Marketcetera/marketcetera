package org.marketcetera.photon.commons.ui.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.marketcetera.photon.commons.ui.table.TableSupportTest;

/* $License$ */

/**
 * Test suite that can run as a regular JUnit suite.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@RunWith(Suite.class)
@Suite.SuiteClasses( { TableSupportTest.class })
public class HeadlessSuite {
}
