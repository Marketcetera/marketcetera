package org.marketcetera.photon.commons.databinding;

import org.eclipse.core.databinding.observable.value.IObservableValue;
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
public interface ITypedObservableValue<T> extends IObservableValue {

    /**
     * Returns the typed value, equivalent to {@link #getValue()}.
     * 
     * @return the typed value
     */
    T getTypedValue();
}