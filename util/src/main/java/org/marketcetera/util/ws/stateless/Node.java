package org.marketcetera.util.ws.stateless;

import java.lang.annotation.Annotation;
import java.util.Collection;

import javax.jws.WebService;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.stateful.PortDescriptor;
import org.marketcetera.util.ws.stateful.UsesPort;
import org.marketcetera.util.ws.tags.NodeId;

import com.google.common.collect.Lists;

/* $License$ */

/**
 * A communication endpoint.
 * 
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */
@ClassVersion("$Id$")
public class Node
        implements UsesPort
{
    /**
     * Sets the receiver's host name to the given one.
     *
     * @param inHost a <code>String</code> value
     */
    public void setHost(String inHost)
    {
        host = inHost;
    }
    /**
     * Returns the receiver's host name.
     *
     * @return a <code>String</code> value
     */
    public String getHost()
    {
        return host;
    }
    /**
     * Returns the receiver's port.
     *
     * @return an <code>int</code> value
     */
    public int getPort()
    {
        return port;
    }
    /**
     * Sets the port value.
     *
     * @param inPort an <code>int</code> value
     */
    public void setPort(int inPort)
    {
        port = inPort;
    }
    /**
     * Returns the receiver's node ID.
     *
     * @return a <code>NodeId</code> value
     */
    public NodeId getId()
    {
        return nodeId;
    }
    /**
     * Get the nodeDescription value.
     *
     * @return a <code>String</code> value
     */
    public String getNodeDescription()
    {
        return nodeDescription;
    }
    /**
     * Sets the nodeDescription value.
     *
     * @param inNodeDescription a <code>String</code> value
     */
    public void setNodeDescription(String inNodeDescription)
    {
        nodeDescription = inNodeDescription;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.util.ws.stateful.UsesPort#getPortDescriptors()
     */
    @Override
    public Collection<PortDescriptor> getPortDescriptors()
    {
        return Lists.newArrayList(new PortDescriptor(port,
                                                     getNodeDescription()));
    }
    /**
     * Creates a new communication node with the given host name and port.
     *
     * @param inHost a <code>String</code> value
     * @param inPort an <code>int</code> value
     */
    protected Node(String inHost,
                   int inPort)
    {
        host = inHost;
        port = inPort;
        nodeDescription = nodeId.getValue();
    }
    /**
     * Creates a new communication node with the default host name and port.
     */
    protected Node()
    {
        this(DEFAULT_SERVER_HOST,
             DEFAULT_PORT);
    }
    /**
     * Returns the connection URL for the given service interface.
     *
     * @param inInterfaceClazz a <code>Class&lt;?&gt;</code> value
     * @return a <code>String</code> containing the connection namespace
     */
    protected String getConnectionUrl(Class<?> inInterfaceClazz)
    {
        String targetNamespace = null;
        for(Annotation annotation : inInterfaceClazz.getDeclaredAnnotations()) {
            if(annotation instanceof WebService) {
                WebService webServiceAnnotation = (WebService)annotation;
                targetNamespace = webServiceAnnotation.targetNamespace();
            }
        }
        StringBuilder builder = new StringBuilder();
        builder.append("http://"); //$NON-NLS-1$
        builder.append(getHost());
        builder.append(':'); //$NON-NLS-1$
        builder.append(getPort());
        builder.append('/'); //$NON-NLS-1$
        if(targetNamespace == null) {
            builder.append(inInterfaceClazz.getName().
                           replace('.','_'). //$NON-NLS-1$ //$NON-NLS-2$
                           replace('$','_')); //$NON-NLS-1$ //$NON-NLS-2$
        } else {
            builder.append(targetNamespace);
        }
        return builder.toString();
    }
    /**
     * node host value
     */
    private String host;
    /**
     * node port value
     */
    private int port;
    /**
     * node id for this node
     */
    private final NodeId nodeId = NodeId.generate();
    /**
     * node description value
     */
    private String nodeDescription;
    /**
     * The default port on which the server listens and to which the client connects.
     */
    public static final int DEFAULT_PORT = 9000;
    /**
     * The default host name for the server
     */
    public static final String DEFAULT_SERVER_HOST = "0.0.0.0"; //$NON-NLS-1$
    /**
     * The default host name for the client
     */
    public static final String DEFAULT_CLIENT_HOST = "127.0.0.1"; //$NON-NLS-1$
}
