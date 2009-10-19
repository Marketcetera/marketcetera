package org.marketcetera.options;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.regex.Pattern;

import javax.annotation.concurrent.ThreadSafe;

import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.OptionType;
import org.marketcetera.trade.SecurityType;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Provides a set of utilities for option instruments.
 *
 * @author gmiller
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.5.0
 */
@ClassVersion("$Id$")
@ThreadSafe
public class OptionUtils
{
	/**
	 * Determines the <code>Date</code> value of the US Equity Option expiration day
	 * for the given month and year.
	 * 
	 * @param month an <code>int</code> value containing one of Calendar.JANUARY - Calendar.DECEMBER
	 * @param year an <code>int</code> value containing the full year
	 * @return a <code>Date</code> value indicating the option expiration 
	 */
	public static final Date getUSEquityOptionExpiration(int month, int year){
		Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone("GMT")); //i18n_date
		// set the calendar to midnight of the first day of the month in the given year GMT
		cal.set(year,
		        month,
		        1,
		        0,
		        0,
		        0);
		int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
		int firstFridayOffset = (Calendar.FRIDAY - dayOfWeek + 7)%7;

		cal.add(Calendar.DAY_OF_MONTH, firstFridayOffset + 15); // two weeks and a day
		return cal.getTime();
	}
	
	public static final Date getNextUSEquityOptionExpiration(){
		Calendar cal = GregorianCalendar.getInstance();    //i18n_datetime
		long currentTime = cal.getTimeInMillis();
		int currentMonth = cal.get(Calendar.MONTH);
		int currentYear = cal.get(Calendar.YEAR);
		Date candidate = getUSEquityOptionExpiration(currentMonth, currentYear);
		if (currentTime > candidate.getTime()){
			candidate = getUSEquityOptionExpiration(currentMonth+1, currentYear);
		} 
		return candidate;
	}
	/**
	 * 
	 *
	 *
	 * @param inSymbol
	 * @return
	 */
	public static boolean isOsiOptionSymbol(Option inSymbol)
	{
	    try {
	        validateOsiSymbol(inSymbol);
	    } catch (IllegalArgumentException e) {
	        return false;
	    }
	    return true;
	}
	/**
	 * 
	 *
	 *
	 * @param inOption
	 */
	private static void validateOsiSymbol(Option inOption)
	{
        if(!inOption.getSecurityType().equals(SecurityType.Option)) {
            throw new IllegalArgumentException(inOption + " is not of type option."); //TODO message catalog
        }
        if(!OSI_SYMBOL_PATTERN.matcher(inOption.getSymbol()).matches()) {
            throw new IllegalArgumentException(inOption + " is not an OSI-compliant symbol."); //TODO message catalog
        }
        // TODO check that date is valid
	}
	/**
	 * 
	 *
	 *
	 * @param inSymbol
	 * @return
	 */
	public static Equity getUnderlyingSymbol(Option inSymbol)
	{
	    validateOsiSymbol(inSymbol);
	    return new Equity(inSymbol.getSymbol().substring(0,
	                                                     5).trim());
	}
	public static int getExpirationYear(Option inSymbol)
	{
        validateOsiSymbol(inSymbol);
	    return Integer.parseInt(inSymbol.getSymbol().substring(6,
	                                                           8));
	}
	public static int getExpirationMonth(Option inSymbol)
	{
        validateOsiSymbol(inSymbol);
        return Integer.parseInt(inSymbol.getSymbol().substring(8,
                                                               10));
	}
	public static int getExpirationDay(Option inSymbol)
	{
        validateOsiSymbol(inSymbol);
        return Integer.parseInt(inSymbol.getSymbol().substring(10,
                                                               12));
	}
	public static OptionType getType(Option inSymbol)
	{
        validateOsiSymbol(inSymbol);
        char c = inSymbol.getSymbol().charAt(12);
        if(c == 'C') {
            return OptionType.Call;
        } else {
            return OptionType.Put;
        }
	}
	public static BigDecimal getStrike(Option inSymbol)
	{
        validateOsiSymbol(inSymbol);
        String rawStrike = String.format("%s.%s",
                                         inSymbol.getSymbol().substring(13,
                                                                        18),
                                         inSymbol.getSymbol().substring(18));
	    return new BigDecimal(rawStrike);
	}
	/**
	 * the pattern of an OSI-compliant option symbol
	 */
	private static final Pattern OSI_SYMBOL_PATTERN = Pattern.compile(".{6}[0-9]{2}(01|02|03|04|05|06|07|08|09|10|11|12)[0-3][0-9](C|P)[0-9]{5}[0-9]{3}");
}
