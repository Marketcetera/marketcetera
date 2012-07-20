package org.marketcetera.util.misc;

import java.util.HashMap;
import java.util.Vector;

/**
 * A meta-information holder for a {@link UCPFilter}.
 *
 * @author tlerios@marketcetera.com
 * @since 0.6.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class UCPFilterInfo
{

    // CLASS DATA.

    private static final HashMap<UCPFilter,UCPFilterInfo> mMap=
        new HashMap<UCPFilter,UCPFilterInfo>();


    // INSTANCE DATA.

    private int[] mUCPs;


    // CONSTRUCTOR.

    /**
     * Creates a new meta-information holder for the given filter.
     *
     * @param filter The filter.
     */

    private UCPFilterInfo
        (UCPFilter filter)
    {
        Vector<Integer> ucps=new Vector<Integer>();
        for (int ucp=Character.MIN_CODE_POINT;
             ucp<=Character.MAX_CODE_POINT;ucp++) {
            if (filter.isAcceptable(ucp)) {
                ucps.add(ucp);
            }
        }
        mUCPs=CollectionUtils.toArray(ucps);
    }


    // CLASS METHODS.

    /**
     * Returns the meta-information holder for the given filter.
     *
     * @param filter The filter.
     *
     * @return The holder.
     */
    
    public static UCPFilterInfo getInfo
        (UCPFilter filter)
    {
        synchronized (mMap) {
            UCPFilterInfo info=mMap.get(filter);
            if (info!=null) {
                return info;
            }
            info=new UCPFilterInfo(filter);
            mMap.put(filter,info);
            return info;
        }
    }


    // INSTANCE METHODS.    
    
    /**
     * Returns the code points acceptable to the receiver's associated
     * filter, in ascending numerical order.
     *
     * @return The code points.
     */

    public int[] getUCPs()
    {
        return mUCPs;
    }
}
