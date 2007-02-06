package org.marketcetera.photon.views;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbenchPartSite;
import org.marketcetera.core.MSymbol;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.core.IncomingMessageHolder;
import org.marketcetera.photon.core.MessageHolder;
import org.marketcetera.photon.messaging.DirectMessageListenerAdapter;
import org.marketcetera.photon.messaging.SimpleMessageListenerContainer;
import org.marketcetera.photon.quotefeed.QuoteFeedService;
import org.marketcetera.photon.ui.EventListContentProvider;
import org.marketcetera.photon.ui.IndexedTableViewer;
import org.marketcetera.photon.ui.MessageListTableFormat;
import org.marketcetera.photon.ui.TextContributionItem;
import org.marketcetera.quotefeed.IQuoteFeed;
import org.osgi.util.tracker.ServiceTracker;

import quickfix.FieldMap;
import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.MDEntryPx;
import quickfix.field.MDEntrySize;
import quickfix.field.MDEntryType;
import quickfix.field.NoMDEntries;
import quickfix.field.Symbol;
import quickfix.fix42.MarketDataSnapshotFullRefresh;
import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.matchers.Matcher;

public class MarketDataView extends MessagesView implements IMSymbolListener {
	public static final String ID = "org.marketcetera.photon.views.MarketDataView"; 

	private static final int LAST_NORMAL_COLUMN = 1;
	
	public enum MarketDataColumns
	{
		SYMBOL("Symbol"), LASTPX("LastPx"), BIDSZ("BidSz"),BID("Bid"),ASK("Ask"),ASKSZ("AskSz");
		
		private String mName;

		MarketDataColumns(String name) {
			mName = name;
		}

		public String toString() {
			return mName;
		}
	}

	private ServiceTracker quoteFeedTracker;

	private SimpleMessageListenerContainer quoteListener;

	public MarketDataView()
	{
		super(true);
		quoteFeedTracker = new ServiceTracker(
				PhotonPlugin.getDefault().getBundleContext(),
				QuoteFeedService.class.getName(),
				null);
		quoteFeedTracker.open();
		
		quoteListener = PhotonPlugin.getDefault().newQuoteListenerForAdapter(new DirectMessageListenerAdapter()
		{
			@Override
			protected Object doOnMessage(Object convertedMessage) {
				MarketDataView.this.onQuote((Message) convertedMessage);
				return null;
			}
		});
		quoteListener.afterPropertiesSet();
	}
	
	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
	    this.setInput(new BasicEventList<MessageHolder>());
	}


	
	@Override
	public void dispose() {
		quoteListener.stop();
		quoteFeedTracker.close();
		super.dispose();
	}
	
	@Override
	protected void formatTable(Table messageTable) {
        messageTable.getVerticalBar().setEnabled(true);
        messageTable.setForeground(
        		messageTable.getDisplay().getSystemColor(
						SWT.COLOR_INFO_FOREGROUND));

        messageTable.setHeaderVisible(true);

    }

	@Override
	protected IndexedTableViewer createTableViewer(Table aMessageTable, Enum[] enums) {
		IndexedTableViewer aMessagesViewer = new IndexedTableViewer(aMessageTable);
		getSite().setSelectionProvider(aMessagesViewer);
		aMessagesViewer.setContentProvider(new EventListContentProvider<MessageHolder>());
		aMessagesViewer.setLabelProvider(new MarketDataTableFormat(aMessageTable, getSite()));
		
		// Create the cell editors
	    CellEditor[] editors = new CellEditor[MarketDataColumns.values().length];

	    // Column 1 : Completed (Checkbox)
	    editors[0] = new TextCellEditor(aMessageTable);
	    for (int i = 1; i < MarketDataColumns.values().length; i++)
	    {
	    	editors[i] = null;
	    }

	    // Assign the cell editors to the viewer 
	    aMessagesViewer.setCellEditors(editors);
	    String[] columnProperties = new String[MarketDataColumns.values().length];
	    columnProperties[0] = MarketDataColumns.SYMBOL.toString();
	    aMessagesViewer.setColumnProperties(columnProperties);
	    
	    // Set the cell modifier for the viewer
	    aMessagesViewer.setCellModifier(new MarketDataCellModifier(this));

	    return aMessagesViewer;
	}

	@Override
	protected Enum[] getEnumValues() {
		return MarketDataColumns.values();
	}

	@Override
	protected void initializeToolBar(IToolBarManager theToolBarManager) {
		TextContributionItem textContributionItem = new TextContributionItem("");
		theToolBarManager.add(textContributionItem);
		theToolBarManager.add(new AddSymbolAction(textContributionItem, this));
	}
	
	private boolean listContains(String stringValue) {
		if (stringValue == null){
			return false;
		}
		EventList<MessageHolder> list = getInput();
		for (MessageHolder holder : list) {
			try {
				if (stringValue.equalsIgnoreCase(holder.getMessage().getString(Symbol.FIELD))){
					return true;
				}
			} catch (FieldNotFound e) {
				// do nothing
			}
		}
		return false;
	}

	private void updateQuote(Message quote) {
		EventList<MessageHolder> list = getInput();
		int i = 0;
		for (MessageHolder holder : list) {
			Message message = holder.getMessage();
			try {
				if (message.getString(Symbol.FIELD).equals(quote.getString(Symbol.FIELD)))
				{
					IncomingMessageHolder newHolder = new IncomingMessageHolder(quote);
					list.set(i, newHolder);
					getMessagesViewer().update(newHolder, null);
					return;
				}
			} catch (FieldNotFound e) {
			}
			i++;
		}
	}

	
	public void onQuote(final Message aQuote) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if (!getMessagesViewer().getTable().isDisposed())
					updateQuote(aQuote);
			}
		});
	}


	class MarketDataCellModifier implements ICellModifier
	{
		private final MarketDataView view;

		public MarketDataCellModifier(MarketDataView view) {
			this.view = view;
		}

		public boolean canModify(Object element, String property) {
			return MarketDataColumns.SYMBOL.toString().equals(property);
		}

		public Object getValue(Object element, String property) {
			try {
				return ((MessageHolder)element).getMessage().getString(Symbol.FIELD);
			} catch (FieldNotFound e) {
				return "";
			}
		}

		public void modify(Object element, String property, Object value) {
			IQuoteFeed quoteFeed = getQuoteFeed();
			if (quoteFeed == null){
				PhotonPlugin.getMainConsoleLogger().warn("Missing quote feed");
				return;
			}

			String stringValue = value.toString();
			if (listContains(stringValue)){
				return;
			}
			TableItem tableItem = (TableItem) element;
			MessageHolder messageHolder = (MessageHolder)tableItem.getData();
			Message message = messageHolder.getMessage();
			
			try {
				MSymbol symbol = new MSymbol(message.getString(Symbol.FIELD));
				if (quoteFeed != null)
					quoteFeed.unlistenQuotes(symbol);
			} catch (FieldNotFound fnf){}
			message.clear();
			if (stringValue.length()>0){
				message.setField(new Symbol(stringValue));
				if (quoteFeed != null)
					quoteFeed.listenQuotes(new MSymbol(stringValue));
				getMessagesViewer().refresh();
			}
		}
	}


	private static final int BID_SIZE_INDEX = 2;
	private static final int BID_INDEX = 3;
	private static final int ASK_INDEX = 4;
	private static final int ASK_SIZE_INDEX = 5;
	
	class MarketDataTableFormat extends MessageListTableFormat {


		public MarketDataTableFormat(Table table, IWorkbenchPartSite site) {
			super(table, MarketDataColumns.values(), site);
		}

		@Override
		public String getColumnName(int index) {
			if (index <= LAST_NORMAL_COLUMN){
				return super.getColumnName(index);
			}
			switch(index){
			case BID_SIZE_INDEX:
				return "BidSz";
			case BID_INDEX:
				return "Bid";
			case ASK_INDEX:
				return "Ask";
			case ASK_SIZE_INDEX:
				return "AskSz";
			default:
				return "";
			}
		}

		@Override
		public String getColumnText(Object element, int index) {
			if (index <= LAST_NORMAL_COLUMN) {
				return super.getColumnText(element, index);
			}
			MessageHolder messageHolder = (MessageHolder) element;
			Message message = messageHolder.getMessage();
			try {
				switch (index) {
				case BID_SIZE_INDEX:
					return getGroup(message, MDEntryType.BID).getString(
							MDEntrySize.FIELD);
				case BID_INDEX:
					return getGroup(message, MDEntryType.BID).getString(
							MDEntryPx.FIELD);
				case ASK_INDEX:
					return getGroup(message, MDEntryType.OFFER).getString(
							MDEntryPx.FIELD);
				case ASK_SIZE_INDEX:
					return getGroup(message, MDEntryType.OFFER).getString(
							MDEntrySize.FIELD);
				default:
					return "";
				}
			} catch (FieldNotFound e) {
				return "";
			}
		}

		private FieldMap getGroup(Message message, char type) {
			int noEntries;
			try {
				noEntries = message.getInt(NoMDEntries.FIELD);
				for (int i = 1; i < noEntries+1; i++){
					MarketDataSnapshotFullRefresh.NoMDEntries group = new MarketDataSnapshotFullRefresh.NoMDEntries();
					message.getGroup(i, group);
					if (type == group.getChar(MDEntryType.FIELD)){
						return group;
					}
				}
			} catch (FieldNotFound e) {
			}
			return new Message();
		}

		
		
	}

	
	public void onAssertSymbol(MSymbol symbol) {
		addSymbol(symbol);
	}

	/**
	 * @param symbol
	 */
	public void addSymbol(MSymbol symbol) {
		IQuoteFeed quoteFeed = getQuoteFeed();
		if (quoteFeed == null){
			PhotonPlugin.getMainConsoleLogger().warn("Missing quote feed");
			return;
		}

		if (hasSymbol(symbol)) {
			PhotonPlugin.getMainConsoleLogger().warn("Duplicate symbol added to view: " +symbol);
		} else {
			EventList<MessageHolder> list = getInput();

			Message message = new Message();
			message.setField(new Symbol(symbol.toString()));
			list.add(new MessageHolder(message));

			quoteFeed.listenQuotes(symbol);
			getMessagesViewer().refresh();
		}
	}
	
	private boolean hasSymbol(final MSymbol symbol) {
		EventList<MessageHolder> list = getInput();
			
		FilterList<MessageHolder> matches = new FilterList<MessageHolder>(list, 
				new Matcher<MessageHolder>() {
					public boolean matches(MessageHolder listItem) {
						try {
							String listSymbol = listItem.getMessage().getString(Symbol.FIELD).trim().toLowerCase();
							return listSymbol.equals(symbol.getFullSymbol().trim().toLowerCase());
						} catch (FieldNotFound e) {
							return false;
						}
					}
				});
		boolean rv = !matches.isEmpty();
		return rv;
	}

	private IQuoteFeed getQuoteFeed() {
		QuoteFeedService service = (QuoteFeedService) quoteFeedTracker.getService();
		return (IQuoteFeed) (service == null ? null : service.getQuoteFeed());
	}

	public void removeItem(MessageHolder holder){
		IQuoteFeed quoteFeed = getQuoteFeed();
		if (quoteFeed == null){
			PhotonPlugin.getMainConsoleLogger().warn("Missing quote feed");
			return;
		}
		try {
			quoteFeed.unlistenQuotes(new MSymbol(holder.getMessage().getString(Symbol.FIELD)));
		} catch (FieldNotFound e) {
		}
		getInput().remove(holder);
		
	}
	
	public void removeSymbol(MSymbol symbol) {
		IQuoteFeed quoteFeed = getQuoteFeed();
		if (quoteFeed == null){
			PhotonPlugin.getMainConsoleLogger().warn("Missing quote feed");
			return;
		}

		quoteFeed.unlistenQuotes(symbol);

		EventList<MessageHolder> list = getInput();
		for (MessageHolder holder : list) {
			try {
				String messageSymbolString = holder.getMessage().getString(Symbol.FIELD);
				if (messageSymbolString.equals(symbol.toString())){
					list.remove(holder);
					break;
				}
			} catch (FieldNotFound e) {
			}
		}
		getMessagesViewer().refresh();
		
	}
	
}
