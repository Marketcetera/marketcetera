package org.marketcetera.photon.views;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.databinding.EMFObservables;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.part.ViewPart;
import org.marketcetera.photon.FIXFieldLocalizer;
import org.marketcetera.photon.Messages;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.commons.ui.workbench.ChooseColumnsMenu.IColumnProvider;
import org.marketcetera.photon.commons.ui.workbench.ColumnState;
import org.marketcetera.photon.marketdata.IMarketDataManager;
import org.marketcetera.photon.model.marketdata.MDPackage;
import org.marketcetera.photon.ui.TextContributionItem;
import org.marketcetera.trade.*;
import org.marketcetera.trade.Side;
import org.marketcetera.trade.TimeInForce;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;

import quickfix.field.*;

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

    private Map<Instrument, MarketDataViewItem> mItemMap = new LinkedHashMap<Instrument, MarketDataViewItem>();

    private TextContributionItem mSymbolEntryText;

    private final IMarketDataManager mMarketDataManager = PhotonPlugin
            .getDefault().getMarketDataManager();

    private TableViewer mViewer;

    private WritableList mItems;

    private IMemento mViewState;

    private Clipboard mClipboard;

    private final static String INSTRUMENT_LIST = "Instrument_List"; //$NON-NLS-1$

    public static enum MD_TABLE_COLUMNS {
        MD_SYMBOL, LAST_PX, LAST_SZ, BID_SZ, BID_PX, OFFER_PX, OFFER_SZ, PREV_CLOSE, OPEN_PX, HIGH_PX, LOW_PX, TRD_VOLUME,
    }
    // default FX *100 size
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
        createColumn(table,
                FIXFieldLocalizer.getLocalizedFIXFieldName(LastPx.class
                        .getSimpleName()), SWT.RIGHT, listener);
        createColumn(table,
                FIXFieldLocalizer.getLocalizedFIXFieldName(LastQty.class
                        .getSimpleName()), SWT.RIGHT, listener);
        createColumn(table,
                FIXFieldLocalizer.getLocalizedFIXFieldName(BidSize.class
                        .getSimpleName()), SWT.RIGHT, listener);
        createColumn(table,
                FIXFieldLocalizer.getLocalizedFIXFieldName(BidPx.class
                        .getSimpleName()), SWT.RIGHT, listener);
        createColumn(table,
                FIXFieldLocalizer.getLocalizedFIXFieldName(OfferPx.class
                        .getSimpleName()), SWT.RIGHT, listener);
        createColumn(table,
                FIXFieldLocalizer.getLocalizedFIXFieldName(OfferSize.class
                        .getSimpleName()), SWT.RIGHT, listener);
        createColumn(table,
                FIXFieldLocalizer.getLocalizedFIXFieldName(PrevClosePx.class
                        .getSimpleName()), SWT.RIGHT, listener);
        createColumn(table,
                FIXFieldLocalizer.getLocalizedFIXFieldName(OpenClose.class
                        .getSimpleName()), SWT.RIGHT, listener);
        createColumn(table,
                FIXFieldLocalizer.getLocalizedFIXFieldName(HighPx.class
                        .getSimpleName()), SWT.RIGHT, listener);
        createColumn(table,
                FIXFieldLocalizer.getLocalizedFIXFieldName(LowPx.class
                        .getSimpleName()), SWT.RIGHT, listener);
        createColumn(table,
                FIXFieldLocalizer.getLocalizedFIXFieldName(TradeVolume.class
                        .getSimpleName()), SWT.RIGHT, listener);

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
                BeansObservables.observeMap(domain, MarketDataViewItem.class,
                        "symbol"), //$NON-NLS-1$
                createCompositeMap(domain,
                        "latestTick", MDPackage.Literals.MD_LATEST_TICK__PRICE), //$NON-NLS-1$
                createCompositeMap(domain,
                        "latestTick", MDPackage.Literals.MD_LATEST_TICK__SIZE), //$NON-NLS-1$
                createCompositeMap(
                        domain,
                        "topOfBook", MDPackage.Literals.MD_TOP_OF_BOOK__BID_SIZE), //$NON-NLS-1$
                createCompositeMap(
                        domain,
                        "topOfBook", MDPackage.Literals.MD_TOP_OF_BOOK__BID_PRICE), //$NON-NLS-1$
                createCompositeMap(
                        domain,
                        "topOfBook", MDPackage.Literals.MD_TOP_OF_BOOK__ASK_PRICE), //$NON-NLS-1$
                createCompositeMap(
                        domain,
                        "topOfBook", MDPackage.Literals.MD_TOP_OF_BOOK__ASK_SIZE), //$NON-NLS-1$
                createCompositeMap(
                        domain,
                        "marketStat", MDPackage.Literals.MD_MARKETSTAT__PREVIOUS_CLOSE_PRICE), //$NON-NLS-1$							
                createCompositeMap(
                        domain,
                        "marketStat", MDPackage.Literals.MD_MARKETSTAT__OPEN_PRICE), //$NON-NLS-1$
                createCompositeMap(
                        domain,
                        "marketStat", MDPackage.Literals.MD_MARKETSTAT__HIGH_PRICE), //$NON-NLS-1$
                createCompositeMap(
                        domain,
                        "marketStat", MDPackage.Literals.MD_MARKETSTAT__LOW_PRICE), //$NON-NLS-1$				
                createCompositeMap(domain,
                        "marketStat", MDPackage.Literals.MD_MARKETSTAT__VOLUME) //$NON-NLS-1$
        };

        mViewer.setLabelProvider(new ObservableMapLabelProvider(maps));
        mViewer.setUseHashlookup(true);
        mItems = WritableList.withElementType(MarketDataViewItem.class);
        mViewer.setInput(mItems);
        if (mViewState != null ) {
            IMemento[] symbList = mViewState.getChildren(INSTRUMENT_LIST);
            if (symbList != null)
                for (IMemento symbolMemo : symbList) {
                    addSymbol(InstrumentFromMemento.restore(symbolMemo));
                }
        }

        table.addListener(SWT.MouseDoubleClick, new Listener() {
            @Override
            public void handleEvent(Event event) {
                Point pt = new Point(event.x, event.y);
                TableItem item = table.getItem(pt);
                if (item == null)
                    return;
                int col_count = table.getColumnCount();
                for (int i = 0; i < col_count; i++) {
                    Rectangle rect = item.getBounds(i);
                    if (rect.contains(pt)) {
                        MD_TABLE_COLUMNS enum_col = MD_TABLE_COLUMNS.values()[i];
                        MarketDataViewItem mdi = (MarketDataViewItem) item
                                .getData();
                        if (mdi != null && mdi.getTopOfBook() != null)
                            switch (enum_col) {
                            case BID_PX: // hit the bid
                                newOrder(mdi, Side.Sell, mdi.getTopOfBook()
                                        .getBidPrice(), mdi.getTopOfBook()
                                        .getBidSize());
                                break;
                            case BID_SZ: // join the bid
                                newOrder(mdi, Side.Buy, mdi.getTopOfBook()
                                        .getBidPrice(), null);
                                break;
                            case OFFER_PX: // lift the offer
                                newOrder(mdi, Side.Buy, mdi.getTopOfBook()
                                        .getAskPrice(), mdi.getTopOfBook()
                                        .getAskSize());
                                break;
                            case OFFER_SZ: // join the ask
                                newOrder(mdi, Side.Sell, mdi.getTopOfBook()
                                        .getAskPrice(), null);
                                break;
                           default:
                        	   break;
                            }
                    }
                }
            }
        });

    }

    private void newOrder(final MarketDataViewItem mdi, final Side side,
            final BigDecimal Px, final BigDecimal ordSz) {
        busyRun(new Runnable() {
            public void run() {
                BigDecimal defaultOrderSize = getDefaultOrderSize(
                        mdi.getInstrument(), Px);

                Instrument instrument = mdi.getInstrument();
                OrderSingle newOrder = Factory.getInstance()
                        .createOrderSingle();
                newOrder.setInstrument(instrument);
                newOrder.setOrderType(OrderType.Limit);
                newOrder.setSide(side);
                newOrder.setQuantity(defaultOrderSize);
                if (ordSz != null && ordSz.compareTo(defaultOrderSize) == -1) {
                    newOrder.setQuantity(ordSz);
                }
                newOrder.setPrice(Px);
                newOrder.setTimeInForce(TimeInForce.Day);
                try {
                    PhotonPlugin.getDefault().showOrderInTicket(newOrder);
                } catch (WorkbenchException e) {
                    SLF4JLoggerProxy.error(this, e);
                    ErrorDialog.openError(
                            null,
                            null,
                            null,
                            new Status(IStatus.ERROR, PhotonPlugin.ID, e
                                    .getLocalizedMessage()));
                }
            }
        });
    }

    private IObservableMap createCompositeMap(IObservableSet domain,
            String property, EStructuralFeature feature) {
        return new CompositeMap(BeansObservables.observeMap(domain,
                MarketDataViewItem.class, property),
                EMFObservables.mapFactory(feature));
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
        if (memento != null)
            for (Instrument instr : mItemMap.keySet()) {
                IMemento smyb = memento.createChild(INSTRUMENT_LIST);
                InstrumentToMemento.save(instr, smyb);
            }
    }

    @Override
    public void setFocus() {
        if (mSymbolEntryText.isEnabled())
            mSymbolEntryText.setFocus();
    }

    @Override
    public boolean isListeningSymbol(Instrument instrument) {
        return false;
    }

    @Override
    public void onAssertSymbol(Instrument instrument) {
        addSymbol(instrument);
    }

    /**
     * Adds a new row in the view for the given symbol (if one does not already
     * exist).
     * 
     * @param symbol
     *            symbol to add to view
     */
    public void addSymbol(final Instrument instrument) {
        if (mItemMap.containsKey(instrument)) {
            PhotonPlugin.getMainConsoleLogger().warn(
                    DUPLICATE_SYMBOL.getText(instrument));
        } else {
            busyRun(new Runnable() {
                @Override
                public void run() {
                    MarketDataViewItem item = new MarketDataViewItem(
                            mMarketDataManager.getMarketData(), instrument);
                    // change to request ID
                    mItemMap.put(instrument, item);
                    mItems.add(item);
                }
            });
        }
    }

    private void busyRun(Runnable runnable) {
        BusyIndicator
                .showWhile(getViewSite().getShell().getDisplay(), runnable);
    }

    private void remove(final MarketDataViewItem item) {
        mItemMap.remove(item.getInstrument());
        mItems.remove(item);
        item.dispose();
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
            MD_TABLE_COLUMNS enum_col = MD_TABLE_COLUMNS.values()[mIndex];
            switch (enum_col) {
            case MD_SYMBOL:
                String symbol1 = item1.getInstrument().getSymbol();
                String symbol2 = item2.getInstrument().getSymbol();
                compare = compareNulls(symbol1, symbol2);
                if (compare == 0) {
                    compare = symbol1.compareTo(symbol2);
                }
                break;
            case LAST_PX:
                BigDecimal tradePrice1 = item1.getLatestTick().getPrice();
                BigDecimal tradePrice2 = item2.getLatestTick().getPrice();
                compare = compareNulls(tradePrice1, tradePrice2);
                if (compare == 0) {
                    compare = tradePrice1.compareTo(tradePrice2);
                }
                break;
            case LAST_SZ:
                BigDecimal tradeSize1 = item1.getLatestTick().getSize();
                BigDecimal tradeSize2 = item2.getLatestTick().getSize();
                compare = compareNulls(tradeSize1, tradeSize2);
                if (compare == 0) {
                    compare = tradeSize1.compareTo(tradeSize2);
                }
                break;
            case BID_SZ:
                BigDecimal bidSize1 = item1.getTopOfBook().getBidSize();
                BigDecimal bidSize2 = item2.getTopOfBook().getBidSize();
                compare = compareNulls(bidSize1, bidSize2);
                if (compare == 0) {
                    compare = bidSize1.compareTo(bidSize2);
                }
                break;
            case BID_PX:
                BigDecimal bidPrice1 = item1.getTopOfBook().getBidPrice();
                BigDecimal bidPrice2 = item2.getTopOfBook().getBidPrice();
                compare = compareNulls(bidPrice1, bidPrice2);
                if (compare == 0) {
                    compare = bidPrice1.compareTo(bidPrice2);
                }
                break;
            case OFFER_PX:
                BigDecimal askPrice1 = item1.getTopOfBook().getAskPrice();
                BigDecimal askPrice2 = item2.getTopOfBook().getAskPrice();
                compare = compareNulls(askPrice1, askPrice2);
                if (compare == 0) {
                    compare = askPrice1.compareTo(askPrice2);
                }
                break;
            case OFFER_SZ:
                BigDecimal askSize1 = item1.getTopOfBook().getAskSize();
                BigDecimal askSize2 = item2.getTopOfBook().getAskSize();
                compare = compareNulls(askSize1, askSize2);
                if (compare == 0) {
                    compare = askSize1.compareTo(askSize2);
                }
                break;
            case PREV_CLOSE:
                BigDecimal closeSize1 = item1.getMarketStat()
                        .getPreviousClosePrice();
                BigDecimal closeSize2 = item2.getMarketStat()
                        .getPreviousClosePrice();
                compare = compareNulls(closeSize1, closeSize2);
                if (compare == 0) {
                    compare = closeSize1.compareTo(closeSize2);

                }
                break;
            case OPEN_PX:
                BigDecimal openSize1 = item1.getMarketStat().getOpenPrice();
                BigDecimal openSize2 = item2.getMarketStat().getOpenPrice();
                compare = compareNulls(openSize1, openSize2);
                if (compare == 0) {
                    compare = openSize1.compareTo(openSize2);
                }
                break;
            case HIGH_PX:
                BigDecimal highSize1 = item1.getMarketStat().getHighPrice();
                BigDecimal highSize2 = item2.getMarketStat().getHighPrice();
                compare = compareNulls(highSize1, highSize2);
                if (compare == 0) {
                    compare = highSize1.compareTo(highSize2);
                }
                break;
            case LOW_PX:
                BigDecimal lowSize1 = item1.getMarketStat().getLowPrice();
                BigDecimal lowSize2 = item2.getMarketStat().getLowPrice();
                compare = compareNulls(lowSize1, lowSize2);
                if (compare == 0) {
                    compare = lowSize1.compareTo(lowSize2);
                }
                break;
            case TRD_VOLUME:
                BigDecimal volumeSize1 = item1.getMarketStat()
                        .getVolumeTraded();
                BigDecimal volumeSize2 = item2.getMarketStat()
                        .getVolumeTraded();
                compare = compareNulls(volumeSize1, volumeSize2);
                if (compare == 0) {
                    compare = volumeSize1.compareTo(volumeSize2);
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
            final Instrument instrument = item.getInstrument();
            if (instrument.getSymbol().equals(value))
                return;

            final Instrument newInstrument = PhotonPlugin.getDefault()
                    .getSymbolResolver().resolveSymbol(value.toString());
            if (mItemMap.containsKey(newInstrument)) {
                PhotonPlugin.getMainConsoleLogger().warn(
                        DUPLICATE_SYMBOL.getText(newInstrument.getSymbol()));
                return;
            }
            busyRun(new Runnable() {
                @Override
                public void run() {
                    mItemMap.remove(instrument);
                    item.setInstrument(newInstrument);
                    mItemMap.put(newInstrument, item);
                }
            });
        }

        @Override
        protected Object getValue(Object element) {
            return ((MarketDataViewItem) element).getInstrument().getSymbol();
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
    @ClassVersion("$Id$")
    public static final class DeleteCommandHandler extends AbstractHandler
            implements IHandler {

        @Override
        public Object execute(ExecutionEvent event) throws ExecutionException {
            IWorkbenchPart part = HandlerUtil.getActivePartChecked(event);
            ISelection selection = HandlerUtil
                    .getCurrentSelectionChecked(event);
            if (part instanceof MarketDataView
                    && selection instanceof IStructuredSelection) {
                final MarketDataView view = (MarketDataView) part;
                final IStructuredSelection sselection = (IStructuredSelection) selection;
                // this can take some time
                view.busyRun(new Runnable() {
                    public void run() {
                        for (Object obj : sselection.toArray()) {
                            if (obj instanceof MarketDataViewItem) {
                                view.remove((MarketDataViewItem) obj);
                            }
                        }
                    }
                });
            }
            return null;
        }
    }

    @ClassVersion("$Id$")
    public static final class BuyCommandHandler extends OrderCommandHandler
            implements IHandler {
        @Override
        public Object execute(ExecutionEvent event) throws ExecutionException {
            super.setSide(Side.Buy);
            return super.execute(event);
        }
    }

    @ClassVersion("$Id$")
    public static final class SellCommandHandler extends OrderCommandHandler
            implements IHandler {
        @Override
        public Object execute(ExecutionEvent event) throws ExecutionException {
            super.setSide(Side.Sell);
            return super.execute(event);
        }
    }

    public static BigDecimal getDefaultOrderSize(Instrument instr,
                                                 BigDecimal Px) {
        BigDecimal defaultOrderSize = null;
        if (Px == null)
            return defaultOrderSize;
        if (instr instanceof Equity) {
            defaultOrderSize = new BigDecimal(100);
        } else if (instr instanceof Future) {
            defaultOrderSize = new BigDecimal(1);
        } else if (instr instanceof Option) {
            defaultOrderSize = new BigDecimal(1);
        } else if (instr instanceof org.marketcetera.trade.Currency) {
            defaultOrderSize = new BigDecimal(1);
        }
        return defaultOrderSize;
    }

    /**
     * Handles the send order command for this view.
     * 
     */
    @ClassVersion("$Id$")
    public static class OrderCommandHandler extends AbstractHandler implements
            IHandler {
        private Side side;

        public void setSide(Side side) {
            this.side = side;
        }

        @Override
        public Object execute(ExecutionEvent event) throws ExecutionException {
            IWorkbenchPart part = HandlerUtil.getActivePartChecked(event);
            ISelection selection = HandlerUtil.getCurrentSelectionChecked(event);
            if(part instanceof MarketDataView && selection instanceof IStructuredSelection) {
                final MarketDataView view = (MarketDataView) part;
                final IStructuredSelection sselection = (IStructuredSelection) selection;
                view.busyRun(new Runnable() {
                    public void run() {
                        for(Object obj : sselection.toArray()) {
                            if(obj instanceof MarketDataViewItem) {
                                MarketDataViewItem mdi = (MarketDataViewItem) obj;
                                Instrument instrument = mdi.getInstrument();
                                OrderSingle newOrder = Factory.getInstance().createOrderSingle();
                                newOrder.setInstrument(instrument);
                                newOrder.setOrderType(OrderType.Limit);
                                newOrder.setSide(side);
                                BigDecimal order_px = side == Side.Buy ? mdi.getTopOfBook().getAskPrice() : mdi.getTopOfBook().getBidPrice();
                                BigDecimal defaultOrderSize = getDefaultOrderSize(instrument,
                                                                                  order_px);
                                if(side == Side.Buy && mdi.getTopOfBook().getAskSize() != null) {
                                    if(defaultOrderSize != null && mdi.getTopOfBook().getAskSize().compareTo(defaultOrderSize) == 1) {
                                        newOrder.setQuantity(defaultOrderSize);
                                    } else {
                                        newOrder.setQuantity(mdi.getTopOfBook().getAskSize());
                                    }
                                    if(order_px != null) {
                                        newOrder.setPrice(mdi.getTopOfBook().getAskPrice());
                                    }
                                } else if(mdi.getTopOfBook().getBidSize() != null) {
                                    if(defaultOrderSize != null && mdi.getTopOfBook().getBidSize().compareTo(defaultOrderSize) == 1) {
                                        newOrder.setQuantity(defaultOrderSize);
                                    } else {
                                        newOrder.setQuantity(mdi.getTopOfBook().getBidSize());
                                    }
                                    if(order_px != null) {
                                        newOrder.setPrice(mdi.getTopOfBook().getBidPrice());
                                    }
                                }
                                newOrder.setTimeInForce(TimeInForce.Day);
                                try {
                                    PhotonPlugin.getDefault().showOrderInTicket(newOrder);
                                } catch (WorkbenchException e) {
                                    SLF4JLoggerProxy.error(this,
                                                           e);
                                    ErrorDialog.openError(null,
                                                          null,
                                                          null,
                                                          new Status(IStatus.ERROR,
                                                                     PhotonPlugin.ID,
                                                                     e.getLocalizedMessage()));
                                }
                            }
                        }
                    }
                });
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