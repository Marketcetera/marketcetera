package org.marketcetera.core;

/* $License$ */

/**
 * Registers for changes in the availability of a client.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface ClientStatusListener
{
    /**
     * Receive the client status upon change.
     *
     * @param isAvailable a <code>boolean</code> value
     */
    void receiveClientStatus(boolean isAvailable);
}
