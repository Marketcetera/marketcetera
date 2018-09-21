package com.marketcetera.web.view.dataflows;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/* $License$ */

/**
 * Provides a decorated view of a Strategy Engine connection.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class DecoratedStrategyEngine
{
    /**
     * Get the name value.
     *
     * @return a <code>String</code> value
     */
    public String getName()
    {
        return name;
    }
    /**
     * Sets the name value.
     *
     * @param inName a <code>String</code> value
     */
    public void setName(String inName)
    {
        name = inName;
    }
    /**
     * Get the hostname value.
     *
     * @return a <code>String</code> value
     */
    public String getHostname()
    {
        return hostname;
    }
    /**
     * Sets the hostname value.
     *
     * @param inHostname a <code>String</code> value
     */
    public void setHostname(String inHostname)
    {
        hostname = inHostname;
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
     * Get the url value.
     *
     * @return a <code>String</code> value
     */
    public String getUrl()
    {
        return url;
    }
    /**
     * Sets the url value.
     *
     * @param inUrl a <code>String</code> value
     */
    public void setUrl(String inUrl)
    {
        url = inUrl;
    }
    /**
     * Get the isConnected value.
     *
     * @return a <code>boolean</code> value
     */
    public boolean isConnected()
    {
        return isConnected;
    }
    /**
     * Sets the isConnected value.
     *
     * @param inIsConnected a <code>boolean</code> value
     */
    public void setIsConnected(boolean inIsConnected)
    {
        isConnected = inIsConnected;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return new HashCodeBuilder().append(name).toHashCode();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DecoratedStrategyEngine other = (DecoratedStrategyEngine) obj;
        return new EqualsBuilder().append(name, other.name).isEquals();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("DecoratedStrategyEngine [name=").append(name).append("]");
        return builder.toString();
    }
    /**
     * SE name value
     */
    private String name;
    /**
     * SE web services/rpc hostname value
     */
    private String hostname;
    /**
     * JMS URL value
     */
    private String url;
    /**
     * SE web services/rpc port value
     */
    private int port;
    /**
     * indicates if the connection is active or not
     */
    private boolean isConnected;
}
