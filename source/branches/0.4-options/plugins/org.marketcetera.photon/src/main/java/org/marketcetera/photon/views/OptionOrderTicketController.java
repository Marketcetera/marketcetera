package org.marketcetera.photon.views;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.marketcetera.photon.parser.OpenCloseImage;
import org.marketcetera.photon.parser.OrderCapacityImage;
import org.marketcetera.photon.parser.PutOrCallImage;
import org.marketcetera.photon.ui.validation.fix.EnumStringConverterBuilder;
import org.marketcetera.photon.ui.validation.fix.FIXObservables;
import org.marketcetera.photon.ui.validation.fix.PriceConverterBuilder;

import quickfix.DataDictionary;
import quickfix.Message;
import quickfix.field.OrderCapacity;
import quickfix.field.PutOrCall;
import quickfix.field.StrikePrice;

public class OptionOrderTicketController extends AbstractOrderTicketController
		implements IFIXControllerBinding {
	private IOptionOrderTicket ticket;

	private BindingHelper bindingHelper;

	private OrderTicketControllerHelper controllerHelper;

	private EnumStringConverterBuilder<Character> orderCapacityConverterBuilder;

	private EnumStringConverterBuilder<Character> openCloseConverterBuilder;

	private EnumStringConverterBuilder<Integer> putOrCallConverterBuilder;

	private PriceConverterBuilder strikeConverterBuilder;

	public OptionOrderTicketController(IOptionOrderTicket ticket) {
		this.ticket = ticket;
		bindingHelper = new BindingHelper();
		controllerHelper = new OrderTicketControllerHelper(ticket, this);

		initOrderCapacityConverterBuilder();
		initOpenCloseConverterBuilder();
		initPutOrCallConverterBuilder();

		controllerHelper.clear();
	}

	@Override
	protected OrderTicketControllerHelper getOrderTicketControllerHelper() {
		return controllerHelper;
	}

	public void bind(Realm realm, DataBindingContext dataBindingContext,
			DataDictionary dictionary, Message message) {

		// todo: ExpireDate.FIELD bindings to
		// ticket.getExpireYearCCombo() and
		// ticket.getExpireMonthCCombo()

		// StrikePrice
		dataBindingContext
				.bindValue(
						SWTObservables.observeText(ticket.getStrikeText(),
								SWT.Modify),
						FIXObservables.observeValue(realm, message,
								StrikePrice.FIELD, dictionary),
						bindingHelper
								.createToModelUpdateValueStrategy(strikeConverterBuilder),
						bindingHelper
								.createToTargetUpdateValueStrategy(strikeConverterBuilder));

		// PutOrCall
		dataBindingContext
				.bindValue(
						SWTObservables.observeText(ticket.getPutOrCallCCombo()),
						FIXObservables.observeValue(realm, message,
								PutOrCall.FIELD, dictionary),
						bindingHelper
								.createToModelUpdateValueStrategy(putOrCallConverterBuilder),
						bindingHelper
								.createToTargetUpdateValueStrategy(putOrCallConverterBuilder));

		// OrderCapacity
		dataBindingContext
				.bindValue(
						SWTObservables.observeText(ticket
								.getOrderCapacityCCombo()),
						FIXObservables.observeValue(realm, message,
								OrderCapacity.FIELD, dictionary),
						bindingHelper
								.createToModelUpdateValueStrategy(orderCapacityConverterBuilder),
						bindingHelper
								.createToTargetUpdateValueStrategy(orderCapacityConverterBuilder));
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
		bindingHelper.initIntToImageConverterBuilder(putOrCallConverterBuilder,
				PutOrCallImage.values());
	}
}
