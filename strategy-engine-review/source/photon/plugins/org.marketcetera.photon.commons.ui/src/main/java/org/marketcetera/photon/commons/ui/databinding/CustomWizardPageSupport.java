package org.marketcetera.photon.commons.ui.databinding;

import java.lang.reflect.Field;

import org.eclipse.core.databinding.AggregateValidationStatus;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.jface.databinding.dialog.DialogPageSupport;
import org.eclipse.jface.wizard.WizardPage;
import org.marketcetera.photon.commons.ui.databinding.RequiredFieldSupport.RequiredStatus;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Handles validation status updates for wizard pages. The framework
 * DialogPageSupport is overzealous about giving red X's. This custom subclasses
 * works nicer with {@link RequiredFieldSupport}.
 * 
 * See <a
 * href="http://bugs.eclipse.org/284908">http://bugs.eclipse.org/284908</a>.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class CustomWizardPageSupport extends DialogPageSupport {

    private AggregateValidationStatus mAllStatus;

    private CustomWizardPageSupport(WizardPage wizardPage,
            DataBindingContext dbc) {
        super(wizardPage, dbc);
    }

    @Override
    protected void init() {
        try {
            Field dbcField = DialogPageSupport.class.getDeclaredField("dbc"); //$NON-NLS-1$
            dbcField.setAccessible(true);
            DataBindingContext dbc = (DataBindingContext) dbcField.get(this);
            mAllStatus = new AggregateValidationStatus(dbc
                    .getValidationStatusProviders(),
                    AggregateValidationStatus.MERGED);
        } catch (Exception e) {
            throw new IllegalStateException(
                    "cannot find dbc field in superclass", e); //$NON-NLS-1$
        }
        super.init();
    }

    /**
     * Connect the validation result from the given data binding context to the
     * given wizard page. Initially and upon any validation result change, the
     * wizard page will be marked incomplete if there are any validation errors.
     * The wizard's error message will be set only if an error is encountered
     * that is not an instance of {@link RequiredStatus}.
     * <p>
     * This does not currently support warnings or info messages. All validation
     * status values other than {@link IStatus#ERROR} are treated as okay,
     * removing the error message and setting the page complete.
     * 
     * @param wizardPage
     *            the wizard page to support
     * @param dbc
     *            the data binding context that provides the validation status
     * @return an instance of CustomeWizardPageSupport
     */
    public static CustomWizardPageSupport create(WizardPage wizardPage,
            DataBindingContext dbc) {
        return new CustomWizardPageSupport(wizardPage, dbc);
    }

    @Override
    protected void handleStatusChanged() {
        if (currentStatus != null
                && currentStatus.getSeverity() == IStatus.ERROR) {
            ((WizardPage) getDialogPage()).setPageComplete(false);
            if (currentStatus instanceof RequiredStatus) {
                Object multi = mAllStatus.getValue();
                if (multi instanceof MultiStatus) {
                    IStatus[] children = ((MultiStatus) multi).getChildren();
                    for (IStatus iStatus : children) {
                        if (!(iStatus instanceof RequiredStatus)) {
                            // found a regular error
                            getDialogPage().setErrorMessage(
                                    iStatus.getMessage());
                            return;
                        }
                    }
                }
            } else {
                // current status is a regular error
                getDialogPage().setErrorMessage(currentStatus.getMessage());
                return;
            }
        } else {
            ((WizardPage) getDialogPage()).setPageComplete(true);
            getDialogPage().setErrorMessage(null);
        }
    }
}
