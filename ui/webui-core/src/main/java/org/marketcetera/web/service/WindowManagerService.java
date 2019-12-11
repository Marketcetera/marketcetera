package org.marketcetera.web.service;

import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.core.Util;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.web.SessionUser;
import org.marketcetera.web.events.CascadeWindowsEvent;
import org.marketcetera.web.events.CloseWindowsEvent;
import org.marketcetera.web.events.LoginEvent;
import org.marketcetera.web.events.LogoutEvent;
import org.marketcetera.web.events.NewWindowEvent;
import org.marketcetera.web.events.TileWindowsEvent;
import org.marketcetera.web.view.ContentView;
import org.marketcetera.web.view.ContentViewFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.google.common.collect.Sets;
import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.vaadin.server.Page;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.server.VaadinSession;
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
                              "Starting {}",
                              PlatformServices.getServiceName(getClass()));
        webMessageService.register(this);
    }
    /**
     * Stop the object.
     */
    @PreDestroy
    public void stop()
    {
        SLF4JLoggerProxy.info(this,
                              "Stopping {}",
                              PlatformServices.getServiceName(getClass()));
        webMessageService.unregister(this);
    }
    /**
     * Receive user login events.
     *
     * @param inEvent a <code>LoginEvent</code> value
     */
    @Subscribe
    public void onLogin(LoginEvent inEvent)
    {
        DesktopParameters desktopParameters = new DesktopParameters();
        desktopParameters.recalculate();
        VaadinSession.getCurrent().setAttribute(DesktopParameters.class,
                                                desktopParameters);
        Page.getCurrent().addBrowserWindowResizeListener(desktopParameters);
        Properties displayLayout = displayLayoutService.getDisplayLayout();
        SLF4JLoggerProxy.debug(this,
                               "Received {}, retrieved display layout: {}",
                               inEvent,
                               displayLayout);
        WindowRegistry windowRegistry = getCurrentUserRegistry();
        windowRegistry.restoreLayout(displayLayout);
    }
    /**
     * Receive new window events.
     *
     * @param inEvent a <code>NewWindowEvent</code> value
     */
    @Subscribe
    public void onNewWindow(NewWindowEvent inEvent)
    {
        SLF4JLoggerProxy.debug(this,
                               "onWindow: {}",
                               inEvent.getWindowTitle());
        // create the UI window element
        Window newWindow = new Window(inEvent.getWindowTitle());
        // set properties of the new window based on the received event
        newWindow.setModal(inEvent.isModal());
        newWindow.setDraggable(inEvent.isDraggable());
        newWindow.setResizable(inEvent.isResizable());
        newWindow.setWidth(inEvent.getWindowSize().getFirstMember());
        newWindow.setHeight(inEvent.getWindowSize().getSecondMember());
        // the content view factory will be used to create the new window content
        ContentViewFactory viewFactory = applicationContext.getBean(inEvent.getViewFactoryType());
        // create the window meta data object, which will track data about the window
        WindowRegistry windowRegistry = getCurrentUserRegistry();
        WindowMetaData newWindowWrapper = new WindowMetaData(inEvent,
                                                             newWindow,
                                                             viewFactory);
        windowRegistry.addWindow(newWindowWrapper);
        // create the new window content - initially, the properties will be mostly or completely empty, one would expect
        ContentView contentView = viewFactory.create(newWindow,
                                                     inEvent,
                                                     newWindowWrapper.getProperties());
        styleService.addStyle(contentView);
        // set the content of the new window
        newWindow.setContent(contentView);
        windowRegistry.addWindowListeners(newWindowWrapper);
        windowRegistry.updateDisplayLayout();
        newWindow.setId(inEvent.getWindowStyleId());
        styleService.addStyle(newWindow);
        UI.getCurrent().addWindow(newWindow);
        newWindow.focus();
    }
    /**
     * Receive logout events.
     *
     * @param inEvent a <code>LogoutEvent</code> value
     */
    @Subscribe
    public void onLogout(LogoutEvent inEvent)
    {
        SLF4JLoggerProxy.debug(this,
                               "onLogout: {}",
                               inEvent);
        getCurrentUserRegistry().logout();
        VaadinSession.getCurrent().setAttribute(WindowRegistry.class,
                                                null);
    }
    /**
     * Receive window cascade events.
     *
     * @param inEvent a <code>CascadeWindowEvent</code> inEvent
     */
    @Subscribe
    public void onCascade(CascadeWindowsEvent inEvent)
    {
        SLF4JLoggerProxy.trace(this,
                               "onCascade: {}",
                               inEvent);
        getCurrentUserRegistry().cascadeWindows();
    }
    /**
     * Receive window tile events.
     *
     * @param inEvent a <code>TileWindowsEvent</code> value
     */
    @Subscribe
    public void onTile(TileWindowsEvent inEvent)
    {
        SLF4JLoggerProxy.trace(this,
                               "onTile: {}",
                               inEvent);
        getCurrentUserRegistry().tileWindows();
    }
    /**
     * Receive close all windows events.
     *
     * @param inEvent a <code>CloseWindowsEvent</code> value
     */
    @Subscribe
    public void onCloseAllWindows(CloseWindowsEvent inEvent)
    {
        SLF4JLoggerProxy.trace(this,
                               "onCloseWindows: {}",
                               inEvent);
        getCurrentUserRegistry().closeAllWindows(true);
    }
    /**
     * Determine if the given window is outside the viewable desktop area or not.
     *
     * @param inWindow a <code>Window</code> value
     * @return a <code>boolean</code> value
     */
    private boolean isWindowOutsideDesktop(Window inWindow)
    {
        DesktopParameters params = VaadinSession.getCurrent().getAttribute(DesktopParameters.class);
        return (getWindowBottom(inWindow) > params.getBottom()) || (getWindowLeft(inWindow) < params.getLeft()) || (getWindowTop(inWindow) < params.getTop()) || (getWindowRight(inWindow) > params.getRight());
    }
    /**
     * Get the window top edge coordinate in pixels.
     *
     * @param inWindow a <code>Window</code> value
     * @return a <code>float</code> value
     */
    private float getWindowTop(Window inWindow)
    {
        return inWindow.getPositionY();
    }
    /**
     * Get the window left edge coordinate in pixels.
     *
     * @param inWindow a <code>Window</code> value
     * @return a <code>float</code> value
     */
    private float getWindowLeft(Window inWindow)
    {
        return inWindow.getPositionX();
    }
    /**
     * Get the window bottom edge coordinate in pixels.
     *
     * @param inWindow a <code>Window</code> value
     * @return a <code>float</code> value
     */
    private float getWindowBottom(Window inWindow)
    {
        return getWindowTop(inWindow) + getWindowHeight(inWindow);
    }
    /**
     * Get the window right edge coordinate in pixels.
     *
     * @param inWindow a <code>Window</code> value
     * @return a <code>float</code> value
     */
    private float getWindowRight(Window inWindow)
    {
        return getWindowLeft(inWindow) + getWindowWidth(inWindow);
    }
    /**
     * Get the window height in pixels.
     *
     * @param inWindow a <code>Window</code> value
     * @return a <code>float</code> value
     */
    private float getWindowHeight(Window inWindow)
    {
        return getWindowDimension(inWindow.getHeight(),
                                  Page.getCurrent().getBrowserWindowHeight(),
                                  inWindow.getHeightUnits());
    }
    /**
     * Get the window width in pixels.
     *
     * @param inWindow a <code>Window</code> value
     * @return a <code>float</code> value
     */
    private float getWindowWidth(Window inWindow)
    {
        return getWindowDimension(inWindow.getWidth(),
                                  Page.getCurrent().getBrowserWindowWidth(),
                                  inWindow.getWidthUnits());
    }
    /**
     * Get the window dimension implied by the given attributes.
     *
     * @param inValue a <code>float</code> value
     * @param inBrowserDimension an <code>int</code> value
     * @param inUnit a <code>Unit</code> value
     * @return a <code>float</code> value
     */
    private float getWindowDimension(float inValue,
                                     int inBrowserDimension,
                                     Unit inUnit)
    {
        switch(inUnit) {
            case PERCENTAGE:
                return inBrowserDimension * inValue / 100;
            default:
            case PIXELS:
                return inValue;
            case PICAS:
                return inValue / 16;
            case POINTS:
                return (float)(inValue * 1.3);
            case CM:
            case EM:
            case EX:
            case INCH:
            case MM:
            case REM:
                throw new UnsupportedOperationException("Cannot translate unit: " + inUnit);
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
            registry.scheduleWindowPositionMonitor();
        }
        return registry;
    }
    /**
     * Event used to open a new window on restart.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private class RestartNewWindowEvent
            implements NewWindowEvent
    {
        /* (non-Javadoc)
         * @see org.marketcetera.web.events.NewWindowEvent#getWindowTitle()
         */
        @Override
        public String getWindowTitle()
        {
            return windowTitle;
        }
        /* (non-Javadoc)
         * @see org.marketcetera.web.events.NewWindowEvent#getViewFactoryType()
         */
        @Override
        public Class<? extends ContentViewFactory> getViewFactoryType()
        {
            return contentViewFactory.getClass();
        }
        /**
         * Create a new RestartNewWindowEvent instance.
         *
         * @param inContentViewFactory a <code>ContentViewFactory</code> value
         * @param inWindowTitle a <code>String</code> value
         */
        private RestartNewWindowEvent(ContentViewFactory inContentViewFactory,
                                      String inWindowTitle)
        {
            contentViewFactory = inContentViewFactory;
            windowTitle = inWindowTitle;
        }
        /**
         * content view factory value
         */
        private final ContentViewFactory contentViewFactory;
        /**
         * window title value
         */
        private final String windowTitle;
    }
    /**
     * Holds meta-data for windows.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private class WindowMetaData
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
         * @param inEvent a <code>NewWindowEvent</code> value
         * @param inWindow a <code>Window</code> value
         * @param inContentViewFactory a <code>ContentViewFactory</code> value
         */
        private WindowMetaData(NewWindowEvent inEvent,
                               Window inWindow,
                               ContentViewFactory inContentViewFactory)
        {
            properties = inEvent.getProperties();
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
            try {
                ContentViewFactory contentViewFactory = (ContentViewFactory)applicationContext.getBean(Class.forName(inProperties.getProperty(windowContentViewFactoryProp)));
                ContentView contentView = contentViewFactory.create(window,
                                                                    new RestartNewWindowEvent(contentViewFactory,
                                                                                              properties.getProperty(windowTitleProp)),
                                                                    properties);
                styleService.addStyle(contentView);
                window.setContent(contentView);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            // update window from properties, effectively restoring it to its previous state
            updateWindow();
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
            properties.setProperty(windowHeightUnitProp,
                                   String.valueOf(window.getHeightUnits()));
            properties.setProperty(windowWidthUnitProp,
                                   String.valueOf(window.getWidthUnits()));
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
            properties.setProperty(windowFocusProp,
                                   String.valueOf(hasFocus()));
            if(window.getId() == null) {
                properties.remove(windowStyleId);
            } else {
                properties.setProperty(windowStyleId,
                                       window.getId());
            }
        }
        /**
         * Update the window object with the stored telemetry.
         */
        private void updateWindow()
        {
            Unit widthUnit = Unit.getUnitFromSymbol(properties.getProperty(windowWidthUnitProp));
            Unit heightUnit = Unit.getUnitFromSymbol(properties.getProperty(windowHeightUnitProp));
            window.setWidth(Float.parseFloat(properties.getProperty(windowWidthProp)),
                            widthUnit);
            window.setHeight(Float.parseFloat(properties.getProperty(windowHeightProp)),
                             heightUnit);
            window.setModal(Boolean.parseBoolean(properties.getProperty(windowModalProp)));
            // window mode must be set before posX/posY
            window.setWindowMode(WindowMode.valueOf(properties.getProperty(windowModeProp)));
            window.setScrollLeft(Integer.parseInt(properties.getProperty(windowScrollLeftProp)));
            window.setScrollTop(Integer.parseInt(properties.getProperty(windowScrollTopProp)));
            window.setDraggable(Boolean.parseBoolean(properties.getProperty(windowDraggableProp)));
            window.setResizable(Boolean.parseBoolean(properties.getProperty(windowResizableProp)));
            window.setCaption(properties.getProperty(windowTitleProp));
            window.setPositionX(Integer.parseInt(properties.getProperty(windowPosXProp)));
            window.setPositionY(Integer.parseInt(properties.getProperty(windowPosYProp)));
            window.setId(properties.getProperty(windowStyleId));
            setHasFocus(Boolean.parseBoolean(properties.getProperty(windowFocusProp)));
        }
        /**
         * Set the immutable properties of this window to the underlying properties storage.
         *
         * @param inContentViewFactory a <code>ContentViewFactory</code> value
         * @param inUid a <code>String</code>value
         */
        private void setWindowStaticProperties(ContentViewFactory inContentViewFactory,
                                               String inUid)
        {
            properties.setProperty(windowContentViewFactoryProp,
                                   inContentViewFactory.getClass().getCanonicalName());
            properties.setProperty(windowUidProp,
                                   inUid);
        }
        /**
         * Close this window and remove it from active use.
         */
        private void close()
        {
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
         * Get the hasFocus value.
         *
         * @return a <code>boolean</code> value
         */
        private boolean hasFocus()
        {
            return hasFocus;
        }
        /**
         * Sets the hasFocus value.
         *
         * @param inHasFocus a <code>boolean</code> value
         */
        private void setHasFocus(boolean inHasFocus)
        {
            hasFocus = inHasFocus;
        }
        /**
         * indicates if this window has focus or not
         */
        private transient boolean hasFocus;
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
    }
    /**
     * Provides a registry of all windows.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private class WindowRegistry
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
         * Closes all windows.
         *
         * @param inUpdateDisplay a <code>boolean</code> value
         */
        private void closeAllWindows(boolean inUpdateDisplay)
        {
            synchronized(activeWindows) {
                Set<WindowMetaData> tempActiveWindows = new HashSet<>(activeWindows);
                for(WindowMetaData window : tempActiveWindows) {
                    window.close();
                }
                if(inUpdateDisplay) {
                    updateDisplayLayout();
                }
            }
        }
        /**
         * Rearrange the windows in this registry to a cascaded pattern.
         */
        private void cascadeWindows()
        {
            synchronized(windowPositionExaminerThreadPool) {
                cancelWindowPositionMonitor();
            }
            try {
                synchronized(activeWindows) {
                    int xPos = desktopCascadeWindowOffset;
                    int yPos = desktopCascadeWindowOffset;
                    DesktopParameters params = VaadinSession.getCurrent().getAttribute(DesktopParameters.class);
                    int maxX = params.getRight();
                    int maxY = params.getBottom();
                    for(WindowMetaData activeWindow : activeWindows) {
                        float windowWidth = getWindowWidth(activeWindow.getWindow());
                        float windowHeight = getWindowHeight(activeWindow.getWindow());
                        float proposedX = xPos;
                        if(proposedX + windowWidth > maxX) {
                            proposedX = desktopCascadeWindowOffset;
                        }
                        float proposedY = yPos;
                        if(proposedY + windowHeight > maxY) {
                            proposedY = desktopCascadeWindowOffset;
                        }
                        activeWindow.getWindow().setPosition((int)proposedX,
                                                             (int)proposedY);
                        activeWindow.getWindow().focus();
                        xPos += desktopCascadeWindowOffset;
                        yPos += desktopCascadeWindowOffset;
                        activeWindow.updateProperties();
                    }
                }
                updateDisplayLayout();
            } finally {
                scheduleWindowPositionMonitor();
            }
        }
        /**
         * Rearrange the windows in this registry to a tiled pattern.
         */
        private void tileWindows()
        {
            synchronized(windowPositionExaminerThreadPool) {
                cancelWindowPositionMonitor();
            }
            try {
                synchronized(activeWindows) {
                    DesktopParameters params = VaadinSession.getCurrent().getAttribute(DesktopParameters.class);
                    int numWindows = activeWindows.size();
                    if(numWindows == 0) {
                        return;
                    }
                    int numCols = (int)Math.floor(Math.sqrt(numWindows));
                    int numRows = (int)Math.floor(numWindows / numCols);
                    if(!isPerfectSquare(numWindows)) {
                        numCols += 1;
                    }
                    int windowWidth = Math.floorDiv(params.getRight(),
                                                    numCols);
                    int windowHeight = Math.floorDiv((params.getBottom()-params.getTop()),
                                                     numRows);
                    int colNum = 0;
                    int rowNum = 0;
                    int posX = params.getLeft();
                    int posY = params.getTop();
                    for(WindowMetaData activeWindow : activeWindows) {
                        int suggestedX = posX + (colNum * windowWidth);
                        int suggestedY = posY + (rowNum * windowHeight);
                        activeWindow.getWindow().setWidth(windowWidth,
                                                          Unit.PIXELS);
                        activeWindow.getWindow().setHeight(windowHeight,
                                                           Unit.PIXELS);
                        activeWindow.getWindow().setPosition(suggestedX,
                                                             suggestedY);
                        colNum += 1;
                        if(colNum == numCols) {
                            colNum = 0;
                            rowNum += 1;
                        }
                        activeWindow.updateProperties();
                    }
                }
                updateDisplayLayout();
            } finally {
                scheduleWindowPositionMonitor();
            }
            /*
            If you can relax the requirement that all windows have a given "aspect ratio" then the problem becomes very simple. Suppose you have N "tiles" to arrange on a single screen, 
            then these can be arranged in columns where the number of columns, NumCols is the square root of N rounded up when N is not a perfect square. All columns of tiles are of equal width. 
            The number of tiles in each column is then N/NumCols rounded either up or down as necessary to make the total number of columns be N. This is what Microsoft Excel does under View > Arrange All > Tiled. 
            Excel chooses to put the columns with one fewer tiles on the left of the screen.
            https://stackoverflow.com/questions/4456827/algorithm-to-fit-windows-on-desktop-like-tile
            */
        }
        /**
         * Determine if the given value is a perfect square or not.
         *
         * @param inValue a <code>double</code> value
         * @return a <code>boolean</code> value
         */
        private boolean isPerfectSquare(double inValue)
        {
            double squareOfValue = Math.sqrt(inValue); 
            return ((squareOfValue - Math.floor(squareOfValue)) == 0);
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
                    addWindow(newWindowMetaData);
                    addWindowListeners(newWindowMetaData);
                    styleService.addStyle(newWindowMetaData.getWindow());
                    UI.getCurrent().addWindow(newWindowMetaData.getWindow());
                }
            }
        }
        /**
         * Update the display layout for the windows in the given window registry.
         */
        private void updateDisplayLayout()
        {
            try {
                Properties displayLayout = getDisplayLayout();
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
         * Add the necessary window listeners to the given window meta data.
         *
         * @param inWindowWrapper a <code>WindowMetaData</code> value
         */
        private void addWindowListeners(WindowMetaData inWindowWrapper)
        {
            WindowRegistry windowRegistry = this;
            Window newWindow = inWindowWrapper.getWindow();
            newWindow.addClickListener(inEvent -> {
                SLF4JLoggerProxy.trace(WindowManagerService.this,
                                       "Click: {}",
                                       inEvent);
                verifyWindowLocation(newWindow);
                inWindowWrapper.updateProperties();
                updateDisplayLayout();
            });
            newWindow.addWindowModeChangeListener(inEvent -> {
                SLF4JLoggerProxy.trace(WindowManagerService.this,
                                       "Mode change: {}",
                                       inEvent);
                // TODO might want to do this, might not. a maximized window currently tromps all over the menu bar
//                verifyWindowLocation(newWindow);
                inWindowWrapper.updateProperties();
                updateDisplayLayout();
            });
            newWindow.addResizeListener(inEvent -> {
                SLF4JLoggerProxy.trace(WindowManagerService.this,
                                       "Resize: {}",
                                       inEvent);
                verifyWindowLocation(newWindow);
                inWindowWrapper.updateProperties();
                updateDisplayLayout();
            });
            newWindow.addCloseListener(inEvent -> {
                SLF4JLoggerProxy.trace(WindowManagerService.this,
                                       "Close: {}",
                                       inEvent);
                // this listener will be fired during log out, but, we don't want to update the display layout in that case
                if(!windowRegistry.isLoggingOut()) {
                    windowRegistry.removeWindow(inWindowWrapper);
                    updateDisplayLayout();
                }
            });
            newWindow.addBlurListener(inEvent -> {
                SLF4JLoggerProxy.trace(WindowManagerService.this,
                                       "Blur: {}",
                                       inEvent);
                verifyWindowLocation(newWindow);
                inWindowWrapper.setHasFocus(false);
                inWindowWrapper.updateProperties();
                updateDisplayLayout();
            });
            newWindow.addFocusListener(inEvent -> {
                SLF4JLoggerProxy.trace(WindowManagerService.this,
                                       "Focus: {}",
                                       inEvent);
                verifyWindowLocation(newWindow);
                inWindowWrapper.setHasFocus(true);
                inWindowWrapper.updateProperties();
                updateDisplayLayout();
            });
            newWindow.addContextClickListener(inEvent -> {
                SLF4JLoggerProxy.trace(WindowManagerService.this,
                                       "Context click: {}",
                                       inEvent);
                verifyWindowLocation(newWindow);
                inWindowWrapper.updateProperties();
                updateDisplayLayout();
            });
        }
        /**
         * Verify that the given window is within the acceptable bounds of the desktop viewable area.
         *
         * @param inWindow a <code>Window</code> value
         */
        private void verifyWindowLocation(Window inWindow)
        {
            synchronized(activeWindows) {
                if(isWindowOutsideDesktop(inWindow)) {
                    SLF4JLoggerProxy.trace(WindowManagerService.this,
                                           "{} is outside the desktop",
                                           inWindow.getCaption());
                    returnWindowToDesktop(inWindow);
                } else {
                    SLF4JLoggerProxy.trace(WindowManagerService.this,
                                           "{} is not outside the desktop",
                                           inWindow.getCaption());
                }
            }
        }
        /**
         * Reposition the given window until it is within the acceptable bounds of the desktop viewable area.
         *
         * <p>If the window is already within the acceptable bounds of the desktop viewable area, it will not be repositioned.
         * 
         * @param inWindow a <code>Window</code> value
         */
        private void returnWindowToDesktop(Window inWindow)
        {
            int pad = desktopViewableAreaPad;
            DesktopParameters params = VaadinSession.getCurrent().getAttribute(DesktopParameters.class);
            // the order here is important: first, resize the window, if necessary
            int maxWidth = params.getRight()-params.getLeft();
            float windowWidth = getWindowWidth(inWindow);
            if(windowWidth > maxWidth) {
                inWindow.setWidth(maxWidth - (pad*2),
                                  Unit.PIXELS);
            }
            int maxHeight = params.getBottom() - params.getTop();
            float windowHeight = getWindowHeight(inWindow);
            if(windowHeight > maxHeight) {
                inWindow.setHeight(maxHeight - (pad*2),
                                  Unit.PIXELS);
            }
            // window is now no larger than desktop
            // check bottom
            float windowBottom = getWindowBottom(inWindow);
            if(windowBottom > params.getBottom()) {
                float newWindowTop = params.getBottom() - getWindowHeight(inWindow) - pad;
                inWindow.setPositionY((int)newWindowTop);
            }
            // check top
            float windowTop = getWindowTop(inWindow);
            if(windowTop < params.getTop()) {
                float newWindowTop = params.getTop() + pad;
                inWindow.setPositionY((int)newWindowTop);
            }
            // window is now within the desktop Y range
            // check left
            float windowLeft = getWindowLeft(inWindow);
            if(windowLeft < params.getLeft()) {
                float newWindowLeft = params.getLeft() + pad;
                inWindow.setPositionX((int)newWindowLeft);
            }
            // check right
            float windowRight = getWindowRight(inWindow);
            if(windowRight > params.getRight()) {
                float newWindowLeft = params.getRight() - getWindowWidth(inWindow) - pad;
                inWindow.setPositionX((int)newWindowLeft);
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
            terminateRegistry();
        }
        /**
         * Terminate this registry.
         * 
         * <p>A terminated registry may not be reused.
         */
        private void terminateRegistry()
        {
            synchronized(windowPositionExaminerThreadPool) {
                cancelWindowPositionMonitor();
                windowPositionExaminerThreadPool.shutdownNow();
            }
            closeAllWindows(false);
        }
        /**
         * Verify the position of all windows in this registry.
         */
        private void verifyAllWindowPositions()
        {
            synchronized(activeWindows) {
                UI.getCurrent().access(new Runnable() {
                    @Override
                    public void run()
                    {
                        for(WindowMetaData windowMetaData : activeWindows) {
                            try {
                                returnWindowToDesktop(windowMetaData.getWindow());
                            } catch (Exception e) {
                                SLF4JLoggerProxy.warn(WindowManagerService.this,
                                                      ExceptionUtils.getRootCauseMessage(e));
                            }
//                            if(windowMetaData.hasFocus()) { // && windowMetaData.getWindow().isAttached()) {
//                                windowMetaData.getWindow().focus();
//                            }
                        }
                    }}
                );
            }
        }
        /**
         * Cancel the current window position monitor job, if necessary.
         */
        private void cancelWindowPositionMonitor()
        {
            synchronized(windowPositionExaminerThreadPool) {
                if(windowPositionMonitorToken != null) {
                    try {
                        windowPositionMonitorToken.cancel(true);
                    } catch (Exception ignored) {}
                    windowPositionMonitorToken = null;
                }
            }
        }
        /**
         * Schedule the window position monitor job.
         */
        private void scheduleWindowPositionMonitor()
        {
            synchronized(windowPositionExaminerThreadPool) {
                cancelWindowPositionMonitor();
                windowPositionMonitorToken = windowPositionExaminerThreadPool.scheduleAtFixedRate(new Runnable() {
                    @Override
                    public void run()
                    {
                        try {
                            verifyAllWindowPositions();
                        } catch (Exception e) {
                            SLF4JLoggerProxy.warn(WindowManagerService.this,
                                                  ExceptionUtils.getRootCauseMessage(e));
                        }
                    }},
                                                                                                  desktopWindowPositionMonitorInterval,
                                                                                                  desktopWindowPositionMonitorInterval,
                                                                                                  TimeUnit.MILLISECONDS);
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
        /**
         * holds the token for the window position monitor job, if any
         */
        private Future<?> windowPositionMonitorToken;
        /**
         * checks window position on a periodic basis
         */
        private final ScheduledExecutorService windowPositionExaminerThreadPool = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat(SessionUser.getCurrentUser().getUsername() + "-WindowPositionExaminer").build());
    }
    /**
     * base key for {@see UserAttributeType} display layout properties
     */
    private static final String propId = WindowMetaData.class.getSimpleName();
    /**
     * window uid key name
     */
    private static final String windowUidProp = propId + "_uid";
    /**
     * window content view factory key name
     */
    private static final String windowContentViewFactoryProp = propId + "_contentViewFactory";
    /**
     * window title key name
     */
    private static final String windowTitleProp = propId + "_title";
    /**
     * window X position key name
     */
    private static final String windowPosXProp = propId + "__posX";
    /**
     * window Y position key name
     */
    private static final String windowPosYProp = propId + "_posY";
    /**
     * window height unit key name
     */
    private static final String windowHeightUnitProp = propId + "__unitX";
    /**
     * window width unit key name
     */
    private static final String windowWidthUnitProp = propId + "_unitY";
    /**
     * window height key name
     */
    private static final String windowHeightProp = propId + "_height";
    /**
     * window width key name
     */
    private static final String windowWidthProp = propId + "_width";
    /**
     * window mode key name
     */
    private static final String windowModeProp = propId + "_mode";
    /**
     * window is modal key name
     */
    private static final String windowModalProp = propId + "_modal";
    /**
     * window is focused key name
     */
    private static final String windowFocusProp = propId + "_focus";
    /**
     * window is draggable key name
     */
    private static final String windowDraggableProp = propId + "_draggable";
    /**
     * window is resizable key name
     */
    private static final String windowResizableProp = propId + "_resizable";
    /**
     * window scroll left key name
     */
    private static final String windowScrollLeftProp = propId + "_scrollLeft";
    /**
     * window scroll top key name
     */
    private static final String windowScrollTopProp = propId + "_scrollTop";
    /**
     * window style id key name
     */
    private static final String windowStyleId = propId + "_windowStyleId";
    /**
     * provides access to style services
     */
    @Autowired
    private StyleService styleService;
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
    /**
     * provides access to the application context
     */
    @Autowired
    private ApplicationContext applicationContext;
    /**
     * desktop viewable area pad value
     */
    @Value("${metc.desktop.viewable.area.pad:10}")
    private int desktopViewableAreaPad;
    /**
     * desktop cascade window offset value
     */
    @Value("${metc.desktop.cascade.window.offset:100}")
    private int desktopCascadeWindowOffset;
    /**
     * interval in ms at which to monitor and correct window positions
     */
    @Value("${metc.desktop.window.position.monitor.interval:250}")
    private long desktopWindowPositionMonitorInterval;
}
