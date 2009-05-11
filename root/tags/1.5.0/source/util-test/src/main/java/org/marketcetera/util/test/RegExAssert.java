package org.marketcetera.util.test;

import java.util.regex.Pattern;

import static org.junit.Assert.*;

/**
 * Assertions based on regular expression matching.
 * 
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

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
            content="pattern is null and does not match '"+ //$NON-NLS-1$
                string+"'"; //$NON-NLS-1$
        } else if (string==null) {
            content="pattern '"+pattern+ //$NON-NLS-1$
                "' does not match null"; //$NON-NLS-1$
        } else if (Pattern.matches(pattern,string)) {
            return;
        } else {
            content="pattern '"+pattern+ //$NON-NLS-1$
                "' does not match '"+string+"'"; //$NON-NLS-1$ //$NON-NLS-2$
        }
        if (message!=null) {
            content=message+" "+content; //$NON-NLS-1$
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
