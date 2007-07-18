package org.marketcetera.photon.views;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.marketcetera.photon.EclipseUtils;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.parser.ILexerFIXImage;
import org.marketcetera.photon.parser.OpenCloseImage;
import org.marketcetera.photon.parser.OrderCapacityImage;
import org.marketcetera.photon.parser.PutOrCallImage;
import org.marketcetera.photon.preferences.CustomOrderFieldPage;
import org.marketcetera.photon.ui.IBookComposite;
import org.marketcetera.photon.ui.OptionBookComposite;
import org.marketcetera.quickfix.FIXMessageUtil;

import quickfix.Message;
import quickfix.field.Account;
import quickfix.field.OpenClose;
import quickfix.field.OrderCapacity;
import quickfix.field.PutOrCall;

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

	private Composite inputControlsComposite;
	
	private Text optionSymbolControl;

	private Combo expireMonthCombo;

	private Combo strikePriceControl;

	private Combo putOrCallCombo;

	private Combo expireYearCombo;

	private Section otherExpandableComposite;

	private Text accountText;

	private Combo orderCapacityCombo;

	private Combo openCloseCombo;

	private OptionOrderTicketController optionOrderTicketController;

	private OptionDateHelper optionContractDateHelper = new OptionDateHelper();
	
	private Label optionRootLabel;
	
	private Label optionContractSymbolLabel;

	
	public OptionOrderTicket() {
	}

	public IOrderTicketController getOrderTicketController() {
		return optionOrderTicketController;
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		optionOrderTicketController = new OptionOrderTicketController();
		optionOrderTicketController.bind(this);
	}

	@Override
	public void saveState(IMemento memento) {
		super.saveState(memento);		
	}

	@Override
	public void dispose() {
		safelyDispose(optionOrderTicketController);
		getBookComposite().dispose();
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
		return 4;
	}

	
	@Override
	protected void createViewPieces() {
		checkOutermostFormInitialized();
		inputControlsComposite = getFormToolkit().createComposite(
				outermostForm.getBody());
		orderTicketViewPieces = new OrderTicketViewPieces(
				inputControlsComposite, getFormToolkit());
		customFieldsViewPieces = new CustomFieldsViewPieces(outermostForm
				.getBody(), getFormToolkit());
	}

	@Override
	protected void createFormContents() {
		createOrderTicketInputControls();
		createOtherExpandableComposite();
		customFieldsViewPieces.createCustomFieldsExpandableComposite(6);
	
		createBookSection();

		customFieldsViewPieces.updateCustomFields(PhotonPlugin.getDefault()
				.getPreferenceStore().getString(
						CustomOrderFieldPage.CUSTOM_FIELDS_PREFERENCE));

		outermostForm.pack(true);

//		adjustTabOrder();
	}

	@Override
	protected GridData createSendAndCancelGridData() {
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_END);
		gd.horizontalSpan = getNumColumnsInOrderTicketInputControlsLayout();
		return gd;
	}
	
	private void createOrderTicketInputControls() {
		Composite parent = orderTicketViewPieces.getDefaultParent();
		
		// First row
		getFormToolkit().createLabel(parent, "Side");
		getFormToolkit().createLabel(parent, "Quantity");
		optionRootLabel = getFormToolkit().createLabel(parent, "Symbol/Root");
		optionContractSymbolLabel = getFormToolkit().createLabel(parent, "Option Symbol");

		orderTicketViewPieces.createSideInput();
		orderTicketViewPieces.createQuantityInput(6);
		orderTicketViewPieces.createSymbolInput();
		createOptionSymbolControl(parent);
		
		// Second row
		getFormToolkit().createLabel(parent, "Expiration");
		getFormToolkit().createLabel(parent, "Year");
		getFormToolkit().createLabel(parent, "Strike");
		getFormToolkit().createLabel(parent, "C/P");
		getFormToolkit().createLabel(parent, "Price");
		getFormToolkit().createLabel(parent, "TIF");

		createExpireMonthBorderComposite(parent);
		createExpireYearBorderComposite(parent);
		createStrikeBorderComposite(parent);
		createPutOrCallBorderComposite(parent);
		orderTicketViewPieces.createPriceInput();
		orderTicketViewPieces.createTifInput();

		createSendAndCancelButtons(parent);
		
		assignOrderTicketInputControlsLayout(parent);
	}
	
	private int getNumColumnsInOrderTicketInputControlsLayout() {
		return 6;
	}
	
	private void assignOrderTicketInputControlsLayout(Composite parent) {
		{
			GridLayout layout = new GridLayout();
			layout.numColumns = getNumColumnsInOrderTicketInputControlsLayout();
			parent.setLayout(layout);
		}
		{
			GridData layoutData = orderTicketViewPieces.createDefaultGridData(null, 0);
			layoutData.horizontalSpan = getNumColumnsInForm();
			parent.setLayoutData(layoutData);
		}
		
		assignDefaultGridDataWithHorizontalSpan(optionRootLabel,
				optionRootLabel.getText().length() + 1, 2);
		assignDefaultGridDataWithHorizontalSpan(orderTicketViewPieces
				.getSymbolText(), orderTicketViewPieces.getSymbolText()
				.getText().length() + 1, 2);

		assignDefaultGridDataWithHorizontalSpan(optionContractSymbolLabel,
				optionContractSymbolLabel.getText().length() + 1, 2);
		assignDefaultGridDataWithHorizontalSpan(optionSymbolControl,
				optionSymbolControl.getText().length() + 1, 2);
	}

	private void assignDefaultGridDataWithHorizontalSpan(Control whichControl,
			int charWidthHint, int horizontalSpan) {
		GridData layoutData = orderTicketViewPieces.createDefaultGridData(
				whichControl, charWidthHint);
		layoutData.horizontalSpan = 2;
		whichControl.setLayoutData(layoutData);
	}
	
	// todo: Remove this method if it remains unused.
//	private void adjustTabOrder() {
//		{
//			ArrayList<Control> tabOrder = new ArrayList<Control>();
//			tabOrder.add(orderTicketViewPieces.getSideCombo());
//			tabOrder.add(orderTicketViewPieces.getQuantityText());
//			tabOrder.add(orderTicketViewPieces.getSymbolText());
//			tabOrder.add(expireMonthCombo);
//			tabOrder.add(strikePriceControl);
//			tabOrder.add(expireYearCombo);
//			tabOrder.add(putOrCallCombo);
//			tabOrder.add(orderTicketViewPieces.getPriceText());
//			tabOrder.add(orderTicketViewPieces.getTifCombo());
//
//			tabOrder.add(sendButton.getParent());
//			tabOrder.add(otherExpandableComposite);
//			tabOrder.add(orderTicketViewPieces.getSideCombo());
//
//			Composite parent = orderTicketViewPieces.getSideCombo().getParent();
//			parent.setTabList(tabOrder.toArray(new Control[0]));
//		}
//
//		{
//			ArrayList<Control> tabOrder = new ArrayList<Control>();
//			tabOrder.add(openCloseCombo);
//			tabOrder.add(orderCapacityCombo);
//			Composite parent = openCloseCombo.getParent();
//			parent.setTabList(tabOrder.toArray(new Control[0]));
//		}
//
//		{
//			ArrayList<Control> tabOrder = new ArrayList<Control>();
//			tabOrder.add(sendButton);
//			tabOrder.add(cancelButton);
//			Composite parent = sendButton.getParent();
//			parent.setTabList(tabOrder.toArray(new Control[0]));
//		}
//	}

	private void createOptionSymbolControl(Composite parent) {
		optionSymbolControl = getFormToolkit().createText(
				parent, null, SWT.READ_ONLY | SWT.SINGLE );
		orderTicketViewPieces.assignDefaultGridData(optionSymbolControl, 6);
	}

	private void createExpireMonthBorderComposite(Composite parent) {
		checkOutermostFormInitialized();
		expireMonthCombo = new Combo(parent, SWT.BORDER);
		orderTicketViewPieces.assignDefaultGridData(expireMonthCombo, 5);
		// todo: Dynamically populate expiration choices from market data
		List<String> monthStrings = optionContractDateHelper
				.createDefaultMonths();
		for (String monthStr : monthStrings) {
			expireMonthCombo.add(monthStr);
		}

		orderTicketViewPieces.addInputControlErrorDecoration(expireMonthCombo);
	}

	private void createStrikeBorderComposite(Composite parent) {
		strikePriceControl = new Combo(parent, SWT.BORDER);
		orderTicketViewPieces.assignDefaultComboGridData(strikePriceControl, "STR");  //$NON-NLS-1$
		// addSelectAllFocusListener(strikePriceControl);

		orderTicketViewPieces
				.addInputControlErrorDecoration(strikePriceControl);
	}

	private void createExpireYearBorderComposite(Composite parent) {
		expireYearCombo = new Combo(parent, SWT.BORDER);
		orderTicketViewPieces.assignDefaultComboGridData(expireYearCombo, "2009");  //$NON-NLS-1$
		// todo: Dynamically populate year choices from market data.
		List<String> years = optionContractDateHelper.createDefaultYears();
		for (String year : years) {
			expireYearCombo.add(year.toString());
		}

		orderTicketViewPieces.addInputControlErrorDecoration(expireYearCombo);
	}

	private void createPutOrCallBorderComposite(Composite parent) {
		putOrCallCombo = new Combo(parent, SWT.BORDER);
		putOrCallCombo.add(PutOrCallImage.PUT.getImage());
		putOrCallCombo.add(PutOrCallImage.CALL.getImage());

		orderTicketViewPieces.assignDefaultComboGridData(putOrCallCombo, PutOrCallImage.CALL.getImage());

		orderTicketViewPieces.addInputControlErrorDecoration(putOrCallCombo);
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
		otherExpandableComposite.setExpanded(true);
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
		openCloseCombo = createFixFieldImageComboEntry(otherComposite,
				"OpenClose", OpenClose.FIELD, OpenCloseImage.values());
		orderTicketViewPieces.addInputControlErrorDecoration(openCloseCombo);

		Label capacityLabel = getFormToolkit().createLabel(otherComposite,
				"Capacity");
		capacityLabel.setLayoutData(createStandardSingleColumnGridData());
		orderCapacityCombo = createFixFieldImageComboEntry(otherComposite,
				"Capacity", OrderCapacity.FIELD, OrderCapacityImage.values());
		orderTicketViewPieces
				.addInputControlErrorDecoration(orderCapacityCombo);
	}

	private void addComboChoicesFromLexerEnum(Combo combo,
			ILexerFIXImage[] choices) {
		for (ILexerFIXImage choice : choices) {
			combo.add(choice.getImage());
		}
	}

	private Combo createFixFieldImageComboEntry(Composite parent,
			String fieldNameForValidator, int fixFieldNumber,
			ILexerFIXImage[] choices) {

		Combo combo = new Combo(parent, SWT.BORDER);
		combo.setLayoutData(createStandardSingleColumnGridData());
		addComboChoicesFromLexerEnum(combo, choices);

		return combo;
	}

	private GridData createStandardSingleColumnGridData() {
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
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

	public Text getOptionSymbolControl() {
		return optionSymbolControl;
	}

	public Text getAccountText() {
		return accountText;
	}

	public Combo getExpireMonthCombo() {
		return expireMonthCombo;
	}

	public Combo getExpireYearCombo() {
		return expireYearCombo;
	}

	public Combo getOpenCloseCombo() {
		return openCloseCombo;
	}

	public Combo getOrderCapacityCombo() {
		return orderCapacityCombo;
	}

	public Combo getPutOrCallCombo() {
		return putOrCallCombo;
	}

	public Integer getPutOrCall() {
		String putOrCallStr = putOrCallCombo.getText();
		if (PutOrCallImage.PUT.getImage().equals(putOrCallStr)) {
			return PutOrCall.PUT;
		} else if (PutOrCallImage.CALL.getImage().equals(putOrCallStr)) {
			return PutOrCall.CALL;
		}
		return null;
	}
	
	public void setPutOrCall(Integer putOrCall) {
		String putOrCallStr = "";
		if (putOrCall == PutOrCall.PUT){
			putOrCallStr = PutOrCallImage.PUT.getImage();
		} else if (putOrCall == PutOrCall.CALL){
			putOrCallStr = PutOrCallImage.CALL.getImage();
		}
		putOrCallCombo.setText(putOrCallStr);
	}

	public Combo getStrikePriceControl() {
		return strikePriceControl;
	}
		
	@Override
	protected IBookComposite createBookComposite(Composite parent, int style, FormToolkit formToolkit, IWorkbenchPartSite site, IMemento viewStateMemento) {		
		return new OptionBookComposite(getBookSection(), style, formToolkit, site, viewStateMemento);		
	}

	@Override
	protected void setBookCompositeBackground(IBookComposite book) {
		if (book instanceof OptionBookComposite) {
			OptionBookComposite bookComposite = (OptionBookComposite) book;
			bookComposite.setBackground(getBookSection().getBackground());
		}
	}

	@Override
	protected void setBookSectionClient(IBookComposite book) {
		if (book instanceof OptionBookComposite) {
			getBookSection().setClient((OptionBookComposite) book);			
		}
	}

	@Override
	public String toString() {
		try {
			StringBuilder rval = new StringBuilder();
			rval.append(getSideCombo().getText());
			rval.append(" ");
			rval.append(getQuantityText().getText());
			rval.append(" ");
			rval.append(getSymbolText().getText());
			rval.append(" ");
			rval.append(getOptionSymbolControl().getText());
			rval.append(" ");
			rval.append(getExpireMonthCombo().getText());
			rval.append(" ");
			rval.append(getExpireYearCombo().getText());
			rval.append(" ");
			rval.append(getStrikePriceControl().getText());
			rval.append(" ");
			rval.append(getPutOrCallCombo().getText());
			rval.append(" ");
			rval.append(getTifCombo().getText());
			rval.append(" ");
			rval.append(getOpenCloseCombo().getText());
			rval.append(" ");
			rval.append(getOrderCapacityCombo().getText());
			rval.append(" (");
			rval.append(super.toString());
			rval.append(")");
			return rval.toString();
		}
		catch(Exception anyException ) {
			return super.toString();
		}
	}
}
