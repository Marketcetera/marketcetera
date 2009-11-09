package org.marketcetera.photon.core.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.marketcetera.photon.core.CredentialsTest;
import org.marketcetera.photon.core.InstrumentPrettyPrinterTest;
import org.marketcetera.photon.core.LogoutServiceTest;
import org.marketcetera.photon.core.MessagesTest;

/* $License$ */

/**
 * Test suite for this bundle.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@RunWith(Suite.class)
@Suite.SuiteClasses( { CredentialsTest.class, LogoutServiceTest.class,
        MessagesTest.class, InstrumentPrettyPrinterTest.class })
public final class CoreSuite {
}
