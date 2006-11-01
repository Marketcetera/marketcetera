package org.marketcetera.photon.views;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Table;
import org.marketcetera.photon.actions.ShowHeartbeatsAction;
import org.marketcetera.photon.model.FIXMessageHistory;
import org.marketcetera.photon.model.MessageHolder;
import org.marketcetera.photon.ui.DirectionalMessageTableFormat;
import org.marketcetera.photon.ui.EventListContentProvider;

import quickfix.field.MsgType;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.matchers.Matcher;

public class FIXMessagesView extends HistoryMessagesView {


	public static final String ID = "org.marketcetera.photon.views.FIXMessagesView";
	private static final Matcher<? super MessageHolder> HEARTBEAT_MATCHER = new FIXMatcher<String>(MsgType.FIELD, MsgType.HEARTBEAT, false);

	
	/**
	 * The columns of the Messages page, represented
	 * as FIX fields.
	 * 
	 * @author gmiller
	 *
	 */
	public enum MessageColumns {
		DIRECTION("D"), TRANSACTTIME("TransactTime"), MSGTYPE("MsgType"), CLORDID(
				"ClOrdID"), ORICCLORDID("OrigClOrdID"), ORDSTATUS("OrdStatus"), SIDE(
				"Side"), SYMBOL("Symbol"), ORDERQTY("OrderQty"), CUMQTY(
				"CumQty"), LEAVESQTY("LeavesQty"), Price("Price"), AVGPX(
				"AvgPx"), ACCOUNT("Account"), LASTSHARES("LastShares"), LASTPX(
				"LastPx"), LASTMKT("LastMkt"), EXECID("ExecID"), ORDERID(
				"OrderID");

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
//    	theToolBarManager.add(new TextContributionItem());
    	theToolBarManager.add(new ShowHeartbeatsAction(this));
    }

	@Override
	public void setFocus() {
	}

	@SuppressWarnings("unchecked")
	protected FilterList<MessageHolder> getFilterList() {
		return (FilterList<MessageHolder>) getInput();
	}


	public EventList<MessageHolder> extractList(FIXMessageHistory input) {
		FilterList<MessageHolder> filterList = new FilterList<MessageHolder>(input.getAllMessagesList());
		return filterList;
	}

	public void setShowHeartbeats(boolean shouldShow){
		FilterList<MessageHolder> list = getFilterList();
		if (shouldShow){
			list.setMatcher(HEARTBEAT_MATCHER);
		} else {
			list.setMatcher(null);
		}
		getMessagesViewer().refresh();
	}
	
	@Override
	protected TableViewer createTableViewer(Table aMessageTable, Enum[] enums) {
		TableViewer aMessagesViewer = new TableViewer(aMessageTable);
		getSite().setSelectionProvider(aMessagesViewer);
		aMessagesViewer.setContentProvider(new EventListContentProvider<MessageHolder>());
		aMessagesViewer.setLabelProvider(new DirectionalMessageTableFormat(aMessageTable, enums, getSite()));
		return aMessagesViewer;
	}

    @Override
	protected void packColumns(Table table) {
		super.packColumns(table);
		table.getColumn(0).setWidth(25);
	}
    
}
