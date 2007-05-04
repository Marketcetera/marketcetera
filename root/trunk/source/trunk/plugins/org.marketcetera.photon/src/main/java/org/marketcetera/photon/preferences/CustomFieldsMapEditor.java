package org.marketcetera.photon.preferences;

import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.photon.ui.MapEditor;

import ca.odell.glazedlists.EventList;


@ClassVersion("$Id$")
public class CustomFieldsMapEditor extends MapEditor {

	public CustomFieldsMapEditor(String custom_fields_preference, String label, Composite theFieldEditorParent) {
		super(custom_fields_preference, label, theFieldEditorParent);
	}

	
	@Override
	protected boolean isDuplicateKeyAllowed() {
		return false;
	}

	@Override
	protected String createMap(EventList<Map.Entry<String, String>> entries) {
		return MapEditorUtil.encodeList(entries);
	}

	@Override
	protected EventList<Entry<String, String>> parseString(String stringList) {
		return MapEditorUtil.parseString(stringList);
	}
	
	@Override
    protected Entry<String, String> getNewInputObject() {
    	String keyPrompt = "Key";
    	String valuePrompt = "Value";
    	String title = "New custom field";
		MapDialog dialog = new MapDialog(getShell(), title , keyPrompt, valuePrompt);
        if (dialog.open() == Window.OK){
            return dialog.getEntry();
        }
        return null;
    }

}
