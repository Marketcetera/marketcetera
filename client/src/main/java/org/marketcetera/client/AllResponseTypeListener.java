package org.marketcetera.client;

import org.marketcetera.trade.FIXResponse;

/**
 * A receiver of trading reports and any other reports. Classes that need to be able to
 * receive execution reports can implement this interface and register
 * themselves to receive execution reports via
 * {@link Client#addReportListener(ReportListener)}.
 * <p>
 * It's not expected that report listeners will take too much time to
 * return. Currently all report listeners are invoked sequentially.
 * If a report listener takes too much time to process the report, it will
 * delay the delivery of report to other registered listeners. 
 *
 * @author Milos Djuric
 *
 */
public interface AllResponseTypeListener extends ReportListener {
    /**
     * Invoked to supply message of any type (except execution report and cancel reject) to the report listener.
     *
     * @param inReport The received execution report.
     */
    public void receiveOtherMessage(FIXResponse inMessage);
}
