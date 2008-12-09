package org.marketcetera.photon.views;

import java.io.InputStream;
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
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.photon.IImageKeys;
import org.marketcetera.photon.Messages;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.parser.OpenCloseImage;
import org.marketcetera.photon.parser.OrderCapacityImage;
import org.marketcetera.photon.parser.PutOrCallImage;
import org.marketcetera.photon.parser.SideImage;
import org.marketcetera.photon.ui.databinding.ErrorDecorationObservable;
import org.marketcetera.photon.ui.databinding.IsNewOrderMessageConverter;
import org.marketcetera.photon.ui.databinding.RetainTextObservable;
import org.marketcetera.photon.ui.validation.IgnoreNullValidator;
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
import quickfix.field.MaturityMonthYear;
import quickfix.field.OpenClose;
import quickfix.field.OrderCapacity;
import quickfix.field.PutOrCall;
import quickfix.field.StrikePrice;
import quickfix.field.Symbol;

/* $License$ */

/**
 * This class implements the view that provides the end user
 * the ability to type in--and graphically interact with--stock option orders.
 * 
 * Additionally this class manages the stock and option market data that can be displayed
 * along with the order ticket itself.
 * 
 * @author gmiller
 * @version $Id$
 * @since 1.0.0
 *
 */
@ClassVersion("$Id$")
public class OptionOrderTicketView
    extends OrderTicketView
    implements Messages
{
	private final EnumStringConverterBuilder<Character> orderCapacityConverterBuilder;

	private final EnumStringConverterBuilder<Character> openCloseConverterBuilder;

	private final EnumStringConverterBuilder<Integer> putOrCallConverterBuilder;

	private final Image upImage;

	private final Image downImage;

	public static String ID = "org.marketcetera.photon.views.OptionOrderTicketView"; //$NON-NLS-1$


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
	protected InputStream getXSWTResourceStream() {
		return getClass().getResourceAsStream("/option_order_ticket.xswt"); //$NON-NLS-1$
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
		
		addComboChoicesFromLexerEnum(optionOrderTicket.getPutOrCallCombo(), PutOrCallImage.values());
		addComboChoicesFromLexerEnum(optionOrderTicket.getOrderCapacityCombo(), OrderCapacityImage.values());
		addComboChoicesFromLexerEnum(optionOrderTicket.getOpenCloseCombo(), OpenCloseImage.values());
		
		updateSize(optionOrderTicket.getExpireMonthCombo(), 5);
		updateSize(optionOrderTicket.getExpireYearCombo(), 7);
		updateSize(optionOrderTicket.getStrikePriceCombo(), 7);
		updateSize(optionOrderTicket.getPutOrCallCombo(), 2);

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
	 */
	@Override
	public void setInput(OrderTicketModel model) {
		super.setInput(model);
		OptionOrderTicketModel optionTicketModel = (OptionOrderTicketModel) model;
		IOptionOrderTicket optionOrderTicket = getOptionOrderTicket();
		bindOptionInfo(optionTicketModel, optionOrderTicket);
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

	@Override
	protected String getNewOrderString()
	{
		return NEW_OPTION_LABEL.getText();
	}

	@Override
	protected String getReplaceOrderString()
	{
		return REPLACE_OPTION_LABEL.getText();
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
			symbolText.setText(""); //$NON-NLS-1$
		}
		
		bindMessageValue(
				SWTObservables.observeText(optionTicket.getSymbolText(), SWT.Modify),
				new UpdateOptionInfoObservable(model),
				new UpdateValueStrategy(),
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_NEVER)
				);
		
		/**
		 * Note that the MaturityDate and StrikePrice in the order are not used
		 * by the ORS. They are used here to have a place for the data binding
		 * to store the data. The code part of the option contract symbol
		 * represents that data.
		 */
		// ExpireDate Month
		{
			Control whichControl = optionTicket.getExpireMonthCombo();
			IValidator targetAfterGetValidator = new IgnoreNullValidator(new ObservableListValidator(model.getExpirationMonthList(),
			                                                                                              PhotonPlugin.ID,
			                                                                                              VALUE_NOT_FOUND.getText(),
			                                                                                              false));
			IValidator modelBeforeSetValidator = new IgnoreNullValidator(new StringRequiredValidator());
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
					BeansObservables.observeValue(model, "orderMessage"), //$NON-NLS-1$
					null,
					new UpdateValueStrategy().setConverter(new IsNewOrderMessageConverter()));
			bindMessageValue(
					swtObservable,
					new UpdateOptionInfoObservable(model),
					new UpdateValueStrategy(),
					new UpdateValueStrategy(UpdateValueStrategy.POLICY_NEVER)
					);
		}
		// ExpireDate Year
		{
			Control whichControl = optionTicket.getExpireYearCombo();
			IValidator validator = new IgnoreNullValidator(new ObservableListValidator(model.getExpirationYearList(),
			                                                                                PhotonPlugin.ID,
			                                                                                VALUE_NOT_FOUND.getText(),
			                                                                                false));
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
					BeansObservables.observeValue(model, "orderMessage"), //$NON-NLS-1$
					null,
					new UpdateValueStrategy().setConverter(new IsNewOrderMessageConverter()));
			bindMessageValue(
					swtObservable,
					new UpdateOptionInfoObservable(model),
					new UpdateValueStrategy(),
					new UpdateValueStrategy(UpdateValueStrategy.POLICY_NEVER)
					);
		}

		// StrikePrice
		{
			Control whichControl = optionTicket.getStrikePriceCombo();
			IValidator targetAfterGetValidator = new IgnoreNullValidator(new ObservableListValidator(model.getStrikePriceList(),
			                                                                                              PhotonPlugin.ID,
			                                                                                              VALUE_NOT_FOUND.getText(),
			                                                                                              false));
			
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
					BeansObservables.observeValue(model, "orderMessage"), //$NON-NLS-1$
					null,
					new UpdateValueStrategy().setConverter(new IsNewOrderMessageConverter()));
			bindMessageValue(
					swtObservable,
					new UpdateOptionInfoObservable(model),
					new UpdateValueStrategy(),
					new UpdateValueStrategy(UpdateValueStrategy.POLICY_NEVER)
					);
		}
		// PutOrCall (OptionCFICode)
		{
            Control whichControl = optionTicket.getPutOrCallCombo();
			IValidator targetAfterGetValidator = new IgnoreNullValidator(putOrCallConverterBuilder.newTargetAfterGetValidator());
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
					BeansObservables.observeValue(model, "orderMessage"), //$NON-NLS-1$
					null,
					new UpdateValueStrategy().setConverter(new IsNewOrderMessageConverter()));
			bindMessageValue(
					swtObservable,
					new UpdateOptionInfoObservable(model),
					new UpdateValueStrategy(),
					new UpdateValueStrategy(UpdateValueStrategy.POLICY_NEVER)
					);
		}
		// OrderCapacity
		{
			Control whichControl = optionTicket.getOrderCapacityCombo();
			IValidator targetAfterGetValidator = new IgnoreNullValidator(orderCapacityConverterBuilder.newTargetAfterGetValidator());
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
		}
		// OpenClose
		{
			Control whichControl = optionTicket.getOpenCloseCombo();
			IValidator targetAfterGetValidator = new IgnoreNullValidator(openCloseConverterBuilder.newTargetAfterGetValidator());
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
}
