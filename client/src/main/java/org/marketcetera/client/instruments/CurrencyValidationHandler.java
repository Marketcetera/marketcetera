package org.marketcetera.client.instruments;

import org.marketcetera.client.OrderValidationException;
import org.marketcetera.trade.Currency;
import org.marketcetera.trade.Instrument;

import org.marketcetera.util.log.I18NBoundMessage1P;
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
        super(Currency.class);
    }
	
	/**
	 * 
	 */
	@Override
	public void validate(Instrument inInstrument) throws OrderValidationException {
		
	}
	
	public static void validateCurrencySymbol(String symbol) throws OrderValidationException
	{
		if(symbol == null || symbol.isEmpty()) 
		{
			return;
        }
		try {
					String[] currencyPair = symbol.split("/");
					if (currencyPair==null ||  currencyPair.length !=2 || currencyPair[0].length()!=3 || currencyPair[1].length()!=3 || currencyPair[0].equalsIgnoreCase(currencyPair[1])) 
					{
						throw new OrderValidationException(new I18NBoundMessage1P
								(Messages.INVALID_CURRENCY_SYMBOL_FORMAT,symbol));
					}
		} catch (OrderValidationException e) {
			throw e;
		} catch (Exception e) {
			throw new OrderValidationException(new I18NBoundMessage1P
					(Messages.INVALID_CURRENCY_SYMBOL_FORMAT,symbol));
		}
	}
	
}
