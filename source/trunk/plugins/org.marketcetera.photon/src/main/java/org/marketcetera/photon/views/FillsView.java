package org.marketcetera.photon.views;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.marketcetera.photon.model.FIXMessageHistory;
import org.marketcetera.photon.model.FillMatcher;
import org.marketcetera.photon.model.MessageHolder;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;

public class FillsView extends HistoryMessagesView {

	public static final String ID = "org.marketcetera.photon.views.FillsView";

	/**
	 * The columns of the Fills page represented
	 * as FIX fields.
	 * 
	 * @author gmiller
	 *
	 */
	public enum FillColumns {
		CLORDID("ClOrdID"), ORDSTATUS("OrdStatus"), SIDE("Side"), SYMBOL("Symbol"), ORDERQTY(
				"OrderQty"), CUMQTY("CumQty"), LEAVESQTY("LeavesQty"), Price(
				"Price"), AVGPX("AvgPx"), STRATEGY("Strategy"), ACCOUNT(
				"Account"), LASTSHARES("LastShares"), LASTPX("LastPx"), LASTMKT(
				"LastMkt");

		private String mName;

		FillColumns(String name) {
			mName = name;
		}

		public String toString() {
			return mName;
		}
	};

	@Override
	protected Enum[] getEnumValues() {
		return FillColumns.values();
	}

	@Override
	protected void initializeToolBar(IToolBarManager theToolBarManager) {
		theToolBarManager.add(new ContributionItem()
		{
			@Override
			public void fill(ToolBar parent, int index) {
				Text symbolText = new Text(parent,SWT.BORDER);
			}
		});
		theToolBarManager.update(true);

	}


	public EventList<MessageHolder> extractList(FIXMessageHistory input) {
		return input.getFillsList();
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}

}