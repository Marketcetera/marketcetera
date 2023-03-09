package org.marketcetera.ui.marketdata.view;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Properties;
import java.util.SortedMap;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.event.AggregateEvent;
import org.marketcetera.event.AskEvent;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.Event;
import org.marketcetera.event.QuoteEvent;
import org.marketcetera.marketdata.Content;
import org.marketcetera.marketdata.MarketDataListener;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.MarketDataRequestBuilder;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.trade.OrderSingleSuggestion;
import org.marketcetera.trade.OrderType;
import org.marketcetera.trade.Side;
import org.marketcetera.ui.PhotonServices;
import org.marketcetera.ui.events.NewWindowEvent;
import org.marketcetera.ui.marketdata.event.MarketDataDetailEvent;
import org.marketcetera.ui.marketdata.event.MarketDataSuggestionEvent;
import org.marketcetera.ui.marketdata.service.MarketDataClientService;
import org.marketcetera.ui.service.trade.TradeClientService;
import org.marketcetera.ui.view.AbstractContentView;
import org.marketcetera.ui.view.ContentView;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;

import io.fair_acc.chartfx.XYChart;
import io.fair_acc.dataset.spi.financial.api.attrs.AttributeModel;
import io.fair_acc.dataset.spi.financial.api.ohlcv.IOhlcvItem;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/* $License$ */

/**
 * Provides a view for market data detail.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MarketDataDetailView
        extends AbstractContentView
        implements ContentView
{
    /* (non-Javadoc)
     * @see org.marketcetera.ui.view.ContentView#getScene()
     */
    @Override
    public Scene getScene()
    {
        return scene;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ui.view.ContentView#onClose(javafx.stage.WindowEvent)
     */
    @Override
    public void onClose(WindowEvent inEvent)
    {
        SLF4JLoggerProxy.trace(this,
                               "{} {} stop",
                               PlatformServices.getServiceName(getClass()),
                               hashCode());
        try {
//            synchronized(symbolsByRequestId) {
//                updateViewProperties();
//                for(String requestId : symbolsByRequestId.keySet()) {
//                    marketdataClient.cancel(requestId);
//                }
//                symbolsByRequestId.clear();
//            }
        } catch (Exception e) {
            SLF4JLoggerProxy.warn(this,
                                  e);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ui.view.ContentView#getViewName()
     */
    @Override
    public String getViewName()
    {
      return NAME;
    }
    /**
     * Initialize and start the object.
     */
    @PostConstruct
    public void start()
    {
        SLF4JLoggerProxy.trace(this,
                               "{} {} start",
                               PlatformServices.getServiceName(getClass()),
                               hashCode());
        marketDataClient = serviceManager.getService(MarketDataClientService.class);
        SLF4JLoggerProxy.debug(this,
                               "Available market data capabilities are: {}",
                               marketDataClient.getAvailableCapability());
        tradeClient = serviceManager.getService(TradeClientService.class);
        rootLayout = new VBox(5);
        initializeAddSymbol();
        if(marketDataInstrument != null) {
            addSymbolTextField.setText(marketDataInstrument.getSymbol());
        }
        marketDataGrid = new GridPane();
        marketDataGrid.setHgap(10);
        marketDataGrid.setVgap(10);
        marketDataGrid.setPadding(new Insets(10,10,10,10));
        initializeTables();
//        initializeChart();
        int rowCount = 0;
        marketDataGrid.add(addSymbolLayout,0,rowCount,2,1);
//        marketDataGrid.add(chart,0,++rowCount,2,1);;
        marketDataGrid.add(bidMarketDataTable,0,++rowCount);
        marketDataGrid.add(askMarketDataTable,1,rowCount);
        marketDataGrid.setMaxWidth(Double.MAX_VALUE);
        marketDataGrid.setMaxHeight(Double.MAX_VALUE);
        RowConstraints addSymbolRowConstraint = new RowConstraints();
        addSymbolRowConstraint.setVgrow(Priority.NEVER);
        RowConstraints marketDataTableRowConstraint = new RowConstraints();
        marketDataTableRowConstraint.setVgrow(Priority.ALWAYS);
        marketDataGrid.getRowConstraints().addAll(addSymbolRowConstraint,
                                                  marketDataTableRowConstraint);
        ColumnConstraints bidTableColumnConstraint = new ColumnConstraints();
        bidTableColumnConstraint.setHgrow(Priority.ALWAYS);
        bidTableColumnConstraint.setPercentWidth(50);
        ColumnConstraints askTableColumnConstraint = new ColumnConstraints();
        askTableColumnConstraint.setHgrow(Priority.ALWAYS);
        askTableColumnConstraint.setPercentWidth(50);
        marketDataGrid.getColumnConstraints().addAll(bidTableColumnConstraint,
                                                     askTableColumnConstraint);
        rootLayout.getChildren().addAll(marketDataGrid);
        rootLayout.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if(event.getCode() == KeyCode.ENTER) {
               addSymbolButton.fire();
               event.consume(); 
            }
        });
        scene = new Scene(rootLayout);
        if(marketDataInstrument != null) {
            doMarketDataRequest();
        }
    }
    /**
     * Create a new MarketDataDetailView instance.
     *
     * @param inParent a <code>Window</code> value
     * @param inNewWindowEvent a <code>NewWindowEvent</code> value
     * @param inProperties a <code>Properties</code> value
     */
    public MarketDataDetailView(Stage inParent,
                                NewWindowEvent inEvent,
                                Properties inProperties)
    {
        super(inParent,
              inEvent,
              inProperties);
        if(inEvent instanceof MarketDataDetailEvent) {
            MarketDataDetailEvent marketDataDetailEvent = (MarketDataDetailEvent)inEvent;
            marketDataInstrument = marketDataDetailEvent.getInstrument();
        }
    }
    private void initializeAddSymbol()
    {
        addSymbolLayout = new HBox(5);
        addSymbolTextField = new TextField();
        addSymbolButton = new Button();
        addSymbolButton.setGraphic(new ImageView(new Image("images/add.png")));
        addSymbolButton.setDisable(true);
        addSymbolTextField.textProperty().addListener((observableValue,oldValue,newValue) -> {
            newValue = StringUtils.trimToNull(newValue);
            addSymbolButton.setDisable(newValue == null);
        });
        addSymbolButton.setOnAction(event -> {
            String symbol = StringUtils.trimToNull(addSymbolTextField.getText());
            if(symbol == null) {
                return;
            }
            symbol = symbol.toUpperCase();
            addSymbolTextField.textProperty().set(symbol);
            marketDataInstrument = tradeClient.resolveSymbol(symbol);
            doMarketDataRequest();
        });
        addSymbolLayout.setAlignment(Pos.CENTER_LEFT);
        addSymbolLayout.getChildren().addAll(addSymbolTextField,
                                             addSymbolButton);
    }
    private void initializeTables()
    {
        bidMarketDataTable = new TableView<>();
        bidMarketDataTable.setPlaceholder(new Label("no bid data"));
        askMarketDataTable = new TableView<>();
        askMarketDataTable.setPlaceholder(new Label("no ask data"));
        initializeColumns();
        initializeContextMenu();
        bidMarketDataTable.getColumns().forEach(column -> { column.setReorderable(false); column.setSortable(false); });
        askMarketDataTable.getColumns().forEach(column -> { column.setReorderable(false); column.setSortable(false); });
    }
    private void initializeColumns()
    {
        // TODO need to manage count column for aggregated display
        bidTimestampColumn = new TableColumn<>("Timestamp");
        bidTimestampColumn.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        bidTimestampColumn.setCellFactory(tableColumn -> PhotonServices.renderDateTimeCell(tableColumn));
        bidExchangeColumn = new TableColumn<>("Exchange");
        bidExchangeColumn.setCellValueFactory(new PropertyValueFactory<>("exchange"));
        bidSizeColumn = new TableColumn<>("Size");
        bidSizeColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        bidSizeColumn.setCellFactory(tableColumn -> PhotonServices.renderNumberCell(tableColumn));
        bidPriceColumn = new TableColumn<>("Price");
        bidPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        bidPriceColumn.setCellFactory(tableColumn -> PhotonServices.renderCurrencyCell(tableColumn));
        bidMarketDataTable.getColumns().add(bidTimestampColumn);
        bidMarketDataTable.getColumns().add(bidExchangeColumn);
        bidMarketDataTable.getColumns().add(bidSizeColumn);
        bidMarketDataTable.getColumns().add(bidPriceColumn);
        askPriceColumn = new TableColumn<>("Price");
        askPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        askPriceColumn.setCellFactory(tableColumn -> PhotonServices.renderCurrencyCell(tableColumn));
        askSizeColumn = new TableColumn<>("Size");
        askSizeColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        askSizeColumn.setCellFactory(tableColumn -> PhotonServices.renderNumberCell(tableColumn));
        askTimestampColumn = new TableColumn<>("Timestamp");
        askTimestampColumn.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        askTimestampColumn.setCellFactory(tableColumn -> PhotonServices.renderDateTimeCell(tableColumn));
        askExchangeColumn = new TableColumn<>("Exchange");
        askExchangeColumn.setCellValueFactory(new PropertyValueFactory<>("exchange"));
        askMarketDataTable.getColumns().add(askPriceColumn);
        askMarketDataTable.getColumns().add(askSizeColumn);
        askMarketDataTable.getColumns().add(askExchangeColumn);
        askMarketDataTable.getColumns().add(askTimestampColumn);
    }
    private void initializeContextMenu()
    {
        buyMarketDataMenuItem = new MenuItem("Buy");
        buyMarketDataMenuItem.setOnAction(event -> {
            MarketDataQuoteItem selectedItem = bidMarketDataTable.getSelectionModel().getSelectedItem();
            buyOrSellAction(Side.Buy,
                            selectedItem.priceProperty().get());
        });
        sellMarketDataMenuItem = new MenuItem("Sell");
        sellMarketDataMenuItem.setOnAction(event -> {
            MarketDataQuoteItem selectedItem = bidMarketDataTable.getSelectionModel().getSelectedItem();
            buyOrSellAction(Side.Sell,
                            selectedItem.priceProperty().get());
        });
        bidMarketDataContextMenu = new ContextMenu();
        askMarketDataContextMenu = new ContextMenu();
        bidMarketDataContextMenu.getItems().addAll(sellMarketDataMenuItem);
        askMarketDataContextMenu.getItems().addAll(buyMarketDataMenuItem);
        bidMarketDataTable.setContextMenu(bidMarketDataContextMenu);
        askMarketDataTable.setContextMenu(askMarketDataContextMenu);
    }
    /**
     * Execute buy or sell action for the given selected row.
     *
     * @param inSide a <code>Side</code> value
     * @param inPrice a <code>BigDecimal</code> value
     */
    private void buyOrSellAction(Side inSide,
                                 BigDecimal inPrice)
    {
        OrderSingle orderSingle = Factory.getInstance().createOrderSingle();
        orderSingle.setInstrument(marketDataInstrument);
        orderSingle.setOrderType(OrderType.Limit);
        orderSingle.setPrice(inPrice);
        orderSingle.setSide(inSide);
        OrderSingleSuggestion suggestion = Factory.getInstance().createOrderSingleSuggestion();
        suggestion.setIdentifier("Market Data List View Action");
        suggestion.setScore(BigDecimal.ONE);
        suggestion.setOrder(orderSingle);
        webMessageService.post(new MarketDataSuggestionEvent(inSide.name() + " " + marketDataInstrument.getSymbol(),
                                                             suggestion));
    }
    private void doMarketDataRequest()
    {
        if(depthMarketDataRequestId != null) {
            marketDataClient.cancel(depthMarketDataRequestId);
        }
        Platform.runLater(() -> {
            bidMarketDataTable.getItems().clear();
            askMarketDataTable.getItems().clear();
        });
        bids.clear();
        asks.clear();
        // TODO reset chart/top-level view
        depthMarketDataRequestId = UUID.randomUUID().toString();
        // TODO choose between aggregated and unaggregated?
        MarketDataRequest depthRequest = MarketDataRequestBuilder.newRequest().withRequestId(depthMarketDataRequestId).withSymbols(marketDataInstrument.getSymbol()).withContent(Content.AGGREGATED_DEPTH).create();
        marketDataClient.request(depthRequest,
                                 new MarketDataListener() {
            /* (non-Javadoc)
             * @see org.marketcetera.marketdata.MarketDataListener#receiveMarketData(org.marketcetera.event.Event)
             */
            @Override
            public void receiveMarketData(Event inEvent)
            {
                updateDepthMarketData(inEvent);
            }
        });
    }
    private void updateDepthMarketData(Event inEvent)
    {
        SLF4JLoggerProxy.trace(this,
                               "{} received depth event: {}",
                               this,
                               inEvent);
        // TODO probably need to handle snapshots vs updates
        // clear the tables at the first snapshot_part but not again until after the snapshot_final, probably don't do anything until the snapshot final
        // TODO need to handle quote actions: ADD vs DEL vs CHANGE
        if(inEvent instanceof QuoteEvent) {
            QuoteEvent quoteEvent = (QuoteEvent)inEvent;
            MarketDataQuoteItem quoteItem = new MarketDataQuoteItem(quoteEvent);
            if(quoteEvent instanceof BidEvent) {
                // TODO need to handle action
                bids.put(quoteEvent.getLevel(),
                         quoteItem);
            } else if(quoteEvent instanceof AskEvent) {
                // TODO need to handle action
                asks.put(quoteEvent.getLevel(),
                         quoteItem);
            } else {
                SLF4JLoggerProxy.warn(this,
                                      "Discarding unexpected event: {}",
                                      inEvent);
                return;
            }
        } else if(inEvent instanceof AggregateEvent) {
            AggregateEvent aggregateEvent = (AggregateEvent)inEvent;
            // TODO probably need to handle AggregateEvent
        }
        // TODO bind the tables to the maps?
        refreshDepthDisplay();
    }
    private void clearDepthDisplay()
    {
        bidMarketDataTable.getItems().clear();
        askMarketDataTable.getItems().clear();
    }
    private void refreshDepthDisplay()
    {
        Platform.runLater(() -> {
            // TODO this seems very heavy-handed
            clearDepthDisplay();
            bidMarketDataTable.getItems().addAll(bids.values());
            askMarketDataTable.getItems().addAll(asks.values());
        });
    }
    private void initializeChart()
    {
////        ohlcvDataSet = new OhlcvDataSet("Sample Chart");
//        
//        final DefaultNumericAxis xAxis1 = new DefaultNumericAxis("time", "iso");
//        xAxis1.setOverlapPolicy(AxisLabelOverlapPolicy.SKIP_ALT);
//        xAxis1.setAutoRangeRounding(false);
//        xAxis1.setTimeAxis(true);
//        final DefaultNumericAxis yAxis1 = new DefaultNumericAxis("price", "points");
//
//        // prepare chart structure
//        chart = new XYChart(xAxis1, yAxis1);
////        chart.setTitle(theme);
//        chart.setLegendVisible(true);
////        chart.setPrefSize(prefChartWidth, prefChartHeight);
//        // set them false to make the plot faster
//        chart.setAnimated(false);
//
//        // prepare plugins
//        chart.getPlugins().add(new Zoomer(AxisMode.X));
//        chart.getPlugins().add(new EditAxis());
//        chart.getPlugins().add(new DataPointTooltip());
//
//        // basic chart financial structure style
//        chart.getGridRenderer().setDrawOnTop(false);
//        yAxis1.setAutoRangeRounding(true);
//        yAxis1.setSide(io.fair_acc.chartfx.ui.geometry.Side.RIGHT);
//        CandleStickRenderer candleStickRenderer = new CandleStickRenderer();
//        candleStickRenderer.getDatasets().addAll(ohlcvDataSet);
//
//        chart.getRenderers().clear();
//        chart.getRenderers().add(candleStickRenderer);
    }
    private class OhlcvDataSet
    {
        
    }
    private class OhlcItem
            implements IOhlcvItem
    {

        /* (non-Javadoc)
         * @see io.fair_acc.dataset.spi.financial.api.ohlcv.IOhlcvItem#getTimeStamp()
         */
        @Override
        public Date getTimeStamp()
        {
            throw new UnsupportedOperationException(); // TODO
            
        }

        /* (non-Javadoc)
         * @see io.fair_acc.dataset.spi.financial.api.ohlcv.IOhlcvItem#getOpen()
         */
        @Override
        public double getOpen()
        {
            throw new UnsupportedOperationException(); // TODO
            
        }

        /* (non-Javadoc)
         * @see io.fair_acc.dataset.spi.financial.api.ohlcv.IOhlcvItem#getHigh()
         */
        @Override
        public double getHigh()
        {
            throw new UnsupportedOperationException(); // TODO
            
        }

        /* (non-Javadoc)
         * @see io.fair_acc.dataset.spi.financial.api.ohlcv.IOhlcvItem#getLow()
         */
        @Override
        public double getLow()
        {
            throw new UnsupportedOperationException(); // TODO
            
        }

        /* (non-Javadoc)
         * @see io.fair_acc.dataset.spi.financial.api.ohlcv.IOhlcvItem#getClose()
         */
        @Override
        public double getClose()
        {
            throw new UnsupportedOperationException(); // TODO
            
        }

        /* (non-Javadoc)
         * @see io.fair_acc.dataset.spi.financial.api.ohlcv.IOhlcvItem#getVolume()
         */
        @Override
        public double getVolume()
        {
            throw new UnsupportedOperationException(); // TODO
            
        }

        /* (non-Javadoc)
         * @see io.fair_acc.dataset.spi.financial.api.ohlcv.IOhlcvItem#getOpenInterest()
         */
        @Override
        public double getOpenInterest()
        {
            throw new UnsupportedOperationException(); // TODO
            
        }

        /* (non-Javadoc)
         * @see io.fair_acc.dataset.spi.financial.api.ohlcv.IOhlcvItem#getAddon()
         */
        @Override
        public AttributeModel getAddon()
        {
            throw new UnsupportedOperationException(); // TODO
            
        }

        /* (non-Javadoc)
         * @see io.fair_acc.dataset.spi.financial.api.ohlcv.IOhlcvItem#getAddonOrCreate()
         */
        @Override
        public AttributeModel getAddonOrCreate()
        {
            throw new UnsupportedOperationException(); // TODO
            
        }
        
    }
    private XYChart chart;
    private OhlcvDataSet ohlcvDataSet;
    private String depthMarketDataRequestId;
    private final SortedMap<Integer,MarketDataQuoteItem> bids = Maps.newTreeMap();
    private final SortedMap<Integer,MarketDataQuoteItem> asks = Maps.newTreeMap();
    private Instrument marketDataInstrument;
    private GridPane marketDataGrid;
    private ContextMenu bidMarketDataContextMenu;
    private ContextMenu askMarketDataContextMenu;
    private MenuItem buyMarketDataMenuItem;
    private MenuItem sellMarketDataMenuItem;
    private TableView<MarketDataQuoteItem> bidMarketDataTable;
    private TableView<MarketDataQuoteItem> askMarketDataTable;
    private MarketDataClientService marketDataClient;
    private TradeClientService tradeClient;
    private Scene scene;
    private VBox rootLayout;
    private final String symbolsKey = "SYMBOLS";
    private HBox addSymbolLayout;
    private TextField addSymbolTextField;
    private Button addSymbolButton;
    private TableColumn<MarketDataQuoteItem,DateTime> bidTimestampColumn;
    private TableColumn<MarketDataQuoteItem,String> bidExchangeColumn;
    private TableColumn<MarketDataQuoteItem,BigDecimal> bidSizeColumn;
    private TableColumn<MarketDataQuoteItem,BigDecimal> bidPriceColumn;
    private TableColumn<MarketDataQuoteItem,BigDecimal> askSizeColumn;
    private TableColumn<MarketDataQuoteItem,BigDecimal> askPriceColumn;
    private TableColumn<MarketDataQuoteItem,DateTime> askTimestampColumn;
    private TableColumn<MarketDataQuoteItem,String> askExchangeColumn;
    /**
     * global name of this view
     */
    private static final String NAME = "Market Data Detail View";
}
