package org.marketcetera.ui.strategy.view;

import java.net.URL;

import org.marketcetera.ui.view.MenuContent;

/* $License$ */

/**
 * Provides the menu category for strategy actions.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class StrategyContentCategory
        implements MenuContent
{
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.MenuContent#getCaption()
     */
    @Override
    public String getMenuCaption()
    {
        return "Strategy";
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.MenuContent#getWeight()
     */
    @Override
    public int getWeight()
    {
        return 700;
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
    public Runnable getCommand()
    {
        return null;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.MenuContent#getIcon()
     */
    @Override
    public URL getMenuIcon()
    {
        return null;
    }
    /**
     * trade menu category
     */
    public static final StrategyContentCategory instance = new StrategyContentCategory();
}
