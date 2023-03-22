package org.marketcetera.ui.service;

import java.net.URL;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.marketcetera.core.Pair;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.core.Util;
import org.marketcetera.ui.DragResizeMod;
import org.marketcetera.ui.DragResizeMod.OnDragResizeEventListener;
import org.marketcetera.ui.PhotonApp;
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

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;

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
        // create the new window content - initially, the properties will be mostly or completely empty, one would expect
        // the content view factory will be used to create the new window content
        ContentViewFactory viewFactory = applicationContext.getBean(inEvent.getViewFactoryType());
        // create the window meta data object, which will track data about the window
        final WindowLayout newWindow = new WindowLayout(inEvent,
                                                        viewFactory);
        if(inEvent.getWindowIcon() != null) {
            // TODO need to convert to Image to show here
//            newWindow.getIcons().add(PhotonServices.getSvgResource(inEvent.getWindowIcon()));
        }
        WindowRegistry windowRegistry = getCurrentUserRegistry();
        ContentView contentView = viewFactory.create(newWindow.getMainLayout(),
                                                     inEvent,
                                                     newWindow.getProperties());
        newWindow.setContentView(contentView);
        // set properties of the new window based on the received event
        newWindow.setDraggable(inEvent.isDraggable());
        newWindow.setResizable(inEvent.isResizable());
        if(inEvent.getWindowSize().getFirstMember() > 0) {
            newWindow.setWidth(inEvent.getWindowSize().getFirstMember());
        }
        if(inEvent.getWindowSize().getSecondMember() > 0) {
            newWindow.setHeight(inEvent.getWindowSize().getSecondMember());
        }
        windowRegistry.addWindow(newWindow);
        // set the content of the new window
        newWindow.setRoot(contentView.getMainLayout());
        windowRegistry.updateDisplayLayout();
        newWindow.show();
        windowRegistry.verifyWindowLocation(newWindow);
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
     * @param inWindow a <code>WindowLayout</code> value
     * @return a <code>boolean</code> value
     */
    private boolean isWindowOutsideDesktop(WindowLayout inWindow)
    {
        double windowTop = inWindow.getY();
        double windowLeft = inWindow.getX();
        double windowHeight = inWindow.getHeight();
        double windowWidth = inWindow.getWidth();
        double windowBottom = windowTop + windowHeight;
        double windowRight = windowLeft + windowWidth;
        double workspaceWidth = getWorkspaceWidth();
        double workspaceBottom = getWorkspaceBottom();
        double workspaceLeft = getWorkspaceLeft();
        double workspaceTop = getWorkspaceTop();
        double workspaceRight = workspaceWidth;
        boolean outsideDesktop = windowBottom > workspaceBottom || windowLeft < workspaceLeft || windowTop < workspaceTop || windowRight > workspaceRight;
        return outsideDesktop;
    }
    /**
     * Get the window height in pixels.
     *
     * @param inWindow a <code>WindowLayout</code> value
     * @return a <code>double</code> value
     */
    private double getWindowHeight(WindowLayout inWindow)
    {
        return inWindow.getHeight();
    }
    /**
     * Get the window width in pixels.
     *
     * @param inWindow a <code>WindowLayout</code> value
     * @return a <code>double</code> value
     */
    private double getWindowWidth(WindowLayout inWindow)
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
        }
        return registry;
    }
    /**
     * Get the main workspace width.
     *
     * @return a <code>double</code> value
     */
    private double getWorkspaceWidth()
    {
        return PhotonApp.getWorkspace().getLayoutBounds().getWidth();
    }
    /**
     * Get the main workspace height.
     *
     * @return a <code>double</code> value
     */
    private double getWorkspaceHeight()
    {
        return PhotonApp.getWorkspace().getLayoutBounds().getHeight();
    }
    /**
     * Get the main workspace left.
     *
     * @return a <code>double</code> value
     */
    private double getWorkspaceLeft()
    {
        return 0.0;
    }
    /**
     * Get the main workspace top.
     *
     * @return a <code>double</code> value
     */
    private double getWorkspaceTop()
    {
        return 0.0;
    }
    /**
     * Get the main workspace right.
     *
     * @return a <code>double</code> value
     */
    private double getWorkspaceRight()
    {
        return getWorkspaceLeft() + getWorkspaceWidth();
    }
    /**
     * Get the main workspace bottom.
     *
     * @return a <code>double</code> value
     */
    private double getWorkspaceBottom()
    {
        return getWorkspaceTop() + getWorkspaceHeight();
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
         * @see org.marketcetera.ui.events.NewWindowEvent#getProperties()
         */
        @Override
        public Properties getProperties()
        {
            return properties;
        }
        /* (non-Javadoc)
         * @see org.marketcetera.ui.events.NewWindowEvent#getWindowIcon()
         */
        @Override
        public URL getWindowIcon()
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
         * @param inProperties a <code>Properties</code> value
         */
        private RestartNewWindowEvent(ContentViewFactory inContentViewFactory,
                                      Properties inProperties)
        {
            contentViewFactory = inContentViewFactory;
            properties = inProperties;
            windowTitle = properties.getProperty(windowTitleProp);
        }
        /**
         * window properties value
         */
        private final Properties properties;
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
     * Holds the information needed for each window being displayed.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    @SuppressWarnings("unused")
    private class WindowLayout
    {
        /**
         * Create a new WindowLayout instance.
         *
         * @param inEvent a <code>NewWindowEvent</code> value
         * @param inViewFactory a <code>ContentViewFactory</code> value
         */
        private WindowLayout(NewWindowEvent inEvent,
                             ContentViewFactory inViewFactory)
        {
            newWindowEventProperty.set(inEvent);
            viewFactoryProperty.set(inViewFactory);
            properties = inEvent.getProperties();
            uuidProperty.set(UUID.randomUUID().toString());
            setWindowStaticProperties();
            windowLayout = new VBox();
            windowTitleLayout = new HBox();
            titleLayout = new HBox();
            closeButtonLayout = new HBox();
            contentLayout = new VBox();
            mainScrollPane = new ScrollPane();
            mainScrollPane.setContent(contentLayout);
            mainScrollPane.setPannable(true);
            scrollHorizontalProperty.bind(mainScrollPane.hvalueProperty());
            scrollVerticalProperty.bind(mainScrollPane.vvalueProperty());
            windowLayout.getChildren().addAll(windowTitleLayout,
                                              mainScrollPane);
            windowTitleLayout.getChildren().addAll(titleLayout,
                                                   closeButtonLayout);
            windowTitle = new Label();
            windowTitle.textProperty().bind(windowTitleProperty);
            closeLabel = new Label("X");
            titleLayout.getChildren().addAll(windowTitle);
            closeButtonLayout.getChildren().addAll(closeLabel);
            windowTitleLayout.getStyleClass().add("title-bar");
            HBox.setHgrow(windowTitleLayout,
                          Priority.ALWAYS);
            HBox.setHgrow(titleLayout,
                          Priority.ALWAYS);
            HBox.setHgrow(closeButtonLayout,
                          Priority.NEVER);
            windowTitleLayout.setAlignment(Pos.CENTER);
            closeButtonLayout.setAlignment(Pos.BASELINE_RIGHT);
            closeButtonLayout.setPrefWidth(20);
            contentLayout.setAlignment(Pos.CENTER);
            VBox.setVgrow(contentLayout,
                          Priority.ALWAYS);
            DragResizeMod.makeResizable(windowLayout,
                                        new OnDragResizeEventListener() {
                @Override
                public void onDrag(Node inNode,
                                   double inX,
                                   double inY,
                                   double inH,
                                   double inW)
                {
                    setX(inX);
                    setY(inY);
                    WindowRegistry windowRegistry = getCurrentUserRegistry();
                    windowRegistry.verifyWindowLocation(WindowLayout.this);
                    windowRegistry.updateDisplayLayout();
                }
                @Override
                public void onResize(Node inNode,
                                     double inX,
                                     double inY,
                                     double inH,
                                     double inW)
                {
                    setHeight(inH);
                    setWidth(inW);
                    WindowRegistry windowRegistry = getCurrentUserRegistry();
                    windowRegistry.verifyWindowLocation(WindowLayout.this);
                    windowRegistry.updateDisplayLayout();
                }
            });
            windowLayout.getStyleClass().add("view");
            windowLayout.getStylesheets().clear();
            windowLayout.getStylesheets().add("dark-mode.css");
//            DropShadow dropShadow = new DropShadow(BlurType.THREE_PASS_BOX,new Color(0,0,0,0.8),10,0,0,0);
//            windowLayout.setEffect(dropShadow);
//            windowLayout.setPickOnBounds(false);
            setupWindowListeners();
            Pair<Double,Double> suggestedWindowSize = inEvent.getWindowSize();
            String rawProperty = StringUtils.trimToNull(properties.getProperty(windowWidthProp));
            if(rawProperty == null) {
                setWidth(suggestedWindowSize.getFirstMember());
            } else {
                setWidth(Double.parseDouble(rawProperty));
            }
            rawProperty = StringUtils.trimToNull(properties.getProperty(windowHeightProp));
            if(rawProperty == null) {
                setHeight(suggestedWindowSize.getSecondMember());
            } else {
                setHeight(Double.parseDouble(rawProperty));
            }
            rawProperty = StringUtils.trimToNull(properties.getProperty(windowPosXProp));
            if(rawProperty == null) {
                setX(200);
            } else {
                setX(Double.parseDouble(rawProperty));
            }
            rawProperty = StringUtils.trimToNull(properties.getProperty(windowPosYProp));
            if(rawProperty == null) {
                setY(200);
            } else {
                setY(Double.parseDouble(rawProperty));
            }
            rawProperty = StringUtils.trimToNull(properties.getProperty(windowTitleProp));
            if(rawProperty == null) {
                setTitle(inEvent.getWindowTitle());
            } else {
                setTitle(rawProperty);
            }
            rawProperty = StringUtils.trimToNull(properties.getProperty(windowHorizontalScrollProp));
            if(rawProperty != null) {
                setHorizontalScroll(Double.parseDouble(rawProperty));
            }
            rawProperty = StringUtils.trimToNull(properties.getProperty(windowVerticalScrollProp));
            if(rawProperty != null) {
                setVerticalScroll(Double.parseDouble(rawProperty));
            }
        }
        /**
         * Set up the window listeners for the new window.
         */
        private void setupWindowListeners()
        {
            closeLabel.setOnMouseClicked(event -> {
                contentViewProperty.get().onClose();
                close();
            });
            windowLayout.setOnMouseClicked(event -> {
                WindowRegistry windowRegistry = getCurrentUserRegistry();
                synchronized(windowRegistry.activeWindows) {
                    for(WindowLayout windowLayout : windowRegistry.activeWindows) {
                        windowLayout.getMainLayout().viewOrderProperty().set(0.0);
                    }
                }
                windowLayout.viewOrderProperty().set(-1.0);
            });
            xProperty.addListener((observableValue,oldValue,newValue) -> {
                properties.setProperty(windowPosXProp,
                                       String.valueOf(newValue));
            });
            yProperty.addListener((observableValue,oldValue,newValue) -> {
                properties.setProperty(windowPosYProp,
                                       String.valueOf(newValue));
            });
            heightProperty.addListener((observableValue,oldValue,newValue) -> {
                properties.setProperty(windowHeightProp,
                                       String.valueOf(newValue));
            });
            widthProperty.addListener((observableValue,oldValue,newValue) -> {
                properties.setProperty(windowWidthProp,
                                       String.valueOf(newValue));
            });
            windowTitleProperty.addListener((observableValue,oldValue,newValue) -> {
                if(newValue == null) {
                    properties.remove(windowTitleProp);
                } else {
                    properties.setProperty(windowTitleProp,
                                           getTitle());
                }
            });
            draggableProperty.addListener((observableValue,oldValue,newValue) -> {
                properties.setProperty(windowDraggableProp,
                                       String.valueOf(newValue));
            });
            resizableProperty.addListener((observableValue,oldValue,newValue) -> {
                properties.setProperty(windowResizableProp,
                                       String.valueOf(newValue));
            });
            scrollVerticalProperty.addListener((observableValue,oldValue,newValue) -> {
                properties.setProperty(windowVerticalScrollProp,
                                       String.valueOf(newValue));
                getCurrentUserRegistry().updateDisplayLayout();
            });
            scrollHorizontalProperty.addListener((observableValue,oldValue,newValue) -> {
                properties.setProperty(windowHorizontalScrollProp,
                                       String.valueOf(newValue));
                getCurrentUserRegistry().updateDisplayLayout();
            });
            viewOrderProperty.addListener((observableValue,oldValue,newValue) -> {
                properties.setProperty(windowViewOrderProp,
                                       String.valueOf(newValue));
            });
            // TODO
//            properties.setProperty(windowModeProp,
//                                   String.valueOf(isMaximized()));
        }
        /**
         * Set the view order value.
         *
         * @param inViewOrder a <code>double</code> value
         */
        private void setViewOrder(double inViewOrder)
        {
            viewOrderProperty.set(inViewOrder);
            Platform.runLater(() -> windowLayout.setViewOrder(inViewOrder));
        }
        /**
         * Set the content view value.
         *
         * @param inContentView a <code>ContentView</code> value
         */
        private void setContentView(ContentView inContentView)
        {
            contentViewProperty.set(inContentView);
        }
        /**
         * Request the focus for this window.
         */
        private void requestFocus()
        {
            Platform.runLater(() -> windowLayout.requestFocus());
        }
        /**
         * Get the window properties.
         *
         * @return a <code>Properties</code> value
         */
        private Properties getProperties()
        {
            return windowProperties;
        }
        /**
         * Set the root content.
         *
         * @param inNode a <code>Node</code> value
         */
        private void setRoot(Node inNode)
        {
            contentLayout.getChildren().add(inNode);
        }
        /**
         * Set the draggable value for the window.
         *
         * @param inDraggable a <code>boolean</code> value
         */
        private void setDraggable(boolean inDraggable)
        {
            draggableProperty.set(inDraggable);
        }
        /**
         * Set the height value for the window.
         *
         * @param inHeight a <code>double</code> value
         */
        private void setHeight(double inHeight)
        {
            heightProperty.set(inHeight);
            windowLayout.setPrefHeight(inHeight);
            windowLayout.setMinHeight(inHeight);
        }
        /**
         * Set the width value for the window.
         *
         * @param inWidth a <code>double</code> value
         */
        private void setWidth(double inWidth)
        {
            widthProperty.set(inWidth);
            windowLayout.setPrefWidth(inWidth);
            windowLayout.setMinWidth(inWidth);
        }
        /**
         * Set the resizable value for the window.
         *
         * @param inResizable a <code>boolean</code> value
         */
        public void setResizable(boolean inResizable)
        {
            resizableProperty.set(inResizable);
        }
        /**
         * Set the window title property for the window.
         *
         * @param inWindowTitle a <code>String</code> value
         */
        private void setTitle(String inWindowTitle)
        {
            windowTitleProperty.set(inWindowTitle);
        }
        /**
         * Get the main layout node for the window.
         *
         * @return a <code>Node</code> value
         */
        private Node getMainLayout()
        {
            return windowLayout;
        }
        /**
         * Get the window X position value.
         *
         * @return a <code>double</code> value
         */
        private double getX()
        {
            return xProperty.get();
        }
        /**
         * Get the window Y position value.
         *
         * @return a <code>double</code> value
         */
        private double getY()
        {
            return yProperty.get();
        }
        /**
         * Get the window height value.
         *
         * @return a <code>double</code> value
         */
        private double getHeight()
        {
            return heightProperty.get();
        }
        /**
         * Get the window width value.
         *
         * @return a <code>double</code> value
         */
        private double getWidth()
        {
            return widthProperty.get();
        }
        /**
         * Get the window maximized value.
         *
         * @return a <code>boolean</code> value
         */
        private boolean isMaximized()
        {
            return maximizedProperty.get();
        }
        /**
         * Get the window title value.
         *
         * @return a <code>String</code> value
         */
        private String getTitle()
        {
            return windowTitleProperty.get();
        }
        /**
         * Get the window modality value.
         *
         * @return a <code>Modality</code> value
         */
        private Modality getModality()
        {
            return modalityProperty.get();
        }
        /**
         * Get the window draggable value.
         *
         * @return a <code>boolean</code> value
         */
        private boolean isDraggable()
        {
            return draggableProperty.get();
        }
        /**
         * Get the window resizable value.
         *
         * @return a <code>boolean</code> value
         */
        private boolean isResizable()
        {
            return resizableProperty.get();
        }
        /**
         * Get the window scroll left value.
         *
         * @return a <code>double</code> value
         */
        private double getScrollLeft()
        {
            return scrollVerticalProperty.get();
        }
        /**
         * Set the window modality value.
         *
         * @param inModality a <code>Modality</code> value
         */
        private void setModality(Modality inModality)
        {
            modalityProperty.set(inModality);
        }
        /**
         * Set the window maximized value.
         *
         * @param inMaximized a <code>boolean</code> value
         */
        private void setMaximized(boolean inMaximized)
        {
            maximizedProperty.set(inMaximized);
        }
        /**
         * Set the window vertical scroll value.
         *
         * @param inVerticalScrollValue a <code>double</code> value
         */
        private void setVerticalScroll(double inVerticalScrollValue)
        {
            Platform.runLater(() -> mainScrollPane.vvalueProperty().set(inVerticalScrollValue));
        }
        /**
         * Set the window horizontal scroll value.
         *
         * @param inHorizontalScrollValue a <code>double</code> value
         */
        private void setHorizontalScroll(double inHorizontalScrollValue)
        {
            Platform.runLater(() -> mainScrollPane.hvalueProperty().set(inHorizontalScrollValue));
        }
        /**
         * Set the window X position value.
         *
         * @param inX a <code>double</code> value
         */
        private void setX(double inX)
        {
            xProperty.set(inX);
            getMainLayout().translateXProperty().set(inX);
        }
        /**
         * Set the window Y position value.
         *
         * @param inY a <code>double</code> value
         */
        private void setY(double inY)
        {
            yProperty.set(inY);
            getMainLayout().translateYProperty().set(inY);
        }
        /**
         * Close the window.
         */
        private void close()
        {
            WindowRegistry windowRegistry = getCurrentUserRegistry();
            if(!windowRegistry.isLoggingOut()) {
                windowRegistry.removeWindow(this);
                windowRegistry.updateDisplayLayout();
            }
            Platform.runLater(() -> PhotonApp.getWorkspace().getChildren().remove(getMainLayout()));
        }
        /**
         * Show the window.
         */
        private void show()
        {
            getMainLayout().translateXProperty().set(getX());
            getMainLayout().translateYProperty().set(getY());
            setViewOrder(-1);
            Platform.runLater(() -> {
                windowLayout.autosize();
                PhotonApp.getWorkspace().getChildren().add(getMainLayout());
                requestFocus();
            });
        }
        /**
         * Get the window uuid value.
         *
         * @return a <code>String</code> value
         */
        private String getUuid()
        {
            return uuidProperty.get();
        }
        /**
         * Get a storable implementation of the window properties.
         *
         * @return a <code>String</code> value
         */
        private String getStorableValue()
        {
            return Util.propertiesToString(properties);
        }
        /**
         * Set the immutable properties of this window to the underlying properties storage.
         */
        private void setWindowStaticProperties()
        {
            properties.setProperty(windowContentViewFactoryProp,
                                   viewFactoryProperty.get().getClass().getCanonicalName());
            properties.setProperty(windowUuidProp,
                                   uuidProperty.get());
        }
        /**
         * holds the Content View of the window
         */
        private final ObjectProperty<ContentView> contentViewProperty = new SimpleObjectProperty<>();
        /**
         * holds the initial event of the window
         */
        private final ObjectProperty<NewWindowEvent> newWindowEventProperty = new SimpleObjectProperty<>();
        /**
         * holds the window view order value
         */
        private final DoubleProperty viewOrderProperty = new SimpleDoubleProperty();
        /**
         * cached uuid value
         */
        private final StringProperty uuidProperty = new SimpleStringProperty();
        /**
         * properties used to record details about this window
         */
        private final Properties properties;
        /**
         * holds the window content view factory value
         */
        private final ObjectProperty<ContentViewFactory> viewFactoryProperty = new SimpleObjectProperty<>();
        /**
         * holds the window height value
         */
        private final DoubleProperty heightProperty = new SimpleDoubleProperty();
        /**
         * holds the window width value
         */
        private final DoubleProperty widthProperty = new SimpleDoubleProperty();
        /**
         * holds the window X position value
         */
        private final DoubleProperty xProperty = new SimpleDoubleProperty();
        /**
         * holds the window Y position value
         */
        private final DoubleProperty yProperty = new SimpleDoubleProperty();
        /**
         * holds the window maximized property
         */
        private final BooleanProperty maximizedProperty = new SimpleBooleanProperty();
        /**
         * holds the window draggable property
         */
        private final BooleanProperty draggableProperty = new SimpleBooleanProperty();
        /**
         * holds the window resizable property
         */
        private final BooleanProperty resizableProperty = new SimpleBooleanProperty();
        /**
         * holds the window scroll left property
         */
        private final DoubleProperty scrollVerticalProperty = new SimpleDoubleProperty();
        /**
         * holds the window scroll top property
         */
        private final DoubleProperty scrollHorizontalProperty = new SimpleDoubleProperty();
        /**
         * holds the window modality property
         */
        private final ObjectProperty<Modality> modalityProperty = new SimpleObjectProperty<>();
        /**
         * holds all the window's properties
         */
        private final Properties windowProperties = new Properties();
        /**
         * holds the window title property
         */
        private final StringProperty windowTitleProperty = new SimpleStringProperty();
        /**
         * holds the overall main window layout
         */
        private final VBox windowLayout;
        /**
         * holds the layout for the entire title bar
         */
        private final HBox windowTitleLayout;
        /**
         * holds the layout for the window title
         */
        private final HBox titleLayout;
        /**
         * holds the layout for the close button
         */
        private final HBox closeButtonLayout;
        /**
         * holds the layout for the window content
         */
        private final VBox contentLayout;
        /**
         * holds the window title node
         */
        private final Label windowTitle;
        /**
         * holds the window close widget
         */
        private final Label closeLabel;
        /**
         * provides default scrollbars for windows
         */
        private final ScrollPane mainScrollPane;
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
         * @param inWindowLayout a <code>WindowLayout</code> value
         */
        private void addWindow(WindowLayout inWindowMetaData)
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
                Set<WindowLayout> tempActiveWindows = new HashSet<>(activeWindows);
                for(WindowLayout window : tempActiveWindows) {
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
            synchronized(activeWindows) {
                double xPos = desktopCascadeWindowOffset;
                double yPos = desktopCascadeWindowOffset;
                double maxX = getWorkspaceRight();
                double maxY = getWorkspaceBottom();
                for(WindowLayout activeWindow : activeWindows) {
                    double windowWidth = activeWindow.getWidth();
                    double windowHeight = activeWindow.getHeight();
                    double proposedX = xPos;
                    if(proposedX + windowWidth > maxX) {
                        proposedX = desktopCascadeWindowOffset;
                    }
                    double proposedY = yPos;
                    if(proposedY + windowHeight > maxY) {
                        proposedY = desktopCascadeWindowOffset;
                    }
                    activeWindow.setX(proposedX);
                    activeWindow.setY(proposedY);
                    activeWindow.requestFocus();
                    xPos += desktopCascadeWindowOffset;
                    yPos += desktopCascadeWindowOffset;
                }
            }
            updateDisplayLayout();
        }
        /**
         * Rearrange the windows in this registry to a tiled pattern.
         */
        private void tileWindows()
        {
            synchronized(activeWindows) {
                int numWindows = activeWindows.size();
                if(numWindows == 0) {
                    return;
                }
                int numCols = (int)Math.floor(Math.sqrt(numWindows));
                int numRows = (int)Math.floor(numWindows / numCols);
                if(!isPerfectSquare(numWindows)) {
                    numCols += 1;
                }
                double windowWidth = Math.floorDiv(((Double)getWorkspaceRight()).intValue(),
                                                   numCols);
                double windowHeight = Math.floorDiv(((Double)(getWorkspaceBottom()-getWorkspaceTop())).intValue(),
                                                    numRows);
                int colNum = 0;
                int rowNum = 0;
                double posX = getWorkspaceLeft();
                double posY = getWorkspaceTop();
                for(WindowLayout activeWindow : activeWindows) {
                    double suggestedX = posX + (colNum * windowWidth);
                    double suggestedY = posY + (rowNum * windowHeight);
                    activeWindow.setWidth(windowWidth);
                    activeWindow.setHeight(windowHeight);
                    activeWindow.setX(suggestedX);
                    activeWindow.setY(suggestedY);
                    colNum += 1;
                    if(colNum == numCols) {
                        colNum = 0;
                        rowNum += 1;
                    }
                }
            }
            updateDisplayLayout();
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
                    // TODO need to do a permissions re-check, perhaps
                    try {
                        ContentViewFactory viewFactory = (ContentViewFactory)applicationContext.getBean(Class.forName(windowProperties.getProperty(windowContentViewFactoryProp)));
                        RestartNewWindowEvent restartWindowEvent = new RestartNewWindowEvent(viewFactory,
                                                                                             windowProperties);
                        WindowLayout newWindow = new WindowLayout(restartWindowEvent,
                                                                  viewFactory);
                        ContentView contentView = viewFactory.create(newWindow.getMainLayout(),
                                                                     restartWindowEvent,
                                                                     windowProperties);
                        newWindow.contentViewProperty.set(contentView);
                        newWindow.setRoot(contentView.getMainLayout());
                        addWindow(newWindow);
                        newWindow.show();
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            verifyAllWindowPositions();
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
         * Verify that the given window is within the acceptable bounds of the desktop viewable area.
         *
         * @param inWindow a <code>WindowLayout</code> value
         */
        private void verifyWindowLocation(WindowLayout inWindow)
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
         * @param inWindow a <code>WindowLayout</code> value
         */
        private void returnWindowToDesktop(WindowLayout inWindow)
        {
            double windowTop = inWindow.getY();
            double windowLeft = inWindow.getX();
            double windowHeight = inWindow.getHeight();
            double windowWidth = inWindow.getWidth();
            double windowBottom = windowTop + windowHeight;
            double windowRight = windowLeft + windowWidth;
            double workspaceWidth = getWorkspaceWidth();
            double workspaceHeight = getWorkspaceHeight();
            double workspaceBottom = workspaceHeight;
            double workspaceLeft = getWorkspaceLeft();
            double workspaceTop = getWorkspaceTop();
            double workspaceRight = workspaceWidth;
            int pad = desktopViewableAreaPad;
            // the order here is important: first, resize the window, if necessary
            double maxWidth = workspaceRight;
            if(windowWidth > maxWidth) {
                inWindow.setWidth(maxWidth - (pad*2));
            }
            if(windowWidth <= 10) {
                windowWidth = 100;
                inWindow.setWidth(windowWidth);
            }
            double maxHeight = workspaceBottom;
            if(windowHeight > maxHeight) {
                inWindow.setHeight(maxHeight - (pad*2));
            }
            // window is now no larger than desktop
            // check bottom
            if(windowBottom > workspaceBottom) {
                double newWindowTop = workspaceBottom - getWindowHeight(inWindow) - pad;
                inWindow.setY(newWindowTop);
            }
            // check top
            if(windowTop < workspaceTop+pad) {
                double newWindowTop = workspaceTop + pad;
                inWindow.setY(newWindowTop);
            }
            // window is now within the desktop Y range
            // check left
            if(windowLeft < workspaceLeft) {
                double newWindowLeft = workspaceLeft + pad;
                inWindow.setX(newWindowLeft);
            }
            // check right
            if(windowRight > workspaceRight) {
                double newWindowLeft = workspaceRight - getWindowWidth(inWindow) - pad;
                inWindow.setX(newWindowLeft);
            }
        }
        /**
         * Remove the given window from this registry.
         *
         * @param inWindowLayout a <code>WindowLayout</code> value
         */
        private void removeWindow(WindowLayout inWindowLayout)
        {
            synchronized(activeWindows) {
                activeWindows.remove(inWindowLayout);
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
                        for(WindowLayout window : activeWindows) {
                            try {
                                if(WindowManagerService.this.isWindowOutsideDesktop(window)) {
                                    returnWindowToDesktop(window);
                                }
                            } catch (Exception e) {
                                SLF4JLoggerProxy.warn(WindowManagerService.this,
                                                      ExceptionUtils.getRootCauseMessage(e));
                            }
                        }
                    }}
                );
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
                for(WindowLayout activeWindow : activeWindows) {
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
        private final Set<WindowLayout> activeWindows = Sets.newHashSet();
    }
    /**
     * base key for {@see UserAttributeType} display layout properties
     */
    private static final String propId = WindowLayout.class.getSimpleName();
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
//    /**
//     * window mode key name
//     */
//    private static final String windowModeProp = propId + "_mode";
//    /**
//     * window is modal key name
//     */
//    private static final String windowModalProp = propId + "_modal";
    /**
     * window view order key name
     */
    private static final String windowViewOrderProp = propId + "_viewOrder";
    /**
     * window is draggable key name
     */
    private static final String windowDraggableProp = propId + "_draggable";
    /**
     * window is resizable key name
     */
    private static final String windowResizableProp = propId + "_resizable";
    /**
     * window vertical scroll key name
     */
    private static final String windowVerticalScrollProp = propId + "_scrollV";
    /**
     * window horizontal scroll key name
     */
    private static final String windowHorizontalScrollProp = propId + "_scrollH";
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
    @Value("${metc.desktop.viewable.area.pad:2}")
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
