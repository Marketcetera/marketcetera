/* $License$ */
/**
 * Provides mechanisms to abstract out instrument specific functionality in
 * the system.
 * <p>
 * Two types of abstractions are available.
 * <ol>
 * <li>{@link org.marketcetera.core.instruments.InstrumentFunctionHandler}: For carrying out instrument
 * specific functions when an instrument instance is available.</li>
 * <li>{@link org.marketcetera.core.instruments.DynamicInstrumentHandler}: For carrying out
 * instrument specific functions when an instrument instance is not available
 * but some other data is available from which the instrument type can be
 * determined.</li>
 * </ol>
 * <h3>InstrumentFunctionHandler</h3>
 * <p>
 * Any instrument specific functionality that needs to be performed when an
 * instrument instance is available is abstracted out by
 * implementing it in a subclass of
 * {@link org.marketcetera.core.instruments.InstrumentFunctionHandler}.
 * <p>
 * A {@link org.marketcetera.core.instruments.StaticInstrumentFunctionSelector}
 * instance can be used to dynamically select the appropriate handler
 * instance for an instrument via 
 * {@link org.marketcetera.core.instruments.StaticInstrumentFunctionSelector#forInstrument(org.marketcetera.trade.Instrument)}.
 * The selector chooses the appropriate instrument specific subclass of
 * {@link org.marketcetera.core.instruments.StaticInstrumentFunctionSelector}
 * based on the runtime type of the instrument.
 * <p>
 * See {@link org.marketcetera.core.instruments.InstrumentToMessage} for an
 * example.
 * <p>
 * <h3>DynamicInstrumentHandler</h3>
 * In cases when an instrument instance is not available and instrument
 * specific functionality needs to be invoked based on an arbitrary data,
 * a combination of {@link org.marketcetera.core.instruments.DynamicInstrumentFunctionSelector}
 * & {@link org.marketcetera.core.instruments.DynamicInstrumentHandler} is
 * used. These classes help dynamically select the instrument specific
 * functionality in absence of the Instrument object. See
 * {@link org.marketcetera.core.instruments.InstrumentFromMessage}
 * for an example.
 * <h3>Available Handlers</h3>
 * The following handlers are available for extraction and insertion of
 * instruments from / in to FIX messages.
 * <ol>
 * <li>{@link org.marketcetera.core.instruments.InstrumentToMessage}:
 * Sets the fields corresponding to an instrument on a FIX message</li>
 * <li>{@link org.marketcetera.core.instruments.InstrumentFromMessage}:
 * Extracts an instrument from a FIX message</li>
 * </ol>
 * <h3>packaging</h3>
 * The {@link java.util.ServiceLoader} class is used to dynamically
 * discover and load instrument specific functions. A
 * file in <code>META-INF/services</code> is created for each instrument
 * specific platform function. The file contains the names of classes
 * that implement the instrument specific function for each instrument type.
 * See  {@link java.util.ServiceLoader} documentation for more details.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 2.0.0
 */
package org.marketcetera.core.instruments;