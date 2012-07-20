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
            content="'"+c+"' unequal to self"; //$NON-NLS-1$ //$NON-NLS-2$
        } else if (cEqual.compareTo(cEqual)!=0) {
            content="'"+cEqual+"' unequal to self"; //$NON-NLS-1$ //$NON-NLS-2$
        } else if (cGreater.compareTo(cGreater)!=0) {
            content="'"+cGreater+ //$NON-NLS-1$
                "' unequal to self"; //$NON-NLS-1$
        } else if (c.compareTo(cEqual)!=0) {
            content="'"+c+"' unequal to '"+ //$NON-NLS-1$ //$NON-NLS-2$
                cEqual+"'"; //$NON-NLS-1$
        } else if (cEqual.compareTo(c)!=0) {
            content="'"+cEqual+"' unequal to '"+ //$NON-NLS-1$ //$NON-NLS-2$
                c+"'"; //$NON-NLS-1$
        } else if (cEqual==c) {
            content="'"+cEqual+"' same as '"+ //$NON-NLS-1$ //$NON-NLS-2$
                c+"'"; //$NON-NLS-1$
        } else if (c.compareTo(cGreater)>=0) {
            content="'"+c+"' no less than '"+ //$NON-NLS-1$ //$NON-NLS-2$
                cGreater+"'"; //$NON-NLS-1$
        } else if (cEqual.compareTo(cGreater)>=0) {
            content="'"+cEqual+"' no less than '"+ //$NON-NLS-1$ //$NON-NLS-2$
                cGreater+"'"; //$NON-NLS-1$
        } else if (cGreater.compareTo(c)<=0) {
            content="'"+cGreater+ //$NON-NLS-1$
                "' no more than '"+c+"'"; //$NON-NLS-1$ //$NON-NLS-2$
        } else if (cGreater.compareTo(cEqual)<=0) {
            content="'"+cGreater+ //$NON-NLS-1$
                "' no more than '"+cEqual+"'"; //$NON-NLS-1$ //$NON-NLS-2$
        } else {
            try {
                c.compareTo(null);
                content="null argument did not fail"; //$NON-NLS-1$
            } catch (NullPointerException ex) {
                if ((nullMessage!=null) &&
                    !nullMessage.equals(ex.getMessage())) {
                    content="expected message '"+nullMessage+ //$NON-NLS-1$
                        "' does not match actual '"+ //$NON-NLS-1$
                        ex.getMessage()+"'"; //$NON-NLS-1$
                }
            }
        }
        if (content==null) {
            return;
        }
        if (message!=null) {
            content=message+" "+content; //$NON-NLS-1$
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
