package org.marketcetera.core.options;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.regex.Pattern;

import org.marketcetera.core.attributes.ClassVersion;
import org.marketcetera.core.trade.Option;
import org.marketcetera.core.trade.OptionType;

/* $License$ */

/**
 * Provides various option related utilities. 
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: OptionUtils.java 16063 2012-01-31 18:21:55Z colin $
 * @since 2.0.0
 */
@ClassVersion("$Id: OptionUtils.java 16063 2012-01-31 18:21:55Z colin $")
public class OptionUtils
{
    /**
     * Gets the <code>Calendar</code> value of the Saturday after the third Friday
     * of the given month and year in the default timezone of the default localte.
     *
     * @param inMonth an <code>int</code> value containing the month in the range 0-11
     * @param inYear an <code>int</code> value containing the year in YYYY format
     * @return a <code>Calendar</code> value indicating the specified date
     */
    private Calendar getSaturdayAfterThirdFriday(int inMonth,
                                                 int inYear)
    {
        Calendar cal = new GregorianCalendar(inYear,
                                             inMonth,
                                             1);
		int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
		int firstFridayOffset = (Calendar.FRIDAY - dayOfWeek + 7)%7;
		cal.add(Calendar.DAY_OF_MONTH,
		        firstFridayOffset + 15); // two weeks and a day
        return cal;
    }
    /**
     * Adds day to a YYYYMM expiry string.
     * 
     * <p>If the given expiry is already in YYYYMMDD format, the given expiry is returned without
     * modification. Note that, in this case, the expiry is assumed to be correct if it is a string
     * of eight numbers - no further validation is performed.</p>
     * 
     * <p>The day added is the Saturday after the third Friday of the month (US standard rules).</p>
     * 
     * @param inExpiry a <code>String</code> value containing an expiry in YYYYMM format
     * @return a <code>String</code> value containing the expiry in YYYYMMDD
     * @throws IllegalArgumentException is the provided expiry is not YYYYMM or cannot be normalized
     */
    public String normalizeUSEquityOptionExpiry(String inExpiry)
    {
        if(inExpiry.length() == 8 &&
           YYYYMMDD.matcher(inExpiry).matches()) {
            return inExpiry;
        }
        if(inExpiry.length() == 6 &&
           YYYYMM.matcher(inExpiry).matches()) {
            try {
                int expiryYear = Integer.parseInt(inExpiry.substring(0,
                                                                     4));
                int expiryMonth = Integer.parseInt(inExpiry.substring(4));
                if(expiryMonth > 0 &&
                   expiryMonth < 13) {
                    Calendar cal = getSaturdayAfterThirdFriday(expiryMonth - 1,
                                                               expiryYear);
                    int expiryDay = cal.get(Calendar.DAY_OF_MONTH);
                    return String.format("%s%02d", //$NON-NLS-1$
                                         inExpiry,
                                         expiryDay);
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(e);
            }
        }
        throw new IllegalArgumentException();
    }
    /**
     * Normalizes the supplied expiry date with a day if it doesn't
     * include the day, ie. if it's in YYYYMM format.
     * 
     * @param inExpiry a <code>String</code> value containing the option expiry string to be normalized
     * @return a <code>String</code> value in YYYYMMDD
     * @throws IllegalArgumentException if the expiry could not be normalized
     */
    public String normalizeEquityOptionExpiry(String inExpiry)
    {
        OptionExpiryNormalizer normalizer = getNormalizer();
        if(normalizer != null) {
            return normalizer.normalizeEquityOptionExpiry(inExpiry);
        }
        return normalizeUSEquityOptionExpiry(inExpiry);
    }
    /**
	 * Gets the <code>OptionType</code> value for the given character
	 * interpreted as an OSI-compliant symbol value.
	 *
	 * @param inOsiChar a <code>char</code> value
	 * @return an <code>OptionType</code> value corresponding with the given
	 *  <code>character</code> or {@link org.marketcetera.core.trade.OptionType#Unknown}
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
     * Gets a string symbol as specified by <a
     * href="http://www.theocc.com/initiatives/symbology/default.jsp">Option
     * Symbology Initiative</a> from an <code>Option</code>. The provided option
     * must have property values compliant with OSI tuples. Otherwise, an
     * exception may be thrown, or an invalid OSI symbol may be returned.
     * 
     * @param inOption an <code>Option</code> value
     * @return a <code>String</code> value compliant with the OSI
     * @throws IllegalArgumentException
     *             if the given option symbol is greater than 6 characters, if
     *             the expiry is not 8 digits, if the type is not {@code
     *             OptionType.Call} or {@code OptionType.Put}, or if the strike
     *             is negative, has more than 5 digits to the left of the
     *             decimal point, or more than 3 digits to the right.
     */
    public static String getOsiSymbolFromOption(Option inOption)
    {
        String symbol = inOption.getSymbol();
        if (symbol.length() > 6) {
            throw new IllegalArgumentException();
        }
        String expiry = inOption.getExpiry().substring(2);
        if (expiry.length() != 6) {
            throw new IllegalArgumentException();
        }
        for (char c : expiry.toCharArray()) {
            if (!Character.isDigit(c)) {
                throw new IllegalArgumentException();
            }
        }
        BigDecimal strike = inOption.getStrikePrice();
        if (strike.signum() == -1 || strike.scale() > 3) {
            throw new IllegalArgumentException();
        }
        long longStrike = inOption.getStrikePrice().movePointRight(3).longValue();
        if (longStrike > 99999999) {
            throw new IllegalArgumentException();
        }
        char type;
        switch (inOption.getType()) {
        case Call:
            type = 'C';
            break;
        case Put:
            type = 'P';
            break;
        default:
            throw new IllegalArgumentException();
        }
        return String.format("%-6s%s%s%08d", //$NON-NLS-1$
               symbol, expiry, type, longStrike);
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
     * Gets the normalizer value.
     *
     * @return an <code>OptionExpiryNormalizer</code> or <code>null</code> if no normalizer is available
     */
    public OptionExpiryNormalizer getNormalizer()
    {
        return optionExpiryNormalizer;
    }
    /**
     * Sets the normalizer value.
     *
     * @param inNormalizer an <code>OptionExpiryNormalizer</code> value
     */
    public void setNormalizer(OptionExpiryNormalizer inNormalizer)
    {
        optionExpiryNormalizer = inNormalizer;
    }
    /**
     * option expiry normalizer value
     */
    private volatile OptionExpiryNormalizer optionExpiryNormalizer;
    /**
     * this pattern does a basic syntax check of a symbol to see if it complies with the OSI
     * note that this pattern does not check the expiry date to see if it is completely valid, for example Feb 31st would be valid
     */
    private static final Pattern OSI_SYMBOL_PATTERN = Pattern.compile(".{6}\\d{2}(0\\d|1[0-2])(0[1-9]|[12]\\d|3[01])(C|P)\\d{5}\\d{3}"); //$NON-NLS-1$
    /**
     * pattern used to check the syntax of a short expiry
     */
    private static final Pattern YYYYMM = Pattern.compile("\\d{6}"); //$NON-NLS-1$
    /**
     * pattern used to check the syntax of a long expiry
     */
    private static final Pattern YYYYMMDD = Pattern.compile("\\d{8}"); //$NON-NLS-1$
}
