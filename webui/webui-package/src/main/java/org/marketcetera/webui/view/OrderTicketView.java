package org.marketcetera.webui.view;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.server.Resource;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.UI;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@SpringView(name=OrderTicketView.NAME)
public class OrderTicketView
        implements View, MenuContent
{
    /* (non-Javadoc)
     * @see org.marketcetera.webui.view.MenuContent#getMenuCaption()
     */
    @Override
    public String getMenuCaption()
    {
        return "Order Ticket";
    }
    /* (non-Javadoc)
     * @see org.marketcetera.webui.view.MenuContent#getWeight()
     */
    @Override
    public int getWeight()
    {
        return 100;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.webui.view.MenuContent#getCategory()
     */
    @Override
    public MenuContent getCategory()
    {
        return null;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.webui.view.MenuContent#getMenuIcon()
     */
    @Override
    public Resource getMenuIcon()
    {
        return VaadinIcons.HOME;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.webui.view.MenuContent#getCommand()
     */
    @Override
    public Command getCommand()
    {
        return new MenuBar.Command() {
            @Override
            public void menuSelected(MenuItem inSelectedItem)
            {
                UI.getCurrent().getNavigator().navigateTo(NAME);
            }
            private static final long serialVersionUID = -8828829733639273416L;
        };
    }
    /**
     * view identifier
     */
    public static final String NAME = "Order Ticket";
    private static final long serialVersionUID = 6475017611278180983L;
}
