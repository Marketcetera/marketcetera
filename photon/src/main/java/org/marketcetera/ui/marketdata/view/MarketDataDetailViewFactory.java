package org.marketcetera.ui.marketdata.view;

import java.net.URL;
import java.util.Collections;
import java.util.Set;

import org.marketcetera.core.Pair;
import org.marketcetera.marketdata.MarketDataPermissions;
import org.marketcetera.ui.events.NewWindowEvent;
import org.marketcetera.ui.view.AbstractContentViewFactory;
import org.marketcetera.ui.view.ContentView;
import org.marketcetera.ui.view.ContentViewFactory;
import org.marketcetera.ui.view.MenuContent;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.google.common.collect.Sets;

/* $License$ */

/**
 * Creates {@link MarketDataDetailView} content objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Component
public class MarketDataDetailViewFactory
        extends AbstractContentViewFactory
        implements MenuContent
{
    /* (non-Javadoc)
     * @see org.marketcetera.web.view.MenuContent#getMenuCaption()
     */
    @Override
    public String getMenuCaption()
    {
        return "Detail";
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
     * @see org.marketcetera.web.view.MenuContent#getMenuIcon()
     */
    @Override
    public URL getMenuIcon()
    {
        return getClass().getClassLoader().getResource("images/Market_Data.svg");
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ui.view.MenuContent#getCategory()
     */
    @Override
    public MenuContent getCategory()
    {
        return MarketDataContentCategory.instance;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ui.view.MenuContent#getCommand()
     */
    @Override
    public Runnable getCommand()
    {
        return new Runnable() {
            @Override
            public void run()
            {
                webMessageService.post(new NewWindowEvent() {
                    /* (non-Javadoc)
                     * @see org.marketcetera.ui.events.NewWindowEvent#getWindowSize()
                     */
                    @Override
                    public Pair<Double,Double> getWindowSize()
                    {
                        return Pair.create(635.0,365.0);
                    }
                    @Override
                    public String getWindowTitle()
                    {
                        return getMenuCaption();
                    }
                    @Override
                    public Class<? extends ContentViewFactory> getViewFactoryType()
                    {
                        return MarketDataDetailViewFactory.class;
                    }}
                );
            }
        };
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
        return MarketDataDetailView.class;
    }
    /**
     * permission(s) required to execute fix session view
     */
    private static final Set<GrantedAuthority> requiredPermissions = Collections.unmodifiableSet(Sets.newHashSet(MarketDataPermissions.RequestMarketDataAction));
}
