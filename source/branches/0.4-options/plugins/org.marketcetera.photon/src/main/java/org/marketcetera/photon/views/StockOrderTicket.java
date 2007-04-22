package org.marketcetera.photon.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.Section;
import org.marketcetera.photon.EclipseUtils;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.preferences.CustomOrderFieldPage;
import org.marketcetera.quickfix.FIXMessageUtil;

import quickfix.Message;

public class StockOrderTicket extends AbstractOrderTicket implements
		IStockOrderTicket {

	private static final String NEW_EQUITY_ORDER = "New Equity Order";

	private static final String REPLACE_EQUITY_ORDER = "Replace Equity Order";

	public static String ID = "org.marketcetera.photon.views.StockOrderTicket";

	private Section otherExpandableComposite;

	private Text accountText;

	private StockOrderTicketController stockOrderTicketController;

	public StockOrderTicket() {

	}

	@Override
	protected int getNumColumnsInForm() {
		return 5;
	}

	public StockOrderTicketController getStockOrderTicketController() {
		return stockOrderTicketController;
	}

	@Override
	protected void postCreatePartControl() {
		stockOrderTicketController = new StockOrderTicketController(this);
	}

	@Override
	public void dispose() {
		safelyDispose(stockOrderTicketController);

		super.dispose();
	}

	@Override
	protected void createFormContents() {
		Composite formBody = outermostForm.getBody();
		getFormToolkit().createLabel(formBody, "Side");
		getFormToolkit().createLabel(formBody, "Quantity");
		getFormToolkit().createLabel(formBody, "Symbol");
		getFormToolkit().createLabel(formBody, "Price");
		getFormToolkit().createLabel(formBody, "TIF");
		orderTicketViewPieces.createSideInput();
		orderTicketViewPieces.createQuantityInput();
		orderTicketViewPieces.createSymbolInput();
		orderTicketViewPieces.createPriceInput();
		orderTicketViewPieces.createTifInput();

		createSendAndCancelButtons();

		createOtherExpandableComposite();
		customFieldsViewPieces.createCustomFieldsExpandableComposite(3);
		createBookSection();

		customFieldsViewPieces.updateCustomFields(PhotonPlugin.getDefault()
				.getPreferenceStore().getString(
						CustomOrderFieldPage.CUSTOM_FIELDS_PREFERENCE));
	}

	/**
	 * This method initializes customFieldsExpandableComposite
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
		otherComposite.setLayout(new RowLayout(SWT.HORIZONTAL));
		getFormToolkit().createLabel(otherComposite, "Account:");
		accountText = getFormToolkit().createText(otherComposite, "");
		Point sizeHint = EclipseUtils.getTextAreaSize(accountText, null, 10,
				1.0);

		RowData accountTextRowData = new RowData();
		accountTextRowData.height = sizeHint.y;
		accountTextRowData.width = sizeHint.x;
		accountText.setLayoutData(accountTextRowData);
		orderTicketViewPieces.addInputControlErrorDecoration(accountText);

		getFormToolkit().paintBordersFor(otherComposite);
		otherExpandableComposite.setClient(otherComposite);
	}

	@Override
	protected void updateOutermostFormTitle(Message targetOrder) {
		checkOutermostFormInitialized();
		if (targetOrder == null
				|| !FIXMessageUtil.isCancelReplaceRequest(targetOrder)) {
			outermostForm.setText(NEW_EQUITY_ORDER);
		} else {
			outermostForm.setText(REPLACE_EQUITY_ORDER);
		}
	}

	public static StockOrderTicket getDefault() {
		return (StockOrderTicket) PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().findView(
						StockOrderTicket.ID);
	}

	public Text getAccountText() {
		return accountText;
	}
}
