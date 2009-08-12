package org.marketcetera.photon.commons.ui.databinding;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.ObservablesManager;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.emf.databinding.EMFObservables;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Utilities for using JFace data binding.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class DataBindingUtils {

    /**
     * Convenience method that binds the target to the model with
     * {@link UpdateStrategyFactory#createEMFUpdateValueStrategyWithEmptyStringToNull()}
     * as the target to model update value strategy.
     * 
     * @param dataBindingContext
     *            the data binding context
     * @param targetObservable
     *            the target observable
     * @param modelObservable
     *            the model observable
     */
    public static void bindValue(DataBindingContext dataBindingContext,
            IObservableValue targetObservable, IObservableValue modelObservable) {
        dataBindingContext.bindValue(targetObservable, modelObservable,
                UpdateStrategyFactory
                        .createEMFUpdateValueStrategyWithEmptyStringToNull(),
                null);
    }

    /**
     * Convenience method that binds the target to the model with
     * {@link UpdateStrategyFactory#createEMFUpdateValueStrategyWithEmptyStringToNull()}
     * as the target to model update value strategy, and initializes the target
     * as a required field using
     * {@link RequiredFieldSupport#initFor(DataBindingContext, org.eclipse.core.databinding.observable.IObservable, String)}
     * .
     * 
     * @param dataBindingContext
     *            the data binding context
     * @param targetObservable
     *            the target observable
     * @param modelObservable
     *            the model observable
     * @param description
     *            the field description
     */
    public static void bindRequiredField(DataBindingContext dataBindingContext,
            IObservableValue targetObservable,
            IObservableValue modelObservable, String description) {
        bindValue(dataBindingContext, targetObservable, modelObservable);
        RequiredFieldSupport.initFor(dataBindingContext, targetObservable,
                description);
    }

    /**
     * Convenience method to create an observable and add it to the manager.
     * 
     * @param manager
     *            the manager to use to track the observable
     * @param object
     *            the object to observe
     * @param feature
     *            the feature of the object to observe
     * @return the observable value
     */
    public static IObservableValue observeAndTrack(ObservablesManager manager,
            EObject object, EStructuralFeature feature) {
        final IObservableValue observable = EMFObservables.observeValue(object,
                feature);
        manager.addObservable(observable);
        return observable;
    }

    private DataBindingUtils() {
        throw new AssertionError("non-instantiable"); //$NON-NLS-1$
    }
}
