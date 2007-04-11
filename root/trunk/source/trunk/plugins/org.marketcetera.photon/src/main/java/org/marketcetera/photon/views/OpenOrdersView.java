package org.marketcetera.photon.views;


import org.eclipse.jface.action.IToolBarManager;
import org.marketcetera.photon.core.FIXMessageHistory;
import org.marketcetera.photon.core.MessageHolder;

import ca.odell.glazedlists.EventList;

public class OpenOrdersView extends HistoryMessagesView {

	public static final String ID = "org.marketcetera.photon.views.OpenOrdersView";
	/**
	 * The columns of the "Open Orders" page, specified as 
	 * FIX fields.
	 * 
	 * @author gmiller
	 *
	 */
	public enum OpenOrderColumns {
		SENDINGTIME("SendingTime"), CLORDID("ClOrdID"), ORDSTATUS("OrdStatus"), SIDE(
				"Side"), SYMBOL("Symbol"), ORDERQTY("OrderQty"), CUMQTY(
				"CumQty"), LEAVESQTY("LeavesQty"), Price("Price"), AVGPX(
				"AvgPx"), ACCOUNT("Account"), LASTSHARES("LastShares"), LASTPX(
				"LastPx"), LASTMKT("LastMkt"), EXECID("ExecID"),
				ORDERID("OrderID");

		private String mName;

		OpenOrderColumns(String name) {
			mName = name;
		}

		public String toString() {
			return mName;
		}
	};

	@Override
	protected Enum[] getEnumValues() {
		return OpenOrderColumns.values();
	}

	@Override
	protected void initializeToolBar(IToolBarManager theToolBarManager) {
		
	}

	public EventList<MessageHolder> extractList(FIXMessageHistory hist){
		return hist.getOpenOrdersList();
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}

}
