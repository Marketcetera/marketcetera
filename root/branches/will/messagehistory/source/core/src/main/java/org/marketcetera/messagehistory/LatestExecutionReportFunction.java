package org.marketcetera.messagehistory;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.trade.ExecutionReport;

import quickfix.Message;
import quickfix.field.OrderID;

/* $License$ */

/**
 * LatestExecutionReportsFunction is a subclass of
 * LatestMessageFunction that filters for only incoming
 * execution report messages.
 *
 * @author gmiller
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class LatestExecutionReportFunction extends LatestReportFunction {

	/**
	 * Tests to see if the incoming
     * {@link org.marketcetera.messagehistory.MessageHolder}
	 * represents an execution report.
     *
	 * @param holder the message to test
     *
	 * @return true if the incoming message represents an execution report,
     * false otherwise.
     * 
	 * @see LatestMessageFunction#filter(MessageHolder)
	 */
	protected boolean filter(ReportHolder holder){
		return (holder instanceof ReportHolder
				&& holder.getReport() instanceof ExecutionReport);
	}

	/* (non-Javadoc)
	 * @see org.marketcetera.photon.editors.LatestMessageFunction#isLater(org.marketcetera.photon.model.ReportHolder, org.marketcetera.photon.model.ReportHolder)
	 */
	@Override
	protected boolean isLater(ReportHolder inHolder1, ReportHolder inHolder2) {
        Message message1 = inHolder1.getMessage();
		Message message2 = inHolder2.getMessage();
		boolean hasOrderID1 = message1.isSetField(OrderID.FIELD);
		boolean hasOrderID2 = message2.isSetField(OrderID.FIELD);
        //todo depends on OrderID field. Fix it to not depend on OrderID
        if ((hasOrderID1 && hasOrderID2) || (!hasOrderID1 && !hasOrderID2)){
			return super.isLater(inHolder1, inHolder2);
		} else {
			return hasOrderID1;
		}
	}
}