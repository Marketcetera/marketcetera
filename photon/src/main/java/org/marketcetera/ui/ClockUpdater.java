package org.marketcetera.ui;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.application.Platform;
import javafx.scene.control.Label;

/* $License$ */

/**
 * Updates the clock display.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Component
@EnableAutoConfiguration
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ClockUpdater
{
    /**
     * Create a new ClockUpdater instance.
     *
     * @param inTarget
     */
    public ClockUpdater(Label inTarget)
    {
        target = inTarget;
    }
    /**
     * Validate and start the object.
     */
    @PostConstruct
    public void start()
    {
        final DateTimeFormatter formatter = DateTimeFormat.forPattern(clockFormat);
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run()
            {
                Platform.runLater(() -> { target.setText(formatter.print(System.currentTimeMillis()));});
            }},new Date(),1000);
    }
    /**
     * Stop the object.
     */
    @PreDestroy
    public void stop()
    {
        timer.cancel();
    }
    /**
     * configurable clock format, see: https://www.joda.org/joda-time/apidocs/org/joda/time/format/DateTimeFormat.html
     */
    @Value("${metc.workspace.footer.clock.format:dd-MMM-YYYY kk:mm:ss}")
    private String clockFormat;
    /**
     * timer used to update the clock display
     */
    private Timer timer;
    /**
     * target node to write the time to
     */
    private final Label target;
}
