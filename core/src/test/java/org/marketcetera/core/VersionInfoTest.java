package org.marketcetera.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.util.test.EqualityAssert;
import org.springframework.util.SerializationUtils;

/* $License$ */

/**
 * Tests {@link #VersionInfo}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.4.0
 */
public class VersionInfoTest
{
    /**
     * Tests {@link VersionInfo#VersionInfo(String)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testConstructor()
            throws Exception
    {
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                new VersionInfo(null);
            }
        };
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                new VersionInfo("");
            }
        };
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                new VersionInfo("    ");
            }
        };
        verify(new VersionInfo("2.4.0"),
               2,
               4,
               0,
               "2.4.0",
               false);
        verify(new VersionInfo("              2.4.0               "),
               2,
               4,
               0,
               "2.4.0",
               false);
        verify(new VersionInfo("2.4.0-SNAPSHOT"),
               2,
               4,
               0,
               "2.4.0-SNAPSHOT",
               true);
        verify(new VersionInfo("100.100000." + Integer.MAX_VALUE),
               100,
               100000,
               Integer.MAX_VALUE,
               "100.100000." + Integer.MAX_VALUE,
               false);
    }
    /**
     * Tests {@link VersionInfo#isValid(String)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testIsValid()
            throws Exception
    {
        assertFalse(VersionInfo.isValid(null));
        assertFalse(VersionInfo.isValid(""));
        assertFalse(VersionInfo.isValid("    "));
        assertTrue(VersionInfo.isValid("2.4.0"));
        assertTrue(VersionInfo.isValid("              2.4.0               "));
        assertTrue(VersionInfo.isValid("2.4.0-SNAPSHOT"));
        assertTrue(VersionInfo.isValid("100.100000." + Integer.MAX_VALUE));
    }
    /**
     * Tests {@link VersionInfo#equals(Object)} and {@link VersionInfo#hashCode()}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testHashcodeAndEquals()
            throws Exception
    {
        VersionInfo version1 = new VersionInfo("2.4.0");
        VersionInfo version2 = new VersionInfo("2.4.0");
        VersionInfo version3 = new VersionInfo("2.5.0");
        VersionInfo version4 = new VersionInfo("2.4.2");
        EqualityAssert.assertEquality(version1,
                                      version2,
                                      version3,
                                      null,
                                      this);
        EqualityAssert.assertEquality(version1,
                                      version4);
    }
    /**
     * Tests ability to serialize {@link VersionInfo}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testSerializable()
            throws Exception
    {
        VersionInfo version1 = new VersionInfo("2.4.0");
        VersionInfo version2 = (VersionInfo)SerializationUtils.deserialize(SerializationUtils.serialize(version1));
        assertEquals(version1.getVersionInfo(),
                     version2.getVersionInfo());
    }
    /**
     * Verifies that the given actual object matches the given expected attributes.
     *
     * @param inActualVersion a <code>VersionInfo</code> value
     * @param inExpectedMajor an <code>int</code> value
     * @param inExpectedMinor an <code>int</code> value
     * @param inExpectedPatch an <code>int</code> value
     * @param inExpectedVersionInfo a <code>String</code> value
     * @param inExpectedSnapshot a <code>boolean</code> value
     * @throws Exception if an unexpected error occurs
     */
    private void verify(VersionInfo inActualVersion,
                        int inExpectedMajor,
                        int inExpectedMinor,
                        int inExpectedPatch,
                        String inExpectedVersionInfo,
                        boolean inExpectedSnapshot)
            throws Exception
    {
        assertNotNull(inActualVersion.toString());
        assertEquals(inExpectedMajor,
                     inActualVersion.getMajor());
        assertEquals(inExpectedMinor,
                     inActualVersion.getMinor());
        assertEquals(inExpectedPatch,
                     inActualVersion.getPatch());
        assertEquals(inExpectedVersionInfo,
                     inActualVersion.getVersionInfo());
        assertEquals(inExpectedSnapshot,
                     inActualVersion.getIsSnapshot());
    }
}
