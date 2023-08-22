package org.marketcetera.ui.view;

import java.net.URL;

import org.marketcetera.ui.events.CloseWindowsEvent;
import org.springframework.stereotype.Component;


/* $License$ */

/**
 * Provides a menu item to close all open windows.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Component
public class CloseAllWindowsMenuItem
        extends AbstractMenuItem
{
    /* (non-Javadoc)
     * @see org.marketcetera.web.view.MenuContent#getMenuCaption()
     */
    @Override
    public String getMenuCaption()
    {
        return "Close All Windows";
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.view.MenuContent#getWeight()
     */
    @Override
    public int getWeight()
    {
        return 900;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.view.MenuContent#getCategory()
     */
    @Override
    public MenuContent getCategory()
    {
        return WindowContentCategory.instance;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.view.MenuContent#getMenuIcon()
     */
    @Override
    public URL getMenuIcon()
    {
        return getClass().getClassLoader().getResource("images/close.svg");
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.view.MenuContent#getCommand()
     */
    @Override
    public Runnable getCommand()
    {
        return new Runnable() {
            @Override
            public void run()
            {
                webMessageService.post(new CloseWindowsEvent());
            }
        };
    }
//    /**
//     * provides access to message services for the web UI
//     */
//    @Autowired
//    private WebMessageService webMessageService;
}
