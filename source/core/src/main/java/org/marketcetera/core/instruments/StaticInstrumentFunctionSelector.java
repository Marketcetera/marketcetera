package org.marketcetera.core.instruments;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.trade.Instrument;
import org.apache.commons.lang.Validate;

import java.util.Map;
import java.util.HashMap;
import java.util.ServiceLoader;

import com.google.common.collect.ImmutableMap;

/* $License$ */
/**
 * A selector that maintains a mapping of {@link InstrumentFunctionHandler}
 * instance for each instrument type. The handler for a instrument can be
 * obtained via {@link #forInstrument(Instrument)}.
 * <p>
 * Since creation of an instance of the selector is an expensive operation,
 * it's recommended that a singleton instance of this selector be created
 * for processing.
 *
 * @param <T> the type of handler selected by this instance. 
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public final class StaticInstrumentFunctionSelector<T extends InstrumentFunctionHandler> {
    /**
     * Creates an instance.
     *
     * @param inClass the instrument function handler class. Cannot be null.
     */
    public StaticInstrumentFunctionSelector(Class<T> inClass) {
        Validate.notNull(inClass, "class");  //$NON-NLS-1$
        mClass = inClass;
        Map<Class<?>, T> handlers =
                new HashMap<Class<?>, T>();
        ServiceLoader<T> loader = ServiceLoader.load(inClass);
        for (T t : loader) {
            handlers.put(t.getInstrumentType(), t);
        }
        SLF4JLoggerProxy.debug(this, "Available handlers {}", handlers);  //$NON-NLS-1$
        mHandlers = ImmutableMap.copyOf(handlers);
    }

    /**
     * Returns the Instrument function handler for the supplied instrument.
     *
     * @param inInstrument the instrument, cannot be null.
     *
     * @return the function for the supplied instrument
     * 
     * @throws IllegalArgumentException if the function for the supplied
     *                                       instrument is not available.
     */
    public T forInstrument(Instrument inInstrument) {
        Validate.notNull(inInstrument, "instrument");  //$NON-NLS-1$
        Class<? extends Instrument> cls = inInstrument.getClass();
        T t = mHandlers.get(cls);
        if (t == null) {
            throw new IllegalArgumentException(
                    Messages.NO_HANDLER_FOR_INSTRUMENT.getText(cls.getName(),
                            mClass.getName()));
        }
        return t;
    }

    private final Class<T> mClass;
    private final Map<Class<?>, T> mHandlers;
}

