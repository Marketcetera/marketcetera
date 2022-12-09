package org.marketcetera.webui.security;

import java.util.Set;

import org.marketcetera.admin.AdminClient;
import org.marketcetera.admin.AdminClientFactory;
import org.marketcetera.admin.AdminRpcClientParameters;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.vaadin.flow.component.UI;

/* $License$ */

/**
 * Provides authn/authz via RPC.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Component
@EnableAutoConfiguration
public class RpcAuthenticationProvider
        implements AuthenticationProvider
{
    /* (non-Javadoc)
     * @see org.springframework.security.authentication.AuthenticationProvider#authenticate(org.springframework.security.core.Authentication)
     */
    @Override
    public Authentication authenticate(Authentication inAuthentication)
            throws AuthenticationException
    {
        String name = inAuthentication.getName();
        String password = inAuthentication.getCredentials().toString();
        AdminRpcClientParameters clientParameters = new AdminRpcClientParameters();
        clientParameters.setHostname(rpcHostname);
        clientParameters.setPassword(password);
        clientParameters.setPort(rpcPort);
        clientParameters.setUsername(name);
        try(AdminClient adminClient = adminClientFactory.create(clientParameters)) {
            adminClient.start();
            Set<? extends GrantedAuthority> permissions = adminClient.getPermissionsForCurrentUser();
            // TODO DON'T DO THIS!
            UI.getCurrent().getSession().setAttribute("username",
                                                      name);
            UI.getCurrent().getSession().setAttribute("password",
                                                      password);
            return new UsernamePasswordAuthenticationToken(name,
                                                           password,
                                                           permissions);
        } catch (Exception e) {
            SLF4JLoggerProxy.warn(this,
                                  e);
            throw new AuthenticationServiceException(e.getMessage());
        }
    }
    /* (non-Javadoc)
     * @see org.springframework.security.authentication.AuthenticationProvider#supports(java.lang.Class)
     */
    @Override
    public boolean supports(Class<?> inAuthentication)
    {
        return inAuthentication.equals(UsernamePasswordAuthenticationToken.class);
    }
    /**
     * provides a connection to the server for authn/authz
     */
    @Autowired
    private AdminClientFactory<AdminRpcClientParameters> adminClientFactory;
    /**
     * MATP RPC server port
     */
    @Value("${metc.rpc.port}")
    private int rpcPort;
    /**
     * server hostname
     */
    @Value("${metc.rpc.hostname}")
    private String rpcHostname;
}
