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
import org.marketcetera.algo.BrokerAlgoTagSpec;
import org.marketcetera.photon.views.ObservableAlgoTag;

/**
 * Editor support for BrokerAlgoTags table.
 * TextBox (options == null) or comboBox (options != null).
 * 
 * @author Milos Djuric
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 *
 */
public class AlgoTableColumnEditorSupport
        extends EditingSupport
{
    /**
     * Create a new AlgoTableColumnEditorSupport instance.
     *
     * @param inViewer a <code>ColumnViewer</code> value
     */
    public AlgoTableColumnEditorSupport(ColumnViewer inViewer)
    {
        super(inViewer);
    }
    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.EditingSupport#getCellEditor(java.lang.Object)
     */
    @Override
    protected CellEditor getCellEditor(Object inElement)
    {
        if(inElement instanceof ObservableAlgoTag){
            ObservableAlgoTag observableAlgoTag = (ObservableAlgoTag)inElement;
            if(observableAlgoTag.getAlgoTag().getTagSpec().getOptions() == null || observableAlgoTag.getAlgoTag().getTagSpec().getOptions().isEmpty()) {
                editor = createTextBoxCellEditor(observableAlgoTag);
            } else {
                editor = createComboBoxCellEditor(observableAlgoTag);
            }
            return editor;
        }
        return null;
    }
    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.EditingSupport#canEdit(java.lang.Object)
     */
    @Override
    protected boolean canEdit(Object inElement)
    {
        if(editor == null) {
            return true;
        }
        if(inElement instanceof ObservableAlgoTag) {
            return !((ObservableAlgoTag)inElement).getAlgoTag().getTagSpec().isReadOnly();
        }
        return false;
    }
    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.EditingSupport#getValue(java.lang.Object)
     */
    @Override
    protected Object getValue(Object inElement)
    {
        if(inElement instanceof ObservableAlgoTag) {
            ObservableAlgoTag observableAlgoTag = (ObservableAlgoTag)inElement;
            return observableAlgoTag.getValueString() != null ? observableAlgoTag.getValueString() : "";
        }
        return "";
    }
    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.EditingSupport#setValue(java.lang.Object, java.lang.Object)
     */
    @Override
    protected void setValue(Object inElement,
                            Object inValue)
    {
        if(inElement instanceof ObservableAlgoTag) {
            String newValue = (inValue == null)?"": inValue.toString();
            ObservableAlgoTag observableAlgoTag = (ObservableAlgoTag)inElement;
            if(!(observableAlgoTag.getAlgoTag().getTagSpec().getOptions() == null || observableAlgoTag.getAlgoTag().getTagSpec().getOptions().isEmpty())) {
                newValue = (observableAlgoTag.getAlgoTag().getTagSpec().getOptions().get(inValue));
                if(newValue == null) {
                    newValue = "";
                }
            }
            observableAlgoTag.setValue(newValue);
            getViewer().refresh();
        }
    }
    /**
     * Create a combo box cell editor based on the given broker algo tag.
     *
     * @param inObservableAlgoTag an <code>ObservableAlgoTag</code> value
     * @return a <code>CellEditor</code> value
     */
    private CellEditor createComboBoxCellEditor(ObservableAlgoTag inObservableAlgoTag)
    {
        BrokerAlgoTagSpec brokerAlgoTagSpec = inObservableAlgoTag.getAlgoTag().getTagSpec();
        Set<String> keys = brokerAlgoTagSpec.getOptions().keySet();
        String[] values = new String[keys.size() + (brokerAlgoTagSpec.getIsMandatory()? 1 : 0)];
        String selectedValue = inObservableAlgoTag.getValueString();
        String selectedKey = null;
        int index = 0;
        if(brokerAlgoTagSpec.getIsMandatory()){
            values[index++] = "";
        }
        for(String key:keys) {
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
     * Create a text box cell editor based on the given broker algo tag.
     *
     * @param inObservableAlgoTag an <code>ObservableAlgoTag</code> value
     * @return a <code>CellEditor</code> value
     */
    private CellEditor createTextBoxCellEditor(ObservableAlgoTag inObservableAlgoTag)
    {
        TextCellEditor editor = new TextCellEditor((Composite)(getViewer().getControl()));
        if(inObservableAlgoTag.getAlgoTag().getTagSpec().getDefaultValue() != null) {
            editor.setValue(inObservableAlgoTag.getAlgoTag().getTagSpec().getDefaultValue());
            inObservableAlgoTag.setValue(inObservableAlgoTag.getAlgoTag().getTagSpec().getDefaultValue());
        }
        if(inObservableAlgoTag.getAlgoTag().getTagSpec().isReadOnly()) {
            editor.setStyle(SWT.READ_ONLY);
        }
        return editor;
    }
    /**
     * cell editor value
     */
    private CellEditor editor = null;
}
