package com.marketcetera.colin.backend.client;

import java.util.Collection;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.marketcetera.core.BaseClient;
import org.marketcetera.core.BaseClientParameters;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;
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
public class ClientServiceManager
{
    @SuppressWarnings("unchecked") // these are safe casts given how we put the client services in the collection
    public <ClientClazz extends BaseClient,ParamClazz extends BaseClientParameters,ClientServiceClazz extends AbstractClientService<ClientClazz,ParamClazz>> ClientServiceClazz getClient(Class<ClientServiceClazz> inClientType)
    {
        if(registeredClientServicesByType.containsKey(inClientType)) {
            AbstractClientService<ClientClazz,ParamClazz> clientService = (AbstractClientService<ClientClazz,ParamClazz>)registeredClientServicesByType.get(inClientType);
            return (ClientServiceClazz)clientService;
        }
        throw new UnsupportedOperationException("No registered client type for " + inClientType.getSimpleName());
    }
    public void logout()
    {
        for(AbstractClientService<?,?> clientService : registeredClientServices) {
            try {
                BaseClient client = clientService.getClient(false);
                if(client != null) {
                    client.stop();
                }
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        }
    }
    @PostConstruct
    public void start()
    {
        for(AbstractClientService<?,?> clientService : registeredClientServices) {
            SLF4JLoggerProxy.info(this,
                                  "Registering {}",
                                  clientService.getClientType().getSimpleName());
            registeredClientServicesByType.put(clientService.getClientType(),
                                               clientService);
        }
    }
    private final Map<Class<?>,AbstractClientService<?,?>> registeredClientServicesByType = Maps.newHashMap();
    @Autowired
    private Collection<AbstractClientService<?,?>> registeredClientServices;
}
