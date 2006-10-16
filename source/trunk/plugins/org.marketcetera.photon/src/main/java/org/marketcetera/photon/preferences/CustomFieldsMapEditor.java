package org.marketcetera.photon.preferences;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.MMapEntry;
import org.marketcetera.photon.ui.MapEditor;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;


@ClassVersion("$Id$")
public class CustomFieldsMapEditor extends MapEditor {

	
	private static final String UTF_8 = "UTF-8";

	public CustomFieldsMapEditor(String custom_fields_preference, String label, Composite theFieldEditorParent) {
		super(custom_fields_preference, label, theFieldEditorParent);
	}

	@Override
	protected String createMap(EventList<Map.Entry<String, String>> entries) {
		StringBuffer buf = new StringBuffer();
		int i = 0;
		for (Map.Entry<String, String> anEntry : entries) {
			try {
				buf.append(URLEncoder.encode(anEntry.getKey(),UTF_8));
				buf.append("=");
				buf.append(URLEncoder.encode(anEntry.getValue(), UTF_8));
			} catch (UnsupportedEncodingException e) {
				buf.append(anEntry.getKey());
				buf.append("=");
				buf.append(anEntry.getValue());
			}
			if (i < entries.size() - 1){
				buf.append("&");
			}
			i++;
		}
		return buf.toString();
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

	@Override
	protected EventList<Entry<String, String>> parseString(String stringList) {
		String [] pieces = stringList.split("&");
		EventList<Map.Entry<String, String>> outList = new BasicEventList<Map.Entry<String, String>>();
		int i = 0;
		for (String aPiece : pieces) {
			if (aPiece.contains("=")){
				String[] keyValueArray = aPiece.split("=");
				MMapEntry<String, String> outEntry;
				try {
					outEntry = new MMapEntry<String, String>(URLDecoder.decode(keyValueArray[0], UTF_8)
							, URLDecoder.decode(keyValueArray[1], UTF_8));
				} catch (UnsupportedEncodingException e) {
					outEntry = new MMapEntry<String, String>(keyValueArray[0]
							, keyValueArray[1]);
				}
				outList.add(outEntry);
				i++;
			}
		}
		return outList;
	}

}
