package org.marketcetera.photon.commons.databinding;

import java.text.MessageFormat;

import org.eclipse.core.databinding.observable.value.DecoratingObservableValue;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * A {@link DecoratingObservableValue} that adds strongly typed semantics to an
 * existing observable value.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class TypedObservableValueDecorator<T> extends DecoratingObservableValue
        implements ITypedObservableValue<T> {

    /**
     * Adds strongly typed semantics to an observable.
     * 
     * @param decorated
     *            the observable value being decorated, must have same type as
     *            clazz
     * @param disposeDecoratedOnDispose
     *            whether the decorated observable should be disposed when the
     *            decorator is disposed
     * @param type
     *            type parameter
     * @return a typed observable value
     * @throws IllegalArgumentException
     *             if decorated does not have the correct type
     */
    public static <T> ITypedObservableValue<T> decorate(
            IObservableValue decorated, boolean disposeDecoratedOnDispose,
            Class<T> type) {
        return new TypedObservableValueDecorator<T>(decorated,
                disposeDecoratedOnDispose, type);
    }

    /**
     * Creates a strongly typed observable value.
     * 
     * @param type
     *            type parameter
     * @return a typed observable value
     * @throws IllegalArgumentException
     *             if decorated does not have the correct type
     */
    public static <T> ITypedObservableValue<T> create(Class<T> type) {
        return new TypedObservableValueDecorator<T>(WritableValue
                .withValueType(type), true, type);
    }

    /**
     * Constructor.
     * 
     * @param decorated
     *            the observable value being decorated, must have same type as
     *            the type parameter
     * @param disposeDecoratedOnDispose
     *            whether the decorated observable should be disposed when the
     *            decorator is disposed
     * @param type
     *            type parameter
     * @throws IllegalArgumentException
     *             if decorated does not have the correct type
     */
    protected TypedObservableValueDecorator(IObservableValue decorated,
            boolean disposeDecoratedOnDispose, Class<T> type) {
        super(decorated, disposeDecoratedOnDispose);
        Class<?> decoratedType = (Class<?>) decorated.getValueType();
        if (!type.isAssignableFrom(decoratedType)) {
            throw new IllegalArgumentException(MessageFormat.format(
                    "invalid type {0}, expected {1}", decoratedType, type)); //$NON-NLS-1$
        }
    }

    /**
     * Returns the typed value, equivalent to {@link #getValue()}.
     * 
     * @return the typed value
     */
    @SuppressWarnings("unchecked")
    @Override
    public final T getTypedValue() {
        return (T) getValue();
    }
}