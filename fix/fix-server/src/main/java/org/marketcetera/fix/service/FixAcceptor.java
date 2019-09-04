package org.marketcetera.fix.service;

import java.util.Collection;

import org.marketcetera.util.ws.stateful.PortDescriptor;
import org.marketcetera.util.ws.stateful.UsesPort;

import com.google.common.collect.Lists;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class FixAcceptor
        implements UsesPort
{
    /* (non-Javadoc)
     * @see org.marketcetera.util.ws.stateful.UsesPort#getPortDescriptors()
     */
    @Override
    public Collection<PortDescriptor> getPortDescriptors()
    {
        return ports;
    }
    /**
     * indicates ports in use
     */
    private final Collection<PortDescriptor> ports = Lists.newArrayList();
}
