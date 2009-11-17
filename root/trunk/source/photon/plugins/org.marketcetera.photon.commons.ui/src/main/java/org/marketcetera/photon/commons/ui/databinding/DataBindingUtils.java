package org.marketcetera.photon.commons.ui.databinding;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.ObservablesManager;
import org.eclipse.core.databinding.ValidationStatusProvider;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.databinding.EMFObservables;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Utilities for using JFace data binding.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public class DataBindingUtils {

    /**
     * Key used for setting {@link ControlDecoration} on a control's
     * {@link Control#getData() data}.
     */
    public static final String CONTROL_DECORATION = "CONTROL_DECORATION"; //$NON-NLS-1$

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
     * {@link RequiredFieldSupport#initFor(DataBindingContext, org.eclipse.core.databinding.observable.IObservable, String, boolean, Binding)}
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
                description, true, null);
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

    /**
     * Initializes ControlDecorationSupport for the provided validation status
     * provider. UI code should use this instead of referencing the provisional
     * Eclipse code directly.
     * 
     * @param provider
     *            the object that will control the state of the control
     *            decoration
     */
    public static void initControlDecorationSupportFor(
            ValidationStatusProvider provider) {
        initControlDecorationSupportFor(provider, SWT.TOP | SWT.LEFT);
    }

    /**
     * Initializes ControlDecorationSupport for the provided validation status
     * provider. UI code should use this instead of referencing the provisional
     * Eclipse code directly.
     * 
     * @param provider
     *            the object that will control the state of the control
     *            decoration
     * @param position
     *            the position of the decoration
     */
    public static void initControlDecorationSupportFor(
            ValidationStatusProvider provider, int position) {
        initControlDecorationSupportFor(provider, position,
                new CaptureUpdater());
    }

    /**
     * Initializes ControlDecorationSupport for the provided validation status
     * provider. UI code should use this instead of referencing the provisional
     * Eclipse code directly.
     * 
     * @param provider
     *            the object that will control the state of the control
     *            decoration
     * @param position
     *            the position of the decoration
     * @param updater
     *            controls the appearance of the {@link ControlDecoration}
     */
    @SuppressWarnings("restriction")
    public static void initControlDecorationSupportFor(
            ValidationStatusProvider provider,
            int position,
            org.eclipse.jface.internal.databinding.provisional.fieldassist.ControlDecorationUpdater updater) {
        org.eclipse.jface.internal.databinding.provisional.fieldassist.ControlDecorationSupport
                .create(provider, position, null, updater);
    }

    /**
     * Attaches the control decoration to the control using the
     * {@value #CONTROL_DECORATION} key. This enables the decoration to be
     * accessed for testing purposes.
     * 
     * @param decoration
     *            the decoration to attach to its control
     */
    public static void attachControlDecoration(ControlDecoration decoration) {
        // add the decoration to the control for testing access
        decoration.getControl().setData(CONTROL_DECORATION, decoration);
    }

    /**
     * Captures the control decoration and attaches it to the control.
     */
    @ClassVersion("$Id$")
    @SuppressWarnings("restriction")
    static class CaptureUpdater
            extends
            org.eclipse.jface.internal.databinding.provisional.fieldassist.ControlDecorationUpdater {

        @Override
        protected void update(ControlDecoration decoration, IStatus status) {
            attachControlDecoration(decoration);
            super.update(decoration, status);
        }
    }

    private DataBindingUtils() {
        throw new AssertionError("non-instantiable"); //$NON-NLS-1$
    }
}
