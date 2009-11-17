package org.marketcetera.photon.commons.ui.databinding;

import org.eclipse.core.databinding.AggregateValidationStatus;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.ObservablesManager;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.preference.PreferencePage;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * A {@link PreferencePage} that provides a {@link DataBindingContext} and an
 * {@link ObservablesManager} for convenience. Additionally, the page will have
 * its valid state bound to the data binding context such that it will only be
 * valid if the data binding context aggregate validation state is ok.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public abstract class DataBindingPreferencePage extends PreferencePage {

    private final DataBindingContext mDataBindingContext;
    private final ObservablesManager mObservablesManager;

    /**
     * Constructor.
     */
    public DataBindingPreferencePage() {
        mDataBindingContext = new DataBindingContext();
        mObservablesManager = new ObservablesManager();
        final AggregateValidationStatus agg = new AggregateValidationStatus(
                mDataBindingContext, AggregateValidationStatus.MAX_SEVERITY);
        mObservablesManager.addObservable(agg);
        agg.addValueChangeListener(new IValueChangeListener() {
            @Override
            public void handleValueChange(ValueChangeEvent event) {
                setValid(((IStatus) event.diff.getNewValue()).isOK());
            }
        });
    }

    /**
     * Returns the data binding context.
     * 
     * @return the data binding context
     */
    protected DataBindingContext getDataBindingContext() {
        return mDataBindingContext;
    }

    /**
     * Returns the observables manager.
     * 
     * @return the observables manager
     */
    protected ObservablesManager getObservablesManager() {
        return mObservablesManager;
    }

    @Override
    public void dispose() {
        super.dispose();
        // dispose the ObservablesManager first due to http://bugs.eclipse.org/287247
        mObservablesManager.dispose();
        mDataBindingContext.dispose();
    }

}
