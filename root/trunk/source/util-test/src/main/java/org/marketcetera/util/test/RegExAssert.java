package org.marketcetera.util.test;

import java.util.regex.Pattern;
import org.marketcetera.core.ClassVersion;

import static org.junit.Assert.*;

/**
 * Assertions based on regular expression matching.
 * 
 * @author tlerios
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id")
public final class RegExAssert
{

    // CLASS METHODS.

    /**
     * Asserts that the given string matches the given pattern. This
     * assertion holds if both pattern and string are null. If the
     * assertion does not hold, the {@link AssertionError} thrown
     * starts with the given message, which may be null if no such
     * custom message prefix is desired.
     *
     * @param message The message.
     * @param pattern The pattern.
     * @param string The string.
     */

    public static void assertMatches
        (String message,
         String pattern,
         String string)
    {
        if ((pattern==null) && (string==null)) {
            return;
        }
        String content=null;
        if (pattern==null) {
            content="pattern is null and does not match '"+string+"'";
        } else if (string==null) {
            content="pattern '"+pattern+"' does not match null";
        } else if (Pattern.matches(pattern,string)) {
            return;
        } else {
            content="pattern '"+pattern+"' does not match '"+string+"'";
        }
        if (message!=null) {
            content=message+" "+content;
        }
        fail(content);
    }

    /**
     * Asserts that the given string matches the given pattern. This
     * assertion holds if both pattern and string are null.
     *
     * @param pattern The pattern.
     * @param string The string.
     */

    public static void assertMatches
        (String pattern,
         String string)
    {
        assertMatches(null,pattern,string);
    }


    // CONSTRUCTOR.

    /**
     * Constructor. It is private so that no instances can be created.
     */

    private RegExAssert() {}
}
