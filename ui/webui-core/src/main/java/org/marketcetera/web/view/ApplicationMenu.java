package org.marketcetera.web.view;

import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

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
     *
     * @param inApplicationContext an <code>ApplicationContext</code> value
     */
    public ApplicationMenu(ApplicationContext inApplicationContext)
    {
        applicationContext = inApplicationContext;
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
        
        
//        SortedSet<MenuContent> noCategoryContent = new TreeSet<>(categoryComparator);
//        SortedMap<MenuContent,SortedSet<MenuContent>> sortedContent = new TreeMap<>(categoryComparator);
//        for(Map.Entry<String,MenuContent> entry : applicationContext.getBeansOfType(MenuContent.class).entrySet()) {
//            final MenuContent contentView = entry.getValue();
//            SLF4JLoggerProxy.debug(this,
//                                   "Creating menu entry for {}",
//                                   contentView.getLabel());
//            MenuContent viewCategory = contentView.getCategory();
//            if(viewCategory == null) {
//                noCategoryContent.add(contentView);
//            } else {
//                SortedSet<MenuContent> viewsForCategory = sortedContent.get(viewCategory);
//                if(viewsForCategory == null) {
//                    viewsForCategory = new TreeSet<>(categoryComparator);
//                    sortedContent.put(viewCategory,
//                                      viewsForCategory);
//                }
//                viewsForCategory.add(contentView);
//            }
//        }
//        menu.addItem("Home",
//                     FontAwesome.HOME,
//                     new MenuBar.Command() {
//            @Override
//            public void menuSelected(MenuItem inSelectedItem)
//            {
//                SLF4JLoggerProxy.debug(ApplicationMenu.this,
//                                       "Navigating to {}",
//                                       MainView.NAME);
//                UI.getCurrent().getNavigator().navigateTo(MainView.NAME);
//            }
//            private static final long serialVersionUID = -4840986259382011275L;
//        });
////        // add all items with no category
////        for(final MenuContent contentView : noCategoryContent) {
////            SLF4JLoggerProxy.debug(this,
////                                   "Adding menu item for content view w/o category: {}",
////                                   contentView.getLabel());
////            menu.addItem(contentView.getLabel(),contentView.getIcon(),new MenuBar.Command() {
////                @Override
////                public void menuSelected(MenuItem inSelectedItem)
////                {
////                    SLF4JLoggerProxy.debug(ApplicationMenu.this,
////                                           "Navigating to {}",
////                                           contentView.getViewName());
////                    UI.getCurrent().getNavigator().navigateTo(contentView.getViewName());
////                }
////                private static final long serialVersionUID = 2624855188645792796L;
////            });
////        }
////        // add all items with a category
////        for(Map.Entry<MenuCategory,SortedSet<ContentView>> entry : sortedContent.entrySet()) {
////            final MenuCategory contentCategory = entry.getKey();
////            final SortedSet<ContentView> viewsForCategory = entry.getValue();
////            MenuItem parent = menu.addItem(contentCategory.getLabel(),
////                                           contentCategory.getIcon(),
////                                           null);
////            for(final ContentView contentView : viewsForCategory) {
////                SLF4JLoggerProxy.debug(this,
////                                       "Adding menu item for content category {}: {}",
////                                       contentView.getCategory().getLabel(),
////                                       contentView.getLabel());
////                parent.addItem(contentView.getLabel(),
////                               contentView.getIcon(),
////                               new MenuBar.Command() {
////                    @Override
////                    public void menuSelected(MenuItem inSelectedItem)
////                    {
////                        SLF4JLoggerProxy.debug(ApplicationMenu.this,
////                                               "Navigating to {}",
////                                               contentView.getViewName());
////                        UI.getCurrent().getNavigator().navigateTo(contentView.getViewName());
////                    }
////                    private static final long serialVersionUID = 2624855188645792796L;
////                });
////            }
////        }
//        menu.addItem("Log Out",
//                     new MenuBar.Command() {
//            @Override
//            public void menuSelected(MenuItem inSelectedItem)
//            {
//                SLF4JLoggerProxy.debug(ApplicationMenu.this,
//                                       "Logging out");
//                VaadinSession.getCurrent().setAttribute("user",
//                                                        null);
//                UI.getCurrent().getNavigator().navigateTo(MainView.NAME);
//            }
//            private static final long serialVersionUID = -4840986259382011275L;
//        });
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
