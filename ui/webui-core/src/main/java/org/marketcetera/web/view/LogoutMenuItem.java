package org.marketcetera.web.view;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.web.SessionUser;
import org.marketcetera.web.events.LogoutEvent;
import org.marketcetera.web.font.MarketceteraFont;
import org.marketcetera.web.service.WebMessageService;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.server.Resource;
import com.vaadin.server.VaadinSession;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.UI;

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
    public Resource getMenuIcon()
    {
        return MarketceteraFont.Logout;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.MenuContent#getCommand()
     */
    @Override
    public Command getCommand()
    {
        return new MenuBar.Command() {
            @Override
            public void menuSelected(MenuItem inSelectedItem)
            {
                SLF4JLoggerProxy.info(LogoutMenuItem.this,
                                      "{} logging out",
                                      SessionUser.getCurrentUser());
                try {
                    webMessageService.post(new LogoutEvent());
                } catch (Exception e) {
                    SLF4JLoggerProxy.warn(LogoutMenuItem.this,
                                          "Problem occurred while logging out: {}",
                                          ExceptionUtils.getRootCauseMessage(e));
                }
                VaadinSession.getCurrent().setAttribute(SessionUser.class,
                                                        null);
                UI.getCurrent().getNavigator().navigateTo(MainView.NAME);
            }
            private static final long serialVersionUID = -4840986259382011275L;
        };
    }
    /**
     * provides access to web message services
     */
    @Autowired
    private WebMessageService webMessageService;
}
