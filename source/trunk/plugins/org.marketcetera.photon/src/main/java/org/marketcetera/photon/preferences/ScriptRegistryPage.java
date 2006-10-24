package org.marketcetera.photon.preferences;

import java.io.IOException;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.marketcetera.photon.Application;


/**
 * "Script Registry" preference page.
 *  
 * @author gmiller
 * @author andrei@lissovski.org
 */
public class ScriptRegistryPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	private static final String SCRIPT_REGISTRY_PREFERENCE = "script.registry";

	
	public ScriptRegistryPage() {
		super(GRID);
        setPreferenceStore(Application.getPreferenceStore());
	}
	
    public void init(IWorkbench workbench) {
        // TODO Auto-generated method stub
    }

    @Override
	protected void createFieldEditors() {
		Composite fieldEditorParent = getFieldEditorParent();
		ScriptRegistryMapEditor mapEditor = 
			new ScriptRegistryMapEditor(SCRIPT_REGISTRY_PREFERENCE, "Registered scripts", fieldEditorParent);
		addField(mapEditor);
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
