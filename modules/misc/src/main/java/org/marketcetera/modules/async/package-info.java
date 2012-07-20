/**
 * Modules that enable asynchronous processing of data objects in a data flow.
 * <p>
 * By default all the data items in a data flow are processed synchronously and
 * delivery of the next data item has to wait until the previous data item
 * has been processed. Modules in this package enable asynchronous processing
 * of data items by decoupling the delivery and processing of data items.
 * <p>
 * The following module providers are available.
 * <ol>
 * <li><strong>Simple Async Processor</strong> : Instances of this module
 * can be inserted between any two modules within a data flow. The data
 * received from the upstream module is added to a queue for that data flow.
 * For each data flow that this module participates in, a separate thread
 * is spawned. That thread removes the data from the queue for the data flow
 * and sends it to the downstream module.
 * See {@link org.marketcetera.modules.async.SimpleAsyncProcessorFactory} and
 * {@link org.marketcetera.modules.async.SimpleAsyncProcessor} for more details
 * </li>
 * </ol>
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 2.0.0
 */
package org.marketcetera.modules.async;