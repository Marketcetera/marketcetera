package org.marketcetera.webui.view;

import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.webui.config.AppConfiguration;
import org.springframework.context.ApplicationContext;

import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.themes.ValoTheme;

/* $License$ */

/**
 * Builds a top-level menu for the entire application.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class ApplicationMenu
{
    /**
     * Create a new ApplicationMenu instance.
     */
    public ApplicationMenu()
    {
        applicationContext = AppConfiguration.getApplicationContext();
        initMenu();
    }
    /**
     * Get the menu value.
     *
     * @return a <code>MenuBar</code> value
     */
    public MenuBar getMenu()
    {
        return menu;
    }
    /**
     * Initialize the menu bar with application content buttons.
     */
    private final void initMenu()
    {
        // TODO need recursion - this method handles two levels of menus only
        // collect all the contents and organize them by category, if any category is present
        SortedSet<MenuContent> topLevelContent = new TreeSet<>(MenuContent.comparator);
        SortedMap<MenuContent,SortedSet<MenuContent>> categoryContent = new TreeMap<>(MenuContent.comparator);
        for(Map.Entry<String,MenuContent> entry : applicationContext.getBeansOfType(MenuContent.class).entrySet()) {
            SLF4JLoggerProxy.debug(this,
                                   "Adding menu entry {}",
                                   entry);
            MenuContent contentItem = entry.getValue();
            MenuContent contentCategory = contentItem.getCategory();
            if(contentCategory == null) {
                // add this item to the top-level menu set - this will show in the main menu
                topLevelContent.add(contentItem);
            } else {
                // this item belongs to a category, make sure there's a set for that category and add the item to it
                SortedSet<MenuContent> contentForCategory = categoryContent.get(contentCategory);
                if(contentForCategory == null) {
                    contentForCategory = new TreeSet<>(MenuContent.comparator);
                    categoryContent.put(contentCategory,
                                        contentForCategory);
                }
                contentForCategory.add(contentItem);
                // now add the category itself to the top-level menu (might already be there, doesn't matter because it's a set)
                topLevelContent.add(contentCategory);
            }
        }
        // all menu items have now been categorized and sorted, go back through and create the menu bar
        menu = new MenuBar();
        menu.setStyleName(ValoTheme.MENUBAR_BORDERLESS);
        // the top-level menu is in topLevelContent, some of the items may be categories
        for(MenuContent topLevelContentItem : topLevelContent) {
            // this item may be a parent or a leaf
            MenuItem parent = menu.addItem(topLevelContentItem.getMenuCaption(),
                                           topLevelContentItem.getMenuIcon(),
                                           topLevelContentItem.getCommand());
            SortedSet<MenuContent> childItems = categoryContent.get(topLevelContentItem);
            if(childItems != null) {
                for(MenuContent childItem : childItems) {
                    parent.addItem(childItem.getMenuCaption(),
                                   childItem.getMenuIcon(),
                                   childItem.getCommand());
                }
            }
        }
    }
    /**
     * 
     */
    private ApplicationContext applicationContext;
    /**
     * 
     */
    private MenuBar menu;
}
