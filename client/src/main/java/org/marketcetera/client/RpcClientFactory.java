package org.marketcetera.client;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class RpcClientFactory
        implements ClientFactory
{
    /* (non-Javadoc)
     * @see org.marketcetera.client.ClientFactory#getClient(org.marketcetera.client.ClientParameters)
     */
    @Override
    public Client getClient(ClientParameters inClientParameters)
            throws ClientInitException, ConnectionException
    {
        return new RpcClientImpl(inClientParameters);
    }
}
