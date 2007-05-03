package org.marketcetera.photon.ui;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.marketcetera.photon.FIXFieldLocalizer;
import org.marketcetera.photon.IFieldIdentifier;
import org.marketcetera.photon.core.MessageHolder;
import org.marketcetera.quickfix.FIXDataDictionaryManager;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXValueExtractor;
import org.marketcetera.quickfix.FIXVersion;

import quickfix.DataDictionary;
import quickfix.FieldMap;
import quickfix.FieldType;
import ca.odell.glazedlists.gui.TableFormat;

public class EnumTableFormat<T> implements TableFormat<T>, ITableLabelProvider
{
	Enum [] columns;
	private DataDictionary dataDictionary;
//	private Map<String, Integer> fieldMap = new HashMap<String, Integer>();
	private FIXValueExtractor valueExtractor;
	
	private static final String COLUMN_WIDTH_SAVED_KEY_NAME = "width.saved";  //$NON-NLS-1$
	private static final DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

	
	public EnumTableFormat(Table table, Enum[] columns) {
		this.columns = columns;
		dataDictionary = FIXDataDictionaryManager.getCurrentFIXDataDictionary().getDictionary();
		FIXMessageFactory messageFactory = FIXVersion.getFIXVersion(dataDictionary.getVersion()).getMessageFactory();
		valueExtractor = new FIXValueExtractor(dataDictionary, messageFactory);
		
		int i = 0;
        for (Enum aColumn : columns) {
			int alignment;
			if (isNumericColumn(aColumn, dataDictionary)){
				alignment = SWT.RIGHT;
			} else {
				alignment = SWT.LEFT;
			}
			TableColumn tableColumn = new TableColumn(table, alignment);
			String localizedName = FIXFieldLocalizer.getLocalizedMessage(getColumnName(i++));
			tableColumn.setText(localizedName);
		}
	}

	private boolean isNumericColumn(Enum column, DataDictionary dict) {
		Class javaType;
		FieldType fieldTypeEnum;
		Integer fieldID;
		if (column instanceof IFieldIdentifier
				&& (fieldID = ((IFieldIdentifier)column).getFieldID()) != null
				&& (fieldTypeEnum = dict.getFieldTypeEnum(fieldID)) != null
				&& (javaType = fieldTypeEnum.getJavaType()) != null
				&& (Number.class.isAssignableFrom(javaType)
					|| Date.class.isAssignableFrom(javaType)
					|| Calendar.class.isAssignableFrom(javaType)))
		{
			return true;
		}
		return false;
	}

	public int getColumnCount() {
		return columns.length;
	}
	
	public String getColumnName(int index) {
		return columns[index].toString();
	}

	public Object getColumnValue(T element, int columnIndex) {
		Enum columnEnum = columns[columnIndex];
		if (columnEnum instanceof IFieldIdentifier)
		{
			IFieldIdentifier fieldIdentifier = ((IFieldIdentifier)columnEnum);

			Integer fieldID = fieldIdentifier.getFieldID();
			Integer groupID = fieldIdentifier.getGroupID();
			Integer groupDiscriminatorID = fieldIdentifier.getGroupDiscriminatorID();
			Object groupDiscriminatorValue = fieldIdentifier.getGroupDiscriminatorValue();

			FieldMap fieldMap;
			if (element instanceof MessageHolder){
				fieldMap = ((MessageHolder)element).getMessage();
			} else {
				fieldMap = (FieldMap)element;
			}
			Object value = valueExtractor.extractValue(fieldMap, fieldID, groupID, groupDiscriminatorID, groupDiscriminatorValue, true);
			return value;
		} else {
			return null;
		}
	}



	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		Enum columnEnum = columns[columnIndex];
		Integer fieldID;
		if (columnEnum instanceof IFieldIdentifier && 
				(fieldID = ((IFieldIdentifier)columnEnum).getFieldID()) != null){
			Object objValue = getColumnValue((T)element, columnIndex);
			String value = "";
			if (objValue != null){
				FieldType fieldType = dataDictionary.getFieldTypeEnum(fieldID);
				if (fieldType.equals(FieldType.UtcTimeOnly)
						|| fieldType.equals(FieldType.UtcTimeStamp)){
					value = TIME_FORMAT.format((Date)objValue);
				} else if (fieldType.equals(FieldType.UtcDateOnly)
						||fieldType.equals(FieldType.UtcDate)){
					value = DATE_FORMAT.format((Date)objValue);
				} else if (objValue instanceof BigDecimal){
					value  = ((BigDecimal)objValue).toPlainString();
				} else {
					value = objValue.toString();
				}
			}
			return value;
		} else {
			return null;
		}
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
