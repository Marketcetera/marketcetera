package org.marketcetera.photon.views;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.runtime.AssertionFailedException;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.part.ViewPart;
import org.marketcetera.core.MSymbol;
import org.marketcetera.event.AskEvent;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.TradeEvent;
import org.marketcetera.photon.FIXFieldLocalizer;
import org.marketcetera.photon.Messages;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.marketdata.MarketDataManager;
import org.marketcetera.photon.marketdata.MarketDataReceiverModule.MarketDataSubscriber;
import org.marketcetera.photon.ui.TextContributionItem;
import org.marketcetera.photon.ui.ChooseColumnsMenu.ITableProvider;
import org.marketcetera.util.misc.ClassVersion;

import quickfix.field.BidPx;
import quickfix.field.BidSize;
import quickfix.field.LastPx;
import quickfix.field.LastQty;
import quickfix.field.OfferPx;
import quickfix.field.OfferSize;
import quickfix.field.Symbol;

/* $License$ */

/**
 * Market data view.
 * 
 * Note: enabling/disabling the symbol entry text field based on feed status has been commented out
 * since I don't believe it is necessary anymore (now that you can still add tickers even if the feed
 * is offline). Someone may ask me to add this back so I haven't removed it yet.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")//$NON-NLS-1$
public final class MarketDataView extends ViewPart implements IMSymbolListener,
		ITableProvider, Messages {

	/**
	 * The view ID.
	 */
	public static final String ID = "org.marketcetera.photon.views.MarketDataView"; //$NON-NLS-1$

	private static final String RESTORED_WIDTH_KEY = "restoredWidth"; //$NON-NLS-1$

	private static final String COLUMN_WIDTHS = "COLUMN_WIDTHS"; //$NON-NLS-1$

	private static final String COLUMN_ORDER = "COLUMN_ORDER"; //$NON-NLS-1$

	private static final String COLUMN_RESTORED_WIDTHS = "COLUMN_RESTORED_WIDTHS"; //$NON-NLS-1$

	private Map<MSymbol, MarketDataViewSubscriber> mModules = new HashMap<MSymbol, MarketDataViewSubscriber>();

	private TextContributionItem mSymbolEntryText;

	private final MarketDataManager mMarketDataManager = PhotonPlugin.getDefault().getMarketDataManager();
	
	private TableViewer mViewer;

	private WritableList mItems;

	private IMemento mViewState;

	private Clipboard mClipboard;

//	private IFeedStatusChangedListener mFeedStatusChangedListener;

	/**
	 * Constructor.
	 */
	public MarketDataView() {
	}

	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site);
		mViewState = memento;
	}

	/**
	 * Returns the clipboard for this view.
	 * 
	 * @return the clipboard for this view
	 */
	public Clipboard getClipboard() {
		if (mClipboard == null) {
			mClipboard = new Clipboard(mViewer.getControl().getDisplay());
		}
		return mClipboard;
	}

	@Override
	public Table getTable() {
		return mViewer != null ? mViewer.getTable() : null;
	}

	@Override
	public void createPartControl(Composite parent) {
		final IActionBars actionBars = getViewSite().getActionBars();
		IToolBarManager toolbar = actionBars.getToolBarManager();
		mSymbolEntryText = new TextContributionItem(""); //$NON-NLS-1$
//		mFeedStatusChangedListener = new IFeedStatusChangedListener() {
//
//			@Override
//			public void feedStatusChanged(FeedStatusEvent event) {
//				handleFeedStatusChanged(event.getNewStatus());				
//			}			
//		};
//		mMarketDataManager.addActiveFeedStatusChangedListener(mFeedStatusChangedListener);
//		handleFeedStatusChanged(mMarketDataManager.getActiveFeedStatus());
		toolbar.add(mSymbolEntryText);
		toolbar.add(new AddSymbolAction(mSymbolEntryText, this));

		final Table table = new Table(parent, SWT.MULTI | SWT.FULL_SELECTION
				| SWT.V_SCROLL | SWT.BORDER);
		table.setHeaderVisible(true);
		mViewer = new TableViewer(table);
		GridDataFactory.defaultsFor(table).applyTo(table);

		final MarketDataItemComparator comparator = new MarketDataItemComparator();
		mViewer.setComparator(comparator);

		SelectionListener listener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// determine new sort column and direction
				TableColumn sortColumn = table.getSortColumn();
				TableColumn currentColumn = (TableColumn) e.widget;
				final int index = table.indexOf(currentColumn);
				int dir = table.getSortDirection();
				if (sortColumn == currentColumn) {
					dir = dir == SWT.UP ? SWT.DOWN : SWT.UP;
				} else {
					table.setSortColumn(currentColumn);
					dir = SWT.UP;
				}
				table.setSortDirection(dir);
				comparator.setSort(dir == SWT.UP ? 1 : -1);
				comparator.setIndex(index);
				mViewer.refresh();
			}
		};

		// create columns, using FIXFieldLocalizer to preserve backwards
		// compatibility
		TableViewerColumn symbolColumn = new TableViewerColumn(mViewer,
				createColumn(table,
						FIXFieldLocalizer.getLocalizedFIXFieldName(Symbol.class
								.getSimpleName()), SWT.LEFT, listener));
		symbolColumn.setEditingSupport(new SymbolEditingSupport());
		createColumn(table, FIXFieldLocalizer
				.getLocalizedFIXFieldName(LastPx.class.getSimpleName()),
				SWT.RIGHT, listener);
		createColumn(table, FIXFieldLocalizer
				.getLocalizedFIXFieldName(LastQty.class.getSimpleName()),
				SWT.RIGHT, listener);
		createColumn(table, FIXFieldLocalizer
				.getLocalizedFIXFieldName(BidSize.class.getSimpleName()),
				SWT.RIGHT, listener);
		createColumn(table, FIXFieldLocalizer
				.getLocalizedFIXFieldName(BidPx.class.getSimpleName()),
				SWT.RIGHT, listener);
		createColumn(table, FIXFieldLocalizer
				.getLocalizedFIXFieldName(OfferPx.class.getSimpleName()),
				SWT.RIGHT, listener);
		createColumn(table, FIXFieldLocalizer
				.getLocalizedFIXFieldName(OfferSize.class.getSimpleName()),
				SWT.RIGHT, listener);

		// restore table state if it exists
		if (mViewState != null) {
			String columnOrderString = mViewState.getString(COLUMN_ORDER);
			if (columnOrderString != null) {
				int[] columnOrder = deserialize(columnOrderString);
				if (columnOrder.length == table.getColumns().length) {
					table.setColumnOrder(columnOrder);
				}
			}
			String columnWidthsString = mViewState.getString(COLUMN_WIDTHS);
			if (columnWidthsString != null) {
				int[] columnWidths = deserialize(columnWidthsString);
				if (columnWidths.length == table.getColumns().length) {
					for (int i = 0; i < columnWidths.length; i++) {
						table.getColumn(i).setWidth(columnWidths[i]);
					}
				}
			}
			String columnRestoredWidthsString = mViewState
					.getString(COLUMN_RESTORED_WIDTHS);
			if (columnRestoredWidthsString != null) {
				int[] restoredWidths = deserialize(columnRestoredWidthsString);
				if (restoredWidths.length == table.getColumns().length) {
					for (int i = 0; i < restoredWidths.length; i++) {
						table.getColumn(i).setData(RESTORED_WIDTH_KEY,
								restoredWidths[i]);
					}
				}
			}
		}

		registerContextMenu();
		getSite().setSelectionProvider(mViewer);

		ObservableListContentProvider content = new ObservableListContentProvider();
		mViewer.setContentProvider(content);
		IObservableMap[] maps = BeansObservables.observeMaps(content
				.getKnownElements(), MarketDataViewItem.class, new String[] {
				"symbol", //$NON-NLS-1$
				"lastPx", //$NON-NLS-1$
				"lastQty", //$NON-NLS-1$
				"bidSize", //$NON-NLS-1$
				"bidPx", //$NON-NLS-1$
				"offerPx", //$NON-NLS-1$
				"offerSize" }); //$NON-NLS-1$
		mViewer.setLabelProvider(new ObservableMapLabelProvider(maps));
		mViewer.setUseHashlookup(true);
		mItems = WritableList.withElementType(MarketDataViewItem.class);
		mViewer.setInput(mItems);
	}

	private TableColumn createColumn(final Table table, String text,
			int alignment, SelectionListener listener) {
		final TableColumn column = new TableColumn(table, SWT.NONE);
		column.setWidth(70);
		column.setText(text);
		column.setMoveable(true);
		column.setAlignment(alignment);
		column.addSelectionListener(listener);
		return column;
	}

	/**
	 * Register the context menu for the viewer so that commands may be added to
	 * it.
	 */
	private void registerContextMenu() {
		MenuManager contextMenu = new MenuManager();
		contextMenu.setRemoveAllWhenShown(true);
		getSite().registerContextMenu(contextMenu, mViewer);
		Control control = mViewer.getControl();
		Menu menu = contextMenu.createContextMenu(control);
		control.setMenu(menu);
	}

	@Override
	public void saveState(IMemento memento) {
		memento.putString(COLUMN_ORDER, serialize(getTable().getColumnOrder()));
		final TableColumn[] columns = getTable().getColumns();
		int[] columnWidths = new int[columns.length];
		for (int i = 0; i < columns.length; i++) {
			columnWidths[i] = columns[i].getWidth();
		}
		memento.putString(COLUMN_WIDTHS, serialize(columnWidths));
		int[] restoredWidths = new int[columns.length];
		for (int i = 0; i < columns.length; i++) {
			final Integer restoredWidth = (Integer) columns[i]
					.getData(RESTORED_WIDTH_KEY);
			restoredWidths[i] = restoredWidth == null ? 70 : restoredWidth;
		}
		memento.putString(COLUMN_RESTORED_WIDTHS, serialize(restoredWidths));
	}

	private String serialize(int[] array) {
		StringBuilder builder = new StringBuilder();
		if (array.length > 0) {
			builder.append(array[0]);
			for (int i = 1; i < array.length; i++) {
				builder.append(',');
				builder.append(array[i]);
			}
		}
		return builder.toString();
	}

	private int[] deserialize(String string) {
		String[] split = string.split(","); //$NON-NLS-1$
		int[] array = new int[split.length];
		try {
			for (int i = 0; i < split.length; i++) {
				array[i] = Integer.parseInt(split[i]);
			}
		} catch (NumberFormatException e) {
			return new int[0];
		}
		return array;
	}

//	private void handleFeedStatusChanged(FeedStatus status) {
//		if (mSymbolEntryText == null) {
//			return;
//		}
//		if (status == FeedStatus.AVAILABLE) {
//			mSymbolEntryText.setEnabled(true);
//		} else {
//			mSymbolEntryText.setEnabled(false);
//		}
//	}

	@Override
	public void setFocus() {
		if (mSymbolEntryText.isEnabled())
			mSymbolEntryText.setFocus();
	}

	@Override
	public boolean isListeningSymbol(MSymbol symbol) {
		return false;
	}

	@Override
	public void onAssertSymbol(MSymbol symbol) {
		addSymbol(symbol);
	}

	/**
	 * Adds a new row in the view for the given symbol (if one does not already
	 * exist).
	 * 
	 * @param symbol
	 *            symbol to add to view
	 */
	public void addSymbol(MSymbol symbol) {
		if (mModules.containsKey(symbol)) {
			PhotonPlugin.getMainConsoleLogger().warn(
					DUPLICATE_SYMBOL.getText(symbol));
		} else {
			MarketDataViewItem item = new MarketDataViewItem(symbol);
			mModules.put(symbol, doSubscribe(item));
			mItems.add(item);
		}
	}

	private MarketDataViewSubscriber doSubscribe(final MarketDataViewItem viewItem) {
		final MarketDataViewSubscriber subscriber = new MarketDataViewSubscriber(viewItem);
		BusyIndicator.showWhile(getViewSite().getShell().getDisplay(), new Runnable() {
		
			@Override
			public void run() {
				mMarketDataManager.addSubscriber(subscriber);
			}
		});
		return subscriber;
	}

	private void remove(MarketDataViewItem item) {

		mMarketDataManager.removeSubscriber(mModules.get(item.getSymbol()));
		mModules.remove(item.getSymbol());
		mItems.remove(item);
	}

	@Override
	public void dispose() {
//		mMarketDataManager.removeActiveFeedStatusChangedListener(mFeedStatusChangedListener);
		for (Object object : mItems) {
			mMarketDataManager.removeSubscriber(mModules.get(((MarketDataViewItem) object).getSymbol()));
		}
		if (mClipboard != null) {
			mClipboard.dispose();
		}
		super.dispose();
	}	
	
	private final class MarketDataViewSubscriber extends MarketDataSubscriber {

		MarketDataViewItem mItem;
		
		MarketDataViewSubscriber(MarketDataViewItem item) {
			super(item.getSymbol().toString());
			mItem = item;
		}
		
		@Override
		public void receiveData(final Object inData) {
			getViewSite().getShell().getDisplay().asyncExec(new Runnable() {

				@Override
				public void run() {
					if (inData instanceof BidEvent) {
						mItem.setBidEvent((BidEvent) inData);
					} else if (inData instanceof AskEvent) {
						mItem.setAskEvent((AskEvent) inData);
					} else if (inData instanceof TradeEvent) {
						mItem.setTradeEvent((TradeEvent) inData);
					} else {
						MARKET_DATA_UNEXPECTED_EVENT_TYPE.warn(this, inData
								.getClass());
					}
				}
			});
		}
		
	}

	/**
	 * Handles column sorting.
	 * 
	 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
	 * @version $Id$
	 * @since $Release$
	 */
	@ClassVersion("$Id$")//$NON-NLS-1$
	private static class MarketDataItemComparator extends ViewerComparator {

		private int mIndex = -1;
		private int mDirection = 0;

		/**
		 * @param index
		 *            index of sort column
		 */
		public void setIndex(int index) {
			mIndex = index;
		}

		/**
		 * @param direction
		 *            sort direction, 1 for ascending and -1 for descending
		 */
		public void setSort(int direction) {
			mDirection = direction;
		}

		@Override
		public int compare(Viewer viewer, Object e1, Object e2) {
			if (mIndex == -1)
				return 0;
			MarketDataViewItem item1 = (MarketDataViewItem) e1;
			MarketDataViewItem item2 = (MarketDataViewItem) e2;
			int compare;
			switch (mIndex) {
			case 0:
				compare = compareNulls(item1.getSymbol().toString(), item2
						.getSymbol().toString());
				if (compare == 0) {
					compare = item1.getSymbol().toString().compareTo(
							item2.getSymbol().toString());
				}
				break;
			case 1:
				compare = compareNulls(item1.getLastPx(), item2.getLastPx());
				if (compare == 0) {
					compare = item1.getLastPx().compareTo(item2.getLastPx());
				}
				break;
			case 2:
				compare = compareNulls(item1.getLastQty(), item2.getLastQty());
				if (compare == 0) {
					compare = item1.getLastQty().compareTo(item2.getLastQty());
				}
				break;
			case 3:
				compare = compareNulls(item1.getBidSize(), item2.getBidSize());
				if (compare == 0) {
					compare = item1.getBidSize().compareTo(item2.getBidSize());
				}
				break;
			case 4:
				compare = compareNulls(item1.getBidPx(), item2.getBidPx());
				if (compare == 0) {
					compare = item1.getBidPx().compareTo(item2.getBidPx());
				}
				break;
			case 5:
				compare = compareNulls(item1.getOfferPx(), item2.getOfferPx());
				if (compare == 0) {
					compare = item1.getOfferPx().compareTo(item2.getOfferPx());
				}
				break;
			case 6:
				compare = compareNulls(item1.getOfferSize(), item2
						.getOfferSize());
				if (compare == 0) {
					compare = item1.getOfferSize().compareTo(
							item2.getOfferSize());
				}
				break;
			default:
				throw new AssertionFailedException("Invalid column index"); //$NON-NLS-1$
			}
			return mDirection * compare;
		}

		private int compareNulls(Object o1, Object o2) {
			if (o1 == null) {
				return (o2 == null) ? 0 : -1;
			}
			if (o2 == null) {
				return (o1 == null) ? 0 : 1;
			}
			return 0;
		}
	}

	/**
	 * Provides support for editing symbols in-line
	 * 
	 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
	 * @version $Id$
	 * @since $Release$
	 */
	@ClassVersion("$Id$")//$NON-NLS-1$
	private final class SymbolEditingSupport extends EditingSupport {

		private final TextCellEditor mTextCellEditor;

		private SymbolEditingSupport() {
			super(mViewer);
			this.mTextCellEditor = new TextCellEditor(mViewer.getTable());
		}

		@Override
		protected void setValue(Object element, Object value) {
			if (StringUtils.isBlank(value.toString()))
				return;
			MarketDataViewItem item = (MarketDataViewItem) element;
			final MSymbol symbol = item.getSymbol();
			if (symbol.toString().equals(value))
				return;
			MSymbol newSymbol = new MSymbol(value.toString());
			if (mModules.containsKey(newSymbol)) {
				PhotonPlugin.getMainConsoleLogger().warn(
						DUPLICATE_SYMBOL.getText(symbol));
				return;
			}

			mMarketDataManager.removeSubscriber(mModules.get(symbol));
			mModules.remove(symbol);
			item.setSymbol(newSymbol);
			mModules.put(newSymbol, doSubscribe(item));
		}

		@Override
		protected Object getValue(Object element) {
			return ((MarketDataViewItem) element).getSymbol().toString();
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			return mTextCellEditor;
		}

		@Override
		protected boolean canEdit(Object element) {
			return true;
		}
	}

	/**
	 * Handles the delete command for this view.
	 * 
	 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
	 * @version $Id$
	 * @since $Release$
	 */
	@ClassVersion("$Id$")//$NON-NLS-1$
	public static final class DeleteCommandHandler extends AbstractHandler
			implements IHandler {

		@Override
		public Object execute(ExecutionEvent event) throws ExecutionException {
			IWorkbenchPart part = HandlerUtil.getActivePartChecked(event);
			ISelection selection = HandlerUtil
					.getCurrentSelectionChecked(event);
			if (part instanceof MarketDataView
					&& selection instanceof IStructuredSelection) {
				MarketDataView view = (MarketDataView) part;
				IStructuredSelection sselection = (IStructuredSelection) selection;
				for (Object obj : sselection.toArray()) {
					if (obj instanceof MarketDataViewItem) {
						view.remove((MarketDataViewItem) obj);
					}
				}
			}
			return null;
		}
	}

	/**
	 * Handles the copy command for this view
	 * 
	 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
	 * @version $Id$
	 * @since $Release$
	 */
	@ClassVersion("$Id$")//$NON-NLS-1$
	public static final class CopyCommandHandler extends AbstractHandler
			implements IHandler {

		@Override
		public Object execute(ExecutionEvent event) throws ExecutionException {
			IWorkbenchPart part = HandlerUtil.getActivePartChecked(event);
			ISelection selection = HandlerUtil
					.getCurrentSelectionChecked(event);
			if (part instanceof MarketDataView
					&& selection instanceof IStructuredSelection) {
				MarketDataView view = (MarketDataView) part;
				IStructuredSelection sselection = (IStructuredSelection) selection;
				StringBuilder builder = new StringBuilder();
				for (Object obj : sselection.toArray()) {
					if (obj instanceof MarketDataViewItem) {
						MarketDataViewItem item = (MarketDataViewItem) obj;
						builder.append(item);
						builder.append(System.getProperty("line.separator")); //$NON-NLS-1$
					}
				}
				view.getClipboard().setContents(
						new Object[] { builder.toString() },
						new Transfer[] { TextTransfer.getInstance() });
			}
			return null;
		}
	}

}
