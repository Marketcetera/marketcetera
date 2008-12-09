package org.marketcetera.photon.internal.strategy.ruby;

import java.util.regex.Pattern;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.databinding.validation.MultiValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.wizard.WizardPageSupport;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.marketcetera.photon.internal.strategy.Messages;
import org.marketcetera.photon.internal.strategy.ui.StrategyUI;
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
final class NewRubyStrategyWizardPage extends WizardPage {

	static final String NAMESPACE_DELIMITER = "::"; //$NON-NLS-1$

	private final IObservableValue mContainerName = WritableValue
			.withValueType(String.class);

	private final IObservableValue mClassName = WritableValue
			.withValueType(String.class);

	private final ISelection mSelection;

	/**
	 * Constructor.
	 * 
	 * @param selection
	 *            current workbench selection to initialize the container
	 */
	public NewRubyStrategyWizardPage(ISelection selection) {
		super("page", Messages.NEW_RUBY_STRATEGY_TITLE.getText(), null); //$NON-NLS-1$
		setDescription(Messages.NEW_RUBY_STRATEGY_DESCRIPTION.getText());
		this.mSelection = selection;
	}

	@Override
	public void createControl(Composite parent) {
		initializeDialogUnits(parent);
		DataBindingContext dbc = new DataBindingContext();
		Font font = parent.getFont();

		Composite container = new Composite(parent, SWT.NONE);
		container.setFont(font);

		Label label = new Label(container, SWT.NONE);
		label.setText(StrategyUI
				.formatLabel(Messages.NEW_RUBY_STRATEGY_CONTAINER_LABEL));

		Text containerText = new Text(container, SWT.BORDER);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).applyTo(
				containerText);
		dbc.bindValue(SWTObservables.observeText(containerText, SWT.Modify),
				mContainerName, null, null);

		Button button = new Button(container, SWT.PUSH);
		button.setText(Messages.NEW_RUBY_STRATEGY_BROWSE_LABEL.getText());
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleBrowse();
			}
		});
		label = new Label(container, SWT.NULL);
		label.setText(StrategyUI
				.formatLabel(Messages.NEW_RUBY_STRATEGY_CLASS_NAME_LABEL));

		Text classNameText = new Text(container, SWT.BORDER);
		dbc.bindValue(SWTObservables.observeText(classNameText, SWT.Modify),
				mClassName, null, null);

		dbc.addValidationStatusProvider(new NotBlankValidator(mContainerName,
				Messages.NEW_RUBY_STRATEGY_CONTAINER_LABEL.getText()));
		dbc.addValidationStatusProvider(new NotBlankValidator(mClassName,
				Messages.NEW_RUBY_STRATEGY_CLASS_NAME_LABEL.getText()));
		dbc.addValidationStatusProvider(new ClassNameValidator());

		initialize();

		WizardPageSupport.create(this, dbc);

		if (mContainerName.getValue() != null) {
			classNameText.setFocus();
		}

		GridLayoutFactory.swtDefaults().numColumns(3).spacing(
				convertHorizontalDLUsToPixels(4),
				convertVerticalDLUsToPixels(4)).generateLayout(container);
		setControl(container);
	}

	/**
	 * Tests if the current workbench selection is a suitable container to use.
	 */
	private void initialize() {
		if (mSelection != null && mSelection.isEmpty() == false
				&& mSelection instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection) mSelection;
			if (ssel.size() > 1)
				return;
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
				getShell(), ResourcesPlugin.getWorkspace().getRoot(), false,
				Messages.NEW_RUBY_STRATEGY_CONTAINER_SELECTION_INSTRUCTIONS
						.getText());
		if (dialog.open() == ContainerSelectionDialog.OK) {
			Object[] result = dialog.getResult();
			if (result.length == 1) {
				mContainerName.setValue(((Path) result[0]).toString());
			}
		}
	}

	String getContainerName() {
		return (String) mContainerName.getValue();
	}

	String getClassName() {
		return (String) mClassName.getValue();
	}

	/**
	 * Validator that fails if the class name is invalid.
	 * 
	 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
	 * @version $Id$
	 * @since 1.0.0
	 */
	@ClassVersion("$Id$")
	private final class ClassNameValidator extends MultiValidator {

		/**
		 * Start with a capital letter, followed only by letters, numbers, and
		 * underscores.
		 */
		private final Pattern CLASS_NAME_PATTERN = Pattern
				.compile("^[A-Z]\\w*"); //$NON-NLS-1$
		
		@Override
		public IStatus validate() {
			String string = (String) mClassName.getValue();
			if (!isValid(string)) {
				return ValidationStatus
						.error(Messages.NEW_RUBY_STRATEGY_INVALID_CLASS_NAME
								.getText());
			}
			return ValidationStatus.ok();
		}

		private boolean isValid(String className) {
			if (className == null || className.length() == 0)
				return false;
			int namespaceDelimeterIndex = className
					.indexOf(NAMESPACE_DELIMITER);
			if (namespaceDelimeterIndex != -1) {
				return isValid(className.substring(0, namespaceDelimeterIndex))
						&& isValid(className
								.substring(namespaceDelimeterIndex + 2));
			}
			return CLASS_NAME_PATTERN.matcher(className).matches();
		}
	}
}