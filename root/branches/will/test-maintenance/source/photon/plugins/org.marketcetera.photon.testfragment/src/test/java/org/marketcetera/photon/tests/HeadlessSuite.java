package org.marketcetera.photon.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.marketcetera.photon.TimeOfDayTest;

/* $License$ */

/**
 * Test suite that can run as a regular JUnit suite.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@RunWith(Suite.class)
@Suite.SuiteClasses( {
		TimeOfDayTest.class })
public class HeadlessSuite {

}
