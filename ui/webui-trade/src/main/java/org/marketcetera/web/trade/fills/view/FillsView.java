package org.marketcetera.web.trade.fills.view;

import java.util.Locale;
import java.util.Properties;

import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.web.SessionUser;
import org.marketcetera.web.converters.DateConverter;
import org.marketcetera.web.converters.DecimalConverter;
import org.marketcetera.web.converters.UserConverter;
import org.marketcetera.web.view.AbstractGridView;
import org.marketcetera.web.view.PagedDataContainer;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.spring.annotation.SpringComponent;

/* $License$ */

/**
 * Provides a view for order fills.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FillsView
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
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.ContentView#getViewName()
     */
    @Override
    public String getViewName()
    {
        return NAME;
    }
    /**
     * Create a new FillsView instance.
     *
     * @param inViewProperties a <code>Properties</code> value
     */
    FillsView(Properties inViewProperties)
    {
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.AbstractGridView#setGridColumns()
     */
    @Override
    protected void setGridColumns()
    {
        System.out.println("COCO: " + getGrid().getColumns());
//        getGrid().setColumns("transactTime",
//                             "orderId",
//                             "orderStatus",
//                             "side",
//                             "instrument",
//                             "orderQuantity",
//                             "cumulativeQuantity",
//                             "leavesQuantity",
//                             "orderPrice",
//                             "averagePrice",
//                             "account",
//                             "lastQuantity",
//                             "lastPrice",
//                             "actor");
//        getGrid().getColumn("actor").setHeaderCaption("User").setConverter(UserConverter.instance);
//        getGrid().getColumn("instrument").setConverter(new Converter<String,Instrument>() {
//            @Override
//            public Instrument convertToModel(String inValue,
//                                             Class<? extends Instrument> inTargetType,
//                                             Locale inLocale)
//                    throws ConversionException
//            {
//                throw new UnsupportedOperationException(); // TODO
//            }
//            @Override
//            public String convertToPresentation(Instrument inValue,
//                                                Class<? extends String> inTargetType,
//                                                Locale inLocale)
//                    throws ConversionException
//            {
//                return inValue.getFullSymbol();
//            }
//            @Override
//            public Class<Instrument> getModelType()
//            {
//                return Instrument.class;
//            }
//            @Override
//            public Class<String> getPresentationType()
//            {
//                return String.class;
//            }
//            private static final long serialVersionUID = 2362260803441310303L;
//        });
//        getGrid().getColumn("orderPrice").setConverter(DecimalConverter.instance).setHeaderCaption("Ord Px");
//        getGrid().getColumn("averagePrice").setConverter(DecimalConverter.instance).setHeaderCaption("Avg Px");
//        getGrid().getColumn("lastPrice").setConverter(DecimalConverter.instance).setHeaderCaption("Last Px");
//        getGrid().getColumn("lastQuantity").setHeaderCaption("Last Qty");
//        getGrid().getColumn("leavesQuantity").setHeaderCaption("Leaves Qty");
//        getGrid().getColumn("orderQuantity").setHeaderCaption("Ord Qty");
//        getGrid().getColumn("transactTime").setConverter(DateConverter.instance);
//        getGrid().getColumn("cumulativeQuantity").setHeaderCaption("Cum Qty");
//        getGrid().getColumn("orderStatus").setHeaderCaption("Ord Status");
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
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.AbstractGridView#createBeanItemContainer()
     */
    @Override
    protected PagedDataContainer<ExecutionReport> createDataContainer()
    {
        return new FillsPagedDataContainer(this);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.AbstractGridView#getViewSubjectName()
     */
    @Override
    protected String getViewSubjectName()
    {
        return "Fills";
    }
    /**
     * global name of this view
     */
    private static final String NAME = "Fills View";
    private static final long serialVersionUID = 8743932938054580853L;
}
