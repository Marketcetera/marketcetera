package org.marketcetera.web.view.admin;

import org.marketcetera.admin.Role;
import org.marketcetera.web.view.ContentView;
import org.marketcetera.web.view.ContentViewFactory;

import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.spring.annotation.SpringComponent;

/* $License$ */

/**
 * Provides a UI view for {@link Role} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@SpringComponent
public class RoleViewFactory
        extends AbstractAdminViewFactory
{
    /* (non-Javadoc)
     * @see org.marketcetera.web.view.MenuContent#getMenuCaption()
     */
    @Override
    public String getMenuCaption()
    {
        return "Roles";
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
    public Resource getMenuIcon()
    {
        return FontAwesome.GROUP;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.view.admin.AbstractAdminViewFactory#getViewName()
     */
    @Override
    protected String getViewName()
    {
        return "Roles";
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.view.AbstractContentViewFactory#getViewType()
     */
    @Override
    protected Class<? extends ContentView> getViewType()
    {
        return RoleView.class;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.view.admin.AbstractAdminViewFactory#getViewFactory()
     */
    @Override
    protected Class<? extends ContentViewFactory> getViewFactory()
    {
        return getClass();
    }
}
