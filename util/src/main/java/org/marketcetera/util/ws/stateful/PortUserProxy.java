package org.marketcetera.util.ws.stateful;

import java.util.Collection;

import com.google.common.collect.Lists;

/* $License$ */

/**
 * Allows a port that is implicitly used by the system to be identified.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class PortUserProxy
        implements UsesPort
{
    /* (non-Javadoc)
     * @see org.marketcetera.util.ws.stateful.UsesPort#getPortDescriptors()
     */
    @Override
    public Collection<PortDescriptor> getPortDescriptors()
    {
        return Lists.newArrayList(new PortDescriptor(port,
                                                     description));
    }
    /**
     * Get the port value.
     *
     * @return a <code>int</code> value
     */
    public int getPort()
    {
        return port;
    }
    /**
     * Sets the port value.
     *
     * @param inPort a <code>int</code> value
     */
    public void setPort(int inPort)
    {
        port = inPort;
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
     * Sets the description value.
     *
     * @param inDescription a <code>String</code> value
     */
    public void setDescription(String inDescription)
    {
        description = inDescription;
    }
    /**
     * port value
     */
    private int port;
    /**
     * description value
     */
    private String description;
}
