package org.marketcetera.photon.views;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IWorkbenchPartSite;
import org.marketcetera.core.MSymbol;
import org.marketcetera.marketdata.ISubscription;
import org.marketcetera.photon.EclipseUtils;
import org.marketcetera.photon.IFieldIdentifier;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.marketdata.MarketDataFeedService;
import org.marketcetera.photon.marketdata.MarketDataFeedTracker;
import org.marketcetera.photon.marketdata.OptionInfoComponent;
import org.marketcetera.photon.marketdata.OptionMarketDataUtils;
import org.marketcetera.photon.marketdata.OptionMessageHolder;
import org.marketcetera.photon.marketdata.OptionMessageHolder.OptionPairKey;
import org.marketcetera.photon.ui.EnumTableFormat;
import org.marketcetera.photon.ui.EventListContentProvider;
import org.marketcetera.photon.ui.IndexedTableViewer;
import org.marketcetera.photon.ui.OptionMessageListTableFormat;
import org.marketcetera.photon.ui.TableComparatorChooser;
import org.marketcetera.quickfix.FIXDataDictionary;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.quickfix.FIXVersion;

import quickfix.FieldMap;
import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.MDEntryPx;
import quickfix.field.MDEntrySize;
import quickfix.field.MDEntryType;
import quickfix.field.MaturityMonthYear;
import quickfix.field.NoMDEntries;
import quickfix.field.NoRelatedSym;
import quickfix.field.StrikePrice;
import quickfix.field.Symbol;
import quickfix.fix44.DerivativeSecurityList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.impl.swt.SWTThreadProxyEventList;
import ca.odell.glazedlists.matchers.AbstractMatcherEditor;
import ca.odell.glazedlists.matchers.Matcher;
import ca.odell.glazedlists.util.concurrent.Lock;

/**
 * @author caroline.leung@softwaregoodness.com
 * @author michael.lossos@softwaregoodness.com
 */
public class OptionMessagesComposite extends Composite {

	public enum OptionDataColumns implements IFieldIdentifier {
		ZEROWIDTH(""), 
		CVOL("cVol", OptionInfoComponent.CALL_EXTRA_INFO, MDEntrySize.FIELD, NoMDEntries.FIELD, MDEntryType.FIELD, MDEntryType.TRADE_VOLUME), 
		CBIDSZ("cBidSz", OptionInfoComponent.CALL_MARKET_DATA, MDEntrySize.FIELD, NoMDEntries.FIELD, MDEntryType.FIELD, MDEntryType.BID), 
		CBID("cBid", OptionInfoComponent.CALL_MARKET_DATA, MDEntryPx.FIELD, NoMDEntries.FIELD, MDEntryType.FIELD, MDEntryType.BID),
		CASK("cAsk", OptionInfoComponent.CALL_MARKET_DATA, MDEntryPx.FIELD, NoMDEntries.FIELD, MDEntryType.FIELD, MDEntryType.OFFER),
		CASKSZ("cAskSz", OptionInfoComponent.CALL_MARKET_DATA, MDEntrySize.FIELD, NoMDEntries.FIELD, MDEntryType.FIELD, MDEntryType.OFFER),
		CSYM("cSym", OptionInfoComponent.CALL_EXTRA_INFO, Symbol.FIELD, null, null, null),
		STRIKE("Strike", OptionInfoComponent.STRIKE_INFO, StrikePrice.FIELD, null, null, null),
		EXP("Exp", OptionInfoComponent.STRIKE_INFO, MaturityMonthYear.FIELD, null, null, null),
		PSYM("pSym", OptionInfoComponent.PUT_EXTRA_INFO, Symbol.FIELD, null, null, null),
		PBIDSZ("pBidSz", OptionInfoComponent.PUT_MARKET_DATA, MDEntrySize.FIELD, NoMDEntries.FIELD, MDEntryType.FIELD, MDEntryType.BID), 
		PBID("pBid", OptionInfoComponent.PUT_MARKET_DATA, MDEntryPx.FIELD, NoMDEntries.FIELD, MDEntryType.FIELD, MDEntryType.BID),
		PASK("pAsk", OptionInfoComponent.PUT_MARKET_DATA, MDEntryPx.FIELD, NoMDEntries.FIELD, MDEntryType.FIELD, MDEntryType.OFFER),
		PASKSZ("pAskSz", OptionInfoComponent.PUT_MARKET_DATA, MDEntrySize.FIELD, NoMDEntries.FIELD, MDEntryType.FIELD, MDEntryType.OFFER),
		PVOL("pVol", OptionInfoComponent.PUT_EXTRA_INFO, MDEntrySize.FIELD, NoMDEntries.FIELD, MDEntryType.FIELD, MDEntryType.TRADE_VOLUME);


		private String name;
		private Integer fieldID;
		private Integer groupID;
		private Integer groupDiscriminatorID;
		private Object groupDiscriminatorValue;
		private OptionInfoComponent component;

		OptionDataColumns(String name){
			this.name = name;
		}

		OptionDataColumns(String name, OptionInfoComponent component, Integer fieldID, Integer groupID, Integer groupDiscriminatorID, Object groupDiscriminatorValue){
			this.name=name;
			this.component = component;
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

		public OptionInfoComponent getComponent() {
			return component;
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

	

	private Label errorLabel;
	private Table messageTable;
	private IndexedTableViewer messagesViewer;
	private EnumTableFormat<OptionMessageHolder> tableFormat;
	private TableComparatorChooser<OptionMessageHolder> chooser;
	private EventList<OptionMessageHolder> rawInputList;
	private final boolean sortableColumns;
	
	private final IWorkbenchPartSite site;
	private IMemento viewStateMemento; 

	private FIXMessageFactory messageFactory = FIXVersion.FIX44.getMessageFactory();
	
	private boolean showSingleLineOptionData;
	private OptionSymbolMatcherEditor optionSymbolMatcherEditor;
	private HashMap<MSymbol, OptionMessageHolder> optionSymbolToSeriesMap;

	
    public OptionMessagesComposite(Composite parent, IWorkbenchPartSite site, IMemento memento, boolean showSingleLineOptionData) {
    	super(parent, SWT.MULTI | SWT.FULL_SELECTION | SWT.VIRTUAL);
    	this.sortableColumns = true;   	
    	this.site = site;
    	this.viewStateMemento = memento;
    	this.showSingleLineOptionData = showSingleLineOptionData;
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
		optionSymbolToSeriesMap = new HashMap<MSymbol, OptionMessageHolder>();
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

	@SuppressWarnings("unchecked")
	private void createTable(Composite parent) {
        messageTable = createMessageTable(parent);
		messagesViewer = createTableViewer(messageTable);
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
    	    
	protected IndexedTableViewer createTableViewer(Table aMessageTable) {
		IndexedTableViewer aMessagesViewer = new IndexedTableViewer(
				aMessageTable);
		getSite().setSelectionProvider(aMessagesViewer);
		aMessagesViewer
				.setContentProvider(new EventListContentProvider<OptionMessageHolder>());
		FIXDataDictionary dictionary = PhotonPlugin.getDefault().getFIXDataDictionary();
		aMessagesViewer.setLabelProvider(
				new OptionMessageListTableFormat(
						aMessageTable, OptionDataColumns.values(), getSite(), dictionary.getDictionary()));

		return aMessagesViewer;
	}

	public IndexedTableViewer getMessagesViewer() {
		return messagesViewer;
	}
	
	public void setInput(EventList<OptionMessageHolder> input)
	{
		SortedList<OptionMessageHolder> sortedList = 
			new SortedList<OptionMessageHolder>(rawInputList = input);

		if (sortableColumns){
			if (chooser != null){
				chooser.dispose();
				chooser = null;
			}
			chooser = new TableComparatorChooser<OptionMessageHolder>(
								messageTable, 
								tableFormat,
								sortedList, true);
			chooser.disableSortOnColumnHeader();
			
			//Sort by Expiration date first, then by Strike 
			chooser.appendComparator(EXP_DATE_INDEX, 0, false);
			chooser.appendComparator(STRIKE_INDEX, 0, false);
		}
		EventList<?> inputList;
		if (showSingleLineOptionData){
			optionSymbolMatcherEditor = new OptionSymbolMatcherEditor();
			inputList = new FilterList<OptionMessageHolder>(sortedList, optionSymbolMatcherEditor);
		} else {
			inputList = sortedList;
		}
		if (!(input instanceof SWTThreadProxyEventList)){
			inputList = new SWTThreadProxyEventList(inputList, this.getDisplay());
		}
		messagesViewer.setInput(inputList);
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
		optionSymbolToSeriesMap.clear();
	}

	public void clear() {
		EventList<OptionMessageHolder> inputList = getInput();
		if (inputList != null && inputList.size() > 0) {
			getInput().clear();
		}
		clearDataMaps();
		getMessagesViewer().refresh();		
	}
	
	@Override
	public void dispose() {
		clearDataMaps();
		super.dispose();
	}

	public void handleDerivativeSecuritiyList(Message derivativeSecurityList, MarketDataFeedTracker marketDataTracker){
		clearDataMaps();
		
		MarketDataFeedService feed = marketDataTracker.getMarketDataFeedService();
		try {
			int numDerivs = 0;
			if (derivativeSecurityList.isSetField(NoRelatedSym.FIELD)){
				numDerivs = derivativeSecurityList.getInt(NoRelatedSym.FIELD);
			}
			HashMap<OptionPairKey, OptionMessageHolder> optionContractMap = new HashMap<OptionPairKey, OptionMessageHolder>();
			HashMap<MSymbol, OptionPairKey> optionSymbolToKeyMap = new HashMap<MSymbol, OptionPairKey>();

			for (int i = 1; i <= numDerivs; i++)
			{
				try {
					DerivativeSecurityList.NoRelatedSym info = new DerivativeSecurityList.NoRelatedSym();
					derivativeSecurityList.getGroup(i, info);

					int putOrCall = OptionMarketDataUtils.getOptionType(info);
					String optionSymbolString = info.getString(Symbol.FIELD);
					MSymbol optionSymbol = feed.symbolFromString(optionSymbolString);
					OptionPairKey optionKey;
						optionKey = OptionPairKey.fromFieldMap(optionSymbol, info);
					optionSymbolToKeyMap.put(optionSymbol, optionKey);
					OptionMessageHolder holder;
					if (optionContractMap.containsKey(optionKey)){
						holder = optionContractMap.get(optionKey);
					} else {
						holder = new OptionMessageHolder(OptionMarketDataUtils.getOptionRootSymbol(optionSymbolString), info);
						optionContractMap.put(optionKey, holder);

						Lock writeLock = rawInputList.getReadWriteLock().writeLock();
						try {
							writeLock.lock();
							rawInputList.add(holder);
						} finally {
							writeLock.lock();
						}
					}						
					
					holder.setExtraInfo(putOrCall, info);
					optionSymbolToSeriesMap.put(optionSymbol, holder);
				} catch (ParseException e) {
					MSymbol underlying =feed.symbolFromString(derivativeSecurityList.getString(Symbol.FIELD));
					PhotonPlugin.getDefault().getMarketDataLogger().error("Exception parsing option info", e);
					enableErrorLabelText("Error getting option contracts data for " + underlying.getBaseSymbol());
				}
			}
			disableErrorLabelText();
			getMessagesViewer().refresh();
		} catch (FieldNotFound e) {
			PhotonPlugin.getDefault().getMarketDataLogger().error("Exception parsing option info", e);
			enableErrorLabelText("Exception parsing option info - " + e);
		}
	}

	
	public void handleQuote(Message marketDataRefresh){
		String symbol;
		try {
			symbol = marketDataRefresh.getString(Symbol.FIELD);
			OptionMessageHolder line = optionSymbolToSeriesMap.get(new MSymbol(symbol));
			if (line != null){
				
				FieldMap existingMessage = line.getMarketDataForSymbol(symbol);
				if (FIXMessageUtil.isMarketDataIncrementalRefresh(marketDataRefresh)
						&& existingMessage != null){
					FIXMessageUtil.mergeMarketDataMessages(marketDataRefresh, 
							(Message)existingMessage, messageFactory);
				} else {
					existingMessage = marketDataRefresh;
				}
				int putOrCall = line.symbolOptionType(symbol);
				line.setMarketData(putOrCall, existingMessage);		
				
				getMessagesViewer().update(line, null);
			} else {
				PhotonPlugin.getDefault().getMarketDataLogger().debug("Unknown symbol: "+symbol);
			}
		} catch (FieldNotFound e) {
			PhotonPlugin.getDefault().getMarketDataLogger().debug("Symbol missing from option quote.");
		}
	}

	public MSymbol getFilterOptionContractSymbol() {
		return this.optionSymbolMatcherEditor == null ? null : optionSymbolMatcherEditor.getFilterSymbol();
	}

	public void setFilterOptionContractSymbol(MSymbol filterOptionContractSymbol) {
		if (optionSymbolMatcherEditor != null){
			optionSymbolMatcherEditor.setFilterSymbol(filterOptionContractSymbol);
		}
	}
	
	
	private class OptionSymbolMatcherEditor extends AbstractMatcherEditor<OptionMessageHolder>{

		private MSymbol filterSymbol;

		public void setFilterSymbol(final MSymbol filterSymbol){
			this.filterSymbol = filterSymbol;
			fireChanged(new Matcher<OptionMessageHolder>() {
				public boolean matches(OptionMessageHolder item) {
					return filterSymbol != null && item != null && item.getExtraInfoForSymbol(filterSymbol.toString())!=null;
				}
			});
		}

		public MSymbol getFilterSymbol() {
			return filterSymbol;
		}
		
	}
	
}
