package org.marketcetera.photon.preferences;

import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.marketcetera.photon.ui.MapEditor;

import ca.odell.glazedlists.EventList;


/**
 * todo:doc
 *  
 * @author andrei@lissovski.org
 */
public class ScriptRegistryMapEditor extends MapEditor {

	public ScriptRegistryMapEditor(String preferenceName, String label, Composite fieldEditorParent) {
		super(preferenceName, label, fieldEditorParent);
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
		EventScriptMapDialog dialog = new EventScriptMapDialog(getShell());
        if (dialog.open() == Window.OK){
            return dialog.getEntry();
        }
        return null;
    }

}
