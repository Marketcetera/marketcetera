package org.marketcetera.web.trade.report.view;

import java.util.Properties;

import org.marketcetera.core.XmlService;
import org.marketcetera.trade.Report;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.web.SessionUser;
import org.marketcetera.web.converters.DateConverter;
import org.marketcetera.web.converters.ReportTypeConverter;
import org.marketcetera.web.converters.StringFixMessageConverter;
import org.marketcetera.web.converters.UserConverter;
import org.marketcetera.web.service.ServiceManager;
import org.marketcetera.web.service.WebMessageService;
import org.marketcetera.web.view.AbstractGridView;
import org.marketcetera.web.view.PagedDataContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.spring.annotation.SpringComponent;

/* $License$ */

/**
 * Provides a view for reports.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ReportView
        extends AbstractGridView<Report>
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
            Report selectedObject = getSelectedItem();
            getActionSelect().removeAllItems();
            if(selectedObject == null) {
                getActionSelect().setReadOnly(true);
            } else {
                // TODO permission check before adding action to dropdown
                getActionSelect().setReadOnly(false);
                // adjust the available actions based on the status of the selected row
//                if(selectedObject.getOrderStatus().isCancellable()) {
//                    getActionSelect().addItems(ACTION_CANCEL,
//                                               ACTION_REPLACE);
//                }
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
    ReportView(Properties inViewProperties)
    {
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.AbstractGridView#setGridColumns()
     */
    @Override
    protected void setGridColumns()
    {
        getGrid().setColumns("sessionId",
                             "msgSeqNum",
                             "sendingTime",
                             "orderID",
                             "reportID",
                             "reportType",
                             "originator",
                             "hierarchy",
                             "actor",
                             "fixMessage");
        getGrid().getColumn("actor").setHeaderCaption("User").setConverter(UserConverter.instance);
        getGrid().getColumn("sessionId").setHeaderCaption("Session Id");
        getGrid().getColumn("sendingTime").setConverter(DateConverter.instance);
        getGrid().getColumn("reportType").setConverter(ReportTypeConverter.instance);
        getGrid().getColumn("fixMessage").setConverter(StringFixMessageConverter.instance);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.AbstractGridView#onActionSelect(com.vaadin.data.Property.ValueChangeEvent)
     */
    @Override
    protected void onActionSelect(ValueChangeEvent inEvent)
    {
        Report selectedItem = getSelectedItem();
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
            default:
                throw new UnsupportedOperationException("Unsupported action: " + action);
        }
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.AbstractGridView#createBeanItemContainer()
     */
    @Override
    protected PagedDataContainer<Report> createDataContainer()
    {
        return new ReportPagedDataContainer(this);
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
