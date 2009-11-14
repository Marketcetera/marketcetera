package org.marketcetera.photon.commons.databinding;

import org.eclipse.core.databinding.observable.value.AbstractObservableValue;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * A strongly typed observable value.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public abstract class TypedObservableValue<T> extends
        AbstractObservableValue implements ITypedObservableValue<T> {

    private final Class<T> mType;

    /**
     * Constructor.
     * 
     * @param type
     *            type parameter
     */
    protected TypedObservableValue(Class<T> type) {
        mType = type;
    }

    @Override
    public final Object getValueType() {
        return mType;
    }

    /**
     * Returns the typed value, equivalent to {@link #getValue()}.
     * 
     * @return the typed value
     */
    public final T getTypedValue() {
        return mType.cast(getValue());
    }

    @Override
    protected final void doSetValue(Object value) {
        doSetTypedValue(mType.cast(value));
    }

    protected abstract void doSetTypedValue(T value);

    @Override
    protected abstract T doGetValue();
}