package org.marketcetera.photon.module.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.dialogs.ErrorDialog;
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
import org.marketcetera.module.MXBeanOperationException;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.photon.internal.module.ui.Messages;
import org.marketcetera.photon.module.IModuleAttributeSupport;
import org.marketcetera.photon.module.ModuleSupport;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Generic preference page for module attributes
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.1.0
 */
@ClassVersion("$Id$")
public abstract class ModuleAttributePreferencePage extends PreferencePage {

	private static final String PLUGIN_ID = "org.marketcetera.photon.module.ui"; //$NON-NLS-1$

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
		GridLayoutFactory.fillDefaults().numColumns(2).generateLayout(composite);
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
	protected final void createTextField(Composite parent, String attributeName, String labelText,
			boolean isPassword) {
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
		mDataBindingContext.bindValue(SWTObservables.observeText(text, SWT.Modify), model, null,
				null);
		if (isPassword) {
			model.setValue(ModuleSupport.getModuleAttributeSupport().getDefaultFor(mURN,
					attributeName));
			text.addFocusListener(new FocusAdapter() {
				@Override
				public void focusGained(FocusEvent e) {
					// help the user replace the password text by selecting it
					text.selectAll();
				}
			});
		} else {
			try {
				model.setValue(ModuleSupport.getModuleAttributeSupport().getModuleAttribute(mURN,
						attributeName));
			} catch (MXBeanOperationException e) {
				e.getI18NBoundMessage().error(this, e);
			}
		}
		mFields.add(new Field(attributeName, model, isPassword));
	}

	@Override
	public boolean performOk() {
		IModuleAttributeSupport moduleAttributeSupport = ModuleSupport.getModuleAttributeSupport();
		MultiStatus status = new MultiStatus(PLUGIN_ID, IStatus.ERROR,
				Messages.MODULE_ATTRIBUTE_PREFERENCE_PAGE_UPDATE_FAILURE_SEE_DETAILS.getText(),
				null);
		for (Field field : mFields) {
			IObservableValue observable = field.getObservable();
			if (observable.getValue() == null) {
				moduleAttributeSupport.removeDefaultFor(mURN, field.getAttributeName());
			} else {
				moduleAttributeSupport.setDefaultFor(mURN, field.getAttributeName(), observable
						.getValue().toString());
			}
			try {
				moduleAttributeSupport.setModuleAttribute(mURN, field.getAttributeName(),
						observable.getValue());
			} catch (MXBeanOperationException e) {
				e.getI18NBoundMessage().error(this, e);
				status.add(new Status(IStatus.ERROR, PLUGIN_ID, e.getLocalizedMessage(), e.getCause()));
			}
			// try to refresh in case something failed
			if (!field.isWriteOnly()) {
				try {
					observable.setValue(moduleAttributeSupport.getModuleAttribute(mURN, field
							.getAttributeName()));
				} catch (MXBeanOperationException e) {
					e.getI18NBoundMessage().error(this, e);
				}
			}
		}
		if (status.getChildren().length > 0) {
			ErrorDialog.openError(getShell(), null,
					Messages.MODULE_ATTRIBUTE_PREFERENCE_PAGE_UPDATE_FAILURE.getText(), status);
		}
		moduleAttributeSupport.flush();
		return true;
	}

	/**
	 * Internal class representing a single field in the preference page.
	 * 
	 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
	 * @version $Id$
	 * @since 1.1.0
	 */
	@ClassVersion("$Id$")
	private static class Field {
		final String mAttributeName;
		final IObservableValue mObservable;
		final boolean mWriteOnly;

		Field(String attributeName, IObservableValue observable) {
			this(attributeName, observable, false);
		}

		Field(String attributeName, IObservableValue observable, boolean writeOnly) {
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
