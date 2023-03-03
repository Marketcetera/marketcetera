package org.marketcetera.ui.fix.view;

import java.util.Collections;
import java.util.Set;

import org.marketcetera.admin.AdminPermissions;
import org.marketcetera.core.Pair;
import org.marketcetera.ui.PhotonServices;
import org.marketcetera.ui.events.NewWindowEvent;
import org.marketcetera.ui.service.WebMessageService;
import org.marketcetera.ui.view.AbstractContentViewFactory;
import org.marketcetera.ui.view.ContentView;
import org.marketcetera.ui.view.ContentViewFactory;
import org.marketcetera.ui.view.MenuContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.google.common.collect.Sets;

import javafx.scene.image.Image;

/* $License$ */

/**
 * Creates {@link FixSessionView} content objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Component
public class FixSessionViewFactory
        extends AbstractContentViewFactory
        implements MenuContent
{
    /* (non-Javadoc)
     * @see org.marketcetera.web.view.MenuContent#getMenuCaption()
     */
    @Override
    public String getMenuCaption()
    {
        return "FIX Sessions";
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.view.MenuContent#getWeight()
     */
    @Override
    public int getWeight()
    {
        return 300;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.view.MenuContent#getMenuIcon()
     */
    @Override
    public Image getMenuIcon()
    {
        return PhotonServices.getIcon("images/FIX_Sessions.png");
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ui.view.MenuContent#getCategory()
     */
    @Override
    public MenuContent getCategory()
    {
        return null;
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
                    public Pair<Double, Double> getWindowSize()
                    {
                        return Pair.create(500.0,
                                           225.0);
                    }
                    @Override
                    public String getWindowTitle()
                    {
                        return getMenuCaption();
                    }
                    @Override
                    public Class<? extends ContentViewFactory> getViewFactoryType()
                    {
                        return FixSessionViewFactory.class;
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
        return FixSessionView.class;
    }
    /**
     * permission(s) required to execute open order view
     */
    private static final Set<GrantedAuthority> requiredPermissions = Collections.unmodifiableSet(Sets.newHashSet(AdminPermissions.ViewSessionAction));
    /**
     * provides access to web message services
     */
    @Autowired
    private WebMessageService webMessageService;
}
