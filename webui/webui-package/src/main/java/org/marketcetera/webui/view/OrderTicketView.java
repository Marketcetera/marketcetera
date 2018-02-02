package org.marketcetera.webui.view;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.marketcetera.algo.BrokerAlgoSpec;
import org.marketcetera.algo.BrokerAlgoTag;
import org.marketcetera.brokers.BrokerStatus;
import org.marketcetera.brokers.BrokerStatusListener;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.trade.OrderType;
import org.marketcetera.trade.Side;
import org.marketcetera.trade.TimeInForce;
import org.marketcetera.trade.client.SendOrderResponse;
import org.marketcetera.trade.client.TradeClient;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.webui.service.TradeClientService;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.vaadin.data.HasValue;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.server.Resource;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/* $License$ */

/**
 * Provides a view to manage order tickets.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@SpringView(name=OrderTicketView.NAME)
public class OrderTicketView
        extends VerticalLayout
        implements View, MenuContent, BrokerStatusListener
{
    /* (non-Javadoc)
     * @see org.marketcetera.webui.view.MenuContent#getMenuCaption()
     */
    @Override
    public String getMenuCaption()
    {
        return "Order Ticket";
    }
    /* (non-Javadoc)
     * @see org.marketcetera.webui.view.MenuContent#getWeight()
     */
    @Override
    public int getWeight()
    {
        return 100;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.webui.view.MenuContent#getCategory()
     */
    @Override
    public MenuContent getCategory()
    {
        return null;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.webui.view.MenuContent#getMenuIcon()
     */
    @Override
    public Resource getMenuIcon()
    {
        return VaadinIcons.SHOP;
    }
    /* (non-Javadoc)
     * @see com.vaadin.ui.AbstractComponent#attach()
     */
    @Override
    public void attach()
    {
        final Collection<HasValue<?>> clearOnClearComponents = Lists.newArrayList();
        // TODO move this earlier in the lifecycle?
        TradeClient tradeClient = tradeClientService.getTradeClient();
        tradeClient.addBrokerStatusListener(this);
        HorizontalLayout row1 = new HorizontalLayout();
        addComponent(row1);
        HorizontalLayout row2 = new HorizontalLayout();
        addComponent(row2);
        VerticalLayout row2Block1 = new VerticalLayout();
        row2.addComponent(row2Block1);
        HorizontalLayout buttonLayout = new HorizontalLayout();
        addComponent(buttonLayout);
        // broker
        brokerSelect = new ComboBox<>("Broker",
                                      brokerCollection);
        brokerSelect.setPlaceholder("Auto Select");
        brokerSelect.setItemCaptionGenerator(BrokerStatus::getName);
        brokerSelect.setEmptySelectionAllowed(true);
        brokerSelect.setEmptySelectionCaption("Auto Select");
        row1.addComponent(brokerSelect);
        clearOnClearComponents.add(brokerSelect);
        // side
        sideSelect = new ComboBox<>("Side",
                                    EnumSet.complementOf(EnumSet.of(Side.Unknown)));
        sideSelect.setPlaceholder("Select side");
        sideSelect.setItemCaptionGenerator(Side::name);
        sideSelect.setEmptySelectionAllowed(false);
        sideSelect.setEmptySelectionCaption("Select side");
        sideSelect.setRequiredIndicatorVisible(true);
        sideSelect.addValueChangeListener(inValue -> {
            sendButton.setEnabled(enableSendButton());
        });
        clearOnClearComponents.add(sideSelect);
        row1.addComponent(sideSelect);
        // quantity
        quantity = new TextField("Quantity");
        quantity.setPlaceholder("Enter order quantity");
        quantity.setRequiredIndicatorVisible(true);
        quantity.addValueChangeListener(inValue -> {
            String newValue = StringUtils.trimToNull(String.valueOf(inValue.getValue()));
            if(newValue == null) {
                return;
            }
            try {
                Integer.parseInt(newValue);
            } catch (Exception e) {
                quantity.setValue(inValue.getOldValue());
            }
            sendButton.setEnabled(enableSendButton());
        });
        clearOnClearComponents.add(quantity);
        row1.addComponent(quantity);
        // symbol
        symbol = new TextField("Symbol");
        symbol.setPlaceholder("Enter symbol");
        symbol.setRequiredIndicatorVisible(true);
        symbol.addValueChangeListener(inValue -> {
            String newValue = StringUtils.trimToNull(String.valueOf(inValue.getValue()));
            if(newValue == null) {
                symbol.setDescription("");
                resolvedInstrument = null;
                return;
            }
            resolvedInstrument = resolvedSymbols.getUnchecked(newValue);
            if(resolvedInstrument == null) {
                symbol.setDescription("");
                Notification.show("Symbol Resolution Failed",
                                  "Unable to resolve " + newValue + " to an instrument",
                                  Notification.Type.ERROR_MESSAGE);
            } else {
                symbol.setDescription(resolvedInstrument.getSecurityType() + ": " + resolvedInstrument.getFullSymbol());
            }
            sendButton.setEnabled(enableSendButton());
        });
        clearOnClearComponents.add(symbol);
        row1.addComponent(symbol);
        // order type
        orderTypeSelect = new ComboBox<>("OrderType",
                                         EnumSet.complementOf(EnumSet.of(OrderType.Unknown)));
        orderTypeSelect.setPlaceholder("Select order type");
        orderTypeSelect.setItemCaptionGenerator(OrderType::name);
        orderTypeSelect.setEmptySelectionAllowed(true);
        orderTypeSelect.setEmptySelectionCaption("Select order type");
        orderTypeSelect.setRequiredIndicatorVisible(false);
        orderTypeSelect.addValueChangeListener(inValue -> {
            if(inValue.getValue() != null && inValue.getValue().isMarketOrder()) {
                price.setVisible(false);
                price.setRequiredIndicatorVisible(false);
            } else {
                price.setVisible(true);
                price.setRequiredIndicatorVisible(true);
            }
            sendButton.setEnabled(enableSendButton());
        });
        clearOnClearComponents.add(orderTypeSelect);
        row1.addComponent(orderTypeSelect);
        // price
        price = new TextField("Price");
        price.setPlaceholder("Enter order price");
        price.setRequiredIndicatorVisible(true);
        price.addValueChangeListener(inValue -> {
            String newValue = StringUtils.trimToNull(String.valueOf(inValue.getValue()));
            if(newValue == null) {
                return;
            }
            try {
                new BigDecimal(newValue);
            } catch (Exception e) {
                price.setValue(inValue.getOldValue());
            }
            sendButton.setEnabled(enableSendButton());
        });
        clearOnClearComponents.add(price);
        row1.addComponent(price);
        // tif
        timeInForceSelect = new ComboBox<>("Time in Force",
                                           EnumSet.complementOf(EnumSet.of(TimeInForce.Unknown)));
        timeInForceSelect.setPlaceholder("Select time in force");
        timeInForceSelect.setItemCaptionGenerator(TimeInForce::name);
        timeInForceSelect.setEmptySelectionAllowed(true);
        timeInForceSelect.setEmptySelectionCaption("Select time in force");
        timeInForceSelect.setRequiredIndicatorVisible(false);
        clearOnClearComponents.add(timeInForceSelect);
        row1.addComponent(timeInForceSelect);
        // account
        account = new TextField("Account");
        account.setPlaceholder("Enter order account");
        account.setRequiredIndicatorVisible(false);
        clearOnClearComponents.add(account);
        row2Block1.addComponent(account);
        // ex destination
        exDestination = new TextField("External Destination");
        exDestination.setPlaceholder("Enter external destination");
        exDestination.setRequiredIndicatorVisible(false);
        clearOnClearComponents.add(exDestination);
        row2Block1.addComponent(exDestination);
        // max floor
        maxFloor = new TextField("Max Floor");
        maxFloor.setPlaceholder("Enter max floor quantity");
        maxFloor.setRequiredIndicatorVisible(false);
        maxFloor.addValueChangeListener(inValue -> {
            String newValue = StringUtils.trimToNull(String.valueOf(inValue.getValue()));
            if(newValue == null) {
                return;
            }
            try {
                Integer.parseInt(newValue);
            } catch (Exception e) {
                maxFloor.setValue(inValue.getOldValue());
            }
        });
        clearOnClearComponents.add(maxFloor);
        row2Block1.addComponent(maxFloor);
        // create a horizontal layout just for the peg indicators
        HorizontalLayout pegLayout = new HorizontalLayout();
        pegToMidpoint = new CheckBox("Peg to Midpoint",
                                     false);
        pegToMidpoint.addValueChangeListener(inValue -> {
            if(inValue.getValue()) {
                pegToMidpointLocked.setVisible(true);
            } else {
                pegToMidpointLocked.setVisible(false);
            }
        });
        clearOnClearComponents.add(pegToMidpoint);
        pegLayout.addComponent(pegToMidpoint);
        pegToMidpointLocked = new CheckBox("Locked",
                                           false);
        pegToMidpointLocked.setValue(false);
        clearOnClearComponents.add(pegToMidpointLocked);
        pegLayout.addComponent(pegToMidpointLocked);
        row2Block1.addComponent(pegLayout);
        // broker algos
        VerticalLayout row2Block2Layout = new VerticalLayout();
        row2.addComponent(row2Block2Layout);
        // broker algo selector
        brokerAlgoSelect = new ComboBox<>("Broker Algo");
        brokerAlgoSelect.setPlaceholder("Select broker algo");
        brokerAlgoSelect.setItemCaptionGenerator(BrokerAlgoSpec::getName);
        brokerAlgoSelect.setEmptySelectionAllowed(true);
        brokerAlgoSelect.setEmptySelectionCaption("Select broker algo");
        brokerAlgoSelect.setRequiredIndicatorVisible(false);
        clearOnClearComponents.add(brokerAlgoSelect);
        row2Block2Layout.addComponent(brokerAlgoSelect);
        // broker algo grid
        brokerAlgoTagGrid = new Grid<>();
        brokerAlgoTagGrid.addColumn(BrokerAlgoTag::getLabel)
                .setId("BrokerAlgoTagLabelColumn")
                .setCaption("Tag");
        brokerAlgoTagGrid.addColumn(BrokerAlgoTag::getValue)
                .setId("BrokerAlgoTagValueColumn")
                .setCaption("Value");
        brokerAlgoTagGrid.addColumn(algoTag -> algoTag.getTagSpec().getDescription()).setCaption("Description").setId("BrokerAlgoTagDescriptionColumn");
//        clearableComponents.add(brokerAlgoTagGrid); TODO
        row2Block2Layout.addComponent(brokerAlgoTagGrid);
        // custom fields
        VerticalLayout row2Block3Layout = new VerticalLayout();
        row2.addComponent(row2Block3Layout);
        row2Block3Layout.addComponent(new Label("Custom Fields"));
        customFieldsGrid = new Grid<>();
        customFieldsGrid.addColumn(Map.Entry::getKey).setId("Key").setCaption("Key");
        customFieldsGrid.addColumn(Map.Entry::getValue).setId("Value").setCaption("Value");
        row2Block3Layout.addComponent(customFieldsGrid);
        // send button
        sendButton = new Button("Send");
        sendButton.setEnabled(false);
        sendButton.addClickListener(inValue -> {
            if(sendOrder()) {
                quantity.clear();
            }
            sendButton.setEnabled(enableSendButton());
        });
        buttonLayout.addComponent(sendButton);
        // clear button
        clearButton = new Button("Clear");
        clearButton.setEnabled(true);
        clearButton.addClickListener(inValue -> {
           clearOnClearComponents.stream().forEach(component->component.clear());
           brokerAlgoTagGrid.setItems(Collections.emptyList());
           // TODO unclick all items in the custom fields grid, but don't empty
        });
        buttonLayout.addComponent(clearButton);
    }
    /* (non-Javadoc)
     * @see com.vaadin.ui.AbstractComponent#detach()
     */
    @Override
    public void detach()
    {
        tradeClientService.getTradeClient().removeBrokerStatusListener(this);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.webui.view.MenuContent#getCommand()
     */
    @Override
    public Command getCommand()
    {
        return new MenuBar.Command() {
            @Override
            public void menuSelected(MenuItem inSelectedItem)
            {
                Window subWindow = new Window(NAME);
                subWindow.setContent(OrderTicketView.this);
                subWindow.center();
                subWindow.setHeightUndefined();
                subWindow.setWidthUndefined();
                UI.getCurrent().addWindow(subWindow);
            }
            private static final long serialVersionUID = -8828829733639273416L;
        };
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.BrokerStatusListener#receiveBrokerStatus(org.marketcetera.brokers.BrokerStatus)
     */
    @Override
    public void receiveBrokerStatus(BrokerStatus inStatus)
    {
        SLF4JLoggerProxy.debug(this,
                               "{} received {}",
                               this,
                               inStatus);
        try {
            UI.getCurrent().access(new Runnable() {
                @Override
                public void run()
                {
                    if(inStatus.getLoggedOn()) {
                        SLF4JLoggerProxy.debug(OrderTicketView.this,
                                               "Adding {}",
                                               inStatus);
                        brokerCollection.add(inStatus);
                    } else {
                        SLF4JLoggerProxy.debug(OrderTicketView.this,
                                               "Removing {}",
                                               inStatus);
                        brokerCollection.remove(inStatus);
                    }
                    brokerSelect.setItems(brokerCollection);
                }}
            );
        } catch (Exception e) {
            SLF4JLoggerProxy.warn(this,
                                  e);
        }
    }
    private boolean sendOrder()
    {
        OrderSingle order = Factory.getInstance().createOrderSingle();
        try {
            if(!account.isEmpty()) {
                order.setAccount(StringUtils.trimToNull(account.getValue()));
            }
            if(!brokerAlgoSelect.isEmpty()) {
                // TODO 
            }
            if(!brokerSelect.isEmpty()) {
                order.setBrokerID(brokerSelect.getValue().getId());
            }
            if(!exDestination.isEmpty()) {
                order.setExecutionDestination(StringUtils.trimToNull(exDestination.getValue()));
            }
            if(!maxFloor.isEmpty()) {
                order.setDisplayQuantity(new BigDecimal(StringUtils.trimToNull(maxFloor.getValue())));
            }
            if(!orderTypeSelect.isEmpty()) {
                order.setOrderType(orderTypeSelect.getValue());
            }
            if(pegToMidpointLocked.getValue()) {
                // this means use the current price, don't set pegToMidpoint
                order.setPegToMidpoint(false);
            } else {
                if(pegToMidpoint.getValue()) {
                    order.setPegToMidpoint(true);
                }
            }
            if(!price.isEmpty()) {
                order.setPrice(new BigDecimal(StringUtils.trimToNull(price.getValue())));
            }
            if(!quantity.isEmpty()) {
                order.setQuantity(new BigDecimal(StringUtils.trimToNull(quantity.getValue())));
            }
            if(!sideSelect.isEmpty()) {
                order.setSide(sideSelect.getValue());
            }
            if(resolvedInstrument != null) {
                order.setInstrument(resolvedInstrument);
            }
            if(!timeInForceSelect.isEmpty()) {
                order.setTimeInForce(timeInForceSelect.getValue());
            }
            SendOrderResponse response = tradeClientService.getTradeClient().sendOrder(order);
            SLF4JLoggerProxy.debug(this,
                                   "Order send received: {}",
                                   response);
            if(response.getFailed()) {
                Notification.show("Order Send Failed",
                                  response.getMessage(),
                                  Notification.Type.ERROR_MESSAGE);
                return false;
            } else {
                Notification.show("Order " + order.getOrderID() + " sent",
                                  "",
                                  Notification.Type.TRAY_NOTIFICATION);
            }
        } catch (Exception e) {
            SLF4JLoggerProxy.warn(this,
                                  e);
            Notification.show("Order Send Failed",
                              PlatformServices.getMessage(e),
                              Notification.Type.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
    /**
     * 
     *
     *
     * @return
     */
    private boolean enableSendButton()
    {
        if(orderTypeSelect.isEmpty() || quantity.isEmpty() || sideSelect.isEmpty() || symbol.isEmpty() || resolvedInstrument == null) {
            return false;
        }
        if(!orderTypeSelect.getValue().isMarketOrder() && price.isEmpty()) {
            return false;
        }
        return true;
    }
    /**
     * 
     */
    @Autowired
    private TradeClientService tradeClientService;
    /**
     * holds brokers
     */
    private final Collection<BrokerStatus> brokerCollection = new TreeSet<>();
    /**
     * 
     */
    private ComboBox<BrokerStatus> brokerSelect;
    /**
     * 
     */
    private ComboBox<Side> sideSelect;
    /**
     * 
     */
    private ComboBox<OrderType> orderTypeSelect;
    /**
     * 
     */
    private ComboBox<TimeInForce> timeInForceSelect;
    /**
     * 
     */
    private ComboBox<BrokerAlgoSpec> brokerAlgoSelect;
    /**
     * 
     */
    private Grid<BrokerAlgoTag> brokerAlgoTagGrid;
    /**
     * 
     */
    private Grid<Map.Entry<String,String>> customFieldsGrid;
    /**
     * 
     */
    private TextField quantity;
    /**
     * 
     */
    private TextField price;
    /**
     * 
     */
    private TextField symbol;
    /**
     * 
     */
    private TextField account;
    /**
     * 
     */
    private TextField exDestination;
    /**
     * 
     */
    private TextField maxFloor;
    /**
     * 
     */
    private CheckBox pegToMidpoint;
    /**
     * 
     */
    private CheckBox pegToMidpointLocked;
    /**
     * 
     */
    private Button sendButton;
    /**
     * 
     */
    private Instrument resolvedInstrument;
    /**
     * 
     */
    private Button clearButton;
    /**
     * view identifier
     */
    public static final String NAME = "Order Ticket";
    private final LoadingCache<String,Instrument> resolvedSymbols = CacheBuilder.newBuilder().expireAfterAccess(60,TimeUnit.SECONDS).build(new CacheLoader<String,Instrument>() {
        @Override
        public Instrument load(String inKey)
                throws Exception
        {
            Instrument instrument = tradeClientService.getTradeClient().resolveSymbol(inKey);
            return instrument;
        }}
    );
    private static final long serialVersionUID = 6475017611278180983L;
}
