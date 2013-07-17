package org.marketcetera.client;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.OrderCancelReject;

/* $License$ */
/**
 * A receiver of trading reports. Classes that need to be able to
 * receive execution reports can implement this interface and register
 * themselves to receive execution reports via
 * {@link Client#addReportListener(ReportListener)}.
 * <p>
 * It's not expected that report listeners will take too much time to
 * return. Currently all report listeners are invoked sequentially.
 * If a report listener takes too much time to process the report, it will
 * delay the delivery of report to other registered listeners. 
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public interface ReportListener {
    /**
     * Invoked to supply an execution report instance to the report listener.
     *
     * @param inReport The received execution report.
     */
    public void receiveExecutionReport(ExecutionReport inReport);

    /**
     * Invoked to supply an order cancel reject report instance to the report
     * listener.
     *
     * @param inReport The received order cancel rejection report.
     */
    public void receiveCancelReject(OrderCancelReject inReport);
}
