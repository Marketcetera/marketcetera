package org.marketcetera.photon.views;

import java.util.List;

import org.eclipse.jface.action.IToolBarManager;
import org.marketcetera.photon.editors.ClOrdIDComparator;
import org.marketcetera.photon.model.FIXMessageHistory;
import org.marketcetera.photon.model.LatestExecutionReportsFunction;
import org.marketcetera.photon.model.MessageHolder;
import org.marketcetera.photon.model.NotNullMatcher;
import org.marketcetera.photon.model.OpenOrderMatcher;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.FunctionList;
import ca.odell.glazedlists.GroupingList;

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
