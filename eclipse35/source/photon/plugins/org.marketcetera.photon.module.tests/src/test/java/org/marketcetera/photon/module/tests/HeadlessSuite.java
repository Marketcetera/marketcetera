package org.marketcetera.photon.module.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.marketcetera.photon.internal.module.MessagesTest;
import org.marketcetera.photon.internal.module.SinkDataManagerTest;

/* $License$ */

/**
 * Test suite that can run as a regular JUnit suite.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@RunWith(Suite.class)
@Suite.SuiteClasses( { MessagesTest.class, SinkDataManagerTest.class })
public class HeadlessSuite {
}
