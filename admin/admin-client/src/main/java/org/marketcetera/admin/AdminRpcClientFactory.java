package org.marketcetera.admin;

import java.util.Locale;

import org.marketcetera.admin.AdminClient;
import org.marketcetera.admin.AdminClientFactory;
import org.marketcetera.admin.Permission;
import org.marketcetera.admin.PermissionFactory;
import org.marketcetera.admin.Role;
import org.marketcetera.admin.RoleFactory;
import org.marketcetera.admin.User;
import org.marketcetera.admin.UserAttributeFactory;
import org.marketcetera.admin.UserFactory;
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
        implements AdminClientFactory
{
    /* (non-Javadoc)
     * @see com.marketcetera.admin.AdminRpcClientFactory#create(java.lang.String, java.lang.String, java.lang.String, int)
     */
    @Override
    public AdminClient create(String inUsername,
                              String inPassword,
                              String inHostname,
                              int inPort)
    {
        return create(inUsername,
                      inPassword,
                      inHostname,
                      inPort,
                      Locale.getDefault());
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.AdminRpcClientFactory#create(java.lang.String, java.lang.String, java.lang.String, int, java.util.Locale)
     */
    @Override
    public AdminClient create(String inUsername,
                              String inPassword,
                              String inHostname,
                              int inPort,
                              Locale inLocale)
    {
        AdminRpcClient client = new AdminRpcClient();
        client.setHostname(inHostname);
        client.setPassword(inPassword);
        client.setPermissionFactory(permissionFactory);
        client.setPort(inPort);
        client.setRoleFactory(roleFactory);
        client.setUserFactory(userFactory);
        client.setUsername(inUsername);
        client.setUserAttributeFactory(userAttributeFactory);
        client.setLocale(inLocale);
        return client;
    }
    /**
     * creates {@link UserAttributeFactory} objects
     */
    @Autowired
    private UserAttributeFactory userAttributeFactory;
    /**
     * creates {@link Role} objects
     */
    @Autowired
    private RoleFactory roleFactory;
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
}
