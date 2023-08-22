package org.marketcetera.ui.trade.view;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
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
        pagination = new Pagination();
        pagination.setPageCount(0);
        pagination.setCurrentPageIndex(1);
        pagination.setMaxPageIndicatorCount(0);
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
                updateReports();
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
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent clipboardContent = new ClipboardContent();
            Collection<FixClazz> selectedItems = getSelectedItems();
            StringBuilder output = new StringBuilder();
            for(FixClazz report : selectedItems) {
                quickfix.Message fixMessage = report.getMessage();
                try {
                    output.append(new AnalyzedMessage(FIXMessageUtil.getDataDictionary(FIXVersion.getFIXVersion(fixMessage)),
                                                      fixMessage).toString());
                    output.append(StringUtils.LF);
                } catch (Exception e) {
                    SLF4JLoggerProxy.warn(getClass(),
                                          e,
                                          "Unable to generate pretty string for {}",
                                          fixMessage);
                    output.append(FIXMessageUtil.toHumanDelimitedString(fixMessage)).append(StringUtils.LF);
                }
            }
            clipboardContent.putString(output.toString());
            clipboard.setContent(clipboardContent);
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
        // any report can be viewed or copied
        viewFixMessageDetailsMenuItem.setDisable(false);
        copyOrderMenuItem.setDisable(false);
        // enable the cancel and replace options only if all the reports in the selection are cancellable
        boolean disable = false;
        for(FixClazz report : inSelectedItems) {
            OrderStatus orderStatus = report.getOrderStatus();
            if(!orderStatus.isCancellable()) {
                disable = true;
                break;
            }
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
     * Render the given column as an Instrument cell.
     *
     * @param inTableColumn a <code>TableColumn&lt;FixClazz,Instrument&gt;</code> value
     * @return a <code>TableCell&lt;FixClazz,Instrument&gt;</code> value
     */
    protected TableCell<FixClazz,Instrument> renderInstrumentCell(TableColumn<FixClazz,Instrument> inTableColumn)
    {
        TableCell<FixClazz,Instrument> tableCell = new TableCell<>() {
            @Override
            protected void updateItem(Instrument inItem,
                                      boolean isEmpty)
            {
                super.updateItem(inItem,
                                 isEmpty);
                this.setText(null);
                this.setGraphic(null);
                if(!isEmpty && inItem != null){
                    this.setText(inItem.getFullSymbol());
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
        sendingTimeColumn = new TableColumn<>("SendingTime"); 
        orderIdColumn = new TableColumn<>("OrdId"); 
        origOrderIdColumn = new TableColumn<>("OrigOrdId"); 
        orderStatusColumn = new TableColumn<>("OrdStatus"); 
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
        orderPriceColumn = new TableColumn<>("OrderPx"); 
        averagePriceColumn = new TableColumn<>("AvgPx"); 
        averagePriceColumn.setCellValueFactory(new PropertyValueFactory<>("averagePrice"));
        averagePriceColumn.setCellFactory(tableColumn -> renderNumberCell(tableColumn));
        accountColumn = new TableColumn<>("Account"); 
        lastQtyColumn = new TableColumn<>("LastQty"); 
        lastPriceColumn = new TableColumn<>("LastPx"); 
        exchangeColumn = new TableColumn<>("Exchange"); 
        brokerIdColumn = new TableColumn<>("BrokerId"); 
        traderColumn = new TableColumn<>("Trader"); 
        if(includeTransactTimeColumn()) {
            transactTimeColumn.setCellValueFactory(new PropertyValueFactory<>("transactTime"));
            transactTimeColumn.setCellFactory(tableColumn -> renderDateCell(tableColumn));
            inTableView.getColumns().add(transactTimeColumn);
        }
        if(includeSendingTimeColumn()) {
            sendingTimeColumn.setCellValueFactory(new PropertyValueFactory<>("sendingTime"));
            sendingTimeColumn.setCellFactory(tableColumn -> renderDateCell(tableColumn));
            inTableView.getColumns().add(sendingTimeColumn);
        }
        if(includeOrderIdColumn()) {
            orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("orderId"));
            inTableView.getColumns().add(orderIdColumn);
        }
        if(includeOriginalOrderIdColumn()) {
            origOrderIdColumn.setCellValueFactory(new PropertyValueFactory<>("originalOrderId"));
            inTableView.getColumns().add(origOrderIdColumn);
        }
        if(includeOrderStatusColumn()) {
            orderStatusColumn.setCellValueFactory(new PropertyValueFactory<>("orderStatus"));
            inTableView.getColumns().add(orderStatusColumn);
        }
        inTableView.getColumns().add(sideColumn);
        inTableView.getColumns().add(symbolColumn);
        if(includeOrderQuantityColumn()) {
            ordQtyColumn.setCellValueFactory(new PropertyValueFactory<>("orderQuantity"));
            ordQtyColumn.setCellFactory(tableColumn -> renderNumberCell(tableColumn));
            inTableView.getColumns().add(ordQtyColumn);
        }
        inTableView.getColumns().add(cumQtyColumn);
        if(includeLeavesQuantityColumn()) {
            leavesQtyColumn.setCellValueFactory(new PropertyValueFactory<>("leavesQuantity"));
            leavesQtyColumn.setCellFactory(tableColumn -> renderNumberCell(tableColumn));
            inTableView.getColumns().add(leavesQtyColumn);
        }
        if(includeOrderPriceColumn()) {
            orderPriceColumn.setCellValueFactory(new PropertyValueFactory<>("orderPrice"));
            orderPriceColumn.setCellFactory(tableColumn -> renderOrderPriceCell(tableColumn));
            inTableView.getColumns().add(orderPriceColumn);
        }
        inTableView.getColumns().add(averagePriceColumn);
        if(includeAccountColumn()) {
            accountColumn.setCellValueFactory(new PropertyValueFactory<>("account"));
            inTableView.getColumns().add(accountColumn);
        }
        if(includeLastQuantityColumn()) {
            lastQtyColumn.setCellValueFactory(new PropertyValueFactory<>("lastQuantity"));
            lastQtyColumn.setCellFactory(tableColumn -> renderNumberCell(tableColumn));
            inTableView.getColumns().add(lastQtyColumn);
        }
        if(includeLastPriceColumn()) {
            lastPriceColumn.setCellValueFactory(new PropertyValueFactory<>("lastPrice"));
            lastPriceColumn.setCellFactory(tableColumn -> renderNumberCell(tableColumn));
            inTableView.getColumns().add(lastPriceColumn);
        }
        if(includeExchangeColumn()) {
            exchangeColumn.setCellValueFactory(new PropertyValueFactory<>("exchange"));
            inTableView.getColumns().add(exchangeColumn);
        }
        if(includeBrokerIdColumn()) {
            brokerIdColumn.setCellValueFactory(new PropertyValueFactory<>("brokerId"));
            inTableView.getColumns().add(brokerIdColumn);
        }
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
     * mesage side table column
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
}
