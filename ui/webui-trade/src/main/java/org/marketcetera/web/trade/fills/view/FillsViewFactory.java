package org.marketcetera.web.trade.fills.view;

import java.util.Collections;
import java.util.Properties;
import java.util.Set;

import org.marketcetera.trade.TradePermissions;
import org.marketcetera.web.trade.executionreport.AbstractExecutionReportViewFactory;
import org.marketcetera.web.trade.openorders.view.OpenOrderView;
import org.springframework.security.core.GrantedAuthority;

import com.google.common.collect.Sets;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Window;

/* $License$ */

/**
 * Creates {@link OpenOrderView} content objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@SpringComponent
public class FillsViewFactory
        extends AbstractExecutionReportViewFactory
{
    /* (non-Javadoc)
     * @see org.marketcetera.web.view.ContentViewFactory#create(com.vaadin.ui.Window, java.util.Properties)
     */
    @Override
    public FillsView create(Window inParent,
                            Properties inViewProperties)
    {
        return applicationContext.getBean(FillsView.class,
                                          inViewProperties);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.view.MenuContent#getMenuCaption()
     */
    @Override
    public String getMenuCaption()
    {
        return "Fills";
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.view.MenuContent#getWeight()
     */
    @Override
    public int getWeight()
    {
        return 400;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.view.MenuContent#getMenuIcon()
     */
    @Override
    public Resource getMenuIcon()
    {
        return FontAwesome.SHOPPING_BASKET;
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
    private static final Set<GrantedAuthority> requiredPermissions = Collections.unmodifiableSet(Sets.newHashSet(TradePermissions.ViewReportAction));
}
