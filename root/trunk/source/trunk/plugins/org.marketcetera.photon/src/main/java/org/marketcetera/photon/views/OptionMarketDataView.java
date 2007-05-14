package org.marketcetera.photon.views;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.marketcetera.core.MSymbol;
import org.marketcetera.core.MarketceteraException;
import org.marketcetera.marketdata.MarketDataListener;
import org.marketcetera.photon.IFieldIdentifier;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.marketdata.IMarketDataListCallback;
import org.marketcetera.photon.marketdata.MarketDataFeedService;
import org.marketcetera.photon.marketdata.MarketDataFeedTracker;
import org.marketcetera.photon.marketdata.MarketDataUtils;
import org.marketcetera.photon.marketdata.OptionContractData;
import org.marketcetera.photon.marketdata.OptionMarketDataUtils;
import org.marketcetera.photon.marketdata.OptionMessageHolder;
import org.marketcetera.photon.marketdata.OptionMessageHolder.OptionPairKey;
import org.marketcetera.photon.ui.EventListContentProvider;
import org.marketcetera.photon.ui.IndexedTableViewer;
import org.marketcetera.photon.ui.OptionMessageListTableFormat;
import org.marketcetera.photon.ui.TextContributionItem;
import org.marketcetera.photon.views.UnderlyingSymbolInfo.UnderlyingSymbolDataFields;
import org.marketcetera.quickfix.FIXDataDictionaryManager;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXValueExtractor;
import org.marketcetera.quickfix.FIXVersion;

import quickfix.DataDictionary;
import quickfix.FieldMap;
import quickfix.FieldNotFound;
import quickfix.FieldType;
import quickfix.Message;
import quickfix.field.MDEntryPx;
import quickfix.field.MDEntrySize;
import quickfix.field.MDEntryType;
import quickfix.field.MaturityMonthYear;
import quickfix.field.NoMDEntries;
import quickfix.field.StrikePrice;
import quickfix.field.Symbol;
import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;

/**
 * Option market data view.
 * 
 * @author caroline.leung@softwaregoodness.com
 */
public class OptionMarketDataView extends OptionMessagesView  implements
		IMSymbolListener {

	public static final String ID = "org.marketcetera.photon.views.OptionMarketDataView";
	
	public static final int FIRST_PUT_DATA_COLUMN_INDEX = 9; 

	private static final int ZERO_WIDTH_COLUMN_INDEX = 0;

	private static final int LAST_NORMAL_COLUMN_INDEX = ZERO_WIDTH_COLUMN_INDEX;

	private FormToolkit formToolkit;

	private Section underlyingSymbolsSection;

	private Composite underlyingSymbolsContainer;

	private HashMap<String, UnderlyingSymbolInfo> underlyingSymbolInfoMap;

	private ScrolledForm form = null;
	
	private DataDictionary dictionary;
	private FIXValueExtractor extractor;

	private HashMap<String, OptionPairKey> optionSymbolToKeyMap;
	
	private HashMap<OptionPairKey, OptionMessageHolder> optionContractMap;
	
	private HashMap<String, Boolean> optionSymbolToSideMap;


	private static final DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

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

	private MarketDataFeedTracker marketDataTracker;

	public OptionMarketDataView() {
		super(true);
		marketDataTracker = new MarketDataFeedTracker(PhotonPlugin.getDefault()
				.getBundleContext());
		marketDataTracker.open();

		marketDataListener = new MDVMarketDataListener();
		marketDataTracker.setMarketDataListener(marketDataListener);
		initializeFixValueExtractor();
	}
	
	private void initializeFixValueExtractor()
	{
		dictionary = FIXDataDictionaryManager.getFIXDataDictionary(FIXVersion.FIX44).getDictionary();
		FIXMessageFactory messageFactory = FIXVersion.getFIXVersion(dictionary.getVersion()).getMessageFactory();
		extractor = new FIXValueExtractor(dictionary, messageFactory);
	}

	@Override
	public void createPartControl(Composite parent) {
		createForm(parent);
		createUnderlyingSymbolsSection();
		Composite tableExpandable = createDataTableSection();
		super.createPartControl(tableExpandable);
		this.setInput(new BasicEventList<OptionMessageHolder>());
		initializeDataMaps();
	}
	
	private void initializeDataMaps() {
		optionSymbolToKeyMap = new HashMap<String, OptionPairKey>();
		optionSymbolToSideMap = new HashMap<String, Boolean>();
		optionContractMap = new HashMap<OptionPairKey, OptionMessageHolder>();
		underlyingSymbolInfoMap = new HashMap<String, UnderlyingSymbolInfo>();  		
	}

	/**
	 * This method initializes formToolkit
	 * 
	 * @return org.eclipse.ui.forms.widgets.FormToolkit
	 */
	private FormToolkit getFormToolkit() {
		if (formToolkit == null) {
			formToolkit = new FormToolkit(Display.getCurrent());
		}
		return formToolkit;
	}

	private void createForm(Composite top) {
		form = getFormToolkit().createScrolledForm(top);
		form.setLayout(createBasicGridLayout(1));
		form.getBody().setLayout(createBasicGridLayout(1));
		form.getBody().setLayoutData(
				createTopAlignedHorizontallySpannedGridData());
	}

	private void createUnderlyingSymbolsSection() {
		underlyingSymbolsSection = getFormToolkit().createSection(
				form.getBody(), Section.EXPANDED | Section.NO_TITLE);
		underlyingSymbolsSection
				.setLayoutData(createTopAlignedHorizontallySpannedGridData());
		createUnderlyingSymbolsContainerComposite();
	}

	private void createUnderlyingSymbolsContainerComposite() {
		underlyingSymbolsContainer = getFormToolkit().createComposite(
				underlyingSymbolsSection, SWT.NONE);
		underlyingSymbolsContainer
				.setLayoutData(createTopAlignedHorizontallySpannedGridData());
		underlyingSymbolsContainer.setLayout(createBasicGridLayout(1));
		underlyingSymbolsSection.setClient(underlyingSymbolsContainer);	
	}
		
	private Composite createDataTableSection() {
		Section tableSection = getFormToolkit().createSection(
				form.getBody(), Section.EXPANDED | Section.NO_TITLE);
		tableSection.setLayout(createBasicGridLayout(1));
		GridData gridData1 = createTopAlignedHorizontallySpannedGridData();
		gridData1.grabExcessVerticalSpace = true;
		gridData1.verticalAlignment = GridData.FILL;
		tableSection.setLayoutData(gridData1);
		Composite tableComposite = getFormToolkit().createComposite(
				tableSection, SWT.NONE);
		tableComposite.setLayout(createBasicGridLayout(1));
		GridData gridData2 = createTopAlignedHorizontallySpannedGridData();
		gridData2.grabExcessVerticalSpace = true;
		gridData2.verticalAlignment = GridData.FILL;
		tableComposite.setLayoutData(gridData2);

		tableSection.setClient(tableComposite);
		return tableComposite;
	}

	private Composite createUnderlyingSymbolComposite(Composite parent) {
		Composite underlyingSymbolComposite = getFormToolkit().createComposite(
				parent, SWT.NONE);
		underlyingSymbolComposite.setLayout(createBasicGridLayout(1));
		underlyingSymbolComposite.setLayoutData(createTopAlignedHorizontallySpannedGridData());
		return underlyingSymbolComposite;
	}

	private void addUnderlyerInfo(String underlyingSymbol) {
		if (underlyingSymbolInfoMap.size() > 0) {
			Label separator = new Label(underlyingSymbolsContainer, SWT.SEPARATOR
					| SWT.HORIZONTAL);
			GridData gridData = new GridData(SWT.LEFT, SWT.CENTER, true, false);
			gridData.widthHint = 400;
			separator.setLayoutData(gridData);			
		}
		Composite underlyingSymbolComposite = createUnderlyingSymbolComposite(underlyingSymbolsContainer);
		underlyingSymbolInfoMap.put(underlyingSymbol, new UnderlyingSymbolInfo(underlyingSymbolComposite));
	}

	private GridData createTopAlignedHorizontallySpannedGridData() {
		GridData formGridData = new GridData();
		formGridData.grabExcessHorizontalSpace = true;
		formGridData.horizontalAlignment = GridData.FILL;
		// formGridData.grabExcessVerticalSpace = true;
		formGridData.verticalAlignment = GridData.FILL;
		return formGridData;
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

	@Override
	public void dispose() {
		marketDataTracker.setMarketDataListener(null);
		marketDataTracker.close();
		super.dispose();
	}
	
	private void disposeUnderlyerInfoSection() {
		Control[] children = underlyingSymbolsContainer.getChildren();
		for (Control child : children) {
			child.dispose();
		}
	}

	@Override
	protected void formatTable(Table messageTable) {
		messageTable.getVerticalBar().setEnabled(true);
		messageTable.setForeground(messageTable.getDisplay().getSystemColor(
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

	//cl todo: duplicated code from MarketDataView, need to refactor
	//cl todo: pack other columns as well
	@Override
	protected void packColumns(Table table) {
		super.packColumns(table);
		TableColumn zeroFirstColumn = table.getColumn(ZERO_WIDTH_COLUMN_INDEX);
		zeroFirstColumn.setWidth(0);
		zeroFirstColumn.setResizable(false);
		zeroFirstColumn.setMoveable(false);
		zeroFirstColumn.setText("");
		zeroFirstColumn.setImage(null);
	}

	@Override
	protected IndexedTableViewer createTableViewer(Table aMessageTable,
			Enum[] enums) {
		IndexedTableViewer aMessagesViewer = new IndexedTableViewer(
				aMessageTable);
		getSite().setSelectionProvider(aMessagesViewer);
		aMessagesViewer
				.setContentProvider(new EventListContentProvider<OptionMessageHolder>());
		aMessagesViewer.setLabelProvider(new MarketDataTableFormat(
				aMessageTable, getSite()));

		return aMessagesViewer;
	}

	@Override
	protected Enum[] getEnumValues() {
		return OptionDataColumns.values();
	}

	@Override
	protected void initializeToolBar(IToolBarManager theToolBarManager) {
		TextContributionItem textContributionItem = new TextContributionItem("");
		theToolBarManager.add(textContributionItem);
		theToolBarManager.add(new AddSymbolAction(textContributionItem, this));
	}

	/**
	 * Perform one of two tasks here. 
	 * 1. Update the underlying info on top if matching the underlying symbol
	 * 2. Update the call or put side in the MessagesTable if matching put/call contract in the table row
	 */
	private void updateQuote(Message quote) {
		if (matchUnderlyingSymbol(quote)) {
			updateUnderlyingSymbol(quote);
			return;
		}
		OptionMessageHolder newHolder = null;
		OptionPairKey key = optionSymbolToKeyMap.get(getSymbol(quote));
		if (key != null) {
			EventList<OptionMessageHolder> list = getInput();
			OptionMessageHolder holder = optionContractMap.get(key);
			int index = list.indexOf(holder);

			boolean isPut = isPut(holder, quote);
			if (isPut) {
				newHolder = new OptionMessageHolder(key, holder
						.getCallMessage(), quote);
			} else {
				newHolder = new OptionMessageHolder(key, quote, holder
						.getPutMessage());
			}
			optionContractMap.put(key, newHolder);
			list.set(index, newHolder);
			getMessagesViewer().update(newHolder, null);
		}
	}
	
	private boolean matchUnderlyingSymbol(Message quote)
	{
		String quoteSymbol = getSymbol(quote);
		UnderlyingSymbolInfo symbolInfo = underlyingSymbolInfoMap.get(quoteSymbol);
		return (symbolInfo != null);
	}
	
		
	private void updateUnderlyingSymbol(Message quote)
	{
		String quoteSymbol = getSymbol(quote);
		UnderlyingSymbolInfo symbolInfo = underlyingSymbolInfoMap.get(quoteSymbol);
		if (symbolInfo != null)
		{
			symbolInfo.setInstrumentLabelText(extractStockValue(UnderlyingSymbolDataFields.SYMBOL, quote).toString());
			symbolInfo.setLastPriceLabelText(extractStockValue(UnderlyingSymbolDataFields.LASTPX, quote));
			symbolInfo.setLastPriceChangeLabelText((String) extractStockValue(UnderlyingSymbolDataFields.LASTPX, quote));
			
			symbolInfo.setAskPriceLabelText(extractStockValue(
					UnderlyingSymbolInfo.UnderlyingSymbolDataFields.ASK, quote));
			symbolInfo.setAskSizeLabelText(extractStockValue(
					UnderlyingSymbolInfo.UnderlyingSymbolDataFields.ASKSZ, quote));

			symbolInfo.setBidPriceLabelText(extractStockValue(
					UnderlyingSymbolInfo.UnderlyingSymbolDataFields.BID, quote));
			symbolInfo.setBidSizeLabelText(extractStockValue(
					UnderlyingSymbolInfo.UnderlyingSymbolDataFields.BIDSZ, quote));

			//cl todo:retrieve dateAmountStrings
//			symbolInfo.setExDividendsDateAndAmountItems(dateAmountStrings);
		
			symbolInfo.setLastUpdatedTimeLabelText(extractStockValue(UnderlyingSymbolDataFields.LASTUPDATEDTIME, quote));
			symbolInfo.setOpenPriceLabelText(extractStockValue(UnderlyingSymbolDataFields.OPENPX, quote));
			symbolInfo.setOpenPriceLabelText(extractStockValue(UnderlyingSymbolDataFields.OPENPX, quote));
			symbolInfo.setHighPriceLabelText(extractStockValue(UnderlyingSymbolDataFields.HI, quote));
			symbolInfo.setLowPriceLabelText(extractStockValue(UnderlyingSymbolDataFields.LOW, quote));
			symbolInfo.setTradeValueLabelText(extractStockValue(UnderlyingSymbolDataFields.TRADEVOL, quote));
			underlyingSymbolsContainer.pack(true);
		}		
	}
		
	private boolean isPut(OptionMessageHolder holder, Message quote) {
		Boolean isPut = optionSymbolToSideMap.get(getSymbol(quote));
		if (isPut != null)
			return isPut;
		return false;		
	}

	public void onQuote(final Message aQuote) {
		Display theDisplay = Display.getDefault();
		if (theDisplay.getThread() == Thread.currentThread()) {
			updateQuote(aQuote);
		} else {
			theDisplay.asyncExec(new Runnable() {
				public void run() {
					if (!getMessagesViewer().getTable().isDisposed())
						updateQuote(aQuote);
				}
			});
		}
	}

	private static String getSymbol(Message message) {
		try {
			return message.getString(Symbol.FIELD).trim();
		} catch (FieldNotFound e) {
			return null;
		}
	}
	
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

	private MDVMarketDataListener marketDataListener;

	class MarketDataTableFormat extends OptionMessageListTableFormat {

		public MarketDataTableFormat(Table table, IWorkbenchPartSite site) {
			super(table, OptionDataColumns.values(), site);
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

	public void onAssertSymbol(MSymbol symbol) {
		addSymbol(symbol);
	}
	
	//cl todo:clean up this - fieldID should be encapsulate better, refactor common methods from 
	// EnumTableFormat into common util class
	private String convertExtractedValue(Object objValue, Integer fieldID)
	{
		String value = "";
		if (objValue != null && fieldID != null){
			FieldType fieldType = dictionary.getFieldTypeEnum(fieldID);
			if (fieldType.equals(FieldType.UtcTimeOnly)
					|| fieldType.equals(FieldType.UtcTimeStamp)){
				value = TIME_FORMAT.format((Date)objValue);
			} else if (fieldType.equals(FieldType.UtcDateOnly)
					||fieldType.equals(FieldType.UtcDate)){
				value = DATE_FORMAT.format((Date)objValue);
			} else if (objValue instanceof BigDecimal){
				value  = ((BigDecimal)objValue).toPlainString();
			} else {
				value = objValue.toString();
			}
		}
		return value;
	}

	public String extractStockValue(Enum fieldEnum, Object element) {
		Object value = null;
		Integer fieldID = null;
		if (fieldEnum instanceof IFieldIdentifier)
		{
			IFieldIdentifier fieldIdentifier = ((IFieldIdentifier)fieldEnum);

			fieldID = fieldIdentifier.getFieldID();
			Integer groupID = fieldIdentifier.getGroupID();
			Integer groupDiscriminatorID = fieldIdentifier.getGroupDiscriminatorID();
			Object groupDiscriminatorValue = fieldIdentifier.getGroupDiscriminatorValue();

			FieldMap fieldMap = (FieldMap) element;
			value = extractor.extractValue(fieldMap, fieldID, groupID, groupDiscriminatorID, groupDiscriminatorValue, true);
		}
		return convertExtractedValue(value, fieldID);
	}
	
	public void addSymbol(MSymbol symbol) {
		if (symbol == null || symbol.getBaseSymbol().length() <= 0) {
			return;
		}
		if (hasSymbol(symbol)) {
			return; // do nothing, already subscribed
		}
		if (hasUnderlyerInfo()) {
			// remove and unsubscribe underlying symbols and all related contracts
			Set<String> subscribedUnderlyingSymbols =  underlyingSymbolInfoMap.keySet();	
			for (String subscribedUnderlyingSymbol : subscribedUnderlyingSymbols) {
				removeUnderlyingSymbol(subscribedUnderlyingSymbol);
			}
		}
		// Step 1 - subscribe to the underlying symbol
		// Step 2 - retrieve and subscribe to all put/call options on the
		// underlying symbol
		addUnderlyerInfo(symbol.getBaseSymbol());

		try {
			marketDataTracker.simpleSubscribe(symbol);
			requestOptionSecurityList(marketDataTracker
					.getMarketDataFeedService(), symbol);

		} catch (MarketceteraException e) {
			PhotonPlugin.getMainConsoleLogger().error(
					"Exception subscribing to market data for " + symbol);
		}
		getMessagesViewer().refresh();
	}
	
    
	private void requestOptionSecurityList(MarketDataFeedService service,
			final MSymbol underlyingSymbol) {

		IMarketDataListCallback callback = new IMarketDataListCallback() {

			public void onMarketDataFailure(MSymbol symbol) {
				return; // do nothing
			}

			public void onMarketDataListAvailable(
					List<Message> derivativeSecurityList) {
				List<OptionContractData> optionContracts = OptionMarketDataUtils
						.getOptionExpirationMarketData(underlyingSymbol
								.getBaseSymbol(), derivativeSecurityList);
				if (optionContracts == null || optionContracts.isEmpty()) {
					// do nothing
				} else {
					EventList<OptionMessageHolder> list = getInput();

					for (OptionContractData data : optionContracts) {
						MSymbol optionSymbol = data.getOptionSymbol();

						// construct the option key
						OptionPairKey optionKey = new OptionPairKey(
								underlyingSymbol.getBaseSymbol(),
								OptionMarketDataUtils.getUnderlyingSymbol(
										optionSymbol).getBaseSymbol(), data
										.getExpirationYear(), data
										.getExpirationMonth(), data
										.getStrikePrice());

						
						Message callMessage = new Message();
						Message putMessage = new Message();

						if (data.isPut()) {
							subscribeOption(optionSymbol, putMessage);

							// Since OptionPairKey does not track put/call option on
							// purpose, need to track this separately with a map
							optionSymbolToSideMap.put(optionSymbol.getBaseSymbol(), true);							
						} else {
							subscribeOption(optionSymbol, callMessage);
							optionSymbolToSideMap.put(optionSymbol.getBaseSymbol(), false);							
						}
						
						optionSymbolToKeyMap.put(optionSymbol.getBaseSymbol(),
								optionKey);
						updateOptionContractMap(data.isPut(), optionKey,
								callMessage, putMessage);
					}
					
					Set<OptionPairKey> optionKeys = optionContractMap.keySet();
					for (OptionPairKey optionPairKey : optionKeys) {
						list.add(optionContractMap.get(optionPairKey));												
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

		Message query = OptionMarketDataUtils.newRelatedOptionsQuery(underlyingSymbol,
				false);
		MarketDataUtils.asyncMarketDataQuery(underlyingSymbol, query, service
				.getMarketDataFeed(), callback);
	}
	
	

	private void updateOptionContractMap(boolean isPut,
			OptionPairKey optionKey, Message callMessage, Message putMessage) {
		OptionMessageHolder newHolder = null;

		// lining up the call and put options
		if (optionContractMap.containsKey(optionKey)) {
			OptionMessageHolder holder = optionContractMap.get(optionKey);
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
		optionContractMap.put(optionKey, newHolder);
	}
	
	private boolean hasSymbol(final MSymbol symbol) {
		return (underlyingSymbolInfoMap.get(symbol.getBaseSymbol()) != null);		
	}
	
	private boolean hasUnderlyerInfo() {
		return (underlyingSymbolInfoMap != null && underlyingSymbolInfoMap.size() > 0);		
	}
		
	private void removeUnderlyingSymbol(String underlyingSymbol) {
		// retrieve all related contract symbols, unsubscribe and remove them
		MarketDataFeedService service = (MarketDataFeedService) marketDataTracker
				.getService();
		if (service == null) {
			PhotonPlugin.getMainConsoleLogger().warn("Missing quote feed");
			return;
		}	
		
		// unsubscribe and remove the underlying symbol
		MSymbol symbol = service.symbolFromString(underlyingSymbol);			
		marketDataTracker.simpleUnsubscribe(symbol);
		underlyingSymbolInfoMap.clear();  		
		disposeUnderlyerInfoSection();
				
		Set<String> contractSymbols = optionSymbolToKeyMap.keySet();	
		MSymbol contractSymbolToUnsubscribe = null;
		for (String optionSymbol : contractSymbols) {
			contractSymbolToUnsubscribe = service.symbolFromString(optionSymbol);			
			marketDataTracker.simpleUnsubscribe(contractSymbolToUnsubscribe);			
		}
		//clear out all maps and list
		EventList<OptionMessageHolder> list = getInput();
		list.clear();
		optionContractMap.clear();
		optionSymbolToKeyMap.clear();
		optionSymbolToSideMap.clear();
		getMessagesViewer().refresh();
		
	}

	public class MDVMarketDataListener extends MarketDataListener {

		public void onLevel2Quote(Message aQuote) {
		}

		public void onQuote(Message aQuote) {
			OptionMarketDataView.this.onQuote(aQuote);
		}

		public void onTrade(Message aTrade) {
		}

	}

}
