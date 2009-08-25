package org.marketcetera.photon.commons.ui.databinding;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.IObservableCollection;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.MultiValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.databinding.swt.ISWTObservable;
import org.eclipse.jface.databinding.viewers.IViewerObservable;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.internal.databinding.provisional.fieldassist.ControlDecorationSupport;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.marketcetera.photon.commons.Validate;
import org.marketcetera.photon.commons.ui.CommonsUI;
import org.marketcetera.photon.commons.ui.databinding.DataBindingUtils.CaptureUpdater;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Support class for decorating fields that are required. See
 * {@link #initFor(DataBindingContext, IObservable, String, Binding)} for use
 * cases.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@SuppressWarnings("restriction")
@ClassVersion("$Id$")
public final class RequiredFieldSupport {

    /**
     * Initialized required field UI for the given target observable. Two use
     * cases are supported:
     * <ol>
     * <li>String based {@link IObservableValue} - when the String value is the
     * empty string (""), an error status with a message such as
     * "&lt;description> is required" will be generated.</li>
     * <li>{@link IObservableCollection} - when the collection is empty, an
     * error status with a message such as "At least one &lt;description> must
     * be selected" will be generated.</li>
     * </ol>
     * The actual text of the error is subject to localization.
     * <p>
     * In both cases, if the observable is an {@link ISWTObservable} or
     * {@link IViewerObservable} (or decoration thereof), when the error status
     * is generated, a {@link ControlDecoration} will be added to the
     * observable's control. The decoration will display the
     * {@link FieldDecorationRegistry#DEC_REQUIRED} (black asterisk) icon in the
     * top left corner with the error message as the icon tooltip. The
     * decoration will be accessible from the control using
     * <code>control.getData(DataBindingUtils.CONTROL_DECORATION)</code>.
     * <p>
     * If the optional binding is provided, the binding status will be queried
     * first and the required field validation will only kick in when the
     * binding is ok.
     * 
     * @param context
     *            the databinding context that manages validation status
     * @param target
     *            the observable to validate and decorate
     * @param description
     *            a description of the observable (for error messages)
     * @param binding
     *            a binding that also contributes validation status, can be null
     * @throws IllegalArgumentException
     *             if context, target, or description is null
     * @throws IllegalStateException
     *             if the context validation realm or the target realm is not
     *             {@link Realm#isCurrent() current}
     */
    public static void initFor(DataBindingContext context, IObservable target,
            String description, Binding binding) {
        Validate.notNull(context, "context", //$NON-NLS-1$
                target, "target", //$NON-NLS-1$
                description, "description"); //$NON-NLS-1$
        if (!context.getValidationRealm().isCurrent()) {
            throw new IllegalStateException(
                    "must be called from the validation realm of context"); //$NON-NLS-1$
        }
        if (!target.getRealm().isCurrent()) {
            throw new IllegalStateException(
                    "must be called from the realm of target"); //$NON-NLS-1$
        }
        MultiValidator validator = new RequiredFieldValidator(target,
                description, binding);
        context.addValidationStatusProvider(validator);
        ControlDecorationSupport.create(validator, SWT.LEFT | SWT.TOP, null,
                new RequiredDecorationUpdater());
    }

    /**
     * Updates control decorations. If the status is {@link RequiredStatus},
     * {@link FieldDecorationRegistry#DEC_REQUIRED} will be used for the icon
     * instead of {@link FieldDecorationRegistry#DEC_ERROR}.
     */
    @ClassVersion("$Id$")
    private static final class RequiredDecorationUpdater extends CaptureUpdater {

        @Override
        protected Image getImage(IStatus status) {
            if (status instanceof RequiredStatus) {
                return FieldDecorationRegistry.getDefault().getFieldDecoration(
                        FieldDecorationRegistry.DEC_REQUIRED).getImage();
            }
            return super.getImage(status);
        }
    }

    /**
     * A custom status for indicating a required field is missing.
     */
    @ClassVersion("$Id$")
    public static class RequiredStatus extends Status {

        /**
         * Constructor.
         * 
         * @param message
         *            a human-readable message, localized to the current locale
         */
        public RequiredStatus(String message) {
            super(IStatus.ERROR, CommonsUI.PLUGIN_ID, 0, message, null);
        }
    }

    /**
     * Validates required fields. For {@link IObservableValue}, it validates
     * that the value is not null and not an empty string. For
     * {@link IObservableCollection}, it validates that the collection is not
     * empty.
     * <p>
     * If a binding is provided, the binding status will be queried first and
     * the required field validation will only kick in when the binding is ok.
     */
    @ClassVersion("$Id$")
    private static final class RequiredFieldValidator extends MultiValidator {
        private final IObservable mTargetObservable;
        private final String mDescription;
        private final Binding mBinding;

        /**
         * Constructor.
         * 
         * @param observable
         *            the observable to validate
         * @param description
         *            the description of the field for error messages
         * @param binding
         *            a binding that also contributes validation status, can be
         *            null
         * @throws IllegalArgumentException
         *             if observable or description is null
         */
        public RequiredFieldValidator(IObservable observable,
                String description, Binding binding) {
            Validate.notNull(observable, "observable", //$NON-NLS-1$
                    description, "description"); //$NON-NLS-1$
            mTargetObservable = observable;
            mDescription = description;
            mBinding = binding;
        }

        @Override
        protected IStatus validate() {
            // first query observables to ensure they are tracked
            String message = null;
            if (mTargetObservable instanceof IObservableValue) {
                final Object value = ((IObservableValue) mTargetObservable)
                        .getValue();
                if (value == null || String.valueOf(value).isEmpty()) {
                    message = Messages.REQUIRED_FIELD_SUPPORT_MISSING_VALUE
                            .getText(mDescription);
                }
            } else if (mTargetObservable instanceof IObservableCollection) {
                if (((IObservableCollection) mTargetObservable).isEmpty()) {
                    message = Messages.REQUIRED_FIELD_SUPPORT_MISSING_COLLECTION
                            .getText(mDescription);
                }
            }
            // even if the field is missing, prefer the binding status
            if (mBinding != null) {
                final IStatus status = (IStatus) mBinding.getValidationStatus()
                        .getValue();
                if (!status.isOK()) {
                    return status;
                }
            }
            // binding is null or its status is ok so return result of
            // observable validation
            if (message == null) {
                return Status.OK_STATUS;
            } else {
                return new RequiredStatus(message);
            }
        }
    }
}
