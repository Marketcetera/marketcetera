package org.marketcetera.photon.internal.core;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.marketcetera.photon.commons.Validate;
import org.marketcetera.photon.core.InstrumentPrettyPrinter;
import org.marketcetera.trade.Currency;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Pretty prints {@link Currency} objects for the UI.
 *
 */
@ClassVersion("$Id$")
public class CurrencyPrettyPrinter
        extends InstrumentPrettyPrinter<Currency>
{
    private static final Pattern EXPIRY_PATTERN = Pattern
            .compile("^(\\d{4})(\\d{2})(\\d{2})?"); //$NON-NLS-1$
    
    private static final String EXPIRY_DISPLAY_MONTH = "%tb-%<ty"; //$NON-NLS-1$

    private static final String EXPIRY_DISPLAY_DAY = "%td-%<tb-%<ty"; //$NON-NLS-1$
	
    /**
     * Create a new CurrencyPrettyPrinter instance.
     */
    public CurrencyPrettyPrinter()
    {
        super(Currency.class);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.photon.core.InstrumentPrettyPrinter#doPrint(org.marketcetera.trade.Instrument)
     */
    @Override
    protected String doPrint(Currency inInstrument)
    {
        return String.format("%s %s", //$NON-NLS-1$
        					inInstrument.getSymbol(),
        					printExpiry(inInstrument)).trim();
    }
    /**
     * Pretty prints a currency tenors.
     * 
     * @param inCurrency the currency
     * @return the string value
     */
   /* public static String testPrintExpiry(Currency inCurrency)
    {
        Validate.notNull(inCurrency,
                         "currency"); //$NON-NLS-1$
        return inCurrency.getNearTenor()+"-"+inCurrency.getFarTenor();//$NON-NLS-1$
    }*/
    
    /**
     * Pretty prints an currency expiry. If the expiry cannot be parsed, it is
     * returned.
     * 
     * @param currency
     *            the Currency
     * @return the string value
     * @throws IllegalArgumentException
     *             if option is null
     */
    public static String printExpiry(Currency currency) {
    	StringBuilder combinedExpiry = new StringBuilder("");
        Validate.notNull(currency, "currency"); //$NON-NLS-1$
        String expiry = currency.getNearTenor();
        Matcher matcher;
        if(expiry != null)
        {
	        matcher = EXPIRY_PATTERN.matcher(expiry);
	        if (matcher.matches()) {
	            String day = matcher.group(3);
	            Calendar c = new GregorianCalendar(Integer.parseInt(matcher
	                    .group(1)), Integer.parseInt(matcher.group(2)) - 1,
	                    day == null ? 1 : Integer.parseInt(day));
	            if (day != null ) {
	            		combinedExpiry.append(String.format(EXPIRY_DISPLAY_DAY, c));
	            } else {
	            	combinedExpiry.append(String.format(EXPIRY_DISPLAY_MONTH, c));
	            }
	        }
	        combinedExpiry.append(" , ");
        }
        
        expiry = currency.getFarTenor();
        if(expiry != null)
        {
	        matcher = EXPIRY_PATTERN.matcher(expiry);
	        if (matcher.matches()) {
	            String day = matcher.group(3);
	            Calendar c = new GregorianCalendar(Integer.parseInt(matcher
	                    .group(1)), Integer.parseInt(matcher.group(2)) - 1,
	                    day == null ? 1 : Integer.parseInt(day));
	            if (day != null) {
	                combinedExpiry.append(String.format(EXPIRY_DISPLAY_DAY, c));
	            } else {
	            	combinedExpiry.append(String.format(EXPIRY_DISPLAY_MONTH, c));
	            }
	        }
        }
        return combinedExpiry.toString().trim();
    }
}
