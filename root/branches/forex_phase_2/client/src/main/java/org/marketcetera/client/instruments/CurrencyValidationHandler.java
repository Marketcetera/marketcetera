package org.marketcetera.client.instruments;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.marketcetera.client.OrderValidationException;
import org.marketcetera.trade.Currency;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.misc.ClassVersion;

/**
 * A class that perform currency validation functions.
 *
 * @param <I> The type of instrument handled by this function
 * 
 */
@ClassVersion("$Id: CurrencyValidationHandler")
public class CurrencyValidationHandler extends InstrumentValidationHandler<Currency>{

	public CurrencyValidationHandler(){
        super(Currency.class);
    }
	

	@Override
	public void validate(Instrument inInstrument) throws OrderValidationException {
		Currency currency = (Currency) inInstrument;
		validateCurrencySymbol(currency.getSymbol());
		if(currency.getNearTenor() !=null && !currency.getNearTenor().isEmpty())
		{
			validateTenor(currency.getNearTenor());
		}
		if(currency.getFarTenor() !=null && !currency.getFarTenor().isEmpty())
		{
			validateTenor(currency.getFarTenor());
		}		
	}
	
	public static void validateCurrencySymbol(String symbol) throws OrderValidationException
	{
		if(symbol == null || symbol.isEmpty()) 
		{
			return;
        }
		try {
			String[] currencyPair = symbol.split("/");
			if (currencyPair==null ||  currencyPair.length !=2 || currencyPair[0].length()!=3 || 
					currencyPair[1].length()!=3 || currencyPair[0].equalsIgnoreCase(currencyPair[1])) 
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
	
	public static void validateTenor(String tenor) throws OrderValidationException
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		sdf.setLenient(false);
		Date date;
		try {
			date  = sdf.parse(tenor);	
		} catch (Exception e) {
			throw new OrderValidationException(new I18NBoundMessage1P
					(Messages.INVALID_CURRENCY_TENOR_FORMAT,tenor));
		}
		if(date.before(new Date()))
		{
			throw new OrderValidationException(new I18NBoundMessage1P
					(Messages.INVALID_CURRENCY_TENOR_VALUE,tenor));
		}
	}	
}
