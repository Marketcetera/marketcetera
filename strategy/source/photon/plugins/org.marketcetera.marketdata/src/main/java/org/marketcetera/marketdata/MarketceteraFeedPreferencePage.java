package org.marketcetera.marketdata;

import java.io.IOException;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.marketcetera.photon.preferences.UrlFieldEditor;
import org.marketcetera.quickfix.ConnectionConstants;

public class MarketceteraFeedPreferencePage 
    extends FieldEditorPreferencePage 
    implements IWorkbenchPreferencePage, MarketdataMessages
{

	private UrlFieldEditor feedUrlEditor;
	private StringFieldEditor senderCompIDEditor;
	private StringFieldEditor targetCompIDEditor;

	public MarketceteraFeedPreferencePage() {
		super(GRID);
		setPreferenceStore(MarketceteraFeedPlugin.getDefault().getPreferenceStore());
	}
	
	@Override
	protected void createFieldEditors() {
        feedUrlEditor = new UrlFieldEditor(
        		ConnectionConstants.MARKETDATA_URL_SUFFIX,
        		URL_LABEL.getText(),
                getFieldEditorParent()
                );
		addField(feedUrlEditor);

        senderCompIDEditor = new StringFieldEditor(
        		MarketceteraFeed.SETTING_SENDER_COMP_ID,
        		SENDER_COMP_LABEL.getText(),
                getFieldEditorParent()
                );
		addField(senderCompIDEditor);

        targetCompIDEditor = new StringFieldEditor(
        		MarketceteraFeed.SETTING_TARGET_COMP_ID,
        		TARGET_COMP_LABEL.getText(),
                getFieldEditorParent()
                );
		addField(targetCompIDEditor);
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
        	feedUrlEditor.setStringValue(feedUrlEditor.getStringValue().trim());
        	
        	super.performOk();  // pulls the data out of the page fields and into the preference store. this call does _not_ persist the data to disk.
        	
            ((ScopedPreferenceStore)getPreferenceStore()).save();  // persists the preference store to disk
        } catch (IOException e) {
            //TODO: do something

        	return false;
        }
        
        return true;
    }

	public void init(IWorkbench workbench) {
	}

}
