package org.marketcetera.photon.views;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.map.IMapChangeListener;
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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.marketcetera.core.MSymbol;
import org.marketcetera.marketdata.ConjunctionMessageSelector;
import org.marketcetera.marketdata.MarketDataListener;
import org.marketcetera.marketdata.MessageTypeSelector;
import org.marketcetera.marketdata.SymbolMessageSelector;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.marketdata.MarketDataFeedService;
import org.marketcetera.photon.marketdata.MarketDataFeedTracker;
import org.marketcetera.photon.parser.ILexerFIXImage;
import org.marketcetera.photon.parser.OpenCloseImage;
import org.marketcetera.photon.parser.OrderCapacityImage;
import org.marketcetera.photon.parser.PriceImage;
import org.marketcetera.photon.parser.PutOrCallImage;
import org.marketcetera.photon.parser.SideImage;
import org.marketcetera.photon.parser.TimeInForceImage;
import org.marketcetera.photon.ui.validation.DataDictionaryValidator;
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
import quickfix.field.OrderCapacity;
import quickfix.field.OrderQty;
import quickfix.field.Price;
import quickfix.field.PutOrCall;
import quickfix.field.Side;
import quickfix.field.StrikePrice;
import quickfix.field.Symbol;
import quickfix.field.TimeInForce;

public class OptionOrderTicketController {
	IOptionOrderTicket ticket;

	private MarketDataFeedTracker marketDataTracker;

	private MSymbol listenedSymbol = null;

	private ConjunctionMessageSelector currentSubscription;

	private MarketDataListener marketDataListener;

	private Message targetMessage;

	private EnumStringConverterBuilder<Character> sideConverterBuilder;

	private EnumStringConverterBuilder<Character> tifConverterBuilder;

	private PriceConverterBuilder priceConverterBuilder;

	private EnumStringConverterBuilder<Character> orderCapacityConverterBuilder;

	private EnumStringConverterBuilder<Character> openCloseConverterBuilder;

	private EnumStringConverterBuilder<Integer> putOrCallConverterBuilder;

	private PriceConverterBuilder strikeConverterBuilder;

	private DataDictionary dictionary;

	private DataBindingContext dataBindingContext;

	// todo: Duplicated from StockOrderTicketController, added inits for option
	// specific ConverterBuilders
	public OptionOrderTicketController(IOptionOrderTicket ticket) {
		this.ticket = ticket;

		dictionary = FIXDataDictionaryManager.getCurrentFIXDataDictionary()
				.getDictionary();
		dataBindingContext = new DataBindingContext();
		marketDataTracker = new MarketDataFeedTracker(PhotonPlugin.getDefault()
				.getBundleContext());
		marketDataTracker.open();

		marketDataListener = new MarketDataListener() {

			public void onLevel2Quote(Message aQuote) {
				OptionOrderTicketController.this.onQuote(aQuote);
			}

			public void onQuote(Message aQuote) {
				OptionOrderTicketController.this.onQuote(aQuote);
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
		initOrderCapacityConverterBuilder();
		initOpenCloseConverterBuilder();
		initPutOrCallConverterBuilder();

		clear();
	}

	// todo: Duplicated from StockOrderTicketController
	public void dispose() {
		marketDataTracker.setMarketDataListener(null);
		marketDataTracker.close();
		dataBindingContext.dispose();
	}

	// todo: Duplicated from StockOrderTicketController
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

	// todo: Duplicated from StockOrderTicketController
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

	// todo: Duplicated from StockOrderTicketController
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

	// todo: Duplicated from StockOrderTicketController
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

	// todo: Duplicated from StockOrderTicketController
	public void handleCancel() {
		clear();
	}

	// todo: Duplicated from StockOrderTicketController
	private void clear() {
		unlisten();
		unbind();
		ticket.clear();
		bind(newNewOrderSingle());
	}

	// todo: Duplicated from StockOrderTicketController
	private void unbind() {
		IObservableList bindingList = dataBindingContext.getBindings();
		Object[] bindings = bindingList.toArray();
		for (Object bindingObj : bindings) {
			((Binding) (bindingObj)).dispose();
		}
	}

	// todo: Duplicated from StockOrderTicketController, added call to
	// bindOptionFields
	private void bind(Message message) {
		targetMessage = message;
		try {
			Realm realm = Realm.getDefault();
			dataBindingContext.bindValue(SWTObservables.observeText(ticket
					.getSideCCombo()), FIXObservables.observeValue(realm,
					message, Side.FIELD, dictionary), new UpdateValueStrategy()
					.setAfterGetValidator(
							sideConverterBuilder.newTargetAfterGetValidator())
					.setConverter(sideConverterBuilder.newToModelConverter()),
					new UpdateValueStrategy().setConverter(sideConverterBuilder
							.newToTargetConverter()));
			dataBindingContext.bindValue(SWTObservables.observeText(ticket
					.getQuantityText(), SWT.Modify), FIXObservables
					.observeValue(realm, message, OrderQty.FIELD, dictionary),
					new UpdateValueStrategy().setAfterGetValidator(
							new StringRequiredValidator()).setConverter(
							new StringToBigDecimalConverter()),
					new UpdateValueStrategy()
							.setConverter(new BigDecimalToStringConverter()));
			dataBindingContext
					.bindValue(
							SWTObservables.observeText(ticket.getSymbolText(),
									SWT.Modify),
							FIXObservables.observeValue(realm, message,
									Symbol.FIELD, dictionary),
							new UpdateValueStrategy()
									.setAfterGetValidator(new StringRequiredValidator()),
							new UpdateValueStrategy());
			dataBindingContext
					.bindValue(SWTObservables.observeText(
							ticket.getPriceText(), SWT.Modify), FIXObservables
							.observePriceValue(realm, message, Price.FIELD,
									dictionary),
							new UpdateValueStrategy().setAfterGetValidator(
									priceConverterBuilder
											.newTargetAfterGetValidator())
									.setConverter(
											priceConverterBuilder
													.newToModelConverter()),
							new UpdateValueStrategy().setAfterGetValidator(
									priceConverterBuilder
											.newModelAfterGetValidator())
									.setConverter(
											priceConverterBuilder
													.newToTargetConverter()));
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
					new IMapChangeListener() {
						public void handleMapChange(MapChangeEvent event) {
							if (!ticket.getErrorMessageLabel().isDisposed()) {
								ticket.clearErrors();
								for (Object binding : event.diff
										.getChangedKeys()) {
									IStatus status = ((IStatus) dataBindingContext
											.getValidationStatusMap().get(
													binding));
									Control aControl = (Control) ((ISWTObservable) ((Binding) binding)
											.getTarget()).getWidget();
									ticket
											.showErrorForControl(aControl,
													status.getSeverity(),
													status.getMessage());
									if (status.getSeverity() == IStatus.ERROR) {
										ticket.showErrorMessage(status
												.getMessage(), status
												.getSeverity());
									}
								}
							}
						}
					});

			bindOptionFields(realm, message);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private UpdateValueStrategy createToTargetUpdateValueStrategy(
			EnumStringConverterBuilder<?> converterBuilder) {
		UpdateValueStrategy updateValueStrategy = new UpdateValueStrategy();
		updateValueStrategy.setAfterGetValidator(converterBuilder
				.newModelAfterGetValidator());
		updateValueStrategy.setConverter(converterBuilder
				.newToTargetConverter());
		return updateValueStrategy;
	}

	private UpdateValueStrategy createToModelUpdateValueStrategy(
			EnumStringConverterBuilder<?> converterBuilder) {
		UpdateValueStrategy updateValueStrategy = new UpdateValueStrategy();
		updateValueStrategy.setAfterGetValidator(converterBuilder
				.newTargetAfterGetValidator());
		updateValueStrategy
				.setConverter(converterBuilder.newToModelConverter());
		return updateValueStrategy;
	}

	private void bindOptionFields(Realm realm, Message message) {
		// todo: ExpireDate.FIELD bindings to ticket.getExpireYearCCombo() and ticket.getExpireMonthCCombo()
		
		// StrikePrice
		dataBindingContext.bindValue(SWTObservables.observeText(ticket
				.getStrikeText(), SWT.Modify), FIXObservables.observeValue(
				realm, message, StrikePrice.FIELD, dictionary),
				createToModelUpdateValueStrategy(strikeConverterBuilder),
				createToTargetUpdateValueStrategy(strikeConverterBuilder));

		// PutOrCall
		dataBindingContext.bindValue(SWTObservables.observeText(ticket
				.getPutOrCallCCombo()), FIXObservables.observeValue(realm,
				message, PutOrCall.FIELD, dictionary),
				createToModelUpdateValueStrategy(putOrCallConverterBuilder),
				createToTargetUpdateValueStrategy(putOrCallConverterBuilder));

		// OrderCapacity
		dataBindingContext
				.bindValue(
						SWTObservables.observeText(ticket
								.getOrderCapacityCCombo()),
						FIXObservables.observeValue(realm, message,
								OrderCapacity.FIELD, dictionary),
						createToModelUpdateValueStrategy(orderCapacityConverterBuilder),
						createToTargetUpdateValueStrategy(orderCapacityConverterBuilder));

	}

	// todo: Duplicated from StockOrderTicketController
	private void initSideConverterBuilder() {
		sideConverterBuilder = new EnumStringConverterBuilder<Character>(
				Character.class);
		sideConverterBuilder.addMapping(Side.BUY, SideImage.BUY.getImage());
		sideConverterBuilder.addMapping(Side.SELL, SideImage.SELL.getImage());
		sideConverterBuilder.addMapping(Side.SELL_SHORT, SideImage.SELL_SHORT
				.getImage());
		sideConverterBuilder.addMapping(Side.SELL_SHORT_EXEMPT,
				SideImage.SELL_SHORT_EXEMPT.getImage());
	}

	// todo: Duplicated from StockOrderTicketController
	private void initTifConverterBuilder() {
		tifConverterBuilder = new EnumStringConverterBuilder<Character>(
				Character.class);

		tifConverterBuilder.addMapping(TimeInForce.DAY, TimeInForceImage.DAY
				.getImage());
		tifConverterBuilder.addMapping(TimeInForce.AT_THE_OPENING,
				TimeInForceImage.OPG.getImage());
		tifConverterBuilder.addMapping(TimeInForce.AT_THE_CLOSE,
				TimeInForceImage.CLO.getImage());
		tifConverterBuilder.addMapping(TimeInForce.FILL_OR_KILL,
				TimeInForceImage.FOK.getImage());
		tifConverterBuilder.addMapping(TimeInForce.GOOD_TILL_CANCEL,
				TimeInForceImage.GTC.getImage());
		tifConverterBuilder.addMapping(TimeInForce.IMMEDIATE_OR_CANCEL,
				TimeInForceImage.IOC.getImage());
	}

	private void initCharToImageConverterBuilder(
			EnumStringConverterBuilder<Character> converterBuilder,
			ILexerFIXImage[] lexerImages) {
		for (ILexerFIXImage lexerImage : lexerImages) {
			String image = lexerImage.getImage();
			char fixValue = lexerImage.getFIXCharValue();
			converterBuilder.addMapping(fixValue, image);
		}
	}

	private void initIntToImageConverterBuilder(
			EnumStringConverterBuilder<Integer> converterBuilder,
			ILexerFIXImage[] lexerImages) {
		for (ILexerFIXImage lexerImage : lexerImages) {
			String image = lexerImage.getImage();
			int fixValue = lexerImage.getFIXIntValue();
			converterBuilder.addMapping(fixValue, image);
		}
	}

	private void initOrderCapacityConverterBuilder() {
		orderCapacityConverterBuilder = new EnumStringConverterBuilder<Character>(
				Character.class);
		initCharToImageConverterBuilder(orderCapacityConverterBuilder,
				OrderCapacityImage.values());
	}

	private void initOpenCloseConverterBuilder() {
		openCloseConverterBuilder = new EnumStringConverterBuilder<Character>(
				Character.class);
		initCharToImageConverterBuilder(openCloseConverterBuilder,
				OpenCloseImage.values());
	}

	private void initPutOrCallConverterBuilder() {
		putOrCallConverterBuilder = new EnumStringConverterBuilder<Integer>(
				Integer.class);
		initIntToImageConverterBuilder(putOrCallConverterBuilder,
				PutOrCallImage.values());
	}

	// todo: Duplicated from StockOrderTicketController
	private void initPriceConverterBuilder() {
		priceConverterBuilder = new PriceConverterBuilder(dictionary);

		priceConverterBuilder.addMapping(OrdType.MARKET, PriceImage.MKT
				.getImage());
	}

	// todo: Duplicated from StockOrderTicketController
	private Message newNewOrderSingle() {
		PhotonPlugin plugin = PhotonPlugin.getDefault();
		Message aMessage = plugin.getMessageFactory().createNewMessage();
		aMessage.getHeader().setField(new MsgType(MsgType.ORDER_SINGLE));
		return aMessage;
	}

	// todo: Duplicated from StockOrderTicketController
	public void showMessage(Message aMessage) {
		unbind();
		bind(aMessage);
		ticket.showMessage(aMessage);
	}

	// todo: Duplicated from StockOrderTicketController
	public Message getMessage() {
		return targetMessage;
	}
}
