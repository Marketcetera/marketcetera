package org.marketcetera.photon;

import java.io.IOException;

import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.photon.preferences.UrlFieldEditor;
import org.marketcetera.quickfix.ConnectionConstants;

/**
 * Preference page for editing the parameters of connection to a JMS server.
 * 
 * @author gmiller
 * @author alissovski
 */
@ClassVersion("$Id$")
public class JMSPreferencePage extends FieldEditorPreferencePage implements
                                                                 IWorkbenchPreferencePage {

	public static String ID = "org.marketcetera.photon.preferences.jms";
	
    private ScopedPreferenceStore mPreferences;

	private UrlFieldEditor serverUrlEditor;

    /**
     * Creates a new JMSPreferencePage and a {@link ScopedPreferenceStore} to 
     * serve as the backing store for the preference page.
     */
    public JMSPreferencePage() {
        super(GRID);
        mPreferences = new ScopedPreferenceStore(new ConfigurationScope(), Application.PLUGIN_ID);
        setPreferenceStore(mPreferences);
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
     */
    public void init(IWorkbench workbench) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
     */
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
        serverUrlEditor = new UrlFieldEditor(
		                ConnectionConstants.JMS_URL_KEY, "Server URL",
		                getFieldEditorParent()
		                );
		addField(serverUrlEditor);
    }

    /** 
     * Massages and saves the preferences back into the backing store.
     * 
     * @see org.eclipse.jface.preference.FieldEditorPreferencePage#performOk()
     */
    @Override
    public boolean performOk() {
        try {
        	serverUrlEditor.setStringValue(serverUrlEditor.getStringValue().trim());
        	
        	super.performOk();  // pulls the data out of the page fields and into the preference store. this call does _not_ persist the data to disk.
        	
            mPreferences.save();  // persists the preference store to disk
        } catch (IOException e) {
            //TODO: do something

        	return false;
        }
        
        return true;
    }

}
