package org.marketcetera.photon.views;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.databinding.observable.map.CompositeMap;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.runtime.AssertionFailedException;
import org.eclipse.emf.databinding.EMFObservables;
import org.eclipse.emf.ecore.EStructuralFeature;
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
import org.marketcetera.photon.FIXFieldLocalizer;
import org.marketcetera.photon.Messages;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.commons.ui.table.ColumnState;
import org.marketcetera.photon.commons.ui.table.ChooseColumnsMenu.IColumnProvider;
import org.marketcetera.photon.marketdata.MarketDataManager;
import org.marketcetera.photon.model.marketdata.MDPackage;
import org.marketcetera.photon.ui.TextContributionItem;
import org.marketcetera.trade.MSymbol;
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
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public final class MarketDataView extends ViewPart implements IMSymbolListener,
		IColumnProvider, Messages {

	/**
	 * The view ID.
	 */
	public static final String ID = "org.marketcetera.photon.views.MarketDataView"; //$NON-NLS-1$

	private Map<MSymbol, MarketDataViewItem> mItemMap = new HashMap<MSymbol, MarketDataViewItem>();

	private TextContributionItem mSymbolEntryText;

	private final MarketDataManager mMarketDataManager = PhotonPlugin.getDefault().getMarketDataManager();
	
	private TableViewer mViewer;

	private WritableList mItems;

	private IMemento mViewState;

	private Clipboard mClipboard;

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
	public Table getColumnWidget() {
		return mViewer != null ? mViewer.getTable() : null;
	}

	@Override
	public void createPartControl(Composite parent) {
		final IActionBars actionBars = getViewSite().getActionBars();
		IToolBarManager toolbar = actionBars.getToolBarManager();
		mSymbolEntryText = new TextContributionItem(""); //$NON-NLS-1$
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
			ColumnState.restore(table, mViewState);
			for (TableColumn column : table.getColumns()) {
				if (column.getWidth() == 0) {
					column.setResizable(false);
				}
			}
		}

		registerContextMenu();
		getSite().setSelectionProvider(mViewer);

		ObservableListContentProvider content = new ObservableListContentProvider();
		mViewer.setContentProvider(content);
		IObservableSet domain = content.getKnownElements();
		IObservableMap[] maps = new IObservableMap[] {
				BeansObservables.observeMap(domain, MarketDataViewItem.class, "symbol"), //$NON-NLS-1$
				createCompositeMap(domain, "latestTick", MDPackage.Literals.MD_LATEST_TICK__PRICE), //$NON-NLS-1$
				createCompositeMap(domain, "latestTick", MDPackage.Literals.MD_LATEST_TICK__SIZE), //$NON-NLS-1$
				createCompositeMap(domain, "topOfBook", MDPackage.Literals.MD_TOP_OF_BOOK__BID_SIZE), //$NON-NLS-1$
				createCompositeMap(domain,
						"topOfBook", MDPackage.Literals.MD_TOP_OF_BOOK__BID_PRICE), //$NON-NLS-1$
				createCompositeMap(domain,
						"topOfBook", MDPackage.Literals.MD_TOP_OF_BOOK__ASK_PRICE), //$NON-NLS-1$
				createCompositeMap(domain, "topOfBook", MDPackage.Literals.MD_TOP_OF_BOOK__ASK_SIZE) //$NON-NLS-1$
		};
		mViewer.setLabelProvider(new ObservableMapLabelProvider(maps));
		mViewer.setUseHashlookup(true);
		mItems = WritableList.withElementType(MarketDataViewItem.class);
		mViewer.setInput(mItems);
	}

	private IObservableMap createCompositeMap(IObservableSet domain, String property,
			EStructuralFeature feature) {
		return new CompositeMap(BeansObservables.observeMap(domain, MarketDataViewItem.class,
				property), EMFObservables.mapFactory(feature));
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
		ColumnState.save(getColumnWidget(), memento);
	}

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
	public void addSymbol(final MSymbol symbol) {
		if (mItemMap.containsKey(symbol)) {
			PhotonPlugin.getMainConsoleLogger().warn(
					DUPLICATE_SYMBOL.getText(symbol));
		} else {
			busyRun(new Runnable() {
				@Override
				public void run() {
					MarketDataViewItem item = new MarketDataViewItem(mMarketDataManager
							.getMarketData(), symbol);
					mItemMap.put(symbol, item);
					mItems.add(item);
				}
			});
		}
	}

	private void busyRun(Runnable runnable) {
		BusyIndicator.showWhile(getViewSite().getShell().getDisplay(), runnable);
	}

	private void remove(final MarketDataViewItem item) {
		busyRun(new Runnable() {
			@Override
			public void run() {
				mItemMap.remove(item.getSymbol());
				mItems.remove(item);
				item.dispose();
			}
		});
	}

	@Override
	public void dispose() {
		for (MarketDataViewItem item : mItemMap.values()) {
		    item.dispose();
		}
		mItemMap = null;
		mItems = null;
		if (mClipboard != null) {
			mClipboard.dispose();
		}
		super.dispose();
	}

	/**
	 * Handles column sorting.
	 * 
	 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
	 * @version $Id$
	 * @since 1.0.0
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
				String symbol1 = item1.getSymbol().toString();
				String symbol2 = item2.getSymbol().toString();
				compare = compareNulls(symbol1, symbol2);
				if (compare == 0) {
					compare = symbol1.compareTo(symbol2);
				}
				break;
			case 1:
				BigDecimal tradePrice1 = item1.getLatestTick().getPrice();
				BigDecimal tradePrice2 = item2.getLatestTick().getPrice();
				compare = compareNulls(tradePrice1, tradePrice2);
				if (compare == 0) {
					compare = tradePrice1.compareTo(tradePrice2);
				}
				break;
			case 2:
				BigDecimal tradeSize1 = item1.getLatestTick().getSize();
				BigDecimal tradeSize2 = item2.getLatestTick().getSize();
				compare = compareNulls(tradeSize1, tradeSize2);
				if (compare == 0) {
					compare = tradeSize1.compareTo(tradeSize2);
				}
				break;
			case 3:
				BigDecimal bidSize1 = item1.getTopOfBook().getBidSize();
				BigDecimal bidSize2 = item2.getTopOfBook().getBidSize();
				compare = compareNulls(bidSize1, bidSize2);
				if (compare == 0) {
					compare = bidSize1.compareTo(bidSize2);
				}
				break;
			case 4:
				BigDecimal bidPrice1 = item1.getTopOfBook().getBidPrice();
				BigDecimal bidPrice2 = item2.getTopOfBook().getBidPrice();
				compare = compareNulls(bidPrice1, bidPrice2);
				if (compare == 0) {
					compare = bidPrice1.compareTo(bidPrice2);
				}
				break;
			case 5:
				BigDecimal askPrice1 = item1.getTopOfBook().getAskPrice();
				BigDecimal askPrice2 = item2.getTopOfBook().getAskPrice();
				compare = compareNulls(askPrice1, askPrice2);
				if (compare == 0) {
					compare = askPrice1.compareTo(askPrice2);
				}
				break;
			case 6:
				BigDecimal askSize1 = item1.getTopOfBook().getAskSize();
				BigDecimal askSize2 = item2.getTopOfBook().getAskSize();
				compare = compareNulls(askSize1, askSize2);
				if (compare == 0) {
					compare = askSize1.compareTo(askSize2);
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
	 * @since 1.0.0
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
			final MarketDataViewItem item = (MarketDataViewItem) element;
			final MSymbol symbol = item.getSymbol();
			if (symbol.toString().equals(value))
				return;
			final MSymbol newSymbol = new MSymbol(value.toString());
			if (mItemMap.containsKey(newSymbol)) {
				PhotonPlugin.getMainConsoleLogger().warn(
						DUPLICATE_SYMBOL.getText(symbol));
				return;
			}
			busyRun(new Runnable() {
				@Override
				public void run() {
					mItemMap.remove(symbol);
					item.setSymbol(newSymbol);
					mItemMap.put(newSymbol, item);
				}
			});
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
	 * @since 1.0.0
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
	 * @since 1.0.0
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
