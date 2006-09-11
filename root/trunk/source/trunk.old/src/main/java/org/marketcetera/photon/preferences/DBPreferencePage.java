package org.marketcetera.photon.preferences;

import java.io.IOException;

import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.ConfigPropertiesLoader;
import org.marketcetera.photon.Application;

@ClassVersion("$Id$")
public class DBPreferencePage extends FieldEditorPreferencePage implements
                                                                IWorkbenchPreferencePage {

    private ScopedPreferenceStore mPreferences;

    public DBPreferencePage() {
        super(GRID);
        mPreferences = new ScopedPreferenceStore(new ConfigurationScope(), Application.PLUGIN_ID);
        setPreferenceStore(mPreferences);
    }

    public void init(IWorkbench workbench) {
        // TODO Auto-generated method stub

    }

    protected void createFieldEditors() {
        StringFieldEditor stringEditor = new StringFieldEditor(
                ConfigPropertiesLoader.DB_URL_KEY, "Database URL",
                getFieldEditorParent()
                );
        addField(stringEditor);
        stringEditor = new StringFieldEditor(
                ConfigPropertiesLoader.DB_DRIVER_KEY, "Database driver",
                getFieldEditorParent()
                );
        addField(stringEditor);
        stringEditor = new StringFieldEditor(
                ConfigPropertiesLoader.DB_USER_KEY, "Database user",
                getFieldEditorParent()
                );
        addField(stringEditor);
        stringEditor = new StringFieldEditor(
                ConfigPropertiesLoader.DB_PASS_KEY, "Database password",
                getFieldEditorParent()
                );
        addField(stringEditor);
    }

    @Override
    public boolean performOk() {
        try {
            mPreferences.save();
        } catch (IOException e) {
            //TODO: do something
        }
        return super.performOk();
    }


}
