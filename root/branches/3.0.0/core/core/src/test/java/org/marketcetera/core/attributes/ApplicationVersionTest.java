package org.marketcetera.core.attributes;

import org.junit.BeforeClass;
import org.junit.Test;
import org.marketcetera.core.LoggerConfiguration;
import org.marketcetera.util.test.UnicodeData;

import static org.junit.Assert.assertEquals;

/* $License$ */
/**
 * Tests {@link org.marketcetera.core.attributes.ApplicationVersion}
 *
 * @author anshul@marketcetera.com
 * @version $Id: ApplicationVersionTest.java 82306 2012-02-29 23:18:25Z colin $
 * @since 1.5.0
 */
@ClassVersion("$Id: ApplicationVersionTest.java 82306 2012-02-29 23:18:25Z colin $")
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
        assertEquals(ApplicationVersion.DEFAULT_VERSION,
                     ApplicationVersion.getVersion());
        assertEquals(ApplicationVersion.DEFAULT_BUILD,
                     ApplicationVersion.getBuildNumber());
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
        assertEquals(ApplicationVersion.DEFAULT_VERSION,
                     ApplicationVersion.getVersion(UnicodeData.class));
        assertEquals(ApplicationVersion.DEFAULT_BUILD,
                     ApplicationVersion.getBuildNumber(UnicodeData.class));
    }
}
