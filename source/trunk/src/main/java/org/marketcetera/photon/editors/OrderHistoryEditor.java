package org.marketcetera.photon.editors;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.marketcetera.photon.PhotonAdapterFactory;
import org.marketcetera.photon.model.FIXMessageHistory;
import org.marketcetera.photon.model.IFIXMessageListener;

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
		IFIXMessageListener {

	

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
		DIRECTION("D"), MSGTYPE("MsgType"), CLORDID("ClOrdID"), STATUS("Status"), SIDE(
				"Side"), SYMBOL("Symbol"), ORDERQTY("OrderQty"), CUMQTY(
				"CumQty"), LEAVESQTY("LeavesQty"), Price("Price"), AVGPX(
				"AvgPx"), ACCOUNT("Account"), LAST_QUANTITY("LastShares"), LAST_PRICE(
				"LastPx"), LAST_MARKET("LastMkt");

		private String mName;

		MessageColumns(String name) {
			mName = name;
		}

		public String toString() {
			return mName;
		}
	};

	public enum FillColumns {
		ORDER_ID("ID"), STATUS("Status"), SIDE("Side"), SYMBOL("Symbol"), TOTAL_QUANTITY(
				"Quantity"), EXECUTED_QUANTITY("Executed"), LEAVES_QUANTITY(
				"Leaves"), ORDER_PRICE("Order Price"), AVERAGE_PRICE(
				"Avg Price"), STRATEGY("Strategy"), ACCOUNT("Account"), LAST_QUANTITY(
				"Last Qty"), LAST_PRICE("Last Price"), LAST_MARKET(
				"Last Market");

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

	/**
	 * Creates a multi-page editor example.
	 */
	public OrderHistoryEditor() {
		super();
		// ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
		Platform.getAdapterManager().registerAdapters(adapterFactory,
				FIXMessageHistory.class);
		Platform.getAdapterManager().registerAdapters(adapterFactory,
				FIXMessageHistory.MessageHolder.class);
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
				.setContentProvider(new ExecutionReportContentProvider());
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
		fillsViewer.setLabelProvider(new WorkbenchLabelProvider());
		fillsViewer.setContentProvider(new ExecutionReportContentProvider());
	}

	/**
	 * Creates the pages of the multi-page editor.
	 */
	protected void createPages() {
		createPage0();
		createPage1();
		createPage2();
		setFIXMessageHistory(input);
	}

	/**
	 * The <code>MultiPageEditorPart</code> implementation of this
	 * <code>IWorkbenchPart</code> method disposes all nested editors.
	 * Subclasses may extend.
	 */
	public void dispose() {
		// ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		super.dispose();
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
		super.init(site, editorInput);
	}

	public void setFIXMessageHistory(FIXMessageHistory input) {
		if (input != null){
			if (messagesViewer != null) {
				messagesViewer.setInput(input);
			}
			if (averagePriceViewer != null) {
				averagePriceViewer.setInput(input);
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
		asyncExec(new Runnable() {
			public void run() {
				messagesViewer.refresh();
				averagePriceViewer.refresh();
			}
		});
	}

	public void outgoingMessage(Message message) {
		asyncExec(new Runnable() {
			public void run() {
				messagesViewer.refresh();
				averagePriceViewer.refresh();
			}
		});
	};

	public void asyncExec(Runnable runnable) {
		Display display = this.getContainer().getDisplay();

		// If the display is disposed, you can’t do anything with it!!!
		if (display == null || display.isDisposed())
			return;

		display.asyncExec(runnable);
	}
}
