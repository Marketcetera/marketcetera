package org.marketcetera.photon.views;

import java.util.concurrent.Callable;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.ViewPart;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.messagehistory.ReportHolder;
import org.marketcetera.photon.Messages;
import org.marketcetera.photon.ui.EventListContentProvider;
import org.marketcetera.photon.ui.IndexedTableViewer;
import org.marketcetera.photon.ui.MessageListTableFormat;
import org.marketcetera.photon.ui.TableComparatorChooser;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.gui.TableFormat;

/* $License$ */

@ClassVersion("$Id$") //$NON-NLS-1$
public abstract class MessagesViewBase<T>
    extends ViewPart
    implements Messages
{

	public static final String COLUMN_ORDER_KEY = "COLUMN_ORDER";  //$NON-NLS-1$
	public static final String COLUMN_ORDER_DELIMITER = ",";  //$NON-NLS-1$
	public static final String COLUMN_WIDTH_KEY = "COLUMN_WIDTH";  //$NON-NLS-1$
	public static final String COLUMN_WIDTH_DELIMITER = ",";  //$NON-NLS-1$

	public static final String SORT_BY_COLUMN_KEY = "SORT_BY_COLUMN";  //$NON-NLS-1$

	private Table messageTable;
	private IndexedTableViewer messagesViewer;
	private IToolBarManager toolBarManager;
	private TableFormat<T> tableFormat;
	private TableComparatorChooser<T> chooser;
	private Clipboard clipboard;
	private CopyMessagesAction copyMessagesAction;
	private EventList<T> rawInputList;
	private final boolean sortableColumns;
	private IMemento viewStateMemento; 

    private IAction selectAllAction;

	public MessagesViewBase()
	{
		this(true);
	}

    public MessagesViewBase(boolean sortableColumns) {
		this.sortableColumns = sortableColumns;
    	
    }

	protected void formatTable(Table messageTable) {
        messageTable.getVerticalBar().setEnabled(true);
        messageTable.setBackground(
        		messageTable.getDisplay().getSystemColor(
						SWT.COLOR_INFO_BACKGROUND));
        messageTable.setForeground(
        		messageTable.getDisplay().getSystemColor(
						SWT.COLOR_INFO_FOREGROUND));

        messageTable.setHeaderVisible(true);

		for (int i = 0; i < messageTable.getColumnCount(); i++) {
			messageTable.getColumn(i).setMoveable(true);
		}
    }

	@SuppressWarnings("unchecked")  //$NON-NLS-1$
	@Override
	public void createPartControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().applyTo(composite);
		
		GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		composite.setLayoutData(gridData);

		messageTable = createMessageTable(composite);
		messagesViewer = createTableViewer(messageTable, getEnumValues());
		
		tableFormat = (TableFormat<T>)messagesViewer.getLabelProvider();
		formatTable(messageTable);
		restoreColumnWidth(viewStateMemento);
		restoreColumnOrder(viewStateMemento);
		
        toolBarManager = getViewSite().getActionBars().getToolBarManager();
		initializeToolBar(toolBarManager);
		
		copyMessagesAction = new CopyMessagesAction(getClipboard(),
		                                            messageTable,
		                                            COPY_LABEL.getText());
		IWorkbench workbench = PlatformUI.getWorkbench();
		ISharedImages platformImages = workbench.getSharedImages();
		copyMessagesAction.setImageDescriptor(platformImages .getImageDescriptor(ISharedImages.IMG_TOOL_COPY));
		copyMessagesAction.setDisabledImageDescriptor(platformImages.getImageDescriptor(ISharedImages.IMG_TOOL_COPY_DISABLED));

		Object menuObj = messageTable.getData(MenuManager.class.toString());
		if (menuObj != null && menuObj instanceof MenuManager) {
			MenuManager menuManager = (MenuManager) menuObj;
			menuManager.add(copyMessagesAction);
		}
		
		createSelectAllAction();
		hookGlobalActions();
	}

	protected abstract void initializeToolBar(IToolBarManager theToolBarManager);
	
	protected abstract Enum<?>[] getEnumValues();
	
	
	protected void packColumns(final Table table) {
		for (int i = 0; i < table.getColumnCount(); i++) {
			table.getColumn(i).pack();
		}
	}

	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);
		
		this.viewStateMemento = memento;
	}

	@Override
	public void saveState(IMemento memento) {
		super.saveState(memento);
		
		saveColumnOrder(memento);
		saveSortByColumn(memento);
		saveColumnWidth(memento);
	}

	protected String serializeColumnOrder(int[] columnOrder) {
		StringBuilder sb = new StringBuilder();
		for(int columnNumber : columnOrder) {
			sb.append(columnNumber);
			sb.append(COLUMN_ORDER_DELIMITER);
		}
		return sb.toString();
	}

	protected int[] deserializeDelimitedIntegersIntoArray(String delimiter, String delimitedValue) {
		if (delimitedValue == null) {
			return new int[0];
		}
		String[] returnAsStringArray = delimitedValue.split(delimiter);
		if (returnAsStringArray == null || returnAsStringArray.length == 0) {
			return new int[0];
		}
		int[] returnAsIntArray = new int[returnAsStringArray.length];
		for(int index = 0; index < returnAsIntArray.length; ++index)  {
			try {
				returnAsIntArray[index] = Integer.parseInt(returnAsStringArray[index]);
			}
			catch(Exception anyException) {
				// TODO Log?
				// org.marketcetera.photon.PhotonPlugin.getMainConsoleLogger().warn("Failed to load column order.", anyException);
				return new int[0];
			}
		}
		return returnAsIntArray;
	}

	protected int[] deserializeColumnOrder(String delimitedValue) {
		return deserializeDelimitedIntegersIntoArray(COLUMN_ORDER_DELIMITER, delimitedValue);
	}

	protected void saveColumnOrder(IMemento memento) {
		if (memento == null) 
			return;
		int[] columnOrder = messageTable.getColumnOrder();
		String serializedColumnOrder = serializeColumnOrder(columnOrder);
		memento.putString(COLUMN_ORDER_KEY, serializedColumnOrder);
	}
	
	protected void restoreColumnOrder(IMemento memento) {
		try {
			if (memento == null)
				return;
			String delimitedColumnOrder = memento.getString(COLUMN_ORDER_KEY);
			int[] columnOrder = deserializeColumnOrder(delimitedColumnOrder);
			if(columnOrder != null && columnOrder.length > 0) {
				messageTable.setColumnOrder(columnOrder);
			}
		} catch (Throwable t){
			// do nothing
		}
	}

	
	protected void restoreSortByColumn(IMemento memento) {
		if (memento == null)
			return;
		String sortByColumn = memento.getString(SORT_BY_COLUMN_KEY);
		if (sortByColumn != null && sortByColumn.length() > 0 && chooser != null)
		{
			chooser.fromString(sortByColumn);
		}
	}

	protected void saveSortByColumn(IMemento memento) {
		if (memento == null) 
			return;
		memento.putString(SORT_BY_COLUMN_KEY, chooser.toString());
	}
	
	protected void saveColumnWidth(IMemento memento) {
		if (memento == null)
			return;
		
		TableColumn[] columns = messageTable.getColumns();
		StringBuilder sb = new StringBuilder();
		for (TableColumn col : columns) {
			int width = col.getWidth();
			sb.append(width);
			sb.append(COLUMN_WIDTH_DELIMITER);
		}
		memento.putString(COLUMN_WIDTH_KEY, sb.toString());
	}

	protected void restoreColumnWidth(IMemento memento) {
		try {
			if (memento == null) {
				packColumns(messageTable);
				return;
			}
			String delimitedColumnWidth = memento.getString(COLUMN_WIDTH_KEY);
			int[] columnWidth = deserializeDelimitedIntegersIntoArray(
					COLUMN_WIDTH_DELIMITER, delimitedColumnWidth);
			if(columnWidth != null && columnWidth.length > 0) {
				TableColumn[] columns = messageTable.getColumns();
				for (int i = 0; i < columns.length; i++) {
					columns[i].setWidth(columnWidth[i]);
				}					
			}
		} catch (Throwable t){
			// do nothing
		}
	}

			
    protected Table createMessageTable(Composite parent) {
        Table messageTable = new Table(parent, SWT.MULTI | SWT.FULL_SELECTION | SWT.V_SCROLL);
        GridData messageTableLayout = new GridData();
        messageTableLayout.horizontalSpan = 2;
        messageTableLayout.verticalSpan = 1;
        messageTableLayout.horizontalAlignment = GridData.FILL;
        messageTableLayout.verticalAlignment = GridData.FILL;
        messageTableLayout.grabExcessHorizontalSpace = true;
        messageTableLayout.grabExcessVerticalSpace = true;
        messageTable.setLayoutData(messageTableLayout);
        return messageTable;
    }
    
	protected IndexedTableViewer createTableViewer(Table aMessageTable, Enum<?>[] enums) {
		IndexedTableViewer aMessagesViewer = new IndexedTableViewer(aMessageTable);
		getSite().setSelectionProvider(aMessagesViewer);
		aMessagesViewer.setContentProvider(new EventListContentProvider<ReportHolder>());
		aMessagesViewer.setLabelProvider(new MessageListTableFormat(aMessageTable, enums, getSite()));
		return aMessagesViewer;
	}
	

	public IndexedTableViewer getMessagesViewer() {
		return messagesViewer;
	}
	
	public void setInput(EventList<T> input)
	{
		SortedList<T> extractedList = 
			new SortedList<T>(rawInputList = input);

		if (sortableColumns){
			if (chooser != null){
				chooser.dispose();
				chooser = null;
			}
			chooser = TableComparatorChooser.install(messagesViewer.getTable(), tableFormat, extractedList, false);
			restoreSortByColumn(viewStateMemento);
		}
		messagesViewer.setInput(extractedList);
	}
	
	public EventList<T> getInput()
	{
		return rawInputList;
	}

	
	private void hookGlobalActions(){
		getViewSite().getActionBars()
		.setGlobalActionHandler(ActionFactory.COPY.getId(), copyMessagesAction);
		getViewSite().getActionBars().setGlobalActionHandler(
				ActionFactory.SELECT_ALL.getId(), selectAllAction);
	}

	@Override
	public void dispose() {
		if (clipboard != null)
			clipboard.dispose();
		super.dispose();

	}
	
	protected Clipboard getClipboard()
	{
		if (clipboard == null){
			clipboard = new Clipboard(getSite().getShell().getDisplay());
		}
		return clipboard;
	}

	@Override
	public void setFocus() {
		messageTable.setFocus();
	}
	
    private void createSelectAllAction() {
        selectAllAction = new Action() {

            @Override
            public void run() {
            	getMessagesViewer().getTable().selectAll();
            }
        };
        selectAllAction.setActionDefinitionId( "org.eclipse.ui.edit.selectAll" );  //$NON-NLS-1$
    }

    /**
     * Sets the view cursor to the system wait cursor while the given block executes.
     * 
     * <p>At the close of the given block, the cursor is guaranteed to be reset to the state it was in at the start of the block.
     *
     * @param inSite a <code>IWorkbenchPartSite</code> value
     * @param inBlock a <code>Callable&lt;V&gt;</code> value containing the code to execute while the wait cursor is displayed
     * @return a <code>V</code> value
     * @throws Exception if an error occurs during the block execution
     */
    public static <V> V doWaitCursor(IWorkbenchPartSite inSite,
                                     Callable<V> inBlock)
        throws Exception
    {
        Cursor initialCursor = inSite.getShell().getCursor();
        try {
            inSite.getShell().setCursor(inSite.getShell().getDisplay().getSystemCursor(SWT.CURSOR_WAIT));
            return inBlock.call();
        } finally {
            inSite.getShell().setCursor(initialCursor);
        }
    }

    /**
     * Sets the view cursor to the system wait cursor while the given block executes.
     * 
     * <p>At the close of the given block, the cursor is guaranteed to be reset to the state it was in at the start of the block.
     *
     * @param inSite a <code>IWorkbenchPartSite</code> value
     * @param inBlock a <code>Runnable</code> value containing the code to execute while the wait cursor is displayed
     * @throws Exception if an error occurs during the block execution
     */
    public static void doWaitCursor(IWorkbenchPartSite inSite,
                                    Runnable inBlock)
    {
        Cursor initialCursor = inSite.getShell().getCursor();
        try {
            inSite.getShell().setCursor(inSite.getShell().getDisplay().getSystemCursor(SWT.CURSOR_WAIT));
            inBlock.run();
        } finally {
            inSite.getShell().setCursor(initialCursor);
        }
    }
}
