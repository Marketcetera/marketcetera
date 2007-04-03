package org.marketcetera.photon.views;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.ViewPart;
import org.marketcetera.core.MSymbol;
import org.marketcetera.core.MarketceteraException;
import org.marketcetera.marketdata.ConjunctionMessageSelector;
import org.marketcetera.marketdata.MarketDataListener;
import org.marketcetera.marketdata.MessageTypeSelector;
import org.marketcetera.marketdata.SymbolMessageSelector;
import org.marketcetera.photon.EclipseUtils;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.marketdata.MarketDataFeedService;
import org.marketcetera.photon.marketdata.MarketDataFeedTracker;
import org.marketcetera.photon.parser.SideImage;
import org.marketcetera.photon.parser.TimeInForceImage;
import org.marketcetera.photon.preferences.CustomOrderFieldPage;
import org.marketcetera.photon.preferences.MapEditorUtil;
import org.marketcetera.photon.ui.BookComposite;
import org.marketcetera.photon.ui.validation.CComboValidator;
import org.marketcetera.photon.ui.validation.FormValidator;
import org.marketcetera.photon.ui.validation.IMessageDisplayer;
import org.marketcetera.photon.ui.validation.NumericTextValidator;
import org.marketcetera.photon.ui.validation.ParentColorHighlighter;
import org.marketcetera.photon.ui.validation.TextValidator;
import org.marketcetera.photon.ui.validation.fix.AbstractFIXExtractor;
import org.marketcetera.photon.ui.validation.fix.FIXCComboExtractor;
import org.marketcetera.photon.ui.validation.fix.FIXTextExtractor;
import org.marketcetera.photon.ui.validation.fix.PriceTextValidator;
import org.marketcetera.quickfix.FIXDataDictionaryManager;
import org.marketcetera.quickfix.FIXMessageUtil;

import quickfix.DataDictionary;
import quickfix.FieldMap;
import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.StringField;
import quickfix.field.Account;
import quickfix.field.OrderQty;
import quickfix.field.Price;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.field.TimeInForce;
import ca.odell.glazedlists.EventList;

public class StockOrderTicket extends ViewPart implements IMessageDisplayer, IPropertyChangeListener {

	private static final String NEW_EQUITY_ORDER = "New Equity Order";

	private static final String REPLACE_EQUITY_ORDER = "Replace Equity Order";

	public static String ID = "org.marketcetera.photon.views.StockOrderTicket";

	private Composite top = null;

	private FormToolkit formToolkit = null; // @jve:decl-index=0:visual-constraint=""

	private ScrolledForm form = null;

	private Label errorMessageLabel = null;

	private Label sideLabel = null;

	private Label quantityLabel = null;

	private Label symbolLabel = null;

	private Label priceLabel = null;

	private Label tifLabel = null;

	private Composite sideBorderComposite = null;

	private CCombo sideCCombo = null;

	private Composite quantityBorderComposite = null;

	private Composite symbolBorderComposite = null;

	private Composite priceBorderComposite = null;

	private Composite tifBorderComposite = null;

	private Text quantityText = null;

	private Text symbolText = null;

	private Text priceText = null;

	private CCombo tifCCombo = null;

	private ExpandableComposite customFieldsExpandableComposite = null;

	private Composite customFieldsComposite = null;

	private Table customFieldsTable = null;

	private CheckboxTableViewer tableViewer = null;

	private FormValidator validator = new FormValidator(this);

	private MSymbol listenedSymbol = null;

	List<AbstractFIXExtractor> extractors = new LinkedList<AbstractFIXExtractor>();

	private Button sendButton;

	private Button cancelButton;

	private BookComposite bookComposite;

	private Message targetOrder;

	private Section otherExpandableComposite;

	private Text accountText;

	private Section bookSection;

	private MarketDataFeedTracker marketDataTracker;

	private IMemento viewStateMemento;
	private static final String CUSTOM_FIELD_VIEW_SAVED_STATE_KEY_PREFIX = "CUSTOM_FIELD_CHECKED_STATE_OF_";

	private MarketDataListener marketDataListener;

	private ConjunctionMessageSelector currentSubscription;
	

	public StockOrderTicket() {
		marketDataTracker = new MarketDataFeedTracker(PhotonPlugin.getDefault().getBundleContext());
		marketDataTracker.open();
		
	}

	@Override
	public void createPartControl(Composite parent) {
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = GridData.END;
		top = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		top.setLayout(gridLayout);

		createForm();
		errorMessageLabel = getFormToolkit().createLabel(top, "");
		errorMessageLabel.setLayoutData(gridData);
		top.setBackground(errorMessageLabel.getBackground());

		PhotonPlugin plugin = PhotonPlugin.getDefault();
		plugin.getPreferenceStore().addPropertyChangeListener(this);
		
		marketDataListener = new MarketDataListener(){
					public void onLevel2Quote(Message aQuote) {
						StockOrderTicket.this.onQuote(aQuote);
					}
		
					public void onQuote(Message aQuote) {
						StockOrderTicket.this.onQuote(aQuote);
					}
		
					public void onTrade(Message aTrade) {
					}
					
				};
		marketDataTracker.setMarketDataListener(marketDataListener);

	}

	@Override
	public void dispose() {
		marketDataTracker.setMarketDataListener(null);
		marketDataTracker.close();
		PhotonPlugin.getDefault().getPreferenceStore().removePropertyChangeListener(this);
		
		PhotonPlugin plugin = PhotonPlugin.getDefault();
	}

	@Override
	public void setFocus() {
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

	/**
	 * This method initializes form
	 * 
	 */
	private void createForm() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 5;
		gridLayout.marginWidth = 3;
		gridLayout.verticalSpacing = 1;
		gridLayout.horizontalSpacing = 1;
		gridLayout.marginHeight = 1;
		form = getFormToolkit().createScrolledForm(top);
		form.setText(NEW_EQUITY_ORDER);
		form.getBody().setLayout(gridLayout);
		sideLabel = getFormToolkit().createLabel(form.getBody(), "Side");
		quantityLabel = getFormToolkit()
				.createLabel(form.getBody(), "Quantity");
		symbolLabel = getFormToolkit().createLabel(form.getBody(), "Symbol");
		priceLabel = getFormToolkit().createLabel(form.getBody(), "Price");
		tifLabel = getFormToolkit().createLabel(form.getBody(), "TIF");
		createSideBorderComposite();
		createQuantityBorderComposite();
		createSymbolBorderComposite();
		createPriceBorderComposite();
		createTifBorderComposite();
		// createCustomFieldsExpandableComposite();
		GridData formGridData = new GridData();
		formGridData.grabExcessHorizontalSpace = true;
		formGridData.horizontalAlignment = GridData.FILL;
		formGridData.grabExcessVerticalSpace = true;
		formGridData.verticalAlignment = GridData.FILL;
		form.setLayoutData(formGridData);

		Composite okCancelComposite = getFormToolkit().createComposite(
				form.getBody());
		okCancelComposite.setLayout(new RowLayout(SWT.HORIZONTAL));
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_END);
		gd.horizontalSpan = 5;
		okCancelComposite.setLayoutData(gd);
		sendButton = getFormToolkit().createButton(okCancelComposite, "Send",
				SWT.PUSH);
		cancelButton = getFormToolkit().createButton(okCancelComposite,
				"Cancel", SWT.PUSH);
		cancelButton.addMouseListener(new MouseAdapter() {
			public void mouseUp(MouseEvent e) {
				handleCancel();
			}
		});
		sendButton.addMouseListener(new MouseAdapter() {
			public void mouseUp(MouseEvent e) {
				handleSend();
			}
		});

		createCustomFieldsExpandableComposite();
		createOtherExpandableComposite();
		createBookComposite();

		updateCustomFields(PhotonPlugin.getDefault().getPreferenceStore()
				.getString(CustomOrderFieldPage.CUSTOM_FIELDS_PREFERENCE));
		restoreCustomFieldStates();
	}

	private void restoreCustomFieldStates() {
		if (viewStateMemento == null)
			return;
		
		TableItem[] items = customFieldsTable.getItems();
		for (int i = 0; i < items.length; i++) {
			TableItem item = items[i];
			String key = CUSTOM_FIELD_VIEW_SAVED_STATE_KEY_PREFIX + item.getText(1);
			if (viewStateMemento.getInteger(key) != null) {
				boolean itemChecked = (viewStateMemento.getInteger(key).intValue() != 0);
				item.setChecked(itemChecked);
			}
		}
	}

	/**
	 * This method initializes sideBorderComposite
	 * 
	 */
	private void createSideBorderComposite() {
		sideBorderComposite = getFormToolkit().createComposite(form.getBody());
		GridLayout gridLayout = new GridLayout();
		gridLayout.marginWidth = 2;
		gridLayout.verticalSpacing = 2;
		gridLayout.horizontalSpacing = 2;
		gridLayout.marginHeight = 2;
		sideBorderComposite.setLayout(gridLayout);
		sideCCombo = new CCombo(sideBorderComposite, SWT.BORDER);
		sideCCombo.add(SideImage.BUY.getImage());
		sideCCombo.add(SideImage.SELL.getImage());
		sideCCombo.add(SideImage.SELL_SHORT.getImage());
		sideCCombo.add(SideImage.SELL_SHORT_EXEMPT.getImage());
		CComboValidator comboValidator = new CComboValidator(sideCCombo,
				"Side", Arrays.asList(sideCCombo.getItems()), false);
		ParentColorHighlighter highlighter = new ParentColorHighlighter(
				sideCCombo);

		Map<String, String> uiStringToMessageStringMap = new HashMap<String, String>();
		uiStringToMessageStringMap.put(SideImage.BUY.getImage(), "" + Side.BUY);
		uiStringToMessageStringMap.put(SideImage.SELL.getImage(), ""
				+ Side.SELL);
		uiStringToMessageStringMap.put(SideImage.SELL_SHORT.getImage(), ""
				+ Side.SELL_SHORT);
		uiStringToMessageStringMap.put(SideImage.SELL_SHORT_EXEMPT.getImage(),
				"" + Side.SELL_SHORT_EXEMPT);
		FIXCComboExtractor extractor = new FIXCComboExtractor(sideCCombo,
				Side.FIELD, FIXDataDictionaryManager.getDictionary(),
				uiStringToMessageStringMap);

		validator.register(sideCCombo, true);
		extractors.add(extractor);
	}

	/**
	 * This method initializes quantityBorderComposite
	 * 
	 */
	private void createQuantityBorderComposite() {
		quantityBorderComposite = getFormToolkit().createComposite(
				form.getBody());
		GridLayout gridLayout = new GridLayout();
		gridLayout.marginWidth = 2;
		gridLayout.verticalSpacing = 2;
		gridLayout.horizontalSpacing = 2;
		gridLayout.marginHeight = 2;
		quantityBorderComposite.setLayout(gridLayout);
		quantityText = getFormToolkit().createText(quantityBorderComposite,
				null, SWT.SINGLE | SWT.BORDER);
		
		Point sizeHint = EclipseUtils.getTextAreaSize(quantityBorderComposite, null, 10, 1.0);

		GridData quantityTextGridData = new GridData();
		//quantityTextGridData.heightHint = sizeHint.y;
		quantityTextGridData.widthHint = sizeHint.x;
		quantityText.setLayoutData(quantityTextGridData);
		
		quantityText.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				((Text) e.widget).selectAll();
			}
		});
		NumericTextValidator textValidator = new NumericTextValidator(
				quantityText, "Quantity", true, false, false, false);
		ParentColorHighlighter highlighter = new ParentColorHighlighter(
				quantityText);
		FIXTextExtractor extractor = new FIXTextExtractor(quantityText,
				OrderQty.FIELD, FIXDataDictionaryManager.getDictionary());
		validator.register(quantityText, true);
		extractors.add(extractor);
	}

	/**
	 * This method initializes symbolBorderComposite
	 * 
	 */
	private void createSymbolBorderComposite() {
		GridData symbolTextGridData = new GridData();
		symbolTextGridData.horizontalAlignment = GridData.FILL;
		symbolTextGridData.grabExcessHorizontalSpace = true;
		symbolTextGridData.verticalAlignment = GridData.CENTER;
		GridData symbolBorderGridData = new GridData();
		symbolBorderGridData.horizontalAlignment = GridData.FILL;
		symbolBorderGridData.grabExcessHorizontalSpace = true;
		symbolBorderGridData.verticalAlignment = GridData.CENTER;
		symbolBorderComposite = getFormToolkit()
				.createComposite(form.getBody());
		GridLayout gridLayout = new GridLayout();
		gridLayout.marginWidth = 2;
		gridLayout.verticalSpacing = 2;
		gridLayout.horizontalSpacing = 2;
		gridLayout.marginHeight = 2;
		symbolBorderComposite.setLayout(gridLayout);
		symbolBorderComposite.setLayoutData(symbolBorderGridData);
		symbolText = getFormToolkit().createText(symbolBorderComposite, null,
				SWT.SINGLE | SWT.BORDER);
		symbolText.setLayoutData(symbolTextGridData);
		symbolText.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				((Text) e.widget).selectAll();
			}

			@Override
			public void focusLost(FocusEvent e) {
				listenMarketData(((Text) e.widget).getText());
			}
		});
		TextValidator textValidator = new TextValidator(symbolText, "Symbol",
				false);
		ParentColorHighlighter highlighter = new ParentColorHighlighter(
				symbolText);
		FIXTextExtractor extractor = new FIXTextExtractor(symbolText,
				Symbol.FIELD, FIXDataDictionaryManager.getDictionary());
		extractors.add(extractor);
	}

	protected void listenMarketData(String symbol) {
		unlisten();
		if (symbol != null && !"".equals(symbol)){
			MSymbol newListenedSymbol = new MSymbol(symbol);
			MarketDataFeedService service = marketDataTracker.getMarketDataFeedService();
			
			if (service != null
					&& !newListenedSymbol.equals(listenedSymbol)) {
				ConjunctionMessageSelector subscription = new ConjunctionMessageSelector(
												new SymbolMessageSelector(newListenedSymbol),
												new MessageTypeSelector(false, false, true));
				service.subscribe(subscription);
				listenedSymbol = newListenedSymbol;
				currentSubscription = subscription;
			}
		}
	}

	protected void unlisten() {
		MarketDataFeedService service = marketDataTracker
				.getMarketDataFeedService();

		if (service != null) {
			if (currentSubscription != null) {
				service.unsubscribe(currentSubscription);
				listenedSymbol = null;
				currentSubscription = null;
			}
		}
		bookComposite.setInput(null);
	}

	/**
	 * This method initializes priceBorderComposite
	 * 
	 */
	private void createPriceBorderComposite() {
		priceBorderComposite = getFormToolkit().createComposite(form.getBody());
		GridLayout gridLayout = new GridLayout();
		gridLayout.marginWidth = 2;
		gridLayout.verticalSpacing = 2;
		gridLayout.horizontalSpacing = 2;
		gridLayout.marginHeight = 2;
		priceBorderComposite.setLayout(gridLayout);
		priceText = getFormToolkit().createText(priceBorderComposite, null,
				SWT.SINGLE | SWT.BORDER);
		
		Point sizeHint = EclipseUtils.getTextAreaSize(priceBorderComposite, null, 10, 1.0);

		GridData quantityTextGridData = new GridData();
		//quantityTextGridData.heightHint = sizeHint.y;
		quantityTextGridData.widthHint = sizeHint.x;
		priceText.setLayoutData(quantityTextGridData);

		priceText.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				((Text) e.widget).selectAll();
			}
		});
		PriceTextValidator textValidator = new PriceTextValidator(priceText,
				"Price", false, false);
		ParentColorHighlighter highlighter = new ParentColorHighlighter(
				priceText);
		validator.register(priceText, true);
		FIXTextExtractor extractor = new OrderPriceExtractor(priceText,
				FIXDataDictionaryManager.getDictionary());
		extractors.add(extractor);
	}

	/**
	 * This method initializes tifBorderComposite
	 * 
	 */
	private void createTifBorderComposite() {
		tifBorderComposite = getFormToolkit().createComposite(form.getBody());
		GridLayout gridLayout = new GridLayout();
		gridLayout.marginWidth = 2;
		gridLayout.verticalSpacing = 2;
		gridLayout.horizontalSpacing = 2;
		gridLayout.marginHeight = 2;
		tifBorderComposite.setLayout(gridLayout);
		tifCCombo = new CCombo(tifBorderComposite, SWT.BORDER);
		tifCCombo.add(TimeInForceImage.DAY.getImage());
		tifCCombo.add(TimeInForceImage.OPG.getImage());
		tifCCombo.add(TimeInForceImage.CLO.getImage());
		tifCCombo.add(TimeInForceImage.FOK.getImage());
		tifCCombo.add(TimeInForceImage.GTC.getImage());
		tifCCombo.add(TimeInForceImage.IOC.getImage());

		CComboValidator comboValidator = new CComboValidator(tifCCombo, "TIF",
				Arrays.asList(tifCCombo.getItems()), false);
		ParentColorHighlighter highlighter = new ParentColorHighlighter(
				tifCCombo);

		Map<String, String> uiStringToMessageStringMap = new HashMap<String, String>();
		uiStringToMessageStringMap.put(TimeInForceImage.DAY.getImage(), ""
				+ TimeInForce.DAY);
		uiStringToMessageStringMap.put(TimeInForceImage.OPG.getImage(), ""
				+ TimeInForce.AT_THE_OPENING);
		uiStringToMessageStringMap.put(TimeInForceImage.CLO.getImage(), ""
				+ TimeInForce.AT_THE_CLOSE);
		uiStringToMessageStringMap.put(TimeInForceImage.FOK.getImage(), ""
				+ TimeInForce.FILL_OR_KILL);
		uiStringToMessageStringMap.put(TimeInForceImage.GTC.getImage(), ""
				+ TimeInForce.GOOD_TILL_CANCEL);
		uiStringToMessageStringMap.put(TimeInForceImage.IOC.getImage(), ""
				+ TimeInForce.IMMEDIATE_OR_CANCEL);
		FIXCComboExtractor extractor = new FIXCComboExtractor(tifCCombo,
				TimeInForce.FIELD, FIXDataDictionaryManager.getDictionary(),
				uiStringToMessageStringMap, TimeInForceImage.DAY.getImage());

		validator.register(tifCCombo, true);
		extractors.add(extractor);
	}

	/**
	 * This method initializes customFieldsExpandableComposite
	 * 
	 */
	private void createCustomFieldsExpandableComposite() {
		GridData gridData3 = new GridData();
		gridData3.horizontalSpan = 3;
		gridData3.verticalAlignment = GridData.BEGINNING;
		gridData3.grabExcessHorizontalSpace = true;
		gridData3.horizontalAlignment = GridData.FILL;
		customFieldsExpandableComposite = getFormToolkit()
				.createSection(
						form.getBody(),
						Section.TITLE_BAR|
						  Section.TWISTIE);
		customFieldsExpandableComposite.setText("Custom Fields");
		customFieldsExpandableComposite.setExpanded(false);
		customFieldsExpandableComposite.setLayoutData(gridData3);
		customFieldsExpandableComposite
			.addExpansionListener(new ExpansionAdapter() {
				@Override
				public void expansionStateChanging(ExpansionEvent e) {
									form.reflow(true);
								}
				public void expansionStateChanged(ExpansionEvent e) {
					form.reflow(true);
				}
			});


		createCustomFieldsComposite();
	}

	/**
	 * This method initializes customFieldsComposite
	 * 
	 */
	private void createCustomFieldsComposite() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.marginWidth = 1;
		gridLayout.verticalSpacing = 1;
		gridLayout.horizontalSpacing = 1;
		gridLayout.marginHeight = 1;

		customFieldsComposite = getFormToolkit().createComposite(
				customFieldsExpandableComposite);
		customFieldsComposite.setLayout(gridLayout);
		GridData tableGridData = new GridData();
		tableGridData.verticalAlignment = GridData.CENTER;
		tableGridData.grabExcessHorizontalSpace = true;
		tableGridData.horizontalAlignment = GridData.FILL;

		customFieldsTable = new Table(customFieldsComposite, SWT.BORDER
				| SWT.CHECK | SWT.FULL_SELECTION);
		customFieldsTable.setLayoutData(tableGridData);
		customFieldsTable.setHeaderVisible(true);

		TableColumn enabledColumn = new TableColumn(customFieldsTable, SWT.LEFT);
		enabledColumn.setText("Enabled");
		enabledColumn.pack();
		TableColumn keyColumn = new TableColumn(customFieldsTable, SWT.LEFT);
		keyColumn.setText("Key");
		keyColumn.pack();
		TableColumn valueColumn = new TableColumn(customFieldsTable, SWT.LEFT);
		valueColumn.setText("Value");
		valueColumn.pack();

		// tableViewer = new CheckboxTableViewer(
		// customFieldsTable);
		// tableViewer.setContentProvider(new
		// MapEntryContentProvider(tableViewer, mapEntryList));
		// tableViewer.setLabelProvider(new MapEntryLabelProvider());
		// tableViewer.setInput(mapEntryList);

		customFieldsExpandableComposite.setClient(customFieldsComposite);

	}
	/**
	 * This method initializes customFieldsExpandableComposite
	 * 
	 */
	private void createOtherExpandableComposite() {
		GridData gridData3 = new GridData();
		gridData3.horizontalSpan = 2;
		gridData3.verticalAlignment = GridData.BEGINNING;
//		gridData3.grabExcessHorizontalSpace = true;
		gridData3.horizontalAlignment = GridData.FILL;
		otherExpandableComposite = getFormToolkit()
				.createSection(
						form.getBody(),
						Section.TITLE_BAR|
						  Section.TWISTIE);
		otherExpandableComposite.setText("Other");
		otherExpandableComposite.setExpanded(false);
		otherExpandableComposite.setLayoutData(gridData3);

		Composite otherComposite = getFormToolkit().createComposite(otherExpandableComposite);
		otherComposite.setLayout(new RowLayout(SWT.HORIZONTAL));
		Label accountLabel = getFormToolkit().createLabel(otherComposite, "Account:");
		accountText = getFormToolkit().createText(otherComposite, "");
		Point sizeHint = EclipseUtils.getTextAreaSize(quantityBorderComposite, null, 10, 1.0); 
	 	
		RowData accountTextRowData = new RowData(); 
	 	accountTextRowData.height = sizeHint.y; 
	 	accountTextRowData.width = sizeHint.x; 
	 	accountText.setLayoutData(accountTextRowData); 
	 	
		getFormToolkit().paintBordersFor(otherComposite);
		otherExpandableComposite.setClient(otherComposite);
		FIXTextExtractor extractor = new FIXTextExtractor(accountText,Account.FIELD, FIXDataDictionaryManager.getDictionary());

		extractors.add(extractor);
	}


	private void createBookComposite() {
		bookSection = getFormToolkit().createSection(form.getBody(), Section.TITLE_BAR);
		bookSection.setText("Market data");
		bookSection.setExpanded(true);
			
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

		bookSection.setLayout(gridLayout);
		bookSection.setLayoutData(layoutData);
		
		bookComposite = new BookComposite(bookSection, SWT.NONE, getFormToolkit());
		bookSection.setClient(bookComposite);
	}

	public void handleSend() {
		try {
			if (validator.validateAll()) {
				Message aMessage;
				PhotonPlugin plugin = PhotonPlugin.getDefault();
				if (targetOrder == null) {
					String orderID = plugin.getIDFactory().getNext();
					aMessage = plugin.getMessageFactory()
							.newLimitOrder(orderID, Side.BUY,
									BigDecimal.ZERO, new MSymbol(""),
									BigDecimal.ZERO, TimeInForce.DAY, null);
					aMessage.removeField(Side.FIELD);
					aMessage.removeField(OrderQty.FIELD);
					aMessage.removeField(Symbol.FIELD);
					aMessage.removeField(Price.FIELD);
					aMessage.removeField(TimeInForce.FIELD);
				} else {
					aMessage = targetOrder;
				}
				for (AbstractFIXExtractor extractor : extractors) {
					extractor.modifyOrder(aMessage);
				}
				addCustomFields(aMessage);
				plugin.getPhotonController().handleInternalMessage(aMessage);
				clear();
			}
		} catch (Exception e) {
			PhotonPlugin.getMainConsoleLogger().error(
					"Error sending order: " + e.getMessage(), e);
		}
	}


	protected void handleCancel() {
		clear();
	}

	private void clear() {
		for (AbstractFIXExtractor extractor : extractors) {
			extractor.clearUI();
		}
		unlisten();
		targetOrder = null;
		updateTitle();
		symbolText.setEnabled(true);
	}

	public void clearMessage() {
		errorMessageLabel.setText("");
	}

	public void showError(String errorString) {
		if (errorString == null) {
			errorMessageLabel.setText("");
		} else {
			errorMessageLabel.setText(errorString);
		}
	}

	public void showWarning(String warningString) {
		if (warningString == null) {
			errorMessageLabel.setText("");
		} else {
			errorMessageLabel.setText(warningString);
		}
	}

	public void showOrder(Message order) {
		for (AbstractFIXExtractor extractor : extractors) {
			extractor.updateUI(order);
		}
		symbolText.setEnabled(FIXMessageUtil.isOrderSingle(order));
		targetOrder = order;
		updateTitle();
		listenMarketData(symbolText.getText());
	}
	
	private void updateTitle()
	{
		if (FIXMessageUtil.isCancelReplaceRequest(targetOrder)){
			form.setText(REPLACE_EQUITY_ORDER);
		} else {
			form.setText(NEW_EQUITY_ORDER);
		}
	}

	public static StockOrderTicket getDefault() {
		return (StockOrderTicket) PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().findView(
						StockOrderTicket.ID);
	}

	public void propertyChange(PropertyChangeEvent event) {
		String property = event.getProperty();
		if (CustomOrderFieldPage.CUSTOM_FIELDS_PREFERENCE.equals(property)){
			String valueString = event.getNewValue().toString();
			updateCustomFields(valueString);
		}
	}

	private void updateCustomFields(String preferenceString) {
		// Save previous enabled checkbox state
		final int keyColumnNum = 1;
		HashMap<String, Boolean> existingEnabledMap = new HashMap<String, Boolean>(); 
		TableItem[] existingItems = customFieldsTable.getItems();
		for(TableItem existingItem : existingItems) {
			String key = existingItem.getText(keyColumnNum);
			boolean checkedState = existingItem.getChecked();
			existingEnabledMap.put(key, checkedState);
		}
		
		customFieldsTable.setItemCount(0);
		EventList<Entry<String, String>> fields = MapEditorUtil.parseString(preferenceString);
		for (Entry<String, String> entry : fields) {
			TableItem item = new TableItem(customFieldsTable, SWT.NONE);
			String key = entry.getKey();
			// Column order must match column numbers used above
			String[] itemText = new String[]{"", key, entry.getValue()};
			item.setText(itemText);
			if( existingEnabledMap.containsKey(key)) {
				boolean previousEnabledValue = existingEnabledMap.get(key);
				item.setChecked(previousEnabledValue);
			} 
		}
		TableColumn[] columns = customFieldsTable.getColumns();
		for (TableColumn column : columns) {
			column.pack();
		}
	}

	private void addCustomFields(Message message) throws MarketceteraException {
		TableItem[] items = customFieldsTable.getItems();
		DataDictionary dictionary = FIXDataDictionaryManager.getDictionary();
		for (TableItem item : items) {
			if (item.getChecked()) {
				String key = item.getText(1);
				String value = item.getText(2);
				int fieldNumber = -1;
				try {
					fieldNumber = Integer.parseInt(key);
				} catch (Exception e) {
					try {
						fieldNumber = dictionary.getFieldTag(key);
					} catch (Exception ex) {

					}
				}
				if (fieldNumber > 0) {
					if (dictionary.isHeaderField(fieldNumber)) {
						insertFieldIfMissing(fieldNumber, value, message
								.getHeader());
					} else if (dictionary.isTrailerField(fieldNumber)) {
						insertFieldIfMissing(fieldNumber, value, message
								.getTrailer());
					} else if (dictionary.isField(fieldNumber)) {
						insertFieldIfMissing(fieldNumber, value, message);
					}
				} else {
					throw new MarketceteraException("Could not find field "
							+ key);
				}
			}
		}
	}

	private void insertFieldIfMissing(int fieldNumber, String value, FieldMap fieldMap) throws MarketceteraException {
		if (fieldMap.isSetField(fieldNumber)){
			throw new MarketceteraException("Field "+fieldNumber+" is already set in message.");
		} else {
			fieldMap.setField(new StringField(fieldNumber, value));
		}
	}

	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);
		
		this.viewStateMemento = memento;
	}

	@Override
	public void saveState(IMemento memento) {
		super.saveState(memento);
		
		TableItem[] items = customFieldsTable.getItems();
		for (int i = 0; i < items.length; i++) {
			TableItem item = items[i];
			String key = CUSTOM_FIELD_VIEW_SAVED_STATE_KEY_PREFIX + item.getText(1);
			memento.putInteger(key, (item.getChecked() ? 1 : 0));
		}
	}

	public void onQuote(Message message) {
		try {
			if (listenedSymbol!=null){
				String listenedSymbolString = listenedSymbol.toString();
				if (message.isSetField(Symbol.FIELD) &&
						listenedSymbolString.equals(message.getString(Symbol.FIELD))){
					bookComposite.onQuote(message);
				}
			}
		} catch (FieldNotFound e) {
			// Do nothing
		}
	}

}
