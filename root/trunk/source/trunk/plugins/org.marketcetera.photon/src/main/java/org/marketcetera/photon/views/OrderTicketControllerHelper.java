package org.marketcetera.photon.views;

import java.util.HashMap;
import java.util.HashSet;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.conversion.NumberToStringConverter;
import org.eclipse.core.databinding.conversion.StringToNumberConverter;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.map.IMapChangeListener;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.map.MapChangeEvent;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.databinding.swt.ISWTObservable;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.marketcetera.core.MSymbol;
import org.marketcetera.core.MarketceteraException;
import org.marketcetera.marketdata.ISubscription;
import org.marketcetera.marketdata.MarketDataListener;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.marketdata.MarketDataFeedService;
import org.marketcetera.photon.marketdata.MarketDataFeedTracker;
import org.marketcetera.photon.marketdata.MarketDataUtils;
import org.marketcetera.photon.parser.PriceImage;
import org.marketcetera.photon.parser.SideImage;
import org.marketcetera.photon.parser.TimeInForceImage;
import org.marketcetera.photon.ui.validation.DataDictionaryValidator;
import org.marketcetera.photon.ui.validation.DecimalRequiredValidator;
import org.marketcetera.photon.ui.validation.IToggledValidator;
import org.marketcetera.photon.ui.validation.IntegerRequiredValidator;
import org.marketcetera.photon.ui.validation.StringRequiredValidator;
import org.marketcetera.photon.ui.validation.fix.BigDecimalToStringConverter;
import org.marketcetera.photon.ui.validation.fix.EnumStringConverterBuilder;
import org.marketcetera.photon.ui.validation.fix.FIXObservables;
import org.marketcetera.photon.ui.validation.fix.PriceConverterBuilder;
import org.marketcetera.photon.ui.validation.fix.StringToBigDecimalConverter;
import org.marketcetera.quickfix.FIXDataDictionaryManager;
import org.marketcetera.quickfix.FIXMessageUtil;

import quickfix.DataDictionary;
import quickfix.FieldNotFound;
import quickfix.FieldType;
import quickfix.Message;
import quickfix.field.Account;
import quickfix.field.MsgType;
import quickfix.field.OrdType;
import quickfix.field.OrderQty;
import quickfix.field.Price;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.field.TimeInForce;

/**
 * 
 * @author michael.lossos@softwaregoodness.com
 *
 */
public class OrderTicketControllerHelper {
	private IOrderTicket ticket;

	private MarketDataFeedTracker marketDataTracker;

	private MSymbol listenedSymbol = null;

	private ISubscription currentSubscription;

	private MarketDataListener marketDataListener;

	private Message targetMessage;

	private EnumStringConverterBuilder<? extends Object> sideConverterBuilder;

	private EnumStringConverterBuilder<? extends Object> tifConverterBuilder;

	private PriceConverterBuilder priceConverterBuilder;

	private DataDictionary dictionary;

	private DataBindingContext dataBindingContext;

	private BindingHelper bindingHelper;

	private Color colorRed;

	private HashSet<Control> controlsRequiringUserInput;

	private HashMap<Control, IStatus> inputControlErrorStatus;

	private HashSet<IToggledValidator> allValidators;

	private Realm targetRealm;

	protected boolean orderQtyIsInt;

	private boolean hasRealCharDatatype;

	private boolean bindErrorsOccurred;
	
	protected boolean bindSymbolToModelDirection = true;
	
	public OrderTicketControllerHelper(IOrderTicket ticket) {
		this.ticket = ticket;
	}

	public void init() {

		preInit();
		initListeners();
		initBuilders();
		postInit();
	}

	protected void preInit() {
		bindingHelper = new BindingHelper();

		colorRed = Display.getCurrent().getSystemColor(SWT.COLOR_RED);

		dictionary = FIXDataDictionaryManager.getCurrentFIXDataDictionary()
				.getDictionary();
		dataBindingContext = new DataBindingContext();

		orderQtyIsInt = (FieldType.Int == dictionary
				.getFieldTypeEnum(OrderQty.FIELD));
		hasRealCharDatatype = FieldType.Char.equals(dictionary
				.getFieldTypeEnum(Side.FIELD));

		allValidators = new HashSet<IToggledValidator>();
	}

	protected void initListeners() {
		marketDataTracker = new MarketDataFeedTracker(PhotonPlugin.getDefault()
				.getBundleContext());
		marketDataTracker.open();

		marketDataListener = createMarketDataListener();
		marketDataTracker.setMarketDataListener(marketDataListener);

		ticket.getSymbolText().addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				((Text) e.widget).selectAll();
			}

			@Override
			public void focusLost(FocusEvent e) {
				listenMarketData(((Text) e.widget).getText());
			}
		});

//		ticket.getSymbolText().addModifyListener(new ModifyListener() {
//			public void modifyText(ModifyEvent e) {
//				Text symbolText = (Text) e.widget;
//				if (!symbolText.isFocusControl()) {
//					String symbolTextString = symbolText.getText();
//					listenMarketData(symbolTextString);
//				}
//			}
//		});

		ticket.getCancelButton().addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				handleCancel();
			}

			public void widgetSelected(SelectionEvent e) {
				handleCancel();
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
	}

	protected MarketDataListener createMarketDataListener() {
		MarketDataListener dataListener = new MarketDataListener() {
			public void onMessage(Message aQuote) {
				OrderTicketControllerHelper.this.onQuote(aQuote);
			}
		};
		return dataListener;
	}

	protected void initBuilders() {
		initSideConverterBuilder();
		initTifConverterBuilder();
		initPriceConverterBuilder();
	}

	protected void postInit() {
		resetTrackers();

		// To force the initial state to appear the same as the Canceled state,
		// bind first, then clear. The IMapChangeListener is notified when the
		// controls are unbound.
		bind(newNewOrderSingle(), false);
		clear();
	}

	public void dispose() {
		marketDataTracker.setMarketDataListener(null);
		marketDataTracker.close();
		dataBindingContext.dispose();
		// don't dispose of system colors
	}

	public void listenMarketData(String symbol) {
		unlisten();
		if (symbol != null && !"".equals(symbol.trim())) {
			MSymbol newListenedSymbol = new MSymbol(symbol);
			MarketDataFeedService service = marketDataTracker
					.getMarketDataFeedService();

			if (service != null && !newListenedSymbol.equals(listenedSymbol)) {
				if (listenedSymbol != null) {
					unlisten();
				}
				Message subscriptionMessage = MarketDataUtils
						.newSubscribeLevel2(newListenedSymbol);
				ISubscription subscription = null;
				try {
					subscription = service.subscribe(subscriptionMessage);
					currentSubscription = subscription;
					listenMarketDataAdditional(symbol);
				} catch (MarketceteraException e) {
					PhotonPlugin.getMainConsoleLogger().error(
							"Exception requesting quotes for "
									+ newListenedSymbol);
				} finally {
					listenedSymbol = newListenedSymbol;
				}
			}
		}
	}

	/**
	 * Derived classes can listen for additional market data when subscribing to
	 * a new symbol.
	 */
	protected void listenMarketDataAdditional(String symbol)
			throws MarketceteraException {
	}

	/**
	 * Derived classes can unsubscribe the additional market data they are
	 * listening to.
	 */
	protected void unlistenMarketDataAdditional() throws MarketceteraException {
	}

	protected void unlisten() {
		MarketDataFeedService service = marketDataTracker
				.getMarketDataFeedService();

		if (service != null) {
			if (currentSubscription != null) {
				try {
					service.unsubscribe(currentSubscription);
					listenedSymbol = null;
					currentSubscription = null;
					unlistenMarketDataAdditional();
				} catch (MarketceteraException e) {
					PhotonPlugin.getMainConsoleLogger().warn(
							"Error unsubscribing to quotes for "
									+ listenedSymbol);
				}
			}
		}
		ticket.getBookComposite().setInput(null);
	}

	public void onQuote(Message message) {
		try {
			if (listenedSymbol != null && 
					(FIXMessageUtil.isMarketDataIncrementalRefresh(message)
					|| FIXMessageUtil.isMarketDataSnapshotFullRefresh(message))
					) {
				String listenedSymbolString = listenedSymbol.toString();
				if (message.isSetField(Symbol.FIELD)
						&& listenedSymbolString.equals(message
								.getString(Symbol.FIELD))) {
					ticket.getBookComposite().onQuote(message);  
				}
			}
		} catch (FieldNotFound e) {
			// Do nothing
		}
	}

	public void handleSend() {
		try {
			PhotonPlugin plugin = PhotonPlugin.getDefault();
			ticket.updateMessage(targetMessage);
			plugin.getPhotonController().handleInternalMessage(targetMessage);
			clear();
		} catch (Exception e) {
			PhotonPlugin.getMainConsoleLogger().error(
					"Error sending order: " + e.getMessage(), e);
		}
	}

	public void handleCancel() {
		clear();
	}

	private void addInputControlSendOrderListeners() {
		addSendOrderListener(ticket.getSideCombo());
		addSendOrderListener(ticket.getQuantityText());
		addSendOrderListener(ticket.getSymbolText());
		addSendOrderListener(ticket.getPriceText());
		addSendOrderListener(ticket.getTifCombo());
	}

	public void addSendOrderListener(Control targetControl) {
		targetControl.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.keyCode == SWT.CR) {
					if (ticket.getSendButton().isEnabled()) {
						handleSend();
					}
				}
			}
		});
	}

	private void resetTrackers() {
		controlsRequiringUserInput = new HashSet<Control>();
		inputControlErrorStatus = new HashMap<Control, IStatus>();
	}

	public void addControlRequiringUserInput(Control targetControl) {
		if (targetControl != null) {
			controlsRequiringUserInput.add(targetControl);
		}
	}

	public void clear() {
		unlisten();
		unbind();
		ticket.clear();
		resetTrackers();
		bind(newNewOrderSingle(), false);
		updateSendButtonState();
	}

	private void unbind() {
		IObservableList bindingList = dataBindingContext.getBindings();
		Object[] bindings = bindingList.toArray();
		for (Object bindingObj : bindings) {
			((Binding) (bindingObj)).dispose();
		}
	}

	protected void bind(Message message, boolean enableValidators) {
		try {
			bindErrorsOccurred = false;
			bindImpl(message, enableValidators);
		} catch (Exception ex) {
			bindErrorsOccurred = true;
			ex.printStackTrace();
		}
	}
	
	public boolean hasBindErrors() {
		return bindErrorsOccurred;
	}
	
	protected void bindValue(Control whichControl,
			IObservableValue targetObservableValue,
			IObservableValue modelObservableValue,
			UpdateValueStrategy targetToModel, UpdateValueStrategy modelToTarget) {

		// This is a workaround for a databinding bug that doesn't 
		// reset ComboObservableValue.currentValue when rebinding.
		// See http://trac.marketcetera.org/trac.fcgi/ticket/313
		if (whichControl instanceof Combo) {
			Combo combo = (Combo) whichControl;
			combo.setText("");
		} 
		else if( whichControl instanceof CCombo) {
			CCombo combo = (CCombo) whichControl;
			combo.setText("");
		} else if (whichControl instanceof Text) {
			Text text = (Text) whichControl;
			text.setText("");
		}

		DataBindingContext context = getDataBindingContext();
		context.bindValue(targetObservableValue, modelObservableValue,
				targetToModel, modelToTarget);
	}
	
	protected void bindImpl(Message message, boolean enableValidators) {

		targetMessage = message;
		Realm realm = Realm.getDefault();
		targetRealm = realm;
		// todo: Refactor to use BindingHelper for UpdateValueStrategy
		// creation.
		final int swtEvent = SWT.Modify;
		{
			Control whichControl = ticket.getSideCombo();
			IToggledValidator validator = sideConverterBuilder
					.newTargetAfterGetValidator();
			validator.setEnabled(enableValidators);
			bindValue( whichControl, 
							SWTObservables.observeText(whichControl),
							FIXObservables.observeValue(realm, message,
									Side.FIELD, dictionary),
							new UpdateValueStrategy().setAfterGetValidator(
									validator).setConverter(
									sideConverterBuilder.newToModelConverter()),
							new UpdateValueStrategy()
									.setConverter(sideConverterBuilder
											.newToTargetConverter()));
			addControlStateListeners(whichControl, validator);
			if (!enableValidators)
				addControlRequiringUserInput(whichControl);
		}
		{
			Control whichControl = ticket.getQuantityText();
			IToggledValidator validator;
			IConverter toModelConverter;
			IConverter toUIConverter;
			if (orderQtyIsInt) {
				validator = new IntegerRequiredValidator();
				toModelConverter = StringToNumberConverter.toInteger(false);
				toUIConverter = NumberToStringConverter.fromInteger(false);
			} else {
				validator = new DecimalRequiredValidator();
				toModelConverter = new StringToBigDecimalConverter();
				toUIConverter = new BigDecimalToStringConverter();
			}
			validator.setEnabled(enableValidators);
			bindValue( whichControl, SWTObservables.observeText(
					whichControl, swtEvent), FIXObservables.observeValue(realm,
					message, OrderQty.FIELD, dictionary),
					new UpdateValueStrategy().setAfterGetValidator(validator)
							.setConverter(toModelConverter),
					new UpdateValueStrategy().setConverter(toUIConverter));
			addControlStateListeners(whichControl, validator);
			if (!enableValidators)
				addControlRequiringUserInput(whichControl);
		}
		{
			Control whichControl = ticket.getSymbolText();
			IToggledValidator validator = new StringRequiredValidator();
			validator.setEnabled(enableValidators);
			int toModelUpdatePolicy = bindSymbolToModelDirection ? UpdateValueStrategy.POLICY_UPDATE : UpdateValueStrategy.POLICY_NEVER;
			bindValue( 
					whichControl,
					SWTObservables.observeText(whichControl, swtEvent),
					FIXObservables.observeValue(realm, message, Symbol.FIELD, dictionary),
					new UpdateValueStrategy(toModelUpdatePolicy).setAfterGetValidator(validator),
					new UpdateValueStrategy()
					);
			addControlStateListeners(whichControl, validator);
			if (!enableValidators)
				addControlRequiringUserInput(whichControl);
		}
		{
			Control whichControl = ticket.getPriceText();
			IToggledValidator validator = priceConverterBuilder
					.newTargetAfterGetValidator();
			validator.setEnabled(enableValidators);
			bindValue( whichControl, 
					SWTObservables.observeText(whichControl,
							swtEvent), FIXObservables.observePriceValue(realm,
							message, Price.FIELD, dictionary),
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
			addControlStateListeners(whichControl, validator);
			if (!enableValidators)
				addControlRequiringUserInput(whichControl);
		}
		{
			Control whichControl = ticket.getTifCombo();
			IToggledValidator afterGetValidator = tifConverterBuilder
					.newTargetAfterGetValidator();
			afterGetValidator.setEnabled(enableValidators);
			IToggledValidator afterConvertValidator = new DataDictionaryValidator(
					dictionary, TimeInForce.FIELD,
					"Not a valid value for TimeInForce", PhotonPlugin.ID);
			afterConvertValidator.setEnabled(enableValidators);
			bindValue( whichControl, SWTObservables
					.observeText(whichControl), FIXObservables.observeValue(
					realm, message, TimeInForce.FIELD, dictionary),
					new UpdateValueStrategy().setAfterGetValidator(
							afterGetValidator).setAfterConvertValidator(
							afterConvertValidator).setConverter(
							tifConverterBuilder.newToModelConverter()),
					new UpdateValueStrategy().setConverter(tifConverterBuilder
							.newToTargetConverter()));
			addControlStateListeners(whichControl, afterGetValidator);
			addControlStateListeners(whichControl, afterConvertValidator);
			if (!enableValidators)
				addControlRequiringUserInput(whichControl);
		}
		{
			Control whichControl = ticket.getAccountText();
			bindValue(whichControl, SWTObservables.observeText(whichControl,
					swtEvent), FIXObservables.observeValue(realm, message,
					Account.FIELD, dictionary), new UpdateValueStrategy(),
					new UpdateValueStrategy());
		}

		dataBindingContext.getValidationStatusMap().addMapChangeListener(
				createMapChangeListener());
	}

	public void addControlStateListeners(Control control,
			final IToggledValidator validator) {

		allValidators.add(validator);

		control.addListener(SWT.FocusIn, new Listener() {
			private boolean initialState = true;

			public void handleEvent(Event event) {
				if (initialState) {
					initialState = false;
					validator.setEnabled(true);
				}
				updateSendButtonState();
			}

		});

		control.addListener(SWT.Modify, new Listener() {
			public void handleEvent(Event event) {
				if (!controlsRequiringUserInput.isEmpty()
						&& event.widget instanceof Control) {
					Control aControl = (Control) event.widget;
					controlsRequiringUserInput.remove(aControl);
				}
				updateSendButtonState();
			}

		});
	}

	private IMapChangeListener createMapChangeListener() {
		return new IMapChangeListener() {

			public void handleMapChange(MapChangeEvent event) {
				if (ticket.getErrorMessageLabel().isDisposed()) {
					return;
				}

				for (Object bindingObj : event.diff.getChangedKeys()) {
					IObservableMap validationStatusMap = dataBindingContext
							.getValidationStatusMap();
					IStatus status = (IStatus) validationStatusMap
							.get(bindingObj);
					Binding binding = (Binding) bindingObj;
					ISWTObservable targetObservable = (ISWTObservable) binding
							.getTarget();
					Control aControl = (Control) targetObservable.getWidget();

					ticket.showErrorForControl(aControl, status.getSeverity(),
							status.getMessage());
					if (status.getSeverity() == IStatus.ERROR) {
						inputControlErrorStatus.put(aControl, status);
						ticket.showErrorMessage(status.getMessage(), status
								.getSeverity());
						aControl.setBackground(colorRed);
					} else {
						try {
							aControl
									.setBackground((Color) aControl
											.getData(OrderTicketViewPieces.CONTROL_DEFAULT_COLOR));
						} catch (Exception e) {
							aControl.setBackground(null);
						}
						if (inputControlErrorStatus.containsKey(aControl)) {
							inputControlErrorStatus.remove(aControl);
						}
					}
				}

				conditionallyClearErrors();
				updateSendButtonState();
			}
		};
	}

	private void conditionallyClearErrors() {
		if (inputControlErrorStatus.isEmpty()) {
			ticket.clearErrors();
		}
	}

	private void updateSendButtonState() {
		if (!controlsRequiringUserInput.isEmpty()) {
			ticket.getSendButton().setEnabled(false);
		} else {
			boolean enabled = inputControlErrorStatus.isEmpty();
			ticket.getSendButton().setEnabled(enabled);
		}
	}

	private void initSideConverterBuilder() {
		if (!hasRealCharDatatype) {
			EnumStringConverterBuilder<String> escb = new EnumStringConverterBuilder<String>(
					String.class);

			bindingHelper.initStringToImageConverterBuilder(escb, SideImage
					.values());
			sideConverterBuilder = escb;
		} else {
			EnumStringConverterBuilder<Character> escb = new EnumStringConverterBuilder<Character>(
					Character.class);

			bindingHelper.initCharToImageConverterBuilder(escb, SideImage
					.values());
			sideConverterBuilder = escb;
		}
	}

	private void initTifConverterBuilder() {
		if (!hasRealCharDatatype) {
			EnumStringConverterBuilder<String> escb = new EnumStringConverterBuilder<String>(
					String.class);

			bindingHelper.initStringToImageConverterBuilder(escb,
					TimeInForceImage.values());
			tifConverterBuilder = escb;
		} else {
			EnumStringConverterBuilder<Character> escb = new EnumStringConverterBuilder<Character>(
					Character.class);

			bindingHelper.initCharToImageConverterBuilder(escb,
					TimeInForceImage.values());
			tifConverterBuilder = escb;
		}
	}

	private void initPriceConverterBuilder() {
		priceConverterBuilder = new PriceConverterBuilder(dictionary);

		priceConverterBuilder.addMapping(OrdType.MARKET, PriceImage.MKT
				.getImage());
	}

	protected Message newNewOrderSingle() {
		PhotonPlugin plugin = PhotonPlugin.getDefault();
		Message aMessage = plugin.getMessageFactory().newBasicOrder();
		aMessage.getHeader().setField(new MsgType(MsgType.ORDER_SINGLE));
		return aMessage;
	}

	public void showMessage(Message aMessage) {
		unbind();
		bind(aMessage, true);
		ticket.showMessage(aMessage);
		updateSendButtonState();
	}

	protected Message getMessage() {
		return targetMessage;
	}

	protected DataDictionary getDictionary() {
		return dictionary;
	}

	protected DataBindingContext getDataBindingContext() {
		return dataBindingContext;
	}

	protected Message getTargetMessage() {
		return targetMessage;
	}

	protected Realm getTargetRealm() {
		return targetRealm;
	}

	protected MarketDataFeedTracker getMarketDataTracker() {
		return marketDataTracker;
	}	
	
}
