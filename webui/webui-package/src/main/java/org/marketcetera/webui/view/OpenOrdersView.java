package org.marketcetera.webui.view;

import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.marketcetera.trade.OrderSummary;
import org.marketcetera.trade.TradeMessage;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.webui.service.TradeClientService;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.server.Resource;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Grid;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.renderers.DateRenderer;

/* $License$ */

/**
 * Provides a view of open orders.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@SpringView(name=OpenOrdersView.NAME)
public class OpenOrdersView
        extends VerticalLayout
        implements View,MenuContent
{
    /* (non-Javadoc)
     * @see org.marketcetera.webui.view.MenuContent#getMenuCaption()
     */
    @Override
    public String getMenuCaption()
    {
        return "Open Orders";
    }
    /* (non-Javadoc)
     * @see org.marketcetera.webui.view.MenuContent#getWeight()
     */
    @Override
    public int getWeight()
    {
        return 200;
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
        return VaadinIcons.CART;
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
                subWindow.setContent(OpenOrdersView.this);
                subWindow.center();
                subWindow.setHeightUndefined();
                subWindow.setWidthUndefined();
                UI.getCurrent().addWindow(subWindow);
            }
            private static final long serialVersionUID = -8828829733639273416L;
        };
    }
    /* (non-Javadoc)
     * @see com.vaadin.ui.AbstractComponent#attach()
     */
    @Override
    public void attach()
    {
        openOrderGrid = new Grid<>(OrderSummary.class);
        openOrderGrid.setDataProvider((sortOrders,offset,limit) -> {
            Map<String,Boolean> sortOrder = sortOrders.stream().collect(Collectors.toMap(sort -> sort.getSorted(),
                                                                                          sort -> SortDirection.ASCENDING.equals(sort.getDirection())));
            return tradeClientService.findAllOpenOrders(offset,limit,sortOrder).stream();
        },
                                      () -> tradeClientService.countAllOpenOrders());
        openOrderGrid.setSizeFull();
        openOrderGrid.setColumnReorderingAllowed(true);
        openOrderGrid.removeAllColumns();
        DateRenderer dateRenderer = new DateRenderer("%1$tY%1$tm%1$td-%1$tT.%1$tL",Locale.ENGLISH);
        openOrderGrid.addColumn(OrderSummary::getSendingTime,dateRenderer).setId("OpenOrderSendingTime").setCaption("Sending Time");
        openOrderGrid.addColumn(OrderSummary::getOrderId).setId("OpenOrderOrderId").setCaption("ClOrdId");
        openOrderGrid.addColumn(OrderSummary::getOrderStatus).setId("OpenOrderOrderStatus").setCaption("OrdStatus");
        openOrderGrid.addColumn(OrderSummary::getSide).setId("OpenOrderOrderSide").setCaption("Side");
        openOrderGrid.addColumn(OrderSummary::getInstrument).setId("OpenOrderOrderInstrument").setCaption("Symbol");
        openOrderGrid.addColumn(OrderSummary::getOrderQuantity).setId("OpenOrderOrderQuantity").setCaption("OrderQty");
        openOrderGrid.addColumn(OrderSummary::getCumulativeQuantity).setId("OpenOrderCumulativeQuantity").setCaption("CumQty");
        openOrderGrid.addColumn(OrderSummary::getLeavesQuantity).setId("OpenOrderLeavesQuantity").setCaption("LeavesQty");
        openOrderGrid.addColumn(OrderSummary::getOrderPrice).setId("OpenOrderOrderPrice").setCaption("OrderPrice");
        openOrderGrid.addColumn(OrderSummary::getAveragePrice).setId("OpenOrderAveragePrice").setCaption("AvgPx");
        openOrderGrid.addColumn(OrderSummary::getAccount).setId("OpenOrderAccount").setCaption("Account");
        openOrderGrid.addColumn(OrderSummary::getLastQuantity).setId("OpenOrderLastQuantity").setCaption("LastQty");
        openOrderGrid.addColumn(OrderSummary::getLastPrice).setId("OpenOrderLastPrice").setCaption("LastPx");
        openOrderGrid.addColumn(OrderSummary::getBrokerId).setId("OpenOrderBrokerId").setCaption("BrokerID");
        openOrderGrid.addColumn(OrderSummary::getActor).setId("OpenOrderActor").setCaption("Trader");
        openOrderGrid.getColumns().stream().forEach(column -> column.setHidable(true));
        addComponent(openOrderGrid);
        tradeMessageSubscriber = new TradeMessageSubscriber();
        eventBus.register(tradeMessageSubscriber);
    }
    /* (non-Javadoc)
     * @see com.vaadin.ui.AbstractComponent#detach()
     */
    @Override
    public void detach()
    {
        if(tradeMessageSubscriber != null) {
            eventBus.unregister(tradeMessageSubscriber);
        }
    }
    /**
     * Listens for incoming trade messages.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    public class TradeMessageSubscriber
    {
        /**
         * Receive trade messages.
         *
         * @param inTradeMessage a <code>TradeMessage</code> value
         */
        @Subscribe
        public void receiveTradeMessage(TradeMessage inTradeMessage)
        {
            SLF4JLoggerProxy.trace(OpenOrdersView.this,
                                   "Received {}",
                                   inTradeMessage);
            openOrderGrid.getDataProvider().refreshAll();
        }
    }
    /**
     * 
     */
    private Grid<OrderSummary> openOrderGrid;
    /**
     * subscribes to trade messages
     */
    private TradeMessageSubscriber tradeMessageSubscriber;
    /**
     * provides access to trade services
     */
    @Autowired
    private TradeClientService tradeClientService;
    /**
     * provides access to pushed event data
     */
    @Autowired
    private EventBus eventBus;
    /**
     * view identifier
     */
    public static final String NAME = "Open Orders";
    private static final long serialVersionUID = -1065341933671284744L;
}
