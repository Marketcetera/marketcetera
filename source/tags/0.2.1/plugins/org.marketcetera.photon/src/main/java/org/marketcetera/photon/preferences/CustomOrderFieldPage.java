package org.marketcetera.photon.preferences;

import java.io.IOException;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.photon.PhotonPlugin;

@ClassVersion("$Id$")
public class CustomOrderFieldPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public static final String CUSTOM_FIELDS_PREFERENCE = "custom.fields";

	public CustomOrderFieldPage() {
		super(GRID);
        setPreferenceStore(PhotonPlugin.getDefault().getPreferenceStore());
	}
	
    public void init(IWorkbench workbench) {
        // TODO Auto-generated method stub

    }
	@Override
	protected void createFieldEditors() {
		Composite theFieldEditorParent = getFieldEditorParent();
		@SuppressWarnings("unused")
		CustomFieldsMapEditor pathEditor = new CustomFieldsMapEditor(CUSTOM_FIELDS_PREFERENCE, "Custom Fields", theFieldEditorParent);
		addField(pathEditor);
	}

    @Override
    public boolean performOk() {
        try {
            ((ScopedPreferenceStore)getPreferenceStore()).save();  // persists the preference store to disk
        } catch (IOException e) {
            //TODO: do something
        }
        return super.performOk();
    }



}
