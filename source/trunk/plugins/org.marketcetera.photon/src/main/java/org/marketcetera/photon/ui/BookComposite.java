package org.marketcetera.photon.ui;


import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.marketcetera.quotefeed.IMessageListener;

import quickfix.FieldNotFound;
import quickfix.Group;
import quickfix.Message;
import quickfix.field.MDEntryType;
import quickfix.field.NoMDEntries;
import quickfix.fix42.MarketDataSnapshotFullRefresh;
import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;

public class BookComposite extends Composite implements IMessageListener
{

	public enum BookColumns{
		MDMKT("MDMkt"), MDENTRYPX("MDEntryPx"), MDENTRYSIZE("MDEntrySize"),
		MDENTRYTIME("MDEntryTime");

		private String mName;

		BookColumns(String name) {
			mName = name;
		}

		public String toString() {
			return mName;
		}
	}	
	private Table bidTable;
	private Table askTable;
	private TableViewer bidViewer;
	private TableViewer askViewer;

	public BookComposite(Composite parent, int style) 
	{
		super(parent, style);
		
		GridLayout gridLayout = new GridLayout();
		GridData layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace=true;
		layoutData.grabExcessVerticalSpace=true;
		layoutData.verticalAlignment = SWT.FILL;
		layoutData.horizontalAlignment = SWT.FILL;
		layoutData.horizontalSpan = 5;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.numColumns = 2;
		
		this.setLayout(gridLayout);
		this.setLayoutData(layoutData);
		
		bidTable = getTable();
		askTable = getTable();
		bidViewer = getTableViewer(bidTable);
		askViewer = getTableViewer(askTable);
		
	}

	private TableViewer getTableViewer(Table theTable) {
		TableViewer tableViewer = new TableViewer(theTable);
		EnumTableFormat format = new EnumTableFormat(theTable, BookColumns.values());
		tableViewer.setContentProvider(new EventListContentProvider<Group>());
		tableViewer.setLabelProvider(format);
		return tableViewer;
	}

	private Table getTable() {
        Table table = new Table(this, SWT.SINGLE | SWT.FULL_SELECTION | SWT.VIRTUAL);
        GridData tableLayout = new GridData();
        tableLayout.horizontalAlignment = GridData.FILL;
        tableLayout.verticalAlignment = GridData.FILL;
        tableLayout.grabExcessHorizontalSpace = true;
        tableLayout.grabExcessVerticalSpace = true;
        tableLayout.heightHint = 200;
        tableLayout.widthHint = 200;
        table.setLayoutData(tableLayout);
        table.setHeaderVisible(true);

        return table;
	}


	public void setInput(Message marketRefresh){
		Object oldInput = bidViewer.getInput();
		bidViewer.setInput(getBookEntryList(marketRefresh, MDEntryType.BID));
		askViewer.setInput(getBookEntryList(marketRefresh, MDEntryType.OFFER));
		if (oldInput == null){
			packColumns();
		}
	}
	
	public EventList<Group> getBookEntryList(Message marketRefresh, char mdEntryType)
	{
		Group g;
		EventList<Group> outputList = new BasicEventList<Group>();
		try {
			int numEntries = marketRefresh.getInt(NoMDEntries.FIELD);
			for (int i = 1; i <= numEntries; i++){
				MarketDataSnapshotFullRefresh.NoMDEntries group = new MarketDataSnapshotFullRefresh.NoMDEntries();
				marketRefresh.getGroup(i, group);
				if (group.getMDEntryType().getValue() == mdEntryType) {
					outputList.add(group);
				}
			}
		} catch (FieldNotFound e) {
		}
		return outputList;
	}

	private void packColumns() {
		for (TableColumn aColumn : bidTable.getColumns()) {
			aColumn.pack();
		}
		for (TableColumn aColumn : askTable.getColumns()) {
			aColumn.pack();
		}
	}

	public void onQuote(final Message aMarketRefresh) {
		Display.getDefault().asyncExec(
			new Runnable(){
				public void run()
				{
					setInput(aMarketRefresh);
				}
			}
		);
	}
	public void onAdmin(Message arg0) {
	}

	public void onTrade(Message arg0) {
	}


    public void onQuotes(Message [] quotes) {
        for (Message quoteMessage : quotes) {
            onQuote(quoteMessage);
        }
    }

    public void onTrades(Message [] trades) {
        for (Message trade : trades) {
            onTrade(trade);
        }
    }

    public void onAdmins(Message [] adminMessages) {
        for (Message adminMessage : adminMessages) {
            onAdmin(adminMessage);
        }
    }

}
