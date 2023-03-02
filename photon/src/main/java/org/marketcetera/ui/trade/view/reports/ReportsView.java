package org.marketcetera.ui.trade.view.reports;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.core.XmlService;
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
import org.marketcetera.trade.Report;
import org.marketcetera.trade.ReportType;
import org.marketcetera.trade.Side;
import org.marketcetera.trade.TradeMessage;
import org.marketcetera.trade.TradeMessageListener;
import org.marketcetera.trade.TradePermissions;
import org.marketcetera.trade.client.SendOrderResponse;
import org.marketcetera.ui.events.NewWindowEvent;
import org.marketcetera.ui.events.NotificationEvent;
import org.marketcetera.ui.service.AuthorizationHelperService;
import org.marketcetera.ui.service.ServiceManager;
import org.marketcetera.ui.service.SessionUser;
import org.marketcetera.ui.service.WebMessageService;
import org.marketcetera.ui.service.trade.TradeClientService;
import org.marketcetera.ui.trade.event.FixMessageDetailsViewEvent;
import org.marketcetera.ui.trade.event.ReplaceOrderEvent;
import org.marketcetera.ui.view.AbstractContentView;
import org.marketcetera.ui.view.ContentView;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.quickfix.AnalyzedMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DialogPane;
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
 * Provides a view for Order Tickets.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ReportsView
        extends AbstractContentView
        implements ContentView,TradeMessageListener
{
    /* (non-Javadoc)
     * @see org.marketcetera.ui.view.ContentView#onClose(javafx.stage.WindowEvent)
     */
    @Override
    public void onClose(WindowEvent inEvent)
    {
        tradeClientService.removeTradeMessageListener(this);
    }
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
        reportsTableView.setPlaceholder(new Label("no reports"));
        TableViewSelectionModel<DisplayReport> selectionModel = reportsTableView.getSelectionModel();
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
    private void initializeContextMenu(TableView<DisplayReport> inTableView)
    {
        reportsTableContextMenu = new ContextMenu();
        MenuItem cancelOrderMenuItem = new MenuItem("Cancel Order");
        cancelOrderMenuItem.setOnAction(event -> {
            DisplayReport report = inTableView.getSelectionModel().getSelectedItem();
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
        MenuItem replaceOrderMenuItem = new MenuItem("Replace Order");
        replaceOrderMenuItem.setOnAction(event -> {
            DisplayReport report = inTableView.getSelectionModel().getSelectedItem();
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
        MenuItem viewFixMessageDetailsMenuItem = new MenuItem("View FIX Message Details");
        MenuItem copyOrderMenuItem = new MenuItem("Copy");
        copyOrderMenuItem.setOnAction(event -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent clipboardContent = new ClipboardContent();
            DisplayReport report = inTableView.getSelectionModel().getSelectedItem();
            String output;
            quickfix.Message fixMessage = report.getMessage();
            try {
                output = new AnalyzedMessage(FIXMessageUtil.getDataDictionary(FIXVersion.getFIXVersion(fixMessage)),
                                             fixMessage).toString();
            } catch (Exception e) {
                SLF4JLoggerProxy.warn(ReportsView.this,
                                      e,
                                      "Unable to generate pretty string for {}",
                                      fixMessage);
                output = FIXMessageUtil.toHumanDelimitedString(fixMessage);
            }
            clipboardContent.putString(output);
            clipboard.setContent(clipboardContent);
        });
        viewFixMessageDetailsMenuItem.setOnAction(event -> {
            DisplayReport report = inTableView.getSelectionModel().getSelectedItem();
            Properties replaceProperties = new Properties();
            replaceProperties.setProperty(quickfix.Message.class.getCanonicalName(),
                                          report.getMessage().toString());
            FixMessageDetailsViewEvent viewFixMessageDetailsEvent = applicationContext.getBean(FixMessageDetailsViewEvent.class,
                                                                                               report,
                                                                                               replaceProperties);
            webMessageService.post(viewFixMessageDetailsEvent);
        });
        SeparatorMenuItem contextMenuSeparator2 = new SeparatorMenuItem();
        MenuItem deleteReportMenuItem = new MenuItem("Delete Report");
        deleteReportMenuItem.setOnAction(event -> {
            DisplayReport report = inTableView.getSelectionModel().getSelectedItem();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Report " + report.getReportID());
            alert.setContentText("Deleting a report may modify positions, continue?");
            ButtonType okButton = new ButtonType("Ok",
                                                 ButtonBar.ButtonData.OK_DONE);
            ButtonType cancelButton = new ButtonType("Cancel",
                                                     ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(okButton,
                                          cancelButton);
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStylesheets().clear();
            dialogPane.getStylesheets().add("dark-mode.css");
            alert.showAndWait().ifPresent(type -> {
                if (type == okButton) {
                    try {
                        tradeClientService.deleteReport(report.getReportID());
                        updateReports();
                        webMessageService.post(new NotificationEvent("Delete Report",
                                                                     "Report " + report.getReportID() + " deleted",
                                                                     AlertType.INFORMATION));
                    } catch (Exception e) {
                        SLF4JLoggerProxy.warn(this,
                                              e,
                                              "Unable to delete {}",
                                              report);
                        webMessageService.post(new NotificationEvent("Delete Report",
                                                                     "Report " + report.getReportID() + " not deleted: " + PlatformServices.getMessage(e),
                                                                     AlertType.ERROR));
                    }
                } else {
                    return;
                }
            });
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
        if(authzHelperService.hasPermission(TradePermissions.DeleteReportAction)) {
            reportsTableContextMenu.getItems().addAll(contextMenuSeparator2,
                                                      deleteReportMenuItem);
        }
        inTableView.getSelectionModel().selectedItemProperty().addListener((ChangeListener<DisplayReport>) (inObservable,inOldValue,inNewValue) -> {
            if(inNewValue == null) {
                return;
            }
            // enable/disable menu selections based on selected report
            // any report can be deleted, may ${DEITY} have mercy on your soul
            deleteReportMenuItem.setDisable(false);
            // any report can be viewed or copied
            viewFixMessageDetailsMenuItem.setDisable(false);
            copyOrderMenuItem.setDisable(false);
            // a report can be canceled or replaced only if the status is cancellable
            OrderStatus orderStatus = inNewValue.getOrderStatus();
            cancelOrderMenuItem.setDisable(!orderStatus.isCancellable());
            replaceOrderMenuItem.setDisable(!orderStatus.isCancellable());
        });
        inTableView.setContextMenu(reportsTableContextMenu);
    }
    private ContextMenu reportsTableContextMenu;
    private TableCell<DisplayReport,Date> renderDateCell(TableColumn<DisplayReport,Date> tableColumn)
    {
        TableCell<DisplayReport,Date> tableCell = new TableCell<>() {
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
    private TableCell<DisplayReport,Instrument> renderInstrumentCell(TableColumn<DisplayReport,Instrument> tableColumn)
    {
        TableCell<DisplayReport,Instrument> tableCell = new TableCell<>() {
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
    private final DateTimeFormatter isoDateFormatter = TimeFactoryImpl.FULL_MILLISECONDS;
    private void initializeColumns(TableView<DisplayReport> reportsTableView)
    {
        TableColumn<DisplayReport,Date> transactTimeColumn = new TableColumn<>("TransactTime"); 
        transactTimeColumn.setCellValueFactory(new PropertyValueFactory<>("transactTime"));
        transactTimeColumn.setCellFactory(tableColumn -> renderDateCell(tableColumn));
        TableColumn<DisplayReport,Date> sendingTimeColumn = new TableColumn<>("SendingTime"); 
        sendingTimeColumn.setCellValueFactory(new PropertyValueFactory<>("sendingTime"));
        sendingTimeColumn.setCellFactory(tableColumn -> renderDateCell(tableColumn));
        TableColumn<DisplayReport,ReportType> msgTypeColumn = new TableColumn<>("MsgType"); 
        msgTypeColumn.setCellValueFactory(new PropertyValueFactory<>("msgType"));
        TableColumn<DisplayReport,OrderID> orderIdColumn = new TableColumn<>("OrdId"); 
        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        TableColumn<DisplayReport,OrderID> origOrderIdColumn = new TableColumn<>("OrigOrdId"); 
        origOrderIdColumn.setCellValueFactory(new PropertyValueFactory<>("originalOrderId"));
        TableColumn<DisplayReport,OrderStatus> orderStatusColumn = new TableColumn<>("OrdStatus"); 
        orderStatusColumn.setCellValueFactory(new PropertyValueFactory<>("orderStatus"));
        TableColumn<DisplayReport,Side> sideColumn = new TableColumn<>("Side"); 
        sideColumn.setCellValueFactory(new PropertyValueFactory<>("side"));
        TableColumn<DisplayReport,Instrument> symbolColumn = new TableColumn<>("Symbol"); 
        symbolColumn.setCellValueFactory(new PropertyValueFactory<>("instrument"));
        symbolColumn.setCellFactory(tableColumn -> renderInstrumentCell(tableColumn));
        TableColumn<DisplayReport,BigDecimal> ordQtyColumn = new TableColumn<>("OrdQty"); 
        ordQtyColumn.setCellValueFactory(new PropertyValueFactory<>("orderQuantity"));
        TableColumn<DisplayReport,BigDecimal> cumQtyColumn = new TableColumn<>("CumQty"); 
        cumQtyColumn.setCellValueFactory(new PropertyValueFactory<>("cumulativeQuantity"));
        TableColumn<DisplayReport,BigDecimal> leavesQtyColumn = new TableColumn<>("LeavesQty"); 
        leavesQtyColumn.setCellValueFactory(new PropertyValueFactory<>("leavesQuantity"));
        TableColumn<DisplayReport,BigDecimal> orderPriceColumn = new TableColumn<>("OrderPx"); 
        orderPriceColumn.setCellValueFactory(new PropertyValueFactory<>("orderPrice"));
        TableColumn<DisplayReport,BigDecimal> averagePriceColumn = new TableColumn<>("AvgPx"); 
        averagePriceColumn.setCellValueFactory(new PropertyValueFactory<>("averagePrice"));
        TableColumn<DisplayReport,String> accountColumn = new TableColumn<>("Account"); 
        accountColumn.setCellValueFactory(new PropertyValueFactory<>("account"));
        TableColumn<DisplayReport,BigDecimal> lastQtyColumn = new TableColumn<>("LastQty"); 
        lastQtyColumn.setCellValueFactory(new PropertyValueFactory<>("lastQuantity"));
        TableColumn<DisplayReport,BigDecimal> lastPriceColumn = new TableColumn<>("LastPx"); 
        lastPriceColumn.setCellValueFactory(new PropertyValueFactory<>("lastPrice"));
        TableColumn<DisplayReport,String> exchangeColumn = new TableColumn<>("Exchange"); 
        exchangeColumn.setCellValueFactory(new PropertyValueFactory<>("exchange"));
        TableColumn<DisplayReport,BrokerID> brokerIdColumn = new TableColumn<>("BrokerId"); 
        brokerIdColumn.setCellValueFactory(new PropertyValueFactory<>("brokerID"));
        TableColumn<DisplayReport,String> traderColumn = new TableColumn<>("Trader"); 
        traderColumn.setCellValueFactory(new PropertyValueFactory<>("trader"));
        reportsTableView.getColumns().add(transactTimeColumn);
        reportsTableView.getColumns().add(sendingTimeColumn);
        reportsTableView.getColumns().add(msgTypeColumn);
        reportsTableView.getColumns().add(orderIdColumn);
        reportsTableView.getColumns().add(origOrderIdColumn);
        reportsTableView.getColumns().add(orderStatusColumn);
        reportsTableView.getColumns().add(sideColumn);
        reportsTableView.getColumns().add(symbolColumn);
        reportsTableView.getColumns().add(ordQtyColumn);
        reportsTableView.getColumns().add(cumQtyColumn);
        reportsTableView.getColumns().add(leavesQtyColumn);
        reportsTableView.getColumns().add(orderPriceColumn);
        reportsTableView.getColumns().add(averagePriceColumn);
        reportsTableView.getColumns().add(accountColumn);
        reportsTableView.getColumns().add(lastQtyColumn);
        reportsTableView.getColumns().add(lastPriceColumn);
        reportsTableView.getColumns().add(exchangeColumn);
        reportsTableView.getColumns().add(brokerIdColumn);
        reportsTableView.getColumns().add(traderColumn);
    }
    private void updateReports()
    {
        CollectionPageResponse<Report> response = tradeClientService.getReports(new PageRequest(currentPage,
                                                                                                pageSize));
        // TODO figure out if the contents have changed?
        Platform.runLater(new Runnable() {
            @Override
            public void run()
            {
                pagination.setPageCount(response.getTotalPages());
                pagination.setCurrentPageIndex(currentPage);
                reportsTableView.getItems().clear();
                for(Report report : response.getElements()) {
                    reportsTableView.getItems().add(new DisplayReport(report));
                }
            }}
        );
    }
    private int currentPage;
    private int pageSize;
    private VBox mainLayout;
    private Pagination pagination;
    private TableView<DisplayReport> reportsTableView;
    private TradeClientService tradeClientService;
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
     * @see org.marketcetera.ui.view.ContentView#getViewName()
     */
    @Override
    public String getViewName()
    {
        return NAME;
    }
    /**
     * Create a new ReportsView instance.
     *
     * @param inParent a <code>Stage</code> value
     * @param inNewWindowEvent a <code>NewWindowEvent</code> value
     * @param inProperties a <code>Properties</code> value
     */
    public ReportsView(Stage inParent,
                       NewWindowEvent inEvent,
                       Properties inProperties)
    {
        super(inParent,
              inEvent,
              inProperties);
    }
    /**
     * global name of this view
     */
    private static final String NAME = "FIX Messages View";
    /**
     * main scene object
     */
    private Scene mainScene;
    /**
     * provides access to the running context
     */
    @Autowired
    private ApplicationContext applicationContext;
    /**
     * provides access to XML services
     */
    @Autowired
    private XmlService xmlService;
    /**
     * web message service value
     */
    @Autowired
    private WebMessageService webMessageService;
    /**
     * provides access to client services
     */
    @Autowired
    private ServiceManager serviceManager;
    /**
     * helps determine if authorization is granted for actions
     */
    @Autowired
    protected AuthorizationHelperService authzHelperService;
}
