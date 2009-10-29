package org.marketcetera.options;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Pattern;

import org.marketcetera.trade.Option;
import org.marketcetera.trade.OptionType;

public class OptionUtils {

	/**
	 * 
	 * @param month one of Calendar.JANUARY - Calendar.DECEMBER
	 */
	public static final Date getUSEquityOptionExpiration(int month, int year){
		Calendar cal = GregorianCalendar.getInstance(); //i18n_date
		cal.set(year, month, 1);
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
	 * Gets the <code>OptionType</code> value for the given character
	 * interpreted as an OSI-compliant symbol value.
	 *
	 * @param inOsiChar a <code>char</code> value
	 * @return an <code>OptionType</code> value corresponding with the given
	 *  <code>character</code> or {@link OptionType#Unknown}
	 */
	public static OptionType getOptionTypeForOSICharacter(char inOsiChar)
	{
	    switch(inOsiChar) {
	        case 'P':
	            return OptionType.Put;
	        case 'C':
	            return OptionType.Call;
	        default:
	            return OptionType.Unknown;
	    }
	}
    /**
     * Gets an <code>Option</code> from the given full symbol if the given symbol
     * complies with the option format set out in the
     * <a href="http://www.theocc.com/initiatives/symbology/default.jsp">Option Symbology Initiative</a>.
     *
     * @param inFullSymbol a <code>String</code> value
     * @return an <code>Option</code> value compliant with the OSI
     * @throws IllegalArgumentException if the given full symbol is not OSI-compliant
     */
    public static Option getOsiOptionFromString(String inFullSymbol)
    {
        // this is basic check for the symbol to see if syntactically conforms to the OSI standard - it does not check
        //  for valid dates, for example, just that the right type of character is at the right place in the symbol
        if(OSI_SYMBOL_PATTERN.matcher(inFullSymbol).matches()) {
            // we now know that the symbol is 21 characters long and can be split into sensical pieces
            String symbol = inFullSymbol.substring(0,
                                                   6);
            // note that the Option object expects a four-character year.  the OSI standard doesn't specify how two-character
            //  years are to be interpreted.
            String expiryYear = String.valueOf(getFullYear(Integer.parseInt(inFullSymbol.substring(6,
                                                                                                   8))));
            String expiryMonth = inFullSymbol.substring(8,
                                                        10);
            String expiryDay = inFullSymbol.substring(10,
                                                      12);
            OptionType type = getOptionTypeForOSICharacter(inFullSymbol.charAt(12));
            BigDecimal strikeWhole = new BigDecimal(inFullSymbol.substring(13,
                                                                           18));
            BigDecimal strikeDecimal = new BigDecimal(inFullSymbol.substring(18,
                                                                             21));
            BigDecimal strike = new BigDecimal(String.format("%s.%s", //$NON-NLS-1$
                                                             strikeWhole,
                                                             strikeDecimal));
            Option osiOption = new Option(symbol,
                                          String.format("%s%s%s", //$NON-NLS-1$
                                                        expiryYear,
                                                        expiryMonth,
                                                        expiryDay),
                                          strike,
                                          type);
            return osiOption;
        }
        throw new IllegalArgumentException();
    }
    /**
     * Returns a full year complete with century from the given
     * year-only value.
     *
     * <p>This method assumes that the given value is a number in the
     * interval [0,99].  The returned value will be the given year plus
     * the current century.  If the resulting value < today, the returned
     * value will be in the next century instead.
     *   
     * @param inYear an <code>int</code> containing a number between 0 and 99 inclusive
     * @return an <code>int</code> value containing a full year representation (century and year)
     * @throws IllegalArgumentException if the given year is outside of the interval [0,99] 
     */
    private static int getFullYear(int inYear)
    {
        if(inYear < 0 ||
           inYear > 99) {
            throw new IllegalArgumentException();
        }
        // determine current century as an int
        int currentYear = GregorianCalendar.getInstance().get(Calendar.YEAR);
        int currentCentury = (currentYear / 100) * 100;
        int extrapolatedYear = currentCentury + inYear;
        // completeYear contains an int representing a specific year
        if(extrapolatedYear < currentYear) {
            extrapolatedYear += 100;
        }
        return extrapolatedYear;
    }
    /**
     * this pattern does a basic syntax check of a symbol to see if it complies with the OSI
     * note that this pattern does not check the expiry date to see if it is completely valid, for example Feb 31st would be valid
     */
    private static final Pattern OSI_SYMBOL_PATTERN = Pattern.compile(".{6}\\d{2}(0\\d|1[0-2])(0[1-9]|[12]\\d|3[01])(C|P)\\d{5}\\d{3}"); //$NON-NLS-1$
}
