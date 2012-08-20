package org.marketcetera.security;

import org.marketcetera.core.util.log.I18NBoundMessage;

/**
 * A wrapper around a data holder that sets the holder's data. A
 * holder may have more than one setter.
 *
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id: Setter.java 82324 2012-04-09 20:56:08Z colin $
 */

/* $License$ */

public class Setter<T extends Holder<?>>
{

    // INSTANCE DATA.

    private T mHolder;
    private I18NBoundMessage mUsage;


    // CONSTRUCTORS.

    /**
     * Creates a new setter, associated with the given holder, and
     * with the given usage instructions.
     *
     * @param holder The data holder.
     * @param usage The usage instructions.
     */

    public Setter
        (T holder,
         I18NBoundMessage usage)
    {
        mHolder=holder;
        mUsage=usage;
    }


    // INSTANCE METHODS.

    /**
     * Returns the receiver's holder.
     *
     * @return The holder.
     */

    public T getHolder()
    {
        return mHolder;
    }

    /**
     * Returns the receiver's usage instructions.
     *
     * @return The instructions.
     */

    public I18NBoundMessage getUsage()
    {
        return mUsage;
    }
}
