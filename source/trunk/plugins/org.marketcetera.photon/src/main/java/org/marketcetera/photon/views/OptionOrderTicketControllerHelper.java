package org.marketcetera.photon.views;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.marketcetera.core.MSymbol;
import org.marketcetera.core.MarketceteraException;
import org.marketcetera.marketdata.ISubscription;
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
import org.marketcetera.photon.ui.validation.DecimalRequiredValidator;
import org.marketcetera.photon.ui.validation.IToggledValidator;
import org.marketcetera.photon.ui.validation.StringRequiredValidator;
import org.marketcetera.photon.ui.validation.fix.BigDecimalToStringConverter;
import org.marketcetera.photon.ui.validation.fix.DateToStringCustomConverter;
import org.marketcetera.photon.ui.validation.fix.EnumStringConverterBuilder;
import org.marketcetera.photon.ui.validation.fix.FIXObservables;
import org.marketcetera.photon.ui.validation.fix.PriceConverterBuilder;
import org.marketcetera.photon.ui.validation.fix.StringToBigDecimalConverter;
import org.marketcetera.photon.ui.validation.fix.StringToDateCustomConverter;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.quickfix.FIXVersion;

import quickfix.DataDictionary;
import quickfix.FieldNotFound;
import quickfix.FieldType;
import quickfix.Message;
import quickfix.field.CFICode;
import quickfix.field.MaturityDate;
import quickfix.field.MaturityMonthYear;
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

	private BindingHelper bindingHelper;

	private OptionSeriesManager optionSeriesManager;

	public OptionOrderTicketControllerHelper(IOptionOrderTicket ticket, OptionSeriesManager seriesManager) {
		super(ticket);
		this.optionTicket = ticket;

		bindingHelper = new BindingHelper();
		bindSymbolToModelDirection = false;
		optionSeriesManager = seriesManager;
	}

	@Override
	protected void initBuilders() {
		super.initBuilders();
		initOrderCapacityConverterBuilder();
		initOpenCloseConverterBuilder();
		initPutOrCallConverterBuilder();
		
	}
	
	@Override
	protected void initListeners() {
		super.initListeners();
		getMarketDataTracker().setMarketDataListener(
				new MDVMarketDataListener());

		{
			final Text optionSymbolLabel = optionTicket.getOptionSymbolControl();
			Text symbolText = optionTicket.getSymbolText();
			symbolText.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					String symbolString = ((Text)e.getSource()).getText();
					optionSymbolLabel.setText(symbolString);
				}
			});
		}
	}
	
	@Override
	public void clear() {
		super.clear();
		optionSeriesManager.clear();
	}

	@Override
	protected Message newNewOrderSingle() {
		Message message = super.newNewOrderSingle();
		// These fields should match the ones from bindImpl() and the
		// ILexerImage
		if (isOrderCapacityAndOpenCloseAllowed()) {
			message.setField(new OrderCapacity(OrderCapacityImage.CUSTOMER
					.getFIXCharValue()));
			message.setField(new OpenClose(OpenCloseImage.OPEN
					.getFIXCharValue()));
		}
		return message;
	}

	@Override
	protected MarketDataListener createMarketDataListener() {
		MarketDataListener dataListener = new MDVMarketDataListener();
		return dataListener;
	}

	@Override
	protected void listenMarketDataAdditional(final String optionRootStr)
			throws MarketceteraException {
		UnderlyingSymbolInfoComposite symbolComposite = getUnderlyingSymbolInfoComposite();
		symbolComposite.addUnderlyingSymbolInfo(optionRootStr);
	}

	@Override
	protected void unlistenMarketDataAdditional() throws MarketceteraException {
		super.unlistenMarketDataAdditional();

		UnderlyingSymbolInfoComposite symbolComposite = getUnderlyingSymbolInfoComposite();
		symbolComposite.removeUnderlyingSymbol();
		OptionMessagesComposite messagesComposite = getOptionMessagesComposite();
		messagesComposite.unlistenAllMarketData(getMarketDataTracker());
	}

	private boolean isOrderCapacityAndOpenCloseAllowed() {
		return PhotonPlugin.getDefault().getFIXVersion().getVersionAsDouble() >= FIXVersion.FIX43.getVersionAsDouble();
	}

	@Override
	protected void bindImpl(Message message, boolean enableValidators) {
		super.bindImpl(message, enableValidators);

		Realm realm = getTargetRealm();
//		DataBindingContext dataBindingContext = getDataBindingContext();
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
			bindValue( 
					whichControl,
					SWTObservables.observeText(whichControl),
					FIXObservables.observeMonthDateValue(realm, message, MaturityMonthYear.FIELD,dictionary),
					new UpdateValueStrategy().setAfterGetValidator(validator).setConverter(new StringToDateCustomConverter(DateToStringCustomConverter.MONTH_FORMAT)),
					new UpdateValueStrategy().setConverter(new DateToStringCustomConverter(DateToStringCustomConverter.MONTH_FORMAT)));
			addControlStateListeners(whichControl, validator);
			if (!enableValidators)
				addControlRequiringUserInput(whichControl);
		}
		// ExpireDate Year
		{
			Control whichControl = optionTicket.getExpireYearCombo();
			IToggledValidator validator = new StringRequiredValidator();
			validator.setEnabled(enableValidators);
			bindValue( whichControl,
					SWTObservables.observeText(whichControl),
					FIXObservables.observeMonthDateValue(realm, message, MaturityMonthYear.FIELD,dictionary),
					new UpdateValueStrategy().setAfterGetValidator(validator).setConverter(new StringToDateCustomConverter(DateToStringCustomConverter.SHORT_YEAR_FORMAT, DateToStringCustomConverter.LONG_YEAR_FORMAT)),
					new UpdateValueStrategy().setConverter(new DateToStringCustomConverter(DateToStringCustomConverter.SHORT_YEAR_FORMAT)));
			addControlStateListeners(whichControl, validator);
			if (!enableValidators)
				addControlRequiringUserInput(whichControl);
		}

		// StrikePrice
		{
			Control whichControl = optionTicket.getStrikePriceControl();
			IToggledValidator targetAfterGetValidator = new DecimalRequiredValidator();
			targetAfterGetValidator.setEnabled(enableValidators);
			
			bindValue( whichControl,
					SWTObservables.observeText(whichControl),
					FIXObservables.observeValue(realm, message, StrikePrice.FIELD, dictionary),
					new UpdateValueStrategy().setAfterGetValidator(targetAfterGetValidator).setConverter(new StringToBigDecimalConverter()),
					new UpdateValueStrategy().setConverter(new BigDecimalToStringConverter())
			);
			addControlStateListeners(whichControl, targetAfterGetValidator);
			if (!enableValidators)
				addControlRequiringUserInput(whichControl);
		}
		// PutOrCall (OptionCFICode)
		{
            Control whichControl = optionTicket.getPutOrCallCombo();
			IToggledValidator targetAfterGetValidator = putOrCallConverterBuilder.newTargetAfterGetValidator();
			targetAfterGetValidator.setEnabled(enableValidators);
			IToggledValidator modelAfterGetValidator = putOrCallConverterBuilder.newModelAfterGetValidator();
			modelAfterGetValidator.setEnabled(enableValidators);
			
			bindValue(
					whichControl, 
					SWTObservables.observeText(whichControl),
					FIXObservables.observeValue(realm,
							message, PutOrCall.FIELD, dictionary, PutOrCall.class.getSimpleName(), FieldType.Int),
					new UpdateValueStrategy().setAfterGetValidator(targetAfterGetValidator).setConverter(putOrCallConverterBuilder.newToModelConverter()),
					new UpdateValueStrategy().setAfterGetValidator(modelAfterGetValidator).setConverter(putOrCallConverterBuilder.newToTargetConverter())
			);
			addControlStateListeners(whichControl, targetAfterGetValidator);
			addControlStateListeners(whichControl, modelAfterGetValidator);
			if (!enableValidators)
				addControlRequiringUserInput(whichControl);
		}
		// OrderCapacity
		if(isOrderCapacityAndOpenCloseAllowed())
		{
			Control whichControl = optionTicket.getOrderCapacityCombo();
			IToggledValidator targetAfterGetValidator = orderCapacityConverterBuilder.newTargetAfterGetValidator();
			targetAfterGetValidator.setEnabled(enableValidators);
			IToggledValidator modelAfterGetValidator = orderCapacityConverterBuilder.newModelAfterGetValidator();
			modelAfterGetValidator.setEnabled(enableValidators);

			// The FIX field may need to be updated., See
			// http://trac.marketcetera.org/trac.fcgi/ticket/185
			// If the field below is changed the defaults in the 
			// message created by newNewOrderSingle() must be updated. 
			final int orderCapacityFIXField = OrderCapacity.FIELD;
			// final int orderCapacityFIXField = CustomerOrFirm.FIELD;
			bindValue(
					whichControl,
					SWTObservables.observeText(whichControl),
					FIXObservables.observeValue(realm, message, orderCapacityFIXField, dictionary, OrderCapacity.class.getSimpleName(), FieldType.Char),
					new UpdateValueStrategy().setAfterGetValidator(targetAfterGetValidator).setConverter(orderCapacityConverterBuilder.newToModelConverter()),
					new UpdateValueStrategy().setAfterGetValidator(modelAfterGetValidator).setConverter(orderCapacityConverterBuilder.newToTargetConverter())
			);
			addControlStateListeners(whichControl, targetAfterGetValidator);
			addControlStateListeners(whichControl, modelAfterGetValidator);
//			if (!enableValidators)
//				addControlRequiringUserInput(whichControl);
		}
		// OpenClose
		if(isOrderCapacityAndOpenCloseAllowed())
		{
			Control whichControl = optionTicket.getOpenCloseCombo();
			IToggledValidator targetAfterGetValidator = openCloseConverterBuilder.newTargetAfterGetValidator();
			targetAfterGetValidator.setEnabled(enableValidators);
			IToggledValidator modelAfterGetValidator = openCloseConverterBuilder.newModelAfterGetValidator();
			modelAfterGetValidator.setEnabled(enableValidators);
			
			// If the field below is changed the defaults in the 
			// message created by newNewOrderSingle() must be updated.
			final int openCloseField = OpenClose.FIELD;
			bindValue(
					whichControl,
					SWTObservables.observeText(whichControl), 
					FIXObservables.observeValue(realm, message, openCloseField, dictionary),
					new UpdateValueStrategy().setAfterGetValidator(targetAfterGetValidator).setConverter(openCloseConverterBuilder.newToModelConverter()),
					new UpdateValueStrategy().setAfterGetValidator(modelAfterGetValidator).setConverter(openCloseConverterBuilder.newToTargetConverter())
			);
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
			bindValue(
					whichControl, 
					SWTObservables.observeText(whichControl, SWT.Modify),
					FIXObservables.observeValue(realm, message, Symbol.FIELD, dictionary),
					new UpdateValueStrategy().setAfterGetValidator(validator),
					new UpdateValueStrategy()
			);
			addControlStateListeners(whichControl, validator);
		}
		optionSeriesManager.updateOptionSymbolFromLocalCache();
	}

	// todo: Remove this method if it remains unused.
	/**
	 * If a field such as OrderCapacity is missing from the dictionary it will
	 * have a null FieldClass in the FIXObservableValue. 
	 * This is a workaround for http://trac.marketcetera.org/trac.fcgi/ticket/294
	 */
//	private IObservableValue repairFIXTypeMapping(
//			IObservableValue observableValue, Class<?> fieldClass,
//			FieldType fieldType, Realm realm, Message message, int fieldNumber,
//			DataDictionary dataDictionary) {
//		IObservableValue rval = observableValue;
//		if (fieldClass != null && observableValue != null
//				&& Field.class.isAssignableFrom(fieldClass)) {
//			if (FIXObservableValue.class.isAssignableFrom(observableValue
//					.getClass())) {
//				FIXObservableValue fixObservableValue = (FIXObservableValue) observableValue;
//				if (fixObservableValue.getFieldClass() == null) {
//					String fieldName = fieldClass.getSimpleName();
//					rval = new FIXObservableValue(realm, message, fieldNumber,
//							dataDictionary, fieldName, fieldType);
//				}
//			}
//		}
//		return rval;
//	}
	
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

	public void onDerivativeSecurityList(final Message message) {
		Display theDisplay = Display.getDefault();
		if (theDisplay.getThread() == Thread.currentThread()) {
			optionSeriesManager.onMessage(message);
		} else {
			theDisplay.asyncExec(new Runnable() {
			public void run() {
				optionSeriesManager.onMessage(message);
			}
			});
		}
	}

	private void underlyingSymbolOnQuote(Message message) {
		UnderlyingSymbolInfoComposite symbolComposite = getUnderlyingSymbolInfoComposite();
		if (symbolComposite != null && !symbolComposite.isDisposed()) {
			if (symbolComposite.matchUnderlyingSymbol(message)) {
				symbolComposite.onQuote(message);
			}
		}
	}

	private UnderlyingSymbolInfoComposite getUnderlyingSymbolInfoComposite() {
		OptionBookComposite bookComposite = ((OptionBookComposite) optionTicket
				.getBookComposite());
		if(bookComposite == null || bookComposite.isDisposed()) {
			return null;
		}
        return bookComposite.getUnderlyingSymbolInfoComposite();
	}

	private OptionMessagesComposite getOptionMessagesComposite() {
		OptionBookComposite bookComposite = ((OptionBookComposite) optionTicket
				.getBookComposite());
		if(bookComposite == null || bookComposite.isDisposed()) {
			return null;
		}
		return bookComposite.getOptionMessagesComposite();
	}

	public class MDVMarketDataListener extends MarketDataListener {
		public void onMessage(Message aMessage) {
			if (FIXMessageUtil.isDerivativeSecurityList(aMessage)){
				OptionOrderTicketControllerHelper.this.onDerivativeSecurityList(aMessage);
			} else {
				OptionOrderTicketControllerHelper.this.onQuote(aMessage);
			}
		}
	}

	public OptionSeriesManager getOptionSeriesManager() {
		return optionSeriesManager;
	}

}
