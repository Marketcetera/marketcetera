package org.marketcetera.photon.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.marketcetera.core.MSymbol;
import org.marketcetera.core.MarketceteraException;
import org.marketcetera.photon.marketdata.IMarketDataListCallback;
import org.marketcetera.photon.marketdata.MarketDataFeedService;
import org.marketcetera.photon.marketdata.MarketDataUtils;
import org.marketcetera.photon.marketdata.OptionContractData;
import org.marketcetera.photon.marketdata.OptionMarketDataUtils;
import org.marketcetera.photon.parser.OpenCloseImage;
import org.marketcetera.photon.parser.OrderCapacityImage;
import org.marketcetera.photon.parser.PriceImage;
import org.marketcetera.photon.parser.PutOrCallImage;
import org.marketcetera.photon.ui.validation.IToggledValidator;
import org.marketcetera.photon.ui.validation.StringRequiredValidator;
import org.marketcetera.photon.ui.validation.fix.DateToStringCustomConverter;
import org.marketcetera.photon.ui.validation.fix.EnumStringConverterBuilder;
import org.marketcetera.photon.ui.validation.fix.FIXObservables;
import org.marketcetera.photon.ui.validation.fix.PriceConverterBuilder;
import org.marketcetera.photon.ui.validation.fix.StringToDateCustomConverter;

import quickfix.DataDictionary;
import quickfix.Message;
import quickfix.field.CustomerOrFirm;
import quickfix.field.MaturityDate;
import quickfix.field.OpenClose;
import quickfix.field.OrdType;
import quickfix.field.PutOrCall;
import quickfix.field.StrikePrice;

public class OptionOrderTicketControllerHelper extends
		OrderTicketControllerHelper {
	private IOptionOrderTicket optionTicket;

	private EnumStringConverterBuilder<Integer> orderCapacityConverterBuilder;

	private EnumStringConverterBuilder<Character> openCloseConverterBuilder;

	private EnumStringConverterBuilder<Integer> putOrCallConverterBuilder;

	private PriceConverterBuilder strikeConverterBuilder;

	private BindingHelper bindingHelper;

	/**
	 * Map from option root symbol to cache entry.
	 */
	private HashMap<MSymbol, OptionContractCacheEntry> optionContractCache = new HashMap<MSymbol, OptionContractCacheEntry>();

	private MSymbol lastOptionRoot;

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

		addUpdateOptionSymbolModifyListener(optionTicket.getExpireYearCombo());
		addUpdateOptionSymbolModifyListener(optionTicket.getExpireMonthCombo());
		addUpdateOptionSymbolModifyListener(optionTicket
				.getStrikePriceControl());
		addUpdateOptionSymbolModifyListener(optionTicket.getPutOrCallCombo());
	}

	@Override
	public void clear() {
		super.clear();
		lastOptionRoot = null;
	}

	private void addUpdateOptionSymbolModifyListener(Control targetControl) {
		targetControl.addListener(SWT.Modify, new Listener() {
			public void handleEvent(Event event) {
				conditionallyUpdateOptionContractSymbol();
			}
		});
	}

	@Override
	protected void listenMarketDataAdditional(MarketDataFeedService service,
			final String optionRootStr) throws MarketceteraException {

		MSymbol optionRoot = new MSymbol(optionRootStr);
		if (!optionContractCache.containsKey(optionRoot)) {
			requestOptionSecurityList(service, optionRoot);
		} else {
			conditionallyUpdateInputControls(optionRoot);
		}
	}

	private void requestOptionSecurityList(MarketDataFeedService service,
			final MSymbol optionRoot) {
		IMarketDataListCallback callback = new IMarketDataListCallback() {
			public void onMarketDataFailure(MSymbol symbol) {
				// Restore the full expiration choices.
				updateComboChoicesFromDefaults();
			}

			public void onMarketDataListAvailable(
					List<Message> derivativeSecurityList) {
				List<OptionContractData> optionContracts = OptionMarketDataUtils
						.getOptionExpirationMarketData(optionRoot
								.getBaseSymbol(), derivativeSecurityList);
				if (optionContracts == null || optionContracts.isEmpty()) {
					updateComboChoicesFromDefaults();
				} else {
					OptionContractCacheEntry cacheEntry = new OptionContractCacheEntry(
							optionContracts);
					optionContractCache.put(optionRoot, cacheEntry);

					conditionallyUpdateInputControls(optionRoot);
				}
			}
		};

		Message query = OptionMarketDataUtils.newOptionRootQuery(optionRoot,
				false);
		MarketDataUtils.asyncMarketDataQuery(optionRoot, query, service
				.getMarketDataFeed(), callback);
	}

	private void conditionallyUpdateInputControls(MSymbol optionRoot) {
		if (lastOptionRoot == null || !lastOptionRoot.equals(optionRoot)) {
			lastOptionRoot = optionRoot;
			OptionContractCacheEntry cacheEntry = optionContractCache
					.get(optionRoot);
			if (cacheEntry != null) {
				updateComboChoices(cacheEntry);
				updateOptionContractSymbol(cacheEntry);
			}
		}
	}

	private void conditionallyUpdateOptionContractSymbol() {
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
			IToggledValidator validator = strikeConverterBuilder
					.newTargetAfterGetValidator();
			validator.setEnabled(enableValidators);
			dataBindingContext.bindValue(SWTObservables
					.observeText(whichControl), FIXObservables.observeValue(
					realm, message, StrikePrice.FIELD, dictionary),
					bindingHelper.createToModelUpdateValueStrategy(
							strikeConverterBuilder, validator), bindingHelper
							.createToTargetUpdateValueStrategy(
									strikeConverterBuilder, validator));
			addControlStateListeners(whichControl, validator);
			if (!enableValidators)
				addControlRequiringUserInput(whichControl);
		}
		// PutOrCall
		{
			Control whichControl = optionTicket.getPutOrCallCombo();
			IToggledValidator validator = putOrCallConverterBuilder
					.newTargetAfterGetValidator();
			validator.setEnabled(enableValidators);
			dataBindingContext.bindValue(SWTObservables
					.observeText(whichControl), FIXObservables.observeValue(
					realm, message, PutOrCall.FIELD, dictionary), bindingHelper
					.createToModelUpdateValueStrategy(
							putOrCallConverterBuilder, validator),
					bindingHelper.createToTargetUpdateValueStrategy(
							putOrCallConverterBuilder, validator));
			addControlStateListeners(whichControl, validator);
			if (!enableValidators)
				addControlRequiringUserInput(whichControl);
		}
		// OrderCapacity
		{
			Control whichControl = optionTicket.getOrderCapacityCombo();
			IToggledValidator validator = orderCapacityConverterBuilder
					.newTargetAfterGetValidator();
			validator.setEnabled(enableValidators);
			// The FIX field may need to be updated., See
			// http://trac.marketcetera.org/trac.fcgi/ticket/185
			dataBindingContext.bindValue(SWTObservables
					.observeText(whichControl), FIXObservables.observeValue(
					realm, message, CustomerOrFirm.FIELD, dictionary),
					bindingHelper.createToModelUpdateValueStrategy(
							orderCapacityConverterBuilder, validator),
					bindingHelper.createToTargetUpdateValueStrategy(
							orderCapacityConverterBuilder, validator));
			addControlStateListeners(whichControl, validator);
			if (!enableValidators)
				addControlRequiringUserInput(whichControl);
		}
		// OpenClose
		{
			Control whichControl = optionTicket.getOpenCloseCombo();
			IToggledValidator validator = openCloseConverterBuilder
					.newTargetAfterGetValidator();
			validator.setEnabled(enableValidators);
			dataBindingContext.bindValue(SWTObservables
					.observeText(whichControl), FIXObservables.observeValue(
					realm, message, OpenClose.FIELD, dictionary), bindingHelper
					.createToModelUpdateValueStrategy(
							openCloseConverterBuilder, validator),
					bindingHelper.createToTargetUpdateValueStrategy(
							openCloseConverterBuilder, validator));
			addControlStateListeners(whichControl, validator);
			if (!enableValidators)
				addControlRequiringUserInput(whichControl);
		}
	}

	public void initOrderCapacityConverterBuilder() {
		orderCapacityConverterBuilder = new EnumStringConverterBuilder<Integer>(
				Integer.class);
		bindingHelper.initIntToImageConverterBuilder(
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
		bindingHelper.initIntToImageConverterBuilder(putOrCallConverterBuilder,
				PutOrCallImage.values());
	}

	private void initStrikeConverterBuilder() {
		strikeConverterBuilder = new PriceConverterBuilder(getDictionary());
		// todo: Is this mapping correct for strike price?
		strikeConverterBuilder.addMapping(OrdType.MARKET, PriceImage.MKT
				.getImage());
	}
}
