package org.marketcetera.admin;

import org.marketcetera.rpc.client.RpcClientFactory;
import org.springframework.beans.factory.annotation.Autowired;

/* $License$ */

/**
 * Create {@link AdminClient} implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class AdminRpcClientFactory
        implements RpcClientFactory<AdminRpcClientParameters,AdminRpcClient>
{
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.client.RpcClientFactory#create(org.marketcetera.rpc.client.RpcClientParameters)
     */
    @Override
    public AdminRpcClient create(AdminRpcClientParameters inParameters)
    {
        AdminRpcClient adminClient = new AdminRpcClient(inParameters);
        adminClient.setPermissionFactory(permissionFactory);
        adminClient.setRoleFactory(roleFactory);
        adminClient.setUserAttributeFactory(userAttributeFactory);
        adminClient.setUserFactory(userFactory);
        return adminClient;
    }
    /**
     * creates {@link UserAttributeFactory} objects
     */
    @Autowired
    private UserAttributeFactory userAttributeFactory;
    /**
     * creates {@link Permission} objects
     */
    @Autowired
    private PermissionFactory permissionFactory;
    /**
     * creates {@link User} objects
     */
    @Autowired
    private UserFactory userFactory;
    /**
     * creates {@link Role} objects
     */
    @Autowired
    private RoleFactory roleFactory;
}
