package org.marketcetera.fix;

import java.io.Serializable;

/* $License$ */

/**
 * Holds session attributes for an acceptor session with a given affinity.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class AcceptorSessionAttributes
        implements Serializable
{
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("AcceptorSessionAttributes [affinity=").append(affinity).append(", host=").append(host)
                .append(", port=").append(port).append("]");
        return builder.toString();
    }
    /**
     * Get the affinity value.
     *
     * @return an <code>int</code> value
     */
    public int getAffinity()
    {
        return affinity;
    }
    /**
     * Sets the affinity value.
     *
     * @param inAffinity an <code>int</code> value
     */
    public void setAffinity(int inAffinity)
    {
        affinity = inAffinity;
    }
    /**
     * Get the host value.
     *
     * @return a <code>String</code> value
     */
    public String getHost()
    {
        return host;
    }
    /**
     * Sets the host value.
     *
     * @param inHost a <code>String</code> value
     */
    public void setHost(String inHost)
    {
        host = inHost;
    }
    /**
     * Get the port value.
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
     * affinity value
     */
    private int affinity;
    /**
     * host value
     */
    private String host;
    /**
     * port value
     */
    private int port;
    private static final long serialVersionUID = -4681217168663583139L;
}
