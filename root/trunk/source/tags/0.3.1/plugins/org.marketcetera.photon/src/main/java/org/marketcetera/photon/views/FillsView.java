package org.marketcetera.photon.views;

import java.lang.reflect.Field;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.marketcetera.photon.IFieldIdentifier;
import org.marketcetera.photon.core.FIXMessageHistory;
import org.marketcetera.photon.core.MessageHolder;

import quickfix.field.Account;
import quickfix.field.AvgPx;
import quickfix.field.ClOrdID;
import quickfix.field.CumQty;
import quickfix.field.LastMkt;
import quickfix.field.LastPx;
import quickfix.field.LastShares;
import quickfix.field.LeavesQty;
import quickfix.field.OrdStatus;
import quickfix.field.OrderQty;
import quickfix.field.Price;
import quickfix.field.Side;
import quickfix.field.Symbol;
import ca.odell.glazedlists.EventList;

public class FillsView extends HistoryMessagesView {

	public static final String ID = "org.marketcetera.photon.views.FillsView";

	/**
	 * The columns of the Fills page represented
	 * as FIX fields.
	 * 
	 * @author gmiller
	 *
	 */
	public enum FillColumns implements IFieldIdentifier{
		CLORDID(ClOrdID.class), ORDSTATUS(OrdStatus.class), SIDE(Side.class), SYMBOL(
				Symbol.class), ORDERQTY(OrderQty.class), CUMQTY(CumQty.class), LEAVESQTY(
				LeavesQty.class), Price(Price.class), AVGPX(AvgPx.class), ACCOUNT(
				Account.class), LASTSHARES(LastShares.class), LASTPX(
				LastPx.class), LASTMKT(LastMkt.class);
		private String name;
		private Integer fieldID;
		private Integer groupID;
		private Integer groupDiscriminatorID;
		private Object groupDiscriminatorValue;

		FillColumns(String name){
			this.name = name;
		}
		
		FillColumns(Class clazz, Integer fieldID, Integer groupID, Integer groupDiscriminatorID, Object groupDiscriminatorValue){
			this(clazz);
			this.fieldID = fieldID;
			this.groupID = groupID;
			this.groupDiscriminatorID = groupDiscriminatorID;
			this.groupDiscriminatorValue = groupDiscriminatorValue;
		}

		FillColumns(Class clazz) {
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