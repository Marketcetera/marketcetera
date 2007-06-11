package org.marketcetera.photon.views;


import java.lang.reflect.Field;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.widgets.Composite;
import org.marketcetera.photon.IFieldIdentifier;
import org.marketcetera.photon.actions.CancelAllOpenOrdersAction;
import org.marketcetera.photon.core.FIXMessageHistory;
import org.marketcetera.photon.core.MessageHolder;

import quickfix.field.Account;
import quickfix.field.AvgPx;
import quickfix.field.ClOrdID;
import quickfix.field.CumQty;
import quickfix.field.ExecID;
import quickfix.field.LastMkt;
import quickfix.field.LastPx;
import quickfix.field.LastShares;
import quickfix.field.LeavesQty;
import quickfix.field.OrdStatus;
import quickfix.field.OrderID;
import quickfix.field.OrderQty;
import quickfix.field.Price;
import quickfix.field.SendingTime;
import quickfix.field.Side;
import quickfix.field.Symbol;
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
	public enum OpenOrderColumns implements IFieldIdentifier {
		SENDINGTIME(SendingTime.class), CLORDID(ClOrdID.class), ORDSTATUS(OrdStatus.class), SIDE(
				Side.class), SYMBOL(Symbol.class), ORDERQTY(OrderQty.class), CUMQTY(
				CumQty.class), LEAVESQTY(LeavesQty.class), Price(Price.class), AVGPX(
				AvgPx.class), ACCOUNT(Account.class), LASTSHARES(LastShares.class), LASTPX(
				LastPx.class), LASTMKT(LastMkt.class), EXECID(ExecID.class),
				ORDERID(OrderID.class);

		private String name;
		private Integer fieldID;
		private Integer groupID;
		private Integer groupDiscriminatorID;
		private Object groupDiscriminatorValue;

		OpenOrderColumns(String name){
			this.name = name;
		}

		OpenOrderColumns(Class clazz, Integer fieldID, Integer groupID, Integer groupDiscriminatorID, Object groupDiscriminatorValue){
			this(clazz);
			this.fieldID = fieldID;
			this.groupID = groupID;
			this.groupDiscriminatorID = groupDiscriminatorID;
			this.groupDiscriminatorValue = groupDiscriminatorValue;
		}

		OpenOrderColumns(Class clazz) {
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
		
		public Integer getGroupID() {
			return groupID;
		}

		public Integer getGroupDiscriminatorID() {
			return groupDiscriminatorID;
		}

		public Object getGroupDiscriminatorValue() {
			return groupDiscriminatorValue;
		}

	};

	@Override
	protected Enum[] getEnumValues() {
		return OpenOrderColumns.values();
	}

	@Override
	protected void initializeToolBar(IToolBarManager theToolBarManager) {
		theToolBarManager.add(new CancelAllOpenOrdersAction());
	}

	public EventList<MessageHolder> extractList(FIXMessageHistory hist){
		return hist.getOpenOrdersList();
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}

}
