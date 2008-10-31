package org.marketcetera.photon.views;

import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.CoreException;
import org.marketcetera.photon.Messages;
import org.marketcetera.quickfix.CurrentFIXDataDictionary;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.util.log.I18NBoundMessage1P;

import quickfix.DataDictionary;
import quickfix.Message;

/* $License$ */

/**
 * A UI section for custom fields.
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class CustomFieldsViewPieces
    implements Messages
{
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
		customFieldsExpandableComposite.setText(CUSTOM_FIELDS_LABEL.getText());
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
		enabledColumn.setText(ENABLED_LABEL.getText());
		enabledColumn.pack();
		TableColumn keyColumn = new TableColumn(customFieldsTable, SWT.LEFT);
		keyColumn.setText(KEY_LABEL.getText());
		keyColumn.pack();
		TableColumn valueColumn = new TableColumn(customFieldsTable, SWT.LEFT);
		valueColumn.setText(VALUE_LABEL.getText());
		valueColumn.pack();

		// tableViewer = new CheckboxTableViewer(
		// customFieldsTable);
		// tableViewer.setContentProvider(new
		// MapEntryContentProvider(tableViewer, mapEntryList));
		// tableViewer.setLabelProvider(new MapEntryLabelProvider());
		// tableViewer.setInput(mapEntryList);

		customFieldsExpandableComposite.setClient(customFieldsComposite);

	}


	public void addCustomFields(Message message) throws CoreException {
		TableItem[] items = customFieldsTable.getItems();
		DataDictionary dictionary = CurrentFIXDataDictionary
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
					} else {
						FIXMessageUtil.insertFieldIfMissing(fieldNumber, value,
								message);
					}
				} else {
					throw new CoreException(new I18NBoundMessage1P(CANNOT_FIND_CUSTOM_FIELD,
					                                               key));
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

}
