package org.marketcetera.photon.views;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.marketcetera.photon.model.MessageHolder;
import org.marketcetera.photon.ui.EnumLabelProvider;
import org.marketcetera.photon.ui.EventListContentProvider;

public class FIXMessagesView extends MessagesView {

    public static final String ID = "org.marketcetera.photon.views.FIXMessagesView";

	
	/**
	 * The columns of the Messages page, represented
	 * as FIX fields.
	 * 
	 * @author gmiller
	 *
	 */
	public enum MessageColumns {
		DIRECTION("D"), TRANSACTTIME("TransactTime"), MSGTYPE("MsgType"), CLORDID("ClOrdID"),
		ORDERID("OrderID"), ORICCLORDID("OrigClOrdID"), ORDSTATUS("OrdStatus"), SIDE(
				"Side"), SYMBOL("Symbol"), ORDERQTY("OrderQty"), CUMQTY(
				"CumQty"), LEAVESQTY("LeavesQty"), Price("Price"), AVGPX(
				"AvgPx"), ACCOUNT("Account"), LASTSHARES("LastShares"), LASTPX(
				"LastPx"), LASTMKT("LastMkt"), EXECID("ExecID");

		private String mName;

		MessageColumns(String name) {
			mName = name;
		}

		public String toString() {
			return mName;
		}
	}

	private Table messageTable;
	private TableViewer messagesViewer;

	@Override
	public void createPartControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		composite.setLayout(layout);
		layout.numColumns = 1;

        messageTable = createMessageTable(composite);
		messagesViewer = new TableViewer(messageTable);
		messagesViewer.setContentProvider(new EventListContentProvider<MessageHolder>());
		messagesViewer.setLabelProvider(new EnumLabelProvider(MessageColumns.values()));
		
		formatTable(messageTable);

        messageTable.setBackground(
        		messageTable.getDisplay().getSystemColor(
						SWT.COLOR_INFO_BACKGROUND));
        messageTable.setForeground(
        		messageTable.getDisplay().getSystemColor(
						SWT.COLOR_INFO_FOREGROUND));

        messageTable.setHeaderVisible(true);

		packColumns(messageTable);
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
