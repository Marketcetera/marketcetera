package org.marketcetera.web.service;

import java.util.Collection;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.web.SessionUser;
import org.marketcetera.web.view.LoginView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.vaadin.ui.UI;

/* $License$ */

/**
 *
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
     * Get the service of the given type for the current user.
     *
     * @param inServiceClass a <code>Class&lt;ServiceClazz&gt;</code> value
     * @return a <code>ServiceClazz</code> value
     * @throws NoServiceException if the service cannot be retrieved
     */
    @SuppressWarnings("unchecked")
    public <ServiceClazz extends ConnectableService> ServiceClazz getService(Class<ServiceClazz> inServiceClass)
            throws NoServiceException
    {
        // retrieve the current user from the session - this should exist even if the user hasn't been authenticated yet
        SessionUser sessionUser = SessionUser.getCurrentUser();
        // we don't expect this, but we should handle it, just in case
        if(sessionUser == null) {
            // TODO
//            menuLayout.setVisible(false);
            UI.getCurrent().getNavigator().navigateTo(LoginView.NAME);
            // do I need to return something here? or thrown something?
        }
        Cache<String,ConnectableService> serviceCache = servicesByUser.getUnchecked(sessionUser);
        ConnectableService service = serviceCache.getIfPresent(inServiceClass.getSimpleName());
        if(service == null) {
            // no current service for this user, this is a normal condition if the service hasn't been accessed yet
            ConnectableServiceFactory<?> serviceFactory = connectableServiceFactoriesByServiceClass.get(inServiceClass);
            // this is not a normal condition, and it seems unlikely that this should arise as there shouldn't be a module than can ask for a service that doesn't also provide a factory
            if(serviceFactory == null) {
                throw new NoServiceException("No connectable service factory for " + inServiceClass.getSimpleName());
            }
            SLF4JLoggerProxy.debug(this,
                                   "Creating {} service for {}",
                                   inServiceClass.getSimpleName(),
                                   sessionUser);
            // create a service for this user
            service = serviceFactory.create();
        }
        // service is guaranteed to be non-null, but might or might not be running at this point
        if(!service.isRunning()) {
            SLF4JLoggerProxy.debug(this,
                                   "{} service exists for {}, but is not running",
                                   inServiceClass.getSimpleName(),
                                   sessionUser);
            try {
                service.disconnect();
            } catch (Exception e) {
                SLF4JLoggerProxy.warn(this,
                                      e);
                // we'll skip over this error, it might not be bad enough to cause the connection to fail
            }
            try {
                SLF4JLoggerProxy.debug(this,
                                       "Connecting {} service for {}",
                                       inServiceClass.getSimpleName(),
                                       sessionUser);
                if(service.connect(sessionUser.getUsername(),
                                   sessionUser.getPassword(),
                                   hostname,
                                   port)) {
                    SLF4JLoggerProxy.debug(this,
                                           "Created {} for {}",
                                           service,
                                           sessionUser);
                    // cache it for the next access
                    serviceCache.put(inServiceClass.getSimpleName(),
                                     service);
                }
            } catch (Exception e) {
                SLF4JLoggerProxy.warn(this,
                                      e,
                                      "Failed to connect {} service for {}",
                                      inServiceClass.getSimpleName(),
                                      sessionUser);
                throw new NoServiceException("Failed to connect " + sessionUser + " to " + inServiceClass.getSimpleName(),
                                             e);
            }
        }
        if(!service.isRunning()) {
            SLF4JLoggerProxy.warn(this,
                                  "Failed to connect {} service for {}",
                                  inServiceClass.getSimpleName(),
                                  sessionUser);
            throw new NoServiceException("Failed to connect " + sessionUser + " to " + inServiceClass.getSimpleName());
        }
        // service is non-null and running
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
    private final LoadingCache<SessionUser,Cache<String,ConnectableService>> servicesByUser = CacheBuilder.newBuilder().build(new CacheLoader<SessionUser,Cache<String,ConnectableService>>() {
        @Override
        public Cache<String,ConnectableService> load(SessionUser inKey)
                throws Exception
        {
            return CacheBuilder.newBuilder().build();
        }}
    );
    /**
     * hostname to connect to
     */
    @Value("${host.name}")
    private String hostname;
    /**
     * port to connect to
     */
    @Value("${host.port}")
    private int port;
    /**
     * 
     */
    private final Map<Class<?>,ConnectableServiceFactory<?>> connectableServiceFactoriesByServiceClass = Maps.newHashMap();
    /**
     * service factories
     */
    @Autowired
    private Collection<ConnectableServiceFactory<?>> connectableServiceFactories = Lists.newArrayList();
    /**
     * static instance
     */
    private static ServiceManager instance;
}
