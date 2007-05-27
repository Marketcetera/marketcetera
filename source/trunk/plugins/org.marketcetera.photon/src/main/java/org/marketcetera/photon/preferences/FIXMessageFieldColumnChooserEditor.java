package org.marketcetera.photon.preferences;

import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.Assert;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Widget;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.ui.EventListContentProvider;
import org.marketcetera.photon.ui.IndexedTableViewer;
import org.marketcetera.quickfix.FIXDataDictionary;
import org.marketcetera.quickfix.FIXMessageUtil;

import ca.odell.glazedlists.BasicEventList;

public class FIXMessageFieldColumnChooserEditor extends FieldEditor {
	
	private FIXMessageDetailPreferenceParser parser;
	
	private FIXDataDictionary fixDictionary;

	private char orderType;
	
	private Table availableFieldsTable;

	private Table chosenFieldsTable;

	private IndexedTableViewer availableFieldsTableViewer;

	private IndexedTableViewer chosenFieldsTableViewer;

	/**
	 * The button box containing the Add, Add All, Remove, and Remove All
	 * buttons; <code>null</code> if none (before creation or after disposal).
	 */
	private Composite addRemoveButtonBox;

	private Button addButton;

	private Button removeButton;

	private Button addAllButton;

	private Button removeAllButton;
	
	private Composite upDownButtonBox;

	private Button upButton;

	private Button downButton;

	private SelectionListener selectionListener;

	private List<String> toEntries;
	
	private List<String> fromEntries;

	protected FIXMessageFieldColumnChooserEditor(String name, String labelText,
			Composite parent, char orderType) {
		init(name, labelText);
		this.fixDictionary = PhotonPlugin.getDefault().getFIXDataDictionary();
		this.orderType = orderType;
		this.parser = new FIXMessageDetailPreferenceParser();
		createControl(parent);
	}

	/**
	 * Notifies that the Add button has been pressed.
	 */
	private void addPressed() {
		setPresentsDefaultValue(false);		
		List<String> input = getNewInputObject(availableFieldsTable);
		if (input != null) {
			fromEntries.removeAll(input);
			toEntries.addAll(input);
			availableFieldsTableViewer.refresh(false);
			//Updates the list starting from the first added entry
			chosenFieldsTableViewer.refresh(input.get(0), false);
			
			selectionChanged();
		}
	}

	protected void adjustForNumColumns(int numColumns) {
		Control control = getLabelControl();
		((GridData) control.getLayoutData()).horizontalSpan = numColumns;
		((GridData) availableFieldsTable.getLayoutData()).horizontalSpan = numColumns - 1;
	}

	private void createAddRemoveButtons(Composite box) {
		addButton = createPushButton(box, "Add");//$NON-NLS-1$
		removeButton = createPushButton(box, "Remove");//$NON-NLS-1$
		addAllButton = createPushButton(box, "Add All");//$NON-NLS-1$
		removeAllButton = createPushButton(box, "Remove All");//$NON-NLS-1$
	}

	private void createUpDownButtons(Composite box) {
		upButton = createPushButton(box, "Up");//$NON-NLS-1$
		downButton = createPushButton(box, "Down");//$NON-NLS-1$
	}
	
	/**
	 * Helper method to create a push button.
	 */
	private Button createPushButton(Composite parent, String key) {
		Button button = new Button(parent, SWT.PUSH);
		button.setText(JFaceResources.getString(key));
		button.setFont(parent.getFont());
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		int widthHint = convertHorizontalDLUsToPixels(button,
				IDialogConstants.BUTTON_WIDTH);
		data.widthHint = Math.max(widthHint, button.computeSize(SWT.DEFAULT,
				SWT.DEFAULT, true).x);
		button.setLayoutData(data);
		button.addSelectionListener(getSelectionListener());
		return button;
	}

	private void createSelectionListener() {
		selectionListener = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				Widget widget = event.widget;
				if (widget == addButton) {
					addPressed();
				} else if (widget == removeButton) {
					removePressed();
				} else if (widget == addAllButton) {
					addAllPressed();
				} else if (widget == removeAllButton) {
					removeAllPressed();
				} else if (widget == availableFieldsTable) {
					selectionChanged();
				}
			}
		};
	}

	protected void doFillIntoGrid(Composite parent, int numColumns) {
		Control control = getLabelControl(parent);
		GridData gd = new GridData();
		gd.horizontalSpan = numColumns;
		control.setLayoutData(gd);

		availableFieldsTable = createAvailableFieldsTable(parent);

		addRemoveButtonBox = getAddRemoveButtonBoxControl(parent);
		gd = new GridData();
		gd.verticalAlignment = GridData.BEGINNING;
		gd.horizontalSpan = 1;
		addRemoveButtonBox.setLayoutData(gd);

		chosenFieldsTable = createChosenFieldsTable(parent);
		
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.verticalAlignment = GridData.FILL;
		gd.horizontalSpan = 1;
		gd.grabExcessHorizontalSpace = true;
		availableFieldsTable.setLayoutData(gd);
		chosenFieldsTable.setLayoutData(gd);

		availableFieldsTableViewer = createTableViewer(availableFieldsTable);
		chosenFieldsTableViewer = createTableViewer(chosenFieldsTable);
		
		upDownButtonBox = getUpDownButtonBoxControl(parent);
		gd = new GridData();
		gd.verticalAlignment = GridData.BEGINNING;
		gd.horizontalSpan = 1;
		upDownButtonBox.setLayoutData(gd);
	}

	protected void doLoad() {
		List<Integer> savedIntFields = parser.getFieldsToShow(orderType);
		if (chosenFieldsTable != null) {
			loadChosenFieldsTable(savedIntFields);
		}		
		if (availableFieldsTable != null) {
			loadAvailableFieldsTable(savedIntFields);				
		}
	}
	
	protected void doLoadDefault() {
		List<Integer> savedIntFields = parser.getFieldsToShow(orderType);
		if (chosenFieldsTable != null) {
			chosenFieldsTable.removeAll();
			loadChosenFieldsTable(savedIntFields);
		}		
		if (availableFieldsTable != null) {
			availableFieldsTable.removeAll();
			loadAvailableFieldsTable(savedIntFields);				
		}
	}

	private void loadAvailableFieldsTable(List<Integer> savedIntFields) {
		fromEntries = new BasicEventList<String>(); 	
		for (int i = 0; i < FIXMessageUtil.getMaxFIXFields(); i++) {
			if (!savedIntFields.contains(i) && FIXMessageUtil.isValidField(i)) {
				String fieldName = fixDictionary.getHumanFieldName(i);
				fromEntries.add(fieldName + " (" + i + ")");
			}
		}
		availableFieldsTableViewer.setInput(fromEntries);			
	}

	private void loadChosenFieldsTable(List<Integer> savedIntFields) {
		if (savedIntFields != null && savedIntFields.size() > 0) {
			toEntries = new BasicEventList<String>();			
			for (int intField : savedIntFields) {
				String fieldName = fixDictionary.getHumanFieldName(intField);
				toEntries.add(fieldName + " (" + intField + ")");
			}
			chosenFieldsTableViewer.setInput(toEntries);
		}
	}

	@SuppressWarnings("unchecked")
	protected void doStore() {
		List<Integer> chosenFields = (List<Integer>) chosenFieldsTableViewer
				.getInput();
		if (chosenFields != null && chosenFields.size() > 0) {
			parser.setFieldsToShow(orderType, chosenFields);
		}
	}

	/**
	 * Notifies that the Down button has been pressed.
	 */
	private void removeAllPressed() {
		swap(false);
	}

	/**
	 * Returns this field editor's button box containing the Add, Remove, Up,
	 * and Down button.
	 */
	private Composite getAddRemoveButtonBoxControl(Composite parent) {
		if (addRemoveButtonBox == null) {
			addRemoveButtonBox = new Composite(parent, SWT.NULL);
			GridLayout layout = new GridLayout();
			layout.marginWidth = 0;
			addRemoveButtonBox.setLayout(layout);
			createAddRemoveButtons(addRemoveButtonBox);
			addRemoveButtonBox.addDisposeListener(new DisposeListener() {
				public void widgetDisposed(DisposeEvent event) {
					addButton = null;
					removeButton = null;
					addAllButton = null;
					removeAllButton = null;
					addRemoveButtonBox = null;
				}
			});

		} else {
			checkParent(addRemoveButtonBox, parent);
		}

//		selectionChanged();
		return addRemoveButtonBox;
	}

	private Composite getUpDownButtonBoxControl(Composite parent) {
		if (upDownButtonBox == null) {
			upDownButtonBox = new Composite(parent, SWT.NULL);
			GridLayout layout = new GridLayout();
			layout.marginWidth = 0;
			upDownButtonBox.setLayout(layout);
			createUpDownButtons(upDownButtonBox);
			upDownButtonBox.addDisposeListener(new DisposeListener() {
				public void widgetDisposed(DisposeEvent event) {
					upButton = null;
					downButton = null;
					upDownButtonBox = null;
				}
			});

		} else {
			checkParent(upDownButtonBox, parent);
		}
//		selectionChanged();
		return upDownButtonBox;
	}

	private Table createAvailableFieldsTable(Composite parent) {
		if (availableFieldsTable == null) {
			availableFieldsTable = new Table(parent, SWT.BORDER | SWT.V_SCROLL
					| SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.MULTI);
			availableFieldsTable.setHeaderVisible(false);
			availableFieldsTable.setFont(parent.getFont());
			availableFieldsTable.addSelectionListener(getSelectionListener());
			availableFieldsTable.addDisposeListener(new DisposeListener() {
				public void widgetDisposed(DisposeEvent event) {
					availableFieldsTable = null;
				}
			});
			TableColumn column;
			column = new TableColumn(availableFieldsTable, SWT.CENTER);
			column.setText("Field");
			column.setWidth(200);
		} else {
			checkParent(availableFieldsTable, parent);
		}
		return availableFieldsTable;
	}
	
	private Table createChosenFieldsTable(Composite parent) {
		if (chosenFieldsTable == null) {
			chosenFieldsTable = new Table(parent, SWT.BORDER | SWT.V_SCROLL
					| SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.MULTI);
			chosenFieldsTable.setHeaderVisible(false);
			chosenFieldsTable.setFont(parent.getFont());
			chosenFieldsTable.addSelectionListener(getSelectionListener());
			chosenFieldsTable.addDisposeListener(new DisposeListener() {
				public void widgetDisposed(DisposeEvent event) {
					chosenFieldsTable = null;
				}
			});
			TableColumn column;
			column = new TableColumn(chosenFieldsTable, SWT.CENTER);
			column.setText("Field");
			column.setWidth(200);
		} else {
			checkParent(chosenFieldsTable, parent);
		}
		return chosenFieldsTable;
	}

	private IndexedTableViewer createTableViewer(Table aTable) {
		IndexedTableViewer tableViewer = new IndexedTableViewer(aTable);
		tableViewer.setUseHashlookup(true);
		tableViewer.setColumnProperties(new String[] { "Field" });

		CellEditor[] editors = new CellEditor[1];
		editors[0] = new TextCellEditor(aTable);
		tableViewer.setContentProvider(new EventListContentProvider<String>());
		tableViewer.setLabelProvider(new FIXMessageFieldChooserLabelProvider());
		return tableViewer;
	}

	// public void elementChanged(MMapEntry<String, String> element) {
	// tableViewer.update(element, null);
	// }

	protected List<String> getNewInputObject(Table aTable) {
		List<String> itemsAsList = new BasicEventList<String>();
		TableItem[] selectedItems = aTable.getSelection();
		if (selectedItems != null && selectedItems.length > 0) {
			for (TableItem item : selectedItems) {
				itemsAsList.add(item.getText());
			}
		}
		return itemsAsList;
	}
	
	public int getNumberOfControls() {
		return 4;
	}

	/**
	 * Returns this field editor's selection listener. The listener is created
	 * if nessessary.
	 */
	private SelectionListener getSelectionListener() {
		if (selectionListener == null) {
			createSelectionListener();
		}
		return selectionListener;
	}

	/**
	 * Returns this field editor's shell.
	 * <p>
	 * This method is internal to the framework; subclassers should not call
	 * this method.
	 * </p>
	 * 
	 * @return the shell
	 */
	protected Shell getShell() {
		if (addButton == null) {
			return null;
		}
		return addButton.getShell();
	}

	/**
	 * Notifies that the Remove button has been pressed.
	 */
	private void removePressed() {
		setPresentsDefaultValue(false);
		int index = availableFieldsTable.getSelectionIndex();

		if (index >= 0) {
			// table.remove(index);
			toEntries.remove(index);
			selectionChanged();
		}
	}

	private void selectionChanged() {
		if (availableFieldsTable != null) {
			int fromSize = availableFieldsTable.getItemCount();
			addAllButton.setEnabled(fromSize > 0);
		} else {
			addButton.setEnabled(false);
			addAllButton.setEnabled(false);
		}
		
		if (chosenFieldsTable != null) {
			int toSize = chosenFieldsTable.getItemCount();
			removeButton.setEnabled(toSize > 0);
			removeAllButton.setEnabled(toSize > 0);
			upButton.setEnabled(toSize > 0);
			downButton.setEnabled(toSize > 0);
		} else {
			removeButton.setEnabled(false);
			removeAllButton.setEnabled(false);
			upButton.setEnabled(false);
			downButton.setEnabled(false);
		}
	}

	public void setFocus() {
		if (availableFieldsTable != null) {
			availableFieldsTable.setFocus();
		}
	}

	/**
	 * Moves the currently selected item up or down.
	 * 
	 * @param up
	 *            <code>true</code> if the item should move up, and
	 *            <code>false</code> if it should move down
	 */
	private void swap(boolean up) {
		setPresentsDefaultValue(false);
		int index = chosenFieldsTable.getSelectionIndex();
		int target = up ? index - 1 : index + 1;

		if (index >= 0) {
			TableItem[] selection = chosenFieldsTable.getSelection();
			String toReplace = (String) selection[0].getData();
			Assert.isTrue(selection.length == 1);
			toEntries.remove(index);
			toEntries.add(target, toReplace);
			chosenFieldsTable.setSelection(target);
		}
		selectionChanged();
	}

	/**
	 * Notifies that the Up button has been pressed.
	 */
	private void addAllPressed() {
		swap(true);
	}

	protected void refreshOrderType(char newType) {
		orderType = newType;
		doLoadDefault();
	}

	

//	/*
//	 * @see FieldEditor.setEnabled(boolean,Composite).
//	 */
//	public void setEnabled(boolean enabled, Composite parent) {
//		super.setEnabled(enabled, parent);
//		getTableControl(parent).setEnabled(enabled);
//		addButton.setEnabled(enabled);
//		removeButton.setEnabled(enabled);
//		addAllButton.setEnabled(enabled);
//		removeAllButton.setEnabled(enabled);
//	}
		

}
