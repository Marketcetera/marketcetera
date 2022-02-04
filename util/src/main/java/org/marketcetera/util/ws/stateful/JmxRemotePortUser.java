package org.marketcetera.util.ws.stateful;

import java.util.Collection;

import org.marketcetera.util.log.SLF4JLoggerProxy;

import com.google.common.collect.Lists;

/* $License$ */

/**
 * Indicates the port in use for JMX Remote, if present.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class JmxRemotePortUser
        implements UsesPort
{
    /* (non-Javadoc)
     * @see org.marketcetera.util.ws.stateful.UsesPort#getPortDescriptors()
     */
    @Override
    public Collection<PortDescriptor> getPortDescriptors()
    {
        Collection<PortDescriptor> ports = Lists.newArrayList();
        String portValue = System.getProperty("com.sun.management.jmxremote.port"); //$NON-NLS-1$
        if(portValue != null) {
            try {
                ports.add(new PortDescriptor(Integer.valueOf(portValue),
                                             Messages.JMX_REMOTE_SERVICE_DESCRIPTION.getText()));
            } catch (Exception e) {
                SLF4JLoggerProxy.warn(this,
                                      e);
            }
        }
        return ports;
    }
}
