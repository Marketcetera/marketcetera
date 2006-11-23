package org.marketcetera.photon.views;

import java.util.List;

import org.eclipse.jface.action.IToolBarManager;
import org.marketcetera.photon.core.AveragePriceFunction;
import org.marketcetera.photon.core.FIXMessageHistory;
import org.marketcetera.photon.core.MessageHolder;
import org.marketcetera.photon.core.NotNullMatcher;
import org.marketcetera.photon.core.SymbolSideComparator;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.FunctionList;
import ca.odell.glazedlists.GroupingList;

public class AveragePriceView extends HistoryMessagesView {


	public static final String ID = "org.marketcetera.photon.views.AveragePriceView";

	/**
	 * The columns of the average price page specified as
	 * FIX fields.
	 * 
	 * @author gmiller
	 *
	 */
	public enum AvgPriceColumns {
		DIRECTION("D"), SIDE("Side"), SYMBOL("Symbol"), ORDERQTY("OrderQty"), CUMQTY("CumQty"), 
		AVGPX("AvgPx"), ACCOUNT("Account");

		private String mName;

		AvgPriceColumns(String name) {
			mName = name;
		}

		public String toString() {
			return mName;
		}
	};

	@Override
	protected Enum[] getEnumValues() {
		return AvgPriceColumns.values();
	}

	@Override
	protected void initializeToolBar(IToolBarManager theToolBarManager) {
		
	}

	@SuppressWarnings("unchecked")
	protected FilterList<MessageHolder> getFilterList() {
		return (FilterList<MessageHolder>) getInput();
	}


	public EventList<MessageHolder> extractList(FIXMessageHistory input) {
		return input.getAveragePricesList();
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}

}
