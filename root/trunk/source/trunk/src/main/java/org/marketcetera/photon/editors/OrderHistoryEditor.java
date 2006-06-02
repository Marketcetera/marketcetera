package org.marketcetera.photon.editors;

import java.sql.SQLException;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.marketcetera.photon.Application;
import org.marketcetera.photon.PhotonAdapterFactory;
import org.marketcetera.photon.model.FIXMessageHistory;
import org.marketcetera.photon.model.IFIXMessageListener;
import org.marketcetera.photon.model.MessageHolder;
import org.marketcetera.photon.views.FilterGroup;
import org.marketcetera.photon.views.FilterItem;

import quickfix.Message;

/**
 * An example showing how to create a multi-page editor. This example has 3
 * pages:
 * <ul>
 * <li>page 0 contains a nested text editor.
 * <li>page 1 allows you to change the font used in page 2
 * <li>page 2 shows the words in page 0 in sorted order
 * </ul>
 */
public class OrderHistoryEditor extends MultiPageEditorPart implements
		IFIXMessageListener, ISelectionListener {

	public enum AvgPriceColumns {
		DIRECTION("D"), SIDE("Side"), SYMBOL("Symbol"), ORDERQTY("OrderQty"), CUMQTY("CumQty"), 
		AVGPX("AvgPx"), ACCOUNT("Account");

		private String mName;

		AvgPriceColumns(String name) {
			mName = name;
		}

		public String toString() {
			return mName;
		}
	};

	public enum MessageColumns {
		DIRECTION("D"), TRANSACTTIME("TransactTime"), MSGTYPE("MsgType"), CLORDID("ClOrdID"),
		ORDERID("OrderID"), ORICCLORDID("OrigClOrdID"), ORDSTATUS("OrdStatus"), SIDE(
				"Side"), SYMBOL("Symbol"), ORDERQTY("OrderQty"), CUMQTY(
				"CumQty"), LEAVESQTY("LeavesQty"), Price("Price"), AVGPX(
				"AvgPx"), ACCOUNT("Account"), LASTSHARES("LastShares"), LASTPX(
				"LastPx"), LASTMKT("LastMkt");

		private String mName;

		MessageColumns(String name) {
			mName = name;
		}

		public String toString() {
			return mName;
		}
	};

	public enum FillColumns {
		CLORDID("ClOrdID"), ORDSTATUS("OrdStatus"), SIDE("Side"), SYMBOL("Symbol"), ORDERQTY(
				"OrderQty"), CUMQTY("CumQty"), LEAVESQTY("LeavesQty"), Price(
				"Price"), AVGPX("AvgPx"), STRATEGY("Strategy"), ACCOUNT(
				"Account"), LASTSHARES("LastShares"), LASTPX("LastPx"), LASTMKT(
				"LastMkt");

		private String mName;

		FillColumns(String name) {
			mName = name;
		}

		public String toString() {
			return mName;
		}
	};

	public static final String ID = "org.marketcetera.photon.editors.OrderHistoryEditor";

	private TableViewer averagePriceViewer;

	private TableViewer messagesViewer;

	private TableViewer fillsViewer;

	private IAdapterFactory adapterFactory = new PhotonAdapterFactory();

	private FIXMessageHistory input;

	private IWorkbenchWindow window;
	
	private static final int FILLS_VIEWER_INDEX = 0;
	private static final int AVERAGE_PRICE_VIEWER_INDEX = 1;
	private static final int MESSAGES_VIEWER_INDEX = 2;

	/**
	 * Creates a multi-page editor example.
	 */
	public OrderHistoryEditor() {
		super();
		// ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
		Platform.getAdapterManager().registerAdapters(adapterFactory,
				FIXMessageHistory.class);
		Platform.getAdapterManager().registerAdapters(adapterFactory,
				MessageHolder.class);
		Platform.getAdapterManager().registerAdapters(adapterFactory,
				quickfix.Message.class);
	}

	/**
	 * Creates page 0 of the multi-page editor, which contains a text editor.
	 */
	void createPage0() {
		Composite composite = new Composite(getContainer(), SWT.NONE);
		GridLayout layout = new GridLayout();
		composite.setLayout(layout);
		layout.numColumns = 1;

		fillsViewer = new TableViewer(composite, SWT.BORDER | SWT.MULTI
				| SWT.WRAP | SWT.FULL_SELECTION);
		fillsViewer.getControl().setLayoutData(
				new GridData(GridData.FILL, GridData.FILL, true, true));
		// orderViewer.getControl().setEditable(false);
		fillsViewer.getControl().setBackground(
				fillsViewer.getControl().getDisplay().getSystemColor(
						SWT.COLOR_INFO_BACKGROUND));
		fillsViewer.getControl().setForeground(
				fillsViewer.getControl().getDisplay().getSystemColor(
						SWT.COLOR_INFO_FOREGROUND));
		fillsViewer.getTable().setHeaderVisible(true);

		for (FillColumns aColumn : FillColumns.values()) {
			TableColumn column = new TableColumn(fillsViewer.getTable(),
					SWT.LEFT);
			column.setText(aColumn.toString());
			column.setWidth(50);
		}
		int index = addPage(composite);
		setPageText(index, "Fills");
		fillsViewer.setLabelProvider(new FIXMessageLabelProvider(fillsViewer
				.getTable().getColumns()));
		fillsViewer.setContentProvider(new FillContentProvider());
		
		packColumns(fillsViewer.getTable());
	}

	/**
	 * @param table
	 */
	private void packColumns(final Table table) {
		for (int i = 0; i < table.getColumnCount(); i++) {
			table.getColumn(i).pack();
		}
	}
	
	/**
	 * Creates page 1 of the multi-page editor, which allows you to change the
	 * font used in page 2.
	 */
	void createPage1() {
		Composite composite = new Composite(getContainer(), SWT.NONE);
		GridLayout layout = new GridLayout();
		composite.setLayout(layout);
		layout.numColumns = 1;

		messagesViewer = new TableViewer(composite, SWT.BORDER | SWT.MULTI
				| SWT.WRAP | SWT.FULL_SELECTION);
		messagesViewer.getControl().setLayoutData(
				new GridData(GridData.FILL, GridData.FILL, true, true));
		// orderViewer.getControl().setEditable(false);
		messagesViewer.getControl().setBackground(
				messagesViewer.getControl().getDisplay().getSystemColor(
						SWT.COLOR_INFO_BACKGROUND));
		messagesViewer.getControl().setForeground(
				messagesViewer.getControl().getDisplay().getSystemColor(
						SWT.COLOR_INFO_FOREGROUND));
		messagesViewer.getTable().setHeaderVisible(true);

		for (MessageColumns aColumn : MessageColumns.values()) {
			TableColumn column = new TableColumn(messagesViewer.getTable(),
					SWT.LEFT);
			column.setText(aColumn.toString());
			column.setWidth(50);
		}
		int index = addPage(composite);
		setPageText(index, "Messages");
		messagesViewer.setLabelProvider(new FIXMessageLabelProvider(
				messagesViewer.getTable().getColumns()));
		messagesViewer.setContentProvider(new BaseWorkbenchContentProvider());
		packColumns(messagesViewer.getTable());

	}

	/**
	 * Creates page 2 of the multi-page editor, which shows the sorted text.
	 */
	void createPage2() {
		Composite composite = new Composite(getContainer(), SWT.NONE);
		GridLayout layout = new GridLayout();
		composite.setLayout(layout);
		layout.numColumns = 1;

		averagePriceViewer = new TableViewer(composite, SWT.BORDER | SWT.MULTI
				| SWT.WRAP | SWT.FULL_SELECTION);
		averagePriceViewer.getControl().setLayoutData(
				new GridData(GridData.FILL, GridData.FILL, true, true));
		// orderViewer.getControl().setEditable(false);
		averagePriceViewer.getControl().setBackground(
				averagePriceViewer.getControl().getDisplay().getSystemColor(
						SWT.COLOR_INFO_BACKGROUND));
		averagePriceViewer.getControl().setForeground(
				averagePriceViewer.getControl().getDisplay().getSystemColor(
						SWT.COLOR_INFO_FOREGROUND));
		averagePriceViewer.getTable().setHeaderVisible(true);

		for (AvgPriceColumns aColumn : AvgPriceColumns.values()) {
			TableColumn column = new TableColumn(averagePriceViewer.getTable(),
					SWT.LEFT);
			column.setText(aColumn.toString());
			column.setWidth(50);
		}
		int index = addPage(composite);
		setPageText(index, "Average Price");
		averagePriceViewer.setLabelProvider(new FIXMessageLabelProvider(
				averagePriceViewer.getTable().getColumns()));
		averagePriceViewer
				.setContentProvider(new AveragePriceContentProvider());
		packColumns(averagePriceViewer.getTable());

	}

	/**
	 * Creates the pages of the multi-page editor.
	 */
	protected void createPages() {
		createPage0();
		createPage1();
		createPage2();
		setFIXMessageHistory(input);
		makeActions();
	}

	private void makeActions() {
		MenuManager menuMgr = new MenuManager("orderHistoryPopup");
		Menu menu = menuMgr.createContextMenu(messagesViewer.getControl());
		menuMgr.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
		messagesViewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, messagesViewer);
		getSite().setSelectionProvider(new OrderHistorySelectionProvider(this));
	}

	/**
	 * The <code>MultiPageEditorPart</code> implementation of this
	 * <code>IWorkbenchPart</code> method disposes all nested editors.
	 * Subclasses may extend.
	 */
	public void dispose() {
		// ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		super.dispose();
		window.getSelectionService().removeSelectionListener(this);
	}

	/**
	 * Saves the multi-page editor's document.
	 */
	public void doSave(IProgressMonitor monitor) {
		getEditor(0).doSave(monitor);
	}

	/**
	 * Saves the multi-page editor's document as another file. Also updates the
	 * text for page 0's tab, and updates this multi-page editor's input to
	 * correspond to the nested editor's.
	 */
	public void doSaveAs() {
		IEditorPart editor = getEditor(0);
		editor.doSaveAs();
		setPageText(0, editor.getTitle());
		setInput(editor.getEditorInput());
	}

	/*
	 * (non-Javadoc) Method declared on IEditorPart
	 */
	public void gotoMarker(IMarker marker) {
		setActivePage(0);
		IDE.gotoMarker(getEditor(0), marker);
	}

	/**
	 * The <code>MultiPageEditorExample</code> implementation of this method
	 * checks that the input is an instance of <code>IFileEditorInput</code>.
	 */
	public void init(IEditorSite site, IEditorInput editorInput)
			throws PartInitException {
		if (!(editorInput instanceof OrderHistoryInput)) {
			throw new PartInitException(
					"Invalid Input: Must be IFileEditorInput");
		} else {
			input = ((OrderHistoryInput) editorInput).getHistory();
		}
		window = site.getWorkbenchWindow();
		window.getSelectionService().addSelectionListener(this);

		super.init(site, editorInput);
	}

	public void setFIXMessageHistory(FIXMessageHistory input) {
		if (input != null) {
			if (messagesViewer != null) {
				messagesViewer.setInput(input);
			}
			if (fillsViewer != null) {
				fillsViewer.setInput(input);
			}
			if (averagePriceViewer != null) {
				averagePriceViewer.setInput(input.getAveragePriceHistory());
			}
			input.addFIXMessageListener(this);
		}
	}

	public ISelectionProvider getActiveSelectionProvider() {
		int pageIndex = getActivePage();
		switch (pageIndex) {
		case FILLS_VIEWER_INDEX:
			return fillsViewer;
		case AVERAGE_PRICE_VIEWER_INDEX:
			return averagePriceViewer;
		case MESSAGES_VIEWER_INDEX:
			return messagesViewer;
		default:
			return null;
		}

	}
	
	/*
	 * (non-Javadoc) Method declared on IEditorPart.
	 */
	public boolean isSaveAsAllowed() {
		return true;
	}

	public void incomingMessage(Message message) {
		averagePriceViewer.setInput(input.getAveragePriceHistory());
		asyncRefresh();
	}

	public void outgoingMessage(Message message) {
		asyncRefresh();
	};

	public void asyncExec(Runnable runnable) {
		Display display = this.getContainer().getDisplay();

		// If the display is disposed, you can't do anything with it!!!
		if (display == null || display.isDisposed())
			return;

		display.asyncExec(runnable);
	}
	
	protected void asyncRefresh()
	{
		asyncExec(new Runnable() {
			public void run() {
				messagesViewer.refresh();
				fillsViewer.refresh();
				averagePriceViewer.refresh();
			}
		});
	}

	public void selectionChanged(IWorkbenchPart part, ISelection incoming) {
		if (incoming instanceof IStructuredSelection) {
			IStructuredSelection selection = (IStructuredSelection) incoming;
			if (selection.size() == 1) {
				Object firstElement = selection.getFirstElement();
				if (firstElement instanceof FilterItem) {
					FilterItem filterItem = (FilterItem) firstElement;
					setFilter(filterItem.getFilter());

					//Application.getMainConsoleLogger().debug(
					//		"Selected" + filterItem.getItem());
				} else if (firstElement instanceof FilterGroup) {
					resetFilters();
					//Application.getMainConsoleLogger().debug("Unselected");
				} else {
					//Application.getMainConsoleLogger().debug(
					//		"Structured selection with unknown item.");
				}
			}
		} else {
			// Other selections, for example containing text or of other kinds.
			//Application.getMainConsoleLogger().debug("Other selection type");
		}
	}

	public void setFilter(ViewerFilter filter){
		resetFilters();
		addFilter(filter);
	}
	
	public void addFilter(ViewerFilter filter) {
		fillsViewer.addFilter(filter);
		messagesViewer.addFilter(filter);
		averagePriceViewer.addFilter(filter);
		asyncRefresh();
		
	}

	public void removeFilter(ViewerFilter filter) {
		fillsViewer.removeFilter(filter);
		messagesViewer.removeFilter(filter);
		averagePriceViewer.removeFilter(filter);
		asyncRefresh();
	}

	public void resetFilters() {
		boolean didReset = false;
		if (fillsViewer.getFilters().length > 0){
			fillsViewer.resetFilters();
			didReset = true;
		}
		if (messagesViewer.getFilters().length > 0){
			messagesViewer.resetFilters();
			didReset = true;
		}
		if (averagePriceViewer.getFilters().length > 0){
			averagePriceViewer.resetFilters();
			didReset = true;
		}
		if (didReset){
			asyncRefresh();
		}
	}

	
//	public static TableViewer constructSQLViewer(Composite parentComposite, ResultSetMetaData metaData) {
//		DBTableModel tableModel = null;
//		int count = 0;
//		TableViewer viewer = null;
//
//			viewer = new TableViewer(parentComposite, SWT.BORDER | SWT.MULTI
//					| SWT.WRAP | SWT.FULL_SELECTION);
//			try {
//				count = metaData.getColumnCount();
//			
//			// set up the SelectionAdapter
//			final Table table = viewer.getTable();
//			final SQLTableSorter sorter = new SQLTableSorter(count, metaData);
//			viewer.setSorter(sorter);
//			final String[] ss = new String[count];
//			final TableViewer tmpViewer = viewer;
//			SelectionListener headerListener = new SelectionAdapter() {
//
//				public void widgetSelected(SelectionEvent e) {
//					// column selected - need to sort
//					int column = table.indexOf((TableColumn) e.widget);
//					if (column == sorter.getTopPriority()) {
//						int k = sorter.reverseTopPriority();
//// if (k == SQLTableSorter.ASCENDING)
//// ((TableColumn) e.widget).setImage(imgAsc);
//// else
//// ((TableColumn) e.widget).setImage(imgDesc);
//					} else {
//						sorter.setTopPriority(column);
//// ((TableColumn) e.widget).setImage(imgAsc);
//					}
//					TableColumn[] tcArr = table.getColumns();
//					for (int i = 0; i < tcArr.length; i++) {
//						if (i != column) {
//							tcArr[i].setImage(null);
//						}
//					}
//					tmpViewer.refresh();
//
//				}
//			};
//
//			table.setLinesVisible(true);
//			table.setHeaderVisible(true);
//			SQLTableContentProvider slp = new SQLTableContentProvider();
//			viewer.setContentProvider(slp);
//			for (int i = 0; i < count; i++) {
//				TableColumn tc = new TableColumn(table, SWT.NULL);
//				String rawColumnLabel = metaData.getColumnLabel(i + 1);
//				tc.setText(rawColumnLabel);
//				ss[i] = new String(rawColumnLabel);
//				tc.addSelectionListener(headerListener);
////				 if (i == 0)
////				 tc.setImage(imgAsc);
//			}
//			viewer.setColumnProperties(ss);
//			viewer.setLabelProvider(slp);
//			for (int i = 0; i < count; i++) {
//				table.getColumn(i).pack();
//			}
//			table.layout();
//		} catch (SQLException ex){
//			ex.printStackTrace();
//		}
//		return viewer;
//	}
}
