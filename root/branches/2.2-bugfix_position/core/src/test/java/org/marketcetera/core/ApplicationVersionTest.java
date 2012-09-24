package org.marketcetera.core;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.test.UnicodeData;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

/* $License$ */
/**
 * Tests {@link ApplicationVersion}
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public class ApplicationVersionTest
{
    /**
     * Run once before all tests.
     *
     * @throws Exception if an unexpected error occurs
     */
    @BeforeClass
    public static void once()
            throws Exception
    {
        LoggerConfiguration.logSetup();
    }
    /**
     * Basic version tests.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void basic()
            throws Exception
    {
        assertEquals(ApplicationVersion.DEFAULT_DIALECT,
                     ApplicationVersion.getDialectNumber());
        assertEquals(ApplicationVersion.DEFAULT_VERSION,
                     ApplicationVersion.getVersionNumber());
        assertEquals(ApplicationVersion.DEFAULT_BUILD,
                     ApplicationVersion.getBuildNumber());
        assertEquals(ApplicationVersion.DEFAULT_APPLICATION,
                     ApplicationVersion.getApplicationNumber());
    }
    /**
     * Tests that a class with no embedded resource still returns a value.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testsMissingResource()
            throws Exception
    {
        assertEquals(ApplicationVersion.DEFAULT_DIALECT,
                     ApplicationVersion.getDialectNumber(UnicodeData.class));
        assertEquals(ApplicationVersion.DEFAULT_VERSION,
                     ApplicationVersion.getVersionNumber(UnicodeData.class));
        assertEquals(ApplicationVersion.DEFAULT_BUILD,
                     ApplicationVersion.getBuildNumber(UnicodeData.class));
        assertEquals(ApplicationVersion.DEFAULT_APPLICATION,
                     ApplicationVersion.getApplicationNumber(UnicodeData.class));
    }
}
