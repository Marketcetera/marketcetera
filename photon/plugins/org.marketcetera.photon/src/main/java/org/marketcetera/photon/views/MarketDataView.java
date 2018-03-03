package org.marketcetera.photon.views;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;
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
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
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
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.marketcetera.marketdata.FeedStatus;
import org.marketcetera.photon.FIXFieldLocalizer;
import org.marketcetera.photon.Messages;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.commons.ui.workbench.ChooseColumnsMenu.IColumnProvider;
import org.marketcetera.photon.commons.ui.workbench.ColumnState;
import org.marketcetera.photon.marketdata.IFeedStatusChangedListener;
import org.marketcetera.photon.marketdata.IMarketDataManager;
import org.marketcetera.photon.model.marketdata.MDPackage;
import org.marketcetera.photon.ui.TextContributionItem;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.Future;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.trade.OrderType;
import org.marketcetera.trade.Side;
import org.marketcetera.trade.TimeInForce;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;

import quickfix.field.BidPx;
import quickfix.field.BidSize;
import quickfix.field.HighPx;
import quickfix.field.LastPx;
import quickfix.field.LastQty;
import quickfix.field.LowPx;
import quickfix.field.OfferPx;
import quickfix.field.OfferSize;
import quickfix.field.OpenClose;
import quickfix.field.PrevClosePx;
import quickfix.field.Symbol;
import quickfix.field.TradeVolume;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/* $License$ */

/**
 * Market data view.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public final class MarketDataView
        extends ViewPart
        implements IMSymbolListener,IColumnProvider
{
    @Override
    public void init(IViewSite site,
                     IMemento memento)
            throws PartInitException
    {
        super.init(site);
        viewState = memento;
    }
    /**
     * Returns the clipboard for this view.
     * 
     * @return the clipboard for this view
     */
    public Clipboard getClipboard()
    {
        if (clipboard == null) {
            clipboard = new Clipboard(itemTableViewer.getControl().getDisplay());
        }
        return clipboard;
    }

    @Override
    public Table getColumnWidget() {
        return itemTableViewer != null ? itemTableViewer.getTable() : null;
    }

    @Override
    public void createPartControl(Composite parent)
    {
        final IActionBars actionBars = getViewSite().getActionBars();
        IToolBarManager toolbar = actionBars.getToolBarManager();
        symbolEntryText = new TextContributionItem(""); //$NON-NLS-1$
        toolbar.add(symbolEntryText);
        toolbar.add(new AddSymbolAction(symbolEntryText, this));
        PhotonPlugin.getDefault().getMarketDataManager().addActiveFeedStatusChangedListener(new IFeedStatusChangedListener() {
            @Override
            public void feedStatusChanged(final IFeedStatusEvent inEvent)
            {
                Display.getDefault().asyncExec(new Runnable() {
                    @Override
                    public void run()
                    {
                        symbolEntryText.setEnabled(inEvent.getNewStatus() == FeedStatus.AVAILABLE);
                    }
                });
            }
        });
        final Table table = new Table(parent, SWT.MULTI | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.BORDER);
        table.setHeaderVisible(true);
        itemTableViewer = new TableViewer(table);
        GridDataFactory.defaultsFor(table).applyTo(table);
        final MarketDataItemComparator comparator = new MarketDataItemComparator();
        itemTableViewer.setComparator(comparator);
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
                itemTableViewer.refresh();
            }
        };

        // create columns, using FIXFieldLocalizer to preserve backwards compatibility
//        TableViewerColumn symbolColumn = new TableViewerColumn(mViewer,
//                                                               createColumn(table,
//                                                                            FIXFieldLocalizer.getLocalizedFIXFieldName(Symbol.class.getSimpleName()),
//                                                                            SWT.LEFT,
//                                                                            listener));
//        symbolColumn.setEditingSupport(new SymbolEditingSupport());
        createColumn(table,
                     FIXFieldLocalizer.getLocalizedFIXFieldName(Symbol.class.getSimpleName()),
                     SWT.LEFT,
                     listener);
        createColumn(table,
                     FIXFieldLocalizer.getLocalizedFIXFieldName(quickfix.field.LastMkt.class.getSimpleName()),
                     SWT.LEFT,
                     listener);
        createColumn(table,
                     FIXFieldLocalizer.getLocalizedFIXFieldName(LastPx.class.getSimpleName()),
                     SWT.RIGHT,
                     listener);
        createColumn(table,
                     FIXFieldLocalizer.getLocalizedFIXFieldName(LastQty.class.getSimpleName()),
                     SWT.RIGHT,
                     listener);
        createColumn(table,
                     FIXFieldLocalizer.getLocalizedFIXFieldName(BidSize.class.getSimpleName()),
                     SWT.RIGHT,
                     listener);
        createColumn(table,
                     FIXFieldLocalizer.getLocalizedFIXFieldName(BidPx.class.getSimpleName()),
                     SWT.RIGHT,
                     listener);
        createColumn(table,
                     FIXFieldLocalizer.getLocalizedFIXFieldName(OfferPx.class.getSimpleName()),
                     SWT.RIGHT,
                     listener);
        createColumn(table,
                     FIXFieldLocalizer.getLocalizedFIXFieldName(OfferSize.class.getSimpleName()),
                     SWT.RIGHT,
                     listener);
        createColumn(table,
                     FIXFieldLocalizer.getLocalizedFIXFieldName(PrevClosePx.class.getSimpleName()),
                     SWT.RIGHT,
                     listener);
        createColumn(table,
                     FIXFieldLocalizer.getLocalizedFIXFieldName(OpenClose.class.getSimpleName()),
                     SWT.RIGHT,
                     listener);
        createColumn(table,
                     FIXFieldLocalizer.getLocalizedFIXFieldName(HighPx.class.getSimpleName()),
                     SWT.RIGHT,
                     listener);
        createColumn(table,
                     FIXFieldLocalizer.getLocalizedFIXFieldName(LowPx.class.getSimpleName()),
                     SWT.RIGHT,
                     listener);
        createColumn(table,
                     FIXFieldLocalizer.getLocalizedFIXFieldName(TradeVolume.class.getSimpleName()),
                     SWT.RIGHT,
                     listener);
        // restore table state if it exists
        if(viewState != null) {
            ColumnState.restore(table, viewState);
            for(TableColumn column : table.getColumns()) {
                if(column.getWidth() == 0) {
                    column.setResizable(false);
                }
            }
        }
        registerContextMenu();
        getSite().setSelectionProvider(itemTableViewer);
        ObservableListContentProvider content = new ObservableListContentProvider();
        itemTableViewer.setContentProvider(content);
        IObservableSet domain = content.getKnownElements();
        IObservableMap[] maps = new IObservableMap[] {
            BeansObservables.observeMap(domain,
                                        MarketDataViewItem.class,
                                        "symbolPattern"), //$NON-NLS-1$
            createCompositeMap(domain,
                               "latestTick", //$NON-NLS-1$
                               MDPackage.Literals.MD_LATEST_TICK__EXCHANGE),
            createCompositeMap(domain,
                               "latestTick", //$NON-NLS-1$
                               MDPackage.Literals.MD_LATEST_TICK__PRICE),
            createCompositeMap(domain,
                               "latestTick", //$NON-NLS-1$
                               MDPackage.Literals.MD_LATEST_TICK__SIZE),
            createCompositeMap(domain,
                               "topOfBook", //$NON-NLS-1$
                               MDPackage.Literals.MD_TOP_OF_BOOK__BID_SIZE),
            createCompositeMap(domain,
                               "topOfBook", //$NON-NLS-1$
                               MDPackage.Literals.MD_TOP_OF_BOOK__BID_PRICE),
            createCompositeMap(domain,
                               "topOfBook",
                               MDPackage.Literals.MD_TOP_OF_BOOK__ASK_PRICE), //$NON-NLS-1$
            createCompositeMap(domain,
                               "topOfBook",
                               MDPackage.Literals.MD_TOP_OF_BOOK__ASK_SIZE), //$NON-NLS-1$
            createCompositeMap(domain,
                               "marketStat", //$NON-NLS-1$
                               MDPackage.Literals.MD_MARKETSTAT__PREVIOUS_CLOSE_PRICE),
            createCompositeMap(domain,
                               "marketStat", //$NON-NLS-1$
                               MDPackage.Literals.MD_MARKETSTAT__OPEN_PRICE),
            createCompositeMap(domain,
                               "marketStat", //$NON-NLS-1$
                               MDPackage.Literals.MD_MARKETSTAT__HIGH_PRICE),
            createCompositeMap(domain,
                               "marketStat", //$NON-NLS-1$
                               MDPackage.Literals.MD_MARKETSTAT__LOW_PRICE),
            createCompositeMap(domain,
                               "marketStat", //$NON-NLS-1$
                               MDPackage.Literals.MD_MARKETSTAT__VOLUME_TRADED)
        };
        itemTableViewer.setLabelProvider(new ObservableMapLabelProvider(maps));
        itemTableViewer.setUseHashlookup(true);
        viewItems = WritableList.withElementType(MarketDataViewItem.class);
        itemTableViewer.setInput(viewItems);
        List<String> symbolPatternsToAdd = Lists.newArrayList();
        if(viewState == null) {
            // this block is entered if this is the first time the market data view has ever been entered
            ScopedPreferenceStore preferenceStore = PhotonPlugin.getDefault().getPreferenceStore();
            String instrumentList = StringUtils.trimToNull(preferenceStore.getString(preferenceSymbolKey));
            if(instrumentList != null) {
                for(String symbol : instrumentList.split(",")) {
                    symbol = StringUtils.trimToNull(symbol);
                    if(symbol != null) {
                        symbolPatternsToAdd.add(symbol);
                    }
                }
                // schedule the job with the symbol patterns from the installation config
                addSymbolPatternJobToken = addSymbolPatternService.scheduleAtFixedRate(new AddSymbolPatternAgent(symbolPatternsToAdd),
                                                                                       1000,
                                                                                       1000,
                                                                                       TimeUnit.MILLISECONDS);
            }
        } else {
            IMemento[] symbolPatternList = viewState.getChildren(SYMBOL_PATTERN_LIST);
            if(symbolPatternList != null) {
                for(IMemento symbolMemoParent : symbolPatternList) {
                    String symbolPattern = symbolMemoParent.getString(SYMBOL_ATTRIBUTE);
                    symbolPatternsToAdd.add(symbolPattern);
                }
                addSymbolPatternJobToken = addSymbolPatternService.scheduleAtFixedRate(new AddSymbolPatternAgent(symbolPatternsToAdd),
                                                                                       1000,
                                                                                       1000,
                                                                                       TimeUnit.MILLISECONDS);
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
                    if(rect.contains(pt)) {
                        MD_TABLE_COLUMNS enum_col = MD_TABLE_COLUMNS.values()[i];
                        MarketDataViewItem mdi = (MarketDataViewItem)item.getData();
                        if (mdi != null && mdi.getTopOfBook() != null)
                            switch (enum_col) {
                            case BID_PX: // hit the bid
                                newOrder(mdi,
                                         Side.Sell,
                                         mdi.getTopOfBook().getBidPrice(),
                                         mdi.getTopOfBook().getBidSize());
                                break;
                            case BID_SZ: // join the bid
                                newOrder(mdi,
                                         Side.Buy,
                                         mdi.getTopOfBook().getBidPrice(),
                                         null);
                                break;
                            case OFFER_PX: // lift the offer
                                newOrder(mdi,
                                         Side.Buy,
                                         mdi.getTopOfBook().getAskPrice(),
                                         mdi.getTopOfBook().getAskSize());
                                break;
                            case OFFER_SZ: // join the ask
                                newOrder(mdi,
                                         Side.Sell,
                                         mdi.getTopOfBook().getAskPrice(),
                                         null);
                                break;
                           default:
                        	   break;
                            }
                    }
                }
            }
        });
    }
    /**
     * Generate a new order with the given attributes.
     *
     * @param inMarketDataViewItem a <code>MarketDataViewItem</code> value
     * @param inSide a <code>Side</code> value
     * @param inPrice a <code>BigDecimal</code> value
     * @param inQuantity a <code>BigDecimal</code> value
     */
    private void newOrder(final MarketDataViewItem inMarketDataViewItem,
                          final Side inSide,
                          final BigDecimal inPrice,
                          final BigDecimal inQuantity)
    {
        busyRun(new Runnable() {
            public void run() {
                BigDecimal defaultOrderSize = getDefaultOrderSize(inMarketDataViewItem.getInstrument(),
                                                                  inPrice);
                Instrument instrument = inMarketDataViewItem.getInstrument();
                OrderSingle newOrder = Factory.getInstance().createOrderSingle();
                newOrder.setInstrument(instrument);
                newOrder.setOrderType(OrderType.Limit);
                newOrder.setSide(inSide);
                newOrder.setQuantity(defaultOrderSize);
                if (inQuantity != null && inQuantity.compareTo(defaultOrderSize) == -1) {
                    newOrder.setQuantity(inQuantity);
                }
                newOrder.setPrice(inPrice);
                newOrder.setTimeInForce(TimeInForce.Day);
                try {
                    PhotonPlugin.getDefault().showOrderInTicket(newOrder);
                } catch (WorkbenchException e) {
                    SLF4JLoggerProxy.error(this, e);
                    ErrorDialog.openError(null,
                                          null,
                                          null,
                                          new Status(IStatus.ERROR,PhotonPlugin.ID,e.getLocalizedMessage()));
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
        getSite().registerContextMenu(contextMenu, itemTableViewer);
        Control control = itemTableViewer.getControl();
        Menu menu = contextMenu.createContextMenu(control);
        control.setMenu(menu);
    }
    /* (non-Javadoc)
     * @see org.eclipse.ui.part.ViewPart#saveState(org.eclipse.ui.IMemento)
     */
    @Override
    public void saveState(IMemento inMemento)
    {
        ColumnState.save(getColumnWidget(),
                         inMemento);
        if(inMemento != null) {
            for(String symbolPattern : requestedSymbols) {
                IMemento symbolMemento = inMemento.createChild(SYMBOL_PATTERN_LIST);
                symbolMemento.putString(SYMBOL_ATTRIBUTE,
                                        symbolPattern);
            }
        }
    }

    @Override
    public void setFocus() {
        if (symbolEntryText.isEnabled())
            symbolEntryText.setFocus();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.photon.views.IMSymbolListener#onAddSymbol(java.lang.String)
     */
    @Override
    public void onAddSymbol(String inSymbolPattern)
    {
        if(requestedSymbols.contains(inSymbolPattern)) {
            PhotonPlugin.getMainConsoleLogger().warn(Messages.DUPLICATE_SYMBOL.getText(inSymbolPattern));
            return;
        }
        requestedSymbols.add(inSymbolPattern);
        String[] symbolComponents = inSymbolPattern.split("\\.");
        Instrument instrument = PhotonPlugin.getDefault().getSymbolResolver().resolveSymbol(symbolComponents[0]);
        String exchange = symbolComponents.length > 1 ? symbolComponents[1]:null;
        addSymbol(instrument,
                  exchange,
                  inSymbolPattern);
    }
    /**
     * Adds a new row in the view for the given instrument (if one does not already exist).
     * 
     * @param inInstrument an <code>Instrument</code> value
     * @param inExchange a <code>String</code> value or <code>null</code>
     * @param inSymbolPattern a <code>String</code> value
     */
    public void addSymbol(final Instrument inInstrument,
                          final String inExchange,
                          final String inSymbolPattern)
    {
        busyRun(new Runnable() {
            @Override
            public void run()
            {
                MarketDataViewItem item = new MarketDataViewItem(marketDataManager.getMarketData(),
                                                                 inInstrument,
                                                                 inExchange,
                                                                 inSymbolPattern);
                viewItems.add(item);
            }
        });
    }
    /* (non-Javadoc)
     * @see org.eclipse.ui.part.WorkbenchPart#dispose()
     */
    @Override
    public void dispose()
    {
        @SuppressWarnings("unchecked")
        Iterator<Object> itemIterator = viewItems.iterator();
        while(itemIterator.hasNext()) {
            MarketDataViewItem item = (MarketDataViewItem)itemIterator.next();
            item.dispose();
        }
        requestedSymbols.clear();
        viewItems = null;
        if(clipboard != null) {
            clipboard.dispose();
        }
        super.dispose();
    }
    /**
     * Get the default order size for the given instrument at the given price.
     *
     * @param inInstrument an <code>Instrument</code> value
     * @param inPrice a <code>BigDecimal</code> value
     * @return a <code>BigDecimal</code> value
     */
    static BigDecimal getDefaultOrderSize(Instrument inInstrument,
                                          BigDecimal inPrice)
    {
        BigDecimal defaultOrderSize = null;
        if (inPrice == null)
            return defaultOrderSize;
        if (inInstrument instanceof Equity) {
            defaultOrderSize = new BigDecimal(100);
        } else if (inInstrument instanceof Future) {
            defaultOrderSize = new BigDecimal(1);
        } else if (inInstrument instanceof Option) {
            defaultOrderSize = new BigDecimal(1);
        } else if (inInstrument instanceof org.marketcetera.trade.Currency) {
            defaultOrderSize = new BigDecimal(1);
        }
        return defaultOrderSize;
    }
    /**
     * Runs the given command while displaying an hourglass.
     *
     * @param inRunnable a <code>Runnable</code> value
     */
    private void busyRun(Runnable inRunnable)
    {
        BusyIndicator.showWhile(getViewSite().getShell().getDisplay(),
                                inRunnable);
    }
    /**
     * Remove the given view item.
     *
     * @param inViewItem a <code>MarketDataViewItem</code> value
     */
    private void remove(MarketDataViewItem inViewItem)
    {
        requestedSymbols.remove(inViewItem.getSymbolPattern());
        viewItems.remove(inViewItem);
        inViewItem.dispose();
    }
    /**
     * Handles column sorting.
     * 
     * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
     * @version $Id$
     * @since 1.0.0
     */
    private static class MarketDataItemComparator
            extends ViewerComparator
    {
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
            case EXCHANGE:
                String exchange1 = item1.getLatestTick().getExchange();
                String exchange2 = item2.getLatestTick().getExchange();
                compare = new CompareToBuilder().append(exchange1,exchange2).toComparison();
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
     * Handles the delete command for this view.
     * 
     * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
     * @version $Id$
     * @since 1.0.0
     */
    @ClassVersion("$Id$")
    public static final class DeleteCommandHandler
            extends AbstractHandler
            implements IHandler
    {
        @Override
        public Object execute(ExecutionEvent event)
                throws ExecutionException
        {
            IWorkbenchPart part = HandlerUtil.getActivePartChecked(event);
            ISelection selection = HandlerUtil.getCurrentSelectionChecked(event);
            if (part instanceof MarketDataView && selection instanceof IStructuredSelection) {
                final MarketDataView view = (MarketDataView) part;
                final IStructuredSelection sselection = (IStructuredSelection) selection;
                // this can take some time
                view.busyRun(new Runnable() {
                    public void run() {
                        for (Object obj : sselection.toArray()) {
                            if(obj instanceof MarketDataViewItem) {
                                view.remove((MarketDataViewItem)obj);
                            }
                        }
                    }
                });
            }
            return null;
        }
    }
    /**
     * Handles the buy command for the view.
     *
     * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
     * @version $Id$
     * @since 1.0.0
     */
    public static final class BuyCommandHandler
            extends OrderCommandHandler
            implements IHandler
    {
        @Override
        public Object execute(ExecutionEvent event)
                throws ExecutionException
        {
            super.setSide(Side.Buy);
            return super.execute(event);
        }
    }
    /**
     * Handles the sell command for the view.
     *
     * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
     * @version $Id$
     * @since 1.0.0
     */
    public static final class SellCommandHandler
            extends OrderCommandHandler
            implements IHandler
    {
        @Override
        public Object execute(ExecutionEvent event)
                throws ExecutionException
        {
            super.setSide(Side.Sell);
            return super.execute(event);
        }
    }
    /**
     * Handles the send order command for this view.
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
    /**
     * Adds instruments on start from the last market data session.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 2.4.0
     */
    private class AddSymbolPatternAgent
            implements Runnable
    {
        /**
         * Create a new AddSymbolPatternAgent instance.
         *
         * @param inSymbolPatternsToAdd a <code>List&lt;String&gt;</code> value
         */
        public AddSymbolPatternAgent(List<String> inSymbolPatternsToAdd)
        {
            symbolPatternsToAdd = inSymbolPatternsToAdd;
        }
        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run()
        {
            if(!marketDataManager.isRunning()) {
                return;
            }
            try {
                if(symbolPatternsToAdd.isEmpty()) {
                    return;
                }
                Display.getDefault().asyncExec(new Runnable() {
                    @Override
                    public void run()
                    {
                        Iterator<String> symbolPatternToAddIterator = symbolPatternsToAdd.iterator();
                        while(symbolPatternToAddIterator.hasNext()) {
                            String symbolPatternToAdd = symbolPatternToAddIterator.next();
                            try {
                                String[] symbolComponents = symbolPatternToAdd.split("\\.");
                                String symbol = symbolComponents[0];
                                String exchange = null;
                                if(symbolComponents.length > 1) {
                                    exchange = symbolComponents[1];
                                }
                                Instrument instrument = PhotonPlugin.getDefault().getSymbolResolver().resolveSymbol(symbol);
                                addSymbol(instrument,
                                          exchange,
                                          symbolPatternToAdd);
                            } catch (Exception e) {
                                SLF4JLoggerProxy.error(org.marketcetera.core.Messages.USER_MSG_CATEGORY,
                                                       "A problem occurred restoring market data for {}",
                                                       symbolPatternToAdd);
                                SLF4JLoggerProxy.error(MarketDataView.this,
                                                       e);
                            }
                            symbolPatternToAddIterator.remove();
                        }
                    }
                });
            } finally {
                addSymbolPatternJobToken.cancel(true);
                addSymbolPatternJobToken = null;
            }
        }
        /**
         * symbol patterns to add
         */
        private final List<String> symbolPatternsToAdd;
    }
    /**
     * Identifies the columns in the view.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private static enum MD_TABLE_COLUMNS
    {
        MD_SYMBOL,
        EXCHANGE,
        LAST_PX,
        LAST_SZ,
        BID_SZ,
        BID_PX,
        OFFER_PX,
        OFFER_SZ,
        PREV_CLOSE,
        OPEN_PX,
        HIGH_PX,
        LOW_PX,
        TRD_VOLUME
    }
    /**
     * service used to asynchronously add instruments from the last session after start
     */
    private ScheduledExecutorService addSymbolPatternService = Executors.newScheduledThreadPool(1);
    /**
     * The view ID.
     */
    public static final String ID = "org.marketcetera.photon.views.MarketDataView"; //$NON-NLS-1$
    /**
     * token which identifies the job which adds symbols from the last session
     */
    private java.util.concurrent.Future<?> addSymbolPatternJobToken;
    /**
     * text control used to accept new market data requests
     */
    private TextContributionItem symbolEntryText;
    /**
     * provides access to market data services
     */
    private final IMarketDataManager marketDataManager = PhotonPlugin.getDefault().getMarketDataManager();
    /**
     * main table view artifact
     */
    private TableViewer itemTableViewer;
    /**
     * holds the items (table rows) in the view
     */
    private WritableList viewItems;
    /**
     * used to preserve the state of the view between sessions
     */
    private IMemento viewState;
    /**
     * clipboard object, used to cut and paste
     */
    private Clipboard clipboard;
    /**
     * key used to store market data requests from the last session (used for memento)
     */
    private static final String SYMBOL_PATTERN_LIST = "Symbol_Pattern_List"; //$NON-NLS-1$
    /**
     * symbol pattern attribute (used for memento)
     */
    private static final String SYMBOL_ATTRIBUTE = "symbol"; //$NON-NLS-1$
    /**
     * holds the symbol patterns already requested
     */
    private final Set<String> requestedSymbols = Sets.newHashSet();
    /**
     * indicates the initial symbol list to use
     */
    private static final String preferenceSymbolKey = "org.marketcetera.photon.preferences.MarketDataSymbols";
}
