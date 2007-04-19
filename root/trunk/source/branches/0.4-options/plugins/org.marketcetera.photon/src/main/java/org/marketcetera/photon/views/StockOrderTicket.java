package org.marketcetera.photon.views;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.ViewPart;
import org.marketcetera.core.MarketceteraException;
import org.marketcetera.photon.EclipseUtils;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.preferences.CustomOrderFieldPage;
import org.marketcetera.photon.ui.BookComposite;
import org.marketcetera.photon.ui.validation.IMessageDisplayer;
import org.marketcetera.quickfix.FIXMessageUtil;

import quickfix.Message;

public class StockOrderTicket extends ViewPart implements IMessageDisplayer,
		IPropertyChangeListener, IStockOrderTicket {

	private static final String NEW_EQUITY_ORDER = "New Equity Order";

	private static final String REPLACE_EQUITY_ORDER = "Replace Equity Order";

	public static String ID = "org.marketcetera.photon.views.StockOrderTicket";

	private Composite top = null;

	private FormToolkit formToolkit = null; // @jve:decl-index=0:visual-constraint=""

	private ScrolledForm form = null;

	private OrderTicketViewPieces orderTicketViewPieces;

	private CustomFieldsViewPieces customFieldsViewPieces;

	private BookViewPieces bookViewPieces;

	private Label errorMessageLabel = null;

	private Button sendButton;

	private Button cancelButton;

	private Section otherExpandableComposite;

	private Text accountText;

	private IMemento viewStateMemento;

	private static final String CUSTOM_FIELD_VIEW_SAVED_STATE_KEY_PREFIX = "CUSTOM_FIELD_CHECKED_STATE_OF_";

	public StockOrderTicket() {

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

	}

	@Override
	public void dispose() {
		PhotonPlugin.getDefault().getPreferenceStore()
				.removePropertyChangeListener(this);
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
		gridLayout.marginWidth = 6;
		gridLayout.verticalSpacing = 1;
		// The horizontalSpacing needs to be wide enough to show the error image
		// in ControlDecoration.
		gridLayout.horizontalSpacing = 6;
		gridLayout.marginHeight = 1;
		form = getFormToolkit().createScrolledForm(top);
		form.setText(NEW_EQUITY_ORDER);
		form.getBody().setLayout(gridLayout);
		GridData formGridData = new GridData();
		formGridData.grabExcessHorizontalSpace = true;
		formGridData.horizontalAlignment = GridData.FILL;
		formGridData.grabExcessVerticalSpace = true;
		formGridData.verticalAlignment = GridData.FILL;
		form.setLayoutData(formGridData);

		orderTicketViewPieces = new OrderTicketViewPieces(form.getBody(),
				getFormToolkit());
		customFieldsViewPieces = new CustomFieldsViewPieces(form.getBody(),
				getFormToolkit());
		bookViewPieces = new BookViewPieces(form.getBody(), getFormToolkit());

		getFormToolkit().createLabel(form.getBody(), "Side");
		getFormToolkit().createLabel(form.getBody(), "Quantity");
		getFormToolkit().createLabel(form.getBody(), "Symbol");
		getFormToolkit().createLabel(form.getBody(), "Price");
		getFormToolkit().createLabel(form.getBody(), "TIF");
		orderTicketViewPieces.createSideBorderComposite();
		orderTicketViewPieces.createQuantityBorderComposite();
		orderTicketViewPieces.createSymbolBorderComposite();
		orderTicketViewPieces.createPriceBorderComposite();
		orderTicketViewPieces.createTifBorderComposite();
		// createCustomFieldsExpandableComposite();

		Composite okCancelComposite = getFormToolkit().createComposite(
				form.getBody());
		okCancelComposite.setLayout(new RowLayout(SWT.HORIZONTAL));
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_END);
		gd.horizontalSpan = 5;
		okCancelComposite.setLayoutData(gd);
		sendButton = getFormToolkit().createButton(okCancelComposite, "Send",
				SWT.PUSH);
		sendButton.setEnabled(false);
		cancelButton = getFormToolkit().createButton(okCancelComposite,
				"Cancel", SWT.PUSH);

		customFieldsViewPieces.createCustomFieldsExpandableComposite();
		customFieldsViewPieces.getCustomFieldsExpandableComposite()
				.addExpansionListener(new ExpansionAdapter() {
					@Override
					public void expansionStateChanging(ExpansionEvent e) {
						form.reflow(true);
					}

					public void expansionStateChanged(ExpansionEvent e) {
						form.reflow(true);
					}
				});

		createOtherExpandableComposite();
		bookViewPieces.createBookComposite();

		customFieldsViewPieces.updateCustomFields(PhotonPlugin.getDefault()
				.getPreferenceStore().getString(
						CustomOrderFieldPage.CUSTOM_FIELDS_PREFERENCE));
		restoreCustomFieldStates();
	}

	private void restoreCustomFieldStates() {
		if (viewStateMemento == null)
			return;

		customFieldsViewPieces.restoreCustomFieldsTableItemCheckedState(
				viewStateMemento, CUSTOM_FIELD_VIEW_SAVED_STATE_KEY_PREFIX);
	}

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
		otherComposite.setLayout(new RowLayout(SWT.HORIZONTAL));
		getFormToolkit().createLabel(otherComposite, "Account:");
		accountText = getFormToolkit().createText(otherComposite, "");
		Point sizeHint = EclipseUtils.getTextAreaSize(accountText, null, 10,
				1.0);

		RowData accountTextRowData = new RowData();
		accountTextRowData.height = sizeHint.y;
		accountTextRowData.width = sizeHint.x;
		accountText.setLayoutData(accountTextRowData);
		orderTicketViewPieces.addInputControl(accountText);

		getFormToolkit().paintBordersFor(otherComposite);
		otherExpandableComposite.setClient(otherComposite);
	}

	public void clear() {
		updateTitle(null);
		orderTicketViewPieces.getSymbolText().setEnabled(true);
		sendButton.setEnabled(false);
	}

	public void updateMessage(Message aMessage) throws MarketceteraException {
		customFieldsViewPieces.addCustomFields(aMessage);
	}

	public void clearMessage() {
		errorMessageLabel.setText("");
	}

	public void showMessage(Message order) {
		orderTicketViewPieces.getSymbolText().setEnabled(
				FIXMessageUtil.isOrderSingle(order));
		updateTitle(order);
	}

	private void updateTitle(Message targetOrder) {
		if (targetOrder == null
				|| !FIXMessageUtil.isCancelReplaceRequest(targetOrder)) {
			form.setText(NEW_EQUITY_ORDER);
		} else {
			form.setText(REPLACE_EQUITY_ORDER);
		}
	}

	public static StockOrderTicket getDefault() {
		return (StockOrderTicket) PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().findView(
						StockOrderTicket.ID);
	}

	public void propertyChange(PropertyChangeEvent event) {
		String property = event.getProperty();
		if (CustomOrderFieldPage.CUSTOM_FIELDS_PREFERENCE.equals(property)) {
			String valueString = event.getNewValue().toString();
			customFieldsViewPieces.updateCustomFields(valueString);
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

		customFieldsViewPieces.saveState(memento,
				CUSTOM_FIELD_VIEW_SAVED_STATE_KEY_PREFIX);
	}

	public Text getAccountText() {
		return accountText;
	}

	public BookComposite getBookComposite() {
		return bookViewPieces.getBookComposite();
	}

	public Button getCancelButton() {
		return cancelButton;
	}

	public Table getCustomFieldsTable() {
		return customFieldsViewPieces.getCustomFieldsTable();
	}

	public Label getErrorMessageLabel() {
		return errorMessageLabel;
	}

	public Text getPriceText() {
		return orderTicketViewPieces.getPriceText();
	}

	public Text getQuantityText() {
		return orderTicketViewPieces.getQuantityText();
	}

	public Button getSendButton() {
		return sendButton;
	}

	public CCombo getSideCCombo() {
		return orderTicketViewPieces.getSideCCombo();
	}

	public Text getSymbolText() {
		return orderTicketViewPieces.getSymbolText();
	}

	public CheckboxTableViewer getTableViewer() {
		return customFieldsViewPieces.getTableViewer();
	}

	public CCombo getTifCCombo() {
		return orderTicketViewPieces.getTifCCombo();
	}

	public void showErrorMessage(String errorMessage, int severity) {
		orderTicketViewPieces.showErrorMessage(errorMessage, severity,
				errorMessageLabel, sendButton);
	}

	public void clearErrors() {
		showErrorMessage("", 0);
		orderTicketViewPieces.clearErrors();
	}

	public void showErrorForControl(Control aControl, int severity,
			String message) {
		orderTicketViewPieces.showErrorForControl(aControl, severity, message);
	}

}
