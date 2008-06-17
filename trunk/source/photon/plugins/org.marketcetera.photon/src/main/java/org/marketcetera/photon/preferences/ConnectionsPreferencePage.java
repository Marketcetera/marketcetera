package org.marketcetera.photon.preferences;

import java.io.IOException;

import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.actions.ReconnectMarketDataFeedJob;
import org.marketcetera.quickfix.ConnectionConstants;

@ClassVersion("$Id$")
public class ConnectionsPreferencePage extends FieldEditorPreferencePage implements
                                                                IWorkbenchPreferencePage {

	public static final String ID = "org.marketcetera.photon.preferences.connections";

	
	private UrlFieldEditor jmsServerUrlEditor;

	private UrlFieldEditor webAppHostEditor;
	
	private IntegerFieldEditor webAppPortEditor;

	private ComboFieldEditor quoteFeedNameEditor;

	private StringFieldEditor orderIDPrefixEditor;
	
    public ConnectionsPreferencePage() {
        super(GRID);
		setPreferenceStore(PhotonPlugin.getDefault().getPreferenceStore());
    }

    /**
     * Does nothing
     * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
     */
    public void init(IWorkbench workbench) {
    }

    /**
     * Adds labels to the field editor describing the FIX version. 
     */
    private void createFIXVersionLabels() {
    	String fixVersionStr = PhotonPlugin.getDefault().getMessageFactory().getBeginString();
    	if (fixVersionStr != null && fixVersionStr.length() > 0)
    	{
    		Label descriptionLabel = new Label(getFieldEditorParent(), SWT.NONE);
    		descriptionLabel.setText("FIX Version");
    		Label versionLabel = new Label(getFieldEditorParent(), SWT.NONE);
    		versionLabel.setText(fixVersionStr);
    		versionLabel.setToolTipText("The FIX version can be changed in photon-config.ini. " 
    				+ "You must restart Photon for changes to photon-config.ini to take effect.");
    	}
    }
    
    /**
	 * Creates the field editor components associated with the vaious connections.
	 * 
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
	 * 
	 * @see ConnectionConstants#JMS_OUTGOING_QUEUE_KEY
	 * @see ConnectionConstants#JMS_INCOMING_TOPIC_KEY
	 * @see ConnectionConstants#JMS_URL_KEY
	 */

    protected void createFieldEditors() {
        createFIXVersionLabels();
        
        jmsServerUrlEditor = new UrlFieldEditor(
                ConnectionConstants.JMS_URL_KEY, "JMS Server URL",
                getFieldEditorParent()
                );
		addField(jmsServerUrlEditor);
		StringFieldEditor stringEditor = new StringFieldEditor(
                ConnectionConstants.JMS_INCOMING_TOPIC_KEY, "Incoming topic name",
                getFieldEditorParent()
                );
        addField(stringEditor);
        stringEditor = new StringFieldEditor(
                ConnectionConstants.JMS_OUTGOING_QUEUE_KEY, "Outgoing queue name",
                getFieldEditorParent()
                );
        addField(stringEditor);

        webAppHostEditor = new UrlFieldEditor(
                ConnectionConstants.WEB_APP_HOST_KEY, "Web App Host",
                getFieldEditorParent()
                );
		addField(webAppHostEditor);
				
        webAppPortEditor = new IntegerFieldEditor(
                ConnectionConstants.WEB_APP_PORT_KEY, "Web App Port",
                getFieldEditorParent()
                );
		addField(webAppPortEditor);
		
		String[][] namesValues = ReconnectMarketDataFeedJob.getFeedNames();
		quoteFeedNameEditor = new ComboFieldEditor(ConnectionConstants.MARKETDATA_STARTUP_KEY, "Market Data Feed", namesValues, getFieldEditorParent());
		addField(quoteFeedNameEditor);
		
//        StringFieldEditor stringEditor = new StringFieldEditor(
//                ConfigPropertiesLoader.DB_URL_KEY, "Database URL",
//                getFieldEditorParent()
//                );
//        addField(stringEditor);
//        stringEditor = new StringFieldEditor(
//                ConfigPropertiesLoader.DB_USER_KEY, "Database user",
//                getFieldEditorParent()
//                );
//        addField(stringEditor);
//        stringEditor = new StringFieldEditor(
//                ConfigPropertiesLoader.DB_PASS_KEY, "Database password",
//                getFieldEditorParent()
//                );
//        addField(stringEditor);
		
        orderIDPrefixEditor = new StringFieldEditor(
                ConnectionConstants.ORDER_ID_PREFIX_KEY, "Order ID Prefix\n(Requires restart)",
                getFieldEditorParent()
                );
		addField(orderIDPrefixEditor);
		
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
        	jmsServerUrlEditor.setStringValue(jmsServerUrlEditor.getStringValue().trim());
        	webAppHostEditor.setStringValue(webAppHostEditor.getStringValue().trim());
        	
        	super.performOk();  // pulls the data out of the page fields and into the preference store. this call does _not_ persist the data to disk.
        	
            ((ScopedPreferenceStore)getPreferenceStore()).save();  // persists the preference store to disk
        } catch (IOException e) {
            //TODO: do something

        	return false;
        }
        
        return true;
    }


}
