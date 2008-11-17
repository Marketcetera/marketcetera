package org.marketcetera.photon.internal.strategy.ruby;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.databinding.validation.MultiValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.wizard.WizardPageSupport;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.marketcetera.photon.internal.strategy.Messages;
import org.marketcetera.photon.internal.strategy.StrategyManager;
import org.marketcetera.photon.internal.strategy.ui.StrategyUI;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Page that collects a class name and a human readable display name for the new
 * strategy.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
final class RegisterRubyStrategyWizardPage extends WizardPage {

	private final String mFile;

	private final IObservableValue mName = WritableValue.withValueType(String.class);

	private final IObservableValue mClassName = WritableValue
			.withValueType(String.class);

	/**
	 * Returns the display name provided by the user.
	 * 
	 * @return the display name provided by the user
	 */
	String getDisplayName() {
		return (String) mName.getValue();
	}

	/**
	 * Returns the class name provided by the user.
	 * 
	 * @return the class name provided by the user
	 */
	String getClassName() {
		return (String) mClassName.getValue();
	}

	/**
	 * Constructor.
	 * 
	 * @param file the file name of the script that will be registered 
	 */
	RegisterRubyStrategyWizardPage(String file) {
		super("page", Messages.REGISTER_RUBY_STRATEGY_TITLE.getText(), null); //$NON-NLS-1$
		setDescription(Messages.REGISTER_RUBY_STRATEGY_DESCRIPTION.getText());
		mFile = file;
	}

	@Override
	public void createControl(Composite parent) {
		DataBindingContext dbc = new DataBindingContext();

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setFont(parent.getFont());

		Text file = StrategyUI.createFileText(composite);
		file.setText(mFile);

		final Text className = StrategyUI.createClassNameText(composite, false);
		dbc.bindValue(SWTObservables.observeText(className, SWT.Modify),
				mClassName, null, null);

		final Text name = StrategyUI.createDisplayNameText(composite);
		dbc.bindValue(SWTObservables.observeText(name, SWT.Modify), mName,
				null, null);

		composite.setTabList(new Control[] { className, name });

		mClassName.addValueChangeListener(new IValueChangeListener() {

			boolean mSynchronizing;

			@Override
			public void handleValueChange(ValueChangeEvent event) {
				String currentName = name.getText();
				String oldValue = (String) event.diff.getOldValue();
				if (StringUtils.isBlank(oldValue)
						&& StringUtils.isBlank(currentName)) {
					mSynchronizing = true;
				} else if (!ObjectUtils.equals(currentName, event.diff
						.getOldValue())) {
					mSynchronizing = false;
				}
				if (mSynchronizing) {
					mName.setValue(event.diff.getNewValue());
				}
			}
		});

		dbc.addValidationStatusProvider(new NotBlankValidator(mClassName,
				Messages.STRATEGYUI_CLASS_LABEL.getText()));
		dbc.addValidationStatusProvider(new NotBlankValidator(mName,
				Messages.STRATEGYUI_DISPLAY_NAME_LABEL.getText()));
		dbc.addValidationStatusProvider(new UniqueNameValidator());

		WizardPageSupport.create(this, dbc);
		GridLayoutFactory.swtDefaults().numColumns(2).generateLayout(composite);
		setControl(composite);
	}

	/**
	 * Validator that fails if the display name is not unique.
	 * 
	 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
	 * @version $Id$
	 * @since $Release$
	 */
	@ClassVersion("$Id$")
	private final class UniqueNameValidator extends MultiValidator {
		@Override
		public IStatus validate() {
			String string = (String) mName.getValue();
			if (!StrategyManager.getCurrent().isUniqueName(string)) {
				return ValidationStatus
						.error(Messages.REGISTER_RUBY_STRATEGY_NAME_NOT_UNIQUE
								.getText(string));
			}
			return ValidationStatus.ok();
		}
	}

}