package org.marketcetera.photon.views;

import java.util.HashMap;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.map.IMapChangeListener;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.map.MapChangeEvent;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.databinding.swt.ISWTObservable;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.marketcetera.core.MSymbol;
import org.marketcetera.marketdata.ConjunctionMessageSelector;
import org.marketcetera.marketdata.MarketDataListener;
import org.marketcetera.marketdata.MessageTypeSelector;
import org.marketcetera.marketdata.SymbolMessageSelector;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.marketdata.MarketDataFeedService;
import org.marketcetera.photon.marketdata.MarketDataFeedTracker;
import org.marketcetera.photon.parser.PriceImage;
import org.marketcetera.photon.parser.SideImage;
import org.marketcetera.photon.parser.TimeInForceImage;
import org.marketcetera.photon.ui.validation.DataDictionaryValidator;
import org.marketcetera.photon.ui.validation.IToggledValidator;
import org.marketcetera.photon.ui.validation.StringRequiredValidator;
import org.marketcetera.photon.ui.validation.fix.BigDecimalToStringConverter;
import org.marketcetera.photon.ui.validation.fix.EnumStringConverterBuilder;
import org.marketcetera.photon.ui.validation.fix.FIXObservables;
import org.marketcetera.photon.ui.validation.fix.PriceConverterBuilder;
import org.marketcetera.photon.ui.validation.fix.StringToBigDecimalConverter;
import org.marketcetera.quickfix.FIXDataDictionaryManager;

import quickfix.DataDictionary;
import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.Account;
import quickfix.field.MsgType;
import quickfix.field.OrdType;
import quickfix.field.OrderQty;
import quickfix.field.Price;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.field.TimeInForce;

public class OrderTicketControllerHelper {
	private IOrderTicket ticket;

	private MarketDataFeedTracker marketDataTracker;

	private MSymbol listenedSymbol = null;

	private ConjunctionMessageSelector currentSubscription;

	private MarketDataListener marketDataListener;

	private Message targetMessage;

	private EnumStringConverterBuilder<Character> sideConverterBuilder;

	private EnumStringConverterBuilder<Character> tifConverterBuilder;

	private PriceConverterBuilder priceConverterBuilder;

	private DataDictionary dictionary;

	private DataBindingContext dataBindingContext;

	private IFIXControllerBinding fixControllerBinding;

	private BindingHelper bindingHelper;
	
	private Color colorRed;
	
	public OrderTicketControllerHelper(IOrderTicket ticket) {
		this(ticket, null);
	}

	public OrderTicketControllerHelper(IOrderTicket ticket,
			IFIXControllerBinding fixControllerBinding) {
		this.ticket = ticket;
		this.fixControllerBinding = fixControllerBinding;

		init();
	}

	private void init() {

		bindingHelper = new BindingHelper();

		colorRed = Display.getCurrent().getSystemColor( SWT.COLOR_RED);
		
		dictionary = FIXDataDictionaryManager.getCurrentFIXDataDictionary()
				.getDictionary();
		dataBindingContext = new DataBindingContext();
		marketDataTracker = new MarketDataFeedTracker(PhotonPlugin.getDefault()
				.getBundleContext());
		marketDataTracker.open();

		marketDataListener = new MarketDataListener() {

			public void onLevel2Quote(Message aQuote) {
				OrderTicketControllerHelper.this.onQuote(aQuote);
			}

			public void onQuote(Message aQuote) {
				OrderTicketControllerHelper.this.onQuote(aQuote);
			}

			public void onTrade(Message aTrade) {
			}

		};
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

		ticket.getSymbolText().addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				Text symbolText = (Text) e.widget;
				if (!symbolText.isFocusControl()) {
					String symbolTextString = symbolText.getText();
					listenMarketData(symbolTextString);
				}
			}
		});

		ticket.getCancelButton().addMouseListener(new MouseAdapter() {
			public void mouseUp(MouseEvent e) {
				handleCancel();
			}
		});
		ticket.getSendButton().addMouseListener(new MouseAdapter() {
			public void mouseUp(MouseEvent e) {
				handleSend();
			}
		});

		initSideConverterBuilder();
		initTifConverterBuilder();
		initPriceConverterBuilder();

		// To force the initial state to appear the same as the Canceled state,
		// bind first, then clear. The IMapChangeListener is notified when the
		// controls are unbound.
		bind(newNewOrderSingle());
		clear();
	}

	public void dispose() {
		marketDataTracker.setMarketDataListener(null);
		marketDataTracker.close();
		dataBindingContext.dispose();
		colorRed.dispose();
	}

	protected void listenMarketData(String symbol) {
		unlisten();
		if (symbol != null && !"".equals(symbol)) {
			MSymbol newListenedSymbol = new MSymbol(symbol);
			MarketDataFeedService service = marketDataTracker
					.getMarketDataFeedService();

			if (service != null && !newListenedSymbol.equals(listenedSymbol)) {
				if (listenedSymbol != null) {
					unlisten();
				}
				ConjunctionMessageSelector subscription = new ConjunctionMessageSelector(
						new SymbolMessageSelector(newListenedSymbol),
						new MessageTypeSelector(false, false, true));
				service.subscribe(subscription);
				listenedSymbol = newListenedSymbol;
				currentSubscription = subscription;
			}
		}
	}

	protected void unlisten() {
		MarketDataFeedService service = marketDataTracker
				.getMarketDataFeedService();

		if (service != null) {
			if (currentSubscription != null) {
				service.unsubscribe(currentSubscription);
				listenedSymbol = null;
				currentSubscription = null;
			}
		}
		ticket.getBookComposite().setInput(null);
	}

	public void onQuote(Message message) {
		try {
			if (listenedSymbol != null) {
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

	public void clear() {
		unlisten();
		unbind();
		ticket.clear();
		bind(newNewOrderSingle());
	}

	private void unbind() {
		IObservableList bindingList = dataBindingContext.getBindings();
		Object[] bindings = bindingList.toArray();
		for (Object bindingObj : bindings) {
			((Binding) (bindingObj)).dispose();
		}
	}

	private void bind(Message message) {
		targetMessage = message;
		try {
			Realm realm = Realm.getDefault();
			// todo: Refactor to using BindingHelper for UpdateValueStrategy
			// creation.
			{
				IToggledValidator validator = (IToggledValidator) sideConverterBuilder
						.newTargetAfterGetValidator();
				validator.setEnabled(false);
				dataBindingContext.bindValue(SWTObservables.observeText(ticket
						.getSideCCombo()), FIXObservables.observeValue(realm,
						message, Side.FIELD, dictionary),
						new UpdateValueStrategy().setAfterGetValidator(
								validator).setConverter(
								sideConverterBuilder.newToModelConverter()),
						new UpdateValueStrategy()
								.setConverter(sideConverterBuilder
										.newToTargetConverter()));
				addInitialStateFocusListener(ticket.getSideCCombo(), validator);
			}
			{
				IToggledValidator validator = new StringRequiredValidator();
				validator.setEnabled(false);
				dataBindingContext
						.bindValue(
								SWTObservables.observeText(ticket
										.getQuantityText(), SWT.Modify),
								FIXObservables.observeValue(realm, message,
										OrderQty.FIELD, dictionary),
								new UpdateValueStrategy().setAfterGetValidator(
										validator).setConverter(
										new StringToBigDecimalConverter()),
								new UpdateValueStrategy()
										.setConverter(new BigDecimalToStringConverter()));
				addInitialStateFocusListener(ticket.getQuantityText(),
						validator);
			}
			dataBindingContext
					.bindValue(
							SWTObservables.observeText(ticket.getSymbolText(),
									SWT.Modify),
							FIXObservables.observeValue(realm, message,
									Symbol.FIELD, dictionary),
							new UpdateValueStrategy()
									.setAfterGetValidator(new StringRequiredValidator()),
							new UpdateValueStrategy());
			{
				IToggledValidator validator = (IToggledValidator) priceConverterBuilder
						.newTargetAfterGetValidator();
				validator.setEnabled(false);
				dataBindingContext.bindValue(SWTObservables.observeText(ticket
						.getPriceText(), SWT.Modify), FIXObservables
						.observePriceValue(realm, message, Price.FIELD,
								dictionary), new UpdateValueStrategy()
						.setAfterGetValidator(validator).setConverter(
								priceConverterBuilder.newToModelConverter()),
						new UpdateValueStrategy().setAfterGetValidator(
								priceConverterBuilder
										.newModelAfterGetValidator())
								.setConverter(
										priceConverterBuilder
												.newToTargetConverter()));
				addInitialStateFocusListener(ticket.getPriceText(), validator);
			}
			dataBindingContext
					.bindValue(
							SWTObservables.observeText(ticket.getTifCCombo()),
							FIXObservables.observeValue(realm, message,
									TimeInForce.FIELD, dictionary),
							new UpdateValueStrategy()
									.setAfterGetValidator(
											tifConverterBuilder
													.newTargetAfterGetValidator())
									.setAfterConvertValidator(
											new DataDictionaryValidator(
													dictionary,
													TimeInForce.FIELD,
													"Not a valid value for TimeInForce",
													PhotonPlugin.ID))
									.setConverter(
											tifConverterBuilder
													.newToModelConverter()),
							new UpdateValueStrategy()
									.setConverter(tifConverterBuilder
											.newToTargetConverter()));
			dataBindingContext.bindValue(SWTObservables.observeText(ticket
					.getAccountText(), SWT.Modify), FIXObservables
					.observeValue(realm, message, Account.FIELD, dictionary),
					new UpdateValueStrategy(), new UpdateValueStrategy());

			dataBindingContext.getValidationStatusMap().addMapChangeListener(
					createMapChangeListener());

			if (fixControllerBinding != null) {
				fixControllerBinding.bind(realm, dataBindingContext,
						dictionary, message);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void addInitialStateFocusListener(Control control,
			final IToggledValidator validator) {
		control.addFocusListener(new FocusAdapter() {
			private boolean initialState = true;

			@Override
			public void focusGained(FocusEvent e) {
				if (initialState) {
					initialState = false;
					validator.setEnabled(true);
				}
			}

		});
	}

	private IMapChangeListener createMapChangeListener() {
		return new IMapChangeListener() {
			private HashMap<Control, IStatus> inputControlErrorStatus = new HashMap<Control, IStatus>();

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
						aControl.setBackground( colorRed );
					} else {
						aControl.setBackground( null );
						if (inputControlErrorStatus.containsKey(aControl)) {
							inputControlErrorStatus.remove(aControl);
						}
					}
				}
				if (inputControlErrorStatus.isEmpty()) {
					ticket.clearErrors();
				}
			}
		};
	}

	private void initSideConverterBuilder() {
		sideConverterBuilder = new EnumStringConverterBuilder<Character>(
				Character.class);

		bindingHelper.initCharToImageConverterBuilder(sideConverterBuilder,
				SideImage.values());
	}

	private void initTifConverterBuilder() {
		tifConverterBuilder = new EnumStringConverterBuilder<Character>(
				Character.class);

		bindingHelper.initCharToImageConverterBuilder(tifConverterBuilder,
				TimeInForceImage.values());
	}

	private void initPriceConverterBuilder() {
		priceConverterBuilder = new PriceConverterBuilder(dictionary);

		priceConverterBuilder.addMapping(OrdType.MARKET, PriceImage.MKT
				.getImage());
	}

	private Message newNewOrderSingle() {
		PhotonPlugin plugin = PhotonPlugin.getDefault();
		Message aMessage = plugin.getMessageFactory().createNewMessage();
		aMessage.getHeader().setField(new MsgType(MsgType.ORDER_SINGLE));
		return aMessage;
	}

	public void showMessage(Message aMessage) {
		unbind();
		bind(aMessage);
		ticket.showMessage(aMessage);
	}

	public Message getMessage() {
		return targetMessage;
	}

}
