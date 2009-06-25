package org.marketcetera.photon.ui;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.marketcetera.client.ClientManager;
import org.marketcetera.messagehistory.ReportHolder;
import org.marketcetera.photon.Messages;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.UserID;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Label provider that adds additional non-fix message columns.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public class BrokerSupportTableFormat extends FIXMessageTableFormat<ReportHolder> {

	private int mBrokerIndex;
	private int mTraderIndex;

	/**
	 * Constructor.
	 * 
	 * @param table the table
	 * @param assignedViewID the view id
	 */
	public BrokerSupportTableFormat(Table table, String assignedViewID) {
		super(table, assignedViewID, ReportHolder.class);
	}
	
	@Override
	protected void recreateAllColumns(List<Integer> fieldsToShow) {
		super.recreateAllColumns(fieldsToShow);
		TableColumn broker = new TableColumn(getTable(), SWT.LEFT);
		broker.setText(Messages.BROKER_SUPPORT_TABLE_FORMAT_BROKER_COLUMN.getText());
		broker.setMoveable(false);
		broker.setWidth(70);
		mBrokerIndex  = getTable().getColumnCount() - 1;
		TableColumn trader = new TableColumn(getTable(), SWT.LEFT);
		trader.setText(Messages.BROKER_SUPPORT_TABLE_FORMAT_TRADER_COLUMN.getText());
		trader.setMoveable(false);
		trader.setWidth(70);
		mTraderIndex  = getTable().getColumnCount() - 1;
	}
	
	@Override
	protected String convertColumnValueToText(ReportHolder baseObject,
			int columnIndex) {
		if (columnIndex == mBrokerIndex) {
			BrokerID brokerID = baseObject.getReport().getBrokerID();
			return brokerID == null ? null : brokerID.getValue();
		} else if (columnIndex == mTraderIndex) {
			UserID traderID = baseObject.getReport().getActorID();
			return traderID == null ? null : getTrader(traderID);
		}
		return super.convertColumnValueToText(baseObject, columnIndex);
	}

	private String getTrader(UserID id) {
		try {
			return ClientManager.getInstance().getUserInfo(id, true).getName();
		} catch (Exception e) {
			Messages.BROKER_SUPPORT_TABLE_FORMAT_TRADER_NAME_ERROR.error(this, e, id);
			return id.toString();
		}
	}

}
