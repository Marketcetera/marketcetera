package org.marketcetera.web.trade.avgpx.view;

import java.util.Collections;
import java.util.Set;

import org.marketcetera.trade.TradePermissions;
import org.marketcetera.web.font.MarketceteraFont;
import org.marketcetera.web.trade.executionreport.view.AbstractExecutionReportViewFactory;
import org.marketcetera.web.trade.view.AbstractTradeViewFactory;
import org.marketcetera.web.view.ContentView;
import org.springframework.security.core.GrantedAuthority;

import com.google.common.collect.Sets;
import com.vaadin.server.Resource;
import com.vaadin.spring.annotation.SpringComponent;

/* $License$ */

/**
 * Creates {@link AveragePriceView} content objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@SpringComponent
public class AveragePriceViewFactory
        extends AbstractExecutionReportViewFactory
{
    /* (non-Javadoc)
     * @see org.marketcetera.web.view.MenuContent#getMenuCaption()
     */
    @Override
    public String getMenuCaption()
    {
        return "Average Price";
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.view.MenuContent#getWeight()
     */
    @Override
    public int getWeight()
    {
        return averagePriceWeight;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.view.MenuContent#getMenuIcon()
     */
    @Override
    public Resource getMenuIcon()
    {
        return MarketceteraFont.Average_Price;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.view.MenuContent#getAllPermissions()
     */
    @Override
    public Set<GrantedAuthority> getAllPermissions()
    {
        return requiredPermissions;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.view.AbstractContentViewFactory#getViewType()
     */
    @Override
    protected Class<? extends ContentView> getViewType()
    {
        return AveragePriceView.class;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.trade.view.AbstractTradeViewFactory#getViewFactoryType()
     */
    @Override
    protected Class<? extends AbstractTradeViewFactory> getViewFactoryType()
    {
        return AveragePriceViewFactory.class;
    }
    /**
     * permission(s) required to execute open order view
     */
    private static final Set<GrantedAuthority> requiredPermissions = Collections.unmodifiableSet(Sets.newHashSet(TradePermissions.ViewReportAction));
}
