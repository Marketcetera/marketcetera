package org.marketcetera.web.trade.openorders.view;

import java.util.Collections;
import java.util.Properties;
import java.util.Set;

import org.marketcetera.trade.TradePermissions;
import org.marketcetera.web.view.ContentView;
import org.springframework.security.core.GrantedAuthority;

import com.google.common.collect.Sets;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.spring.annotation.SpringComponent;

/* $License$ */

/**
 * Creates {@link OpenOrderView} content objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@SpringComponent
public class OpenOrderViewFactory
        extends AbstractTradeViewFactory
{
    /* (non-Javadoc)
     * @see org.marketcetera.web.view.ContentViewFactory#create(java.util.Properties)
     */
    @Override
    public ContentView create(Properties inViewProperties)
    {
        OpenOrderView userView = new OpenOrderView(inViewProperties);
        userView.setWebMessageService(webMessageService);
        return userView;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.view.MenuContent#getMenuCaption()
     */
    @Override
    public String getMenuCaption()
    {
        return "Open Orders";
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.view.MenuContent#getWeight()
     */
    @Override
    public int getWeight()
    {
        return 100;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.view.MenuContent#getMenuIcon()
     */
    @Override
    public Resource getMenuIcon()
    {
        return FontAwesome.MONEY;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.view.admin.AbstractAdminViewFactory#getViewName()
     */
    @Override
    protected String getViewName()
    {
        return "Open Orders";
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
     * permission(s) required to execute open order view
     */
    private static final Set<GrantedAuthority> requiredPermissions = Collections.unmodifiableSet(Sets.newHashSet(TradePermissions.ViewOpenOrdersAction));
}
