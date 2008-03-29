package org.marketcetera.photon.views;

import java.util.List;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.UpdateListStrategy;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.value.AbstractObservableValue;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.marketcetera.photon.IFieldIdentifier;
import org.marketcetera.photon.IImageKeys;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.marketdata.OptionInfoComponent;
import org.marketcetera.photon.parser.OpenCloseImage;
import org.marketcetera.photon.parser.OrderCapacityImage;
import org.marketcetera.photon.parser.PutOrCallImage;
import org.marketcetera.photon.parser.SideImage;
import org.marketcetera.photon.parser.TimeInForceImage;
import org.marketcetera.photon.ui.OptionMessageListTableFormat;
import org.marketcetera.photon.ui.databinding.ErrorDecorationObservable;
import org.marketcetera.photon.ui.databinding.HasValueConverter;
import org.marketcetera.photon.ui.databinding.IsNewOrderMessageConverter;
import org.marketcetera.photon.ui.databinding.LabelBooleanImageObservableValue;
import org.marketcetera.photon.ui.databinding.RetainTextObservable;
import org.marketcetera.photon.ui.validation.IgnoreFirstNullValidator;
import org.marketcetera.photon.ui.validation.ObservableListValidator;
import org.marketcetera.photon.ui.validation.StringRequiredValidator;
import org.marketcetera.photon.ui.validation.fix.BigDecimalToStringConverter;
import org.marketcetera.photon.ui.validation.fix.DateToStringCustomConverter;
import org.marketcetera.photon.ui.validation.fix.EnumStringConverterBuilder;
import org.marketcetera.photon.ui.validation.fix.FIXObservables;
import org.marketcetera.photon.ui.validation.fix.StringToBigDecimalConverter;
import org.marketcetera.photon.ui.validation.fix.StringToDateCustomConverter;
import org.marketcetera.quickfix.FIXMessageUtil;

import quickfix.FieldNotFound;
import quickfix.FieldType;
import quickfix.Message;
import quickfix.field.MDEntryPx;
import quickfix.field.MDEntrySize;
import quickfix.field.MDEntryType;
import quickfix.field.MaturityMonthYear;
import quickfix.field.NoMDEntries;
import quickfix.field.OpenClose;
import quickfix.field.OrderCapacity;
import quickfix.field.PutOrCall;
import quickfix.field.StrikePrice;
import quickfix.field.Symbol;

/**
 * This class implements the view that provides the end user
 * the ability to type in--and graphically interact with--stock option orders.
 * 
 * Additionally this class manages the stock and option market data that can be displayed
 * along with the order ticket itself.
 * 
 * @author gmiller
 *
 */
public class OptionOrderTicketView extends OrderTicketView {

	private final EnumStringConverterBuilder<Character> orderCapacityConverterBuilder;

	private final EnumStringConverterBuilder<Character> openCloseConverterBuilder;

	private final EnumStringConverterBuilder<Integer> putOrCallConverterBuilder;

	private final Image upImage;

	private final Image downImage;

	private static final String NEW_OPTION_ORDER = "New Option Order";

	private static final String REPLACE_OPTION_ORDER = "Replace Option Order";
	
	public static String ID = "org.marketcetera.photon.views.OptionOrderTicketView";


	/**
	 * Create a new {@link OptionOrderTicketView}, initializing the builders
	 * for databinding and getting the uptick and downtick images out of
	 * the plugin.
	 */
	public OptionOrderTicketView() {
		orderCapacityConverterBuilder = createOrderCapacityConverterBuilder();
		openCloseConverterBuilder = createOpenCloseConverterBuilder();
		putOrCallConverterBuilder = createPutOrCallConverterBuilder();

		upImage = PhotonPlugin.getImageDescriptor(IImageKeys.ARROW_UP).createImage();
		downImage = PhotonPlugin.getImageDescriptor(IImageKeys.ARROW_DOWN).createImage();
	}
	
	@Override
	protected String getXSWTResourceName() {
		return "/option_order_ticket.xswt";
	}


	/**
	 * After invoking the superclass implementation, this method adds
	 * error decoration, initializes combo choices, and updates the sizes
	 * of a number of additional UI controls related to options orders.
	 * 
	 * Finally, this method sets up the {@link LabelProvider} and {@link IContentProvider}
	 * for the market data table.
	 */
	@Override
	protected void finishUI() {
		super.finishUI();
		
		IOptionOrderTicket optionOrderTicket = getOptionOrderTicket();

		addInputControlErrorDecoration(optionOrderTicket.getExpireMonthCombo());
		addInputControlErrorDecoration(optionOrderTicket.getStrikePriceCombo());
		addInputControlErrorDecoration(optionOrderTicket.getExpireYearCombo());
		addInputControlErrorDecoration(optionOrderTicket.getPutOrCallCombo());
		addInputControlErrorDecoration(optionOrderTicket.getOpenCloseCombo());
		addInputControlErrorDecoration(optionOrderTicket.getOrderCapacityCombo());


		optionOrderTicket.getSideCombo().removeAll();
		optionOrderTicket.getSideCombo().add(SideImage.BUY.getImage());
		optionOrderTicket.getSideCombo().add(SideImage.SELL.getImage());
		
		addComboChoicesFromLexerEnum(optionOrderTicket.getTifCombo(), TimeInForceImage.values());
		addComboChoicesFromLexerEnum(optionOrderTicket.getPutOrCallCombo(), PutOrCallImage.values());
		addComboChoicesFromLexerEnum(optionOrderTicket.getOrderCapacityCombo(), OrderCapacityImage.values());
		addComboChoicesFromLexerEnum(optionOrderTicket.getOpenCloseCombo(), OpenCloseImage.values());
		
		updateSize(optionOrderTicket.getExpireMonthCombo(), 5);
		updateSize(optionOrderTicket.getExpireYearCombo(), 7);
		updateSize(optionOrderTicket.getStrikePriceCombo(), 7);
		updateSize(optionOrderTicket.getPutOrCallCombo(), 2);

		TableViewer bidViewer = optionOrderTicket.getOptionMarketDataTableViewer();
		bidViewer.setLabelProvider(new OptionMessageListTableFormat(bidViewer.getTable(), OptionDataColumns.values(), getSite(), dictionary));
		bidViewer.setContentProvider(new ObservableListContentProvider());
		packColumns(bidViewer.getTable());

	}
	

	@Override
	public void dispose() {
		super.dispose();
		if (upImage != null) upImage.dispose();
		if (downImage != null) downImage.dispose();
	}
	
	@Override
	protected void setDefaultInput() {
		setInput(PhotonPlugin.getDefault().getOptionOrderTicketModel());
	}

	/**
	 * After calling superclass implementation, does additional binding work
	 * by calling {@link #bindOptionInfo(OptionOrderTicketModel, IOptionOrderTicket)}
	 * and {@link #bindUnderlyingInfo(OptionOrderTicketModel, IOptionOrderTicket)}
	 */
	@Override
	public void setInput(OrderTicketModel model) {
		super.setInput(model);
		OptionOrderTicketModel optionTicketModel = (OptionOrderTicketModel) model;
		IOptionOrderTicket optionOrderTicket = getOptionOrderTicket();
		bindOptionInfo(optionTicketModel, optionOrderTicket);
		bindUnderlyingInfo(optionTicketModel, optionOrderTicket);
	}

	/**
	 * This method binds the "choices" in the option order Combo UI elements
	 * to lists in the model.  For example the list returned by {@link OptionOrderTicketModel#getExpirationMonthList()}
	 * is bound to the available items in the expire month combo
	 * UI element.
	 * 
	 * The elements of these lists are updated to allow the user to choose
	 * a consistent set of values, e.g. Oct 25 C and Nov 30 C are both valid,
	 * but Oct 30 C is not.  
	 * 
	 * @param optionTicketModel the model underlying the order ticket
	 * @param optionOrderTicket the order ticket itself
	 */
	private void bindOptionInfo(OptionOrderTicketModel optionTicketModel,
			IOptionOrderTicket optionOrderTicket) {
		getDataBindingContext().bindList(
				new RetainTextObservable(optionOrderTicket.getExpireMonthCombo()),
				(optionTicketModel).getExpirationMonthList(),
				new UpdateListStrategy(UpdateListStrategy.POLICY_NEVER),
				null);
		getDataBindingContext().bindList(
				new RetainTextObservable(optionOrderTicket.getExpireYearCombo()),
				(optionTicketModel).getExpirationYearList(),
				new UpdateListStrategy(UpdateListStrategy.POLICY_NEVER),
				null);
		getDataBindingContext().bindList(
				new RetainTextObservable(optionOrderTicket.getStrikePriceCombo()),
				(optionTicketModel).getStrikePriceList(),
				new UpdateListStrategy(UpdateListStrategy.POLICY_NEVER),
				null);
	}
	
	/**
	 * Binds the fields related to underlying market data to the UI, including
	 * stock bid, stock offer, stock last trade, etc.
	 * 
	 * @param model the model underlying the ticket
	 * @param optionTicket the ticket itself
	 */
	private void bindUnderlyingInfo(OptionOrderTicketModel model,
			IOptionOrderTicket optionTicket) {
		{
			getDataBindingContext().bindValue(
					SWTObservables.observeText(optionTicket.getUnderlyingSymbolLabel()), 
					model.getUnderlyingSymbol(),
					new UpdateValueStrategy(UpdateValueStrategy.POLICY_NEVER),
					new UpdateValueStrategy()
			);
		}
		{
			getDataBindingContext().bindValue(
					SWTObservables.observeText(optionTicket.getUnderlyingLastPriceLabel()), 
					model.getUnderlyingLastPrice(),
					new UpdateValueStrategy(UpdateValueStrategy.POLICY_NEVER),
					new UpdateValueStrategy().setConverter(new BigDecimalToStringConverter(false))
			);
		}
		{
			getDataBindingContext().bindValue(
					new LabelBooleanImageObservableValue(optionTicket.getUnderlyingLastPriceUpDownArrowLabel(),upImage,downImage), 
					model.getUnderlyingTickIndicator(),
					new UpdateValueStrategy(UpdateValueStrategy.POLICY_NEVER),
					new UpdateValueStrategy()
			);
		}
		{
			getDataBindingContext().bindValue(
					SWTObservables.observeText(optionTicket.getUnderlyingLastPriceChangeLabel()), 
					model.getUnderlyingLastPriceChange(),
					new UpdateValueStrategy(UpdateValueStrategy.POLICY_NEVER),
					new UpdateValueStrategy().setConverter(new BigDecimalToStringConverter(false))
			);
		}
		{
			getDataBindingContext().bindValue(
					SWTObservables.observeText(optionTicket.getUnderlyingBidPriceLabel()), 
					model.getUnderlyingBidPrice(),
					new UpdateValueStrategy(UpdateValueStrategy.POLICY_NEVER),
					new UpdateValueStrategy().setConverter(new BigDecimalToStringConverter(false))
			);
		}
		{
			getDataBindingContext().bindValue(
					SWTObservables.observeText(optionTicket.getUnderlyingOfferPriceLabel()), 
					model.getUnderlyingOfferPrice(),
					new UpdateValueStrategy(UpdateValueStrategy.POLICY_NEVER),
					new UpdateValueStrategy().setConverter(new BigDecimalToStringConverter(false))
			);
		}
		{
			getDataBindingContext().bindValue(
					SWTObservables.observeText(optionTicket.getUnderlyingBidSizeLabel()), 
					model.getUnderlyingBidSize(),
					new UpdateValueStrategy(UpdateValueStrategy.POLICY_NEVER),
					new UpdateValueStrategy().setConverter(new BigDecimalToStringConverter(false))
			);
		}
		{
			getDataBindingContext().bindValue(
					SWTObservables.observeText(optionTicket.getUnderlyingOfferSizeLabel()), 
					model.getUnderlyingOfferSize(),
					new UpdateValueStrategy(UpdateValueStrategy.POLICY_NEVER),
					new UpdateValueStrategy().setConverter(new BigDecimalToStringConverter(false))
			);
		}
		{
			getDataBindingContext().bindValue(
					SWTObservables.observeText(optionTicket.getUnderlyingLastUpdatedTimeLabel()), 
					model.getUnderlyingLastUpdated(),
					new UpdateValueStrategy(UpdateValueStrategy.POLICY_NEVER),
					new UpdateValueStrategy().setConverter(new DateToStringCustomConverter("HH:mm"))
			);
		}
		{
			getDataBindingContext().bindValue(
					SWTObservables.observeText(optionTicket.getUnderlyingVolumeLabel()), 
					model.getUnderlyingVolume(),
					new UpdateValueStrategy(UpdateValueStrategy.POLICY_NEVER),
					new UpdateValueStrategy().setConverter(new BigDecimalToStringConverter(true))
			);
		}
		{
			getDataBindingContext().bindValue(
					SWTObservables.observeText(optionTicket.getUnderlyingOpenPriceLabel()), 
					model.getUnderlyingOpenPrice(),
					new UpdateValueStrategy(UpdateValueStrategy.POLICY_NEVER),
					new UpdateValueStrategy().setConverter(new BigDecimalToStringConverter(false))
			);
		}
		{
			getDataBindingContext().bindValue(
					SWTObservables.observeText(optionTicket.getUnderlyingHighPriceLabel()), 
					model.getUnderlyingHighPrice(),
					new UpdateValueStrategy(UpdateValueStrategy.POLICY_NEVER),
					new UpdateValueStrategy().setConverter(new BigDecimalToStringConverter(false))
			);
		}
		{
			getDataBindingContext().bindValue(
					SWTObservables.observeText(optionTicket.getUnderlyingLowPriceLabel()), 
					model.getUnderlyingLowPrice(),
					new UpdateValueStrategy(UpdateValueStrategy.POLICY_NEVER),
					new UpdateValueStrategy().setConverter(new BigDecimalToStringConverter(false))
			);
		}
		{
			getDataBindingContext().bindValue(
					SWTObservables.observeText(optionTicket.getUnderlyingTradedValueLabel()), 
					model.getUnderlyingTradedValue(),
					new UpdateValueStrategy(UpdateValueStrategy.POLICY_NEVER),
					new UpdateValueStrategy().setConverter(new BigDecimalToStringConverter(false))
			);
		}
		{
			// Only show underlying market data composite when the
			// underlying symbol has a value
			getDataBindingContext().bindValue(
					SWTObservables.observeVisible(optionTicket.getUnderlyingMarketDataComposite()),
					model.getUnderlyingSymbol(),
					new UpdateValueStrategy(UpdateValueStrategy.POLICY_NEVER), 
					new UpdateValueStrategy().setConverter(new HasValueConverter()));
		}
		
	}

	@Override
	protected String getNewOrderString() {
		return NEW_OPTION_ORDER;
	}

	@Override
	protected String getReplaceOrderString() {
		return REPLACE_OPTION_ORDER;
	}
	
	/**
	 * Get a reference to the underlying {@link IOptionOrderTicket} representation,
	 * which at this time is an XSWT proxy object.
	 * @return the XSWT proxy object
	 */
	public IOptionOrderTicket getOptionOrderTicket(){
		return (IOptionOrderTicket) getOrderTicket();
	}

	/**
	 * Gets the "default" OptionOrderTicketView, that is the first one returned
	 * by {@link IWorkbenchPage#findView(String)}
	 * @return the default OptionOrderTicketView
	 */
	public static OptionOrderTicketView getDefault() {
		OptionOrderTicketView orderTicket = (OptionOrderTicketView) PlatformUI
				.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.findView(OptionOrderTicketView.ID);

		return orderTicket;
	}

	@Override
	protected Class<? extends IOrderTicket> getXSWTInterfaceClass() {
		return IOptionOrderTicket.class;
	}

	/**
	 * Binds the additional fields to UI components necessary to specify
	 * an option order.  Examples include expiration month, expiration year
	 * and strike price.
	 * 
	 * @param pModel the model underlying the order ticket
	 * @param controlsRequiringInput updated with the controls that require user input
	 */
	@Override
	protected void bindMessage(final OrderTicketModel pModel, List<IObservableValue> controlsRequiringInput) {
		super.bindMessage(pModel, controlsRequiringInput);
		OptionOrderTicketModel model = (OptionOrderTicketModel) pModel;
		Message message = model.getOrderMessage();
		
		IOptionOrderTicket optionTicket = getOptionOrderTicket();
		// in addition, disable Expiry month/year, strike, call/put
		boolean isOrderSingle = FIXMessageUtil.isOrderSingle(message);
		optionTicket.getPutOrCallCombo().setEnabled(isOrderSingle);
		
		Realm realm = Realm.getDefault();

		Text symbolText = optionTicket.getSymbolText();
		try {
			symbolText.setText(message.getString(Symbol.FIELD));
		} catch (FieldNotFound ex){
			symbolText.setText("");
		}
		
		bindMessageValue(
				SWTObservables.observeText(optionTicket.getSymbolText(), SWT.Modify),
				new UpdateOptionInfoObservable(model),
				new UpdateValueStrategy(),
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_NEVER)
				);
		
		/**
		 * Note that the MaturityDate and StrikePrice in the order are not used
		 * by the OMS. They are used here to have a place for the data binding
		 * to store the data. The code part of the option contract symbol
		 * represents that data.
		 */
		// ExpireDate Month
		{
			Control whichControl = optionTicket.getExpireMonthCombo();
			IValidator targetAfterGetValidator = new IgnoreFirstNullValidator(new ObservableListValidator(model.getExpirationMonthList(), PhotonPlugin.ID, "Value not found", false));
			IValidator modelBeforeSetValidator = new IgnoreFirstNullValidator(new StringRequiredValidator());
			ISWTObservableValue swtObservable = SWTObservables.observeText(whichControl);
			Binding binding = bindMessageValue( 
					swtObservable,
					FIXObservables.observeMonthDateValue(realm, message, MaturityMonthYear.FIELD,dictionary),
					new UpdateValueStrategy().setAfterGetValidator(targetAfterGetValidator).setConverter(new StringToDateCustomConverter(DateToStringCustomConverter.MONTH_FORMAT)),
					new UpdateValueStrategy().setBeforeSetValidator(modelBeforeSetValidator).setConverter(new DateToStringCustomConverter(DateToStringCustomConverter.MONTH_FORMAT)));
			bindMessageValue(
					new ErrorDecorationObservable(whichControl, errorImage, warningImage),
					binding.getValidationStatus(),
					new UpdateValueStrategy(),
					new UpdateValueStrategy());
			bindMessageValue(
					SWTObservables.observeEnabled(whichControl),
					BeansObservables.observeValue(model, "orderMessage"),
					null,
					new UpdateValueStrategy().setConverter(new IsNewOrderMessageConverter()));
			bindMessageValue(
					swtObservable,
					new UpdateOptionInfoObservable(model),
					new UpdateValueStrategy(),
					new UpdateValueStrategy(UpdateValueStrategy.POLICY_NEVER)
					);
			controlsRequiringInput.add(swtObservable);
		}
		// ExpireDate Year
		{
			Control whichControl = optionTicket.getExpireYearCombo();
			IValidator validator = new IgnoreFirstNullValidator(new ObservableListValidator(model.getExpirationYearList(), PhotonPlugin.ID, "Value not found", false));
			ISWTObservableValue swtObservable = SWTObservables.observeText(whichControl);
			Binding binding = bindMessageValue(
					swtObservable,
					FIXObservables.observeYearDateValue(realm, message, MaturityMonthYear.FIELD,dictionary),
					new UpdateValueStrategy().setAfterGetValidator(validator).setConverter(new StringToDateCustomConverter(DateToStringCustomConverter.SHORT_YEAR_FORMAT, DateToStringCustomConverter.LONG_YEAR_FORMAT)),
					new UpdateValueStrategy().setConverter(new DateToStringCustomConverter(DateToStringCustomConverter.SHORT_YEAR_FORMAT)));
			bindMessageValue(
					new ErrorDecorationObservable(whichControl, errorImage, warningImage),
					binding.getValidationStatus(),
					new UpdateValueStrategy(),
					new UpdateValueStrategy());
			bindMessageValue(
					SWTObservables.observeEnabled(whichControl),
					BeansObservables.observeValue(model, "orderMessage"),
					null,
					new UpdateValueStrategy().setConverter(new IsNewOrderMessageConverter()));
			bindMessageValue(
					swtObservable,
					new UpdateOptionInfoObservable(model),
					new UpdateValueStrategy(),
					new UpdateValueStrategy(UpdateValueStrategy.POLICY_NEVER)
					);
			controlsRequiringInput.add(swtObservable);
		}

		// StrikePrice
		{
			Control whichControl = optionTicket.getStrikePriceCombo();
			IValidator targetAfterGetValidator = new IgnoreFirstNullValidator(new ObservableListValidator(model.getStrikePriceList(), PhotonPlugin.ID, "Value not found", false));
			
			ISWTObservableValue swtObservable = SWTObservables.observeText(whichControl);
			Binding binding = bindMessageValue(
					swtObservable,
					FIXObservables.observeValue(realm, message, StrikePrice.FIELD, dictionary),
					new UpdateValueStrategy().setAfterGetValidator(targetAfterGetValidator).setConverter(new StringToBigDecimalConverter()),
					new UpdateValueStrategy().setConverter(new BigDecimalToStringConverter(false))
			);
			bindMessageValue(
					new ErrorDecorationObservable(whichControl, errorImage, warningImage),
					binding.getValidationStatus(),
					new UpdateValueStrategy(),
					new UpdateValueStrategy());
			bindMessageValue(
					SWTObservables.observeEnabled(whichControl),
					BeansObservables.observeValue(model, "orderMessage"),
					null,
					new UpdateValueStrategy().setConverter(new IsNewOrderMessageConverter()));
			bindMessageValue(
					swtObservable,
					new UpdateOptionInfoObservable(model),
					new UpdateValueStrategy(),
					new UpdateValueStrategy(UpdateValueStrategy.POLICY_NEVER)
					);
			controlsRequiringInput.add(swtObservable);
		}
		// PutOrCall (OptionCFICode)
		{
            Control whichControl = optionTicket.getPutOrCallCombo();
			IValidator targetAfterGetValidator = new IgnoreFirstNullValidator(putOrCallConverterBuilder.newTargetAfterGetValidator());
			IValidator modelAfterGetValidator = putOrCallConverterBuilder.newModelAfterGetValidator();
			
			ISWTObservableValue swtObservable = SWTObservables.observeText(whichControl);
			Binding binding = bindMessageValue(
					swtObservable,
					FIXObservables.observeValue(realm,
							message, PutOrCall.FIELD, dictionary, PutOrCall.class.getSimpleName(), FieldType.Int),
					new UpdateValueStrategy().setAfterGetValidator(targetAfterGetValidator).setConverter(putOrCallConverterBuilder.newToModelConverter()),
					new UpdateValueStrategy().setAfterGetValidator(modelAfterGetValidator).setConverter(putOrCallConverterBuilder.newToTargetConverter())
			);
			bindMessageValue(
					new ErrorDecorationObservable(whichControl, errorImage, warningImage),
					binding.getValidationStatus(),
					new UpdateValueStrategy(),
					new UpdateValueStrategy());
			bindMessageValue(
					SWTObservables.observeEnabled(whichControl),
					BeansObservables.observeValue(model, "orderMessage"),
					null,
					new UpdateValueStrategy().setConverter(new IsNewOrderMessageConverter()));
			bindMessageValue(
					swtObservable,
					new UpdateOptionInfoObservable(model),
					new UpdateValueStrategy(),
					new UpdateValueStrategy(UpdateValueStrategy.POLICY_NEVER)
					);
			controlsRequiringInput.add(swtObservable);
		}
		// OrderCapacity
		if(OptionOrderTicketModel.isOrderCapacityAllowed())
		{
			Control whichControl = optionTicket.getOrderCapacityCombo();
			IValidator targetAfterGetValidator = new IgnoreFirstNullValidator(orderCapacityConverterBuilder.newTargetAfterGetValidator());
			IValidator modelAfterGetValidator = orderCapacityConverterBuilder.newModelAfterGetValidator();

			// The FIX field may need to be updated., See
			// http://trac.marketcetera.org/trac.fcgi/ticket/185
			// If the field below is changed the defaults in the 
			// message created by newNewOrderSingle() must be updated. 
			final int orderCapacityFIXField = OrderCapacity.FIELD;
			// final int orderCapacityFIXField = CustomerOrFirm.FIELD;
			ISWTObservableValue swtObservable = SWTObservables.observeText(whichControl);
			Binding binding = bindMessageValue(
					swtObservable,
					FIXObservables.observeValue(realm, message, orderCapacityFIXField, dictionary, OrderCapacity.class.getSimpleName(), FieldType.Char),
					new UpdateValueStrategy().setAfterGetValidator(targetAfterGetValidator).setConverter(orderCapacityConverterBuilder.newToModelConverter()),
					new UpdateValueStrategy().setAfterGetValidator(modelAfterGetValidator).setConverter(orderCapacityConverterBuilder.newToTargetConverter())
			);
			bindMessageValue(
					new ErrorDecorationObservable(whichControl, errorImage, warningImage),
					binding.getValidationStatus(),
					new UpdateValueStrategy(),
					new UpdateValueStrategy());
			bindMessageValue(
					swtObservable,
					new UpdateOptionInfoObservable(model),
					new UpdateValueStrategy(),
					new UpdateValueStrategy(UpdateValueStrategy.POLICY_NEVER)
					);
			controlsRequiringInput.add(swtObservable);
		}
		// OpenClose
		if(OptionOrderTicketModel.isOpenCloseAllowed())
		{
			Control whichControl = optionTicket.getOpenCloseCombo();
			IValidator targetAfterGetValidator = new IgnoreFirstNullValidator(openCloseConverterBuilder.newTargetAfterGetValidator());
			IValidator modelAfterGetValidator = openCloseConverterBuilder.newModelAfterGetValidator();
			
			// If the field below is changed the defaults in the 
			// message created by newNewOrderSingle() must be updated.
			final int openCloseField = OpenClose.FIELD;
			ISWTObservableValue swtObservable = SWTObservables.observeText(whichControl);
			Binding binding = bindMessageValue(
					swtObservable, 
					FIXObservables.observeValue(realm, message, openCloseField, dictionary),
					new UpdateValueStrategy().setAfterGetValidator(targetAfterGetValidator).setConverter(openCloseConverterBuilder.newToModelConverter()),
					new UpdateValueStrategy().setAfterGetValidator(modelAfterGetValidator).setConverter(openCloseConverterBuilder.newToTargetConverter())
			);
			bindMessageValue(
					new ErrorDecorationObservable(whichControl, errorImage, warningImage),
					binding.getValidationStatus(),
					new UpdateValueStrategy(),
					new UpdateValueStrategy());
			bindMessageValue(
					swtObservable,
					new UpdateOptionInfoObservable(model),
					new UpdateValueStrategy(),
					new UpdateValueStrategy(UpdateValueStrategy.POLICY_NEVER)
					);
			controlsRequiringInput.add(swtObservable);
		}
		// Option symbol
		{
			Control whichControl = optionTicket.getOptionSymbolText();
			bindMessageValue(
					SWTObservables.observeText(whichControl, SWT.Modify),
					((OptionOrderTicketModel) model).getCurrentOptionSymbol(),
					new UpdateValueStrategy(),
					new UpdateValueStrategy()
			);
		}
		{
			optionTicket.getOptionMarketDataTableViewer().setInput(model.getOptionMarketDataList());
		}
	}

	/**
	 * Initialize the converter builder for the OrderCapacity field and UI representation
	 * @return the builder
	 */
    private EnumStringConverterBuilder<Character> createOrderCapacityConverterBuilder() {
		EnumStringConverterBuilder<Character> returnBuilder = new EnumStringConverterBuilder<Character>(
				Character.class);
		bindingHelper.initCharToImageConverterBuilder(
				returnBuilder, OrderCapacityImage.values());
		return returnBuilder;
	}

	/**
	 * Initialize the converter builder for the OpenClose field and UI representation
	 * @return the builder
	 */
	private EnumStringConverterBuilder<Character> createOpenCloseConverterBuilder() {
		EnumStringConverterBuilder<Character> returnBuilder = new EnumStringConverterBuilder<Character>(
				Character.class);
		bindingHelper.initCharToImageConverterBuilder(
				returnBuilder, OpenCloseImage.values());
		return returnBuilder;
	}

	/**
	 * Initialize the converter builder for the PutOrCall field and UI representation
	 * @return the builder
	 */
	private EnumStringConverterBuilder<Integer> createPutOrCallConverterBuilder() {
		EnumStringConverterBuilder<Integer> returnBuilder = new EnumStringConverterBuilder<Integer>(
				Integer.class);
		bindingHelper.initIntToImageConverterBuilder(
				returnBuilder, PutOrCallImage.values());
		return returnBuilder;
	}

	private final class UpdateOptionInfoObservable extends
			AbstractObservableValue {
		private final OrderTicketModel model;

		private UpdateOptionInfoObservable(OrderTicketModel model) {
			this.model = model;
		}

		@Override
		protected void doSetValue(Object value) {
			((OptionOrderTicketModel) model).updateOptionInfo();
		}

		@Override
		protected Object doGetValue() {
			return null;
		}

		public Object getValueType() {
			return null;
		}
	}

	public enum OptionDataColumns implements IFieldIdentifier {
		ZEROWIDTH(""), 
		CVOL("cVol", OptionInfoComponent.CALL_EXTRA_INFO, MDEntrySize.FIELD, NoMDEntries.FIELD, MDEntryType.FIELD, MDEntryType.TRADE_VOLUME), 
		CBIDSZ("cBidSz", OptionInfoComponent.CALL_MARKET_DATA, MDEntrySize.FIELD, NoMDEntries.FIELD, MDEntryType.FIELD, MDEntryType.BID), 
		CBID("cBid", OptionInfoComponent.CALL_MARKET_DATA, MDEntryPx.FIELD, NoMDEntries.FIELD, MDEntryType.FIELD, MDEntryType.BID),
		CASK("cAsk", OptionInfoComponent.CALL_MARKET_DATA, MDEntryPx.FIELD, NoMDEntries.FIELD, MDEntryType.FIELD, MDEntryType.OFFER),
		CASKSZ("cAskSz", OptionInfoComponent.CALL_MARKET_DATA, MDEntrySize.FIELD, NoMDEntries.FIELD, MDEntryType.FIELD, MDEntryType.OFFER),
		CSYM("cSym", OptionInfoComponent.CALL_EXTRA_INFO, Symbol.FIELD, null, null, null),
		STRIKE("Strike", OptionInfoComponent.STRIKE_INFO, StrikePrice.FIELD, null, null, null),
		EXP("Exp", OptionInfoComponent.STRIKE_INFO, MaturityMonthYear.FIELD, null, null, null),
		PSYM("pSym", OptionInfoComponent.PUT_EXTRA_INFO, Symbol.FIELD, null, null, null),
		PBIDSZ("pBidSz", OptionInfoComponent.PUT_MARKET_DATA, MDEntrySize.FIELD, NoMDEntries.FIELD, MDEntryType.FIELD, MDEntryType.BID), 
		PBID("pBid", OptionInfoComponent.PUT_MARKET_DATA, MDEntryPx.FIELD, NoMDEntries.FIELD, MDEntryType.FIELD, MDEntryType.BID),
		PASK("pAsk", OptionInfoComponent.PUT_MARKET_DATA, MDEntryPx.FIELD, NoMDEntries.FIELD, MDEntryType.FIELD, MDEntryType.OFFER),
		PASKSZ("pAskSz", OptionInfoComponent.PUT_MARKET_DATA, MDEntrySize.FIELD, NoMDEntries.FIELD, MDEntryType.FIELD, MDEntryType.OFFER),
		PVOL("pVol", OptionInfoComponent.PUT_EXTRA_INFO, MDEntrySize.FIELD, NoMDEntries.FIELD, MDEntryType.FIELD, MDEntryType.TRADE_VOLUME);


		private String name;
		private Integer fieldID;
		private Integer groupID;
		private Integer groupDiscriminatorID;
		private Object groupDiscriminatorValue;
		private OptionInfoComponent component;

		OptionDataColumns(String name){
			this.name = name;
		}

		OptionDataColumns(String name, OptionInfoComponent component, Integer fieldID, Integer groupID, Integer groupDiscriminatorID, Object groupDiscriminatorValue){
			this.name=name;
			this.component = component;
			this.fieldID = fieldID;
			this.groupID = groupID;
			this.groupDiscriminatorID = groupDiscriminatorID;
			this.groupDiscriminatorValue = groupDiscriminatorValue;
		}

		public String toString() {
			return name;
		}

		public Integer getFieldID() {
			return fieldID;
		}
		
		public Integer getGroupID() {
			return groupID;
		}

		public Integer getGroupDiscriminatorID() {
			return groupDiscriminatorID;
		}

		public Object getGroupDiscriminatorValue() {
			return groupDiscriminatorValue;
		}

		public OptionInfoComponent getComponent() {
			return component;
		}
	}

}
