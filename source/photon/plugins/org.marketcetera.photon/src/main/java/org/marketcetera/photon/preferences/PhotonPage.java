package org.marketcetera.photon.preferences;

import java.io.IOException;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.marketcetera.photon.Messages;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.PhotonPreferences;

public class PhotonPage
    extends FieldEditorPreferencePage
    implements IWorkbenchPreferencePage, Messages
{

	public static final String LOG_LEVEL_VALUE_ERROR = "error"; //$NON-NLS-1$
	public static final String LOG_LEVEL_VALUE_WARN = "warn"; //$NON-NLS-1$
	public static final String LOG_LEVEL_VALUE_INFO = "info"; //$NON-NLS-1$
	public static final String LOG_LEVEL_VALUE_DEBUG = "debug"; //$NON-NLS-1$

	public PhotonPage() {
		super(GRID);
		setPreferenceStore(PhotonPlugin.getDefault().getPreferenceStore());
	}

	@Override
	protected void createFieldEditors() {
		RadioGroupFieldEditor editor = new RadioGroupFieldEditor(PhotonPreferences.CONSOLE_LOG_LEVEL,
		                                                         LOG_LEVEL_LABEL.getText(),
		                                                         1,
		                                                         new String[][] { { LOG_LEVEL_ERROR_LABEL.getText(),
		                                                                            LOG_LEVEL_VALUE_ERROR },
		                                                                          { LOG_LEVEL_WARN_LABEL.getText(),
		                                                                            LOG_LEVEL_VALUE_WARN },
		                                                                          { LOG_LEVEL_INFO_LABEL.getText(),
		                                                                            LOG_LEVEL_VALUE_INFO },
		                                                                          { LOG_LEVEL_DEBUG_LABEL.getText(),
		                                                                            LOG_LEVEL_VALUE_DEBUG }},
		                                                         getFieldEditorParent(),
		                                                         true);
		addField(editor);
	}

	public void init(IWorkbench workbench)
	{
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
