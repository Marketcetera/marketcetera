package org.marketcetera.photon.ui;


import java.lang.reflect.Field;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.marketcetera.photon.IFieldIdentifier;
import org.marketcetera.quickfix.FIXDataDictionaryManager;
import org.marketcetera.quickfix.FIXVersion;

import quickfix.DataDictionary;
import quickfix.FieldNotFound;
import quickfix.Group;
import quickfix.Message;
import quickfix.field.MDEntryPx;
import quickfix.field.MDEntrySize;
import quickfix.field.MDEntryTime;
import quickfix.field.MDEntryType;
import quickfix.field.MDMkt;
import quickfix.field.NoMDEntries;
import quickfix.fix44.MarketDataSnapshotFullRefresh;
import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;

public class BookComposite extends Composite implements IBookComposite
{

	public enum BookColumns implements IFieldIdentifier {
		MDMKT(MDMkt.class), MDENTRYPX(MDEntryPx.class), MDENTRYSIZE(MDEntrySize.class),
		MDENTRYTIME(MDEntryTime.class);

		private String name;
		private Integer fieldID;
		private Integer groupID;
		private Integer groupDiscriminatorID;
		private Object groupDiscriminatorValue;


		// todo:remove below constructors if they are not used
//		BookColumns(String name){
//			this.name = name;
//		}
//
//		BookColumns(Class clazz, Integer fieldID, Integer groupID, Integer groupDiscriminatorID, Object groupDiscriminatorValue){
//			this(clazz);
//			this.fieldID = fieldID;
//			this.groupID = groupID;
//			this.groupDiscriminatorID = groupDiscriminatorID;
//			this.groupDiscriminatorValue = groupDiscriminatorValue;
//		}

		BookColumns(Class<?> clazz) {
			name = clazz.getSimpleName();
			try {
				Field fieldField = clazz.getField("FIELD"); //$NON-NLS-1$
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
	
	private Table bidTable;
	private Table askTable;
	private IndexedTableViewer bidViewer;
	private IndexedTableViewer askViewer;
	private final FormToolkit toolkit;
	private Message currentMarketRefresh;

	public BookComposite(Composite parent, int style){
		this(parent, style, null);
	}
	
	public BookComposite(Composite parent, int style, FormToolkit toolkit) 
	{
		super(parent, style);
		this.toolkit = toolkit;
		
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

	@Override
	public void dispose() {
		super.dispose();
	}

	private IndexedTableViewer getTableViewer(Table theTable) {
		IndexedTableViewer tableViewer = new IndexedTableViewer(theTable);
		DataDictionary dictionary = FIXDataDictionaryManager.getFIXDataDictionary(FIXVersion.FIX44).getDictionary();
		EnumTableFormat<Message> format = new EnumTableFormat<Message>(theTable, BookColumns.values(), dictionary);
		tableViewer.setContentProvider(new EventListContentProvider<Group>());
		tableViewer.setLabelProvider(format);
		return tableViewer;
	}

	private Table getTable() {

		Table table;
		if (toolkit == null){
			table = new Table(this, SWT.SINGLE | SWT.FULL_SELECTION | SWT.VIRTUAL);
		} else {
			table = toolkit.createTable(this, SWT.SINGLE | SWT.FULL_SELECTION | SWT.VIRTUAL);
		}
        GridData tableLayout = new GridData();
        tableLayout.horizontalAlignment = GridData.FILL;
        tableLayout.verticalAlignment = GridData.FILL;
        tableLayout.grabExcessHorizontalSpace = true;
        tableLayout.grabExcessVerticalSpace = true;
        tableLayout.heightHint = 200;
        tableLayout.widthHint = 200;
        table.setLayoutData(tableLayout);
        table.setHeaderVisible(false);

        return table;
	}


	public void setInput(Message marketRefresh){
		currentMarketRefresh = marketRefresh;
		boolean hadOldInput = bidViewer.getInput()!= null;
		if (marketRefresh == null) {
			if (!isDisposed()) {
				bidViewer.setInput(null);
				askViewer.setInput(null);
			}
		} else {
			bidViewer.setInput(getBookEntryList(marketRefresh, MDEntryType.BID));
			askViewer.setInput(getBookEntryList(marketRefresh, MDEntryType.OFFER));
		}
		if (!hadOldInput && marketRefresh != null){
			packColumns();
		}
	}
	
	public Message getInput(){
		return currentMarketRefresh;
	}
	
	public EventList<Group> getBookEntryList(Message marketRefresh, char mdEntryType)
	{
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
		Display theDisplay = Display.getDefault();
		if (theDisplay.getThread() == Thread.currentThread()){
			setInput(aMarketRefresh);
		} else {
			theDisplay.asyncExec(
				new Runnable(){
					public void run()
					{
						setInput(aMarketRefresh);
					}
				}
			);
		}
	}

	public void saveState(IMemento memento) {		
	}

}
