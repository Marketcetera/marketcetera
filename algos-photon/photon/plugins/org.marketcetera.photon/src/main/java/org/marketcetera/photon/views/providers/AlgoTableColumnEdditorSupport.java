package org.marketcetera.photon.views.providers;

import java.util.Map;
import java.util.Set;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.marketcetera.algo.BrokerAlgoTag;
import org.marketcetera.algo.BrokerAlgoTagSpec;

public class AlgoTableColumnEdditorSupport extends EditingSupport {
	
	CellEditor mEditor = null;

	public AlgoTableColumnEdditorSupport(ColumnViewer viewer) {
		super(viewer);
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		if(element instanceof BrokerAlgoTag){
			BrokerAlgoTag brokerAlgoTag = (BrokerAlgoTag)element;		
			if(brokerAlgoTag.getTagSpec().getOptions() != null){
				mEditor = createComboBoxCellEditor(brokerAlgoTag.getTagSpec());
			}else{
				mEditor = createTextBoxCellEditor(brokerAlgoTag.getTagSpec());
			}
			return mEditor;
		}
		return null;
	}

	private CellEditor createComboBoxCellEditor(BrokerAlgoTagSpec brokerAlgoTagSpec) {
		Set<String> keys = brokerAlgoTagSpec.getOptions().keySet();
		String[] values = new String[keys.size() + (brokerAlgoTagSpec.getIsMandatory()? 1 : 0)];
		int index = 0;
		if(brokerAlgoTagSpec.getIsMandatory()){
			values[index++] = "";
		}
		for(String key:keys){
			values[index++] = key;
		}
		ComboBoxViewerCellEditor editor = new ComboBoxViewerCellEditor((Composite)(getViewer().getControl()), SWT.READ_ONLY);
		editor.setLabelProvider(new LabelProvider());
		editor.setContenProvider(new ArrayContentProvider());
		editor.setInput(values);
		return editor;
	}
	
	private CellEditor createTextBoxCellEditor(BrokerAlgoTagSpec brokerAlgoTagSpec) {
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
