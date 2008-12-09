package org.marketcetera.photon.ui;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.marketcetera.messagehistory.ReportHolder;
import org.marketcetera.photon.FIXFieldLocalizer;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.preferences.FIXMessageColumnPreferenceParser;
import org.marketcetera.quickfix.FIXDataDictionary;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.quickfix.FIXValueExtractor;
import org.marketcetera.quickfix.FIXVersion;

import quickfix.DataDictionary;
import quickfix.FieldMap;
import quickfix.FieldType;
import ca.odell.glazedlists.gui.TableFormat;

/**
 * A table format and label provider for FIX message based tables. Listens to
 * preference changes for the assigned view ID and updates the visible columns.
 * 
 * @author michael.lossos@softwaregoodness.com
 */
public class FIXMessageTableFormat<T> implements TableFormat<T>,
		ITableLabelProvider {

	private static final int INVALID_FIELD_ID = -1;

	// todo: This constant is duplicated from EnumTableFormat.
	private static final DateFormat TIME_FORMAT = new SimpleDateFormat(
			"HH:mm:ss"); //$NON-NLS-1$

	// todo: This constant is duplicated from EnumTableFormat.
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd"); //$NON-NLS-1$

	private static final Class<?>[] NUMERIC_TYPES = { Number.class, Date.class,
			Calendar.class };
	
	private static final int NUM_DIGITS = 5;

	private final String assignedViewID;

	private FIXMessageColumnPreferenceParser prefsParser;

	private Table underlyingTable;

	private FIXValueExtractor valueExtractor;

	private Class<T> underlyingClass;

	private ColumnTracker columnTracker = new ColumnTracker();

	public FIXMessageTableFormat(Table table, final String assignedViewID,
			Class<T> underlyingClass) {
		this.underlyingTable = table;
		this.assignedViewID = assignedViewID;
		this.underlyingClass = underlyingClass;
		prefsParser = new FIXMessageColumnPreferenceParser();

		updateColumnsFromPreferences();
	}

	protected static class ColumnTracker {
		protected HashMap<Integer, TableColumn> fieldToColumnMap = new HashMap<Integer, TableColumn>();

		protected HashMap<TableColumn, Integer> columnToFieldMap = new HashMap<TableColumn, Integer>();

		protected HashMap<Integer, Integer> columnIndexToFieldMap = new HashMap<Integer, Integer>();

		protected HashMap<Integer, Integer> fieldToColumnIndexMap = new HashMap<Integer, Integer>();

		public void add(int fieldNum, TableColumn column, int columnIndex) {
			fieldToColumnMap.put(fieldNum, column);
			columnToFieldMap.put(column, fieldNum);
			columnIndexToFieldMap.put(columnIndex, fieldNum);
			fieldToColumnIndexMap.put(fieldNum, columnIndex);
		}

		public void remove(TableColumn column) {
			Integer fieldNum = columnToFieldMap.get(column);
			if (fieldNum != null) {
				int columnIndex = fieldToColumnIndexMap.get(fieldNum);
	
				fieldToColumnMap.remove(fieldNum);
				columnToFieldMap.remove(column);
				columnIndexToFieldMap.remove(columnIndex);
				fieldToColumnIndexMap.remove(fieldNum);
			}
		}

		public boolean containsFieldNumber(int fieldNum) {
			return fieldToColumnMap.containsKey(fieldNum);
		}

		public int getFieldNumber(int columnIndex) {
			int fieldNum = INVALID_FIELD_ID;
			if (columnIndexToFieldMap.containsKey(columnIndex)) {
				fieldNum = columnIndexToFieldMap.get(columnIndex);
			}
			return fieldNum;
		}
	}

	public String getAssignedViewID() {
		return assignedViewID;
	}

	protected void createColumn(int fieldNum) {
		createColumn(fieldNum, null, null);
	}

	protected void createColumn(int fieldNum, FIXDataDictionary fixDictionary,
			DataDictionary dictionary) {
		int alignment;
		if (isNumericColumn(fieldNum, dictionary)) {
			alignment = SWT.RIGHT;
		} else {
			alignment = SWT.LEFT;
		}
		TableColumn tableColumn = new TableColumn(underlyingTable, alignment);
		String columnName = getFIXFieldColumnName(fieldNum, fixDictionary);
		String localizedName = ""; //$NON-NLS-1$
		if (columnName != null) {
			localizedName = FIXFieldLocalizer.getLocalizedFIXFieldName(columnName);
		}
		tableColumn.setText(localizedName);
		tableColumn.setResizable(true);
		tableColumn.pack();
		/**
		 * todo: Allow column moving to change the order in the column
		 * preferences. See FIXMessageColumnPreferencePage for what needs to be
		 * set.
		 */
		tableColumn.setMoveable(false);

		int columnIndex = underlyingTable.getColumnCount() - 1;
		columnTracker.add(fieldNum, tableColumn, columnIndex);
	}

	protected void removeColumn(TableColumn whichColumn) {
		columnTracker.remove(whichColumn);
		whichColumn.dispose();
	}

	protected void createAllMissingColumns(List<Integer> fieldsToShow) {
		// todo: Handle columns that are not FIX fields.
		// todo: Handle adding custom FIX fields as columns.
		FIXDataDictionary fixDictionary = getFIXDataDictionary();
		DataDictionary dictionary = getDataDictionary();
		if (fieldsToShow.isEmpty()) {
			for (int fieldNum = 1; fieldNum < FIXMessageUtil.getMaxFIXFields(); ++fieldNum) {
				if (dictionary.isField(fieldNum)) {
					if (!columnTracker.containsFieldNumber(fieldNum)) {
						createColumn(fieldNum, fixDictionary, dictionary);
					}
				}
			}
		} else {
			for (int fieldNum : fieldsToShow) {
				if (!columnTracker.containsFieldNumber(fieldNum)) {
					createColumn(fieldNum, fixDictionary, dictionary);
				}
			}
		}
	}

	/**
	 * Derived classes can override this method to add columns that are always
	 * present. Call the createColumn() methods to create the columns and
	 * override getFIXFieldColumnName(), getColumnValue(), and isNumericColumn()
	 * methods to provide information about them.
	 */
	protected void createExtraColumns() {
		// Do nothing
	}

	protected boolean isNumericColumn(int fieldNum, DataDictionary dict) {
		if (dict == null) {
			return false;
		}
		try {
			FieldType fieldTypeEnum = dict.getFieldTypeEnum(fieldNum);
			Class<?> javaType;
			if (fieldTypeEnum == null){
				javaType = String.class;
			} else {
				javaType = fieldTypeEnum.getJavaType();
			}

			for (Class<?> type : NUMERIC_TYPES) {
				if (type.isAssignableFrom(javaType)) {
					return true;
				}
			}
		} catch (Exception anyException) {
			// Ignore
		}
		return false;
	}

	public void addListener(ILabelProviderListener listener) {
	}

	public void dispose() {
	}

	public void removeListener(ILabelProviderListener listener) {
	}

	public boolean isLabelProperty(Object element, String property) {
		return true;
	}

	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	protected void removeAllColumns() {
		TableColumn[] columns = underlyingTable.getColumns();
		for (TableColumn column : columns) {
			removeColumn(column);
		}
	}

	protected void recreateAllColumns(List<Integer> fieldsToShow) {
		removeAllColumns();
		// Additional static columns come first.
		createExtraColumns();
		// Columns chosen by the user come after.
		createAllMissingColumns(fieldsToShow);
	}

	public void updateColumnsFromPreferences() {
		// todo: This should also dictate column order.
		List<Integer> fieldsToShowList = prefsParser
				.getFieldsToShow(assignedViewID);
		long begin = 0, end = 0;
		try {
			underlyingTable.getParent().setRedraw(false);
			underlyingTable.setRedraw(false);
			begin = System.nanoTime();
			recreateAllColumns(fieldsToShowList);
		} finally {
			end = System.nanoTime();
			underlyingTable.getParent().setRedraw(true);
			underlyingTable.setRedraw(true);

		}
		long elapsedMillis = (end - begin) / 1000000L;
		PhotonPlugin.getMainConsoleLogger().debug(
				"Rendered table: " + elapsedMillis + "ms"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public int getColumnCount() {
		return underlyingTable.getColumnCount();
	}

	public String getFIXFieldColumnName(int fixFieldNum,
			FIXDataDictionary fixDataDictionary) {
		if (fixDataDictionary == null) {
			return null;
		}
		String fieldName = null;
		try {
			fieldName = fixDataDictionary.getHumanFieldName(fixFieldNum);
		} catch (Exception anyException) {
			// Ignore
		}
		if (fieldName == null || fieldName.trim().length() == 0) {
			fieldName = "(" + fixFieldNum + ")"; //$NON-NLS-1$ //$NON-NLS-2$
		}
		return fieldName;
	}

	public String getColumnName(int column) {
		return getFIXFieldColumnName(column, PhotonPlugin.getDefault()
				.getFIXDataDictionary());
	}

	public Object getColumnValue(T baseObject, int columnIndex) {
		int fieldNum = columnTracker.getFieldNumber(columnIndex);
		Object columnValue = null;
		if (fieldNum > INVALID_FIELD_ID) {
			columnValue = extractValue(fieldNum, baseObject, columnIndex);
		}
		return columnValue;
	}

	@SuppressWarnings("unchecked") //$NON-NLS-1$
	public String getColumnText(Object element, int columnIndex) {
		String columnText = ""; //$NON-NLS-1$
		if (element != null
				&& underlyingClass.isAssignableFrom(element.getClass())) {
			T elementAsT = (T) element;
			columnText = convertColumnValueToText(elementAsT, columnIndex);
		}
		return columnText;
	}

	protected String convertColumnValueToText(T baseObject, int columnIndex) {
		Object objValue = getColumnValue(baseObject, columnIndex);
		String textValue = null;
		if (objValue != null) {
			DataDictionary dictionary = getDataDictionary();
			int fieldNum = columnTracker.getFieldNumber(columnIndex);
			FieldType fieldType = dictionary.getFieldTypeEnum(fieldNum);
			if (objValue instanceof Date) {
				if (fieldType.equals(FieldType.UtcTimeOnly)
						|| fieldType.equals(FieldType.UtcTimeStamp)) {
					textValue = TIME_FORMAT.format((Date) objValue);
				} else if (fieldType.equals(FieldType.UtcDateOnly)
						|| fieldType.equals(FieldType.UtcDate)) {
					textValue = DATE_FORMAT.format((Date) objValue);
				}
			} else if (objValue instanceof BigDecimal) {
				BigDecimal n = (BigDecimal)objValue;
				if (n.scale() <= NUM_DIGITS) {
					return n.toPlainString();
				} else {
					return n.setScale(NUM_DIGITS, BigDecimal.ROUND_DOWN).toPlainString() + "..."; //$NON-NLS-1$
				}
			}

			if (textValue == null) {
				textValue = objValue.toString();
				
				String fieldName = dictionary.getFieldName(fieldNum);
				textValue = FIXFieldLocalizer.getLocalizedFIXValueName(fieldName, textValue);
			}
		}
		if (textValue == null) {
			textValue = ""; //$NON-NLS-1$
		}
		return textValue;
	}

	public FieldMap getFieldMap(T element, int columnIndex) {
		FieldMap fieldMap = null;
		// todo: This specialization should be in a derived class.
		if (element instanceof ReportHolder) {
			fieldMap = ((ReportHolder) element).getMessage();
		}
		return fieldMap;
	}

	protected FIXDataDictionary getFIXDataDictionary() {
		return PhotonPlugin.getDefault().getFIXDataDictionary();
	}

	protected DataDictionary getDataDictionary() {
		return getFIXDataDictionary().getDictionary();
	}

    protected Object extractValue(int fieldNum, T element, int columnIndex) {
		if (valueExtractor == null) {
			// Lazily initialize the FIXValueExtractor
			DataDictionary dictionary = getDataDictionary();
			FIXMessageFactory messageFactory = FIXVersion.getFIXVersion(
					dictionary.getVersion()).getMessageFactory();
			valueExtractor = new FIXValueExtractor(dictionary, messageFactory);
		}

		// todo: Handle repeating groups.
		int groupID = 0;
		int groupDiscriminatorID = 0;
		Object groupDiscriminatorValue = null;
		FieldMap fieldMap = getFieldMap(element, columnIndex);
		Object value = valueExtractor.extractValue(fieldMap, fieldNum, groupID,
				groupDiscriminatorID, groupDiscriminatorValue, true);
		return value;
	}
    
    /**
     * Returns the table being formatted by this class.
     * 
     * @return the table being formatted by this class
     */
    protected final Table getTable() {
    	return underlyingTable;
    }
}

