package org.marketcetera.core.instruments;

import org.marketcetera.trade.Currency;

import org.marketcetera.trade.Instrument;


import quickfix.DataDictionary;
import quickfix.Message;
import quickfix.field.FutSettDate;
import quickfix.field.FutSettDate2;
import quickfix.field.MsgType;
import quickfix.field.Product;
import quickfix.field.SecurityType;
import quickfix.field.Symbol;

public class CurrencyToMessage extends InstrumentToMessage<Currency>{

	public CurrencyToMessage(){
		super(org.marketcetera.trade.Currency.class);
	}
	
	@Override
	public boolean isSupported(DataDictionary inDictionary, String inMsgType) {
		if(MsgType.ORDER_CANCEL_REQUEST.equals(inMsgType))
		{
			return inDictionary.isMsgField(inMsgType,Symbol.FIELD);
		}
		else
		{
        return (inDictionary.isMsgField(inMsgType,Symbol.FIELD) && 
        		inDictionary.isMsgField(inMsgType,FutSettDate.FIELD) &&
        		inDictionary.isMsgField(inMsgType,quickfix.field.Currency.FIELD));
		}
	}

	@Override
	public void set(Instrument inInstrument, String inBeginString, Message inMessage) {
		
	}

	/**
	 * simply plug the values in.
	 * 
	 *   do some basic conditioning on message type
	 */
	@Override
	public void set(Instrument instrument, DataDictionary dictionary,String msgType, Message message) {

		// always include the symbol
		message.setString(Symbol.FIELD, instrument.getSymbol());
		message.setString(SecurityType.FIELD,SecurityType.FOREIGN_EXCHANGE_CONTRACT);

		if (dictionary.isMsgField(msgType, Product.FIELD)) {
			message.setInt(Product.FIELD, Product.CURRENCY);
		}

		Currency currencyInstrument = (Currency) instrument;
		if (dictionary.isMsgField(msgType, quickfix.field.Currency.FIELD)) {
			message.setString(quickfix.field.Currency.FIELD,currencyInstrument.getTradedCCY());
		}
		if (MsgType.ORDER_CANCEL_REQUEST.equals(msgType)) {
			message.removeField(FutSettDate.FIELD);
		} else {
			message.setString(FutSettDate.FIELD,currencyInstrument.getNearTenor());
		}
		if (currencyInstrument.isSwap()) {
			message.setString(FutSettDate2.FIELD,currencyInstrument.getFarTenor());
			message.setChar(quickfix.field.OrdType.FIELD,quickfix.field.OrdType.FOREX_SWAP);
		} else {

			if (MsgType.ORDER_CANCEL_REQUEST.equals(msgType)) {
				message.removeField(quickfix.field.OrdType.FIELD);
			} else {
				// potentially a broker dependent element
				message.setChar(quickfix.field.OrdType.FIELD,quickfix.field.OrdType.PREVIOUSLY_QUOTED);
			}
		}
	}

}
