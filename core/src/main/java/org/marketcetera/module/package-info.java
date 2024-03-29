/* $License$ */
/**
 * <p>
 *     This package provides the infrastructure for creating data flows between
 *     pluggable modules. The modules are independent pieces of functionality
 *     that may be packaged separately and may be supplied by different vendors.
 * </p>
 * <p>
 *     Data flow involves exchange of data of any type from a module that emits
 *     it, via a couple of modules that process it, to a module that consumes it.
 * </p>
 * <p>
 *     The data flows can be dynamically set up and canceled, throughout the life
 *     of the application. The modules' implementations can be dynamically added
 *     to the application however, there's no mechanism to remove module
 *     implementations from the system without stopping it.
 * </p>
 * <p>
 *     Each module can participate in data flows in any of the following capacities.
 * </p>
 * <ul>
 *     <li>{@link org.marketcetera.module.DataEmitter Generating Data}</li>
 *     <li>{@link org.marketcetera.module.DataReceiver Consuming Data}</li>
 *     <li>{@link org.marketcetera.module.DataFlowRequester Creating Data Flows}</li>
 * </ul>
 * <p>
 *     Modules indicate their capabilities by implementing various interfaces
 *     linked above.
 * </p>
 * <p>
 *     {@link org.marketcetera.module.ModuleURN URNs} are used to uniquely identify module types &amp; module
 *     instances.
 * </p>
 * <p>
 *     Requests to setup data flow between modules can either be initiated
 *     by a module, if it's designed to create data flows, when it's started.
 *     Or a request can be made to the framework to setup a
 *     data flow between multiple modules.
 * </p>
 * <p>
 *     The Module framework services are available via
 *     {@link org.marketcetera.module.ModuleManager}. Look at its documentation
 *     to figure out the services are offered by the module framework.
 * </p>
 * <h2>Concepts</h2>
 * <h3>Module Factory / Provider</h3>
 * <p>
 *     Module implementations are provided by indvidual Module Providers. Typically, module
 *     implementation from a single provider is bundled within a single jar. However,
 *     nothing prevents a module provider from bundling the modules in different jars.
 * </p>
 * <p>
 *     Each module provider provides a {@link org.marketcetera.module.ModuleFactory}
 *     implementation that creates new Module instances. The Module Framework discovers
 *     all the factory implementations available to its class loader and uses them to
 *     create new Module Instances as needed.
 * </p>
 * <p>
 *     Each provider has a URN that uniquely identifies the provider. The URNs of all
 *     the modules created by the provider always have the provider URN as its prefix.
 * </p>
 * <p>
 *     Each module factory creates modules that perform a certain type of function.
 *     There is a notion of factory/provider types, such that different implementations
 *     can provide similar kinds of functions. For example, there can be different
 *     implementations of market data providers, each providing market data
 *     from a different market data vendor.
 * </p>
 * <p>
 *     Each factory can support either multiple module instances or just one singleton
 *     module instance, depending on the feature they provide. For example, typically
 *     market data provider modules will have a singleton instance whereas strategy
 *     modules will have multiple instances, one per strategy.
 * </p>
 * <h3>Module</h3>
 * <p>
 *     A module is an entity that is capable of participating in data flows within the
 *     module framework. Each module is instantiated by a module factory. A module can
 *     emit data and / or receive data and / or request data flows.
 * </p>
 * <p>
 *     After being created by the factory, the module needs to started before it
 *     can start participating in the data flows. Each module receives a callback
 *     when it's started, to let the module initialize itself before it's started.
 *     Similarly a module can be stopped to prevent it from participating in
 *     any data flows. A module receives a callback when it's being stopped
 *     as well.
 * </p>
 * <h3>Provider/Module URN</h3>
 * <p>
 *     URNs of the following form are used everywhere in the system to identify
 *     Module Providers and Instances:
 * </p>
 * <pre>
 *     metc:provType:provider:instance
 * </pre>
 * <p>
 *     See {@link org.marketcetera.module.ModuleURN} for more details.
 * </p>
 * <p>
 *     The <code>provType</code> value in the URN serves to identify providers that provide
 *     modules that deliver similar kind of function. For example, <code>mdata</code> is
 *     used to identify all market data vendors, <code>cep</code> is used to identify all
 *     modules that provide Complex Event Processing functionality.
 * </p>
 * <p>
 *     For each <code>provType</code> a standard set of request type and data types are
 *     defined, allowing the modules to use modules that are from different providers but
 *     have the same <code>provType</code>, interchangeably. The documentation for the
 *     standard set of <code>provType</code> will be generated when the module
 *     implementations are being authored.
 * </p>
 * <p>
 *     The following standard provider types are currently envisioned:
 * </p>
 * <ol>
 *     <li><b>sink</b>: Universal sink for all kinds of data.
 *         Only capable of receiving data.</li>
 *     <li><b>mdata</b>: Generates market data. Only capable of emitting data.
 *         Needs definition of standard request parameters</li>
 *     <li><b>strategy</b>: Receives and Emits data. Optionally creates data flows as well.
 *         May need to define standard request parameters for generating trades v/s suggestions.</li>
 *     <li><b>cep</b>: Receives and Emits data. Performs complex event processing.
 *         Needs definition of standard request parameters.</li>
 *     <li><b>ors</b>: Receives trades and emits execution report data. Need to define
 *         standard request parameters for generating execution reports.</li>
 * </ol>
 * <h3>Module Framework</h3>
 * <p>
 *     The Module Framework provides an operating environment for these modules to exist
 *     and exchange data. It provides services, to list available providers, module
 *     instances and data flows between them, create / start / stop / delete modules
 *     and set up / tear down data flows between them.
 *     All the module framework services are offered by
 *     {@link org.marketcetera.module.ModuleManager}
 * </p>
 * <p>
 *     The module framework also exports its services via JMX via
 *     {@link org.marketcetera.module.ModuleManagerMXBean}. The main client is
 *     jconsole, however this interface allows for the framework services to
 *     be programmatically accessed if needed.
 * </p>
 * <h3>Data Types</h3>
 * <p>
 *     Data Types are relevant for the following usages:
 * </p>
 * <ol>
 *     <li>Parameters supplied when creating a module instance.</li>
 *     <li>Parameters supplied when requesting data from emitter modules.</li>
 *     <li>Data Exchanged between modules</li>
 * </ol>
 * <p>
 *     Data of any type can be used for all these three usages. However, modules are
 *     strongly recommended to accept strings, in addition to any specific types they need,
 *     for usages 1 &amp; 2 above.
 * </p>
 * <p>
 *     Module Framework API is available via an MXBean through JMX. And
 *     that API only supports string parameters for usages 1 &amp; 2 above. The module
 *     framework automatically converts string parameters to actual java types,
 *     for a limited set of types listed below, for usage 1 above.
 *     To the extent modules support string
 *     parameters for usages 2 above, they will be usable via JMX interface.
 *     Otherwise, those modules (or subset of their functionalities that require
 *     non-string parameters) will not be usable in the standalone module container
 *     where all module framework functions are exposed via JMX.
 * </p>
 * <p>
 *     The module framework supports automatic conversion of strings to actual java
 *     types for the following java types.
 * </p>
 * <ul>
 *     <li>Java Primitive Types</li>
 *     <li>{@link java.math.BigDecimal}</li>
 *     <li>{@link java.math.BigInteger}</li>
 *     <li>{@link java.io.File}</li>
 *     <li>{@link java.net.URL}</li>
 *     <li>{@link org.marketcetera.module.ModuleURN}</li>
 * </ul>
 * <h2>Module Lifecycle</h2>
 * <p>
 *     Module instances can be created, started, stopped and deleted via services provided
 *     by {@link org.marketcetera.module.ModuleManager}. The module manager uses the
 *     provider specific {@link org.marketcetera.module.ModuleFactory} instance
 *     to create modules.
 * </p>
 * <p>
 *     Parameters can be optionally supplied to the module factory when requesting it to
 *     create new instances. Although these parameters can be of any type, modules are
 *     strongly recommended to support parameter types, that the module framework
 *     is capable of converting strings to, to enable their
 *     instantiation, via JMX, in the standalone module container environment.
 * </p>
 * <p>
 *     If a factory does not support
 *     {@link org.marketcetera.module.ModuleFactory#isMultipleInstances() multiple}
 *     module instances , its singleton module instance is created when the module
 *     framework is being initialized.
 * </p>
 * <p>
 *     If a factory is capable of
 *     {@link org.marketcetera.module.ModuleFactory#isAutoInstantiate() auto-instantiating}
 *     module instances, the module framework
 *     will automatically instantiate its module instances whenever the first data flow
 *     request identifying an instance that does not exist is issued. The module framework
 *     at that point will request the module factory to instantiate a module instance
 *     supplying it the module instance URN as a parameter.
 *     Any factory that is capable of auto-instantiating modules, should accept
 *     module URN as the one and only parameter to create the module instance.
 * </p>
 * <p>
 *     If a created module designates itself as
 *     {@link org.marketcetera.module.Module#isAutoStart() auto-startable}, the module
 *     framework will automatically start the module as soon as it's created.
 * </p>
 * <h2>Module Packaging</h2>
 * <p>
 *     Each module implementation is packaged in a jar that has a file named
 *     <code>org.marketcetera.module.ModuleFactory</code> within the
 *     <code>META-INF/services</code>. The file contains fully qualified name
 *     of the ModuleFactory implementation that will instantiate Module(s)
 *     delivered by the provider.
 *     For more information see {@link java.util.ServiceLoader}
 * </p>
 * <p>
 *     Module Implementations can be added dynamically by making its classes
 *     available in the classpath and invoking
 *     {@link org.marketcetera.module.ModuleManager#refresh()}.
 * </p>
 *
 * <h2>Emitting Data</h2>
 * <p>
 *     A module that can emit data should implement
 *     {@link org.marketcetera.module.DataEmitter}. When an emitter is
 *     requested to emit data, it's provided an
 *     {@link org.marketcetera.module.DataEmitterSupport}
 *     instance which can be used to emit data.
 *     A module can receive a request to emit data when it's in the
 *     {@link org.marketcetera.module.ModuleState#isStarted() started}
 *     state.
 * </p>
 * <p>
 *     Each request is identified by a unique
 *     {@link org.marketcetera.module.RequestID} that is supplied to the module
 *     along with the request. This requestID is also supplied when the framework
 *     asks the module to cancel the request.
 * </p>
 * <p>
 *     The emitting module has an option to send any data emit errors via
 *     {@link org.marketcetera.module.DataEmitterSupport#dataEmitError(org.marketcetera.util.log.I18NBoundMessage,boolean)}
 *     It can also request that the data flow be stopped via that method.
 * </p>
 * <p>
 *     Although modules can accept request parameters of any type, they
 *     are strongly encouraged to be able to accept string requests, to
 *     enable their usage, via JMX, in the standalone module container
 *     environment.
 * </p>
 * <h2>Receiving Data</h2>
 * <p>
 *     A module that can receive data should implement
 *     {@link org.marketcetera.module.DataReceiver}. A receiver
 *     module can receive data anytime after it has been started.
 *     Data can be of type and the module can choose to throw
 *     {@link org.marketcetera.module.StopDataFlowException} if it
 *     wants the data flow thats sending it data to stop.
 * </p>
 *
 * <h2>Setting up Data Flows</h2>
 * <p>
 *     There are two ways to set up data flows.
 * </p>
 *     <ol>
 *     <li>Data Flows initiated by a module instance.</li>
 *     <li>Data Flows initiated by a user / client of module framework.</li>
 *     </ol>
 * <p>
 *     A module can acquire the capability to initiate a data flow by implementing
 *     {@link org.marketcetera.module.DataFlowRequester}. It can then create and
 *     cancel data flows via {@link org.marketcetera.module.DataFlowSupport}.
 *     A module can request &amp; cancel data flows when it's in the
 *     {@link org.marketcetera.module.ModuleState#isStarted() started} state.
 *     It's expected that a module will typically create data flows when it's started
 *     from within its {@link org.marketcetera.module.Module#preStart()} method.
 *     And that a module will typically cancel data flows when it's stopped from within
 *     its {@link org.marketcetera.module.Module#preStop()} method.
 *     A module cannot be stopped if it's participating in any data flows that it
 *     didn't initiate. As a convenience, the module framework stops all the data
 *     flows that a module had initiated but didn't stop from within its
 *     <code>preStop()</code> when it's being stopped.
 * </p>
 * <p>
 *     Any client of module framework can create and delete data flows by invoking
 *     {@link org.marketcetera.module.ModuleManager#createDataFlow(DataRequest[])} and
 *     {@link org.marketcetera.module.ModuleManager#cancel(DataFlowID)}.
 * </p>
 * <p>
 *     Once a data flow has been setup, its details and current status can be obtained by
 *     invoking {@link org.marketcetera.module.ModuleManager#getDataFlowInfo(DataFlowID)}.
 * </p>
 * <h2>Module Configuration and Management</h2>
 * <p>
 *     The Module factory and individual instances can export MXBean interfaces
 *     that expose their attributes and operations to enable their management.
 *     Upon creation, if a factory or a module instance is found to be implementing
 *     an {@link javax.management.MXBean} interface or an
 *     {@link javax.management.DynamicMBean}, it registers the MBean with the
 *     platform MBean server. Similarly, whenever a module instance is being
 *     deleted, the module framework unregisters its MBean from the platform
 *     MBean server.
 * </p>
 * <p>
 *     Do note that if the module or the module factory implements
 *     {@link javax.management.DynamicMBean} make sure that the types used
 *     for various bean attributes &amp; operations are limited to standard java
 *     types. Otherwise, tools like jconsole will not be able to display
 *     information from those beans.
 * </p>
 * <p>
 *     The MBeans, their attributes, methods and method parameters can be annotated
 *     with {@link org.marketcetera.module.DisplayName}, to specify descriptive text
 *     that will be available for viewing within the MBean's
 *     {@link javax.management.Descriptor}.
 * </p>
 * <p>
 *     The module framework functions are exported via
 *     {@link org.marketcetera.module.ModuleManagerMXBean}. All of its operations
 *     accept parameters of primitive &amp; string types, so that they can be easily
 *     invoked by widely available jmx clients like jconsole. Operations to
 *     create module instances and data flows support special string syntax as
 *     these operations accept complex parameters. Look at the bean's class
 *     documentation for more details on these syntacies.
 * </p>
 * <h3>Default Configuration</h3>
 * <p>
 *     Right after registering the module factory / instance's MBean with the
 *     platform MBean server, the module framework, will discover all the writable
 *     attributes of the MBean that are of types that can be converted from string
 *     to the actual types by the framework and will populate the their values
 *     using its default configuration initialization mechanism.
 * </p>
 * <p>
 *     The default configuration mechanism within the module framework, allows a
 *     {@link org.marketcetera.module.ModuleConfigurationProvider configuration provider}
 *     to be configured that provides a module container specific mechanism to
 *     obtain the default property values for a module.
 *     A particular implementation of this mechanism may obtain default property
 *     values from
 *     {@link org.marketcetera.module.PropertiesConfigurationProvider property}
 *     files within the standalone module container.
 *     Another implementation may obtain the default value from the photon's
 *     preferences framework or XML files.
 * </p>
 * <h3>MXBeans</h3>
 * <p>
 *     The module framework, exports the Module Factory / Instance
 *     implementations as MXBeans if the instance implements an interface
 *     that is a MXBean interface. The Instance is wrapped in a proxy object
 *     before it's registered with the MBean server. The proxy object ensures
 *     that the thread context classloader is set for every method invocation
 *     on the MXBean implementation.
 *     After that, any JMX client can invoke
 *     operations on these MXBeans, register for their notifications or
 *     read / write their attributes. It's the responsibility of the
 *     Module factory / instance implementation to ensure if any of these
 *     operations are allowed based on its current state. For example,
 *     a module instance may not want to allow some of its attribute
 *     values to be changed while it's in the start state.
 *     In such cases, the module may choose to throw an exception disallowing
 *     such an attribute change.
 * </p>
 * <p>
 *     To be able to emit notifications, usually the MXBeans have to extend
 *     the {@link javax.management.NotificationBroadcasterSupport} class.
 *     However, the modules have to extend
 *     {@link org.marketcetera.module.Module} and hence they are not able to
 *     extend <code>NotificationBroadcasterSupport</code>. The recommendation
 *     here is to have the MXBean implement {@link javax.management.NotificationEmitter}
 *     instead and delegate to a private <code>NotificationBroadcasterSupport</code>
 *     instance instead.
 * </p>
 * <h4>Thread Context Classloader</h4>
 * <p>
 *     The module manager sets the thread context classloader when handling
 *     MBean method invocations on the module factories and instances to the
 *     same classloader as the one supplied to its constructor. This is useful
 *     when certain module implementations depend on libraries that need the
 *     thread context classloader set.
 * </p>
 * <h2>Sink Module</h2>
 * <p>
 *     A sink module is provided along with the framework that can be used to receive
 *     all the data that is being generated by the pipeline. The sink module has
 *     a singleton auto started instance with the URN
 *     <code>metc:sink:system:single</code>. The sink module is implicitly appended
 *     to all data flows, if possible.
 * </p>
 * <p>
 *     Other components of the application can receive all the data received by the
 *     sink module by implementing the
 *     {@link org.marketcetera.module.SinkDataListener} and registering it with
 *     the module manager via
 *     {@link org.marketcetera.module.ModuleManager#addSinkListener(SinkDataListener)}.
 * </p>
 * <p>
 *     The Sink module also exports a management interface via
 *     {@link org.marketcetera.module.SinkModuleMXBean}.
 * </p>
 * <h2>Documenting Factories and Modules</h2>
 * <p>
 *     It's recommended that the javadocs for the Module Factories and Modules
 *     include the following pieces of information.
 * </p>
 * <h3>Module Factory</h3>
 * <p>
 *     See documentation of {@link org.marketcetera.module.ModuleFactory}
 *     sub-classes for examples.
 * </p>
 * <table>
 *   <caption>Describes the ModuleFactory</caption>
 *   <tr><th>Provider URN:</th><td>The provider URN</td></tr>
 *   <tr><th>Cardinality:</th><td>If the factory creates a singleton module
 *       instance or if it can create module instances.</td></tr>
 *   <tr><th>InstanceURN:</th><td>The singleton module instance URN, if the
 *       factory only creates a single module instance.</td></tr>
 *   <tr><th>Auto-Instantiated:</th><td>If the module instances can be
 *       {@link org.marketcetera.module.ModuleFactory#isAutoInstantiate() auto-instantiated}
 *       by the factory.</td></tr>
 *   <tr><th>Auto-Started:</th><td>If the created module instance can be
 *       {@link org.marketcetera.module.Module#isAutoStart() auto-started}.</td></tr>
 *   <tr><th>Instantiation Arguments:</th><td>Description of arguments needed
 *       when requesting the factory to create new instances.</td></tr>
 *   <tr><th>Management Interface</th><td>MXBean Management interface exposed
 *       by the module.</td></tr>
 *   <tr><th>MX Notifications</th><td>Description of JMX notifications emitted
 *         by the module factory.</td></tr>
 *   <tr><th>Module Type</th><td>The type / class of modules created by this
 *       factory.</td></tr>
 * </table>
 * <h3>Module</h3>
 * <p>
 *     See documentation of {@link org.marketcetera.module.Module}
 *     sub-classes for examples.
 * </p>
 * <table>
 *   <caption>Describes the Module</caption>
 *   <tr><th>Capabilities</th><td>If the module can emit, receive data and if it
 *       can request data flows.</td></tr>
 *   <tr><th>DataFlow Request Parameters</th><td>The type of parameters that
 *       should be included in a {@link org.marketcetera.module.DataRequest data flow request}
 *       when requesting module to emit data.</td></tr>
 *   <tr><th>Stops data flows</th><td>If the module stops data flows when emitting
 *       or receiving data.</td></tr>
 *   <tr><th>Start Operation</th><td>Description of any significant functions
 *       performed when the module starts.</td></tr>
 *   <tr><th>Stop Operation</th><td>Description of any significant functions
 *       performed when the module stops.</td></tr>
 *   <tr><th>Management Interface</th><td>MXBean Management interface exposed by
 *       the module.</td></tr>
 *   <tr><th>MX Notifications</th><td>Description of JMX notifications emitted
 *       by the module.</td></tr>
 *   <tr><th>Factory</th><td>The factory class that creates this module
 *       instance.</td></tr>
 * </table>
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@XmlSchema(namespace = "http://marketcetera.org/types/modules")
package org.marketcetera.module;

import javax.xml.bind.annotation.XmlSchema;