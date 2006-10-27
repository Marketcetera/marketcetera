package org.marketcetera.photon.views;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.ViewPart;
import org.marketcetera.core.InternalID;
import org.marketcetera.core.MSymbol;
import org.marketcetera.core.FeedComponent.FeedStatus;
import org.marketcetera.photon.Application;
import org.marketcetera.photon.parser.SideImage;
import org.marketcetera.photon.parser.TimeInForceImage;
import org.marketcetera.photon.preferences.CustomOrderFieldPage;
import org.marketcetera.photon.ui.AbstractFIXExtractor;
import org.marketcetera.photon.ui.BookComposite;
import org.marketcetera.photon.ui.CComboValidator;
import org.marketcetera.photon.ui.FIXCComboExtractor;
import org.marketcetera.photon.ui.FIXTextExtractor;
import org.marketcetera.photon.ui.FormValidator;
import org.marketcetera.photon.ui.IMessageDisplayer;
import org.marketcetera.photon.ui.NumericTextValidator;
import org.marketcetera.photon.ui.ParentColorHighlighter;
import org.marketcetera.photon.ui.PriceTextValidator;
import org.marketcetera.photon.ui.TextValidator;
import org.marketcetera.quickfix.FIXDataDictionaryManager;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.quotefeed.IQuoteFeed;

import quickfix.Message;
import quickfix.field.MsgType;
import quickfix.field.OrderQty;
import quickfix.field.Price;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.field.TimeInForce;

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

		Application.getPreferenceStore().addPropertyChangeListener(this);
	}

	@Override
	public void dispose() {
		Application.getPreferenceStore().removePropertyChangeListener(this);
		unlisten();
		IQuoteFeed quoteFeed = Application.getQuoteFeed();
		if (quoteFeed != null) {
			quoteFeed.unlistenLevel2(listenedSymbol, bookComposite);
		}
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
			IQuoteFeed quoteFeed = Application.getQuoteFeed();
			MSymbol newListenedSymbol = new MSymbol(symbol);
			if (quoteFeed != null
					&& quoteFeed.getFeedStatus() == FeedStatus.AVAILABLE
					&& !newListenedSymbol.equals(listenedSymbol)) {
				quoteFeed.listenLevel2(newListenedSymbol, bookComposite);
				listenedSymbol = newListenedSymbol;
			}
		}
	}

	protected void unlisten() {
		IQuoteFeed quoteFeed = Application.getQuoteFeed();
		if (quoteFeed != null
				&& quoteFeed.getFeedStatus() == FeedStatus.AVAILABLE) {
			if (listenedSymbol != null) {
				quoteFeed.unlistenLevel2(listenedSymbol, bookComposite);
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
				| SWT.CHECK);
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
		getFormToolkit().paintBordersFor(otherComposite);
		otherExpandableComposite.setClient(otherComposite);
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

	protected void handleSend() {
		try {
			validator.validateAll();
			Message aMessage;
			if (targetOrder == null) {
				String orderID = Application.getIDFactory().getNext();
				aMessage = FIXMessageUtil
						.newLimitOrder(new InternalID(orderID), Side.BUY,
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
			Application.getOrderManager().handleInternalMessage(aMessage);
			clear();
		} catch (Exception e) {
			Application.getMainConsoleLogger().error(
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
	}
	
	private void updateTitle()
	{
		if (FIXMessageUtil.isReplaceRequest(targetOrder)){
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
		if (CustomOrderFieldPage.CUSTOM_FIELDS_PREFERENCE.equals(event.getProperty())){
			System.out.println("GOT HERE");
		}
	}

}
