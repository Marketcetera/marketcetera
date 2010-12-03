package org.marketcetera.photon.commons.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.marketcetera.photon.commons.ExceptionUtilsTest;
import org.marketcetera.photon.commons.ReflectiveMessagesTest;
import org.marketcetera.photon.commons.SimpleExecutorServiceTest;
import org.marketcetera.photon.commons.SynchronizedProxyTest;
import org.marketcetera.photon.commons.ValidateTest;
import org.marketcetera.photon.commons.emf.EMFFilePersistenceTest;
import org.marketcetera.photon.commons.osgi.HighestRankedTrackerTest;

/* $License$ */

/**
 * Test suite for this bundle.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@RunWith(Suite.class)
@Suite.SuiteClasses( { ReflectiveMessagesTest.class, ValidateTest.class,
        ExceptionUtilsTest.class,
        org.marketcetera.photon.commons.MessagesTest.class,
        SimpleExecutorServiceTest.class, EMFFilePersistenceTest.class,
        HighestRankedTrackerTest.class, SynchronizedProxyTest.class })
public final class CommonsSuite {
}
