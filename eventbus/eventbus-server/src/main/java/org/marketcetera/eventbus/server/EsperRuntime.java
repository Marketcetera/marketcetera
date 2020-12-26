package org.marketcetera.eventbus.server;

import java.util.Collection;

import javax.annotation.PostConstruct;

import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import com.espertech.esper.common.client.configuration.Configuration;
import com.espertech.esper.runtime.client.EPRuntime;
import com.espertech.esper.runtime.client.EPRuntimeProvider;
import com.google.common.collect.Lists;

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
        eventTypes.forEach(eventType -> configuration.getCommon().addEventType(eventType));
        runtime = EPRuntimeProvider.getRuntime(runtimeName,
                                               configuration);
        runtime.initialize();
        SLF4JLoggerProxy.info(this,
                              "Created Esper runtime {}",
                              runtimeName);
    }
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
