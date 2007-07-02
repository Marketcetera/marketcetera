package org.marketcetera.photon.views;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.SortedList;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IWorkbenchPartSite;
import org.marketcetera.core.MSymbol;
import org.marketcetera.core.MarketceteraException;
import org.marketcetera.photon.EclipseUtils;
import org.marketcetera.photon.IFieldIdentifier;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.marketdata.*;
import org.marketcetera.photon.marketdata.OptionMessageHolder.OptionPairKey;
import org.marketcetera.photon.ui.*;
import org.marketcetera.quickfix.FIXDataDictionary;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.quickfix.FIXVersion;
import quickfix.DataDictionary;
import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.*;

import java.util.*;
import java.util.List;

/**
 * @author caroline.leung@softwaregoodness.com
 * @author michael.lossos@softwaregoodness.com
 */
public class OptionMessagesComposite extends Composite {

	public enum OptionDataColumns implements IFieldIdentifier {
		ZEROWIDTH(""), 
		CVOL("cVol", MDEntrySize.FIELD, NoMDEntries.FIELD, MDEntryType.FIELD, MDEntryType.TRADE_VOLUME), 
		CBIDSZ("cBidSz", MDEntrySize.FIELD, NoMDEntries.FIELD, MDEntryType.FIELD, MDEntryType.BID), 
		CBID("cBid", MDEntryPx.FIELD, NoMDEntries.FIELD, MDEntryType.FIELD, MDEntryType.BID),
		CASK("cAsk", MDEntryPx.FIELD, NoMDEntries.FIELD, MDEntryType.FIELD, MDEntryType.OFFER),
		CASKSZ("cAskSz", MDEntrySize.FIELD, NoMDEntries.FIELD, MDEntryType.FIELD, MDEntryType.OFFER),
		CSYM("cSym", Symbol.FIELD, null, null, null),
		STRIKE("Strike", StrikePrice.FIELD, null, null, null),
		EXP("Exp", MaturityMonthYear.FIELD, null, null, null),
		PSYM("pSym", Symbol.FIELD, null, null, null),
		PBIDSZ("pBidSz", MDEntrySize.FIELD, NoMDEntries.FIELD, MDEntryType.FIELD, MDEntryType.BID), 
		PBID("pBid", MDEntryPx.FIELD, NoMDEntries.FIELD, MDEntryType.FIELD, MDEntryType.BID),
		PASK("pAsk", MDEntryPx.FIELD, NoMDEntries.FIELD, MDEntryType.FIELD, MDEntryType.OFFER),
		PASKSZ("pAskSz", MDEntrySize.FIELD, NoMDEntries.FIELD, MDEntryType.FIELD, MDEntryType.OFFER),
		PVOL("pVol", MDEntrySize.FIELD, NoMDEntries.FIELD, MDEntryType.FIELD, MDEntryType.TRADE_VOLUME);


		private String name;
		private Integer fieldID;
		private Integer groupID;
		private Integer groupDiscriminatorID;
		private Object groupDiscriminatorValue;

		OptionDataColumns(String name){
			this.name = name;
		}

		OptionDataColumns(String name, Integer fieldID, Integer groupID, Integer groupDiscriminatorID, Object groupDiscriminatorValue){
			this.name=name;
			this.fieldID = fieldID;
			this.groupID = groupID;
			this.groupDiscriminatorID = groupDiscriminatorID;
			this.groupDiscriminatorValue = groupDiscriminatorValue;
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
	}
	
	public static final String COLUMN_ORDER_KEY = "COLUMN_ORDER";  //$NON-NLS-1$
	public static final String COLUMN_ORDER_DELIMITER = ",";  //$NON-NLS-1$
	public static final String SORT_BY_COLUMN_KEY = "SORT_BY_COLUMN";  //$NON-NLS-1$

	public static final int FIRST_PUT_DATA_COLUMN_INDEX = 9;
	private static final int ZERO_WIDTH_COLUMN_INDEX = 0;
	private static final int LAST_NORMAL_COLUMN_INDEX = ZERO_WIDTH_COLUMN_INDEX;

	
	private static final int CALL_VOLUME_INDEX = LAST_NORMAL_COLUMN_INDEX + 1;
	private static final int CALL_BID_SIZE_INDEX = LAST_NORMAL_COLUMN_INDEX + 2;
	private static final int CALL_BID_PRICE_INDEX = LAST_NORMAL_COLUMN_INDEX + 3;
	private static final int CALL_ASK_PRICE_INDEX = LAST_NORMAL_COLUMN_INDEX + 4;
	private static final int CALL_ASK_SIZE_INDEX = LAST_NORMAL_COLUMN_INDEX + 5;
	private static final int CALL_SYMBOL_INDEX = LAST_NORMAL_COLUMN_INDEX + 6;
	public static final int STRIKE_INDEX = LAST_NORMAL_COLUMN_INDEX + 7;
	public static final int EXP_DATE_INDEX = LAST_NORMAL_COLUMN_INDEX + 8;
	private static final int PUT_SYMBOL_INDEX = LAST_NORMAL_COLUMN_INDEX + 9;
	private static final int PUT_BID_SIZE_INDEX = LAST_NORMAL_COLUMN_INDEX + 10;
	private static final int PUT_BID_PRICE_INDEX = LAST_NORMAL_COLUMN_INDEX + 11;
	private static final int PUT_ASK_PRICE_INDEX = LAST_NORMAL_COLUMN_INDEX + 12;
	private static final int PUT_ASK_SIZE_INDEX = LAST_NORMAL_COLUMN_INDEX + 13;
	private static final int PUT_VOLUME_INDEX = LAST_NORMAL_COLUMN_INDEX + 14;

	
	class MarketDataTableFormat extends OptionMessageListTableFormat {

		public MarketDataTableFormat(Table table, IWorkbenchPartSite site, DataDictionary dictionary) {
			super(table, OptionDataColumns.values(), site, dictionary);
		}

		@Override
		public String getColumnName(int index) {
			if (index == ZERO_WIDTH_COLUMN_INDEX) {
				return ""; //$NON-NLS-1$
			}
			if (index <= LAST_NORMAL_COLUMN_INDEX) {
				return super.getColumnName(index);
			}
			switch (index) {
			case CALL_VOLUME_INDEX:
				return "cVol";
			case CALL_BID_SIZE_INDEX:
				return "cBidSz";
			case CALL_BID_PRICE_INDEX:
				return "cBid";
			case CALL_ASK_PRICE_INDEX:
				return "cAsk";
			case CALL_ASK_SIZE_INDEX:
				return "cAskSz";
			case CALL_SYMBOL_INDEX:
				return "cSym";
			case STRIKE_INDEX:
				return "Strike";
			case EXP_DATE_INDEX:
				return "Exp";
			case PUT_SYMBOL_INDEX:
				return "pSym";
			case PUT_BID_SIZE_INDEX:
				return "pBidSz";
			case PUT_BID_PRICE_INDEX:
				return "pBid";
			case PUT_ASK_PRICE_INDEX:
				return "pAsk";
			case PUT_ASK_SIZE_INDEX:
				return "pAskSz";
			case PUT_VOLUME_INDEX:
				return "pVol";
			default:
				return "";
			}
		}

		@Override
		public String getColumnText(Object element, int index) {
			if (index == ZERO_WIDTH_COLUMN_INDEX) {
				return ""; //$NON-NLS-1$
			}
			return super.getColumnText(element, index);
		}
	}

	private Label errorLabel;
	private Table messageTable;
	private IndexedTableViewer messagesViewer;
	private EnumTableFormat<OptionMessageHolder> tableFormat;
	private TableComparatorChooser<OptionMessageHolder> chooser;
	private EventList<OptionMessageHolder> rawInputList;
	private final boolean sortableColumns;
	
	private final IWorkbenchPartSite site;
	private IMemento viewStateMemento; 

	private HashMap<OptionPairKey, OptionMessageHolder> optionContractMap;
	private HashMap<String, OptionPairKey> optionSymbolToKeyMap;
	private HashMap<String, Boolean> optionSymbolToSideMap;

	public OptionMessagesComposite(Composite parent, IWorkbenchPartSite site, IMemento memento) {
		this(parent, site, memento, true);
	}
	
    public OptionMessagesComposite(Composite parent, IWorkbenchPartSite site, IMemento memento, boolean sortableColumns) {
    	super(parent, SWT.MULTI | SWT.FULL_SELECTION | SWT.VIRTUAL);
    	this.sortableColumns = sortableColumns;   	
    	this.site = site;
    	this.viewStateMemento = memento;
    	createErrorLabel(this);
		createTable(this);
		this.setLayout(createBasicGridLayout(1));
		initializeDataMaps();
    }

	private GridLayout createBasicGridLayout(int numColumns) {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = numColumns;
		gridLayout.marginWidth = 2;
		gridLayout.verticalSpacing = 0;
		gridLayout.horizontalSpacing = 0;
		gridLayout.marginHeight = 0;
		return gridLayout;
	}

	private void initializeDataMaps() {
		optionSymbolToKeyMap = new HashMap<String, OptionPairKey>();
		optionSymbolToSideMap = new HashMap<String, Boolean>();
		optionContractMap = new HashMap<OptionPairKey, OptionMessageHolder>();
	}

	protected void formatTable(Table messageTable) {
        messageTable.getVerticalBar().setEnabled(true);
        messageTable.setForeground(
        		messageTable.getDisplay().getSystemColor(
						SWT.COLOR_INFO_FOREGROUND));

        messageTable.setHeaderVisible(true);

		for (int i = 0; i < messageTable.getColumnCount(); i++) {
			messageTable.getColumn(i).setMoveable(true);
		}
    }

	private void createTable(Composite parent) {
        messageTable = createMessageTable(parent);
		messagesViewer = createTableViewer(messageTable, getEnumValues());
		tableFormat = (EnumTableFormat<OptionMessageHolder>)messagesViewer.getLabelProvider();
		formatTable(messageTable);
		packColumns(messageTable);
		restoreColumnOrder(viewStateMemento);		
	}
	
	private void createErrorLabel(Composite parent) {
		errorLabel = new Label(parent, SWT.WRAP);
		errorLabel.setForeground(getDisplay().getSystemColor(SWT.COLOR_RED));
		errorLabel.setEnabled(false);
        GridData labelGridData = new GridData();
        labelGridData.horizontalSpan = 1;
        labelGridData.verticalSpan = 1;
        labelGridData.horizontalAlignment = GridData.BEGINNING;
        labelGridData.verticalAlignment = GridData.END;
        labelGridData.grabExcessHorizontalSpace = true;
        errorLabel.setLayoutData(labelGridData);

	}
	
	private void disableErrorLabelText() {
		errorLabel.setText("");
		errorLabel.pack();
		errorLabel.setEnabled(false);
	}
	
	private void enableErrorLabelText(String text) {
		errorLabel.setText(text);
		errorLabel.pack();
		errorLabel.setEnabled(true);
	}
		
	protected Enum[] getEnumValues() {
		return OptionDataColumns.values();
	}
	
	// cl todo: duplicated code from MarketDataView, need to refactor
	public void packColumns(final Table table) {
		TableColumn zeroFirstColumn = table.getColumn(ZERO_WIDTH_COLUMN_INDEX);
		zeroFirstColumn.setWidth(0);
		zeroFirstColumn.setResizable(false);
		zeroFirstColumn.setMoveable(false);
		zeroFirstColumn.setText("");
		zeroFirstColumn.setImage(null);
		for (int i = 1; i < table.getColumnCount(); i++) {
			table.getColumn(i).setWidth(getTableColumnWidth(i));
		}
	}

	private int getColumnWidth(int width) {
		return EclipseUtils.getTextAreaSize(messageTable, null, width, 1.0).x;

	}
	private int getTableColumnWidth(int index) {
		switch (index) {
		case CALL_VOLUME_INDEX:
			return getColumnWidth(10);
		case CALL_BID_SIZE_INDEX:
			return getColumnWidth(10);
		case CALL_BID_PRICE_INDEX:
			return getColumnWidth(10);
		case CALL_ASK_PRICE_INDEX:
			return getColumnWidth(10);
		case CALL_ASK_SIZE_INDEX:
			return getColumnWidth(10);
		case CALL_SYMBOL_INDEX:
			return getColumnWidth(12);
		case STRIKE_INDEX:
			return getColumnWidth(10);
		case EXP_DATE_INDEX:
			return getColumnWidth(12);
		case PUT_SYMBOL_INDEX:
			return getColumnWidth(12);
		case PUT_BID_SIZE_INDEX:
			return getColumnWidth(10);
		case PUT_BID_PRICE_INDEX:
			return getColumnWidth(10);
		case PUT_ASK_PRICE_INDEX:
			return getColumnWidth(10);
		case PUT_ASK_SIZE_INDEX:
			return getColumnWidth(10);
		case PUT_VOLUME_INDEX:
			return getColumnWidth(10);
		default:
			return getColumnWidth(10);
		}
	}
 
	public void saveTableState(IMemento memento) {		
		saveColumnOrder(memento);
		saveSortByColumn(memento);
	}

	protected String serializeColumnOrder(int[] columnOrder) {
		StringBuilder sb = new StringBuilder();
		for(int columnNumber : columnOrder) {
			sb.append(columnNumber);
			sb.append(COLUMN_ORDER_DELIMITER);
		}
		return sb.toString();
	}
	
	protected int[] deserializeColumnOrder(String delimitedValue) {
		if (delimitedValue == null) {
			return new int[0];
		}
		String[] columnNumbers = delimitedValue.split(COLUMN_ORDER_DELIMITER);
		if (columnNumbers == null || columnNumbers.length == 0) {
			return new int[0];
		}
		int[] columnOrder = new int[columnNumbers.length];
		for(int index = 0; index < columnOrder.length; ++index)  {
			try {
				columnOrder[index] = Integer.parseInt(columnNumbers[index]);
			}
			catch(Exception anyException) {
				// TODO Log?
				// org.marketcetera.photon.PhotonPlugin.getMainConsoleLogger().warn("Failed to load column order.", anyException);
				return new int[0];
			}
		}
		return columnOrder;
	}
	
	private void saveColumnOrder(IMemento memento) {
		if (memento == null) 
			return;
		int[] columnOrder = messageTable.getColumnOrder();
		String serializedColumnOrder = serializeColumnOrder(columnOrder);
		memento.putString(COLUMN_ORDER_KEY, serializedColumnOrder);
	}
	
	private void restoreColumnOrder(IMemento memento) {
		try {
			if (memento == null)
				return;
			String delimitedColumnOrder = memento.getString(COLUMN_ORDER_KEY);
			int[] columnOrder = deserializeColumnOrder(delimitedColumnOrder);
			if(columnOrder != null && columnOrder.length > 0) {
				messageTable.setColumnOrder(columnOrder);
			}
		} catch (Throwable t){
			// do nothing
		}
	}

	private void restoreSortByColumn(IMemento memento) {
		if (memento == null)
			return;
		String sortByColumn = memento.getString(SORT_BY_COLUMN_KEY);
		if (sortByColumn != null && sortByColumn.length() > 0 && chooser != null)
		{
			chooser.fromString(sortByColumn);
			List<Integer> sortingCols = chooser.getSortingColumns();
			for (int col : sortingCols) {
				chooser.updateSortIndicatorIcon(col);
			}
		}
	}
	
	private void saveSortByColumn(IMemento memento) {
		if (memento == null) 
			return;
		memento.putString(SORT_BY_COLUMN_KEY, chooser.toString());
	}
	
    protected Table createMessageTable(Composite parent) {
        Table messageTable = new Table(parent, SWT.MULTI | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.BORDER);
        GridData messageTableLayout = new GridData();
        messageTableLayout.horizontalSpan = 2;
        messageTableLayout.verticalSpan = 1;
        messageTableLayout.horizontalAlignment = GridData.FILL;
        messageTableLayout.verticalAlignment = GridData.FILL;
        messageTableLayout.grabExcessHorizontalSpace = true;
        messageTableLayout.grabExcessVerticalSpace = true;
        messageTable.setLayoutData(messageTableLayout);
        return messageTable;
    }
    	    
	protected IndexedTableViewer createTableViewer(Table aMessageTable,
			Enum[] enums) {
		IndexedTableViewer aMessagesViewer = new IndexedTableViewer(
				aMessageTable);
		getSite().setSelectionProvider(aMessagesViewer);
		aMessagesViewer
				.setContentProvider(new EventListContentProvider<OptionMessageHolder>());
		FIXDataDictionary dictionary = PhotonPlugin.getDefault().getFIXDataDictionary();
		aMessagesViewer.setLabelProvider(new MarketDataTableFormat(
				aMessageTable, getSite(), dictionary.getDictionary()));

		return aMessagesViewer;
	}

	public IndexedTableViewer getMessagesViewer() {
		return messagesViewer;
	}
	
	public void setInput(EventList<OptionMessageHolder> input)
	{
		SortedList<OptionMessageHolder> extractedList = 
			new SortedList<OptionMessageHolder>(rawInputList = input);

		if (sortableColumns){
			if (chooser != null){
				chooser.dispose();
				chooser = null;
			}
			chooser = new TableComparatorChooser<OptionMessageHolder>(
								messageTable, 
								tableFormat,
								extractedList, false);
			restoreSortByColumn(viewStateMemento);
		}
		messagesViewer.setInput(extractedList);
	}
	
	public EventList<OptionMessageHolder> getInput()
	{
		return rawInputList;
	}

	protected IWorkbenchPartSite getSite() {
		return site;
	}


	public Table getMessageTable() {
		return messageTable;
	}
	
	protected void clearDataMaps() {
		optionContractMap.clear();
		optionSymbolToKeyMap.clear();
		optionSymbolToSideMap.clear();
	}

	public void unlistenAllMarketData(MarketDataFeedTracker marketDataTracker) {
		unsubscribeOptions(marketDataTracker);
		getInput().clear();
		clearDataMaps();
		getMessagesViewer().refresh();		
	}
	
	@Override
	public void dispose() {
		clearDataMaps();
		super.dispose();
	}
	
	protected void requestOptionSecurityList(
			final MarketDataFeedTracker marketDataTracker,
			final MSymbol underlyingSymbol) {
		requestOptionSecurityList(marketDataTracker, underlyingSymbol, null);
	}
	
	protected void requestOptionSecurityList(final MarketDataFeedTracker marketDataTracker,
			final MSymbol symbol, final String filteredByOptionSymbol) {

		MarketDataFeedService service = marketDataTracker.getMarketDataFeedService();

		IMarketDataListCallback callback = createMarketDataListCallback(
				marketDataTracker, symbol, filteredByOptionSymbol);

		Message query = null;

		//Returns a query for all option contracts for the underlying symbol 
		//symbol = underlyingSymbol  (e.g. MSFT)
		if (showAllOptions(filteredByOptionSymbol)) {			
			query = OptionMarketDataUtils.newRelatedOptionsQuery(symbol, false);		
		} 			
		//Returns a query for all the option contracts for the option root 			
		//symbol = optionRoot (e.g. MSQ)
		else {  			
			query = OptionMarketDataUtils.newOptionRootQuery(symbol, false);
		}
		MarketDataUtils.asyncMarketDataQuery(symbol, query, service
				.getMarketDataFeed(), callback);
	}
	
	private boolean showAllOptions(String filteredByOptionSymbol) {
		return filteredByOptionSymbol == null;
	}
	

	private IMarketDataListCallback createMarketDataListCallback(
			final MarketDataFeedTracker marketDataTracker,
			final MSymbol underlyingSymbol, final String filteredByOptionSymbol) {
		IMarketDataListCallback callback = new IMarketDataListCallback() {

			public void onMarketDataFailure(MSymbol symbol) {
				// do nothing
			}

            public void onMessage(Message aMessage) {
                onMessages(new Message[]{aMessage});
            }

            public void onMessages(Message[] derivativeSecurityList) {
            	if(isDisposed()) {
            		return;
            	}
				List<OptionContractData> optionContracts = new ArrayList<OptionContractData>();

				try {					
					// underlying symbol case
					if (showAllOptions(filteredByOptionSymbol)) {
						optionContracts = OptionMarketDataUtils.getOptionExpirationMarketData(null,
								derivativeSecurityList);
					} else {
					// option root case
						optionContracts = OptionMarketDataUtils.getOptionExpirationMarketData(
								underlyingSymbol.getBaseSymbol(),
								derivativeSecurityList);						
					}
				} catch (Exception anyException) {
					PhotonPlugin.getMainConsoleLogger().warn("Error getting market data - ", anyException);
					enableErrorLabelText("Error getting option contracts data for " + underlyingSymbol.getBaseSymbol());
					return;
				}
				
				disableErrorLabelText();
				if (optionContracts == null || optionContracts.isEmpty()) {
					// do nothing
				} else {
					EventList<OptionMessageHolder> list = getInput();

					for (OptionContractData data : optionContracts) {
						MSymbol optionSymbol = data.getOptionSymbol();

						// construct the option key
						OptionPairKey optionKey = new OptionPairKey(
								underlyingSymbol.getBaseSymbol(),
								OptionMarketDataUtils.getOptionRootSymbol(
										optionSymbol).getBaseSymbol(), data
										.getExpirationYear(), data
										.getExpirationMonth(), data
										.getStrikePrice());

						Message callMessage = new Message();
						Message putMessage = new Message();

						if (data.isPut()) {
							subscribeOption(optionSymbol, putMessage);

							// Since OptionPairKey does not track put/call
							// option on purpose, need to track this separately with a map
							getOptionSymbolToSideMap().put(optionSymbol
									.getBaseSymbol(), true);
						} else {
							subscribeOption(optionSymbol, callMessage);
							getOptionSymbolToSideMap().put(optionSymbol
									.getBaseSymbol(), false);
						}

						optionSymbolToKeyMap.put(optionSymbol.getBaseSymbol(),
								optionKey);
						updateOptionContractMap(data.isPut(), optionKey,
								callMessage, putMessage);
					}

					Set<OptionPairKey> optionKeys = getOptionContractMap().keySet();
					
					String callSymbolString = null;
					String putSymbolString = null;
					
					for (OptionPairKey optionPairKey : optionKeys) {
						OptionMessageHolder optionMessageHolder = getOptionContractMap()
								.get(optionPairKey);
						
						if (showAllOptions(filteredByOptionSymbol)) {
							list.add(optionMessageHolder);						
							continue;
						}
							
						callSymbolString = getSymbol(optionMessageHolder.getCallMessage());
						putSymbolString = getSymbol(optionMessageHolder.getPutMessage());
						 
						if (filteredByOptionSymbol
										.equalsIgnoreCase(callSymbolString)
								|| filteredByOptionSymbol
										.equalsIgnoreCase(putSymbolString)) {
							list.clear();
							list.add(optionMessageHolder);
						}						
					}
				}
			}

			private void subscribeOption(MSymbol optionSymbol, Message message) {
				message.setField(new Symbol(optionSymbol.getBaseSymbol()));
				try {
					marketDataTracker.simpleSubscribe(optionSymbol);
				} catch (MarketceteraException e) {
					PhotonPlugin.getMainConsoleLogger().warn(
							"Error subscribing to quotes for " + optionSymbol);
				}
			}
		};
		return callback;
	}
			
	private void updateOptionContractMap(boolean isPut,
			OptionPairKey optionKey, Message callMessage, Message putMessage) {
		OptionMessageHolder newHolder = null;

		// lining up the call and put options
		if (getOptionContractMap().containsKey(optionKey)) {
			OptionMessageHolder holder = getOptionContractMap().get(optionKey);
			if (isPut) {
				newHolder = new OptionMessageHolder(optionKey, holder
						.getCallMessage(), putMessage);
			} else {
				newHolder = new OptionMessageHolder(optionKey, callMessage,
						holder.getPutMessage());
			}
		} else {
			newHolder = new OptionMessageHolder(optionKey, callMessage,
					putMessage);
		}
		getOptionContractMap().put(optionKey, newHolder);
	}

	
	public void updateQuote(Message quote) {
		Display theDisplay = Display.getDefault();
		if (theDisplay.getThread() == Thread.currentThread()) {
			OptionMessageHolder newHolder = null;
			OptionPairKey key = optionSymbolToKeyMap.get(getSymbol(quote));
			if (key != null) {
				OptionMessageHolder holder = optionContractMap.get(key);
				
				EventList<OptionMessageHolder> list = getInput();
				int index = list.indexOf(holder);

				
				boolean isIncrementalRefresh = FIXMessageUtil
						.isMarketDataIncrementalRefresh(quote);
				
				boolean isPut = isPut(holder, quote);
				FIXVersion fixVersion = PhotonPlugin.getDefault().getFIXVersion();
				FIXMessageFactory messageFactory = fixVersion.getMessageFactory();
				
				
				if (isPut) {
					if (isIncrementalRefresh) {
						System.out.println("Incremental");
						FIXMessageUtil.mergeMarketDataMessages(quote, holder
								.getPutMessage(), messageFactory);
					}
					newHolder = new OptionMessageHolder(key, holder
							.getCallMessage(), quote);
				} else {  
					//isCall
					if (isIncrementalRefresh) {
						System.out.println("Incremental");
						FIXMessageUtil.mergeMarketDataMessages(quote, holder
								.getCallMessage(), messageFactory);
					}
					newHolder = new OptionMessageHolder(key, quote, holder
							.getPutMessage());
				}
				optionContractMap.put(key, newHolder);
				if (index >= 0) {
					list.set(index, newHolder);
					getMessagesViewer().update(newHolder, null);
				}
			}
		}
	}
	
	protected void unsubscribeOptions(MarketDataFeedTracker marketDataTracker) {
		 MarketDataFeedService service = marketDataTracker
				.getMarketDataFeedService();
		Set<String> contractSymbols = getOptionSymbolToKeyMap().keySet();
		MSymbol contractSymbolToUnsubscribe = null;
		for (String optionSymbol : contractSymbols) {
			contractSymbolToUnsubscribe = service
					.symbolFromString(optionSymbol);
			marketDataTracker.simpleUnsubscribe(contractSymbolToUnsubscribe);
		}
	}

	private boolean isPut(OptionMessageHolder holder, Message quote) {
		Boolean isPut = optionSymbolToSideMap.get(getSymbol(quote));
		if (isPut != null)
			return isPut;
		return false;
	}

	private static String getSymbol(Message message) {
		try {
			return message.getString(Symbol.FIELD).trim();
		} catch (FieldNotFound e) {
			return null;
		}
	}


	protected HashMap<OptionPairKey, OptionMessageHolder> getOptionContractMap() {
		return optionContractMap;
	}


	protected HashMap<String, OptionPairKey> getOptionSymbolToKeyMap() {
		return optionSymbolToKeyMap;
	}


	protected HashMap<String, Boolean> getOptionSymbolToSideMap() {
		return optionSymbolToSideMap;
	}
	
	
	
}
