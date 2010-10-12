package org.marketcetera.util.ws.stateless;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.tags.NodeId;

/**
 * A communication endpoint.
 * 
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class Node
{

    // CLASS DATA.

    /**
     * The default port on which the server listens and to which the
     * client connects.
     */

    public static final int DEFAULT_PORT=
        9000;

    /**
     * The default host name for the server and to which the client
     * connects.
     */

    public static final String DEFAULT_HOST=
        "localhost"; //$NON-NLS-1$


    // INSTANCE DATA.

    private String mHost;
    private int mPort;
    private final NodeId mId=NodeId.generate();


    // CONSTRUCTORS.

    /**
     * Creates a new communication node with the given host name and
     * port.
     *
     * @param host The host name.
     * @param port The port.
     */    

    protected Node
        (String host,
         int port)
    {
        mHost=host;
        mPort=port;
    }

    /**
     * Creates a new communication node with the default host name and
     * port.
     */    

    protected Node()
    {
        this(DEFAULT_HOST,DEFAULT_PORT);
    }


    // INSTANCE METHODS.

    /**
     * Sets the receiver's host name to the given one.
     *
     * @param host The host name.
     */

    public void setHost
        (String host)
    {
        mHost=host;
    }
 
    /**
     * Returns the receiver's host name.
     *
     * @return The host name.
     */

    public String getHost()
    {
        return mHost;
    }

    /**
     * Returns the receiver's port.
     *
     * @return The port.
     */

    public int getPort()
    {
        return mPort;
    }

    /**
     * Returns the receiver's node ID.
     *
     * @return The node ID.
     */

    public NodeId getId()
    {
        return mId;
    }

    /**
     * Returns the connection URL for the given service interface.
     *
     * @param iface The interface.
     *
     * @return The URL.
     */

    protected String getConnectionUrl
        (Class<?> iface)
    {
        StringBuilder builder=new StringBuilder();
        builder.append("http://"); //$NON-NLS-1$
        builder.append(getHost());
        builder.append(':'); //$NON-NLS-1$
        builder.append(getPort());
        builder.append('/'); //$NON-NLS-1$
        builder.append(iface.getName().
                       replace('.','_'). //$NON-NLS-1$ //$NON-NLS-2$
                       replace('$','_')); //$NON-NLS-1$ //$NON-NLS-2$
        return builder.toString();
    }
}
