package org.marketcetera.ui.view;

import java.net.URL;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.annotation.PostConstruct;

import org.marketcetera.ui.PhotonServices;
import org.marketcetera.ui.service.AuthorizationHelperService;
import org.marketcetera.ui.service.SessionUser;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;


/* $License$ */

/**
 * Builds a top-level menu for the entire application.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ApplicationMenu
{
    /**
     * Validate and start the object.
     */
    @PostConstruct
    public void start()
    {
        initMenu();
    }
    /**
     * Refresh the menu permissions.
     */
    public void refreshMenuPermissions()
    {
        SessionUser currentUser = SessionUser.getCurrent();
        if(currentUser == null) {
            menu.setVisible(false);
            return;
        }
        // there is a current user, already logged in, the menu should now be visible
        menu.setVisible(true);
        // examine over the top level content items
        for(MenuItemMetaData topLevelContentItem : topLevelContent) {
            // it's possible to define permissions for top-level menu items independently from the child items
            evaluatePermissions(topLevelContentItem.getAllPermissions(),
                                SessionUser.getCurrent().getPermissions(),
                                topLevelContentItem.getMenuItem());
            // if the user has permissions to view the top level contents, examine the children, if nay
            if(topLevelContentItem.getMenuItem().isVisible()) {
                // if no children are visible, we're going to hide the top-level menu, too
                boolean atLeastOneChildVisible = false;
                // examine the children (may be empty)
                for(MenuItemMetaData childContentItem : topLevelContentItem.getChildItems()) {
                    evaluatePermissions(childContentItem.getAllPermissions(),
                                        SessionUser.getCurrent().getPermissions(),
                                        childContentItem.getMenuItem());
                    // track whether at least one child item is visible
                    atLeastOneChildVisible |= childContentItem.getMenuItem().isVisible();
                }
                // if there is at least one child item, then, at least one child item must be visible or we're going to hide the top-level item
                if(!topLevelContentItem.getChildItems().isEmpty()) {
                    topLevelContentItem.getMenuItem().setVisible(atLeastOneChildVisible);
                }
            }
        }
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
    private void initMenu()
    {
        // TODO need recursion - this method handles two levels of menus only
        // collect all the contents and organize them by category, if any category is present
        SortedMap<MenuContent,SortedSet<MenuContent>> categoryContent = new TreeMap<>(MenuContent.comparator);
        for(Map.Entry<String,MenuContent> entry : applicationContext.getBeansOfType(MenuContent.class).entrySet()) {
            MenuContent contentItem = entry.getValue();
            MenuContent contentCategory = contentItem.getCategory();
            if(contentCategory == null) {
                // add this item to the top-level menu set - this will show in the main menu
                topLevelContent.add(new MenuItemMetaData(contentItem));
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
                topLevelContent.add(new MenuItemMetaData(contentCategory));
            }
        }
        // all menu items have now been categorized and sorted, go back through and create the menu bar
        menu = new MenuBar();
        // the top-level menu is in topLevelContent, some of the items may be categories
        for(MenuItemMetaData topLevelContentItem : topLevelContent) {
            // this item may be a parent or a leaf
            Menu parent = new Menu();
            parent.setText(topLevelContentItem.getMenuCaption());
            if(topLevelContentItem.getMenuIcon() != null) {
                // TODO it might be better w/o the top-level icons?
//                parent.setGraphic(PhotonServices.getSvgResource(topLevelContentItem.getMenuIcon()));
            }
            menu.getMenus().add(parent);
            topLevelContentItem.setMenuItem(parent);
            SortedSet<MenuContent> childItems = categoryContent.get(topLevelContentItem);
            if(childItems == null || childItems.isEmpty()) {
                // special handling is required for menus that have no menu items (top level menu choices, like logout)
                MenuItem placeholderMenuItem = new MenuItem();
                parent.getItems().add(placeholderMenuItem);
                parent.showingProperty().addListener((observableValue, oldValue, newValue) -> {
                                                       if (newValue) {
                                                           // the first menuItem is triggered
                                                           parent.getItems().get(0).fire();
                                                       }
                                                   }
                                               );
                placeholderMenuItem.setOnAction(e -> topLevelContentItem.getCommand().run());
            } else {
                for(MenuContent childItem : childItems) {
                    MenuItem newChildItem = new MenuItem();
                    newChildItem.setText(childItem.getMenuCaption());
                    if(childItem.getMenuIcon() != null) {
                        newChildItem.setGraphic(PhotonServices.getSvgResource(childItem.getMenuIcon(),
                                                                              0.75));
                    }
                    newChildItem.setOnAction(e -> childItem.getCommand().run());
                    parent.getItems().add(newChildItem);
                    MenuItemMetaData newChildItemMetaData = new MenuItemMetaData(childItem);
                    newChildItemMetaData.setMenuItem(newChildItem);
                    topLevelContentItem.getChildItems().add(newChildItemMetaData);
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
     * Evaluate the permissions for the given menu item and set the menu item to be visible accordingly.
     *
     * @param inRequiredPermissions a <code>Set&lt;GrantedAuthority&gt;</code> value
     * @param inActualPermissions a <code>Set&lt;GrantedAuthority&gt;</code> value
     * @param inMenuItem a <code>MenuItem</code> value
     */
    private void evaluatePermissions(Set<GrantedAuthority> inRequiredPermissions,
                                     Set<GrantedAuthority> inActualPermissions,
                                     MenuItem inMenuItem)
    {
        inMenuItem.setVisible(true);
        if(inRequiredPermissions != null) {
            for(GrantedAuthority requiredPermission : inRequiredPermissions) {
                if(!authzHelperService.hasPermission(requiredPermission)) {
                    SLF4JLoggerProxy.trace(this,
                                           "Cannot display {}, {} not in {}",
                                           inMenuItem.getText(),
                                           requiredPermission,
                                           inActualPermissions);
                    inMenuItem.setVisible(false);
                    break;
                }
            }
        }
    }
    /**
     * Holds meta data for a menu item.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private class MenuItemMetaData
            implements Comparable<MenuItemMetaData>,MenuContent
    {
        /* (non-Javadoc)
         * @see org.marketcetera.web.view.MenuContent#getAllPermissions()
         */
        @Override
        public Set<GrantedAuthority> getAllPermissions()
        {
            return menuContent.getAllPermissions();
        }
        /* (non-Javadoc)
         * @see org.marketcetera.web.view.MenuContent#getMenuCaption()
         */
        @Override
        public String getMenuCaption()
        {
            return menuContent.getMenuCaption();
        }
        /* (non-Javadoc)
         * @see org.marketcetera.web.view.MenuContent#getWeight()
         */
        @Override
        public int getWeight()
        {
            return menuContent.getWeight();
        }
        /* (non-Javadoc)
         * @see org.marketcetera.web.view.MenuContent#getCategory()
         */
        @Override
        public MenuContent getCategory()
        {
            return menuContent.getCategory();
        }
        /* (non-Javadoc)
         * @see org.marketcetera.web.view.MenuContent#getMenuIcon()
         */
        @Override
        public URL getMenuIcon()
        {
            return menuContent.getMenuIcon();
        }
        /* (non-Javadoc)
         * @see org.marketcetera.web.view.MenuContent#getCommand()
         */
        @Override
        public Runnable getCommand()
        {
            return menuContent.getCommand();
        }
        /* (non-Javadoc)
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
        @Override
        public int compareTo(MenuItemMetaData inO)
        {
            return Integer.valueOf(getMenuContent().getWeight()).compareTo(inO.getMenuContent().getWeight());
        }
        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            return new StringBuilder().append(menuContent.getMenuCaption()).append(" children: ").append(childItems).toString();
        }
        /**
         * Get the menuContent value.
         *
         * @return a <code>MenuContent</code> value
         */
        private MenuContent getMenuContent()
        {
            return menuContent;
        }
        /**
         * Get the childItems value.
         *
         * @return a <code>SortedSet&lt;MenuContent&gt;</code> value
         */
        private SortedSet<MenuItemMetaData> getChildItems()
        {
            return childItems;
        }
        /**
         * Get the menu item value.
         *
         * @return a <code>MenuItem</code> value
         */
        private MenuItem getMenuItem()
        {
            return menuItem;
        }
        /**
         * Create a new MenuItemMetaData instance.
         *
         * @param inMenuContent a <code>MenuContent</code> value
         */
        private MenuItemMetaData(MenuContent inMenuContent)
        {
            menuContent = inMenuContent;
        }
        /**
         * Set the menu item value.
         *
         * @param inMenuItem a <code>MenuItem</code> value
         */
        private void setMenuItem(MenuItem inMenuItem)
        {
            menuItem = inMenuItem;
        }
        /**
         * menu item value
         */
        private MenuItem menuItem;
        /**
         * menu content value
         */
        private final MenuContent menuContent;
        /**
         * child menu items, may be empty but will never be <code>null</code>
         */
        private final SortedSet<MenuItemMetaData> childItems = new TreeSet<>();
    }
    /**
     * top level content, sorted by menu item precedence
     */
    private SortedSet<MenuItemMetaData> topLevelContent = new TreeSet<>();
    /**
     * provides help resolving permissions
     */
    @Autowired
    private AuthorizationHelperService authzHelperService;
    /**
     * provides the application context
     */
    @Autowired
    private ApplicationContext applicationContext;
    /**
     * menu display widget that contains all the menu items
     */
    private MenuBar menu;
}
