package org.marketcetera.web.trade.orderticket.view;

import java.util.List;
import java.util.Properties;

import org.marketcetera.brokers.BrokerStatusListener;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.fix.ActiveFixSession;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.web.service.ServiceManager;
import org.marketcetera.web.service.StyleService;
import org.marketcetera.web.service.admin.AdminClientService;
import org.marketcetera.web.service.trade.TradeClientService;
import org.marketcetera.web.view.ContentView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import com.google.common.collect.Lists;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;

/* $License$ */

/**
 * Provides a view for system Fix Sessions.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class OrderTicketView
        extends CssLayout
        implements ContentView,BrokerStatusListener
{
    /* (non-Javadoc)
     * @see com.vaadin.ui.AbstractComponent#attach()
     */
    @Override
    public void attach()
    {
        SLF4JLoggerProxy.trace(this,
                               "{} {} attach",
                               PlatformServices.getServiceName(getClass()),
                               hashCode());
        super.attach();
        newOrderLabel = new Label();
        newOrderLabel.setCaption("New Order");
        newOrderLabel.setId(getClass().getCanonicalName() + ".newOrderLabel");
        styleService.addStyle(getClass(),
                              newOrderLabel);
        brokerComboBox = new ComboBox();
        brokerComboBox.setId(getClass().getCanonicalName() + ".brokerComboBox");
        AdminClientService adminClientService = serviceManager.getService(AdminClientService.class);
        TradeClientService tradeClientService = serviceManager.getService(TradeClientService.class);
        adminClientService.addBrokerStatusListener(this);
        brokerComboBox.setTextInputAllowed(false);
        brokerComboBox.addItem("Auto Select");
        brokerComboBox.addItems(renderForBrokerList(tradeClientService.getAvailableFixInitiatorSessions()));
        brokerComboBox.setValue("Auto Select");
        addComponents(newOrderLabel,
                      brokerComboBox);
    }
    /**
     * Render the list of active FIX session values for the broker list to display.
     *
     * @param inAvailableFixInitiatorSessions a <code>List&lt;ActiveFixSession&gt;</code> value
     * @return a <code>List&lt;String&gt;</code> value
     */
    private List<String> renderForBrokerList(List<ActiveFixSession> inAvailableFixInitiatorSessions)
    {
        List<String> results = Lists.newArrayList();
        inAvailableFixInitiatorSessions.forEach(activeFixSession -> activeFixSession.getFixSession().getName());
        return results;
    }
    /* (non-Javadoc)
     * @see com.vaadin.ui.AbstractComponent#detach()
     */
    @Override
    public void detach()
    {
        SLF4JLoggerProxy.trace(this,
                               "{} {} detach",
                               PlatformServices.getServiceName(getClass()),
                               hashCode());
        serviceManager.getService(AdminClientService.class).removeBrokerStatusListener(this);
    }
    /* (non-Javadoc)
     * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
     */
    @Override
    public void enter(ViewChangeEvent inEvent)
    {
        SLF4JLoggerProxy.trace(this,
                               "{} enter: {}",
                               PlatformServices.getServiceName(getClass()),
                               inEvent);
    }
    private Label newOrderLabel;
    private ComboBox brokerComboBox;
    /* (non-Javadoc)
     * @see org.marketcetera.web.view.ContentView#getViewName()
     */
    @Override
    public String getViewName()
    {
        return NAME;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.BrokerStatusListener#receiveBrokerStatus(org.marketcetera.fix.ActiveFixSession)
     */
    @Override
    public void receiveBrokerStatus(ActiveFixSession inActiveFixSession)
    {
        SLF4JLoggerProxy.trace(this,
                               "{} receiveBrokerStatus: {}",
                               PlatformServices.getServiceName(getClass()),
                               inActiveFixSession);
    }
    /**
     * Create a new OrderTicketView instance.
     *
     * @param inProperties a <code>Properties</code> value
     */
    public OrderTicketView(Properties inProperties)
    {
        viewProperties = inProperties;
    }
    /**
     * properties that initialize this view
     */
    private final Properties viewProperties;
    /**
     * global name of this view
     */
    private static final String NAME = "Order Ticket View";
    /**
     * provides access to style services
     */
    @Autowired
    private StyleService styleService;
    /**
     * provides access to client services
     */
    @Autowired
    private ServiceManager serviceManager;
    private static final long serialVersionUID = 1779141772237455129L;
}
