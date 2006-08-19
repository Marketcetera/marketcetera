package org.marketcetera.photon.editors;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.photon.model.IncomingMessageHolder;
import org.marketcetera.photon.model.MessageHolder;
import org.marketcetera.quickfix.FIXMessageUtil;

/**
 * LatestExecutionReportsFunction is a subclass of 
 * LatestMessageFunction that filters for only incoming
 * execution report messages.
 * 
 * @author gmiller
 *
 */
@ClassVersion("$Id$")
public class LatestExecutionReportsFunction extends LatestMessageFunction {

	/** 
	 * Tests to see if the incoming {@link MessageHolder}
	 * represents an execution report.
	 * @param holder the message to test
	 * @return true if the incoming message represents an execution report, false otherwise
	 * @see org.marketcetera.photon.editors.LatestMessageFunction#filter(org.marketcetera.photon.model.MessageHolder)
	 */
	protected boolean filter(MessageHolder holder){
		return (holder instanceof IncomingMessageHolder 
				&& FIXMessageUtil.isExecutionReport(holder.getMessage()));
	}
}
