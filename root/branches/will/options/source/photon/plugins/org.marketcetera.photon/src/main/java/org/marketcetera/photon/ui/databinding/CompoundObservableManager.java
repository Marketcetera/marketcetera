package org.marketcetera.photon.ui.databinding;

import java.util.Collection;

import org.eclipse.core.databinding.observable.DisposeEvent;
import org.eclipse.core.databinding.observable.IDisposeListener;
import org.eclipse.core.databinding.observable.masterdetail.MasterDetailObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.marketcetera.util.misc.ClassVersion;

import com.google.common.collect.ImmutableList;

/* $License$ */

/**
 * Manages an observable whose value is made up of other child values.
 * Generally, a {@link MasterDetailObservables master/detail observable} is
 * better, but this class is for cases where all children must be set for the
 * master to take a value (e.g. the master observes an immutable object).
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public abstract class CompoundObservableManager<T extends IObservableValue> {

    private final T mParent;
    private ImmutableList<IObservableValue> mChildren;
    private boolean mUpdatingParent;
    private boolean mUpdatingChildren;

    /**
     * Constructor.
     * 
     * @param parent
     *            the parent observable to be managed
     */
    public CompoundObservableManager(T parent) {
        mParent = parent;
    }

    /**
     * Initialize this class with the collection of child observables. This
     * class is responsible for the children and will dispose them when the
     * parent is disposed.
     * 
     * @param children
     *            the child observables
     */
    protected final void init(Collection<? extends IObservableValue> children) {
        mChildren = ImmutableList.copyOf(children);
        mParent.addDisposeListener(new IDisposeListener() {
            @Override
            public void handleDispose(DisposeEvent staleEvent) {
                for (IObservableValue child : mChildren) {
                    child.dispose();
                }
            }
        });
        mParent.addValueChangeListener(new IValueChangeListener() {
            @Override
            public void handleValueChange(ValueChangeEvent event) {
                if (!mUpdatingParent) {
                    mUpdatingChildren = true;
                    updateChildren();
                    mUpdatingChildren = false;
                }
            }
        });
        for (IObservableValue child : mChildren) {
            child.addValueChangeListener(new IValueChangeListener() {
                @Override
                public void handleValueChange(ValueChangeEvent event) {
                    if (!mUpdatingChildren) {
                        mUpdatingParent = true;
                        updateParent();
                        mUpdatingParent = false;
                    }
                }
            });
        }
        mUpdatingChildren = true;
        updateChildren();
        mUpdatingChildren = false;
    }

    /**
     * Get the parent observable.
     * 
     * @return the parent observable
     */
    protected T getParent() {
        return mParent;
    }

    /**
     * Update the child observables since the parent has changed.
     */
    protected abstract void updateChildren();

    /**
     * Update the parent observable since a child has changed.
     */
    protected abstract void updateParent();
}
