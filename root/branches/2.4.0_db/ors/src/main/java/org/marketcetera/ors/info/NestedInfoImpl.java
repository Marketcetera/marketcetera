package org.marketcetera.ors.info;

import org.marketcetera.util.misc.ClassVersion;

/**
 * A store for key-value pairs, which also has a parent store.
 *
 * <p>This class is not intended to be thread-safe.</p>
 *
 * @author tlerios@marketcetera.com
 * @since 2.0.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
class NestedInfoImpl<T extends ReadInfo>
    extends ReadWriteInfoImpl
{

    // CLASS DATA.

    /**
     * The name separator in paths.
     */

    static final String NAME_SEPARATOR=
        ":"; //$NON-NLS-1$


    // INSTANCE DATA.

    private final T mParentInfo;


    // CONSTRUCTORS.

    /**
     * Creates a new store with the given name and the given parent
     * store.
     *
     * @param name The store name.
     * @param parentInfo The parent store.
     */

    NestedInfoImpl
        (String name,
         T parentInfo)
    {
        super(name);
        mParentInfo=parentInfo;
    }


    // INSTANCE METHODS.

    /**
     * Returns the receiver's parent store.
     *
     * @return The parent store.
     */

    T getParentInfo()
    {
        return mParentInfo;
    }


    // ReadWriteInfoImpl.

    @Override
    public String getPath()
    {
        StringBuilder builder=new StringBuilder();
        builder.append(getParentInfo().getPath());
        builder.append(NAME_SEPARATOR);
        builder.append(super.getPath());
        return builder.toString();
    }
}
