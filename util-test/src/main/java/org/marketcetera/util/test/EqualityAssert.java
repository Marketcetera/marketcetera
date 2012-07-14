package org.marketcetera.util.test;

import org.apache.commons.lang.math.NumberUtils;

import static org.junit.Assert.*;

/**
 * Assertions for equality (and hash code generation).
 * 
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

public final class EqualityAssert
{

    // CLASS METHODS.

    /**
     * Asserts that the given target object implements equality
     * correctly. If the assertion does not hold, the {@link
     * AssertionError} thrown starts with the given message, which may
     * be null if no such custom message prefix is desired.
     *
     * @param message The message.
     * @param o The target object.
     * @param oEqual Another object that is equal to (but not the same
     * as) the target object.
     * @param osUnequal Objects that are all unequal to the target
     * object. It may be null or contain null elements.
     */

    public static void assertEquality
        (String message,
         Object o,
         Object oEqual,
         Object... osUnequal)
    {
        String content=null;
        if (!o.equals(o)) {
            content="'"+o+"' unequal to self"; //$NON-NLS-1$ //$NON-NLS-2$
        } else if (!o.equals(oEqual)) {
            content="'"+o+"' unequal to '"+ //$NON-NLS-1$ //$NON-NLS-2$
                oEqual+"'"; //$NON-NLS-1$
        } else if (!oEqual.equals(o)) {
            content="'"+oEqual+"' unequal to '"+ //$NON-NLS-1$ //$NON-NLS-2$
                o+"'"; //$NON-NLS-1$
        } else if (oEqual==o) {
            content="'"+oEqual+"' same as '"+ //$NON-NLS-1$ //$NON-NLS-2$
                o+"'"; //$NON-NLS-1$
        } else if (osUnequal!=null) {
            for (Object oUnequal:osUnequal) {
                if (o.equals(oUnequal)) {
                    content="'"+o+"' equal to '"+ //$NON-NLS-1$ //$NON-NLS-2$
                        oUnequal+"'"; //$NON-NLS-1$ 
                    break;
                }
                if ((oUnequal!=null) && (oUnequal.equals(o))) {
                    content="'"+oUnequal+ //$NON-NLS-1$ 
                        "' equal to '"+o+"'"; //$NON-NLS-1$ //$NON-NLS-2$
                    break;
                }
            }
        }
        if (content==null) {
            if (o.equals(null)) {
                content="'"+o+"' equal to null"; //$NON-NLS-1$ //$NON-NLS-2$
            } else if (o.equals(NumberUtils.INTEGER_ZERO)) {
                content="'"+o+"' equal to zero"; //$NON-NLS-1$ //$NON-NLS-2$
            } else if (o.hashCode()!=oEqual.hashCode()) {
                content="'"+o+ //$NON-NLS-1$
                    "' hash code unequal to copy's '"+ //$NON-NLS-1$
                    oEqual+"'"; //$NON-NLS-1$
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
     * Asserts that the given target object implements equality
     * correctly.
     *
     * @param o The target object.
     * @param oEqual Another object that is equal to (but not the same
     * as) the target object.
     * @param osUnequal Objects that are all unequal to the target
     * object. It may be null or contain null elements.
     */

    public static void assertEquality
        (Object o,
         Object oEqual,
         Object... osUnequal)
    {
        assertEquality(null,o,oEqual,osUnequal);
    }

    /**
     * Asserts that the given target object implements equality
     * correctly. If the assertion does not hold, the {@link
     * AssertionError} thrown starts with the given message, which may
     * be null if no such custom message prefix is desired.
     *
     * @param message The message.
     * @param o The target object.
     * @param equalIndex The index in the collection below for another
     * object that is equal to (but not the same as) the target
     * object; all other collection items are unequal to the target
     * object.
     * @param osOther A nonempty collection of objects.
     */

    public static void assertEquality
        (String message,
         Object o,
         int equalIndex,
         Object... osOther)
    {
        Object oEqual=null;
        Object[] osUnequal=new Object[osOther.length-1];
        int j=0;
        for (int i=0;i<osOther.length;i++) {
            if (i==equalIndex) {
                oEqual=osOther[i];
            } else {
                osUnequal[j++]=osOther[i];
            }
        }
        assertEquality(message,o,oEqual,osUnequal);
    }

    /**
     * Asserts that the given target object implements equality
     * correctly.
     *
     * @param o The target object.
     * @param equalIndex The index in the collection below for another
     * object that is equal to (but not the same as) the target
     * object; all other collection items are unequal to the target
     * object.
     * @param osOther A nonempty collection of objects.
     */

    public static void assertEquality
        (Object o,
         int equalIndex,
         Object... osOther)
    {
        assertEquality(null,o,equalIndex,osOther);
    }


    // CONSTRUCTOR.

    /**
     * Constructor. It is private so that no instances can be created.
     */

    private EqualityAssert() {}
}
