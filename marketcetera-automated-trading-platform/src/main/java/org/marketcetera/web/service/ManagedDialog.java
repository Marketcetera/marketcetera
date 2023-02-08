package org.marketcetera.web.service;

import java.util.Properties;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.marketcetera.core.Pair;
import org.marketcetera.core.Util;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.web.events.NewWindowEvent;
import org.marketcetera.web.service.WindowManagerService.WindowRegistry;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 * @todo main layout scrollable/scroll bar?
 * @todo window maximize/minimize
 */
public class ManagedDialog
        extends Dialog
{
    /**
     * Create a new ManagedDialog instance.
     *
     * @param inEvent
     * @param inWindowId
     * @param inWindowManager
     * @param inWindowRegistry 
     */
    public ManagedDialog(NewWindowEvent inEvent,
                         String inWindowId,
                         WindowManagerService inWindowManager,
                         WindowRegistry inWindowRegistry)
    {
        windowManager = inWindowManager;
        windowRegistry = inWindowRegistry;
        Pair<String,String> suggestedSize = inEvent.getWindowSize();
        properties = inEvent.getProperties();
        properties.setProperty(windowContentViewFactoryProp,
                               inEvent.getViewFactoryType().getCanonicalName());
        // TODO pull stored size from properties?
        position = new Position("100px",
                                "100px");
        position.setHeight(suggestedSize.getFirstMember());
        position.setWidth(suggestedSize.getSecondMember());
        super.setId(inWindowId);
        uuid = inWindowId;
    }
    /**
     * 
     * Create a new ManagedDialog instance.
     *
     * @param inWindowProperties
     * @param inWindowManager
     * @param inWindowRegistry
     */
    ManagedDialog(Properties inWindowProperties,
                  WindowManagerService inWindowManager,
                  WindowRegistry inWindowRegistry)
    {
        windowManager = inWindowManager;
        windowRegistry = inWindowRegistry;
        properties = inWindowProperties;
        // TODO pull stored size from properties?
        position = new Position("100px",
                                "100px");
    }
    /* (non-Javadoc)
     * @see com.vaadin.flow.component.dialog.Dialog#setWidth(java.lang.String)
     */
    @Override
    public void setWidth(String inValue)
    {
        System.out.println("COCO: set width to " + inValue);
        super.setWidth(inValue);
    }
    /* (non-Javadoc)
     * @see com.vaadin.flow.component.dialog.Dialog#setHeight(java.lang.String)
     */
    @Override
    public void setHeight(String inValue)
    {
        System.out.println("COCO: set height to " + inValue);
        super.setHeight(inValue);
    }
    /**
     *
     *
     */
    public void focus()
    {
        UI.getCurrent().getPage().executeJs("document.getElementById(\"" + mainLayout.getId() + "\").focus();");
    }
    /**
     *
     *
     * @return
     */
    String getUuid()
    {
        return uuid;
    }
    /**
     *
     *
     * @return
     */
    Properties getProperties()
    {
        return properties;
    }
    /**
     *
     *
     * @return
     */
    String getStorableValue()
    {
        return Util.propertiesToString(properties);
    }
    /**
     *
     *
     */
    void updateProperties()
    {
        updatePositionTopLeft();
        properties.setProperty(windowPosXProp,
                               String.valueOf(getWindowLeft()));
        properties.setProperty(windowPosYProp,
                               String.valueOf(getWindowBottom()));
        properties.setProperty(windowHeightProp,
                               String.valueOf(getWindowHeight()));
        properties.setProperty(windowWidthProp,
                               String.valueOf(getWindowWidth()));
        properties.setProperty(windowHeightUnitProp,
                               Unit.PIXELS.getSymbol());
        properties.setProperty(windowWidthUnitProp,
                               Unit.PIXELS.getSymbol());
        //properties.setProperty(windowModeProp,
        //      String.valueOf(window.getWindowMode()));
        properties.setProperty(windowTitleProp,
                               headerLabel.getText());
        properties.setProperty(windowModalProp,
                               String.valueOf(isModal()));
        properties.setProperty(windowDraggableProp,
                               String.valueOf(isDraggable()));
        properties.setProperty(windowResizableProp,
                               String.valueOf(isResizable()));
        //properties.setProperty(windowScrollLeftProp,
        //      String.valueOf(window.getScrollLeft()));
        //properties.setProperty(windowScrollTopProp,
        //      String.valueOf(window.getScrollTop()));
//        properties.setProperty(windowFocusProp,
//                               String.valueOf(hasFocus()));
        if(!getId().isPresent()) {
            properties.remove(windowStyleId);
        } else {
            properties.setProperty(windowStyleId,
                                   getId().get());
        }
        System.out.println("COCO: " + getId() + " properties: " + properties);
    }
    /**
     *
     *
     * @param inEvent
     * @return
     */
    void dialogClose(DialogCloseActionEvent inEvent)
    {
        SLF4JLoggerProxy.debug(this,
                               "DialogCloseEvent: {}",
                               inEvent);
        // TODO
////    newWindow.addCloseListener(inEvent -> {
////    SLF4JLoggerProxy.trace(WindowManagerService.this,
////                           "Close: {}",
////                           inEvent);
////    // this listener will be fired during log out, but, we don't want to update the display layout in that case
////    if(!windowRegistry.isLoggingOut()) {
////        windowRegistry.removeWindow(inWindowWrapper);
////        updateDisplayLayout();
////    }
////});
    }
    /**
     *
     *
     * @param inEvent
     * @return
     */
    void dialogAttach(AttachEvent inEvent)
    {
        SLF4JLoggerProxy.debug(this,
                               "DialogAttachEvent: {} session={} ui={}",
                               inEvent,
                               inEvent.getSession(),
                               inEvent.getUI());
        enablePositioning(true);
        updateProperties();
    }
    /**
     *
     *
     * @param inEvent
     * @return
     */
    void dialogClick(ClickEvent<?> inEvent)
    {
        SLF4JLoggerProxy.debug(this,
                               "DialogClickEvent: {} button={} clickCount={} clientX={} clientY={} screenX={} screenY={}",
                               inEvent,
                               inEvent.getButton(),
                               inEvent.getClickCount(),
                               inEvent.getClientX(),
                               inEvent.getClientY(),
                               inEvent.getScreenX(),
                               inEvent.getScreenY());
        updateProperties();
        windowRegistry.verifyWindowLocation(this);
        windowRegistry.updateDisplayLayout();
    }
    /**
     *
     *
     * @param inEvent
     * @return
     */
    void dialogDetach(DetachEvent inEvent)
    {
        SLF4JLoggerProxy.debug(this,
                               "DialogDetachEvent: {}",
                               inEvent);
    }
    void dialogResize(DialogResizeEvent inEvent)
    {
        SLF4JLoggerProxy.debug(this,
                               "DialogResizeEvent: {} {} x {}",
                               inEvent,
                               inEvent.getHeight(),
                               inEvent.getWidth());
        position.setHeight(inEvent.getHeight());
        position.setWidth(inEvent.getWidth());
        updateProperties();
        windowRegistry.verifyWindowLocation(this);
        windowRegistry.updateDisplayLayout();
    }
    /**
     *
     *
     * @param inEvent
     * @return
     */
    void dialogOpened(OpenedChangeEvent<Dialog> inEvent)
    {
        SLF4JLoggerProxy.debug(this,
                               "DialogOpenedChangeEvent: {}",
                               inEvent);
        // setPosition?
        // setSize?
    }
    /**
     *
     *
     * @param inComponent
     */
    void setContent(Component inComponent)
    {
        mainLayout = new VerticalLayout();
        mainLayout.setId(UUID.randomUUID().toString());
        bodyLayout = new FlexLayout();
        bodyLayout.setId(UUID.randomUUID().toString());
        headerLayout = new HorizontalLayout();
        headerLayout.setId(UUID.randomUUID().toString());
        bodyScroller = new Scroller();
        bodyScroller.setId(UUID.randomUUID().toString());
        headerLabel = new Label();
        headerLabel.setId(UUID.randomUUID().toString());
        headerLabel.setWidthFull();
        headerLabel.setText(properties.getProperty(WindowManagerService.windowTitleProp));
        mainLayout.setSizeFull();
        headerLayout.setWidthFull();
        headerLayout.add(headerLabel);
        bodyLayout.add(bodyScroller);
        bodyLayout.setSizeFull();
        bodyScroller.setSizeFull();
        bodyScroller.setContent(inComponent);
        bodyLayout.add(bodyScroller);
        mainLayout.add(headerLayout,
                       bodyLayout);
        add(mainLayout);
        addWindowListeners();
    }
    /**
     * Add the necessary window listeners to the given window meta data.
     */
    private void addWindowListeners()
    {
        addResizeListener(inEvent -> { dialogResize(inEvent); });
        addOpenedChangeListener(inEvent -> dialogOpened(inEvent));
        addAttachListener(inEvent -> dialogAttach(inEvent));
        addDetachListener(inEvent -> dialogDetach(inEvent));
        addDialogCloseActionListener(inEvent -> dialogClose(inEvent));
        mainLayout.addClickListener(inClickEvent -> dialogClick(inClickEvent));
        headerLayout.addClickListener(inClickEvent -> dialogClick(inClickEvent));
//        // TODO
////        newWindow.addWindowModeChangeListener(inEvent -> {
////            SLF4JLoggerProxy.trace(WindowManagerService.this,
////                                   "Mode change: {}",
////                                   inEvent);
////            // TODO might want to do this, might not. a maximized window currently tromps all over the menu bar
//////            verifyWindowLocation(newWindow);
////            inWindowWrapper.updateProperties();
////            updateDisplayLayout();
////        });
////        newWindow.addBlurListener(inEvent -> {
////            SLF4JLoggerProxy.trace(WindowManagerService.this,
////                                   "Blur: {}",
////                                   inEvent);
////            verifyWindowLocation(newWindow);
////            inWindowWrapper.setHasFocus(false);
////            inWindowWrapper.updateProperties();
////            updateDisplayLayout();
////        });
////        newWindow.addFocusListener(inEvent -> {
////            SLF4JLoggerProxy.trace(WindowManagerService.this,
////                                   "Focus: {}",
////                                   inEvent);
////            verifyWindowLocation(newWindow);
////            inWindowWrapper.setHasFocus(true);
////            inWindowWrapper.updateProperties();
////            updateDisplayLayout();
////        });
////        newWindow.addContextClickListener(inEvent -> {
////            SLF4JLoggerProxy.trace(WindowManagerService.this,
////                                   "Context click: {}",
////                                   inEvent);
////            verifyWindowLocation(newWindow);
////            inWindowWrapper.updateProperties();
////            updateDisplayLayout();
////        });
    }
    /**
     *
     *
     * @param inSuggestedX
     * @param inSuggestedY
     */
    void setPosition(int inSuggestedX,
                     int inSuggestedY)
    {
        setPositionX(inSuggestedX);
        setPositionY(inSuggestedY);
    }
    /**
     *
     *
     * @param inNewWindowLeft
     */
    void setPositionX(int inNewWindowLeft)
    {
        position.setLeft(inNewWindowLeft + Unit.PIXELS.name().toLowerCase());
        getElement().executeJs(SET_PROPERTY_IN_OVERLAY_JS,
                               "left",
                               String.valueOf(position.getLeft()));
    }
    /**
     *
     *
     * @param inNewWindowTop
     */
    void setPositionY(int inNewWindowTop)
    {
        position.setTop(inNewWindowTop + Unit.PIXELS.name().toLowerCase());
        getElement().executeJs(SET_PROPERTY_IN_OVERLAY_JS,
                               "top",
                               String.valueOf(position.getLeft()));
    }
    /**
     * Get the window top edge coordinate in pixels.
     *
     * @return a <code>float</code> value
     */
    float getWindowTop()
    {
        return getTop().getValue();
    }
    /**
     * Get the window left edge coordinate in pixels.
     *
     * @return a <code>float</code> value
     */
    float getWindowLeft()
    {
        return getLeft().getValue();
    }
    /**
     * Get the window bottom edge coordinate in pixels.
     *
     * @return a <code>float</code> value
     */
    float getWindowBottom()
    {
        return getWindowTop() + getWindowHeight();
    }
    /**
     * Get the window right edge coordinate in pixels.
     *
     * @return a <code>float</code> value
     */
    float getWindowRight()
    {
        return getWindowLeft() + getWindowWidth();
    }
    /**
     * Get the window height in pixels.
     *
     * @return a <code>float</code> value
     */
    float getWindowHeight()
    {
        Dimension height = getHeightDimension();
        return getWindowDimension(height.getValue(),
                                  windowManager.getBrowserWindowHeight(),
                                  height.getUnit());
    }
    /**
     * Get the window width in pixels.
     *
     * @return a <code>float</code> value
     */
    float getWindowWidth()
    {
        Dimension width = getWidthDimension();
        return getWindowDimension(width.getValue(),
                                  windowManager.getBrowserWindowWidth(),
                                  width.getUnit());
    }
    private void updatePositionTopLeft()
    {
        getElement().executeJs("return [" + "this.$.overlay.$.overlay.style['top'], this.$.overlay.$.overlay.style['left']" + "]").then(String.class, s -> {
            System.out.println("COCO: position raw value: " + s);
            String[] split = StringUtils.split(s, ',');
            if(split.length == 2 && split[0] != null && split[1] != null) {
                position.setTop(split[0]);
                position.setLeft(split[1]);
            }
        });
        SLF4JLoggerProxy.trace(this,
                               "{} position updated: {}",
                               this,
                               position);
    }
    /**
     * Get the window dimension implied by the given attributes.
     *
     * @param inValue a <code>float</code> value
     * @param inBrowserDimension an <code>int</code> value
     * @param inUnit a <code>Unit</code> value
     * @return a <code>float</code> value
     */
    private static float getWindowDimension(float inValue,
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
     *
     *
     * @return
     */
    public Dimension getWidthDimension()
    {
        return position.getWidth();
    }
    public Dimension getHeightDimension()
    {
        return position.getHeight();
    }
    /**
     *
     *
     * @return
     */
    public Dimension getTop()
    {
        return position.getTop();
    }
    /**
     *
     *
     * @return
     */
    public Dimension getLeft()
    {
        return position.getLeft();
    }
    private void enablePositioning(boolean positioningEnabled)
    {
        getElement().executeJs(SET_PROPERTY_IN_OVERLAY_JS, "align-self", positioningEnabled ? "flex-start" : "unset");
        getElement().executeJs(SET_PROPERTY_IN_OVERLAY_JS, "position", positioningEnabled ? "absolute" : "relative");
    }
    private static Dimension getDimension(String inValue,
                                          int inBrowserWindowDimension)
    {
        inValue = StringUtils.trimToNull(inValue);
        if(inValue == null) {
            return new Dimension(0.0f,Unit.PIXELS);
        }
        String[] components = inValue.split("((?<=[^0-9\\-\\.])(?=[0-9\\-\\.]))|((?<=[0-9\\-\\.])(?=[^0-9\\-\\.]))");
        float value = Float.parseFloat(components[0]);
        Unit unit = Unit.getUnitFromSymbol(components[1]==null?null:components[1].toUpperCase());
        if(unit != Unit.PIXELS) {
            value = getWindowDimension(value,
                                       inBrowserWindowDimension,
                                       unit);
            unit = Unit.PIXELS;
        }
        Dimension newDimension = new Dimension(value,
                                               unit);
        return newDimension;
    }
    public static class Dimension
            extends Pair<Float,Unit>
    {
        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            StringBuilder builder = new StringBuilder();
            builder.append(getValue()).append(getUnit());
            return builder.toString();
        }
        /**
         * Create a new Dimension instance.
         *
         * @param inFirst
         * @param inSecond
         */
        private Dimension(Float inFirst,
                          Unit inSecond)
        {
            super(inFirst,
                  inSecond);
        }
        public float getValue()
        {
            return super.getFirstMember();
        }
        public Unit getUnit()
        {
            return super.getSecondMember();
        }
    }
    private class Position
    {
        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            StringBuilder builder = new StringBuilder();
            builder.append("Position [top=").append(top).append(", left=").append(left).append(", width=").append(width)
                    .append(", height=").append(height).append("]");
            return builder.toString();
        }
        /**
         * Create a new Position instance.
         *
         * @param inTop
         * @param inLeft
         */
        private Position(String inTop,
                         String inLeft)
        {
            setTop(inTop);
            setLeft(inLeft);
        }
        /**
         *
         *
         * @return
         */
        public Dimension getWidth()
        {
            return width;
        }
        /**
         *
         *
         * @return
         */
        public Dimension getHeight()
        {
            return height;
        }
        /**
         *
         *
         * @param inWidth
         */
        private void setWidth(String inWidth)
        {
            width = getDimension(inWidth,
                                 windowManager.getBrowserWindowWidth());
        }
        /**
         *
         *
         * @param inHeight
         */
        private void setHeight(String inHeight)
        {
            height = getDimension(inHeight,
                                  windowManager.getBrowserWindowHeight());
        }
        private Dimension getTop()
        {
            return top;
        }
        private void setTop(String inTop)
        {
            top = getDimension(inTop,
                               windowManager.getBrowserWindowHeight());
        }
        private Dimension getLeft()
        {
            return left;
        }
        private void setLeft(String inLeft)
        {
            left = getDimension(inLeft,
                                windowManager.getBrowserWindowWidth());
        }
        private Dimension top;
        private Dimension left;
        private Dimension width;
        private Dimension height;
    }
    private String uuid;
    private FlexLayout bodyLayout;
    private HorizontalLayout headerLayout;
    private Label headerLabel;
    private VerticalLayout mainLayout;
    private Scroller bodyScroller;
    private final Position position;
    private final Properties properties;
    private final WindowManagerService windowManager;
    private final WindowRegistry windowRegistry;
    private static final String SET_PROPERTY_IN_OVERLAY_JS = "this.$.overlay.$.overlay.style[$0]=$1";
    /**
     * base key for {@see UserAttributeType} display layout properties
     */
    private static final String propId = ManagedDialog.class.getSimpleName();
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
    private static final long serialVersionUID = 8923324415848396409L;
}
