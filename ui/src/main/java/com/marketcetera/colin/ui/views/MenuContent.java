package com.marketcetera.colin.ui.views;

import java.util.Comparator;

import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.server.Command;

/* $License$ */

/**
 * Provides a menu entry.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface MenuContent
{
    /**
     * Get the caption value.
     *
     * @return a <code>String</code> value
     */
    String getMenuCaption();
    /**
     * Get the menu item weight.
     *
     * @return an <code>int</code> value
     */
    int getWeight();
    /**
     * Get the menu category value.
     *
     * @return a <code>MenuContent</code> value
     */
    MenuContent getCategory();
    /**
     * Get the menu icon value.
     *
     * @return a <code>Resource</code> value
     */
    VaadinIcon getMenuIcon();
    /**
     * Get the command value to execute.
     *
     * @return a <code>Command</code> value
     */
    Command getCommand();
    /**
     * static comparator used to compare menu items
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
