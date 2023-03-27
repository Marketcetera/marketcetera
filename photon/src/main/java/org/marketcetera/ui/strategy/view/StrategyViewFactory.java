package org.marketcetera.ui.strategy.view;

import java.net.URL;
import java.util.Collections;
import java.util.Set;

import org.marketcetera.core.Pair;
import org.marketcetera.strategy.StrategyPermissions;
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
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Component
public class StrategyViewFactory
        extends AbstractContentViewFactory
        implements MenuContent
{
    /* (non-Javadoc)
     * @see org.marketcetera.ui.view.MenuContent#getMenuCaption()
     */
    @Override
    public String getMenuCaption()
    {
        return "Strategy Control";
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ui.view.MenuContent#getWeight()
     */
    @Override
    public int getWeight()
    {
        return 100;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ui.view.MenuContent#getCategory()
     */
    @Override
    public MenuContent getCategory()
    {
        return StrategyContentCategory.instance;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ui.view.MenuContent#getMenuIcon()
     */
    @Override
    public URL getMenuIcon()
    {
        return getClass().getClassLoader().getResource("images/puzzle-piece.svg");
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
                        return Pair.create(530.0,400.0);
                    }
                    @Override
                    public String getWindowTitle()
                    {
                        return getMenuCaption();
                    }
                    @Override
                    public Class<? extends ContentViewFactory> getViewFactoryType()
                    {
                        return StrategyViewFactory.class;
                    }}
                );
            }
        };
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ui.view.AbstractContentViewFactory#getViewType()
     */
    @Override
    protected Class<? extends ContentView> getViewType()
    {
        return StrategyView.class;
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
    private static final Set<GrantedAuthority> requiredPermissions = Collections.unmodifiableSet(Sets.newHashSet(StrategyPermissions.ReadStrategyAction));
}
