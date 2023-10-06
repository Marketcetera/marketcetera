package org.marketcetera.ui.trade.view;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.core.time.TimeFactoryImpl;
import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;
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
import org.marketcetera.ui.PhotonServices;
import org.marketcetera.ui.events.NewWindowEvent;
import org.marketcetera.ui.events.NotificationEvent;
import org.marketcetera.ui.service.SessionUser;
import org.marketcetera.ui.trade.event.FixMessageDetailsViewEvent;
import org.marketcetera.ui.trade.event.ReplaceOrderEvent;
import org.marketcetera.ui.trade.executionreport.view.FixMessageDisplayType;
import org.marketcetera.ui.trade.service.TradeClientService;
import org.marketcetera.ui.view.AbstractContentView;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Value;

import com.google.common.collect.Lists;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Pagination;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

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
{
    /* (non-Javadoc)
     * @see org.marketcetera.ui.view.AbstractContentView#onStart()
     */
    @Override
    protected void onStart()
    {
        // TODO need to preserve column order
        // TODO add page size widget
        // TODO implement sorting
        tradeClientService = serviceManager.getService(TradeClientService.class);
        initializeTradeMessageListener();
        mainLayout = new VBox();
        aboveTableLayout = new FlowPane();
        reportsTableView = new TableView<>();
        reportsTableView.setPlaceholder(getPlaceholder());
        TableViewSelectionModel<FixClazz> selectionModel = reportsTableView.getSelectionModel();
        selectionModel.setSelectionMode(getTableSelectionMode());
        initializeColumns(reportsTableView);
        initializeContextMenu(reportsTableView);
        reportsTableView.setRowFactory(tableView -> new TableRow<FixClazz>() {
            /* (non-Javadoc)
             * @see javafx.scene.control.Cell#updateItem(java.lang.Object, boolean)
             */
            @Override
            protected void updateItem(FixClazz inItem,
                                      boolean inEmpty)
            {
                super.updateItem(inItem,
                                 inEmpty);
                if(inItem == null) {
                    setStyle("");
                } else if(inItem.isFillProperty().get()) {
                    setStyle(tradeHightlightCss);
                    styleUpdateTimerService.schedule(new Runnable() {
                        @Override
                        public void run()
                        {
                            try {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run()
                                    {
                                        setStyle("");
                                    }
                                });
                            } catch (Exception e) {
                                SLF4JLoggerProxy.warn(AbstractFixMessageView.this,
                                                      e);
                            }
                        }},tradeHighlightDuration,TimeUnit.MILLISECONDS);
                } else if(inItem.isCancelProperty().get()) {
                    setStyle(cancelHightlightCss);
                    styleUpdateTimerService.schedule(new Runnable() {
                        @Override
                        public void run()
                        {
                            try {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run()
                                    {
                                        setStyle("");
                                    }
                                });
                            } catch (Exception e) {
                                SLF4JLoggerProxy.warn(AbstractFixMessageView.this,
                                                      e);
                            }
                        }},cancelHighlightDuration,TimeUnit.MILLISECONDS);
                } else {
                    setStyle("");
                }
            }
        });
        pagination = new Pagination();
        pagination.setPageCount(1);
        pagination.setCurrentPageIndex(1);
        pagination.setMaxPageIndicatorCount(1);
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
        reportsTableView.prefWidthProperty().bind(getParentWindow().widthProperty());
        reportsTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        mainLayout.prefHeightProperty().bind(getParentWindow().heightProperty());
        mainLayout.getChildren().addAll(aboveTableLayout,
                                        reportsTableView,
                                        pagination);
        currentPage = 0;
        pageSize = 10;
        updateReports();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ui.view.ContentView#getMainLayout()
     */
    @Override
    public Region getMainLayout()
    {
        return mainLayout;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ui.view.ContentView#onClose()
     */
    @Override
    public void onClose()
    {
        if(tradeMessageListener != null) {
            tradeClientService.removeTradeMessageListener(tradeMessageListener);
        }
        super.onClose();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ui.view.AbstractContentView#onClientConnect()
     */
    @Override
    protected void onClientConnect()
    {
        updateReports();
        initializeTradeMessageListener();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ui.view.AbstractContentView#onClientDisconnect()
     */
    @Override
    protected void onClientDisconnect()
    {
        Platform.runLater(() -> {
            reportsTableView.getItems().clear();
        });
    }
    /**
     * Get the selection model of the main reports table.
     *
     * @return a <code>SelectionMode</code> value
     */
    protected SelectionMode getTableSelectionMode()
    {
        return SelectionMode.SINGLE;
    }
    /**
     * Create the trade message listener value.
     */
    protected void initializeTradeMessageListener()
    {
        tradeMessageListener =  new TradeMessageListener() {
            @Override
            public void receiveTradeMessage(TradeMessage inTradeMessage)
            {
                SLF4JLoggerProxy.trace(getClass(),
                                       "{} received {}",
                                       viewName,
                                       inTradeMessage);
                updateReports(inTradeMessage);
            }
        };
        tradeClientService.addTradeMessageListener(tradeMessageListener);
    }
    /**
     * Cancel the order from the given report.
     *
     * @param inReport a <code>FixClazz</code> value
     */
    protected void cancelOrder(FixClazz inReport)
    {
        ExecutionReport executionReport = tradeClientService.getLatestExecutionReportForOrderChain(inReport.getOrderId());
        if(executionReport == null) {
            uiMessageService.post(new NotificationEvent("Cancel Order",
                                                         "Unable to cancel " + inReport.getOrderId() + ": no execution report",
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
            uiMessageService.post(new NotificationEvent("Cancel Order",
                                                        "Unable to submit cancel: " + response.getOrderId() + " " + response.getMessage(),
                                                        AlertType.ERROR));
            return;
        } else {
            uiMessageService.post(new NotificationEvent("Cancel Order",
                                                        "Cancel order " + response.getOrderId() + " submitted",
                                                        AlertType.INFORMATION));
        }
    }
    /**
     * Get the items currently selected, if any.
     *
     * @return a <code>Collection&lt;FixClazz&gt;</code> value
     */
    protected Collection<FixClazz> getSelectedItems()
    {
        switch(reportsTableView.getSelectionModel().getSelectionMode()) {
            case MULTIPLE:
                return reportsTableView.getSelectionModel().getSelectedItems();
            case SINGLE:
                FixClazz report = reportsTableView.getSelectionModel().getSelectedItem();
                if(report == null) {
                    return Collections.emptyList();
                }
                return Lists.newArrayList(report);
            default:
                throw new UnsupportedOperationException("Unexpected selection mode: " + reportsTableView.getSelectionModel().getSelectionMode());
        }
    }
    /**
     * Initialize the context menu for the FIX table.
     *
     * @param inTableView a <code>TableView&lt;FixClazz&gt;</code> value
     */
    protected void initializeContextMenu(TableView<FixClazz> inTableView)
    {
        reportsTableContextMenu = new ContextMenu();
        cancelOrderMenuItem = new MenuItem("Cancel Order");
        cancelOrderMenuItem.setOnAction(event -> {
            Collection<FixClazz> selectedItems = getSelectedItems();
            for(FixClazz report : selectedItems) {
                cancelOrder(report);
            }
        });
        replaceOrderMenuItem = new MenuItem("Replace Order");
        replaceOrderMenuItem.setOnAction(event -> {
            Collection<FixClazz> selectedItems = getSelectedItems();
            for(FixClazz report : selectedItems) {
                ExecutionReport executionReport = tradeClientService.getLatestExecutionReportForOrderChain(report.getOrderId());
                if(executionReport == null) {
                    uiMessageService.post(new NotificationEvent("Replace Order",
                                                                "Unable to replace " + report.getOrderId() + ": no execution report",
                                                                AlertType.ERROR));
                    continue;
                }
                String executionReportXml;
                try {
                    executionReportXml = xmlService.marshall(executionReport);
                } catch (Exception e) {
                    uiMessageService.post(new NotificationEvent("Replace Order",
                                                                "Unable to replace " + report.getOrderId() + ": " + PlatformServices.getMessage(e),
                                                                AlertType.ERROR));
                    continue;
                }
                Properties replaceProperties = new Properties();
                replaceProperties.setProperty(ExecutionReport.class.getCanonicalName(),
                                              executionReportXml);
                ReplaceOrderEvent replaceOrderEvent = applicationContext.getBean(ReplaceOrderEvent.class,
                                                                                 executionReport,
                                                                                 replaceProperties);
                uiMessageService.post(replaceOrderEvent);
            }
        });
        SeparatorMenuItem contextMenuSeparator1 = new SeparatorMenuItem();
        viewFixMessageDetailsMenuItem = new MenuItem("View FIX Message Details");
        copyOrderMenuItem = new MenuItem("Copy");
        copyOrderMenuItem.setOnAction(event -> {
            try {
                Clipboard clipboard = Clipboard.getSystemClipboard();
                ClipboardContent clipboardContent = new ClipboardContent();
                Collection<FixClazz> selectedItems = getSelectedItems();
                StringBuilder output = new StringBuilder();
                boolean commaNeeded = false;
                for(TableColumn<FixClazz,?> column : reportsTableView.getColumns()) {
                    if(commaNeeded) {
                        output.append(',');
                    }
                    output.append(column.getText());
                    commaNeeded = true;
                }
                output.append(StringUtils.LF);
                // need to be able to retrieve the field from the column header name
                for(FixClazz report : selectedItems) {
                    commaNeeded = false;
                    for(TableColumn<FixClazz,?> column : reportsTableView.getColumns()) {
                        if(commaNeeded) {
                            output.append(',');
                        }
                        try {
                            output.append(PhotonServices.getFieldValue(report,column.getText()));
                        } catch (Exception e) {
                            SLF4JLoggerProxy.warn(AbstractFixMessageView.this,
                                                  e);
                        }
                        commaNeeded = true;
                    }
                    output.append(StringUtils.LF);
                }
                clipboardContent.putString(output.toString());
                clipboard.setContent(clipboardContent);
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        viewFixMessageDetailsMenuItem.setOnAction(event -> {
            Collection<FixClazz> selectedItems = getSelectedItems();
            for(FixClazz report : selectedItems) {
                Properties replaceProperties = new Properties();
                replaceProperties.setProperty(quickfix.Message.class.getCanonicalName(),
                                              report.getMessage().toString());
                FixMessageDetailsViewEvent viewFixMessageDetailsEvent = applicationContext.getBean(FixMessageDetailsViewEvent.class,
                                                                                                   report,
                                                                                                   replaceProperties);
                uiMessageService.post(viewFixMessageDetailsEvent);
            }
        });
        viewFixMessageDetailsMenuItem.disableProperty().set(!allowViewFixMessageDetailsContextMenuAction());
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
            enableContextMenuItems(getSelectedItems());
        });
        inTableView.setContextMenu(reportsTableContextMenu);
    }
    /**
     * Indicates if the <code>View Fix Message Details</code> action should be enabled by default.
     *
     * @return a <code>boolean</code> value
     */
    protected boolean allowViewFixMessageDetailsContextMenuAction()
    {
        return true;
    }
    /**
     * Enable or disable context menu items based on the given selected row item.
     *
     * @param inSelectedItems a <code>Collection&lt;FixClazz&gt;</code> value
     */
    protected void enableContextMenuItems(Collection<FixClazz> inSelectedItems)
    {
        if(inSelectedItems == null || inSelectedItems.isEmpty()) {
            cancelOrderMenuItem.setDisable(true);
            replaceOrderMenuItem.setDisable(true);
            viewFixMessageDetailsMenuItem.setDisable(true);
            copyOrderMenuItem.setDisable(true);
            return;
        }
        // any report can be viewed or copied unless forbidden by the subclass
        viewFixMessageDetailsMenuItem.setDisable(!allowViewFixMessageDetailsContextMenuAction());
        copyOrderMenuItem.setDisable(false);
        // enable the cancel and replace options only if all the reports in the selection are cancellable
        boolean disable = false;
        if(includeOrderStatusColumn()) {
            for(FixClazz report : inSelectedItems) {
                OrderStatus orderStatus = report.getOrderStatus();
                if(!orderStatus.isCancellable()) {
                    disable = true;
                    break;
                }
            }
        } else {
            disable = true;
        }
        cancelOrderMenuItem.setDisable(disable);
        replaceOrderMenuItem.setDisable(disable);
    }
    /**
     * Get the layout for above-the-table controls.
     * 
     * <p>Subclasses can add controls to this layout as desired.</p>
     *
     * @return a <code>FlowPane</code> value
     */
    protected FlowPane getAboveTableLayout()
    {
        return aboveTableLayout;
    }
    /**
     * Render the given column as a Date cell.
     *
     * @param inTableColumn
     * @return a <code>TableCell&lt;FixClazz,Date&gt;</code> value
     */
    protected TableCell<FixClazz,Date> renderDateCell(TableColumn<FixClazz,Date> inTableColumn)
    {
        TableCell<FixClazz,Date> tableCell = new TableCell<>() {
            @Override
            protected void updateItem(Date inItem,
                                      boolean isEmpty)
            {
                super.updateItem(inItem,
                                 isEmpty);
                this.setText(null);
                this.setGraphic(null);
                if(!isEmpty){
                    this.setText(isoDateFormatter.print(new DateTime(inItem)));
                }
            }
        };
        return tableCell;
    }
    /**
     * Render an order price cell.
     *
     * @param inTableColumn a <code>TableColumn&lt;FixClazz,BigDecimal&gt;</code> value
     * @return a <code>TableCell&lt;FixClazz,BigDecimal&gt;</code> value
     */
    protected TableCell<FixClazz,BigDecimal> renderOrderPriceCell(TableColumn<FixClazz,BigDecimal> inTableColumn)
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
                if(!isEmpty) {
                    if(inItem == null) {
                        this.setText("MKT");
                    } else {
                        // TODO need to set up decimal preferences
                        this.setText(inItem.toPlainString());
                    }
                }
            }
        };
        return tableCell;
    }
    /**
     * Render the given column as a regular numeric (not currency) cell.
     *
     * @param inTableColumn a <code>TableColumn&lt;FixClazz,BigDecimal&gt;</code> value
     * @return a <code>TableCell&lt;FixClazz,BigDecimal&gt;</code> value
     */
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
    /**
     * Indicate if the <code>OrderQuantity</code> column should be included.
     *
     * @return a <code>boolean</code> value
     */
    protected boolean includeOrderQuantityColumn()
    {
        return true;
    }
    /**
     * Indicate if the <code>LeavesQuantity</code> column should be included.
     *
     * @return a <code>boolean</code> value
     */
    protected boolean includeLeavesQuantityColumn()
    {
        return true;
    }
    /**
     * Indicate if the <code>LastQuantity</code> column should be included.
     *
     * @return a <code>boolean</code> value
     */
    protected boolean includeLastQuantityColumn()
    {
        return true;
    }
    /**
     * Indicate if the <code>LastPrice</code> column should be included.
     *
     * @return a <code>boolean</code> value
     */
    protected boolean includeLastPriceColumn()
    {
        return true;
    }
    /**
     * Indicate if the <code>OrderPrice</code> column should be included.
     *
     * @return a <code>boolean</code> value
     */
    protected boolean includeOrderPriceColumn()
    {
        return true;
    }
    /**
     * Indicate if the <code>OriginalOrderId</code> column should be included.
     *
     * @return a <code>boolean</code> value
     */
    protected boolean includeOriginalOrderIdColumn()
    {
        return true;
    }
    /**
     * Indicate if the <code>Exchange</code> column should be included.
     *
     * @return a <code>boolean</code> value
     */
    protected boolean includeExchangeColumn()
    {
        return true;
    }
    /**
     * Indicate if the <code>TransactTime</code> column should be included.
     *
     * @return a <code>boolean</code> value
     */
    protected boolean includeTransactTimeColumn()
    {
        return true;
    }
    /**
     * Indicate if the <code>SendingTime</code> column should be included.
     *
     * @return a <code>boolean</code> value
     */
    protected boolean includeSendingTimeColumn()
    {
        return true;
    }
    /**
     * Indicate if the <code>OrderId</code> column should be included.
     *
     * @return a <code>boolean</code> value
     */
    protected boolean includeOrderIdColumn()
    {
        return true;
    }
    /**
     * Indicate if the <code>OrderStatus</code> column should be included.
     *
     * @return a <code>boolean</code> value
     */
    protected boolean includeOrderStatusColumn()
    {
        return true;
    }
    /**
     * Indicate if the <code>Account</code> column should be included.
     *
     * @return a <code>boolean</code> value
     */
    protected boolean includeAccountColumn()
    {
        return true;
    }
    /**
     * Indicate if the <code>BrokerId</code> column should be included.
     *
     * @return a <code>boolean</code> value
     */
    protected boolean includeBrokerIdColumn()
    {
        return true;
    }
    /**
     * Indicate if the <code>Trader</code> column should be included.
     *
     * @return a <code>boolean</code> value
     */
    protected boolean includeTraderColumn()
    {
        return true;
    }
    /**
     * Initialize the table columns
     *
     * @param inTableView a <code>TableView&lt;FixClazz&gt;</code> value
     */
    protected void initializeColumns(TableView<FixClazz> inTableView)
    {
        transactTimeColumn = new TableColumn<>("TransactTime"); 
        if(includeTransactTimeColumn()) {
            transactTimeColumn.setCellValueFactory(new PropertyValueFactory<>("transactTime"));
            transactTimeColumn.setCellFactory(tableColumn -> renderDateCell(tableColumn));
            inTableView.getColumns().add(transactTimeColumn);
        }
        sendingTimeColumn = new TableColumn<>("SendingTime"); 
        if(includeSendingTimeColumn()) {
            sendingTimeColumn.setCellValueFactory(new PropertyValueFactory<>("sendingTime"));
            sendingTimeColumn.setCellFactory(tableColumn -> renderDateCell(tableColumn));
            inTableView.getColumns().add(sendingTimeColumn);
        }
        orderIdColumn = new TableColumn<>("OrdId"); 
        if(includeOrderIdColumn()) {
            orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("ordId"));
            inTableView.getColumns().add(orderIdColumn);
        }
        origOrderIdColumn = new TableColumn<>("OrigOrdId"); 
        if(includeOriginalOrderIdColumn()) {
            origOrderIdColumn.setCellValueFactory(new PropertyValueFactory<>("origOrdId"));
            inTableView.getColumns().add(origOrderIdColumn);
        }
        orderStatusColumn = new TableColumn<>("OrdStatus"); 
        if(includeOrderStatusColumn()) {
            orderStatusColumn.setCellValueFactory(new PropertyValueFactory<>("ordStatus"));
            inTableView.getColumns().add(orderStatusColumn);
        }
        sideColumn = new TableColumn<>("Side"); 
        sideColumn.setCellValueFactory(new PropertyValueFactory<>("side"));
        inTableView.getColumns().add(sideColumn);
        symbolColumn = new TableColumn<>("Symbol"); 
        symbolColumn.setCellValueFactory(new PropertyValueFactory<>("symbol"));
        inTableView.getColumns().add(symbolColumn);
        ordQtyColumn = new TableColumn<>("OrdQty"); 
        if(includeOrderQuantityColumn()) {
            ordQtyColumn.setCellValueFactory(new PropertyValueFactory<>("ordQty"));
            ordQtyColumn.setCellFactory(tableColumn -> renderNumberCell(tableColumn));
            inTableView.getColumns().add(ordQtyColumn);
        }
        cumQtyColumn = new TableColumn<>("CumQty"); 
        cumQtyColumn.setCellValueFactory(new PropertyValueFactory<>("cumQty"));
        cumQtyColumn.setCellFactory(tableColumn -> renderNumberCell(tableColumn));
        inTableView.getColumns().add(cumQtyColumn);
        leavesQtyColumn = new TableColumn<>("LeavesQty"); 
        if(includeLeavesQuantityColumn()) {
            leavesQtyColumn.setCellValueFactory(new PropertyValueFactory<>("leavesQty"));
            leavesQtyColumn.setCellFactory(tableColumn -> renderNumberCell(tableColumn));
            inTableView.getColumns().add(leavesQtyColumn);
        }
        orderPriceColumn = new TableColumn<>("OrderPx"); 
        if(includeOrderPriceColumn()) {
            orderPriceColumn.setCellValueFactory(new PropertyValueFactory<>("orderPx"));
            orderPriceColumn.setCellFactory(tableColumn -> renderOrderPriceCell(tableColumn));
            inTableView.getColumns().add(orderPriceColumn);
        }
        averagePriceColumn = new TableColumn<>("AvgPx"); 
        averagePriceColumn.setCellValueFactory(new PropertyValueFactory<>("avgPx"));
        averagePriceColumn.setCellFactory(tableColumn -> renderNumberCell(tableColumn));
        inTableView.getColumns().add(averagePriceColumn);
        accountColumn = new TableColumn<>("Account"); 
        if(includeAccountColumn()) {
            accountColumn.setCellValueFactory(new PropertyValueFactory<>("account"));
            inTableView.getColumns().add(accountColumn);
        }
        lastQtyColumn = new TableColumn<>("LastQty"); 
        if(includeLastQuantityColumn()) {
            lastQtyColumn.setCellValueFactory(new PropertyValueFactory<>("lastQty"));
            lastQtyColumn.setCellFactory(tableColumn -> renderNumberCell(tableColumn));
            inTableView.getColumns().add(lastQtyColumn);
        }
        lastPriceColumn = new TableColumn<>("LastPx"); 
        if(includeLastPriceColumn()) {
            lastPriceColumn.setCellValueFactory(new PropertyValueFactory<>("lastPx"));
            lastPriceColumn.setCellFactory(tableColumn -> renderNumberCell(tableColumn));
            inTableView.getColumns().add(lastPriceColumn);
        }
        exchangeColumn = new TableColumn<>("Exchange"); 
        if(includeExchangeColumn()) {
            exchangeColumn.setCellValueFactory(new PropertyValueFactory<>("exchange"));
            inTableView.getColumns().add(exchangeColumn);
        }
        brokerIdColumn = new TableColumn<>("BrokerId"); 
        if(includeBrokerIdColumn()) {
            brokerIdColumn.setCellValueFactory(new PropertyValueFactory<>("brokerId"));
            inTableView.getColumns().add(brokerIdColumn);
        }
        traderColumn = new TableColumn<>("Trader"); 
        if(includeTraderColumn()) {
            traderColumn.setCellValueFactory(new PropertyValueFactory<>("trader"));
            inTableView.getColumns().add(traderColumn);
        }
    }
    /**
     * Update the reports table view the set of reports dictated by the view controls.
     */
    protected void updateReports()
    {
        updateReports(null);
    }
    /**
     * Update the reports table view with the set of reports dictated by the view controls with an optional <code>TradeMessage</code> that forced the update.
     *
     * @param inTradeMessage a <code>TradeMessage</code> value or <code>null</code>
     */
    protected void updateReports(TradeMessage inTradeMessage)
    {
        CollectionPageResponse<ClientClazz> response = getClientReports(new PageRequest(currentPage,
                                                                                        pageSize));
        // TODO figure out if the contents have changed?
        Platform.runLater(new Runnable() {
            @Override
            public void run()
            {
                if(response.getTotalSize() == 0) {
                    pagination.setPageCount(1);
                    pagination.setCurrentPageIndex(1);
                    pagination.setMaxPageIndicatorCount(1);
                } else {
                    pagination.setPageCount(response.getTotalPages());
                    pagination.setCurrentPageIndex(currentPage);
                }
                reportsTableView.getItems().clear();
                for(ClientClazz report : response.getElements()) {
                    FixClazz newReport = createFixDisplayObject(report);
                    newReport.isFillProperty().set(false);
                    newReport.isCancelProperty().set(false);
                    if(shouldHighlightTrades && includeOrderIdColumn()) {
                        if(inTradeMessage != null && inTradeMessage instanceof ExecutionReport) {
                            ExecutionReport executionReport = (ExecutionReport)inTradeMessage;
                            if(newReport.getOrderId().equals(executionReport.getOrderID()) && executionReport.getExecutionType().isFill()) {
                                newReport.isFillProperty().set(true);
                            }
                        }
                    }
                    if(shouldHighlightCancels && includeOrderIdColumn()) {
                        if(inTradeMessage != null && inTradeMessage instanceof ExecutionReport) {
                            ExecutionReport executionReport = (ExecutionReport)inTradeMessage;
                            if(newReport.getOrderId().equals(executionReport.getOrderID()) && executionReport.getExecutionType().isCancel()) {
                                newReport.isCancelProperty().set(true);
                            }
                        }
                    }
                    reportsTableView.getItems().add(newReport);
                }
            }
        });
    }
    /**
     * Get a page of items to display in the FIX table.
     *
     * @param inPageRequest a <code>PageRequest</code> value
     * @return a <code>CollectionPageResponse&lt;ClientClazz&gt;</code> value
     */
    protected abstract CollectionPageResponse<ClientClazz> getClientReports(PageRequest inPageRequest);
    /**
     * Create a single FIX item for the table.
     *
     * @param inClientClazz a <code>ClientClazz</code> value
     * @return a <code>FixClazz</code> value
     */
    protected abstract FixClazz createFixDisplayObject(ClientClazz inClientClazz);
    /**
     * Get the table placeholder for empty tables.
     *
     * @return a <code>Node</code> value
     */
    protected Node getPlaceholder()
    {
        return new Label("no reports");
    }
    /**
     * Create a new AbstractFixMessageView instance.
     *
     * @param inParentWindow a <code>Region</code> value
     * @param inEvent a <code>NewWindowEvent</code> value
     * @param inViewProperties a <code>Properties</code> value
     */
    protected AbstractFixMessageView(Region inParentWindow,
                                     NewWindowEvent inEvent,
                                     Properties inViewProperties)
    {
        super(inParentWindow,
              inEvent,
              inViewProperties);
    }
    /**
     * used to format date columns
     */
    protected static final DateTimeFormatter isoDateFormatter = TimeFactoryImpl.FULL_MILLISECONDS;
    /**
     * provides access to trade services
     */
    protected TradeClientService tradeClientService;
    /**
     * pagination current page value
     */
    protected int currentPage;
    /**
     * pagination page size value
     */
    protected int pageSize;
    /**
     * main layout of the window
     */
    protected VBox mainLayout;
    /**
     * table pagination widget
     */
    protected Pagination pagination;
    /**
     * FIX message table
     */
    protected TableView<FixClazz> reportsTableView;
    /**
     * table context menu
     */
    protected ContextMenu reportsTableContextMenu;
    /**
     * message transaction time table column
     */
    protected TableColumn<FixClazz,Date> transactTimeColumn;
    /**
     * message sending time table column
     */
    protected TableColumn<FixClazz,Date> sendingTimeColumn;
    /**
     * message order id table column
     */
    protected TableColumn<FixClazz,OrderID> orderIdColumn;
    /**
     * message original order id table column
     */
    protected TableColumn<FixClazz,OrderID> origOrderIdColumn;
    /**
     * message order status table column
     */
    protected TableColumn<FixClazz,OrderStatus> orderStatusColumn;
    /**
     * message side table column
     */
    protected TableColumn<FixClazz,Side> sideColumn;
    /**
     * message symbol table column
     */
    protected TableColumn<FixClazz,Instrument> symbolColumn;
    /**
     * message order quantity table column
     */
    protected TableColumn<FixClazz,BigDecimal> ordQtyColumn;
    /**
     * message cumulative quantity table column
     */
    protected TableColumn<FixClazz,BigDecimal> cumQtyColumn;
    /**
     * message leaves quantity table column
     */
    protected TableColumn<FixClazz,BigDecimal> leavesQtyColumn;
    /**
     * message order price table column
     */
    protected TableColumn<FixClazz,BigDecimal> orderPriceColumn;
    /**
     * message average price table column
     */
    protected TableColumn<FixClazz,BigDecimal> averagePriceColumn;
    /**
     * message account table column
     */
    protected TableColumn<FixClazz,String> accountColumn;
    /**
     * message last quantity table column
     */
    protected TableColumn<FixClazz,BigDecimal> lastQtyColumn;
    /**
     * message last price table column
     */
    protected TableColumn<FixClazz,BigDecimal> lastPriceColumn;
    /**
     * message exchange table column
     */
    protected TableColumn<FixClazz,String> exchangeColumn;
    /**
     * message broker id table column
     */
    protected TableColumn<FixClazz,BrokerID> brokerIdColumn;
    /**
     * message trader table column
     */
    protected TableColumn<FixClazz,String> traderColumn;
    /**
     * cancel order context menu item
     */
    protected MenuItem cancelOrderMenuItem;
    /**
     * replace order context menu item
     */
    protected MenuItem replaceOrderMenuItem;
    /**
     * view FIX messages details context menu item
     */
    protected MenuItem viewFixMessageDetailsMenuItem;
    /**
     * copy FIX message context menu item
     */
    protected MenuItem copyOrderMenuItem;
    /**
     * listens for trade messages
     */
    private TradeMessageListener tradeMessageListener;
    /**
     * optional layout used for above-the-table
     */
    private FlowPane aboveTableLayout;
    /**
     * used to restore table styles to default values
     */
    private ScheduledExecutorService styleUpdateTimerService = Executors.newSingleThreadScheduledExecutor();
    /**
     * how long in ms to highlight a FIX message view row on trade
     */
    @Value("${metc.trade.highlight.duration:1000}")
    private long tradeHighlightDuration;
    /**
     * indicate if FIX message view rows should by highlighted on trade
     */
    @Value("${metc.trade.should.highlight:true}")
    private boolean shouldHighlightTrades;
    /**
     * CSS value to apply to a table row in a FIX message view on trade
     */
    @Value("${metc.trade.highlight.css:-fx-background-color: RED;}")
    private String tradeHightlightCss;
    /**
     * how long in ms to highlight a FIX message view row on cancel
     */
    @Value("${metc.cancel.highlight.duration:1000}")
    private long cancelHighlightDuration;
    /**
     * indicate if FIX message view rows should by highlighted on cancel
     */
    @Value("${metc.cancel.should.highlight:true}")
    private boolean shouldHighlightCancels;
    /**
     * CSS value to apply to a table row in a FIX message view on cancel
     */
    @Value("${metc.cancel.highlight.css:-fx-background-color: ORANGE;}")
    private String cancelHightlightCss;
}
