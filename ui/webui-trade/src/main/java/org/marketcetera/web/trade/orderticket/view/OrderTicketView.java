package org.marketcetera.web.trade.orderticket.view;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.Properties;
import java.util.SortedSet;

import javax.annotation.concurrent.GuardedBy;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.marketcetera.brokers.BrokerStatusListener;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.fix.ActiveFixSession;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.OrderType;
import org.marketcetera.trade.Side;
import org.marketcetera.trade.TimeInForce;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.web.service.ServiceManager;
import org.marketcetera.web.service.StyleService;
import org.marketcetera.web.service.admin.AdminClientService;
import org.marketcetera.web.service.trade.TradeClientService;
import org.marketcetera.web.view.ContentView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import com.google.common.collect.Sets;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

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
        // broker status layout
        // broker combo
        brokerComboBox = new ComboBox();
        brokerComboBox.setCaption("Broker");
        brokerComboBox.setId(getClass().getCanonicalName() + ".brokerComboBox");
        brokerComboBox.setTextInputAllowed(false);
        brokerComboBox.addItem(AUTO_SELECT_BROKER);
        brokerComboBox.setValue(AUTO_SELECT_BROKER);
        brokerComboBox.setNullSelectionAllowed(false);
        styleService.addStyle(brokerComboBox);
        AdminClientService adminClientService = serviceManager.getService(AdminClientService.class);
        adminClientService.addBrokerStatusListener(this);
        // side combo
        sideComboBox = new ComboBox();
        sideComboBox.setCaption("Side");
        sideComboBox.setId(getClass().getCanonicalName() + ".sideComboBox");
        sideComboBox.setTextInputAllowed(false);
        sideComboBox.addItems(EnumSet.complementOf(EnumSet.of(Side.Unknown)));
        sideComboBox.setValue(Side.Buy);
        sideComboBox.setNullSelectionAllowed(false);
        styleService.addStyle(sideComboBox);
        // quantity text
        quantityTextField = new TextField();
        quantityTextField.setCaption("Quantity");
        quantityTextField.setId(getClass().getCanonicalName() + ".quantityTextField");
        quantityTextField.setNullSettingAllowed(true);
        quantityTextField.addValidator(inValue -> {
            String value = StringUtils.trimToNull(String.valueOf(inValue));
            if(value == null) {
                return;
            }
            try {
                new BigDecimal(value);
            } catch (Exception e) {
                throw new InvalidValueException(ExceptionUtils.getRootCauseMessage(e));
            }
        });
        styleService.addStyle(quantityTextField);
        // symbol text
        symbolTextField = new TextField();
        symbolTextField.setCaption("Symbol");
        symbolTextField.setId(getClass().getCanonicalName() + ".symbolTextField");
        symbolTextField.addValidator(inValue -> {
            String symbol = StringUtils.trimToNull(String.valueOf(inValue));
            if(symbol == null) {
                return;
            }
            Instrument instrument = serviceManager.getService(TradeClientService.class).resolveSymbol(symbol);
            if(instrument == null) {
                throw new InvalidValueException("Cannot resolve symbol");
            }
            symbolTextField.setDescription(instrument.toString());
        });
        symbolTextField.setNullSettingAllowed(true);
        styleService.addStyle(symbolTextField);
        // order type combo
        orderTypeComboBox = new ComboBox();
        orderTypeComboBox.setCaption("Order Type");
        orderTypeComboBox.setId(getClass().getCanonicalName() + ".orderTypeComboBox");
        orderTypeComboBox.setTextInputAllowed(false);
        orderTypeComboBox.addItems(EnumSet.complementOf(EnumSet.of(OrderType.Unknown)));
        orderTypeComboBox.setNullSelectionAllowed(true);
        orderTypeComboBox.setValue(null);
        orderTypeComboBox.addValueChangeListener(event -> {
            Object value = event.getProperty().getValue();
            boolean isMarket = false;
            if(value == null) {
                isMarket = false;
            } else {
                OrderType newValue = (OrderType)value;
                isMarket = newValue.isMarketOrder();
            }
            if(isMarket) {
                priceTextField.setValue("");
            }
            priceTextField.setReadOnly(isMarket);
        });
        styleService.addStyle(orderTypeComboBox);
        // price text
        priceTextField = new TextField();
        priceTextField.setCaption("Price");
        priceTextField.setId(getClass().getCanonicalName() + ".priceTextField");
        priceTextField.setNullSettingAllowed(true);
        priceTextField.addValidator(inValue -> {
            String value = StringUtils.trimToNull(String.valueOf(inValue));
            if(value == null) {
                return;
            }
            try {
                new BigDecimal(value);
            } catch (Exception e) {
                throw new InvalidValueException(ExceptionUtils.getRootCauseMessage(e));
            }
        });
        styleService.addStyle(priceTextField);
        // time in force combo
        timeInForceComboBox = new ComboBox();
        timeInForceComboBox.setCaption("Time in Force");
        timeInForceComboBox.setId(getClass().getCanonicalName() + ".timeInForceComboBox");
        timeInForceComboBox.setTextInputAllowed(false);
        timeInForceComboBox.addItems(EnumSet.complementOf(EnumSet.of(TimeInForce.Unknown)));
        timeInForceComboBox.setNullSelectionAllowed(true);
        timeInForceComboBox.setValue(null);
        styleService.addStyle(timeInForceComboBox);
        brokerStatusLayout = new HorizontalLayout();
        brokerStatusLayout.setId(getClass().getCanonicalName() + ".brokerStatusLayout");
        brokerStatusLayout.addComponents(brokerComboBox,
                                         sideComboBox,
                                         quantityTextField,
                                         symbolTextField,
                                         orderTypeComboBox,
                                         priceTextField,
                                         timeInForceComboBox);
        styleService.addStyle(brokerStatusLayout);
        addComponents(brokerStatusLayout);
        // finish main layout
        setId(getClass().getCanonicalName() + ".contentLayout");
        styleService.addStyle(this);
    }
    private ComboBox sideComboBox;
    private HorizontalLayout brokerStatusLayout;
    private TextField quantityTextField;
    private TextField symbolTextField;
    private ComboBox orderTypeComboBox;
    private TextField priceTextField;
    private ComboBox timeInForceComboBox;
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
        boolean brokersStatusChanged = false;
        synchronized(availableBrokers) {
            if(inActiveFixSession.getStatus().isLoggedOn()) {
                brokersStatusChanged = availableBrokers.add(inActiveFixSession.getFixSession().getName());
            } else {
                brokersStatusChanged = availableBrokers.remove(inActiveFixSession.getFixSession().getName());
            }
            if(brokersStatusChanged) {
                availableBrokers.notifyAll();
            }
        }
        if(brokersStatusChanged) {
            UI.getCurrent().access(new Runnable() {
                @Override
                public void run()
                {
                    Object currentSelectedBroker = brokerComboBox.getValue();
                    brokerComboBox.clear();
                    brokerComboBox.addItem(AUTO_SELECT_BROKER);
                    brokerComboBox.addItems(availableBrokers);
                    if(AUTO_SELECT_BROKER.equals(currentSelectedBroker)) {
                        brokerComboBox.setValue(AUTO_SELECT_BROKER);
                    } else {
                        if(availableBrokers.contains(currentSelectedBroker)) {
                            brokerComboBox.setValue(currentSelectedBroker);
                        } else {
                            brokerComboBox.setValue(AUTO_SELECT_BROKER);
                        }
                    }
                }}
            );
        }
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
     * token to indicate that the broker should be auto-selected
     */
    private final static String AUTO_SELECT_BROKER = "Auto Select";
    /**
     * contains currently available brokers
     */
    @GuardedBy("availableBrokers")
    private final SortedSet<String> availableBrokers = Sets.newTreeSet();
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
