package com.marketcetera.colin.backend.client;

import java.util.Map;

import org.marketcetera.core.BaseClient;
import org.marketcetera.core.BaseClientParameters;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.google.common.collect.Maps;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class AbstractClientService<ClientClazz extends BaseClient,ParamClazz extends BaseClientParameters>
{
    /**
     * 
     *
     *
     * @return
     * @throws Exception
     */
    public abstract ClientClazz getClient()
            throws Exception;
    /**
     * 
     *
     *
     * @param inType
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    protected ClientClazz getClient(Class<ClientClazz> inType)
            throws Exception
    {
        SecurityContext context = SecurityContextHolder.getContext();
        if(context.getAuthentication() == null) {
            throw new IllegalArgumentException("Not logged in");
        }
        String username = String.valueOf(context.getAuthentication().getPrincipal());
        SLF4JLoggerProxy.debug(this,
                               "{} requesting {} client",
                               username,
                               inType.getSimpleName());
        Map<Class<? extends BaseClient>,BaseClient> clientsForThisUser = clients.get(username);
        if(clientsForThisUser == null) {
            clientsForThisUser = Maps.newHashMap();
            clients.put(username,
                        clientsForThisUser);
        }
        BaseClient client = clientsForThisUser.get(inType);
        if(client == null) {
            SLF4JLoggerProxy.debug(this,
                                   "{} has no {} client yet",
                                   username,
                                   inType.getSimpleName());
            ParamClazz params = getParameters();
            params.setHostname(rpcServerHostname);
            params.setPassword(String.valueOf(context.getAuthentication().getCredentials()));
            params.setPort(rpcPort);
            params.setUsername(String.valueOf(context.getAuthentication().getPrincipal()));
            client = createClient(params);
            client.start();
            SLF4JLoggerProxy.debug(this,
                                   "{} new {} client successfully connected at {}:{}",
                                   username,
                                   inType.getSimpleName(),
                                   rpcServerHostname,
                                   rpcPort);
            clientsForThisUser.put(inType,
                                   client);
        }
        if(!client.isRunning()) {
            SLF4JLoggerProxy.debug(this,
                                   "{} {} client not running, trying to restart",
                                   username,
                                   inType.getSimpleName());
            try {
                client.stop();
                client.start();
            } catch (Exception e) {
                SLF4JLoggerProxy.warn(this,
                                      e);
            }
        }
        if(!client.isRunning()) {
            SLF4JLoggerProxy.debug(this,
                                   "{} {} client not active, discarding",
                                   username,
                                   inType.getSimpleName());
            clientsForThisUser.remove(inType);
            throw new IllegalArgumentException("Cannot connect client");
        }
        SLF4JLoggerProxy.debug(this,
                               "{} {} client active and ready for use",
                               username,
                               inType.getSimpleName());
        // it's ok, we know this is a safe cast because of what we've done above to create/retrieve it
        return (ClientClazz)client;
    }
    /**
     * 
     *
     *
     * @param inParams
     * @return
     */
    protected abstract ClientClazz createClient(ParamClazz inParams);
    /**
     * 
     *
     *
     * @return
     */
    protected abstract ParamClazz getParameters();
    /**
     * 
     */
    private final Map<String,Map<Class<? extends BaseClient>,BaseClient>> clients = Maps.newHashMap();
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
