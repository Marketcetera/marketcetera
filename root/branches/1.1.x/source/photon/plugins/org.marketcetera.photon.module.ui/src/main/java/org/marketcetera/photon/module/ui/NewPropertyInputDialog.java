package org.marketcetera.photon.module.ui;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.marketcetera.photon.internal.module.ui.Messages;
import org.marketcetera.photon.internal.module.ui.ModulePropertiesPreferencePage;
import org.marketcetera.util.misc.ClassVersion;

/**
 * Helper dialog used for adding new properties to
 * {@link ModulePropertiesPreferencePage}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public final class NewPropertyInputDialog extends InputDialog implements Messages {

	/**
	 * Property Value text widget.
	 */
	private Text mPropertyValueText;

	/**
	 * Instance Default check box.
	 */
	private Button mInstanceDefaultButton;

	/**
	 * KEY Provided property value (after OK is pressed).
	 */
	private String mPropertyValue;

	/**
	 * Provided instance default value (after OK is pressed).
	 */
	private boolean mInstanceDefault;

	/**
	 * Indicates whethere the Instance Default check box should be presented.
	 */
	private boolean mAllowInstanceDefault;

	/**
	 * Constructor.
	 * 
	 * @param parentShell
	 *            the parent shell, or <code>null</code> to create a top-level
	 *            shell
	 * @param allowInstanceDefault
	 *            indicates if the Instance Default check box should be
	 *            available
	 */
	public NewPropertyInputDialog(Shell parentShell,
			boolean allowInstanceDefault) {
		super(parentShell, NEW_PROPERTY_DIALOG_TITLE.getText(),
				NEW_PROPERTY_DIALOG_KEY_LABEL.getText(), null,
				new IInputValidator() {
					@Override
					public String isValid(String newText) {
						if (newText.isEmpty())
							return null;
						if (!Character.isJavaIdentifierStart(newText.charAt(0)))
							return NEW_PROPERTY_DIALOG_INVALID_INITIAL_CHARACTER_ERROR
									.getText();
						if (newText.contains(" ")) //$NON-NLS-1$
							return NEW_PROPERTY_DIALOG_CONTAINS_SPACE_ERROR
									.getText();
						for (int i = 1; i < newText.length(); i++) {
							final char c = newText.charAt(i);
							if (!Character.isJavaIdentifierPart(c) && c != '.') {
								return NEW_PROPERTY_DIALOG_INVALID_CHARACTER_ERROR
										.getText(c);
							}
						}
						if (newText.charAt(newText.length() - 1) == '.')
							return NEW_PROPERTY_DIALOG_END_WITH_PERIOD_ERROR.getText();
						return null;
					}
				});
		this.mAllowInstanceDefault = allowInstanceDefault;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		final Composite composite = (Composite) super.createDialogArea(parent);
		Label label = new Label(composite, SWT.WRAP);
		label.setText(NEW_PROPERTY_DIALOG_VALUE_LABEL.getText());
		GridData data = new GridData(SWT.FILL, SWT.CENTER, true, true);
		data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
		label.setLayoutData(data);
		label.setFont(parent.getFont());
		mPropertyValueText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		mPropertyValueText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				true, false));

		if (mAllowInstanceDefault) {
			mInstanceDefaultButton = new Button(composite, SWT.CHECK);
			mInstanceDefaultButton
					.setText(NEW_PROPERTY_DIALOG_INSTANCE_DEFAULTS_LABEL
							.getText());
			GridData data2 = new GridData(SWT.FILL, SWT.CENTER, true, true);
			data2.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
			mInstanceDefaultButton.setLayoutData(data2);
			mInstanceDefaultButton.setFont(parent.getFont());
		}
		return composite;
	}

	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.OK_ID) {
			mPropertyValue = mPropertyValueText.getText();
			mInstanceDefault = mAllowInstanceDefault
					&& mInstanceDefaultButton.getSelection();
		}
		super.buttonPressed(buttonId);
	}

	/**
	 * Returns the property key entered by the user. Should only be called when
	 * this dialog returned OK.
	 * 
	 * @return the provided property key
	 */
	public String getPropertyKey() {
		return super.getValue();
	}

	/**
	 * Returns the property value entered by the user. Should only be called
	 * when this dialog returned OK.
	 * 
	 * @return the provided property value
	 */
	public String getPropertyValue() {
		return mPropertyValue;
	}

	/**
	 * Indicates whether the user selected to make the new property an instance
	 * default. Should only be called when the dialog was configured to show the
	 * Instance Default check box and also returned OK.
	 * 
	 * @return if the property should be an instance default
	 */
	public boolean isInstanceDefault() {
		return mInstanceDefault;
	}
}