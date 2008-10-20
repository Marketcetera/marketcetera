package org.marketcetera.photon.preferences;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
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
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.photon.FIXFieldLocalizer;
import org.marketcetera.photon.Messages;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.ui.EventListContentProvider;
import org.marketcetera.photon.ui.IndexedTableViewer;
import org.marketcetera.quickfix.FIXDataDictionary;
import org.marketcetera.quickfix.FIXMessageUtil;

import ca.odell.glazedlists.BasicEventList;

/* $License$ */

/**
 * 
 * The Editor for choosing various FIX message field columns in the Preferences Page.
 * 
 * @author caroline.leung@softwaregoodness.com
 * @author anshul@marketcetera.com
 *
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class FIXMessageColumnChooserEditor
    extends FieldEditor
    implements Messages
{
	/**
	 * Name to use for a fix field that for whatever reason doesn't have
	 * human field name.
	 */
	private static final String UNKNOWN_FIX_FIELD_NAME = "Unknown"; //$NON-NLS-1$

	protected static final String CUSTOM_FIELD_PREFIX = "Custom Field"; //$NON-NLS-1$
		
	private FIXMessageColumnPreferenceParser parser;
	
	private FIXDataDictionary fixDictionary;

	private String currentSubPageID;
	
	private Table availableFieldsTable;

	private Table chosenFieldsTable;

	private IndexedTableViewer availableFieldsTableViewer;

	private IndexedTableViewer chosenFieldsTableViewer;

	private Composite addRemoveButtonBox;

	private Button addButton;

	private Button removeButton;

	private Button addAllButton;

	private Button removeAllButton;
	
	private Composite upDownButtonBox;

	private Button upButton;

	private Button downButton;

	private SelectionListener selectionListener;
	
	private MouseListener mouseListener;
	
	private Map<String, FIXMessageColumnChooserEditorPage> idToPageMap;	
	
	private BasicEventList<String> filteredAvailableEntries;
	
	private BasicEventList<String> filteredChosenEntries;
	
	private Map<String, Integer> fieldEntryToFieldIDMap;
	
	private static final int TABLE_COLUMN_WIDTH = 205;
	
	private static final int TABLE_HEIGHT = 150;
	
	private DisposeListener disposeListener;

	protected FIXMessageColumnChooserEditor(String name, String labelText,
			Composite parent, String subPageID) {
		init(name, labelText);
		this.fixDictionary = PhotonPlugin.getDefault().getFIXDataDictionary();
		this.currentSubPageID = subPageID;
		this.parser = new FIXMessageColumnPreferenceParser();		
		this.idToPageMap = new HashMap<String, FIXMessageColumnChooserEditorPage>();
		loadFieldEntryToFieldIDMap();
		createControl(parent);
	}

	protected void adjustForNumColumns(int numColumns) {
		Control control = getLabelControl();
		((GridData) control.getLayoutData()).horizontalSpan = numColumns;
		((GridData) availableFieldsTable.getLayoutData()).horizontalSpan = numColumns - 1;
	}

	private void createAddRemoveButtons(Composite box) {
		addButton = createPushButton(box, "Add >");//$NON-NLS-1$
		removeButton = createPushButton(box, "< Remove");//$NON-NLS-1$
		addAllButton = createPushButton(box, "Add All >>");//$NON-NLS-1$
		removeAllButton = createPushButton(box, "<< Remove All");//$NON-NLS-1$
	}

	private void createUpDownButtons(Composite box) {
		upButton = createPushButton(box, "Up");//$NON-NLS-1$
		downButton = createPushButton(box, "Down");//$NON-NLS-1$
	}
	
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
				} else if (widget == availableFieldsTable || widget == chosenFieldsTable) {
					selectionChanged();
				} else if (widget == upButton) {
					upPressed();
				} else if (widget == downButton) {
					downPressed();
				}				
			}
		};
	}
	
	private void createMouseListener() {
		mouseListener = new MouseAdapter() {

			public void mouseDoubleClick(MouseEvent event) {
				Widget widget = event.widget;
				if (widget == availableFieldsTable) {
					doubleClickAvailableField();
				} else if (widget == chosenFieldsTable) {
					doubleClickChosenField();
				}
			}
		};
	}
	
	private void doubleClickAvailableField() {
		addPressed();
	}
	
	private void doubleClickChosenField() {
		removePressed();
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
		
		availableFieldsTableViewer = createTableViewer(availableFieldsTable);
		chosenFieldsTableViewer = createTableViewer(chosenFieldsTable);
		
		upDownButtonBox = getUpDownButtonBoxControl(parent);
		gd = new GridData();
		gd.verticalAlignment = GridData.BEGINNING;
		gd.horizontalSpan = 1;
		upDownButtonBox.setLayoutData(gd);
	}

	protected void doLoad() {
		List<Integer> savedIntFields = parser.getFieldsToShow(currentSubPageID);
		loadTables(savedIntFields, hasBeenLoaded());
	}
	
	protected void doLoadDefault() {
		ScopedPreferenceStore defaultPrefsStore = new ScopedPreferenceStore(
				new DefaultScope(), PhotonPlugin.ID);
		List<Integer> savedIntFields = parser.getFieldsToShow(currentSubPageID,
				defaultPrefsStore);
		loadTables(savedIntFields, false);
	}
	
	protected void loadTables(List<Integer> savedIntFields, boolean loadedBefore) {
		FIXMessageColumnChooserEditorPage currPage;
		if (loadedBefore) {
			currPage = loadPageFromMemory();
		} else {
			currPage = loadPageFromPreference();  
		}

		if (chosenFieldsTable != null) {
			loadChosenFieldsTable(loadedBefore, currPage, savedIntFields);
		}		
		if (availableFieldsTable != null) {
			loadDefaultAvailableFieldsTable(loadedBefore, currPage, savedIntFields);				
		}
		resetFilter();
		selectionChanged();	
	}
	
	private FIXMessageColumnChooserEditorPage loadPageFromMemory() {
		return idToPageMap.get(currentSubPageID);
	}
	
	private FIXMessageColumnChooserEditorPage loadPageFromPreference() {
		FIXMessageColumnChooserEditorPage currPage = new FIXMessageColumnChooserEditorPage(currentSubPageID);
		idToPageMap.put(currentSubPageID, currPage);
		return currPage;
	}

	private FIXMessageColumnChooserEditorPage getCurrentPage() {
		return loadPageFromMemory();
	}
	
	private boolean hasBeenLoaded() {
		return idToPageMap.containsKey(currentSubPageID);
	}

	private void loadDefaultAvailableFieldsTable(boolean loadedBefore,
			FIXMessageColumnChooserEditorPage currPage,
			List<Integer> savedIntFields) {
		availableFieldsTable.removeAll();
		BasicEventList<String> currPageAvailableFieldsEntries = new BasicEventList<String>();
		if (loadedBefore) {
			currPageAvailableFieldsEntries = currPage.getAvailableFieldsList();
		} else {
			Set<String> fieldEntryStrings = fieldEntryToFieldIDMap.keySet();
			for (String fieldEntry : fieldEntryStrings) {
				int fieldID = fieldEntryToFieldIDMap.get(fieldEntry);
				if (!savedIntFields.contains(fieldID)) {
					currPageAvailableFieldsEntries.add(fieldEntry);
				}
			}
		}
		currPage.setAvailableFieldsList(currPageAvailableFieldsEntries);
	}
	
	private void loadFieldEntryToFieldIDMap() {
		fieldEntryToFieldIDMap = new LinkedHashMap<String, Integer>();
		for (int i = 0; i < FIXMessageUtil.getMaxFIXFields(); i++) {
			if (FIXMessageUtil.isValidField(i)) {
				String fieldEntry = getFixFieldDisplayName(i);
				fieldEntryToFieldIDMap.put(fieldEntry, i);
			}
		}
	}
	
	/**
	 * Return the localized display name for the supplied FIX field number.
	 * @param i The FIX field number.
	 * @return The localized display name for the FIX field.
	 */
	private String getFixFieldDisplayName(int i) {
		String humanFieldName = fixDictionary.getHumanFieldName(i);
		StringBuilder sb;
		if(humanFieldName != null) {
			sb = new StringBuilder(
					FIXFieldLocalizer.getLocalizedFIXFieldName(
					humanFieldName));
		} else {
			sb = new StringBuilder(UNKNOWN_FIX_FIELD_NAME);
		}
		
		return  sb.append("(").append(i).append(")").toString(); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	private void addCustomFieldToFieldEntryMap(String fieldName, int fieldID) {
		if (fieldEntryToFieldIDMap != null) {
			fieldEntryToFieldIDMap.put(fieldName, fieldID);
		}
	}
	
	private String getCustomFieldName(int fieldID) {
		return new StringBuffer(CUSTOM_FIELD_PREFIX)
		.append(" (").append(fieldID).append(")") //$NON-NLS-1$ //$NON-NLS-2$
		.toString(); 		

	}
	
	protected void addCustomFieldToAvailableFieldsList(int fieldID) {
		FIXMessageColumnChooserEditorPage currPage = getCurrentPage();
		String fieldEntry = getCustomFieldName(fieldID);
		currPage.getAvailableFieldsList().add(fieldEntry);
		filteredAvailableEntries.add(fieldEntry);
		addCustomFieldToFieldEntryMap(fieldEntry, fieldID);
		availableFieldsTableViewer.refresh(false);	
	}

	private void loadChosenFieldsTable(boolean loadedBefore,
			FIXMessageColumnChooserEditorPage currPage,
			List<Integer> savedIntFields) {
		chosenFieldsTable.removeAll();
		BasicEventList<String> currPageChosenFieldsEntries = new BasicEventList<String>();

		if (loadedBefore) {
			currPageChosenFieldsEntries = currPage.getChosenFieldsList();
		} else {
			currPageChosenFieldsEntries = getChosenFields(savedIntFields);
			currPage.setChosenFieldsList(currPageChosenFieldsEntries);
		}
	}
	
	private BasicEventList<String> getChosenFields(List<Integer> savedIntFields) {
		BasicEventList<String> fieldsList = new BasicEventList<String>();			
		for (int intField : savedIntFields) {
			String fieldName;
			if (FIXMessageUtil.isValidField(intField)) {
				fieldName = getFixFieldDisplayName(intField);				
			} else {
				fieldName = getCustomFieldName(intField);
			}
			fieldsList.add(fieldName);
		}
		return fieldsList;
	}
	
	private FIXMessageColumnChooserEditorPage getCurrentChooserEditorPage() {
		if (!hasBeenLoaded()) {
			return null;
		}
		FIXMessageColumnChooserEditorPage currentPage = loadPageFromMemory();
		return currentPage;
	}
	
	public void resetFilter() {
		FIXMessageColumnChooserEditorPage currentPage = getCurrentChooserEditorPage();
		resetAvailableFilter(currentPage);
		resetChosenFilter(currentPage);
	}
	
	private void resetAvailableFilter(FIXMessageColumnChooserEditorPage currentPage) {
		filteredAvailableEntries = new BasicEventList<String>();
		if(currentPage != null) {
			filteredAvailableEntries.addAll(currentPage.getAvailableFieldsList());
		}
		availableFieldsTableViewer.setInput(filteredAvailableEntries);
	}
	
	private void resetChosenFilter(FIXMessageColumnChooserEditorPage currentPage) {
		filteredChosenEntries = new BasicEventList<String>();
		if(currentPage != null) {
			filteredChosenEntries.addAll(currentPage.getChosenFieldsList());
		}
		chosenFieldsTableViewer.setInput(filteredChosenEntries);
	}
	
	public void applyFilter(String filterText) {
		FIXMessageColumnChooserEditorPage currentPage = getCurrentChooserEditorPage();
		if(currentPage == null) {
			return;
		}
		applyFilter(filterText, currentPage.getAvailableFieldsList(), filteredAvailableEntries);
		availableFieldsTable.deselectAll();
		chosenFieldsTable.deselectAll();
		selectionChanged();
	}
	
	private void applyFilter(String filterText,
			BasicEventList<String> allPossibleEntries,
			BasicEventList<String> currentFilteredEntries) {
		if(filterText == null) { 
			filterText = ""; //$NON-NLS-1$
		}
		for (String possibleEntry : allPossibleEntries) {
			if (isFilterMatch(filterText, possibleEntry)) {
				if (!currentFilteredEntries.contains(possibleEntry)) {
					currentFilteredEntries.add(possibleEntry);
				}
			} else {
				currentFilteredEntries.remove(possibleEntry);
			}
		}
	}
	
	private boolean isFilterMatch(String filterText, String possibleEntry) {
		if (possibleEntry != null) {
			String processedFilterText = filterText.trim().toLowerCase();
			String processedPossibleEntry = possibleEntry.trim()
					.toLowerCase();
			if (processedFilterText.length() == 0
					|| processedPossibleEntry.indexOf(processedFilterText) >= 0) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked") //$NON-NLS-1$
	protected void doStore() {
		Set<String> loadedSubPageIDs = idToPageMap.keySet();
		for (String subPageKey : loadedSubPageIDs) {
			FIXMessageColumnChooserEditorPage chooserPage = idToPageMap.get(subPageKey);			
			List<String> chosenFields = chooserPage.getChosenFieldsList();
			List<Integer> chosenFieldIDs = new BasicEventList<Integer>();
			for (String chosenField : chosenFields) {
				Integer foundID = fieldEntryToFieldIDMap.get(chosenField);
				if (foundID != null) {
					int fieldID = foundID;
					chosenFieldIDs.add(fieldID);
				}
			}
			if (chosenFieldIDs != null) {
				parser.setFieldsToShow(subPageKey, chosenFieldIDs);
			}
		}
	}

	private void removePressed() {
		BasicEventList<String> input = getNewInputObject(chosenFieldsTable);
		removePressedWithInput(input);		
	}

	private void removeAllPressed() {
		BasicEventList<String> input = getAllItemsAsInputList(chosenFieldsTable);
		removePressedWithInput(input);
	}

	private BasicEventList<String> getAllItemsAsInputList(Table aTable) {
		BasicEventList<String> input = new BasicEventList<String>();
		TableItem[] selectedItems = aTable.getItems(); 
		if (selectedItems != null && selectedItems.length > 0) {
			for (TableItem item : selectedItems) {
				input.add(item.getText());
			}
		}
		return input;
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
			addRemoveButtonBox.addDisposeListener(getDisposeListener());

		} else {
			checkParent(addRemoveButtonBox, parent);
		}
		return addRemoveButtonBox;
	}

	private Composite getUpDownButtonBoxControl(Composite parent) {
		if (upDownButtonBox == null) {
			upDownButtonBox = new Composite(parent, SWT.NULL);
			GridLayout layout = new GridLayout();
			layout.marginWidth = 0;
			upDownButtonBox.setLayout(layout);
			createUpDownButtons(upDownButtonBox);
			upDownButtonBox.addDisposeListener(getDisposeListener());

		} else {
			checkParent(upDownButtonBox, parent);
		}
		return upDownButtonBox;
	}

	private Table createAvailableFieldsTable(Composite parent) {
		if (availableFieldsTable == null) {
			availableFieldsTable = createTable(parent);
		} else {
			checkParent(availableFieldsTable, parent);
		}
		return availableFieldsTable;
	}
	
	private Table createChosenFieldsTable(Composite parent) {
		if (chosenFieldsTable == null) {
			chosenFieldsTable = createTable(parent);
		} else {
			checkParent(chosenFieldsTable, parent);
		}
		return chosenFieldsTable;
	}

	private GridData createTableGridData() {
		GridData gd = new GridData();
		gd.verticalAlignment = GridData.BEGINNING;
		gd.horizontalSpan = 1;
		gd.heightHint = TABLE_HEIGHT;
		return gd;
	}
	
	private Table createTable(Composite parent) {
		Table aTable = new Table(parent, SWT.BORDER | SWT.V_SCROLL
				| SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.MULTI);
		aTable.setHeaderVisible(false);
		aTable.setFont(parent.getFont());
		aTable.addSelectionListener(getSelectionListener());
		aTable.addDisposeListener(getDisposeListener());
		aTable.addMouseListener(getMouseListener());
		GridData gd = createTableGridData();
		aTable.setLayoutData(gd);

		TableColumn column;
		column = new TableColumn(aTable, SWT.BEGINNING | SWT.H_SCROLL);
		column.setWidth(TABLE_COLUMN_WIDTH);
		return aTable;
	}
	
	private DisposeListener getDisposeListener() {
		if (disposeListener == null) {
			createDisposeListener();
		}
		return disposeListener;
	}
	
	private void createDisposeListener() {
		disposeListener = new DisposeListener() {
			
			public void widgetDisposed(DisposeEvent event) {
				Widget widget = event.widget;
				if (widget == chosenFieldsTable) {
					chosenFieldsTable = null;
				} else if (widget == availableFieldsTable) {
					availableFieldsTable = null;
				} else if (widget == addRemoveButtonBox) {
					addButton = null;
					removeButton = null;
					addAllButton = null;
					removeAllButton = null;
					addRemoveButtonBox = null;
				} else if (widget == upDownButtonBox) {
					upButton = null;
					downButton = null;
					upDownButtonBox = null;
				}				
			}
		};
	}

	private IndexedTableViewer createTableViewer(Table aTable) {
		IndexedTableViewer tableViewer = new IndexedTableViewer(aTable);
		tableViewer.setUseHashlookup(true);
		tableViewer.setColumnProperties(new String[] { FIELD_LABEL.getText() } );

		CellEditor[] editors = new CellEditor[1];
		editors[0] = new TextCellEditor(aTable);
		tableViewer.setContentProvider(new EventListContentProvider<String>());
		tableViewer.setLabelProvider(new FIXMessageColumnChooserLabelProvider());
		return tableViewer;
	}

	protected BasicEventList<String> getNewInputObject(Table aTable) {
		BasicEventList<String> itemsAsList = new BasicEventList<String>();
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
	 * Returns this field editor's mouse listener. The listener is created
	 * if nessessary.
	 */
	private MouseListener getMouseListener() {
		if (mouseListener == null) {
			createMouseListener();
		}
		return mouseListener;
	}
	
	/**
	 * Returns this field editor's shell.
	 * This method is internal to the framework; subclassers should not call
	 * this method.
	 */
	protected Shell getShell() {
		if (addButton == null) {
			return null;
		}
		return addButton.getShell();
	}

	private void selectionChanged() {
		if (availableFieldsTable != null) {
			int availableFieldsTableSize = filteredAvailableEntries.size();
			int availableFieldsTableSelectionCount = availableFieldsTable.getSelectionCount();				
			addButton.setEnabled(availableFieldsTableSelectionCount > 0);				
			addAllButton.setEnabled(availableFieldsTableSize > 0);
			
		} else {
			addButton.setEnabled(false);
			addAllButton.setEnabled(false);
		}
		
		if (chosenFieldsTable != null) {
			int chosenFieldsTableSize = filteredChosenEntries.size();
			int chosenFieldsTableSelectionCount = chosenFieldsTable.getSelectionCount();				
			removeButton.setEnabled(chosenFieldsTableSelectionCount > 0);
			removeAllButton.setEnabled(chosenFieldsTableSize > 0);
			upButton.setEnabled(chosenFieldsTableSelectionCount > 0);
			downButton.setEnabled(chosenFieldsTableSelectionCount > 0);

		} else {
			removeButton.setEnabled(false);
			removeAllButton.setEnabled(false);
			upButton.setEnabled(false);
			downButton.setEnabled(false);
		}
	}

	public void setFocus() {
		selectionChanged();
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
        int filteredIndex = chosenFieldsTable.getSelectionIndex();
        if ((up && filteredIndex <= 0)
				|| (!up && filteredIndex >= chosenFieldsTable.getItemCount() - 1)) {
			return;   
		}
        int filteredTargetIndex = up ? filteredIndex - 1 : filteredIndex + 1;
        if(filteredTargetIndex < 0) {
            filteredTargetIndex = 0;
        }
        if (filteredIndex >= 0) {
            TableItem[] selection = chosenFieldsTable.getSelection();

            if (selection.length != 1) {
    			PhotonPlugin.getMainConsoleLogger().warn(MULTI_SELECT_NOT_ENABLED.getText());
            	return;
            }
            String toReplace = (String) selection[0].getData();            	
            
            filteredChosenEntries.remove(filteredIndex);
            filteredChosenEntries.add(filteredTargetIndex, toReplace);
            
			FIXMessageColumnChooserEditorPage currPage = getCurrentPage();
			List<String> chosenFieldsList = currPage.getChosenFieldsList();

            int actualIndex = chosenFieldsList.indexOf(toReplace);
            int actualTargetIndex = up ? actualIndex - 1 : actualIndex + 1;
            if(actualTargetIndex < 0) {
                actualTargetIndex = 0;
            }
            chosenFieldsList.remove(actualIndex );
            chosenFieldsList.add(actualTargetIndex, toReplace);
            
            chosenFieldsTable.setSelection(filteredTargetIndex);
			chosenFieldsTableViewer.refresh(false);	
            chosenFieldsTable.setSelection(filteredTargetIndex);

        }
        selectionChanged();
    }

    private void removePressedWithInput(BasicEventList<String> input) {
		if (input != null && input.size() > 0) {
			setPresentsDefaultValue(false);		
			FIXMessageColumnChooserEditorPage currPage = getCurrentPage();
			currPage.getChosenFieldsList().removeAll(input);
			currPage.getAvailableFieldsList().addAll(input);

			filteredAvailableEntries.addAll(input);
			filteredChosenEntries.removeAll(input);
			availableFieldsTableViewer.refresh(false);
			chosenFieldsTableViewer.refresh(false);	
			selectionChanged();
		}
	}
    
    private void addPressedWithInput(BasicEventList<String> input) {
		if (input != null && input.size() > 0) {
			setPresentsDefaultValue(false);		
			FIXMessageColumnChooserEditorPage currPage = getCurrentPage();
			currPage.getAvailableFieldsList().removeAll(input);
			currPage.getChosenFieldsList().addAll(input);

			filteredAvailableEntries.removeAll(input);
			filteredChosenEntries.addAll(input);
			availableFieldsTableViewer.refresh(false);
			chosenFieldsTableViewer.refresh(false);	
			selectionChanged();
		}
	}

    private void addPressed() {
		BasicEventList<String> input = getNewInputObject(availableFieldsTable);
		addPressedWithInput(input);
	}
    
	@SuppressWarnings("unchecked") //$NON-NLS-1$
	private void addAllPressed() {		
		BasicEventList<String> input = getAllItemsAsInputList(availableFieldsTable);
		addPressedWithInput(input);
	}
	
	private void upPressed() {
		swap(true);
	}
	
	private void downPressed() {
		swap(false);
	}

	protected void changeSubPage(String subPageID) {
		this.currentSubPageID = subPageID;
		doLoad();
	}

}
