package org.marketcetera.web.trade.executionreport;

import java.util.Properties;

import org.marketcetera.trade.ExecutionReportSummary;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.web.SessionUser;
import org.marketcetera.web.converters.DateConverter;
import org.marketcetera.web.converters.DecimalConverter;
import org.marketcetera.web.converters.ExecutionTypeConverter;
import org.marketcetera.web.converters.OrderStatusConverter;
import org.marketcetera.web.converters.SecurityTypeConverter;
import org.marketcetera.web.converters.SideConverter;
import org.marketcetera.web.converters.UserConverter;
import org.marketcetera.web.view.AbstractGridView;

import com.vaadin.data.Property.ValueChangeEvent;

/* $License$ */

/**
 * Provides common behavior for views that display {@link ExecutionReportSummary} values in a grid.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class AbstractExecutionReportView
        extends AbstractGridView<ExecutionReportSummary>
{
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.AbstractGridView#attach()
     */
    @Override
    public void attach()
    {
        super.attach();
        getActionSelect().setNullSelectionAllowed(false);
        getActionSelect().setReadOnly(true);
        getGrid().addSelectionListener(inEvent -> {
            ExecutionReportSummary selectedObject = getSelectedItem();
            getActionSelect().removeAllItems();
            if(selectedObject == null) {
                getActionSelect().setReadOnly(true);
            } else {
                // TODO permission check before adding action to dropdown
                getActionSelect().setReadOnly(true);
                // adjust the available actions based on the status of the selected row
                if(selectedObject.getOrderStatus().isCancellable()) {
//                    getActionSelect().addItems(ACTION_CANCEL,
//                                               ACTION_REPLACE);
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
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.AbstractGridView#setGridColumns()
     */
    @Override
    protected void setGridColumns()
    {
        getGrid().setColumns("sendingTime",
                             "orderID",
                             "orderStatus",
                             "executionType",
                             "side",
                             "securityType",
                             "symbol",
//                             "symbolSfx", TODO
                             "expiry",
                             "optionType",
                             "strikePrice",
                           //"transactTime", TODO
//                             "orderQuantity", TODO
                             "cumulativeQuantity",
//                             "leavesQuantity", TODO
//                             "price", TODO
                             "averagePrice",
                             "account",
                             "lastQuantity",
                             "lastPrice",
//                             "brokerId", TODO
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
        getGrid().getColumn("orderStatus").setConverter(OrderStatusConverter.instance);
        getGrid().getColumn("securityType").setConverter(SecurityTypeConverter.instance);
        getGrid().getColumn("sendingTime").setConverter(DateConverter.instance);
        getGrid().getColumn("side").setConverter(SideConverter.instance);
//        getGrid().getColumn("transactTime").setConverter(DateConverter.instance).setSortable(false); // TODO not sortable because transact time is derived
//        getGrid().getColumn("instrument").setConverter(InstrumentConverter.instance);
//        getGrid().getColumn("orderQuantity").setHeaderCaption("Ord Qty").setSortable(false); // TODO not sortable because this column is derived
//        getGrid().getColumn("leavesQuantity").setHeaderCaption("Leaves Qty").setSortable(false); // TODO not sortable because this column is derived
//        getGrid().getColumn("orderType").setHeaderCaption("Ord Type").setConverter(OrderTypeConverter.instance).setSortable(false); // TODO not sortable because this column is derived
//        getGrid().getColumn("price").setConverter(DecimalConverter.instance).setHeaderCaption("Ord Px").setSortable(false); // TODO not sortable because this column is derived
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.AbstractGridView#onActionSelect(com.vaadin.data.Property.ValueChangeEvent)
     */
    @Override
    protected void onActionSelect(ValueChangeEvent inEvent)
    {
        ExecutionReportSummary selectedItem = getSelectedItem();
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
//            case ACTION_CANCEL:
//            case ACTION_REPLACE:
//                TradeClientService tradeClient = serviceManager.getService(TradeClientService.class);
//                ExecutionReport executionReport = tradeClient.getLatestExecutionReportForOrderChain(selectedItem.getRootOrderId());
//                if(executionReport == null) {
//                    Notification.show("Unable to cancel or replace " + selectedItem.getOrderId() + ": no execution report",
//                                      Type.ERROR_MESSAGE);
//                    return;
//                }
//                if(action == ACTION_CANCEL) {
//                    OrderCancel orderCancel = Factory.getInstance().createOrderCancel(executionReport);
//                    SLF4JLoggerProxy.info(this,
//                                          "{} sending {}",
//                                          SessionUser.getCurrentUser().getUsername(),
//                                          orderCancel);
//                    SendOrderResponse response = tradeClient.send(orderCancel);
//                    if(response.getFailed()) {
//                        Notification.show("Unable to submit cancel: " + response.getOrderId() + " " + response.getMessage(),
//                                          Type.ERROR_MESSAGE);
//                        return;
//                    } else {
//                        Notification.show(response.getOrderId() + " submitted",
//                                          Type.TRAY_NOTIFICATION);
//                    }
//                } else if(action == ACTION_REPLACE) {
//                    String executionReportXml;
//                    try {
//                        executionReportXml = xmlService.marshall(executionReport);
//                    } catch (JAXBException e) {
//                        Notification.show("Unable to cancel or replace " + selectedItem.getOrderId() + ": " + PlatformServices.getMessage(e),
//                                          Type.ERROR_MESSAGE);
//                        return;
//                    }
//                    Properties replaceProperties = new Properties();
//                    replaceProperties.setProperty(ExecutionReport.class.getCanonicalName(),
//                                                  executionReportXml);
//                    System.out.println("COCO: " + replaceProperties);
//                    ReplaceOrderEvent replaceOrderEvent = applicationContext.getBean(ReplaceOrderEvent.class,
//                                                                                     executionReport,
//                                                                                     replaceProperties);
//                    webMessageService.post(replaceOrderEvent);
//                    return;
//                } else {
//                    throw new UnsupportedOperationException("Unsupported action: " + action);
//                }
//                break;
//            default:
//                throw new UnsupportedOperationException("Unsupported action: " + action);
        }
    }
    private static final long serialVersionUID = -3203095665399884857L;
}
