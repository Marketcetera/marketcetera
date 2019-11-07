package org.marketcetera.web.trade.openorders.view;

import java.util.Properties;

import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.OrderCancel;
import org.marketcetera.trade.OrderSummary;
import org.marketcetera.trade.client.SendOrderResponse;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.web.SessionUser;
import org.marketcetera.web.service.ServiceManager;
import org.marketcetera.web.service.WebMessageService;
import org.marketcetera.web.service.trade.TradeClientService;
import org.marketcetera.web.view.AbstractGridView;
import org.marketcetera.web.view.PagedDataContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

/* $License$ */

/**
 * Provides a view for open orders.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class OpenOrderView
        extends AbstractGridView<OrderSummary>
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
            OrderSummary selectedObject = getSelectedItem();
            getActionSelect().removeAllItems();
            if(selectedObject == null) {
                getActionSelect().setReadOnly(true);
            } else {
                // TODO permission check before adding action to dropdown
                getActionSelect().setReadOnly(false);
                // adjust the available actions based on the status of the selected row
                if(selectedObject.getOrderStatus().isCancellable()) {
                    getActionSelect().addItems(ACTION_CANCEL,
                                               ACTION_REPLACE);
                }
            }
        });
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.ContentView#getViewName()
     */
    @Override
    public String getViewName()
    {
        return NAME;
    }
    /**
     * Create a new SessionView instance.
     *
     * @param inViewProperties a <code>Properties</code> value
     */
    OpenOrderView(Properties inViewProperties)
    {
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.AbstractGridView#setGridColumns()
     */
    @Override
    protected void setGridColumns()
    {
        System.out.println("COCO: columns are: " + getGrid().getColumns());
//        getGrid().setColumns("TransactTime",
//                             "OrderId",
//                             "orderStatus",
//                             "side",
//                             "Instrument",
//                             "OrderQuantity");
//        getGrid().addColumn("TransactTime",
//                            Date.class);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.AbstractGridView#onActionSelect(com.vaadin.data.Property.ValueChangeEvent)
     */
    @Override
    protected void onActionSelect(ValueChangeEvent inEvent)
    {
        OrderSummary selectedItem = getSelectedItem();
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
                TradeClientService tradeClient = serviceManager.getService(TradeClientService.class);
                ExecutionReport executionReport = tradeClient.getLatestExecutionReportForOrderChain(selectedItem.getRootOrderId());
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
                break;
            case ACTION_REPLACE:
                break;
            default:
                throw new UnsupportedOperationException("Unsupported action: " + action);
        }
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.AbstractGridView#createBeanItemContainer()
     */
    @Override
    protected PagedDataContainer<OrderSummary> createDataContainer()
    {
        return new OrderSummaryPagedDataContainer(this);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.AbstractGridView#getViewSubjectName()
     */
    @Override
    protected String getViewSubjectName()
    {
        return "Open Orders";
    }
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
     * global name of this view
     */
    private static final String NAME = "Open Orders View";
    /**
     * cancel action label
     */
    private final String ACTION_CANCEL = "Cancel";
    /**
     * replace action label
     */
    private final String ACTION_REPLACE = "Replace";
    /**
     * stop action label
     */
    private final String ACTION_STOP = "Stop";
    /**
     * enable action label
     */
    private final String ACTION_ENABLE = "Enable";
    /**
     * disable action label
     */
    private final String ACTION_DISABLE = "Disable";
    /**
     * delete action label
     */
    private final String ACTION_DELETE = "Delete";
    /**
     * edit sequence numbers label
     */
    private final String ACTION_SEQUENCE = "Update Sequence Numbers";
    private static final long serialVersionUID = 1901286026590258969L;
}
