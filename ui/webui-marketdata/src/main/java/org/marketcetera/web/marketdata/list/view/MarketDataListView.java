package org.marketcetera.web.marketdata.list.view;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.SortedMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;
import org.marketcetera.core.Util;
import org.marketcetera.event.AskEvent;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.HasInstrument;
import org.marketcetera.event.MarketstatEvent;
import org.marketcetera.event.TradeEvent;
import org.marketcetera.marketdata.AssetClass;
import org.marketcetera.marketdata.Content;
import org.marketcetera.marketdata.MarketDataListener;
import org.marketcetera.marketdata.MarketDataPermissions;
import org.marketcetera.marketdata.MarketDataRequestBuilder;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.trade.OrderSingleSuggestion;
import org.marketcetera.trade.OrderType;
import org.marketcetera.trade.Side;
import org.marketcetera.trade.TradePermissions;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.web.SessionUser;
import org.marketcetera.web.events.NewWindowEvent;
import org.marketcetera.web.marketdata.event.MarketDataSuggestionEvent;
import org.marketcetera.web.marketdata.list.view.MarketDataListView.MarketDataRow;
import org.marketcetera.web.marketdata.service.MarketDataClientService;
import org.marketcetera.web.service.trade.TradeClientService;
import org.marketcetera.web.view.AbstractGridView;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Scope;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItem;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

/* $License$ */

/**
 * Provides a list view for market data.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@SpringComponent
@EnableAutoConfiguration
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MarketDataListView
        extends AbstractGridView<MarketDataRow,MarketDataListDataContainer>
{
    /**
     * Create a new MarketDataView instance.
     *
     * @param inParentWindow a <code>Window</code> value
     * @param inNewWindowEvent a <code>NewWindowEvent</code> value
     * @param inViewProperties a <code>Properties</code> value
     */
    public MarketDataListView(Window inParentWindow,
                              NewWindowEvent inEvent,
                              Properties inViewProperties)
    {
        super(inParentWindow,
              inEvent,
              inViewProperties);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.view.ContentView#getViewName()
     */
    @Override
    public String getViewName()
    {
        return NAME;
    }
    /* (non-Javadoc)
     * @see com.vaadin.ui.AbstractComponent#attach()
     */
    @Override
    public void attach()
    {
        super.attach();
        getCreateNewButton().setCaption("");
        marketDataSymbolText = new TextField();
        marketDataSymbolText.addTextChangeListener(inEvent -> {
            String value = StringUtils.trimToNull(marketDataSymbolText.getValue());
            getCreateNewButton().setReadOnly(value != null);
        });
        marketDataSymbolText.setId(getClass().getCanonicalName() + ".marketDataSymbolText");
        styleService.addStyle(marketDataSymbolText);
        getAboveTheGridLayout().removeAllComponents();
        getAboveTheGridLayout().addComponents(getActionSelect(),
                                              marketDataSymbolText,
                                              getCreateNewButton());
        getCreateNewButton().setClickShortcut(KeyCode.ENTER);
        getActionSelect().setNullSelectionAllowed(false);
        getActionSelect().setReadOnly(true);
        getGrid().addSelectionListener(inEvent -> {
            MarketDataRow selectedObject = getSelectedItem();
            getActionSelect().removeAllItems();
            if(selectedObject == null || selectedObject.getInstrument() == null) {
                getActionSelect().setReadOnly(true);
            } else {
                getActionSelect().setReadOnly(false);
                // adjust the available actions based on the status of the selected row
                if(authzHelperService.hasPermission(MarketDataPermissions.RequestMarketDataAction)) { 
                    getActionSelect().addItems(ACTION_DETAIL);
                }
                if(authzHelperService.hasPermission(TradePermissions.SendOrderAction)) {
                    getActionSelect().addItem(ACTION_BUY);
                    getActionSelect().addItem(ACTION_SELL);
                }
                getActionSelect().addItem(ACTION_REMOVE);
            }
        });
        restoreSymbols();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.view.AbstractGridView#onCreateNew(com.vaadin.ui.Button.ClickEvent)
     */
    @Override
    protected void onCreateNew(ClickEvent inEvent)
    {
        String newSymbol = StringUtils.trimToNull(marketDataSymbolText.getValue());
        if(newSymbol == null) {
            return;
        }
        doAddSymbolToGrid(newSymbol);
        marketDataSymbolText.clear();
        marketDataSymbolText.focus();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.view.AbstractGridView#setGridColumns()
     */
    @Override
    protected void setGridColumns()
    {
        getGrid().setColumns(symbolColumn,
                             tradePriceColumn,
                             tradeQuantityColumn,
                             tradeExchangeColumn,
                             bidExchangeColumn,
                             bidQuantityColumn,
                             bidPriceColumn,
                             offerPriceColumn,
                             offerQuantityColumn,
                             offerExchangeColumn,
                             openColumn,
                             highColumn,
                             lowColumn,
                             closeColumn,
                             volumeColumn);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.view.AbstractGridView#getDataContainerType()
     */
    @Override
    protected Class<MarketDataListDataContainer> getDataContainerType()
    {
        return MarketDataListDataContainer.class;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.view.AbstractGridView#createDataContainer()
     */
    @Override
    protected MarketDataListDataContainer createDataContainer()
    {
        return new MarketDataListDataContainer();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.view.AbstractGridView#getViewSubjectName()
     */
    @Override
    protected String getViewSubjectName()
    {
        return NAME;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.AbstractGridView#onActionSelect(com.vaadin.data.Property.ValueChangeEvent)
     */
    @Override
    protected void onActionSelect(ValueChangeEvent inEvent)
    {
        MarketDataRow selectedItem = getSelectedItem();
        if(selectedItem == null || inEvent.getProperty().getValue() == null || selectedItem.getInstrument() == null) {
            return;
        }
        String action = String.valueOf(inEvent.getProperty().getValue());
        SLF4JLoggerProxy.info(this,
                              "{}: {} {} '{}'",
                              SessionUser.getCurrentUser().getUsername(),
                              getViewName(),
                              action,
                              selectedItem);
        switch(action) {
            case ACTION_BUY:
            case ACTION_SELL:
                OrderSingle orderSingle = Factory.getInstance().createOrderSingle();
                orderSingle.setInstrument(selectedItem.getInstrument());
                orderSingle.setOrderType(OrderType.Limit);
                orderSingle.setPrice(action.equals(ACTION_BUY)?selectedItem.getOfferPx():selectedItem.getBidPx());
                orderSingle.setSide(action.equals(ACTION_BUY)?Side.Buy:Side.Sell);
                OrderSingleSuggestion suggestion = Factory.getInstance().createOrderSingleSuggestion();
                suggestion.setIdentifier("Market Data List View Action");
                suggestion.setScore(BigDecimal.ONE);
                suggestion.setOrder(orderSingle);
                webMessageService.post(new MarketDataSuggestionEvent((action.equals(ACTION_BUY)?"Buy ":"Sell ") + selectedItem.getSymbol(),
                                                                     suggestion));
                break;
            case ACTION_REMOVE:
                removeRow(selectedItem);
                break;
            case ACTION_DETAIL:
                break;
            default:
                throw new UnsupportedOperationException("Unsupported action: " + action);
        }
    }
    /**
     * Add the given symbol to the display grid.
     *
     * <p>This method will add the symbol only if it is not already present.
     *
     * @param inSymbol a <code>String</code> value
     */
    private void doAddSymbolToGrid(String inSymbol)
    {
        if(!allowLowerCaseSymbols) {
            inSymbol = inSymbol.toUpperCase();
        }
        if(!getSymbols().contains(inSymbol)) {
            MarketDataRow marketDataRow = new MarketDataRow(inSymbol);
            getDataContainer().addBean(marketDataRow);
            addSymbol(inSymbol);
        }
    }
    /**
     * Restore all saved symbols to the display grid.
     */
    private void restoreSymbols()
    {
        synchronized(sortedGridSymbols) {
            String rawData = getViewProperties().getProperty(existingSymbolsKey);
            if(rawData != null) {
                Properties symbolProperties = Util.propertiesFromString(rawData);
                final SortedMap<Integer,String> tempSortedSymbols = Maps.newTreeMap();
                symbolProperties.forEach((index,symbol) -> {
                    tempSortedSymbols.put(Integer.parseInt(String.valueOf(index)),
                                          String.valueOf(symbol));
                });
                for(String symbol : tempSortedSymbols.values()) {
                    doAddSymbolToGrid(symbol);
                }
            }
        }
    }
    /**
     * Get the set of current symbols.
     *
     * @return a <code>Set&lt;String&gt;</code> value
     */
    private Set<String> getSymbols()
    {
        synchronized(sortedGridSymbols) {
            return Sets.newHashSet(sortedGridSymbols.values());
        }
    }
    /**
     * Add the given symbol to the display grid.
     *
     * @param inSymbol a <code>String</code> value
     */
    private void addSymbol(String inSymbol)
    {
        // sortedGridSymbols holds the symbols that currently exist in the grid sorted by the order in which they were added
        //  note that there may be gaps if something was added then removed - that's ok, doesn't matter, we just want to be
        //  able to retain the original order they were added in so we can restore them to the same order if the grid isn't otherwise
        //  sorted
        synchronized(sortedGridSymbols) {
            int index = sortedGridIndex.incrementAndGet();
            sortedGridSymbols.put(index,
                                  inSymbol);
            String rawData = getViewProperties().getProperty(existingSymbolsKey);
            Properties symbolProperties;
            if(rawData == null) {
                symbolProperties = new Properties();
            } else {
                symbolProperties = Util.propertiesFromString(rawData);
            }
            symbolProperties.setProperty(String.valueOf(index),
                                         inSymbol);
            getViewProperties().setProperty(existingSymbolsKey,
                                            Util.propertiesToString(symbolProperties));
        }
    }
    /**
     * Remove the given row from the display grid.
     *
     * @param inItem a <code>MarketDataRow</code> value
     */
    private void removeRow(MarketDataRow inItem)
    {
        String symbolToRemove = inItem.getSymbol();
        synchronized(sortedGridSymbols) {
            // remove from persisted symbols
            String rawData = getViewProperties().getProperty(existingSymbolsKey);
            if(rawData != null) {
                Properties symbolProperties = Util.propertiesFromString(rawData);
                symbolProperties.remove(symbolToRemove);
                getViewProperties().setProperty(existingSymbolsKey,
                                                Util.propertiesToString(symbolProperties));
            }
            // find this symbol in the grid and remove it from the grid-backing set
            Iterator<Integer> sortedGridSymbolsIterator = sortedGridSymbols.keySet().iterator();
            while(sortedGridSymbolsIterator.hasNext()) {
                int index = sortedGridSymbolsIterator.next();
                String symbol = sortedGridSymbols.get(index);
                if(symbol.equals(symbol)) {
                    sortedGridSymbolsIterator.remove();
                    break;
                }
            }
            // remove the symbol from the grid
            getDataContainer().removeItem(inItem);
        }
    }
    /**
     * Represents a single row in the market data list.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    public class MarketDataRow
            implements Comparable<MarketDataRow>,MarketDataListener
    {
        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode()
        {
            return Objects.hash(symbol);
        }
        /* (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj)
        {
            if(this == obj) {
                return true;
            }
            if(obj == null) {
                return false;
            }
            if(!(obj instanceof MarketDataRow)) {
                return false;
            }
            MarketDataRow other = (MarketDataRow)obj;
            return Objects.equals(symbol,
                                  other.symbol);
        }
        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            StringBuilder builder = new StringBuilder();
            builder.append("MarketDataRow [symbol=").append(symbol).append(", tradePrice=").append(tradePrice)
                    .append(", tradeQuantity=").append(tradeQuantity).append(", tradeExchange=").append(tradeExchange)
                    .append(", bidExchange=").append(bidExchange).append(", bidQuantity=").append(bidQuantity)
                    .append(", bidPrice=").append(bidPrice).append(", offerPrice=").append(offerPrice)
                    .append(", offerQuantity=").append(offerQuantity).append(", offerExchange=").append(offerExchange)
                    .append(", volume=").append(volume).append(", open=").append(open).append(", high=").append(high)
                    .append(", low=").append(low).append(", close=").append(close).append(", requestId=")
                    .append(requestId).append("]");
            return builder.toString();
        }
        /**
         * Get the symbol value.
         *
         * @return a <code>String</code> value
         */
        public String getSymbol()
        {
            return symbol;
        }
        /**
         * Get the tradePrice value.
         *
         * @return a <code>BigDecimal</code> value
         */
        public BigDecimal getTradePx()
        {
            return tradePrice;
        }
        /**
         * Get the tradeQuantity value.
         *
         * @return a <code>BigDecimal</code> value
         */
        public BigDecimal getTradeQty()
        {
            return tradeQuantity;
        }
        /**
         * Get the tradeExchange value.
         *
         * @return a <code>String</code> value
         */
        public String getTradeExch()
        {
            return tradeExchange;
        }
        /**
         * Get the bidExchange value.
         *
         * @return a <code>String</code> value
         */
        public String getBidExch()
        {
            return bidExchange;
        }
        /**
         * Get the offerExchange value.
         *
         * @return a <code>String</code> value
         */
        public String getOfferExch()
        {
            return offerExchange;
        }
        /**
         * Get the bidQuantity value.
         *
         * @return a <code>BigDecimal</code> value
         */
        public BigDecimal getBidQty()
        {
            return bidQuantity;
        }
        /**
         * Get the bidPrice value.
         *
         * @return a <code>BigDecimal</code> value
         */
        public BigDecimal getBidPx()
        {
            return bidPrice;
        }
        /**
         * Get the offerPrice value.
         *
         * @return a <code>BigDecimal</code> value
         */
        public BigDecimal getOfferPx()
        {
            return offerPrice;
        }
        /**
         * Get the offerQuantity value.
         *
         * @return a <code>BigDecimal</code> value
         */
        public BigDecimal getOfferQty()
        {
            return offerQuantity;
        }
        /**
         * Get the volume value.
         *
         * @return a <code>BigDecimal</code> value
         */
        public BigDecimal getVolume()
        {
            return volume;
        }
        /**
         * Get the open value.
         *
         * @return a <code>BigDecimal</code> value
         */
        public BigDecimal getOpen()
        {
            return open;
        }
        /**
         * Get the high value.
         *
         * @return a <code>BigDecimal</code> value
         */
        public BigDecimal getHigh()
        {
            return high;
        }
        /**
         * Get the low value.
         *
         * @return a <code>BigDecimal</code> value
         */
        public BigDecimal getLow()
        {
            return low;
        }
        /**
         * Get the close value.
         *
         * @return a <code>BigDecimal</code> value
         */
        public BigDecimal getClose()
        {
            return close;
        }
        /* (non-Javadoc)
         * @see org.marketcetera.marketdata.MarketDataListener#receiveMarketData(org.marketcetera.event.Event)
         */
        @Override
        public void receiveMarketData(org.marketcetera.event.Event inEvent)
        {
            SLF4JLoggerProxy.trace(MarketDataListView.this,
                                   "Received {}",
                                   inEvent);
            if(inEvent instanceof HasInstrument) {
                instrument = ((HasInstrument)inEvent).getInstrument();
            }
            if(inEvent instanceof BidEvent) {
                BidEvent bidEvent = (BidEvent)inEvent;
                bidPrice = bidEvent.getPrice();
                bidQuantity = bidEvent.getSize();
                bidExchange = bidEvent.getExchange();
            } else if(inEvent instanceof AskEvent) {
                AskEvent askEvent = (AskEvent)inEvent;
                offerPrice = askEvent.getPrice();
                offerQuantity = askEvent.getSize();
                offerExchange = askEvent.getExchange();
            } else if(inEvent instanceof TradeEvent) {
                TradeEvent tradeEvent = (TradeEvent)inEvent;
                tradePrice = tradeEvent.getPrice();
                tradeQuantity = tradeEvent.getSize();
                tradeExchange = tradeEvent.getExchange();
            } else if(inEvent instanceof MarketstatEvent) {
                MarketstatEvent marketstatEvent = (MarketstatEvent)inEvent;
                open = marketstatEvent.getOpen();
                high = marketstatEvent.getHigh();
                low = marketstatEvent.getLow();
                close = marketstatEvent.getClose();
                volume = marketstatEvent.getVolume();
            } else {
                return;
            }
            BeanItem<MarketDataRow> beanRow = getDataContainer().getItem(this);
            beanRow.setBean(this);
        }
        /* (non-Javadoc)
         * @see org.marketcetera.marketdata.MarketDataListener#onError(java.lang.Throwable)
         */
        @Override
        public void onError(Throwable inThrowable)
        {
            SLF4JLoggerProxy.warn(MarketDataListView.this,
                                  inThrowable);
        }
        /* (non-Javadoc)
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
        @Override
        public int compareTo(MarketDataRow inO)
        {
            return inO.symbol.compareTo(symbol);
        }
        /**
         * Get the instrument value.
         *
         * @return an <code>Instrument</code> value
         */
        private Instrument getInstrument()
        {
            return instrument;
        }
        /**
         * Create a new MarketDataRow instance.
         *
         * @param inSymbol a <code>String</code> value
         */
        private MarketDataRow(String inSymbol)
        {
            symbol = inSymbol;
            AssetClass assetClass = AssetClass.EQUITY;
            MarketDataClientService marketDataClientService = serviceManager.getService(MarketDataClientService.class);
            Instrument resolvedInstrument = serviceManager.getService(TradeClientService.class).resolveSymbol(symbol);
            if(resolvedInstrument != null) {
                assetClass = AssetClass.getFor(resolvedInstrument.getSecurityType());
            }
            MarketDataRequestBuilder requestBuilder = MarketDataRequestBuilder.newRequest();
            requestBuilder.withAssetClass(assetClass).withSymbols(symbol).withContent(Content.LATEST_TICK,
                                                                                      Content.MARKET_STAT,
                                                                                      Content.TOP_OF_BOOK);
            requestId = marketDataClientService.request(requestBuilder.create(),
                                                        this);
        }
        /**
         * symbol value
         */
        private final String symbol;
        /**
         * trade price value
         */
        private BigDecimal tradePrice;
        /**
         * trade quantity value
         */
        private BigDecimal tradeQuantity;
        /**
         * trade exchange value
         */
        private String tradeExchange;
        /**
         * bid exchange value
         */
        private String bidExchange;
        /**
         * bid quantity value
         */
        private BigDecimal bidQuantity;
        /**
         * bid price value
         */
        private BigDecimal bidPrice;
        /**
         * offer price value
         */
        private BigDecimal offerPrice;
        /**
         * offer quantity value
         */
        private BigDecimal offerQuantity;
        /**
         * offer exchange value
         */
        private String offerExchange;
        /**
         * volume value
         */
        private BigDecimal volume;
        /**
         * open value
         */
        private BigDecimal open;
        /**
         * high value
         */
        private BigDecimal high;
        /**
         * low value
         */
        private BigDecimal low;
        /**
         * close value
         */
        private BigDecimal close;
        /**
         * market data request id value
         */
        private final String requestId;
        /**
         * instrument value
         */
        private Instrument instrument;
    }
    /**
     * indicate whether to allow lower case symbols or not
     */
    @Value("${metc.marketdata.view.allowLowerCaseSymbols:false}")
    private boolean allowLowerCaseSymbols;
    /**
     * allows new market data symbols to be entered
     */
    private TextField marketDataSymbolText;
    /**
     * global name of this view
     */
    private static final String NAME = "Market Data View";
    /**
     * symbol column value
     */
    private static final String symbolColumn = "symbol";
    /**
     * trade price column value
     */
    private static final String tradePriceColumn = "tradePx";
    /**
     * trade quantity column value
     */
    private static final String tradeQuantityColumn = "tradeQty";
    /**
     * trade exchange column value
     */
    private static final String tradeExchangeColumn = "tradeExch";
    /**
     * bid exchange column value
     */
    private static final String bidExchangeColumn = "bidExch";
    /**
     * bid quantity column value
     */
    private static final String bidQuantityColumn = "bidQty";
    /**
     * bid price column value
     */
    private static final String bidPriceColumn = "bidPx";
    /**
     * offer price column value
     */
    private static final String offerPriceColumn = "offerPx";
    /**
     * offer quantity column value
     */
    private static final String offerQuantityColumn = "offerQty";
    /**
     * offer exchange column value
     */
    private static final String offerExchangeColumn = "offerExch";
    /**
     * open column value
     */
    private static final String openColumn = "open";
    /**
     * high column value
     */
    private static final String highColumn = "high";
    /**
     * low column value
     */
    private static final String lowColumn = "low";
    /**
     * close column value
     */
    private static final String closeColumn = "close";
    /**
     * volume column value
     */
    private static final String volumeColumn = "volume";
    /**
     * buy action value
     */
    private static final String ACTION_BUY = "Buy";
    /**
     * sell action value
     */
    private static final String ACTION_SELL = "Sell";
    /**
     * remove action value
     */
    private static final String ACTION_REMOVE = "Remove";
    /**
     * show details action value
     */
    private static final String ACTION_DETAIL = "Show Details";
    /**
     * persisted properties existing symbols key
     */
    private static final String existingSymbolsKey = MarketDataListView.class.getSimpleName() + ".symbols";
    /**
     * holds the current sorted grid symbols keyed by counter - note that the counter may have gaps
     */
    private final SortedMap<Integer,String> sortedGridSymbols = Maps.newTreeMap();
    /**
     * value of the next sorted grid symbol index
     */
    private AtomicInteger sortedGridIndex = new AtomicInteger(0);
    private static final long serialVersionUID = -4416759265511242121L;
}
