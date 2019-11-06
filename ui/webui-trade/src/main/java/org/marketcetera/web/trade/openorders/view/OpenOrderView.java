package org.marketcetera.web.trade.openorders.view;

import java.util.Properties;

import org.marketcetera.trade.OrderSummary;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.web.SessionUser;
import org.marketcetera.web.service.WebMessageService;
import org.marketcetera.web.service.trade.TradeClientService;
import org.marketcetera.web.view.AbstractGridView;
import org.marketcetera.web.view.PagedDataContainer;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.server.VaadinSession;

/* $License$ */

/**
 * Provides a view for open orders.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
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
//        getGrid().addSelectionListener(inEvent -> {
//            OrderSummary selectedObject = getSelectedItem();
//            getActionSelect().removeAllItems();
//            if(selectedObject == null) {
//                getActionSelect().setReadOnly(true);
//            } else {
//                // TODO permission check before adding action to dropdown
//                getActionSelect().setReadOnly(false);
//                // adjust the available actions based on the status of the selected row
//                switch(selectedObject.getStatus()) {
//                    case CONNECTED:
//                    case DISCONNECTED:
//                    case NOT_CONNECTED:
//                        getActionSelect().addItems(ACTION_STOP);
//                        break;
//                    case DISABLED:
//                        getActionSelect().addItems(ACTION_ENABLE,
//                                                   ACTION_SEQUENCE,
//                                                   ACTION_EDIT,
//                                                   ACTION_DELETE);
//                        break;
//                    case STOPPED:
//                        getActionSelect().addItems(ACTION_START,
//                                                   ACTION_DISABLE,
//                                                   ACTION_SEQUENCE);
//                        break;
//                    case AFFINITY_MISMATCH:
//                    case BACKUP:
//                    case DELETED:
//                    case UNKNOWN:
//                    default:
//                        // nothing available, these are essentially weird statuses for display
//                        break;
//                }
//            }
//        });
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
                              String.valueOf(VaadinSession.getCurrent().getAttribute(SessionUser.class)),
                              getViewName(),
                              action,
                              selectedItem);
        TradeClientService adminClientService = TradeClientService.getInstance();
//        switch(action) {
//            case ACTION_START:
//                adminClientService.startSession(selectedItem.getName());
//                break;
//            case ACTION_STOP:
//                adminClientService.stopSession(selectedItem.getName());
//                break;
//            case ACTION_ENABLE:
//                adminClientService.enableSession(selectedItem.getName());
//                break;
//            case ACTION_DISABLE:
//                adminClientService.disableSession(selectedItem.getName());
//                break;
//            case ACTION_DELETE:
//                adminClientService.deleteSession(selectedItem.getName());
//                break;
//            case ACTION_SEQUENCE:
//                doUpdateSequenceNumbers(selectedItem.getSource());
//                break;
//            case ACTION_EDIT:
//                createOrEdit(selectedItem.getSource(),
//                             false);
//                break;
//            default:
//                throw new UnsupportedOperationException("Unsupported action: " + action);
//        }
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
     * Get the webMessageService value.
     *
     * @return a <code>WebMessageService</code> value
     */
    WebMessageService getWebMessageService()
    {
        return webMessageService;
    }
    /**
     * Sets the webMessageService value.
     *
     * @param inWebMessageService a <code>WebMessageService</code> value
     */
    void setWebMessageService(WebMessageService inWebMessageService)
    {
        webMessageService = inWebMessageService;
    }
    /**
     * provides access to web message services
     */
    private WebMessageService webMessageService;
    /**
     * global name of this view
     */
    private static final String NAME = "Open Orders View";
    /**
     * edit action label
     */
    private final String ACTION_EDIT = "Edit";
    /**
     * start action label
     */
    private final String ACTION_START = "Start";
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
