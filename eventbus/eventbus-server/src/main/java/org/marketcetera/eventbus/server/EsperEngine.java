package org.marketcetera.eventbus.server;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import org.marketcetera.eventbus.EsperEvent;
import org.marketcetera.eventbus.HasEsperEvent;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.espertech.esper.common.client.EPCompiled;
import com.espertech.esper.common.client.configuration.Configuration;
import com.espertech.esper.compiler.client.CompilerArguments;
import com.espertech.esper.compiler.client.EPCompileException;
import com.espertech.esper.compiler.client.EPCompiler;
import com.espertech.esper.compiler.client.EPCompilerProvider;
import com.espertech.esper.runtime.client.EPDeployException;
import com.espertech.esper.runtime.client.EPDeployment;
import com.espertech.esper.runtime.client.EPRuntime;
import com.espertech.esper.runtime.client.EPRuntimeDestroyedException;
import com.espertech.esper.runtime.client.EPRuntimeProvider;
import com.espertech.esper.runtime.client.EPStatement;
import com.espertech.esper.runtime.client.EPUndeployException;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/* $License$ */

/**
 * Provides a default Esper workspace.
 * 
 * <p>Events need to be registered with the Esper runtime in order to be added. In the Marketcetera
 * Automated Trading Platform, it is expected that events for the Esper runtime will implement
 * either {@link EsperEvent} or {@link HasEsperEvent}. This is not explicitly required, but is the recommended
 * process in order to allow as much automated behavior as possible.</p>
 * 
 * <p>For events that implement {@link EsperEvent}, in order to complete registration, one of two
 * things must be done:</p>
 * 
 * <ol>
 *   <li>Implement {@link Component}, {@link Scope}, and define the scope as {@link ConfigurableBeanFactory#SCOPE_PROTOTYPE}. This will cause the event to be automatically registered on start.
 *     Subsequent events can be instantiated either directly or with the Spring {@link ApplicationContext}. It doesn't matter which, for this purpose.</li>
 *   <li>Explicitly register the event class with this bean in your application configuration. Add the event class to {@link #getEventTypes()}.</li>
 * </ol>
 * 
 * <p>Once these steps have been followed, any events passed to the default event bus will automatically be added to the MATP Esper runtime by this object.</p>
 * 
 * <p>Inject this bean if you want to use the "default" MATP runtime.</p>
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@EnableAutoConfiguration
public class EsperEngine
{
    /**
     * Validate and start the object.
     */
    @PostConstruct
    public void start()
    {
        configuration = new Configuration();
        // create a new collection based on provided event types, if any. a new collection is used because we're going to add automatically
        //  derived event types to the collection and we don't want to modify the original.
        Collection<Class<?>> eventTypeCollection = Sets.newHashSet(eventTypes);
        // these are events that directly implement EsperEvent and are defined to the Spring AP in some way (the most logical way is as Prototype Beans (see main javadoc), but
        //  that's not mandatory.
        Map<String,EsperEvent> esperEventTypes = applicationContext.getBeansOfType(EsperEvent.class);
        esperEventTypes.values().forEach(esperEvent -> eventTypeCollection.add(esperEvent.getClass()));
        // next, find the types that implement HasEsperEvent and add their event types to the collection (the types of the contained beans)
        Map<String,HasEsperEvent> hasEsperEventTypes = applicationContext.getBeansOfType(HasEsperEvent.class);
        hasEsperEventTypes.values().forEach(hasEsperEvent -> eventTypeCollection.add(hasEsperEvent.getEsperEvent().getClass()));
        // these are the types we're going to add to the config
        eventTypeCollection.forEach(eventType -> configuration.getCommon().addEventType(eventType));
        SLF4JLoggerProxy.debug(this,
                               "Adding the following event type(s): {}",
                               eventTypeCollection);
        // create the runtime. it is expected that this runtime is the "default" runtime for the MATP.
        runtime = EPRuntimeProvider.getRuntime(runtimeName,
                                               configuration);
        runtime.initialize();
        SLF4JLoggerProxy.info(this,
                              "Created Esper runtime: {}",
                              runtimeName);
    }
    /**
     * Stop the object.
     */
    @PreDestroy
    public void stop()
    {
        if(runtime != null) {
            try {
                runtime.destroy();
            } catch (Exception ignored) {
            } finally {
                runtime = null;
            }
        }
    }
    /**
     * Deploy the given statement to the Esper engine.
     *
     * @param inStatement a <code>String</code> value, separated by ';' as necessary for multiple statements
     * @return an <code>EsperQueryMetaData</code> value
     * @throws EPRuntimeDestroyedException if the Esper engine is no longer available
     * @throws EPDeployException if the compiled Esper statement(s) cannot be deployed
     * @throws EPCompileException if the Esper statement(s) cannot be compiled
     */
    public EsperQueryMetaData deployStatement(String inStatement)
            throws EPRuntimeDestroyedException, EPDeployException, EPCompileException
    {
        String statementId = UUID.randomUUID().toString();
        CompilerArguments args = new CompilerArguments(configuration);
        EPCompiler compiler = EPCompilerProvider.getCompiler();
        EPCompiled epCompiled = compiler.compile("@name('" + statementId + "') " + inStatement,
                                                 args);
        EPDeployment deployment = runtime.getDeploymentService().deploy(epCompiled);
        EPStatement statement = runtime.getDeploymentService().getStatement(deployment.getDeploymentId(),
                                                                            statementId);
        return new EsperQueryMetaData(statementId,
                                      statement);
    }
    /**
     * Undeploy the Esper statement associated with the given query data.
     *
     * @param inQueryMetaData an <code>EsperQueryMetaData</code> value
     * @throws EPRuntimeDestroyedException if the Esper engine is no longer available
     * @throws EPUndeployException if the statement associated with the given meta data cannot be undeployed
     */
    public void undeployStatement(EsperQueryMetaData inQueryMetaData)
            throws EPRuntimeDestroyedException, EPUndeployException
    {
        runtime.getDeploymentService().undeploy(inQueryMetaData.getEsperStatement().getDeploymentId());
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
     * Get the runtime value.
     *
     * @return an <code>EPRuntime</code> value
     */
    public EPRuntime getRuntime()
    {
        return runtime;
    }
    /**
     * Get the configuration value.
     *
     * @return a <code>Configuration</code> value
     */
    public Configuration getConfiguration()
    {
        return configuration;
    }
    /**
     * Esper configuration value
     */
    private Configuration configuration;
    /**
     * Esper main runtime object
     */
    private EPRuntime runtime;
    /**
     * event types provided by configuration
     */
    private final Collection<Class<?>> eventTypes = Lists.newArrayList();
    /**
     * provides access to the Spring application context
     */
    @Autowired
    private ApplicationContext applicationContext;
    /**
     * allows the default MATP runtime to have a custom name
     */
    @Value("${metc.esper.runtime.name:MATP}")
    private String runtimeName;
}
