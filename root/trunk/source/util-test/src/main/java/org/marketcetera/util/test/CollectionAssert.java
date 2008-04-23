package org.marketcetera.util.test;

import org.apache.commons.collections.Bag;
import org.apache.commons.collections.bag.HashBag;
import org.marketcetera.core.ClassVersion;

import static org.junit.Assert.*;

/**
 * Assertions for collections.
 * 
 * @author tlerios
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id")
public final class CollectionAssert
{

    // CLASS METHODS.

    /**
     * Returns a bag with the same elements as the given array.
     *
     * @param array The array.
     *
     * @return The bag.
     */

    private static Bag toBag
        (Object[] array)
    {
        Bag bag=new HashBag();
        for (Object e:array) {
            bag.add(e);
        }
        return bag;
    }

    /**
     * Asserts that the two given arrays are permutations of each
     * other. This assertion holds if both arrays are null, or if they
     * have one or more (but an equal number of) null elements. If the
     * assertion does not hold, the {@link AssertionError} thrown
     * starts with the given message, which may be null if no such
     * custom message prefix is desired.
     *
     * @param message The identifying message.
     * @param expected The expected array.
     * @param actual The actual array.
     */

    public static <T> void assertArrayPermutation
        (String message,
         T[] expected,
         T[] actual)
    {
        if ((expected==null) && (actual==null)) {
            return;
        }
        String content=null;
        if (expected==null) {
            content="expected array is null but actual is not";
        } else if (actual==null) {
            content="actual array is null but expected is not";
        } else {
            Bag expectedBag=toBag(expected);
            Bag actualBag=toBag(actual);
            for (Object e:expectedBag) {
                if (!actualBag.remove(e,1)) {
                    content="actual is missing '"+e+"'";
                    break;
                }
            }
            if (content==null) {
                if (actualBag.size()==0) {
                    return;
                }
                content="actual contains extra elements such as "+
                    actualBag.iterator().next();
            }
        }
        if (message!=null) {
            content=message+" "+content;
        }
        fail(content);
    }

    /**
     * Asserts that the two given arrays are permutations of each
     * other. This assertion holds if both arrays are null, or if they
     * have one or more (but an equal number of) null elements.
     *
     * @param expected The expected array.
     * @param actual The actual array.
     */

    public static <T> void assertArrayPermutation
        (T[] expected,
         T[] actual)
    {
        assertArrayPermutation(null,expected,actual);
    }


    // CONSTRUCTOR.

    /**
     * Constructor. It is private so that no instances can be created.
     */

    private CollectionAssert() {}
}
