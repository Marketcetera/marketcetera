package org.marketcetera.core.ws.tags;

import java.util.UUID;
import org.marketcetera.api.attributes.ClassVersion;

/**
 * A session ID. Each stateful client connection to a server has a
 * unique ID during a session delineated by a login and a logout. New
 * IDs should be obtained using {@link #generate()}.
 * 
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id: SessionId.java 82324 2012-04-09 20:56:08Z colin $
 */

/* $License$ */

@ClassVersion("$Id: SessionId.java 82324 2012-04-09 20:56:08Z colin $")
public class SessionId
    extends Tag
{

    // CLASS DATA.

    private static final long serialVersionUID=1L;


    // CONSTRUCTORS.

    /**
     * Creates a new session ID with the given ID value.
     *
     * @param value The ID value.
     */

    private SessionId
        (String value)
    {
        super(value);
    }

    /**
     * Creates a new session ID. This empty constructor is intended
     * for use by JAXB.
     */

    protected SessionId() {}


    // CLASS METHODS.

    /**
     * Returns a new, unique session ID.
     *
     * @return The ID.
     */

    public static SessionId generate()
    {
        return new SessionId(UUID.randomUUID().toString());
    }
}
