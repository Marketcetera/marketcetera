package org.marketcetera.photon.views;

import org.eclipse.jface.util.Assert;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.ViewPart;
import org.marketcetera.core.MarketceteraException;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.preferences.CustomOrderFieldPage;
import org.marketcetera.photon.ui.BookComposite;
import org.marketcetera.photon.ui.IBookComposite;
import org.marketcetera.photon.ui.validation.IMessageDisplayer;
import org.marketcetera.quickfix.FIXMessageUtil;

import quickfix.Message;

public abstract class AbstractOrderTicket extends ViewPart implements
		IOrderTicket, IMessageDisplayer, IPropertyChangeListener {

	private static final String CUSTOM_FIELD_VIEW_SAVED_STATE_KEY_PREFIX = "CUSTOM_FIELD_CHECKED_STATE_OF_";

	protected Composite outermostComposite;

	protected ScrolledForm outermostForm;

	protected FormToolkit formToolkit;

	protected Label errorMessageLabel;
	
	protected Label errorIconLabel;

	protected OrderTicketViewPieces orderTicketViewPieces;

	protected CustomFieldsViewPieces customFieldsViewPieces;

	protected Button sendButton;

	protected Button cancelButton;

	protected IMemento viewStateMemento;
	
	private IBookComposite bookComposite;

	private Section bookSection;

	/**
	 * Set the human readable title on the outermostForm.
	 * 
	 * @param targetOrder the order on which to base the update. When null, the title should be updated to its default.
	 */
	protected abstract void updateOutermostFormTitle( Message targetOrder );

	/**
	 * Create the contents of the ScrolledForm. Call outermostForm.getBody() to
	 * get the container occupying the body of the form and use that as the parent for the controls.
	 */
	protected abstract void createFormContents();
	
	/**
	 * @return the number of columns in the grid for the outermostForm.
	 */
	protected abstract int getNumColumnsInForm();

	@Override
	public void createPartControl(Composite parent) {

		createTopComposite(parent);

		createForm();
		createViewPieces();
		createFormContents();
		createErrorIconLabel();
		createErrorLabel();
		addCustomFieldsExpansionListener();

		restoreCustomFieldStates();

		PhotonPlugin plugin = PhotonPlugin.getDefault();
		plugin.getPreferenceStore().addPropertyChangeListener(this);
	}
	
	protected void createTopComposite(Composite parent) {
		outermostComposite = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		outermostComposite.setLayout(gridLayout);
	}

	protected void createForm() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = getNumColumnsInForm();
		gridLayout.marginWidth = 6;
		gridLayout.verticalSpacing = 1;
		// The horizontalSpacing needs to be wide enough to show the error image
		// in ControlDecoration.
		gridLayout.horizontalSpacing = 10;
		gridLayout.marginHeight = 1;
		outermostForm = getFormToolkit().createScrolledForm(outermostComposite);
		outermostForm.getBody().setLayout(gridLayout);
		updateOutermostFormTitle( null );

		GridData formGridData = new GridData();
		formGridData.grabExcessHorizontalSpace = true;
		formGridData.horizontalAlignment = GridData.FILL;
		formGridData.grabExcessVerticalSpace = true;
		formGridData.verticalAlignment = GridData.FILL;
		formGridData.horizontalSpan = 2;
		outermostForm.setLayoutData(formGridData);
	}

	/**
	 * Check that the outermostForm is initialized and throw an exception if
	 * not.
	 * 
	 * @throws org.eclipse.jface.util.AssertionFailedException
	 *             if outermostForm is null.
	 */
	protected void checkOutermostFormInitialized() {
		Assert.isNotNull(outermostForm, "Form was not yet initialized."); //$NON-NLS-1$
	}

	protected void createViewPieces() {
		checkOutermostFormInitialized();
		orderTicketViewPieces = new OrderTicketViewPieces(outermostForm
				.getBody(), getFormToolkit());
		customFieldsViewPieces = new CustomFieldsViewPieces(outermostForm
				.getBody(), getFormToolkit());
	}
	
	/**
	 * Create the market data book section. 
	 * <p>
	 * This method is not called automatically and derived classes should invoke it.
	 * </p> 
	 */
	protected void createBookSection() {
		bookSection = getFormToolkit().createSection(outermostForm
				.getBody(),
				Section.TITLE_BAR);
		bookSection.setText("Market data");
		bookSection.setExpanded(true);

		GridLayout gridLayout = new GridLayout();
		GridData layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = true;
		layoutData.verticalAlignment = SWT.FILL;
		layoutData.horizontalAlignment = SWT.FILL;
		layoutData.horizontalSpan = getNumColumnsInForm();
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.numColumns = 2;

		bookSection.setLayout(gridLayout);
		bookSection.setLayoutData(layoutData);

		bookComposite = createBookComposite(bookSection, SWT.NONE,
				getFormToolkit(), getSite(), viewStateMemento);
		setBookCompositeBackground(bookComposite); 
		setBookSectionClient(bookComposite);
	}

	protected void setBookSectionClient(IBookComposite book) {
		bookSection.setClient((BookComposite) book);
	}

	protected void setBookCompositeBackground(IBookComposite book) {
		((BookComposite) book).setBackground(bookSection
				.getBackground());
	}
	
	protected IBookComposite createBookComposite(Composite parent, int style,
			FormToolkit formToolkit, IWorkbenchPartSite site, IMemento memento) {
		return new BookComposite(bookSection, style, formToolkit);
	}
			
	protected void addCustomFieldsExpansionListener()
	{
		Assert.isNotNull(customFieldsViewPieces, "Custom fields view was null." );
		customFieldsViewPieces.getCustomFieldsExpandableComposite()
		.addExpansionListener(new ExpansionAdapter() {
			@Override
			public void expansionStateChanging(ExpansionEvent e) {
				outermostForm.reflow(true);
			}

			public void expansionStateChanged(ExpansionEvent e) {
				outermostForm.reflow(true);
			}
		});

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
	protected FormToolkit getFormToolkit() {
		if (formToolkit == null) {
			formToolkit = new FormToolkit(Display.getCurrent());
		}
		return formToolkit;
	}

	protected void createSendAndCancelButtons() {
		checkOutermostFormInitialized();
		Composite okCancelComposite = getFormToolkit().createComposite(
				outermostForm.getBody());
		okCancelComposite.setLayout(new RowLayout(SWT.HORIZONTAL));
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_END);
		gd.horizontalSpan = getNumColumnsInForm();
		okCancelComposite.setLayoutData(gd);
		sendButton = getFormToolkit().createButton(okCancelComposite, "Send",
				SWT.PUSH);
		sendButton.setEnabled(false);
		cancelButton = getFormToolkit().createButton(okCancelComposite,
				"Cancel", SWT.PUSH);
	}

	protected void createErrorLabel() {
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = GridData.END;

		errorMessageLabel = getFormToolkit()
				.createLabel(outermostComposite, "");
		errorMessageLabel.setLayoutData(gridData);
		outermostComposite.setBackground(errorMessageLabel.getBackground());
	}
	
	protected void createErrorIconLabel() {
		GridData iconGridData = new GridData();
		iconGridData.horizontalAlignment = GridData.BEGINNING;
		iconGridData.verticalAlignment = GridData.END;
		iconGridData.horizontalIndent = 3;
		iconGridData.widthHint = 7;
		errorIconLabel = getFormToolkit().createLabel(outermostComposite, null);
		errorIconLabel.setImage(null);
		errorIconLabel.setLayoutData(iconGridData);
	}

	public void propertyChange(PropertyChangeEvent event) {
		String property = event.getProperty();
		if (CustomOrderFieldPage.CUSTOM_FIELDS_PREFERENCE.equals(property)) {
			String valueString = event.getNewValue().toString();
			customFieldsViewPieces.updateCustomFields(valueString);
		}
		
		outermostForm.reflow(true);
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
		bookComposite.saveState(memento);
	}

	protected void restoreCustomFieldStates() {
		if (viewStateMemento == null)
			return;

		if (customFieldsViewPieces != null) {
			customFieldsViewPieces.restoreCustomFieldsTableItemCheckedState(
					viewStateMemento, CUSTOM_FIELD_VIEW_SAVED_STATE_KEY_PREFIX);
		}
	}
	
	protected void safelyDispose(IOrderTicketController orderTicketController) {
		try {
			orderTicketController.dispose();
		} catch (Exception anyException) {
			PhotonPlugin.getMainConsoleLogger().warn(
					"Failed to dispose of order ticket controller. " + orderTicketController,
					anyException);
		}
	}
	
	public void clear() {
		updateOutermostFormTitle(null);
		orderTicketViewPieces.getSymbolText().setEnabled(true);
		sendButton.setEnabled(false);
		clearErrors();
	}

	public void updateMessage(Message aMessage) throws MarketceteraException {
		customFieldsViewPieces.addCustomFields(aMessage);
	}

	public void clearMessage() {
		errorMessageLabel.setText("");
		errorIconLabel.setImage(null);		
	}

	public void showMessage(Message order) {
		orderTicketViewPieces.getSymbolText().setEnabled(
				FIXMessageUtil.isOrderSingle(order));
		updateOutermostFormTitle(order);
	}

	public IBookComposite getBookComposite() {
		return bookComposite;
	}

	protected Section getBookSection() {
		return bookSection;
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

	public Combo getSideCombo() {
		return orderTicketViewPieces.getSideCombo();
	}

	public Text getSymbolText() {
		return orderTicketViewPieces.getSymbolText();
	}

	public CheckboxTableViewer getTableViewer() {
		return customFieldsViewPieces.getTableViewer();
	}

	public Combo getTifCombo() {
		return orderTicketViewPieces.getTifCombo();
	}

	public void showErrorMessage(String errorMessage, int severity) {
		orderTicketViewPieces.showErrorMessage(errorMessage, severity,
				errorMessageLabel, errorIconLabel, sendButton);
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
