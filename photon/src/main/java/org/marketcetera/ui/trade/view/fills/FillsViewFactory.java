package org.marketcetera.ui.trade.view.fills;

import java.util.Collections;
import java.util.Set;

import org.marketcetera.trade.TradePermissions;
import org.marketcetera.ui.trade.view.AbstractTradeViewFactory;
import org.marketcetera.ui.view.ContentView;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.google.common.collect.Sets;

import javafx.scene.image.Image;

/* $License$ */

/**
 * Creates {@link FillsView} content objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Component
public class FillsViewFactory
        extends AbstractTradeViewFactory
{
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
        return fillsWeight;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.view.MenuContent#getMenuIcon()
     */
    @Override
    public Image getMenuIcon()
    {
        return getIcon("images/Fills.png");
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
     * @see org.marketcetera.web.trade.openorders.view.AbstractTradeViewFactory#getViewName()
     */
    @Override
    protected String getViewName()
    {
        return getMenuCaption();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.view.AbstractContentViewFactory#getViewType()
     */
    @Override
    protected Class<? extends ContentView> getViewType()
    {
        return FillsView.class;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.trade.view.AbstractTradeViewFactory#getViewFactoryType()
     */
    @Override
    protected Class<? extends AbstractTradeViewFactory> getViewFactoryType()
    {
        return FillsViewFactory.class;
    }
//    /* (non-Javadoc)
//     * @see org.marketcetera.web.trade.openorders.view.AbstractTradeViewFactory#getWindowSize()
//     */
//    @Override
//    protected Pair<Double,Double> getWindowSize()
//    {
//        return Pair.create(800.0, 
//                           200.0);
//    }
    /**
     * permission(s) required to execute fills view
     */
    private static final Set<GrantedAuthority> requiredPermissions = Collections.unmodifiableSet(Sets.newHashSet(TradePermissions.ViewReportAction));
}
