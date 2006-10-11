package org.marketcetera.photon.views;

import org.eclipse.jface.action.IToolBarManager;
import org.marketcetera.photon.actions.ShowHeartbeatsAction;
import org.marketcetera.photon.model.FIXMessageHistory;
import org.marketcetera.photon.model.MessageHolder;

import quickfix.field.MsgType;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.matchers.Matcher;

public class FIXMessagesView extends MessagesView {

    public static final String ID = "org.marketcetera.photon.views.FIXMessagesView";
	private static final Matcher<? super MessageHolder> HEARTBEAT_MATCHER = new FIXMatcher<String>(MsgType.FIELD, MsgType.HEARTBEAT);

	
	/**
	 * The columns of the Messages page, represented
	 * as FIX fields.
	 * 
	 * @author gmiller
	 *
	 */
	public enum MessageColumns {
		DIRECTION("D"), TRANSACTTIME("TransactTime"), MSGTYPE("MsgType"), CLORDID("ClOrdID"),
		ORDERID("OrderID"), ORICCLORDID("OrigClOrdID"), ORDSTATUS("OrdStatus"), SIDE(
				"Side"), SYMBOL("Symbol"), ORDERQTY("OrderQty"), CUMQTY(
				"CumQty"), LEAVESQTY("LeavesQty"), Price("Price"), AVGPX(
				"AvgPx"), ACCOUNT("Account"), LASTSHARES("LastShares"), LASTPX(
				"LastPx"), LASTMKT("LastMkt"), EXECID("ExecID");

		private String mName;

		MessageColumns(String name) {
			mName = name;
		}

		public String toString() {
			return mName;
		}
	}

	protected Enum[] getEnumValues() {
		return MessageColumns.values();
	}

    protected void initializeToolBar(IToolBarManager theToolBarManager) {
    	theToolBarManager.add(new ShowHeartbeatsAction(this));
    }

	@Override
	public void setFocus() {
	}

	@SuppressWarnings("unchecked")
	protected FilterList<MessageHolder> getFilterList() {
		return (FilterList<MessageHolder>) getMessagesViewer().getInput();
	}


	public EventList<MessageHolder> extractList(FIXMessageHistory input) {
		return input.getAllMessagesList();
	}

	public void setShowHeartbeats(boolean shouldShow){
		FilterList<MessageHolder> list = getFilterList();
		if (shouldShow){
			list.setMatcher(HEARTBEAT_MATCHER);
		} else {
			list.setMatcher(null);
		}
	}
}
