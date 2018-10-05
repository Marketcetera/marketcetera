package org.marketcetera.web.view.admin;

import java.util.Properties;

import org.marketcetera.web.view.ContentView;

import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.spring.annotation.SpringComponent;

/* $License$ */

/**
 *
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
     * @see org.marketcetera.web.view.ContentViewFactory#create(java.util.Properties)
     */
    @Override
    public ContentView create(Properties inViewProperties)
    {
        RoleView roleView = new RoleView(inViewProperties);
        roleView.setWebMessageService(webMessageService);
        return roleView;
    }
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
}
