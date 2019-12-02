package org.marketcetera.web.marketdata.list.view;

import java.math.BigDecimal;
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
        marketDataGrid = new Grid();
        marketDataGrid.addColumn("Symbol",
                                 String.class);
        marketDataGrid.addColumn("Trade Px",
                                 BigDecimal.class).setConverter(DecimalConverter.instance);
        marketDataGrid.addColumn("Trade Qty",
                                 BigDecimal.class);
        marketDataGrid.addColumn("Bid Qty",
                                 BigDecimal.class);
        marketDataGrid.addColumn("Bid Px",
                                 BigDecimal.class).setConverter(DecimalConverter.instance);
        marketDataGrid.addColumn("Offer Px",
                                 BigDecimal.class).setConverter(DecimalConverter.instance);
        marketDataGrid.addColumn("Offer Qty",
                                 BigDecimal.class);
//                                  "Trade Qty",
//                                  "Bid Qty",
//                                  "Bid Px",
//                                  "Offer Px",
//                                  "Offer Qty",
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
        marketDataGrid.setId(getClass().getCanonicalName() + ".marketDataGrid");
        styleService.addStyle(marketDataGrid);
        marketDataGridLayout.addComponents(marketDataGrid);
        marketDataSymbolText = new TextField();
        addMarketDataSymbolButton = new Button();
        addMarketDataSymbolButton.setIcon(FontAwesome.PLUS_CIRCLE);
        addMarketDataSymbolButton.addClickListener(inClickEvent -> {
            String newSymbol = StringUtils.trimToNull(marketDataSymbolText.getValue());
            if(newSymbol != null) {
                MarketDataClientService marketDataClientService = serviceManager.getService(MarketDataClientService.class);
                Instrument resolvedInstrument = serviceManager.getService(TradeClientService.class).resolveSymbol(newSymbol);
                AssetClass assetClass = AssetClass.EQUITY;
                if(resolvedInstrument != null) {
                    assetClass = AssetClass.getFor(resolvedInstrument.getSecurityType());
                }
                MarketDataRequestBuilder requestBuilder = MarketDataRequestBuilder.newRequest();
                requestBuilder.withAssetClass(assetClass).withSymbols(newSymbol).withContent(Content.LATEST_TICK,Content.MARKET_STAT,Content.TOP_OF_BOOK);
                String requestId = marketDataClientService.request(requestBuilder.create(),
                                                                   new MarketDataListener() {
                    /* (non-Javadoc)
                     * @see org.marketcetera.marketdata.MarketDataListener#receiveMarketData(org.marketcetera.event.Event)
                     */
                    @Override
                    public void receiveMarketData(org.marketcetera.event.Event inEvent)
                    {
                        SLF4JLoggerProxy.warn(MarketDataListView.this,
                                              "COCO: received {}",
                                              inEvent);
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
                });
            }
        });
        addMarketDataSymbolButton.setId(getClass().getCanonicalName() + ".addMarketDataSymbolButton");
        styleService.addStyle(addMarketDataSymbolButton);
        symbolEntryLayout.addComponents(marketDataSymbolText,
                                        addMarketDataSymbolButton);
    }
    private class MarketDataRow
            implements Comparable<MarketDataRow>
    {
        /* (non-Javadoc)
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
        @Override
        public int compareTo(MarketDataRow inO)
        {
            return inO.symbol.compareTo(symbol);
        }
        private String requestId;
        private String symbol;
        private BidEvent bidEvent;
        private AskEvent askEvent;
        private TradeEvent tradeEvent;
        private MarketstatEvent marketStatEvent;
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
