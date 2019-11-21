package org.marketcetera.web.trade.executionreport;

import java.util.Properties;

import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.web.SessionUser;
import org.marketcetera.web.converters.DateConverter;
import org.marketcetera.web.converters.DecimalConverter;
import org.marketcetera.web.converters.InstrumentConverter;
import org.marketcetera.web.converters.OrderIdConverter;
import org.marketcetera.web.converters.OrderStatusConverter;
import org.marketcetera.web.converters.OrderTypeConverter;
import org.marketcetera.web.view.AbstractGridView;

import com.vaadin.data.Property.ValueChangeEvent;

/* $License$ */

/**
 * Provides common behavior for views that display {@link ExecutionReport} values in a grid.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class AbstractExecutionReportView
        extends AbstractGridView<ExecutionReport>
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
            ExecutionReport selectedObject = getSelectedItem();
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
        getGrid().setColumns("transactTime",
                             "sendingTime",
                             "orderID",
                             "originalOrderID",
                             "orderStatus",
                             "side",
                             "instrument",
                             "orderQuantity",
                             "cumulativeQuantity",
                             "leavesQuantity",
                             "orderType",
                             "price",
                             "averagePrice",
                             "account",
                             "lastQuantity",
                             "lastPrice",
                             "executionID",
                             "brokerOrderID",
                             "brokerId",
                             "actorID");
        getGrid().getColumn("transactTime").setConverter(DateConverter.instance).setSortable(false); // TODO not sortable because transact time is derived
        getGrid().getColumn("sendingTime").setConverter(DateConverter.instance);
        getGrid().getColumn("orderID").setConverter(OrderIdConverter.instance);
        getGrid().getColumn("originalOrderID").setConverter(OrderIdConverter.instance).setHeaderCaption("Orig\nOrder ID");
        getGrid().getColumn("orderStatus").setConverter(OrderStatusConverter.instance).setHeaderCaption("Ord Status");
        getGrid().getColumn("instrument").setConverter(InstrumentConverter.instance);
        getGrid().getColumn("orderQuantity").setHeaderCaption("Ord Qty").setSortable(false); // TODO not sortable because this column is derived
        getGrid().getColumn("cumulativeQuantity").setHeaderCaption("Cum Qty");
        getGrid().getColumn("leavesQuantity").setHeaderCaption("Leaves Qty").setSortable(false); // TODO not sortable because this column is derived
        getGrid().getColumn("orderType").setHeaderCaption("Ord Type").setConverter(OrderTypeConverter.instance).setSortable(false); // TODO not sortable because this column is derived
        getGrid().getColumn("price").setConverter(DecimalConverter.instance).setHeaderCaption("Ord Px").setSortable(false); // TODO not sortable because this column is derived
        getGrid().getColumn("averagePrice").setConverter(DecimalConverter.instance).setHeaderCaption("Avg Px");
        getGrid().getColumn("lastQuantity").setHeaderCaption("Last Qty");
        getGrid().getColumn("lastPrice").setConverter(DecimalConverter.instance).setHeaderCaption("Last Px");
        getGrid().getColumn("executionID").setHeaderCaption("Exec ID").setSortable(false); // TODO not sortable because this column is derived
        getGrid().getColumn("brokerOrderID").setSortable(false); // TODO not sortable because this column is derived
        getGrid().getColumn("brokerId").setHeaderCaption("Broker ID");
        getGrid().getColumn("actorID").setHeaderCaption("Trader ID");
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.AbstractGridView#onActionSelect(com.vaadin.data.Property.ValueChangeEvent)
     */
    @Override
    protected void onActionSelect(ValueChangeEvent inEvent)
    {
        ExecutionReport selectedItem = getSelectedItem();
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
