package org.marketcetera.photon.module.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.photon.module.IModuleAttributeSupport;
import org.marketcetera.photon.module.ModuleSupport;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Generic preference page for module attributes
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public abstract class ModuleAttributePreferencePage extends PreferencePage {

	private DataBindingContext mDataBindingContext;

	private final List<Field> mFields = new ArrayList<Field>();

	private final ModuleURN mURN;

	/**
	 * Constructor.
	 * 
	 * @param urn
	 *            the ModuleURN of the module being edited
	 */
	public ModuleAttributePreferencePage(ModuleURN urn) {
		mURN = urn;
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setFont(parent.getFont());
		mDataBindingContext = new DataBindingContext();
		createFields(composite);
		GridLayoutFactory.fillDefaults().numColumns(2)
				.generateLayout(composite);
		return composite;
	}

	protected abstract void createFields(Composite parent);

	/**
	 * Creates a text field in the UI.
	 * 
	 * @param parent
	 *            the parent composite
	 * @param attributeName
	 *            the module's MBean attribute name
	 * @param labelText
	 *            the label for the attribute
	 * @param isPassword
	 *            whether a password (masked) field should be created
	 */
	protected final void createTextField(Composite parent,
			String attributeName, String labelText, boolean isPassword) {
		Font font = parent.getFont();
		Label label = new Label(parent, SWT.LEFT);
		label.setFont(font);
		label.setText(labelText);
		int style = SWT.BORDER | SWT.SINGLE;
		if (isPassword) {
			style |= SWT.PASSWORD;
		}
		final Text text = new Text(parent, style);
		text.setFont(font);
		WritableValue model = WritableValue.withValueType(String.class);
		mDataBindingContext.bindValue(SWTObservables.observeText(text,
				SWT.Modify), model, null, null);
		if (isPassword) {
			// bogus text as a place holder since the actual value cannot be
			// displayed
			text.setText("password"); //$NON-NLS-1$
			text.addFocusListener(new FocusAdapter() {
				@Override
				public void focusGained(FocusEvent e) {
					// help the user replace the password text by selecting it
					text.selectAll();
				}
			});
		} else {
			model.setValue(ModuleSupport.getModuleAttributeSupport()
					.getModuleAttribute(mURN, attributeName));
		}
		mFields.add(new Field(attributeName, model, isPassword));
	}

	@Override
	public boolean performOk() {
		IModuleAttributeSupport moduleAttributeSupport = ModuleSupport
				.getModuleAttributeSupport();
		for (Field field : mFields) {
			IObservableValue observable = field.getObservable();
			moduleAttributeSupport.setModuleAttribute(mURN, field
					.getAttributeName(), observable.getValue());
			if (observable.getValue() == null) {
				moduleAttributeSupport.removeDefaultFor(mURN, field
						.getAttributeName());
			} else {
				moduleAttributeSupport.setDefaultFor(mURN, field
						.getAttributeName(), observable.getValue().toString());
			}
			// refresh in case something failed
			if (!field.isWriteOnly()) {
				observable.setValue(moduleAttributeSupport.getModuleAttribute(
						mURN, field.getAttributeName()));
			}
		}
		moduleAttributeSupport.flush();
		return true;
	}

	/**
	 * Internal class representing a single field in the preference page.
	 * 
	 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
	 * @version $Id$
	 * @since $Release$
	 */
	@ClassVersion("$Id$")
	private static class Field {
		final String mAttributeName;
		final IObservableValue mObservable;
		final boolean mWriteOnly;

		Field(String attributeName, IObservableValue observable) {
			this(attributeName, observable, false);
		}

		Field(String attributeName, IObservableValue observable,
				boolean writeOnly) {
			mAttributeName = attributeName;
			mObservable = observable;
			mWriteOnly = writeOnly;
		}

		String getAttributeName() {
			return mAttributeName;
		}

		IObservableValue getObservable() {
			return mObservable;
		}

		boolean isWriteOnly() {
			return mWriteOnly;
		}
	}

	@Override
	public void dispose() {
		if (mDataBindingContext != null) {
			mDataBindingContext.dispose();
		}
	}
}
