package org.marketcetera.core.instruments;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.apache.commons.lang.Validate;

import java.util.ServiceLoader;
import java.util.LinkedList;
import java.util.List;

import com.google.common.collect.ImmutableList;

/* $License$ */
/**
 * A Instrument function selector that selects an appropriate instance
 * of {@link DynamicInstrumentHandler} given an instance of
 * type <code>T</code> by dynamically querying the handlers.
 * <p>
 * Since creation of an instance of the selector is an expensive operation,
 * it's recommended that a singleton instance of this selector be created
 * for processing.
 * 
 * @param <T> The type of objects that are used to dynamically select an
 * {@link DynamicInstrumentHandler} instance.
 * @param <S> The type of function handler selected by this instance.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class DynamicInstrumentFunctionSelector<T,S extends DynamicInstrumentHandler<T>> {
    /**
     * Creates an instance.
     *
     * @param inClass the class of dynamic functions that are selected
     * by this instance. Cannot be null.
     */
    public DynamicInstrumentFunctionSelector(Class<S> inClass) {
        Validate.notNull(inClass, "class");  //$NON-NLS-1$
        List<S> list = new LinkedList<S>();
        ServiceLoader<S> loader = ServiceLoader.load(inClass);
        for(S s : loader) {
            list.add(s);
        }
        SLF4JLoggerProxy.debug(this, "Available handlers {}", list);  //$NON-NLS-1$
        mClass = inClass;
        mHandlers = ImmutableList.copyOf(list);
    }

    /**
     * Selects an appropriate handler for the specified value.
     * <p>
     * The first handler whose {@link DynamicInstrumentHandler#isHandled(Object)}
     * returns true is returned back. 
     *
     * @param inValue the value
     *
     * @return the appropriate handler for the specified value.
     *
     * @throws IllegalArgumentException if a selectable could not be
     * found for the specified object.
     */
    public S forValue(T inValue) {
        Validate.notNull(inValue, "value");  //$NON-NLS-1$
        for(S s: mHandlers) {
            if(s.isHandled(inValue)) {
                return s;
            }
        }
        throw new IllegalArgumentException(Messages.NO_HANDLER_FOR_VALUE.
                getText(inValue,mClass.getName()));
    }

    /**
     * Returns the immutable list of handlers that are available
     * to the selector.
     *
     * @return the list of handlers available.
     */
    public List<S> getHandlers() {
        return mHandlers;
    }

    private final Class<S> mClass;
    private final List<S> mHandlers;
}
