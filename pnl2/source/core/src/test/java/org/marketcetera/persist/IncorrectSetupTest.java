package org.marketcetera.persist;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.LoggerConfiguration;
import static org.marketcetera.persist.Messages.ERS_NOT_INITIALIZED;
import static org.marketcetera.persist.Messages.JPA_VENDOR_NOT_INITIALIZED;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.BeforeClass;

/* $License$ */
/**
 * This class tests various failures around setup of persistence
 * infrastructure
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class IncorrectSetupTest extends PersistTestBase {
    @Test
    public void remoteServicesNotConfigured() throws Exception {
        try {
            EntityRemoteServices.getInstance();
            fail("instance should not be initialized"); //$NON-NLS-1$
        } catch(PersistSetupException expected) {
            assertEquals(ERS_NOT_INITIALIZED,expected.getI18NBoundMessage());
        }
    }
    @Test
    public void jpaVendorNotConfigured() throws Exception {
        try {
            VendorUtils.initBlob();
            fail("instance should not be initialized"); //$NON-NLS-1$
        } catch(PersistSetupException expected) {
            assertEquals(JPA_VENDOR_NOT_INITIALIZED,
                    expected.getI18NBoundMessage());
        }
        try {
            VendorUtils.initClob();
            fail("instance should not be initialized"); //$NON-NLS-1$
        } catch(PersistSetupException expected) {
            assertEquals(JPA_VENDOR_NOT_INITIALIZED,
                    expected.getI18NBoundMessage());
        }
    }
    @BeforeClass
    public static void setup() {
        LoggerConfiguration.logSetup();
    }
}
