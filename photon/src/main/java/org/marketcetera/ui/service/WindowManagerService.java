package org.marketcetera.ui.service;

import java.net.URL;
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
import com.google.common.util.concurrent.ThreadFactoryBuilder;

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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

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
        // TODO call something on every window to update data or on-close or something like that
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
    /**
     * 
     *
     *
     * @param inMainStage
     */
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
        newWindow.setRoot(contentView.getNode());
        windowRegistry.addWindowListeners(newWindow);
        windowRegistry.updateDisplayLayout();
        newWindow.show();
    }
    private class WindowLayout
    {
        private WindowLayout(NewWindowEvent inEvent,
                             ContentViewFactory inViewFactory)
        {
            newWindowEventProperty.set(inEvent);
            viewFactoryProperty.set(inViewFactory);
            properties = inEvent.getProperties();
            setTitle(inEvent.getWindowTitle());
            uuidProperty.set(UUID.randomUUID().toString());
            setWindowStaticProperties();
            windowLayout = new VBox();
            windowTitleLayout = new HBox();
            titleLayout = new HBox();
            closeButtonLayout = new HBox();
            contentLayout = new VBox();
            windowLayout.getChildren().addAll(windowTitleLayout,
                                              contentLayout);
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
//                  inWindowLayout.updateProperties();
//                    WindowRegistry windowRegistry = getCurrentUserRegistry();
//                    windowRegistry.verifyWindowLocation(WindowLayout.this);
//                    windowRegistry.updateDisplayLayout();
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
//                    WindowRegistry windowRegistry = getCurrentUserRegistry();
//                    windowRegistry.verifyWindowLocation(WindowLayout.this);
//                    windowRegistry.updateDisplayLayout();
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
        }
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
                System.out.println(uuidProperty.get() + " updated: " + properties);
            });
            yProperty.addListener((observableValue,oldValue,newValue) -> {
                properties.setProperty(windowPosYProp,
                                       String.valueOf(newValue));
                System.out.println(uuidProperty.get() + " updated: " + properties);
            });
            heightProperty.addListener((observableValue,oldValue,newValue) -> {
                properties.setProperty(windowHeightProp,
                                       String.valueOf(newValue));
                System.out.println(uuidProperty.get() + "height updated: " + properties);
            });
            widthProperty.addListener((observableValue,oldValue,newValue) -> {
                properties.setProperty(windowWidthProp,
                                       String.valueOf(newValue));
                System.out.println(uuidProperty.get() + " width updated: " + properties);
            });
            windowTitleProperty.addListener((observableValue,oldValue,newValue) -> {
                if(newValue == null) {
                    properties.remove(windowTitleProp);
                } else {
                    properties.setProperty(windowTitleProp,
                                           getTitle());
                }
                System.out.println(uuidProperty.get() + " updated: " + properties);
            });
            draggableProperty.addListener((observableValue,oldValue,newValue) -> {
                properties.setProperty(windowDraggableProp,
                                       String.valueOf(newValue));
                System.out.println(uuidProperty.get() + " updated: " + properties);
            });
            resizableProperty.addListener((observableValue,oldValue,newValue) -> {
                properties.setProperty(windowResizableProp,
                                       String.valueOf(newValue));
                System.out.println(uuidProperty.get() + " updated: " + properties);
            });
            scrollLeftProperty.addListener((observableValue,oldValue,newValue) -> {
                properties.setProperty(windowScrollLeftProp,
                                       String.valueOf(newValue));
                System.out.println(uuidProperty.get() + " updated: " + properties);
            });
            scrollTopProperty.addListener((observableValue,oldValue,newValue) -> {
                properties.setProperty(windowScrollTopProp,
                                       String.valueOf(newValue));
                System.out.println(uuidProperty.get() + " updated: " + properties);
            });
            viewOrderProperty.addListener((observableValue,oldValue,newValue) -> {
                properties.setProperty(windowViewOrderProp,
                                       String.valueOf(newValue));
                System.out.println(uuidProperty.get() + " updated: " + properties);
            });
            // TODO
//            properties.setProperty(windowModeProp,
//                                   String.valueOf(isMaximized()));
        }
        /**
         * Update the window object with the stored telemetry.
         */
        private void updateWindow()
        {
            setWidth(Double.parseDouble(properties.getProperty(windowWidthProp)));
            setHeight(Double.parseDouble(properties.getProperty(windowHeightProp)));
            setModality(properties.getProperty(windowModalProp) == null ? Modality.NONE:Modality.valueOf(properties.getProperty(windowModalProp)));
            Boolean isMaximized = Boolean.parseBoolean(properties.getProperty(windowModeProp));
            setMaximized(isMaximized);
            setScrollLeft(Double.parseDouble(properties.getProperty(windowScrollLeftProp)));
            setScrollTop(Double.parseDouble(properties.getProperty(windowScrollTopProp)));
            setDraggable(Boolean.parseBoolean(properties.getProperty(windowDraggableProp)));
            setResizable(Boolean.parseBoolean(properties.getProperty(windowResizableProp)));
            setTitle(properties.getProperty(windowTitleProp));
            setX(getDoubleValue(properties,
                                windowPosXProp));
            setY(getDoubleValue(properties,
                                       windowPosYProp));
            setViewOrder(Double.parseDouble(windowViewOrderProp));
        }
        /**
         *
         *
         * @param inViewOrder
         */
        private void setViewOrder(double inViewOrder)
        {
            viewOrderProperty.set(inViewOrder);
            Platform.runLater(() -> windowLayout.setViewOrder(inViewOrder));
        }
        private void setContentView(ContentView inContentView)
        {
            contentViewProperty.set(inContentView);
        }
        /**
         *
         *
         */
        private void requestFocus()
        {
            windowLayout.requestFocus();
        }
        /**
         *
         *
         * @return
         */
        private Properties getProperties()
        {
            return windowProperties;
        }
        /**
         *
         *
         * @param inNode
         */
        private void setRoot(Node inNode)
        {
            contentLayout.getChildren().add(inNode);
        }
        /**
         *
         *
         * @param inDraggable
         */
        private void setDraggable(boolean inDraggable)
        {
            draggableProperty.set(inDraggable);
        }
        /**
         *
         *
         * @param inHeight
         */
        private void setHeight(double inHeight)
        {
            heightProperty.set(inHeight);
            windowLayout.setPrefHeight(inHeight);
            windowLayout.setMinHeight(inHeight);
        }
        /**
         *
         *
         * @param inWidth
         */
        private void setWidth(double inWidth)
        {
            widthProperty.set(inWidth);
            windowLayout.setPrefWidth(inWidth);
            windowLayout.setMinWidth(inWidth);
        }
        /**
         *
         *
         * @param inResizable
         */
        public void setResizable(boolean inResizable)
        {
            resizableProperty.set(inResizable);
        }
        /**
         *
         *
         * @param inWindowTitle
         */
        private void setTitle(String inWindowTitle)
        {
            windowTitleProperty.set(inWindowTitle);
        }
        /**
         *
         *
         * @return
         */
        private Node getMainLayout()
        {
            return windowLayout;
        }
        /**
         *
         *
         * @return
         */
        private double getX()
        {
            return xProperty.get();
        }
        /**
         *
         *
         * @return
         */
        private double getY()
        {
            return yProperty.get();
        }
        /**
         *
         *
         * @return
         */
        private double getHeight()
        {
            return heightProperty.get();
        }
        /**
         *
         *
         * @return
         */
        private double getWidth()
        {
            return widthProperty.get();
        }
        /**
         *
         *
         * @return
         */
        private boolean isMaximized()
        {
            return maximizedProperty.get();
        }
        /**
         *
         *
         * @return
         */
        private String getTitle()
        {
            return windowTitle.getText();
        }
        /**
         *
         *
         * @return
         */
        private Modality getModality()
        {
            return modalityProperty.get();
        }
        /**
         *
         *
         * @return
         */
        private boolean isDraggable()
        {
            return draggableProperty.get();
        }
        /**
         *
         *
         * @return
         */
        private boolean isResizable()
        {
            return resizableProperty.get();
        }
        /**
         *
         *
         * @return
         */
        private double getScrollLeft()
        {
            return scrollLeftProperty.get();
        }
        /**
         *
         *
         * @param inModality
         */
        private void setModality(Modality inModality)
        {
            modalityProperty.set(inModality);
        }
        /**
         *
         *
         * @param inMaximized
         */
        private void setMaximized(boolean inMaximized)
        {
            maximizedProperty.set(inMaximized);
        }
        /**
         *
         *
         * @param inScrollLeft
         */
        private void setScrollLeft(double inScrollLeft)
        {
            scrollLeftProperty.set(inScrollLeft);
        }
        /**
         *
         *
         * @param inScrollTop
         */
        private void setScrollTop(double inScrollTop)
        {
            scrollTopProperty.set(inScrollTop);
        }
        /**
         *
         *
         * @param inX
         */
        private void setX(double inX)
        {
            xProperty.set(inX);
            getMainLayout().translateXProperty().set(inX);
        }
        /**
         *
         *
         * @param inY
         */
        private void setY(double inY)
        {
            yProperty.set(inY);
            getMainLayout().translateYProperty().set(inY);
        }
        /**
         *
         *
         */
        private void close()
        {
            WindowRegistry windowRegistry = getCurrentUserRegistry();
            if(!windowRegistry.isLoggingOut()) {
                windowRegistry.removeWindow(this);
                windowRegistry.updateDisplayLayout();
            }
            PhotonApp.getWorkspace().getChildren().remove(getMainLayout());
        }
        private void show()
        {
            System.out.println("COCO: displaying " + uuidProperty + " x: " + getX() + " y: " + getY() + 
                               " width: " + getWidth() + " height: " + getHeight() + 
                               " minWidth: " + windowLayout.getMinWidth() + " minHeight: " + windowLayout.getMinHeight() +
                               " prefWidth: " + windowLayout.getPrefWidth() + " prefHeight: " + windowLayout.getPrefHeight() +
                               " layoutWidth: " + windowLayout.getWidth() + " layoutHeight: " + windowLayout.getHeight());
            getMainLayout().translateXProperty().set(getX());
            getMainLayout().translateYProperty().set(getY());
            windowLayout.setViewOrder(-1);
            windowLayout.autosize();
            System.out.println("COCO: displaying " + uuidProperty + " x: " + getX() + " y: " + getY() + 
                               " width: " + getWidth() + " height: " + getHeight() + 
                               " minWidth: " + windowLayout.getMinWidth() + " minHeight: " + windowLayout.getMinHeight() +
                               " prefWidth: " + windowLayout.getPrefWidth() + " prefHeight: " + windowLayout.getPrefHeight() +
                               " layoutWidth: " + windowLayout.getWidth() + " layoutHeight: " + windowLayout.getHeight());
            PhotonApp.getWorkspace().getChildren().add(getMainLayout());
            requestFocus();
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
        private final ObjectProperty<ContentView> contentViewProperty = new SimpleObjectProperty<>();
        private final ObjectProperty<NewWindowEvent> newWindowEventProperty = new SimpleObjectProperty<>();
        private final DoubleProperty viewOrderProperty = new SimpleDoubleProperty();
        /**
         * cached uuid value
         */
        private final StringProperty uuidProperty = new SimpleStringProperty();
        /**
         * properties used to record details about this window
         */
        private final Properties properties;
        private final ObjectProperty<ContentViewFactory> viewFactoryProperty = new SimpleObjectProperty<>();
        private final DoubleProperty heightProperty = new SimpleDoubleProperty();
        private final DoubleProperty widthProperty = new SimpleDoubleProperty();
        private final DoubleProperty xProperty = new SimpleDoubleProperty();
        private final DoubleProperty yProperty = new SimpleDoubleProperty();
        private final BooleanProperty maximizedProperty = new SimpleBooleanProperty();
        private final BooleanProperty draggableProperty = new SimpleBooleanProperty();
        private final BooleanProperty resizableProperty = new SimpleBooleanProperty();
        private final DoubleProperty scrollLeftProperty = new SimpleDoubleProperty();
        private final DoubleProperty scrollTopProperty = new SimpleDoubleProperty();
        private final ObjectProperty<Modality> modalityProperty = new SimpleObjectProperty<>();
        private final Properties windowProperties = new Properties();
        private final StringProperty windowTitleProperty = new SimpleStringProperty();
        private VBox windowLayout;
        private HBox windowTitleLayout;
        private HBox titleLayout;
        private HBox closeButtonLayout;
        private VBox contentLayout;
        private Label windowTitle;
        private Label closeLabel;
        /**
         *
         *
         * @return
         */
        private String getUuid()
        {
            return uuidProperty.get();
        }
        /**
         *
         *
         * @return
         */
        private String getStorableValue()
        {
            System.out.println("Storing: " + properties);
            return Util.propertiesToString(properties);
        }
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
        DesktopParameters params = SessionUser.getCurrent().getAttribute(DesktopParameters.class);
        return (getWindowBottom(inWindow) > params.getBottom()) || (getWindowLeft(inWindow) < params.getLeft()) || (getWindowTop(inWindow) < params.getTop()) || (getWindowRight(inWindow) > params.getRight());
    }
    /**
     * Get the window top edge coordinate in pixels.
     *
     * @param inWindow a <code>WindowLayout</code> value
     * @return a <code>double</code> value
     */
    private double getWindowTop(WindowLayout inWindow)
    {
        return inWindow.getY();
    }
    /**
     * Get the window left edge coordinate in pixels.
     *
     * @param inWindow a <code>WindowLayout</code> value
     * @return a <code>double</code> value
     */
    private double getWindowLeft(WindowLayout inWindow)
    {
        return inWindow.getX();
    }
    /**
     * Get the window bottom edge coordinate in pixels.
     *
     * @param inWindow a <code>WindowLayout</code> value
     * @return a <code>double</code> value
     */
    private double getWindowBottom(WindowLayout inWindow)
    {
        return getWindowTop(inWindow) + getWindowHeight(inWindow);
    }
    /**
     * Get the window right edge coordinate in pixels.
     *
     * @param inWindow a <code>WindowLayout</code> value
     * @return a <code>double</code> value
     */
    private double getWindowRight(WindowLayout inWindow)
    {
        return getWindowLeft(inWindow) + getWindowWidth(inWindow);
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
//    /**
//     * Holds meta-data for windows.
//     *
//     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
//     * @version $Id$
//     * @since $Release$
//     */
//    private class WindowMetaData
//    {
//        /* (non-Javadoc)
//         * @see java.lang.Object#toString()
//         */
//        @Override
//        public String toString()
//        {
//            return properties.toString();
//        }
//        /**
//         * Create a new WindowMetaData instance.
//         *
//         * <p>This constructor is invoked for a new window.
//         * 
//         * @param inEvent a <code>NewWindowEvent</code> value
//         * @param inWindow a <code>WindowLayout</code> value
//         * @param inContentViewFactory a <code>ContentViewFactory</code> value
//         */
//        private WindowMetaData(NewWindowEvent inEvent,
//                               WindowLayout inWindow,
//                               ContentViewFactory inContentViewFactory)
//        {
//            properties = inEvent.getProperties();
//            window = inWindow;
//            setWindowStaticProperties(inContentViewFactory,
//                                      UUID.randomUUID().toString());
//            updateProperties();
//        }
//        /**
//         * Create a new WindowMetaData instance.
//         *
//         * <p>This constructor is invoked to recreate a previously-created window.
//         * 
//         * @param inProperties a <code>Properties</code> value
//         * @param inWindow a <code>WindowLayout</code> value
//         */
//        private WindowMetaData(Properties inProperties,
//                               WindowLayout inWindow)
//        {
//            // TODO need to do a permissions re-check, perhaps
//            window = inWindow;
//            properties = inProperties;
//            try {
//                ContentViewFactory contentViewFactory = (ContentViewFactory)applicationContext.getBean(Class.forName(inProperties.getProperty(windowContentViewFactoryProp)));
//                ContentView contentView = contentViewFactory.create(window.getMainLayout(),
//                                                                    new RestartNewWindowEvent(contentViewFactory,
//                                                                                              properties.getProperty(windowTitleProp)),
//                                                                    properties);
//                inWindow.contentViewProperty.set(contentView);
//                window.setRoot(contentView.getNode());
//            } catch (ClassNotFoundException e) {
//                throw new RuntimeException(e);
//            }
//            // update window from properties, effectively restoring it to its previous state
//            updateWindow();
//        }
//        /**
//         * Get the storable value for this window.
//         *
//         * @return a <code>String</code> value
//         */
//        private String getStorableValue()
//        {
//            return Util.propertiesToString(properties);
//        }
//        /**
//         * Get the properties value.
//         *
//         * @return a <code>Properties</code> value
//         */
//        private Properties getProperties()
//        {
//            return properties;
//        }
//        /**
//         * Get the window value.
//         *
//         * @return a <code>WindowLayout</code> value
//         */
//        private WindowLayout getWindow()
//        {
//            return window;
//        }
//        /**
//         * Update the window telemetry from the underlying window object.
//         */
//        private void updateProperties()
//        {
//            properties.setProperty(windowPosXProp,
//                                   String.valueOf(window.getX()));
//            properties.setProperty(windowPosYProp,
//                                   String.valueOf(window.getX()));
//            properties.setProperty(windowHeightProp,
//                                   String.valueOf(window.getHeight()));
//            properties.setProperty(windowWidthProp,
//                                   String.valueOf(window.getWidth()));
//            properties.setProperty(windowModeProp,
//                                   String.valueOf(window.isMaximized()));
//            if(window.getTitle() != null) {
//                properties.setProperty(windowTitleProp,
//                                       window.getTitle());
//            }
//            properties.setProperty(windowDraggableProp,
//                                   String.valueOf(window.isDraggable()));
//            properties.setProperty(windowResizableProp,
//                                   String.valueOf(window.isResizable()));
//            properties.setProperty(windowScrollLeftProp,
//                                   String.valueOf(window.getScrollLeft()));
//            properties.setProperty(windowScrollTopProp,
//                                   String.valueOf(window.getScrollLeft()));
//            properties.setProperty(windowFocusProp,
//                                   String.valueOf(hasFocus()));
//        }
//        /**
//         * Update the window object with the stored telemetry.
//         */
//        private void updateWindow()
//        {
//            window.setWidth(Double.parseDouble(properties.getProperty(windowWidthProp)));
//            window.setHeight(Double.parseDouble(properties.getProperty(windowHeightProp)));
//            window.setModality(properties.getProperty(windowModalProp) == null ? Modality.NONE:Modality.valueOf(properties.getProperty(windowModalProp)));
//            Boolean isMaximized = Boolean.parseBoolean(properties.getProperty(windowModeProp));
//            window.setMaximized(isMaximized);
//            window.setScrollLeft(Double.parseDouble(properties.getProperty(windowScrollLeftProp)));
//            window.setScrollTop(Double.parseDouble(properties.getProperty(windowScrollTopProp)));
//            window.setDraggable(Boolean.parseBoolean(properties.getProperty(windowDraggableProp)));
//            window.setResizable(Boolean.parseBoolean(properties.getProperty(windowResizableProp)));
//            window.setTitle(properties.getProperty(windowTitleProp));
//            window.setX(getDoubleValue(properties,
//                                       windowPosXProp));
//            window.setY(getDoubleValue(properties,
//                                       windowPosYProp));
//            setHasFocus(Boolean.parseBoolean(properties.getProperty(windowFocusProp)));
//            if(hasFocus) {
//                window.requestFocus();
//            }
//        }
//        /**
//         * Set the immutable properties of this window to the underlying properties storage.
//         *
//         * @param inContentViewFactory a <code>ContentViewFactory</code> value
//         * @param inUuid a <code>String</code>value
//         */
//        private void setWindowStaticProperties(ContentViewFactory inContentViewFactory,
//                                               String inUuid)
//        {
//            properties.setProperty(windowContentViewFactoryProp,
//                                   inContentViewFactory.getClass().getCanonicalName());
//            properties.setProperty(windowUuidProp,
//                                   inUuid);
//        }
//        /**
//         * Close this window and remove it from active use.
//         */
//        private void close()
//        {
//            getWindow().close();
//        }
//        /**
//         * Get the window uuid value.
//         *
//         * @return a <code>String</code> value
//         */
//        private String getUuid()
//        {
//            if(uuid == null) {
//                uuid = properties.getProperty(windowUuidProp);
//            }
//            return uuid;
//        }
//        /**
//         * Get the hasFocus value.
//         *
//         * @return a <code>boolean</code> value
//         */
//        private boolean hasFocus()
//        {
//            return hasFocus;
//        }
//        /**
//         * Sets the hasFocus value.
//         *
//         * @param inHasFocus a <code>boolean</code> value
//         */
//        private void setHasFocus(boolean inHasFocus)
//        {
//            hasFocus = inHasFocus;
//        }
//        /**
//         * indicates if this window has focus or not
//         */
//        private transient boolean hasFocus;
//        /**
//         * cached uuid value
//         */
//        private transient String uuid;
//        /**
//         * properties used to record details about this window
//         */
//        private final Properties properties;
//        /**
//         * underlying UI element
//         */
//        private final WindowLayout window;
//    }
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
            synchronized(windowPositionExaminerThreadPool) {
                cancelWindowPositionMonitor();
            }
            try {
                synchronized(activeWindows) {
                    double xPos = desktopCascadeWindowOffset;
                    double yPos = desktopCascadeWindowOffset;
                    DesktopParameters params = SessionUser.getCurrent().getAttribute(DesktopParameters.class);
                    double maxX = params.getRight();
                    double maxY = params.getBottom();
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
//                        activeWindow.updateProperties();
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
//                        activeWindow.updateProperties();
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
                        newWindow.setRoot(contentView.getNode());
                        addWindow(newWindow);
                        addWindowListeners(newWindow);
                        newWindow.show();
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
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
         * @param inWindowWrapper a <code>WindowLayout</code> value
         */
        private void addWindowListeners(WindowLayout inWindowWrapper)
        {
            WindowRegistry windowRegistry = this;
            WindowLayout newWindow = inWindowWrapper;
//            newWindow.addEventHandler(MouseEvent.MOUSE_CLICKED,
//                                      new EventHandler<MouseEvent>() {
//                @Override
//                public void handle(MouseEvent inEvent)
//                {
//                    SLF4JLoggerProxy.trace(WindowManagerService.this,
//                                           "Click: {}",
//                                           inEvent);
//                    verifyWindowLocation(newWindow);
//                    inWindowWrapper.updateProperties();
//                    updateDisplayLayout();
//                }}
//            );
//            newWindow.setOnShown(new EventHandler<WindowEvent>() {
//                @Override
//                public void handle(WindowEvent inEvent)
//                {
//                    SLF4JLoggerProxy.trace(WindowManagerService.this,
//                                           "Shown: {}",
//                                           inEvent);
//                    verifyWindowLocation(newWindow);
//                    inWindowWrapper.updateProperties();
//                    updateDisplayLayout();
//                }}
//            );
////            newWindow.addWindowModeChangeListener(inEvent -> {
////                SLF4JLoggerProxy.trace(WindowManagerService.this,
////                                       "Mode change: {}",
////                                       inEvent);
////                // TODO might want to do this, might not. a maximized window currently tromps all over the menu bar
//////                verifyWindowLocation(newWindow);
////                inWindowWrapper.updateProperties();
////                updateDisplayLayout();
////            });
//            newWindow.widthProperty().addListener(new ChangeListener<Number>() {
//                @Override
//                public void changed(ObservableValue<? extends Number> inObservable,
//                                    Number inOldValue,
//                                    Number inNewValue)
//                {
//                    newWindowResize("width",
//                                    inWindowWrapper);
//                }}
//            );
//            newWindow.heightProperty().addListener(new ChangeListener<Number>() {
//                @Override
//                public void changed(ObservableValue<? extends Number> inObservable,
//                                    Number inOldValue,
//                                    Number inNewValue)
//                {
//                    newWindowResize("height",
//                                    inWindowWrapper);
//                }}
//            );
//            newWindow.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST,new EventHandler<WindowEvent>() {
//                @Override
//                public void handle(WindowEvent inEvent)
//                {
//                    SLF4JLoggerProxy.trace(WindowManagerService.this,
//                                           "Close: {}",
//                                           inEvent);
//                    // this listener will be fired during log out, but, we don't want to update the display layout in that case
//                    if(!windowRegistry.isLoggingOut()) {
//                        windowRegistry.removeWindow(inWindowWrapper);
//                        updateDisplayLayout();
//                    }
//                }}
//            );
////            newWindow.addBlurListener(inEvent -> {
////                SLF4JLoggerProxy.trace(WindowManagerService.this,
////                                       "Blur: {}",
////                                       inEvent);
////                verifyWindowLocation(newWindow);
////                inWindowWrapper.setHasFocus(false);
////                inWindowWrapper.updateProperties();
////                updateDisplayLayout();
////            });
////            newWindow.addFocusListener(inEvent -> {
////                SLF4JLoggerProxy.trace(WindowManagerService.this,
////                                       "Focus: {}",
////                                       inEvent);
////                verifyWindowLocation(newWindow);
////                inWindowWrapper.setHasFocus(true);
////                inWindowWrapper.updateProperties();
////                updateDisplayLayout();
////            });
//            newWindow.addEventHandler(ContextMenuEvent.CONTEXT_MENU_REQUESTED,new EventHandler<ContextMenuEvent>() {
//                @Override
//                public void handle(ContextMenuEvent inEvent)
//                {
//                    SLF4JLoggerProxy.trace(WindowManagerService.this,
//                                           "Context click: {}",
//                                           inEvent);
//                    verifyWindowLocation(newWindow);
//                    inWindowWrapper.updateProperties();
//                    updateDisplayLayout();
//                }}
//            );
        }
        private void newWindowResize(String inDimension,
                                     WindowLayout inWindowLayout)
        {
            SLF4JLoggerProxy.trace(WindowManagerService.this,
                                   "Verify: {}",
                                   inDimension);
            verifyWindowLocation(inWindowLayout);
//            inWindowLayout.updateProperties();
            updateDisplayLayout();
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
//            System.out.println("Entering returnWindowToDesktop");
//            int pad = desktopViewableAreaPad;
//            DesktopParameters params = SessionUser.getCurrent().getAttribute(DesktopParameters.class);
//            // the order here is important: first, resize the window, if necessary
//            double maxWidth = params.getRight()-params.getLeft();
//            double windowWidth = getWindowWidth(inWindow);
//            if(windowWidth > maxWidth) {
//                inWindow.setWidth(maxWidth - (pad*2));
//            }
//            if(windowWidth <= 10) {
//                windowWidth = 100;
//                inWindow.setWidth(windowWidth);
//            }
//            double maxHeight = params.getBottom() - params.getTop();
//            double windowHeight = getWindowHeight(inWindow);
//            if(windowHeight > maxHeight) {
//                inWindow.setHeight(maxHeight - (pad*2));
//            }
//            // window is now no larger than desktop
//            // check bottom
//            double windowBottom = getWindowBottom(inWindow);
//            if(windowBottom > params.getBottom()) {
//                double newWindowTop = params.getBottom() - getWindowHeight(inWindow) - pad;
//                inWindow.setY(newWindowTop);
//            }
//            // check top
//            double windowTop = getWindowTop(inWindow);
//            if(windowTop < params.getTop()+pad) {
//                double newWindowTop = params.getTop() + pad;
//                inWindow.setY(newWindowTop);
//            }
//            // window is now within the desktop Y range
//            // check left
//            double windowLeft = getWindowLeft(inWindow);
//            if(windowLeft < params.getLeft()) {
//                double newWindowLeft = params.getLeft() + pad;
//                inWindow.setX(newWindowLeft);
//            }
//            // check right
//            double windowRight = getWindowRight(inWindow);
//            if(windowRight > params.getRight()) {
//                double newWindowLeft = params.getRight() - getWindowWidth(inWindow) - pad;
//                inWindow.setX(newWindowLeft);
//            }
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
                        for(WindowLayout window : activeWindows) {
                            try {
                                if(WindowManagerService.this.isWindowOutsideDesktop(window)) {
                                    returnWindowToDesktop(window);
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
    /**
     * window mode key name
     */
    private static final String windowModeProp = propId + "_mode";
    /**
     * window is modal key name
     */
    private static final String windowModalProp = propId + "_modal";
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
     * window scroll left key name
     */
    private static final String windowScrollLeftProp = propId + "_scrollLeft";
    /**
     * window scroll top key name
     */
    private static final String windowScrollTopProp = propId + "_scrollTop";
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
