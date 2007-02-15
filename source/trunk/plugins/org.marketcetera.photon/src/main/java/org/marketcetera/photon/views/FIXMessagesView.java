package org.marketcetera.photon.views;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.marketcetera.photon.actions.ShowHeartbeatsAction;
import org.marketcetera.photon.core.FIXMatcher;
import org.marketcetera.photon.core.FIXMessageHistory;
import org.marketcetera.photon.core.MessageHolder;
import org.marketcetera.photon.ui.DirectionalMessageTableFormat;
import org.marketcetera.photon.ui.EventListContentProvider;
import org.marketcetera.photon.ui.IndexedTableViewer;

import quickfix.field.MsgType;
import quickfix.field.RefSeqNum;
import quickfix.field.SessionRejectReason;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.matchers.Matcher;

/**
 * FIX Messages view.
 * 
 * @author gmiller
 * @author andrei@lissovski.org
 */
public class FIXMessagesView extends HistoryMessagesView {

	public static final String ID = "org.marketcetera.photon.views.FIXMessagesView";
	private static final Matcher<? super MessageHolder> HEARTBEAT_MATCHER = new FIXMatcher<String>(MsgType.FIELD, MsgType.HEARTBEAT, false);

	private ShowHeartbeatsAction showHeartbeatsAction;
	private static final String SHOW_HEARTBEATS_SAVED_STATE_KEY = "SHOW_HEARTBEATS";

	
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
				"CumQty"), LEAVESQTY("LeavesQty"), ORDTYPE("OrdType"), Price("Price"), AVGPX(
				"AvgPx"), ACCOUNT("Account"), LASTSHARES("LastShares"), LASTPX(
				"LastPx"), LASTMKT("LastMkt"), EXECID("ExecID"), ORDERID(
				"OrderID"), SESSION_REJECT_REASON("SessionRejectReason"), REF_SEQ_NUM("RefSeqNum");

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

    /* (non-Javadoc)
	 * @see org.eclipse.ui.part.ViewPart#init(org.eclipse.ui.IViewSite, org.eclipse.ui.IMemento)
	 */
	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);

		showHeartbeatsAction = new ShowHeartbeatsAction(this);
		
		if (memento != null  // can be null if there is no previous saved state
			&& memento.getInteger(SHOW_HEARTBEATS_SAVED_STATE_KEY) != null) 
		{
			showHeartbeatsAction.setChecked(memento.getInteger(SHOW_HEARTBEATS_SAVED_STATE_KEY).intValue() != 0);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.ViewPart#saveState(org.eclipse.ui.IMemento)
	 */
	@Override
	public void saveState(IMemento memento) {
		super.saveState(memento);
		memento.putInteger(SHOW_HEARTBEATS_SAVED_STATE_KEY, showHeartbeatsAction.isChecked() ? 1 : 0);
	}

	protected void initializeToolBar(IToolBarManager theToolBarManager) {
		//theToolBarManager.add(new TextContributionItem(""));
    	theToolBarManager.add(showHeartbeatsAction);
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
	protected IndexedTableViewer createTableViewer(Table aMessageTable, Enum[] enums) {
		IndexedTableViewer aMessagesViewer = new IndexedTableViewer(aMessageTable);
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
