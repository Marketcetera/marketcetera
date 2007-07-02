package org.marketcetera.photon.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.marketcetera.core.MSymbol;
import org.marketcetera.core.MarketceteraException;
import org.marketcetera.marketdata.MarketDataListener;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.marketdata.IMarketDataListCallback;
import org.marketcetera.photon.marketdata.MarketDataFeedService;
import org.marketcetera.photon.marketdata.MarketDataFeedTracker;
import org.marketcetera.photon.marketdata.MarketDataUtils;
import org.marketcetera.photon.marketdata.OptionContractData;
import org.marketcetera.photon.marketdata.OptionMarketDataUtils;
import org.marketcetera.photon.parser.OpenCloseImage;
import org.marketcetera.photon.parser.OrderCapacityImage;
import org.marketcetera.photon.parser.PriceImage;
import org.marketcetera.photon.parser.PutOrCallImage;
import org.marketcetera.photon.ui.OptionBookComposite;
import org.marketcetera.photon.ui.ToggledListener;
import org.marketcetera.photon.ui.validation.IToggledValidator;
import org.marketcetera.photon.ui.validation.StringRequiredValidator;
import org.marketcetera.photon.ui.validation.fix.DateToStringCustomConverter;
import org.marketcetera.photon.ui.validation.fix.EnumStringConverterBuilder;
import org.marketcetera.photon.ui.validation.fix.FIXObservableValue;
import org.marketcetera.photon.ui.validation.fix.FIXObservables;
import org.marketcetera.photon.ui.validation.fix.PriceConverterBuilder;
import org.marketcetera.photon.ui.validation.fix.StringToDateCustomConverter;
import org.marketcetera.photon.views.OptionContractCacheEntry.OptionCodeUIValues;

import quickfix.DataDictionary;
import quickfix.Field;
import quickfix.FieldNotFound;
import quickfix.FieldType;
import quickfix.Message;
import quickfix.field.CFICode;
import quickfix.field.MaturityDate;
import quickfix.field.OpenClose;
import quickfix.field.OrdType;
import quickfix.field.OrderCapacity;
import quickfix.field.PutOrCall;
import quickfix.field.StrikePrice;
import quickfix.field.Symbol;
import quickfix.field.UnderlyingSymbol;

/**
 * @author michael.lossos@softwaregoodness.com
 */
public class OptionOrderTicketControllerHelper extends
		OrderTicketControllerHelper {
	private IOptionOrderTicket optionTicket;

	private EnumStringConverterBuilder<Character> orderCapacityConverterBuilder;

	private EnumStringConverterBuilder<Character> openCloseConverterBuilder;

	private EnumStringConverterBuilder<Integer> putOrCallConverterBuilder;

	private PriceConverterBuilder strikeConverterBuilder;

	private BindingHelper bindingHelper;

	/**
	 * Map from option root symbol to cache entry.
	 */
	private HashMap<MSymbol, OptionContractCacheEntry> optionContractCache = new HashMap<MSymbol, OptionContractCacheEntry>();

	private MSymbol lastOptionRoot;

	private MSymbol selectedSymbol;

	private List<ToggledListener> optionSymbolListeners;

	public OptionOrderTicketControllerHelper(IOptionOrderTicket ticket) {
		super(ticket);
		this.optionTicket = ticket;

		bindingHelper = new BindingHelper();
	}

	@Override
	protected void initBuilders() {
		super.initBuilders();
		initOrderCapacityConverterBuilder();
		initOpenCloseConverterBuilder();
		initPutOrCallConverterBuilder();
		initStrikeConverterBuilder();
	}

	@Override
	protected void initListeners() {
		super.initListeners();
		getMarketDataTracker().setMarketDataListener(
				new MDVMarketDataListener());

		optionSymbolListeners = new ArrayList<ToggledListener>();
		addOptionSpecifierModifyListener(optionTicket.getExpireYearCombo());
		addOptionSpecifierModifyListener(optionTicket.getExpireMonthCombo());
		addOptionSpecifierModifyListener(optionTicket.getStrikePriceControl());
		addOptionSpecifierModifyListener(optionTicket.getPutOrCallCombo());

		ToggledListener optionSymbolModifyListener = new ToggledListener() {
			@Override
			protected void handleEventWhenEnabled(Event event) {
				try {
					setOptionSymbolListenersEnabled(false);
					updateOptionContractSpecifiers();
				} finally {
					setOptionSymbolListenersEnabled(true);
				}
			}
		};
//		optionTicket.getOptionSymbolControl().addListener(SWT.Modify,
//				optionSymbolModifyListener);
	}

	@Override
	public void clear() {
		super.clear();
		lastOptionRoot = null;
	}

	/**
	 * Update the option contract specifiers (expiration etc.) based on the
	 * option contract symbol (e.g. MSQ+GE).
	 */
	private void updateOptionContractSpecifiers() {
		String optionContractSymbolStr = optionTicket.getOptionSymbolControl()
				.getText();
		if (optionContractSymbolStr == null
				|| optionContractSymbolStr.length() == 0) {
			return;
		}

		String optionRootStr = OptionMarketDataUtils
				.getOptionRootSymbol(optionContractSymbolStr);
		MSymbol optionRoot = new MSymbol(optionRootStr);
		if (optionContractCache.containsKey(optionRoot)) {
			OptionContractCacheEntry cacheEntry = optionContractCache
					.get(optionRoot);
			if (cacheEntry != null) {
				MSymbol optionContractSymbol = new MSymbol(
						optionContractSymbolStr);
				OptionCodeUIValues optionUIValues = cacheEntry
						.getOptionCodeUIValues(optionContractSymbol);
				if (optionUIValues != null) {
					setOptionSpecifiers(optionUIValues);
				}
			}
		}
	}

	private void setOptionSpecifiers(OptionCodeUIValues optionUIValues) {
		String expirationYear = optionUIValues.getExpirationYear();
		optionTicket.getExpireYearCombo().setText(expirationYear);
		String expirationMonth = optionUIValues.getExpirationMonth();
		optionTicket.getExpireMonthCombo().setText(expirationMonth);
		String strikePrice = optionUIValues.getStrikePrice();
		optionTicket.getStrikePriceControl().setText(strikePrice);
		boolean optionIsPut = optionUIValues.isPut();
		optionTicket.setPut(optionIsPut);
	}

	private void addOptionSpecifierModifyListener(Control targetControl) {
		ToggledListener modifyListener = new ToggledListener() {
			public void handleEventWhenEnabled(Event event) {
				try {
					setOptionSymbolListenersEnabled(false);
					attemptUpdateOptionContractSymbol();
				} finally {
					setOptionSymbolListenersEnabled(true);
				}
			}
		};
		optionSymbolListeners.add(modifyListener);
		targetControl.addListener(SWT.Modify, modifyListener);
	}

	private void setOptionSymbolListenersEnabled(boolean enabled) {
		for (ToggledListener listener : optionSymbolListeners) {
			listener.setEnabled(enabled);
		}
	}

	@Override
	protected MarketDataListener createMarketDataListener() {
		MarketDataListener dataListener = new MDVMarketDataListener();
		return dataListener;
	}

	@Override
	protected void listenMarketDataAdditional(final String optionRootStr)
			throws MarketceteraException {

		MarketDataFeedTracker marketDataTracker = getMarketDataTracker();
		MSymbol optionRoot = new MSymbol(optionRootStr);
		UnderlyingSymbolInfoComposite symbolComposite = getUnderlyingSymbolInfoComposite();
		symbolComposite.addUnderlyingSymbolInfo(optionRootStr);
		selectedSymbol = optionRoot;

		if (!optionContractCache.containsKey(optionRoot)) {
			requestOptionSecurityList(marketDataTracker
					.getMarketDataFeedService(), optionRoot);

		} else {
			conditionallyUpdateInputControls(optionRoot);
		}
	}

	@Override
	protected void unlistenMarketDataAdditional() throws MarketceteraException {
		super.unlistenMarketDataAdditional();

		UnderlyingSymbolInfoComposite symbolComposite = getUnderlyingSymbolInfoComposite();
		symbolComposite.removeUnderlyingSymbol();
		OptionMessagesComposite messagesComposite = getOptionMessagesComposite();
		messagesComposite.unlistenAllMarketData(getMarketDataTracker());
	}

	private void requestOptionSecurityList(MarketDataFeedService service,
			final MSymbol optionRoot) {

		IMarketDataListCallback callback = new IMarketDataListCallback() {
			public void onMarketDataFailure(MSymbol symbol) {
				// Restore the full expiration choices.
				updateComboChoicesFromDefaults();
			}

            public void onMessage(Message aMessage) {
                handleMarketDataList(new Message[] {aMessage}, optionRoot);
            }

            public void onMessages(Message[] derivativeSecurityList) {
                handleMarketDataList(derivativeSecurityList, optionRoot);
			}
		};

		Message query = OptionMarketDataUtils.newOptionRootQuery(optionRoot,
				false);
		MarketDataUtils.asyncMarketDataQuery(optionRoot, query, service
				.getMarketDataFeed(), callback);
	}

    /* package */ void handleMarketDataList(Message[] derivativeSecurityList) {
        handleMarketDataList(derivativeSecurityList, new MSymbol(optionTicket.getSymbolText().getText()));
    }
    /* package */ void handleMarketDataList(Message[] derivativeSecurityList, MSymbol optionRoot) {
        List<OptionContractData> optionContracts = new ArrayList<OptionContractData>();
        try {
            String baseSymbol = (optionRoot == null) ? null : optionRoot.getBaseSymbol();
            optionContracts = OptionMarketDataUtils
                .getOptionExpirationMarketData(baseSymbol, derivativeSecurityList);
        } catch (Exception anyException) {
            PhotonPlugin.getMainConsoleLogger().warn("Error getting market data - ", anyException);
            return;
        }
        if (optionContracts == null || optionContracts.isEmpty()) {
            updateComboChoicesFromDefaults();
        } else {
            OptionContractCacheEntry cacheEntry = new OptionContractCacheEntry(
                    optionContracts);
            optionContractCache.put(optionRoot, cacheEntry);

            conditionallyUpdateInputControls(optionRoot);
        }
    }

    private void conditionallyUpdateInputControls(MSymbol optionRoot) {
		if (optionRoot != null && (lastOptionRoot == null || !lastOptionRoot.equals(optionRoot))) {
			lastOptionRoot = optionRoot;
			OptionContractCacheEntry cacheEntry = optionContractCache
					.get(optionRoot);
			if (cacheEntry != null) {
				updateComboChoices(cacheEntry);
				updateOptionContractSymbol(cacheEntry);
			}
		}
	}

	/**
	 * Only update the option contract symbol (e.g. MSQ+GE) based on the option
	 * specifiers (expiration etc.) if there is market data available for the
	 * option. If not, clears the option contract symbol text.
	 */
	private void attemptUpdateOptionContractSymbol() {
		String symbolText = optionTicket.getSymbolText().getText();
		boolean attemptedUpdate = false;
		if (symbolText != null) {
			MSymbol optionRoot = new MSymbol(symbolText);
			OptionContractCacheEntry cacheEntry = optionContractCache
					.get(optionRoot);
			if (cacheEntry != null) {
				attemptedUpdate = true;
				updateOptionContractSymbol(cacheEntry);
			}
		}
		if (!attemptedUpdate) {
			clearOptionSymbolControl();
		}
	}

	/**
	 * Update the option contract (e.g. MSQ+GE) based on the option specifiers
	 * (expiration etc.)
	 */
	private boolean updateOptionContractSymbol(
			OptionContractCacheEntry cacheEntry) {
		String expirationYear = optionTicket.getExpireYearCombo().getText();
		String expirationMonth = optionTicket.getExpireMonthCombo().getText();
		String strikePrice = optionTicket.getStrikePriceControl().getText();
		boolean putWhenTrue = optionTicket.isPut();

		boolean textWasSet = false;
		OptionContractData optionContract = cacheEntry.getOptionContractData(
				expirationYear, expirationMonth, strikePrice, putWhenTrue);
		if (optionContract != null) {
			MSymbol optionContractSymbol = optionContract.getOptionSymbol();
			if (optionContractSymbol != null) {
				String fullSymbol = optionContractSymbol.getFullSymbol();
				optionTicket.getOptionSymbolControl().setText(fullSymbol);
				textWasSet = true;
				getOptionMessagesComposite().requestOptionSecurityList(
						getMarketDataTracker(), selectedSymbol, fullSymbol);
			}
		}
		if (!textWasSet) {
			clearOptionSymbolControl();
		}
		return textWasSet;
	}

	private void clearOptionSymbolControl() {
		optionTicket.getOptionSymbolControl().setText("");
	}

	private void updateComboChoices(OptionContractCacheEntry cacheEntry) {
		updateComboChoices(optionTicket.getExpireMonthCombo(), cacheEntry
				.getExpirationMonthsForUI());
		updateComboChoices(optionTicket.getExpireYearCombo(), cacheEntry
				.getExpirationYearsForUI());
		updateComboChoices(optionTicket.getStrikePriceControl(), cacheEntry
				.getStrikePricesForUI());
	}

	private void updateComboChoicesFromDefaults() {
		OptionDateHelper dateHelper = new OptionDateHelper();
		List<String> months = dateHelper.createDefaultMonths();
		updateComboChoices(optionTicket.getExpireMonthCombo(), months);
		List<String> years = dateHelper.createDefaultYears();
		updateComboChoices(optionTicket.getExpireYearCombo(), years);
		// todo: What should the defaults be for strike price?
		List<String> strikePrices = new ArrayList<String>();
		updateComboChoices(optionTicket.getStrikePriceControl(), strikePrices);
	}

	private void updateComboChoices(Combo combo, Collection<String> choices) {
		combo.removeAll();
		boolean first = true;
		for (String choice : choices) {
			if (choice != null) {
				combo.add(choice);
				if (first) {
					combo.setText(choice);
					first = false;
				}
			}
		}
		if (combo.isFocusControl()) {
			combo.setSelection(new Point(0, 3));
		}
	}

	@Override
	protected int getSymbolFIXField() {
		// Make the Symbol input control be the option root.
		return UnderlyingSymbol.FIELD;
	}

	@Override
	protected void bindImpl(Message message, boolean enableValidators) {
		super.bindImpl(message, enableValidators);

		Realm realm = getTargetRealm();
		DataBindingContext dataBindingContext = getDataBindingContext();
		DataDictionary dictionary = getDictionary();

		/**
		 * Note that the MaturityDate and StrikePrice in the order are not used
		 * by the OMS. They are used here to have a place for the data binding
		 * to store the data. The code part of the option contract symbol
		 * represents that data.
		 */

		final int swtEvent = SWT.Modify;
		// ExpireDate Month
		{
			Control whichControl = optionTicket.getExpireMonthCombo();
			IToggledValidator validator = new StringRequiredValidator();
			validator.setEnabled(enableValidators);
			dataBindingContext.bindValue(SWTObservables
					.observeText(whichControl), FIXObservables
					.observeMonthDateValue(realm, message, MaturityDate.FIELD,
							dictionary), new UpdateValueStrategy()
					.setAfterGetValidator(validator).setConverter(
							new StringToDateCustomConverter(
									DateToStringCustomConverter.MONTH_FORMAT)),
					new UpdateValueStrategy()
							.setConverter(new DateToStringCustomConverter(
									DateToStringCustomConverter.MONTH_FORMAT)));
			addControlStateListeners(whichControl, validator);
			if (!enableValidators)
				addControlRequiringUserInput(whichControl);
		}
		// ExpireDate Year
		{
			Control whichControl = optionTicket.getExpireYearCombo();
			IToggledValidator validator = new StringRequiredValidator();
			validator.setEnabled(enableValidators);
			dataBindingContext.bindValue(SWTObservables
					.observeText(whichControl), FIXObservables
					.observeMonthDateValue(realm, message, MaturityDate.FIELD,
							dictionary), new UpdateValueStrategy()
					.setAfterGetValidator(validator).setConverter(
							new StringToDateCustomConverter(
									DateToStringCustomConverter.YEAR_FORMAT)),
					new UpdateValueStrategy()
							.setConverter(new DateToStringCustomConverter(
									DateToStringCustomConverter.YEAR_FORMAT)));
			addControlStateListeners(whichControl, validator);
			if (!enableValidators)
				addControlRequiringUserInput(whichControl);
		}

		// StrikePrice
		{
			Control whichControl = optionTicket.getStrikePriceControl();
			IToggledValidator targetAfterGetValidator = strikeConverterBuilder
					.newTargetAfterGetValidator();
			targetAfterGetValidator.setEnabled(enableValidators);
			IToggledValidator modelAfterGetValidator = strikeConverterBuilder.newModelAfterGetValidator();
			modelAfterGetValidator.setEnabled(enableValidators);
			dataBindingContext.bindValue(SWTObservables
					.observeText(whichControl), FIXObservables.observeValue(
					realm, message, StrikePrice.FIELD, dictionary),
					bindingHelper.createToModelUpdateValueStrategy(
							strikeConverterBuilder, targetAfterGetValidator), bindingHelper
							.createToTargetUpdateValueStrategy(
									strikeConverterBuilder, 
									modelAfterGetValidator));
			addControlStateListeners(whichControl, targetAfterGetValidator);
			addControlStateListeners(whichControl, modelAfterGetValidator);
			if (!enableValidators)
				addControlRequiringUserInput(whichControl);
		}
		// PutOrCall (OptionCFICode)
		{
            Control whichControl = optionTicket.getPutOrCallCombo();
			IToggledValidator targetAfterGetValidator = putOrCallConverterBuilder
					.newTargetAfterGetValidator();
			targetAfterGetValidator.setEnabled(enableValidators);
			IToggledValidator modelAfterGetValidator = putOrCallConverterBuilder.newModelAfterGetValidator();
			modelAfterGetValidator.setEnabled(enableValidators);
			IObservableValue fixObservable = FIXObservables.observeValue(realm,
					message, PutOrCall.FIELD, dictionary, PutOrCall.class.getSimpleName(), FieldType.Int);
			dataBindingContext.bindValue(SWTObservables
					.observeText(whichControl), fixObservable, bindingHelper
					.createToModelUpdateValueStrategy(
							putOrCallConverterBuilder, targetAfterGetValidator),
					bindingHelper.createToTargetUpdateValueStrategy(
							putOrCallConverterBuilder, modelAfterGetValidator
							));
			addControlStateListeners(whichControl, targetAfterGetValidator);
			addControlStateListeners(whichControl, modelAfterGetValidator);
			if (!enableValidators)
				addControlRequiringUserInput(whichControl);
		}
		// OrderCapacity
		{
			Control whichControl = optionTicket.getOrderCapacityCombo();
			IToggledValidator targetAfterGetValidator = orderCapacityConverterBuilder
					.newTargetAfterGetValidator();
			targetAfterGetValidator.setEnabled(enableValidators);
			IToggledValidator modelAfterGetValidator = orderCapacityConverterBuilder.newModelAfterGetValidator();
			modelAfterGetValidator.setEnabled(enableValidators);
			// The FIX field may need to be updated., See
			// http://trac.marketcetera.org/trac.fcgi/ticket/185
			final int orderCapacityFIXField = OrderCapacity.FIELD;
			// final int orderCapacityFIXField = CustomerOrFirm.FIELD;
			IObservableValue observableValue = FIXObservables.observeValue(
					realm, message, orderCapacityFIXField, dictionary, OrderCapacity.class.getSimpleName(), FieldType.Char);
			dataBindingContext.bindValue(SWTObservables
					.observeText(whichControl), observableValue,
					bindingHelper.createToModelUpdateValueStrategy(
							orderCapacityConverterBuilder, targetAfterGetValidator),
					bindingHelper.createToTargetUpdateValueStrategy(
							orderCapacityConverterBuilder, 
							modelAfterGetValidator ));
			addControlStateListeners(whichControl, targetAfterGetValidator);
			addControlStateListeners(whichControl, modelAfterGetValidator);
//			if (!enableValidators)
//				addControlRequiringUserInput(whichControl);
		}
		// OpenClose
		{
			Control whichControl = optionTicket.getOpenCloseCombo();
			IToggledValidator targetAfterGetValidator = openCloseConverterBuilder
					.newTargetAfterGetValidator();
			targetAfterGetValidator.setEnabled(enableValidators);
			IToggledValidator modelAfterGetValidator = openCloseConverterBuilder.newModelAfterGetValidator();
			modelAfterGetValidator.setEnabled(enableValidators);
			dataBindingContext.bindValue(SWTObservables
					.observeText(whichControl), FIXObservables.observeValue(
					realm, message, OpenClose.FIELD, dictionary), bindingHelper
					.createToModelUpdateValueStrategy(
							openCloseConverterBuilder, targetAfterGetValidator),
					bindingHelper.createToTargetUpdateValueStrategy(
							openCloseConverterBuilder, 
							modelAfterGetValidator));
			addControlStateListeners(whichControl, targetAfterGetValidator);
			addControlStateListeners(whichControl, modelAfterGetValidator);
//			if (!enableValidators)
//				addControlRequiringUserInput(whichControl);
		}
		// OptionSymbol (the symbol for the actual option contract)
		/**
		 * Note that the OptionSymbol value depends on other contract specifiers
		 * (strike, Put/Call, expiration). OptionSymbol must be bound after the
		 * others so that if a symbol is present in the FIX message, it is not
		 * erased.
		 */
		{
			Control whichControl = optionTicket.getOptionSymbolControl();
			IToggledValidator validator = new StringRequiredValidator();
			validator.setEnabled(enableValidators);
			dataBindingContext.bindValue(SWTObservables.observeText(
					whichControl, swtEvent), FIXObservables.observeValue(realm,
					message, Symbol.FIELD, dictionary),
					new UpdateValueStrategy().setAfterGetValidator(validator),
					new UpdateValueStrategy());
			addControlStateListeners(whichControl, validator);
		}
	}

	// todo: Remove this method if it remains unused.
	/**
	 * If a field such as OrderCapacity is missing from the dictionary it will
	 * have a null FieldClass in the FIXObservableValue. 
	 * This is a workaround for http://trac.marketcetera.org/trac.fcgi/ticket/294
	 */
	private IObservableValue repairFIXTypeMapping(
			IObservableValue observableValue, Class<?> fieldClass,
			FieldType fieldType, Realm realm, Message message, int fieldNumber,
			DataDictionary dataDictionary) {
		IObservableValue rval = observableValue;
		if (fieldClass != null && observableValue != null
				&& Field.class.isAssignableFrom(fieldClass)) {
			if (FIXObservableValue.class.isAssignableFrom(observableValue
					.getClass())) {
				FIXObservableValue fixObservableValue = (FIXObservableValue) observableValue;
				if (fixObservableValue.getFieldClass() == null) {
					String fieldName = fieldClass.getSimpleName();
					rval = new FIXObservableValue(realm, message, fieldNumber,
							dataDictionary, fieldName, fieldType);
				}
			}
		}
		return rval;
	}
	
    public static final String CFI_CODE_PUT = "OPASPS";
    public static final String CFI_CODE_CALL = "OCASPS";

    @Override
    /** If we are using a version of FIX that has it, need to substitute
     * {@link PutOrCall} field with {@link CFICode} instead.
     */
    public void handleSend() {
        if(getDictionary().isField(CFICode.FIELD)) {
            Message theMsg = getTargetMessage();
            int putOrCall = 0;
            try {
                putOrCall = theMsg.getInt(PutOrCall.FIELD);
                CFICode cfiCode = (putOrCall == PutOrCall.PUT) ? new CFICode(CFI_CODE_PUT) : new CFICode(CFI_CODE_CALL);
                theMsg.setField(cfiCode);
                theMsg.removeField(PutOrCall.FIELD);
            } catch (FieldNotFound ignored) {
                //ignored
            }
        }
        super.handleSend();
    }

    public void initOrderCapacityConverterBuilder() {
		orderCapacityConverterBuilder = new EnumStringConverterBuilder<Character>(
				Character.class);
		bindingHelper.initCharToImageConverterBuilder(
				orderCapacityConverterBuilder, OrderCapacityImage.values());
	}

	private void initOpenCloseConverterBuilder() {
		openCloseConverterBuilder = new EnumStringConverterBuilder<Character>(
				Character.class);
		bindingHelper.initCharToImageConverterBuilder(
				openCloseConverterBuilder, OpenCloseImage.values());
	}

	private void initPutOrCallConverterBuilder() {
		putOrCallConverterBuilder = new EnumStringConverterBuilder<Integer>(
				Integer.class);
		bindingHelper.initIntToImageConverterBuilder(
				putOrCallConverterBuilder, PutOrCallImage.values());
	}

	private void initStrikeConverterBuilder() {
		strikeConverterBuilder = new PriceConverterBuilder(getDictionary());
		// todo: Is this mapping correct for strike price?
		strikeConverterBuilder.addMapping(OrdType.MARKET, PriceImage.MKT
				.getImage());
	}

	@Override
	public void onQuote(final Message message) {
		Display theDisplay = Display.getDefault();
		if (theDisplay.getThread() == Thread.currentThread()) {
			onQuoteHelper(message);
		} else {
			theDisplay.asyncExec(new Runnable() {
				public void run() {
					onQuoteHelper(message);
				}
			});
		}
	}

	private void onQuoteHelper(Message message) {
		underlyingSymbolOnQuote(message); // todo:message
		optionTicket.getBookComposite().onQuote(message);
	}

	private void underlyingSymbolOnQuote(Message message) {
		UnderlyingSymbolInfoComposite symbolComposite = getUnderlyingSymbolInfoComposite();
		if (symbolComposite.matchUnderlyingSymbol(message)) {
			symbolComposite.onQuote(message);
		}
	}

	private UnderlyingSymbolInfoComposite getUnderlyingSymbolInfoComposite() {
		OptionBookComposite bookComposite = ((OptionBookComposite) optionTicket
				.getBookComposite());
        return bookComposite.getUnderlyingSymbolInfoComposite();
	}

	private OptionMessagesComposite getOptionMessagesComposite() {
		OptionBookComposite bookComposite = ((OptionBookComposite) optionTicket
				.getBookComposite());
		return bookComposite.getOptionMessagesComposite();
	}

	public class MDVMarketDataListener extends MarketDataListener {
		public void onMessage(Message aQuote) {
			OptionOrderTicketControllerHelper.this.onQuote(aQuote);
		}
	}

}
