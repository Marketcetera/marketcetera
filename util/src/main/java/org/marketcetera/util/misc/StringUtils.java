package org.marketcetera.util.misc;

/**
 * Utilities for string management.
 * 
 * @author tlerios@marketcetera.com
 * @since 0.6.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public final class StringUtils
{

    // CLASS METHODS.

    /**
     * Returns the number of Unicode code points in the given string.
     *
     * @param s The string.
     *
     * @return The number of Unicode code points.
     */

    public static int lengthUCP
        (String s)
    {
        return s.codePointCount(0,s.length());
    }

    /**
     * Returns a string containing the characters with the given
     * Unicode code points.
     *
     * @param ucps The code points.
     *
     * @return The string.
     */

    public static String fromUCP
        (int[] ucps)
    {
        return new String(ucps,0,ucps.length);
    }

    /**
     * Returns a string containing a single character, namely the
     * given Unicode code point.
     *
     * @param ucp The code point.
     *
     * @return The string.
     */

    public static String fromUCP
        (int ucp)
    {
        return fromUCP(new int[] {ucp});
    }

    /**
     * Returns an array containing the Unicode code points of the
     * given string.
     *
     * @param s The string. It may be null, in which case null is
     * returned.
     *
     * @return The code points.
     */

    public static int[] toUCPArray
        (String s)
    {
        if (s==null) {
            return null;
        }
        int[] result=new int[lengthUCP(s)];
        int i=0;
        int j=0;
        while (i<s.length()) {
            int c=s.codePointAt(i);
            result[j++]=c;
            i+=Character.charCount(c);
        }
        return result;
    }

    /**
     * Returns a string containing the integer representations, in
     * uppercase hex, of the Unicode code points of the given string.
     *
     * @param s The input string. It may be null, in which case null
     * is returned.
     *
     * @return The resulting string.
     */

    public static String toUCPArrayStr
        (String s)
    {
        if (s==null) {
            return null;
        }
        StringBuilder builder=new StringBuilder();
        boolean addSpace=false;
        for (int c:toUCPArray(s)) {
            if (addSpace) {
                builder.append(' '); //$NON-NLS-1$
            }
            builder.append("U+"); //$NON-NLS-1$
            builder.append(Integer.toHexString(c));
            addSpace=true;
        }
        return builder.toString().toUpperCase();
    }

    /**
     * Checks whether the given integer represents a well-defined
     * unicode code point that is also not an ISO control character or
     * within the surrogate areas (high or low).
     *
     * @param ucp The integer (candidate code point).
     *
     * @return True if so.
     */

    public static boolean isValid
        (int ucp)
    {
        return (Character.isDefined(ucp) &&
                !Character.isISOControl(ucp) &&
                !((0xD800<=ucp) && (ucp<=0xDFFF)));
    }


    // CONSTRUCTOR.

    /**
     * Constructor. It is private so that no instances can be created.
     */

    private StringUtils() {}
}
