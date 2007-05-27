package org.marketcetera.photon.preferences;

import java.io.IOException;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.marketcetera.photon.EclipseUtils;
import org.marketcetera.photon.PhotonPlugin;

public class FIXMessageDetailPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	public static final String COLUMN_FILTER_TEXT = "column.filter.text";

	public static final String FIX_MESSAGE_DETAIL_PREFERENCE = "fix.message.detail";

	public static final String ID = "org.marketcetera.photon.preferences.fixmessagedetailpreference";

	private Combo fixMsgTypeCombo;

	private Text columnFilterText;

	private Text customFixFieldIDText;

	private Button customFixFieldInputButton;

	private FIXMessageFieldColumnChooserEditor fixMsgFieldsChooser;

	public FIXMessageDetailPreferencePage() {
		super(FLAT);
		setPreferenceStore(PhotonPlugin.getDefault().getPreferenceStore());
	}

	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void createFieldEditors() {
		createMsgTypesCombo(getFieldEditorParent());
		createColumnFilterText(getFieldEditorParent());

		fixMsgFieldsChooser = new FIXMessageFieldColumnChooserEditor(FIX_MESSAGE_DETAIL_PREFERENCE,
				"FIX Message Detail Preference", getFieldEditorParent());
		addField(fixMsgFieldsChooser);
		
		createCustomFixFieldIDText(getFieldEditorParent());
		
	}

	private void createMsgTypesCombo(Composite parent) {
		parent.setLayout(new FormLayout());

		Label viewFixMsgTypeLabel = new Label(parent, SWT.NONE);
		viewFixMsgTypeLabel.setText("View: ");

		FormData labelFormData = new FormData();
		labelFormData.left = new FormAttachment(0);
		labelFormData.top = new FormAttachment(0);
		viewFixMsgTypeLabel.setLayoutData(labelFormData);

		fixMsgTypeCombo = new Combo(parent, SWT.BORDER | SWT.READ_ONLY);
		String[] msgTypes = getFixMsgTypes();
		fixMsgTypeCombo.setItems(msgTypes);
		fixMsgTypeCombo.setText(msgTypes[0]);

		FormData comboFormData = new FormData();
		comboFormData.left = new FormAttachment(viewFixMsgTypeLabel, 10);
		comboFormData.top = new FormAttachment(0);
		fixMsgTypeCombo.setLayoutData(comboFormData);
	}

	private void createColumnFilterText(Composite parent) {
		parent.setLayout(new FormLayout());

		Label availableColumnsLabel = new Label(parent, SWT.NONE);
		availableColumnsLabel.setText("Available Columns");

		FormData labelFormData = new FormData();
		labelFormData.left = new FormAttachment(0);
		labelFormData.top = new FormAttachment(fixMsgTypeCombo, 20);
		availableColumnsLabel.setLayoutData(labelFormData);

		Label columnFilterLabel = new Label(parent, SWT.NONE);
		columnFilterLabel.setText("Column Filter: ");

		FormData columnFilterLabelFormData = new FormData();
		columnFilterLabelFormData.left = new FormAttachment(0);
		columnFilterLabelFormData.top = new FormAttachment(
				availableColumnsLabel, 10);
		columnFilterLabel.setLayoutData(columnFilterLabelFormData);

		columnFilterText = new Text(parent, SWT.BORDER);

		FormData filterFormData = new FormData();
		filterFormData.left = new FormAttachment(columnFilterLabel);
		filterFormData.top = new FormAttachment(availableColumnsLabel, 10);
		filterFormData.width = EclipseUtils.getTextAreaSize(columnFilterText,
				"account type account type", 0, 1.0).x;
		columnFilterText.setLayoutData(filterFormData);
	}

	private void createCustomFixFieldIDText(Composite parent) {
		parent.setLayout(new FormLayout());

		Label customFixFieldIDLabel = new Label(parent, SWT.NONE);
		customFixFieldIDLabel.setText("Custom FIX field ID");

		FormData labelFormData = new FormData();
		labelFormData.left = new FormAttachment(0);
		customFixFieldIDLabel.setLayoutData(labelFormData);

		customFixFieldIDText = new Text(parent, SWT.BORDER);

		FormData fieldIDFormData = new FormData();
		fieldIDFormData.left = new FormAttachment(0);
		fieldIDFormData.top = new FormAttachment(customFixFieldIDLabel, 2);
		fieldIDFormData.bottom = new FormAttachment(100);
		fieldIDFormData.width = EclipseUtils.getTextAreaSize(
				customFixFieldIDText, "1000000", 0, 1.0).x;
		customFixFieldIDText.setLayoutData(fieldIDFormData);

		customFixFieldInputButton = new Button(parent, SWT.PUSH);
		customFixFieldInputButton.setText("Add Custom");

		FormData buttonFormData = new FormData();
		buttonFormData.left = new FormAttachment(
				customFixFieldIDText, 5);
		buttonFormData.top = new FormAttachment(
				customFixFieldIDLabel, 2);
		buttonFormData.bottom = new FormAttachment(100);
		customFixFieldInputButton.setLayoutData(buttonFormData);

	}

	@Override
	public boolean performOk() {
		try {
			super.performOk(); 
			((ScopedPreferenceStore) getPreferenceStore()).save(); 
		} catch (IOException e) {
			// TODO: do something
		}
		return super.performOk();
	}

	//cl todo:fake data for mockup
	private String[] getFixMsgTypes() {
		return new String[] { "Open Orders", "Close Orders", "Market Data" };
	}

}
