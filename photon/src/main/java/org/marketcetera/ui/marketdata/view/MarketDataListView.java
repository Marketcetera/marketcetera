package org.marketcetera.ui.marketdata.view;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.marketcetera.core.BigDecimalUtil;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.event.Event;
import org.marketcetera.event.LogEvent;
import org.marketcetera.marketdata.AssetClass;
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
import org.marketcetera.ui.events.NotificationEvent;
import org.marketcetera.ui.marketdata.event.MarketDataDetailEvent;
import org.marketcetera.ui.marketdata.service.MarketDataClientService;
import org.marketcetera.ui.trade.event.SuggestionEvent;
import org.marketcetera.ui.trade.service.TradeClientService;
import org.marketcetera.ui.view.AbstractContentView;
import org.marketcetera.ui.view.ContentView;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.nocrala.tools.texttablefmt.BorderStyle;
import org.nocrala.tools.texttablefmt.ShownBorders;
import org.nocrala.tools.texttablefmt.Table;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/* $License$ */

/**
 * Provides a view for the market data list.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MarketDataListView
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
        try {
            synchronized(symbolsByRequestId) {
                updateViewProperties();
                for(String requestId : symbolsByRequestId.keySet()) {
                    marketDataClient.cancel(requestId);
                }
                symbolsByRequestId.clear();
            }
        } catch (Exception e) {
            SLF4JLoggerProxy.warn(this,
                                  e);
        }
        super.onClose();
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
        tradeClient = serviceManager.getService(TradeClientService.class);
        rootLayout = new VBox(5);
        initializeAddSymbol();
        initializeTable();
        marketDataTable.prefWidthProperty().bind(getParentWindow().widthProperty());
        rootLayout.prefHeightProperty().bind(getParentWindow().heightProperty());
        rootLayout.getChildren().addAll(addSymbolLayout,
                                        marketDataTable);
        rootLayout.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if(event.getCode() == KeyCode.ENTER) {
               addSymbolButton.fire();
               event.consume(); 
            }
        });
        restoreSymbols();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ui.view.AbstractContentView#onClientConnect()
     */
    @Override
    protected void onClientConnect()
    {
        synchronized(symbolsByRequestId) {
            symbolsByRequestId.clear();
        }
        restoreSymbols();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ui.view.AbstractContentView#onClientDisconnect()
     */
    @Override
    protected void onClientDisconnect()
    {
        Platform.runLater(() -> {
            marketDataTable.getItems().clear();
        });
    }
    /**
     * Create a new MarketDataListView instance.
     *
     * @param inParent a <code>Region</code> value
     * @param inEvent a <code>NewWindowEvent</code> value
     * @param inProperties a <code>Properties</code> value
     */
    public MarketDataListView(Region inParent,
                              NewWindowEvent inEvent,
                              Properties inProperties)
    {
        super(inParent,
              inEvent,
              inProperties);
    }
    /**
     * Restore the symbol layout.
     */
    private void restoreSymbols()
    {
        Properties windowProperties = getViewProperties();
        String symbolList = windowProperties.getProperty(symbolsKey);
        if(symbolList != null) {
            symbolList = symbolList.replaceAll("\\[|\\]","");
            String[] symbols = symbolList.split(",");
            for(String symbol : symbols) {
                doMarketDataRequest(symbol);
            }
        }
    }
    /**
     * Update the view properties for this view.
     */
    private void updateViewProperties()
    {
        synchronized(symbolsByRequestId) {
            getViewProperties().setProperty(symbolsKey,
                                            String.valueOf(symbolsByRequestId.values()));
        }
    }
    /**
     * Perform the market data request for the given symbol.
     *
     * @param inSymbol a <code>String</code> value
     */
    private void doMarketDataRequest(String inSymbol)
    {
        if(inSymbol == null) {
            return;
        }
        if(symbolsByRequestId.values().contains(inSymbol)) {
            return;
        }
        Instrument instrument = tradeClient.resolveSymbol(inSymbol);
        String marketDataRequestId = UUID.randomUUID().toString();
        MarketDataItem newItem = new MarketDataItem(instrument,
                                                    marketDataRequestId);
        marketDataTable.getItems().add(newItem);
        MarketDataRequestBuilder requestBuilder = MarketDataRequestBuilder.newRequest();
        if(providerComboBox.valueProperty().get() != null && providerComboBox.valueProperty().get() != ALL_PROVIDERS) {
            requestBuilder.withProvider(providerComboBox.valueProperty().get());
        }
        requestBuilder.withSymbols(inSymbol).withAssetClass(AssetClass.getFor(instrument.getSecurityType()))
                .withContent(Content.LATEST_TICK,Content.TOP_OF_BOOK,Content.MARKET_STAT).withRequestId(marketDataRequestId).create();
        MarketDataRowListener rowListener = new MarketDataRowListener(newItem);
        symbolsByRequestId.put(marketDataRequestId,
                               inSymbol);
        MarketDataRequest request = requestBuilder.create();
        SLF4JLoggerProxy.debug(this,
                               "Submitting {}",
                               request);
        marketDataClient.request(request,
                                 rowListener);
        updateViewProperties();
    }
    /**
     * Initialize the Add Symbol controls.
     */
    private void initializeAddSymbol()
    {
        addSymbolLayout = new HBox(5);
        addSymbolLayout.setPadding(new Insets(10,30,10,10));
        addSymbolTextField = new TextField();
        addSymbolButton = new Button();
        addSymbolButton.setGraphic(new ImageView(PhotonServices.getIcon("images/add.png")));
        addSymbolButton.setDisable(true);
        addSymbolTextField.textProperty().addListener((observableValue,oldValue,newValue) -> {
            newValue = tradeClient.getTreatedSymbol(newValue);
            addSymbolButton.setDisable(newValue == null);
        });
        addSymbolButton.setOnAction(event -> {
            String symbol = tradeClient.getTreatedSymbol(addSymbolTextField.getText());
            addSymbolTextField.setText(null);
            doMarketDataRequest(symbol);
        });
        addSymbolLayout.setAlignment(Pos.CENTER_RIGHT);
        providerComboBox = new ComboBox<>();
        providerComboBox.getItems().add(ALL_PROVIDERS);
        providerComboBox.getItems().addAll(marketDataClient.getProviders());
        addSymbolLayout.getChildren().addAll(providerComboBox,
                                             addSymbolTextField,
                                             addSymbolButton);
    }
    /**
     * Initialize the market data table.
     */
    private void initializeTable()
    {
        marketDataTable = new TableView<>();
        marketDataTable.setPlaceholder(new Label("no market data selected"));
        initializeColumns();
        initializeContextMenu();
        marketDataTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }
    /**
     * Initialize the market data table columns.
     */
    private void initializeColumns()
    {
        symbolColumn = new TableColumn<>("Symbol");
        symbolColumn.setCellValueFactory(new PropertyValueFactory<>("symbol"));
        providerColumn = new TableColumn<>("Provider");
        providerColumn.setCellValueFactory(new PropertyValueFactory<>("provider"));
        tradeExchangeColumn = new TableColumn<>("LastMkt");
        tradeExchangeColumn.setCellValueFactory(new PropertyValueFactory<>("tradeExchange"));
        execPriceColumn = new TableColumn<>("ExecPrice");
        execPriceColumn.setCellValueFactory(new PropertyValueFactory<>("lastPrice"));
        execPriceColumn.setCellFactory(tableColumn -> PhotonServices.renderCurrencyCell(tableColumn));
        lastQtyColumn = new TableColumn<>("LastQty");
        lastQtyColumn.setCellValueFactory(new PropertyValueFactory<>("lastQuantity"));
        lastQtyColumn.setCellFactory(tableColumn -> PhotonServices.renderNumberCell(tableColumn));
        bidQtyColumn = new TableColumn<>("BidQty");
        bidQtyColumn.setCellValueFactory(new PropertyValueFactory<>("bidQuantity"));
        bidQtyColumn.setCellFactory(tableColumn -> PhotonServices.renderNumberCell(tableColumn));
        bidPriceColumn = new TableColumn<>("BidPx");
        bidPriceColumn.setCellValueFactory(new PropertyValueFactory<>("bidPrice"));
        bidPriceColumn.setCellFactory(tableColumn -> PhotonServices.renderCurrencyCell(tableColumn));
        askQtyColumn = new TableColumn<>("AskQty");
        askQtyColumn.setCellValueFactory(new PropertyValueFactory<>("askQuantity"));
        askQtyColumn.setCellFactory(tableColumn -> PhotonServices.renderNumberCell(tableColumn));
        askPriceColumn = new TableColumn<>("AskPx");
        askPriceColumn.setCellValueFactory(new PropertyValueFactory<>("askPrice"));
        askPriceColumn.setCellFactory(tableColumn -> PhotonServices.renderCurrencyCell(tableColumn));
        prevCloseColumn = new TableColumn<>("PrevClose");
        prevCloseColumn.setCellValueFactory(new PropertyValueFactory<>("previousClosePrice"));
        prevCloseColumn.setCellFactory(tableColumn -> PhotonServices.renderCurrencyCell(tableColumn));
        openPriceColumn = new TableColumn<>("Open");
        openPriceColumn.setCellValueFactory(new PropertyValueFactory<>("openPrice"));
        openPriceColumn.setCellFactory(tableColumn -> PhotonServices.renderCurrencyCell(tableColumn));
        highPriceColumn = new TableColumn<>("High");
        highPriceColumn.setCellValueFactory(new PropertyValueFactory<>("highPrice"));
        highPriceColumn.setCellFactory(tableColumn -> PhotonServices.renderCurrencyCell(tableColumn));
        lowPriceColumn = new TableColumn<>("Low");
        lowPriceColumn.setCellValueFactory(new PropertyValueFactory<>("lowPrice"));
        lowPriceColumn.setCellFactory(tableColumn -> PhotonServices.renderCurrencyCell(tableColumn));
        closePriceColumn = new TableColumn<>("Close");
        closePriceColumn.setCellValueFactory(new PropertyValueFactory<>("closePrice"));
        closePriceColumn.setCellFactory(tableColumn -> PhotonServices.renderCurrencyCell(tableColumn));
        volumeColumn = new TableColumn<>("Volume");
        volumeColumn.setCellValueFactory(new PropertyValueFactory<>("tradeVolume"));
        volumeColumn.setCellFactory(tableColumn -> PhotonServices.renderNumberCell(tableColumn));
        marketDataTable.getColumns().add(symbolColumn);
        marketDataTable.getColumns().add(providerColumn);
        marketDataTable.getColumns().add(tradeExchangeColumn);
        marketDataTable.getColumns().add(execPriceColumn);
        marketDataTable.getColumns().add(lastQtyColumn);
        marketDataTable.getColumns().add(bidQtyColumn);
        marketDataTable.getColumns().add(bidPriceColumn);
        marketDataTable.getColumns().add(askPriceColumn);
        marketDataTable.getColumns().add(askQtyColumn);
        marketDataTable.getColumns().add(prevCloseColumn);
        marketDataTable.getColumns().add(openPriceColumn);
        marketDataTable.getColumns().add(highPriceColumn);
        marketDataTable.getColumns().add(lowPriceColumn);
        marketDataTable.getColumns().add(closePriceColumn);
        marketDataTable.getColumns().add(volumeColumn);
    }
    /**
     * Initialize the market data item context menu.
     */
    private void initializeContextMenu()
    {
        removeMarketDataMenuItem = new MenuItem("Remove");
        removeMarketDataMenuItem.setOnAction(event -> {
            MarketDataItem marketDataItem = marketDataTable.getSelectionModel().getSelectedItem();
            if(marketDataItem == null) {
                return;
            }
            try {
                marketDataClient.cancel(marketDataItem.marketDataRequestIdProperty().get());
            } catch (Exception e) {
                SLF4JLoggerProxy.warn(this,
                                      e);
            }
            synchronized(symbolsByRequestId) {
                symbolsByRequestId.remove(marketDataItem.marketDataRequestIdProperty().get());
            }
            marketDataTable.getItems().remove(marketDataItem);
            updateViewProperties();
        });
        copyMarketDataMenuItem = new MenuItem("Copy");
        copyMarketDataMenuItem.setOnAction(event -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent clipboardContent = new ClipboardContent();
            MarketDataItem marketDataItem = marketDataTable.getSelectionModel().getSelectedItem();
            if(marketDataItem == null) {
                return;
            }
            String output = renderMarketDataItem(marketDataItem);
            clipboardContent.putString(output);
            clipboard.setContent(clipboardContent);
        });
        buyMarketDataMenuItem = new MenuItem("Buy");
        buyMarketDataMenuItem.setOnAction(event -> {
            MarketDataItem selectedItem = marketDataTable.getSelectionModel().getSelectedItem();
            buyOrSellAction(selectedItem,
                            Side.Buy,
                            selectedItem.askPriceProperty().get());
        });
        sellMarketDataMenuItem = new MenuItem("Sell");
        sellMarketDataMenuItem.setOnAction(event -> {
            MarketDataItem selectedItem = marketDataTable.getSelectionModel().getSelectedItem();
            buyOrSellAction(selectedItem,
                            Side.Sell,
                            selectedItem.bidPriceProperty().get());
        });
        viewDetailMarketDataMenuItem = new MenuItem("View Detail");
        viewDetailMarketDataMenuItem.setOnAction(event -> {
            MarketDataItem selectedItem = marketDataTable.getSelectionModel().getSelectedItem();
            if(selectedItem == null) {
                return;
            }
            MarketDataDetailEvent viewFixMessageDetailsEvent = applicationContext.getBean(MarketDataDetailEvent.class,
                                                                                          selectedItem.getInstrument().getFullSymbol(),
                                                                                          selectedItem.getInstrument());
            uiMessageService.post(viewFixMessageDetailsEvent);
        });
        marketDataContextMenu = new ContextMenu();
        marketDataContextMenu.getItems().addAll(removeMarketDataMenuItem,
                                                copyMarketDataMenuItem,
                                                buyMarketDataMenuItem,
                                                sellMarketDataMenuItem,
                                                viewDetailMarketDataMenuItem);
        marketDataTable.setContextMenu(marketDataContextMenu);
    }
    /**
     * Execute buy or sell action for the given selected row.
     *
     * @param inSelectedItem a <code>MarketDataItem</code> value
     * @param inSide a <code>Side</code> value
     * @param inPrice a <code>BigDecimal</code> value
     */
    private void buyOrSellAction(MarketDataItem inSelectedItem,
                                 Side inSide,
                                 BigDecimal inPrice)
    {
        if(inSelectedItem == null) {
            return;
        }
        OrderSingle orderSingle = Factory.getInstance().createOrderSingle();
        orderSingle.setInstrument(inSelectedItem.getInstrument());
        orderSingle.setOrderType(OrderType.Limit);
        orderSingle.setPrice(inPrice);
        orderSingle.setSide(inSide);
        OrderSingleSuggestion suggestion = Factory.getInstance().createOrderSingleSuggestion();
        suggestion.setIdentifier("Market Data List View Action");
        suggestion.setScore(BigDecimal.ONE);
        suggestion.setOrder(orderSingle);
        uiMessageService.post(applicationContext.getBean(SuggestionEvent.class,
                                                         inSide.name() + " " + inSelectedItem.symbolProperty().get(),
                                                         suggestion));
    }
    /**
     * Prepare a nice, human-readable rendering of the given market data item.
     *
     * @param inMarketDataItem a <code>MarketDataItem</code> value
     * @return a <code>String</code> value
     */
    private String renderMarketDataItem(MarketDataItem inMarketDataItem)
    {
        Table table = new Table(15,
                                BorderStyle.CLASSIC_COMPATIBLE_WIDE,
                                ShownBorders.ALL,
                                false);
        table.addCell("Symbol",
                      PlatformServices.cellStyleCenterAlign);
        table.addCell("Provider",
                      PlatformServices.cellStyleCenterAlign);
        table.addCell("LastMk",
                      PlatformServices.cellStyleCenterAlign);
        table.addCell("LastPrice",
                      PlatformServices.cellStyleCenterAlign);
        table.addCell("LastQty",
                      PlatformServices.cellStyleCenterAlign);
        table.addCell("BidQty",
                      PlatformServices.cellStyleCenterAlign);
        table.addCell("BidPx",
                      PlatformServices.cellStyleCenterAlign);
        table.addCell("AskPx",
                      PlatformServices.cellStyleCenterAlign);
        table.addCell("AskQty",
                      PlatformServices.cellStyleCenterAlign);
        table.addCell("PrevClose",
                      PlatformServices.cellStyleCenterAlign);
        table.addCell("Open",
                      PlatformServices.cellStyleCenterAlign);
        table.addCell("High",
                      PlatformServices.cellStyleCenterAlign);
        table.addCell("Low",
                      PlatformServices.cellStyleCenterAlign);
        table.addCell("Close",
                      PlatformServices.cellStyleCenterAlign);
        table.addCell("Volume",
                      PlatformServices.cellStyleCenterAlign);
        table.addCell(inMarketDataItem.symbolProperty().get(),
                      PlatformServices.cellStyleLeftAlign);
        table.addCell(inMarketDataItem.providerProperty().get(),
                      PlatformServices.cellStyleLeftAlign);
        table.addCell(inMarketDataItem.tradeExchangeProperty().get(),
                      PlatformServices.cellStyleLeftAlign);
        table.addCell(BigDecimalUtil.renderCurrency(inMarketDataItem.lastPriceProperty().get()),
                      PlatformServices.cellStyleRightAlign);
        table.addCell(BigDecimalUtil.render(inMarketDataItem.lastQuantityProperty().get()),
                      PlatformServices.cellStyleRightAlign);
        table.addCell(BigDecimalUtil.render(inMarketDataItem.bidQuantityProperty().get()),
                      PlatformServices.cellStyleRightAlign);
        table.addCell(BigDecimalUtil.renderCurrency(inMarketDataItem.bidPriceProperty().get()),
                      PlatformServices.cellStyleRightAlign);
        table.addCell(BigDecimalUtil.renderCurrency(inMarketDataItem.askPriceProperty().get()),
                      PlatformServices.cellStyleRightAlign);
        table.addCell(BigDecimalUtil.render(inMarketDataItem.askQuantityProperty().get()),
                      PlatformServices.cellStyleRightAlign);
        table.addCell(BigDecimalUtil.renderCurrency(inMarketDataItem.previousClosePriceProperty().get()),
                      PlatformServices.cellStyleRightAlign);
        table.addCell(BigDecimalUtil.renderCurrency(inMarketDataItem.openPriceProperty().get()),
                      PlatformServices.cellStyleRightAlign);
        table.addCell(BigDecimalUtil.renderCurrency(inMarketDataItem.highPriceProperty().get()),
                      PlatformServices.cellStyleRightAlign);
        table.addCell(BigDecimalUtil.renderCurrency(inMarketDataItem.lowPriceProperty().get()),
                      PlatformServices.cellStyleRightAlign);
        table.addCell(BigDecimalUtil.renderCurrency(inMarketDataItem.closePriceProperty().get()),
                      PlatformServices.cellStyleRightAlign);
        table.addCell(BigDecimalUtil.render(inMarketDataItem.tradeVolumeProperty().get()),
                      PlatformServices.cellStyleRightAlign);
        return table.render();
    }
    /**
     * Create a market data row listener to update a market data item.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private class MarketDataRowListener
            implements MarketDataListener
    {
        /* (non-Javadoc)
         * @see org.marketcetera.marketdata.MarketDataListener#receiveMarketData(org.marketcetera.event.Event)
         */
        @Override
        public void receiveMarketData(Event inEvent)
        {
            if(inEvent instanceof LogEvent) {
                LogEvent logEvent = (LogEvent)inEvent;
                AlertType alertType;
                switch(logEvent.getLevel()) {
                    case INFO:
                    case DEBUG:
                        alertType = AlertType.INFORMATION;
                        break;
                    case ERROR:
                        alertType = AlertType.ERROR;
                        break;
                    case WARN:
                        alertType = AlertType.WARNING;
                        break;
                    default:
                        throw new UnsupportedOperationException("Unexpected log level: " + logEvent.getLevel());
                }
                uiMessageService.post(new NotificationEvent("Market Data Request Failed",
                                                             logEvent.getMessage(),
                                                             alertType));
            } else {
                Platform.runLater(() -> marketDataItem.update(inEvent));
            }
        }
        /**
         * Create a new MarketDataRowListener instance.
         *
         * @param inMarketDataItem a <code>MarketDataItem</code> value
         */
        private MarketDataRowListener(MarketDataItem inMarketDataItem)
        {
            marketDataItem = inMarketDataItem;
        }
        /**
         * market data item to update for this listener
         */
        private final MarketDataItem marketDataItem;
    }
    /**
     * wrench value to indicate all providers are selected
     */
    private static final String ALL_PROVIDERS = "<all providers>";
    /**
     * market data item context menu
     */
    private ContextMenu marketDataContextMenu;
    /**
     * remove market data context menu item
     */
    private MenuItem removeMarketDataMenuItem;
    /**
     * copy market data context menu item
     */
    private MenuItem copyMarketDataMenuItem;
    /**
     * buy market data context menu item
     */
    private MenuItem buyMarketDataMenuItem;
    /**
     * sell market data context menu item
     */
    private MenuItem sellMarketDataMenuItem;
    /**
     * view detail market data menu item
     */
    private MenuItem viewDetailMarketDataMenuItem;
    /**
     * holds the requested symbols by the request id
     */
    private final Map<String,String> symbolsByRequestId = Maps.newHashMap();
    /**
     * market data item table
     */
    private TableView<MarketDataItem> marketDataTable;
    /**
     * provides access to market data services
     */
    private MarketDataClientService marketDataClient;
    /**
     * provides access to trade services
     */
    private TradeClientService tradeClient;
    /**
     * main layout of the view
     */
    private VBox rootLayout;
    /**
     * key in the view properties for the symbols list
     */
    private final String symbolsKey = "SYMBOLS";
    /**
     * layout of the add symbol controls
     */
    private HBox addSymbolLayout;
    /**
     * allows selection of a specific market data provider
     */
    private ComboBox<String> providerComboBox;
    /**
     * add symbol text field
     */
    private TextField addSymbolTextField;
    /**
     * add symbol button
     */
    private Button addSymbolButton;
    /**
     * symbol table column
     */
    private TableColumn<MarketDataItem,String> symbolColumn;
    /**
     * market data provider table column
     */
    private TableColumn<MarketDataItem,String> providerColumn;
    /**
     * trade exchange table column
     */
    private TableColumn<MarketDataItem,String> tradeExchangeColumn;
    /**
     * exec price table column
     */
    private TableColumn<MarketDataItem,BigDecimal> execPriceColumn;
    /**
     * last qty table column
     */
    private TableColumn<MarketDataItem,BigDecimal> lastQtyColumn;
    /**
     * bid qty table column
     */
    private TableColumn<MarketDataItem,BigDecimal> bidQtyColumn;
    /**
     * bid price table column
     */
    private TableColumn<MarketDataItem,BigDecimal> bidPriceColumn;
    /**
     * ask qty table column
     */
    private TableColumn<MarketDataItem,BigDecimal> askQtyColumn;
    /**
     * ask price table column
     */
    private TableColumn<MarketDataItem,BigDecimal> askPriceColumn;
    /**
     * prev close table column
     */
    private TableColumn<MarketDataItem,BigDecimal> prevCloseColumn;
    /**
     * open price table column
     */
    private TableColumn<MarketDataItem,BigDecimal> openPriceColumn;
    /**
     * high price table column
     */
    private TableColumn<MarketDataItem,BigDecimal> highPriceColumn;
    /**
     * low price table column
     */
    private TableColumn<MarketDataItem,BigDecimal> lowPriceColumn;
    /**
     * close price table column
     */
    private TableColumn<MarketDataItem,BigDecimal> closePriceColumn;
    /**
     * volume table column
     */
    private TableColumn<MarketDataItem,BigDecimal> volumeColumn;
    /**
     * global name of this view
     */
    private static final String NAME = "Market Data List View";
}
