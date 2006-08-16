package org.marketcetera.photon;

import java.io.IOException;

import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.marketcetera.core.ClassVersion;
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

	private ServerUrlFieldEditor serverUrlEditor;

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
        serverUrlEditor = new ServerUrlFieldEditor(
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

/**
 * String field editor for a Server URL with syntactic validation of the URL.
 * <p>
 * Can be easily refactored into a generic URL validating editor by externalizing the error message text.
 * </p>
 *  
 * @author alissovski
 */
class ServerUrlFieldEditor extends StringFieldEditor
{
	private boolean isValid = false;
	
	
	public ServerUrlFieldEditor(String name, String labelText, Composite parent) {
		super(name, labelText, parent);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.StringFieldEditor#isValid()
	 */
	@Override
	public boolean isValid() {
		return isValid;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.StringFieldEditor#refreshValidState()
	 */
	@Override
	protected void refreshValidState() {
		boolean oldValid = isValid;
		isValid = checkUrlSyntax(getTextControl().getText());
		if (!isValid)
			showErrorMessage("The Server URL is not a valid URL.");
		else
			clearErrorMessage();
		
		if (oldValid != isValid)
			fireValueChanged(FieldEditor.IS_VALID, new Boolean(oldValid), new Boolean(isValid));  // causes the framework to update the enabled state of the Ok and Apply buttons
	}
	
	/**
	 * Checks whether a given string represents a syntactically valid URL. Ignores leading and trailing
	 * whitespace.
	 * 
	 * @param url a URL string; cannot be <code>null</code>.
	 * @return <code>true</code> if the string is a syntactically valid URL; <code>false</code> otherwise.
	 */
	private boolean checkUrlSyntax(String url) {
		return url.trim().startsWith("tcp://");  // todo:temp until we add a plug-in wrapping org.apache.commons.validator and vet out licensing
	}
}