package org.marketcetera.web.trade.executionreport.view;

import java.util.Properties;

import javax.xml.bind.JAXBException;

import org.marketcetera.core.PlatformServices;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.OrderCancel;
import org.marketcetera.trade.TradePermissions;
import org.marketcetera.trade.client.SendOrderResponse;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.web.SessionUser;
import org.marketcetera.web.events.NewWindowEvent;
import org.marketcetera.web.service.trade.TradeClientService;
import org.marketcetera.web.trade.event.FixMessageDetailsViewEvent;
import org.marketcetera.web.trade.event.ReplaceOrderEvent;
import org.marketcetera.web.view.AbstractPagedGridView;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Window;

/* $License$ */

/**
 * Provides common behavior for views that display {@link DisplayExecutionReportSummary} values in a grid.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class AbstractHasFixMessageView<DisplayClazz extends FixMessageDisplayType>
        extends AbstractPagedGridView<DisplayClazz>
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
            DisplayClazz selectedObject = getSelectedItem();
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
     * @param inParentWindow a <code>Window</code> value
     * @param inNewWindowEvent a <code>NewWindowEvent</code> value
     * @param inViewProperties a <code>Properties</code> value
     */
    protected AbstractHasFixMessageView(Window inParentWindow,
                                        NewWindowEvent inEvent,
                                        Properties inViewProperties)
    {
        super(inParentWindow,
              inEvent,
              inViewProperties);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.AbstractGridView#onActionSelect(com.vaadin.data.Property.ValueChangeEvent)
     */
    @Override
    protected void onActionSelect(ValueChangeEvent inEvent)
    {
        DisplayClazz selectedItem = getSelectedItem();
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
                ExecutionReport executionReport = tradeClient.getLatestExecutionReportForOrderChain(selectedItem.getOrderId());
                if(executionReport == null) {
                    Notification.show("Unable to cancel or replace " + selectedItem.getOrderId() + ": no execution report",
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
                        Notification.show("Unable to cancel or replace " + selectedItem.getOrderId() + ": " + PlatformServices.getMessage(e),
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
                                              selectedItem.getMessage().toString());
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
