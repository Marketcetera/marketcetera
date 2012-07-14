package org.marketcetera.photon.internal.strategy.ui;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.ObservablesManager;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.databinding.dialog.DialogPageSupport;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.marketcetera.photon.commons.ui.databinding.CustomWizardPageSupport;
import org.marketcetera.photon.commons.ui.databinding.DataBindingUtils;
import org.marketcetera.photon.commons.ui.databinding.RequiredFieldSupport;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * {@link WizardPage} that requests a container and class name for creating a
 * new strategy script.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public class NewStrategyWizardPage extends WizardPage {

    /*
     * unit tested with the AbstractNewStrategyWizardTestBase
     */

    private final IObservableValue mContainerName = WritableValue
            .withValueType(String.class);

    private final IObservableValue mClassName = WritableValue
            .withValueType(String.class);

    private final DataBindingContext mDataBindingContext = new DataBindingContext();

    private final ObservablesManager mObservablesManager = new ObservablesManager();

    private DialogPageSupport mWizardSupport;

    private final IValidator mClassNameValidator;

    /**
     * Constructor.
     * 
     * @param selection
     *            current workbench selection to initialize the container
     * @param title
     *            the page title
     * @param classNameValidator
     *            a validator that checks if strings are valid class names for
     *            the strategy
     */
    public NewStrategyWizardPage(ISelection selection, String title,
            IValidator classNameValidator) {
        super("page", title, null); //$NON-NLS-1$
        setDescription(Messages.NEW_STRATEGY_WIZARD_PAGE_DESCRIPTION.getText());
        initialize(selection);
        mClassNameValidator = classNameValidator;
    }

    @Override
    public void createControl(Composite parent) {
        mWizardSupport = CustomWizardPageSupport.create(this,
                mDataBindingContext);

        initializeDialogUnits(parent);
        Font font = parent.getFont();
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setFont(font);

        Messages.NEW_STRATEGY_WIZARD_PAGE_FOLDER.createLabel(composite);

        {
            Text containerText = new Text(composite, SWT.BORDER);
            GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).applyTo(
                    containerText);
            DataBindingUtils.bindRequiredField(mDataBindingContext,
                    SWTObservables.observeText(containerText, SWT.Modify),
                    mContainerName, Messages.NEW_STRATEGY_WIZARD_PAGE_FOLDER
                            .getRawLabel());
        }

        {
            Button button = new Button(composite, SWT.PUSH);
            button
                    .setText(Messages.NEW_STRATEGY_WIZARD_PAGE_BROWSE_BUTTON__TEXT
                            .getText());
            button.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    handleBrowse();
                }
            });
        }

        Messages.NEW_STRATEGY_WIZARD_PAGE_CLASS_NAME.createLabel(composite);

        {
            Text classNameText = new Text(composite, SWT.BORDER);
            ISWTObservableValue target = SWTObservables.observeText(
                    classNameText, SWT.Modify);
            Binding binding = mDataBindingContext.bindValue(target, mClassName,
                    new UpdateValueStrategy()
                            .setBeforeSetValidator(new IValidator() {
                                @Override
                                public IStatus validate(Object value) {
                                    final String string = (String) value;
                                    if (StringUtils.isEmpty(string)) {
                                        // return ok here so
                                        // RequiredFieldSupport kicks in
                                        return ValidationStatus.ok();
                                    }
                                    return mClassNameValidator.validate(value);
                                }
                            }), null);
            RequiredFieldSupport.initFor(mDataBindingContext, target,
                    Messages.NEW_STRATEGY_WIZARD_PAGE_CLASS_NAME.getRawLabel(),
                    true, binding);

            if (mContainerName.getValue() != null) {
                classNameText.setFocus();
            }
        }

        GridLayoutFactory.swtDefaults().numColumns(3).spacing(
                convertHorizontalDLUsToPixels(8),
                convertVerticalDLUsToPixels(4)).generateLayout(composite);
        setControl(composite);
    }

    /**
     * Tests if the current workbench selection is a suitable container to use.
     * 
     * @param selection
     *            the selection
     */
    private void initialize(ISelection selection) {
        if (selection != null && !selection.isEmpty()
                && selection instanceof IStructuredSelection) {
            IStructuredSelection ssel = (IStructuredSelection) selection;
            Object obj = ssel.getFirstElement();
            if (obj instanceof IResource) {
                IContainer container;
                if (obj instanceof IContainer)
                    container = (IContainer) obj;
                else
                    container = ((IResource) obj).getParent();
                mContainerName.setValue(container.getFullPath().toString());
            }
        }
    }

    /**
     * Uses the standard container selection dialog to choose the new value for
     * the container field.
     */
    private void handleBrowse() {
        ContainerSelectionDialog dialog = new ContainerSelectionDialog(
                getShell(),
                ResourcesPlugin.getWorkspace().getRoot(),
                false,
                Messages.NEW_STRATEGY_WIZARD_PAGE_CONTAINER_SELECTION_INSTRUCTIONS
                        .getText());
        if (dialog.open() == ContainerSelectionDialog.OK) {
            Object[] result = dialog.getResult();
            if (result.length == 1) {
                mContainerName.setValue(((Path) result[0]).toString());
            }
        }
    }

    /**
     * Returns the container value.
     * 
     * @return the container
     */
    public String getContainerName() {
        return (String) mContainerName.getValue();
    }

    /**
     * Returns the class name value.
     * 
     * @return the class name
     */
    public String getClassName() {
        return (String) mClassName.getValue();
    }

    @Override
    public void dispose() {
        super.dispose();
        if (mWizardSupport != null) {
            mWizardSupport.dispose();
        }
        /*
         * Dispose ObservablesManager first, see http://bugs.eclipse.org/287247
         * and o.m.p.commons.ui.databinding.ObservablesManagerCaveatTest.
         */
        mObservablesManager.dispose();
        mDataBindingContext.dispose();

    }
}