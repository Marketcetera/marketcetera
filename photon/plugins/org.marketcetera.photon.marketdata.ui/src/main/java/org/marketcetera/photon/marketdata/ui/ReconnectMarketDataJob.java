package org.marketcetera.photon.marketdata.ui;

import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.progress.UIJob;
import org.marketcetera.photon.marketdata.IMarketDataManager;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class ReconnectMarketDataJob
        extends UIJob
{
    /**
     * Create a new ReconnectMarketDataJob instance.
     */
    public ReconnectMarketDataJob()
    {
        super("Connecting to Market Data Nexus");
        setUser(true);
        setSystem(true);
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
     * @see org.eclipse.ui.progress.UIJob#runInUIThread(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public IStatus runInUIThread(IProgressMonitor inMonitor)
    {
        try {
            marketDataManager.reconnectFeed();
            return Status.OK_STATUS;
        } finally {
            sScheduled.set(false);
        }
    }
    /**
     *
     *
     * @param inMarketDataManager
     */
    public void setMarketDataManager(IMarketDataManager inMarketDataManager)
    {
        marketDataManager = inMarketDataManager;
    }
    /**
     * 
     */
    private IMarketDataManager marketDataManager;
    /**
     * 
     */
    private static final AtomicBoolean sScheduled = new AtomicBoolean();
}
