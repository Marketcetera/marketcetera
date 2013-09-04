package org.marketcetera.client;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class ClientParametersSpec
{
    /**
     * Create a new ClientParametersSpec instance.
     *
     * @param inUsername a <code>String</code> value
     * @param inHostname a <code>String</code> value
     * @param inPort an <code>int</code> value
     * @param inURL a <code>String</code> value
     */
    public ClientParametersSpec(String inUsername,
                                String inHostname,
                                int inPort,
                                String inURL)
    {
        username = StringUtils.trimToNull(inUsername);
        hostname = StringUtils.trimToNull(inHostname);
        port = inPort;
        url = StringUtils.trimToNull(inURL);
    }
    /**
     * Get the username value.
     *
     * @return a <code>String</code> value
     */
    public String getUsername()
    {
        return username;
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
     * Get the port value.
     *
     * @return an <code>int</code> value
     */
    public int getPort()
    {
        return port;
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
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return new HashCodeBuilder().append(hostname).append(port).append(url).append(username).toHashCode();
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
        ClientParametersSpec other = (ClientParametersSpec) obj;
        if (hostname == null) {
            if (other.hostname != null)
                return false;
        } else if (!hostname.equals(other.hostname))
            return false;
        if (port != other.port)
            return false;
        if (url == null) {
            if (other.url != null)
                return false;
        } else if (!url.equals(other.url))
            return false;
        if (username == null) {
            if (other.username != null)
                return false;
        } else if (!username.equals(other.username))
            return false;
        return true;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("ClientParametersSpec [username=").append(username).append(", hostname=").append(hostname)
                .append(", port=").append(port).append(", url=").append(url).append("]");
        return builder.toString();
    }
    /**
     * 
     */
    private final String username;
    /**
     * 
     */
    private final String hostname;
    /**
     * 
     */
    private final int port;
    /**
     * 
     */
    private final String url;
}
