package org.marketcetera.photon.internal.strategy.engine.ui.workbench;

import org.eclipse.core.databinding.AggregateValidationStatus;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.ObservablesManager;
import org.eclipse.core.databinding.observable.value.ComputedValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.marketcetera.photon.commons.ui.databinding.DataBindingUtils;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Helper dialog for {@link DeployedStrategyConfigurationPropertyPage} used for
 * adding new parameters.
 * 
 * TODO: maybe validate non-empty value if EG-782 is not fixed soon.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
final class NewPropertyInputDialog extends Dialog {

    /**
     * Provided property key (after OK is pressed).
     */
    private final WritableValue mPropertyKey = WritableValue
            .withValueType(String.class);

    /**
     * Provided property value (after OK is pressed).
     */
    private final WritableValue mPropertyValue = new WritableValue("", //$NON-NLS-1$
            String.class);

    private final DataBindingContext mDataBindingContext = new DataBindingContext();

    private final ObservablesManager mObservablesManager = new ObservablesManager();

    /**
     * Constructor.
     * 
     * @param parentShell
     *            the parent shell, or <code>null</code> to create a top-level
     *            shell
     */
    public NewPropertyInputDialog(Shell parentShell) {
        super(parentShell);
        mObservablesManager.addObservablesFromContext(mDataBindingContext,
                true, true);
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(Messages.NEW_PROPERTY_INPUT_DIALOG__TEXT.getText());
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        final Composite composite = (Composite) super.createDialogArea(parent);

        {
            Label label = new Label(composite, SWT.NONE);
            label.setText(Messages.NEW_PROPERTY_INPUT_DIALOG_KEY__LABEL
                    .getText());
        }

        {
            Text keyText = new Text(composite, SWT.SINGLE | SWT.BORDER);
            DataBindingUtils.bindRequiredField(mDataBindingContext,
                    SWTObservables.observeText(keyText, SWT.Modify),
                    mPropertyKey,
                    Messages.NEW_PROPERTY_INPUT_DIALOG_KEY__DESCRIPTION
                            .getText());
        }

        {
            Label label = new Label(composite, SWT.NONE);
            label.setText(Messages.NEW_PROPERTY_INPUT_DIALOG_VALUE__LABEL
                    .getText());
        }

        {
            Text valueText = new Text(composite, SWT.SINGLE | SWT.BORDER);
            mDataBindingContext.bindValue(SWTObservables
                    .observeText(valueText, SWT.Modify), mPropertyValue);
        }

        GridLayoutFactory.swtDefaults().numColumns(2).spacing(10, 10)
                .generateLayout(composite);

        applyDialogFont(composite);
        return composite;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        super.createButtonsForButtonBar(parent);
        final AggregateValidationStatus agg = new AggregateValidationStatus(
                mDataBindingContext, AggregateValidationStatus.MAX_SEVERITY);
        mObservablesManager.addObservable(agg);
        mDataBindingContext.bindValue(SWTObservables
                .observeEnabled(getButton(IDialogConstants.OK_ID)),
                new ComputedValue() {
                    @Override
                    protected Object calculate() {
                        return ((IStatus) agg.getValue()).isOK();
                    }
                });
    }

    /**
     * Returns the property key entered by the user. Should only be called when
     * this dialog returned OK.
     * 
     * @return the provided property key
     */
    public String getPropertyKey() {
        return (String) mPropertyKey.getValue();
    }

    /**
     * Returns the property value entered by the user. Should only be called
     * when this dialog returned OK.
     * 
     * @return the provided property value
     */
    public String getPropertyValue() {
        return (String) mPropertyValue.getValue();
    }

    @Override
    public boolean close() {
        final boolean close = super.close();
        /*
         * Dispose ObservablesManager first, see http://bugs.eclipse.org/287247
         * and o.m.p.commons.ui.databinding.ObservablesManagerCaveatTest.
         */
        mObservablesManager.dispose();
        mDataBindingContext.dispose();
        return close;

    }
}