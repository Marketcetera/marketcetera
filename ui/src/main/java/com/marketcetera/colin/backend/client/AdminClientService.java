package com.marketcetera.colin.backend.client;

import org.marketcetera.admin.AdminClient;
import org.marketcetera.admin.AdminClientFactory;
import org.marketcetera.admin.AdminRpcClientParameters;
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
public class AdminClientService
        extends AbstractClientService<AdminClient,AdminRpcClientParameters>
{
    /* (non-Javadoc)
     * @see com.marketcetera.colin.backend.client.AbstractClientService#createClient(org.marketcetera.core.BaseClientParameters)
     */
    @Override
    protected AdminClient createClient(AdminRpcClientParameters inParams)
    {
        return adminClientFactory.create(inParams);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.colin.backend.client.AbstractClientService#getParameters()
     */
    @Override
    protected AdminRpcClientParameters getParameters()
    {
        return new AdminRpcClientParameters();
    }
    /* (non-Javadoc)
     * @see com.marketcetera.colin.backend.client.AbstractClientService#getClient()
     */
    @Override
    public AdminClient getClient()
            throws Exception
    {
        return super.getClient(AdminClient.class);
    }
    /**
     * creates new {@link AdminClient} objects
     */
    @Autowired
    private AdminClientFactory<AdminRpcClientParameters> adminClientFactory;
}
