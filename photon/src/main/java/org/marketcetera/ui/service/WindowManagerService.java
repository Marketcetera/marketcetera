package org.marketcetera.ui.service;

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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.core.Util;
import org.marketcetera.ui.App;
import org.marketcetera.ui.events.CascadeWindowsEvent;
import org.marketcetera.ui.events.CloseWindowsEvent;
import org.marketcetera.ui.events.LoginEvent;
import org.marketcetera.ui.events.LogoutEvent;
import org.marketcetera.ui.events.NewWindowEvent;
import org.marketcetera.ui.events.TileWindowsEvent;
import org.marketcetera.ui.view.ContentView;
import org.marketcetera.ui.view.ContentViewFactory;
import org.marketcetera.ui.view.MenuContent;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.google.common.collect.Sets;
import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

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
        DesktopParameters desktopParameters = new DesktopParameters(mainStage);
        desktopParameters.recalculate();
        SessionUser.getCurrent().setAttribute(DesktopParameters.class,
                                              desktopParameters);
        Properties displayLayout = displayLayoutService.getDisplayLayout();
        SLF4JLoggerProxy.debug(this,
                               "Received {}, retrieved display layout: {}",
                               inEvent,
                               displayLayout);
        WindowRegistry windowRegistry = getCurrentUserRegistry();
        windowRegistry.restoreLayout(displayLayout);
    }
    public void initializeMainStage(Stage inMainStage)
    {
        mainStage = inMainStage;
    }
    private Stage mainStage;
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
        final Stage newWindow = new Stage();
        newWindow.initOwner(App.getPrimaryStage());
        newWindow.initStyle(StageStyle.UNDECORATED);
        if(inEvent.getWindowIcon() != null) {
            newWindow.getIcons().add(inEvent.getWindowIcon());
        }
        // create the new window content - initially, the properties will be mostly or completely empty, one would expect
        // the content view factory will be used to create the new window content
        ContentViewFactory viewFactory = applicationContext.getBean(inEvent.getViewFactoryType());
        // create the window meta data object, which will track data about the window
        WindowRegistry windowRegistry = getCurrentUserRegistry();
        WindowMetaData newWindowWrapper = new WindowMetaData(inEvent,
                                                             newWindow,
                                                             viewFactory);
        ContentView contentView = viewFactory.create(newWindow,
                                                     inEvent,
                                                     newWindowWrapper.getProperties());
        newWindow.setOnCloseRequest(inCloseEvent -> contentView.onClose(inCloseEvent));
        styleService.addStyle(contentView);
        Scene rootScene = contentView.getScene();
        rootScene.getStylesheets().clear();
        rootScene.getStylesheets().add("dark-mode.css");
        newWindow.setTitle(inEvent.getWindowTitle());
        // set properties of the new window based on the received event
        newWindow.initModality(inEvent.isModal()?Modality.APPLICATION_MODAL:Modality.NONE);
        // TODO not sure how to disallow dragging
//        newWindow.setDraggable(inEvent.isDraggable());
        newWindow.setResizable(inEvent.isResizable());
        newWindow.setWidth(Double.valueOf(inEvent.getWindowSize().getFirstMember()));
        newWindow.setHeight(Double.valueOf(inEvent.getWindowSize().getSecondMember()));
        windowRegistry.addWindow(newWindowWrapper);
        // set the content of the new window
        newWindow.setScene(rootScene);
        windowRegistry.addWindowListeners(newWindowWrapper);
        windowRegistry.updateDisplayLayout();
        // TODO pretty sure this isn't right
        newWindow.getProperties().put(WindowManagerService.windowUuidProp,
                                      inEvent.getWindowStyleId());
        newWindow.show();
        newWindow.requestFocus();
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
        if(SessionUser.getCurrent() == null) {
            return;
        }
        getCurrentUserRegistry().logout();
        SessionUser.getCurrent().setAttribute(WindowRegistry.class,
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
        DesktopParameters params = SessionUser.getCurrent().getAttribute(DesktopParameters.class);
        return (getWindowBottom(inWindow) > params.getBottom()) || (getWindowLeft(inWindow) < params.getLeft()) || (getWindowTop(inWindow) < params.getTop()) || (getWindowRight(inWindow) > params.getRight());
    }
    /**
     * Get the window top edge coordinate in pixels.
     *
     * @param inWindow a <code>Window</code> value
     * @return a <code>double</code> value
     */
    private double getWindowTop(Window inWindow)
    {
        return inWindow.getY();
    }
    /**
     * Get the window left edge coordinate in pixels.
     *
     * @param inWindow a <code>Window</code> value
     * @return a <code>double</code> value
     */
    private double getWindowLeft(Window inWindow)
    {
        return inWindow.getX();
    }
    /**
     * Get the window bottom edge coordinate in pixels.
     *
     * @param inWindow a <code>Window</code> value
     * @return a <code>double</code> value
     */
    private double getWindowBottom(Window inWindow)
    {
        return getWindowTop(inWindow) + getWindowHeight(inWindow);
    }
    /**
     * Get the window right edge coordinate in pixels.
     *
     * @param inWindow a <code>Window</code> value
     * @return a <code>double</code> value
     */
    private double getWindowRight(Window inWindow)
    {
        return getWindowLeft(inWindow) + getWindowWidth(inWindow);
    }
    /**
     * Get the window height in pixels.
     *
     * @param inWindow a <code>Window</code> value
     * @return a <code>double</code> value
     */
    private double getWindowHeight(Window inWindow)
    {
        return inWindow.getHeight();
    }
    /**
     * Get the window width in pixels.
     *
     * @param inWindow a <code>Window</code> value
     * @return a <code>double</code> value
     */
    private double getWindowWidth(Window inWindow)
    {
        return inWindow.getWidth();
    }
    /**
     * Get the window registry for the current user.
     *
     * @return a <code>WindowRegistry</code> value
     */
    private WindowRegistry getCurrentUserRegistry()
    {
        WindowRegistry registry = SessionUser.getCurrent().getAttribute(WindowRegistry.class);
        if(registry == null) {
            registry = new WindowRegistry();
            SessionUser.getCurrent().setAttribute(WindowRegistry.class,
                                                  registry);
            registry.scheduleWindowPositionMonitor();
        }
        return registry;
    }
    private static double getDoubleValue(Properties inProperties,
                                         String inPropertyName)
    {
        return getDoubleValue(inProperties,
                              inPropertyName,
                              0.0);
    }
    private static double getDoubleValue(Properties inProperties,
                                         String inPropertyName,
                                         double inDefaultValue)
    {
        String rawValue = StringUtils.trimToNull(inProperties.getProperty(inPropertyName));
        double value = inDefaultValue;
        if(rawValue != null) {
            try {
                value = Double.parseDouble(rawValue);
            } catch (NumberFormatException ignored) {}
        }
        return value;
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
         * @see org.marketcetera.ui.events.NewWindowEvent#getWindowIcon()
         */
        @Override
        public Image getWindowIcon()
        {
            if(contentViewFactory instanceof MenuContent) {
                return ((MenuContent)contentViewFactory).getMenuIcon();
            }
            return null;
        }
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
         * @param inWindow a <code>Stage</code> value
         * @param inContentViewFactory a <code>ContentViewFactory</code> value
         */
        private WindowMetaData(NewWindowEvent inEvent,
                               Stage inWindow,
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
         * @param inWindow a <code>Stage</code> value
         */
        private WindowMetaData(Properties inProperties,
                               Stage inWindow)
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
                Scene scene = contentView.getScene();
                scene.getStylesheets().clear();
                scene.getStylesheets().add("dark-mode.css");
                window.setScene(scene);
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
         * @return a <code>Stage</code> value
         */
        private Stage getWindow()
        {
            return window;
        }
        /**
         * Update the window telemetry from the underlying window object.
         */
        private void updateProperties()
        {
            properties.setProperty(windowPosXProp,
                                   String.valueOf(window.getX()));
            properties.setProperty(windowPosYProp,
                                   String.valueOf(window.getY()));
            properties.setProperty(windowHeightProp,
                                   String.valueOf(window.getHeight()));
            properties.setProperty(windowWidthProp,
                                   String.valueOf(window.getWidth()));
            properties.setProperty(windowModeProp,
                                   String.valueOf(window.isMaximized()));
            if(window.getTitle() != null) {
                properties.setProperty(windowTitleProp,
                                       window.getTitle());
            }
            properties.setProperty(windowModalProp,
                                   String.valueOf(window.getModality()));
            // TODO not sure what to do about dragging yet
//            properties.setProperty(windowDraggableProp,
//                                   String.valueOf(window.isDraggable()));
            properties.setProperty(windowResizableProp,
                                   String.valueOf(window.isResizable()));
//            properties.setProperty(windowScrollLeftProp,
//                                   String.valueOf(window.getScrollLeft()));
//            properties.setProperty(windowScrollTopProp,
//                                   String.valueOf(window.getScrollTop()));
            properties.setProperty(windowFocusProp,
                                   String.valueOf(hasFocus()));
            Object windowId = window.getProperties().getOrDefault(windowStyleId,
                                                                  null);
            if(windowId == null) {
                properties.remove(windowStyleId);
            } else {
                properties.setProperty(windowStyleId,
                                       String.valueOf(windowId));
            }
        }
        /**
         * Update the window object with the stored telemetry.
         */
        private void updateWindow()
        {
            window.setWidth(Double.parseDouble(properties.getProperty(windowWidthProp)));
            window.setHeight(Double.parseDouble(properties.getProperty(windowHeightProp)));
            window.initModality(Modality.valueOf(properties.getProperty(windowModalProp)));
            Boolean isMaximized = Boolean.parseBoolean(properties.getProperty(windowModeProp));
            window.setMaximized(isMaximized);
            // TODO not sure about these yet
//            window.setScrollLeft(Integer.parseInt(properties.getProperty(windowScrollLeftProp)));
//            window.setScrollTop(Integer.parseInt(properties.getProperty(windowScrollTopProp)));
//            window.setDraggable(Boolean.parseBoolean(properties.getProperty(windowDraggableProp)));
            window.setResizable(Boolean.parseBoolean(properties.getProperty(windowResizableProp)));
            window.setTitle(properties.getProperty(windowTitleProp));
            window.setX(getDoubleValue(properties,
                                       windowPosXProp));
            window.setY(getDoubleValue(properties,
                                       windowPosYProp));
            window.getProperties().put(windowStyleId,
                                       properties.getProperty(windowStyleId));
            setHasFocus(Boolean.parseBoolean(properties.getProperty(windowFocusProp)));
            if(hasFocus) {
                window.requestFocus();
            }
        }
        /**
         * Set the immutable properties of this window to the underlying properties storage.
         *
         * @param inContentViewFactory a <code>ContentViewFactory</code> value
         * @param inUuid a <code>String</code>value
         */
        private void setWindowStaticProperties(ContentViewFactory inContentViewFactory,
                                               String inUuid)
        {
            properties.setProperty(windowContentViewFactoryProp,
                                   inContentViewFactory.getClass().getCanonicalName());
            properties.setProperty(windowUuidProp,
                                   inUuid);
        }
        /**
         * Close this window and remove it from active use.
         */
        private void close()
        {
            ((Stage)getWindow().getScene().getWindow()).close();
        }
        /**
         * Get the window uuid value.
         *
         * @return a <code>String</code> value
         */
        private String getUuid()
        {
            if(uuid == null) {
                uuid = properties.getProperty(windowUuidProp);
            }
            return uuid;
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
         * cached uuid value
         */
        private transient String uuid;
        /**
         * properties used to record details about this window
         */
        private final Properties properties;
        /**
         * underlying UI element
         */
        private final Stage window;
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
                    DesktopParameters params = SessionUser.getCurrent().getAttribute(DesktopParameters.class);
                    double maxX = params.getRight();
                    double maxY = params.getBottom();
                    for(WindowMetaData activeWindow : activeWindows) {
                        double windowWidth = getWindowWidth(activeWindow.getWindow());
                        double windowHeight = getWindowHeight(activeWindow.getWindow());
                        double proposedX = xPos;
                        if(proposedX + windowWidth > maxX) {
                            proposedX = desktopCascadeWindowOffset;
                        }
                        double proposedY = yPos;
                        if(proposedY + windowHeight > maxY) {
                            proposedY = desktopCascadeWindowOffset;
                        }
                        activeWindow.getWindow().setX(proposedX);
                        activeWindow.getWindow().setY(proposedY);
                        activeWindow.getWindow().requestFocus();
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
                    DesktopParameters params = SessionUser.getCurrent().getAttribute(DesktopParameters.class);
                    int numWindows = activeWindows.size();
                    if(numWindows == 0) {
                        return;
                    }
                    int numCols = (int)Math.floor(Math.sqrt(numWindows));
                    int numRows = (int)Math.floor(numWindows / numCols);
                    if(!isPerfectSquare(numWindows)) {
                        numCols += 1;
                    }
                    double windowWidth = Math.floorDiv(((Double)params.getRight()).intValue(),
                                                       numCols);
                    double windowHeight = Math.floorDiv(((Double)(params.getBottom()-params.getTop())).intValue(),
                                                        numRows);
                    int colNum = 0;
                    int rowNum = 0;
                    double posX = params.getLeft();
                    double posY = params.getTop();
                    for(WindowMetaData activeWindow : activeWindows) {
                        double suggestedX = posX + (colNum * windowWidth);
                        double suggestedY = posY + (rowNum * windowHeight);
                        activeWindow.getWindow().setWidth(windowWidth);
                        activeWindow.getWindow().setHeight(windowHeight);
                        activeWindow.getWindow().setX(suggestedX);
                        activeWindow.getWindow().setY(suggestedY);
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
                    Stage newWindow = new Stage();
                    newWindow.initOwner(App.getPrimaryStage());
                    WindowMetaData newWindowMetaData = new WindowMetaData(windowProperties,
                                                                          newWindow);
                    addWindow(newWindowMetaData);
                    addWindowListeners(newWindowMetaData);
                    styleService.addStyle(newWindowMetaData.getWindow().getScene());
                    newWindowMetaData.getWindow().show();
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
                                       SessionUser.getCurrent(),
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
            Stage newWindow = inWindowWrapper.getWindow();
//            newWindow.addEventHandler()
            newWindow.addEventHandler(MouseEvent.MOUSE_CLICKED,
                                      new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent inEvent)
                {
                    SLF4JLoggerProxy.trace(WindowManagerService.this,
                                           "Click: {}",
                                           inEvent);
                    verifyWindowLocation(newWindow);
                    inWindowWrapper.updateProperties();
                    updateDisplayLayout();
                }}
            );
            newWindow.setOnShown(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent inEvent)
                {
                    SLF4JLoggerProxy.trace(WindowManagerService.this,
                                           "Shown: {}",
                                           inEvent);
                    verifyWindowLocation(newWindow);
                    inWindowWrapper.updateProperties();
                    updateDisplayLayout();
                }}
            );
//            newWindow.addWindowModeChangeListener(inEvent -> {
//                SLF4JLoggerProxy.trace(WindowManagerService.this,
//                                       "Mode change: {}",
//                                       inEvent);
//                // TODO might want to do this, might not. a maximized window currently tromps all over the menu bar
////                verifyWindowLocation(newWindow);
//                inWindowWrapper.updateProperties();
//                updateDisplayLayout();
//            });
            newWindow.widthProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> inObservable,
                                    Number inOldValue,
                                    Number inNewValue)
                {
                    newWindowResize("width",
                                    inWindowWrapper);
                }}
            );
            newWindow.heightProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> inObservable,
                                    Number inOldValue,
                                    Number inNewValue)
                {
                    newWindowResize("height",
                                    inWindowWrapper);
                }}
            );
            newWindow.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST,new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent inEvent)
                {
                    SLF4JLoggerProxy.trace(WindowManagerService.this,
                                           "Close: {}",
                                           inEvent);
                    // this listener will be fired during log out, but, we don't want to update the display layout in that case
                    if(!windowRegistry.isLoggingOut()) {
                        windowRegistry.removeWindow(inWindowWrapper);
                        updateDisplayLayout();
                    }
                }}
            );
//            newWindow.addBlurListener(inEvent -> {
//                SLF4JLoggerProxy.trace(WindowManagerService.this,
//                                       "Blur: {}",
//                                       inEvent);
//                verifyWindowLocation(newWindow);
//                inWindowWrapper.setHasFocus(false);
//                inWindowWrapper.updateProperties();
//                updateDisplayLayout();
//            });
//            newWindow.addFocusListener(inEvent -> {
//                SLF4JLoggerProxy.trace(WindowManagerService.this,
//                                       "Focus: {}",
//                                       inEvent);
//                verifyWindowLocation(newWindow);
//                inWindowWrapper.setHasFocus(true);
//                inWindowWrapper.updateProperties();
//                updateDisplayLayout();
//            });
            newWindow.addEventHandler(ContextMenuEvent.CONTEXT_MENU_REQUESTED,new EventHandler<ContextMenuEvent>() {
                @Override
                public void handle(ContextMenuEvent inEvent)
                {
                    SLF4JLoggerProxy.trace(WindowManagerService.this,
                                           "Context click: {}",
                                           inEvent);
                    verifyWindowLocation(newWindow);
                    inWindowWrapper.updateProperties();
                    updateDisplayLayout();
                }}
            );
        }
        private void newWindowResize(String inDimension,
                                     WindowMetaData inWindowWrapper)
        {
            SLF4JLoggerProxy.trace(WindowManagerService.this,
                                   "Resize: {}",
                                   inDimension);
            verifyWindowLocation(inWindowWrapper.getWindow());
            inWindowWrapper.updateProperties();
            updateDisplayLayout();
        }
        /**
         * Verify that the given window is within the acceptable bounds of the desktop viewable area.
         *
         * @param inWindow a <code>Stage</code> value
         */
        private void verifyWindowLocation(Stage inWindow)
        {
            synchronized(activeWindows) {
                if(isWindowOutsideDesktop(inWindow)) {
                    SLF4JLoggerProxy.trace(WindowManagerService.this,
                                           "{} is outside the desktop",
                                           inWindow.getTitle());
                    returnWindowToDesktop(inWindow);
                } else {
                    SLF4JLoggerProxy.trace(WindowManagerService.this,
                                           "{} is not outside the desktop",
                                           inWindow.getTitle());
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
            DesktopParameters params = SessionUser.getCurrent().getAttribute(DesktopParameters.class);
            // the order here is important: first, resize the window, if necessary
            double maxWidth = params.getRight()-params.getLeft();
            double windowWidth = getWindowWidth(inWindow);
            if(windowWidth > maxWidth) {
                inWindow.setWidth(maxWidth - (pad*2));
            }
            if(windowWidth <= 10) {
                windowWidth = 100;
                inWindow.setWidth(windowWidth);
            }
            double maxHeight = params.getBottom() - params.getTop();
            double windowHeight = getWindowHeight(inWindow);
            if(windowHeight > maxHeight) {
                inWindow.setHeight(maxHeight - (pad*2));
            }
            // window is now no larger than desktop
            // check bottom
            double windowBottom = getWindowBottom(inWindow);
            if(windowBottom > params.getBottom()) {
                double newWindowTop = params.getBottom() - getWindowHeight(inWindow) - pad;
                inWindow.setY(newWindowTop);
            }
            // check top
            double windowTop = getWindowTop(inWindow);
            if(windowTop < params.getTop()+pad) {
                double newWindowTop = params.getTop() + pad;
                inWindow.setY(newWindowTop);
            }
            // window is now within the desktop Y range
            // check left
            double windowLeft = getWindowLeft(inWindow);
            if(windowLeft < params.getLeft()) {
                double newWindowLeft = params.getLeft() + pad;
                inWindow.setX(newWindowLeft);
            }
            // check right
            double windowRight = getWindowRight(inWindow);
            if(windowRight > params.getRight()) {
                double newWindowLeft = params.getRight() - getWindowWidth(inWindow) - pad;
                inWindow.setX(newWindowLeft);
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
                Platform.runLater(new Runnable() {
                    @Override
                    public void run()
                    {
                        for(WindowMetaData windowMetaData : activeWindows) {
                            try {
                                if(WindowManagerService.this.isWindowOutsideDesktop(windowMetaData.getWindow())) {
                                    returnWindowToDesktop(windowMetaData.getWindow());
                                }
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
                    String windowKey = activeWindow.getUuid();
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
        private final ScheduledExecutorService windowPositionExaminerThreadPool = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat(SessionUser.getCurrent().getUsername() + "-WindowPositionExaminer").build());
    }
    /**
     * base key for {@see UserAttributeType} display layout properties
     */
    private static final String propId = WindowMetaData.class.getSimpleName();
    /**
     * window uuid key name
     */
    public static final String windowUuidProp = propId + "_uid";
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
    @Value("${metc.desktop.viewable.area.pad:50}")
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
