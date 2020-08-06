package org.marketcetera.web.view.admin;

import org.marketcetera.web.view.ContentView;
import org.marketcetera.web.view.ContentViewFactory;

import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.spring.annotation.SpringComponent;

/* $License$ */

/**
 * Creates {@link UserView} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@SpringComponent
public class UserViewFactory
        extends AbstractAdminViewFactory
{
    /* (non-Javadoc)
     * @see org.marketcetera.web.view.MenuContent#getMenuCaption()
     */
    @Override
    public String getMenuCaption()
    {
        return "Users";
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
        return FontAwesome.USERS;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.view.admin.AbstractAdminViewFactory#getViewName()
     */
    @Override
    protected String getViewName()
    {
        return "Users";
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.view.AbstractContentViewFactory#getViewType()
     */
    @Override
    protected Class<? extends ContentView> getViewType()
    {
        return UserView.class;
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
