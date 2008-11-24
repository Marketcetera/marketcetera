package org.marketcetera.messagehistory;

import java.util.Date;
import java.util.List;


import ca.odell.glazedlists.FunctionList.Function;
import org.marketcetera.util.misc.ClassVersion;
/* $License$ */

/**
 * LatestReportFunction is a {@link ca.odell.glazedlists.FunctionList.Function} that
 * will process a list of FIX messages and find only the
 * most recent message.  Note that this function does not
 * do any filtering to ensure that only messages of the
 * same type are compared.  You must ensure that the
 * messages passed to {@link #evaluate(java.util.List)} are
 * all comparable based on the {@link quickfix.field.TransactTime} field.
 *
 * Note that subclasses may restrict the messages that are
 * considered by this function by implementing the
 * 
 * @author gmiller
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class LatestReportFunction implements
  Function<List<ReportHolder>, ReportHolder>{

	/** Given a list of messages, compare them all
	 * based on the value of the {@link quickfix.field.TransactTime} field
	 * returning only the most recent message.
	 *
	 * @param messages the list of messages to process
	 * @return the message object representing the most recent message from the input
	 * @see ca.odell.glazedlists.FunctionList.Function#evaluate(Object)
	 */
	public ReportHolder evaluate(List<ReportHolder> messages) {
		ReportHolder latestReportHolder = null;
		for (ReportHolder loopReportHolder : messages) {
			if (filter(loopReportHolder)) {
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
	 * Determines whether msg1 is a "later" message than msg2
	 * @param inHolder1 the first message to consider
	 * @param inHolder2 the second message to consider
	 * @return true if ReportHolder1 occurred after ReportHolder2, false otherwise
	 */
	protected boolean isLater(ReportHolder inHolder1, ReportHolder inHolder2) {
        long ref1 = inHolder1.getMessageReference();
        long ref2 = inHolder2.getMessageReference();
        if(ref1 != ref2) {
            return (ref1 > ref2);
        }

        Date date1;
        Date date2;
        date1 = inHolder1.getReport().getSendingTime();
        date2 = inHolder2.getReport().getSendingTime();
        return (date1.compareTo(date2) > 0);
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
	protected boolean filter(ReportHolder inHolder)
	{
		return true;
	}
}