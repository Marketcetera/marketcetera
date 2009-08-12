package org.marketcetera.ors.info;

import org.marketcetera.util.misc.ClassVersion;

/**
 * A store for key-value pairs specific to a session: implementation.
 *
 * <p>This class is not intended to be thread-safe.</p>
 *
 * @author tlerios@marketcetera.com
 * @since $Release$
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class SessionInfoImpl
    extends NestedInfoImpl<SystemInfo>
    implements SessionInfo
{

    // CLASS DATA.

    private static final NameGenerator NAME_GENERATOR=
        new NameGenerator("SessionInfo"); //$NON-NLS-1$


    // CONSTRUCTORS.

    /**
     * Creates a new store with an automatically assigned name and
     * encompassed by the given system store.
     *
     * @param systemInfo The system store.
     */

    public SessionInfoImpl
        (SystemInfo systemInfo)
    {
        this(NAME_GENERATOR.getNextName(),systemInfo);
    }

    /**
     * Creates a new store with the given name and encompassed by the
     * given system store.
     *
     * @param name The store name.
     * @param systemInfo The system store.
     */

    public SessionInfoImpl
        (String name,
         SystemInfo systemInfo)
    {
        super(name,systemInfo);
    }


    // SessionInfo.

    @Override
    public SystemInfo getSystemInfo()
    {
        return getParentInfo();
    } 
}