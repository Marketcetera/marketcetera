package org.marketcetera.web.service;

import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.marketcetera.core.Util;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.web.SessionUser;
import org.marketcetera.web.events.LoginEvent;
import org.marketcetera.web.events.LogoutEvent;
import org.marketcetera.web.events.NewWindowEvent;
import org.marketcetera.web.events.TileWindowsEvent;
import org.marketcetera.web.view.ContentView;
import org.marketcetera.web.view.ContentViewFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Sets;
import com.google.common.eventbus.Subscribe;
import com.vaadin.shared.ui.window.WindowMode;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

/* $License$ */

/**
 * Manages window views in the UI.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Component
public class WindowManagerService
{
    /**
     * Validate and start the object.
     */
    @PostConstruct
    public void start()
    {
        SLF4JLoggerProxy.info(this,
                              "Starting window manager service");
        webMessageService.register(this);
    }
    /**
     * Stop the object.
     */
    @PreDestroy
    public void stop()
    {
        SLF4JLoggerProxy.info(this,
                              "Stopping window manager service");
        webMessageService.unregister(this);
    }
    /**
     * Receive user login events.
     *
     * @param inEvent a <code>LoginEvent</code> value
     */
    @Subscribe
    public void receiveLoginEvent(LoginEvent inEvent)
    {
        Properties displayLayout = displayLayoutService.getDisplayLayout();
        SLF4JLoggerProxy.debug(this,
                               "Received {}, retrieved display layout: {}",
                               inEvent,
                               displayLayout);
        WindowRegistry windowRegistry = getCurrentUserRegistry();
        windowRegistry.restoreLayout(displayLayout);
    }
    /**
     * Receive menu events.
     *
     * @param inNewWindowEvent a <code>NewWindowEvent</code> value
     */
    @Subscribe
    public void receiveMenuEvent(NewWindowEvent inNewWindowEvent)
    {
        SLF4JLoggerProxy.debug(this,
                               "Received new window event: {}",
                               inNewWindowEvent.getWindowTitle());
        // create the UI window element
        Window newWindow = new Window(inNewWindowEvent.getWindowTitle());
        // set properties of the new window based on the received event
        newWindow.setModal(inNewWindowEvent.isModal());
        newWindow.setDraggable(inNewWindowEvent.isDraggable());
        newWindow.setResizable(inNewWindowEvent.isResizable());
        newWindow.setWidth(inNewWindowEvent.getWindowSize().getFirstMember());
        newWindow.setHeight(inNewWindowEvent.getWindowSize().getSecondMember());
        // the content view factory will be used to create the new window content
        ContentViewFactory viewFactory = inNewWindowEvent.getViewFactory();
        // create the window meta data object, which will track data about the window
        WindowRegistry windowRegistry = getCurrentUserRegistry();
        WindowMetaData newWindowWrapper = new WindowMetaData(newWindow,
                                                             viewFactory);
        windowRegistry.addWindow(newWindowWrapper);
        // create the new window content - initially, the properties will be mostly or completely empty, one would expect
        ContentView contentView = viewFactory.create(newWindowWrapper.getProperties());
        // set the content of the new window
        newWindow.setContent(contentView);
        newWindow.addClickListener(inEvent -> {
            newWindowWrapper.updateProperties();
            updateDisplayLayout(windowRegistry);
        });
        newWindow.addWindowModeChangeListener(inEvent -> {
            newWindowWrapper.updateProperties();
            updateDisplayLayout(windowRegistry);
        });
        newWindow.addResizeListener(inEvent -> {
            newWindowWrapper.updateProperties();
            updateDisplayLayout(windowRegistry);
        });
        newWindow.addCloseListener(inEvent -> {
            // this listener will be fired during log out, but, we don't want to update the display layout in that case
            if(!windowRegistry.isLoggingOut) {
                windowRegistry.removeWindow(newWindowWrapper);
                updateDisplayLayout(windowRegistry);
            }
        });
        newWindow.addBlurListener(inEvent -> {
            newWindowWrapper.updateProperties();
            updateDisplayLayout(windowRegistry);
        });
        newWindow.addFocusListener(inEvent -> {
            newWindowWrapper.updateProperties();
            updateDisplayLayout(windowRegistry);
        });
        newWindow.addContextClickListener(inEvent -> {
            newWindowWrapper.updateProperties();
            updateDisplayLayout(windowRegistry);
        });
        updateDisplayLayout(windowRegistry);
        UI.getCurrent().addWindow(newWindow);
        newWindow.focus();
    }
    /**
     * Receive logout events.
     *
     * @param inEvent a <code>LogoutEvent</code> value
     */
    @Subscribe
    public void receiveLogoutEvent(LogoutEvent inEvent)
    {
        getCurrentUserRegistry().logout();
    }
    /**
     * Receive window tile events.
     *
     * @param inEvent a <code>TimeWindowsEvent</code> value
     */
    @Subscribe
    public void receiveTileEvent(TileWindowsEvent inEvent)
    {
        /*
If you can relax the requirement that all windows have a given "aspect ratio" then the problem becomes very simple. Suppose you have N "tiles" to arrange on a single screen, 
then these can be arranged in columns where the number of columns, NumCols is the square root of N rounded up when N is not a perfect square. All columns of tiles are of equal width. 
The number of tiles in each column is then N/NumCols rounded either up or down as necessary to make the total number of columns be N. This is what Microsoft Excel does under View > Arrange All > Tiled. 
Excel chooses to put the columns with one fewer tiles on the left of the screen.
https://stackoverflow.com/questions/4456827/algorithm-to-fit-windows-on-desktop-like-tile
*/
        
    }
    private void updateDisplayLayout(WindowRegistry inRegistry)
    {
        try {
            Properties displayLayout = inRegistry.getDisplayLayout();
            SLF4JLoggerProxy.debug(this,
                                   "Updating display layout for {}: {}",
                                   SessionUser.getCurrentUser(),
                                   displayLayout);
            displayLayoutService.setDisplayLayout(displayLayout);
        } catch (Exception e) {
            SLF4JLoggerProxy.warn(this,
                                  e,
                                  ExceptionUtils.getRootCauseMessage(e));
        }
    }
    /**
     * Get the window registry for the current user.
     *
     * @return a <code>WindowRegistry</code> value
     */
    private WindowRegistry getCurrentUserRegistry()
    {
        WindowRegistry registry = UI.getCurrent().getSession().getAttribute(WindowRegistry.class);
        if(registry == null) {
            registry = new WindowRegistry();
            UI.getCurrent().getSession().setAttribute(WindowRegistry.class,
                                                      registry);
        }
        return registry;
    }
    /**
     * Holds meta-data for windows.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private static class WindowMetaData
    {
        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            return properties.toString();
        }
        /**
         * Create a new WindowMetaData instance.
         *
         * <p>This constructor is invoked for a new window.
         * 
         * @param inWindow a <code>Window</code> value
         * @param inContentViewFactory a <code>ContentViewFactory</code> value
         */
        private WindowMetaData(Window inWindow,
                               ContentViewFactory inContentViewFactory)
        {
            properties = new Properties();
            window = inWindow;
            setWindowStaticProperties(inContentViewFactory,
                                      UUID.randomUUID().toString());
            updateProperties();
        }
        /**
         * Create a new WindowMetaData instance.
         *
         * <p>This constructor is invoked to recreate a previously-created window.
         * 
         * @param inProperties a <code>Properties</code> value
         * @param inWindow a <code>Window</code> value
         */
        private WindowMetaData(Properties inProperties,
                               Window inWindow)
        {
            // TODO need to do a permissions re-check, perhaps
            window = inWindow;
            properties = inProperties;
            // update window from properties, effectively restoring it to its previous state
            updateWindow();
            try {
                ContentViewFactory contentViewFactory = (ContentViewFactory)Class.forName(inProperties.getProperty(windowContentViewFactoryProp)).newInstance();
                window.setContent(contentViewFactory.create(properties));
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        /**
         * Get the storable value for this window.
         *
         * @return a <code>String</code> value
         */
        private String getStorableValue()
        {
            return Util.propertiesToString(properties);
        }
        /**
         * Get the properties value.
         *
         * @return a <code>Properties</code> value
         */
        private Properties getProperties()
        {
            return properties;
        }
        /**
         * Get the window value.
         *
         * @return a <code>Window</code> value
         */
        private Window getWindow()
        {
            return window;
        }
        /**
         * Update the window telemetry from the underlying window object.
         */
        private void updateProperties()
        {
            properties.setProperty(windowPosXProp,
                                   String.valueOf(window.getPositionX()));
            properties.setProperty(windowPosYProp,
                                   String.valueOf(window.getPositionY()));
            properties.setProperty(windowHeightProp,
                                   String.valueOf(window.getHeight()));
            properties.setProperty(windowWidthProp,
                                   String.valueOf(window.getWidth()));
            properties.setProperty(windowModeProp,
                                   String.valueOf(window.getWindowMode()));
            properties.setProperty(windowTitleProp,
                                   window.getCaption());
            properties.setProperty(windowModalProp,
                                   String.valueOf(window.isModal()));
            properties.setProperty(windowDraggableProp,
                                   String.valueOf(window.isDraggable()));
            properties.setProperty(windowResizableProp,
                                   String.valueOf(window.isResizable()));
            properties.setProperty(windowScrollLeftProp,
                                   String.valueOf(window.getScrollLeft()));
            properties.setProperty(windowScrollTopProp,
                                   String.valueOf(window.getScrollTop()));
        }
        /**
         * Update the window object with the stored telemetry.
         */
        private void updateWindow()
        {
            window.setPositionX(Integer.parseInt(properties.getProperty(windowPosXProp)));
            window.setPositionY(Integer.parseInt(properties.getProperty(windowPosYProp)));
            window.setScrollLeft(Integer.parseInt(properties.getProperty(windowScrollLeftProp)));
            window.setScrollTop(Integer.parseInt(properties.getProperty(windowScrollTopProp)));
            window.setWidth(properties.getProperty(windowWidthProp));
            window.setHeight(properties.getProperty(windowHeightProp));
            window.setWindowMode(WindowMode.valueOf(properties.getProperty(windowModeProp)));
            window.setModal(Boolean.parseBoolean(properties.getProperty(windowModalProp)));
            window.setDraggable(Boolean.parseBoolean(properties.getProperty(windowDraggableProp)));
            window.setResizable(Boolean.parseBoolean(properties.getProperty(windowResizableProp)));
        }
        private void setWindowStaticProperties(ContentViewFactory inContentViewFactory,
                                               String inUid)
        {
            properties.setProperty(windowContentViewFactoryProp,
                                   inContentViewFactory.getClass().getCanonicalName());
            properties.setProperty(windowUidProp,
                                   inUid);
        }
        /**
         *
         *
         */
        private void close()
        {
            // TODO probably need to mark this window as closed to keep from doing anything else to it
            getWindow().close();
        }
        /**
         * Get the window uid value.
         *
         * @return a <code>String</code> value
         */
        private String getUid()
        {
            if(uid == null) {
                uid = properties.getProperty(windowUidProp);
            }
            return uid;
        }
        /**
         * cached uid value
         */
        private transient String uid;
        /**
         * properties used to record details about this window
         */
        private final Properties properties;
        /**
         * underlying UI element
         */
        private final Window window;
        private static final String propId = WindowMetaData.class.getSimpleName();
        private static final String windowUidProp = propId + "_uid";
        private static final String windowContentViewFactoryProp = propId + "_contentViewFactory";
        private static final String windowTitleProp = propId + "_title";
        private static final String windowPosXProp = propId + "__posX";
        private static final String windowPosYProp = propId + "_posY";
        private static final String windowHeightProp = propId + "_height";
        private static final String windowWidthProp = propId + "_width";
        private static final String windowModeProp = propId + "_mode";
        private static final String windowModalProp = propId + "_modal";
        private static final String windowDraggableProp = propId + "_draggable";
        private static final String windowResizableProp = propId + "_resizable";
        private static final String windowScrollLeftProp = propId + "_scrollLeft";
        private static final String windowScrollTopProp = propId + "_scrollTop";
    }
    /**
     * Provides a registry of all windows.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private static class WindowRegistry
    {
        /**
         * Add the given window to this registry.
         *
         * @param inWindowMetaData a <code>WindowWrapper</code> value
         */
        private void addWindow(WindowMetaData inWindowMetaData)
        {
            synchronized(activeWindows) {
                activeWindows.add(inWindowMetaData);
            }
        }
        /**
         * Restore the display layout with the given values.
         *
         * @param inDisplayLayout a <code>Properties</code> value
         */
        private void restoreLayout(Properties inDisplayLayout)
        {
            synchronized(activeWindows) {
                for(Map.Entry<Object,Object> entry : inDisplayLayout.entrySet()) {
                    String windowUid = String.valueOf(entry.getKey());
                    Properties windowProperties = Util.propertiesFromString(String.valueOf(entry.getValue()));
                    SLF4JLoggerProxy.debug(this,
                                           "Restoring {} {}",
                                           windowUid,
                                           windowProperties);
                    WindowMetaData newWindowMetaData = new WindowMetaData(windowProperties,
                                                                          new Window());
                    UI.getCurrent().addWindow(newWindowMetaData.getWindow());
                }
            }
        }
        /**
         * Remove the given window from this registry.
         *
         * @param inWindowMetaData a <code>WindowMetaData</code> value
         */
        private void removeWindow(WindowMetaData inWindowMetaData)
        {
            synchronized(activeWindows) {
                activeWindows.remove(inWindowMetaData);
            }
        }
        /**
         * Execute logout actions.
         */
        private void logout()
        {
            isLoggingOut = true;
            closeAllWindows();
        }
        /**
         * Close all windows in this registry.
         */
        private void closeAllWindows()
        {
            synchronized(activeWindows) {
                Set<WindowMetaData> tempActiveWindows = new HashSet<>(activeWindows);
                for(WindowMetaData window : tempActiveWindows) {
                    window.close();
                }
            }
        }
        /**
         * Get the isLoggingOut value.
         *
         * @return a <code>boolean</code> value
         */
        private boolean isLoggingOut()
        {
            return isLoggingOut;
        }
        /**
         * Get the display layout for all active windows.
         *
         * @return a <code>Properties</code> value
         */
        private Properties getDisplayLayout()
        {
            synchronized(activeWindows) {
                Properties displayLayout = new Properties();
                for(WindowMetaData activeWindow : activeWindows) {
                    String windowKey = activeWindow.getUid();
                    String windowValue = activeWindow.getStorableValue();
                    displayLayout.setProperty(windowKey,
                                              windowValue);
                }
                return displayLayout;
            }
        }
        /**
         * indicates if the user is in the process of logging out
         */
        private boolean isLoggingOut = false;
        /**
         * holds all active windows
         */
        private final Set<WindowMetaData> activeWindows = Sets.newHashSet();
    }
    /**
     * web message service value
     */
    @Autowired
    private WebMessageService webMessageService;
    /**
     * provides access to display layout services
     */
    @Autowired
    private DisplayLayoutService displayLayoutService;
}
