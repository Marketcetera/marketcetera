package org.marketcetera.client.instruments;

import org.marketcetera.client.OrderValidationException;
import org.marketcetera.trade.Currency;
import org.marketcetera.trade.Instrument;

import org.marketcetera.util.misc.ClassVersion;

/**
 * A class that abstracts out instrument validation functions.
 *
 * @param <I> The type of instrument handled by this function
 * 
 * @author richard.obrien@qmscapital.com
 * @version $Id: CurrencyValidationHandler.java 2012-02-24 richard.obrien $
 * @since 2.1.15
 */
@ClassVersion("$Id: CurrencyValidationHandler 2012-02-24 richard.obrien $")
public class CurrencyValidationHandler extends InstrumentValidationHandler<Currency>{

	/**
	 * 
	 */
	public CurrencyValidationHandler(){
        super(org.marketcetera.trade.Currency.class);
    }
	
	/**
	 * 
	 */
	@Override
	public void validate(Instrument inInstrument) throws OrderValidationException {
		
	}
	
}
