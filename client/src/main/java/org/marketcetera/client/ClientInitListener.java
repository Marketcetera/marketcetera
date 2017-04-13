package org.marketcetera.client;

/* $License$ */

/**
 * Listen for the initialization of the {@link Client}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface ClientInitListener
{
    /**
     * Receive the initialized client.
     *
     * @param inClient a <code>Client</code> value
     */
    void receiveClient(Client inClient);
}
