/* $License$ */

/**
 * Remote Receiver Module capable of receiving data and emitting it to
 * remote destinations.
 * <p>
 * See {@link org.marketcetera.modules.remote.receiver.ReceiverFactory}
 * for more information on the module provider.
 * <p>
 * See {@link org.marketcetera.modules.remote.receiver.ReceiverModule}
 * for details on the singleton module instance.
 * <p>
 * See {@link org.marketcetera.modules.remote.receiver.ReceiverModuleMXBean}
 * for details on the management interface for the module instance.
 * <p>
 * <h4>Logging:</h4>
 * <p>
 * Note that the receiver module depends on log4j as documented in
 * {@link org.marketcetera.modules.remote.receiver.ReceiverModuleMXBean#setLogLevel(org.marketcetera.event.LogEvent.Level)}.
 * If log4j is not used as the logger provider for slf4j, modules that emit
 * log events into the receiver conditionally based on
 * <code>SLF4JLoggerProxy.is*Enabled()</code> calls will not emit events
 * correctly based on the currently configured value of receiver's log level
 * {@link org.marketcetera.modules.remote.receiver.ReceiverModuleMXBean#getLogLevel()}.
 *
 * <h4>JAAS Configuration</h4>
 * <p>
 * The receiver module performs automatic JAAS Configuration. See
 * {@link org.marketcetera.modules.remote.receiver.JaasConfiguration} for
 * details.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.5.0
 */
package org.marketcetera.modules.remote.receiver;