package org.marketcetera.web.service;

import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
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
import org.marketcetera.web.events.CascadeWindowsEvent;
import org.marketcetera.web.events.CloseWindowsEvent;
import org.marketcetera.web.events.LoginEvent;
import org.marketcetera.web.events.LogoutEvent;
import org.marketcetera.web.events.NewWindowEvent;
import org.marketcetera.web.events.TileWindowsEvent;
import org.marketcetera.web.view.ContentView;
import org.marketcetera.web.view.ContentViewFactory;
import org.marketcetera.web.view.DesktopParameters;
import org.marketcetera.webui.security.AuthenticatedUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;

import com.google.common.collect.Sets;
import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.page.BrowserWindowResizeEvent;
import com.vaadin.flow.component.page.BrowserWindowResizeListener;
import com.vaadin.flow.server.Command;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.spring.annotation.SpringComponent;

/* $License$ */

/**
 * Manages window views in the UI.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@SpringComponent
public class WindowManagerService
        implements BrowserWindowResizeListener
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
        final DesktopParameters desktopParameters = new DesktopParameters(this);
        desktopParameters.recalculate(browserWindowHeight,
                                      browserWindowWidth);
        UI.getCurrent().access(new Command() {
            @Override
            public void execute()
            {
                UI.getCurrent().getPage().addBrowserWindowResizeListener(desktopParameters);
                VaadinSession.getCurrent().setAttribute(DesktopParameters.class,
                                                        desktopParameters);
            }
            private static final long serialVersionUID = 8953679285233816338L;
        });
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
        String windowId = PlatformServices.generateId();
        SLF4JLoggerProxy.debug(this,
                               "onWindow: title={} id={} size={} viewType={} styleId={} properties={}",
                               inEvent.getWindowTitle(),
                               windowId,
                               inEvent.getWindowSize(),
                               inEvent.getViewFactoryType().getSimpleName(),
                               inEvent.getWindowStyleId(),
                               inEvent.getProperties());
        // create the window meta data object, which will track data about the window
        WindowRegistry windowRegistry = getCurrentUserRegistry();
        // create the UI window element
        ManagedDialog newWindow = new ManagedDialog(inEvent,
                                                    windowId,
                                                    this,
                                                    windowRegistry);
        windowRegistry.addWindow(newWindow);
        // set properties of the new window based on the received event
        newWindow.setModal(inEvent.isModal());
        newWindow.setDraggable(inEvent.isDraggable());
        newWindow.setResizable(inEvent.isResizable());
        newWindow.setWidth(inEvent.getWindowSize().getFirstMember());
        newWindow.setHeight(inEvent.getWindowSize().getSecondMember());
        // the content view factory will be used to create the new window content
        ContentViewFactory viewFactory = applicationContext.getBean(inEvent.getViewFactoryType());
        // create the new window content - initially, the properties will be mostly or completely empty, one would expect
        ContentView contentView = viewFactory.create(newWindow,
                                                     inEvent,
                                                     newWindow.getProperties());
        styleService.addStyle(contentView);
        // set the content of the new window
        newWindow.setContent(contentView);
        windowRegistry.updateDisplayLayout();
        styleService.addStyle(newWindow);
        newWindow.open();
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
    /* (non-Javadoc)
     * @see com.vaadin.flow.component.page.BrowserWindowResizeListener#browserWindowResized(com.vaadin.flow.component.page.BrowserWindowResizeEvent)
     */
    @Override
    public void browserWindowResized(BrowserWindowResizeEvent inEvent)
    {
        SLF4JLoggerProxy.trace(this,
                               "browserWindowResized: {} x {}",
                               inEvent.getWidth(),
                               inEvent.getHeight());
        browserWindowHeight = inEvent.getHeight();
        browserWindowWidth = inEvent.getWidth();
    }
    /**
     *
     *
     * @return
     */
    public int getBrowserWindowWidth()
    {
        return browserWindowWidth;
    }
    /**
     *
     *
     * @return
     */
    public int getBrowserWindowHeight()
    {
        return browserWindowHeight;
    }
    /**
     * Determine if the given window is outside the viewable desktop area or not.
     *
     * @param inWindow a <code>ManagedDialog</code> value
     * @return a <code>boolean</code> value
     */
    private boolean isWindowOutsideDesktop(ManagedDialog inWindow)
    {
        DesktopParameters params = VaadinSession.getCurrent().getAttribute(DesktopParameters.class);
        System.out.println("COCO: determining if " + inWindow + " is outside " + params);
        return (inWindow.getWindowBottom() > params.getBottom()) || (inWindow.getWindowLeft() < params.getLeft()) || (inWindow.getWindowTop() < params.getTop()) || (inWindow.getWindowRight() > params.getRight());
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
     * Provides a registry of all windows.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    class WindowRegistry
    {
        /**
         * Create a new WindowRegistry instance.
         */
        private WindowRegistry()
        {
            windowPositionExaminerThreadPool = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat(AuthenticatedUser.getCurrentUser().getName() + "-WindowPositionExaminer").build());
        }
        /**
         * Add the given window to this registry.
         *
         * @param inWindowMetaData a <code>ManagedDialog</code> value
         */
        private void addWindow(ManagedDialog inWindowMetaData)
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
                Set<ManagedDialog> tempActiveWindows = new HashSet<>(activeWindows);
                for(ManagedDialog window : tempActiveWindows) {
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
                    for(ManagedDialog activeWindow : activeWindows) {
                        float windowWidth = activeWindow.getWindowWidth();
                        float windowHeight = activeWindow.getWindowHeight();
                        float proposedX = xPos;
                        if(proposedX + windowWidth > maxX) {
                            proposedX = desktopCascadeWindowOffset;
                        }
                        float proposedY = yPos;
                        if(proposedY + windowHeight > maxY) {
                            proposedY = desktopCascadeWindowOffset;
                        }
                        activeWindow.setPosition((int)proposedX,
                                                 (int)proposedY);
                        activeWindow.focus();
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
                    for(ManagedDialog activeWindow : activeWindows) {
                        int suggestedX = posX + (colNum * windowWidth);
                        int suggestedY = posY + (rowNum * windowHeight);
                        activeWindow.setWidth(windowWidth,
                                              Unit.PIXELS);
                        activeWindow.setHeight(windowHeight,
                                               Unit.PIXELS);
                        activeWindow.setPosition(suggestedX,
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
                    ManagedDialog newWindow = new ManagedDialog(windowProperties,
                                                                WindowManagerService.this,
                                                                this);
                    addWindow(newWindow);
                    styleService.addStyle(newWindow);
                    newWindow.open();
                }
            }
        }
        /**
         * Update the display layout for the windows in the given window registry.
         */
        void updateDisplayLayout()
        {
            try {
                Properties displayLayout = getDisplayLayout();
                SLF4JLoggerProxy.debug(this,
                                       "Updating display layout for {}: {}",
                                       AuthenticatedUser.getCurrentUser(),
                                       displayLayout);
                displayLayoutService.setDisplayLayout(displayLayout);
            } catch (Exception e) {
                SLF4JLoggerProxy.warn(this,
                                      e,
                                      ExceptionUtils.getRootCauseMessage(e));
            }
        }
        /**
         * Verify that the given window is within the acceptable bounds of the desktop viewable area.
         *
         * @param inWindow a <code>ManagedDialog</code> value
         */
        void verifyWindowLocation(ManagedDialog inWindow)
        {
            synchronized(activeWindows) {
                if(isWindowOutsideDesktop(inWindow)) {
                    SLF4JLoggerProxy.trace(WindowManagerService.this,
                                           "{} is outside the desktop",
                                           inWindow.getId());
                    returnWindowToDesktop(inWindow);
                } else {
                    SLF4JLoggerProxy.trace(WindowManagerService.this,
                                           "{} is not outside the desktop",
                                           inWindow.getId());
                }
            }
        }
        /**
         * Reposition the given window until it is within the acceptable bounds of the desktop viewable area.
         *
         * <p>If the window is already within the acceptable bounds of the desktop viewable area, it will not be repositioned.
         * 
         * @param inWindow a <code>ManagedDialog</code> value
         */
        private void returnWindowToDesktop(ManagedDialog inWindow)
        {
            System.out.println("COCO: " + inWindow + " must be returned to the desktop");
            int pad = desktopViewableAreaPad;
            DesktopParameters params = VaadinSession.getCurrent().getAttribute(DesktopParameters.class);
            // the order here is important: first, resize the window, if necessary
            int maxWidth = params.getRight()-params.getLeft();
            float windowWidth = inWindow.getWindowWidth();
            if(windowWidth > maxWidth) {
                inWindow.setWidth(maxWidth - (pad*2),
                                  Unit.PIXELS);
            }
            int maxHeight = params.getBottom() - params.getTop();
            float windowHeight = inWindow.getWindowHeight();
            if(windowHeight > maxHeight) {
                inWindow.setHeight(maxHeight - (pad*2),
                                  Unit.PIXELS);
            }
            // window is now no larger than desktop
            // check bottom
            float windowBottom = inWindow.getWindowBottom();
            if(windowBottom > params.getBottom()) {
                float newWindowTop = params.getBottom() - inWindow.getWindowHeight() - pad;
                inWindow.setPositionY((int)newWindowTop);
            }
            // check top
            float windowTop = inWindow.getWindowTop();
            if(windowTop < params.getTop()) {
                float newWindowTop = params.getTop() + pad;
                inWindow.setPositionY((int)newWindowTop);
            }
            // window is now within the desktop Y range
            // check left
            float windowLeft = inWindow.getWindowLeft();
            if(windowLeft < params.getLeft()) {
                float newWindowLeft = params.getLeft() + pad;
                inWindow.setPositionX((int)newWindowLeft);
            }
            // check right
            float windowRight = inWindow.getWindowRight();
            if(windowRight > params.getRight()) {
                float newWindowLeft = params.getRight() - inWindow.getWindowWidth() - pad;
                inWindow.setPositionX((int)newWindowLeft);
            }
        }
        /**
         * Remove the given window from this registry.
         *
         * @param inWindowMetaData a <code>ManagedDialog</code> value
         */
        private void removeWindow(ManagedDialog inWindowMetaData)
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
                UI ui = UI.getCurrent();
                if(ui == null) {
                    
                } else {
                    // TODO this doesn't work because ui isn't available in this thread
                    ui.access(new Command() {
                        @Override
                        public void execute()
                        {
                            for(ManagedDialog windowMetaData : activeWindows) {
                                try {
                                    returnWindowToDesktop(windowMetaData);
                                } catch (Exception e) {
                                    SLF4JLoggerProxy.warn(WindowManagerService.this,
                                                          ExceptionUtils.getRootCauseMessage(e));
                                }
//                                if(windowMetaData.hasFocus()) { // && windowMetaData.getWindow().isAttached()) {
//                                    windowMetaData.getWindow().focus();
//                                }
                            }
                        }
                        private static final long serialVersionUID = -37737336642258533L;
                    });
                }
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
                            // TODO this doesn't work because we can't find the ui - probably need to add on to the listener(s)
                            verifyAllWindowPositions();
                        } catch (Exception e) {
                            SLF4JLoggerProxy.warn(WindowManagerService.this,
                                                  ExceptionUtils.getRootCauseMessage(e));
                        }
                    }},desktopWindowPositionMonitorInterval,desktopWindowPositionMonitorInterval,TimeUnit.MILLISECONDS);
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
                for(ManagedDialog activeWindow : activeWindows) {
                    String windowKey = activeWindow.getUuid();
                    String windowValue = activeWindow.getStorableValue();
                    System.out.println("COCO: setting " + windowKey + " = " + windowValue);
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
        private final Set<ManagedDialog> activeWindows = Sets.newHashSet();
        /**
         * holds the token for the window position monitor job, if any
         */
        private Future<?> windowPositionMonitorToken;
        /**
         * checks window position on a periodic basis
         */
//        private final ScheduledExecutorService windowPositionExaminerThreadPool = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat(AuthenticatedUser.getCurrentUser().getUsername() + "-WindowPositionExaminer").build());
        private final ScheduledExecutorService windowPositionExaminerThreadPool;
    }
    /**
     * base key for {@see UserAttributeType} display layout properties
     */
    private static final String propId = ManagedDialog.class.getSimpleName();
    /**
     * window uuid key name
     */
    public static final String windowUuidProp = propId + "_uid";
    /**
     * window title key name
     */
    public static final String windowTitleProp = propId + "_title";
    private int browserWindowHeight = 768;
    private int browserWindowWidth = 1024;
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
    private static final long serialVersionUID = 7414055125589532285L;
}
