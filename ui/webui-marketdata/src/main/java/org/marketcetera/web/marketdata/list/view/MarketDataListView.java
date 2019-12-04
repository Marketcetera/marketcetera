package org.marketcetera.web.marketdata.list.view;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.marketcetera.event.AskEvent;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.MarketstatEvent;
import org.marketcetera.event.TradeEvent;
import org.marketcetera.marketdata.AssetClass;
import org.marketcetera.marketdata.Content;
import org.marketcetera.marketdata.MarketDataListener;
import org.marketcetera.marketdata.MarketDataRequestBuilder;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.web.marketdata.service.MarketDataClientService;
import org.marketcetera.web.service.trade.TradeClientService;
import org.marketcetera.web.view.AbstractContentView;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.grid.ColumnResizeMode;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
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
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MarketDataListView
        extends AbstractContentView
{
    /**
     * Create a new MarketDataView instance.
     *
     * @param inParentWindow a <code>Window</code> value
     * @param inViewProperties a <code>Properties</code> value
     */
    public MarketDataListView(Window inParentWindow,
                              Properties inViewProperties)
    {
        super(inParentWindow,
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
     * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
     */
    @Override
    public void enter(ViewChangeEvent inEvent)
    {
    }
    /* (non-Javadoc)
     * @see com.vaadin.ui.AbstractComponent#attach()
     */
    @Override
    public void attach()
    {
        super.attach();
        setSizeFull();
        CssLayout symbolEntryLayout = new CssLayout();
        symbolEntryLayout.setId(getClass().getCanonicalName() + ".symbolEntryLayout");
        symbolEntryLayout.setWidth("100%");
        styleService.addStyle(symbolEntryLayout);
        HorizontalLayout marketDataGridLayout = new HorizontalLayout();
        marketDataGridLayout.setId(getClass().getCanonicalName() + ".marketDataGridLayout");
        marketDataGridLayout.setWidth("100%");
        marketDataGridLayout.setMargin(true);
        marketDataGridLayout.setHeight("75%");
        styleService.addStyle(marketDataGridLayout);
        addComponents(symbolEntryLayout,
                      marketDataGridLayout);
        List<MarketDataRow> marketDataRows = Lists.newArrayList();
        BeanItemContainer<MarketDataRow> marketDataBeanItemContainer = new BeanItemContainer<>(MarketDataRow.class,
                                                                                               marketDataRows);
        marketDataGrid = new Grid(marketDataBeanItemContainer);
        marketDataGrid.setColumnOrder(symbolColumn,
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
        marketDataGrid.setHeightMode(HeightMode.CSS);
        marketDataGrid.setSizeFull();
        marketDataGrid.setColumnReorderingAllowed(true);
        marketDataGrid.setColumnResizeMode(ColumnResizeMode.ANIMATED);
        marketDataGrid.setSelectionMode(SelectionMode.SINGLE);
        marketDataGrid.setId(getClass().getCanonicalName() + ".marketDataGrid");
        styleService.addStyle(marketDataGrid);
        marketDataGridLayout.addComponents(marketDataGrid);
        marketDataSymbolText = new TextField();
        marketDataSymbolText.addTextChangeListener(inEvent -> {
            String value = StringUtils.trimToNull(marketDataSymbolText.getValue());
            addMarketDataSymbolButton.setReadOnly(value != null);
        });
        addMarketDataSymbolButton = new Button();
        addMarketDataSymbolButton.setReadOnly(true);
        addMarketDataSymbolButton.setIcon(FontAwesome.PLUS_CIRCLE);
        addMarketDataSymbolButton.setClickShortcut(KeyCode.ENTER);
        addMarketDataSymbolButton.addClickListener(inClickEvent -> {
            String newSymbol = StringUtils.trimToNull(marketDataSymbolText.getValue());
            if(newSymbol != null && !rowsBySymbol.containsKey(newSymbol)) {
                MarketDataRow marketDataRow = new MarketDataRow(newSymbol);
                marketDataBeanItemContainer.addBean(marketDataRow);
                rowsBySymbol.put(newSymbol,
                                 marketDataRow);
            }
            marketDataSymbolText.clear();
            marketDataSymbolText.focus();
        });
        addMarketDataSymbolButton.setId(getClass().getCanonicalName() + ".addMarketDataSymbolButton");
        styleService.addStyle(addMarketDataSymbolButton);
        symbolEntryLayout.addComponents(marketDataSymbolText,
                                        addMarketDataSymbolButton);
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
            marketDataGrid.refreshAllRows();
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
    }
    /**
     * triggers the add symbol action
     */
    private Button addMarketDataSymbolButton;
    /**
     * allows new market data symbols to be entered
     */
    private TextField marketDataSymbolText;
    /**
     * shows market data elements in a grid
     */
    private Grid marketDataGrid;
    /**
     * global name of this view
     */
    private static final String NAME = "Market Data View";
    /**
     * holds requested rows by symbol
     */
    private final Map<String,MarketDataRow> rowsBySymbol = Maps.newHashMap();
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
    private static final long serialVersionUID = -4416759265511242121L;
}
