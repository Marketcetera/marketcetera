package org.marketcetera.photon.parser;

import java.text.ParseException;

import org.eclipse.core.databinding.conversion.IConverter;
import org.marketcetera.photon.marketdata.OptionMarketDataUtils;
import org.marketcetera.photon.ui.databinding.BindingHelper;
import org.marketcetera.photon.ui.validation.fix.EnumStringConverterBuilder;
import org.marketcetera.photon.ui.validation.fix.PriceConverterBuilder;
import org.marketcetera.photon.ui.validation.fix.PriceObservableValue;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.quickfix.cficode.OptionCFICode;

import quickfix.DataDictionary;
import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.CFICode;
import quickfix.field.MsgType;
import quickfix.field.OrdType;
import quickfix.field.OrderQty;
import quickfix.field.PutOrCall;
import quickfix.field.SecurityType;
import quickfix.field.Side;
import quickfix.field.StrikePrice;
import quickfix.field.Symbol;

/**
 * This class can format QuickFIX orders int human readable strings,
 * given a {@link DataDictionary}.
 * 
 * A stock order might format to "SS 100 IBM 10", and an option order
 * "B 100 IBM 08Oct75P MKT".
 * 
 * The string will likely not represent every field in the given order,
 * and should be used primarily to give a user a "summary" of the given order.
 * 
 * 
 * @author gmiller
 *
 */
public class OrderFormatter {

	private static final String BAD_ORDER_MESSAGE = "Unknown message type";
	private static final String MISSING_FIELD = "Missing field";
	private IConverter sideConverter;
	private IConverter priceConverter;
	private IConverter putOrCallConverter;

	public OrderFormatter(DataDictionary dict) {
		EnumStringConverterBuilder<Character> sideConverterBuilder = 
			new EnumStringConverterBuilder<Character>(Character.class);
		BindingHelper bindingHelper = new BindingHelper();
		bindingHelper.initCharToImageConverterBuilder(
				sideConverterBuilder, SideImage.values());
		sideConverter = sideConverterBuilder.newToTargetConverter();

		PriceConverterBuilder priceConverterBuilder = new
			PriceConverterBuilder(dict);
		priceConverterBuilder.addMapping(OrdType.MARKET, PriceImage.MKT
				.getImage());

		priceConverter = priceConverterBuilder.newToTargetConverter();

		EnumStringConverterBuilder<Integer> putOrCallConverterBuilder = 
			new EnumStringConverterBuilder<Integer>(Integer.class);
		bindingHelper.initIntToImageConverterBuilder(putOrCallConverterBuilder, PutOrCallImage.values());
		putOrCallConverter = putOrCallConverterBuilder.newToTargetConverter();
	}

	/**
	 * Create a human readable representation for the given order message.
	 * @param orderMessage an order message with message type {@link MsgType#ORDER_SINGLE}
	 * @return a string representing the basics of this order
	 * @throws ParseException
	 */
	public String format(Message orderMessage) throws ParseException{
		StringBuffer sb = new StringBuffer();
		try {
			if (FIXMessageUtil.isOrderSingle(orderMessage))
			{
				char side = orderMessage.getChar(Side.FIELD);
				sb.append(sideConverter.convert(side));
				sb.append(" ");
				String orderQty = orderMessage.getString(OrderQty.FIELD);
				sb.append(orderQty);
				sb.append(" ");
				String symbol = orderMessage.getString(Symbol.FIELD);
				sb.append(symbol);
				sb.append(" ");
				
				String securityType = null;
				String cfiCode = null;
				if ((orderMessage.isSetField(SecurityType.FIELD) && SecurityType.OPTION.equals(securityType = orderMessage.getString(SecurityType.FIELD)))
						|| (orderMessage.isSetField(CFICode.FIELD) && OptionCFICode.isOptionCFICode(cfiCode = orderMessage.getString(CFICode.FIELD)))){
					
					String putOrCallString = "";
					if (SecurityType.OPTION.equals(securityType)){
						putOrCallString = (String)putOrCallConverter.convert(orderMessage.getInt(PutOrCall.FIELD));
					} else {
						OptionCFICode opcfi = new OptionCFICode(cfiCode);
						if (OptionCFICode.TYPE_PUT == opcfi.getType()){
							putOrCallString = PutOrCallImage.PUT.getImage();
						} else if (OptionCFICode.TYPE_CALL == opcfi.getType()) {
							putOrCallString = PutOrCallImage.CALL.getImage();
						}
					}
					String strikePrice = orderMessage.getString(StrikePrice.FIELD);

					String expirationString = OptionMarketDataUtils.getOptionExpirationMonthString(orderMessage);
					sb.append(expirationString);
					sb.append(strikePrice);
					sb.append(putOrCallString);
					sb.append(" ");
				}
				
				Object price = PriceObservableValue.extractValue(orderMessage);
				sb.append(priceConverter.convert(price));
				
				return sb.toString();
			} else {
				return BAD_ORDER_MESSAGE;
			}
		} catch (FieldNotFound ex){
			return MISSING_FIELD;
		}
	}
}
