package org.marketcetera.web.service;

import java.util.Collection;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.Validate;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/* $License$ */

/**
 * Provides access to services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Service
public class ServiceManager
{
    /**
     * Validate and start the object.
     */
    @PostConstruct
    public void start()
    {
        SLF4JLoggerProxy.info(this,
                              "Starting service manager");
        for(ConnectableServiceFactory<?> factory : connectableServiceFactories) {
            connectableServiceFactoriesByServiceClass.put(factory.getServiceType(),
                                                          factory);
        }
        instance = this;
    }
    /**
     * 
     *
     *
     * @param inAuthentication
     * @return
     */
    public int connectServices(Authentication inAuthentication)
    {
        String username = inAuthentication.getName();
        String password = inAuthentication.getCredentials().toString();
        Cache<String,ConnectableService> serviceCache = servicesByUser.getUnchecked(username);
        int services = 0;
        for(ConnectableServiceFactory<?> serviceFactory : connectableServiceFactories) {
            try {
                SLF4JLoggerProxy.debug(this,
                                       "Creating {} service for {}",
                                       serviceFactory.getClass().getSimpleName(),
                                       username);
                // create a service for this user
                ConnectableService service = serviceFactory.create();
                // service is guaranteed to be non-null, but might or might not be running at this point
                if(!service.isRunning()) {
                    SLF4JLoggerProxy.debug(this,
                                           "{} service exists for {}, but is not running",
                                           service.getClass().getSimpleName(),
                                           username);
                    try {
                        service.disconnect();
                    } catch (Exception e) {
                        SLF4JLoggerProxy.warn(this,
                                              e);
                        // we'll skip over this error, it might not be bad enough to cause the connection to fail
                    }
                    SLF4JLoggerProxy.debug(this,
                                           "Connecting {} service for {}",
                                           service.getClass().getSimpleName(),
                                           username);
                    if(service.connect(username,
                                       password,
                                       rpcHostname,
                                       rpcPort)) {
                        SLF4JLoggerProxy.debug(this,
                                               "Created {} for {}",
                                               service,
                                               username);
                        // cache it for the next access
                        serviceCache.put(service.getClass().getSimpleName(),
                                         service);
                        services += 1;
                    }
                }
            } catch (Exception e) {
                SLF4JLoggerProxy.warn(this,
                                      e,
                                      "Unable to connect service for {}",
                                      serviceFactory.getClass().getSimpleName());
            }
        }
        return services;
    }
    /**
     * Get the service of the given type for the current user.
     *
     * @param <ServiceClazz>
     * @param inServiceClass a <code>Class&lt;ServiceClazz&gt;</code> value
     * @return a <code>ServiceClazz</code> value
     * @throws NoServiceException if the service cannot be retrieved
     */
    @SuppressWarnings("unchecked")
    public <ServiceClazz extends ConnectableService> ServiceClazz getService(Class<ServiceClazz> inServiceClass)
            throws NoServiceException
    {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Validate.notNull(securityContext,
                         "No Security Context");
        Validate.notNull(!(securityContext.getAuthentication() instanceof AnonymousAuthenticationToken),
                         "User Not Logged In");
        String username = securityContext.getAuthentication().getName();
        String serviceClassName = inServiceClass.getSimpleName();
        SLF4JLoggerProxy.debug(this,
                               "Retrieving {} for {}",
                               serviceClassName,
                               username);
        Cache<String,ConnectableService> serviceCache = servicesByUser.getUnchecked(username);
        ConnectableService service = serviceCache.getIfPresent(serviceClassName);
        if(service == null) {
            throw new NoServiceException("No service " + serviceClassName + " for " + username);
        }
        if(!service.isRunning()) {
            serviceCache.invalidate(serviceClassName);
            throw new NoServiceException("Service " + serviceClassName + " disconnected for " + username);
        }
        return (ServiceClazz)service;
    }
    /**
     * Get the instance.
     *
     * @return a <code>ServiceManager</code> value
     */
    public static ServiceManager getInstance()
    {
        return instance;
    }
    /**
     * caches services by owning user and then by service type
     */
    private final LoadingCache<String,Cache<String,ConnectableService>> servicesByUser = CacheBuilder.newBuilder().build(new CacheLoader<String,Cache<String,ConnectableService>>() {
        @Override
        public Cache<String,ConnectableService> load(String inKey)
                throws Exception
        {
            return CacheBuilder.newBuilder().build();
        }}
    );
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
    /**
     * holds connectable services by service class
     */
    private final Map<Class<?>,ConnectableServiceFactory<?>> connectableServiceFactoriesByServiceClass = Maps.newHashMap();
    /**
     * service factories
     */
    @Autowired(required=false)
    private Collection<ConnectableServiceFactory<?>> connectableServiceFactories = Lists.newArrayList();
    /**
     * static instance
     */
    private static ServiceManager instance;
}
