package org.marketcetera.api.server;

import org.marketcetera.api.Config;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface ServerConfig
        extends Config
{
    /**
     * 
     *
     *
     * @return
     */
    public String getHostname();
    /**
     * 
     *
     *
     * @return
     */
    public int getPort();
}
