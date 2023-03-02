package org.marketcetera.ui.trade.view;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.core.time.TimeFactoryImpl;
import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.OrderCancel;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.OrderStatus;
import org.marketcetera.trade.Side;
import org.marketcetera.trade.TradeMessage;
import org.marketcetera.trade.TradeMessageListener;
import org.marketcetera.trade.TradePermissions;
import org.marketcetera.trade.client.SendOrderResponse;
import org.marketcetera.ui.events.NewWindowEvent;
import org.marketcetera.ui.events.NotificationEvent;
import org.marketcetera.ui.service.SessionUser;
import org.marketcetera.ui.service.trade.TradeClientService;
import org.marketcetera.ui.trade.event.FixMessageDetailsViewEvent;
import org.marketcetera.ui.trade.event.ReplaceOrderEvent;
import org.marketcetera.ui.trade.executionreport.view.FixMessageDisplayType;
import org.marketcetera.ui.view.AbstractContentView;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.quickfix.AnalyzedMessage;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Pagination;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/* $License$ */

/**
 * Provides common behaviors for views that need to display FIX messages in a table.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class AbstractFixMessageView<FixClazz extends FixMessageDisplayType,ClientClazz>
        extends AbstractContentView
        implements TradeMessageListener
{
    /**
     * Validate and start the object.
     */
    @PostConstruct
    public void start()
    {
        // TODO need to preserve column order
        // TODO add page size widget
        // TODO implement sorting
        // TODO need to set initial view size to something reasonable
        tradeClientService = serviceManager.getService(TradeClientService.class);
        tradeClientService.addTradeMessageListener(this);
        mainLayout = new VBox();
        reportsTableView = new TableView<>();
        reportsTableView.setPlaceholder(getPlaceholder());
        TableViewSelectionModel<FixClazz> selectionModel = reportsTableView.getSelectionModel();
        selectionModel.setSelectionMode(SelectionMode.SINGLE);
        initializeColumns(reportsTableView);
        initializeContextMenu(reportsTableView);
        mainScene = new Scene(mainLayout);
        pagination = new Pagination();
        pagination.setPageCount(10);
        pagination.setCurrentPageIndex(1);
        pagination.setMaxPageIndicatorCount(10);
        pagination.currentPageIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> inObservable,
                                Number inOldValue,
                                Number inNewValue)
            {
                currentPage = inNewValue.intValue();
                updateReports();
            }}
        );
        mainLayout.getChildren().addAll(reportsTableView,
                                        pagination);
        currentPage = 0;
        pageSize = 10;
        updateReports();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.TradeMessageListener#receiveTradeMessage(org.marketcetera.trade.TradeMessage)
     */
    @Override
    public void receiveTradeMessage(TradeMessage inTradeMessage)
    {
        SLF4JLoggerProxy.trace(this,
                               "Received {}",
                               inTradeMessage);
        updateReports();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ui.view.ContentView#getScene()
     */
    @Override
    public Scene getScene()
    {
        return mainScene;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ui.view.ContentView#onClose(javafx.stage.WindowEvent)
     */
    @Override
    public void onClose(WindowEvent inEvent)
    {
        tradeClientService.removeTradeMessageListener(this);
    }
    protected void initializeContextMenu(TableView<FixClazz> inTableView)
    {
        reportsTableContextMenu = new ContextMenu();
        cancelOrderMenuItem = new MenuItem("Cancel Order");
        cancelOrderMenuItem.setOnAction(event -> {
            FixClazz report = inTableView.getSelectionModel().getSelectedItem();
            ExecutionReport executionReport = tradeClientService.getLatestExecutionReportForOrderChain(report.getOrderId());
            if(executionReport == null) {
                webMessageService.post(new NotificationEvent("Cancel Order",
                                                             "Unable to cancel " + report.getOrderId() + ": no execution report",
                                                             AlertType.ERROR));
                return;
            }
            OrderCancel orderCancel = Factory.getInstance().createOrderCancel(executionReport);
            SLF4JLoggerProxy.info(this,
                                  "{} sending {}",
                                  SessionUser.getCurrent().getUsername(),
                                  orderCancel);
            SendOrderResponse response = tradeClientService.send(orderCancel);
            if(response.getFailed()) {
                webMessageService.post(new NotificationEvent("Cancel Order",
                                                             "Unable to submit cancel: " + response.getOrderId() + " " + response.getMessage(),
                                                             AlertType.ERROR));
                return;
            } else {
                webMessageService.post(new NotificationEvent("Cancel Order",
                                                             "Cancel order " + response.getOrderId() + " submitted",
                                                             AlertType.INFORMATION));
            }
        });
        replaceOrderMenuItem = new MenuItem("Replace Order");
        replaceOrderMenuItem.setOnAction(event -> {
            FixClazz report = inTableView.getSelectionModel().getSelectedItem();
            ExecutionReport executionReport = tradeClientService.getLatestExecutionReportForOrderChain(report.getOrderId());
            if(executionReport == null) {
                webMessageService.post(new NotificationEvent("Replace Order",
                                                             "Unable to replace " + report.getOrderId() + ": no execution report",
                                                             AlertType.ERROR));
                return;
            }
                String executionReportXml;
                try {
                    executionReportXml = xmlService.marshall(executionReport);
                } catch (Exception e) {
                    webMessageService.post(new NotificationEvent("Replace Order",
                                                                 "Unable to replace " + report.getOrderId() + ": " + PlatformServices.getMessage(e),
                                                                 AlertType.ERROR));
                    return;
                }
                Properties replaceProperties = new Properties();
                replaceProperties.setProperty(ExecutionReport.class.getCanonicalName(),
                                              executionReportXml);
                ReplaceOrderEvent replaceOrderEvent = applicationContext.getBean(ReplaceOrderEvent.class,
                                                                                 executionReport,
                                                                                 replaceProperties);
                webMessageService.post(replaceOrderEvent);
                return;
        });
        SeparatorMenuItem contextMenuSeparator1 = new SeparatorMenuItem();
        viewFixMessageDetailsMenuItem = new MenuItem("View FIX Message Details");
        copyOrderMenuItem = new MenuItem("Copy");
        copyOrderMenuItem.setOnAction(event -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent clipboardContent = new ClipboardContent();
            FixClazz report = inTableView.getSelectionModel().getSelectedItem();
            String output;
            quickfix.Message fixMessage = report.getMessage();
            try {
                output = new AnalyzedMessage(FIXMessageUtil.getDataDictionary(FIXVersion.getFIXVersion(fixMessage)),
                                             fixMessage).toString();
            } catch (Exception e) {
                SLF4JLoggerProxy.warn(getClass(),
                                      e,
                                      "Unable to generate pretty string for {}",
                                      fixMessage);
                output = FIXMessageUtil.toHumanDelimitedString(fixMessage);
            }
            clipboardContent.putString(output);
            clipboard.setContent(clipboardContent);
        });
        viewFixMessageDetailsMenuItem.setOnAction(event -> {
            FixClazz report = inTableView.getSelectionModel().getSelectedItem();
            Properties replaceProperties = new Properties();
            replaceProperties.setProperty(quickfix.Message.class.getCanonicalName(),
                                          report.getMessage().toString());
            FixMessageDetailsViewEvent viewFixMessageDetailsEvent = applicationContext.getBean(FixMessageDetailsViewEvent.class,
                                                                                               report,
                                                                                               replaceProperties);
            webMessageService.post(viewFixMessageDetailsEvent);
        });
        // TODO need Add Report Action
        if(authzHelperService.hasPermission(TradePermissions.SendOrderAction)) { 
            reportsTableContextMenu.getItems().addAll(cancelOrderMenuItem,
                                                      replaceOrderMenuItem,
                                                      contextMenuSeparator1);
        }
        if(authzHelperService.hasPermission(TradePermissions.ViewReportAction)) {
            reportsTableContextMenu.getItems().addAll(viewFixMessageDetailsMenuItem,
                                                      copyOrderMenuItem);
        }
        inTableView.getSelectionModel().selectedItemProperty().addListener((ChangeListener<FixClazz>) (inObservable,inOldValue,inNewValue) -> {
            enableContextMenuItems(inNewValue);
        });
        inTableView.setContextMenu(reportsTableContextMenu);
    }
    protected void enableContextMenuItems(FixClazz inNewValue)
    {
        if(inNewValue == null) {
            return;
        }
        // any report can be viewed or copied
        viewFixMessageDetailsMenuItem.setDisable(false);
        copyOrderMenuItem.setDisable(false);
        // a report can be canceled or replaced only if the status is cancellable
        OrderStatus orderStatus = inNewValue.getOrderStatus();
        cancelOrderMenuItem.setDisable(!orderStatus.isCancellable());
        replaceOrderMenuItem.setDisable(!orderStatus.isCancellable());
    }
    protected TableCell<FixClazz,Date> renderDateCell(TableColumn<FixClazz,Date> tableColumn)
    {
        TableCell<FixClazz,Date> tableCell = new TableCell<>() {
            @Override
            protected void updateItem(Date item, boolean empty)
            {
                super.updateItem(item, empty);
                this.setText(null);
                this.setGraphic(null);
                if(!empty){
                    this.setText(isoDateFormatter.print(new DateTime(item)));
                }
            }
        };
        return tableCell;
    }
    protected TableCell<FixClazz,Instrument> renderInstrumentCell(TableColumn<FixClazz,Instrument> tableColumn)
    {
        TableCell<FixClazz,Instrument> tableCell = new TableCell<>() {
            @Override
            protected void updateItem(Instrument item,
                                      boolean empty)
            {
                super.updateItem(item,
                                 empty);
                this.setText(null);
                this.setGraphic(null);
                if(!empty && item != null){
                    this.setText(item.getFullSymbol());
                }
            }
        };
        return tableCell;
    }
    protected TableCell<FixClazz,BigDecimal> renderNumberCell(TableColumn<FixClazz,BigDecimal> inTableColumn)
    {
        TableCell<FixClazz,BigDecimal> tableCell = new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal inItem,
                                      boolean isEmpty)
            {
                super.updateItem(inItem,
                                 isEmpty);
                this.setText(null);
                this.setGraphic(null);
                if(!isEmpty && inItem != null){
                    // TODO need to set up decimal preferences
                    this.setText(inItem.toPlainString());
                }
            }
        };
        return tableCell;
    }
    protected boolean includeOrderQuantityColumn()
    {
        return true;
    }
    protected boolean includeOrderPriceColumn()
    {
        return true;
    }
    protected boolean includeOriginalOrderIdColumn()
    {
        return true;
    }
    protected boolean includeExchangeColumn()
    {
        return true;
    }
    protected void initializeColumns(TableView<FixClazz> reportsTableView)
    {
        transactTimeColumn = new TableColumn<>("TransactTime"); 
        transactTimeColumn.setCellValueFactory(new PropertyValueFactory<>("transactTime"));
        transactTimeColumn.setCellFactory(tableColumn -> renderDateCell(tableColumn));
        sendingTimeColumn = new TableColumn<>("SendingTime"); 
        sendingTimeColumn.setCellValueFactory(new PropertyValueFactory<>("sendingTime"));
        sendingTimeColumn.setCellFactory(tableColumn -> renderDateCell(tableColumn));
        orderIdColumn = new TableColumn<>("OrdId"); 
        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        origOrderIdColumn = new TableColumn<>("OrigOrdId"); 
        orderStatusColumn = new TableColumn<>("OrdStatus"); 
        orderStatusColumn.setCellValueFactory(new PropertyValueFactory<>("orderStatus"));
        sideColumn = new TableColumn<>("Side"); 
        sideColumn.setCellValueFactory(new PropertyValueFactory<>("side"));
        symbolColumn = new TableColumn<>("Symbol"); 
        symbolColumn.setCellValueFactory(new PropertyValueFactory<>("instrument"));
        symbolColumn.setCellFactory(tableColumn -> renderInstrumentCell(tableColumn));
        ordQtyColumn = new TableColumn<>("OrdQty"); 
        cumQtyColumn = new TableColumn<>("CumQty"); 
        cumQtyColumn.setCellValueFactory(new PropertyValueFactory<>("cumulativeQuantity"));
        cumQtyColumn.setCellFactory(tableColumn -> renderNumberCell(tableColumn));
        leavesQtyColumn = new TableColumn<>("LeavesQty"); 
        leavesQtyColumn.setCellValueFactory(new PropertyValueFactory<>("leavesQuantity"));
        leavesQtyColumn.setCellFactory(tableColumn -> renderNumberCell(tableColumn));
        orderPriceColumn = new TableColumn<>("OrderPx"); 
        averagePriceColumn = new TableColumn<>("AvgPx"); 
        averagePriceColumn.setCellValueFactory(new PropertyValueFactory<>("averagePrice"));
        averagePriceColumn.setCellFactory(tableColumn -> renderNumberCell(tableColumn));
        accountColumn = new TableColumn<>("Account"); 
        accountColumn.setCellValueFactory(new PropertyValueFactory<>("account"));
        lastQtyColumn = new TableColumn<>("LastQty"); 
        lastQtyColumn.setCellValueFactory(new PropertyValueFactory<>("lastQuantity"));
        lastQtyColumn.setCellFactory(tableColumn -> renderNumberCell(tableColumn));
        lastPriceColumn = new TableColumn<>("LastPx"); 
        lastPriceColumn.setCellValueFactory(new PropertyValueFactory<>("lastPrice"));
        lastPriceColumn.setCellFactory(tableColumn -> renderNumberCell(tableColumn));
        exchangeColumn = new TableColumn<>("Exchange"); 
        brokerIdColumn = new TableColumn<>("BrokerId"); 
        brokerIdColumn.setCellValueFactory(new PropertyValueFactory<>("brokerId"));
        traderColumn = new TableColumn<>("Trader"); 
        traderColumn.setCellValueFactory(new PropertyValueFactory<>("trader"));
        reportsTableView.getColumns().add(transactTimeColumn);
        reportsTableView.getColumns().add(sendingTimeColumn);
        reportsTableView.getColumns().add(orderIdColumn);
        if(includeOriginalOrderIdColumn()) {
            origOrderIdColumn.setCellValueFactory(new PropertyValueFactory<>("originalOrderId"));
            reportsTableView.getColumns().add(origOrderIdColumn);
        }
        reportsTableView.getColumns().add(orderStatusColumn);
        reportsTableView.getColumns().add(sideColumn);
        reportsTableView.getColumns().add(symbolColumn);
        if(includeOrderQuantityColumn()) {
            ordQtyColumn.setCellValueFactory(new PropertyValueFactory<>("orderQuantity"));
            ordQtyColumn.setCellFactory(tableColumn -> renderNumberCell(tableColumn));
            reportsTableView.getColumns().add(ordQtyColumn);
        }
        reportsTableView.getColumns().add(cumQtyColumn);
        reportsTableView.getColumns().add(leavesQtyColumn);
        if(includeOrderPriceColumn()) {
            orderPriceColumn.setCellValueFactory(new PropertyValueFactory<>("orderPrice"));
            orderPriceColumn.setCellFactory(tableColumn -> renderNumberCell(tableColumn));
            reportsTableView.getColumns().add(orderPriceColumn);
        }
        reportsTableView.getColumns().add(averagePriceColumn);
        reportsTableView.getColumns().add(accountColumn);
        reportsTableView.getColumns().add(lastQtyColumn);
        reportsTableView.getColumns().add(lastPriceColumn);
        if(includeExchangeColumn()) {
            exchangeColumn.setCellValueFactory(new PropertyValueFactory<>("exchange"));
            reportsTableView.getColumns().add(exchangeColumn);
        }
        reportsTableView.getColumns().add(brokerIdColumn);
        reportsTableView.getColumns().add(traderColumn);
    }
    protected void updateReports()
    {
        CollectionPageResponse<ClientClazz> response = getClientReports(new PageRequest(currentPage,
                                                                                        pageSize));
        // TODO figure out if the contents have changed?
        Platform.runLater(new Runnable() {
            @Override
            public void run()
            {
                pagination.setPageCount(response.getTotalPages());
                pagination.setCurrentPageIndex(currentPage);
                reportsTableView.getItems().clear();
                for(ClientClazz report : response.getElements()) {
                    reportsTableView.getItems().add(createFixDisplayObject(report));
                }
            }}
        );
    }
    protected abstract CollectionPageResponse<ClientClazz> getClientReports(PageRequest inPageRequest);
    protected abstract FixClazz createFixDisplayObject(ClientClazz inClientClazz);
    protected Node getPlaceholder()
    {
        return new Label("no reports");
    }
    /**
     * Create a new AbstractFixMessageView instance.
     *
     * @param inParentWindow a <code>Stage</code> value
     * @param inNewWindowEvent a <code>NewWindowEvent</code> value
     * @param inViewProperties a <code>Properties</code> value
     */
    protected AbstractFixMessageView(Stage inParentWindow,
                                     NewWindowEvent inEvent,
                                     Properties inViewProperties)
    {
        super(inParentWindow,
              inEvent,
              inViewProperties);
    }
    protected static final DateTimeFormatter isoDateFormatter = TimeFactoryImpl.FULL_MILLISECONDS;
    protected Scene mainScene;
    protected TradeClientService tradeClientService;
    protected int currentPage;
    protected int pageSize;
    protected VBox mainLayout;
    protected Pagination pagination;
    protected TableView<FixClazz> reportsTableView;
    protected ContextMenu reportsTableContextMenu;
    protected TableColumn<FixClazz,Date> transactTimeColumn; 
    protected TableColumn<FixClazz,Date> sendingTimeColumn; 
    protected TableColumn<FixClazz,OrderID> orderIdColumn; 
    protected TableColumn<FixClazz,OrderID> origOrderIdColumn; 
    protected TableColumn<FixClazz,OrderStatus> orderStatusColumn; 
    protected TableColumn<FixClazz,Side> sideColumn; 
    protected TableColumn<FixClazz,Instrument> symbolColumn; 
    protected TableColumn<FixClazz,BigDecimal> ordQtyColumn; 
    protected TableColumn<FixClazz,BigDecimal> cumQtyColumn; 
    protected TableColumn<FixClazz,BigDecimal> leavesQtyColumn; 
    protected TableColumn<FixClazz,BigDecimal> orderPriceColumn; 
    protected TableColumn<FixClazz,BigDecimal> averagePriceColumn; 
    protected TableColumn<FixClazz,String> accountColumn; 
    protected TableColumn<FixClazz,BigDecimal> lastQtyColumn; 
    protected TableColumn<FixClazz,BigDecimal> lastPriceColumn; 
    protected TableColumn<FixClazz,String> exchangeColumn; 
    protected TableColumn<FixClazz,BrokerID> brokerIdColumn; 
    protected TableColumn<FixClazz,String> traderColumn; 
    protected MenuItem cancelOrderMenuItem;
    protected MenuItem replaceOrderMenuItem;
    protected MenuItem viewFixMessageDetailsMenuItem;
    protected MenuItem copyOrderMenuItem;
}
