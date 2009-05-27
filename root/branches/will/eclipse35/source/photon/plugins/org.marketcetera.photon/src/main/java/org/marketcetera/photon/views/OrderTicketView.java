package org.marketcetera.photon.views;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.databinding.AggregateValidationStatus;
import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.conversion.Converter;
import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.conversion.NumberToStringConverter;
import org.eclipse.core.databinding.conversion.StringToNumberConverter;
import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.list.IListChangeListener;
import org.eclipse.core.databinding.observable.list.ListChangeEvent;
import org.eclipse.core.databinding.observable.list.ListDiffEntry;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.photon.BrokerManager;
import org.marketcetera.photon.Messages;
import org.marketcetera.photon.PhotonController;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.BrokerManager.Broker;
import org.marketcetera.photon.BrokerManager.BrokerLabelProvider;
import org.marketcetera.photon.parser.ILexerFIXImage;
import org.marketcetera.photon.parser.PriceImage;
import org.marketcetera.photon.parser.SideImage;
import org.marketcetera.photon.parser.TimeInForceImage;
import org.marketcetera.photon.ui.databinding.BindingHelper;
import org.marketcetera.photon.ui.databinding.ErrorDecorationObservable;
import org.marketcetera.photon.ui.databinding.FormTextObservableValue;
import org.marketcetera.photon.ui.databinding.IsNewOrderMessageConverter;
import org.marketcetera.photon.ui.databinding.LabelStatusImageObservableValue;
import org.marketcetera.photon.ui.databinding.RequiredInputAggregator;
import org.marketcetera.photon.ui.databinding.StatusAggregator;
import org.marketcetera.photon.ui.validation.DataDictionaryValidator;
import org.marketcetera.photon.ui.validation.DecimalRequiredValidator;
import org.marketcetera.photon.ui.validation.IgnoreFirstNullValidator;
import org.marketcetera.photon.ui.validation.IntegerRequiredValidator;
import org.marketcetera.photon.ui.validation.StringRequiredValidator;
import org.marketcetera.photon.ui.validation.fix.BigDecimalToStringConverter;
import org.marketcetera.photon.ui.validation.fix.EnumStringConverterBuilder;
import org.marketcetera.photon.ui.validation.fix.FIXObservables;
import org.marketcetera.photon.ui.validation.fix.PriceConverterBuilder;
import org.marketcetera.photon.ui.validation.fix.StringToBigDecimalConverter;
import org.marketcetera.quickfix.CurrentFIXDataDictionary;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.trade.BrokerID;

import quickfix.DataDictionary;
import quickfix.FieldType;
import quickfix.Message;
import quickfix.field.Account;
import quickfix.field.MsgType;
import quickfix.field.OrdType;
import quickfix.field.OrderQty;
import quickfix.field.Price;
import quickfix.field.Side;
import quickfix.field.TimeInForce;

/* $License$ */

/**
 * This is the abstract base class for all order ticket views.  It
 * is responsible for setting up the databindings for the "common" order
 * ticket fields, such as side, price, and time in force.
 * 
 * It also is responsible for managing the "custom fields" for order messages
 * that can be set by the user in the preferences dialog, and activated
 * in the order ticket.
 * 
 * @author gmiller
 *
 */
@ClassVersion("$Id$")
public abstract class OrderTicketView
    extends XSWTView<IOrderTicket>
    implements Messages
{

	private static final String CONTROL_DECORATOR_KEY = "CONTROL_DECORATOR_KEY"; //$NON-NLS-1$

	public static final String CONTROL_DEFAULT_COLOR = "CONTROL_DEFAULT_COLOR"; //$NON-NLS-1$

	protected final Image errorImage;

	protected final Image warningImage;

	private final List<Binding> messageBindings = new LinkedList<Binding>();
	
	private final EnumStringConverterBuilder<?> sideConverterBuilder;

	private final EnumStringConverterBuilder<?> tifConverterBuilder;

	private final PriceConverterBuilder priceConverterBuilder;

	private final boolean hasRealCharDatatype;

	protected final boolean orderQtyIsInt;
	
	protected final DataDictionary dictionary;
	
	protected OrderTicketModel orderTicketModel;
	
	protected final BindingHelper bindingHelper;

	protected static final String CUSTOM_FIELD_VIEW_SAVED_STATE_KEY_PREFIX = "CUSTOM_FIELD_CHECKED_STATE_OF_"; //$NON-NLS-1$

	private IMemento memento;

	private AggregateValidationStatus aggregateValidationStatus;

	private ComboViewer mAvailableBrokersViewer;

	/**
	 * Create a new order ticket view.  Get the error and warning images out of the
	 * FieldDecorationRegistry.  And set up the converter builders for Side, TimeInForce, and
	 * Price.
	 * 
	 */
	public OrderTicketView() {

		FieldDecorationRegistry decorationRegistry = FieldDecorationRegistry.getDefault();
		FieldDecoration deco = decorationRegistry
				.getFieldDecoration(FieldDecorationRegistry.DEC_ERROR);
		errorImage = deco.getImage();
		deco = decorationRegistry.getFieldDecoration(
				FieldDecorationRegistry.DEC_WARNING);
		warningImage = deco.getImage();
		
		dictionary = CurrentFIXDataDictionary.getCurrentFIXDataDictionary()
			.getDictionary();

		hasRealCharDatatype = FieldType.Char.equals(dictionary
				.getFieldTypeEnum(Side.FIELD));
		bindingHelper = new BindingHelper();

		orderQtyIsInt = (FieldType.Int == dictionary
				.getFieldTypeEnum(OrderQty.FIELD));

		sideConverterBuilder = createSideConverterBuilder();
		tifConverterBuilder = createTifConverterBuilder();
		priceConverterBuilder = createPriceConverterBuilder();

	}
	
	/**
	 * Get the XSWT order ticket proxy object.
	 * @return the XSWT order ticket proxy object.
	 */
	public IOrderTicket getOrderTicket() {
		return (IOrderTicket) getXSWTView();
	}

	/**
	 * Calls the superclass {@link #createPartControl(Composite)}, and then calls
	 * {@link #setDefaultInput()} to set up the default order in the ticket.
	 */
	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		setDefaultInput();
	}
	
	/**
	 * Performs a bunch of UI cleanup tasks that cannot be accomplished in
	 * XSWT.  Adds error decoration functionality to several inputs.  Sets up
	 * the valid values for the "side" and "time in force" combos.  Adds event
	 * handlers that cause the top-level form to reflow when a "twistie" is clicked
	 * on one of the {@link ExpandableComposite}s. Sets up the button events.
	 * Updates the size of several controls based on the default font width.  Then
	 * sets up the UI for the custom fields table.
	 */
	@Override
	protected void finishUI() {

		final IOrderTicket ticket = getOrderTicket();

		addInputControlErrorDecoration(ticket.getPriceText());
		addInputControlErrorDecoration(ticket.getQuantityText());
		addInputControlErrorDecoration(ticket.getSideCombo());
		addInputControlErrorDecoration(ticket.getSymbolText());
		addInputControlErrorDecoration(ticket.getBrokerCombo());
		addInputControlErrorDecoration(ticket.getTifCombo());

		Color bg = ticket.getForm().getParent().getBackground();
		ticket.getErrorIconLabel().setBackground(bg);
		ticket.getErrorMessageLabel().setBackground(bg);
		
		ticket.getSideCombo().add(SideImage.BUY.getImage());
		ticket.getSideCombo().add(SideImage.SELL.getImage());
		ticket.getSideCombo().add(SideImage.SELL_SHORT.getImage());
		
		mAvailableBrokersViewer = new ComboViewer(ticket.getBrokerCombo());
		ObservableListContentProvider brokerContentProvider = new ObservableListContentProvider();
		mAvailableBrokersViewer.setContentProvider(brokerContentProvider);
		mAvailableBrokersViewer.setLabelProvider(new BrokerLabelProvider());
		mAvailableBrokersViewer.setInput(BrokerManager.getCurrent().getAvailableBrokers());
		
		addComboChoicesFromLexerEnum(ticket.getTifCombo(), TimeInForceImage.values());
		
		ticket.getCustomExpandableComposite().addExpansionListener(
				new ExpansionAdapter() {
					@Override
					public void expansionStateChanging(ExpansionEvent e) {
						ScrolledForm form = ticket.getForm();
						if (!form.isDisposed()){
							form.reflow(true);
						}
					}
		
					public void expansionStateChanged(ExpansionEvent e) {
						ScrolledForm form = ticket.getForm();
						if (!form.isDisposed()){
							form.reflow(true);
						}
					}
				});

		ticket.getOtherExpandableComposite().addExpansionListener(
				new ExpansionAdapter() {
					@Override
					public void expansionStateChanging(ExpansionEvent e) {
						ScrolledForm form = ticket.getForm();
						if (!form.isDisposed()){
							form.reflow(true);
						}
					}
		
					public void expansionStateChanged(ExpansionEvent e) {
						ScrolledForm form = ticket.getForm();
						if (!form.isDisposed()){
							form.reflow(true);
						}
					}
				});

		ticket.getSymbolText().addFocusListener(new FocusAdapter() {

			@Override
			public void focusGained(FocusEvent e) {
				((Text) e.widget).selectAll();
			}

		});

		ticket.getClearButton().addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				orderTicketModel.clearOrderMessage();
			}

			public void widgetSelected(SelectionEvent e) {
				orderTicketModel.clearOrderMessage();
			}
		});
		ticket.getSendButton().addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				handleSend();
			}

			public void widgetSelected(SelectionEvent e) {
				handleSend();
			}
		});

		addInputControlSendOrderListeners();

		updateSize(ticket.getQuantityText(), 10);
		updateSize(ticket.getAccountText(), 10);

		TableViewer customFieldsTableViewer = ticket.getCustomFieldsTableViewer();
		Table customFieldsTable = customFieldsTableViewer.getTable();
		disposePhantomColumn(customFieldsTable);
		// Create a standard content provider
		ObservableListContentProvider contentProvider = 
			new ObservableListContentProvider();
		customFieldsTableViewer.setContentProvider(contentProvider);
		
		// And a standard label provider that maps columns
		IObservableMap[] attributeMaps = BeansObservables.observeMaps(contentProvider.getKnownElements(), 
		                                                              CustomField.class,
		                                                              new String[] { "keyString", "valueString" }); //$NON-NLS-1$ //$NON-NLS-2$
		customFieldsTableViewer.setLabelProvider(new ObservableMapLabelProvider(attributeMaps));

		ticket.getForm().reflow(true);
	}

	/**
	 * Get rid of phantom first column
	 * see https://bugs.eclipse.org/bugs/show_bug.cgi?id=6643
	 */
	private void disposePhantomColumn(Table table) {
		TableColumn firstColumn = table.getColumn(0);
		if (firstColumn.getText() == null || firstColumn.getText().length() ==0){
			firstColumn.dispose();
		}
	}

	/**
	 * Bind the top level form title to show "new order" or "replace order" depending
	 * on the {@link MsgType} in the order ticket model's message.
	 * 
	 * @param model
	 * @param orderMessageObservable
	 */
	protected void bindFormTitle(final OrderTicketModel model, IObservableValue orderMessageObservable) {
		getDataBindingContext().bindValue(
				new FormTextObservableValue(getOrderTicket().getForm()),
				orderMessageObservable,
				new UpdateValueStrategy(), 
				new UpdateValueStrategy().setConverter(new IConverter() {
					public Object convert(Object fromObject) {
						if (fromObject == null
								|| !FIXMessageUtil.isCancelReplaceRequest((Message) fromObject)) {
							return getNewOrderString();
						} else {
							return getReplaceOrderString();
						}
					}
					public Object getFromType() {
						return Message.class;
					}
					public Object getToType() {
						return String.class;
					}
				}));
	}

	/**
	 * Get the UI string to show for a "replace" message.
	 * @return the UI string
	 */
	protected abstract String getReplaceOrderString();

	/**
	 * Get the UI string to show for a "new order" message.
	 * @return the UI string
	 */
	protected abstract String getNewOrderString();

	/**
	 * Set up the model for this order ticket to have the "default" (probably empty)
	 * input message.
	 */
	protected abstract void setDefaultInput();

	/**
	 * Add a {@link ControlDecoration} to the given control, and hide it.
	 * Store a pointer in the control itself for later retrieval. 
	 *  
	 * @param control
	 * @see #CONTROL_DECORATOR_KEY
	 * @see Control#setData(String, Object)
	 */
	public void addInputControlErrorDecoration(Control control) {
		ControlDecoration cd = new ControlDecoration(control, SWT.LEFT
				| SWT.BOTTOM);
		cd.setImage(errorImage);
		cd.hide();
		control.setData(CONTROL_DECORATOR_KEY, cd);
		control.setData(CONTROL_DEFAULT_COLOR, control.getBackground());
	}


	/**
	 * Show the given error message in this order ticket's error display area.
	 * 
	 * @param errorMessage the text of the error message
	 * @param severity the severity of the error message, see {@link IStatus#ERROR}
	 */
	public void showErrorMessage(String errorMessage, int severity) {
		Label errorMessageLabel = getOrderTicket().getErrorMessageLabel();
		Label errorIconLabel = getOrderTicket().getErrorIconLabel();
		
		if (errorMessage == null) {
			errorMessageLabel.setText(""); //$NON-NLS-1$
			errorIconLabel.setImage(null);
		} else {
			errorMessageLabel.setText(errorMessage);
			if (severity == IStatus.OK) {
				errorIconLabel.setImage(null);
			} else {
				if (severity == IStatus.ERROR)
					errorIconLabel.setImage(errorImage);
				else
					errorIconLabel.setImage(warningImage);
			}
		}
	}

	/**
	 * Add the choices to the given {@link Combo}, given the UI images in choices
	 * @param combo the combo to update
	 * @param choices the choices to put in the combo
	 */
	protected void addComboChoicesFromLexerEnum(Combo combo, ILexerFIXImage[] choices) {
		for (ILexerFIXImage choice : choices) {
			combo.add(choice.getImage());
		}
	}

	/**
	 * Set the input for this "view" component.
	 * 
	 * @param model the "model" for this "view"
	 */
	public void setInput(OrderTicketModel model) {
		clearDataBindingContext();
		orderTicketModel = model;

		aggregateValidationStatus = new AggregateValidationStatus(getDataBindingContext().getBindings(),
				AggregateValidationStatus.MAX_SEVERITY);

		IObservableValue orderMessageObservable = BeansObservables.observeValue(model, "orderMessage"); //$NON-NLS-1$
		bindFormTitle(model, orderMessageObservable);
		bindCustomFields(model, 
		                 getOrderTicket());
		bindOrderMessageProperty(model, orderMessageObservable);
		
		getDataBindingContext().bindValue(
				SWTObservables.observeText(getOrderTicket().getErrorMessageLabel()),
				aggregateValidationStatus,
				new UpdateValueStrategy(),
				new UpdateValueStrategy());

		getDataBindingContext().bindValue(
				new LabelStatusImageObservableValue(getOrderTicket().getErrorIconLabel(), this.errorImage, this.warningImage),
				aggregateValidationStatus,
				new UpdateValueStrategy(),
				new UpdateValueStrategy());
		
		

	}
	
	/**
	 * Bind the custom fields components of the model to this view.  Also
	 * sets up the event handlers necessary to manage checkbox state, and updates.
	 * 
	 * @param model the model to bind
	 * @param orderTicket the proxy object for the actual UI controls.
	 */
	protected void bindCustomFields(final OrderTicketModel model,
	                                IOrderTicket orderTicket) 
	{
		orderTicket.getCustomFieldsTableViewer().setInput(model.getCustomFieldsList());
		orderTicket.getCustomFieldsTableViewer().addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent event) 
			{
				Object source = event.getElement();
				((CustomField)source).setEnabled(event.getChecked());
			}		
		});
		model.getCustomFieldsList().addListChangeListener(new IListChangeListener() {
			public void handleListChange(ListChangeEvent event) 
			{
				ScrolledForm theForm = getOrderTicket().getForm();
				if (!theForm.isDisposed()) {
					ListDiffEntry[] differences = event.diff.getDifferences();
					for (ListDiffEntry listDiffEntry : differences) {
						if (listDiffEntry.isAddition()) {
							CustomField customField = (CustomField)listDiffEntry.getElement();
							String key = CUSTOM_FIELD_VIEW_SAVED_STATE_KEY_PREFIX + customField.getKeyString();
							IMemento theMemento = getMemento();
							if (theMemento != null && 
							    theMemento.getInteger(key) != null) {
								boolean itemChecked = (theMemento.getInteger(key).intValue() != 0);
								customField.setEnabled(itemChecked);
							}
						}
					}
					theForm.reflow(true);
				}
			}
		});
	}

	/**
	 * This method is repsonsible for binding the QuickFIX message inside the
	 * given {@link OrderTicketModel} to the UI.  It does this by adding a value
	 * change listener to to the property on the model, and then calling 
	 * {@link #onNewOrderMessage(OrderTicketModel)}.
	 * 
	 * @param model the model containing the order message to bind
	 * @param orderMessageObservable the instance of {@link IObservableValue} representing the message property
	 */
	protected void bindOrderMessageProperty(final OrderTicketModel model, IObservableValue orderMessageObservable) {
		orderMessageObservable.addValueChangeListener(new IValueChangeListener() {
			public void handleValueChange(ValueChangeEvent event) {
				onNewOrderMessage(model);
			}
		});
		onNewOrderMessage(model);
	}

	
	/**
	 * Binds the actual message object to the UI by extracting it from the 
	 * {@link OrderTicketModel} and then using the data binding framework
	 * to bind aspects of the message to UI controls.
	 * 
	 * This method must also be provided with a List<IObservableValue>
	 * that can store all the controls that require user input.  That
	 * is the contents of the list will be returned by this function 
	 * for further processing.
	 * 
	 * This implementation provides bindings for side, quantity
	 * symbol, price, time-in-force and account fields for the order
	 * message.
	 * 
	 * @param model the order ticket model
	 * @param controlsRequiringInput observable values connected to controls are added to this list to indicate that the controls require user input
	 */
	protected void bindMessage(OrderTicketModel model, List<IObservableValue> controlsRequiringInput) {
		try {

			Message message = model.getOrderMessage();
			
			Realm realm = Realm.getDefault();

//			validationsRequiringUserInput = new HashSet<IToggledValidator>();
			
			final int swtEvent = SWT.Modify;
			{
				Control whichControl = getOrderTicket().getSideCombo();
				IValidator validator = new IgnoreFirstNullValidator(sideConverterBuilder
						.newTargetAfterGetValidator());
				ISWTObservableValue swtObservable = SWTObservables.observeText(whichControl);
				Binding binding = bindMessageValue( 
								swtObservable,
								FIXObservables.observeValue(realm, message, Side.FIELD, dictionary),
								new UpdateValueStrategy().setAfterGetValidator(
										validator).setConverter(
										sideConverterBuilder.newToModelConverter()),
								new UpdateValueStrategy()
										.setConverter(sideConverterBuilder
												.newToTargetConverter()));
				bindMessageValue(
								new ErrorDecorationObservable(whichControl, errorImage, warningImage),
								binding.getValidationStatus(),
								new UpdateValueStrategy(),
								new UpdateValueStrategy());
				bindMessageValue(
								SWTObservables.observeEnabled(whichControl),
								BeansObservables.observeValue(model, "orderMessage"), //$NON-NLS-1$
								null,
								new UpdateValueStrategy().setConverter(new IsNewOrderMessageConverter()));
				controlsRequiringInput.add(swtObservable);
			}
			{
				Control whichControl = getOrderTicket().getQuantityText();
				IValidator validator;
				IConverter toModelConverter;
				IConverter toUIConverter;
				if (orderQtyIsInt) {
					validator = new IgnoreFirstNullValidator(new IntegerRequiredValidator());
					toModelConverter = StringToNumberConverter.toInteger(false);
					toUIConverter = NumberToStringConverter.fromInteger(false);
				} else {
					validator = new IgnoreFirstNullValidator(new DecimalRequiredValidator());
					toModelConverter = new StringToBigDecimalConverter();
					toUIConverter = new BigDecimalToStringConverter(false);
				}
				ISWTObservableValue swtObservable = SWTObservables.observeText(whichControl, swtEvent);
				Binding binding = bindMessageValue( 
						swtObservable, 
						FIXObservables.observeValue(realm, message, OrderQty.FIELD, dictionary),
						new UpdateValueStrategy().setAfterGetValidator(validator)
								.setConverter(toModelConverter),
						new UpdateValueStrategy().setConverter(toUIConverter));
				bindMessageValue(
						new ErrorDecorationObservable(whichControl, errorImage, warningImage),
						binding.getValidationStatus(),
						new UpdateValueStrategy(),
						new UpdateValueStrategy());
				controlsRequiringInput.add(swtObservable);
			}
			{
				Control whichControl = getOrderTicket().getSymbolText();
				IValidator validator = new IgnoreFirstNullValidator(new StringRequiredValidator());
				IObservableValue observableSymbol = orderTicketModel.getObservableSymbol();
				ISWTObservableValue swtObservable = SWTObservables.observeText(whichControl, swtEvent);
				Binding binding = bindMessageValue( 
						swtObservable,
						observableSymbol,
						new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE).setAfterGetValidator(validator),
						new UpdateValueStrategy()
						);
				bindMessageValue(
						new ErrorDecorationObservable(whichControl, errorImage, warningImage),
						binding.getValidationStatus(),
						new UpdateValueStrategy(),
						new UpdateValueStrategy());
				bindMessageValue(
						SWTObservables.observeEnabled(whichControl),
						BeansObservables.observeValue(model, "orderMessage"), //$NON-NLS-1$
						null,
						new UpdateValueStrategy().setConverter(new IsNewOrderMessageConverter()));
				controlsRequiringInput.add(swtObservable);
			}
			{
				Control whichControl = getOrderTicket().getPriceText();
				IValidator validator = new IgnoreFirstNullValidator(priceConverterBuilder
						.newTargetAfterGetValidator());
				ISWTObservableValue swtObservable = SWTObservables.observeText(whichControl,swtEvent);
				Binding binding = bindMessageValue( 
						swtObservable,
						FIXObservables.observePriceValue(realm, message, Price.FIELD, dictionary),
								new UpdateValueStrategy().setAfterGetValidator(
										validator)
										.setConverter(
												priceConverterBuilder
														.newToModelConverter()),
								new UpdateValueStrategy().setAfterGetValidator(
										priceConverterBuilder
												.newModelAfterGetValidator())
										.setConverter(
												priceConverterBuilder
														.newToTargetConverter()));
				bindMessageValue(
						new ErrorDecorationObservable(whichControl, errorImage, warningImage),
						binding.getValidationStatus(),
						new UpdateValueStrategy(),
						new UpdateValueStrategy());
				controlsRequiringInput.add(swtObservable);
			}
			{
				Control whichControl = getOrderTicket().getBrokerCombo();
				IObservableValue observable = ViewersObservables.observeSingleSelection(mAvailableBrokersViewer);
				Binding binding = bindMessageValue( 
						observable,
						BeansObservables.observeValue(model, "brokerId"), //$NON-NLS-1$
						new UpdateValueStrategy().setConverter(new Converter(Broker.class, String.class) {
							@Override
							public Object convert(Object fromObject) {
								BrokerID id = ((Broker) fromObject).getId();
								return id == null ? null : id.getValue();
							}
						}),
						new UpdateValueStrategy().setConverter(new Converter(String.class, Broker.class) {
							@Override
							public Object convert(Object fromObject) {
								if (fromObject == null) {
									return BrokerManager.AUTO_SELECT_BROKER;
								}									
								for (Object obj : BrokerManager.getCurrent().getAvailableBrokers()) {
									BrokerID id = ((Broker) obj).getId();
									if (id != null && id.getValue() != null && id.getValue().equals(fromObject)) {
										return obj;
									}
								}
								return BrokerManager.AUTO_SELECT_BROKER;
							}
						}));
				bindMessageValue(
						new ErrorDecorationObservable(whichControl, errorImage, warningImage),
						binding.getValidationStatus(),
						null,
						null);
				bindMessageValue(
						SWTObservables.observeEnabled(whichControl),
						BeansObservables.observeValue(model, "orderMessage"), //$NON-NLS-1$
						null,
						new UpdateValueStrategy().setConverter(new IsNewOrderMessageConverter()));
				controlsRequiringInput.add(observable);
			}
			{
				Control whichControl = getOrderTicket().getTifCombo();
				IValidator afterGetValidator = new IgnoreFirstNullValidator(tifConverterBuilder
						.newTargetAfterGetValidator());
				IValidator afterConvertValidator = new IgnoreFirstNullValidator(new DataDictionaryValidator(dictionary,
				                                                                                            TimeInForce.FIELD,
                                                                                                            INVALID_TIME_IN_FORCE.getText(),
				                                                                                            PhotonPlugin.ID));
				ISWTObservableValue swtObservable = SWTObservables.observeText(whichControl);
				Binding binding = bindMessageValue( 
						swtObservable,
						FIXObservables.observeValue(realm, message, TimeInForce.FIELD, dictionary),
						new UpdateValueStrategy().setAfterGetValidator(afterGetValidator).setAfterConvertValidator(afterConvertValidator).setConverter(tifConverterBuilder.newToModelConverter()),
						new UpdateValueStrategy().setConverter(tifConverterBuilder.newToTargetConverter()));
				bindMessageValue(
						new ErrorDecorationObservable(whichControl, errorImage, warningImage),
						binding.getValidationStatus(),
						new UpdateValueStrategy(),
						new UpdateValueStrategy());
				bindMessageValue(
						SWTObservables.observeEnabled(whichControl),
						BeansObservables.observeValue(model, "orderMessage"), //$NON-NLS-1$
						null,
						new UpdateValueStrategy().setConverter(new IsNewOrderMessageConverter()));
				controlsRequiringInput.add(swtObservable);
			}
			{
				Control whichControl = getOrderTicket().getAccountText();
				bindMessageValue(
						SWTObservables.observeText(whichControl,swtEvent),
						FIXObservables.observeValue(realm, message, Account.FIELD, dictionary),
						new UpdateValueStrategy(),
						new UpdateValueStrategy());
			}

		} catch (Throwable ex) {
			PhotonPlugin.getMainConsoleLogger().error(CANNOT_BIND_TO_TICKET.getText(),
			                                          ex);
		}
	}

	
	/**
	 * This method is a thin wrapper around {@link DataBindingContext#bindValue(IObservableValue, IObservableValue, UpdateValueStrategy, UpdateValueStrategy)}
	 * that records all bindings on the Message object so that they can be
	 * disposed when the message is replaced.
	 * 
	 * @param targetObservableValue the IObservableValue that wraps the UI controls
	 * @param modelObservableValue the IObservableValue that wraps the model
	 * @param targetToModel the UpdateValueStrategy for the target-to-model direction
	 * @param modelToTarget the UpdateValueStrategy for the model-to-target direction
	 * @return the result of {@link DataBindingContext#bindValue(IObservableValue, IObservableValue, UpdateValueStrategy, UpdateValueStrategy)}
	 */
	protected Binding bindMessageValue(
			IObservableValue targetObservableValue,
			IObservableValue modelObservableValue,
			UpdateValueStrategy targetToModel,
			UpdateValueStrategy modelToTarget) {

		Binding binding = getDataBindingContext().bindValue(targetObservableValue, modelObservableValue,
				targetToModel, modelToTarget);
		messageBindings.add(binding);
		return binding;
	}

	/**
	 * This method should be invoked when the message object inside the
	 * model has been replaced.  It unbinds all previous databindings to the 
	 * message, and then rebinds by calling {@link #bindMessage(OrderTicketModel, List)}
	 * @param model
	 */
	private void onNewOrderMessage(OrderTicketModel model) {
		if (getOrderTicket().getForm().isDisposed()){
			return;
		}
		unbindMessage();
		List<IObservableValue> controlsRequiringInput = new LinkedList<IObservableValue>();
		bindMessage(model, controlsRequiringInput);

		bindMessageValue(
				SWTObservables.observeEnabled(getOrderTicket().getSendButton()),
				new StatusAggregator(new RequiredInputAggregator(controlsRequiringInput), aggregateValidationStatus),
				new UpdateValueStrategy(), 
				new UpdateValueStrategy().setConverter(new IConverter() {
					public Object convert(Object fromObject) {
						return (((IStatus)fromObject).getSeverity() < IStatus.ERROR);
					}
					public Object getFromType() {
						return IStatus.class;
					}
					public Object getToType() {
						return Boolean.class;
					}
				}));
        handleFocusOnNewOrderMessage(model);
    }

    /** Subclasses can override in case these fields don't exist */
    protected void handleFocusOnNewOrderMessage(OrderTicketModel model) {
        // FIX for bug #438
        if (FIXMessageUtil.isOrderSingle(model.getOrderMessage())){
            getOrderTicket().getSideCombo().setFocus();
        } else {
            getOrderTicket().getPriceText().setFocus();
        }
    }

    /**
	 * This method unbinds the current order (or replace) message from the UI.
	 * It loops through the known binding objects, first clearing out a UI representation
	 * for any Combo and Text controls, then calling {@link Binding#dispose()}.
	 * 
	 */
	@SuppressWarnings("restriction") //$NON-NLS-1$
    private void unbindMessage() {
		Object[] bindingArray = messageBindings.toArray();
		List<IObservableValue> clearables = new LinkedList<IObservableValue>();
		for (Object bindingObj : bindingArray) {
			Binding binding = ((Binding) (bindingObj));
			IObservable observable = binding.getTarget();
			if (observable instanceof  org.eclipse.jface.internal.databinding.swt.ComboObservableValue ||
                observable instanceof org.eclipse.jface.internal.databinding.swt.CComboObservableValue ||
                observable instanceof org.eclipse.jface.internal.databinding.swt.TextObservableValue) {
				IObservableValue observableValue = (IObservableValue) observable;
				clearables.add(observableValue);
			}
			binding.dispose();
		}
		messageBindings.clear();
		for (IObservableValue value : clearables){
			value.setValue(null);
		}
	}
	
	/**
	 * Initialize the converter builder for the Side field and UI representation
	 * @return 
	 */
	private EnumStringConverterBuilder<?> createSideConverterBuilder() {
		EnumStringConverterBuilder<?> returnBuilder = null;
		
		if (!hasRealCharDatatype) {
			EnumStringConverterBuilder<String> escb = new EnumStringConverterBuilder<String>(
					String.class);

			bindingHelper.initStringToImageConverterBuilder(escb, SideImage
					.values());
			returnBuilder = escb;
		} else {
			EnumStringConverterBuilder<Character> escb = new EnumStringConverterBuilder<Character>(
					Character.class);

			bindingHelper.initCharToImageConverterBuilder(escb, SideImage
					.values());
			returnBuilder = escb;
		}
		return returnBuilder;
	}

	
	/**
	 * Initialize the converter builder for the TimeInForce field and UI representation
	 * @return 
	 */
	private EnumStringConverterBuilder<?> createTifConverterBuilder() {
		EnumStringConverterBuilder<?> returnBuilder = null;
		if (!hasRealCharDatatype) {
			EnumStringConverterBuilder<String> escb = new EnumStringConverterBuilder<String>(
					String.class);

			bindingHelper.initStringToImageConverterBuilder(escb,
					TimeInForceImage.values());
			returnBuilder = escb;
		} else {
			EnumStringConverterBuilder<Character> escb = new EnumStringConverterBuilder<Character>(
					Character.class);

			bindingHelper.initCharToImageConverterBuilder(escb,
					TimeInForceImage.values());
			returnBuilder = escb;
		}
		return returnBuilder;
	}

	/**
	 * Initialize the converter builder for the Price field and UI representation
	 * @return 
	 */
	private PriceConverterBuilder createPriceConverterBuilder() {
		PriceConverterBuilder returnBuilder = new PriceConverterBuilder(dictionary);

		returnBuilder.addMapping(OrdType.MARKET, PriceImage.MKT
				.getImage());
		return returnBuilder;
	}

	/**
	 * Initializes the listeners responsible for noticing when the 
	 * user hits return or enter in a field.  The action for the "send"
	 * button should be invoked in this case.
	 */
	private void addInputControlSendOrderListeners() {
		IOrderTicket ticket = getOrderTicket();
		addSendOrderListener(ticket.getSideCombo());
		addSendOrderListener(ticket.getQuantityText());
		addSendOrderListener(ticket.getSymbolText());
		addSendOrderListener(ticket.getPriceText());
		addSendOrderListener(ticket.getTifCombo());
	}
	
	/**
	 * Hook up a listener to the targetControl that listens for
	 * {@link SWT#CR} and invokes {@link #handleSend()}
	 * @param targetControl
	 */
	public void addSendOrderListener(Control targetControl) {
		targetControl.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.character == SWT.CR) {
					if (getOrderTicket().getSendButton().isEnabled()) {
						handleSend();
					}
				}
			}
		});
	}

	/**
	 * Get the memento used for storing preferences and state for this view.
	 * @return the memento
	 */
	private IMemento getMemento() {
		return memento;
	}

	/**
	 * This method "completes" the message by calling {@link OrderTicketModel#completeMessage()}
	 * then passes the message on to {@link PhotonController#handleInternalMessage(Message)}
	 * 
	 * It then clears out the message in the view model, and sets the focus
	 * back to the Side UI control.
	 */
	public void handleSend() {
		try {
			PhotonPlugin plugin = PhotonPlugin.getDefault();
			Message orderMessage = orderTicketModel.getOrderMessage();
			orderTicketModel.completeMessage();
			plugin.getPhotonController().handleInternalMessage(orderMessage, orderTicketModel.getBrokerId());
			orderTicketModel.clearOrderMessage();
		} catch (Exception e) {
			String errorMessage = CANNOT_SEND_ORDER_SPECIFIED.getText(e.getMessage());
			PhotonPlugin.getMainConsoleLogger().error(
					errorMessage, e);
			showErrorMessage(errorMessage, IStatus.ERROR);
		}
		getOrderTicket().getSideCombo().setFocus();
	}

	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);

		this.memento = memento;
	}
	
	/**
	 * Stores the checked state of each of the custom fields in the view.
	 */
	@Override
	public void saveState(IMemento memento) {
		TableItem[] items = getOrderTicket().getCustomFieldsTableViewer().getTable().getItems();
		for (int i = 0; i < items.length; i++) {
			TableItem item = items[i];
			String key = OrderTicketView.CUSTOM_FIELD_VIEW_SAVED_STATE_KEY_PREFIX + item.getText(1);
			memento.putInteger(key, (item.getChecked() ? 1 : 0));
		}
	}

	/**
	 * Set the focus on the Side control (in the case of a new order) 
	 * or the Quantity control (in the case of a replace message).
	 */
	@Override
	public void setFocus() {
		IOrderTicket ticket = getOrderTicket();
		if(ticket.getSideCombo().isEnabled()) {
			ticket.getSideCombo().setFocus();
		} else {
			ticket.getQuantityText().setFocus();
		}
	}


}
