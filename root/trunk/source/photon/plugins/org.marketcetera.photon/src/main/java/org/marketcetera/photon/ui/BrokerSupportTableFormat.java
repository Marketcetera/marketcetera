package org.marketcetera.photon.ui;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.marketcetera.messagehistory.ReportHolder;
import org.marketcetera.photon.Messages;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Label provider that adds a broker id column.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public class BrokerSupportTableFormat extends FIXMessageTableFormat<ReportHolder> {

	private int mIndex;

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
		mIndex  = getTable().getColumnCount() - 1;
	}
	
	@Override
	protected String convertColumnValueToText(ReportHolder baseObject,
			int columnIndex) {
		if (columnIndex == mIndex) {
			BrokerID brokerID = baseObject.getReport().getBrokerID();
			return brokerID == null ? null : brokerID.getValue();
		}
		return super.convertColumnValueToText(baseObject, columnIndex);
	}

}
