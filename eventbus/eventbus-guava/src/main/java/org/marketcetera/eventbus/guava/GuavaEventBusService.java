package org.marketcetera.eventbus.guava;

import javax.annotation.PostConstruct;

import org.marketcetera.core.PlatformServices;
import org.marketcetera.eventbus.EventBusService;
import org.marketcetera.eventbus.Messages;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.stereotype.Service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.eventbus.EventBus;

/* $License$ */

/**
 * Provides a Guava event bus implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Service
public class GuavaEventBusService
        implements EventBusService
{
    /**
     * Validate and start the object.
     */
    @PostConstruct
    public void start()
    {
        Messages.SERVICE_STARTING.info(this,
                                       PlatformServices.getServiceName(getClass()));
    }
    /* (non-Javadoc)
     * @see org.marketcetera.eventbus.EventbusService#subscribe(java.lang.Object)
     */
    @Override
    public void register(Object inSubscriber)
    {
        register(inSubscriber,
                 EventBusService.defaultTopic);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.eventbus.EventbusService#subscribe(java.lang.Object, java.lang.String)
     */
    @Override
    public void register(Object inSubscriber,
                         String inTopic)
    {
        if(inTopic == null) {
            return;
        }
        topics.getUnchecked(inTopic).register(inSubscriber);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.eventbus.EventbusService#unregister(java.lang.Object)
     */
    @Override
    public void unregister(Object inSubscriber)
    {
        unregister(inSubscriber,
                   EventBusService.defaultTopic);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.eventbus.EventbusService#unregister(java.lang.Object, java.lang.String)
     */
    @Override
    public void unregister(Object inSubscriber,
                           String inTopic)
    {
        if(inTopic == null) {
            return;
        }
        topics.getUnchecked(inTopic).unregister(inSubscriber);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.eventbus.EventbusService#post(java.lang.Object)
     */
    @Override
    public void post(Object inEvent)
    {
        post(inEvent,
             EventBusService.defaultTopic);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.eventbus.EventbusService#post(java.lang.Object, java.lang.String[])
     */
    @Override
    public void post(Object inEvent,
                     String... inTopics)
    {
        if(inTopics == null) {
            return;
        }
        for(String topic : inTopics) {
            SLF4JLoggerProxy.info(eventCategory,
                                  "{}:{}",
                                  topic,
                                  inEvent);
            topics.getUnchecked(topic).post(inEvent);
        }
    }
    /**
     * holds event buses by topic name
     */
    private final LoadingCache<String,EventBus> topics = CacheBuilder.newBuilder().build(new CacheLoader<String,EventBus>() {
        @Override
        public EventBus load(String inKey)
                throws Exception
        {
            return new EventBus(inKey);
        }}
    );
}
