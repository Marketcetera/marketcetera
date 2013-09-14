package org.marketcetera.client;

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
public interface ClientLifecycleManager
{
    /**
     * 
     *
     *
     * @param inClient
     */
    public void release(Client inClient);
}
