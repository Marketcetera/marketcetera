package org.marketcetera.photon.commons.ui.databinding;

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
import org.eclipse.jface.internal.databinding.provisional.fieldassist.ControlDecorationUpdater;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.marketcetera.photon.commons.ui.CommonsUI;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Support class for decorating fields that are required. See
 * {@link #initFor(DataBindingContext, IObservable, String)} for use cases.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@SuppressWarnings("restriction")
@ClassVersion("$Id$")
public final class RequiredFieldSupport {

    /**
     * Key used for setting {@link ControlDecoration} on a control's
     * {@link Control#getData() data}.
     */
    public static final String CONTROL_DECORATION = "CONTROL_DECORATION"; //$NON-NLS-1$

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
     * observable's control. The decoration will show the error message and and
     * icon. The icon will initially be
     * {@link FieldDecorationRegistry#DEC_REQUIRED} (black asterisk) but will
     * change to {@link FieldDecorationRegistry#DEC_ERROR} (red error icon) on
     * subsequent invalidations. This is to allow the user a chance to enter the
     * value before presenting a glaring error icon. The decoration will be
     * accessible from the control using
     * <code>control.getData(RequiredFieldSupport.CONTROL_DECORATION)</code>.
     * 
     * @param context
     *            the databinding context that manages validation status
     * @param target
     *            the observable to validate and decorate
     * @param description
     *            a description of the observable (for error messages)
     * @throws IllegalArgumentException
     *             if context, target, or description is null
     * @throws IllegalArgumentException
     *             if the context validation realm or the target realm is not
     *             {@link Realm#isCurrent() current}
     */
    public static void initFor(DataBindingContext context, IObservable target,
            String description) {
        if (context == null) {
            throw new IllegalArgumentException("context cannot be null"); //$NON-NLS-1$
        }
        if (target == null) {
            throw new IllegalArgumentException("target cannot be null"); //$NON-NLS-1$
        }
        if (description == null) {
            throw new IllegalArgumentException("description cannot be null"); //$NON-NLS-1$
        }
        if (!context.getValidationRealm().isCurrent()) {
            throw new IllegalArgumentException(
                    "must be called from the validation realm of context"); //$NON-NLS-1$
        }
        if (!target.getRealm().isCurrent()) {
            throw new IllegalArgumentException(
                    "must be called from the realm of target"); //$NON-NLS-1$
        }
        MultiValidator validator = new RequiredFieldValidator(target,
                description);
        context.addValidationStatusProvider(validator);
        ControlDecorationSupport.create(validator, SWT.LEFT | SWT.TOP, null,
                new RequiredDecorationUpdater());
    }

    /**
     * Updates control decorations. If the status is {@link RequiredStatus} and
     * no decoration has yet been shown, FieldDecorationRegistry.DEC_REQUIRED
     * will be used instead of FieldDecorationRegistry.DEC_ERROR.
     */
    @ClassVersion("$Id$")
    private static final class RequiredDecorationUpdater extends
            ControlDecorationUpdater {

        @Override
        protected void update(ControlDecoration decoration, IStatus status) {
            // add the decoration to the control for testing access
            decoration.getControl().setData(CONTROL_DECORATION, decoration);
            if (status == null || status.isOK()) {
                decoration.hide();
            } else {
                if (decoration.getImage() == null
                        && status instanceof RequiredStatus) {
                    decoration.setImage(FieldDecorationRegistry.getDefault()
                            .getFieldDecoration(
                                    FieldDecorationRegistry.DEC_REQUIRED)
                            .getImage());
                } else {
                    decoration.setImage(getImage(status));
                }
                decoration.setDescriptionText(getDescriptionText(status));
                decoration.show();
            }
        }
    }

    /**
     * A custom status for indicating a required field is missing.
     */
    @ClassVersion("$Id$")
    public static class RequiredStatus extends Status {

        private final boolean mFirst;

        public RequiredStatus(boolean first, String message) {
            super(IStatus.ERROR, CommonsUI.PLUGIN_ID, 0, message, null);
            mFirst = first;
        }

        /**
         * @return the init
         */
        public boolean isFirst() {
            return mFirst;
        }
    }

    /**
     * Validates required fields. For {@link IObservableValue}, it validates
     * that the
     */
    @ClassVersion("$Id$")
    private static class RequiredFieldValidator extends MultiValidator {
        private final IObservable mTargetObservable;
        private final String mDescription;
        private boolean init;

        public RequiredFieldValidator(IObservable targetObservable,
                String message) {
            mTargetObservable = targetObservable;
            mDescription = message;
        }

        @Override
        protected IStatus validate() {
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
            if (message == null) {
                return Status.OK_STATUS;
            }
            if (!init) {
                init = true;
                return new RequiredStatus(true, message);
            } else {
                return new RequiredStatus(false, message);
            }
        }
    }
}
