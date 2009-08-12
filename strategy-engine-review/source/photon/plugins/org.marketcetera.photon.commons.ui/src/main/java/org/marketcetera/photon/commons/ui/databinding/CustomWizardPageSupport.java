package org.marketcetera.photon.commons.ui.databinding;

import org.eclipse.core.databinding.AggregateValidationStatus;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.jface.databinding.dialog.DialogPageSupport;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.wizard.WizardPage;
import org.marketcetera.photon.commons.ui.databinding.RequiredFieldSupport.RequiredStatus;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Handles validation status updates for wizard pages. The framework
 * DialogPageSupport is overzealous about giving red X's. This custom subclasses
 * works nicer with {@link RequiredFieldSupport}.
 * 
 * TODO: unit test
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class CustomWizardPageSupport extends DialogPageSupport {

    private boolean mUIChanged;
    private final AggregateValidationStatus mAllStatus;

    private CustomWizardPageSupport(WizardPage wizardPage,
            DataBindingContext dbc) {
        super(wizardPage, dbc);
        mAllStatus = new AggregateValidationStatus(dbc
                .getValidationStatusProviders(),
                AggregateValidationStatus.MERGED);
    }

    /**
     * Connect the validation result from the given data binding context to the
     * given wizard page. Upon creation, the wizard page support will use the
     * context's validation result to determine whether the page is complete.
     * The page's error message will not be set at this time ensuring that the
     * wizard page does not show an error right away. Upon any validation result
     * change, {@link WizardPage#setPageComplete(boolean)} will be called
     * reflecting the new validation result, and the wizard page's error message
     * will be updated according to the current validation result.
     * 
     * @param wizardPage
     * @param dbc
     * @return an instance of WizardPageSupport
     */
    public static CustomWizardPageSupport create(WizardPage wizardPage,
            DataBindingContext dbc) {
        return new CustomWizardPageSupport(wizardPage, dbc);
    }

    @Override
    protected void handleUIChanged() {
        mUIChanged = true; // needed since uiChanged in superclass is private
        super.handleUIChanged();
    }

    @Override
    protected void handleStatusChanged() {
        if (currentStatus != null
                && currentStatus.getSeverity() == IStatus.ERROR) {
            IStatus status = currentStatus;
            if (currentStatus instanceof RequiredStatus
                    && ((RequiredStatus) currentStatus).isFirst()) {
                Object multi = mAllStatus.getValue();
                if (multi instanceof MultiStatus) {
                    IStatus[] children = ((MultiStatus) multi).getChildren();
                    for (IStatus iStatus : children) {
                        if (!(iStatus instanceof RequiredStatus)
                                || !((RequiredStatus) iStatus).isFirst()) {
                            status = iStatus;
                            break;
                        }
                    }
                }
            }

            if (!(status instanceof RequiredStatus)
                    || !((RequiredStatus) status).isFirst()) {
                getDialogPage().setMessage(null);
                getDialogPage().setErrorMessage(
                        mUIChanged ? status.getMessage() : null);
                if (currentStatusHasException()) {
                    handleStatusException();
                }
            } else {
                getDialogPage().setMessage(null);
                getDialogPage().setErrorMessage(null);
            }
        } else if (currentStatus != null
                && currentStatus.getSeverity() != IStatus.OK) {
            int severity = currentStatus.getSeverity();
            int type;
            switch (severity) {
            case IStatus.OK:
                type = IMessageProvider.NONE;
                break;
            case IStatus.CANCEL:
                type = IMessageProvider.NONE;
                break;
            case IStatus.INFO:
                type = IMessageProvider.INFORMATION;
                break;
            case IStatus.WARNING:
                type = IMessageProvider.WARNING;
                break;
            case IStatus.ERROR:
                type = IMessageProvider.ERROR;
                break;
            default:
                Assert.isTrue(false, "incomplete switch statement"); //$NON-NLS-1$
                return; // unreachable
            }
            getDialogPage().setErrorMessage(null);
            getDialogPage().setMessage(currentStatus.getMessage(), type);
        } else {
            getDialogPage().setMessage(null);
            getDialogPage().setErrorMessage(null);
        }
        boolean pageComplete = true;
        if (currentStatusStale) {
            pageComplete = false;
        } else if (currentStatus != null) {
            pageComplete = !currentStatus.matches(IStatus.ERROR
                    | IStatus.CANCEL);
        }
        ((WizardPage) getDialogPage()).setPageComplete(pageComplete);
    }

    private boolean currentStatusHasException() {
        boolean hasException = false;
        if (currentStatus.getException() != null) {
            hasException = true;
        }
        if (currentStatus instanceof MultiStatus) {
            MultiStatus multiStatus = (MultiStatus) currentStatus;

            for (int i = 0; i < multiStatus.getChildren().length; i++) {
                IStatus status = multiStatus.getChildren()[i];
                if (status.getException() != null) {
                    hasException = true;
                    break;
                }
            }
        }
        return hasException;
    }

}
