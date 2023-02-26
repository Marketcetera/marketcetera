package org.marketcetera.ui;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.marketcetera.core.time.TimeFactoryImpl;
import org.springframework.context.Lifecycle;

import javafx.application.Platform;
import javafx.scene.control.Label;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class ClockUpdater
        implements Lifecycle
{
    public ClockUpdater(Label inTarget)
    {
        target = inTarget;
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#start()
     */
    @Override
    public void start()
    {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run()
            {
                Platform.runLater(() -> { target.setText(TimeFactoryImpl.WALLCLOCK_SECONDS_LOCAL.print(System.currentTimeMillis()));});
            }},new Date(),1000);
        running = true;
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#stop()
     */
    @Override
    public void stop()
    {
        try {
            timer.cancel();
        } finally {
            running = false;
        }
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#isRunning()
     */
    @Override
    public boolean isRunning()
    {
        return running;
    }
    private boolean running;
    private Timer timer;
    private final Label target;
}
