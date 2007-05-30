package org.marketcetera.photon.ui;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.marketcetera.photon.FIXFieldLocalizer;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.core.MessageHolder;
import org.marketcetera.photon.preferences.FIXMessageColumnPreferenceParser;
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
 */
public class FIXMessageTableFormat<T> implements TableFormat<T>,
		ITableLabelProvider, IPropertyChangeListener {

	private static final int INVALID_FIELD_ID = -1;

	private static final int MAX_VISIBLE_COLUMNS = 100;

	private static final int DEFAULT_COLUMN_WIDTH = 20;

	// todo: This constant is duplicated from EnumTableFormat.
	private static final DateFormat TIME_FORMAT = new SimpleDateFormat(
			"HH:mm:ss");

	// todo: This constant is duplicated from EnumTableFormat.
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd");

	private static final Class<?>[] NUMERIC_TYPES = { Number.class, Date.class,
			Calendar.class };

	private final String assignedViewID;

	private FIXMessageColumnPreferenceParser prefsParser;

	private Table underlyingTable;

	private HashMap<TableColumn, Integer> columnWidthsWhenVisible = new HashMap<TableColumn, Integer>();

	private HashMap<Integer, TableColumn> fieldToColumnMap = new HashMap<Integer, TableColumn>();

	private HashMap<TableColumn, Integer> columnToFieldMap = new HashMap<TableColumn, Integer>();

	private HashMap<Integer, Integer> columnIndexToFieldMap = new HashMap<Integer, Integer>();

	private FIXValueExtractor valueExtractor;

	private Class<T> underlyingClass;

	public FIXMessageTableFormat(Table table, final String assignedViewID,
			Class<T> underlyingClass) {
		this.underlyingTable = table;
		this.assignedViewID = assignedViewID;
		this.underlyingClass = underlyingClass;
		prefsParser = new FIXMessageColumnPreferenceParser();

		createAllColumns();
		updateColumnsFromPreferences();
		addListeners();
	}

	protected void addListeners() {
		ScopedPreferenceStore thePreferenceStore = PhotonPlugin.getDefault()
				.getPreferenceStore();
		thePreferenceStore.addPropertyChangeListener(this);
	}

	protected void removeListeners() {
		ScopedPreferenceStore thePreferenceStore = PhotonPlugin.getDefault()
				.getPreferenceStore();
		thePreferenceStore.removePropertyChangeListener(this);
	}

	protected void createAllColumns() {
		// todo: Handle columns that are not FIX fields.
		// todo: Handle adding custom FIX fields as columns.
		for (int fieldNum = 1; fieldNum < FIXMessageUtil.getMaxFIXFields(); ++fieldNum) {
			DataDictionary dictionary = getDataDictionary();
			if (dictionary.isField(fieldNum)) {
				int alignment;
				if (isNumericColumn(fieldNum, dictionary)) {
					alignment = SWT.RIGHT;
				} else {
					alignment = SWT.LEFT;
				}
				TableColumn tableColumn = new TableColumn(underlyingTable,
						alignment);
				String columnName = getFIXFieldColumnName(fieldNum);
				String localizedName = FIXFieldLocalizer
						.getLocalizedMessage(columnName);
				tableColumn.setText(localizedName);

				fieldToColumnMap.put(fieldNum, tableColumn);
				columnToFieldMap.put(tableColumn, fieldNum);

				int columnIndex = underlyingTable.getColumnCount() - 1;
				columnIndexToFieldMap.put(columnIndex, fieldNum);
			}
		}
	}

	protected void hideColumn(TableColumn tableColumn) {
		int currentWidth = tableColumn.getWidth();
		if (currentWidth > 0) {
			tableColumn.setResizable(false);
			columnWidthsWhenVisible.put(tableColumn, currentWidth);
			tableColumn.setWidth(0);
		}
		// Else already hidden
	}

	protected void showColumn(TableColumn tableColumn) {
		tableColumn.setResizable(true);
		if (tableColumn.getWidth() > 0) {
			// Already shown
			return;
		}
		int width = 0;
		if (columnWidthsWhenVisible.containsKey(tableColumn)) {
			width = columnWidthsWhenVisible.get(tableColumn);
		}
		if (width > 0) {
			tableColumn.setWidth(width);
		} else {
			tableColumn.setWidth(DEFAULT_COLUMN_WIDTH);
		}
	}

	protected boolean isNumericColumn(int fieldNum, DataDictionary dict) {
		try {
			FieldType fieldTypeEnum = dict.getFieldTypeEnum(fieldNum);
			Class javaType = fieldTypeEnum.getJavaType();

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
		removeListeners();
	}

	public void removeListener(ILabelProviderListener listener) {
	}

	public boolean isLabelProperty(Object element, String property) {
		return true;
	}

	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	public void updateColumnsFromPreferences() {
		// todo: This should also dictate column order.
		List<Integer> fieldsToShowList = prefsParser
				.getFieldsToShow(assignedViewID);
		HashSet<Integer> fieldsToShow = new HashSet<Integer>();
		if (fieldsToShowList != null) {
			fieldsToShow.addAll(fieldsToShowList);
		}
		try {
			underlyingTable.setRedraw(false);
			TableColumn[] columns = underlyingTable.getColumns();
			int numShownColumns = 0;
			for (TableColumn column : columns) {
				int fieldNum = columnToFieldMap.get(column);
				if (numShownColumns < MAX_VISIBLE_COLUMNS
						&& (fieldsToShow.isEmpty() || fieldsToShow
								.contains(fieldNum))) {
					showColumn(column);
					++numShownColumns;
				} else {
					hideColumn(column);
				}
			}
		} finally {
			underlyingTable.setRedraw(true);
		}
	}

	public int getColumnCount() {
		return underlyingTable.getColumnCount();
	}

	public String getFIXFieldColumnName(int fixFieldNum) {
		String fieldName = null;
		try {
			fieldName = PhotonPlugin.getDefault().getFIXDataDictionary()
					.getHumanFieldName(fixFieldNum);
		} catch (Exception anyException) {
			// Ignore
		}
		if (fieldName == null || fieldName.trim().length() == 0) {
			fieldName = "(" + fixFieldNum + ")";
		}
		return fieldName;
	}

	public String getColumnName(int column) {
		return getFIXFieldColumnName(column);
	}

	public Object getColumnValue(T baseObject, int column) {
		int fieldNum = getFieldNumber(baseObject, column);
		Object columnValue = null;
		if (fieldNum != INVALID_FIELD_ID) {
			columnValue = extractValue(fieldNum, baseObject, column);
		}
		return columnValue;
	}

	protected int getFieldNumber(T baseObject, int column) {
		int fieldNum = INVALID_FIELD_ID;
		if (columnIndexToFieldMap.containsKey(column)) {
			fieldNum = columnIndexToFieldMap.get(column);
		}
		return fieldNum;
	}

	@SuppressWarnings("unchecked")
	public String getColumnText(Object element, int columnIndex) {
		String columnText = "";
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
			int fieldNum = getFieldNumber(baseObject, columnIndex);
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
				textValue = ((BigDecimal) objValue).toPlainString();
			}

			if (textValue == null) {
				textValue = objValue.toString();
			}
		}
		if (textValue == null) {
			textValue = "";
		}
		return textValue;
	}

	public FieldMap getFieldMap(T element, int columnIndex) {
		FieldMap fieldMap = null;
		// todo: This specialization should be in a derived class.
		if (element instanceof MessageHolder) {
			fieldMap = ((MessageHolder) element).getMessage();
		}
		return fieldMap;
	}

	protected DataDictionary getDataDictionary() {
		return PhotonPlugin.getDefault().getFIXDataDictionary().getDictionary();
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

	public void propertyChange(PropertyChangeEvent event) {
		String affectedProperty = event.getProperty();
		if (prefsParser.isPreferenceForView(affectedProperty, assignedViewID)) {
			updateColumnsFromPreferences();
		}
	}
}
