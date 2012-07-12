package org.marketcetera.photon.commons.ui.databinding;

import java.util.ArrayList;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.DisposeEvent;
import org.eclipse.core.databinding.observable.IDisposeListener;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.marketcetera.photon.commons.Validate;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Utilities to proxy observables to a different realm.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public class ProxyObservables {

    /**
     * Returns an observable list that proxies on the current realm for the list
     * passed in. The original list will be returned if its realm is the current
     * realm.
     * <p>
     * Warning! As soon as this method begins, the original list can no longer
     * be disposed except on the proxy realm. If the returned proxy is disposed
     * (also must be on the proxy realm), then the original list can once again
     * be safely disposed in its own realm. This is an unfortunate consequence
     * of the usage of a {@link DataBindingContext} which is limited in that its
     * bound observables can only be disposed from its realm.
     * 
     * @param originalList
     *            the list to proxy
     * @return the proxy list
     * @throws IllegalArgumentException
     *             if original is null
     * @throws IllegalStateException
     *             if this thread has no default realm
     * @throws IllegalStateException
     *             if the default realm is not the current realm
     */
    public static IObservableList proxyList(IObservableList originalList) {
        Realm realm = Realm.getDefault();
        if (realm == null) {
            throw new IllegalStateException(
                    "this method requires a default realm"); //$NON-NLS-1$
        }
        return proxyList(realm, originalList);
    }

    /**
     * Returns an observable list that proxies on the given realm for the list
     * passed in. The original list will be returned if its realm is the alread
     * the provided realm.
     * <p>
     * Warning! As soon as this method begins, the original list can no longer
     * be disposed except on the proxy realm. If the returned proxy is disposed
     * (also must be on the proxy realm), then the original list can once again
     * be safely disposed in its own realm. This is an unfortunate consequence
     * of the usage of a {@link DataBindingContext} which is limited in that its
     * bound observables can only be disposed from its realm.
     * 
     * @param realm
     *            the realm on which to proxy
     * @param originalList
     *            the list to proxy
     * @return the proxy list
     * @throws IllegalArgumentException
     *             if realm or original is null
     * @throws IllegalStateException
     *             if the provided realm is not the current realm
     */
    public static IObservableList proxyList(Realm realm,
            final IObservableList originalList) {
        Validate.notNull(realm, "realm", //$NON-NLS-1$
                originalList, "originalList"); //$NON-NLS-1$
        if (!realm.isCurrent()) {
            throw new IllegalStateException(
                    "must be called from the proxy realm"); //$NON-NLS-1$
        }
        if (realm.equals(originalList.getRealm())) {
            return originalList;
        }
        final WritableList list = new WritableList(realm,
                new ArrayList<Object>(), originalList.getElementType());
        final DataBindingContext dbc = new DataBindingContext(realm);
        /*
         * A dispose listener is added to the original list to clean up the
         * proxy list and data binding context. But this will only work if the
         * original is disposed from the proxy realm.
         */
        final IDisposeListener originalDisposeListener = new IDisposeListener() {
            @Override
            public void handleDispose(DisposeEvent staleEvent) {
                if (!list.isDisposed()) {
                    list.dispose();
                }
            }
        };
        originalList.addDisposeListener(originalDisposeListener);
        list.addDisposeListener(new IDisposeListener() {
            @Override
            public void handleDispose(DisposeEvent staleEvent) {
                originalList.removeDisposeListener(originalDisposeListener);
                dbc.dispose();
            }
        });
        dbc.bindList(list, originalList);
        return list;
    }

    private ProxyObservables() {
        throw new AssertionError("non-instantiable"); //$NON-NLS-1$
    }
}