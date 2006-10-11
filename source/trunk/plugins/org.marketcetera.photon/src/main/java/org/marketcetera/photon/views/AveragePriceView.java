package org.marketcetera.photon.views;

import java.util.List;

import org.eclipse.jface.action.IToolBarManager;
import org.marketcetera.photon.model.AveragePriceFunction;
import org.marketcetera.photon.model.FIXMessageHistory;
import org.marketcetera.photon.model.MessageHolder;
import org.marketcetera.photon.model.NotNullMatcher;
import org.marketcetera.photon.model.SymbolSideComparator;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.FunctionList;
import ca.odell.glazedlists.GroupingList;

public class AveragePriceView extends MessagesView {


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
		return (FilterList<MessageHolder>) getMessagesViewer().getInput();
	}


	public EventList<MessageHolder> extractList(FIXMessageHistory input) {
		return input.getAveragePricesList();
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}

}
