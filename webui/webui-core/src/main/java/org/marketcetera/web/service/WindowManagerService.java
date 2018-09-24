package org.marketcetera.web.service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.web.events.NewWindowEvent;
import org.marketcetera.web.events.WindowResizeEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.eventbus.Subscribe;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
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
     * Receive menu events.
     *
     * @param inNewWindowEvent a <code>NewWindowEvent</code> value
     */
    @Subscribe
    public void receiveMenuEvent(NewWindowEvent inNewWindowEvent)
    {
        SLF4JLoggerProxy.debug(this,
                               "Received {}",
                               inNewWindowEvent.getWindowTitle());
        Window newWindow = new Window(inNewWindowEvent.getWindowTitle());
        newWindow.setModal(false);
        newWindow.setDraggable(true);
        newWindow.setResizable(true);
        newWindow.setWidth(inNewWindowEvent.getWindowSize().getFirstMember());
        newWindow.setHeight(inNewWindowEvent.getWindowSize().getSecondMember());
        newWindow.addResizeListener(inE -> {
            webMessageService.post(new WindowResizeEvent() {
                @Override
                public int getPositionX()
                {
                    return newWindow.getPositionX();
                }
                @Override
                public int getPositionY()
                {
                    return newWindow.getPositionY();
                }
                @Override
                public float getWidth()
                {
                    return newWindow.getWidth();
                }
                @Override
                public float getHeight()
                {
                    return newWindow.getHeight();
                }
                @Override
                public String toString()
                {
                    return new StringBuilder().append(newWindow.getCaption()).append(" at ")
                            .append(newWindow.getPositionX()).append(',').append(newWindow.getPositionY())
                            .append(" of ").append(newWindow.getHeight()).append("x").append(newWindow.getWidth()).toString();
                }
            });
        });
        newWindow.setContent(inNewWindowEvent.getComponent());
        UI.getCurrent().addWindow(newWindow);
        newWindow.focus();
    }
    /**
     * web message service value
     */
    @Autowired
    private WebMessageService webMessageService;
}
