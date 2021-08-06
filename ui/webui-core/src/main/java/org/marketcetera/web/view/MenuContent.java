package org.marketcetera.web.view;

import java.util.Collections;
import java.util.Comparator;
import java.util.Set;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.springframework.security.core.GrantedAuthority;

import com.vaadin.server.Resource;
import com.vaadin.ui.MenuBar;

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
     * Get the necessary permissions to display this menu item.
     *
     * <p>The current user must have ALL OF the given permissions to display the menu item. An empty set indicates that no permissions are required.
     * 
     * @return a <code>Set&lt;GrantedAuthority&gt;</code> value
     */
    default Set<GrantedAuthority> getAllPermissions()
    {
        return Collections.emptySet();
    }
    /**
     * Get the menu icon value.
     *
     * @return a <code>Resource</code> value
     */
    Resource getMenuIcon();
    /**
     * Get the command value to execute.
     *
     * @return a <code>MenuBar.Command</code> value
     */
    MenuBar.Command getCommand();
    /**
     * static comparator used to compare menu items
     */
    static Comparator<MenuContent> comparator = new Comparator<MenuContent>() {
        @Override
        public int compare(MenuContent inO1,
                           MenuContent inO2)
        {
            return new CompareToBuilder()
                    .append(inO1.getWeight(),inO2.getWeight())
                    .append(inO1.getMenuCaption(),inO2.getMenuCaption()).toComparison();
        }
    };
}
