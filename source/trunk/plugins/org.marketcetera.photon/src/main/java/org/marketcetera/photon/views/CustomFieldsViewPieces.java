package org.marketcetera.photon.views;

import java.util.HashMap;
import java.util.Map.Entry;

import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.marketcetera.core.MarketceteraException;
import org.marketcetera.photon.preferences.MapEditorUtil;
import org.marketcetera.quickfix.FIXDataDictionaryManager;
import org.marketcetera.quickfix.FIXMessageUtil;

import quickfix.DataDictionary;
import quickfix.Message;
import ca.odell.glazedlists.EventList;

/**
 * A UI section for custom fields.
 */
public class CustomFieldsViewPieces {

	private Composite defaultParent;

	private FormToolkit formToolkit;

	private ExpandableComposite customFieldsExpandableComposite = null;

	private Composite customFieldsComposite = null;

	private Table customFieldsTable = null;

	private CheckboxTableViewer tableViewer = null;

	public CustomFieldsViewPieces(Composite defaultParent,
			FormToolkit formToolkit) {
		this.defaultParent = defaultParent;
		this.formToolkit = formToolkit;
	}

	private FormToolkit getFormToolkit() {
		return formToolkit;
	}

	/**
	 * This method initializes customFieldsExpandableComposite
	 * 
	 */
	public void createCustomFieldsExpandableComposite( int gridHorizontalSpan ) {
		GridData gridData3 = new GridData();
		gridData3.horizontalSpan = gridHorizontalSpan;
		gridData3.verticalAlignment = GridData.BEGINNING;
		gridData3.grabExcessHorizontalSpace = true;
		gridData3.horizontalAlignment = GridData.FILL;
		customFieldsExpandableComposite = getFormToolkit().createSection(
				defaultParent, Section.TITLE_BAR | Section.TWISTIE);
		customFieldsExpandableComposite.setText("Custom Fields");
		customFieldsExpandableComposite.setExpanded(false);
		customFieldsExpandableComposite.setLayoutData(gridData3);

		createCustomFieldsComposite();
	}

	/**
	 * This method initializes customFieldsComposite
	 * 
	 */
	private void createCustomFieldsComposite() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.marginWidth = 1;
		gridLayout.verticalSpacing = 1;
		gridLayout.horizontalSpacing = 1;
		gridLayout.marginHeight = 1;

		customFieldsComposite = getFormToolkit().createComposite(
				customFieldsExpandableComposite);
		customFieldsComposite.setLayout(gridLayout);
		GridData tableGridData = new GridData();
		tableGridData.verticalAlignment = GridData.CENTER;
		tableGridData.grabExcessHorizontalSpace = true;
		tableGridData.horizontalAlignment = GridData.FILL;

		customFieldsTable = new Table(customFieldsComposite, SWT.BORDER
				| SWT.CHECK | SWT.FULL_SELECTION);
		customFieldsTable.setLayoutData(tableGridData);
		customFieldsTable.setHeaderVisible(true);

		TableColumn enabledColumn = new TableColumn(customFieldsTable, SWT.LEFT);
		enabledColumn.setText("Enabled");
		enabledColumn.pack();
		TableColumn keyColumn = new TableColumn(customFieldsTable, SWT.LEFT);
		keyColumn.setText("Key");
		keyColumn.pack();
		TableColumn valueColumn = new TableColumn(customFieldsTable, SWT.LEFT);
		valueColumn.setText("Value");
		valueColumn.pack();

		// tableViewer = new CheckboxTableViewer(
		// customFieldsTable);
		// tableViewer.setContentProvider(new
		// MapEntryContentProvider(tableViewer, mapEntryList));
		// tableViewer.setLabelProvider(new MapEntryLabelProvider());
		// tableViewer.setInput(mapEntryList);

		customFieldsExpandableComposite.setClient(customFieldsComposite);

	}

	public void updateCustomFields(String preferenceString) {
		// Save previous enabled checkbox state
		final int keyColumnNum = 1;
		HashMap<String, Boolean> existingEnabledMap = new HashMap<String, Boolean>();
		TableItem[] existingItems = customFieldsTable.getItems();
		for (TableItem existingItem : existingItems) {
			String key = existingItem.getText(keyColumnNum);
			boolean checkedState = existingItem.getChecked();
			existingEnabledMap.put(key, checkedState);
		}

		customFieldsTable.setItemCount(0);
		EventList<Entry<String, String>> fields = MapEditorUtil
				.parseString(preferenceString);
		for (Entry<String, String> entry : fields) {
			TableItem item = new TableItem(customFieldsTable, SWT.NONE);
			String key = entry.getKey();
			// Column order must match column numbers used above
			String[] itemText = new String[] { "", key, entry.getValue() };
			item.setText(itemText);
			if (existingEnabledMap.containsKey(key)) {
				boolean previousEnabledValue = existingEnabledMap.get(key);
				item.setChecked(previousEnabledValue);
			}
		}
		TableColumn[] columns = customFieldsTable.getColumns();
		for (TableColumn column : columns) {
			column.pack();
		}
	}

	public void addCustomFields(Message message) throws MarketceteraException {
		TableItem[] items = customFieldsTable.getItems();
		DataDictionary dictionary = FIXDataDictionaryManager
				.getCurrentFIXDataDictionary().getDictionary();
		for (TableItem item : items) {
			if (item.getChecked()) {
				String key = item.getText(1);
				String value = item.getText(2);
				int fieldNumber = -1;
				try {
					fieldNumber = Integer.parseInt(key);
				} catch (Exception e) {
					try {
						fieldNumber = dictionary.getFieldTag(key);
					} catch (Exception ex) {

					}
				}
				if (fieldNumber > 0) {
					if (dictionary.isHeaderField(fieldNumber)) {
						FIXMessageUtil.insertFieldIfMissing(fieldNumber, value,
								message.getHeader());
					} else if (dictionary.isTrailerField(fieldNumber)) {
						FIXMessageUtil.insertFieldIfMissing(fieldNumber, value,
								message.getTrailer());
					} else if (dictionary.isField(fieldNumber)) {
						FIXMessageUtil.insertFieldIfMissing(fieldNumber, value,
								message);
					}
				} else {
					throw new MarketceteraException("Could not find field "
							+ key);
				}
			}
		}
	}

	public Table getCustomFieldsTable() {
		return customFieldsTable;
	}

	public CheckboxTableViewer getTableViewer() {
		return tableViewer;
	}

	public ExpandableComposite getCustomFieldsExpandableComposite() {
		return customFieldsExpandableComposite;
	}

	/**
	 * Set the item checked state for all items in the custom fields table using
	 * the integer value in the memento.
	 */
	public void restoreCustomFieldsTableItemCheckedState(
			IMemento viewStateMemento, String keyPrefix) {
		TableItem[] items = getCustomFieldsTable().getItems();
		for (int i = 0; i < items.length; i++) {
			TableItem item = items[i];
			String key = keyPrefix + item.getText(1);
			if (viewStateMemento.getInteger(key) != null) {
				boolean itemChecked = (viewStateMemento.getInteger(key)
						.intValue() != 0);
				item.setChecked(itemChecked);
			}
		}
	}

	public void saveState(IMemento viewStateMemento, String keyPrefix) {
		TableItem[] items = getCustomFieldsTable().getItems();
		for (int i = 0; i < items.length; i++) {
			TableItem item = items[i];
			String key = keyPrefix + item.getText(1);
			viewStateMemento.putInteger(key, (item.getChecked() ? 1 : 0));
		}
	}
}
