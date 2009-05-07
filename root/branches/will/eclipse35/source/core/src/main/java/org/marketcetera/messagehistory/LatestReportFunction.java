package org.marketcetera.messagehistory;

import java.util.List;

import org.marketcetera.util.misc.ClassVersion;

import ca.odell.glazedlists.FunctionList.Function;

/**
 * LatestReportFunction is a {@link ca.odell.glazedlists.FunctionList.Function}
 * that will process a list of FIX messages and find only the most recent
 * message. Note that this function does not do any filtering to ensure that
 * only messages of the same type are compared. You must ensure that the
 * messages passed to {@link #evaluate(java.util.List)} are all comparable based
 * on {@link ReportHolder#getMessageReference()}.
 * 
 * Note that subclasses may restrict the messages that are considered by this
 * function by implementing the {@link #accept(ReportHolder)} method.
 * 
 * @author gmiller
 * @author anshul@marketcetera.com
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class LatestReportFunction implements
  Function<List<ReportHolder>, ReportHolder>{

    /** Given a list of messages, compare them all
     * based on {@link #isLater(ReportHolder, ReportHolder)}.
     *
     * @param messages the list of messages to process
     * @return the message object representing the most recent message from the input
     * @see ca.odell.glazedlists.FunctionList.Function#evaluate(Object)
     */
    public ReportHolder evaluate(List<ReportHolder> messages) {
        ReportHolder latestReportHolder = null;
        for (ReportHolder loopReportHolder : messages) {
            if (accept(loopReportHolder)) {
                if (latestReportHolder == null){
                    latestReportHolder = loopReportHolder;
                } else {
                    if (isLater(loopReportHolder, latestReportHolder)){
                        latestReportHolder = loopReportHolder;
                    }
                }
            }
        }
        return latestReportHolder;
    }

    /**
     * Determines whether msg1 is a "later" message than msg2 via {@link ReportHolder#getMessageReference()}.
     * @param inHolder1 the first message to consider
     * @param inHolder2 the second message to consider
     * @return true if ReportHolder1 occurred after ReportHolder2, false otherwise
     */
    protected boolean isLater(ReportHolder inHolder1, ReportHolder inHolder2) {
        return inHolder1.compareTo(inHolder2) > 0;
    }
    /**
     * Determines whether a given {@link org.marketcetera.messagehistory.ReportHolder} should
     * be considered for inclusion in the "latest message" calculation.
     * This method is called once per message in the input to
     * {@link #evaluate(java.util.List)}.
     *
     * @param inHolder the message to consider
     * @return true if the message should be considered for inclusion in the "latest message" calculation, false otherwise
     */
    protected boolean accept(ReportHolder inHolder)
    {
        return true;
    }
}