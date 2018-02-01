package org.marketcetera.webui.view;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.marketcetera.algo.BrokerAlgoSpec;
import org.marketcetera.algo.BrokerAlgoTag;
import org.marketcetera.brokers.BrokerStatus;
import org.marketcetera.brokers.BrokerStatusListener;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.OrderType;
import org.marketcetera.trade.Side;
import org.marketcetera.trade.TimeInForce;
import org.marketcetera.trade.client.TradeClient;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.webui.service.TradeClientService;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.server.Resource;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/* $License$ */

/**
 *
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
        // side
        sideSelect = new ComboBox<>("Side",
                                    EnumSet.complementOf(EnumSet.of(Side.Unknown)));
        sideSelect.setPlaceholder("Select side");
        sideSelect.setItemCaptionGenerator(Side::name);
        sideSelect.setEmptySelectionAllowed(false);
        sideSelect.setEmptySelectionCaption("Select side");
        sideSelect.setRequiredIndicatorVisible(true);
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
                new BigDecimal(newValue);
            } catch (Exception e) {
                quantity.setValue(inValue.getOldValue());
            }
        });
        row1.addComponent(quantity);
        // symbol
        symbol = new TextField("Symbol");
        symbol.setPlaceholder("Enter symbol");
        symbol.setRequiredIndicatorVisible(true);
        symbol.addValueChangeListener(inValue -> {
            String newValue = StringUtils.trimToNull(String.valueOf(inValue.getValue()));
            if(newValue == null) {
                symbol.setDescription("");
                return;
            }
            Instrument instrument = resolvedSymbols.getUnchecked(newValue);
            if(instrument == null) {
                symbol.setDescription("");
            } else {
                symbol.setDescription(instrument.getSecurityType() + ": " + instrument.getFullSymbol());
            }
        });
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
        });
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
        });
        row1.addComponent(price);
        // tif
        timeInForceSelect = new ComboBox<>("Time in Force",
                                           EnumSet.complementOf(EnumSet.of(TimeInForce.Unknown)));
        timeInForceSelect.setPlaceholder("Select time in force");
        timeInForceSelect.setItemCaptionGenerator(TimeInForce::name);
        timeInForceSelect.setEmptySelectionAllowed(true);
        timeInForceSelect.setEmptySelectionCaption("Select time in force");
        timeInForceSelect.setRequiredIndicatorVisible(false);
        row1.addComponent(timeInForceSelect);
        // account
        account = new TextField("Account");
        account.setPlaceholder("Enter order account");
        account.setRequiredIndicatorVisible(false);
        row2Block1.addComponent(account);
        // ex destination
        exDestination = new TextField("External Destination");
        exDestination.setPlaceholder("Enter external destination");
        exDestination.setRequiredIndicatorVisible(false);
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
        pegLayout.addComponent(pegToMidpoint);
        pegToMidpointLocked = new CheckBox("Locked",
                                           false);
        pegToMidpointLocked.setValue(false);
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
        buttonLayout.addComponent(sendButton);
        buttonLayout.setEnabled(false);
        // clear button
        clearButton = new Button("Clear");
        clearButton.setEnabled(true);
        clearButton.setVisible(true);
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
                               "{} received {} {}",
                               this,
                               inStatus,
                               inStatus.getClass());
        if(inStatus.getLoggedOn()) {
            SLF4JLoggerProxy.debug(this,
                                   "Adding {}",
                                   inStatus);
            brokerCollection.add(inStatus);
        } else {
            SLF4JLoggerProxy.debug(this,
                                   "Removing {}",
                                   inStatus);
            brokerCollection.remove(inStatus);
        }
        brokerSelect.setItems(brokerCollection);
    }
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
