package org.marketcetera.ui.marketdata.view;

import java.math.BigDecimal;
import java.util.Properties;
import java.util.SortedMap;
import java.util.UUID;

import org.joda.time.DateTime;
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

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;

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
     * @see org.marketcetera.ui.view.ContentView#getMainLayout()
     */
    @Override
    public Region getMainLayout()
    {
        return rootLayout;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ui.view.ContentView#onClose()
     */
    @Override
    public void onClose()
    {
        super.onClose();
        try {
            if(depthMarketDataRequestId != null) {
                marketDataClient.cancel(depthMarketDataRequestId);
            }
            clearDepthDisplay();
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
    /* (non-Javadoc)
     * @see org.marketcetera.ui.view.AbstractContentView#onStart()
     */
    @Override
    protected void onStart()
    {
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
        marketDataLayout = new GridPane();
        marketDataLayout.setHgap(10);
        marketDataLayout.setVgap(10);
        marketDataLayout.setPadding(new Insets(10,10,10,10));
        initializeTables();
        initializeChart();
        int rowCount = 0;
        marketDataLayout.add(addSymbolLayout,0,rowCount,2,1);
//        marketDataGrid.add(chart,0,++rowCount,2,1);
        marketDataLayout.add(bidMarketDataTable,0,++rowCount);
        marketDataLayout.add(askMarketDataTable,1,rowCount);
        marketDataLayout.setMaxWidth(Double.MAX_VALUE);
        marketDataLayout.setMaxHeight(Double.MAX_VALUE);
        RowConstraints addSymbolRowConstraint = new RowConstraints();
        addSymbolRowConstraint.setVgrow(Priority.NEVER);
        RowConstraints marketDataTableRowConstraint = new RowConstraints();
        marketDataTableRowConstraint.setVgrow(Priority.ALWAYS);
        marketDataLayout.getRowConstraints().addAll(addSymbolRowConstraint,
                                                  marketDataTableRowConstraint);
        ColumnConstraints bidTableColumnConstraint = new ColumnConstraints();
        bidTableColumnConstraint.setHgrow(Priority.ALWAYS);
        bidTableColumnConstraint.setPercentWidth(50);
        ColumnConstraints askTableColumnConstraint = new ColumnConstraints();
        askTableColumnConstraint.setHgrow(Priority.ALWAYS);
        askTableColumnConstraint.setPercentWidth(50);
        marketDataLayout.prefWidthProperty().bind(getParentWindow().widthProperty());
        marketDataLayout.getColumnConstraints().addAll(bidTableColumnConstraint,
                                                     askTableColumnConstraint);
        rootLayout.prefHeightProperty().bind(getParentWindow().heightProperty());
        rootLayout.getChildren().addAll(marketDataLayout);
        rootLayout.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if(event.getCode() == KeyCode.ENTER) {
               addSymbolButton.fire();
               event.consume(); 
            }
        });
        if(marketDataInstrument != null) {
            doMarketDataRequest();
        }
        restoreSymbol();
    }
    /**
     * Create a new MarketDataDetailView instance.
     *
     * @param inParent a <code>Region</code> value
     * @param inNewWindowEvent a <code>NewWindowEvent</code> value
     * @param inProperties a <code>Properties</code> value
     */
    public MarketDataDetailView(Region inParent,
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
    /**
     * Initialize the add symbol control.
     */
    private void initializeAddSymbol()
    {
        addSymbolLayout = new HBox(5);
        addSymbolTextField = new TextField();
        addSymbolButton = new Button();
        addSymbolButton.setGraphic(new ImageView(new Image("images/add.png")));
        addSymbolButton.setDisable(true);
        addSymbolTextField.textProperty().addListener((observableValue,oldValue,newValue) -> {
            newValue = tradeClient.getTreatedSymbol(newValue);
            addSymbolButton.setDisable(newValue == null);
        });
        addSymbolButton.setOnAction(event -> {
            String symbol = tradeClient.getTreatedSymbol(addSymbolTextField.getText());
            if(symbol == null) {
                return;
            }
            addSymbolTextField.textProperty().set(symbol);
            marketDataInstrument = tradeClient.resolveSymbol(symbol);
            updateViewProperties();
            doMarketDataRequest();
            // TODO change the title of the window
            addSymbolTextField.setText(null);
        });
        addSymbolLayout.setAlignment(Pos.CENTER_LEFT);
        providerComboBox = new ComboBox<>();
        providerComboBox.getItems().add(ALL_PROVIDERS);
        providerComboBox.getItems().addAll(marketDataClient.getProviders());
        addSymbolLayout.getChildren().addAll(providerComboBox,
                                             addSymbolTextField,
                                             addSymbolButton);
    }
    /**
     * Initialize the tables that hold the quotes.
     */
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
        bidMarketDataTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        askMarketDataTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }
    /**
     * Initialize the table columns.
     */
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
    /**
     * Initialize the tables context menu.
     */
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
        uiMessageService.post(new MarketDataSuggestionEvent(inSide.name() + " " + marketDataInstrument.getSymbol(),
                                                             suggestion));
    }
    /**
     * Execute the market data request.
     */
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
        MarketDataRequestBuilder depthRequestBuilder = MarketDataRequestBuilder.newRequest().withRequestId(depthMarketDataRequestId).withSymbols(marketDataInstrument.getSymbol());
        // TODO choose between aggregated and unaggregated?
        depthRequestBuilder = depthRequestBuilder.withContent(Content.AGGREGATED_DEPTH);
        if(providerComboBox.valueProperty().get() != null && providerComboBox.valueProperty().get() != ALL_PROVIDERS) {
            depthRequestBuilder.withProvider(providerComboBox.valueProperty().get());
        }
        MarketDataRequest depthRequest = depthRequestBuilder.create();
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
    /* (non-Javadoc)
     * @see org.marketcetera.ui.view.AbstractContentView#onClientConnect()
     */
    @Override
    protected void onClientConnect()
    {
        doMarketDataRequest();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ui.view.AbstractContentView#onClientDisconnect()
     */
    @Override
    protected void onClientDisconnect()
    {
        Platform.runLater(() -> {
            bidMarketDataTable.getItems().clear();
            askMarketDataTable.getItems().clear();
        });
    }
    /**
     * Restore the symbol layout.
     */
    private void restoreSymbol()
    {
        Properties windowProperties = getViewProperties();
        String symbol = windowProperties.getProperty(symbolKey);
        if(symbol != null) {
            marketDataInstrument = tradeClient.resolveSymbol(symbol);
            doMarketDataRequest();
        }
    }
    /**
     * Update the view properties for this view.
     */
    private void updateViewProperties()
    {
        if(marketDataInstrument != null) {
            getViewProperties().setProperty(symbolKey,
                                            marketDataInstrument.getFullSymbol());
        }
    }
    /**
     * Update the display table.
     *
     * @param inEvent an <code>Event</code> value
     */
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
    /**
     * Clear the depth display.
     */
    private void clearDepthDisplay()
    {
        bidMarketDataTable.getItems().clear();
        askMarketDataTable.getItems().clear();
    }
    /**
     * Refresh the depth display.
     */
    private void refreshDepthDisplay()
    {
        Platform.runLater(() -> {
            // TODO this seems very heavy-handed
            clearDepthDisplay();
            bidMarketDataTable.getItems().addAll(bids.values());
            askMarketDataTable.getItems().addAll(asks.values());
        });
    }
    /**
     * Initialize the chart display.
     */
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
    /**
     * wrench value to indicate all providers are selected
     */
    private static final String ALL_PROVIDERS = "<all providers>";
    /**
     * market data request id
     */
    private String depthMarketDataRequestId;
    /**
     * bids by level
     */
    private final SortedMap<Integer,MarketDataQuoteItem> bids = Maps.newTreeMap();
    /**
     * asks by level
     */
    private final SortedMap<Integer,MarketDataQuoteItem> asks = Maps.newTreeMap();
    /**
     * instrument being displayed
     */
    private Instrument marketDataInstrument;
    /**
     * layout for displaying market data
     */
    private GridPane marketDataLayout;
    /**
     * context menu for bid quotes
     */
    private ContextMenu bidMarketDataContextMenu;
    /**
     * context menu for ask quotes
     */
    private ContextMenu askMarketDataContextMenu;
    /**
     * buy market data menu item
     */
    private MenuItem buyMarketDataMenuItem;
    /**
     * sell market data menu item
     */
    private MenuItem sellMarketDataMenuItem;
    /**
     * table used to display bids
     */
    private TableView<MarketDataQuoteItem> bidMarketDataTable;
    /**
     * table used to display asks
     */
    private TableView<MarketDataQuoteItem> askMarketDataTable;
    /**
     * provides access to market data services
     */
    private MarketDataClientService marketDataClient;
    /**
     * provides access to trade services
     */
    private TradeClientService tradeClient;
    /**
     * main view layout
     */
    private VBox rootLayout;
    /**
     * key used to store selected symbol for storing preferences
     */
    private final String symbolKey = "SYMBOL";
    /**
     * layout for the add symbol controls
     */
    private HBox addSymbolLayout;
    /**
     * add symbol text box
     */
    private TextField addSymbolTextField;
    /**
     * add symbol control button
     */
    private Button addSymbolButton;
    /**
     * allows selection of a specific market data provider
     */
    private ComboBox<String> providerComboBox;
    /**
     * bid timestamp column
     */
    private TableColumn<MarketDataQuoteItem,DateTime> bidTimestampColumn;
    /**
     * bid exchange column
     */
    private TableColumn<MarketDataQuoteItem,String> bidExchangeColumn;
    /**
     * bid size column
     */
    private TableColumn<MarketDataQuoteItem,BigDecimal> bidSizeColumn;
    /**
     * bid price column
     */
    private TableColumn<MarketDataQuoteItem,BigDecimal> bidPriceColumn;
    /**
     * ask size column
     */
    private TableColumn<MarketDataQuoteItem,BigDecimal> askSizeColumn;
    /**
     * ask price column
     */
    private TableColumn<MarketDataQuoteItem,BigDecimal> askPriceColumn;
    /**
     * ask timestamp column
     */
    private TableColumn<MarketDataQuoteItem,DateTime> askTimestampColumn;
    /**
     * ask exchange column
     */
    private TableColumn<MarketDataQuoteItem,String> askExchangeColumn;
    /**
     * global name of this view
     */
    private static final String NAME = "Market Data Detail View";
}
