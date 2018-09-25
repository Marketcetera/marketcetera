package org.marketcetera.web.service.admin;

import org.marketcetera.admin.AdminRpcClientFactory;
import org.marketcetera.fix.FixAdminRpcClientFactory;
import org.marketcetera.web.service.ConnectableServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/* $License$ */

/**
 * Creates {@link AdminClientService} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Service
public class AdminClientServiceFactory
        implements ConnectableServiceFactory<AdminClientService>
{
    /* (non-Javadoc)
     * @see org.marketcetera.web.services.ConnectableServiceFactory#create()
     */
    @Override
    public AdminClientService create()
    {
        AdminClientService adminClientService = new AdminClientService();
        adminClientService.setAdminClientFactory(adminClientFactory);
        adminClientService.setFixAdminClientFactory(fixAdminClientFactory);
        return adminClientService;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.service.ConnectableServiceFactory#getServiceType()
     */
    @Override
    public Class<AdminClientService> getServiceType()
    {
        return AdminClientService.class;
    }
    /**
     * creates an admin client to connect to the admin server
     */
    @Autowired
    private AdminRpcClientFactory adminClientFactory;
    /**
     * creates a FIX admin client to connect to the fix admin server
     */
    @Autowired
    private FixAdminRpcClientFactory fixAdminClientFactory;
}
