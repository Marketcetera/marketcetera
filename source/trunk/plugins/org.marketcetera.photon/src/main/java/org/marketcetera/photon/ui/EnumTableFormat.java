package org.marketcetera.photon.ui;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.marketcetera.photon.model.MessageHolder;
import org.marketcetera.quickfix.FIXDataDictionaryManager;

import quickfix.DataDictionary;
import quickfix.FieldMap;
import quickfix.FieldNotFound;
import quickfix.FieldType;
import quickfix.Message;
import ca.odell.glazedlists.gui.TableFormat;

public class EnumTableFormat implements TableFormat<MessageHolder>, ITableLabelProvider
{

	Enum [] columns;
	private DataDictionary dataDictionary;
	private Map<String, Integer> fieldMap = new HashMap<String, Integer>();

	private static final String COLUMN_WIDTH_SAVED_KEY_NAME = "width.saved";  //$NON-NLS-1$
	public EnumTableFormat(Table table, Enum[] columns) {
		this.columns = columns;
		dataDictionary = FIXDataDictionaryManager.getDictionary();
		// TODO: how can we get the max number of fields?
		for (int fieldNum = 1; fieldNum < 1000; fieldNum++) {
			if (dataDictionary.isField(fieldNum))
				fieldMap.put(dataDictionary.getFieldName(fieldNum), fieldNum);
		}
		int i = 0;
        for (Enum aColumn : columns) {
			TableColumn tableColumn = new TableColumn(table, SWT.LEFT);
			tableColumn.setText(columns[i++].toString());
		}

	}

	public int getColumnCount() {
		return columns.length;
	}

	public String getColumnName(int index) {
		return columns[index].toString();
	}

	public Object getColumnValue(MessageHolder element, int columnIndex) {
		Integer fieldID = fieldMap.get(columns[columnIndex].toString());
		String text = getColumnTextHelper(element, fieldID);
		FieldType fieldType = dataDictionary.getFieldTypeEnum(fieldID);
		Class clazz = fieldType.getJavaType();
		if (Number.class.isAssignableFrom(clazz)){
			return new BigDecimal(text);
		} else {
			return text;
		}
	}

	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		Integer fieldID = fieldMap.get(columns[columnIndex].toString());
		return getColumnTextHelper(element, fieldID);
	}

	private String getColumnTextHelper(Object element, Integer fieldID) {
		Message message = null;
		FieldMap map = null;
		if (fieldID != null) {
			if (element instanceof MessageHolder) {
				MessageHolder holder = (MessageHolder) element;
				message = holder.getMessage();
				map = getAppropriateMap(fieldID, message);
			} if (element instanceof Message) {
				message = (Message) element;
				map = getAppropriateMap(fieldID, message);
			} else if (element instanceof FieldMap){
				map = (FieldMap) element;
			}
			if (map != null){
				try {
					String value = map.getString(fieldID);
					
					if (dataDictionary.hasFieldValue(fieldID)) {
						value = FIXDataDictionaryManager.getHumanFieldValue(fieldID, value);
					}
					
					return value;
				} catch (FieldNotFound e) {
					return "";
				}
			}
		}
		return "";
	}

	private FieldMap getAppropriateMap(Integer fieldID, Message message) {
		FieldMap map;
		if (dataDictionary.isHeaderField(fieldID)) {
			map = message.getHeader();
		} else if (dataDictionary.isTrailerField(fieldID)) {
			map = message.getTrailer();
		} else {
			map = message;
		}
		return map;
	}

	public void addListener(ILabelProviderListener listener) {
		FieldType fieldTypeEnum = dataDictionary.getFieldTypeEnum(1);
	}

	public void dispose() {
	}

	public boolean isLabelProperty(Object element, String property) {
		return true;
	}

	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub
		
	}
	
	protected void hideColumn(TableColumn column) {
		// short of rebuilding the table, the only way to hide a table column with swt 3.2
		// is to set its width to 0 and make it non-resizable.
		
		column.setData(COLUMN_WIDTH_SAVED_KEY_NAME, column.getWidth());  // save the current width so that we could restore it when the column is shown again

		column.setResizable(false);
		column.setWidth(0);
	}

	protected void showColumn(TableColumn column) {
		column.setResizable(true);
		if (column.getData(COLUMN_WIDTH_SAVED_KEY_NAME) != null) {
			column.setWidth((Integer) column.getData(COLUMN_WIDTH_SAVED_KEY_NAME));
		}
	}

	protected boolean isColumnHidden(TableColumn column) {
		return column.getWidth() == 0 && !column.getResizable();
	}

}
