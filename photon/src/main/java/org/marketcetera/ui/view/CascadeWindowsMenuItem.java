package org.marketcetera.ui.view;

import java.net.URL;

import org.marketcetera.ui.events.CascadeWindowsEvent;
import org.springframework.stereotype.Component;


/* $License$ */

/**
 * Provides a menu item to cascade open windows.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Component
public class CascadeWindowsMenuItem
        extends AbstractMenuItem
{
    /* (non-Javadoc)
     * @see org.marketcetera.web.view.MenuContent#getMenuCaption()
     */
    @Override
    public String getMenuCaption()
    {
        return "Cascade Windows";
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
        return getClass().getClassLoader().getResource("images/Cascade_Windows.svg");
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
                webMessageService.post(new CascadeWindowsEvent());
            }
        };
    }
//    /**
//     * provides access to message services for the web UI
//     */
//    @Autowired
//    private WebMessageService webMessageService;
}
