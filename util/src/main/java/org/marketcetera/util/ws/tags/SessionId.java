package org.marketcetera.util.ws.tags;

import java.util.UUID;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * A session ID. Each stateful client connection to a server has a
 * unique ID during a session delineated by a login and a logout. New
 * IDs should be obtained using {@link #generate()}.
 * 
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */
@ClassVersion("$Id$")
public class SessionId
        extends Tag
{
    /**
     * Create a new SessionId instance.
     *
     * @param inValue a <code>String</code> value
     */
    public SessionId(String inValue)
    {
        super(inValue);
    }
    /**
     * Returns a new, unique session ID.
     *
     * @return a <code>SessionId</code> value
     */
    public static SessionId generate()
    {
        return new SessionId(UUID.randomUUID().toString());
    }
    /**
     * Create a new SessionId instance.
     */
    protected SessionId() {}
    private static final long serialVersionUID=1L;
}
