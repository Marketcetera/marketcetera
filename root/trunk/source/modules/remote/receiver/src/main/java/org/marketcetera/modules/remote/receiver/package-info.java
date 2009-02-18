/* $License$ */

/**
 * Remote Receiver Module capable of receiving data and emitting it to
 * remote destinations.
 * <p>
 * See {@link ReceiverFactory} for more information on the
 * module provider.
 * <p>
 * See {@link ReceiverModule} for details on the singleton
 * module instance.
 * <p>
 * See {@link ReceiverModuleMXBean} for details on the management interface
 * for the module instance.
 * <p>
 * <b>Logging:</b>Note that the receiver module depends on log4j as
 * documented in
 * {@link RecieverModuleMXBean#setLogLevel(org.marketcetera.event.LogEvent.Level)}.
 * If log4j is not used as the logger provider for slf4j, modules that emit
 * log events into the receiver conditionally based on
 * <code>SLF4JLoggerProxy.is*Enabled()</code> calls will not emit events
 * correctly based on the currently configured value of receiver's log level
 * {@link ReceiverModuleMXBean#getLogLevel()}.  
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
package org.marketcetera.modules.remote.receiver;