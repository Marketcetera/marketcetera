package org.marketcetera.photon.commons.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.marketcetera.photon.commons.ExceptionUtilsTest;
import org.marketcetera.photon.commons.ReflectiveMessagesTest;
import org.marketcetera.photon.commons.ValidateTest;

/* $License$ */

/**
 * Test suite for this bundle.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@RunWith(Suite.class)
@Suite.SuiteClasses( { ReflectiveMessagesTest.class, ValidateTest.class, ExceptionUtilsTest.class })
public class CommonsSuite {
}
