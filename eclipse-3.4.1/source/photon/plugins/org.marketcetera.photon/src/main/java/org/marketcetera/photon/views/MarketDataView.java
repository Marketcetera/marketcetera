package org.marketcetera.photon.views;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbenchPartSite;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.IFeedComponentListener;
import org.marketcetera.core.LockHelper;
import org.marketcetera.core.MSymbol;
import org.marketcetera.core.CoreException;
import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.event.HasFIXMessage;
import org.marketcetera.marketdata.FeedStatus;
import org.marketcetera.marketdata.IFeedComponent;
import org.marketcetera.marketdata.IMarketDataFeedToken;
import org.marketcetera.messagehistory.IncomingMessageHolder;
import org.marketcetera.messagehistory.MessageHolder;
import org.marketcetera.photon.EclipseUtils;
import org.marketcetera.photon.IFieldIdentifier;
import org.marketcetera.photon.Messages;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.marketdata.MarketDataFeedService;
import org.marketcetera.photon.marketdata.MarketDataFeedTracker;
import org.marketcetera.photon.marketdata.MarketDataUtils;
import org.marketcetera.photon.ui.EventListContentProvider;
import org.marketcetera.photon.ui.IndexedTableViewer;
import org.marketcetera.photon.ui.MessageListTableFormat;
import org.marketcetera.photon.ui.TextContributionItem;
import org.marketcetera.quickfix.FIXDataDictionaryManager;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.quickfix.FIXVersion;

import quickfix.DataDictionary;
import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.BidPx;
import quickfix.field.BidSize;
import quickfix.field.LastPx;
import quickfix.field.LastQty;
import quickfix.field.MDEntryPx;
import quickfix.field.MDEntrySize;
import quickfix.field.MDEntryType;
import quickfix.field.MDReqID;
import quickfix.field.NoMDEntries;
import quickfix.field.OfferPx;
import quickfix.field.OfferSize;
import quickfix.field.Symbol;
import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.matchers.Matcher;

/* $License$ */

/**
 * Market data view.
 * 
 * @author gmiller
 * @author caroline.leung@softwaregoodness.com
 * @author andrei.lissovski@softwaregoodness.com
 * @author michael.lossos@softwaregoodness.com
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.5.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class MarketDataView
    extends MessagesView
    implements IMSymbolListener, IFeedComponentListener, ISubscriber, Messages
{


	public static final String ID = "org.marketcetera.photon.views.MarketDataView";  //$NON-NLS-1$

	private static final int ZERO_WIDTH_COLUMN_INDEX = 0;
	private static final int SYMBOL_COLUMN_INDEX = 1;
//	private static final int LASTPX_COLUMN_INDEX = 2;
	private static final int LASTQTY_COLUMN_INDEX = 3;
	private static final int LAST_NORMAL_COLUMN_INDEX = LASTQTY_COLUMN_INDEX;
    /**
     * helper object to coordinate read and writes to the model quote list
     */
    private final LockHelper mListLocker = new LockHelper();	
	
	public enum MarketDataColumns implements IFieldIdentifier
	{
		ZEROWIDTH(""),  //$NON-NLS-1$
		SYMBOL(Symbol.class), 
		LASTPX(LastPx.class, MDEntryPx.FIELD, NoMDEntries.FIELD, MDEntryType.FIELD, MDEntryType.TRADE), 
		LASTQTY(LastQty.class, MDEntrySize.FIELD, NoMDEntries.FIELD, MDEntryType.FIELD, MDEntryType.TRADE), 
		BIDSZ(BidSize.class, MDEntrySize.FIELD, NoMDEntries.FIELD, MDEntryType.FIELD, MDEntryType.BID),
		BID(BidPx.class, MDEntryPx.FIELD, NoMDEntries.FIELD, MDEntryType.FIELD, MDEntryType.BID), 
		ASK(OfferPx.class, MDEntryPx.FIELD, NoMDEntries.FIELD, MDEntryType.FIELD, MDEntryType.OFFER), 
		ASKSZ(OfferSize.class, MDEntrySize.FIELD, NoMDEntries.FIELD, MDEntryType.FIELD, MDEntryType.OFFER);
		
		private String name;
		private Integer fieldID;
		private Integer groupID;
		private Integer groupDiscriminatorID;
		private Object groupDiscriminatorValue;


		MarketDataColumns(String name){
			this.name = name;
		}

		MarketDataColumns(Class<?> clazz, Integer fieldID, Integer groupID, Integer groupDiscriminatorID, Object groupDiscriminatorValue){
			this(clazz);
			this.fieldID = fieldID;
			this.groupID = groupID;
			this.groupDiscriminatorID = groupDiscriminatorID;
			this.groupDiscriminatorValue = groupDiscriminatorValue;
		}

		MarketDataColumns(Class<?> clazz) {
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

	private MarketDataFeedTracker marketDataTracker;

	private TextContributionItem symbolEntryText;

	private final Map<MSymbol, IMarketDataFeedToken<?>> tokenMap = new HashMap<MSymbol, IMarketDataFeedToken<?>>();

	public MarketDataView()
	{
		super(true);
		marketDataTracker = new MarketDataFeedTracker(
				PhotonPlugin.getDefault().getBundleContext());
		marketDataTracker.open();
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
	    this.setInput(new BasicEventList<MessageHolder>());
	}

	@Override
	public void setFocus() {
		if(symbolEntryText.isEnabled())
			symbolEntryText.setFocus();
	}
	
	@Override
	public void dispose() {
		marketDataTracker.close();
		
		super.dispose();
	}
	
	@Override
	protected void formatTable(Table messageTable) {
        messageTable.getVerticalBar().setEnabled(true);
        messageTable.setForeground(
        		messageTable.getDisplay().getSystemColor(
						SWT.COLOR_INFO_FOREGROUND));

        messageTable.setHeaderVisible(true);

		for (int i = 0; i < messageTable.getColumnCount(); i++) {
			boolean moveable = true;
			if (i == 0) {
				moveable = false;
			}
			messageTable.getColumn(i).setMoveable(moveable);
		}
    }

	@Override
	protected void packColumns(Table table) {
		super.packColumns(table);
		
		// The following is required to work around the root cause of #132 ""Dirt" rendered in Market Data view 
		// row selection".
		//
		// There is no way to remove the extra spacing in the first TableColumn of an SWT Table on Windows.
		// This extra spacing is what causes the visible gap on a selected row.
		// It has nothing to do with the TextCellEditor or TableViewer -- the problem can be 
		// reproduced using just an SWT Table with text in the TableItems. The first column always has extra space.
		//
		// The best that can be done to get rid of the visible gap in a selected row
		// is to create an unmovable zero width first column. 
		TableColumn zeroFirstColumn = table.getColumn(ZERO_WIDTH_COLUMN_INDEX);
		zeroFirstColumn.setWidth(0);
		zeroFirstColumn.setResizable(false);
		zeroFirstColumn.setMoveable(false);
		zeroFirstColumn.setText(""); //$NON-NLS-1$
		zeroFirstColumn.setImage(null);
		for (int i = 1; i < table.getColumnCount(); i++) {
			table.getColumn(i).setWidth(getTableColumnWidth(table, i));
		}
	}
	
	private int getColumnWidth(Table messageTable, int width) {
		return EclipseUtils.getTextAreaSize(messageTable, null, width, 1.0).x;
	}
	
	private int getTableColumnWidth(Table table, int index) {
		switch (index) {
		case SYMBOL_COLUMN_INDEX:
			return getColumnWidth(table, 10);	// TODO i18n		
		case BID_SIZE_INDEX:
			return getColumnWidth(table, 10);    // TODO i18n
		case BID_INDEX:
			return getColumnWidth(table, 10);    // TODO i18n
		case ASK_INDEX:
			return getColumnWidth(table, 10);    // TODO i18n
		case ASK_SIZE_INDEX:
			return getColumnWidth(table, 10);    // TODO i18n
		default:
			return getColumnWidth(table, 11);    // TODO i18n
		}
	}


	@Override
	protected IndexedTableViewer createTableViewer(Table aMessageTable, Enum<?>[] enums) {
		IndexedTableViewer aMessagesViewer = new IndexedTableViewer(aMessageTable);
		getSite().setSelectionProvider(aMessagesViewer);
		aMessagesViewer.setContentProvider(new EventListContentProvider<MessageHolder>());
		aMessagesViewer.setLabelProvider(new MarketDataTableFormat(aMessageTable, getSite(), FIXDataDictionaryManager.getFIXDataDictionary(FIXVersion.FIX44).getDictionary()));
		
		// Create the cell editors
	    CellEditor[] editors = new CellEditor[MarketDataColumns.values().length];

	    // Column 1 : Completed (Checkbox)
	    editors[SYMBOL_COLUMN_INDEX] = new TextCellEditor(aMessageTable);

	    // Assign the cell editors to the viewer 
	    aMessagesViewer.setCellEditors(editors);
	    String[] columnProperties = new String[MarketDataColumns.values().length];
	    columnProperties[SYMBOL_COLUMN_INDEX] = MarketDataColumns.SYMBOL.toString();
	    aMessagesViewer.setColumnProperties(columnProperties);
	    
	    // Set the cell modifier for the viewer
	    aMessagesViewer.setCellModifier(new MarketDataCellModifier(this));

	    return aMessagesViewer;
	}

	@Override
	protected Enum<?>[] getEnumValues() {
		return MarketDataColumns.values();
	}

	@Override
	protected void initializeToolBar(IToolBarManager theToolBarManager) {
		symbolEntryText = new TextContributionItem(""); //$NON-NLS-1$
		if(marketDataTracker.getMarketDataFeedService() == null) {
			symbolEntryText.setEnabled(false);
		} else {
			FeedStatus feedStatus = marketDataTracker.getMarketDataFeedService().getFeedStatus();
			updateSymbolEntryTextFromFeedStatus(feedStatus);
		}
		marketDataTracker.addFeedEventListener(new MarketDataFeedTracker.FeedEventListener() {
			public void handleEvent(FeedStatus status) {
				if(symbolEntryText == null) {
					return;
				}
				updateSymbolEntryTextFromFeedStatus(status);
			}
		});
		theToolBarManager.add(symbolEntryText);
		theToolBarManager.add(new AddSymbolAction(symbolEntryText, this));
	}
	
	private void updateSymbolEntryTextFromFeedStatus(FeedStatus status) {
		if (symbolEntryText == null || symbolEntryText.isDisposed()) {
			return;
		}
		if(status == FeedStatus.AVAILABLE) {
			symbolEntryText.setEnabled(true);
		} else {
			symbolEntryText.setEnabled(false);
		}
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

	private void updateQuote(final Message quote) 
	{
	    try {
            mListLocker.executeWrite(new Callable<Object>() {
                public Object call() 
                    throws Exception
                {
                    EventList<MessageHolder> list = getInput();
                    int i = 0;
                    for (MessageHolder holder : list) {
                        Message message = holder.getMessage();
                        try {
                            String holderMessageSymbol = message.getString(Symbol.FIELD);
                            String quoteMessageSymbol = quote.getString(Symbol.FIELD);
                            if (holderMessageSymbol.equals(quoteMessageSymbol)) {
                                IncomingMessageHolder newHolder = new IncomingMessageHolder(quote);
                                newHolder.setToken(holder.getToken());
                                list.set(i, 
                                         newHolder);
                                getMessagesViewer().update(newHolder, 
                                                           null);
                            }               
                        } catch (FieldNotFound e) {
                            throw e;
                        }
                        i += 1;
                    }
                    return null;
                }	        
            });
        } catch (Throwable t) {
            PhotonPlugin.getMainConsoleLogger().error(t);
        }
	}

	class MarketDataCellModifier implements ICellModifier
	{
		public MarketDataCellModifier(MarketDataView view) {
		}

		public boolean canModify(Object element, String property) {
			return MarketDataColumns.SYMBOL.toString().equals(property);
		}

		public Object getValue(Object element, String property) {
			try {
				return ((MessageHolder)element).getMessage().getString(Symbol.FIELD);
			} catch (FieldNotFound e) {
				return ""; //$NON-NLS-1$
			}
		}

		public void modify(Object element, String property, Object value) {
			MarketDataFeedService<?> service = (MarketDataFeedService<?>) marketDataTracker.getMarketDataFeedService();
			if (service == null){
				PhotonPlugin.getMainConsoleLogger().warn(MISSING_QUOTE_FEED.getText());
				return;
			}
			MSymbol newSymbol = service.symbolFromString(value.toString());

			String stringValue = newSymbol.toString();
			if (listContains(stringValue)){
				return;
			}
			TableItem tableItem = (TableItem) element;
			MessageHolder messageHolder = (MessageHolder)tableItem.getData();
			Message message = messageHolder.getMessage();
			
			try {
				MSymbol symbol = service.symbolFromString(message.getString(Symbol.FIELD));
				doUnsubscribe(symbol);
			} catch (FieldNotFound fnf){}
			message.clear();
			if (stringValue.length()>0){
				MSymbol mSymbol = service.symbolFromString(stringValue);
				message.setField(new Symbol(stringValue));
				doSubscribe(mSymbol);
				messageHolder.setToken(doSubscribe(mSymbol));
			}
		}
	}


	private static final int BID_SIZE_INDEX = LAST_NORMAL_COLUMN_INDEX + 1;
	private static final int BID_INDEX = LAST_NORMAL_COLUMN_INDEX + 2;
	private static final int ASK_INDEX = LAST_NORMAL_COLUMN_INDEX + 3;
	private static final int ASK_SIZE_INDEX = LAST_NORMAL_COLUMN_INDEX + 4;

	
	class MarketDataTableFormat extends MessageListTableFormat {


		public MarketDataTableFormat(Table table, IWorkbenchPartSite site, DataDictionary dataDictionary) {
			super(table, MarketDataColumns.values(), site, dataDictionary);
		}

		@Override
		public String getColumnName(int index) {
			if (index == ZERO_WIDTH_COLUMN_INDEX) {
				return "";  //$NON-NLS-1$
			}
			if (index <= LAST_NORMAL_COLUMN_INDEX){
				return super.getColumnName(index);
			}
			switch(index){
			case BID_SIZE_INDEX:
				return BID_SZ_LABEL.getText();
			case BID_INDEX:
				return BID_LABEL.getText();
			case ASK_INDEX:
				return ASK_LABEL.getText();
			case ASK_SIZE_INDEX:
				return ASK_SZ_LABEL.getText();
			default:
				return ""; //$NON-NLS-1$
			}
		}

		@Override
		public String getColumnText(Object element, int index) {
			if (index == ZERO_WIDTH_COLUMN_INDEX) {
				return "";  //$NON-NLS-1$
			}
			return super.getColumnText(element, index);
		}
	}
	
	public void onAssertSymbol(MSymbol symbol) {
		addSymbol(symbol);
	}

	/**
	 * @param symbol
	 */
	public void addSymbol(MSymbol symbol) {

		if (hasSymbol(symbol)) {
			PhotonPlugin.getMainConsoleLogger().warn(DUPLICATE_SYMBOL.getText(symbol));
		} else {
			EventList<MessageHolder> list = getInput();

			Message message = new Message();
			message.setField(new Symbol(symbol.toString()));
			MessageHolder messageHolder = new MessageHolder(message);
			list.add(messageHolder);
			messageHolder.setToken(doSubscribe(symbol));
		}
	}
	
	private boolean hasSymbol(final MSymbol symbol) {
		EventList<MessageHolder> list = getInput();
			
		FilterList<MessageHolder> matches = new FilterList<MessageHolder>(list, 
				new Matcher<MessageHolder>() {
					public boolean matches(MessageHolder listItem) {
						try {
							String listSymbol = listItem.getMessage().getString(Symbol.FIELD).trim();
							return listSymbol.equals(symbol.getFullSymbol().trim());
						} catch (FieldNotFound e) {
							return false;
						}
					}
				});
		boolean rv = !matches.isEmpty();
		return rv;
	}

	public void removeItem(MessageHolder holder){
		MarketDataFeedService<?> service = (MarketDataFeedService<?>) marketDataTracker.getService();
		if (service == null){
			PhotonPlugin.getMainConsoleLogger().warn(MISSING_QUOTE_FEED.getText());
			return;
		}
		try {
			MSymbol mSymbol = service.symbolFromString(holder.getMessage().getString(Symbol.FIELD));
			removeSymbol(mSymbol);
		} catch (FieldNotFound e) {
			// do nothing
		}
	}
	
	public void removeSymbol(MSymbol symbol) {
		doUnsubscribe(symbol);

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

	private void doUnsubscribe(final MSymbol symbol) 
	{
	    doWaitCursor(getSite(),
	                 new Runnable() {
	        @Override
	        public void run()
	        {
	            IMarketDataFeedToken<?> token = tokenMap.remove(symbol);
	            if(token != null) {
	                PhotonPlugin.getMainConsoleLogger().debug(String.format("Unsubscribing to updates for %s", //$NON-NLS-1$
	                                                                        symbol));
	                token.cancel();
	            } else {
                        PhotonPlugin.getMainConsoleLogger().warn(CANNOT_UNSUBSCRIBE_NO_RECORD.getText(symbol));
	            }
	        }

	    });
	}
	
	private IMarketDataFeedToken<?> doSubscribe(final MSymbol symbol) 
	{
	    final MarketDataView theView = this;
		try {
	        return doWaitCursor(getSite(),
	                            new Callable<IMarketDataFeedToken<?>>() {
	            @Override 
	            public IMarketDataFeedToken<?> call()
	                throws Exception
	            {
	                MarketDataFeedService<?> service = (MarketDataFeedService<?>) marketDataTracker.getService();
	                IMarketDataFeedToken<?> token = null;
	                if (service == null) {
 			    PhotonPlugin.getMainConsoleLogger().warn(MISSING_QUOTE_FEED.getText());
	                } else {
	                    Message newSubscribeBBO = MarketDataUtils.newSubscribeBBO(symbol);
	                    // this odd try/catch accounts for the equally odd behavior of the id factory.
	                    //  it may throw an exception the first time around but will subsequently
	                    //  be capable of returning ids.  weird.
	                    String id;
	                    try {
	                        id = PhotonPlugin.getDefault().getIDFactory().getNext();
	                    } catch (Throwable t) {
	                        id = PhotonPlugin.getDefault().getIDFactory().getNext();
	                    }
	                    newSubscribeBBO.setField(new MDReqID(id));
	                    token = service.execute(newSubscribeBBO, 
                                                theView);
	                    tokenMap.put(symbol, 
	                                 token);
	                    getMessagesViewer().refresh();
	                }
	                return token;
	            }
	        });
		} catch (Exception e) {
			PhotonPlugin.getMainConsoleLogger().warn(CANNOT_SUBSCRIBE_TO_MARKET_DATA.getText(symbol),
                                                                 e);
			return null;
		}
	}

	public boolean isListeningSymbol(MSymbol symbol) {
		return false;
	}

	public void feedComponentChanged(IFeedComponent feedComponent) {
		
	}

	public boolean isInteresting(Object message) {
		
		return message instanceof HasFIXMessage
		|| (message instanceof Message && FIXMessageUtil.isMarketDataSnapshotFullRefresh((Message) message));
	}

	public void publishTo(Object aQuote) {
		Message message;
		if (aQuote instanceof HasFIXMessage){
			message = ((HasFIXMessage)aQuote).getMessage();
		} else {
			message = (Message)aQuote;
		}
		onQuote(message);
	}
	
    public void onQuote(final Message aQuote) {
        Display theDisplay = Display.getDefault();
        if (theDisplay.getThread() == Thread.currentThread()){
            updateQuote(aQuote);
        } else {			
            theDisplay.asyncExec(new Runnable() {
                public void run()
                {
                    if (!getMessagesViewer().getTable().isDisposed()) {
                        try {
                            updateQuote(aQuote);
                        } catch (Throwable t) {
                            PhotonPlugin.getMainConsoleLogger().error(t);
                        }
                    }
                }
            });
        }
    }
}
