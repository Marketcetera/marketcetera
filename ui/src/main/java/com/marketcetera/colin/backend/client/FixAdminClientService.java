package com.marketcetera.colin.backend.client;

import org.marketcetera.fix.FixAdminClient;
import org.marketcetera.fix.FixAdminClientFactory;
import org.marketcetera.fix.FixAdminRpcClientParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Service
public class FixAdminClientService
        extends AbstractClientService<FixAdminClient,FixAdminRpcClientParameters>
{
    /* (non-Javadoc)
     * @see com.marketcetera.colin.backend.client.AbstractClientService#createClient(org.marketcetera.core.BaseClientParameters)
     */
    @Override
    protected FixAdminClient createClient(FixAdminRpcClientParameters inParams)
    {
        return fixAdminClientFactory.create(inParams);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.colin.backend.client.AbstractClientService#getParameters()
     */
    @Override
    protected FixAdminRpcClientParameters getParameters()
    {
        return new FixAdminRpcClientParameters();
    }
    /* (non-Javadoc)
     * @see com.marketcetera.colin.backend.client.AbstractClientService#getClient()
     */
    @Override
    public FixAdminClient getClient()
            throws Exception
    {
        return super.getClient(FixAdminClient.class);
    }
    /**
     * creates new {@link FixAdminClient} objects
     */
    @Autowired
    private FixAdminClientFactory<FixAdminRpcClientParameters> fixAdminClientFactory;
}
