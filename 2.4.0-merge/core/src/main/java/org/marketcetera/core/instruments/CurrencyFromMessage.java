package org.marketcetera.core.instruments;

import org.marketcetera.core.Messages;
import org.marketcetera.trade.Currency;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.FutSettDate;
import quickfix.field.FutSettDate2;
import quickfix.field.SecurityType;
import quickfix.field.Symbol;
/**
 * Extracts <code>Currency</code> from a <code>Message</code>.
 *
 * @author <a href="mailto:richard.obrien@qmscapital.com">Richard O'Brien</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class CurrencyFromMessage extends InstrumentFromMessage
{
    /* (non-Javadoc)
     * @see org.marketcetera.core.instruments.InstrumentFromMessage#extract(quickfix.Message)
     */
    @Override
    public Instrument extract(Message m)
    {
        Currency instrument=null;
        
        try{
	        String symbol = m.getString(Symbol.FIELD);
	        String baseCCY = symbol.substring(0, 3);
	        String plCCY = symbol.substring(symbol.length()-3,symbol.length());
	        String nearTenor = "SP";
	        if(m.isSetField(FutSettDate.FIELD)){
	        	nearTenor = m.getString(FutSettDate.FIELD);	    
	        }
	        
	        if(m.isSetField(FutSettDate2.FIELD)){
	        	String farTenor = m.getString(FutSettDate2.FIELD);
	        	instrument = new Currency(baseCCY, plCCY, nearTenor, farTenor);
	        }else{
	        	instrument = new Currency(baseCCY, plCCY, nearTenor);
	        }
	        
	        if(m.isSetField(quickfix.field.Currency.FIELD)){
		        String tradedCCY = m.getString(quickfix.field.Currency.FIELD);
		        instrument.setTradedCCY(tradedCCY);
	        }
        }catch(Exception e){
            Messages.ERROR_CURRENCY_FROM_MESSAGE.getText(e.toString());
        }
        return instrument;
        
    }
    
    /* (non-Javadoc)
     * @see org.marketcetera.core.instruments.DynamicInstrumentHandler#isHandled(java.lang.Object)
     */
    @Override
    protected boolean isHandled(Message inValue)
    {
        try {
            return (inValue.isSetField(SecurityType.FIELD) &&
                    SecurityType.FOREIGN_EXCHANGE_CONTRACT.equals(inValue.getString(SecurityType.FIELD)));
                    
        } catch (FieldNotFound ignore) {
            return false;
        }
    }
}