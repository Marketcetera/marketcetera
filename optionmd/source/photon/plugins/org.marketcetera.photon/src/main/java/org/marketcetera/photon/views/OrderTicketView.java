package org.marketcetera.photon.views;

import org.eclipse.core.databinding.AggregateValidationStatus;
import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.ObservablesManager;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.conversion.Converter;
import org.eclipse.core.databinding.conversion.NumberToStringConverter;
import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.list.IListChangeListener;
import org.eclipse.core.databinding.observable.list.ListChangeEvent;
import org.eclipse.core.databinding.observable.list.ListDiffEntry;
import org.eclipse.core.databinding.observable.value.DecoratingObservableValue;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.marketcetera.photon.BrokerManager;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.BrokerManager.Broker;
import org.marketcetera.photon.BrokerManager.BrokerLabelProvider;
import org.marketcetera.photon.commons.databinding.ITypedObservableValue;
import org.marketcetera.photon.commons.databinding.TypedConverter;
import org.marketcetera.photon.commons.databinding.TypedObservableValue;
import org.marketcetera.photon.commons.databinding.TypedObservableValueDecorator;
import org.marketcetera.photon.commons.ui.databinding.RequiredFieldSupport;
import org.marketcetera.photon.commons.ui.databinding.UpdateStrategyFactory;
import org.marketcetera.photon.ui.databinding.StatusToImageConverter;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.NewOrReplaceOrder;
import org.marketcetera.trade.OrderReplace;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.util.misc.ClassVersion;

import com.ibm.icu.text.NumberFormat;

/* $License$ */

/**
 * This is the abstract base class for all order ticket views. It is responsible
 * for setting up the databindings for the "common" order ticket fields, such as
 * side, price, and time in force.
 * 
 * It also is responsible for managing the "custom fields" for order messages
 * that can be set by the user in the preferences dialog, and activated in the
 * order ticket.
 * 
 * @author gmiller
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @since 0.6.0
 */
@ClassVersion("$Id$")
public abstract class OrderTicketView<M extends OrderTicketModel, T extends IOrderTicket>
        extends XSWTView<T> {

    private static final String CUSTOM_FIELD_VIEW_SAVED_STATE_KEY_PREFIX = "CUSTOM_FIELD_CHECKED_STATE_OF_"; //$NON-NLS-1$

    private final Class<T> mTicketClass;

    private final ObservablesManager mObservablesManager = new ObservablesManager();

    private final M mModel;

    private IMemento mMemento;

    private ComboViewer mAvailableBrokersViewer;

    private CheckboxTableViewer mCustomFieldsTableViewer;

    private ComboViewer mSideComboViewer;

    private ComboViewer mTimeInForceComboViewer;

    private ComboViewer mOrderTypeComboViewer;

    private IValueChangeListener mFocusListener;

    /**
     * Constructor.
     * 
     * @param ticketClass
     *            type of ticket class
     * @param model
     *            the ticket model
     */
    protected OrderTicketView(Class<T> ticketClass, M model) {
        mTicketClass = ticketClass;
        mModel = model;
    }

    @Override
    public void init(IViewSite site, IMemento memento) throws PartInitException {
        super.init(site, memento);
        mMemento = memento;
    }

    @Override
    protected Class<T> getXSWTInterfaceClass() {
        return mTicketClass;
    }

    /**
     * Returns the view model.
     * 
     * @return the view model
     */
    protected M getModel() {
        return mModel;
    }

    /**
     * Returns the {@link ObservablesManager} that will clean up managed
     * observables.
     * 
     * @return the observables manager
     */
    public ObservablesManager getObservablesManager() {
        return mObservablesManager;
    }

    @Override
    protected void finishUI() {
        T ticket = getXSWTView();

        /*
         * Set background of error message area.
         */
        Color bg = ticket.getForm().getParent().getBackground();
        ticket.getErrorIconLabel().setBackground(bg);
        ticket.getErrorMessageLabel().setBackground(bg);

        /*
         * Set up viewers.
         */
        initViewers(ticket);

        /*
         * Additional widget customizations.
         */
        customizeWidgets(ticket);

        /*
         * Handle clear button click.
         */
        ticket.getClearButton().addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                getModel().clearOrderMessage();
            }
        });

        /*
         * Handle send button click.
         */
        ticket.getSendButton().addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                handleSend();
            }
        });

        /*
         * Bind to model.
         */
        try {
            bindFormTitle();
            bindMessage();
            bindCustomFields();
        } catch (Exception e) {
            PhotonPlugin.getMainConsoleLogger().error(
                    Messages.ORDER_TICKET_VIEW_CANNOT_BIND_TO_TICKET.getText(),
                    e);
        }

        /*
         * Initialize validation (error message area).
         */
        initValidation();

        /*
         * Control focus when the model's order changes.
         */
        mFocusListener = new IValueChangeListener() {
            @Override
            public void handleValueChange(ValueChangeEvent event) {
                setFocus();
            }
        };
        getModel().getOrderObservable().addValueChangeListener(mFocusListener);

        ticket.getForm().reflow(true);
    }

    /**
     * Customize the widgets.
     * 
     * @param ticket
     *            the order ticket.
     */
    protected void customizeWidgets(T ticket) {
        /*
         * Update size of text fields since default will be small.
         */
        updateSize(ticket.getQuantityText(), 10);
        updateSize(ticket.getSymbolText(), 10);
        updateSize(ticket.getPriceText(), 10);
        updateSize(ticket.getAccountText(), 10);

        /*
         * Customize text fields to auto select the text on focus to make it
         * easy to change the value.
         */
        selectOnFocus(ticket.getQuantityText());
        selectOnFocus(ticket.getSymbolText());
        selectOnFocus(ticket.getPriceText());
        selectOnFocus(ticket.getAccountText());

        /*
         * If the ticket has no errors, enter on these fields will trigger a
         * send.
         */
        addSendOrderListener(ticket.getSideCombo());
        addSendOrderListener(ticket.getQuantityText());
        addSendOrderListener(ticket.getSymbolText());
        addSendOrderListener(ticket.getOrderTypeCombo());
        addSendOrderListener(ticket.getPriceText());
        addSendOrderListener(ticket.getBrokerCombo());
        addSendOrderListener(ticket.getTifCombo());
        addSendOrderListener(ticket.getAccountText());
    }

    /**
     * Set up viewers.
     * 
     * @param ticket
     */
    protected void initViewers(T ticket) {
        /*
         * Side combo based on Side enum.
         */
        mSideComboViewer = new ComboViewer(ticket.getSideCombo());
        mSideComboViewer.setContentProvider(new ArrayContentProvider());
        mSideComboViewer.setInput(getModel().getValidSideValues());

        /*
         * Order type combo based on OrderType enum.
         */
        mOrderTypeComboViewer = new ComboViewer(ticket.getOrderTypeCombo());
        mOrderTypeComboViewer.setContentProvider(new ArrayContentProvider());
        mOrderTypeComboViewer.setInput(getModel().getValidOrderTypeValues());

        /*
         * Broker combo based on available brokers.
         */
        mAvailableBrokersViewer = new ComboViewer(ticket.getBrokerCombo());
        mAvailableBrokersViewer
                .setContentProvider(new ObservableListContentProvider());
        mAvailableBrokersViewer.setLabelProvider(new BrokerLabelProvider());
        mAvailableBrokersViewer.setInput(getModel().getValidBrokers());

        /*
         * Time in Force combo based on TimeInForce enum.
         * 
         * An extra blank entry is added since the field is optional.
         */
        mTimeInForceComboViewer = new ComboViewer(ticket.getTifCombo());
        mTimeInForceComboViewer.setContentProvider(new ArrayContentProvider());
        mTimeInForceComboViewer
                .setInput(getModel().getValidTimeInForceValues());

        /*
         * Custom fields table.
         * 
         * Input is bound to model in bindCustomFields.
         */
        mCustomFieldsTableViewer = new CheckboxTableViewer(ticket
                .getCustomFieldsTable());
        ObservableListContentProvider contentProvider = new ObservableListContentProvider();
        mCustomFieldsTableViewer.setContentProvider(contentProvider);
        mCustomFieldsTableViewer
                .setLabelProvider(new ObservableMapLabelProvider(
                        BeansObservables.observeMaps(contentProvider
                                .getKnownElements(), CustomField.class,
                                new String[] { "keyString", "valueString" })));//$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Get the UI string to show for a "new order" message.
     * 
     * @return the UI string
     */
    protected abstract String getNewOrderString();

    /**
     * Get the UI string to show for a "replace" message.
     * 
     * @return the UI string
     */
    protected abstract String getReplaceOrderString();

    /**
     * Bind the top level form title to show different text depending on the
     * order type.
     */
    protected void bindFormTitle() {
        TypedObservableValue<String> formTextObservable = new TypedObservableValue<String>(
                String.class) {
            @Override
            protected String doGetValue() {
                return getXSWTView().getForm().getText();
            }

            @Override
            protected void doSetTypedValue(String value) {
                getXSWTView().getForm().setText(value);
            }
        };
        getObservablesManager().addObservable(formTextObservable);
        getDataBindingContext().bindValue(
                formTextObservable,
                getModel().getOrderObservable(),
                null,
                new UpdateValueStrategy().setConverter(new Converter(
                        NewOrReplaceOrder.class, String.class) {
                    public Object convert(Object fromObject) {
                        if (fromObject instanceof OrderReplace) {
                            return getReplaceOrderString();
                        } else if (fromObject instanceof OrderSingle) {
                            return getNewOrderString();
                        } else {
                            return null;
                        }
                    }
                }));
    }

    /**
     * Binds the UI to the model.
     */
    protected void bindMessage() {
        final DataBindingContext dbc = getDataBindingContext();
        final OrderTicketModel model = getModel();
        final IOrderTicket ticket = getXSWTView();

        /*
         * Side
         */
        bindRequiredCombo(mSideComboViewer, model.getSide(),
                Messages.ORDER_TICKET_VIEW_SIDE__LABEL.getText());
        enableForNewOrderOnly(mSideComboViewer.getControl());

        /*
         * Quantity
         */
        bindRequiredDecimal(ticket.getQuantityText(), model.getQuantity(),
                Messages.ORDER_TICKET_VIEW_QUANTITY__LABEL.getText());

        /*
         * Symbol
         */
        bindRequiredText(ticket.getSymbolText(), getModel().getSymbol(),
                Messages.ORDER_TICKET_VIEW_SYMBOL__LABEL.getText());
        enableForNewOrderOnly(ticket.getSymbolText());

        /*
         * Order Type
         */
        bindRequiredCombo(mOrderTypeComboViewer, model.getOrderType(),
                Messages.ORDER_TICKET_VIEW_ORDER_TYPE__LABEL.getText());

        /*
         * Price
         * 
         * Need custom required field logic since price is only required for
         * limit orders.
         */
        Binding binding = bindDecimal(ticket.getPriceText(), model.getPrice(),
                Messages.ORDER_TICKET_VIEW_PRICE__LABEL.getText());
        /*
         * RequiredFieldSupport reports an error if the value is null or empty
         * string. We want this behavior when the order is a limit order, but
         * not when it is a market order (since empty string is correct as the
         * price is uneditable. So we decorate the observable and pass the
         * decorated one to RequiredFieldsupport.
         */
        IObservableValue priceDecorator = new DecoratingObservableValue(
                (IObservableValue) binding.getTarget(), false) {
            @Override
            public Object getValue() {
                Object actualValue = super.getValue();
                if ("".equals(actualValue) //$NON-NLS-1$
                        && !model.isLimitOrder().getTypedValue()) {
                    /*
                     * Return an object to "trick" RequiredFieldSupport to not
                     * error.
                     */
                    return new Object();
                }
                return actualValue;
            }
        };
        RequiredFieldSupport.initFor(dbc, priceDecorator,
                Messages.ORDER_TICKET_VIEW_PRICE__LABEL.getText(), false,
                SWT.BOTTOM | SWT.LEFT, binding);
        dbc.bindValue(SWTObservables.observeEnabled(ticket.getPriceText()),
                model.isLimitOrder());

        /*
         * Broker
         * 
         * Need an intermediary observable to handle conversion from Broker to
         * BrokerID.
         */
        ITypedObservableValue<Broker> broker = TypedObservableValueDecorator
                .create(Broker.class);
        getObservablesManager().addObservable(broker);
        dbc.bindValue(broker, model.getBrokerId(), new UpdateValueStrategy()
                .setConverter(new TypedConverter<Broker, BrokerID>(
                        Broker.class, BrokerID.class) {
                    @Override
                    public BrokerID doConvert(Broker fromObject) {
                        return fromObject.getId();
                    }
                }), new UpdateValueStrategy()
                .setConverter(new TypedConverter<BrokerID, Broker>(
                        BrokerID.class, Broker.class) {
                    @Override
                    public Broker doConvert(BrokerID fromObject) {
                        if (fromObject == null) {
                            return BrokerManager.AUTO_SELECT_BROKER;
                        }
                        for (Object obj : BrokerManager.getCurrent()
                                .getAvailableBrokers()) {
                            Broker broker = (Broker) obj;
                            BrokerID id = broker.getId();
                            if (id != null && id.equals(fromObject)) {
                                return broker;
                            }
                        }
                        return BrokerManager.AUTO_SELECT_BROKER;
                    }
                }));
        bindCombo(mAvailableBrokersViewer, broker);
        enableForNewOrderOnly(mAvailableBrokersViewer.getControl());

        /*
         * Time in Force
         */
        bindCombo(mTimeInForceComboViewer, model.getTimeInForce());

        /*
         * Account
         */
        bindText(getXSWTView().getAccountText(), model.getAccount());
    }

    /**
     * Bind the custom fields on the model to the view.
     */
    protected void bindCustomFields() {
        M model = getModel();
        mCustomFieldsTableViewer.setInput(model.getCustomFieldsList());
        mCustomFieldsTableViewer
                .addCheckStateListener(new ICheckStateListener() {
                    public void checkStateChanged(CheckStateChangedEvent event) {
                        Object source = event.getElement();
                        ((CustomField) source).setEnabled(event.getChecked());
                    }
                });
        model.getCustomFieldsList().addListChangeListener(
                new IListChangeListener() {
                    public void handleListChange(ListChangeEvent event) {
                        ScrolledForm theForm = getXSWTView().getForm();
                        if (!theForm.isDisposed()) {
                            ListDiffEntry[] differences = event.diff
                                    .getDifferences();
                            for (ListDiffEntry listDiffEntry : differences) {
                                if (listDiffEntry.isAddition()) {
                                    CustomField customField = (CustomField) listDiffEntry
                                            .getElement();
                                    String key = CUSTOM_FIELD_VIEW_SAVED_STATE_KEY_PREFIX
                                            + customField.getKeyString();
                                    IMemento theMemento = getMemento();
                                    if (theMemento != null
                                            && theMemento.getInteger(key) != null) {
                                        boolean itemChecked = (theMemento
                                                .getInteger(key).intValue() != 0);
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
     * Initialization the validation (error message area) of the view.
     */
    protected void initValidation() {
        DataBindingContext dbc = getDataBindingContext();
        AggregateValidationStatus aggregateValidationStatus = new AggregateValidationStatus(
                dbc, AggregateValidationStatus.MAX_SEVERITY);

        dbc.bindValue(SWTObservables.observeText(getXSWTView()
                .getErrorMessageLabel()), aggregateValidationStatus);

        dbc.bindValue(SWTObservables.observeImage(getXSWTView()
                .getErrorIconLabel()), aggregateValidationStatus, null,
                new UpdateValueStrategy()
                        .setConverter(new StatusToImageConverter()));

        dbc.bindValue(SWTObservables.observeEnabled(getXSWTView()
                .getSendButton()), aggregateValidationStatus, null,
                new UpdateValueStrategy()
                        .setConverter(new TypedConverter<IStatus, Boolean>(
                                IStatus.class, Boolean.class) {
                            @Override
                            public Boolean doConvert(IStatus fromObject) {
                                return fromObject.getSeverity() < IStatus.ERROR;
                            }
                        }));
    }

    /**
     * Binds a combo viewer to a model field that is required.
     * 
     * @param viewer
     *            the viewer
     * @param model
     *            the model observable
     * @return the binding
     */
    protected Binding bindCombo(ComboViewer viewer, IObservableValue model) {
        DataBindingContext dbc = getDataBindingContext();
        IObservableValue target = ViewersObservables
                .observeSingleSelection(viewer);
        return dbc.bindValue(target, model, new UpdateValueStrategy()
                .setConverter(new Converter(target.getValueType(), model
                        .getValueType()) {
                    @Override
                    public Object convert(Object fromObject) {
                        return fromObject instanceof OrderTicketModel.NullSentinel ? null
                                : fromObject;
                    }
                }), null);
    }

    /**
     * Binds a combo viewer and makes it required.
     * 
     * @param viewer
     *            the viewer
     * @param model
     *            the model observable
     * @param description
     *            the description for error messages
     * @return the binding
     */
    protected Binding bindRequiredCombo(ComboViewer viewer,
            IObservableValue model, String description) {
        DataBindingContext dbc = getDataBindingContext();
        IObservableValue target = ViewersObservables
                .observeSingleSelection(viewer);
        Binding binding = dbc.bindValue(target, model);
        setRequired(binding, description);
        return binding;
    }

    /**
     * Binds a text widget to a BigDecimal value.
     * 
     * @param text
     *            the widget
     * @param model
     *            the model observable
     * @param description
     *            the description for error messages
     * @return the binding
     */
    protected Binding bindDecimal(Text text, IObservableValue model,
            String description) {
        DataBindingContext dbc = getDataBindingContext();
        IObservableValue target = SWTObservables.observeText(text, SWT.Modify);
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setGroupingUsed(false);
        return dbc.bindValue(target, model, UpdateStrategyFactory
                .withConvertErrorMessage(new UpdateValueStrategy(),
                        Messages.ORDER_TICKET_VIEW_NOT_DECIMAL_ERROR
                                .getText(description)),
                new UpdateValueStrategy().setConverter(NumberToStringConverter
                        .fromBigDecimal(numberFormat)));
    }

    /**
     * Binds a text widget to a BigDecimal value and makes it required.
     * 
     * @param text
     *            the widget
     * @param model
     *            the model observable
     * @param description
     *            the description for error messages
     * @return the binding
     */
    protected Binding bindRequiredDecimal(Text text, IObservableValue model,
            String description) {
        Binding binding = bindDecimal(text, model, description);
        setRequired(binding, description);
        return binding;
    }

    /**
     * Binds a text widget to the model.
     * 
     * @param text
     *            the widget
     * @param model
     *            the model observable
     * @return the binding
     */
    protected Binding bindText(Text text, IObservableValue model) {
        DataBindingContext dbc = getDataBindingContext();
        IObservableValue target = SWTObservables.observeText(text, SWT.Modify);
        UpdateValueStrategy targetToModel = null;
        if (model.getValueType() == String.class) {
            /*
             * Clearing a text box should set the model to null, not empty
             * string.
             */
            targetToModel = new UpdateValueStrategy()
                    .setConverter(new TypedConverter<String, String>(
                            String.class, String.class) {
                        @Override
                        protected String doConvert(String fromObject) {
                            if (fromObject != null && fromObject.isEmpty()) {
                                return null;
                            }
                            return fromObject;
                        }
                    });
        }
        return dbc.bindValue(target, model, targetToModel, null);
    }

    /**
     * Binds a text widget and makes it required.
     * 
     * @param text
     *            the widget
     * @param model
     *            the model observable
     * @param description
     *            the description for error messages
     * @return the binding
     */
    protected Binding bindRequiredText(Text text, IObservableValue model,
            String description) {
        Binding binding = bindText(text, model);
        setRequired(binding, description);
        return binding;
    }

    /**
     * Add required semantics to a binding.
     * 
     * @param binding
     *            the binding
     * @param description
     *            the description for error messages
     */
    protected void setRequired(Binding binding, String description) {
        RequiredFieldSupport.initFor(getDataBindingContext(), binding
                .getTarget(), description, false, SWT.BOTTOM | SWT.LEFT,
                binding);
    }

    /**
     * Add required semantics to a control.
     * 
     * @param target
     *            the control's observable
     * @param description
     *            the description for error messages
     * @param binding
     *            a binding that also contributes validation status, can be null
     */
    protected void setRequired(IObservable target, String description,
            Binding binding) {
        RequiredFieldSupport.initFor(getDataBindingContext(), target,
                description, false, SWT.BOTTOM | SWT.LEFT, binding);
    }

    /**
     * Configures a control to be enabled only when model contains a new order
     * (as opposed to a replace order).
     * 
     * @param control
     *            the control
     */
    protected void enableForNewOrderOnly(Control control) {
        getDataBindingContext().bindValue(
                SWTObservables.observeEnabled(control),
                getModel().getOrderObservable(),
                null,
                new UpdateValueStrategy().setConverter(new Converter(
                        NewOrReplaceOrder.class, Boolean.class) {
                    @Override
                    public Object convert(Object fromObject) {
                        return fromObject instanceof OrderSingle;
                    }
                }));
    }

    /**
     * Customizes a text widget to select the entire text when it receives focus
     * (makes it easy to change).
     * 
     * @param text
     *            the widget
     */
    protected void selectOnFocus(Text text) {
        text.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                ((Text) e.widget).selectAll();
            }
        });
    }

    /**
     * Hook up a listener to the targetControl that listens for {@link SWT#CR}
     * and invokes {@link #handleSend()}.
     * 
     * @param targetControl
     *            the control to hook up
     */
    protected void addSendOrderListener(Control targetControl) {
        targetControl.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.character == SWT.CR) {
                    if (getXSWTView().getSendButton().isEnabled()) {
                        handleSend();
                    }
                }
            }
        });
    }

    /**
     * This method "completes" the message by calling
     * {@link OrderTicketModel#completeMessage()}, sends the order via the
     * controller, then resets the message in the view model.
     */
    protected void handleSend() {
        try {
            // TODO: this logic should probably be in the controller
            PhotonPlugin plugin = PhotonPlugin.getDefault();
            mModel.completeMessage();
            NewOrReplaceOrder orderMessage = mModel.getOrderObservable()
                    .getTypedValue();
            plugin.getPhotonController().sendOrderChecked(orderMessage);
            mModel.clearOrderMessage();
        } catch (Exception e) {
            String errorMessage = e.getLocalizedMessage();
            PhotonPlugin.getMainConsoleLogger().error(errorMessage);
            showErrorMessage(errorMessage, IStatus.ERROR);
        }
    }

    /**
     * Show the given error message in this order ticket's error display area.
     * 
     * @param errorMessage
     *            the text of the error message
     * @param severity
     *            the severity of the error message, see {@link IStatus}
     */
    protected void showErrorMessage(String errorMessage, int severity) {
        Label errorMessageLabel = getXSWTView().getErrorMessageLabel();
        Label errorIconLabel = getXSWTView().getErrorIconLabel();

        if (errorMessage == null) {
            errorMessageLabel.setText(""); //$NON-NLS-1$
            errorIconLabel.setImage(null);
        } else {
            errorMessageLabel.setText(errorMessage);
            if (severity == IStatus.OK) {
                errorIconLabel.setImage(null);
            } else {
                if (severity == IStatus.ERROR)
                    errorIconLabel.setImage(FieldDecorationRegistry
                            .getDefault().getFieldDecoration(
                                    FieldDecorationRegistry.DEC_ERROR)
                            .getImage());
                else
                    errorIconLabel.setImage(FieldDecorationRegistry
                            .getDefault().getFieldDecoration(
                                    FieldDecorationRegistry.DEC_WARNING)
                            .getImage());
            }
        }
    }

    /**
     * Get the memento used for storing preferences and state for this view.
     * 
     * @return the memento
     */
    protected IMemento getMemento() {
        return mMemento;
    }

    /**
     * Stores the checked state of each of the custom fields in the view.
     */
    @Override
    public void saveState(IMemento memento) {
        TableItem[] items = getXSWTView().getCustomFieldsTable().getItems();
        for (int i = 0; i < items.length; i++) {
            TableItem item = items[i];
            String key = OrderTicketView.CUSTOM_FIELD_VIEW_SAVED_STATE_KEY_PREFIX
                    + item.getText(1);
            memento.putInteger(key, (item.getChecked() ? 1 : 0));
        }
    }

    /**
     * Set the focus on the Side control (in the case of a new order) or the
     * Quantity control (in the case of a replace order).
     */
    @Override
    public void setFocus() {
        IOrderTicket ticket = getXSWTView();
        if (ticket.getSideCombo().isEnabled()) {
            ticket.getSideCombo().setFocus();
        } else {
            ticket.getQuantityText().setFocus();
        }
    }

    @Override
    public void dispose() {
        getModel().getOrderObservable().removeValueChangeListener(
                mFocusListener);
        mObservablesManager.dispose();
        super.dispose();
    }
}