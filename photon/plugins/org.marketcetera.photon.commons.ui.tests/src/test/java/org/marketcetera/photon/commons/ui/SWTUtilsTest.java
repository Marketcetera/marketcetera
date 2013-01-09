package org.marketcetera.photon.commons.ui;

import org.junit.Test;
import org.marketcetera.photon.test.ExpectedIllegalStateException;
import org.marketcetera.photon.test.PhotonTestBase;

/* $License$ */

/**
 * Tests {@link SWTUtils}.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
public class SWTUtilsTest extends PhotonTestBase {

    @Test
    public void testCheckThread() throws Exception {
        new ExpectedThreadCheckFailure() {
            @Override
            protected void run() throws Exception {
                SWTUtils.checkThread();
            }
        };
    }
    
    /**
     * Utility to test thread checks.
     */
    public abstract static class ExpectedThreadCheckFailure extends ExpectedIllegalStateException {

        /**
         * Constructor.
         * 
         * @throws Exception
         *             if there was an unexpected failure
         */
        protected ExpectedThreadCheckFailure() throws Exception {
            super("Must be called from a UI thread.");
        }
        
    }
}
