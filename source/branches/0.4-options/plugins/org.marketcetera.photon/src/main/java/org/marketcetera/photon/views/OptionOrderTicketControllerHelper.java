package org.marketcetera.photon.views;

import java.util.HashMap;
import java.util.List;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.marketcetera.core.MSymbol;
import org.marketcetera.core.MarketceteraException;
import org.marketcetera.photon.marketdata.IMarketDataListCallback;
import org.marketcetera.photon.marketdata.MarketDataFeedService;
import org.marketcetera.photon.marketdata.OptionMarketDataUtils;
import org.marketcetera.photon.marketdata.OptionContractData;
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
import quickfix.field.ExpireDate;
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

	private HashMap<String, Boolean> expirationCache = new HashMap<String, Boolean>();
	
	private String lastUnderlierSymbolStr;

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
	protected void listenMarketDataAdditional(MarketDataFeedService service,
			final String symbol) throws MarketceteraException {

		String underlierSymbol = OptionMarketDataUtils.getUnderlyingSymbol(symbol);
		if (!expirationCache.containsKey(underlierSymbol)) {
			requestOptionSecurityList(service, underlierSymbol);
		}
		

	}

	private void requestOptionSecurityList(MarketDataFeedService service,
			final String underlyingSymbolStr) {
		final MSymbol underlyingSymbol = new MSymbol(underlyingSymbolStr);
		IMarketDataListCallback callback = new IMarketDataListCallback() {
			public void onMarketDataFailure(MSymbol symbol) {
				// Leave the current expiration choices present.
			}

			public void onMarketDataListAvailable(
					List<Message> derivativeSecurityList) {
				expirationCache.put(underlyingSymbolStr, Boolean.TRUE);

				List<OptionContractData> optionExpirations = OptionMarketDataUtils
						.getOptionExpirationMarketData(underlyingSymbol,
								derivativeSecurityList);
				for (OptionContractData optionMd : optionExpirations) {
					System.out.println("" + optionMd.getUnderlyingSymbol()
							+ ", " + optionMd.getOptionSymbol() + ", "
							+ optionMd.getStrikePrice() + ", " 
							+ optionMd.getExpirationYear() + "-"
							+ optionMd.getExpirationMonth());
				}
				// todo: Populate combo choices from security list
				
				if(lastUnderlierSymbolStr == null || !lastUnderlierSymbolStr.equals(underlyingSymbolStr)) {
					lastUnderlierSymbolStr = underlyingSymbolStr;
					updateComboChoices(underlyingSymbolStr);
				}
			}
		};

		OptionMarketDataUtils.asyncOptionSecurityList(underlyingSymbol, service
				.getMarketDataFeed(), callback, true);
	}
	
	private void updateComboChoices(String underlyingSymbolStr) {
		// expirationCache.get(underlyingSymbolStr);
		// Combo expireMonthCombo = optionTicket.getExpireMonthCombo();
		// expireMonthCombo.removeAll();
	}

	@Override
	protected void bindImpl(Message message, boolean enableValidators) {
		super.bindImpl(message, enableValidators);

		Realm realm = getTargetRealm();
		DataBindingContext dataBindingContext = getDataBindingContext();
		DataDictionary dictionary = getDictionary();

		// todo: Handle the Days part of the date.

		// ExpireDate Month
		{
			Control whichControl = optionTicket.getExpireMonthCombo();
			IToggledValidator validator = new StringRequiredValidator();
			validator.setEnabled(enableValidators);
			dataBindingContext.bindValue(SWTObservables
					.observeText(whichControl), FIXObservables
					.observeMonthDateValue(realm, message, ExpireDate.FIELD,
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
					.observeMonthDateValue(realm, message, ExpireDate.FIELD,
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

		final int swtEvent = SWT.Modify;
		// StrikePrice
		{
			Control whichControl = optionTicket.getStrikeText();
			IToggledValidator validator = strikeConverterBuilder
					.newTargetAfterGetValidator();
			validator.setEnabled(enableValidators);
			dataBindingContext.bindValue(SWTObservables.observeText(
					whichControl, swtEvent), FIXObservables.observeValue(realm,
					message, StrikePrice.FIELD, dictionary), bindingHelper
					.createToModelUpdateValueStrategy(strikeConverterBuilder,
							validator), bindingHelper
					.createToTargetUpdateValueStrategy(strikeConverterBuilder,
							validator));
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
