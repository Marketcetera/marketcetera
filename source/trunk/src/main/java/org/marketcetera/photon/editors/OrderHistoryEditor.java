package org.marketcetera.photon.editors;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.part.MultiPageSelectionProvider;
import org.marketcetera.photon.PhotonAdapterFactory;
import org.marketcetera.photon.actions.ViewSecurityAction;
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
		SIDE("Side"), SYMBOL("Symbol"), ORDERQTY("OrderQty"), CUMQTY("CumQty"), LEAVESQTY(
				"LeavesQty"), AVGPX("AvgPx"), ACCOUNT("Account");

		private String mName;

		AvgPriceColumns(String name) {
			mName = name;
		}

		public String toString() {
			return mName;
		}
	};

	public enum MessageColumns {
		DIRECTION("D"), MSGTYPE("MsgType"), CLORDID("ClOrdID"), ORDSTATUS("OrdStatus"), SIDE(
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
		ViewSecurityAction action = new ViewSecurityAction(this.window);
		MenuManager menuMgr = new MenuManager("orderHistoryPopup");
		menuMgr.add(action);
		Menu menu = menuMgr.createContextMenu(messagesViewer.getControl());
		messagesViewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, messagesViewer);
		getSite().setSelectionProvider(new MultiPageSelectionProvider(this));

	
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
			input.addFIXMessageListener(this);
		}
	}

	/*
	 * (non-Javadoc) Method declared on IEditorPart.
	 */
	public boolean isSaveAsAllowed() {
		return true;
	}

	public void incomingMessage(Message message) {
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

}
