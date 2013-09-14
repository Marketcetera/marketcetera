package org.marketcetera.client;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Constructs JMS-based <code>Client</code> objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class JmsClientFactory
        implements ClientFactory
{
    /* (non-Javadoc)
     * @see org.marketcetera.client.ClientFactory#getClient(org.marketcetera.client.ClientParameters, org.marketcetera.client.ClientLifecycleManager)
     */
    @Override
    public Client getClient(ClientParameters inClientParameters,
                            ClientLifecycleManager inClientLifecycleManager)
            throws ClientInitException, ConnectionException
    {
        return new ClientImpl(inClientParameters,
                              inClientLifecycleManager);
    }
}
