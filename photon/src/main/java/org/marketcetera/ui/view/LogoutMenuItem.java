package org.marketcetera.ui.view;

import javax.annotation.PostConstruct;

import org.marketcetera.ui.events.LogoutEvent;
import org.marketcetera.ui.service.SessionUser;
import org.marketcetera.ui.service.WebMessageService;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.scene.image.Image;

/* $License$ */

/**
 * Provides a logout menu action.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Component
public class LogoutMenuItem
        implements MenuContent
{
    @PostConstruct
    public void start()
    {
        icon = new Image("images/Logout.png");
    }
    private Image icon;
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
    public Image getMenuIcon()
    {
        return icon;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.MenuContent#getCommand()
     */
    @Override
    public Runnable getCommand()
    {
        return new Runnable() {
            @Override
            public void run()
            {
                SLF4JLoggerProxy.info(LogoutMenuItem.this,
                                      "{} logging out",
                                      SessionUser.getCurrent());
                webMessageService.post(new LogoutEvent());
            }
        };
    }
    /**
     * provides access to web message services
     */
    @Autowired
    private WebMessageService webMessageService;
}
