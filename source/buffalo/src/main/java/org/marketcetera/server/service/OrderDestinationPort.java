package org.marketcetera.server.service;

import org.marketcetera.util.misc.ClassVersion;
import org.springframework.context.Lifecycle;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface OrderDestinationPort
        extends Lifecycle
{
    /**
     * 
     *
     *
     */
    public void login();
    /**
     * 
     *
     *
     */
    public void logout();
    /**
     * 
     *
     *
     */
    public void send();
}
