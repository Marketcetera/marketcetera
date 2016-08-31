package org.marketcetera.util.ws.stateful;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/* $License$ */

/**
 * Describes the usage of a port.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class PortDescriptor
        implements Comparable<PortDescriptor>
{
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
     * Get the description value.
     *
     * @return a <code>String</code> value
     */
    public String getDescription()
    {
        return description;
    }
    /**
     * Create a new PortDescriptor instance.
     *
     * @param inPort an <code>int</code> value
     * @param inDescription a <code>String</code> value
     */
    public PortDescriptor(int inPort,
                          String inDescription)
    {
        port = inPort;
        description = inDescription;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("PortDescriptor [port=").append(port).append(", description=").append(description).append("]");
        return builder.toString();
    }
    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(PortDescriptor inO)
    {
        return new CompareToBuilder().append(port,inO.port).toComparison();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return new HashCodeBuilder().append(port).toHashCode();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof PortDescriptor)) {
            return false;
        }
        PortDescriptor other = (PortDescriptor) obj;
        return new EqualsBuilder().append(port,other.port).isEquals();
    }
    /**
     * port value
     */
    private final int port;
    /**
     * description value
     */
    private final String description;
}
