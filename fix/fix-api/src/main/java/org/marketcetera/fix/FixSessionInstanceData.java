package org.marketcetera.fix;

/* $License$ */

/**
 * Provides information about a FIX session on a particular cluster instance.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface FixSessionInstanceData
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
