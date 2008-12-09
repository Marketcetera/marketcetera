package org.marketcetera.util.test;

import static org.junit.Assert.*;

/**
 * Assertions for {@link Comparable}.
 * 
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

public final class ComparableAssert
{

    // CLASS METHODS.

    /**
     * Asserts that the given target object implements {@link
     * Comparable} correctly. If the assertion does not hold, the {@link
     * AssertionError} thrown starts with the given message, which may
     * be null if no such custom message prefix is desired.
     *
     * @param message The message.
     * @param c The target object.
     * @param cEqual Another object that is equal to (but not the same
     * as) the target object.
     * @param cGreater Another object that is greater than the target
     * object.
     * @param nullMessage The message of the {@link
     * NullPointerException} expected for comparison against null. If
     * null, no message check is performed.
     */

    public static <T extends Comparable<? super T>> void assertComparable
        (String message,
         T c,
         T cEqual,
         T cGreater,
         String nullMessage)
    {
        String content=null;
        if (c.compareTo(c)!=0) {
            content="'"+c+"' unequal to self";
        } else if (cEqual.compareTo(cEqual)!=0) {
            content="'"+cEqual+"' unequal to self";
        } else if (cGreater.compareTo(cGreater)!=0) {
            content="'"+cGreater+"' unequal to self";
        } else if (c.compareTo(cEqual)!=0) {
            content="'"+c+"' unequal to '"+cEqual+"'";
        } else if (cEqual.compareTo(c)!=0) {
            content="'"+cEqual+"' unequal to '"+c+"'";
        } else if (cEqual==c) {
            content="'"+cEqual+"' same as '"+c+"'";
        } else if (c.compareTo(cGreater)>=0) {
            content="'"+c+"' no less than '"+cGreater+"'";
        } else if (cEqual.compareTo(cGreater)>=0) {
            content="'"+cEqual+"' no less than '"+cGreater+"'";
        } else if (cGreater.compareTo(c)<=0) {
            content="'"+cGreater+"' no more than '"+c+"'";
        } else if (cGreater.compareTo(cEqual)<=0) {
            content="'"+cGreater+"' no more than '"+cEqual+"'";
        } else {
            try {
                c.compareTo(null);
                content="null argument did not fail";
            } catch (NullPointerException ex) {
                if ((nullMessage!=null) &&
                    !nullMessage.equals(ex.getMessage())) {
                    content="expected message '"+nullMessage+
                        "' does not match actual '"+ex.getMessage()+"'";
                }
            }
        }
        if (content==null) {
            return;
        }
        if (message!=null) {
            content=message+" "+content;
        }
        fail(content);
    }

    /**
     * Asserts that the given target object implements {@link
     * Comparable} correctly.
     *
     * @param c The target object.
     * @param cEqual Another object that is equal to (but not the same
     * as) the target object.
     * @param cGreater Another object that is greater than the target
     * object.
     * @param nullMessage The message of the {@link
     * NullPointerException} expected for comparison against null. If
     * null, no message check is performed.
     */

    public static <T extends Comparable<? super T>> void assertComparable
        (T c,
         T cEqual,
         T cGreater,
         String nullMessage)
    {
        assertComparable(null,c,cEqual,cGreater,nullMessage);
    }

    /**
     * Asserts that the given target object implements {@link
     * Comparable} correctly. No message check takes place during
     * comparison against null. If the assertion does not hold, the {@link
     * AssertionError} thrown starts with the given message, which may
     * be null if no such custom message prefix is desired.
     *
     * @param message The message.
     * @param c The target object.
     * @param cEqual Another object that is equal to (but not the same
     * as) the target object.
     * @param cGreater Another object that is greater than the target
     * object.
     */

    public static <T extends Comparable<? super T>> void assertComparable
        (String message,
         T c,
         T cEqual,
         T cGreater)
    {
        assertComparable(message,c,cEqual,cGreater,null);
    }

    /**
     * Asserts that the given target object implements {@link
     * Comparable} correctly. No message check takes place during
     * comparison against null.
     *
     * @param c The target object.
     * @param cEqual Another object that is equal to (but not the same
     * as) the target object.
     * @param cGreater Another object that is greater than the target
     * object.
     */

    public static <T extends Comparable<? super T>> void assertComparable
        (T c,
         T cEqual,
         T cGreater)
    {
        assertComparable(null,c,cEqual,cGreater);
    }


    // CONSTRUCTOR.

    /**
     * Constructor. It is private so that no instances can be created.
     */

    private ComparableAssert() {}
}
