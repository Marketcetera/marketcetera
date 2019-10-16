package com.marketcetera.colin.ui.views;

import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;

import com.marketcetera.colin.backend.client.ClientServiceManager;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.server.Command;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.spring.annotation.SpringComponent;

/* $License$ */

/**
 * Provides a logout menu action.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@SpringComponent
public class LogoutMenuItem
        implements MenuContent
{
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.MenuContent#getMenuCaption()
     */
    @Override
    public String getMenuCaption()
    {
        return "Logout";
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.MenuContent#getWeight()
     */
    @Override
    public int getWeight()
    {
        return 10000;
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
     * @see com.marketcetera.web.view.MenuContent#getMenuIcon()
     */
    @Override
    public VaadinIcon getMenuIcon()
    {
        return VaadinIcon.SIGN_OUT;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.MenuContent#getCommand()
     */
    @Override
    public Command getCommand()
    {
        return new Command() {
            @Override
            public void execute()
            {
                SLF4JLoggerProxy.info(LogoutMenuItem.this,
                                      "{} logging out",
                                      "trader");//currentUserFactory.getUser());
                try {
                    clientServiceManager.logout();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                VaadinSession.getCurrent().getSession().invalidate();
                UI.getCurrent().getPage().executeJs("window.location.href=''");
            }
            private static final long serialVersionUID = -4840986259382011275L;
        };
    }
    @Autowired
    private ClientServiceManager clientServiceManager;
}
