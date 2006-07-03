package org.marketcetera.photon.editors;

import org.marketcetera.photon.model.IncomingMessageHolder;
import org.marketcetera.photon.model.MessageHolder;
import org.marketcetera.quickfix.FIXMessageUtil;

public class LatestExecutionReportsFunction extends LatestMessageFunction {

	protected boolean filter(MessageHolder holder){
		return (holder instanceof IncomingMessageHolder 
				&& FIXMessageUtil.isExecutionReport(holder.getMessage()));
	}
}
