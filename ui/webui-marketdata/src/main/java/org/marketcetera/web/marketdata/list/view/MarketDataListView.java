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
import org.marketcetera.web.converters.DecimalConverter;
import org.marketcetera.web.marketdata.service.MarketDataClientService;
import org.marketcetera.web.service.trade.TradeClientService;
import org.marketcetera.web.view.AbstractContentView;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.grid.ColumnResizeMode;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

/* $License$ */

/**
 * Provides a view for market data.
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
        final String tradePriceColumn = "Trade Px";
        final String tradeQuantityColumn = "Trade Qty";
        final String bidQuantityColumn = "Bid Qty";
        final String bidPriceColumn = "Bid Px";
        final String offerPriceColumn = "Offer Px";
        final String offerQuantityColumn = "Offer Qty";
        List<MarketDataRow> marketDataRows = Lists.newArrayList();
        BeanItemContainer<MarketDataRow> marketDataBeanItemContainer = new BeanItemContainer<>(MarketDataRow.class,
                                                                                               marketDataRows);
        marketDataGrid = new Grid(marketDataBeanItemContainer);
//        marketDataContainer.addContainerProperty(tradePriceColumn,
//                                                 BigDecimal.class,
//                                                 null);
//        marketDataContainer.addContainerProperty(tradeQuantityColumn,
//                                                 BigDecimal.class,
//                                                 null);
//        marketDataContainer.addContainerProperty(bidQuantityColumn,
//                                                 BigDecimal.class,
//                                                 null);
//        marketDataContainer.addContainerProperty(bidPriceColumn,
//                                                 BigDecimal.class,
//                                                 null);
//        marketDataContainer.addContainerProperty(offerPriceColumn,
//                                                 BigDecimal.class,
//                                                 null);
//        marketDataContainer.addContainerProperty(offerQuantityColumn,
//                                                 BigDecimal.class,
//                                                 null);
        marketDataGrid.setContainerDataSource(marketDataBeanItemContainer);
        Column symbolColumn = marketDataGrid.addColumn("Symbol",
                                                       String.class);
        marketDataGrid.addColumn(tradePriceColumn,
                                 BigDecimal.class).setConverter(DecimalConverter.instanceZeroAsNull);
        marketDataGrid.addColumn(tradeQuantityColumn,
                                 BigDecimal.class);
        marketDataGrid.addColumn(bidQuantityColumn,
                                 BigDecimal.class);
        marketDataGrid.addColumn(bidPriceColumn,
                                 BigDecimal.class).setConverter(DecimalConverter.instanceZeroAsNull);
        marketDataGrid.addColumn(offerPriceColumn,
                                 BigDecimal.class).setConverter(DecimalConverter.instanceZeroAsNull);
        marketDataGrid.addColumn(offerQuantityColumn,
                                 BigDecimal.class);
//                                  "Prev Close Px",
//                                  "Open/Close Px",
//                                  "High Px",
//                                  "Low Px",
//                                  "Trade Vol");
        marketDataGrid.setHeightMode(HeightMode.CSS);
        marketDataGrid.setSizeFull();
        marketDataGrid.setColumnReorderingAllowed(true);
        marketDataGrid.setColumnResizeMode(ColumnResizeMode.ANIMATED);
        marketDataGrid.setSelectionMode(SelectionMode.SINGLE);
//        marketDataGrid.setSortOrder(Lists.newArrayList(new SortOrder(symbolColumn,
//                                                                     SortDirection.ASCENDING)));
        marketDataGrid.setId(getClass().getCanonicalName() + ".marketDataGrid");
        styleService.addStyle(marketDataGrid);
        marketDataGridLayout.addComponents(marketDataGrid);
        marketDataSymbolText = new TextField();
        marketDataSymbolText.addValueChangeListener(inEvent -> {
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
//                Object rowId = marketDataGrid.addRow(newSymbol,
//                                                     null,
//                                                     null,
//                                                     null,
//                                                     null,
//                                                     null,
//                                                     null);
                MarketDataRow marketDataRow = new MarketDataRow(newSymbol);
                marketDataRow.setItem(marketDataContainer.addItem(marketDataRow));
                rowsBySymbol.put(newSymbol,
                                 marketDataRow);
                marketDataSymbolText.clear();
                marketDataSymbolText.focus();
            }
        });
        addMarketDataSymbolButton.setId(getClass().getCanonicalName() + ".addMarketDataSymbolButton");
        styleService.addStyle(addMarketDataSymbolButton);
        symbolEntryLayout.addComponents(marketDataSymbolText,
                                        addMarketDataSymbolButton);
    }
    private IndexedContainer marketDataContainer;
    private final Map<String,MarketDataRow> rowsBySymbol = Maps.newHashMap();
    private class MarketDataRow
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
        /*
        final String tradePriceColumn = "Trade Px";
        final String tradeQuantityColumn = "Trade Qty";
        final String bidQuantityColumn = "Bid Qty";
        final String bidPriceColumn = "Bid Px";
        final String offerPriceColumn = "Offer Px";
        final String offerQuantityColumn = "Offer Qty";
         */
        public BigDecimal getTradePx()
        {
            return tradePrice;
        }
        private BigDecimal tradePrice = BigDecimal.ZERO;
        private BigDecimal tradeQuantity = BigDecimal.ZERO;
        private BigDecimal bidQuantity = BigDecimal.ZERO;
        private BigDecimal bidPrice = BigDecimal.ZERO;
        private BigDecimal offerPrice = BigDecimal.ZERO;
        private BigDecimal offerQuantity = BigDecimal.ZERO;
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
//                marketDataGrid.get
            } else if(inEvent instanceof AskEvent) {
                AskEvent askEvent = (AskEvent)inEvent;
            } else if(inEvent instanceof TradeEvent) {
                TradeEvent tradeEvent = (TradeEvent)inEvent;
            } else if(inEvent instanceof MarketstatEvent) {
                MarketstatEvent marketstatEvent = (MarketstatEvent)inEvent;
            } else {
                
            }
        }
        /**
         *
         *
         * @param inItem
         */
        private void setItem(Item inItem)
        {
            rowItem = inItem;
        }
        /* (non-Javadoc)
         * @see org.marketcetera.marketdata.MarketDataListener#onError(java.lang.Throwable)
         */
        @Override
        public void onError(Throwable inThrowable)
        {
            SLF4JLoggerProxy.warn(MarketDataListView.this,
                                  inThrowable,
                                  "COCO: error");
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
        private final String symbol;
        private final String requestId;
        private BidEvent bidEvent;
        private AskEvent askEvent;
        private TradeEvent tradeEvent;
        private MarketstatEvent marketStatEvent;
        private Item rowItem;
        private MarketDataListView getOuterType()
        {
            return MarketDataListView.this;
        }
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
    private static final long serialVersionUID = -4416759265511242121L;
}
