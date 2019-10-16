package com.marketcetera.colin.ui.views;

import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.server.Command;

/* $License$ */

/**
 * Provides the menu category for admin actions.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class AdminContentCategory
        implements MenuContent
{
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.MenuContent#getCaption()
     */
    @Override
    public String getMenuCaption()
    {
        return "Admin";
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.MenuContent#getWeight()
     */
    @Override
    public int getWeight()
    {
        return 100;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.MenuContent#getCategory()
     */
    @Override
    public MenuContent getCategory()
    {
        return null;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.MenuContent#getCommand()
     */
    @Override
    public Command getCommand()
    {
        return null;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.MenuContent#getIcon()
     */
    @Override
    public VaadinIcon getMenuIcon()
    {
        return null;
    }
    /**
     * admin menu category
     */
    public static final AdminContentCategory instance = new AdminContentCategory();
}
