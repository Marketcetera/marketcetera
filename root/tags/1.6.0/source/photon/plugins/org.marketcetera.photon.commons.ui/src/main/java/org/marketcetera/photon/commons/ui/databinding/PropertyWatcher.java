package org.marketcetera.photon.commons.ui.databinding;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Set;

import org.eclipse.core.databinding.ObservablesManager;
import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.map.IMapChangeListener;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.map.MapChangeEvent;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.property.value.IValueProperty;
import org.marketcetera.photon.commons.Validate;
import org.marketcetera.util.misc.ClassVersion;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/* $License$ */

/**
 * Tracks changes to value properties on objects. An instance is initialized
 * with a static list of {@link IValueProperty} that define the properties to
 * watch. When an {@link IObservableSet} is {@link #watch(IObservableSet)
 * watched}, the {@link IPropertiesChangedListener} supplied in the constructor
 * will be notified via {@link #propertiesChanged(ImmutableSet)} every time one
 * or more of the properties changes for items in the set.
 * <p>
 * Currently this only supports value properties and not list properties.
 * <p>
 * This class is thread safe. {@link #watch(IObservableSet)} and
 * {@link #dispose()} can only be called from a single thread that has a default
 * realm while that realm is {@link Realm#isCurrent() current}. The methods will
 * throw an exception otherwise.
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

    /**
     * Captures the realm to which this object is confined. Thread safety
     * provided by thread confinement.
     */
    private Realm mRealm;

    /**
     * Manages observables that need to be disposed with this object. Thread
     * safety provided by thread confinement.
     */
    private ObservablesManager mObservables;

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
     * @param listener
     *            the listener that will be notified when watched properties
     *            change
     * @throws IllegalArgumentException
     *             if properties is null, empty, or has null elements, or if
     *             listener is null
     */
    public PropertyWatcher(Collection<? extends IValueProperty> properties,
            IPropertiesChangedListener listener) {
        Validate.nonNullElements(properties, "properties");
        Validate.notNull(listener, "listener"); //$NON-NLS-1$
        mProperties = ImmutableList.copyOf(properties);
        mListener = listener;
    }

    /**
     * Tracks the provided set of elements. Tracking will not stop until this
     * PropertyWatcher is {@link #dispose() disposed}.
     * <p>
     * This can be called multiple times. If called multiple times with sets
     * that overlap, the listener might be notified multiple times when changes
     * occur.
     * <p>
     * Although not validated, each element in the dynamic elements set is
     * expected to be non-null and an {@link IObservable} that supports the
     * properties being watched. Such observables should also be on the default
     * realm.
     * 
     * @param elements
     *            elements to track
     * @throws IllegalStateException
     *             if called from a thread without a default realm
     * @throws IllegalStateException
     *             if called when the default realm is not current
     * @throws IllegalStateException
     *             if called multiple times from different threads
     * @throws IllegalStateException
     *             if the PropertyWatcher has been disposed
     * @throws IllegalArgumentException
     *             if elements is null or is not on the default realm
     */
    public synchronized void watch(IObservableSet elements) {
        Realm realm = Realm.getDefault();
        if (realm == null) {
            throw new IllegalStateException(
                    "must be called from a thread with a default realm"); //$NON-NLS-1$
        }
        if (!realm.isCurrent()) {
            throw new IllegalStateException(
                    "must be called from the default realm"); //$NON-NLS-1$
        }
        if (mRealm != realm) {
            if (mRealm == null) {
                mRealm = realm;
                mObservables = new ObservablesManager();
            } else {
                throw new IllegalStateException(MessageFormat.format(
                        "called from invalid realm [0], expected [1]", //$NON-NLS-1$
                        realm, mRealm));
            }
        }
        if (mDisposed) {
            throw new IllegalStateException(
                    "PropertyWatcher has already been disposed"); //$NON-NLS-1$
        }
        Validate.notNull(elements, "elements"); //$NON-NLS-1$
        Realm elementsRealm = elements.getRealm();
        if (elementsRealm != mRealm) {
            throw new IllegalArgumentException(MessageFormat.format(
                    "elements is on an invalid realm [0], expected [1]", //$NON-NLS-1$
                    elementsRealm, mRealm));
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
            if (mRealm != null) {
                // dispose on the realm so dispose listeners fire on the realm
                mRealm.exec(new Runnable() {
                    @Override
                    public void run() {
                        mObservables.dispose();
                    }
                });
            }
        }
    }
}
