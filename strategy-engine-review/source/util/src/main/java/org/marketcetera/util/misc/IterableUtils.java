package org.marketcetera.util.misc;

import org.apache.commons.collections.CollectionUtils;

/**
 * Utilities for iterables.
 *
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public final class IterableUtils
{

    // CLASS METHODS.

    /**
     * Returns the size of the given iterable.
     *
     * @param src The iterable.
     *
     * @return The size.
     */

    public static int size
        (Iterable<?> src)
    {
        return CollectionUtils.size(src.iterator());
    }

    /**
     * Converts the given iterable to an array and returns the result.
     *
     * @param src The iterable.
     *
     * @return The array.
     */

    public static Object[] toArray
        (Iterable<?> src)
    {
        Object[] result=new Object[size(src)];
        int i=0;
        for (Object item:src) {
            result[i++]=item;
        }
        return result;
    }


    // CONSTRUCTOR.

    /**
     * Constructor. It is private so that no instances can be created.
     */

    private IterableUtils() {}
}
