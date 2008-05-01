package org.marketcetera.photon.preferences;

import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.photon.ui.MapEditor;

import ca.odell.glazedlists.EventList;


@ClassVersion("$Id$")
public class CustomFieldsMapEditor extends MapEditor {
	private Pattern allowedKeyPattern; 
	
	public CustomFieldsMapEditor(String custom_fields_preference, String label, Composite theFieldEditorParent) {
		super(custom_fields_preference, label, theFieldEditorParent);
		
		allowedKeyPattern = Pattern.compile("[0-9]+");
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
        if (dialog.open() == Window.OK) {
			Entry<String, String> entry = dialog.getEntry();
			if (entry != null) {
				String key = entry.getKey();
				Matcher matcher = allowedKeyPattern.matcher(key);
				if (!matcher.matches()) {
					MessageDialog.openInformation(getShell(),
							"Invalid custom field key",
							"Custom field keys may only contain digits. This key is invalid:\n" + key );
					entry = null;
				}
			}
			return entry;
		}
        return null;
    }

}
