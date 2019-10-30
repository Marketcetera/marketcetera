package org.marketcetera.web.trade.orderticket.view;

import java.util.Collections;
import java.util.Properties;
import java.util.Set;

import org.marketcetera.trade.TradePermissions;
import org.marketcetera.web.events.NewWindowEvent;
import org.marketcetera.web.service.WebMessageService;
import org.marketcetera.web.trade.openorders.view.OpenOrderView;
import org.marketcetera.web.trade.openorders.view.TradeContentCategory;
import org.marketcetera.web.view.ContentView;
import org.marketcetera.web.view.ContentViewFactory;
import org.marketcetera.web.view.MenuContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.GrantedAuthority;

import com.google.common.collect.Sets;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;

/* $License$ */

/**
 * Creates {@link OpenOrderView} content objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@SpringComponent
public class OrderTicketViewFactory
    implements ContentViewFactory,MenuContent
{
    /* (non-Javadoc)
     * @see org.marketcetera.web.view.MenuContent#getMenuCaption()
     */
    @Override
    public String getMenuCaption()
    {
        return "Order Ticket";
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.view.MenuContent#getWeight()
     */
    @Override
    public int getWeight()
    {
        return 200;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.view.MenuContent#getCategory()
     */
    @Override
    public MenuContent getCategory()
    {
        return TradeContentCategory.instance;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.view.MenuContent#getMenuIcon()
     */
    @Override
    public Resource getMenuIcon()
    {
        return FontAwesome.BOOK;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.view.MenuContent#getCommand()
     */
    @Override
    public Command getCommand()
    {
        return new MenuBar.Command() {
            @Override
            public void menuSelected(MenuItem inSelectedItem)
            {
                webMessageService.post(new TradeOrderTicketMenuEvent());
            }
            private static final long serialVersionUID = -7269505766947455017L;
        };
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.view.ContentViewFactory#create(java.util.Properties)
     */
    @Override
    public ContentView create(Properties inViewProperties)
    {
        return applicationContext.getBean(OrderTicketView.class,
                                          inViewProperties);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.view.MenuContent#getAllPermissions()
     */
    @Override
    public Set<GrantedAuthority> getAllPermissions()
    {
        return requiredPermissions;
    }
    /**
     * Indicates that an admin type has been selected.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private class TradeOrderTicketMenuEvent
            implements NewWindowEvent
    {
        /* (non-Javadoc)
         * @see org.marketcetera.web.events.MenuEvent#getWindowTitle()
         */
        @Override
        public String getWindowTitle()
        {
            return getMenuCaption();
        }
        /* (non-Javadoc)
         * @see org.marketcetera.web.events.NewWindowEvent#getViewFactory()
         */
        @Override
        public ContentViewFactory getViewFactory()
        {
            return OrderTicketViewFactory.this;
        }
    }
    /**
     * permission(s) required to execute open order view
     */
    private static final Set<GrantedAuthority> requiredPermissions = Collections.unmodifiableSet(Sets.newHashSet(TradePermissions.SendOrderAction,TradePermissions.ViewBrokerStatusAction));
    /**
     * provides access to web message services
     */
    @Autowired
    private WebMessageService webMessageService;
    /**
     * provides access to the application context
     */
    @Autowired
    private ApplicationContext applicationContext;
}
