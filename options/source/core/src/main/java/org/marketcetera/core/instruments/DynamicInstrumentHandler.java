package org.marketcetera.core.instruments;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */
/**
 * A dynamic instrument specific function. This function is queried
 * dynamically to figure out if it can handle instrument specific processing
 * for an arbitrary object type.
 * <p>
 * A subclass of this class is created to reflect an instrument specific
 * function. The subclass declares any number of abstract methods for
 * the instrument specific function.
 * <p>
 * The instrument specific function instance is selected dynamically by the
 * {@link DynamicInstrumentFunctionSelector}. The selector invokes the
 * {@link #isHandled(Object)} method on each available function to
 * determine the appropriate instrument specific function instance and
 * returns the first function that is capable of handling the instance.
 * For this to work well the subclasses should ensure that the {@link #isHandled(Object)}
 * method returns true for a disjoint set of objects.
 * Any of the functions can then be invoked on the returned instance to
 * perform the instrument specific function.
 * <p>
 * This class is typically used to abstract functions that create / extract
 * instrument objects from other types of objects / data. 
 *
 * @param <T> The type of the object that is processed by this handler.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public abstract class DynamicInstrumentHandler<T> {

    /**
     * Returns true if this instance knows how to carry out the instrument
     * specific function for the supplied object.
     * <p>
     * This method should carry out minimal computation to figure out if it
     * can handle the supplied instance and should return as fast as possible.
     *
     * @param inValue the object
     *
     * @return if this instance can handle the supplied object.
     */
    protected abstract boolean isHandled(T inValue);
}
