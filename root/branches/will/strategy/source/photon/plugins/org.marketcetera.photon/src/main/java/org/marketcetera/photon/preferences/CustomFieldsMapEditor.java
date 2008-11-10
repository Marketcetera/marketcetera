package org.marketcetera.photon.preferences;

import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.photon.Messages;
import org.marketcetera.photon.ui.MapEditor;

import ca.odell.glazedlists.EventList;


@ClassVersion("$Id$") //$NON-NLS-1$
public class CustomFieldsMapEditor
    extends MapEditor
    implements Messages
{
	private Pattern allowedKeyPattern; 
	
	public CustomFieldsMapEditor(String custom_fields_preference, String label, Composite theFieldEditorParent) {
		super(custom_fields_preference, label, theFieldEditorParent);
		
		allowedKeyPattern = Pattern.compile("[0-9]+"); //$NON-NLS-1$
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
		MapDialog dialog = new MapDialog(getShell(),
		                                 NEW_CUSTOM_FIELD_LABEL.getText(),
		                                 KEY_LABEL.getText(),
		                                 VALUE_LABEL.getText());
        if (dialog.open() == Window.OK) {
			Entry<String, String> entry = dialog.getEntry();
			if (entry != null) {
				String key = entry.getKey();
				Matcher matcher = allowedKeyPattern.matcher(key);
				if (!matcher.matches()) {
					MessageDialog.openInformation(getShell(),
					                              INVALID_CUSTOM_FIELD_KEY.getText(),
					                              CUSTOM_FIELD_INVALID_DIGIT.getText(key));
					entry = null;
				}
			}
			return entry;
		}
        return null;
    }

}
