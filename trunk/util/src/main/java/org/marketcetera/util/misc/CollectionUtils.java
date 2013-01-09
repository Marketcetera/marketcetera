package org.marketcetera.util.misc;

import java.util.List;
import java.util.ListIterator;

/**
 * Utilities for collections.
 * 
 * @author tlerios@marketcetera.com
 * @since 0.6.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public final class CollectionUtils
{

    // CLASS METHODS.

    /**
     * Returns the last non-null element in the given list; or, if
     * there is no non-null element, it returns null.
     *
     * @param l The list. It may be null, in which case null is
     * returned.
     *
     * @return The element.
     */
    
    public static <E> E getLastNonNull
        (List<E> l)
    {
        if (l==null) {
            return null;
        }
        ListIterator<E> i=l.listIterator(l.size());
        while (i.hasPrevious()) {
            E e=i.previous();
            if (e!=null) {
                return e;
            }
        }
        return null;
    }

    /**
     * Returns the non-null integers in the given list as an array.
     *
     * @param l The list. It may be null, in which case null is
     * returned.
     *
     * @return The array.
     */
    
    public static int[] toArray
        (List<Integer> l)
    {
        if (l==null) {
            return null;
        }
        int count=0;
        for (Integer e:l) {
            if (e!=null) {
                count++;
            }
        }
        int[] result=new int[count];
        int i=0;
        for (Integer e:l) {
            if (e!=null) {
                result[i++]=e;
            }
        }
        return result;
    }


    // CONSTRUCTOR.

    /**
     * Constructor. It is private so that no instances can be created.
     */

    private CollectionUtils() {}
}
