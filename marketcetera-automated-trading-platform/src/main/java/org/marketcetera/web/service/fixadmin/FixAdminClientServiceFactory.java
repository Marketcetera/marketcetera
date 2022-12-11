package org.marketcetera.web.service.fixadmin;

import org.marketcetera.fix.FixAdminRpcClientFactory;
import org.marketcetera.web.service.ConnectableServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/* $License$ */

/**
 * Creates {@link FixAdminClientService} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Service
public class FixAdminClientServiceFactory
        implements ConnectableServiceFactory<FixAdminClientService>
{
    /* (non-Javadoc)
     * @see org.marketcetera.web.services.ConnectableServiceFactory#create()
     */
    @Override
    public FixAdminClientService create()
    {
        FixAdminClientService fixClientService = new FixAdminClientService();
        fixClientService.setFixAdminClientFactory(fixAdminClientFactory);
        return fixClientService;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.service.ConnectableServiceFactory#getServiceType()
     */
    @Override
    public Class<FixAdminClientService> getServiceType()
    {
        return FixAdminClientService.class;
    }
    /**
     * creates a FIX admin client to connect to the fix admin server
     */
    @Autowired
    private FixAdminRpcClientFactory fixAdminClientFactory;
}
