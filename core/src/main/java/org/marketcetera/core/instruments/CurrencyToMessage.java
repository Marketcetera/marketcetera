package org.marketcetera.core.instruments;

import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.trade.Currency;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;

import quickfix.DataDictionary;
import quickfix.Group;
import quickfix.Message;
import quickfix.field.*;

/**
 * Adds the appropriate fields for a currency instrument to a FIX Message.
 *
 */
@ClassVersion("$Id$")
public class CurrencyToMessage
        extends InstrumentToMessage<Currency>
{

    /**
     * Creates an instance.
     */
	public CurrencyToMessage(){
		super(org.marketcetera.trade.Currency.class);
	}
	
    /* (non-Javadoc)
     * @see org.marketcetera.core.instruments.InstrumentToMessage#isSupported(quickfix.DataDictionary, java.lang.String)
     */
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

    /* (non-Javadoc)
     * @see org.marketcetera.core.instruments.InstrumentToMessage#set(org.marketcetera.trade.Instrument, java.lang.String, quickfix.Message)
     */
	@Override
	public void set(Instrument inInstrument, String inBeginString, Message inMessage) {
	       if(FIXVersion.FIX40.equals(FIXVersion.getFIXVersion(inBeginString))) {
	            throw new IllegalArgumentException(
	                    Messages.FOREX_NOT_SUPPORTED_FOR_FIX_VERSION.getText(inBeginString));
	        }
	        inMessage.setField(new Symbol(inInstrument.getSymbol()));
	        Currency currency = (Currency)inInstrument;
	        switch(FIXVersion.getFIXVersion(inBeginString)){
	            case FIX_SYSTEM: //fall through
	            case FIX40:  //fall through
	            case FIX41:  //fall through
	            case FIX42:  //fall through
	            case FIX43:  //fall through
	            case FIX44:  //fall through
	                setSecurityType(inInstrument, inBeginString, inMessage);
	                break;
	            default:
	            	setSecurityType(inInstrument, inBeginString, inMessage);
	                setCFICode(inMessage, currency);
	                break;
	        }
		
	}

    /* (non-Javadoc)
     * @see org.marketcetera.core.instruments.InstrumentToMessage#set(org.marketcetera.trade.Instrument, quickfix.DataDictionary, java.lang.String, quickfix.Message)
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
        if (dictionary.isMsgField(msgType, quickfix.field.Currency.FIELD) && currencyInstrument.getTradedCCY()!=null) {
            message.setString(quickfix.field.Currency.FIELD,currencyInstrument.getTradedCCY());
        }
        if(MsgType.ORDER_CANCEL_REQUEST.equals(msgType)) {
            message.removeField(FutSettDate.FIELD);
            message.removeField(quickfix.field.OrdType.FIELD);
        } else {
            if(currencyInstrument.getNearTenor() !=null) {
                message.setString(FutSettDate.FIELD,currencyInstrument.getNearTenor());
            }
            if (currencyInstrument.isSwap()) {
                message.setString(FutSettDate2.FIELD,currencyInstrument.getFarTenor());
                message.setChar(quickfix.field.OrdType.FIELD,quickfix.field.OrdType.FOREX_SWAP);
            } else {
                // potentially a broker dependent element
                message.setChar(quickfix.field.OrdType.FIELD,quickfix.field.OrdType.PREVIOUSLY_QUOTED);
            }
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.instruments.InstrumentToMessage#set(org.marketcetera.trade.Instrument, quickfix.DataDictionary, java.lang.String, quickfix.Group)
     */
    @Override
    public void set(Instrument inInstrument,
                    DataDictionary inDictionary,
                    String inMsgType,
                    Group inGroup)
    {
        // always include the symbol
        inGroup.setString(Symbol.FIELD, inInstrument.getSymbol());
        inGroup.setString(SecurityType.FIELD,SecurityType.FOREIGN_EXCHANGE_CONTRACT);
        if(inDictionary.isMsgField(inMsgType, Product.FIELD)) {
            inGroup.setInt(Product.FIELD, Product.CURRENCY);
        }
        Currency currencyInstrument = (Currency) inInstrument;
        if (inDictionary.isMsgField(inMsgType, quickfix.field.Currency.FIELD) && currencyInstrument.getTradedCCY()!=null) {
            inGroup.setString(quickfix.field.Currency.FIELD,currencyInstrument.getTradedCCY());
        }
        if(MsgType.ORDER_CANCEL_REQUEST.equals(inMsgType)) {
            inGroup.removeField(FutSettDate.FIELD);
            inGroup.removeField(quickfix.field.OrdType.FIELD);
        } else {
            if(currencyInstrument.getNearTenor() !=null) {
                inGroup.setString(FutSettDate.FIELD,currencyInstrument.getNearTenor());
            }
            if (currencyInstrument.isSwap()) {
                inGroup.setString(FutSettDate2.FIELD,currencyInstrument.getFarTenor());
                inGroup.setChar(quickfix.field.OrdType.FIELD,quickfix.field.OrdType.FOREX_SWAP);
            } else {
                // potentially a broker dependent element
                inGroup.setChar(quickfix.field.OrdType.FIELD,quickfix.field.OrdType.PREVIOUSLY_QUOTED);
            }
        }
    }
    /**
     * Sets the CFI Code on the given <code>Message</code> for the given <code>Currency</code>.
     *
     * @param inMessage a <code>Message</code> value
     * @param inCurrency a <code>Currency</code> value
     */
    private static void setCFICode(Message inMessage,
    							  Currency inCurrency)
    {
        String cfiCode = CFICodeUtils.getCFICode(inCurrency);
        if(cfiCode != null) {
            inMessage.setField(new CFICode(cfiCode));
        }
    }
}
