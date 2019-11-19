package org.marketcetera.web.trade.openorders.view;

import java.util.Locale;
import java.util.Properties;

import javax.xml.bind.JAXBException;

import org.marketcetera.core.PlatformServices;
import org.marketcetera.core.XmlService;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.OrderCancel;
import org.marketcetera.trade.OrderSummary;
import org.marketcetera.trade.client.SendOrderResponse;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.web.SessionUser;
import org.marketcetera.web.converters.DateConverter;
import org.marketcetera.web.converters.DecimalConverter;
import org.marketcetera.web.converters.UserConverter;
import org.marketcetera.web.service.ServiceManager;
import org.marketcetera.web.service.WebMessageService;
import org.marketcetera.web.service.trade.TradeClientService;
import org.marketcetera.web.trade.event.ReplaceOrderEvent;
import org.marketcetera.web.view.AbstractGridView;
import org.marketcetera.web.view.PagedDataContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.converter.Converter;
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
        getGrid().setColumns("transactTime",
                             "orderId",
                             "orderStatus",
                             "side",
                             "instrument",
                             "orderQuantity",
                             "cumulativeQuantity",
                             "leavesQuantity",
                             "orderPrice",
                             "averagePrice",
                             "account",
                             "lastQuantity",
                             "lastPrice",
                             "actor");
        getGrid().getColumn("actor").setHeaderCaption("User").setConverter(UserConverter.instance);
        getGrid().getColumn("instrument").setConverter(new Converter<String,Instrument>() {
            @Override
            public Instrument convertToModel(String inValue,
                                             Class<? extends Instrument> inTargetType,
                                             Locale inLocale)
                    throws ConversionException
            {
                throw new UnsupportedOperationException(); // TODO
            }
            @Override
            public String convertToPresentation(Instrument inValue,
                                                Class<? extends String> inTargetType,
                                                Locale inLocale)
                    throws ConversionException
            {
                return inValue.getFullSymbol();
            }
            @Override
            public Class<Instrument> getModelType()
            {
                return Instrument.class;
            }
            @Override
            public Class<String> getPresentationType()
            {
                return String.class;
            }
            private static final long serialVersionUID = 2362260803441310303L;
        });
        getGrid().getColumn("orderPrice").setConverter(DecimalConverter.instance).setHeaderCaption("Ord Px");
        getGrid().getColumn("averagePrice").setConverter(DecimalConverter.instance).setHeaderCaption("Avg Px");
        getGrid().getColumn("lastPrice").setConverter(DecimalConverter.instance).setHeaderCaption("Last Px");
        getGrid().getColumn("lastQuantity").setHeaderCaption("Last Qty");
        getGrid().getColumn("leavesQuantity").setHeaderCaption("Leaves Qty");
        getGrid().getColumn("orderQuantity").setHeaderCaption("Ord Qty");
        getGrid().getColumn("transactTime").setConverter(DateConverter.instance);
        getGrid().getColumn("cumulativeQuantity").setHeaderCaption("Cum Qty");
        getGrid().getColumn("orderStatus").setHeaderCaption("Ord Status");
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
            case ACTION_REPLACE:
                TradeClientService tradeClient = serviceManager.getService(TradeClientService.class);
                ExecutionReport executionReport = tradeClient.getLatestExecutionReportForOrderChain(selectedItem.getRootOrderId());
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
                    System.out.println("COCO: " + replaceProperties);
                    ReplaceOrderEvent replaceOrderEvent = applicationContext.getBean(ReplaceOrderEvent.class,
                                                                                     executionReport,
                                                                                     replaceProperties);
                    webMessageService.post(replaceOrderEvent);
                    return;
                } else {
                    throw new UnsupportedOperationException("Unsupported action: " + action);
                }
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
     * provides access to XML services
     */
    @Autowired
    private XmlService xmlService;
    /**
     * provides access to the applicaton context
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
    private static final long serialVersionUID = 1901286026590258969L;
}
