package org.marketcetera.photon.preferences;

import java.io.IOException;

import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.photon.Application;
import org.marketcetera.quickfix.ConnectionConstants;

@ClassVersion("$Id$")
public class JMSPreferencePage extends FieldEditorPreferencePage implements
                                                                 IWorkbenchPreferencePage {

    private ScopedPreferenceStore mPreferences;

    public JMSPreferencePage() {
        super(GRID);
        mPreferences = new ScopedPreferenceStore(new ConfigurationScope(), Application.PLUGIN_ID);
        setPreferenceStore(mPreferences);
    }

    public void init(IWorkbench workbench) {
        // TODO Auto-generated method stub

    }

    protected void createFieldEditors() {
        StringFieldEditor stringEditor = new StringFieldEditor(
        		ConnectionConstants.JMS_CONNECTION_FACTORY_KEY, "Connection factory name",
                getFieldEditorParent()
                );
        addField(stringEditor);
        stringEditor = new StringFieldEditor(
                ConnectionConstants.JMS_CONTEXT_FACTORY_KEY, "Context factory name",
                getFieldEditorParent()
                );
        addField(stringEditor);
        stringEditor = new StringFieldEditor(
                ConnectionConstants.JMS_INCOMING_TOPIC_KEY, "Incoming topic name",
                getFieldEditorParent()
                );
        addField(stringEditor);
        stringEditor = new StringFieldEditor(
                ConnectionConstants.JMS_OUTGOING_QUEUE_KEY, "Outgoing queue name",
                getFieldEditorParent()
                );
        addField(stringEditor);
        stringEditor = new StringFieldEditor(
                ConnectionConstants.JMS_URL_KEY, "Server URL",
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
