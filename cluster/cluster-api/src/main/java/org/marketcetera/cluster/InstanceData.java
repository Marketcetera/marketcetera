package org.marketcetera.cluster;

/* $License$ */

/**
 * Provides information about a cluster instance.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface InstanceData
{
    /**
     * Get the hostname for this instance.
     *
     * @return a <code>String</code> value
     */
    String getHostname();
    /**
     * Get the port for this instance.
     *
     * @return an <code>int</code> value
     */
    int getPort();
}
