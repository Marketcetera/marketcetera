package org.marketcetera.client.utils;

import java.util.Date;

import org.marketcetera.trade.ReportBase;
import org.marketcetera.trade.TradeMessage;
import org.marketcetera.trade.TradeMessageListener;
import org.marketcetera.trade.utils.OrderHistoryManager;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Provides a self-populating {@link OrderHistoryManager} implementation.
 * 
 * <p>Instantiate this class with an origin date. The origin date establishes how far back
 * to look for order history.
 * 
 * <p>This class will receive new reports on its own - it is not necessary nor is it permitted to manually
 * {@link #add(ReportBase) add reports}.
 * 
 * <p>Note that there are significant performance and resource implications when using this class.
 * Depending on historical order volume, this class may be required to process thousands or millions
 * of reports. There are two ramifications of this:
 * <ul>
 *   <li>Make an effort to limit the number of instances of this class. Each instance in the same process
 *       has access to the same reports. The only reason to have more than one instance of this class is
 *       if more than one historical range is required. Even then, it is preferable to use a single instance
 *       with the oldest origin date.</li>
 *   <li>Use the most recent origin date feasible. Ideally, make this midnight of the current day, or whatever
 *       makes sense for the current trading session. Obviously, business requirements will dictate what
 *       the origin date is.</li>
 * </ul>
 * 
 * <p>It may take a significant amount of time to {@link #start() start} this object as it must process historical
 * order history. Callers may choose to make this operation asynchronous. The object will report that it 
 * {@link #isRunning() is running} when the processing is complete.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.1.4
 */
@ClassVersion("$Id$")
public class AutoOrderHistoryManager
        extends LiveOrderHistoryManager
        implements TradeMessageListener
{
    /**
     * Create a new AutoOrderHistoryManager instance.
     *
     * @param inReportHistoryOrigin a <code>Date</code> value indicating the point from which to gather order history or <code>null</code>
     */
    public AutoOrderHistoryManager(Date inReportHistoryOrigin)
    {
        super(inReportHistoryOrigin);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.utils.OrderHistoryManager#add(org.marketcetera.trade.ReportBase)
     */
    @Override
    public void add(TradeMessage inTradeMessage)
    {
        throw new UnsupportedOperationException(org.marketcetera.client.Messages.DONT_ADD_REPORTS.getText());
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.TradeMessageListener#receiveTradeMessage(org.marketcetera.trade.TradeMessage)
     */
    @Override
    public void receiveTradeMessage(TradeMessage inTradeMessage)
    {
        SLF4JLoggerProxy.debug(this,
                               "Received {}", //$NON-NLS-1$
                               inTradeMessage);
        if(inTradeMessage != null) {
            super.add(inTradeMessage);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.utils.LiveOrderHistoryManager#start()
     */
    @Override
    public synchronized void start()
    {
        getClient().addTradeMessageListener(this);
        super.start();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.utils.LiveOrderHistoryManager#stop()
     */
    @Override
    public synchronized void stop()
    {
        getClient().removeTradeMessageListener(this);
        super.stop();
    }
}
