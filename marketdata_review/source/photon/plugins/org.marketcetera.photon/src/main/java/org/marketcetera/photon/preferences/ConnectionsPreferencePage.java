package org.marketcetera.photon.preferences;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
import org.marketcetera.photon.Messages;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.marketdata.MarketDataFeed;
import org.marketcetera.photon.marketdata.MarketDataManager;
import org.marketcetera.quickfix.ConnectionConstants;

@ClassVersion("$Id$") //$NON-NLS-1$
public class ConnectionsPreferencePage
    extends FieldEditorPreferencePage
    implements IWorkbenchPreferencePage, Messages
{

	public static final String ID = "org.marketcetera.photon.preferences.connections"; //$NON-NLS-1$
	
	private final MarketDataManager mdataManager = PhotonPlugin.getDefault().getMarketDataManager();
	
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
    		descriptionLabel.setText(FIX_VERSION_LABEL.getText());
    		Label versionLabel = new Label(getFieldEditorParent(), SWT.NONE);
    		versionLabel.setText(fixVersionStr);
    		versionLabel.setToolTipText(FIX_VERSION_TOOLTIP.getText());
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
        
        jmsServerUrlEditor = new UrlFieldEditor(ConnectionConstants.JMS_URL_KEY,
                                                JMS_SERVER_URL_LABEL.getText(),
                                                getFieldEditorParent());
		addField(jmsServerUrlEditor);
		StringFieldEditor stringEditor = new StringFieldEditor(ConnectionConstants.JMS_INCOMING_TOPIC_KEY,
		                                                       INCOMING_TOPIC_LABEL.getText(),
		                                                       getFieldEditorParent());
        addField(stringEditor);
        stringEditor = new StringFieldEditor(ConnectionConstants.JMS_OUTGOING_QUEUE_KEY,
                                             OUTGOING_QUEUE_LABEL.getText(),
                                             getFieldEditorParent());
        addField(stringEditor);

        webAppHostEditor = new UrlFieldEditor(ConnectionConstants.WEB_APP_HOST_KEY,
                                              WEB_APP_HOST_LABEL.getText(),
                                              getFieldEditorParent());
		addField(webAppHostEditor);
				
        webAppPortEditor = new IntegerFieldEditor(ConnectionConstants.WEB_APP_PORT_KEY,
                                                  WEB_APP_PORT_LABEL.getText(),
                                                  getFieldEditorParent());
		addField(webAppPortEditor);
		
		Collection<MarketDataFeed> providers = mdataManager.getProviders();
		List<String[]> namesValues = new ArrayList<String[]>();
		// blank one to represent no selection
		namesValues.add(new String[] {"",""}); //$NON-NLS-1$ //$NON-NLS-2$
		for (MarketDataFeed provider : providers) {
			String name = provider.getName();
			if (name == null) {
				name = provider.getId();
			}
			namesValues.add(new String[] {name, provider.getId()});
		}
		quoteFeedNameEditor = new ComboFieldEditor(ConnectionConstants.MARKETDATA_STARTUP_KEY,
		                                           MARKET_DATA_FEED_LABEL.getText(),
		                                           namesValues.toArray(new String[namesValues.size()][]),
		                                           getFieldEditorParent());
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
		
        orderIDPrefixEditor = new StringFieldEditor(ConnectionConstants.ORDER_ID_PREFIX_KEY,
                                                    ORDER_ID_PREFIX_LABEL.getText(),
                                                    getFieldEditorParent());
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
