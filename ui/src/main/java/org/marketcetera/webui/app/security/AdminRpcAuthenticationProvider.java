package org.marketcetera.webui.app.security;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.marketcetera.admin.AdminClient;
import org.marketcetera.admin.AdminClientFactory;
import org.marketcetera.admin.AdminRpcClientParameters;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/* $License$ */

/**
 *
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
        SLF4JLoggerProxy.warn(this,
                              "Coco, validating {}/{}",
                              inAuthentication.getName(),
                              inAuthentication.getCredentials());
        AdminClient adminClient = adminClientFactory.create(params);
        try {
            adminClient.start();
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadCredentialsException(ExceptionUtils.getRootCauseMessage(e));
        }
        if(adminClient.isRunning()) {
            inAuthentication.setAuthenticated(true);
        }
        for(String permission : adminClient.getPermissionsForCurrentUser()) {
//            inAuthentication.getAuthorities().add(new GrantedAuthority() {
//                @Override
//                public String getAuthority()
//                {
//                    return permission;
//                }}
//            );
        }
        return inAuthentication;
    }
    /* (non-Javadoc)
     * @see org.springframework.security.authentication.AuthenticationProvider#supports(java.lang.Class)
     */
    @Override
    public boolean supports(Class<?> inAuthentication)
    {
        SLF4JLoggerProxy.warn(this,
                              "Coco, does our authentication provider support {}",
                              inAuthentication.getClass().getCanonicalName());
        return true;
    }
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
