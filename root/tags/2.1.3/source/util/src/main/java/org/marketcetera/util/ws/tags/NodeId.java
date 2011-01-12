package org.marketcetera.util.ws.tags;

import java.util.UUID;
import org.marketcetera.util.misc.ClassVersion;

/**
 * A node ID. Each communication endpoint (client or server class
 * instance) has an unique node ID. New IDs should be obtained using
 * {@link #generate()}.
 * 
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class NodeId
    extends Tag
{

    // CLASS DATA.

    private static final long serialVersionUID=1L;


    // CONSTRUCTORS.

    /**
     * Creates a new node ID with the given ID value.
     *
     * @param value The ID value.
     */

    private NodeId
        (String value)
    {
        super(value);
    }

    /**
     * Creates a new node ID. This empty constructor is intended for
     * use by JAXB.
     */

    protected NodeId() {}


    // CLASS METHODS.

    /**
     * Returns a new, unique node ID.
     *
     * @return The ID.
     */

    public static NodeId generate()
    {
        return new NodeId(UUID.randomUUID().toString());
    }
}
