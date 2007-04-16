package org.marketcetera.photon.views;

import java.lang.reflect.Field;

import org.eclipse.jface.action.IToolBarManager;
import org.marketcetera.photon.IFieldIdentifier;
import org.marketcetera.photon.core.FIXMessageHistory;
import org.marketcetera.photon.core.MessageHolder;

import quickfix.field.Account;
import quickfix.field.AvgPx;
import quickfix.field.CumQty;
import quickfix.field.OrderQty;
import quickfix.field.Side;
import quickfix.field.Symbol;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;

public class AveragePriceView extends HistoryMessagesView {


	public static final String ID = "org.marketcetera.photon.views.AveragePriceView";

	/**
	 * The columns of the average price page specified as
	 * FIX fields.
	 * 
	 * @author gmiller
	 *
	 */
	public enum AvgPriceColumns implements IFieldIdentifier {
		SIDE(Side.class), SYMBOL(Symbol.class), ORDERQTY(OrderQty.class), CUMQTY(CumQty.class), 
		AVGPX(AvgPx.class), ACCOUNT(Account.class);

		private String name;
		private Integer fieldID;

		AvgPriceColumns(String name){
			this.name = name;
		}

		AvgPriceColumns(Class clazz) {
			name = clazz.getSimpleName();
			try {
				Field fieldField = clazz.getField("FIELD");
				fieldID = (Integer) fieldField.get(null);
			} catch (Throwable t){
				assert(false);
			}
		}

		public String toString() {
			return name;
		}

		public Integer getFieldID() {
			return fieldID;
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
