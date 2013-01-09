package org.marketcetera.ors.info;

import org.marketcetera.util.misc.ClassVersion;

/**
 * A store for key-value pairs specific to a request: implementation.
 *
 * <p>This class is not intended to be thread-safe.</p>
 *
 * @author tlerios@marketcetera.com
 * @since 2.0.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class RequestInfoImpl
    extends NestedInfoImpl<SessionInfo>
    implements RequestInfo
{

    // CLASS DATA.

    private static final NameGenerator NAME_GENERATOR=
        new NameGenerator("RequestInfo"); //$NON-NLS-1$


    // CONSTRUCTORS.

    /**
     * Creates a new store with an automatically assigned name and
     * encompassed by the given session store.
     *
     * @param sessionInfo The session store.
     */

    public RequestInfoImpl
        (SessionInfo sessionInfo)
    {
        this(NAME_GENERATOR.getNextName(),sessionInfo);
    }

    /**
     * Creates a new store with the given name and encompassed by the
     * given session store.
     *
     * @param name The store name.
     * @param sessionInfo The session store.
     */

    public RequestInfoImpl
        (String name,
         SessionInfo sessionInfo)
    {
        super(name,sessionInfo);
    }


    // RequestInfo.

    @Override
    public SessionInfo getSessionInfo()
    {
        return getParentInfo();
    }
}
