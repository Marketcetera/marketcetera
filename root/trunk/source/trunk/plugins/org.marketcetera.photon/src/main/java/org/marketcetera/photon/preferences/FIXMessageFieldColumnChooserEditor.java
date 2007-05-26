package org.marketcetera.photon.preferences;


import java.text.Collator;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;

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
import org.marketcetera.core.MMapEntry;
import org.marketcetera.photon.ui.EventListContentProvider;
import org.marketcetera.photon.ui.IndexedTableViewer;
import org.marketcetera.photon.ui.MapEntryLabelProvider;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.gui.AdvancedTableFormat;
import ca.odell.glazedlists.gui.WritableTableFormat;


public abstract class FIXMessageFieldColumnChooserEditor extends FieldEditor /*implements IElementChangeListener<MMapEntry<String, String>> */{

    private Table chooseFromTable;
    
    private Table chooseToTable;
    
    private IndexedTableViewer chooseFromTableViewer;

    private IndexedTableViewer chooseToTableViewer;

    /**
     * The button box containing the Add, Add All, Remove, and Remove All buttons;
     * <code>null</code> if none (before creation or after disposal).
     */
    private Composite buttonBox;

    private Button addButton;

    private Button removeButton;

    private Button addAllButton;

    private Button removeAllButton;

    /**
     * The selection listener.
     */
    private SelectionListener selectionListener;

    EventList<Map.Entry<String, String>> entries;
    
	// Set the table column property names
	private final String KEY_COLUMN 		= "key";
	private final String VALUE_COLUMN 		= "value";

//	// Set column names
//	private String[] columnNames = new String[] { 
//			KEY_COLUMN, 
//			VALUE_COLUMN
//			};

    /**
     * Creates a new list field editor 
     */
    protected FIXMessageFieldColumnChooserEditor() {
    }

    /**
     * Creates a list field editor.
     * 
     * @param name the name of the preference this field editor works on
     * @param labelText the label text of the field editor
     * @param parent the parent of the field edMapFieldEditortrol
     */
    protected FIXMessageFieldColumnChooserEditor(String name, String labelText, Composite parent) {
        init(name, labelText);
        createControl(parent);
    }

    
    protected boolean isDuplicateKeyAllowed() {
		return true;
	}

	/**
     * Notifies that the Add button has been pressed.
     */
    private void addPressed() {
        setPresentsDefaultValue(false);
        Entry<String, String> input = getNewInputObject();

        if (input != null) {
        	if (isDuplicateKeyAllowed() || !hasEntryKey(input.getKey())) {
				int index = chooseFromTable.getSelectionIndex();
				if (index >= 0) {
					entries.add(index + 1, input);
				} else {
					entries.add(0, input);
				}
				selectionChanged();
			}
        }
    }

    /*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
    protected void adjustForNumColumns(int numColumns) {
        Control control = getLabelControl();
        ((GridData) control.getLayoutData()).horizontalSpan = numColumns;
        ((GridData) chooseFromTable.getLayoutData()).horizontalSpan = numColumns - 1;
    }

    /**
     * Creates the Add, Remove, Up, and Down button in the given button box.
     *
     * @param box the box for the buttons
     */
    private void createButtons(Composite box) {
        addButton = createPushButton(box, "Add");//$NON-NLS-1$
        removeButton = createPushButton(box, "Remove");//$NON-NLS-1$
        addAllButton = createPushButton(box, "Add All");//$NON-NLS-1$
        removeAllButton = createPushButton(box, "Remove All");//$NON-NLS-1$
    }

    /**
     * Combines the given list of items into a single string.
     * This method is the converse of <code>parseString</code>. 
     * <p>
     * Subclasses must implement this method.
     * </p>
     *
     * @param items the list of items
     * @return the combined string
     * @see #parseString
     */
    protected abstract String createMap(EventList<Map.Entry<String, String>> entries);

    /**
     * Helper method to create a push button.
     * 
     * @param parent the parent control
     * @param key the resource name used to supply the button's label text
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

    /* (non-Javadoc)
     * Method declared on FieldEditor.
     */
    protected void doFillIntoGrid(Composite parent, int numColumns) {
        Control control = getLabelControl(parent);
        GridData gd = new GridData();
        gd.horizontalSpan = numColumns;
        control.setLayoutData(gd);

        chooseFromTable = getTableControl(parent);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.verticalAlignment = GridData.FILL;
        gd.horizontalSpan = numColumns - 1;
        gd.grabExcessHorizontalSpace = true;
        chooseFromTable.setLayoutData(gd);

        createTableViewer();
        
        buttonBox = getButtonBoxControl(parent);
        gd = new GridData();
        gd.verticalAlignment = GridData.BEGINNING;
        buttonBox.setLayoutData(gd);
    }

    /* (non-Javadoc)
     * Method declared on FieldEditor.
     */
    protected void doLoad() {
        if (chooseFromTable != null) {
            String s = getPreferenceStore().getString(getPreferenceName());
        	
            entries = parseString(s);
            chooseFromTableViewer.setInput(entries);
        }
    }

    /* (non-Javadoc)
     * Method declared on FieldEditor.
     */
    protected void doLoadDefault() {
        if (chooseFromTable != null) {
            chooseFromTable.removeAll();
            String s = getPreferenceStore().getDefaultString(
                    getPreferenceName());
            entries = parseString(s);
            chooseFromTableViewer.setInput(entries);
        }
    }
    
    private boolean hasEntryKey(String entryKey) {
    	if( entryKey == null || entries == null ) {
			return false;
		}
    	for( Map.Entry<String, String> entry : entries ) {
    		if( entry != null ) {
    			String key = entry.getKey();
    			if( entryKey.equals(key)) {
    				return true;
    			}
    		}
    	}
    	return false;
    }

    /* (non-Javadoc)
     * Method declared on FieldEditor.
     */
	@SuppressWarnings("unchecked")
	protected void doStore() {
    	EventList<Map.Entry<String, String>> items = (EventList<Map.Entry<String, String>>) chooseFromTableViewer.getInput();
        String s = createMap(items);
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
     * Returns this field editor's button box containing the Add, Remove,
     * Up, and Down button.
     *
     * @param parent the parent control
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
     * @param parent the parent control
     * @return the list control
     */
    public Table getTableControl(Composite parent) {
        if (chooseFromTable == null) {
        	chooseFromTable = new Table(parent, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL
                    | SWT.H_SCROLL | SWT.FULL_SELECTION );
        	chooseFromTable.setHeaderVisible(false);
        	chooseFromTable.setFont(parent.getFont());
        	chooseFromTable.addSelectionListener(getSelectionListener());
        	chooseFromTable.addDisposeListener(new DisposeListener() {
                public void widgetDisposed(DisposeEvent event) {
                	chooseFromTable = null;
                }
            });
        	chooseFromTable.setHeaderVisible(true);
    		// 2nd column with task Description
    		TableColumn column;
			column = new TableColumn(chooseFromTable, SWT.LEFT);
    		column.setText("Key");
    		column.setWidth(100);

    		column = new TableColumn(chooseFromTable, SWT.LEFT);
    		column.setText("Value");
    		column.setWidth(100);
        } else {
            checkParent(chooseFromTable, parent);
        }
        return chooseFromTable;
    }
    
	/**
	 * Create the TableViewer 
	 */
	private void createTableViewer() {

		chooseFromTableViewer = new IndexedTableViewer(chooseFromTable);
		chooseFromTableViewer.setUseHashlookup(true);
		
		chooseFromTableViewer.setColumnProperties(new String[]{"Fix Field"});

		CellEditor[] editors = new CellEditor[1];
		editors[0] = null;
		editors[1] = new TextCellEditor(chooseFromTable);

		// Assign the cell editors to the viewer 
		//tableViewer.setCellEditors(editors);
		// Set the cell modifier for the viewer
		//tableViewer.setCellModifier(new BeanCellModifier<MMapEntry<String,String>>(this));

		chooseFromTableViewer.setContentProvider(new EventListContentProvider<Map.Entry>());
        chooseFromTableViewer.setLabelProvider(new MapEntryLabelProvider());
	}
 
//    public void elementChanged(MMapEntry<String, String> element) {
//    	tableViewer.update(element, null);
//    }

	protected ITableLabelProvider getTableLabelProvider(){
    	return new ITableLabelProvider(){

			public Image getColumnImage(Object element, int columnIndex) {
				return null;
			}

			@SuppressWarnings("unchecked")
			public String getColumnText(Object element, int columnIndex) {
				return (element instanceof Map.Entry) ? 
						columnIndex == 1 ? ((Map.Entry<String,String>)element).getKey() : ((Map.Entry<String,String>)element).getValue()
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
    protected abstract Entry<String, String> getNewInputObject();

    /* (non-Javadoc)
     * Method declared on FieldEditor.
     */
    public int getNumberOfControls() {
        return 2;
    }

    /**
     * Returns this field editor's selection listener.
     * The listener is created if nessessary.
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
     * Splits the given string into a list of strings.
     * This method is the converse of <code>createList</code>. 
     * <p>
     * Subclasses must implement this method.
     * </p>
     *
     * @param stringList the string
     * @return an array of <code>String</code>
     * @see #createList
     */
    protected abstract EventList<Map.Entry<String, String>> parseString(String stringList);

    /**
     * Notifies that the Remove button has been pressed.
     */
    private void removePressed() {
        setPresentsDefaultValue(false);
        int index = chooseFromTable.getSelectionIndex();
        
        if (index >= 0) {
            //table.remove(index);
        	entries.remove(index);
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

    /* (non-Javadoc)
     * Method declared on FieldEditor.
     */
    public void setFocus() {
        if (chooseFromTable != null) {
            chooseFromTable.setFocus();
        }
    }

    /**
     * Moves the currently selected item up or down.
     *
     * @param up <code>true</code> if the item should move up,
     *  and <code>false</code> if it should move down
     */
    private void swap(boolean up) {
        setPresentsDefaultValue(false);
        int index = chooseFromTable.getSelectionIndex();
        int target = up ? index - 1 : index + 1;

        if (index >= 0) {
            TableItem[] selection = chooseFromTable.getSelection();
            Entry<String, String> toReplace = (Entry<String, String>) selection[0].getData();
            Assert.isTrue(selection.length == 1);
            entries.remove(index);
			entries.add(target, toReplace);
            chooseFromTable.setSelection(target);
        }
        selectionChanged();
    }

    /**
     * Notifies that the Up button has been pressed.
     */
    private void addAllPressed() {
        swap(true);
    }

    /*
     * @see FieldEditor.setEnabled(boolean,Composite).
     */
    public void setEnabled(boolean enabled, Composite parent) {
        super.setEnabled(enabled, parent);
        getTableControl(parent).setEnabled(enabled);
        addButton.setEnabled(enabled);
        removeButton.setEnabled(enabled);
        addAllButton.setEnabled(enabled);
        removeAllButton.setEnabled(enabled);
    }
    
    
	
	protected class MapTableFormat implements WritableTableFormat, AdvancedTableFormat{

		public static final int KEY_COLUMN = 0;
		public static final int VALUE_COLUMN = 1;
		
		public boolean isEditable(Object arg0, int arg1) {
			return true;
		}

		public Object setColumnValue(Object baseObject, Object editedValue,
				int column) {
			switch (column) {
			case KEY_COLUMN:
				return new MMapEntry<String, String>((String) editedValue,
						((Map.Entry<String, String>) baseObject).getValue());
			case VALUE_COLUMN:
				((MMapEntry<String, String>) baseObject)
						.setValue(((String) editedValue));
				return baseObject;
			default:
				return null;
			}
		}

		public int getColumnCount() {
			return 2;
		}

		public String getColumnName(int column) {
			switch(column){
			case KEY_COLUMN:
				return "Key";
			case VALUE_COLUMN:
				return "Value";
			default:
				return null;
			}
		}

		public Object getColumnValue(Object obj, int column) {
			switch(column){
			case KEY_COLUMN:
				return ((Map.Entry<String, String>)obj).getKey();
			case VALUE_COLUMN:
				return ((Map.Entry<String, String>)obj).getValue();
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
}
