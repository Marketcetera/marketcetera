package org.marketcetera.fix.impl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.marketcetera.fix.FixSessionInstanceData;

/* $License$ */

/**
 * Provides a simple <code>FixSessionInstanceData</code> implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@XmlRootElement(name="fixSessionInstanceData")
@XmlAccessorType(XmlAccessType.NONE)
public class SimpleFixSessionInstanceData
        implements FixSessionInstanceData
{
    /* (non-Javadoc)
     * @see com.marketcetera.admin.InstanceData#getHostname()
     */
    @Override
    public String getHostname()
    {
        return hostname;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.InstanceData#getPort()
     */
    @Override
    public int getPort()
    {
        return port;
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
     * Sets the port value.
     *
     * @param inPort an <code>int</code> value
     */
    public void setPort(int inPort)
    {
        port = inPort;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("InstanceData [hostname=").append(hostname).append(", port=").append(port).append("]");
        return builder.toString();
    }
    /**
     * hostname value
     */
    private String hostname;
    /**
     * port value
     */
    private int port;
}
