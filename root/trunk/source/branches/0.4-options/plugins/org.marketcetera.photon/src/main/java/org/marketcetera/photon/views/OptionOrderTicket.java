package org.marketcetera.photon.views;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.ViewPart;
import org.marketcetera.core.MarketceteraException;
import org.marketcetera.photon.EclipseUtils;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.parser.ILexerFIXImage;
import org.marketcetera.photon.parser.OpenCloseImage;
import org.marketcetera.photon.parser.OrderCapacityImage;
import org.marketcetera.photon.parser.PutOrCallImage;
import org.marketcetera.photon.parser.SideImage;
import org.marketcetera.photon.parser.TimeInForceImage;
import org.marketcetera.photon.preferences.CustomOrderFieldPage;
import org.marketcetera.photon.preferences.MapEditorUtil;
import org.marketcetera.photon.ui.BookComposite;
import org.marketcetera.photon.ui.validation.ControlDecoration;
import org.marketcetera.photon.ui.validation.IMessageDisplayer;
import org.marketcetera.quickfix.FIXDataDictionaryManager;
import org.marketcetera.quickfix.FIXMessageUtil;

import quickfix.DataDictionary;
import quickfix.Message;
import quickfix.field.Account;
import quickfix.field.OpenClose;
import quickfix.field.OrderCapacity;
import quickfix.field.StrikePrice;
import ca.odell.glazedlists.EventList;

/**
 * Option order ticket view.
 * 
 * @author andrei.lissovski@softwaregoodness.com
 */
public class OptionOrderTicket extends ViewPart implements IMessageDisplayer,
		IPropertyChangeListener, IOptionOrderTicket {

	public static String ID = "org.marketcetera.photon.views.OptionOrderTicketView"; //$NON-NLS-1$

	private static final String CONTROL_DECORATOR_KEY = "OPTION_ORDER_CONTROL_DECORATOR_KEY";

	private static final String NEW_OPTION_ORDER = "New Option Order";

	private static final String REPLACE_OPTION_ORDER = "Replace Option Order";

	private Composite top = null;

	private FormToolkit formToolkit = null;

	private ScrolledForm form = null;

	private Label errorMessageLabel = null;

	private Label sideLabel;

	private Label quantityLabel;

	private Label symbolLabel;

	private Label expireMonthLabel;

	private Label strikeLabel;

	private Label expireYearLabel;

	private Label putOrCallLabel;

	private Label priceLabel;

	private Label tifLabel;

	private Button sendButton;

	private Button cancelButton;

	private Text symbolText = null;

	private Composite sideBorderComposite = null;

	private CCombo sideCCombo = null;

	private Composite quantityBorderComposite = null;

	private Composite symbolBorderComposite = null;

	private Composite priceBorderComposite = null;

	private Composite tifBorderComposite = null;

	private Text quantityText = null;

	private Text priceText = null;

	private CCombo tifCCombo = null;

	private Composite expireMonthBorderComposite = null;

	private CCombo expireMonthCCombo = null;

	private Composite strikeBorderComposite = null;

	private Text strikeText = null;

	private CCombo putOrCallCCombo = null;

	private Composite putOrCallBorderComposite = null;

	private CCombo expireYearCCombo = null;

	private Composite expireYearBorderComposite = null;

	private ExpandableComposite customFieldsExpandableComposite = null;

	private Composite customFieldsComposite = null;

	private Table customFieldsTable = null;

	private CheckboxTableViewer tableViewer = null;

	private BookComposite bookComposite;

	private Section otherExpandableComposite;

	private Text accountText;

	private CCombo orderCapacityCCombo;

	private CCombo openCloseCCombo;

	private Section bookSection;

	private Image errorImage;

	private Image warningImage;

	private List<Control> inputControls = new LinkedList<Control>();

	public OptionOrderTicket() {
	}

	// todo: Duplicated code from StockOrderTicket
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

	// todo: Duplicated code from StockOrderTicket, partly modified
	@Override
	public void createPartControl(Composite parent) {
		FieldDecoration deco = FieldDecorationRegistry.getDefault()
				.getFieldDecoration(FieldDecorationRegistry.DEC_ERROR);
		errorImage = deco.getImage();
		deco = FieldDecorationRegistry.getDefault().getFieldDecoration(
				FieldDecorationRegistry.DEC_WARNING);
		warningImage = deco.getImage();

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
	}

	private void createForm() {
		GridLayout outerGridLayout = new GridLayout();
		outerGridLayout.numColumns = 9;
		outerGridLayout.marginWidth = 3;
		outerGridLayout.verticalSpacing = 1;
		outerGridLayout.horizontalSpacing = 1;
		outerGridLayout.marginHeight = 1;
		form = getFormToolkit().createScrolledForm(top);
		form.setText(NEW_OPTION_ORDER);
		form.getBody().setLayout(outerGridLayout);

		sideLabel = getFormToolkit().createLabel(form.getBody(), "Side");
		quantityLabel = getFormToolkit()
				.createLabel(form.getBody(), "Quantity");
		symbolLabel = getFormToolkit().createLabel(form.getBody(), "Symbol");
		expireMonthLabel = getFormToolkit().createLabel(form.getBody(),
				"Expiration");
		strikeLabel = getFormToolkit().createLabel(form.getBody(), "Strike");
		expireYearLabel = getFormToolkit().createLabel(form.getBody(), "Year");
		putOrCallLabel = getFormToolkit().createLabel(form.getBody(), "C/P");
		priceLabel = getFormToolkit().createLabel(form.getBody(), "Price");
		tifLabel = getFormToolkit().createLabel(form.getBody(), "TIF");

		createSideBorderComposite();
		createQuantityBorderComposite();
		createSymbolBorderComposite();
		createExpireMonthBorderComposite();
		createStrikeBorderComposite();
		createExpireYearBorderComposite();
		createPutOrCallBorderComposite();
		createPriceBorderComposite();
		createTifBorderComposite();

		assignLayoutDataForOrderEntryComposites();

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
		gd.horizontalSpan = outerGridLayout.numColumns;
		okCancelComposite.setLayoutData(gd);
		sendButton = getFormToolkit().createButton(okCancelComposite, "Send",
				SWT.PUSH);
		sendButton.setEnabled(false);
		cancelButton = getFormToolkit().createButton(okCancelComposite,
				"Cancel", SWT.PUSH);

		createOtherExpandableComposite();
		createCustomFieldsExpandableComposite();
		createBookComposite();

		updateCustomFields(PhotonPlugin.getDefault().getPreferenceStore()
				.getString(CustomOrderFieldPage.CUSTOM_FIELDS_PREFERENCE));
		// restoreCustomFieldStates();

		addAllInputControlDecorations();

		form.pack(true);
	}

	private void addAllInputControlDecorations() {
		addInputControl(expireMonthCCombo);
		addInputControl(openCloseCCombo);
		addInputControl(orderCapacityCCombo);
		addInputControl(priceText);
		addInputControl(putOrCallCCombo);
		addInputControl(quantityText);
		addInputControl(sideCCombo);
		addInputControl(strikeText);
		addInputControl(symbolText);
		addInputControl(tifCCombo);
		addInputControl(expireYearCCombo);
	}

	private void assignLayoutDataForOrderEntryComposites() {
		sideBorderComposite.setLayoutData(createStandardSingleColumnGridData());
		quantityBorderComposite
				.setLayoutData(createStandardSingleColumnGridData());
		// The symbolBorderComposite GridData is assigned in
		// createSymbolBorderComposite.

		expireMonthBorderComposite
				.setLayoutData(createStandardSingleColumnGridData());
		strikeBorderComposite
				.setLayoutData(createStandardSingleColumnGridData());
		expireYearBorderComposite.setLayoutData(createStandardSingleColumnGridData());
		putOrCallBorderComposite
				.setLayoutData(createStandardSingleColumnGridData());
		priceBorderComposite
				.setLayoutData(createStandardSingleColumnGridData());
		tifBorderComposite.setLayoutData(createStandardSingleColumnGridData());
	}

	private GridData createStandardSingleColumnGridData() {
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.LEFT;
		gridData.verticalAlignment = SWT.CENTER;
		gridData.horizontalSpan = 1;
		gridData.verticalSpan = 1;
		return gridData;
	}

	private void createExpireMonthBorderComposite() {
		expireMonthBorderComposite = getFormToolkit().createComposite(
				form.getBody());
		GridLayout gridLayout = createStandardBorderGridLayout();
		expireMonthBorderComposite.setLayout(gridLayout);
		expireMonthCCombo = new CCombo(expireMonthBorderComposite, SWT.BORDER);
		// todo: Dynamically populate expiration choices from market data
		expireMonthCCombo.add("Sept");
		expireMonthCCombo.add("Dec");

		// new CComboValidator(expirationCCombo, "Expiration", Arrays
		// .asList(expirationCCombo.getItems()), false);
		// new ParentColorHighlighter(expirationCCombo);

		// todo: FIXCComboExtractor can't handle the Expiration combo yet, since
		// we would need to initialize it when we receive new market data about
		// available expiration dates for the current symbol.

		// validator.register(expirationCCombo, true);
		// extractors.add(extractor);
	}

	private void initNumericTextBorderComposite(Composite composite, Text text,
			String fieldNameForValidator, int associatedFixField) {

		GridLayout gridLayout = createStandardBorderGridLayout();
		composite.setLayout(gridLayout);

		GridData textGridData = new GridData();
		Point sizeHint = EclipseUtils.getTextAreaSize(composite, null, 10, 1.0);
		// textGridData.heightHint = sizeHint.y;
		textGridData.widthHint = sizeHint.x;
		text.setLayoutData(textGridData);

		text.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				((Text) e.widget).selectAll();
			}
		});
	}

	private void createStrikeBorderComposite() {
		strikeBorderComposite = getFormToolkit()
				.createComposite(form.getBody());
		strikeText = getFormToolkit().createText(strikeBorderComposite, null,
				SWT.SINGLE | SWT.BORDER);

		initNumericTextBorderComposite(strikeBorderComposite, strikeText,
				"Strike", StrikePrice.FIELD);

	}

	private void createExpireYearBorderComposite() {
		expireYearBorderComposite = getFormToolkit().createComposite(form.getBody());
		GridLayout gridLayout = createStandardBorderGridLayout();
		expireYearBorderComposite.setLayout(gridLayout);
		expireYearCCombo = new CCombo(expireYearBorderComposite, SWT.BORDER);
		// todo: Dynamically populate year choices from market data.
		expireYearCCombo.add("07");
		expireYearCCombo.add("08");
	}

	private void createPutOrCallBorderComposite() {
		putOrCallBorderComposite = getFormToolkit().createComposite(
				form.getBody());
		GridLayout gridLayout = createStandardBorderGridLayout();
		putOrCallBorderComposite.setLayout(gridLayout);
		putOrCallCCombo = new CCombo(putOrCallBorderComposite, SWT.BORDER);
		putOrCallCCombo.add(PutOrCallImage.PUT.getImage());
		putOrCallCCombo.add(PutOrCallImage.CALL.getImage());
	}

	private GridLayout createStandardBorderGridLayout() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.marginWidth = 2;
		gridLayout.verticalSpacing = 2;
		gridLayout.horizontalSpacing = 2;
		gridLayout.marginHeight = 2;
		gridLayout.numColumns = 1;
		return gridLayout;
	}

	// todo: Duplicated code from StockOrderTicket
	private void addInputControl(Control control) {
		ControlDecoration cd = new ControlDecoration(control, SWT.LEFT
				| SWT.BOTTOM);
		cd.setMarginWidth(2);
		cd.setImage(errorImage);
		cd.hide();
		control.setData(CONTROL_DECORATOR_KEY, cd);
		inputControls.add(control);
	}

	// todo: Duplicated code from StockOrderTicket
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
	}

	// todo: Duplicated code from StockOrderTicket
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

		Point sizeHint = EclipseUtils.getTextAreaSize(quantityBorderComposite,
				null, 10, 1.0);

		GridData quantityTextGridData = new GridData();
		// quantityTextGridData.heightHint = sizeHint.y;
		quantityTextGridData.widthHint = sizeHint.x;
		quantityText.setLayoutData(quantityTextGridData);

		quantityText.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				((Text) e.widget).selectAll();
			}
		});

		quantityBorderComposite.pack(true);
	}

	// todo: Duplicated code from StockOrderTicket, changed horizontalSpan et al
	// on symbolBorderGridData
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
		symbolBorderGridData.horizontalSpan = 1;
		symbolBorderGridData.horizontalAlignment = SWT.FILL;
		symbolBorderGridData.grabExcessHorizontalSpace = true;
		symbolBorderGridData.verticalAlignment = SWT.CENTER;
		symbolBorderGridData.widthHint = 120;
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
	}

	// todo: Duplicated code from StockOrderTicket
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

		Point sizeHint = EclipseUtils.getTextAreaSize(priceBorderComposite,
				null, 10, 1.0);

		GridData quantityTextGridData = new GridData();
		// quantityTextGridData.heightHint = sizeHint.y;
		quantityTextGridData.widthHint = sizeHint.x;
		priceText.setLayoutData(quantityTextGridData);

		priceText.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				((Text) e.widget).selectAll();
			}
		});

	}

	// todo: Duplicated code from StockOrderTicket
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

	}

	// todo: Duplicated code from StockOrderTicket, changed horizontalSpan
	/**
	 * This method initializes customFieldsExpandableComposite
	 * 
	 */
	private void createCustomFieldsExpandableComposite() {
		GridData gridData3 = new GridData();
		gridData3.horizontalSpan = 7;
		gridData3.verticalAlignment = GridData.BEGINNING;
		gridData3.grabExcessHorizontalSpace = true;
		gridData3.horizontalAlignment = GridData.FILL;
		customFieldsExpandableComposite = getFormToolkit().createSection(
				form.getBody(), Section.TITLE_BAR | Section.TWISTIE);
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

	// todo: Duplicated code from StockOrderTicket
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

	// todo: Duplicated code from StockOrderTicket, substantially modified
	/**
	 * This method initializes customFieldsExpandableComposite
	 * 
	 */
	private void createOtherExpandableComposite() {
		GridData gridData3 = new GridData();
		gridData3.horizontalSpan = 2;
		gridData3.verticalAlignment = GridData.BEGINNING;
		// gridData3.grabExcessHorizontalSpace = true;
		gridData3.horizontalAlignment = GridData.FILL;
		otherExpandableComposite = getFormToolkit().createSection(
				form.getBody(), Section.TITLE_BAR | Section.TWISTIE);
		otherExpandableComposite.setText("Other");
		otherExpandableComposite.setExpanded(false);
		otherExpandableComposite.setLayoutData(gridData3);

		Composite otherComposite = getFormToolkit().createComposite(
				otherExpandableComposite);
		GridLayout otherGridLayout = createStandardBorderGridLayout();
		otherGridLayout.numColumns = 2;
		otherComposite.setLayout(otherGridLayout);
		otherExpandableComposite.setClient(otherComposite);

		Label accountLabel = getFormToolkit().createLabel(otherComposite,
				"Account:");
		accountLabel.setLayoutData(createStandardSingleColumnGridData());
		accountText = getFormToolkit().createText(otherComposite, "");
		addFixFieldEntry(otherComposite, accountText, Account.FIELD);

		Label openCloseLabel = getFormToolkit().createLabel(otherComposite,
				"Open/Close");
		openCloseLabel.setLayoutData(createStandardSingleColumnGridData());
		openCloseCCombo = createFixFieldImageComboEntry(otherComposite,
				"OpenClose", OpenClose.FIELD, OpenCloseImage.values());

		Label capacityLabel = getFormToolkit().createLabel(otherComposite,
				"Capacity");
		capacityLabel.setLayoutData(createStandardSingleColumnGridData());
		orderCapacityCCombo = createFixFieldImageComboEntry(otherComposite,
				"Capacity", OrderCapacity.FIELD, OrderCapacityImage.values());

	}

	private void addComboChoicesFromLexerEnum(CCombo combo,
			ILexerFIXImage[] choices) {
		for (ILexerFIXImage choice : choices) {
			combo.add(choice.getImage());
		}
	}

	private CCombo createFixFieldImageComboEntry(Composite parent,
			String fieldNameForValidator, int fixFieldNumber,
			ILexerFIXImage[] choices) {

		CCombo combo = new CCombo(parent, SWT.BORDER);
		combo.setLayoutData(createStandardSingleColumnGridData());
		addComboChoicesFromLexerEnum(combo, choices);

		return combo;
	}

	private void addFixFieldEntry(Composite targetComposite, Text textControl,
			int fixFieldNumber) {

		Point sizeHint = EclipseUtils.getTextAreaSize(targetComposite, null,
				10, 1.0);

		GridData textGridData = createStandardSingleColumnGridData();
		textGridData.widthHint = sizeHint.x;
		textGridData.heightHint = sizeHint.y;
		textControl.setLayoutData(textGridData);

		getFormToolkit().paintBordersFor(targetComposite);
	}

	// todo: Duplicated code from StockOrderTicket, changed horizontalSpan
	private void createBookComposite() {
		bookSection = getFormToolkit().createSection(form.getBody(),
				Section.TITLE_BAR);
		bookSection.setText("Market data");
		bookSection.setExpanded(true);

		GridLayout gridLayout = new GridLayout();
		GridData layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = true;
		layoutData.verticalAlignment = SWT.FILL;
		layoutData.horizontalAlignment = SWT.FILL;
		layoutData.horizontalSpan = 9;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.numColumns = 2;

		bookSection.setLayout(gridLayout);
		bookSection.setLayoutData(layoutData);

		bookComposite = new BookComposite(bookSection, SWT.NONE,
				getFormToolkit());
		bookSection.setClient(bookComposite);
	}

	// todo: Duplicated code from StockOrderTicket
	@Override
	public void dispose() {
		PhotonPlugin.getDefault().getPreferenceStore()
				.removePropertyChangeListener(this);
	}

	@Override
	public void setFocus() {
	}

	public static OptionOrderTicket getDefault() {
		return (OptionOrderTicket) PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().findView(
						OptionOrderTicket.ID);
	}

	// todo: Duplicated code from StockOrderTicket
	private void updateTitle(Message targetOrder) {
		if (targetOrder == null
				|| !FIXMessageUtil.isCancelReplaceRequest(targetOrder)) {
			form.setText(NEW_OPTION_ORDER);
		} else {
			form.setText(REPLACE_OPTION_ORDER);
		}
	}

	// todo: Duplicated code from StockOrderTicket
	public void clear() {
		updateTitle(null);
		symbolText.setEnabled(true);
		sendButton.setEnabled(false);
	}

	// todo: Duplicated code from StockOrderTicket
	public void updateMessage(Message aMessage) throws MarketceteraException {
		addCustomFields(aMessage);
	}

	// todo: Duplicated code from StockOrderTicket
	void addCustomFields(Message message) throws MarketceteraException {
		TableItem[] items = customFieldsTable.getItems();
		DataDictionary dictionary = FIXDataDictionaryManager
				.getCurrentFIXDataDictionary().getDictionary();
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
						FIXMessageUtil.insertFieldIfMissing(fieldNumber, value,
								message.getHeader());
					} else if (dictionary.isTrailerField(fieldNumber)) {
						FIXMessageUtil.insertFieldIfMissing(fieldNumber, value,
								message.getTrailer());
					} else if (dictionary.isField(fieldNumber)) {
						FIXMessageUtil.insertFieldIfMissing(fieldNumber, value,
								message);
					}
				} else {
					throw new MarketceteraException("Could not find field "
							+ key);
				}
			}
		}
	}

	// todo: Duplicated code from StockOrderTicket
	public void clearMessage() {
		errorMessageLabel.setText("");
	}

	// todo: Duplicated code from StockOrderTicket
	public void propertyChange(PropertyChangeEvent event) {
		String property = event.getProperty();
		if (CustomOrderFieldPage.CUSTOM_FIELDS_PREFERENCE.equals(property)) {
			String valueString = event.getNewValue().toString();
			updateCustomFields(valueString);
		}
	}

	// todo: Duplicated code from StockOrderTicket
	private void updateCustomFields(String preferenceString) {
		// Save previous enabled checkbox state
		final int keyColumnNum = 1;
		HashMap<String, Boolean> existingEnabledMap = new HashMap<String, Boolean>();
		TableItem[] existingItems = customFieldsTable.getItems();
		for (TableItem existingItem : existingItems) {
			String key = existingItem.getText(keyColumnNum);
			boolean checkedState = existingItem.getChecked();
			existingEnabledMap.put(key, checkedState);
		}

		customFieldsTable.setItemCount(0);
		EventList<Entry<String, String>> fields = MapEditorUtil
				.parseString(preferenceString);
		for (Entry<String, String> entry : fields) {
			TableItem item = new TableItem(customFieldsTable, SWT.NONE);
			String key = entry.getKey();
			// Column order must match column numbers used above
			String[] itemText = new String[] { "", key, entry.getValue() };
			item.setText(itemText);
			if (existingEnabledMap.containsKey(key)) {
				boolean previousEnabledValue = existingEnabledMap.get(key);
				item.setChecked(previousEnabledValue);
			}
		}
		TableColumn[] columns = customFieldsTable.getColumns();
		for (TableColumn column : columns) {
			column.pack();
		}
	}

	// todo: Duplicated code from StockOrderTicket
	public void clearErrors() {
		showErrorMessage("", 0);
		for (Control aControl : inputControls) {
			Object cd;
			if (((cd = aControl.getData(CONTROL_DECORATOR_KEY)) != null)
					&& cd instanceof ControlDecoration) {
				ControlDecoration controlDecoration = ((ControlDecoration) cd);
				controlDecoration.hide();
			}

		}
	}

	// todo: Duplicated code from StockOrderTicket
	public void showErrorForControl(Control aControl, int severity,
			String message) {
		Object cd;
		if (((cd = aControl.getData(CONTROL_DECORATOR_KEY)) != null)
				&& cd instanceof ControlDecoration) {
			ControlDecoration controlDecoration = ((ControlDecoration) cd);
			if (severity == IStatus.OK) {
				controlDecoration.hide();
			} else {
				if (severity == IStatus.ERROR) {
					controlDecoration.setImage(errorImage);
				} else {
					controlDecoration.setImage(warningImage);
				}
				if (message != null) {
					controlDecoration.setDescriptionText(message);
				}
				controlDecoration.show();
			}
		}
	}

	// todo: Duplicated code from StockOrderTicket
	public void showMessage(Message order) {
		symbolText.setEnabled(FIXMessageUtil.isOrderSingle(order));
		updateTitle(order);
	}

	// todo: Duplicated code from StockOrderTicket
	public void showErrorMessage(String errorMessage, int severity) {
		if (errorMessage == null) {
			errorMessageLabel.setText("");
		} else {
			errorMessageLabel.setText(errorMessage);
		}
		if (severity == IStatus.ERROR) {
			sendButton.setEnabled(false);
		} else {
			sendButton.setEnabled(true);
		}
	}

	public Text getAccountText() {
		return accountText;
	}

	public CCombo getExpireMonthCCombo() {
		return expireMonthCCombo;
	}

	public Text getPriceText() {
		return priceText;
	}

	public CCombo getPutOrCallCCombo() {
		return putOrCallCCombo;
	}

	public Text getQuantityText() {
		return quantityText;
	}

	public CCombo getSideCCombo() {
		return sideCCombo;
	}

	public Text getStrikeText() {
		return strikeText;
	}

	public Text getSymbolText() {
		return symbolText;
	}

	public CheckboxTableViewer getTableViewer() {
		return tableViewer;
	}

	public CCombo getTifCCombo() {
		return tifCCombo;
	}

	public CCombo getExpireYearCCombo() {
		return expireYearCCombo;
	}

	public Button getCancelButton() {
		return cancelButton;
	}

	public Button getSendButton() {
		return sendButton;
	}

	public BookComposite getBookComposite() {
		return bookComposite;
	}

	public Label getErrorMessageLabel() {
		return errorMessageLabel;
	}

	public CCombo getOrderCapacityCCombo() {
		return orderCapacityCCombo;
	}

	public CCombo getOpenCloseCCombo() {
		return openCloseCCombo;
	}

}
