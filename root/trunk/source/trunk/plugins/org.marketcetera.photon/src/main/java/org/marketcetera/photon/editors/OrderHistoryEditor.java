package org.marketcetera.photon.editors;import org.eclipse.core.resources.IMarker;import org.eclipse.core.runtime.IAdapterFactory;import org.eclipse.core.runtime.IAdapterManager;import org.eclipse.core.runtime.IProgressMonitor;import org.eclipse.core.runtime.Platform;import org.eclipse.jface.action.GroupMarker;import org.eclipse.jface.action.MenuManager;import org.eclipse.jface.viewers.ISelectionProvider;import org.eclipse.swt.SWT;import org.eclipse.swt.events.MenuEvent;import org.eclipse.swt.events.MenuListener;import org.eclipse.swt.layout.GridData;import org.eclipse.swt.layout.GridLayout;import org.eclipse.swt.widgets.Composite;import org.eclipse.swt.widgets.Display;import org.eclipse.swt.widgets.Event;import org.eclipse.swt.widgets.Listener;import org.eclipse.swt.widgets.Menu;import org.eclipse.swt.widgets.MenuItem;import org.eclipse.swt.widgets.Table;import org.eclipse.swt.widgets.TableColumn;import org.eclipse.ui.IEditorInput;import org.eclipse.ui.IEditorPart;import org.eclipse.ui.IEditorSite;import org.eclipse.ui.IWorkbenchActionConstants;import org.eclipse.ui.IWorkbenchWindow;import org.eclipse.ui.PartInitException;import org.eclipse.ui.part.MultiPageEditorPart;import org.marketcetera.core.ClassVersion;import org.marketcetera.photon.PhotonAdapterFactory;import org.marketcetera.photon.model.FIXMessageHistory;import org.marketcetera.photon.model.MessageHolder;import ca.odell.glazedlists.SortedList;import ca.odell.glazedlists.swt.EventTableViewer;import ca.odell.glazedlists.swt.TableComparatorChooser;/** * OrderHistoryEditor is a user-interface component displaying * all of the messages passed between the application and the * counterparties, through the OMS.  The component consists * of several "pages" providing different views on the same data * <ul> * <li><b>Open Orders</b> shows all of the orders currently working in the marketplace * <li><b>Fills</b> shows one line per fill * <li><b>Messages</b> shows all of the messages passed between the application and other platform components * <li><b>Average Price</b> shows a summary of all fills, including total quantity and average price * </ul> *  * 	 */@ClassVersion("$Id$")public class OrderHistoryEditor extends MultiPageEditorPart {	/**	 * The columns of the "Open Orders" page, specified as 	 * FIX fields.	 * 	 * @author gmiller	 *	 */	public enum OpenOrderColumns {		SENDINGTIME("SendingTime"), CLORDID("ClOrdID"),		ORDERID("OrderID"), ORDSTATUS("OrdStatus"), SIDE(				"Side"), SYMBOL("Symbol"), ORDERQTY("OrderQty"), CUMQTY(				"CumQty"), LEAVESQTY("LeavesQty"), Price("Price"), AVGPX(				"AvgPx"), ACCOUNT("Account"), LASTSHARES("LastShares"), LASTPX(				"LastPx"), LASTMKT("LastMkt"), EXECID("ExecID");		private String mName;		OpenOrderColumns(String name) {			mName = name;		}		public String toString() {			return mName;		}	};		/**	 * The columns of the average price page specified as	 * FIX fields.	 * 	 * @author gmiller	 *	 */	public enum AvgPriceColumns {		DIRECTION("D"), SIDE("Side"), SYMBOL("Symbol"), ORDERQTY("OrderQty"), CUMQTY("CumQty"), 		AVGPX("AvgPx"), ACCOUNT("Account");		private String mName;		AvgPriceColumns(String name) {			mName = name;		}		public String toString() {			return mName;		}	};	/**	 * The columns of the Messages page, represented	 * as FIX fields.	 * 	 * @author gmiller	 *	 */	public enum MessageColumns {		DIRECTION("D"), SENDINGTIME("SendingTime"), MSGTYPE("MsgType"), CLORDID("ClOrdID"),		ORDERID("OrderID"), ORICCLORDID("OrigClOrdID"), ORDSTATUS("OrdStatus"), SIDE(				"Side"), SYMBOL("Symbol"), ORDERQTY("OrderQty"), CUMQTY(				"CumQty"), LEAVESQTY("LeavesQty"), Price("Price"), AVGPX(				"AvgPx"), ACCOUNT("Account"), LASTSHARES("LastShares"), LASTPX(				"LastPx"), LASTMKT("LastMkt"), EXECID("ExecID");		private String mName;		MessageColumns(String name) {			mName = name;		}		public String toString() {			return mName;		}	};	/**	 * The columns of the Fills page represented	 * as FIX fields.	 * 	 * @author gmiller	 *	 */	public enum FillColumns {		CLORDID("ClOrdID"), ORDSTATUS("OrdStatus"), SIDE("Side"), SYMBOL("Symbol"), ORDERQTY(				"OrderQty"), CUMQTY("CumQty"), LEAVESQTY("LeavesQty"), Price(				"Price"), AVGPX("AvgPx"), STRATEGY("Strategy"), ACCOUNT(				"Account"), LASTSHARES("LastShares"), LASTPX("LastPx"), LASTMKT(				"LastMkt");		private String mName;		FillColumns(String name) {			mName = name;		}		public String toString() {			return mName;		}	};	public static final String ID = "org.marketcetera.photon.editors.OrderHistoryEditor";	private Table openOrderTable;	private EventTableViewer openOrderViewer;	private ViewerSelectionAdapter openOrderSelectionProvider;	private Table averagePriceTable;	private EventTableViewer averagePriceViewer;	private ViewerSelectionAdapter averagePriceSelectionProvider;		private Table messageTable;	private EventTableViewer messagesViewer;	private ViewerSelectionAdapter messagesSelectionProvider;	private Table fillTable;	private EventTableViewer fillsViewer;	private ViewerSelectionAdapter fillsSelectionProvider;	private IAdapterFactory adapterFactory = new PhotonAdapterFactory();	//private SortedList<MessageHolder> allMessages;		private SortedList<MessageHolder> filteredMessages;	private SortedList<MessageHolder> fillMessages;		private SortedList<MessageHolder> openOrderMessages;	private SortedList<MessageHolder> averagePriceList;	private IWorkbenchWindow window;		private static final int OPEN_ORDER_VIEWER_INDEX = 0;	private static final int FILLS_VIEWER_INDEX = 1;	private static final int AVERAGE_PRICE_VIEWER_INDEX = 2;	private static final int MESSAGES_VIEWER_INDEX = 3;	private FIXMessageHistory messageHistory;	private static final String COLUMN_WIDTH_SAVED_KEY_NAME = "width.saved";  //$NON-NLS-1$		/**	 * Create a new OrderHistoryEditor, and register adapters for	 * various types of model objects.	 * 	 * @see IAdapterManager#registerAdapters(IAdapterFactory, Class)	 */	public OrderHistoryEditor() {		super();		// ResourcesPlugin.getWorkspace().addResourceChangeListener(this);		Platform.getAdapterManager().registerAdapters(adapterFactory,				FIXMessageHistory.class);		Platform.getAdapterManager().registerAdapters(adapterFactory,				MessageHolder.class);		Platform.getAdapterManager().registerAdapters(adapterFactory,				quickfix.Message.class);	}	/**	 * 	 */	void createOpenOrderPage() {		Composite composite = new Composite(getContainer(), SWT.NONE);		GridLayout layout = new GridLayout();		composite.setLayout(layout);		layout.numColumns = 1;        openOrderTable = createMessageTable(composite);        openOrderViewer = new EventTableViewer(openOrderMessages, openOrderTable, new EnumTableFormat(OpenOrderColumns.values()));        openOrderSelectionProvider = new ViewerSelectionAdapter(openOrderViewer);        openOrderTable = formatFillTable(openOrderTable);        hookTableSorting(openOrderViewer, openOrderMessages);                openOrderTable.setBackground(        		openOrderTable.getDisplay().getSystemColor(						SWT.COLOR_INFO_BACKGROUND));        openOrderTable.setForeground(        		openOrderTable.getDisplay().getSystemColor(						SWT.COLOR_INFO_FOREGROUND));        openOrderTable.setHeaderVisible(true);		int index = addPage(composite);		packColumns(openOrderTable);		setPageText(index, "Open Orders");	}		void createFillPage() {		Composite composite = new Composite(getContainer(), SWT.NONE);		GridLayout layout = new GridLayout();		composite.setLayout(layout);		layout.numColumns = 1;        fillTable = createMessageTable(composite);		fillsViewer = new EventTableViewer(fillMessages, fillTable, new EnumTableFormat(FillColumns.values()));		fillsSelectionProvider = new ViewerSelectionAdapter(fillsViewer);		fillTable = formatFillTable(fillTable);		hookTableSorting(fillsViewer, fillMessages);        fillTable.setBackground(        		fillTable.getDisplay().getSystemColor(						SWT.COLOR_INFO_BACKGROUND));        fillTable.setForeground(        		fillTable.getDisplay().getSystemColor(						SWT.COLOR_INFO_FOREGROUND));        fillTable.setHeaderVisible(true);		int index = addPage(composite);		packColumns(fillTable);		setPageText(index, "Fills");	}	/**	 * Creates page 1 of the multi-page editor, which contains the list of messages	 */	void createMessagePage() {		Composite composite = new Composite(getContainer(), SWT.NONE);		GridLayout layout = new GridLayout();		composite.setLayout(layout);		layout.numColumns = 1;        messageTable = createMessageTable(composite);		messagesViewer = new EventTableViewer(filteredMessages, messageTable, new EnumTableFormat(MessageColumns.values()));		messagesSelectionProvider = new ViewerSelectionAdapter(messagesViewer);		messageTable = formatFillTable(messageTable);        hookTableSorting(messagesViewer, filteredMessages);        messageTable.setBackground(        		messageTable.getDisplay().getSystemColor(						SWT.COLOR_INFO_BACKGROUND));        messageTable.setForeground(        		messageTable.getDisplay().getSystemColor(						SWT.COLOR_INFO_FOREGROUND));        messageTable.setHeaderVisible(true);		int index = addPage(composite);		packColumns(messageTable);		setPageText(index, "Messages");	}	private void hookTableSorting(EventTableViewer viewer, SortedList<MessageHolder> messages) {		new TableComparatorChooser(viewer, messages, false);	}	/**	 * Creates page 2 of the multi-page editor, which contains the list of average price fills	 */	void createAveragePricePage() {		Composite composite = new Composite(getContainer(), SWT.NONE);		GridLayout layout = new GridLayout();		composite.setLayout(layout);		layout.numColumns = 1;        averagePriceTable = createMessageTable(composite);		averagePriceViewer = new EventTableViewer(averagePriceList, averagePriceTable, new EnumTableFormat(AvgPriceColumns.values()));		averagePriceSelectionProvider = new ViewerSelectionAdapter(averagePriceViewer);		averagePriceTable = formatFillTable(averagePriceTable);		hookTableSorting(averagePriceViewer, averagePriceList);        averagePriceTable.setBackground(        		averagePriceTable.getDisplay().getSystemColor(						SWT.COLOR_INFO_BACKGROUND));        averagePriceTable.setForeground(        		averagePriceTable.getDisplay().getSystemColor(						SWT.COLOR_INFO_FOREGROUND));        averagePriceTable.setHeaderVisible(true);		int index = addPage(composite);		packColumns(averagePriceTable);		setPageText(index, "Average Price");	}		private void packColumns(final Table table) {		for (int i = 0; i < table.getColumnCount(); i++) {			table.getColumn(i).pack();		}	}    private Table createMessageTable(Composite parent) {        Table messageTable = new Table(parent, SWT.MULTI | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.BORDER | SWT.VIRTUAL);        GridData messageTableLayout = new GridData();        messageTableLayout.horizontalSpan = 2;        messageTableLayout.verticalSpan = 1;        messageTableLayout.horizontalAlignment = GridData.FILL;        messageTableLayout.verticalAlignment = GridData.FILL;        messageTableLayout.grabExcessHorizontalSpace = true;        messageTableLayout.grabExcessVerticalSpace = true;        messageTable.setLayoutData(messageTableLayout);        return messageTable;    }    private Table formatFillTable(Table messageTable) {        messageTable.getVerticalBar().setEnabled(true);        return messageTable;    }	/**	 * Creates the pages of the multi-page editor.	 */	protected void createPages() {		createOpenOrderPage();		createFillPage();		createMessagePage();		createAveragePricePage();		makeActions();	}	private void makeActions() {		createContextMenu("orderHistoryMessagePopup", messageTable, messagesSelectionProvider);		createContextMenu("orderHistoryFillPopup", fillTable, fillsSelectionProvider);		createContextMenu("orderHistoryOpenOrderPopup", openOrderTable, openOrderSelectionProvider);		createContextMenu("orderHistoryAveragePricePopup", averagePriceTable, averagePriceSelectionProvider);				getSite().setSelectionProvider(new OrderHistorySelectionProvider(this));	}		private void createContextMenu(String name, final Table table, ISelectionProvider selectionProvider)	{		MenuManager menuMgr = new MenuManager(name);		Menu menu = menuMgr.createContextMenu(table);		menuMgr.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));		hookColumnChooserMenu(table, menu);					table.setMenu(menu);		getSite().registerContextMenu(menuMgr, selectionProvider);	}	private void hookColumnChooserMenu(final Table table, final Menu menu) {		// we're hooking up a submenu using straight-up swt (as opposed to contribution items)		// since this is how it will eventually work in swt post-3.2 when table supports 		// attaching a pop-up menu to the column headers.				menu.addListener(SWT.Show, new Listener() {			private MenuItem columnsCascadeItem;			public void handleEvent(Event e) {				if (menu.getItemCount() > 0 && menu.getItem(menu.getItemCount() - 1).equals(columnsCascadeItem))  // this menu already has our additions					return;								if (menu.getItemCount() != 0) {					new MenuItem(menu, SWT.SEPARATOR);				}								columnsCascadeItem = new MenuItem(menu, SWT.CASCADE);				columnsCascadeItem.setText("Choose columns");				Menu columnsCascadeMenu = new Menu(table.getShell(), SWT.DROP_DOWN);				columnsCascadeItem.setMenu(columnsCascadeMenu);								for(final TableColumn column : table.getColumns()) {					MenuItem item = new MenuItem(columnsCascadeMenu, SWT.CHECK);					item.setText(column.getText());					item.setEnabled(true);					item.setSelection(!isColumnHidden(column));										item.addListener(SWT.Selection, 							new Listener() {								public void handleEvent(Event event) {									if (isColumnHidden(column))										showColumn(column);									else										hideColumn(column);								}							});				}								new MenuItem(columnsCascadeMenu, SWT.SEPARATOR);								MenuItem moreColumnsItem = new MenuItem(columnsCascadeMenu, SWT.PUSH);				moreColumnsItem.setText("More columns...");				moreColumnsItem.setEnabled(false);			}		});	}	private void hideColumn(TableColumn column) {		// short of rebuilding the table, the only way to hide a table column with swt 3.2		// is to set its width to 0 and make it non-resizable.				column.setData(COLUMN_WIDTH_SAVED_KEY_NAME, column.getWidth());  // save the current width so that we could restore it when the column is shown again		column.setResizable(false);		column.setWidth(0);	}	private void showColumn(TableColumn column) {		column.setResizable(true);		if (column.getData(COLUMN_WIDTH_SAVED_KEY_NAME) != null) {			column.setWidth((Integer) column.getData(COLUMN_WIDTH_SAVED_KEY_NAME));		}	}	private boolean isColumnHidden(TableColumn column) {		return column.getWidth() == 0 && !column.getResizable();	}	/**	 * The <code>MultiPageEditorPart</code> implementation of this	 * <code>IWorkbenchPart</code> method disposes all nested editors.	 * Subclasses may extend.	 */	public void dispose() {		// ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);		super.dispose();	}	/**	 * Saves the multi-page editor's document.	 */	public void doSave(IProgressMonitor monitor) {		getEditor(0).doSave(monitor);	}	/**	 * Saves the multi-page editor's document as another file. Also updates the	 * text for page 0's tab, and updates this multi-page editor's allMessages to	 * correspond to the nested editor's.	 */	public void doSaveAs() {		IEditorPart editor = getEditor(0);		editor.doSaveAs();		setPageText(0, editor.getTitle());		setInput(editor.getEditorInput());	}	/*	 * (non-Javadoc) Method declared on IEditorPart	 */	public void gotoMarker(IMarker marker) {		setActivePage(0);	}	/**	 * The <code>MultiPageEditorExample</code> implementation of this method	 * checks that the allMessages is an instance of <code>IFileEditorInput</code>.	 */	public void init(IEditorSite site, IEditorInput editorInput)			throws PartInitException {		if (!(editorInput instanceof OrderHistoryInput)) {			throw new PartInitException(					"Invalid Input: Must be IFileEditorInput");		} else {			messageHistory = ((OrderHistoryInput) editorInput).getHistory();			//allMessages = new SortedList<MessageHolder>(messageHistory.getAllMessages());			filteredMessages = new SortedList<MessageHolder>(messageHistory.getFilteredMessages());			fillMessages = new SortedList<MessageHolder>(messageHistory.getFills());			averagePriceList = new SortedList<MessageHolder>(messageHistory.getAveragePriceHistory());			openOrderMessages = new SortedList<MessageHolder>(messageHistory.getOpenOrders());			//			allMessages.add(new IncomingMessageHolder(//					FIXMessageUtil.newExecutionReport(new InternalID("1001"), new InternalID("1"), "2001", ExecTransType.NEW, ExecType.NEW, OrdStatus.NEW, Side.BUY, new BigDecimal(1000), new BigDecimal(789), null, null, new BigDecimal(1000), BigDecimal.ZERO, BigDecimal.ZERO, new MSymbol("TESTSYM"))//			));		}		window = site.getWorkbenchWindow();		super.init(site, editorInput);	}	public ISelectionProvider getActiveSelectionProvider() {		int pageIndex = getActivePage();		switch (pageIndex) {		case OPEN_ORDER_VIEWER_INDEX:			return openOrderSelectionProvider;		case FILLS_VIEWER_INDEX:			return fillsSelectionProvider;		case AVERAGE_PRICE_VIEWER_INDEX:			return averagePriceSelectionProvider;		case MESSAGES_VIEWER_INDEX:			return messagesSelectionProvider;		default:			return null;		}	}			/*	 * (non-Javadoc) Method declared on IEditorPart.	 */	public boolean isSaveAsAllowed() {		return true;	}	public void asyncExec(Runnable runnable) {		Display display = this.getContainer().getDisplay();		// If the display is disposed, you can't do anything with it!!!		if (display == null || display.isDisposed())			return;		display.asyncExec(runnable);	}		protected void asyncRefresh()	{		asyncExec(new Runnable() {			public void run() {				refresh();			}		});	}		private void refresh() {//		messagesViewer.refresh();//		fillsViewer.refresh();//		averagePriceViewer.refresh();	}	}