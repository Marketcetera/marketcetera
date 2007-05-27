package org.marketcetera.photon.preferences;

import java.io.File;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.Assert;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
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

import ca.odell.glazedlists.gui.AdvancedTableFormat;

public class FIXMessageFieldColumnChooserEditor extends FieldEditor {

	private char orderType;
	
	private Table chooseFromTable;

	private Table chooseToTable;

	private IndexedTableViewer chooseFromTableViewer;

	private IndexedTableViewer chooseToTableViewer;

	/**
	 * The button box containing the Add, Add All, Remove, and Remove All
	 * buttons; <code>null</code> if none (before creation or after disposal).
	 */
	private Composite buttonBox;

	private Button addButton;

	private Button removeButton;

	private Button addAllButton;

	private Button removeAllButton;

	private SelectionListener selectionListener;

	private List<String> toEntries;
	
	private List<String> fromEntries;

	/**
	 * Creates a new list field editor
	 */
	protected FIXMessageFieldColumnChooserEditor(char orderType) {
		this.orderType = orderType;
	}

	/**
	 * Creates a list field editor.
	 * 
	 * @param name
	 *            the name of the preference this field editor works on
	 * @param labelText
	 *            the label text of the field editor
	 * @param parent
	 *            the parent of the field edMapFieldEditortrol
	 */
	protected FIXMessageFieldColumnChooserEditor(String name, String labelText,
			Composite parent) {
		init(name, labelText);
		createControl(parent);
	}

	/**
	 * Notifies that the Add button has been pressed.
	 */
	private void addPressed() {
		setPresentsDefaultValue(false);
		String input = getNewInputObject();

		if (input != null) {
			int index = chooseFromTable.getSelectionIndex();
			if (index >= 0) {
				toEntries.add(index + 1, input);
			} else {
				toEntries.add(0, input);
			}
			selectionChanged();
		}
	}

	protected void adjustForNumColumns(int numColumns) {
		Control control = getLabelControl();
		((GridData) control.getLayoutData()).horizontalSpan = numColumns;
		((GridData) chooseFromTable.getLayoutData()).horizontalSpan = numColumns - 1;
	}

	/**
	 * Creates the Add, Remove, Up, and Down button in the given button box.
	 * 
	 * @param box
	 *            the box for the buttons
	 */
	private void createButtons(Composite box) {
		addButton = createPushButton(box, "Add");//$NON-NLS-1$
		removeButton = createPushButton(box, "Remove");//$NON-NLS-1$
		addAllButton = createPushButton(box, "Add All");//$NON-NLS-1$
		removeAllButton = createPushButton(box, "Remove All");//$NON-NLS-1$
	}

	/**
	 * Combines the given list of items into a single string. This method is the
	 * converse of <code>parseString</code>.
	 * <p>
	 * Subclasses must implement this method.
	 * </p>
	 * 
	 * @param items
	 *            the list of items
	 * @return the combined string
	 * @see #parseString
	 */
	protected String createList(String[] items) {
		StringBuffer fields = new StringBuffer("");//$NON-NLS-1$

		for (int i = 0; i < items.length; i++) {
			fields.append(items[i]);
			fields.append(File.pathSeparator);
		}
		return fields.toString();
	}

	/**
	 * Helper method to create a push button.
	 * 
	 * @param parent
	 *            the parent control
	 * @param key
	 *            the resource name used to supply the button's label text
	 * @return Button
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

	/**
	 * Creates a selection listener.
	 */
	public void createSelectionListener() {
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
				} else if (widget == chooseFromTable) {
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

		chooseFromTable = createChooseFromTable(parent);
		chooseToTable = createChooseFromTable(parent);
		
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.verticalAlignment = GridData.FILL;
		gd.horizontalSpan = numColumns - 1;
		gd.grabExcessHorizontalSpace = true;
		chooseFromTable.setLayoutData(gd);
		chooseToTable.setLayoutData(gd);

		chooseFromTableViewer = createTableViewer(chooseFromTable);
		chooseToTableViewer = createTableViewer(chooseToTable);
		
		buttonBox = getButtonBoxControl(parent);
		gd = new GridData();
		gd.verticalAlignment = GridData.BEGINNING;
		buttonBox.setLayoutData(gd);
	}

	protected void doLoad() {
		List<Integer> savedIntFields = getUserSelectedFields(orderType);
		FIXDataDictionary fixDictionary = PhotonPlugin.getDefault().getFIXDataDictionary();
		if (chooseToTable != null) {
			loadChooseToTable(savedIntFields, fixDictionary);
		}		
		if (chooseFromTable != null) {
			loadChooseFromTable(savedIntFields, fixDictionary);				
		}
	}
	
	protected void doLoadDefault() {
		List<Integer> savedIntFields = getUserSelectedFields(orderType);
		FIXDataDictionary fixDictionary = PhotonPlugin.getDefault().getFIXDataDictionary();
		if (chooseToTable != null) {
			chooseToTable.removeAll();
			loadChooseToTable(savedIntFields, fixDictionary);
		}		
		if (chooseFromTable != null) {
			chooseFromTable.removeAll();
			loadChooseFromTable(savedIntFields, fixDictionary);				
		}
	}

	private void loadChooseFromTable(List<Integer> savedIntFields, FIXDataDictionary fixDictionary) {
		fromEntries = new ArrayList<String>();		
		for (int i = 0; i < 2000; i++) {
			if (!savedIntFields.contains(i) && FIXMessageUtil.isValidField(i)) {
				String fieldName = fixDictionary.getHumanFieldName(i);
				fromEntries.add(fieldName + "(" + i + ")");
			}
		}
		chooseFromTableViewer.setInput(fromEntries);			
	}

	private void loadChooseToTable(List<Integer> savedIntFields, FIXDataDictionary fixDictionary) {
		if (savedIntFields != null && savedIntFields.size() > 0) {
			toEntries = new ArrayList<String>();			
			for (int intField : savedIntFields) {
				String fieldName = fixDictionary.getHumanFieldName(intField);
				toEntries.add(fieldName + " (" + intField + ")");
			}
			chooseToTableViewer.setInput(toEntries);
		}
	}

	
	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	@SuppressWarnings("unchecked")
	protected void doStore() {
		String[] items = (String[]) chooseFromTableViewer
				.getInput();
		String s = createList(items);
		if (s != null) {
			getPreferenceStore().setValue(getPreferenceName(), s);
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
	 * 
	 * @param parent
	 *            the parent control
	 * @return the button box
	 */
	public Composite getButtonBoxControl(Composite parent) {
		if (buttonBox == null) {
			buttonBox = new Composite(parent, SWT.NULL);
			GridLayout layout = new GridLayout();
			layout.marginWidth = 0;
			buttonBox.setLayout(layout);
			createButtons(buttonBox);
			buttonBox.addDisposeListener(new DisposeListener() {
				public void widgetDisposed(DisposeEvent event) {
					addButton = null;
					removeButton = null;
					addAllButton = null;
					removeAllButton = null;
					buttonBox = null;
				}
			});

		} else {
			checkParent(buttonBox, parent);
		}

		selectionChanged();
		return buttonBox;
	}

	/**
	 * Returns this field editor's list control.
	 * 
	 * @param parent
	 *            the parent control
	 * @return the list control
	 */
	public Table createChooseFromTable(Composite parent) {
		if (chooseFromTable == null) {
			chooseFromTable = new Table(parent, SWT.BORDER | SWT.SINGLE
					| SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
			chooseFromTable.setHeaderVisible(false);
			chooseFromTable.setFont(parent.getFont());
			chooseFromTable.addSelectionListener(getSelectionListener());
			chooseFromTable.addDisposeListener(new DisposeListener() {
				public void widgetDisposed(DisposeEvent event) {
					chooseFromTable = null;
				}
			});
			// 2nd column with task Description
			TableColumn column;
			column = new TableColumn(chooseFromTable, SWT.LEFT);
			column.setText("Field");
			column.setWidth(100);
		} else {
			checkParent(chooseFromTable, parent);
		}
		return chooseFromTable;
	}
	
	public Table createChooseToTable(Composite parent) {
		if (chooseToTable == null) {
			chooseToTable = new Table(parent, SWT.BORDER | SWT.SINGLE
					| SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
			chooseToTable.setHeaderVisible(false);
			chooseToTable.setFont(parent.getFont());
			chooseToTable.addSelectionListener(getSelectionListener());
			chooseToTable.addDisposeListener(new DisposeListener() {
				public void widgetDisposed(DisposeEvent event) {
					chooseToTable = null;
				}
			});
			// 2nd column with task Description
			TableColumn column;
			column = new TableColumn(chooseToTable, SWT.LEFT);
			column.setText("Field");
			column.setWidth(100);
		} else {
			checkParent(chooseToTable, parent);
		}
		return chooseToTable;
	}

	private IndexedTableViewer createTableViewer(Table aTable) {
		IndexedTableViewer tableViewer = new IndexedTableViewer(aTable);
		tableViewer.setUseHashlookup(true);
		tableViewer.setColumnProperties(new String[] { "Field" });

		CellEditor[] editors = new CellEditor[1];
		editors[0] = null;
		editors[1] = new TextCellEditor(aTable);
		tableViewer.setContentProvider(new EventListContentProvider<String>());
		tableViewer.setLabelProvider(new FIXMessageFieldChooserLabelProvider());
		return tableViewer;
	}

	// public void elementChanged(MMapEntry<String, String> element) {
	// tableViewer.update(element, null);
	// }

	protected ITableLabelProvider getTableLabelProvider() {
		return new ITableLabelProvider() {

			public Image getColumnImage(Object element, int columnIndex) {
				return null;
			}

			@SuppressWarnings("unchecked")
			public String getColumnText(Object element, int columnIndex) {
				return (element instanceof Map.Entry) ? columnIndex == 1 ? ((Map.Entry<String, String>) element)
						.getKey()
						: ((Map.Entry<String, String>) element).getValue()
						: null;
			}

			public void addListener(ILabelProviderListener listener) {
			}

			public void dispose() {
			}

			public boolean isLabelProperty(Object element, String property) {
				return false;
			}

			public void removeListener(ILabelProviderListener listener) {
			}

		};
	}

	/**
	 * Creates and returns a new item for the list.
	 * <p>
	 * Subclasses must implement this method.
	 * </p>
	 * 
	 * @return a new item
	 */
	protected String getNewInputObject() {
//        DirectoryDialog dialog = new DirectoryDialog(getShell());
//        if (dirChooserLabelText != null) {
//			dialog.setMessage(dirChooserLabelText);
//		}
//        if (lastPath != null) {
//            if (new File(lastPath).exists()) {
//				dialog.setFilterPath(lastPath);
//			}
//        }
//        String dir = dialog.open();
//        if (dir != null) {
//            dir = dir.trim();
//            if (dir.length() == 0) {
//				return null;
//			}
//            lastPath = dir;
//        }
//        return dir;
		return "";
    }
	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	public int getNumberOfControls() {
		return 2;
	}

	/**
	 * Returns this field editor's selection listener. The listener is created
	 * if nessessary.
	 * 
	 * @return the selection listener
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
		int index = chooseFromTable.getSelectionIndex();

		if (index >= 0) {
			// table.remove(index);
			toEntries.remove(index);
			selectionChanged();
		}
	}

	/**
	 * Notifies that the list selection has changed.
	 */
	private void selectionChanged() {

		int index = chooseFromTable.getSelectionIndex();
		int size = chooseFromTable.getItemCount();

		removeButton.setEnabled(index >= 0);
		addAllButton.setEnabled(size > 1 && index > 0);
		removeAllButton.setEnabled(size > 1 && index >= 0 && index < size - 1);
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	public void setFocus() {
		if (chooseFromTable != null) {
			chooseFromTable.setFocus();
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
		int index = chooseToTable.getSelectionIndex();
		int target = up ? index - 1 : index + 1;

		if (index >= 0) {
			TableItem[] selection = chooseToTable.getSelection();
			String toReplace = (String) selection[0].getData();
			Assert.isTrue(selection.length == 1);
			toEntries.remove(index);
			toEntries.add(target, toReplace);
			chooseToTable.setSelection(target);
		}
		selectionChanged();
	}

	/**
	 * Notifies that the Up button has been pressed.
	 */
	private void addAllPressed() {
		swap(true);
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

	protected class MapTableFormat implements AdvancedTableFormat {

		public static final int FIELD_COLUMN = 0;

		public int getColumnCount() {
			return 2;
		}

		public String getColumnName(int column) {
			switch (column) {
			case FIELD_COLUMN:
				return "Field";
			default:
				return null;
			}
		}

		public Object getColumnValue(Object obj, int column) {
			switch (column) {
			case FIELD_COLUMN:
				return (String) obj;
			default:
				return null;
			}
		}

		public Class getColumnClass(int arg0) {
			return String.class;
		}

		public Comparator getColumnComparator(int arg0) {
			return Collator.getInstance();
		}

	}
	
	private void saveUserSelectedFields(char orderType, List<Integer> fields) {
	}
	
	private List<Integer> getUserSelectedFields(char orderType) {
		return new ArrayList<Integer>();
	}
}
