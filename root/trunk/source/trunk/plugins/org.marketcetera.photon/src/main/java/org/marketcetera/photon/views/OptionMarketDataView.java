package org.marketcetera.photon.views;

import java.lang.reflect.Field;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.marketcetera.core.MSymbol;
import org.marketcetera.core.MarketceteraException;
import org.marketcetera.marketdata.MarketDataListener;
import org.marketcetera.photon.IFieldIdentifier;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.core.IncomingMessageHolder;
import org.marketcetera.photon.core.MessageHolder;
import org.marketcetera.photon.marketdata.MarketDataFeedService;
import org.marketcetera.photon.marketdata.MarketDataFeedTracker;
import org.marketcetera.photon.ui.EventListContentProvider;
import org.marketcetera.photon.ui.IndexedTableViewer;
import org.marketcetera.photon.ui.MessageListTableFormat;
import org.marketcetera.photon.ui.TextContributionItem;

import quickfix.FieldMap;
import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.MDEntryPx;
import quickfix.field.MDEntrySize;
import quickfix.field.MDEntryType;
import quickfix.field.MaturityMonthYear;
import quickfix.field.NoMDEntries;
import quickfix.field.StrikePrice;
import quickfix.field.Symbol;
import quickfix.fix42.MarketDataSnapshotFullRefresh;
import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.matchers.Matcher;

/**
 * Option market data view.
 * 
 * @author caroline.leung@softwaregoodness.com
 */
public class OptionMarketDataView extends MessagesView implements
		IMSymbolListener {

	public static final String ID = "org.marketcetera.photon.views.OptionMarketDataView";

	private static final int ZERO_WIDTH_COLUMN_INDEX = 0;

	private static final int SYMBOL_COLUMN_INDEX = 1;

	private static final int LAST_NORMAL_COLUMN_INDEX = SYMBOL_COLUMN_INDEX;

	private FormToolkit formToolkit;

	private Section underliersSection;

	private Composite underliersContainer;

	private Composite[] underliers;

	private UnderlierInfo[] underlierInfoSections;

	private ScrolledForm form = null;

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
		createUnderliersSection();

		// helper methods for testing and mocking-up the UI only, will remove
		int numUnderliers = 3;
		test_createUnderliers(numUnderliers);
		test_populateUnderliersMktData(numUnderliers);

		Composite tableExpandable = createDataTableSection();
		super.createPartControl(tableExpandable);
		this.setInput(new BasicEventList<MessageHolder>());
	}

	// todo: duplicated, need to refactor
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

	private void createUnderliersSection() {
		underliersSection = getFormToolkit().createSection(
				form.getBody(), Section.EXPANDED | Section.NO_TITLE);
		underliersSection
				.setLayoutData(createTopAlignedHorizontallySpannedGridData());
		createUnderliersContainerComposite();
	}

	private void createUnderliersContainerComposite() {
		underliersContainer = getFormToolkit().createComposite(
				underliersSection, SWT.NONE);
		underliersContainer
				.setLayoutData(createTopAlignedHorizontallySpannedGridData());
		underliersContainer.setLayout(createBasicGridLayout(1));
		underliersSection.setClient(underliersContainer);		
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

	private Composite createUnderlierComposite(Composite parent) {
		Composite underlier = getFormToolkit().createComposite(
				parent, SWT.NONE);
		underlier.setLayout(createBasicGridLayout(1));
		underlier.setLayoutData(createTopAlignedHorizontallySpannedGridData());
		return underlier;
	}

	// =======================================================
	// Helper methods for mocking up the UI only;
	// Remove this section once the data feed is hooked up

	private void test_createUnderliers(int numUnderliers) {

		underliers = new Composite[numUnderliers];
		underlierInfoSections = new UnderlierInfo[numUnderliers];

		for (int i = 0; i < numUnderliers; i++) {
			underliers[i] = createUnderlierComposite(underliersContainer);
			if (i<numUnderliers -1){
				Label separator = new Label(underliersContainer, SWT.SEPARATOR | SWT.HORIZONTAL);
				GridData gridData = new GridData(SWT.LEFT, SWT.CENTER, true, false);
				gridData.widthHint = 400;
				separator.setLayoutData(gridData);
			}
			underlierInfoSections[i] = new UnderlierInfo(underliers[i]);
		}
	}

	private void test_populateUnderliersMktData(int numUnderliers) {
		String[] instrumentLabels = new String[] { "IBM", "SNE", "MSFT" };
		String[] lastPriceLabels = new String[] { "85.96", "70.97", "100.21" };
		String[] priceChangeLabels = new String[] { "-1.01", "+2.45", "-4.56" };
		String[] bidPriceLabels = new String[] { "85.91", "70.80", "99.21" };
		String[] askPriceLabels = new String[] { "88.54", "76.34", "101.21" };
		String[] bidSizeLabels = new String[] { "3", "5", "10" };
		String[] askSizeLabels = new String[] { "15", "23", "12" };
		String[] lastUpdatedTimeLabels = new String[] { "16:00", "16:01",
				"16:04" };
		String[] volLabels = new String[] { "6,614,100", "24,400", "614,100" };
		String[] openPriceLabels = new String[] { "87.01", "69.67", "95.21" };
		String[] hiPriceLabels = new String[] { "87.35", "71.22", "101.11" };
		String[] lowPriceLabels = new String[] { "85.76", "68.52", "93.11" };
		String[] tradingVolLabels = new String[] { "319.026m", "445.21m",
				"446.33m" };
		String[] exDividendInfo = new String[] { "06/11/07 0.30",
				"09/10/07 0.30", "12/10/07 0.30" };

		for (int i = 0; i < numUnderliers; i++) {
			underlierInfoSections[i].setAskPriceLabelText(askPriceLabels[i]);
			underlierInfoSections[i].setAskSizeLabelText(askSizeLabels[i]);
			underlierInfoSections[i].setBidPriceLabelText(bidPriceLabels[i]);
			underlierInfoSections[i].setBidSizeLabelText(bidSizeLabels[i]);
			underlierInfoSections[i].setHighPriceLabelText(hiPriceLabels[i]);
			underlierInfoSections[i]
					.setInstrumentLabelText(instrumentLabels[i]);
			underlierInfoSections[i]
					.setLastPriceChangeLabelText(priceChangeLabels[i]);
			underlierInfoSections[i].setLastPriceLabelText(lastPriceLabels[i]);
			// todo: use arrow up/down image
			// underlierInfoSections[i].setLastPriceUpDownArrowLabelImage()
			underlierInfoSections[i]
					.setLastUpdatedTimeLabelText(lastUpdatedTimeLabels[i]);
			underlierInfoSections[i].setLowPriceLabelText(lowPriceLabels[i]);
			underlierInfoSections[i].setOpenPriceLabelText(openPriceLabels[i]);
			underlierInfoSections[i]
					.setTradingVolumeLabelText(tradingVolLabels[i]);
			underlierInfoSections[i].setVolumeLabelText(volLabels[i]);
			underlierInfoSections[i]
					.setExDividendsDateAndAmountItems(exDividendInfo);
		}
	}

	// =======================================================

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
		for (UnderlierInfo underlier : underlierInfoSections) {
			underlier.dispose();
		}

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

	// todo: duplicated code from MarketDataView, need to refactor
	@Override
	protected void packColumns(Table table) {
		super.packColumns(table);

		// The following is required to work around the root cause of #132
		// ""Dirt" rendered in Market Data view
		// row selection".
		//
		// There is no way to remove the extra spacing in the first TableColumn
		// of an SWT Table on Windows.
		// This extra spacing is what causes the visible gap on a selected row.
		// It has nothing to do with the TextCellEditor or TableViewer -- the
		// problem can be
		// reproduced using just an SWT Table with text in the TableItems. The
		// first column always has extra space.
		//
		// The best that can be done to get rid of the visible gap in a selected
		// row
		// is to create an unmovable zero width first column.
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
				.setContentProvider(new EventListContentProvider<MessageHolder>());
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

	private boolean listContains(String stringValue) {
		if (stringValue == null) {
			return false;
		}
		EventList<MessageHolder> list = getInput();
		for (MessageHolder holder : list) {
			try {
				if (stringValue.equalsIgnoreCase(holder.getMessage().getString(
						Symbol.FIELD))) {
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
				if (message.getString(Symbol.FIELD).equals(
						quote.getString(Symbol.FIELD))) {
					IncomingMessageHolder newHolder = new IncomingMessageHolder(
							quote);
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


	private static final int CALL_VOLUME_INDEX = LAST_NORMAL_COLUMN_INDEX + 1;

	private static final int CALL_BID_SIZE_INDEX = LAST_NORMAL_COLUMN_INDEX + 2;

	private static final int CALL_BID_PRICE_INDEX = LAST_NORMAL_COLUMN_INDEX + 3;

	private static final int CALL_ASK_PRICE_INDEX = LAST_NORMAL_COLUMN_INDEX + 4;

	private static final int CALL_ASK_SIZE_INDEX = LAST_NORMAL_COLUMN_INDEX + 5;

	private static final int CALL_SYMBOL_INDEX = LAST_NORMAL_COLUMN_INDEX + 6;

	private static final int STRIKE_INDEX = LAST_NORMAL_COLUMN_INDEX + 7;

	private static final int EXP_DATE_INDEX = LAST_NORMAL_COLUMN_INDEX + 8;

	private static final int PUT_SYMBOL_INDEX = LAST_NORMAL_COLUMN_INDEX + 9;

	private static final int PUT_BID_SIZE_INDEX = LAST_NORMAL_COLUMN_INDEX + 10;

	private static final int PUT_BID_PRICE_INDEX = LAST_NORMAL_COLUMN_INDEX + 11;

	private static final int PUT_ASK_PRICE_INDEX = LAST_NORMAL_COLUMN_INDEX + 12;

	private static final int PUT_ASK_SIZE_INDEX = LAST_NORMAL_COLUMN_INDEX + 13;

	private static final int PUT_VOLUME_INDEX = LAST_NORMAL_COLUMN_INDEX + 14;

	private MDVMarketDataListener marketDataListener;

	class MarketDataTableFormat extends MessageListTableFormat {

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
				return "cBidSz";
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
			if (index <= LAST_NORMAL_COLUMN_INDEX) {
				return super.getColumnText(element, index);
			}
			MessageHolder messageHolder = (MessageHolder) element;
			Message message = messageHolder.getMessage();
			try {
				switch (index) {
				case CALL_BID_SIZE_INDEX:
					return getGroup(message, MDEntryType.BID).getString(
							MDEntrySize.FIELD);
				case CALL_BID_PRICE_INDEX:
					return getGroup(message, MDEntryType.BID).getString(
							MDEntryPx.FIELD);
				case CALL_ASK_PRICE_INDEX:
					return getGroup(message, MDEntryType.OFFER).getString(
							MDEntryPx.FIELD);
				case CALL_ASK_SIZE_INDEX:
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
				for (int i = 1; i < noEntries + 1; i++) {
					MarketDataSnapshotFullRefresh.NoMDEntries group = new MarketDataSnapshotFullRefresh.NoMDEntries();
					message.getGroup(i, group);
					if (type == group.getChar(MDEntryType.FIELD)) {
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

		if (hasSymbol(symbol)) {
			PhotonPlugin.getMainConsoleLogger().warn(
					"Duplicate symbol added to view: " + symbol);
		} else {
			EventList<MessageHolder> list = getInput();

			Message message = new Message();
			message.setField(new Symbol(symbol.toString()));
			list.add(new MessageHolder(message));

			try {
				marketDataTracker.simpleSubscribe(symbol);
			} catch (MarketceteraException e) {
				PhotonPlugin.getMainConsoleLogger().error(
						"Exception subscribing to market data for " + symbol);
			}
			getMessagesViewer().refresh();
			
		}
	}

	private boolean hasSymbol(final MSymbol symbol) {
		EventList<MessageHolder> list = getInput();

		FilterList<MessageHolder> matches = new FilterList<MessageHolder>(list,
				new Matcher<MessageHolder>() {
					public boolean matches(MessageHolder listItem) {
						try {
							String listSymbol = listItem.getMessage()
									.getString(Symbol.FIELD).trim()
									.toLowerCase();
							return listSymbol.equals(symbol.getFullSymbol()
									.trim().toLowerCase());
						} catch (FieldNotFound e) {
							return false;
						}
					}
				});
		boolean rv = !matches.isEmpty();
		return rv;
	}

	public void removeItem(MessageHolder holder) {
		MarketDataFeedService service = (MarketDataFeedService) marketDataTracker
				.getService();
		if (service == null) {
			PhotonPlugin.getMainConsoleLogger().warn("Missing quote feed");
			return;
		}
		try {
			MSymbol mSymbol = service.symbolFromString(holder.getMessage()
					.getString(Symbol.FIELD));
			marketDataTracker.simpleUnsubscribe(mSymbol);
			removeSymbol(mSymbol);
		} catch (FieldNotFound e) {
			// do nothing
		}
	}

	public void removeSymbol(MSymbol symbol) {

		marketDataTracker.simpleUnsubscribe(symbol);

		EventList<MessageHolder> list = getInput();
		for (MessageHolder holder : list) {
			try {
				String messageSymbolString = holder.getMessage().getString(
						Symbol.FIELD);
				if (messageSymbolString.equals(symbol.toString())) {
					list.remove(holder);
					break;
				}
			} catch (FieldNotFound e) {
			}
		}
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
