package org.marketcetera.web.trade.orderticket.view;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Properties;
import java.util.SortedMap;

import javax.annotation.PostConstruct;
import javax.annotation.concurrent.GuardedBy;
import javax.xml.bind.JAXBException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.marketcetera.algo.BrokerAlgoSpec;
import org.marketcetera.brokers.BrokerStatusListener;
import org.marketcetera.client.Validations;
import org.marketcetera.core.BigDecimalUtil;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.core.XmlService;
import org.marketcetera.fix.ActiveFixSession;
import org.marketcetera.trade.AverageFillPrice;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.HasSuggestion;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.NewOrReplaceOrder;
import org.marketcetera.trade.OrderReplace;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.trade.OrderSingleSuggestion;
import org.marketcetera.trade.OrderType;
import org.marketcetera.trade.Side;
import org.marketcetera.trade.Suggestion;
import org.marketcetera.trade.TimeInForce;
import org.marketcetera.trade.client.OrderValidationException;
import org.marketcetera.trade.client.SendOrderResponse;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.web.SessionUser;
import org.marketcetera.web.events.NewWindowEvent;
import org.marketcetera.web.service.ServiceManager;
import org.marketcetera.web.service.StyleService;
import org.marketcetera.web.service.admin.AdminClientService;
import org.marketcetera.web.service.trade.TradeClientService;
import org.marketcetera.web.view.ContentView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import com.google.common.collect.Maps;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/* $License$ */

/**
 * Provides a view for Order Tickets.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class OrderTicketView
        extends VerticalLayout
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
        // prepare the first row
        // broker combo
        brokerComboBox = new ComboBox();
        brokerComboBox.setCaption("Broker");
        brokerComboBox.setId(getClass().getCanonicalName() + ".brokerComboBox");
        brokerComboBox.setTextInputAllowed(false);
        brokerComboBox.addItem(AUTO_SELECT_BROKER);
        brokerComboBox.setValue(AUTO_SELECT_BROKER);
        brokerComboBox.setNullSelectionAllowed(false);
        brokerComboBox.addValueChangeListener(newValue -> {
            Object selectedBroker = newValue.getProperty().getValue();
            brokerAlgoComboBox.setValue(null);
            brokerAlgoComboBox.clear();
            // TODO clear brokerAlgoTagGrid
            if(selectedBroker == null || AUTO_SELECT_BROKER.equals(selectedBroker)) {
                brokerAlgoComboBox.setEnabled(false);
                brokerAlgoTagGrid.setEnabled(false);
            } else {
                ActiveFixSession underlyingFixSession = availableBrokers.get(selectedBroker);
                if(underlyingFixSession != null && !underlyingFixSession.getBrokerAlgos().isEmpty()) {
                    brokerAlgoComboBox.setEnabled(true);
                    brokerAlgoComboBox.addItems(underlyingFixSession.getBrokerAlgos());
                }
            }
            adjustSendButton();
        });
        styleService.addStyle(brokerComboBox);
        serviceManager.getService(AdminClientService.class).addBrokerStatusListener(this);
        // side combo
        sideComboBox = new ComboBox();
        sideComboBox.setCaption("Side");
        sideComboBox.setId(getClass().getCanonicalName() + ".sideComboBox");
        sideComboBox.setTextInputAllowed(false);
        sideComboBox.addItems(EnumSet.complementOf(EnumSet.of(Side.Unknown)));
        sideComboBox.setValue(Side.Buy);
        sideComboBox.setNullSelectionAllowed(false);
        sideComboBox.setDescription("FIX field " + quickfix.field.Side.FIELD);
        sideComboBox.addValueChangeListener(newValueEvent -> {
            adjustSendButton();
        });
        styleService.addStyle(sideComboBox);
        // quantity text
        quantityTextField = new TextField();
        quantityTextField.setCaption("Order Quantity");
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
        quantityTextField.addValueChangeListener(newValueEvent -> {
            adjustSendButton();
        });
        quantityTextField.setDescription("FIX field " + quickfix.field.OrderQty.FIELD);
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
            resolvedInstrument = serviceManager.getService(TradeClientService.class).resolveSymbol(symbol);
            if(resolvedInstrument == null) {
                throw new InvalidValueException("Cannot resolve symbol");
            }
            symbolTextField.setDescription(resolvedInstrument.toString());
            // TODO if peg-to-midpoint is checked, we need to change the market data subscription
        });
        symbolTextField.addValueChangeListener(newValueEvent -> {
            adjustSendButton();
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
                pegToMidpointCheckBox.setValue(false);
            }
            priceTextField.setReadOnly(isMarket);
            pegToMidpointCheckBox.setReadOnly(isMarket);
            adjustSendButton();
        });
        orderTypeComboBox.setDescription("FIX Field " + quickfix.field.OrdType.FIELD);
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
        priceTextField.addValueChangeListener(newValueEvent -> {
            adjustSendButton();
        });
        priceTextField.setDescription("FIX Field " + quickfix.field.Price.FIELD);
        styleService.addStyle(priceTextField);
        // time in force combo
        timeInForceComboBox = new ComboBox();
        timeInForceComboBox.setCaption("Time in Force");
        timeInForceComboBox.setId(getClass().getCanonicalName() + ".timeInForceComboBox");
        timeInForceComboBox.setTextInputAllowed(false);
        timeInForceComboBox.addItems(EnumSet.complementOf(EnumSet.of(TimeInForce.Unknown)));
        timeInForceComboBox.setNullSelectionAllowed(true);
        timeInForceComboBox.setValue(null);
        timeInForceComboBox.setDescription("FIX Field " + quickfix.field.TimeInForce.FIELD);
        timeInForceComboBox.addValueChangeListener(newValueEvent -> {
            adjustSendButton();
        });
        styleService.addStyle(timeInForceComboBox);
        // create the first row layout
        firstRowLayout = new HorizontalLayout();
        firstRowLayout.setId(getClass().getCanonicalName() + ".firstRowLayout");
        firstRowLayout.addComponents(brokerComboBox,
                                     sideComboBox,
                                     quantityTextField,
                                     symbolTextField,
                                     orderTypeComboBox,
                                     priceTextField,
                                     timeInForceComboBox);
        styleService.addStyle(firstRowLayout);
        // prepare the second row
        otherLayout = new VerticalLayout();
        styleService.addStyle(otherLayout);
        // account
        accountTextField = new TextField();
        accountTextField.setCaption("Account");
        accountTextField.setId(getClass().getCanonicalName() + ".accountTextField");
        accountTextField.setDescription("FIX field " + quickfix.field.Account.FIELD);
        styleService.addStyle(accountTextField);
        // Ex Destination
        exDestinationTextField = new TextField();
        exDestinationTextField.setCaption("External Destination");
        exDestinationTextField.setId(getClass().getCanonicalName() + ".exDestinationTextField");
        exDestinationTextField.setDescription("FIX field " + quickfix.field.ExDestination.FIELD);
        styleService.addStyle(exDestinationTextField);
        // Max Floor
        maxFloorTextField = new TextField();
        maxFloorTextField.setCaption("Max Floor");
        maxFloorTextField.setId(getClass().getCanonicalName() + ".maxFloorTextField");
        maxFloorTextField.setDescription("FIX field " + quickfix.field.MaxFloor.FIELD);
        styleService.addStyle(maxFloorTextField);
        // peg-to-midpoint
        // peg-to-midpoint selector
        pegToMidpointCheckBox = new CheckBox();
        pegToMidpointCheckBox.setCaption("Peg to Midpoint");
        pegToMidpointCheckBox.setId(getClass().getCanonicalName() + ".pegToMidpointCheckBox");
        pegToMidpointCheckBox.addValueChangeListener(valueEvent -> {
            boolean value = (boolean)(valueEvent.getProperty().getValue());
            pegToMidpointLockedCheckBox.setEnabled(value);
            // enable/disable price if the peg-to-midpoint is selected
            priceTextField.setEnabled(!value);
            if(!value) {
                pegToMidpointLockedCheckBox.setValue(false);
            }
            // TODO need to subscribe to market data (if we have a symbol)
        });
        styleService.addStyle(pegToMidpointCheckBox);
        // peg-to-midpoint locked
        pegToMidpointLockedCheckBox = new CheckBox();
        pegToMidpointLockedCheckBox.setCaption("Locked");
        pegToMidpointLockedCheckBox.setId(getClass().getCanonicalName() + ".pegToMidpointLockedCheckBox");
        pegToMidpointLockedCheckBox.setEnabled(false);
        styleService.addStyle(pegToMidpointLockedCheckBox);
        // peg-to-midpoint layout
        pegToMidpointLayout = new HorizontalLayout();
        pegToMidpointLayout.setId(getClass().getCanonicalName() + ".pegToMidpointLayout");
        styleService.addStyle(pegToMidpointLayout);
        pegToMidpointLayout.addComponents(pegToMidpointCheckBox,
                                          pegToMidpointLockedCheckBox);
        // layout for the first group of fields in the second row
        otherLayout = new VerticalLayout();
        otherLayout.setId(getClass().getCanonicalName() + ".otherLayout");
        otherLayout.addComponents(accountTextField,
                                  exDestinationTextField,
                                  maxFloorTextField,
                                  pegToMidpointLayout);
        // second group of fields in the second row
        // broker algo select
        brokerAlgoComboBox = new ComboBox();
        brokerAlgoComboBox.setCaption("Broker Algo");
        brokerAlgoComboBox.setNullSelectionAllowed(true);
        brokerAlgoComboBox.addValueChangeListener(newValue -> {
            // TODO populate/enable brokerAlgoTagGrid
        });
        brokerAlgoComboBox.setId(getClass().getCanonicalName() + ".brokerAlgoComboBox");
        styleService.addStyle(brokerAlgoComboBox);
        // broker algo tags
        brokerAlgoTagGrid = new Grid();
        brokerAlgoTagGrid.setCaption("Broker Algo Tags");
        brokerAlgoTagGrid.setColumns("Tag","Value","Description");
        brokerAlgoTagGrid.setId(getClass().getCanonicalName() + ".brokerAlgoTagGrid");
        styleService.addStyle(brokerAlgoTagGrid);
        // layout for the second group of fields in the second row
        brokerAlgoLayout = new VerticalLayout();
        brokerAlgoLayout.setId(getClass().getCanonicalName() + ".brokerAlgoLayout");
        brokerAlgoLayout.addComponents(brokerAlgoComboBox,
                                       brokerAlgoTagGrid);
        styleService.addStyle(brokerAlgoLayout);
        // custom fields group
        customFieldsGrid = new Grid();
        customFieldsGrid.setCaption("Custom Fields");
        customFieldsGrid.setColumns("","Key","Value");
        customFieldsGrid.setReadOnly(false);
        customFieldsGrid.setId(getClass().getCanonicalName() + ".customFieldsGrid");
        styleService.addStyle(customFieldsGrid);
        customFieldsLayout = new VerticalLayout();
        customFieldsLayout.setId(getClass().getCanonicalName() + ".customFieldsLayout");
        customFieldsLayout.addComponents(customFieldsGrid);
        styleService.addStyle(customFieldsLayout);
        // create the second row layout
        secondRowLayout = new HorizontalLayout();
        secondRowLayout.setId(getClass().getCanonicalName() + ".secondRowLayout");
        secondRowLayout.addComponents(otherLayout,
                                      brokerAlgoLayout,
                                      customFieldsLayout);
        styleService.addStyle(secondRowLayout);
        // prepare button row
        // send button
        sendButton = new Button();
        sendButton.setCaption("Send");
        sendButton.setClickShortcut(KeyCode.ENTER);
        sendButton.addClickListener(event -> {
            NewOrReplaceOrder newOrder;
            if(replaceExecutionReportOption.isPresent()) {
                newOrder = Factory.getInstance().createOrderReplace(replaceExecutionReportOption.get());
            } else {
                newOrder = Factory.getInstance().createOrderSingle();
            }
            newOrder.setAccount(StringUtils.trimToNull(accountTextField.getValue()));
            if(brokerAlgoComboBox.getValue() != null) {
                BrokerAlgoSpec brokerAlgoSpec = (BrokerAlgoSpec)brokerAlgoComboBox.getValue();
                // TODO
            }
            if(brokerComboBox.getValue() != null) {
                String selectedBroker = String.valueOf(brokerComboBox.getValue());
                if(!AUTO_SELECT_BROKER.equals(selectedBroker)) {
                    ActiveFixSession selectedActiveFixSession = availableBrokers.get(selectedBroker);
                    newOrder.setBrokerID(new BrokerID(selectedActiveFixSession.getFixSession().getBrokerId()));
                }
            }
            // TODO custom fields
//            newOrder.setCustomFields(inCustomFields);
            String maxFloorQtyRaw = StringUtils.trimToNull(maxFloorTextField.getValue());
            if(maxFloorQtyRaw != null) {
                newOrder.setDisplayQuantity(new BigDecimal(maxFloorQtyRaw));
            }
            newOrder.setExecutionDestination(StringUtils.trimToNull(exDestinationTextField.getValue()));
            newOrder.setInstrument(resolvedInstrument);
//            newOrder.setOrderCapacity(inOrderCapacity);
            if(orderTypeComboBox.getValue() != null) {
                newOrder.setOrderType((OrderType)orderTypeComboBox.getValue());
            }
            if(pegToMidpointCheckBox.getValue()) {
                if(!pegToMidpointLockedCheckBox.getValue()) {
                    newOrder.setPegToMidpoint(true);
                }
            }
//            newOrder.setPositionEffect(inPositionEffect);
            String rawPrice = StringUtils.trimToNull(priceTextField.getValue());
            if(rawPrice != null) {
                newOrder.setPrice(new BigDecimal(rawPrice));
            }
            String rawQuantity = StringUtils.trimToNull(quantityTextField.getValue());
            if(rawQuantity != null) {
                newOrder.setQuantity(new BigDecimal(rawQuantity));
            }
            if(sideComboBox.getValue() != null) {
                newOrder.setSide((Side)sideComboBox.getValue());
            }
//            newOrder.setText(inText);
            if(timeInForceComboBox.getValue() != null) {
                newOrder.setTimeInForce((TimeInForce)timeInForceComboBox.getValue());
            }
            SLF4JLoggerProxy.debug(OrderTicketView.this,
                                   "Validating {}",
                                   newOrder);
            try {
                if(replaceExecutionReportOption.isPresent()) {
                    Validations.validate((OrderReplace)newOrder);
                } else {
                    Validations.validate((OrderSingle)newOrder);
                }
            } catch (OrderValidationException e) {
                String errorMessage = PlatformServices.getMessage(e);
                SLF4JLoggerProxy.warn(OrderTicketView.this,
                                      "{} failed validation: {}",
                                      newOrder,
                                      errorMessage);
                Notification.show("Unable to submit: " + errorMessage,
                                  Type.ERROR_MESSAGE);
                sendButton.focus();
                return;
            }
            SLF4JLoggerProxy.info(OrderTicketView.this,
                                  "{} submitting {}",
                                  SessionUser.getCurrentUser(),
                                  newOrder);
            SendOrderResponse response = serviceManager.getService(TradeClientService.class).send(newOrder);
            if(response.getFailed()) {
                Notification.show("Unable to submit: " + response.getOrderId() + " " + response.getMessage(),
                                  Type.ERROR_MESSAGE);
                sendButton.focus();
                return;
            } else {
                Notification.show(response.getOrderId() + " submitted",
                                  Type.TRAY_NOTIFICATION);
                if(replaceExecutionReportOption.isPresent()) {
                    // close containing ticket
                    parent.close();
                } else {
                    // partially clear ticket
                    resetTicket(false);
                }
            }
        });
        sendButton.setEnabled(false);
        sendButton.setId(getClass().getCanonicalName() + ".sendButton");
        styleService.addStyle(sendButton);
        // clear button
        clearButton = new Button();
        clearButton.setCaption("Clear");
        clearButton.setId(getClass().getCanonicalName() + ".clearButton");
        clearButton.addClickListener(event -> {
            resetTicket(true);
        });
        styleService.addStyle(clearButton);
        sendClearLayout = new HorizontalLayout();
        sendClearLayout.setId(getClass().getCanonicalName() + ".sendClearLayout");
        sendClearLayout.addComponents(sendButton,
                                      clearButton);
        styleService.addStyle(sendClearLayout);
        // add the layouts
        addComponents(firstRowLayout,
                      secondRowLayout,
                      sendClearLayout);
        // finish main layout
        setId(getClass().getCanonicalName() + ".contentLayout");
        styleService.addStyle(this);
        if(replaceExecutionReportOption.isPresent()) {
            ExecutionReport replaceExecutionReport = replaceExecutionReportOption.get();
            accountTextField.setValue(replaceExecutionReport.getAccount()==null?"":replaceExecutionReport.getAccount());
            // TODO what if this broker isn't available?
            brokerAlgoComboBox.setValue(replaceExecutionReport.getBrokerId().getValue());
            symbolTextField.setValue(replaceExecutionReport.getInstrument().getFullSymbol());
            quantityTextField.setValue(BigDecimalUtil.render(replaceExecutionReport.getOrderQuantity()));
            orderTypeComboBox.setValue(replaceExecutionReport.getOrderType());
            priceTextField.setValue(BigDecimalUtil.renderCurrency(replaceExecutionReport.getPrice()));
            sideComboBox.setValue(replaceExecutionReport.getSide());
            timeInForceComboBox.setValue(replaceExecutionReport.getTimeInForce());
            // TODO max floor
            // TODO ex destination
            symbolTextField.setReadOnly(true);
            sideComboBox.setReadOnly(true);
            brokerAlgoComboBox.setReadOnly(true);
            clearButton.setVisible(false);
        }
        if(averageFillPriceOption.isPresent()) {
            AverageFillPrice averageFillPrice = averageFillPriceOption.get();
            symbolTextField.setValue(averageFillPrice.getInstrument().getFullSymbol());
            quantityTextField.setValue(BigDecimalUtil.render(averageFillPrice.getCumulativeQuantity()));
            orderTypeComboBox.setValue(OrderType.Limit);
            priceTextField.setValue(BigDecimalUtil.renderCurrency(averageFillPrice.getAveragePrice()));
            sideComboBox.setValue(averageFillPrice.getSide().isBuy()?Side.Sell:Side.Buy);
        }
        if(suggestionOption.isPresent()) {
            Suggestion suggestion = suggestionOption.get();
            if(suggestion instanceof OrderSingleSuggestion) {
                OrderSingleSuggestion orderSingleSuggestion = (OrderSingleSuggestion)suggestion;
                symbolTextField.setValue(orderSingleSuggestion.getOrder().getInstrument().getFullSymbol());
                if(!BigDecimalUtil.isNullOrZero(orderSingleSuggestion.getOrder().getQuantity())) {
                    quantityTextField.setValue(BigDecimalUtil.render(orderSingleSuggestion.getOrder().getQuantity()));
                } else {
                    quantityTextField.focus();
                }
                orderTypeComboBox.setValue(OrderType.Limit);
                priceTextField.setValue(BigDecimalUtil.renderCurrency(orderSingleSuggestion.getOrder().getPrice()));
                sideComboBox.setValue(orderSingleSuggestion.getOrder().getSide());
            }
        }
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
        String activeFixSessionKey = inActiveFixSession.getFixSession().getName();
        synchronized(availableBrokers) {
            if(inActiveFixSession.getStatus().isLoggedOn()) {
                brokersStatusChanged = (availableBrokers.put(activeFixSessionKey,
                                                             inActiveFixSession) == null);
            } else {
                brokersStatusChanged = (availableBrokers.remove(activeFixSessionKey) != null);
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
                    brokerComboBox.removeAllItems();
                    brokerComboBox.addItem(AUTO_SELECT_BROKER);
                    brokerComboBox.addItems(availableBrokers.keySet());
                    if(AUTO_SELECT_BROKER.equals(currentSelectedBroker)) {
                        brokerComboBox.setValue(AUTO_SELECT_BROKER);
                    } else {
                        if(availableBrokers.containsKey(currentSelectedBroker)) {
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
     * Validate and start the object.
     */
    @PostConstruct
    public void start()
    {
        String xmlData = StringUtils.trimToNull(viewProperties.getProperty(ExecutionReport.class.getCanonicalName()));
        ExecutionReport replaceExecutionReport = null;
        if(xmlData != null) {
            try {
                replaceExecutionReport = xmlService.unmarshall(xmlData);
            } catch (JAXBException e) {
                Notification.show("Unable to replace order: " + PlatformServices.getMessage(e),
                                  Type.ERROR_MESSAGE);
            }
        }
        replaceExecutionReportOption = Optional.ofNullable(replaceExecutionReport);
        xmlData = StringUtils.trimToNull(viewProperties.getProperty(AverageFillPrice.class.getCanonicalName()));
        AverageFillPrice averageFillPrice = null;
        if(xmlData != null) {
            try {
                averageFillPrice = xmlService.unmarshall(xmlData);
            } catch (JAXBException e) {
                Notification.show("Unable to trade order: " + PlatformServices.getMessage(e),
                                  Type.ERROR_MESSAGE);
            }
        }
        averageFillPriceOption = Optional.ofNullable(averageFillPrice);
        Suggestion suggestion = null;
        if(event instanceof HasSuggestion) {
            suggestion = ((HasSuggestion)event).getSuggestion();
        }
        suggestionOption = Optional.ofNullable(suggestion);
    }
    /**
     * Create a new OrderTicketView instance.
     *
     * @param inParent a <code>Window</code> value
     * @param inNewWindowEvent a <code>NewWindowEvent</code> value
     * @param inProperties a <code>Properties</code> value
     */
    public OrderTicketView(Window inParent,
                           NewWindowEvent inEvent,
                           Properties inProperties)
    {
        parent = inParent;
        event = inEvent;
        viewProperties = inProperties;
    }
    /**
     * Adjust the send button based on the other fields.
     * 
     * <p>Sets the enabled state of the send button based on the state of the rest of the order ticket.
     */
    private void adjustSendButton()
    {
        boolean enabled = true;
        enabled &= sideComboBox.getValue() != null;
        enabled &= StringUtils.trimToNull(quantityTextField.getValue()) != null;
        enabled &= StringUtils.trimToNull(symbolTextField.getValue()) != null;
        enabled &= orderTypeComboBox.getValue() != null;
        if(enabled) {
            OrderType orderType = (OrderType)orderTypeComboBox.getValue();
            if(!orderType.isMarketOrder()) {
                enabled &= StringUtils.trimToNull(priceTextField.getValue()) != null;
            }
        }
        StringBuilder windowCaption = new StringBuilder();
        if(sideComboBox.getValue() == null) {
            windowCaption.append("Trade");
        } else {
            Side currentSide = (Side)sideComboBox.getValue();
            if(currentSide.isBuy()) {
                windowCaption.append("Buy");
            } else {
                windowCaption.append("Sell");
            }
        }
        if(resolvedInstrument != null) {
            windowCaption.append(" ").append(resolvedInstrument.getFullSymbol());
        }
        // time in force is optional
        // account is optional
        // ex destination is optional
        // max floor is optional
        // peg-to-midpoint is optional
        // broker algo is optional
        // custom fields are optional
        sendButton.setEnabled(enabled);
    }
    /**
     * Reset the ticket, either completely or partially.
     *
     * @param inCompletelyReset a <code>boolean</code> value
     */
    private void resetTicket(boolean inCompletelyReset)
    {
        SLF4JLoggerProxy.debug(this,
                               "Clearing order ticket");
        // this one is always cleared (to prevent accidentally submitting the same order twice)
        quantityTextField.clear();
        if(inCompletelyReset) {
            brokerComboBox.setValue(AUTO_SELECT_BROKER);
            sideComboBox.clear();
            symbolTextField.clear();
            orderTypeComboBox.clear();
            priceTextField.clear();
            timeInForceComboBox.clear();
            accountTextField.clear();
            exDestinationTextField.clear();
            maxFloorTextField.clear();
            pegToMidpointCheckBox.clear();
            pegToMidpointLockedCheckBox.clear();
            brokerAlgoComboBox.clear();
//            brokerAlgoTagGrid.clear();
//            customFieldsGrid.clear();
            brokerComboBox.focus();
        } else {
            quantityTextField.focus();
        }
    }
    private ComboBox brokerComboBox;
    private ComboBox sideComboBox;
    private HorizontalLayout firstRowLayout;
    private HorizontalLayout secondRowLayout;
    private VerticalLayout otherLayout;
    private TextField quantityTextField;
    private TextField symbolTextField;
    private ComboBox orderTypeComboBox;
    private TextField priceTextField;
    private ComboBox timeInForceComboBox;
    private TextField accountTextField;
    private TextField exDestinationTextField;
    private TextField maxFloorTextField;
    private HorizontalLayout pegToMidpointLayout;
    private CheckBox pegToMidpointCheckBox;
    private CheckBox pegToMidpointLockedCheckBox;
    private VerticalLayout brokerAlgoLayout;
    private ComboBox brokerAlgoComboBox;
    private Grid brokerAlgoTagGrid;
    private VerticalLayout customFieldsLayout;
    private Grid customFieldsGrid;
    private Button sendButton;
    private Button clearButton;
    private HorizontalLayout sendClearLayout;
    private Instrument resolvedInstrument;
    /**
     * parent window opened for the content
     */
    private final Window parent;
    /**
     * new window event that caused the view to be opened
     */
    private final NewWindowEvent event;
    /**
     * optional suggestion that might have been included on the new window event
     */
    private Optional<Suggestion> suggestionOption;
    /**
     * optional replace execution report
     */
    private Optional<ExecutionReport> replaceExecutionReportOption;
    /**
     * optional average fill price
     */
    private Optional<AverageFillPrice> averageFillPriceOption;
    /**
     * token to indicate that the broker should be auto-selected
     */
    private final static String AUTO_SELECT_BROKER = "Auto Select";
    /**
     * contains currently available brokers
     */
    @GuardedBy("availableBrokers")
    private final SortedMap<String,ActiveFixSession> availableBrokers = Maps.newTreeMap();
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
    /**
     * provides access to XML services
     */
    @Autowired
    private XmlService xmlService;
    private static final long serialVersionUID = 1779141772237455129L;
}
