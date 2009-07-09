package org.marketcetera.photon.preferences;

import java.io.IOException;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.photon.Messages;
import org.marketcetera.photon.PhotonPlugin;

@ClassVersion("$Id$") //$NON-NLS-1$
public class CustomOrderFieldPage
    extends FieldEditorPreferencePage
    implements IWorkbenchPreferencePage, Messages
{

	public static final String CUSTOM_FIELDS_PREFERENCE = "custom.fields"; //$NON-NLS-1$

	public CustomOrderFieldPage() {
		super(GRID);
        setPreferenceStore(PhotonPlugin.getDefault().getPreferenceStore());
	}
	
    public void init(IWorkbench workbench)
    {
    }
	@Override
	protected void createFieldEditors() {
		Composite theFieldEditorParent = getFieldEditorParent();
		CustomFieldsMapEditor pathEditor = new CustomFieldsMapEditor(CUSTOM_FIELDS_PREFERENCE,
		                                                             CUSTOM_FIELDS_LABEL.getText(),
		                                                             theFieldEditorParent);
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
