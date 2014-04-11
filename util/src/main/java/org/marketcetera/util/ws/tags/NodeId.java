package org.marketcetera.util.ws.tags;

import java.util.UUID;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * A node ID. Each communication endpoint (client or server class
 * instance) has an unique node ID. New IDs should be obtained using
 * {@link #generate()}.
 * 
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */
@ClassVersion("$Id$")
public class NodeId
        extends Tag
{
    /**
     * Creates a new node ID with the given ID value.
     *
     * @param inValue a <code>String</code> value
     */
    public NodeId(String inValue)
    {
        super(inValue);
    }
    /**
     * Returns a new, unique node ID.
     *
     * @return a <code>NodeId</code> value
     */
    public static NodeId generate()
    {
        return new NodeId(UUID.randomUUID().toString());
    }
    /**
     * Create a new NodeId instance.
     */
    protected NodeId() {}
    private static final long serialVersionUID=1L;
}
