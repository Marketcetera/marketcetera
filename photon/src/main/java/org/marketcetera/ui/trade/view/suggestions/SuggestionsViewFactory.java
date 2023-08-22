package org.marketcetera.ui.trade.view.suggestions;

import java.net.URL;
import java.util.Collections;
import java.util.Set;

import org.marketcetera.trade.TradePermissions;
import org.marketcetera.ui.trade.view.AbstractTradeViewFactory;
import org.marketcetera.ui.view.ContentView;
import org.marketcetera.ui.view.MenuContent;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.google.common.collect.Sets;

/* $License$ */

/**
 * Creates new {@link SuggestionsView} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Component
public class SuggestionsViewFactory
        extends AbstractTradeViewFactory
        implements MenuContent
{
    /* (non-Javadoc)
     * @see org.marketcetera.ui.trade.view.AbstractTradeViewFactory#getViewFactoryType()
     */
    @Override
    protected Class<? extends AbstractTradeViewFactory> getViewFactoryType()
    {
        return SuggestionsViewFactory.class;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ui.trade.view.AbstractTradeViewFactory#getViewName()
     */
    @Override
    protected String getViewName()
    {
        return getMenuCaption();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ui.view.MenuContent#getMenuCaption()
     */
    @Override
    public String getMenuCaption()
    {
        return "Trade Suggestions";
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ui.view.MenuContent#getWeight()
     */
    @Override
    public int getWeight()
    {
        return tradeSuggestionsWeight;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ui.view.MenuContent#getMenuIcon()
     */
    @Override
    public URL getMenuIcon()
    {
        return getClass().getClassLoader().getResource("images/light-bulb.svg");
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ui.view.AbstractContentViewFactory#getViewType()
     */
    @Override
    protected Class<? extends ContentView> getViewType()
    {
        return SuggestionsView.class;
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
     * permission(s) required to execute strategy session view
     */
    private static final Set<GrantedAuthority> requiredPermissions = Collections.unmodifiableSet(Sets.newHashSet(TradePermissions.ViewSuggestionsAction));
}
