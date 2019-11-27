package org.marketcetera.web.trade.executionreport;

import java.util.Properties;

import javax.xml.bind.JAXBException;

import org.marketcetera.core.PlatformServices;
import org.marketcetera.core.XmlService;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.OrderCancel;
import org.marketcetera.trade.TradePermissions;
import org.marketcetera.trade.client.SendOrderResponse;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.web.SessionUser;
import org.marketcetera.web.converters.DateConverter;
import org.marketcetera.web.converters.DecimalConverter;
import org.marketcetera.web.converters.ExecutionTypeConverter;
import org.marketcetera.web.converters.OrderStatusConverter;
import org.marketcetera.web.converters.OrderTypeConverter;
import org.marketcetera.web.converters.SecurityTypeConverter;
import org.marketcetera.web.converters.SideConverter;
import org.marketcetera.web.converters.TimeInForceConverter;
import org.marketcetera.web.converters.UserConverter;
import org.marketcetera.web.service.AuthorizationHelperService;
import org.marketcetera.web.service.ServiceManager;
import org.marketcetera.web.service.WebMessageService;
import org.marketcetera.web.service.trade.TradeClientService;
import org.marketcetera.web.trade.event.FixMessageDetailsViewEvent;
import org.marketcetera.web.trade.event.ReplaceOrderEvent;
import org.marketcetera.web.trade.report.model.DisplayExecutionReportSummary;
import org.marketcetera.web.view.AbstractGridView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

/* $License$ */

/**
 * Provides common behavior for views that display {@link DisplayExecutionReportSummary} values in a grid.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class AbstractExecutionReportView
        extends AbstractGridView<DisplayExecutionReportSummary>
{
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.AbstractGridView#attach()
     */
    @Override
    public void attach()
    {
        super.attach();
        getCreateNewButton().setVisible(false);
        getActionSelect().setNullSelectionAllowed(false);
        getActionSelect().setReadOnly(true);
        getGrid().addSelectionListener(inEvent -> {
            DisplayExecutionReportSummary selectedObject = getSelectedItem();
            getActionSelect().removeAllItems();
            if(selectedObject == null) {
                getActionSelect().setReadOnly(true);
            } else {
                getActionSelect().setReadOnly(false);
                // adjust the available actions based on the status of the selected row
                if(selectedObject.getOrderStatus().isCancellable()) {
                    if(authzHelperService.hasPermission(TradePermissions.SendOrderAction)) { 
                        getActionSelect().addItems(ACTION_CANCEL,
                                                   ACTION_REPLACE);
                    }
                }
                if(authzHelperService.hasPermission(TradePermissions.AddReportAction)) {
                    getActionSelect().addItem(ACTION_ADD);
                }
                if(authzHelperService.hasPermission(TradePermissions.DeleteReportAction)) {
                    getActionSelect().addItem(ACTION_DELETE);
                }
                if(authzHelperService.hasPermission(TradePermissions.ViewReportAction)) {
                    getActionSelect().addItem(ACTION_FIX_MESSAGE_DETAILS);
                }
            }
        });
    }
    /**
     * Create a new FillsView instance.
     *
     * @param inViewProperties a <code>Properties</code> value
     */
    protected AbstractExecutionReportView(Properties inViewProperties)
    {
        viewProperties = inViewProperties;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.AbstractGridView#setGridColumns()
     */
    @Override
    protected void setGridColumns()
    {
        getGrid().setColumns("sendingTime",
                             "transactTime",
                             "rootOrderID",
                             "orderID",
                             "orderStatus",
                             "executionType",
                             "side",
                             "securityType",
                             "symbol",
                             "expiry",
                             "optionType",
                             "strikePrice",
                             "orderType",
                             "timeInForce",
                             "orderQuantity",
                             "cumulativeQuantity",
                             "leavesQuantity",
                             "price",
                             "averagePrice",
                             "account",
                             "lastQuantity",
                             "lastPrice",
                             "brokerID",
                             "executionId",
                             "brokerOrderId",
                             "actor");
        getGrid().getColumn("actor").setHeaderCaption("Trader").setConverter(UserConverter.instance);
        getGrid().getColumn("averagePrice").setConverter(DecimalConverter.instance).setHeaderCaption("Avg Px");
        getGrid().getColumn("brokerOrderId").setHeaderCaption("Broker Order ID");
        getGrid().getColumn("cumulativeQuantity").setHeaderCaption("Cum Qty");
        getGrid().getColumn("executionId").setHeaderCaption("Exec ID");
        getGrid().getColumn("executionType").setConverter(ExecutionTypeConverter.instance).setHeaderCaption("Exec Type");
        getGrid().getColumn("lastPrice").setConverter(DecimalConverter.instance).setHeaderCaption("Last Px");
        getGrid().getColumn("lastQuantity").setHeaderCaption("Last Qty");
        getGrid().getColumn("leavesQuantity").setHeaderCaption("Leaves Qty");
        getGrid().getColumn("orderQuantity").setHeaderCaption("Ord Qty");
        getGrid().getColumn("orderStatus").setConverter(OrderStatusConverter.instance);
        getGrid().getColumn("orderType").setHeaderCaption("Ord Type").setConverter(OrderTypeConverter.instance);
        getGrid().getColumn("price").setConverter(DecimalConverter.instance).setHeaderCaption("Ord Px");
        getGrid().getColumn("securityType").setConverter(SecurityTypeConverter.instance);
        getGrid().getColumn("sendingTime").setConverter(DateConverter.instance);
        getGrid().getColumn("side").setConverter(SideConverter.instance);
        getGrid().getColumn("timeInForce").setConverter(TimeInForceConverter.instance);
        getGrid().getColumn("transactTime").setConverter(DateConverter.instance);
//        getGrid().getColumn("instrument").setConverter(InstrumentConverter.instance);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.AbstractGridView#onActionSelect(com.vaadin.data.Property.ValueChangeEvent)
     */
    @Override
    protected void onActionSelect(ValueChangeEvent inEvent)
    {
        DisplayExecutionReportSummary selectedItem = getSelectedItem();
        if(selectedItem == null || inEvent.getProperty().getValue() == null) {
            return;
        }
        String action = String.valueOf(inEvent.getProperty().getValue());
        SLF4JLoggerProxy.info(this,
                              "{}: {} {} '{}'",
                              SessionUser.getCurrentUser().getUsername(),
                              getViewName(),
                              action,
                              selectedItem);
        switch(action) {
            case ACTION_CANCEL:
            case ACTION_REPLACE:
                TradeClientService tradeClient = serviceManager.getService(TradeClientService.class);
                ExecutionReport executionReport = tradeClient.getLatestExecutionReportForOrderChain(selectedItem.getRootOrderID());
                if(executionReport == null) {
                    Notification.show("Unable to cancel or replace " + selectedItem.getOrderID() + ": no execution report",
                                      Type.ERROR_MESSAGE);
                    return;
                }
                if(action == ACTION_CANCEL) {
                    OrderCancel orderCancel = Factory.getInstance().createOrderCancel(executionReport);
                    SLF4JLoggerProxy.info(this,
                                          "{} sending {}",
                                          SessionUser.getCurrentUser().getUsername(),
                                          orderCancel);
                    SendOrderResponse response = tradeClient.send(orderCancel);
                    if(response.getFailed()) {
                        Notification.show("Unable to submit cancel: " + response.getOrderId() + " " + response.getMessage(),
                                          Type.ERROR_MESSAGE);
                        return;
                    } else {
                        Notification.show(response.getOrderId() + " submitted",
                                          Type.TRAY_NOTIFICATION);
                    }
                } else if(action == ACTION_REPLACE) {
                    String executionReportXml;
                    try {
                        executionReportXml = xmlService.marshall(executionReport);
                    } catch (JAXBException e) {
                        Notification.show("Unable to cancel or replace " + selectedItem.getOrderID() + ": " + PlatformServices.getMessage(e),
                                          Type.ERROR_MESSAGE);
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
                } else {
                    throw new UnsupportedOperationException("Unsupported action: " + action);
                }
                break;
            case ACTION_FIX_MESSAGE_DETAILS:
                Properties replaceProperties = new Properties();
                replaceProperties.setProperty(quickfix.Message.class.getCanonicalName(),
                                              selectedItem.getFixMessage());
                FixMessageDetailsViewEvent viewFixMessageDetailsEvent = applicationContext.getBean(FixMessageDetailsViewEvent.class,
                                                                                                   selectedItem,
                                                                                                   replaceProperties);
                webMessageService.post(viewFixMessageDetailsEvent);
                return;
            case ACTION_ADD:
                break;
            case ACTION_DELETE:
                break;
            default:
                throw new UnsupportedOperationException("Unsupported action: " + action);
        }
    }
    /**
     * provides access to XML services
     */
    @Autowired
    private XmlService xmlService;
    /**
     * provides access to the application context
     */
    @Autowired
    private ApplicationContext applicationContext;
    /**
     * provides access to web message services
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
    private AuthorizationHelperService authzHelperService;
    /**
     * holds properties used to initialize view
     */
    protected final Properties viewProperties;
    /**
     * view FIX message details action
     */
    private static final String ACTION_FIX_MESSAGE_DETAILS = "View FIX Message Details";
    /**
     * delete report action
     */
    private static final String ACTION_DELETE = "Delete Report";
    /**
     * add report action
     */
    private static final String ACTION_ADD = "Add Report";
    /**
     * cancel order action
     */
    private static final String ACTION_CANCEL = "Cancel Order";
    /**
     * replace order action
     */
    private static final String ACTION_REPLACE = "Replace Order";
    private static final long serialVersionUID = -3203095665399884857L;
}
