package org.marketcetera.ui.service;

import java.io.File;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import org.marketcetera.core.PlatformServices;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Component;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/* $License$ */

/**
 * Provides sound services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Component
@EnableAutoConfiguration
public class PhotonSoundService
{
    /**
     * Initialize and start the service.
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
     * Stop the service.
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
     * Plays the given sound.
     *
     * @param inSoundFilename a <code>String</code> value
     */
    public void playSound(String inSoundFilename)
    {
        Media sound = new Media(new File(inSoundFilename).toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.play();
    }
    /**
     * provides access to client services
     */
    @Autowired
    protected ServiceManager serviceManager;
    /**
     * web message service value
     */
    @Autowired
    private UiMessageService webMessageService;
}
