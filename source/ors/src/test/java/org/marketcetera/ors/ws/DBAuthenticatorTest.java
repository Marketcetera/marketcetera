package org.marketcetera.ors.ws;

import org.junit.Test;
import org.marketcetera.core.ApplicationVersion;
import org.marketcetera.util.test.TestCaseBase;

import static org.junit.Assert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 1.5.0
 * @version $Id$
 */

/* $License$ */

public class DBAuthenticatorTest
    extends TestCaseBase
{
    @Test
    public void basics()
    {
        assertFalse(DBAuthenticator.compatibleVersions
                    (null,"x"));
        assertFalse(DBAuthenticator.compatibleVersions
                    ("x",null));
        assertFalse(DBAuthenticator.compatibleVersions
                    ("x","y"));

        assertFalse(ApplicationVersion.DEFAULT_VERSION.equals("x"));
        assertTrue(DBAuthenticator.compatibleVersions
                   ("x","x"));
        assertTrue(DBAuthenticator.compatibleVersions
                   ("x",ApplicationVersion.DEFAULT_VERSION));
    }
}
