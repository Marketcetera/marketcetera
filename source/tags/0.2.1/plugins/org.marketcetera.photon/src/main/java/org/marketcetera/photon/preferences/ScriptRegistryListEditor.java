package org.marketcetera.photon.preferences;

import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.preference.ListEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;

import ca.odell.glazedlists.EventList;


/**
 * todo:doc
 *  
 * @author andrei@lissovski.org
 */
public class ScriptRegistryListEditor extends ListEditor {

	public ScriptRegistryListEditor(String preferenceName, String label, Composite fieldEditorParent) {
		super(preferenceName, label, fieldEditorParent);
	}

	@Override
	protected String createList(String[] items) {
		return ListEditorUtil.encodeList(items);
	}

	@Override
	protected String[] parseString(String stringList) {
		return ListEditorUtil.parseString(stringList);
	}
	
	@Override
    protected String getNewInputObject() {
		EventScriptMapDialog dialog = new EventScriptMapDialog(getShell());
        if (dialog.open() == Window.OK){
            return dialog.getEntry();
        }
        return null;
    }

}
