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

/**
 * The preference page for maintaining preferences related to the connection
 * to the JMS-based message queue.  To connect to a specific message queue and
 * access the correct resources, the preferences specify the name of the outgoing
 * queue ({@link ConnectionConstants#JMS_OUTGOING_QUEUE_KEY}), the incoming topic
 * ({@link ConnectionConstants#JMS_INCOMING_TOPIC_KEY}), and the URL of the JMS 
 * server ({@link ConnectionConstants#JMS_URL_KEY}).  In order to allow for different JMS implementations
 * the preferences specify the JNDI name of the connection factory 
 * ({@link ConnectionConstants#JMS_CONNECTION_FACTORY_KEY}), and the class name of 
 * the context factory ({@link ConnectionConstants#JMS_CONTEXT_FACTORY_KEY}).
 * 
 * 
 * @author gmiller
 * @see ConnectionConstants
 */
@ClassVersion("$Id$")
public class JMSPreferencePage extends FieldEditorPreferencePage implements
                                                                 IWorkbenchPreferencePage {

    private ScopedPreferenceStore mPreferences;

    /**
     * Creates a new JMSPreference page, initializing the member ScopedPreferenceStore
     */
    public JMSPreferencePage() {
        super(GRID);
        mPreferences = new ScopedPreferenceStore(new ConfigurationScope(), Application.PLUGIN_ID);
        setPreferenceStore(mPreferences);
    }

    /**
     * Does nothing
     * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
     */
    public void init(IWorkbench workbench) {
    }

    
    /**
	 * Creates the field editor components associated with the JMS connection.
	 * 
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
	 * 
	 * @see ConnectionConstants#JMS_OUTGOING_QUEUE_KEY
	 * @see ConnectionConstants#JMS_INCOMING_TOPIC_KEY
	 * @see ConnectionConstants#JMS_URL_KEY
	 * @see ConnectionConstants#JMS_CONNECTION_FACTORY_KEY
	 * @see ConnectionConstants#JMS_CONTEXT_FACTORY_KEY
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
        stringEditor = new StringFieldEditor(
                ConnectionConstants.JMS_URL_KEY, "Server URL",
                getFieldEditorParent()
                );
        addField(stringEditor);
    }

    /**
     * The method called when the OK button is clicked.  Simply calls the
     * save method on the underlying ScopedPreferenceStore
     * 
     * @see org.eclipse.jface.preference.FieldEditorPreferencePage#performOk()
     * @see org.eclipse.ui.preferences.ScopedPreferenceStore#save()
     */
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
