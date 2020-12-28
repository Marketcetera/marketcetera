package org.marketcetera.eventbus.server;

import java.util.Collection;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.marketcetera.eventbus.EsperEvent;
import org.marketcetera.eventbus.HasEsperEvent;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;

import com.espertech.esper.common.client.configuration.Configuration;
import com.espertech.esper.runtime.client.EPRuntime;
import com.espertech.esper.runtime.client.EPRuntimeProvider;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@EnableAutoConfiguration
public class EsperRuntime
{
    /**
     * Validate and start the object.
     */
    @PostConstruct
    public void start()
    {
        configuration = new Configuration();
        Collection<Class<?>> eventTypeCollection = Sets.newHashSet(eventTypes);
        Map<String,EsperEvent> esperEventTypes = applicationContext.getBeansOfType(EsperEvent.class);
        esperEventTypes.values().forEach(esperEvent -> eventTypeCollection.add(esperEvent.getClass()));
        Map<String,HasEsperEvent> hasEsperEventTypes = applicationContext.getBeansOfType(HasEsperEvent.class);
        hasEsperEventTypes.values().forEach(hasEsperEvent -> eventTypeCollection.add(hasEsperEvent.getEsperEvent().getClass()));
        eventTypeCollection.forEach(eventType -> configuration.getCommon().addEventType(eventType));
        SLF4JLoggerProxy.debug(this,
                               "Adding the following event type(s): {}",
                               eventTypeCollection);
        runtime = EPRuntimeProvider.getRuntime(runtimeName,
                                               configuration);
        runtime.initialize();
        SLF4JLoggerProxy.info(this,
                              "Created Esper runtime {}",
                              runtimeName);
    }
    @Autowired
    private ApplicationContext applicationContext;
    /**
     * Get the eventTypes value.
     *
     * @return a <code>Collection&lt;Class&lt;?&gt;&gt;</code> value
     */
    public Collection<Class<?>> getEventTypes()
    {
        return eventTypes;
    }
    /**
     * Sets the eventTypes value.
     *
     * @param inEventTypes a <code>Collection&lt;Class&lt;?&gt;&gt;</code> value
     */
    public void setEventTypes(Collection<Class<?>> inEventTypes)
    {
        eventTypes = inEventTypes;
    }
    /**
     * Get the runtime value.
     *
     * @return an <code>EPRuntime</code> value
     */
    public EPRuntime getRuntime()
    {
        return runtime;
    }
    /**
     * Sets the runtime value.
     *
     * @param inRuntime an <code>EPRuntime</code> value
     */
    public void setRuntime(EPRuntime inRuntime)
    {
        runtime = inRuntime;
    }
    private Configuration configuration;
    /**
     * event types value
     */
    private Collection<Class<?>> eventTypes = Lists.newArrayList();
    private EPRuntime runtime;
    @Value("${metc.esper.runtime.name:MATP}")
    private String runtimeName;
}
