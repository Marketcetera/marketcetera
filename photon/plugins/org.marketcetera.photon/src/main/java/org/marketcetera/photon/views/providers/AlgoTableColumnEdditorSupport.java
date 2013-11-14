package org.marketcetera.photon.views.providers;

import java.util.Set;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.marketcetera.algo.BrokerAlgoTag;
import org.marketcetera.algo.BrokerAlgoTagSpec;

/**
 * Editor support for BrokerAlgoTags table.
 * TextBox (options == null) or comboBox (options != null).
 * 
 * @author Milos Djuric
 *
 */
public class AlgoTableColumnEdditorSupport extends EditingSupport {
	
	CellEditor mEditor = null;

	
	public AlgoTableColumnEdditorSupport(ColumnViewer viewer) {
		super(viewer);
	}

	/**
	 * Return appropriate cell editor for BrokerAlgoTags element from table
	 */
	@Override
	protected CellEditor getCellEditor(Object element) {
		if(element instanceof BrokerAlgoTag){
			BrokerAlgoTag brokerAlgoTag = (BrokerAlgoTag)element;		
			if(brokerAlgoTag.getTagSpec().getOptions() != null){
				mEditor = createComboBoxCellEditor(brokerAlgoTag);
			}else{
				mEditor = createTextBoxCellEditor(brokerAlgoTag);
			}
			return mEditor;
		}
		return null;
	}

	/**
	 * Create and populate comboBox cell editor (with empty value for non mandatory fields).
	 */
	private CellEditor createComboBoxCellEditor(BrokerAlgoTag brokerAlgoTag) {
		BrokerAlgoTagSpec brokerAlgoTagSpec = brokerAlgoTag.getTagSpec();
		Set<String> keys = brokerAlgoTagSpec.getOptions().keySet();
		String[] values = new String[keys.size() + (brokerAlgoTagSpec.getIsMandatory()? 1 : 0)];
		String selectedValue = brokerAlgoTag.getValue();
		String selectedKey = null;
		int index = 0;
		if(brokerAlgoTagSpec.getIsMandatory()){
			values[index++] = "";
		}
		for(String key:keys){
			values[index++] = key;
			if(selectedValue != null && !selectedValue.equals("") && selectedValue.equals(brokerAlgoTagSpec.getOptions().get(key))){
				selectedKey = key;
			}
		}
		ComboBoxViewerCellEditor editor = new ComboBoxViewerCellEditor((Composite)(getViewer().getControl()), SWT.READ_ONLY);
		editor.setLabelProvider(new LabelProvider());
		editor.setContenProvider(new ArrayContentProvider());
		editor.setInput(values);
		if(selectedKey != null){
			editor.setValue(selectedKey);
		}
		return editor;
	}
	
	/**
	 * Create textBox cell editor 
	 */
	private CellEditor createTextBoxCellEditor(BrokerAlgoTag brokerAlgoTag) {
		return new TextCellEditor((Composite)(getViewer().getControl()));
	}

	@Override
	protected boolean canEdit(Object element) {
		if(element instanceof BrokerAlgoTag){
			return true;
		}
		return false;
	}

	@Override
	protected Object getValue(Object element) {
		if(element instanceof BrokerAlgoTag){
			BrokerAlgoTag brokerAlgoTag = (BrokerAlgoTag)element;
			return brokerAlgoTag.getValue() != null ? brokerAlgoTag.getValue() : "";
		}
		return "";
	}

	@Override
	protected void setValue(Object element, Object value) {
		if(value == null)
			return;
		if(element instanceof BrokerAlgoTag){
			String newValue = (value == null)?"": value.toString();
			BrokerAlgoTag brokerAlgoTag = (BrokerAlgoTag)element;
			if(brokerAlgoTag.getTagSpec().getOptions() != null){
				newValue = (brokerAlgoTag.getTagSpec().getOptions().get(value));
				if(newValue == null)
					newValue = "";
			}
			brokerAlgoTag.setValue(newValue);
			getViewer().refresh();
		}
	}

}
