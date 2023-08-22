package org.marketcetera.ui;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.marketcetera.core.ClientStatusListener;
import org.marketcetera.ui.service.ServiceManager;
import org.marketcetera.ui.service.admin.AdminClientService;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.application.Platform;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/* $License$ */

/**
 * Monitors the status of the client connection to the server and updates the toolbar status icon.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Component
@EnableAutoConfiguration
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ClientStatusUpdater
        implements ClientStatusListener
{
    /**
     * Create a new ClientStatusUpdater instance.
     *
     * @param inTarget an <code>ImageView</code> value
     */
    public ClientStatusUpdater(ImageView inTarget)
    {
        target = inTarget;
    }
    /**
     * Validate and start the object.
     */
    @PostConstruct
    public void start()
    {
        AdminClientService adminClientService = serviceManager.getService(AdminClientService.class);
        availableImage = new Image(String.valueOf(getClass().getClassLoader().getResource("images/LedGreen.gif")));
        unavailableImage = new Image(String.valueOf(getClass().getClassLoader().getResource("images/LedRed.gif")));
        unknownImage = new Image(String.valueOf(getClass().getClassLoader().getResource("images/LedNone.gif")));
        if(adminClientService.isRunning()) {
            setAvailable();
        } else {
            setUnavailable();
        }
        adminClientService.addClientStatusListener(this);
    }
    /**
     * Stop the object.
     */
    @PreDestroy
    public void stop()
    {
        AdminClientService adminClientService = serviceManager.getService(AdminClientService.class);
        adminClientService.removeClientStatusListener(this);
        setUnknown();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.ClientStatusListener#receiveClientStatus(boolean)
     */
    @Override
    public void receiveClientStatus(boolean inIsAvailable)
    {
        SLF4JLoggerProxy.debug(this,
                               "{} received client status: {}",
                               getClass().getSimpleName(),
                               inIsAvailable);
        if(inIsAvailable) {
            setAvailable();
        } else {
            setUnavailable();
        }
    }
    /**
     * Indicate that the server is available.
     */
    private void setAvailable()
    {
        updateImage(availableImage,
                    "Server is available");
    }
    /**
     * Indicate that the server is unavailable.
     */
    private void setUnavailable()
    {
        updateImage(unavailableImage,
                    "Server is unavailable");
    }
    /**
     * Indicate that the server status is unknown.
     */
    private void setUnknown()
    {
        updateImage(unknownImage,
                    "Server connection status is unknown");
    }
    /**
     * Update the workspace toolbar with the given image and tooltip message.
     *
     * @param inImage an <code>Image</code> value
     * @param inTooltipMessage a <code>String</code> value
     */
    private void updateImage(Image inImage,
                             String inTooltipMessage)
    {
        Platform.runLater(new Runnable() {
            @Override
            public void run()
            {
                target.setImage(inImage);
                Tooltip.install(target,
                                new Tooltip(inTooltipMessage));
            }}
        );
    }
    /**
     * image to use for when the server is available
     */
    private Image availableImage;
    /**
     * image to use for when the server is unavailable
     */
    private Image unavailableImage;
    /**
     * image to use for when the server status is unknown
     */
    private Image unknownImage;
    /**
     * provides access to system services
     */
    @Autowired
    private ServiceManager serviceManager;
    /**
     * node to update when client status changes
     */
    private final ImageView target;
}
