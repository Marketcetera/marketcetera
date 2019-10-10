package com.marketcetera.colin.app.security;

import java.util.Set;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.marketcetera.admin.AdminClient;
import org.marketcetera.admin.AdminClientFactory;
import org.marketcetera.admin.AdminRpcClientParameters;
import org.marketcetera.admin.Permission;
import org.marketcetera.admin.impl.SimplePermission;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import com.marketcetera.colin.backend.data.Role;

/* $License$ */

/**
 * Uses the {@link AdminClient} to authenticate web ui logins.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class AdminRpcAuthenticationProvider
        implements AuthenticationProvider
{
    /* (non-Javadoc)
     * @see org.springframework.security.authentication.AuthenticationProvider#authenticate(org.springframework.security.core.Authentication)
     */
    @Override
    public Authentication authenticate(Authentication inAuthentication)
            throws AuthenticationException
    {
        AdminRpcClientParameters params = new AdminRpcClientParameters();
        params.setHostname(rpcServerHostname);
        params.setPassword(String.valueOf(inAuthentication.getCredentials()));
        params.setPort(rpcPort);
        params.setUsername(inAuthentication.getName());
        AdminClient adminClient = adminClientFactory.create(params);
        try {
            adminClient.start();
        } catch (Exception e) {
            SLF4JLoggerProxy.warn(this,
                                  e,
                                  "Unable to log {} in",
                                  inAuthentication.getPrincipal());
            throw new BadCredentialsException(ExceptionUtils.getRootCauseMessage(e));
        }
        Validate.isTrue(adminClient.isRunning());
        org.marketcetera.admin.User metcUser = adminClient.getCurrentUser();
        Set<Permission> permissions = adminClient.getPermissionsForCurrentUser();
        // TODO temp for this prototype: begin
        permissions.add(new SimplePermission(Role.ADMIN,
                                             Role.ADMIN));
        permissions.add(new SimplePermission(Role.BAKER,
                                             Role.BAKER));
        permissions.add(new SimplePermission(Role.BARISTA,
                                             Role.BARISTA));
        // TODO temp for this prototype: end
        inAuthentication = new MetcAuthenticationToken(new UIUser(metcUser,
                                                                  permissions),
                                                       inAuthentication.getCredentials(),
                                                       permissions);
        SLF4JLoggerProxy.info(this,
                              "{} logged in with the following permissions: {}",
                              metcUser.getName(),
                              permissions);
        try {
            // discard this admin client, which we used just to log in
            adminClient.stop();
        } catch (Exception ignored) {}
        return inAuthentication;
    }
    /* (non-Javadoc)
     * @see org.springframework.security.authentication.AuthenticationProvider#supports(java.lang.Class)
     */
    @Override
    public boolean supports(Class<?> inAuthentication)
    {
        return true;
    }
    /**
     * creates {@link AdminClient} values
     */
    @Autowired
    private AdminClientFactory<AdminRpcClientParameters> adminClientFactory;
    /**
     * server hostname
     */
    @Value("${metc.rpc.hostname:localhost}")
    private String rpcServerHostname;
    /**
     * RPC services port
     */
    @Value("${metc.rpc.port:9010}")
    private int rpcPort;
}
