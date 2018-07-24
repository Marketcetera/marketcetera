package org.marketcetera.web.view;

import java.util.Comparator;

import com.vaadin.server.Resource;
import com.vaadin.ui.MenuBar;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface MenuContent
{
    /**
     * 
     *
     *
     * @return
     */
    String getMenuCaption();
    /**
     * 
     *
     *
     * @return
     */
    int getWeight();
    /**
     * 
     *
     *
     * @return
     */
    MenuContent getCategory();
    /**
     * 
     *
     *
     * @return
     */
    Resource getMenuIcon();
    /**
     * 
     *
     *
     * @return
     */
    MenuBar.Command getCommand();
    /**
     * 
     */
    static Comparator<MenuContent> comparator = new Comparator<MenuContent>() {
        @Override
        public int compare(MenuContent inO1,
                           MenuContent inO2)
        {
            return new Integer(inO1.getWeight()).compareTo(inO2.getWeight());
        }
    };
    
}
