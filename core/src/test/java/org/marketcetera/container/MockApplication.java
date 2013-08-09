package org.marketcetera.container;

import org.springframework.context.SmartLifecycle;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class MockApplication
        implements SmartLifecycle
{
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#isRunning()
     */
    @Override
    public boolean isRunning()
    {
        return running;
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#start()
     */
    @Override
    public void start()
    {
        running = true;
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#stop()
     */
    @Override
    public void stop()
    {
        running = false;
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Phased#getPhase()
     */
    @Override
    public int getPhase()
    {
        return 0;
    }
    /* (non-Javadoc)
     * @see org.springframework.context.SmartLifecycle#isAutoStartup()
     */
    @Override
    public boolean isAutoStartup()
    {
        return true;
    }
    /* (non-Javadoc)
     * @see org.springframework.context.SmartLifecycle#stop(java.lang.Runnable)
     */
    @Override
    public void stop(Runnable inArg0)
    {
        stop();
    }
    /**
     * indicates if the application is running
     */
    private volatile boolean running = false;
}
