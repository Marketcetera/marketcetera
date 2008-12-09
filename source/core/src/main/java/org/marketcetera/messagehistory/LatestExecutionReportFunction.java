package org.marketcetera.messagehistory;

import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * LatestExecutionReportFunction is a subclass of LatestReportFunction that
 * filters for the latest execution report message.
 * 
 * @author gmiller
 * @author anshul@marketcetera.com
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class LatestExecutionReportFunction extends LatestReportFunction {

    /**
     * Tests to see if the incoming {@link ReportHolder} represents an execution
     * report.
     * 
     * @param holder
     *            the holder of the message to test
     * 
     * @return true if the incoming message represents an execution report,
     *         false otherwise.
     * 
     * @see LatestReportFunction#accept(ReportHolder)
     */
    protected boolean accept(ReportHolder holder){
        return holder.getReport() instanceof ExecutionReport;
    }

    @Override
    protected boolean isLater(ReportHolder inHolder1, ReportHolder inHolder2) {
        boolean hasOrderID1 = inHolder1.getReport().getBrokerOrderID() != null;
        boolean hasOrderID2 = inHolder2.getReport().getBrokerOrderID() != null;
        if ((hasOrderID1 && hasOrderID2) || (!hasOrderID1 && !hasOrderID2)){
            return super.isLater(inHolder1, inHolder2);
        } else {
            return hasOrderID1;
        }
    }
}