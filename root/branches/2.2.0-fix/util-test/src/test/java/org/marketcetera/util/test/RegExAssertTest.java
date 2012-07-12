
package org.marketcetera.util.test;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.marketcetera.util.test.RegExAssert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

public class RegExAssertTest
{
    @Test
    public void match()
    {
        assertMatches(".*ab.*","zabc");
        assertMatches("(?s).*ab.*","zabc\n");
        assertMatches(null,null);
    }

    @Test
    public void patternNull()
    {
        try {
            assertMatches(null,"a");
        } catch (AssertionError ex) {
            assertEquals
                ("pattern is null and does not match 'a'",ex.getMessage());
            return;
        }
        fail();
    }

    @Test
    public void actualNull()
    {
        try {
            assertMatches("a",null);
        } catch (AssertionError ex) {
            assertEquals
                ("pattern 'a' does not match null",ex.getMessage());
            return;
        }
        fail();
    }

    @Test
    public void mismatch()
    {
        try {
            assertMatches("a","b");
        } catch (AssertionError ex) {
            assertEquals
                ("pattern 'a' does not match 'b'",ex.getMessage());
            return;
        }
        fail();
    }

    @Test
    public void message()
    {
        try {
            assertMatches("Right now,","a","b");
        } catch (AssertionError ex) {
            assertEquals
                ("Right now, pattern 'a' does not match 'b'",ex.getMessage());
            return;
        }
        fail();
    }
}
