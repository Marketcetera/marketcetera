package com.marketcetera.colin.backend.client;

import java.util.Map;

import org.marketcetera.fix.FixAdminClient;
import org.marketcetera.fix.FixAdminClientFactory;
import org.marketcetera.fix.FixAdminRpcClientParameters;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;

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
{
    public FixAdminClient getClient()
            throws Exception
    {
        SecurityContext context = SecurityContextHolder.getContext();
        if(context.getAuthentication() == null) {
            throw new IllegalArgumentException("Not logged in");
        }
        String username = String.valueOf(context.getAuthentication().getPrincipal());
        FixAdminClient client = clientsByUsername.get(username);
        if(client == null) {
            FixAdminRpcClientParameters params = new FixAdminRpcClientParameters();
            params.setHostname(rpcServerHostname);
            params.setPassword(String.valueOf(context.getAuthentication().getCredentials()));
            params.setPort(rpcPort);
            params.setUsername(String.valueOf(context.getAuthentication().getPrincipal()));
            client = fixAdminClientFactory.create(params);
            client.start();
            clientsByUsername.put(username,
                                  client);
        }
        if(!client.isRunning()) {
            try {
                client.stop();
                client.start();
            } catch (Exception e) {
                SLF4JLoggerProxy.warn(this,
                                      e);
            }
        }
        if(!client.isRunning()) {
            clientsByUsername.remove(username);
            throw new IllegalArgumentException("Cannot connect client");
        }
        return client;
    }
    private final Map<String,FixAdminClient> clientsByUsername = Maps.newHashMap();
    @Autowired
    private FixAdminClientFactory<FixAdminRpcClientParameters> fixAdminClientFactory;
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
