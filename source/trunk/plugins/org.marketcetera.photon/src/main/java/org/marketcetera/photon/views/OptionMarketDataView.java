package org.marketcetera.photon.views;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
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

import quickfix.FieldNotFound;
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
public class OptionMarketDataView extends OptionMessagesView implements
		IMSymbolListener {

	public static final String ID = "org.marketcetera.photon.views.OptionMarketDataView";

	private UnderlyingSymbolInfoComposite underlyingSymbolInfoComposite;

	public static final int FIRST_PUT_DATA_COLUMN_INDEX = 9;

	private static final int ZERO_WIDTH_COLUMN_INDEX = 0;

	private static final int LAST_NORMAL_COLUMN_INDEX = ZERO_WIDTH_COLUMN_INDEX;

	private FormToolkit formToolkit;

	private ScrolledForm form = null;

	private HashMap<String, OptionPairKey> optionSymbolToKeyMap;

	private HashMap<OptionPairKey, OptionMessageHolder> optionContractMap;

	private HashMap<String, Boolean> optionSymbolToSideMap;

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
	}

	@Override
	public void createPartControl(Composite parent) {
		createForm(parent);
		underlyingSymbolInfoComposite = new UnderlyingSymbolInfoComposite(
				form.getBody());
		underlyingSymbolInfoComposite
				.setLayoutData(createTopAlignedHorizontallySpannedGridData());
		Composite tableExpandable = createDataTableSection();
		super.createPartControl(tableExpandable);
		this.setInput(new BasicEventList<OptionMessageHolder>());
		initializeDataMaps();
	}

	private void initializeDataMaps() {
		optionSymbolToKeyMap = new HashMap<String, OptionPairKey>();
		optionSymbolToSideMap = new HashMap<String, Boolean>();
		optionContractMap = new HashMap<OptionPairKey, OptionMessageHolder>();
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

	private Composite createDataTableSection() {
		Section tableSection = getFormToolkit().createSection(form.getBody(),
				Section.EXPANDED | Section.NO_TITLE);
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
		underlyingSymbolInfoComposite.dispose();
		super.dispose();
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

	// cl todo: duplicated code from MarketDataView, need to refactor
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
		
		if (underlyingSymbolInfoComposite.matchUnderlyingSymbol(quote)) {
			underlyingSymbolInfoComposite.onQuote(quote);
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
	
	public void addSymbol(MSymbol symbol) {
		if (symbol == null || symbol.getBaseSymbol().length() <= 0) {
			return;
		}
		if (underlyingSymbolInfoComposite.hasSymbol(symbol)) {
			return; // do nothing, already subscribed
		}
		if (underlyingSymbolInfoComposite.hasUnderlyingSymbolInfo()) {
			removeUnderlyingSymbol();			
		}
		// Step 1 - subscribe to the underlying symbol
		// Step 2 - retrieve and subscribe to all put/call options 
		underlyingSymbolInfoComposite.addUnderlyingSymbolInfo(symbol
				.getBaseSymbol());

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

							// Since OptionPairKey does not track put/call
							// option on purpose, need to track this separately with a map
							optionSymbolToSideMap.put(optionSymbol
									.getBaseSymbol(), true);
						} else {
							subscribeOption(optionSymbol, callMessage);
							optionSymbolToSideMap.put(optionSymbol
									.getBaseSymbol(), false);
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

		Message query = OptionMarketDataUtils.newRelatedOptionsQuery(
				underlyingSymbol, false);
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

	private void removeUnderlyingSymbol() {
		// retrieve all related contract symbols, unsubscribe and remove them
		MarketDataFeedService service = (MarketDataFeedService) marketDataTracker
				.getService();
		if (service == null) {
			PhotonPlugin.getMainConsoleLogger().warn("Missing quote feed");
			return;
		}

		// remove and unsubscribe underlying symbols and all contracts
		Set<String> subscribedUnderlyingSymbols = underlyingSymbolInfoComposite
				.getUnderlyingSymbolInfoMap().keySet();
		for (String subscribedUnderlyingSymbol : subscribedUnderlyingSymbols) {
			// unsubscribe and remove the underlying symbol
			MSymbol symbol = service.symbolFromString(subscribedUnderlyingSymbol);
			marketDataTracker.simpleUnsubscribe(symbol);
		}
		underlyingSymbolInfoComposite.removeUnderlyingSymbol(); 

		Set<String> contractSymbols = optionSymbolToKeyMap.keySet();
		MSymbol contractSymbolToUnsubscribe = null;
		for (String optionSymbol : contractSymbols) {
			contractSymbolToUnsubscribe = service
					.symbolFromString(optionSymbol);
			marketDataTracker.simpleUnsubscribe(contractSymbolToUnsubscribe);
		}
		// clear out all maps and list
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
