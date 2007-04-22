package org.marketcetera.photon.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.Section;
import org.marketcetera.photon.EclipseUtils;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.parser.ILexerFIXImage;
import org.marketcetera.photon.parser.OpenCloseImage;
import org.marketcetera.photon.parser.OrderCapacityImage;
import org.marketcetera.photon.parser.PutOrCallImage;
import org.marketcetera.photon.preferences.CustomOrderFieldPage;
import org.marketcetera.quickfix.FIXMessageUtil;

import quickfix.Message;
import quickfix.field.Account;
import quickfix.field.OpenClose;
import quickfix.field.OrderCapacity;

/**
 * Option order ticket view.
 * 
 * @author andrei.lissovski@softwaregoodness.com
 */
public class OptionOrderTicket extends AbstractOrderTicket implements
		IOptionOrderTicket {

	public static String ID = "org.marketcetera.photon.views.OptionOrderTicket"; //$NON-NLS-1$

	private static final String NEW_OPTION_ORDER = "New Option Order";

	private static final String REPLACE_OPTION_ORDER = "Replace Option Order";

	private CCombo expireMonthCCombo = null;

	private Text strikeText = null;

	private CCombo putOrCallCCombo = null;

	private CCombo expireYearCCombo = null;

	private Section otherExpandableComposite;

	private Text accountText;

	private CCombo orderCapacityCCombo;

	private CCombo openCloseCCombo;

	private OptionOrderTicketController optionOrderTicketController;

	public OptionOrderTicket() {
	}

	public OptionOrderTicketController getOptionOrderTicketController() {
		return optionOrderTicketController;
	}

	@Override
	protected void postCreatePartControl() {
		optionOrderTicketController = new OptionOrderTicketController(this);
	}

	@Override
	public void dispose() {
		safelyDispose(optionOrderTicketController);

		super.dispose();
	}

	@Override
	protected void updateOutermostFormTitle(Message targetOrder) {
		checkOutermostFormInitialized();
		if (targetOrder == null
				|| !FIXMessageUtil.isCancelReplaceRequest(targetOrder)) {
			outermostForm.setText(NEW_OPTION_ORDER);
		} else {
			outermostForm.setText(REPLACE_OPTION_ORDER);
		}
	}

	@Override
	protected int getNumColumnsInForm() {
		return 9;
	}

	@Override
	protected void createFormContents() {
		Composite formBody = outermostForm.getBody();
		getFormToolkit().createLabel(formBody, "Side");
		getFormToolkit().createLabel(formBody, "Quantity");
		getFormToolkit().createLabel(formBody, "Symbol");
		getFormToolkit().createLabel(formBody, "Expiration");
		getFormToolkit().createLabel(formBody, "Strike");
		getFormToolkit().createLabel(formBody, "Year");
		getFormToolkit().createLabel(formBody, "C/P");
		getFormToolkit().createLabel(formBody, "Price");
		getFormToolkit().createLabel(formBody, "TIF");

		orderTicketViewPieces.createSideInput();
		orderTicketViewPieces.createQuantityInput();
		orderTicketViewPieces.createSymbolInput();
		createExpireMonthBorderComposite();
		createStrikeBorderComposite();
		createExpireYearBorderComposite();
		createPutOrCallBorderComposite();
		orderTicketViewPieces.createPriceInput();
		orderTicketViewPieces.createTifInput();

		createSendAndCancelButtons();

		createOtherExpandableComposite();
		customFieldsViewPieces.createCustomFieldsExpandableComposite(6);
		createBookSection();

		customFieldsViewPieces.updateCustomFields(PhotonPlugin.getDefault()
				.getPreferenceStore().getString(
						CustomOrderFieldPage.CUSTOM_FIELDS_PREFERENCE));

		outermostForm.pack(true);
	}

	private void createExpireMonthBorderComposite() {
		checkOutermostFormInitialized();
		expireMonthCCombo = new CCombo(outermostForm.getBody(), SWT.BORDER);
		// todo: Dynamically populate expiration choices from market data
		expireMonthCCombo.add("Sept");
		expireMonthCCombo.add("Dec");
	}

	private void addSelectAllFocusListener(Control control) {
		control.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				((Text) e.widget).selectAll();
			}
		});
	}

	private void createStrikeBorderComposite() {
		strikeText = getFormToolkit().createText(outermostForm.getBody(), null,
				SWT.SINGLE | SWT.BORDER);
		addSelectAllFocusListener(strikeText);

		GridData textGridData = new GridData();
		Point sizeHint = EclipseUtils
				.getTextAreaSize(strikeText, null, 10, 1.0);
		// textGridData.heightHint = sizeHint.y;
		textGridData.widthHint = sizeHint.x;
		strikeText.setLayoutData(textGridData);
	}

	private void createExpireYearBorderComposite() {
		expireYearCCombo = new CCombo(outermostForm.getBody(), SWT.BORDER);
		// todo: Dynamically populate year choices from market data.
		expireYearCCombo.add("07");
		expireYearCCombo.add("08");
	}

	private void createPutOrCallBorderComposite() {
		putOrCallCCombo = new CCombo(outermostForm.getBody(), SWT.BORDER);
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
				outermostForm.getBody(), Section.TITLE_BAR | Section.TWISTIE);
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

	private GridData createStandardSingleColumnGridData() {
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.LEFT;
		gridData.verticalAlignment = SWT.CENTER;
		gridData.horizontalSpan = 1;
		gridData.verticalSpan = 1;
		return gridData;
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

	public static OptionOrderTicket getDefault() {
		OptionOrderTicket orderTicket = (OptionOrderTicket) PlatformUI
				.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.findView(OptionOrderTicket.ID);

		return orderTicket;
	}

	public Text getAccountText() {
		return accountText;
	}

	public CCombo getExpireMonthCCombo() {
		return expireMonthCCombo;
	}

	public CCombo getExpireYearCCombo() {
		return expireYearCCombo;
	}

	public CCombo getOpenCloseCCombo() {
		return openCloseCCombo;
	}

	public CCombo getOrderCapacityCCombo() {
		return orderCapacityCCombo;
	}

	public CCombo getPutOrCallCCombo() {
		return putOrCallCCombo;
	}

	public Text getStrikeText() {
		return strikeText;
	}

}
