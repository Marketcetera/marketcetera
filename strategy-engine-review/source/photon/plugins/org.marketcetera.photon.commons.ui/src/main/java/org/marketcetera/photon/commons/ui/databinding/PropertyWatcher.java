package org.marketcetera.photon.commons.ui.databinding;

import java.util.Collection;
import java.util.Set;

import org.eclipse.core.databinding.ObservablesManager;
import org.eclipse.core.databinding.observable.map.IMapChangeListener;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.map.MapChangeEvent;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.property.value.IValueProperty;
import org.marketcetera.util.misc.ClassVersion;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/* $License$ */

/**
 * Tracks changes to value properties on objects. An instance is initialized
 * with a static list of {@link IValueProperty} that define the properties to
 * watch. When an {@link IObservableSet} is {@link #watch(IObservableSet)
 * watched}, subclasses will be notified via
 * {@link #propertiesChanged(ImmutableSet)} every time one or more of the
 * properties changes for items in the set.
 * <p>
 * Currently this only supports value properties and not list properties.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public final class PropertyWatcher {

    /**
     * Interface for listeners that receive notification when properties change.
     */
    @ClassVersion("$Id$")
    public interface IPropertiesChangedListener {
        /**
         * Notifies the listener that properties have changed.
         * 
         * @param affectedElements
         *            the elements whose properties have changed
         */
        void propertiesChanged(ImmutableSet<?> affectedElements);
    }

    private final ObservablesManager mObservables = new ObservablesManager();

    private final ImmutableList<IValueProperty> mProperties;

    private final IPropertiesChangedListener mListener;

    private final IMapChangeListener mMapChangeListener = new IMapChangeListener() {
        public void handleMapChange(MapChangeEvent event) {
            Set<?> affectedElements = event.diff.getChangedKeys();
            if (!affectedElements.isEmpty()) {
                mListener.propertiesChanged(ImmutableSet
                        .copyOf(affectedElements));
            }
        }
    };

    private boolean mDisposed;

    /**
     * Constructor.
     * 
     * @param properties
     *            properties to watch
     */
    public PropertyWatcher(Collection<IValueProperty> properties,
            IPropertiesChangedListener listener) {
        mProperties = ImmutableList.copyOf(properties);
        mListener = listener;
    }

    /**
     * Tracks the provided set of elements. Tracking will not stop until this
     * PropertyWatcher is {@link #dispose() disposed}.
     * <p>
     * This can be called multiple times.
     * 
     * @param elements
     *            elements to track
     * @throws IllegalStateException
     *             if the PropertyWatcher has been disposed
     */
    public synchronized void watch(IObservableSet elements) {
        if (mDisposed) {
            throw new IllegalStateException(
                    "PropertyWatcher has already been disposed"); //$NON-NLS-1$
        }
        for (IValueProperty property : mProperties) {
            IObservableMap map = property.observeDetail(elements);
            map.addMapChangeListener(mMapChangeListener);
            mObservables.addObservable(map);
        }
    }

    /**
     * Disposes this PropertyWatcher, after which it must no longer be used.
     */
    public synchronized void dispose() {
        if (!mDisposed) {
            mDisposed = true;
            mObservables.dispose();
        }
    }
}
