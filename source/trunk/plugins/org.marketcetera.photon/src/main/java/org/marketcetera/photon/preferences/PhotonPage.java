package org.marketcetera.photon.preferences;

import java.io.IOException;

import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.marketcetera.photon.Application;

public class PhotonPage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	public static final String LOG_LEVEL_KEY = "photon.log.level";
	public static final String LOG_LEVEL_VALUE_ERROR = "error";
	public static final String LOG_LEVEL_VALUE_WARN = "warn";
	public static final String LOG_LEVEL_VALUE_INFO = "info";
	public static final String LOG_LEVEL_VALUE_DEBUG = "debug";

	public PhotonPage() {
		super(GRID);
		setPreferenceStore(Application.getPreferenceStore());
	}

	@Override
	protected void createFieldEditors() {
		RadioGroupFieldEditor editor = new RadioGroupFieldEditor(
				LOG_LEVEL_KEY, "Console Log Level", 1,
				new String[][] {
						{ "Error (Fewest messages)", LOG_LEVEL_VALUE_ERROR },
						{ "Warn", LOG_LEVEL_VALUE_WARN },
						{ "Info", LOG_LEVEL_VALUE_INFO },
						{ "Debug (Most messages)", LOG_LEVEL_VALUE_DEBUG }}, getFieldEditorParent(), true);
		addField(editor);
	}

	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean performOk() {
        try {
        	super.performOk();  // pulls the data out of the page fields and into the preference store. this call does _not_ persist the data to disk.
        	
            ((ScopedPreferenceStore)getPreferenceStore()).save();  // persists the preference store to disk
        } catch (IOException e) {
        	return false;
        }
        
        return true;
	}

}
