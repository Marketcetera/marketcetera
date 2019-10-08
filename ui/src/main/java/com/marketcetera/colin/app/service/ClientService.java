package com.marketcetera.colin.app.service;

import java.util.Map;
import java.util.Optional;

import org.marketcetera.core.BaseClient;
import org.marketcetera.core.Cachable;
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
public class ClientService
        implements Cachable
{
    public void setService(Class<? extends BaseClient> inServiceType,
                           BaseClient inService)
    {
        synchronized(servicesByType) {
            BaseClient existingService = servicesByType.get(inServiceType);
            if(existingService != null && existingService.isRunning()) {
                try {
                    existingService.stop();
                } catch (Exception ignored) {}
            }
            servicesByType.put(inServiceType,
                               inService);
        }
    }
    public <C extends BaseClient> Optional<C> getService(Class<C> inServiceType)
    {
        synchronized(servicesByType) {
            if(servicesByType.containsKey(inServiceType)) {
                @SuppressWarnings("unchecked") // this is a safe cast, so there
                C service = (C)servicesByType.get(inServiceType);
                if(service.isRunning()) {
                    return Optional.of(service);
                } else {
                    servicesByType.remove(inServiceType);
                }
            }
        }
        return Optional.empty();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.Cachable#clear()
     */
    @Override
    public void clear()
    {
        synchronized(servicesByType) {
            for(BaseClient client : servicesByType.values()) {
                try {
                    client.stop();
                } catch (Exception ignored) {}
            }
            servicesByType.clear();
        }
    }
    /**
     * holds client services
     */
    private final Map<Class<? extends BaseClient>,BaseClient> servicesByType = Maps.newHashMap();
}
