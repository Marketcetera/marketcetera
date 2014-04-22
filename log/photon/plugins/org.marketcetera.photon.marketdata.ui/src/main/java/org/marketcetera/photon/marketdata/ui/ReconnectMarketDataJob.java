package org.marketcetera.photon.marketdata.ui;

import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.marketcetera.photon.marketdata.IMarketDataManager;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Reconnects the market data connection.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class ReconnectMarketDataJob
        extends Job
{
    /**
     * Create a new ReconnectMarketDataJob instance.
     */
    public ReconnectMarketDataJob()
    {
        super("Connecting to Market Data Nexus");
    }
    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.jobs.Job#shouldSchedule()
     */
    @Override
    public boolean shouldSchedule()
    {
        return sScheduled.compareAndSet(false,
                                        true);
    }
    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    protected IStatus run(IProgressMonitor inArg0)
    {
        try {
            marketDataManager.reconnectFeed();
            return Status.OK_STATUS;
        } catch (Exception e) {
            return Status.CANCEL_STATUS;
        } finally {
            sScheduled.set(false);
        }
    }
    /**
     * Sets the market data manager value.
     *
     * @param inMarketDataManager an <code>IMarketDataManager</code> value
     */
    public void setMarketDataManager(IMarketDataManager inMarketDataManager)
    {
        marketDataManager = inMarketDataManager;
    }
    /**
     * market data manager value
     */
    private IMarketDataManager marketDataManager;
    /**
     * indicates if a current reconnection job is scheduled or not
     */
    private static final AtomicBoolean sScheduled = new AtomicBoolean();
}
